package mop.java.benchmarks.triangles.area;

import mop.java.benchmarks.triangles.Defaults;
import mop.java.geometry.Generators;
import mop.java.geometry.triangle.Triangle2D;
import mop.java.numbers.Doubles;
import mop.java.prng.PRNG;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;

/** <pre>
 * mvn -q install && jmh mop.java.benchmarks.triangles.area.ColinearOrientation
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-23
 */

public class ColinearOrientation extends RandomOrientation {

  //--------------------------------------------------------------
  /** Re-initialize the prngs with the same seeds for each
   * test class.
   */
  @Setup(Level.Trial)
  public final void trialSetup () {
    triangleGenerator =
      Generators.colinearTriangleGenerator(
        nTriangles,
        Generators.vectorD2Generator(
          Doubles.laplaceGenerator(
            PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
            0.0, 1.0)),
        Doubles.laplaceGenerator(
          PRNG.well44497b("seeds/Well44497b-2019-01-09.txt"),
          0.0, 1.0)); }

  @Setup(Level.Invocation)
  public void invocationSetup () {
    triangles = Triangle2D.convertTriangles(
      (Triangle2D[]) triangleGenerator.next(), className);
    value = new int[3]; }

  //--------------------------------------------------------------

  @SuppressWarnings("unused")
  public static final void main (final String[] args)  {
    Defaults.run("ColinearOrientation"); }

//--------------------------------------------------------------
}
//--------------------------------------------------------------
