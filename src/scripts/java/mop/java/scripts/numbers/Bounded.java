package mop.java.scripts.numbers;

import mop.java.numbers.BoundedNatural;
import mop.java.numbers.NaturalInts;

//----------------------------------------------------------------
/** Test bounded ranges for various number implementations.
 * <p>
 * <pre>
 * j src/scripts/java/nzqr/java/scripts/numbers/Bounded.java
 * jy src/scripts/java/nzqr/java/scripts/numbers/Bounded.java
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2022-08-25
 */

public final class Bounded {

    private static final void additionBoundedNatural () {
    final long t0 = System.nanoTime();
    try {
      final BoundedNatural one = BoundedNatural.valueOf(1);
      final BoundedNatural n0 = one
        .shiftUp(NaturalInts.MAX_BITS-2)
        .subtract(1);
      final BoundedNatural n1 = one
        .shiftUp(NaturalInts.MAX_BITS-1)
        //.add(n0)
        .add(n0);
      BoundedNatural n = n1;
      for (int i=1;i<=Integer.MAX_VALUE;i++) {
        //        System.out.print("BoundedNatural: " + i + ": " +
        //          Integer.toUnsignedString(n.hiBit(),16));
        //final long t = System.nanoTime();
        final BoundedNatural nn = n.add(1);
        assert nn.compareTo(n) > 0 :
          nn.compareTo(n) + "\n" +
          Integer.toUnsignedString(n1.hiBit(),16) + " " +
          Integer.toUnsignedString(Integer.MAX_VALUE,16);
        //        System.out.printf(" [%4.3f]\n",
        //          Double.valueOf((System.nanoTime()-t)*1.0e-9));
        n = nn;
      } }
    finally {
      System.out.printf("Total seconds: %4.3f\n",
        Double.valueOf((System.nanoTime()-t0)*1.0e-9)); } }

  //  private static final void additionBigInteger () {
  //    final long t0 = System.nanoTime();
  //    try {
  //      BigInteger n =
  //        BigInteger.valueOf(1)
  //        .shiftLeft(Integer.MAX_VALUE-1);
  //      n = n.add(n.subtract(BigInteger.TEN));
  //      for (long i=1L;i<=Long.MAX_VALUE;i++) {
  //        System.out.print("BigInteger: " + i + ": " +
  //          Integer.toUnsignedString(n.bitLength(),16));
  //        final long t = System.nanoTime();
  //        final BigInteger n1 = n.add(BigInteger.ONE);
  //        assert n1.compareTo(n) > 0;
  //        n = n1;
  //        System.out.printf(" [%4.3f]\n",
  //          Double.valueOf((System.nanoTime()-t)*1.0e-9)); } }
  //    finally {
  //      System.out.printf("Total seconds: %4.3f\n",
  //        Double.valueOf((System.nanoTime()-t0)*1.0e-9)); } }

  //  private static final void multiplicationBigInteger () {
  //
  //    final long t0 = System.nanoTime();
  //    try {
  //      BigInteger n =
  //        BigInteger.valueOf(1)
  //        .shiftLeft(1+(Integer.MAX_VALUE>>1))
  //        .subtract(BigInteger.TEN);
  //      for (long i=0L;i<=Long.MAX_VALUE;i++) {
  //        System.out.print("BigInteger: " + i + ": " +
  //          Integer.toUnsignedString(n.bitLength(),16));
  //        final long t = System.nanoTime();
  //        n = n.multiply(n);
  //        System.out.printf(" [%4.3f]\n",
  //          Double.valueOf((System.nanoTime()-t)*1.0e-9)); } }
  //    finally {
  //      System.out.printf("Total seconds: %4.3f\n",
  //        Double.valueOf((System.nanoTime()-t0)*1.0e-9)); } }

  //  private static final void multiplicationNatural() {
  //
  //    final long t0 = System.nanoTime();
  //    try {
  //      BoundedNatural n =
  //        BoundedNatural.valueOf(1,1+(Integer.MAX_VALUE>>1))
  //        .subtract(10);
  //      for (long i=0L;i<=Long.MAX_VALUE;i++) {
  //        System.out.print("BoundedNatural " +
  //          i
  //          + ": " +
  //          Integer.toUnsignedString(n.hiBit(),16));
  //        final long t = System.nanoTime();
  //        n = n.multiply(n);
  //        System.out.printf(" [%4.3f]\n",
  //          Double.valueOf((System.nanoTime()-t)*1.0e-9)); } }
  //    finally {
  //      System.out.printf("Total seconds: %4.3f\n",
  //        Double.valueOf((System.nanoTime()-t0)*1.0e-9)); } }

  public static final void main (final String[] args) {
    System.out.println();
    //overflowBoundedNatural();
    additionBoundedNatural();
    //    additionBigInteger();
  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
