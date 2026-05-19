package mop.java.test.geometry.predicates;

import mop.java.geometry.predicates.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

//----------------------------------------------------------------

/** Common code for geometry predicate tests.
 * <pre>
 * mvn -Dtest=mop.java.test.geometry.predicates.InSphereTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-19
 */

public final class InSphereTest {

  //--------------------------------------------------------------
  private static final String failureMsg (final double truth,
                                          final Predicate gold,
                                          final Predicate pred,
                                          final List<Predicate> predicates,
                                          final double[] p0,
                                          final double[] p1,
                                          final double[] p2,
                                          final double[] p3,
                                          final double[] p4) {
    final StringBuilder msg = new StringBuilder(
      "\ninsphere(" +
        Arrays.toString(p0) + "," +
        Arrays.toString(p1) + "," +
        Arrays.toString(p2) + "," +
        Arrays.toString(p3) + "," +
        Arrays.toString(p4) + ")" +
        "\ngold=" + gold +
        ", truth=" + truth +
        "\npred=" + pred +
        " -> " + Double.toHexString(pred.insphere(p0, p1, p2,p3,p4)));
    for (final Predicate p : predicates) {
      msg.append("\n").append(p).append(" -> ")
         .append(Double.toHexString(p.insphere(p0, p1, p2,p3,p4))); }
    return msg + "\n"; }
  //--------------------------------------------------------------

  private static final void inSphere (final List<Predicate> predicates,
                                      final double[] p0,
                                      final double[] p1,
                                      final double[] p2,
                                      final double[] p3,
                                      final double[] p4) {
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
          Math.signum(trueInc), Math.signum(inc),
          failureMsg(trueInc,gold,p,predicates,p0,p1,p2,p3,p4)); } } }

  @Test
  public final void testInSphere () {
    final double[] p0 = new double[] { 0.0, 0.0, 0.0};
    final double[] p1 = new double[] { 1.0, 0.0, 0.0};
    final double[] p2 = new double[] { 0.0, 1.0, 0.0};
    final double[] p3 = new double[] { 0.0, 0.0, 1.0};
    final double[] p4 = new double[] { 1.0, 1.0, 1.0};
    inSphere(Common.makePredicates(), p0, p1, p2, p3, p4); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
