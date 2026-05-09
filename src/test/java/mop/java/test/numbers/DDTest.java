package mop.java.test.numbers;

import mop.java.benchmarks.accumulate.Common;
import mop.java.numbers.DD;
import org.junit.jupiter.api.Test;

//----------------------------------------------------------------
/** Test desired properties of JTS-derived DD.
 * <pre>
 * mvn -Dtest=mop/java/test/numbers/DDTest test > DDTest.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-08
 */

public final class DDTest {

  @Test
  public final void testRounding () {

    Common.doubleRoundingTests(
      null,
      DD::valueOf,
      q -> ((DD) q).doubleValue(),
      (q0,q1) -> (((DD) q0).subtract((DD) q1)).abs(),
      dd -> ((DD) dd).toHexString(),
      Common::compareTo,
      Common::compareTo);

    Common.floatRoundingTests(
      null,
      DD::valueOf,
      q -> ((DD) q).floatValue(),
      (q0,q1) -> (((DD) q0).subtract((DD) q1)).abs(),
      dd -> ((DD) dd).toHexString(),
      Common::compareTo,
      Common::compareTo);

  // toSciNotation has bugs. not fixing for now.
//  @Test
//  public final void testSciNotation () {
//    final DD dd = new DD(-1.4907925411790628E-301);
//    Assertions.assertEquals("-1.4907925411790628E-301",
//                            dd.toSciNotation());
//  }

//  @Test
//  public final void testDouble () {
//    // no exception, but wrong answer (?) for lo=0.0
//   // final double d = 0x1.0p-1021;
//    final double d = 0x1.0p-977;
//    final DD dd = new DD(d,0.0);
//    System.out.println("d =" + Double.toString(d));
//    System.out.println("d =" + Double.toHexString(d));
//    System.out.println("d =" + String.format("%32.31E",d));
//    System.out.println("ulp(d) =" + Math.ulp(d));
//    System.out.println("ulp(d) =" + Double.toHexString(Math.ulp(d)));
//    System.out.println("MIN_EXPONENT =" + Double.MIN_EXPONENT);
//    System.out.println("MIN_VALUE =" + Double.toHexString(Double.MIN_VALUE));
//    System.out.println("MIN_VALUE =" + Double.toString(Double.MIN_VALUE));
//    System.out.println("dd=" + dd.toHexString());
//    System.out.println("dd=" + dd.toString());
//    Assertions.assertEquals(Double.toString(d),dd.toString());
//  }

//  @Test
//  public final void testLeadingZeros () {
//    // IllegalStateException from DD.toSciNotation
//    // "Found leading zero: "
//    final double d = 0x1.0p-997;
//    final DD dd = new DD(d);
//    System.out.println("d =" + Double.toString(d));
//    System.out.println("d =" + Double.toHexString(d));
//    System.out.println("d =" + String.format("%32.31E",d));
//    System.out.println("dd=" + dd.toString());
//    System.out.println("dd=" + dd.dump());
//    Assertions.assertEquals(Double.toString(d),dd.toString());
//  }


  }
  //--------------------------------------------------------------
}
//--------------------------------------------------------------
