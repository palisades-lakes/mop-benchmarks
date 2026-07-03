package mop.java.test.geometry.predicates;

import mop.java.geometry.predicates.*;
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
 * mvn -Dtest=mop.java.test.geometry.predicates.Orient3DTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-03
 */

public final class Orient3DTest {

  //--------------------------------------------------------------
  private static final String failureMsg (final double truth,
                                          final Predicate gold,
                                          final Predicate pred,
                                          final List<Predicate> predicates,
                                          final double[] p0,
                                          final double[] p1,
                                          final double[] p2,
                                          final double[] p3) {
    final StringBuilder msg = new StringBuilder(
      "\norient3d(" +
        Arrays.toString(p0) + "," +
        Arrays.toString(p1) + "," +
        Arrays.toString(p2) + "," +
        Arrays.toString(p3) + ")" +
        "\norient3d(" + Arrays.toString(p0) + "," +
        Arrays.toString(p1) + "," +
        Arrays.toString(p2) + "," +
        Arrays.toString(p3) + ")" +
        "\ngold=" + gold + "\n-> " + Double.toHexString(truth) +
        "\npred=" + pred + "\n-> " + Double.toHexString(
    pred.orient3d(p0, p1, p2,p3)));
    for (final Predicate p : predicates) {
      msg.append("\n").append(p).append(" -> ")
         .append(Double.toHexString(p.orient3d(p0, p1, p2,p3))); }
    return msg + "\n"; }

  //--------------------------------------------------------------

  private static final void orient3D (final List<Predicate> predicates,
                                      final double[] p0,
                                      final double[] p1,
                                      final double[] p2,
                                      final double[] p3) {
    final Predicate gold = Common.truth();
    final double trueVol = gold.orient3d(p0, p1, p2, p3);
    for (final Predicate p : predicates) {
      final double vol = p.orient3d(p0, p1, p2, p3);
      if (p.isExact()) {
        // with delta=0.0 handles +0 vs -0 'correctly'
        Assertions.assertEquals(
          trueVol, vol, 0.0,
          failureMsg(trueVol,gold,p,predicates,p0,p1,p2,p3)); }
      else {
        Assertions.assertEquals(

          Math.signum(trueVol), Math.signum(vol), 0.0,
          failureMsg(trueVol,gold,p,predicates,p0,p1,p2,p3)); } } }

  @Test
  public final void testOrient3D () {
    final double[] p0 = new double[] { 0.0, 0.0, 0.0};
    final double[] p1 = new double[] { 1.0, 0.0, 0.0};
    final double[] p2 = new double[] { 0.0, 1.0, 0.0};
    final double[] p3 = new double[] { 0.0, 0.0, 1.0};
    orient3D(Common.orient3dPredicates(), p0, p1, p2, p3); }

  @Test
  public final void laplaceTest () {
    final List<Predicate> predicates = Common.orient3dPredicates();
    final int n = 21;
    final UniformRandomProvider urp =
      PRNG.well44497b("seeds/Well44497b-2019-01-05.txt");
    final Generator laplaceGenerator =
      Doubles.laplaceGenerator(n, 3, urp, 0.0, 1.0);
    final double[][] p = (double[][]) laplaceGenerator.next();
    for (int i = 0; i < n-3; i++) {
      orient3D(predicates, p[i], p[i+1], p[i+2], p[i+3]);} }
//--------------------------------------------------------------
}
//--------------------------------------------------------------
