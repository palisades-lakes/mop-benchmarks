package mop.java.geometry.triangle.jts;

import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.triangle.Triangle2D;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-21
 */

public final class InCircleNormalized extends Triangle2D {

  //--------------------------------------------------------------------
  // TODO: from Fast, make consistent with inCircle strategy

  public final double twiceSignedArea () {
    final VectorD2 pa = getP0();
    final VectorD2 pb = getP1();
    final VectorD2 pc = getP2();

    final double acx = pa.getX() - pc.getX();
    final double bcx = pb.getX() - pc.getX();
    final double acy = pa.getY() - pc.getY();
    final double bcy = pb.getY() - pc.getY();
    return (acx * bcy) - (acy * bcx); }

  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  public final double inCircleDistance (final VectorD2 p) {
    final VectorD2 pa = getP0();
    final VectorD2 pb = getP1();
    final VectorD2 pc = getP2();
    double adx = pa.getX() - p.getX();
    double ady = pa.getY() - p.getY();
    double bdx = pb.getX() - p.getX();
    double bdy = pb.getY() - p.getY();
    double cdx = pc.getX() - p.getX();
    double cdy = pc.getY() - p.getY();

    double abdet = adx * bdy - bdx * ady;
    double bcdet = bdx * cdy - cdx * bdy;
    double cadet = cdx * ady - adx * cdy;
    double alift = adx * adx + ady * ady;
    double blift = bdx * bdx + bdy * bdy;
    double clift = cdx * cdx + cdy * cdy;

    return alift * bcdet + blift * cadet + clift * abdet; }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private InCircleNormalized (final VectorD2 a,
                              final VectorD2 b,
                              final VectorD2 c)  {
    super(a,b,c); }

  public static final Triangle2D of (final VectorD2 a,
                                     final VectorD2 b,
                                     final VectorD2 c) {
    return new InCircleNormalized(a,b,c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
