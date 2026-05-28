package mop.java.test.geometry.predicates;

import mop.java.geometry.predicates.Predicate;
import mop.java.geometry.predicates.Slow;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.rng.UniformRandomProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

//----------------------------------------------------------------
/** Common code for geometry predicate tests.
 * <pre>
 * mvn -Dtest=mop.java.test.geometry.predicates.InCircleTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-26
 */

public final class InCircleTest {

  //--------------------------------------------------------------
  private static final String debugMsg (final double truth,
                                        final double check,
                                        final Predicate gold,
                                        final Predicate pred,
                                        final double[] p0,
                                        final double[] p1,
                                        final double[] p2,
                                        final double[] p3) {
    final String msg = "\ninCircle(" +
      Arrays.toString(p0) + "," +
      Arrays.toString(p1) + "," +
      Arrays.toString(p2) + "," +
      Arrays.toString(p3) + ")" +
      "\ngold=" + gold + " -> " + Double.toHexString(truth) +
      "\npred=" + pred + " -> " + Double.toHexString(check) +
      "\ndiff=" + Double.toHexString(truth - check) +
      "\nulp=" + Double.toHexString(Math.ulp(truth));
    return msg + "\n"; }

  private static final String failureMsg (final double truth,
                                          final double check,
                                          final Predicate gold,
                                          final Predicate pred,
                                          final List<Predicate> predicates,
                                          final double[] p0,
                                          final double[] p1,
                                          final double[] p2,
                                          final double[] p3) {
    final StringBuilder msg = new StringBuilder(
      "\ninCircle(" +
        Arrays.toString(p0) + "," +
        Arrays.toString(p1) + "," +
        Arrays.toString(p2) + "," +
        Arrays.toString(p3) + ")" +
        "\ngold=" + gold + " -> " + Double.toHexString(truth) +
        "\npred=" + pred + " -> " + Double.toHexString(check));
    msg.append("\ndiff=").append(Double.toHexString(truth-check));
    msg.append("\nulp=").append(Double.toHexString(Math.ulp(truth)));
    for (final Predicate p : predicates) {
      msg.append("\n").append(p).append(" ->\n")
         .append(Double.toHexString(p.incircle(p0, p1, p2,p3))); }
    return msg + "\n"; }

  //--------------------------------------------------------------

  private static final void inCircle (final List<Predicate> predicates,
                                      final double[] p0,
                                      final double[] p1,
                                      final double[] p2,
                                      final double[] p3) {
    final Predicate gold = Common.truth();
    final double trueInc = gold.incircle(p0, p1, p2, p3);
    for (final Predicate p : predicates) {
      final double inc = p.incircle(p0, p1, p2, p3);
      if (p instanceof Slow) {
        System.out.println(debugMsg(trueInc,inc,gold,p,p0,p1,p2,p3)); }
      if (p.isExact()) {
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
    final double[] p0 = new double[] { 0.0, 0.0, };
    final double[] p1 = new double[] { 1.0, 1.0, };
    final double[] p2 = new double[] { -1.0, 1.0, };
    final double[] p3 = new double[] { -1.0, -1.0, };
    final double[] p4 = new double[] { 1.0, -1.0, };

    final List<Predicate> predicates = Common.inCirclePredicates();
    inCircle(predicates, p1, p2, p3, p0);
    inCircle(predicates, p1, p2, p3, p4);
    inCircle(predicates, p1, p2, p3, p1);
    // Not working for InCircleCC
    // TODO: decide on the right answer for singular cases.
    // inCircle(Common.inCirclePredicates(), p1, p1, p1, p4);
    // inCircle(Common.inCirclePredicates(), p1, p2, p1, p4);
  }
  //--------------------------------------------------------------
  // 2026-05-21: failing, may be Expansion rounding to double?

  @Test
  public final void laplaceTest () {
    final List<Predicate> predicates =Common.inCirclePredicates();
    final int n = 12;
    final UniformRandomProvider urp =
      PRNG.well44497b("seeds/Well44497b-2019-01-05.txt");
    final Generator laplaceGenerator =
      Doubles.laplaceGenerator(n, 2, urp, 0.0, 1.0);
    final double[][] p = (double[][]) laplaceGenerator.next();
    for (int i = 0; i < n-3; i++) {
      inCircle(predicates, p[i], p[i+1], p[i+2], p[i+3]);} }
  //--------------------------------------------------------------
}
//--------------------------------------------------------------
