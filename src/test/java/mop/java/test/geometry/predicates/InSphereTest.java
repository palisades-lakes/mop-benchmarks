package mop.java.test.geometry.predicates;

import mop.java.geometry.predicates.*;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.rng.UniformRandomProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

//----------------------------------------------------------------

/** Common code for geometry predicate tests.
 * <pre>
 * mvn -Dtest=mop.java.test.geometry.predicates.InSphereTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-04
 */

public final class InSphereTest {

  //--------------------------------------------------------------
  private static final String failureMsg (final double truth,
                                          final Predicate gold,
                                          final Predicate pred,
                                          final List<Predicate> predicates,
                                          final Vector3D p0,
                                          final Vector3D p1,
                                          final Vector3D p2,
                                          final Vector3D p3,
                                          final Vector3D p4) {
    final StringBuilder msg = new StringBuilder(
      "\ninsphere(" +
        (p0) + "," +
        (p1) + "," +
        (p2) + "," +
        (p3) + "," +
        (p4) + ")" +
        "\ngold=" + gold +
        "\n -> " + Double.toHexString(truth) +
        "\npred=" + pred +
        "\n -> " +
        Double.toHexString(pred.insphere(p0, p1, p2,p3,p4)));
    for (final Predicate p : predicates) {
      msg.append("\n").append(p).append(" -> ")
         .append(Double.toHexString(p.insphere(p0, p1, p2,p3,p4))); }
    return msg + "\n"; }
  //--------------------------------------------------------------

  private static final void inSphere (final List<Predicate> predicates,
                                      final Vector3D p0,
                                      final Vector3D p1,
                                      final Vector3D p2,
                                      final Vector3D p3,
                                      final Vector3D p4) {
    final Predicate gold = Common.truth();
    final double trueInc = gold.insphere(p0, p1, p2, p3, p4);
    for (final Predicate p : predicates) {
      final double inc = p.insphere(p0, p1, p2, p3,p4);
      if (p.isExact()) {
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
    inSphere(Common.inSpherePredicates(), p0, p1, p2, p3, p4); }

  @Test
  public final void laplaceTest () {
    final List<Predicate> predicates = Common.inSpherePredicates();
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
