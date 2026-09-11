package mop.java.scripts.triangles;

import mop.java.geometry.Generators;
import mop.java.geometry.triangle.RoundingIntervalTriangle2D;
import mop.java.numbers.RoundingInterval;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.geometry.euclidean.twod.shape.Circle;

/** Profile triangle classes over 'cocircular' examples.
 * <pre>
 * mvn -q clean install && jy src/scripts/java/mop/java/scripts/triangles/CocircularProfile.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-09
 */

public final class CocircularProfile {

  //--------------------------------------------------------------

  /** Project <code>p</code> onto (the boundary of) <code>c</code>. */
  private static final Vector2D project (final Circle c,
                                         final Vector2D p) {
    return c.project(p); }

  //--------------------------------------------------------------
  // each row contains npoints 'cocircular' points

  public static final Vector2D[][]
  cocircularPoints (final int ncircles,
                    final int npoints) {

    final double cMu = 1.0;
    final double cSigma = 1.0;
    final double rLambda = 1.0;
    final double pMu = 0.0;
    final double pSigma = 3.0;

    final Generator centerGenerator = Generators.vector2dGenerator(
      Doubles.laplaceGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
        cMu, cSigma));
    final Generator radiusGenerator = Doubles.exponentialGenerator(
      PRNG.well44497b("seeds/Well44497b-2019-01-09.txt"),
      rLambda);
    final Generator circleGenerator =
      Generators.circleGenerator(centerGenerator,radiusGenerator);
    final Generator pointGenerator = Generators.vector2dGenerator(
      Doubles.laplaceGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        pMu, pSigma));
    final Vector2D[][] points = new Vector2D[ncircles][npoints];
    for (int i=0;i<ncircles;i++) {
      final Circle c = (Circle) circleGenerator.next();
      for (int j=0;j<npoints;j++) {
        points[i][j] =  project(c,(Vector2D) pointGenerator.next()); } }

    System.out.println("cMu,cSigma= " + Double.toHexString(cMu) +
                         ", " + Double.toHexString(cSigma));
    System.out.println("rLambda= " + Double.toHexString(rLambda));
    System.out.println("pMu,pSigma= " + Double.toHexString(pMu) +
                         ", " + Double.toHexString(pSigma));
    System.out.flush();

    return points; }

  //--------------------------------------------------------------

  public static final void
  cocircularTrials () {

    final int ncircles = 4096;
    final int npoints = 4096;
    int ntrys = 0;
    int ndit = 0;
    final Vector2D[][] points = cocircularPoints(ncircles, npoints);
    for (int i=0; i<ncircles; i++) {
      final Vector2D[] p = points[i];
      for (int j=0;j<npoints-3;j++) {
        final RoundingIntervalTriangle2D dit =
          (RoundingIntervalTriangle2D)
            RoundingIntervalTriangle2D.of(p[j], p[j+1], p[j+2]);
        for (int k=j+3; k<npoints; k++) {
          ntrys++;
          final RoundingInterval di = dit.inCircleInterval(p[k]);
          if (di.containsZero()) { ndit++; } } } }
    System.out.println(
      "Double interval cocircular= " + ndit + "/" + ntrys +
        " = " + ((double) ndit) / ntrys); }

  //--------------------------------------------------------------------

  @SuppressWarnings("unused")
  public static final void main (final String[] args) {
    cocircularTrials(); }

  //--------------------------------------------------------------------
  // disable construction
  //--------------------------------------------------------------------

  private CocircularProfile () {
    throw new UnsupportedOperationException(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
