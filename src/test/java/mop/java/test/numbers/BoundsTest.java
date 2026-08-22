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
 * mvn -q -Dtest=mop/java/test/numbers/BoundsTest test > BT.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-09
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

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
