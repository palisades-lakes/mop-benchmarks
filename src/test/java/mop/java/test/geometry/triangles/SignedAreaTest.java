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
 * mvn -Dtest=mop.java.test.geometry.triangles.SignedAreaTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-09-26
 */

public final class SignedAreaTest extends TriangleTest {

  //--------------------------------------------------------------

  private static final void reverseSignedArea (final TriangleR2 t0) {
    final TriangleR2
      t1 = TriangleD2Lazy.of(t0.getP0(), t0.getP2(), t0.getP1());
    final TriangleR2 plus = TriangleR2.truth(t0);
    final double aplus = plus.twiceSignedArea();
    final TriangleR2 minus = TriangleR2.truth(t1);
    final double aminus = minus.twiceSignedArea();
        // with delta=0.0 handles +0 vs -0 'correctly'
        Assertions.assertEquals(
          plus.twiceSignedArea(), -minus.twiceSignedArea(), 0.0,
          failureMsg("reverseSignedArea",aplus,aminus,
                     plus,minus,List.of(),null)); }

  private static final void signedArea (final TriangleR2 t0) {
    reverseSignedArea(t0);
    final List<TriangleR2> triangles = TriangleR2.makeTriangles(t0);
    final TriangleR2 gold = TriangleR2.truth(t0);
    final double trueAreaX2 = gold.twiceSignedArea();
    for (final TriangleR2 t : triangles) {
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
    final VectorD2 p0 = new VectorD2( 0.0, 0.0);
    final VectorD2 p1 = new VectorD2( 1.0, 1.0);
    final VectorD2 p2 = new VectorD2( -1.0, 1.0);
    final VectorD2 p3 = new VectorD2( -1.0, -1.0);

    signedArea(TriangleD2Lazy.of(p0, p1, p2));
    // reverse
    signedArea(TriangleD2Lazy.of(p1, p0, p2));
    // 1 pt singular
    signedArea(TriangleD2Lazy.of(p0, p0, p0));
    // 2 pt line segment
    signedArea(TriangleD2Lazy.of(p0, p2, p0));
    signedArea(TriangleD2Lazy.of(p0, p0, p2));
    // Co-linear triangle
    signedArea(TriangleD2Lazy.of(p0, p1, p3));
  }

  //--------------------------------------------------------------

  private static  final void epsilonSignedArea (final double a) {
    // see https://groups.csail.mit.edu/graphics/classes/6.838/S98/meetings/m12/pred/m12.html
    final VectorD2 p0 = new VectorD2( a, 0.0);
    final VectorD2 p1 = new VectorD2( Math.nextUp(a), 0x1.0p10);
    final VectorD2 p2 = new VectorD2( Math.nextDown(a), 0x1.0p10);
    final VectorD2 p3 = new VectorD2( a, 1.0);

//    System.out.println("p0=" + TriangleR2.toHexString(p0));
//    System.out.println("p1=" + TriangleR2.toHexString(p1));
//    System.out.println("p2=" + TriangleR2.toHexString(p2));
//    System.out.println("p3=" + TriangleR2.toHexString(p3));

    final TriangleR2 t013 = TriangleD2Lazy.of(p0, p1, p3);
//    final TriangleR2 bf013 = TriangleBF2.from(t013);
//    System.out.println("bf013=" + bf013);
//    System.out.println(Double.toHexString(bf013.twiceSignedArea()));
    signedArea(t013);

    final TriangleR2 t023 = TriangleD2Lazy.of(p0, p2, p3);
//    final TriangleR2 bf023 = TriangleBF2.from(t023);
//    System.out.println("bf023=" + bf023);
//    System.out.println(Double.toHexString(bf023.twiceSignedArea()));
    signedArea(t023);
//    System.out.println();
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
      Generators.vectorD2Generator(laplaceGenerator);
    final Generator tGenerator = Generators.triangleGenerator(n,vGenerator);
    final TriangleR2[] t = (TriangleR2[]) tGenerator.next();
    for (int i = 0; i < n; i++) {  signedArea(t[i]); } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
