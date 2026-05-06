package mop.java.benchmarks.arithmetic;

import java.math.BigInteger;

import mop.java.numbers.UnboundedNatural;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import mop.java.numbers.BoundedNatural;
import mop.java.prng.Generator;
import mop.java.prng.Generators;
import mop.java.prng.PRNG;
import nzqr.openjdk.math.BigIntegerJDK;
import org.openjdk.jmh.infra.Blackhole;

/** Benchmark arithmetic operations on various number classes.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2024-01-20
 */

@State(Scope.Thread)
public abstract class Base {

  //--------------------------------------------------------------

  Generator gen;

  /** conversions from BigInteger to other number classes. */
  // TODO: use method objects rather than class names
  // and avoid else if expression

  public static final Object fromBigInteger (final BigInteger x,
                                             final String dest) {
    return switch (dest) {
      case "BigInteger" -> x;
      case "BigIntegerJDK" -> new BigIntegerJDK(x.toByteArray());
      case "BoundedNatural" -> BoundedNatural.valueOf(x);
      case "UnboundedNatural" -> UnboundedNatural.valueOf(x);
      default -> throw new UnsupportedOperationException(); }; }

  public static final Object[] fromBigInteger (final BigInteger[] x,
                                               final String dest) {
    final int n = x.length;
    final Object[] y = new Object[n];
    for (int i=0;i<n;i++) { y[i] = fromBigInteger(x[i],dest); }
    return y; }


  @Param({
//    "BigInteger",
//    "BigIntegerJDK",
    "UnboundedNatural",
    "BoundedNatural",
    })
  String numberClassName;

  //--------------------------------------------------------------
    /** size of BigIntegers to generate. */
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

  public abstract Object operation (final Object z0,
                                    final Object z1);

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
    y0 = fromBigInteger(x0,numberClassName);
    y1 = fromBigInteger(x1,numberClassName);
    p = new Object[y0.length];
  }

  @Benchmark
  public final Object bench (final Blackhole blackhole) {
    final int n = y0.length;
    for (int i=0;i<n;i++) { p[i] = operation(y0[i],y1[i]); }
    blackhole.consume(p);
    return p; }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
