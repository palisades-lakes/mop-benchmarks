package mop.java.geometry.predicates.triangle.jts;

import mop.java.geometry.predicates.triangle.Triangle2D;
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
  public final double inCircle (final Vector2D a,
                                final Vector2D b,
                                final Vector2D c,
                                final Vector2D p) {
    double adx = a.getX() - p.getX();
    double ady = a.getY() - p.getY();
    double bdx = b.getX() - p.getX();
    double bdy = b.getY() - p.getY();
    double cdx = c.getX() - p.getX();
    double cdy = c.getY() - p.getY();

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
