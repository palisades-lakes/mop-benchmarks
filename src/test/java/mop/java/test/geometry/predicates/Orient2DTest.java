package mop.java.test.geometry.predicates;

import mop.java.geometry.predicates.*;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

//----------------------------------------------------------------

/** Geometry predicates.
 * <pre>
 * mvn -Dtest=mop.java.test.geometry.predicates.Orient2DTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-03
 */

public final class Orient2DTest {

  //--------------------------------------------------------------
  private static final String failureMsg (final double truth,
                                          final Predicate gold,
                                          final Predicate pred,
                                          final List<Predicate> predicates,
                                          final Vector2D p0,
                                          final Vector2D p1,
                                          final Vector2D p2) {
    StringBuilder msg = new StringBuilder("\norient2d(" +
                                            p0 + "," +
                                            p1 + "," +
                                            p2 + ")" +
                                            "\ngold=" + gold + ", truth=" + truth +
                                            "\npred=" + pred +
                                            " -> " + Double.toHexString(
      pred.orient2d(p0, p1, p2)));
    for (final Predicate p : predicates) {
      msg.append("\n").append(p).append(" -> ")
         .append(Double.toHexString(p.orient2d(p0, p1, p2))); }
    return msg + "\n"; }

  //--------------------------------------------------------------

  private static final void orient2D (final List<Predicate> predicates,
                                      final Vector2D p0,
                                      final Vector2D p1,
                                      final Vector2D p2) {
    final Predicate gold = Common.truth();
    final double trueAreaX2 = gold.orient2d(p0, p1, p2);
    for (final Predicate p : predicates) {
      final double areaX2 = p.orient2d(p0, p1, p2);
      if (p.isExact()) {
        // with delta=0.0 handles +0 vs -0 'correctly'
        Assertions.assertEquals(
          trueAreaX2, areaX2, 0.0,
          failureMsg(trueAreaX2,gold,p,predicates,p0,p1,p2)); }
      else {
        Assertions.assertEquals(
          Math.signum(trueAreaX2), Math.signum(areaX2), 0.0,
          failureMsg(trueAreaX2,gold,p,predicates,p0,p1,p2)); } } }

  @Test
  public final void testOrient2D () {
    final Vector2D p0 = Vector2D.of( 0.0, 0.0);
    final Vector2D p1 = Vector2D.of( 1.0, 1.0);
    final Vector2D p2 = Vector2D.of( -1.0, 1.0);
    final Vector2D p3 = Vector2D.of( -1.0, -1.0);

    final List<Predicate> predicates = Common.orient2dPredicates();
    orient2D(predicates, p0, p1, p2);
    // reverse
    orient2D(predicates, p1, p0, p2);
    // 1 pt singular
    orient2D(predicates, p0, p0, p0);
    // 2 pt line segment
    orient2D(predicates,p0, p2, p0);
    // TODO: Slow returns -1, not 0
    orient2D(predicates,p0, p0, p2);
    //orient2D(List.of(new Adapt(),new Fast()),p0, p0, p2);
    // Co-linear triangle
    orient2D(predicates, p0, p1, p3);
  }

  //--------------------------------------------------------------

//  @Test
//  public final void laplaceTest () {
//    final List<Predicate> predicates = Common.inCirclePredicates();
//    final int n = 12;
//    final UniformRandomProvider urp =
//      PRNG.well44497b("seeds/Well44497b-2019-01-05.txt");
//    final Generator laplaceGenerator =
//      Doubles.laplaceGenerator(n, 2, urp, 0.0, 1.0);
//    final double[][] p = (double[][]) laplaceGenerator.next();
//    for (int i = 0; i < n-3; i++) {
//      orient2D(predicates, p[i], p[i+1], p[i+2], p[i+3]);} }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
