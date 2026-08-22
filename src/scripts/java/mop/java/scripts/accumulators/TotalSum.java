package mop.java.scripts.accumulators;

import mop.java.prng.Generator;
import mop.java.prng.Generators;

/** Benchmark sums.
 *
 * <pre>
 * jy --source 25 src/scripts/java/xfp/java/scripts/TotalSum.java
 * j --source 25 src/scripts/java/xfp/java/scripts/TotalSum.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */
@SuppressWarnings("unused")
public final class TotalSum {

  public static final void main (final String[] args) {
    final int dim = (1024*1024) - 1;
    final int trys = 8 * 1024;
    //final Generator g = Generators.make("exponential",dim);
    final Generator g = Generators.make("finite",dim);
    //final Generator g = Generators.make("gaussian",dim);
    //final Generator g = Generators.make("laplace",dim);
    //final Generator g = Generators.make("uniform",dim);
    for (int i=0;i<trys;i++) {
      final double[] x = (double[]) g.next();
      mop.java.numbers.BigFloat b = mop.java.numbers.BigFloat.ZERO;
      //mop.java.numbers.BigFloat0 f = mop.java.numbers.BigFloat0.ZERO;
      for (final double xi : x) { b = b.add(xi); }
      final double z = b.doubleValue();
      assert Double.isFinite(z);
    }
  }
  //  public static final void main (final String[] args) {
  //    final int dim = 8*1024*1024;
  //    final int trys = 8 * 1024;
  //    final Generator g = Generators.make("exponential",dim);
  //    //final Generator g = Generators.make("finite",dim);
  //    //final Generator g = Generators.make("gaussian",dim);
  //    //final Generator g = Generators.make("laplace",dim);
  //    //final Generator g = Generators.make("uniform",dim);
  //    for (int i=0;i<trys;i++) {
  //      final double[] x0 = (double[]) g.next();
  //      BigFloat0 bf0 = BigFloat0.ZERO;
  //      BigFloat bf1 = BigFloat.ZERO;
  //      for (final double xi : x0) {
  //        bf0 = bf0.add(xi);
  //        bf1 = bf1.add(xi); }
  //      final double s0 = bf0.doubleValue();
  //      assert Double.isFinite(s0);
  //      final double s1 = bf1.doubleValue();
  //      assert Double.isFinite(s1);
  //      assert s0==s1;
  //      }
  //   }
  //  public static final void main (final String[] args) {
  //    final int dim = 2*1024*1024;
  //    final int trys = 8 * 1024;
  //    final Generator g = Generators.make("exponential",dim);
  //    //final Generator g = Generators.make("finite",dim);
  //    //final Generator g = Generators.make("gaussian",dim);
  //    //final Generator g = Generators.make("laplace",dim);
  //    //final Generator g = Generators.make("uniform",dim);
  //    final Accumulator a0 =
  //      mop.jmh.accumulators.BigFloatAccumulator0.make();
  //    final Accumulator a1 =
  //      mop.java.accumulators.BigFloatAccumulator.make();
  //    assert a0.isExact();
  //    assert a1.isExact();
  //    //Debug.DEBUG=true;
  //    for (int i=0;i<trys;i++) {
  //      final double[] x0 = (double[]) g.next();
  //      final double s1 = a1.clear().addAll(x0).doubleValue();
  //      assert Double.isFinite(s1);
  //      final double s0 = a0.clear().addAll(x0).doubleValue();
  //      assert Double.isFinite(s0);
  //      }
  //   }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
