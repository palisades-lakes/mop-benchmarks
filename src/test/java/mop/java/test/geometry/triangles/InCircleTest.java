package mop.java.test.geometry.triangles;

import mop.java.geometry.Generators;
import mop.java.geometry.triangle.Adapt;
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

  private static final void adaptTest (final Vector2D p0,
                                       final Vector2D p1,
                                       final Vector2D p2,
                                       final Vector2D p3) {
    final Triangle2D gold = AdaptMacro.of(p0,p1,p2);
    final Triangle2D p = Adapt.of(p0,p1,p2);
    final double trueInc = gold.inCircle(p3);
    final double inc = p.inCircle(p3);
    // with delta=0.0 handles +0 vs -0 'correctly'
    Assertions.assertEquals(
      trueInc, inc, 0.0,
      failureMsg("inCircle",trueInc,inc,gold,p,null,p3)); }

  //--------------------------------------------------------------

  private static final void inCircle (final Vector2D p0,
                                      final Vector2D p1,
                                      final Vector2D p2,
                                      final Vector2D p3) {
    adaptTest(p0,p1,p2,p3);
    final Triangle2D gold = truth(p0,p1,p2);
    final double trueInc = gold.inCircle(p3);
    final List<Triangle2D> triangles = makeTriangles(p0,p1,p2);
    for (final Triangle2D p :triangles) {
      final double inc = p.inCircle(p3);
      if (p.inCircleExact()) {
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

    inCircle(p1, p2, p3, p0);
    inCircle(p1, p2, p3, p4);
    inCircle( p1, p2, p3, p1);
    // Not working for InCircleCC
    // TODO: decide on the right answer for singular cases.
    // inCircle(p1, p1, p1, p4);
    // inCircle(p1, p2, p1, p4);
  }
  //--------------------------------------------------------------

  @Test
  public final void laplaceTest () {
    final int n = 32;
    final UniformRandomProvider urp =
      PRNG.well44497b("seeds/Well44497b-2019-01-07.txt");
    final Generator laplaceGenerator =
      Doubles.laplaceGenerator(urp, 0.0, 1.0);
    final Generator vGenerator =
      Generators.vector2dGenerator(n, laplaceGenerator);
    final Vector2D[] p = (Vector2D[]) vGenerator.next();
    for (int i = 0; i < n-3; i++) {
      adaptTest(p[i],p[i+1],p[i+2],p[i+3]);
      inCircle(p[i],p[i+1],p[i+2],p[i+3]); } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
