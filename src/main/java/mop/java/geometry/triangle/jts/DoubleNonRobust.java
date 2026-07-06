package mop.java.geometry.triangle.jts;

import mop.java.geometry.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-06
 */

public final class DoubleNonRobust extends Triangle2D {

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
  public final double signedArea (final Vector2D pa, final Vector2D pb,
                                  final Vector2D pc) {
    return triArea(pa, pb, pc); }

  //--------------------------------------------------------------------
  // inCircle
  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  public final double inCircle (final Vector2D pa, final Vector2D pb,
                                final Vector2D pc, final Vector2D p) {
    return (pa.getX() * pa.getX() + pa.getY() * pa.getY()) * triArea(pb,
                                                                     pc, p)
        - (pb.getX() * pb.getX() + pb.getY() * pb.getY()) * triArea(pa,
                                                                    pc, p)
        + (pc.getX() * pc.getX() + pc.getY() * pc.getY()) * triArea(pa,
                                                                    pb, p)
        - (p.getX() * p.getX() + p.getY() * p.getY()) * triArea(pa, pb,
                                                                pc); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public DoubleNonRobust () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
