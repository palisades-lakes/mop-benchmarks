package mop.java.geometry.predicates.jts;

import mop.java.geometry.predicates.Predicate;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.locationtech.jts.math.DD;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-04
 */

public final class DDFast implements Predicate {

//--------------------------------------------------------------------

  public static DD triAreaDDFast (final Vector2D a,
                                  final Vector2D b,
                                  final Vector2D c) {

    DD t1 = DD.valueOf(b.getX()).selfSubtract(a.getX())
              .selfMultiply(
                DD.valueOf(c.getY()).selfSubtract(a.getY()));

    DD t2 = DD.valueOf(b.getY()).selfSubtract(a.getY())
              .selfMultiply(
                DD.valueOf(c.getX()).selfSubtract(a.getX()));

    return t1.selfSubtract(t2); }

  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------

  @Override
  public final double orient2d (final Vector2D a,
                                final Vector2D b,
                                final Vector2D c) {
    return triAreaDDFast(a,b,c) .doubleValue(); }

  //--------------------------------------------------------------------
  // incircle
  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  @Override
  public final double incircle (final Vector2D a,
                                final Vector2D b,
                                final Vector2D c,
                                final Vector2D p) {
    DD aTerm = (DD.sqr(a.getX()).selfAdd(DD.sqr(a.getY())))
      .selfMultiply(triAreaDDFast(b, c, p));
    DD bTerm = (DD.sqr(b.getX()).selfAdd(DD.sqr(b.getY())))
      .selfMultiply(triAreaDDFast(a, c, p));
    DD cTerm = (DD.sqr(c.getX()).selfAdd(DD.sqr(c.getY())))
      .selfMultiply(triAreaDDFast(a, b, p));
    DD pTerm = (DD.sqr(p.getX()).selfAdd(DD.sqr(p.getY())))
      .selfMultiply(triAreaDDFast(a, b, c));

    DD sum = aTerm.selfSubtract(bTerm).selfAdd(cTerm).selfSubtract(pTerm);
    return sum.doubleValue();
  }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public DDFast () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
