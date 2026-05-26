package mop.java.test.numbers;

import mop.java.benchmarks.accumulate.Common;
import mop.java.numbers.BigFloat;
import mop.java.numbers.Numbers;
import mop.java.numbers.XDouble;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.BinaryOperator;

//----------------------------------------------------------------

/** <pre>
 * mvn -Dtest=mop/java/test/numbers/XDoubleTest test > XD.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-24
 */

public final class XDoubleTest {

//  private static final int TRYS = 257;

  private static final BinaryOperator dist =
    (q0,q1) -> ((XDouble) q0).subtract((XDouble) q1).abs();

  @Test
  public final void testAssociativity () {
    final XDouble a = XDouble.valueOf(0x1.64c2c2746c402p654,
                                      -0x1.e9796e57a6d0bp715);
    final XDouble b = XDouble.valueOf(0x1.d5ea1540c55e6p-563,
                                      -0x1.e5846a4182e7fp-500);
    final XDouble c = XDouble.valueOf(-0x1.aee3b1f8ecc46p-470,
                                      -0x1.779975bb25c8bp-279);
    final XDouble abC = a.add(b).add(c);
    final XDouble bc = b.add(c);
    final XDouble aBC = a.add(bc);
    final BigFloat abCBF = abC.bigFloatValue();
    final BigFloat aBCBF = aBC.bigFloatValue();
    Assertions.assertEquals(
      aBC, abC,
      "\na=\n" + a +
        "\nb=\n" + b +
        "\nc=\n" + c +
        "\na.add(b)=\n" + a.add(b) +
        "\nb.add(c)=\n" + bc +
        "\na.add(b).add(c)=\n" + abC +
        "\na.add(b.add(c)))=\n" + aBC +
        "\nabCBF=\n" + abCBF +
        "\naBCBF=\n" + aBCBF +
      "\nabCBF==aBCBF:" + abCBF.equals(aBCBF)+
      "\nabC-aBC=\n" + abC.subtract(aBC) + "\n"); }

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
      null, XDouble::valueOf,Numbers::floatValue, dist,
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
