package mop.java.test.numbers;

//----------------------------------------------------------------

import mop.java.numbers.RoundingInterval;
import mop.java.numbers.RoundingIntervals;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Test desired properties of RoundingInterval.
 * <p>
 * <pre>
 * mvn -q -Dtest=mop.java.test.numbers.RoundingIntervalTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-11
 */

@SuppressWarnings("unused")
public final class RoundingIntervalTest {

  private static final int TRYS = 257;

  @Test
  public final void l2norm2Test () {
    final Generator g =
      RoundingIntervals.fromDoubleGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-09.txt"));
    for (int i = 0; i < TRYS; i++) {
      final RoundingInterval x = (RoundingInterval) g.next();
      final RoundingInterval y = (RoundingInterval) g.next();
      final RoundingInterval l20 = RoundingInterval.l2norm2(x, y);
      final RoundingInterval l21 = x.square().add(y.square());
      Assertions.assertEquals(
        l20,l21,
        "\nx=" + x.toHexString() +
          "\ny=" + y.toHexString() +
          "\nl20=" + l20.toHexString() +
          "\nl21=" + l21.toHexString()); } }

//  private static final String sumFailureMsg (final String name,
//                                             final double z0,
//                                             final double z1,
//                                             final RoundingInterval b0,
//                                             final RoundingInterval b1,
//                                             final RoundingInterval s0,
//                                             final RoundingInterval s1) {
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
//      final RoundingInterval b0 = RoundingInterval.valueOf(z0);
//      final RoundingInterval b1 = RoundingInterval.valueOf(z1);
//      final RoundingInterval expected = b0.add(b1);
//      final RoundingInterval add01 = b0.add(z1);
//      Assertions.assertEquals(
//        expected, add01, sumFailureMsg("b0.add(b1) vs b0.add(z1)",
//                                       z0, z1, b0, b1, expected, add01));
//      final RoundingInterval add10 = b1.add(z0);
//      Assertions.assertEquals(
//        expected, add10, sumFailureMsg("b0.add(b1) vs b1.add(z0)",
//                                       z0, z1, b0, b1, expected, add10));
//      final RoundingInterval sum10 = RoundingInterval.sum(z0, z1);
//      Assertions.assertEquals(
//        expected, sum10, sumFailureMsg("b0.add(b1) vs sum(z1,z0)",
//                                       z0, z1, b0, b1, expected, sum10));
//      final RoundingInterval sum01 = RoundingInterval.sum(z0, z1);
//      Assertions.assertEquals(
//        expected, sum01, sumFailureMsg("b0.add(b1) vs sum(z0,z1)",
//                                       z0, z1, b0, b1, expected, sum01));
//    }
//  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
