package mop.java.test.numbers;

import mop.java.benchmarks.accumulate.Common;
import mop.java.numbers.Numbers;
import mop.java.numbers.XDouble;
import org.junit.jupiter.api.Test;

import java.util.function.BinaryOperator;

//----------------------------------------------------------------

/** Test desired properties of XDouble.
 * <p>
 * <pre>
 * mvn -q -Dtest=xfp/java/test/numbers/XDoubleTest test > XD.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-22
 */

public final class XDoubleTest {

//  private static final int TRYS = 257;

  private static final BinaryOperator dist =
    (q0,q1) -> ((XDouble) q0).subtract((XDouble) q1).abs();

  @SuppressWarnings({ "static-method" })
  @Test
  public final void testRounding () {
    //Debug.DEBUG=false;
//    final XDouble[] f =
//    {
//      XDouble.valueOf(
//      BigFloat.valueOf(
//       true,
//       BoundedNatural.valueOf("232330747ceeab",0x10),-23)),
//      XDouble.valueOf(
//        BigFloat.valueOf(
//       false,
//       BoundedNatural.valueOf("232330747ceeab",0x10),-23)),
//        XDouble.valueOf(
//          BigFloat.valueOf(
//       true,
//       BoundedNatural.valueOf("2366052b8b801d",0x10),-22)),
//          XDouble.valueOf(
//            BigFloat.valueOf(
//       false,
//       BoundedNatural.valueOf("21ab528c4dbc181",0x10),-26)),
//            XDouble.valueOf(
//              BigFloat.valueOf(
//       true,
//       BoundedNatural.valueOf("8d9814ae2e0074",0x10),-25)),
//              XDouble.valueOf(
//                BigFloat.valueOf(
//       true,
//       BoundedNatural.valueOf("2c94d1dcb123a56b9c1",0x10),-43)), };
//    for (final XDouble fi : f) {
//      Common.doubleRoundingTest(
//        XDouble::valueOf,Numbers::doubleValue,dist,
//        Object::toString,fi,
//        Common::compareTo, Common::compareTo);
//      Common.floatRoundingTest(
//        XDouble::valueOf,Numbers::floatValue,dist,
//        Object::toString,fi, Common::compareTo, Common::compareTo);  }
    //Debug.DEBUG=false;

    Common.doubleRoundingTests(
      null, XDouble::valueOf, Numbers::doubleValue, dist,
      Object::toString, Common::compareTo, Common::compareTo);

    Common.floatRoundingTests(
      null, XDouble::valueOf,Numbers::floatValue,dist,
      Object::toString, Common::compareTo, Common::compareTo);

    //Debug.DEBUG=false;
  }

  // TODO: need to implement multiplication for XDouble to make it
  //  field-like
//  @SuppressWarnings("static-method")
//  @Test
//  public final void squareTest () {
//    final Generator g =
//      BigFloats.fromBigIntegerGenerator(
//        PRNG.well44497b("seeds/Well44497b-2019-01-05.txt"));
//    for (int i=0;i<TRYS;i++) {
//      final XDouble x = XDouble.valueOf((BigFloat) g.next());
//      final XDouble x2 = x.square();
//      final XDouble xx = x.multiply(x);
//      Assertions.assertEquals(x2,xx); } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
