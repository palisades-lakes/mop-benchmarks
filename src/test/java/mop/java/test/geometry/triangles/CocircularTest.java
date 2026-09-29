package mop.java.test.geometry.triangles;

//----------------------------------------------------------------

import mop.java.geometry.Generators;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.triangle.TriangleBF2;
import mop.java.geometry.triangle.TriangleR2;
import mop.java.numbers.BigFloat;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/** Check inCircle for (almost) cocircular cases.
 * <pre>
 * mvn -Dtest=mop.java.test.geometry.triangles.CocircularTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-09-29
 */

public final class CocircularTest extends TriangleTest {

  //--------------------------------------------------------------
// TODO: check error bound, instead of interval

  private static final void inCircle (final TriangleR2 t,
                                      final VectorD2 p) {
    final TriangleBF2 gold =
      (TriangleBF2) TriangleR2.truth(t);
    final BigFloat bf = gold.inCircleDistanceBF(p);
    final List<TriangleR2> triangles = TriangleR2.makeTriangles(t);
    for (final TriangleR2 ti :triangles) {
      if (ti.inCircleRobust(p)) {
      Assertions.assertEquals(
        ti.inCircle(p), gold.inCircle(p),
        "\n\nExact:" + bf +
          "\n" + Double.toHexString(bf.doubleValue()) +
          "\n\nApproximate:\n" + ti.inCircleDistance(p) +
          "\n\n" + ti.getClass().getSimpleName() +
          "\n" + ti +
          "\n" + p); } } }

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
