package mop.java.test.geometry.triangles;

import mop.java.geometry.Generators;
import mop.java.geometry.triangle.TriangleVector2D;
import mop.java.geometry.triangle.shewchuk.Adapt;
import mop.java.geometry.triangle.Triangle2D;
import mop.java.geometry.triangle.macro.AdaptMacro;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.rng.UniformRandomProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

//----------------------------------------------------------------
/** Common code for geometry predicate tests.
 * <pre>
 * mvn -Dtest=mop.java.test.geometry.triangles.InCircleTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-27
 */

public final class InCircleTest extends TriangleTest {


  //--------------------------------------------------------------
  /** Compare exact value from cleaned up Adapt and
   * brutal macro-expanded AdaptMacro.
   */

  private static final void adaptTest (final Triangle2D t,
                                       final Vector2D p3) {
    final Triangle2D gold = AdaptMacro.from(t);
    final Triangle2D p = Adapt.from(t);
    final double trueInc = gold.inCircleDistance(p3);
    final double inc = p.inCircleDistance(p3);
    // with delta=0.0 handles +0 vs -0 'correctly'
    Assertions.assertEquals(
      trueInc, inc, 0.0,
      failureMsg("inCircle",trueInc,inc,gold,p,null,p3)); }

  //--------------------------------------------------------------

  private static final void inCircle (final Triangle2D t,
                                      final Vector2D p3) {
    adaptTest(t,p3);
    final Triangle2D gold = truth(t);
    final double trueInc = gold.inCircleDistance(p3);
    final List<Triangle2D> triangles = makeTriangles(t);
    for (final Triangle2D p :triangles) {
      final double inc = p.inCircleDistance(p3);
      if (p.inCircleDistanceExact()) {
        // with delta=0.0 handles +0 vs -0 'correctly'
        Assertions.assertEquals(
          trueInc, inc, 0.0,
          failureMsg("inCircle",trueInc,inc,gold,p,triangles,p3)); }
      else {
        Assertions.assertEquals(
          Math.signum(trueInc), Math.signum(inc), 0.0,
          failureMsg("inCircle",trueInc,inc,gold,p,triangles,p3)); } } }

  //--------------------------------------------------------------

  @Test
  public final void simpleTest () {
    final Vector2D p0 =  Vector2D.of( 0.0, 0.0);
    final Vector2D p1 =  Vector2D.of( 1.0, 1.0);
    final Vector2D p2 =  Vector2D.of( -1.0, 1.0);
    final Vector2D p3 =  Vector2D.of( -1.0, -1.0);
    final Vector2D p4 =  Vector2D.of( 1.0, -1.0);

    final Triangle2D t = TriangleVector2D.of(p1,p2,p3);
    inCircle(t, p0);
    inCircle(t, p4);
    inCircle(t, p1);
    // Not working for InCircleCC
    // TODO: decide on the right answer for singular cases.
    // inCircle(TriangleVector2D.of(p1, p1, p1), p4);
    // inCircle(TriangleVector2D.of(p1, p2, p1), p4);
  }
  //--------------------------------------------------------------

  @Test
  public final void laplaceTest () {
    final int m = 32;
    final int n = 32;
    final UniformRandomProvider urp0 =
      PRNG.well44497b("seeds/Well44497b-2019-01-07.txt");
    final Generator tGenerator =
      Generators.triangleGenerator(
        n, Generators.vector2dGenerator(
          Doubles.laplaceGenerator(urp0, 0.0, 1.0)));
    final Triangle2D[] t = (Triangle2D[]) tGenerator.next();
    final UniformRandomProvider urp1 =
      PRNG.well44497b("seeds/Well44497b-2019-01-09.txt");
    final Generator pGenerator =
      Generators.vector2dGenerator(
        n, Doubles.laplaceGenerator(urp1, 0.0, 1.0));
    final Vector2D[] p = (Vector2D[]) pGenerator.next();
    for (int i = 0; i < m; i++) {
      final Triangle2D ti = t[i];
      for (int j=0;j<n;j++) {
      adaptTest(ti,p[j]);
      inCircle(ti,p[j]); } } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
