package mop.java.benchmarks.accumulate;

import java.util.concurrent.TimeUnit;

import org.apache.commons.rng.UniformRandomProvider;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import mop.java.numbers.Doubles;
import mop.java.numbers.RationalFloat;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.openjdk.jmh.infra.Blackhole;

// java -ea --illegal-access=warn -jar target/benchmarks.jar Roundtrip

/** Benchmark roundtrip double -> rational -> double.
 * <pre>
 * java -ea -jar target\benchmarks.jar TotalSum
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2023-08-16
 */

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class Roundtrip {

  //--------------------------------------------------------------

  private static final int TRYS = 2*1024;

  @SuppressWarnings("static-method")
  @Benchmark
  public final boolean roundtrip (final Blackhole blackhole) {
    final UniformRandomProvider urp =
      PRNG.well44497b("seeds/Well44497b-2019-01-05.txt");
    final Generator g = Doubles.finiteGenerator(urp);
    boolean identical = true;
    for (int i=0;i<TRYS;i++) {
      final double x0 = g.nextDouble();
      final RationalFloat q = RationalFloat.valueOf(x0);
      final double x1 = q.doubleValue();
      identical = identical && (x1 == x0); }
    blackhole.consume(identical);
    assert identical;
    return identical; }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
