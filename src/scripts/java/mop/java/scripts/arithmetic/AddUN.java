package mop.java.scripts.arithmetic;

import mop.java.numbers.UnboundedNatural;
import mop.java.prng.Generator;
import mop.java.prng.Generators;
import mop.java.prng.PRNG;

import java.math.BigInteger;

//----------------------------------------------------------------

/** Profile natural number division.
 * <p>
 * <pre>
 * jy --enable-preview --source 21 src/scripts/java/nzqr/java/scripts/profile/arithmetic/AddUN.java
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2024-01-2
 */

public final class AddUN {
  private static final int FACTOR = 1;
  private static final int NBYTES = FACTOR * 256;
  private static final int NINTS = 1024 * 1024 / FACTOR;

  private static final Generator generator =
    Generators.nonNegativeBigIntegerGenerator(
      NBYTES,
      PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
      NINTS);

  private static final BigInteger[] x0 = (BigInteger[]) generator.next();
  private static final BigInteger[] x1 = (BigInteger[]) generator.next();

  private static final UnboundedNatural[] fromBigInteger (final BigInteger[] x) {
    final int n = x.length;
    final UnboundedNatural[] y = new UnboundedNatural[n];
    for (int i=0;i<n;i++) { y[i] = UnboundedNatural.valueOf(x[i]); }
    return y; }

  private static final UnboundedNatural[] y0 = fromBigInteger(x0);
  private static final UnboundedNatural[] y1 = fromBigInteger(x1);
  private static final UnboundedNatural[] p = new UnboundedNatural[NINTS];

  private static final void add (final String stage,
                                 final int iterations) {
    final int n = y0.length;
    for (int j=0;j<iterations;j++) {
      final long t0 = System.nanoTime();
      for (int i=0;i<n;i++) {
        p[i] = y0[i].add(y1[i]);
        assert y1[i].compareTo(p[i]) <= 0; }
      System.out.printf(stage + " " + j + " Total seconds: %4.3f\n",
        (System.nanoTime()-t0)*1.0e-9); } }

  // distinguish warmup run from profile run in call tree
  private static final void warmup () { add("warmup",4); }
  private static final void profile () { add("profile",32); }

  //--------------------------------------------------------------

  public static final void main (final String[] args) {
    warmup(); profile();   }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
