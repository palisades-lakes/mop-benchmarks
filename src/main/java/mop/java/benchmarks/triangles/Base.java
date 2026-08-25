package mop.java.benchmarks.triangles;

import mop.java.geometry.Generators;
import mop.java.geometry.triangle.*;
import mop.java.geometry.triangle.jts.DDFast;
import mop.java.geometry.triangle.jts.DDNormalized;
import mop.java.geometry.triangle.jts.DDSlow;
import mop.java.geometry.triangle.jts.DoubleNonRobust;
import mop.java.geometry.triangle.jts.InCircleNormalized;
import mop.java.geometry.triangle.macro.AdaptMacro;
import mop.java.geometry.triangle.macro.DefaultMacro;
import mop.java.geometry.triangle.macro.ExactMacro;
import mop.java.geometry.triangle.macro.FastMacro;
import mop.java.geometry.triangle.macro.SlowMacro;
import mop.java.geometry.triangle.shewchuk.Adapt;
import mop.java.geometry.triangle.shewchuk.Exact;
import mop.java.geometry.triangle.shewchuk.ExactCache;
import mop.java.geometry.triangle.shewchuk.Fast;
import mop.java.geometry.triangle.shewchuk.Slow;
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
 * @version 2026-08-25
 */

@State(Scope.Thread)
public abstract class Base {

  //--------------------------------------------------------------

  Generator pointGenerator;

  Generator triangleGenerator;

  /** conversions from any Triangle2D to other Triangle classes. */
  // TODO: lookup method object rather than switch

  public static final Triangle2D convertTriangle (final Triangle2D t,
                                                  final String dest) {
    return switch (dest) {
//      case "TriangleVector2D" -> TriangleVector2D.from(t);
      case "DoubleTriangle2D" ->  DoubleTriangle2D.from(t);
      case "DoubleIntervalTriangle2D" ->  DoubleIntervalTriangle2D.from(t);
      case "DoubleIntervalTriangle2D0" ->  DoubleIntervalTriangle2D0.from(t);
      case "BigFloatTriangle2D" ->  BigFloatTriangle2D.from(t);
      case "DIBFTriangle2D" ->  DIBFTriangle2D.from(t);
      case "RationalFloatTriangle2D" ->  RationalFloatTriangle2D.from(t);
      case "DDFast" ->  DDFast.from(t);
      case "DDNormalized" ->  DDNormalized.from(t);
      case "DDSlow" ->  DDSlow.from(t);
//    case "InCircleCC" ->  InCircleCC.from(t);
      case "DoubleNonRobust" ->  DoubleNonRobust.from(t);
      case "InCircleNormalized" ->  InCircleNormalized.from(t);
      case "Adapt" ->  Adapt.from(t);
      case "Exact" ->  Exact.from(t);
      case "ExactCache" ->  ExactCache.from(t);
      case "Fast" ->  Fast.from(t);
      case "Slow" ->  Slow.from(t);
      case "AdaptMacro" ->  AdaptMacro.from(t);
      case "DefaultMacro" ->  DefaultMacro.from(t);
      case "ExactMacro" ->  ExactMacro.from(t);
      case "FastMacro" ->  FastMacro.from(t);
      case "SlowMacro" ->  SlowMacro.from(t);
      default -> throw new UnsupportedOperationException(); }; }

  public static final Triangle2D[]
  convertTriangles (final Triangle2D[] t,
                    final String dest) {
    for (int i=0; i<t.length; i++) {
      t[i] = convertTriangle(t[i],dest); }
    return t;}

  @Param({
//    "Adapt",
//    "ExactCache",
    "Exact",
//    "Fast",
    "Slow",
//    "TriangleVector2D",
//    "DoubleTriangle2D",
    "DoubleIntervalTriangle2D",
//    "DoubleIntervalTriangle2D0",
//    "DIBFTriangle2D",
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
   * <code>p</code> is ignored for signedArea.
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
    triangles = convertTriangles(
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
