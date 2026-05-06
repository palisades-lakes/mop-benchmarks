package mop.java.scripts;

import mop.java.accumulators.Accumulator;
import mop.java.prng.Generator;
import mop.java.prng.Generators;

/** Benchmark l1 norm.
 *
 * <pre>
 * jy --source 12 src/scripts/java/xfp/java/scripts/TotalL1Norm.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2019-10-09
 * 7
 */
@SuppressWarnings("unchecked")
public final class TotalL1Norm {

  public static final void main (final String[] args) {
    final int dim = (2*1024*1024);
    final int trys = 8 * 1024;
    //final Generator g = Generators.make("finite",dim);
    final Generator g = Generators.make("uniform",dim);
    final Accumulator a =
      mop.java.accumulators.RationalFloatAccumulator.make();
    //    mop.java.accumulators.BigFloatAccumulator.make();
    assert a.isExact();
    for (int i=0;i<trys;i++) {
      final double[] x = (double[]) g.next();
      final double z = a.clear().addAbsAll(x).doubleValue();
      assert Double.isFinite(z); } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
