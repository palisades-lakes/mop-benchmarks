package mop.java.benchmarks.triangles.pt;

import mop.java.benchmarks.triangles.Defaults;
import mop.java.geometry.Generators;
import mop.java.geometry.triangle.Triangle2D;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.openjdk.jmh.annotations.*;
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
  Generator circleGenerator;

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

  /** multiple points per triangle. */
  Vector2D[][] points;

  /** count signs */

  int[] value;

  //--------------------------------------------------------------
  /** This is what is timed.
   */

  public abstract double operation (final Triangle2D t,
                                    final Vector2D p);

  //--------------------------------------------------------------
  /** Re-initialize the prngs with the same seeds for each
   * test class.
   */
  @Setup(Level.Trial)
  public void trialSetup () {
    pointGenerator =
      Generators.vector2dGenerator(
        nTriangles,
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

  //--------------------------------------------------------------

  @Setup(Level.Invocation)
  public void invocationSetup () {
    points = (Vector2D[][]) pointGenerator.next();
    triangles = Defaults.convertTriangles(
      (Triangle2D[]) triangleGenerator.next(),className);
    value = new int[3]; }

//  @TearDown(Level.Invocation)
//  public final void invocationTeardown () {
//    System.out.println(Arrays.toString(value)); }

  //--------------------------------------------------------------

  @Benchmark
  public Object bench (final Blackhole blackhole) {
    for (int i=0;i<nTriangles;i++) {
      final Triangle2D ti = triangles[i];
      for (int j=0;j<nPoints;j++) {
        final Vector2D pij = points[i][j];
        final double sign = operation(ti, pij);
        if (0.0 > sign) { value[0]++; }
        else if (0.0 == sign) { value[1]++; }
        else { value[2]++; } } }
    blackhole.consume(value);
    return value; }

  //--------------------------------------------------------------
}
//----------------------------------------------------------------
