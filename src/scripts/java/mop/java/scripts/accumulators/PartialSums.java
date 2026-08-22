package mop.java.scripts.accumulators;

import mop.java.accumulators.Accumulator;
import mop.java.prng.Generator;
import mop.java.prng.Generators;

/** Profile partial sums.
 *
 * <pre>
 * jy --source 25 src/scripts/java/xfp/java/scripts/PartialSums.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */
@SuppressWarnings("unused")
public final class PartialSums {

  public static final void main (final String[] args) {
    final int dim = (1024 * 1024) - 1;
    final int trys = 8 * 1024;
    //final Generator g = Generators.make("exponential",dim);
    //final Generator g = Generators.make("finite",dim);
    //final Generator g = Generators.make("gaussian",dim);
    //final Generator g = Generators.make("laplace",dim);
    final Generator g = Generators.make("uniform",dim);
    final Accumulator a =
      mop.java.accumulators.RationalFloatAccumulator.make();
    //mop.java.accumulators.BigFloatAccumulator.make();
    assert a.isExact();
    for (int i=0;i<trys;i++) {
      final double[] x = (double[]) g.next();
      final double[] s = a.partialSums(x);
      assert ! Double.isNaN(s[dim-1]); } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
