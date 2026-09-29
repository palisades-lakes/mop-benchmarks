package mop.java.benchmarks.triangles.area;

import mop.java.geometry.Generators;
import mop.java.geometry.triangle.TriangleR2;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/** Benchmark triangle operations.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-29
 */

@State(Scope.Thread)
public abstract class Base {
  // TODO: parent Base class for nopt and pt benchmarks?

  //--------------------------------------------------------------

  Generator triangleGenerator;

  @Param({
    "TriangleD2Eager",
//    "TriangleBF2",
//    "TriangleBF2X",
//    "TriangleD2Lazy",
//    "RationalFloatTriangle2D",
    })
  String className;

  //--------------------------------------------------------------
  @Param({
    "524288",
  })
  int nTriangles;

  /** convert to test class on each invocation. */
  TriangleR2[] triangles;

  /** count signs */

  int[] value;

  //--------------------------------------------------------------
  /** This is what is timed.
   */

  public double operation (final TriangleR2 t) {
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
        Generators.vectorD2Generator(
          Doubles.laplaceGenerator(
            PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
            0.0, 1.0))); }

  @Setup(Level.Invocation)
  public void invocationSetup () {
    triangles = TriangleR2.convertTriangles(
      (TriangleR2[]) triangleGenerator.next(), className);
    value = new int[3];
    System.gc();
    System.gc();
  }

//  @TearDown(Level.Invocation)
//  public final void invocationTeardown () {
//    System.out.println(Arrays.toString(value)); }

  @Benchmark
  public final Object bench (final Blackhole blackhole) {
    for (final TriangleR2 triangle : triangles) {
      final double sign = operation(triangle);
      if (0.0 > sign) { value[0]++; }
      else if (0.0 == sign) { value[1]++; }
      else { value[2]++; } }
    blackhole.consume(value);
    return value; }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
