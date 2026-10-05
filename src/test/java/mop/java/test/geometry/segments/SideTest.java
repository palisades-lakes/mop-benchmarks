package mop.java.test.geometry.segments;

import mop.java.geometry.Generators;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.segment.SegmentD2Lazy;
import mop.java.geometry.segment.SegmentR2;
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
 * mvn -Dtest=mop.java.test.geometry.segments.SideTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-10-05
 */

public final class SideTest extends SegmentTest {

  //--------------------------------------------------------------

  private static final void checkSide (final SegmentR2 t,
                                       final VectorD2 p) {
    final SegmentR2 gold = SegmentR2.truth(t);
    final double trueS = gold.side(p);
    final List<SegmentR2> segments = SegmentR2.makeSegments(t);
    for (final SegmentR2 ti : segments) {
      final double s = ti.side(p);
      if (ti.sideExact()) {
        // with delta=0.0 handles +0 vs -0 'correctly'
        Assertions.assertEquals(
          trueS, s, 0.0,
          failureMsg("side",trueS,s,gold,ti,segments,p)); }
      else if (ti.sideRobust(p)){
        Assertions.assertEquals(
          Math.signum(trueS), Math.signum(s), 0.0,
          failureMsg("inCircle",trueS,s,gold,ti,segments,p)); } } }

  //--------------------------------------------------------------

  @Test
  public final void simpleTest () {
    final VectorD2 p0 =  new VectorD2( 0.0, 0.0);
    final VectorD2 p1 =  new VectorD2( 1.0, 1.0);
    final VectorD2 p2 =  new VectorD2( -1.0, 1.0);
    final VectorD2 p3 =  new VectorD2( -1.0, -1.0);
    final VectorD2 p4 =  new VectorD2( 1.0, -1.0);

    final SegmentR2 t = SegmentD2Lazy.of(p1, p2);
    checkSide(t, p0);
    checkSide(t, p3);
    checkSide(t, p4);
    checkSide(t, p1);
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
      Generators.segmentGenerator(
        n, Generators.vectorD2Generator(
          Doubles.laplaceGenerator(urp0, 0.0, 1.0)));
    final SegmentR2[] t = (SegmentR2[]) tGenerator.next();
    final UniformRandomProvider urp1 =
      PRNG.well44497b("seeds/Well44497b-2019-01-09.txt");
    final Generator pGenerator =
      Generators.vectorD2Generator(
        n, Doubles.laplaceGenerator(urp1, 0.0, 1.0));
    final VectorD2[] p = (VectorD2[]) pGenerator.next();
    for (int i = 0; i < m; i++) {
      final SegmentR2 ti = t[i];
      for (int j=0;j<n;j++) {
        checkSide(ti, p[j]); } } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
