package mop.java.benchmarks.triangles.circle;

import mop.java.benchmarks.triangles.Defaults;
import mop.java.geometry.Generators;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.triangle.Triangle2D;
import mop.java.geometry.triangle.TriangleD2Lazy;
import mop.java.numbers.Doubles;
import mop.java.prng.PRNG;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;

/** <pre>
 * mvn -q install && jmh mop.java.benchmarks.triangles.circle.CocircularInCircle
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-24
 */

public class CocircularInCircle extends Base {

  //--------------------------------------------------------------
  /** Re-initialize the prngs with the same seeds for each
   * test class.
   */

  @Setup(Level.Trial)
  public final void trialSetup () {
    final double cMu = 1.0;
    final double cSigma = 1.0;
    final double rLambda = 1.0;
    final double pMu = 0.0;
    final double pSigma = 3.0;
    centerGenerator = Generators.vectorD2Generator(
      Doubles.laplaceGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
        cMu, cSigma));
    radiusGenerator = Doubles.exponentialGenerator(
      PRNG.well44497b("seeds/Well44497b-2019-01-09.txt"),
      rLambda);
    pointGenerator = Generators.vectorD2Generator(
      Doubles.laplaceGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        pMu, pSigma)); }

    //--------------------------------------------------------------

  @Setup(Level.Invocation)
  public final void invocationSetup () {
    triangles = new Triangle2D[nTriangles];
    points = new VectorD2[nTriangles][nPoints];
    for (int i=0;i<nTriangles;i++) {
      final VectorD2 c = (VectorD2) centerGenerator.next();
      final double r = radiusGenerator.nextDouble();
      final Triangle2D ti =
        TriangleD2Lazy.of(
          ((VectorD2) pointGenerator.next()).project(c,r),
          ((VectorD2) pointGenerator.next()).project(c,r),
          ((VectorD2) pointGenerator.next()).project(c,r));
      triangles[i] = Triangle2D.convertTriangle(ti, className);
      for (int j=0;j<nPoints; j++) {
        points[i][j] = ((VectorD2) pointGenerator.next()).project(c,r); } }
    value = new int[3];
    System.gc(); }

  //--------------------------------------------------------------

  @Override
  public final double operation (final Triangle2D t,
                                 final VectorD2 p) {
    return t.inCircle(p); }

  //--------------------------------------------------------------

  @SuppressWarnings("unused")
  public static final void main (final String[] args)  {
    Defaults.run("CocircularInCircle"); } }
