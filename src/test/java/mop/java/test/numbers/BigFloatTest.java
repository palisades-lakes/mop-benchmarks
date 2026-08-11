package mop.java.test.numbers;

import mop.java.benchmarks.accumulate.Common;
import mop.java.numbers.*;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.rng.UniformRandomProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.BinaryOperator;

//----------------------------------------------------------------

/**
 * Test desired properties of BigFloat.
 * <p>
 * <pre>
 * mvn -q -Dtest=mop.java.test.numbers.BigFloatTest test > BFT.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-10
 */

public final class BigFloatTest {

  private static final int TRYS = 513;

  private static final BinaryOperator dist =
    (q0, q1) -> ((BigFloat) q0).subtract((BigFloat) q1).abs();

  @SuppressWarnings({ "static-method" })
  @Test
  public final void testRounding () {
    //Debug.DEBUG=false;
    final BigFloat[] f =
      {
        BigFloat.valueOf(
          true, BoundedNatural.valueOf("232330747ceeab", 0x10), -23),
        BigFloat.valueOf(
          false, BoundedNatural.valueOf("232330747ceeab", 0x10), -23),
        BigFloat.valueOf(
          true, BoundedNatural.valueOf("2366052b8b801d", 0x10), -22),
        BigFloat.valueOf(
          false, BoundedNatural.valueOf("21ab528c4dbc181", 0x10), -26),
        BigFloat.valueOf(
          true, BoundedNatural.valueOf("8d9814ae2e0074", 0x10), -25),
        BigFloat.valueOf(
          true, BoundedNatural.valueOf("2c94d1dcb123a56b9c1", 0x10),
          -43),
        };
    for (final BigFloat fi : f) {
      Common.doubleRoundingTest(
        BigFloat::valueOf, Numbers::doubleValue, dist,
        Object::toString, fi,
        Common::compareTo, Common::compareTo);
      Common.floatRoundingTest(
        BigFloat::valueOf, Numbers::floatValue, dist,
        Object::toString, fi, Common::compareTo, Common::compareTo);
    }
    //Debug.DEBUG=false;

    Common.doubleRoundingTests(
      null, BigFloat::valueOf, Numbers::doubleValue, dist,
      Object::toString, Common::compareTo, Common::compareTo);

    Common.floatRoundingTests(
      null, BigFloat::valueOf, Numbers::floatValue, dist,
      Object::toString, Common::compareTo, Common::compareTo);
    //Debug.DEBUG=false;
  }

  @Test
  public final void squareTest () {
    final Generator g =
      BigFloats.fromBigIntegerGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-05.txt"));
    for (int i = 0; i < TRYS; i++) {
      final BigFloat x = (BigFloat) g.next();
      final BigFloat x2 = x.square();
      final BigFloat xx = x.multiply(x);
      Assertions.assertEquals(x2, xx); } }

  @Test
  public final void l2norm2Test () {
    final Generator g =
      BigFloats.fromBigIntegerGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-05.txt"));
    for (int i = 0; i < TRYS; i++) {
      final BigFloat x = (BigFloat) g.next();
      final BigFloat y = (BigFloat) g.next();
      final BigFloat l20 = BigFloat.l2norm2(x, y);
      final BigFloat l21 = x.square().add(y.square());
      Assertions.assertEquals(l20,l21); } }

  private static final String sumFailureMsg (final String name,
                                             final double z0,
                                             final double z1,
                                             final BigFloat b0,
                                             final BigFloat b1,
                                             final BigFloat s0,
                                             final BigFloat s1) {
    return
      "\n" + name +
        "\nz0=" + Double.toHexString(z0) +
        "\nnonnegative(z0)= " + Doubles.nonNegative(z0) +
        "\nsignificand(z0)= " + Doubles.significand(z0) +
        "\nexponent(z0)= " + Doubles.exponent(z0) +
        "\nb0=" + b0.toHexString() +
        " (" + Double.toHexString(b0.doubleValue()) + ")" +
        "\nz1=" + Double.toHexString(z1) +
        "\nnonnegative(z1)= " + Doubles.nonNegative(z1) +
        "\nsignificand(z1)= " + Doubles.significand(z1) +
        "\nexponent(z1)= " + Doubles.exponent(z1) +
        "\nb1=" + b1.toHexString() +
          " (" + Double.toHexString(b1.doubleValue()) + ")" +
        "\ns0=" + s0.toHexString() +
        " (" + Double.toHexString(s0.doubleValue()) + ")" +
        "\ns1=" + s1.toHexString() +
        " (" + Double.toHexString(s1.doubleValue()) + ")";
  }

  @Test
  public final void sumTest () {
    final UniformRandomProvider urp =
      PRNG.well44497b("seeds/Well44497b-2019-01-09.txt");
    final Generator g =
      Doubles.laplaceGenerator(urp, 0.0, 1000.0);
    for (int i = 0; i < TRYS; i++) {
      final double z0 = g.nextDouble();
      final double z1 = g.nextDouble();
      final BigFloat b0 = BigFloat.valueOf(z0);
      final BigFloat b1 = BigFloat.valueOf(z1);
      final BigFloat expected = b0.add(b1);
      final BigFloat add01 = b0.add(z1);
      Assertions.assertEquals(
        expected, add01, sumFailureMsg("b0.add(b1) vs b0.add(z1)",
                                       z0, z1, b0, b1, expected, add01));
      final BigFloat add10 = b1.add(z0);
      Assertions.assertEquals(
        expected, add10, sumFailureMsg("b0.add(b1) vs b1.add(z0)",
                                       z0, z1, b0, b1, expected, add10));
      final BigFloat sum10 = BigFloat.sum(z0, z1);
      Assertions.assertEquals(
        expected, sum10, sumFailureMsg("b0.add(b1) vs sum(z1,z0)",
                                       z0, z1, b0, b1, expected, sum10));
      final BigFloat sum01 = BigFloat.sum(z0, z1);
      Assertions.assertEquals(
        expected, sum01, sumFailureMsg("b0.add(b1) vs sum(z0,z1)",
                                       z0, z1, b0, b1, expected, sum01));
    }
  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
