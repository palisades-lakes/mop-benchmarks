package mop.java.test.numbers;

import mop.java.Classes;
import mop.java.numbers.BigFloat;
import mop.java.numbers.Doubles;
import mop.java.numbers.Hilo;
import mop.java.numbers.RationalFloat;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

//----------------------------------------------------------------
/** Test number conversions expected to be lossless.
 * <p>
 * <pre>
 * mvn -Dtest=mop/java/test/numbers/RoundtripTests test > RoundtripTests.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-19
 */

public final class RoundtripTests {

  private static final int TRYS = 32*1024;

  public static final Generator finiteDoubles () {
    return
      Doubles.finiteGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-05.txt")); }

  public static final Generator subnormalDoubles () {
    return
      Doubles.subnormalGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-05.txt")); }

  /** 'normal' as opposed to 'subnormal', not gaussian! */

  public static final Generator normalDoubles () {
    return
      Doubles.normalGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-05.txt")); }

  //--------------------------------------------------------------
  /** Hilo should be able to represent any double exactly.
   */

  private static final boolean double2Hilo2Double () {
    for (final Generator g : List.of(
      finiteDoubles(), subnormalDoubles(), normalDoubles())) {
      for (int i=0;i<TRYS;i++) {
        final double x = g.nextDouble();
        final Hilo f = Hilo.valueOf(x);
        final double xf = f.doubleValue();
        if (x != xf) {
          System.out.println("\n\n" +
                               "Hilo.doubleValue:" + Doubles.isNormal(x) +"\n" +
                               x + "\n" +
                               xf + "\n\n" +
                               Double.toHexString(x) + "\n" +
                               Double.toHexString(xf) + "\n\n" +
                               f + "\n"
            //+ f.toHexString(f) + "\n"
                            );
          return false; } } }
    return true; }

   //--------------------------------------------------------------
  /** BigDecimal should be able to represent any double exactly.
   */

  private static final boolean double2BigDecimal2Double () {
    for (final Generator g : List.of(
      finiteDoubles(), subnormalDoubles(), normalDoubles())) {
      for (int i=0;i<TRYS;i++) {
        final double x = g.nextDouble();
        final BigDecimal f = new BigDecimal(x);
        final double xf = f.doubleValue();
        if (x != xf) {
          System.out.println("\n\n" +
                               "BigDecimal.ToDouble:" + Doubles.isNormal(x) +"\n" +
                               x + "\n" +
                               xf + "\n\n" +
                               Double.toHexString(x) + "\n" +
                               Double.toHexString(xf) + "\n\n" +
                               f + "\n"
            //+ f.toHexString(f) + "\n"
                            );
          return false; } } }
    return true; }

  //--------------------------------------------------------------
  /** RationalFloat should be able to represent any double exactly.
   */

  private static final boolean double2RF2Double () {
    for (final Generator g : List.of(
      finiteDoubles(), subnormalDoubles(), normalDoubles())) {
      for (int i=0;i<TRYS;i++) {
        final double x = g.nextDouble();
        final RationalFloat f = RationalFloat.valueOf(x);
        final double xf = f.doubleValue();
        if (x != xf) {
          System.out.println("\n\n" +
                               Classes.className(f) +
                               ".doubleValue:" + Doubles.isNormal(x) +"\n" +
                               x + "\n" +
                               xf + "\n\n" +
                               Double.toHexString(x) + "\n" +
                               Double.toHexString(xf) + "\n\n" +
                               f.numerator() + "\n" +
                               f.denominator() + "\n" +
                               f.exponent() + "\n\n" +
                               f.numerator().toHexString() + "\n" +
                               f.denominator().toHexString());
          return false; } } }
    return true; }

  //--------------------------------------------------------------
  /** BigFloat should be able to represent any double exactly.
   */

  private static final boolean double2BF2Double () {
    for (final Generator g : List.of(
      finiteDoubles(), subnormalDoubles(), normalDoubles())) {
      for (int i=0;i<TRYS;i++) {
        final double x = g.nextDouble();
        final BigFloat f = BigFloat.valueOf(x);
        final double xf = f.doubleValue();
        if (x != xf) {
          System.out.println("\n\n" +
                               Classes.className(f) +
                               ".doubleValue:" + Doubles.isNormal(x) +"\n" +
                               x + "\n" +
                               xf + "\n\n" +
                               Double.toHexString(x) + "\n" +
                               Double.toHexString(xf) + "\n\n" +
                               f.nonNegative() + "\n0x" +
                               f.significand().toHexString() + "\n" +
                               f.exponent() + "\n");
          return false; } } }
    return true; }

  //--------------------------------------------------------------
  /** check for round trip consistency:
   * double -&gt; rational -&gt; double
   * should be an identity transform.
   */
  @Test
  public final void roundTripTest () {

    assertTrue(double2Hilo2Double());
    assertTrue(double2BF2Double());
    assertTrue(double2RF2Double());
    assertTrue(double2BigDecimal2Double());

  }
  //--------------------------------------------------------------
}
//--------------------------------------------------------------
