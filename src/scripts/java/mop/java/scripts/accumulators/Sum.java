package mop.java.scripts.accumulators;

import mop.java.accumulators.Accumulator;
import mop.java.accumulators.RationalFloatAccumulator;
import mop.java.numbers.Doubles;

// java -ea --illegal-access=warn -jar target/benchmarks.jar

/** Benchmark algebraic structure tests.
 *
 * <pre>
 * jy --source 25 src/scripts/java/xfp/java/scripts/Sum.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */
@SuppressWarnings("unused")
public final class Sum {

  //--------------------------------------------------------------

  private static final int DIM = 1024*1024;

  private static final int TRYS = 32;

  public static final void main (final String[] args)
    throws InterruptedException {
    final double[] x0 = Doubles.sampleDoubles(DIM,1)[0];

    final Accumulator a = RationalFloatAccumulator.make();
    Thread.sleep(16*1024);
    final long t = System.nanoTime();
    for (int i=0;i<TRYS;i++) {
      a.clear();
      a.addAll(x0);
      if ((2.0*Math.ulp(1.0)) > a.doubleValue()) {
        System.out.println("false"); } }
    System.out.printf("total secs: %8.2f\n",
                      (System.nanoTime() - t) * 1.0e-9);
    Thread.sleep(16*1024); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
