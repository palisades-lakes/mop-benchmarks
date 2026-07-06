package mop.java.test.geometry.predicates;

import mop.java.geometry.triangle.Adapt;
import mop.java.geometry.triangle.BigFloatTriangle2D;
import mop.java.geometry.triangle.Exact;
import mop.java.geometry.triangle.Fast;
import mop.java.geometry.triangle.RationalFloatTriangle2D;
import mop.java.geometry.triangle.Slow;
import mop.java.geometry.triangle.Triangle2D;
import mop.java.geometry.triangle.jts.DDFast;
import mop.java.geometry.triangle.jts.DDSlow;
import mop.java.geometry.triangle.jts.DoubleNonRobust;
import mop.java.geometry.triangle.macro.AdaptMacro;
import mop.java.geometry.triangle.macro.DefaultMacro;
import mop.java.geometry.triangle.macro.ExactMacro;
import mop.java.geometry.triangle.macro.FastMacro;
import mop.java.geometry.triangle.macro.SlowMacro;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.rng.UniformRandomProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

//----------------------------------------------------------------

/** Geometry predicates.
 * <pre>
 * mvn -Dtest=mop.java.test.geometry.predicates.SignedAreaTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-06
 */

public final class SignedAreaTest {

  // ground truth predicate.
  public static final Triangle2D truth () {
    return new BigFloatTriangle2D(); }

  public static final List<Triangle2D> signedAreaTriangles () {
    final Triangle2D ddFast = new DDFast();
    final Triangle2D ddSlow = new DDSlow();
    final Triangle2D doubleNonRobust = new DoubleNonRobust();
    final Triangle2D bigFloat = new BigFloatTriangle2D();
    final Triangle2D rationalFloat = new RationalFloatTriangle2D();
    final Triangle2D adapt = new Adapt();
    final Triangle2D exact = new Exact();
    final Triangle2D fast = new Fast();
    final Triangle2D slow = new Slow();
    final Triangle2D adaptMacro = new AdaptMacro();
    final Triangle2D defaultMacro = new DefaultMacro();
    final Triangle2D exactMacro = new ExactMacro();
    final Triangle2D fastMacro = new FastMacro();
    final Triangle2D slowMacro = new SlowMacro();
    return List.of(
      // JTS
      ddFast,ddSlow,doubleNonRobust,
      // mine
      rationalFloat,bigFloat,
      // Shewchuk predicates.c
      exact,
      adapt,fast,slow,
      exactMacro, adaptMacro, defaultMacro, fastMacro, slowMacro); }

  //--------------------------------------------------------------
  private static final String failureMsg (final double truth,
                                          final Triangle2D gold,
                                          final Triangle2D pred,
                                          final List<Triangle2D> predicates,
                                          final Vector2D p0,
                                          final Vector2D p1,
                                          final Vector2D p2) {
    final StringBuilder msg = new StringBuilder(
      "\norient2d(" + p0 + "," + p1 + "," + p2 + ")" +
        "\ngold=" + gold + "\n-> " + Double.toHexString(truth) +
        "\npred=" + pred + "\n-> " + Double.toHexString(
          pred.signedArea(p0,p1,p2)));
    for (final Triangle2D p : predicates) {
      msg.append("\n").append(p).append(" -> ")
         .append(Double.toHexString(p.signedArea(p0,p1,p2))); }
    return msg + "\n"; }

  //--------------------------------------------------------------

  private static final void signedArea (final List<Triangle2D> predicates,
                                        final Vector2D p0,
                                        final Vector2D p1,
                                        final Vector2D p2) {
    final Triangle2D gold = truth();
    final double trueAreaX2 = gold.signedArea(p0,p1,p2);
    for (final Triangle2D p : predicates) {
      final double areaX2 = p.signedArea(p0,p1,p2);
      if (p.signedAreaExact()) {
        // with delta=0.0 handles +0 vs -0 'correctly'
        Assertions.assertEquals(
          trueAreaX2, areaX2, 0.0,
          failureMsg(trueAreaX2,gold,p,predicates,p0,p1,p2)); }
      else {
        Assertions.assertEquals(
          Math.signum(trueAreaX2), Math.signum(areaX2), 0.0,
          failureMsg(trueAreaX2,gold,p,predicates,p0,p1,p2)); } } }

  @Test
  public final void testSignedArea () {
    final Vector2D p0 = Vector2D.of( 0.0, 0.0);
    final Vector2D p1 = Vector2D.of( 1.0, 1.0);
    final Vector2D p2 = Vector2D.of( -1.0, 1.0);
    final Vector2D p3 = Vector2D.of( -1.0, -1.0);

    final List<Triangle2D> predicates = signedAreaTriangles();
    signedArea(predicates, p0, p1, p2);
    // reverse
    signedArea(predicates, p1, p0, p2);
    // 1 pt singular
    signedArea(predicates, p0, p0, p0);
    // 2 pt line segment
    signedArea(predicates, p0, p2, p0);
    // TODO: Slow returns -1, not 0
    signedArea(predicates, p0, p0, p2);
    //orient2D(List.of(new Adapt(),new Fast()),p0, p0, p2);
    // Co-linear triangle
    signedArea(predicates, p0, p1, p3);
  }

  //--------------------------------------------------------------
  // Exact failing in last bit.

  @Test
  public final void laplaceTest () {
    final List<Triangle2D> predicates = signedAreaTriangles();
    final int n = 12;
    final UniformRandomProvider urp =
      PRNG.well44497b("seeds/Well44497b-2019-01-05.txt");
    final Generator laplaceGenerator =
      Doubles.laplaceGenerator(n, 2, urp, 0.0, 1.0);
    final double[][] p = (double[][]) laplaceGenerator.next();
    for (int i = 0; i < n-2; i++) {
      signedArea(predicates,
                 Vector2D.of(p[i]),
                 Vector2D.of(p[i+1]),
                 Vector2D.of(p[i+2]));} }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
