package mop.java.test.geometry.predicates;

import clojure.lang.PersistentStructMap;
import mop.java.geometry.predicates.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

  private static final void orient2D (final double truth,
                                      final List<Predicate> predicates,
                                      final double[] p0,
                                      final double[] p1,
                                      final double[] p2) {
    for (final Predicate predicate : predicates) {
      final double twoSignedArea = predicate.orient2d(p0, p1, p2);
      Assertions.assertEquals(
        Math.signum(truth), Math.signum(twoSignedArea),
        "\n" + " truth=" + Double.toHexString(truth) +
          "\n" + predicate + " orient2d=" + Double.toHexString(twoSignedArea) +
          "\n" + Arrays.toString(p0) +
          "\n" +  Arrays.toString(p1) +
          "\n" + Arrays.toString(p2)); } }

  @Test
  public final void testOrient2D () {
    final double[] p0 = new double[] { 0.0, 0.0, };
    final double[] p1 = new double[] { 1.0, 1.0, };
    final double[] p2 = new double[] { -1.0, 1.0, };
    final double[] p3 = new double[] { -1.0, -1.0, };
    final double[] p4 = new double[] { 1.0, -1.0, };

    final Predicate exact = new Exact();
    final Predicate fast = new Fast();
    final Predicate slow = new Slow();
    final Predicate def = new Default();
    final Predicate adapt = new Adapt();
    final List<Predicate> predicates = List.of(adapt,def,fast,exact,slow);
    // TODO: correct answer should be 1.0, but only Exact gives that.
    orient2D(1.0, predicates, p0, p1, p2);
    orient2D(-1.0, predicates, p1, p0, p2);
    orient2D(0.0, predicates, p0, p0, p0);
    // TODO: Slow returns -1, not 0
    orient2D(0.0, List.of(adapt,def,fast,exact), p0, p0, p2);
    // TODO: Exact, Slow are wrong: 2*signed area = 1.0
    orient2D(0.0, List.of(adapt,def,fast), p1, p0, p3);

  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
