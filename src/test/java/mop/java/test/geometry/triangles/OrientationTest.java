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
 * mvn -Dtest=mop.java.test.geometry.triangles.OrientationTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-29
 */

public final class OrientationTest extends TriangleTest {

  public static final String orientationMsg (final String name,
                                             final double truth,
                                             final double check,
                                             final Triangle2D gold,
                                             final Triangle2D pred,
                                             final List<Triangle2D> triangles) {
    final StringBuilder msg = new StringBuilder(
      "\n" + name +
        "\ngold=" + gold + " -> " + Double.toHexString(truth) +
        "\npred=" + pred + " -> " + Double.toHexString(check));
    if (null != triangles) {
      for (final Triangle2D t : triangles) {
        msg.append("\n").append(t).append(" ->\n");
        msg.append(Double.toHexString(t.orientation())); } }
    return msg + "\n"; }

  //--------------------------------------------------------------

  private static final void checkOrientation (final Triangle2D t0) {
    final List<Triangle2D> triangles = makeTriangles(t0);
    final Triangle2D gold = truth(t0);
    final double trueOrientation = gold.orientation();
    for (final Triangle2D t : triangles) {
      final double orientation = t.orientation();
      if (t.isOrientationRobust()) {
      Assertions.assertEquals(
        trueOrientation, orientation,
        orientationMsg("checkOrientation",trueOrientation,orientation,
                       gold,t,triangles)); } } }

  //--------------------------------------------------------------

  @Test
  public final void testOrientation () {
    final Vector2D p0 = Vector2D.of( 0.0, 0.0);
    final Vector2D p1 = Vector2D.of( 1.0, 1.0);
    final Vector2D p2 = Vector2D.of( -1.0, 1.0);
    final Vector2D p3 = Vector2D.of( -1.0, -1.0);

    checkOrientation(TriangleVector2D.of(p0, p1, p2));
    // reverse
    checkOrientation(TriangleVector2D.of(p1, p0, p2));
    // 1 pt singular
    checkOrientation(TriangleVector2D.of(p0, p0, p0));
    // 2 pt line segment
    checkOrientation(TriangleVector2D.of(p0, p2, p0));
    checkOrientation(TriangleVector2D.of(p0, p0, p2));
    // Co-linear triangle
    checkOrientation(TriangleVector2D.of(p0, p1, p3));
  }

  //--------------------------------------------------------------

  private static  final void epsilonOrientation (final double a) {
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
    System.out.println(Double.toHexString(bf013.orientation()));
    checkOrientation(t013);

    final Triangle2D t023 = TriangleVector2D.of(p0, p2, p3);
    final Triangle2D bf023 = BigFloatTriangle2D.from(t023);
    System.out.println("bf023=" + bf023);
    System.out.println(Double.toHexString(bf023.orientation()));
    checkOrientation(t023);
    System.out.println();
  }

  @Test
  public final void testEpsilonOrientation () {
    // see https://groups.csail.mit.edu/graphics/classes/6.838/S98/meetings/m12/pred/m12.html
    epsilonOrientation(1.0);
    epsilonOrientation(0.0); }

  //--------------------------------------------------------------
  // see https://inria.hal.science/inria-00344310v1/document
  // fig 2

  public final void checkKettnerOrientation (final Vector2D p,
                                             final Vector2D q,
                                             final Vector2D r) {
    double px = p.getX();
    double py = p.getY();
    final double ux = 0x1.0p-53; //Math.ulp(px);
    final double uy = 0x1.0p-53; //Math.ulp(py);
    final int n = 33;
    for (int i=0;i<n;i++) {
      final double pxi = px + i*ux;
      for (int j=0;j<n;j++) {
        final double pyj = py + j*uy;
        final Vector2D pij = Vector2D.of(pxi, pyj);
        final Triangle2D t = TriangleVector2D.of(pij, q, r);
        checkOrientation(t); } } }

  @Test
  public final void testKettnerOrientation () {
    checkKettnerOrientation(
      Vector2D.of(0.5,0.5),
      Vector2D.of( 12, 12),
      Vector2D.of( 24, 24));

    checkKettnerOrientation(
      Vector2D.of(0.50000000000002531,0.5000000000000171),
      Vector2D.of( 17.300000000000001,17.300000000000001),
      Vector2D.of( 24.00000000000005, 24.0000000000000517765));

    checkKettnerOrientation(
      Vector2D.of(0.5,0.5),
      Vector2D.of( 8.8000000000000007, 8.8000000000000007),
      Vector2D.of( 12.1, 12.1));
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
    for (int i = 0; i < n; i++) {  checkOrientation(t[i]); } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
