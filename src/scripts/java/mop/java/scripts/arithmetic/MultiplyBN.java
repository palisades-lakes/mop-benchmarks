package mop.java.scripts.arithmetic;

import java.math.BigInteger;

import mop.java.numbers.BoundedNatural;
import mop.java.numbers.Naturals;
import mop.java.prng.Generator;
import mop.java.prng.Generators;
import mop.java.prng.PRNG;

//----------------------------------------------------------------
/** <pre>
 * jy --enable-preview --source 25 src/scripts/java/mop/java/scripts/profile/arithmetic/MultiplyBN.java
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 *
 */

@SuppressWarnings("unused")
public final class MultiplyBN {

  private static final Naturals NATURALS = Naturals.get();
  private static final int FACTOR = 32;
  private static final int NBYTES = FACTOR * 256;
  private static final int NINTS = 1024 * 1024 / FACTOR;

  private static final Generator generator =
    Generators.nonNegativeBigIntegerGenerator(
    NBYTES, PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"), NINTS);

  private static final BigInteger[] x0 = (BigInteger[]) generator.next();
  private static final BigInteger[] x1 = (BigInteger[]) generator.next();

  private static final BoundedNatural[] fromBigInteger (final BigInteger[] x) {
    final int n = x.length;
    final BoundedNatural[] y = new BoundedNatural[n];
    for (int i=0;i<n;i++) { y[i] = BoundedNatural.valueOf(x[i]); }
    return y; }

  private static final BoundedNatural[] y0 = fromBigInteger(x0);
  private static final BoundedNatural[] y1 = fromBigInteger(x1);

  private static final BoundedNatural[] p = new BoundedNatural[NINTS];

  private static final BoundedNatural[] multiply (final String stage,
                                      final int iterations) {
    final int n = y0.length;
    for (int j=0;j<iterations;j++) {
      final long t0 = System.nanoTime();
      for (int i=0;i<n;i++) {
        p[i] =  y0[i].multiply(y1[i]); }
      System.out.printf(stage + " Total seconds: %4.3f\n",
                        (System.nanoTime() - t0) * 1.0e-9); }
    return p; }

  // distinguish warmup run from profile run in call tree
  private static final BoundedNatural[] warmup () {
    return multiply("warmup",4); }
  private static final BoundedNatural[] profile () {
    return multiply("profile",16); }

  //--------------------------------------------------------------

  public static final void main (final String[] args) {
    System.out.println(warmup().length);
    System.out.println(profile().length); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
