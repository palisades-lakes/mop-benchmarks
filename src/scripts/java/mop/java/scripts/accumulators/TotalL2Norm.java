package mop.java.scripts.accumulators;

import mop.java.accumulators.Accumulator;
import mop.java.accumulators.BigFloatAccumulator;
import mop.java.prng.Generator;
import mop.java.prng.Generators;

/** Benchmark sums.
 *
 * <pre>
 * jy --source 25 src/scripts/java/xfp/java/scripts/TotalL2Norm.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */
@SuppressWarnings("unused")
public final class TotalL2Norm {

  public static final void main (final String[] args) {
    final int dim = 524289;//(8*1024*1024) - 1;
    final int trys = 1024;
    final Generator g = Generators.make("finite",dim);
    final Accumulator a = BigFloatAccumulator.make();
    assert a.isExact();
    for (int i=0;i<trys;i++) {
      final double[] x = (double[]) g.next();
      final double z = a.clear().add2All(x).doubleValue();
      assert Double.isFinite(z); } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
