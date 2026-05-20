package mop.java.geometry.predicates.jts;

import mop.java.geometry.predicates.Predicate;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-05-19
 */

public final class InCircleNormalized implements Predicate {

  //--------------------------------------------------------------------
  // incircle
  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  public final double incircle (final double[] a,
                                final double[] b,
                                final double[] c,
                                final double[] p) {
    double adx = a[0] - p[0];
    double ady = a[1] - p[1];
    double bdx = b[0] - p[0];
    double bdy = b[1] - p[1];
    double cdx = c[0] - p[0];
    double cdy = c[1] - p[1];

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
