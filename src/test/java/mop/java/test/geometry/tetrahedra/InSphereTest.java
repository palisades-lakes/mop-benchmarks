package mop.java.test.geometry.tetrahedra;

import mop.java.geometry.tetrahedron.Adapt;
import mop.java.geometry.tetrahedron.BigFloatTetrahedron3D;
import mop.java.geometry.tetrahedron.Exact;
import mop.java.geometry.tetrahedron.Fast;
import mop.java.geometry.tetrahedron.RationalFloatTetrahedron3D;
import mop.java.geometry.tetrahedron.Slow;
import mop.java.geometry.tetrahedron.Tetrahedron3D;
import mop.java.geometry.tetrahedron.macro.AdaptMacro;
import mop.java.geometry.tetrahedron.macro.DefaultMacro;
import mop.java.geometry.tetrahedron.macro.ExactMacro;
import mop.java.geometry.tetrahedron.macro.FastMacro;
import mop.java.geometry.tetrahedron.macro.SlowMacro;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.rng.UniformRandomProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

//----------------------------------------------------------------

/** <pre>
 * mvn -Dtest=mop.java.test.geometry.predicates.InSphereTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-06
 */

public final class InSphereTest {

  // ground truth predicate.
  public static final Tetrahedron3D truth () {
    return new BigFloatTetrahedron3D(); }


  public static final List<Tetrahedron3D> inSphereTetrahedra () {
    final Tetrahedron3D bigFloat = new BigFloatTetrahedron3D();
    final Tetrahedron3D rationalFloat = new RationalFloatTetrahedron3D();
    final Tetrahedron3D adapt = new Adapt();
    final Tetrahedron3D exact = new Exact();
    final Tetrahedron3D fast = new Fast();
    final Tetrahedron3D slow = new Slow();
    final Tetrahedron3D adaptMacro = new AdaptMacro();
    final Tetrahedron3D defaultMacro = new DefaultMacro();
    final Tetrahedron3D exactMacro = new ExactMacro();
    final Tetrahedron3D fastMacro = new FastMacro();
    final Tetrahedron3D slowMacro = new SlowMacro();
    return List.of(
      // mine
      rationalFloat,bigFloat,
      // Shewchuk predicates.c
      exact,
      adapt,fast,slow,
      exactMacro, adaptMacro, defaultMacro, fastMacro, slowMacro); }

  //--------------------------------------------------------------
  private static final String failureMsg (final double truth,
                                          final Tetrahedron3D gold,
                                          final Tetrahedron3D pred,
                                          final List<Tetrahedron3D> predicates,
                                          final Vector3D p0,
                                          final Vector3D p1,
                                          final Vector3D p2,
                                          final Vector3D p3,
                                          final Vector3D p4) {
    final StringBuilder msg = new StringBuilder(
      "\ninSphere(" +
        (p0) + "," +
        (p1) + "," +
        (p2) + "," +
        (p3) + "," +
        (p4) + ")" +
        "\ngold=" + gold +
        "\n -> " + Double.toHexString(truth) +
        "\npred=" + pred +
        "\n -> " +
        Double.toHexString(pred.inSphere(p0, p1, p2,p3,p4)));
    for (final Tetrahedron3D p : predicates) {
      msg.append("\n").append(p).append(" -> ")
         .append(Double.toHexString(p.inSphere(p0, p1, p2,p3,p4))); }
    return msg + "\n"; }
  //--------------------------------------------------------------

  private static final void inSphere (final List<Tetrahedron3D> predicates,
                                      final Vector3D p0,
                                      final Vector3D p1,
                                      final Vector3D p2,
                                      final Vector3D p3,
                                      final Vector3D p4) {
    final Tetrahedron3D gold = truth();
    final double trueInc = gold.inSphere(p0, p1, p2, p3, p4);
    for (final Tetrahedron3D p : predicates) {
      final double inc = p.inSphere(p0, p1, p2, p3,p4);
      if (p.inSphereExact()) {
        // with delta=0.0 handles +0 vs -0 'correctly'
        Assertions.assertEquals(
          trueInc, inc, 0.0,
          failureMsg(trueInc,gold,p,predicates,p0,p1,p2,p3,p4)); }
      else {
        Assertions.assertEquals(
          Math.signum(trueInc), Math.signum(inc), 0.0,
          failureMsg(trueInc,gold,p,predicates,p0,p1,p2,p3,p4)); } } }

  @Test
  public final void testInSphere () {
    final Vector3D p0 = Vector3D.of(0.0, 0.0, 0.0);
    final Vector3D p1 = Vector3D.of(1.0, 0.0, 0.0);
    final Vector3D p2 = Vector3D.of(0.0, 1.0, 0.0);
    final Vector3D p3 = Vector3D.of(0.0, 0.0, 1.0);
    final Vector3D p4 = Vector3D.of(1.0, 1.0, 1.0);
    inSphere(inSphereTetrahedra(), p0, p1, p2, p3, p4); }

  @Test
  public final void laplaceTest () {
    final List<Tetrahedron3D> predicates = inSphereTetrahedra();
    final int n = 55;
    final UniformRandomProvider urp =
      PRNG.well44497b("seeds/Well44497b-2019-01-05.txt");
    final Generator laplaceGenerator =
      Doubles.laplaceGenerator(n, 3, urp, 0.0, 1.0);
    final double[][] p = (double[][]) laplaceGenerator.next();
    for (int i = 0; i < n-4; i++) {
      inSphere(predicates,
               Vector3D.of(p[i]),
               Vector3D.of(p[i+1]),
               Vector3D.of(p[i+2]),
               Vector3D.of(p[i+3]),
               Vector3D.of(p[i+4]));} }
//--------------------------------------------------------------
}
//--------------------------------------------------------------
