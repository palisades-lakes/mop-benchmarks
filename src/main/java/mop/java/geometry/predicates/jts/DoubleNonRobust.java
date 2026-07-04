package mop.java.geometry.predicates.jts;

import mop.java.geometry.predicates.Predicate;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-04
 */

public final class DoubleNonRobust implements Predicate {

  //--------------------------------------------------------------------
  /** TrianglePredicate.triArea
   * Computes twice the area of the oriented triangle (a, b, c), i.e., the area is positive if the
   * triangle is oriented counterclockwise.
   *
   * @param a a vertex of the triangle
   * @param b a vertex of the triangle
   * @param c a vertex of the triangle
   */
  private static double triArea (final Vector2D a,
                                 final Vector2D b,
                                 final Vector2D c) {
    return (b.getX() - a.getX()) * (c.getY() - a.getY())
      - (b.getY() - a.getY()) * (c.getX() - a.getX());
  }
  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------

  @Override
  public final double signedArea (final Vector2D a,
                                  final Vector2D b,
                                  final Vector2D c) {
    return triArea(a,b,c); }

  //--------------------------------------------------------------------
  // incircle
  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  public final double incircle (final Vector2D a,
                                final Vector2D b,
                                final Vector2D c,
                                final Vector2D p) {
    return (a.getX() * a.getX() + a.getY() * a.getY()) * triArea(b, c, p)
        - (b.getX() * b.getX() + b.getY() * b.getY()) * triArea(a, c, p)
        + (c.getX() * c.getX() + c.getY() * c.getY()) * triArea(a, b, p)
        - (p.getX() * p.getX() + p.getY() * p.getY()) * triArea(a, b, c); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public DoubleNonRobust () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
