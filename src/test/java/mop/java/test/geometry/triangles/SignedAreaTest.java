package mop.java.test.geometry.triangles;

import mop.java.geometry.triangle.Triangle2D;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.rng.UniformRandomProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/** Geometry predicates.
 * <pre>
 * mvn -Dtest=mop.java.test.geometry.predicates.SignedAreaTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-07
 */

public final class SignedAreaTest extends TriangleTest {

  //--------------------------------------------------------------

  private static final void signedArea (final Vector2D p0,
                                        final Vector2D p1,
                                        final Vector2D p2) {
    final List<Triangle2D> triangles = makeTriangles(p0,p1,p2);
    final Triangle2D gold = truth(p0,p1,p2);
    final double trueAreaX2 = gold.signedArea();
    for (final Triangle2D t : triangles) {
      final double areaX2 = t.signedArea();
      if (t.signedAreaExact()) {
        // with delta=0.0 handles +0 vs -0 'correctly'
        Assertions.assertEquals(
          trueAreaX2, areaX2, 0.0,
          failureMsg("signedArea",trueAreaX2,areaX2,
                     gold,t,triangles,null)); }
      else {
        Assertions.assertEquals(
          Math.signum(trueAreaX2), Math.signum(areaX2), 0.0,
          failureMsg("signedArea",trueAreaX2,areaX2,gold,
                     t,triangles,null)); } } }

  @Test
  public final void testSignedArea () {
    final Vector2D p0 = Vector2D.of( 0.0, 0.0);
    final Vector2D p1 = Vector2D.of( 1.0, 1.0);
    final Vector2D p2 = Vector2D.of( -1.0, 1.0);
    final Vector2D p3 = Vector2D.of( -1.0, -1.0);

    signedArea(p0, p1, p2);
    // reverse
    signedArea(p1, p0, p2);
    // 1 pt singular
    signedArea(p0, p0, p0);
    // 2 pt line segment
    signedArea(p0, p2, p0);
    signedArea(p0, p0, p2);
    // Co-linear triangle
    signedArea(p0, p1, p3);
  }

  //--------------------------------------------------------------

  @Test
  public final void laplaceTest () {
    final int n = 12;
    final UniformRandomProvider urp =
      PRNG.well44497b("seeds/Well44497b-2019-01-05.txt");
    final Generator laplaceGenerator =
      Doubles.laplaceGenerator(n, 2, urp, 0.0, 1.0);
    final double[][] p = (double[][]) laplaceGenerator.next();
    for (int i = 0; i < n-2; i++) {
      signedArea(Vector2D.of(p[i]),
                 Vector2D.of(p[i+1]),
                 Vector2D.of(p[i+2]));} }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
