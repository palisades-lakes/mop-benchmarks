package mop.java.geometry.triangle.jts;

import mop.java.geometry.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-06
 */

public final class InCircleNormalized extends Triangle2D {

  //--------------------------------------------------------------------
  // inCircle
  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  public final double inCircle (final Vector2D pa, final Vector2D pb,
                                final Vector2D pc, final Vector2D p) {
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

    return alift * bcdet + blift * cadet + clift * abdet;
  }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public InCircleNormalized () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
