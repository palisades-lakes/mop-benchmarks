package mop.java.benchmarks.triangles;

import mop.java.prng.Generator;
import mop.java.prng.Generators;
import mop.java.prng.PRNG;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigInteger;

/** Benchmark triangle operations.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-08
 */

@State(Scope.Thread)
public abstract class Base {

  //--------------------------------------------------------------

  Generator gen;

  @Param({
    "BigInteger",
    "BigIntegerJDK",
//    "UnboundedNatural",
    "BoundedNatural",
    })
  String numberClassName;

  //--------------------------------------------------------------
  @Param({
//    "8192",
//    "4096",
//    "1024",
    "256",
  })
  int nbytes;

  static final int NINTS = 2048;

  /** random arrays of BigIntegers on each invocation. */
  BigInteger[] x0;
  BigInteger[] x1;

  /** convert to test class on each invocation. */
  Object[] y0;
  Object[] y1;

  // value
  Object[] p;

  //--------------------------------------------------------------
  /** This is what is timed. */

  public abstract Object operation (final Object z);

  //--------------------------------------------------------------
  /** Re-initialize the prngs with the same seeds for each
   * test class.
   */
  @Setup(Level.Trial)
  public final void trialSetup () {
    gen = Generators.nonNegativeBigIntegerGenerator(
      nbytes,
      PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
      NINTS); }

  @Setup(Level.Invocation)
  public final void invocationSetup () {
    x0 = (BigInteger[]) gen.next();
    x1 = (BigInteger[]) gen.next();
    p = new Object[y0.length];
  }

  @Benchmark
  public final Object bench (final Blackhole blackhole) {
    final int n = y0.length;
    for (int i=0;i<n;i++) { p[i] = operation(y0[i]); }
    blackhole.consume(p);
    return p; }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
