package mop.java.test.numbers;

import java.util.function.BinaryOperator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import mop.java.numbers.BigFloat;
import mop.java.numbers.BigFloats;
import mop.java.numbers.BoundedNatural;
import mop.java.numbers.Numbers;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import mop.java.benchmarks.accumulate.Common;

//----------------------------------------------------------------
/** Test desired properties of BigFloat.
 * <p>
 * <pre>
 * mvn -q -Dtest=xfp/java/test/numbers/BigFloatTest test > BFT.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2022-11-07
 */

public final class BigFloatTest {

  private static final int TRYS = 257;

  private static final BinaryOperator dist = (q0,q1) -> ((BigFloat) q0).subtract((BigFloat) q1).abs();

  @SuppressWarnings({ "static-method" })
  @Test
  public final void testRounding () {
    //Debug.DEBUG=false;
    final BigFloat[] f =
    {
     BigFloat.valueOf(
       true,BoundedNatural.valueOf("232330747ceeab",0x10),-23),
     BigFloat.valueOf(
       false,BoundedNatural.valueOf("232330747ceeab",0x10),-23),
     BigFloat.valueOf(
       true,BoundedNatural.valueOf("2366052b8b801d",0x10),-22),
     BigFloat.valueOf(
       false,BoundedNatural.valueOf("21ab528c4dbc181",0x10),-26),
     BigFloat.valueOf(
       true,BoundedNatural.valueOf("8d9814ae2e0074",0x10),-25),
     BigFloat.valueOf(
       true,BoundedNatural.valueOf("2c94d1dcb123a56b9c1",0x10),-43), };
    for (final BigFloat fi : f) {
      Common.doubleRoundingTest(
        BigFloat::valueOf,Numbers::doubleValue,dist,
        Object::toString,fi,
        Common::compareTo, Common::compareTo);
      Common.floatRoundingTest(
        BigFloat::valueOf,Numbers::floatValue,dist,
        Object::toString,fi, Common::compareTo, Common::compareTo);  }
    //Debug.DEBUG=false;

    Common.doubleRoundingTests(
      null,BigFloat::valueOf,Numbers::doubleValue,dist,
      Object::toString, Common::compareTo, Common::compareTo);

    Common.floatRoundingTests(
      null,BigFloat::valueOf,Numbers::floatValue,dist,
      Object::toString, Common::compareTo, Common::compareTo);
    //Debug.DEBUG=false;
  }

  @SuppressWarnings("static-method")
  @Test
  public final void squareTest () {
    final Generator g =
      BigFloats.fromBigIntegerGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-05.txt"));
    for (int i=0;i<TRYS;i++) {
      final BigFloat x = (BigFloat) g.next();
      final BigFloat x2 = x.square();
      final BigFloat xx = x.multiply(x);
      Assertions.assertEquals(x2,xx); } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
