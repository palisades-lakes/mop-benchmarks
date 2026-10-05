package mop.java.test.geometry.triangles;

import mop.java.geometry.Generators;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.triangle.TriangleD2Lazy;
import mop.java.geometry.triangle.TriangleR2;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.rng.UniformRandomProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/** <pre>
 * mvn -Dtest=mop.java.test.geometry.triangles.SideTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-09-26
 */

public final class OrientationTest extends TriangleTest {

  public static final String orientationMsg (final String name,
                                             final double truth,
                                             final double check,
                                             final TriangleR2 gold,
                                             final TriangleR2 pred,
                                             final List<TriangleR2> triangles) {
    final StringBuilder msg = new StringBuilder(
      "\n" + name +
        "\ngold=" + gold + " -> " + Double.toHexString(truth) +
        "\npred=" + pred + " -> " + Double.toHexString(check));
    if (null != triangles) {
      for (final TriangleR2 t : triangles) {
        msg.append("\n").append(t).append(" ->\n");
        msg.append(Double.toHexString(t.orientation())); } }
    return msg + "\n"; }

  //--------------------------------------------------------------

  private static final void checkOrientation (final TriangleR2 t0) {
    final List<TriangleR2> triangles = TriangleR2.makeTriangles(t0);
    final TriangleR2 gold = TriangleR2.truth(t0);
    final double trueOrientation = gold.orientation();
    for (final TriangleR2 t : triangles) {
      final double orientation = t.orientation();
      if (t.isOrientationRobust()) {
      Assertions.assertEquals(
        trueOrientation, orientation,
        orientationMsg("checkOrientation",trueOrientation,orientation,
                       gold,t,triangles)); } } }

  //--------------------------------------------------------------

  @Test
  public final void testOrientation () {
    final VectorD2 p0 = new VectorD2( 0.0, 0.0);
    final VectorD2 p1 = new VectorD2( 1.0, 1.0);
    final VectorD2 p2 = new VectorD2( -1.0, 1.0);
    final VectorD2 p3 = new VectorD2( -1.0, -1.0);

    checkOrientation(TriangleD2Lazy.of(p0, p1, p2));
    // reverse
    checkOrientation(TriangleD2Lazy.of(p1, p0, p2));
    // 1 pt singular
    checkOrientation(TriangleD2Lazy.of(p0, p0, p0));
    // 2 pt line segment
    checkOrientation(TriangleD2Lazy.of(p0, p2, p0));
    checkOrientation(TriangleD2Lazy.of(p0, p0, p2));
    // Co-linear triangle
    checkOrientation(TriangleD2Lazy.of(p0, p1, p3));
  }

  //--------------------------------------------------------------

  private static  final void epsilonOrientation (final double a) {
    // see https://groups.csail.mit.edu/graphics/classes/6.838/S98/meetings/m12/pred/m12.html
    final VectorD2 p0 = new VectorD2( a, 0.0);
    final VectorD2 p1 = new VectorD2( Math.nextUp(a), 0x1.0p10);
    final VectorD2 p2 = new VectorD2( Math.nextDown(a), 0x1.0p10);
    final VectorD2 p3 = new VectorD2( a, 1.0);

//    System.out.println("p0=" + SegmentR2.toHexString(p0));
//    System.out.println("p1=" + SegmentR2.toHexString(p1));
//    System.out.println("p2=" + SegmentR2.toHexString(p2));
//    System.out.println("p3=" + SegmentR2.toHexString(p3));

    final TriangleR2 t013 = TriangleD2Lazy.of(p0, p1, p3);
//    final SegmentR2 bf013 = SegmentBF2.from(t013);
//    System.out.println("bf013=" + bf013);
//    System.out.println(Double.toHexString(bf013.orientation()));
    checkOrientation(t013);

    final TriangleR2 t023 = TriangleD2Lazy.of(p0, p2, p3);
//    final SegmentR2 bf023 = SegmentBF2.from(t023);
//    System.out.println("bf023=" + bf023);
//    System.out.println(Double.toHexString(bf023.orientation()));
    checkOrientation(t023);
//    System.out.println();
  }

  @Test
  public final void testEpsilonOrientation () {
    // see https://groups.csail.mit.edu/graphics/classes/6.838/S98/meetings/m12/pred/m12.html
    epsilonOrientation(1.0);
    epsilonOrientation(0.0); }

  //--------------------------------------------------------------
  // see https://inria.hal.science/inria-00344310v1/document
  // fig 2

  public final void checkKettnerOrientation (final VectorD2 p,
                                             final VectorD2 q,
                                             final VectorD2 r) {
    double px = p.getX();
    double py = p.getY();
    final double ux = 0x1.0p-53; //Math.ulp(px);
    final double uy = 0x1.0p-53; //Math.ulp(py);
    final int n = 33;
    for (int i=0;i<n;i++) {
      final double pxi = px + i*ux;
      for (int j=0;j<n;j++) {
        final double pyj = py + j*uy;
        final VectorD2 pij = new VectorD2(pxi, pyj);
        final TriangleR2 t = TriangleD2Lazy.of(pij, q, r);
        checkOrientation(t); } } }

  @Test
  public final void testKettnerOrientation () {
    checkKettnerOrientation(
      new VectorD2(0.5,0.5),
      new VectorD2( 12, 12),
      new VectorD2( 24, 24));

    checkKettnerOrientation(
      new VectorD2(0.50000000000002531,0.5000000000000171),
      new VectorD2( 17.300000000000001,17.300000000000001),
      new VectorD2( 24.00000000000005, 24.0000000000000517765));

    checkKettnerOrientation(
      new VectorD2(0.5,0.5),
      new VectorD2( 8.8000000000000007, 8.8000000000000007),
      new VectorD2( 12.1, 12.1));
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
      Generators.vectorD2Generator(laplaceGenerator);
    final Generator tGenerator = Generators.triangleGenerator(n,vGenerator);
    final TriangleR2[] t = (TriangleR2[]) tGenerator.next();
    for (int i = 0; i < n; i++) {  checkOrientation(t[i]); } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
