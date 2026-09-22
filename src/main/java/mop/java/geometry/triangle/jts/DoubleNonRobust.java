package mop.java.geometry.triangle.jts;

import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.triangle.Triangle2D;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-21
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
  private static double triArea (final VectorD2 a,
                                 final VectorD2 b,
                                 final VectorD2 c) {
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
  public final double inCircleDistance (final VectorD2 p) {
    final VectorD2 pa = getP0();
    final VectorD2 pb = getP1();
    final VectorD2 pc = getP2();

    return
      (pa.l2norm2()*triArea(pb,pc,p))
        - pb.l2norm2()*triArea(pa,pc,p)
        + pc.l2norm2()*triArea(pa,pb,p)
        - p.l2norm2()*triArea(pa,pb,pc); }
//  return
//    (pa.getX()*pa.getX() + pa.getY()*pa.getY())*triArea(pb,pc,p)
//      - (pb.getX()*pb.getX() + pb.getY()*pb.getY())*triArea(pa,pc,p)
//      + (pc.getX()*pc.getX() + pc.getY()*pc.getY())*triArea(pa,pb,p)
//      - (p.getX()*p.getX() + p.getY()*p.getY())*triArea(pa,pb,pc); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private DoubleNonRobust (final VectorD2 a,
                           final VectorD2 b,
                           final VectorD2 c)  {
    super(a,b,c); }

  public static final Triangle2D of (final VectorD2 a,
                                     final VectorD2 b,
                                     final VectorD2 c) {
    return new DoubleNonRobust(a,b,c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
