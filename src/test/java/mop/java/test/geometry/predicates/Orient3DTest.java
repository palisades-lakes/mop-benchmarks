package mop.java.test.geometry.predicates;

import mop.java.geometry.predicates.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

//----------------------------------------------------------------
/** Common code for geometry predicate tests.
 * <pre>
 * mvn -Dtest=mop.java.test.geometry.predicates.EasyTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-18
 */

public final class Orient3DTest {

  //--------------------------------------------------------------

  private static final void orient3D (final List<Predicate> predicates,
                                      final double[] p0,
                                      final double[] p1,
                                      final double[] p2,
                                      final double[] p3) {
    for (final Predicate predicate : predicates) {
      final double trueOrient = Common.truth().orient3d(p0, p1, p2, p3);
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
    orient3D(Common.makePredicates(), p0, p1, p2, p3); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
