package mop.java.benchmarks.triangles.pt;

import mop.java.benchmarks.triangles.Defaults;
import mop.java.geometry.Generators;
import mop.java.geometry.triangle.Triangle2D;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
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

  //--------------------------------------------------------------

  Generator pointGenerator;
  Generator triangleGenerator;

  @Param({
   "Adapt",
    "ExactCache",
//    "Exact",
//    "Fast",
//    "Slow",
//    "TriangleVector2D",
//    "DoubleTriangle2D",
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
    "2048",
  })
  int nTriangles;

  /** convert to test class on each invocation. */
  Triangle2D[] triangles;

  @Param({
    "2048",
  })
  int nPoints;

  /** convert to test class on each invocation. */
  Vector2D[] points;

  /** signedArea or inCircle distance */

  double[] value;

  //--------------------------------------------------------------
  /** This is what is timed.
   */

  public abstract double operation (final Triangle2D z,
                                    final Vector2D p);

  //--------------------------------------------------------------
  /** Re-initialize the prngs with the same seeds for each
   * test class.
   */
  @Setup(Level.Trial)
  public final void trialSetup () {
    pointGenerator =
      Generators.vector2dGenerator(
        nPoints,
        Doubles.laplaceGenerator(
          PRNG.well44497b("seeds/Well44497b-2019-01-05.txt"),
          0.0, 1.0));
    triangleGenerator =
      Generators.triangleGenerator(
        nTriangles,
        Generators.vector2dGenerator(
          Doubles.laplaceGenerator(
            PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
            0.0, 1.0))); }

  @Setup(Level.Invocation)
  public final void invocationSetup () {
    points = (Vector2D[]) pointGenerator.next();
    triangles = Defaults.convertTriangles(
      (Triangle2D[]) triangleGenerator.next(),
      className);
    value = new double[triangles.length*points.length]; }

  @Benchmark
  public final Object bench (final Blackhole blackhole) {
    int k = 0;
    for (final Triangle2D triangle : triangles) {
      for (final Vector2D point : points) {
        value[k++] = operation(triangle, point); } }
    blackhole.consume(value);
    return value; }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
