package mop.java.scripts.accumulators;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import mop.java.numbers.BoundedNatural;
import mop.java.prng.Generator;
import mop.java.prng.Generators;
import mop.java.prng.PRNG;

/** Compare BigInteger and BoundedNatural results.
 * At least 4*2<sup>32</sup> tests to get calls to BoundedNatural.divadd.
 *
 * <pre>
 * jy --source 20 -ea src/scripts/java/mop/java/scripts/Division.java > division.txt
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */
@SuppressWarnings("unused")
public final class Division {

  private static final void
  divideAndRemainder (final BigInteger x0,
                      final BigInteger x1) {
    if (0 != x1.signum()) {
      final BoundedNatural y0 = BoundedNatural.valueOf(x0);
      final BoundedNatural y1 = BoundedNatural.valueOf(x1);
      final BigInteger[] xqr = x0.divideAndRemainder(x1);
      final BigInteger xq = xqr[0];
      final BigInteger xr = xqr[1];
      final List<BoundedNatural> yqr = Arrays.asList(y0.divideAndRemainder(y1));
      final BigInteger yq = yqr.get(0).toBigInteger();
      final BigInteger yr = yqr.get(1).toBigInteger();

      assert xq.equals(yq) :
        "\nquotients differ!"
        + "\n" + x0.toString(0x10)
        + "\n / "
        + "\n" +  x1.toString(0x10)
        + "\n -> "
        + "\n" + xq.toString(0x10)
        + "\n" + y0
        + "\n / "
        + "\n" +  y1
        + "\n -> "
        + "\n" + yq.toString(0x10);

      assert xr.equals(yr) :
        "\nremainders differ!"
        + "\n" + x0.toString(0x10)
        + "\n / "
        + "\n" +  x1.toString(0x10)
        + "\n -> "
        + "\n" + xr.toString(0x10)
        + "\n" + y0
        + "\n / "
        + "\n" +  y1
        + "\n -> "
        + "\n" + yr.toString(0x10); } }

  //--------------------------------------------------------------

  private static final long TRYS = (1L << 32);

  public static final void main (final String[] args) {
    final Generator gn =
      Generators.bigIntegerGenerator(
        1024, PRNG.well44497b("seeds/Well44497b-2019-01-05.txt"));
    for (long i=0;i<TRYS;i++) {
      if (0L==(i%(1L<<20))) { System.out.println(i); }
      divideAndRemainder(
        ((BigInteger) gn.next()).abs(),
        ((BigInteger) gn.next()).abs()); } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
