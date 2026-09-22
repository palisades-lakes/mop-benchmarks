package mop.java.test.geometry.triangles;

import mop.java.geometry.Generators;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.triangle.*;
import mop.java.numbers.BigFloat;
import mop.java.numbers.DoubleInterval;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

//----------------------------------------------------------------

/** Check inCircle for (almost) cocircular cases.
 * <pre>
 * mvn -Dtest=mop.java.test.geometry.triangles.CocircularTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-21
 */

public final class CocircularTest extends TriangleTest {

  //--------------------------------------------------------------

  public static final List<Triangle2D> makeIntervalTriangles (final Triangle2D t) {
    final Triangle2D
      relaxedIntervalTriangle = RelaxedIntervalTriangle2D.from(t);
    final Triangle2D roundingIntervalTriangle = RoundingIntervalTriangle2D.from(t);
    final Triangle2D shewchukIntervalTriangle = ShewchukIntervalTriangle2D.from(t);
    return List.of(
      relaxedIntervalTriangle,
      roundingIntervalTriangle,
      shewchukIntervalTriangle); }

  private static final void inCircle (final Triangle2D t,
                                      final VectorD2 p) {
    final TriangleBF2 gold =
      (TriangleBF2) Triangle2D.truth(t);
    final BigFloat bf = gold.inCircleDistanceBF(p);
    final List<Triangle2D> triangles = makeIntervalTriangles(t);
    for (final Triangle2D ti :triangles) {
      Assertions.assertTrue(ti.inCircleIntervals());
      final DoubleInterval interval = ti.inCircleInterval(p);
      Assertions.assertTrue(
        interval.contains(bf),
        "\n\nTruth:" + bf +
          "\n(" + Double.toHexString(bf.doubleValue()) + ")" +
          "\n\nInterval:\n" + interval +
          "\n\nbf<min: " + bf.opLT(interval.min()) +
          "\nbf>max: " + bf.opGT(interval.max()) +
          "\n\nbf.exponent(): " + bf.exponent() +
          "\nexponent(bf.doubleValue(): " +
          Doubles.exponent(bf.doubleValue()) +
          "\nexponent(min): " + Doubles.exponent(interval.min()) +
          "\nexponent(max): " + Doubles.exponent(interval.max()) +
          "\n\n" + ti.getClass().getSimpleName() +
          "\n" + ti +
          "\n" + p); } }

  //--------------------------------------------------------------

  @Test
  public final void cocircularTest () {
    final double cMu = 1.0;
    final double cSigma = 1.0;
    final double rLambda = 1.0;
    final double pMu = 0.0;
    final double pSigma = 3.0;
    final Generator centerGenerator = Generators.vectorD2Generator(
      Doubles.laplaceGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
        cMu, cSigma));
    final Generator radiusGenerator = Doubles.exponentialGenerator(
      PRNG.well44497b("seeds/Well44497b-2019-01-09.txt"),
      rLambda);
    final Generator pointGenerator = Generators.vectorD2Generator(
      Doubles.laplaceGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        pMu, pSigma));

    final int ntriangles = 63;
    final int npoints = 63;
    for (int i=0;i<ntriangles;i++) {
      final VectorD2 c = (VectorD2) centerGenerator.next();
      final double r = radiusGenerator.nextDouble();
      final VectorD2 p0 = ((VectorD2) pointGenerator.next()).project(c,r);
      final VectorD2 p1 = ((VectorD2) pointGenerator.next()).project(c,r);
      final VectorD2 p2 = ((VectorD2) pointGenerator.next()).project(c,r);
      final TriangleBF2 t =
        (TriangleBF2) TriangleBF2.of(p0,p1,p2);
      for (int j=0;j<npoints;j++) {
        final VectorD2 p = ((VectorD2) pointGenerator.next()).project(c,r);
        inCircle(t,p); } } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
