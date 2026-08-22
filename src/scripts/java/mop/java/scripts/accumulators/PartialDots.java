package mop.java.scripts.accumulators;

import org.apache.commons.rng.UniformRandomProvider;

import mop.java.accumulators.Accumulator;
import mop.java.accumulators.BigFloatAccumulator;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;

/** Benchmark partial dot products.
 *
 * <pre>
 * jy --source 25 src/scripts/java/xfp/java/scripts/PartialDots.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */
@SuppressWarnings("unused")
public final class PartialDots {

  public static final void main (final String[] args) {
    //Debug.DEBUG=false;
    final int n = (8*1024*1024) - 1;
    final int trys = 1024;
    final UniformRandomProvider urp =
      PRNG.well44497b("seeds/Well44497b-2019-01-09.txt");
    final int emax = Doubles.deMax(n)/2;
    final Generator g = Doubles.finiteGenerator(n,urp,emax);
    final Accumulator a = BigFloatAccumulator.make();
    assert a.isExact();
    for (int i=0;i<trys;i++) {
      final double[] x0 = (double[]) g.next();
      final double[] x1 = (double[]) g.next();
      final double[] z = a.partialDots(x0,x1);
      assert ! Double.isNaN(z[n-1]);} }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
