package mop.java.test.numbers;

import mop.java.numbers.*;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

//----------------------------------------------------------------
/**
 * Test desired properties of DoubleInterval.
 * <p>
 * <pre>
 * mvn -q -Dtest=mop.java.test.numbers.DoubleIntervalTest test > DIT.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-15
 */

public final class DoubleIntervalTest {

  private static final int TRYS = 257;

//  private static final BinaryOperator dist =
//    (q0, q1) -> ((DoubleInterval) q0).subtract((DoubleInterval) q1).abs();

  // NOTE: intervals will fail the squareTest:
  // The set of values that result from
  // { z*z : z in [min,max]} is different from
  // { z0*z1 : z0,z1 in [min,max]}.


//  @Test
//  public final void squareTest () {
//    final Generator g =
//      DoubleIntervals.fromDoubleGenerator(
//        PRNG.well44497b("seeds/Well44497b-2019-01-05.txt"));
//    for (int i = 0; i < TRYS; i++) {
//      final DoubleInterval x = (DoubleInterval) g.next();
//      final DoubleInterval x2 = x.square();
//      final DoubleInterval xx = x.multiply(x);
//      Assertions.assertEquals(x2, xx); } }

  @Test
  public final void l2norm2Test () {
    final Generator g =
      DoubleIntervals.fromDoubleGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-09.txt"));
    for (int i = 0; i < TRYS; i++) {
      final DoubleInterval x = (DoubleInterval) g.next();
      final DoubleInterval y = (DoubleInterval) g.next();
      final DoubleInterval l20 = DoubleInterval.l2norm2(x, y);
      final DoubleInterval l21 = x.square().add(y.square());
      Assertions.assertEquals(
        l20,l21,
        "\nx=" + x.toHexString() +
          "\ny=" + y.toHexString() +
          "\nl20=" + l20.toHexString() +
          "\nl21=" + l21.toHexString()); } }

//  private static final String sumFailureMsg (final String name,
//                                             final double z0,
//                                             final double z1,
//                                             final DoubleInterval b0,
//                                             final DoubleInterval b1,
//                                             final DoubleInterval s0,
//                                             final DoubleInterval s1) {
//    return
//      "\n" + name +
//        "\nz0=" + Double.toHexString(z0) +
//        "\nb0=" + b0.toHexString() +
//        "\nz1=" + Double.toHexString(z1) +
//        "\nb1=" + b1.toHexString() +
//        "\ns0=" + s0.toHexString() +
//        "\ns1=" + s1.toHexString();
//  }

//  @Test
//  public final void sumTest () {
//    final UniformRandomProvider urp =
//      PRNG.well44497b("seeds/Well44497b-2019-01-09.txt");
//    final Generator g =
//      Doubles.laplaceGenerator(urp, 0.0, 1000.0);
//    for (int i = 0; i < TRYS; i++) {
//      final double z0 = g.nextDouble();
//      final double z1 = g.nextDouble();
//      final DoubleInterval b0 = DoubleInterval.valueOf(z0);
//      final DoubleInterval b1 = DoubleInterval.valueOf(z1);
//      final DoubleInterval expected = b0.add(b1);
//      final DoubleInterval add01 = b0.add(z1);
//      Assertions.assertEquals(
//        expected, add01, sumFailureMsg("b0.add(b1) vs b0.add(z1)",
//                                       z0, z1, b0, b1, expected, add01));
//      final DoubleInterval add10 = b1.add(z0);
//      Assertions.assertEquals(
//        expected, add10, sumFailureMsg("b0.add(b1) vs b1.add(z0)",
//                                       z0, z1, b0, b1, expected, add10));
//      final DoubleInterval sum10 = DoubleInterval.sum(z0, z1);
//      Assertions.assertEquals(
//        expected, sum10, sumFailureMsg("b0.add(b1) vs sum(z1,z0)",
//                                       z0, z1, b0, b1, expected, sum10));
//      final DoubleInterval sum01 = DoubleInterval.sum(z0, z1);
//      Assertions.assertEquals(
//        expected, sum01, sumFailureMsg("b0.add(b1) vs sum(z0,z1)",
//                                       z0, z1, b0, b1, expected, sum01));
//    }
//  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
