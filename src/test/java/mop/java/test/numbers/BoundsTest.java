package mop.java.test.numbers;

import java.math.BigInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import mop.java.Classes;
import mop.java.numbers.BoundedNatural;

//----------------------------------------------------------------
/** Test bounded ranges for various number implementations.
 * <p>
 * <pre>
 * mvn -q -Dtest=nzqr/java/test/numbers/BoundsTest test > BT.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2021-05-29
 */

public final class BoundsTest {

  @SuppressWarnings({ "static-method" })
  @Test
  public final void overflowBigInteger () {
    final BigInteger n0 = BigInteger.ONE
      .shiftLeft(Integer.MAX_VALUE-2)
      .subtract(BigInteger.ONE);
    final BigInteger n1 =  BigInteger.ONE
      .shiftLeft(Integer.MAX_VALUE-1)
      .add(n0)
      .add(n0);
    Assertions.assertThrows(
      ArithmeticException.class,
      () -> {
        // overflow at 2nd add
        BigInteger n = n1;
        for (long i=1L;i<=Integer.MAX_VALUE;i++) {
          n = n.add(BigInteger.ONE); } },
      Classes.className(n0)); }

  @SuppressWarnings({ "static-method" })
  @Test
  public final void overflowBoundedNatural () {
    Assertions.assertThrows(
      ArithmeticException.class,
      () -> {
        // overflow at 2nd add
        final BoundedNatural n = BoundedNatural.maxValue().add(1);
        System.out.println(n.hiBit()); },
      "Overflow BoundedNatural"); }

  //  @SuppressWarnings({ "static-method" })
  //  @Test
  //  public final void overflowNatural () {
  //    //Debug.DEBUG=false;
  //
  //    final long t0 = System.nanoTime();
  //    try {
  //      BoundedNatural n =
  ////      BoundedNatural.valueOf(0);
  //      BoundedNatural.valueOf(1)
  //        .shiftUp(BoundedNatural.MAX_BITS-1);
  //      n = n.add(n.subtract(10));
  //      for (long i=1L;i<=Long.MAX_VALUE;i++) {
  //        System.out.print("BoundedNatural: " + i + ": " +
  //          Integer.toUnsignedString(n.hiBit(),16));
  //        final long t = System.nanoTime();
  //        final BoundedNatural n1 = n.add(1L);
  //        assert n1.compareTo(n) > 0 :
  //          n1.compareTo(n) + "\n" +
  //        Integer.toUnsignedString(n1.hiBit(),16) + " " +
  //        Integer.toUnsignedString(Integer.MAX_VALUE,16);
  //        n = n1;
  //        System.out.printf(" [%4.3f]\n",
  //          Double.valueOf((System.nanoTime()-t)*1.0e-9));
  //        } }
  //    finally {
  //      System.out.printf("Total seconds: %4.3f\n",
  //        Double.valueOf((System.nanoTime()-t0)*1.0e-9)); }
  //
  //    //Debug.DEBUG=false;
  //  }


  //--------------------------------------------------------------
}
//--------------------------------------------------------------
