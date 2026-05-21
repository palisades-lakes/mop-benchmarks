package mop.java.test.numbers;

import mop.java.benchmarks.accumulate.Common;
import mop.java.numbers.*;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.BinaryOperator;

//----------------------------------------------------------------
/** Test desired properties of Hilo.
 * <p>
 * <pre>
 * mvn -q -Dmop/java/test/numbers/HiloTest test > Hilo.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-21
 */

public final class HiloTest {

  private static final int TRYS = 257;

  private static final BinaryOperator dist =
    (q0,q1) -> ((Hilo) q0).subtract((Hilo) q1).abs();

  @SuppressWarnings({ "static-method" })
  @Test
  public final void testRounding () {
    //Debug.DEBUG=false;
    final Hilo[] f =
      {
        Hilo.valueOf(
          BigFloat.valueOf(
            true,
            BoundedNatural.valueOf("232330747ceeab",0x10),
            -23)),
        Hilo.valueOf(
          BigFloat.valueOf(
            false,
            BoundedNatural.valueOf("232330747ceeab",0x10),
            -23)),
        Hilo.valueOf(
          BigFloat.valueOf(
            true,
            BoundedNatural.valueOf("2366052b8b801d",0x10),
            -22)),
        Hilo.valueOf(
          BigFloat.valueOf(
            false,
            BoundedNatural.valueOf("21ab528c4dbc181",0x10),
            -26)),
        Hilo.valueOf(
          BigFloat.valueOf(
            true,
            BoundedNatural.valueOf("8d9814ae2e0074",0x10),
            -25)),
        Hilo.valueOf(
          BigFloat.valueOf(
            true,
            BoundedNatural.valueOf("2c94d1dcb123a56b9c1",0x10),
            -43)), };
    for (final Hilo fi : f) {
      Common.doubleRoundingTest(
        Hilo::valueOf,
        Numbers::doubleValue,
        dist,
        Object::toString
        ,fi,
        Common::compareTo,
        Common::compareTo);
      Common.floatRoundingTest(
        Hilo::valueOf,
        Numbers::floatValue,
        dist,
        Object::toString,
        fi,
        Common::compareTo,
        Common::compareTo);  }
    //Debug.DEBUG=false;

    Common.doubleRoundingTests(
      null,Hilo::valueOf,Numbers::doubleValue,dist,
      Object::toString, Common::compareTo, Common::compareTo);

    Common.floatRoundingTests(
      null,Hilo::valueOf,Numbers::floatValue,dist,
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
      final BigFloat bf = (BigFloat) g.next();
      final Hilo x = Hilo.valueOf(bf);
//      assert x.isFinite() :
//        x.toHexString()
//        + "\n" + Double.toHexString(bf.doubleValue())
//        + "\n" + bf;
      final Hilo x2 = x.square();
      final Hilo xx = x.multiply(x);
      Assertions.assertEquals(x2,xx); } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
