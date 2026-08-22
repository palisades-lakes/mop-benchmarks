package mop.java.scripts.accumulators;

import static java.lang.Double.toHexString;

import mop.java.Classes;
import mop.java.accumulators.Accumulator;
import mop.java.accumulators.RationalFloatAccumulator;
import mop.java.numbers.Doubles;

/** Benchmark rational binary float dot products.
 *
 * <pre>
 * jy --source 25 src/scripts/java/xfp/java/scripts/RationalBFDot.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */
@SuppressWarnings("unused")
public final class RationalBFDot {

  //--------------------------------------------------------------

  private static final int DIM = 16 * 1024 * 1024;
  private static final int N = 16;

  //--------------------------------------------------------------

  public static final void main (final String[] args) throws InterruptedException  {

    final double[][] x0 = Doubles.sampleDoubles(DIM,N);
    final double[][] x1 = Doubles.sampleDoubles(DIM,N);

    // should be zero with current construction
    final double[] truth = new double[N];
    final double[] pred = new double[N];
    for (int i=0;i<N;i++) {
      truth[i] =
        RationalFloatAccumulator.make().addProducts(x0[i],x1[i]).doubleValue(); }

    for (int i=0;i<N;i++) {
      System.out.println(
        i + " : "
          + Double.toHexString(truth[i])
          + ", "
          + Double.toHexString(Doubles.maxAbs(x0[i]))
          + ", "
          + Double.toHexString(Doubles.maxAbs(x1[i]))); }
    System.out.println();
    final Accumulator[] accumulators =
    {
     RationalFloatAccumulator.make(),
    };

    Thread.sleep(16*1024);
    for (final Accumulator a : accumulators) {
      long t;
      t = System.nanoTime();
      for (int i=0;i<N;i++) {
        pred[i] =
          a.clear().addProducts(x0[i],x1[i]).doubleValue();
        assert pred[i] == truth[i]; }
      t = (System.nanoTime()-t);

      System.out.println(toHexString(Doubles.l1Dist(truth,pred)) +
        " in " + (t*1.0e-9)
        + " secs " + Classes.className(a)); }

    //    Thread.sleep(16*1024);
  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
