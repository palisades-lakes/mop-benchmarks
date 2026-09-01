package mop.java.benchmarks.triangles.nopt;

import mop.java.benchmarks.triangles.Defaults;
import mop.java.geometry.Generators;
import mop.java.geometry.triangle.Triangle2D;
import mop.java.numbers.Doubles;
import mop.java.prng.PRNG;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;

/** <pre>
 * mvn clean install && java -cp target\benchmarks.jar mop.java.benchmarks.triangles.nopt.ColinearOrientation
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-31
 */

public class ColinearOrientation extends Orientation {

  //--------------------------------------------------------------
  /** Re-initialize the prngs with the same seeds for each
   * test class.
   */
  @Setup(Level.Trial)
  public final void trialSetup () {
    triangleGenerator =
      Generators.colinearTriangleGenerator(
        nTriangles,
        Generators.vector2dGenerator(
          Doubles.laplaceGenerator(
            PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
            0.0, 1.0)),
        Doubles.laplaceGenerator(
          PRNG.well44497b("seeds/Well44497b-2019-01-09.txt"),
          0.0, 1024.0)); }

  @Setup(Level.Invocation)
  public void invocationSetup () {
    triangles = Defaults.convertTriangles(
      (Triangle2D[]) triangleGenerator.next(), className);
    value = new double[triangles.length]; }

  //--------------------------------------------------------------

  @SuppressWarnings("unused")
  public static final void main (final String[] args)  {
    Defaults.run("ColinearOrientation"); }

//--------------------------------------------------------------
}
//--------------------------------------------------------------
