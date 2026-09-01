package mop.java.benchmarks.triangles.pt;

import mop.java.benchmarks.triangles.Defaults;
import mop.java.geometry.Generators;
import mop.java.geometry.triangle.Triangle2D;
import mop.java.geometry.triangle.TriangleVector2D;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.geometry.euclidean.twod.shape.Circle;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;

/** <pre>
 * mvn clean install && jmh mop.java.benchmarks.triangles.pt.CocircularInCircle
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-01
 */

public class CocircularInCircle extends Base {

  //--------------------------------------------------------------
  /** Re-initialize the prngs with the same seeds for each
   * test class.
   */

  @Setup(Level.Trial)
  public final void trialSetup () {
    final Generator centerGenerator = Generators.vector2dGenerator(
      Doubles.laplaceGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
        0.0, 1024.0));
    final Generator radiusGenerator = Doubles.exponentialGenerator(
      PRNG.well44497b("seeds/Well44497b-2019-01-09.txt"),
      1.0);
    circleGenerator =
      Generators.circleGenerator(centerGenerator, radiusGenerator);
    pointGenerator = Generators.vector2dGenerator(
      Doubles.laplaceGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        0.0, 4096.0)); }

  //--------------------------------------------------------------

  private static final Vector2D radiusPt (final Vector2D v,
                                          final Circle circle) {
    final Vector2D c = circle.getCenter();
    final double r = circle.getRadius();
    return v.subtract(c).withNorm(r).add(c); }

  @Setup(Level.Invocation)
  public final void invocationSetup () {
    triangles = new Triangle2D[nTriangles];
    points = new Vector2D[nTriangles][nPoints];
    for (int i=0;i<nTriangles;i++) {
      final Circle circle = (Circle) circleGenerator.next();
      final Triangle2D ti =
        TriangleVector2D.of(
          radiusPt((Vector2D) pointGenerator.next(), circle),
          radiusPt((Vector2D) pointGenerator.next(), circle),
          radiusPt((Vector2D) pointGenerator.next(), circle));
      triangles[i] = Defaults.convertTriangle(ti, className);
      for (int j=0;j<nPoints; j++) {
        points[i][j] =
          radiusPt((Vector2D) pointGenerator.next(), circle); } }
    value = new int[3]; }

  //--------------------------------------------------------------

  @Override
  public final double operation (final Triangle2D t,
                                 final Vector2D p) {
    return t.inCircle(p); }

  //--------------------------------------------------------------

  @SuppressWarnings("unused")
  public static final void main (final String[] args)  {
    Defaults.run("CocircularInCircle"); } }
