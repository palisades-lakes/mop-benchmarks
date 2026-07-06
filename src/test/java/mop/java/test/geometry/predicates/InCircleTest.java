package mop.java.test.geometry.predicates;

import mop.java.geometry.predicates.triangle.Adapt;
import mop.java.geometry.predicates.triangle.BigFloatTriangle2D;
import mop.java.geometry.predicates.triangle.Exact;
import mop.java.geometry.predicates.triangle.Fast;
import mop.java.geometry.predicates.triangle.RationalFloatTriangle2D;
import mop.java.geometry.predicates.triangle.Slow;
import mop.java.geometry.predicates.triangle.Triangle2D;
import mop.java.geometry.predicates.triangle.jts.DDFast;
import mop.java.geometry.predicates.triangle.jts.DDNormalized;
import mop.java.geometry.predicates.triangle.jts.DDSlow;
import mop.java.geometry.predicates.triangle.jts.DoubleNonRobust;
import mop.java.geometry.predicates.triangle.jts.InCircleNormalized;
import mop.java.geometry.predicates.triangle.macro.AdaptMacro;
import mop.java.geometry.predicates.triangle.macro.DefaultMacro;
import mop.java.geometry.predicates.triangle.macro.ExactMacro;
import mop.java.geometry.predicates.triangle.macro.FastMacro;
import mop.java.geometry.predicates.triangle.macro.SlowMacro;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.rng.UniformRandomProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

//----------------------------------------------------------------
/** Common code for geometry predicate tests.
 * <pre>
 * mvn -Dtest=mop.java.test.geometry.predicates.InCircleTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-06
 */

public final class InCircleTest {

  // ground truth predicate.
  public static final Triangle2D truth () {
    return new BigFloatTriangle2D(); }

  public static final List<Triangle2D> inCircleTriangles () {
    final Triangle2D ddFast = new DDFast();
    final Triangle2D ddNormalized = new DDNormalized();
    final Triangle2D ddSlow = new DDSlow();
    //final Triangle2D inCircleCC = new InCircleCC();
    final Triangle2D doubleNonRobust = new DoubleNonRobust();
    final Triangle2D inCircleNormalized = new InCircleNormalized();
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
      ddFast,ddNormalized,ddSlow,doubleNonRobust,inCircleNormalized,
      // mine
      rationalFloat,bigFloat,
      // Shewchuk predicates.c
      exact, adapt,fast ,slow,
      exactMacro, adaptMacro, defaultMacro, fastMacro, slowMacro
                  ); }
  //--------------------------------------------------------------
//  private static final String debugMsg (final double truth,
//                                        final double check,
//                                        final Triangle2D gold,
//                                        final Triangle2D pred,
//                                        final Vector2D p0,
//                                        final Vector2D p1,
//                                        final Vector2D p2,
//                                        final Vector2D p3) {
//    final String msg = "\ninCircle(" +
//      p0 + "," + p1 + "," + p2 + "," + p3 + ")" +
//      "\ngold=" + gold + " -> " + Double.toHexString(truth) +
//      "\npred=" + pred + " -> " + Double.toHexString(check) +
//      "\ndiff=" + Double.toHexString(truth - check) +
//      "\nulp=" + Double.toHexString(Math.ulp(truth));
//    return msg + "\n"; }

  private static final String failureMsg (final double truth,
                                          final double check,
                                          final Triangle2D gold,
                                          final Triangle2D pred,
                                          final List<Triangle2D> predicates,
                                          final Vector2D p0,
                                          final Vector2D p1,
                                          final Vector2D p2,
                                          final Vector2D p3) {
    final StringBuilder msg = new StringBuilder(
      "\ninCircle(" + p0 + "," + p1 + "," + p2 + "," + p3 + ")" +
        "\ngold=" + gold + " -> " + Double.toHexString(truth) +
        "\npred=" + pred + " -> " + Double.toHexString(check));
    msg.append("\ndiff=").append(Double.toHexString(truth-check));
    msg.append("\nulp=").append(Double.toHexString(Math.ulp(truth)));
    if (null != predicates) {
    for (final Triangle2D p : predicates) {
      msg.append("\n").append(p).append(" ->\n")
         .append(Double.toHexString(p.inCircle(p0, p1, p2, p3))); } }
    return msg + "\n"; }

  //--------------------------------------------------------------
  /** Compare exact value from cleaned up Adapt and
   * brutal macro-expanded AdaptMacro.
   */

  private static final void adaptTest (final Vector2D p0,
                                       final Vector2D p1,
                                       final Vector2D p2,
                                       final Vector2D p3) {
    final Triangle2D gold = new AdaptMacro();
    final Triangle2D p = new Adapt();

    final double trueInc = gold.inCircle(p0, p1, p2, p3);
    final double inc = p.inCircle(p0, p1, p2, p3);
    // with delta=0.0 handles +0 vs -0 'correctly'
    Assertions.assertEquals(
      trueInc, inc, 0.0,
      failureMsg(trueInc,inc,gold,p,null,p0,p1,p2,p3)); }

  //--------------------------------------------------------------

  private static final void inCircle (final List<Triangle2D> predicates,
                                      final Vector2D p0,
                                      final Vector2D p1,
                                      final Vector2D p2,
                                      final Vector2D p3) {
    adaptTest(p0,p1,p2,p3);
    final Triangle2D gold = truth();
    final double trueInc = gold.inCircle(p0, p1, p2, p3);
    for (final Triangle2D p : predicates) {
      final double inc = p.inCircle(p0, p1, p2, p3);
//      if (p instanceof Slow) {
//        System.out.println(debugMsg(trueInc,inc,gold,p,p0,p1,p2,p3)); }
      if (p.inCircleExact()) {
        // with delta=0.0 handles +0 vs -0 'correctly'
        Assertions.assertEquals(
          trueInc, inc, 0.0,
          failureMsg(trueInc,inc,gold,p,predicates,p0,p1,p2,p3)); }
      else {
        Assertions.assertEquals(
          Math.signum(trueInc), Math.signum(inc), 0.0,
          failureMsg(trueInc,inc,gold,p,predicates,p0,p1,p2,p3)); } } }

  //--------------------------------------------------------------

  @Test
  public final void simpleTest () {
    final Vector2D p0 =  Vector2D.of( 0.0, 0.0);
    final Vector2D p1 =  Vector2D.of( 1.0, 1.0);
    final Vector2D p2 =  Vector2D.of( -1.0, 1.0);
    final Vector2D p3 =  Vector2D.of( -1.0, -1.0);
    final Vector2D p4 =  Vector2D.of( 1.0, -1.0);

    final List<Triangle2D> predicates = inCircleTriangles();
    inCircle(predicates, p1, p2, p3, p0);
    inCircle(predicates, p1, p2, p3, p4);
    inCircle(predicates, p1, p2, p3, p1);
    // Not working for InCircleCC
    // TODO: decide on the right answer for singular cases.
    // inCircle(Common.inCircleTriangle2Ds(), p1, p1, p1, p4);
    // inCircle(Common.inCircleTriangle2Ds(), p1, p2, p1, p4);
  }
  //--------------------------------------------------------------

  @Test
  public final void laplaceTest () {
    final List<Triangle2D> predicates = inCircleTriangles();
    final int n = 21;
    final UniformRandomProvider urp =
      PRNG.well44497b("seeds/Well44497b-2019-01-05.txt");
    final Generator laplaceGenerator =
      Doubles.laplaceGenerator(n, 2, urp, 0.0, 1.0);
    final double[][] p = (double[][]) laplaceGenerator.next();
    for (int i = 0; i < n-3; i++) {
      inCircle(predicates,
               Vector2D.of(p[i]),
               Vector2D.of(p[i+1]),
               Vector2D.of(p[i+2]),
               Vector2D.of(p[i+3]));} }
  //--------------------------------------------------------------
}
//--------------------------------------------------------------
