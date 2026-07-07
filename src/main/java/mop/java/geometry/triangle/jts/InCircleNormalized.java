package mop.java.geometry.triangle.jts;

import mop.java.geometry.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-07
 */

public final class InCircleNormalized extends Triangle2D {

  //--------------------------------------------------------------------
  // TODO: from Fast, make consistent with inCircle strategy

  public final double signedArea () {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();

    final double acx = pa.getX() - pc.getX();
    final double bcx = pb.getX() - pc.getX();
    final double acy = pa.getY() - pc.getY();
    final double bcy = pb.getY() - pc.getY();
    return (acx * bcy) - (acy * bcx); }

  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  public final double inCircle (final Vector2D p) {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();
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

  private InCircleNormalized (final Vector2D a,
                  final Vector2D b,
                  final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new InCircleNormalized(a,b,c); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
