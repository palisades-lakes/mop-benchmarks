package mop.java.test.geometry.triangles;

import mop.java.geometry.Generators;
import mop.java.geometry.triangle.BigFloatTriangle2D;
import mop.java.geometry.triangle.Triangle2D;
import mop.java.geometry.triangle.TriangleVector2D;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.rng.UniformRandomProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/** <pre>
 * mvn -Dtest=mop.java.test.geometry.triangles.SignedAreaTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-18
 */

public final class SignedAreaTest extends TriangleTest {

  //--------------------------------------------------------------

  private static final void reverseSignedArea (final Triangle2D t0) {
    final Triangle2D t1 = TriangleVector2D.of(t0.getP0(),t0.getP2(),t0.getP1());
    final Triangle2D plus = truth(t0);
    final double aplus = plus.twiceSignedArea();
    final Triangle2D minus = truth(t1);
    final double aminus = minus.twiceSignedArea();
        // with delta=0.0 handles +0 vs -0 'correctly'
        Assertions.assertEquals(
          plus.twiceSignedArea(), -minus.twiceSignedArea(), 0.0,
          failureMsg("reverseSignedArea",aplus,aminus,
                     plus,minus,List.of(),null)); }

  private static final void signedArea (final Triangle2D t0) {
    reverseSignedArea(t0);
    final List<Triangle2D> triangles = makeTriangles(t0);
    final Triangle2D gold = truth(t0);
    final double trueAreaX2 = gold.twiceSignedArea();
    for (final Triangle2D t : triangles) {
      final double areaX2 = t.twiceSignedArea();
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

  //--------------------------------------------------------------

  @Test
  public final void testSignedArea () {
    final Vector2D p0 = Vector2D.of( 0.0, 0.0);
    final Vector2D p1 = Vector2D.of( 1.0, 1.0);
    final Vector2D p2 = Vector2D.of( -1.0, 1.0);
    final Vector2D p3 = Vector2D.of( -1.0, -1.0);

    signedArea(TriangleVector2D.of(p0, p1, p2));
    // reverse
    signedArea(TriangleVector2D.of(p1, p0, p2));
    // 1 pt singular
    signedArea(TriangleVector2D.of(p0, p0, p0));
    // 2 pt line segment
    signedArea(TriangleVector2D.of(p0, p2, p0));
    signedArea(TriangleVector2D.of(p0, p0, p2));
    // Co-linear triangle
    signedArea(TriangleVector2D.of(p0, p1, p3));
  }

  //--------------------------------------------------------------

  private static  final void epsilonSignedArea (final double a) {
    // see https://groups.csail.mit.edu/graphics/classes/6.838/S98/meetings/m12/pred/m12.html
    final Vector2D p0 = Vector2D.of( a, 0.0);
    final Vector2D p1 = Vector2D.of( Math.nextUp(a), 0x1.0p10);
    final Vector2D p2 = Vector2D.of( Math.nextDown(a), 0x1.0p10);
    final Vector2D p3 = Vector2D.of( a, 1.0);

    System.out.println("p0=" + Triangle2D.toHexString(p0));
    System.out.println("p1=" + Triangle2D.toHexString(p1));
    System.out.println("p2=" + Triangle2D.toHexString(p2));
    System.out.println("p3=" + Triangle2D.toHexString(p3));

    final Triangle2D t013 = TriangleVector2D.of(p0, p1, p3);
    final Triangle2D bf013 = BigFloatTriangle2D.from(t013);
    System.out.println("bf013=" + bf013);
    System.out.println(Double.toHexString(bf013.twiceSignedArea()));
    signedArea(t013);

    final Triangle2D t023 = TriangleVector2D.of(p0, p2, p3);
    final Triangle2D bf023 = BigFloatTriangle2D.from(t023);
    System.out.println("bf023=" + bf023);
    System.out.println(Double.toHexString(bf023.twiceSignedArea()));
    signedArea(t023);
    System.out.println();
  }

  @Test
  public final void testEpsilonSignedArea () {
    // see https://groups.csail.mit.edu/graphics/classes/6.838/S98/meetings/m12/pred/m12.html
    epsilonSignedArea(1.0);
    epsilonSignedArea(0.0);
  }

  //--------------------------------------------------------------

  @Test
  public final void laplaceTest () {
    final int n = 32;
    final UniformRandomProvider urp =
      PRNG.well44497b("seeds/Well44497b-2019-01-05.txt");
    final Generator laplaceGenerator =
      Doubles.laplaceGenerator(urp, 0.0, 1.0);
    final Generator vGenerator =
      Generators.vector2dGenerator(laplaceGenerator);
    final Generator tGenerator = Generators.triangleGenerator(n,vGenerator);
    final Triangle2D[] t = (Triangle2D[]) tGenerator.next();
    for (int i = 0; i < n; i++) {  signedArea(t[i]); } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
