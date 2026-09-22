package mop.java.benchmarks.triangles.circle;

import mop.java.geometry.Generators;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.triangle.Triangle2D;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/** Benchmark triangle operations.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-21
 */

@State(Scope.Thread)
public abstract class Base {

  //--------------------------------------------------------------

  Generator pointGenerator;
  Generator triangleGenerator;
  Generator centerGenerator;
  Generator radiusGenerator;

  @Param({
//    "Adapt",
//    "ExactCache",
//    "Exact",
//    "Fast",
//    "Slow",
    "TriangleBF2",
    "TriangleD2Eager",
    "TriangleD2Lazy",
//      "RelaxedIntervalTriangle2D",
//    "RoundingIntervalTriangle2D",
//    "ShewchukIntervalTriangle2D",
//    "ReBfTriangle2D",
//    "RoBfTriangle2D",
//    "ShBFTriangle2D",
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
  VectorD2[][] points;

  /** count signs */

  int[] value;

  //--------------------------------------------------------------
  /** This is what is timed.
   */

  public abstract double operation (final Triangle2D t,
                                    final VectorD2 p);

  //--------------------------------------------------------------
  /** Re-initialize the prngs with the same seeds for each
   * test class.
   */
  @Setup(Level.Trial)
  public void trialSetup () {
    pointGenerator =
      Generators.vectorD2Generator(
        nTriangles,
        nPoints,
        Doubles.laplaceGenerator(
          PRNG.well44497b("seeds/Well44497b-2019-01-05.txt"),
          0.0, 1.0));
    triangleGenerator =
      Generators.triangleGenerator(
        nTriangles,
        Generators.vectorD2Generator(
          Doubles.laplaceGenerator(
            PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
            0.0, 1.0))); }

  //--------------------------------------------------------------

  @Setup(Level.Invocation)
  public void invocationSetup () {
    points = (VectorD2[][]) pointGenerator.next();
    triangles = Triangle2D.convertTriangles(
      (Triangle2D[]) triangleGenerator.next(), className);
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
        final VectorD2 pij = points[i][j];
        final double sign = operation(ti, pij);
        if (0.0 > sign) { value[0]++; }
        else if (0.0 == sign) { value[1]++; }
        else { value[2]++; } } }
    blackhole.consume(value);
    return value; }

  //--------------------------------------------------------------
}
//----------------------------------------------------------------
