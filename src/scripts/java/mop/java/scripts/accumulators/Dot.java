package mop.java.scripts.accumulators;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.toHexString;

import java.util.Arrays;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.ListSampler;

import mop.java.Classes;
import mop.java.accumulators.Accumulator;
import mop.java.accumulators.BigFloatAccumulator;
import mop.java.accumulators.DoubleAccumulator;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import mop.java.accumulators.EFloatAccumulator;
import mop.java.accumulators.ERationalAccumulator;
import mop.java.accumulators.BigDecimalAccumulator;
import mop.java.accumulators.BigFractionAccumulator;
import mop.java.accumulators.DoubleFmaAccumulator;
import mop.java.accumulators.FloatAccumulator;
import mop.java.accumulators.FloatFmaAccumulator;
import mop.java.accumulators.RatioAccumulator;

/** Benchmark double dot products.
 *
 * <pre>
 * java -ea -jar target\benchmarks.jar TotalDot
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2021-07-08
 */
@SuppressWarnings("unused")
public final class Dot {

  //--------------------------------------------------------------

  private static final double maxAbs (final double[] x) {
    double m = NEGATIVE_INFINITY;
    for (final double element : x) {
      m = Math.max(m,Math.abs(element)); }
    return m; }

  private static final double l1Dist (final double[] x0,
                                      final double[] x1) {
    final int n = x0.length;
    assert n == x1.length;
    final Accumulator a = BigFloatAccumulator.make();
    for (int i=0;i<n;i++) { a.add(Math.abs(x0[i]-x1[i])); }
    return a.doubleValue(); }

  // TODO: more efficient via bits?
  private static final boolean isEven (final int k) {
    return k == (2*(k/2)); }

  private static final double[] zeroSum (final double[] x) {
    final int n = x.length;
    final double[] y = new double[2*n];
    System.arraycopy(x,0,y,0,n);
    for (int i=0;i<n;i++) { y[i+n] = -x[i]; }
    return y; }

  // exact sum is 0.0
  private static double[] sampleDoubles (final Generator g,
                                         final UniformRandomProvider urp) {
    final double[] x = zeroSum((double[]) g.next());
    ListSampler.shuffle(urp,Arrays.asList(x));
    return x; }


  private static double[][] sampleDoubles (final int dim,
                                           final int n) {
    assert isEven(dim);
    final UniformRandomProvider urp =
      PRNG.well44497b("seeds/Well44497b-2019-01-05.txt");
    final Generator g =
      Doubles.finiteGenerator(dim/2,urp,Doubles.deMax(dim));

    final double[][] x = new double[n][];
    for (int i=0;i<n;i++) { x[i] = sampleDoubles(g,urp); }
    return x; }

  private static final int DIM = 2 * 1024;
  private static final int N = 16;

  //--------------------------------------------------------------

  public static final void main (final String[] args)
    throws InterruptedException {

    final double[][] x0 = sampleDoubles(DIM,N);
    final double[][] x1 = sampleDoubles(DIM,N);

    // should be zero with current construction
    final double[] truth = new double[N];
    final double[] pred = new double[N];
    // assuming ERational is correct!!!
    for (int i=0;i<N;i++) {
      truth[i] =
        EFloatAccumulator.make().addProducts(x0[i],x1[i]).doubleValue(); }

    for (int i=0;i<N;i++) {
      System.out.println(
        i + " : "
          + Double.toHexString(truth[i])
          + ", "
          + Double.toHexString(maxAbs(x0[i]))
          + ", "
          + Double.toHexString(maxAbs(x1[i]))); }
    System.out.println();
    final Accumulator[] accumulators =
    {
     BigDecimalAccumulator.make(),
     BigFractionAccumulator.make(),
     DoubleAccumulator.make(),
     DoubleFmaAccumulator.make(),
     ERationalAccumulator.make(),
     FloatAccumulator.make(),
     FloatFmaAccumulator.make(),
     RatioAccumulator.make(),
    };

    Thread.sleep(16*1024);
    for (final Accumulator a : accumulators) {
      long t;
      t = System.nanoTime();
      for (int i=0;i<N;i++) {
        pred[i] =
          a.clear().addProducts(x0[i],x1[i]).doubleValue(); }
      t = (System.nanoTime()-t);
      System.out.println(toHexString(l1Dist(truth,pred)) +
        " in " + (t*1.0e-9)
        + " secs " + Classes.className(a)); }

    Thread.sleep(16*1024); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
