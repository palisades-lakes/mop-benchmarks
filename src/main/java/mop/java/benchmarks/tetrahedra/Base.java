package mop.java.benchmarks.tetrahedra;

import mop.java.geometry.Generators;
import mop.java.geometry.tetrahedron.BigFloatTetrahedron3D;
import mop.java.geometry.tetrahedron.RationalFloatTetrahedron3D;
import mop.java.geometry.tetrahedron.Tetrahedron3D;
import mop.java.geometry.tetrahedron.TetrahedronVector3D;
import mop.java.geometry.tetrahedron.macro.AdaptMacro;
import mop.java.geometry.tetrahedron.macro.DefaultMacro;
import mop.java.geometry.tetrahedron.macro.ExactMacro;
import mop.java.geometry.tetrahedron.macro.FastMacro;
import mop.java.geometry.tetrahedron.macro.SlowMacro;
import mop.java.geometry.tetrahedron.shewchuk.Adapt;
import mop.java.geometry.tetrahedron.shewchuk.Exact;
import mop.java.geometry.tetrahedron.shewchuk.Fast;
import mop.java.geometry.tetrahedron.shewchuk.Slow;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/** Benchmark tetrahedra operations.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-29
 */

@State(Scope.Thread)
public abstract class Base {

  //--------------------------------------------------------------

  Generator pointGenerator;

  Generator tetrahedronGenerator;

  /** conversions from any Tetrahedron3D to other Tetrahedron classes. */
  // TODO: lookup method object rather than switch

  public static final Tetrahedron3D
  convertTetrahedron (final Tetrahedron3D t,
                      final String dest) {
    return switch (dest) {
      case "TetrahedronVector3D" -> TetrahedronVector3D.from(t);
      case "BigFloatTetrahedron3D" ->  BigFloatTetrahedron3D.from(t);
      case "RationalFloatTetrahedron3D" ->  RationalFloatTetrahedron3D.from(t);
      case "Adapt" ->  Adapt.from(t);
      case "Exact" ->  Exact.from(t);
      case "Fast" ->  Fast.from(t);
      case "Slow" ->  Slow.from(t);
      case "AdaptMacro" ->  AdaptMacro.from(t);
      case "DefaultMacro" ->  DefaultMacro.from(t);
      case "ExactMacro" ->  ExactMacro.from(t);
      case "FastMacro" ->  FastMacro.from(t);
      case "SlowMacro" ->  SlowMacro.from(t);
      default -> throw new UnsupportedOperationException(); }; }

  public static final Tetrahedron3D[]
  convertTetrahedra (final Tetrahedron3D[] t,
                     final String dest) {
    for (int i=0; i<t.length; i++) {
      t[i] = convertTetrahedron(t[i],dest); }
    return t; }

  @Param({
    //"TetrahedronVector3D",
    "BigFloatTetrahedron3D",
//    "RationalFloatTetrahedron3D",
    "Adapt",
//    "Exact",
//    "Fast",
//    "Slow",
    "AdaptMacro",
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
  int nTetrahedra;

  /** convert to test class on each invocation. */
  Tetrahedron3D[] tetrahedra;

  @Param({
    "2048",
  })
  int nPoints;

  /** convert to test class on each invocation. */
  Vector3D[] points;

  /** signedArea or inCircle distance */

  double[] value;
  //--------------------------------------------------------------
  /** This is what is timed.
   * <code>p</code> is ignored for signedArea.
   */

  public abstract double operation (final Tetrahedron3D z,
                                    final Vector3D p);

  //--------------------------------------------------------------
  /** Re-initialize the prngs with the same seeds for each
   * test class.
   */
  @Setup(Level.Trial)
  public final void trialSetup () {
    pointGenerator =
      Generators.vector3dGenerator(
        nPoints,
        Doubles.laplaceGenerator(
          PRNG.well44497b("seeds/Well44497b-2019-01-05.txt"),
          0.0, 1.0));
    tetrahedronGenerator =
      Generators.tetrahedraGenerator(
        nTetrahedra,
        Generators.vector3dGenerator(
          Doubles.laplaceGenerator(
            PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
            0.0, 1.0))); }

  @Setup(Level.Invocation)
  public final void invocationSetup () {
    points = (Vector3D[]) pointGenerator.next();
    tetrahedra = convertTetrahedra(
      (Tetrahedron3D[]) tetrahedronGenerator.next(),
      className);
    value = new double[tetrahedra.length*points.length]; }

  @Benchmark
  public final Object bench (final Blackhole blackhole) {
    int k = 0;
    for (final Tetrahedron3D tetrahedron : tetrahedra) {
      for (final Vector3D point : points) {
        value[k++] = operation(tetrahedron, point); } }
    blackhole.consume(value);
    return value; }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
