package mop.java.benchmarks.accumulate;

//import java.util.List;

import mop.java.accumulators.Accumulator;
import mop.java.accumulators.BigFloatAccumulator;
import mop.java.prng.Generator;
import mop.java.prng.Generators;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Benchmark operations on <code>double[]</code>.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-05
 */

//@SuppressWarnings("unchecked")
@State(Scope.Thread)
public abstract class Base {

  //--------------------------------------------------------------

//  public static final void save (final double x,
//                                 final List data) {
//    data.add(Double.valueOf(x)); }

  //--------------------------------------------------------------

  //@Param({"exponential",})
  //@Param({"finite",})
  //@Param({"gaussian",})
  //@Param({"laplace",})
  @Param({ "uniform", })
  //@Param({"exponential","finite","gaussian","laplace","uniform",})
    //@Param({"exponential","laplace","uniform",})
    String generator;
  Generator gen;

  Accumulator exact;
  // exact value(s)
  double[] truth;

  @Param({

    "mop.java.accumulators.BigDecimalAccumulator",
    "mop.java.accumulators.BigFractionAccumulator",
    "mop.java.accumulators.BigFloatAccumulator",
    "mop.java.accumulators.DistilledAccumulator",
    "mop.java.accumulators.DoubleAccumulator",
    "mop.java.accumulators.DoubleFmaAccumulator",
    "mop.java.accumulators.EFloatAccumulator",
    "mop.java.accumulators.ERationalAccumulator",
    "mop.java.accumulators.FloatAccumulator",
    "mop.java.accumulators.FloatFmaAccumulator",
    "mop.java.accumulators.KahanAccumulator",
    "mop.java.accumulators.KahanFmaAccumulator",
    "mop.java.accumulators.RatioAccumulator",
    "mop.java.accumulators.RationalFloatAccumulator",
    "mop.java.accumulators.ZhuHayesAccumulator",
    "mop.java.accumulators.ZhuHayesBranch",
    "mop.java.accumulators.ZhuHayesGCAccumulator",
    "mop.java.accumulators.ZhuHayesGCBranch",

    // Fails with infinite loop, stack overflow exception,
    // possibly due to call to scala.collection.concurrent.TrieMap
    // not being thread safe
    // https://github.com/scala/bug/issues/7943
    // Spire accumulators are >10x slower than everything else.
    // "mop.java.accumulators.SpireAlgebraicAccumulator",

    // These 2 work but are too slow to consider (as of 2026-05-04)
    // "mop.java.accumulators.SpireRationalAccumulator",
    // "mop.java.accumulators.SpireRealAccumulator",
  })
  String accumulator;
  Accumulator acc;

  //--------------------------------------------------------------

  @Param({
    //"33554433",
    //"8388609",
    //"4194303",
    //"2097153",
    "1048575",
    //"524289",
    //"131071",
  })
  int dim;

  double[] x0;
  double[] x1;

  // estimated value(s)
  double[] p;

  //--------------------------------------------------------------
  /** This is what is timed.
   */

  public abstract double[] operation (final Accumulator ac,
                                      final double[] z0,
                                      final double[] z1);

  //--------------------------------------------------------------
  /** Re-initialize the prngs with the same seeds for each
   * <code>(accumulator,dim)</code> pair.
   */
  @Setup(Level.Trial)
  public final void trialSetup () {
    gen = Generators.make(generator, dim);
    //exact = EFloatAccumulator.make();
    exact = BigFloatAccumulator.make();
    assert exact.isExact();
    acc = Common.makeAccumulator(accumulator);
  }

  @Setup(Level.Invocation)
  public final void invocationSetup () {
    x0 = (double[]) gen.next();
    x1 = (double[]) gen.next();
    truth = operation(exact, x0, x1);
  }

  @TearDown(Level.Invocation)
  public final void invocationTeardown () {
    assert
      0.0 == exact.clear().addL1Distance(truth, p).doubleValue();
  }

  // not needed while testing exact methods
  //  @TearDown(Level.Trial)
  //  public final void teardownTrial () {
  //    //System.out.println("teardownTrial");
  //    final int n = truth.size();
  //    assert n == est.size();
  //    final String aname = Classes.className(acc);
  //    final String bname =
  //      Classes.className(this).replace("_jmhType","");
  //    final File parent = new File("output/" + bname);
  //    parent.mkdirs();
  //    final File f = new File(parent,
  //      aname + "-" + generator + "-" + dim + "-" + now() + ".csv");
  //    PrintWriter pw = null;
  //    try {
  //      pw = new PrintWriter(f);
  //      pw.println("generator,benchmark,accumulator,dim,truth,est");
  //      for (int i=0;i<n;i++) {
  //        pw.println(
  //          generator + "," + bname + "," + aname + "," + dim + ","
  //            + truth.get(i) + "," + est.get(i)); } }
  //    catch (final FileNotFoundException e) {
  //      throw new RuntimeException(e); }
  //    finally { if (null != pw) { pw.close(); } } }

  @Benchmark
  public final double[] bench (final Blackhole blackhole) {
    p = operation(acc, x0, x1);
    blackhole.consume(p);
    return p;
  }

  //--------------------------------------------------------------
  //  /** <pre>
  //   * java -cp target\benchmarks.jar mop.java.Base
  //   * </pre>
  //   */

  //  public static void main (final String[] args)
  //    throws RunnerException {
  //    System.out.println("args=" + Arrays.toString(args));
  //    final Options opt =
  //      Defaults.options("Generators",
  //      "TotalDot|TotalL2Norm|TotalSum");
  //    System.out.println(opt.toString());
  //    new Runner(opt).run(); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
