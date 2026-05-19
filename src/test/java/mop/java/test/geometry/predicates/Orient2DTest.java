package mop.java.test.geometry.predicates;

import mop.java.geometry.predicates.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

//----------------------------------------------------------------

/** Geometry predicates.
 * <pre>
 * mvn -Dtest=mop.java.test.geometry.predicates.Orient2DTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-18
 */

public final class Orient2DTest {

  //--------------------------------------------------------------

  private static final void orient2D (final List<Predicate> predicates,
                                      final double[] p0,
                                      final double[] p1,
                                      final double[] p2) {
    for (final Predicate predicate : predicates) {
      final double trueAreaX2 = Common.truth().orient2d(p0, p1, p2);
      final double areaX2 = predicate.orient2d(p0, p1, p2);
      if (predicate.isExact()) {
        Assertions.assertEquals(
          trueAreaX2, areaX2,
          "\n" + " truth=" + Double.toHexString(trueAreaX2) +
            "\n" + predicate + " orient2d=" + Double.toHexString(areaX2) +
            "\n" + Arrays.toString(p0) +
            "\n" + Arrays.toString(p1) +
            "\n" + Arrays.toString(p2)); }
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

    orient2D(Common.makePredicates(), p0, p1, p2);
    // reverse
    orient2D(Common.makePredicates(), p1, p0, p2);
    // 1 pt singular
    orient2D(Common.makePredicates(), p0, p0, p0);
    // 2 pt line segment
    orient2D(Common.makePredicates(),p0, p2, p0);
    // TODO: Slow returns -1, not 0
    orient2D(List.of(new Adapt(),new Fast()),p0, p0, p2);
    // Co-linear triangle
    // TODO: Exact is wrong: , returns 2.0, but 2*signed area = 0.0
    orient2D(Common.makePredicates(), p0, p1, p3);
    }


  //--------------------------------------------------------------
}
//--------------------------------------------------------------
