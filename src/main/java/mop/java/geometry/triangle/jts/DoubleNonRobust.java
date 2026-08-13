package mop.java.geometry.triangle.jts;

import mop.java.geometry.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-27
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
    // TODO: cache difference vectors
    return
      (b.getX() - a.getX()) * (c.getY() - a.getY())
      -
        (b.getY() - a.getY()) * (c.getX() - a.getX());
  }
  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------

  @Override
  public final double twiceSignedArea () {
    return triArea(getP0(),getP1(),getP2()); }

  //--------------------------------------------------------------------
  // inCircle
  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  public final double inCircle (final Vector2D p) {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();

    return (pa.getX()*pa.getX()
      + pa.getY()*pa.getY())*triArea(pb,pc,p)
      - (pb.getX()*pb.getX() + pb.getY()*pb.getY())*triArea(pa,pc,p)
      + (pc.getX()*pc.getX()
      + pc.getY()*pc.getY())*triArea(pa,pb,p)
      - (p.getX()*p.getX() + p.getY()*p.getY())*triArea(pa,pb,pc); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private DoubleNonRobust (final Vector2D a,
                  final Vector2D b,
                  final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new DoubleNonRobust(a,b,c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
