package mop.java.test.geometry.predicates;

import mop.java.geometry.predicates.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

//----------------------------------------------------------------
/** Geometry predicates.
 * <pre>
 * mvn -Dtest=mop.java.test.geometry.predicates.EasyTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-16
 */

public final class EasyTest {

  //--------------------------------------------------------------
  // TODO: setup and tear down

  private static final List<Predicate> makePredicates () {
    final Predicate adapt = new Adapt();
    final Predicate def = new Default();
    final Predicate exact = new Exact();
    final Predicate fast = new Fast();
    final Predicate slow = new Slow();
    return List.of(adapt,def,fast,exact,slow); }

  // ground truth predicate.
  // TODO: may be different for different problems
  private static final Predicate truth () { return new Default(); }

  //--------------------------------------------------------------

  private static final void orient2D (final List<Predicate> predicates,
                                      final double[] p0,
                                      final double[] p1,
                                      final double[] p2) {
    for (final Predicate predicate : predicates) {
      final double trueAreaX2 = truth().orient2d(p0, p1, p2);
      final double areaX2 = predicate.orient2d(p0, p1, p2);
      Assertions.assertEquals(
        Math.signum(trueAreaX2), Math.signum(areaX2),
        "\n" + " truth=" + Double.toHexString(trueAreaX2) +
          "\n" + predicate + " orient2d=" + Double.toHexString(areaX2) +
          "\n" + Arrays.toString(p0) +
          "\n" + Arrays.toString(p1) +
          "\n" + Arrays.toString(p2)); } }

  @Test
  public final void testOrient2D () {
    final double[] p0 = new double[] { 0.0, 0.0, };
    final double[] p1 = new double[] { 1.0, 1.0, };
    final double[] p2 = new double[] { -1.0, 1.0, };
    final double[] p3 = new double[] { -1.0, -1.0, };

    // TODO: correct answer should be 1.0, but only Exact gives that.
    orient2D(makePredicates(), p0, p1, p2);
    orient2D(makePredicates(), p1, p0, p2);
    orient2D(makePredicates(), p0, p0, p0);
    // TODO: Slow returns -1, not 0
    orient2D(List.of(new Adapt(),new Default(),new Fast(),new Exact()),
             p0, p0, p2);
    // TODO: Exact, Slow are wrong: 2*signed area = 1.0
    orient2D(List.of(new Adapt(),new Default(),new Fast()),
             p1, p0, p3); }

  //--------------------------------------------------------------

  private static final void incircle (final List<Predicate> predicates,
                                      final double[] p0,
                                      final double[] p1,
                                      final double[] p2,
                                      final double[] p3) {
    for (final Predicate predicate : predicates) {
      final double trueInc = truth().incircle(p0, p1, p2, p3);
      final double inc = predicate.incircle(p0, p1, p2, p3);
      Assertions.assertEquals(
        Math.signum(trueInc), Math.signum(inc),
        "\n" + " truth=" + Double.toHexString(trueInc) +
          "\n" + predicate + " incircle=" + Double.toHexString(inc) +
          "\n" + Arrays.toString(p0) +
          "\n" + Arrays.toString(p1) +
          "\n" + Arrays.toString(p2) +
          "\n" + Arrays.toString(p3)); } }

  @Test
  public final void testIncircle () {
    final double[] p0 = new double[] { 0.0, 0.0, };
    final double[] p1 = new double[] { 1.0, 1.0, };
    final double[] p2 = new double[] { -1.0, 1.0, };
    final double[] p3 = new double[] { -1.0, -1.0, };
    final double[] p4 = new double[] { 1.0, -1.0, };

    incircle(makePredicates(), p1, p2, p3, p0);
    incircle(makePredicates(), p1, p2, p3, p4);
  }
  //--------------------------------------------------------------

  private static final void orient3D (final List<Predicate> predicates,
                                      final double[] p0,
                                      final double[] p1,
                                      final double[] p2,
                                      final double[] p3) {
    for (final Predicate predicate : predicates) {
      final double trueOrient = truth().orient3d(p0, p1, p2, p3);
      final double inc = predicate.orient3d(p0, p1, p2, p3);
      Assertions.assertEquals(
        Math.signum(trueOrient), Math.signum(inc),
        "\n" + " truth=" + Double.toHexString(trueOrient) +
          "\n" + predicate + " orient3d=" + Double.toHexString(inc) +
          "\n" + Arrays.toString(p0) +
          "\n" + Arrays.toString(p1) +
          "\n" + Arrays.toString(p2) +
          "\n" + Arrays.toString(p3)); } }

  @Test
  public final void testOrient3D () {
    final double[] p0 = new double[] { 0.0, 0.0, 0.0};
    final double[] p1 = new double[] { 1.0, 0.0, 0.0};
    final double[] p2 = new double[] { 0.0, 1.0, 0.0};
    final double[] p3 = new double[] { 0.0, 0.0, 1.0};
    orient3D(makePredicates(), p0, p1, p2, p3); }

  //--------------------------------------------------------------

  private static final void insphere (final List<Predicate> predicates,
                                      final double[] p0,
                                      final double[] p1,
                                      final double[] p2,
                                      final double[] p3,
                                      final double[] p4) {
    for (final Predicate predicate : predicates) {
      final double trueIn = truth().insphere(p0, p1, p2, p3, p4);
      final double inc = predicate.insphere(p0, p1, p2, p3,p4);
      Assertions.assertEquals(
        Math.signum(trueIn), Math.signum(inc),
        "\n" + " truth=" + Double.toHexString(trueIn) +
          "\n" + predicate + " insphere=" + Double.toHexString(inc) +
          "\n" + Arrays.toString(p0) +
          "\n" + Arrays.toString(p1) +
          "\n" + Arrays.toString(p2) +
          "\n" + Arrays.toString(p3) +
          "\n" + Arrays.toString(p4)); } }

  @Test
  public final void testInsphere () {
    final double[] p0 = new double[] { 0.0, 0.0, 0.0};
    final double[] p1 = new double[] { 1.0, 0.0, 0.0};
    final double[] p2 = new double[] { 0.0, 1.0, 0.0};
    final double[] p3 = new double[] { 0.0, 0.0, 1.0};
    final double[] p4 = new double[] { 1.0, 1.0, 1.0};
    insphere(makePredicates(), p0, p1, p2, p3, p4); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
