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

//----------------------------------------------------------------
/** Common code for geometry predicate tests.
 * <pre>
 * mvn -Dtest=mop.java.test.geometry.triangles.InCircleTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-09-29
 */

public final class InCircleTest extends TriangleTest {


  //--------------------------------------------------------------

  private static final void inCircle (final TriangleR2 t,
                                      final VectorD2 p) {
    final TriangleR2 gold = TriangleR2.truth(t);
    final double trueInc = gold.inCircleDistance(p);
    final List<TriangleR2> triangles = TriangleR2.makeTriangles(t);
    for (final TriangleR2 ti : triangles) {
      final double inc = ti.inCircleDistance(p);
      if (ti.inCircleDistanceExact()) {
        // with delta=0.0 handles +0 vs -0 'correctly'
        Assertions.assertEquals(
          trueInc, inc, 0.0,
          failureMsg("inCircle",trueInc,inc,gold,ti,triangles,p)); }
      else {
        Assertions.assertEquals(
          Math.signum(trueInc), Math.signum(inc), 0.0,
          failureMsg("inCircle",trueInc,inc,gold,ti,triangles,p)); } } }

  //--------------------------------------------------------------

  @Test
  public final void simpleTest () {
    final VectorD2 p0 =  new VectorD2( 0.0, 0.0);
    final VectorD2 p1 =  new VectorD2( 1.0, 1.0);
    final VectorD2 p2 =  new VectorD2( -1.0, 1.0);
    final VectorD2 p3 =  new VectorD2( -1.0, -1.0);
    final VectorD2 p4 =  new VectorD2( 1.0, -1.0);

    final TriangleR2 t = TriangleD2Lazy.of(p1, p2, p3);
    inCircle(t, p0);
    inCircle(t, p4);
    inCircle(t, p1);
    // Not working for InCircleCC
    // TODO: decide on the right answer for singular cases.
    // inCircle(SegmentD2Lazy.of(p1, p1, p1), p4);
    // inCircle(SegmentD2Lazy.of(p1, p2, p1), p4);
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
        n, Generators.vectorD2Generator(
          Doubles.laplaceGenerator(urp0, 0.0, 1.0)));
    final TriangleR2[] t = (TriangleR2[]) tGenerator.next();
    final UniformRandomProvider urp1 =
      PRNG.well44497b("seeds/Well44497b-2019-01-09.txt");
    final Generator pGenerator =
      Generators.vectorD2Generator(
        n, Doubles.laplaceGenerator(urp1, 0.0, 1.0));
    final VectorD2[] p = (VectorD2[]) pGenerator.next();
    for (int i = 0; i < m; i++) {
      final TriangleR2 ti = t[i];
      for (int j=0;j<n;j++) {
        inCircle(ti,p[j]); } } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
