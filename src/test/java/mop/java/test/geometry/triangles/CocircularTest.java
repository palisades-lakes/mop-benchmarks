package mop.java.test.geometry.triangles;

import mop.java.geometry.Generators;
import mop.java.geometry.triangle.*;
import mop.java.numbers.BigFloat;
import mop.java.numbers.DoubleInterval;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.geometry.euclidean.twod.shape.Circle;
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
 * @version 2026-09-12
 */

public final class CocircularTest extends TriangleTest {

  //--------------------------------------------------------------

  public static final List<Triangle2D> makeIntervalTriangles (final Triangle2D t) {
    final Triangle2D doubleIntervalTriangle = RelaxedIntervalTriangle2D.from(t);
    final Triangle2D roundingIntervalTriangle = RoundingIntervalTriangle2D.from(t);
    final Triangle2D shewchukIntervalTriangle = ShewchukIntervalTriangle2D.from(t);
    return List.of(
     doubleIntervalTriangle,
     roundingIntervalTriangle,
      shewchukIntervalTriangle); }

  private static final void inCircle (final Triangle2D t,
                                      final Vector2D p) {
    final BigFloatTriangle2D gold =
      (BigFloatTriangle2D) Triangle2D.truth(t);
    final BigFloat bf = gold.inCircleDistanceBF(p);
    final List<Triangle2D> triangles = makeIntervalTriangles(t);
    for (final Triangle2D ti :triangles) {
      Assertions.assertTrue(ti.inCircleIntervals());
      final DoubleInterval interval = ti.inCircleInterval(p);
        Assertions.assertTrue(
          ti.inCircleInterval(p).contains(bf),
          "\nTruth:" + bf +
            "\n" + ti.getClass().getSimpleName() +
            "\nInterval:\n" + interval +
            "\n" + ti +
            "\n" + p); } }

  //--------------------------------------------------------------

  private static final Vector2D project (final Circle c,
                                         final Vector2D p) {
    return c.project(p); }

  //--------------------------------------------------------------

  @Test
  public final void cocircularTest () {
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

    final int ntriangles = 1023;
    final int npoints = 1023;
    for (int i=0;i<ntriangles;i++) {
      final Circle c = (Circle) circleGenerator.next();
      final Vector2D p0 = project(c,(Vector2D) pointGenerator.next());
      final Vector2D p1 = project(c,(Vector2D) pointGenerator.next());
      final Vector2D p2 = project(c,(Vector2D) pointGenerator.next());
      final BigFloatTriangle2D t =
        (BigFloatTriangle2D) BigFloatTriangle2D.of(p0,p1,p2);
      for (int j=0;j<npoints;j++) {
        final Vector2D p = project(c,(Vector2D) pointGenerator.next());
        inCircle(t,p); } } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
