package mop.java.benchmarks.triangles.nopt;

import mop.java.benchmarks.triangles.Defaults;
import mop.java.geometry.Generators;
import mop.java.geometry.triangle.*;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/** Benchmark triangle operations.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-31
 */

@State(Scope.Thread)
public abstract class Base {
  // TODO: parent Base class for nopt and pt benchmarks?

  //--------------------------------------------------------------

  Generator triangleGenerator;

  @Param({
    //  "Adapt",
//    "ExactCache",
//    "Exact",
//    "Fast",
//    "Slow",
//    "TriangleVector2D",
    "DoubleTriangle2D",
    "DoubleIntervalTriangle2D",
    "DIBFTriangle2D",
    "BigFloatTriangle2D",
//    "RationalFloatTriangle2D",
//    "DDFast",
//    "DDNormalized",
//    "DDSlow",
//    "InCircleNormalized",
//    "DoubleNonRobust",
//    "AdaptMacro",
//    "DefaultMacro",
//    "ExactMacro",
//    "FastMacro",
//    "SlowMacro",
  })
  String className;

  //--------------------------------------------------------------
  @Param({
    "262144",
  })
  int nTriangles;

  /** convert to test class on each invocation. */
  Triangle2D[] triangles;

  /** signedArea or inCircle distance */

  double[] value;

  //--------------------------------------------------------------
  /** This is what is timed.
   */

  public double operation (final Triangle2D t) {
    return t.orientation(); }

  //--------------------------------------------------------------
  /** Re-initialize the prngs with the same seeds for each
   * test class.
   */
  @Setup(Level.Trial)
  public void trialSetup () {
    triangleGenerator =
      Generators.triangleGenerator(
        nTriangles,
        Generators.vector2dGenerator(
          Doubles.laplaceGenerator(
            PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
            0.0, 1.0))); }

  @Setup(Level.Invocation)
  public void invocationSetup () {
    triangles = Defaults.convertTriangles(
      (Triangle2D[]) triangleGenerator.next(), className);
    value = new double[triangles.length]; }


  @Benchmark
  public final Object bench (final Blackhole blackhole) {
    int k = 0;
    for (final Triangle2D triangle : triangles) {
      value[k++] = operation(triangle); }
    blackhole.consume(value);
    return value; }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
