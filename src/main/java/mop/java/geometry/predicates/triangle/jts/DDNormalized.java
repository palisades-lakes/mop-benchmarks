package mop.java.geometry.predicates.triangle.jts;

import mop.java.geometry.predicates.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.locationtech.jts.math.DD;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-04
 */

public final class DDNormalized extends Triangle2D {

  //--------------------------------------------------------------------
  // inCircle
  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  public final double inCircle (final Vector2D a,
                                final Vector2D b,
                                final Vector2D c,
                                final Vector2D p) {
    DD adx = DD.valueOf(a.getX()).selfSubtract(p.getX());
    DD ady = DD.valueOf(a.getY()).selfSubtract(p.getY());
    DD bdx = DD.valueOf(b.getX()).selfSubtract(p.getX());
    DD bdy = DD.valueOf(b.getY()).selfSubtract(p.getY());
    DD cdx = DD.valueOf(c.getX()).selfSubtract(p.getX());
    DD cdy = DD.valueOf(c.getY()).selfSubtract(p.getY());

    DD abdet = adx.multiply(bdy).selfSubtract(bdx.multiply(ady));
    DD bcdet = bdx.multiply(cdy).selfSubtract(cdx.multiply(bdy));
    DD cadet = cdx.multiply(ady).selfSubtract(adx.multiply(cdy));
    DD alift = adx.multiply(adx).selfAdd(ady.multiply(ady));
    DD blift = bdx.multiply(bdx).selfAdd(bdy.multiply(bdy));
    DD clift = cdx.multiply(cdx).selfAdd(cdy.multiply(cdy));

    DD sum = alift.selfMultiply(bcdet)
                  .selfAdd(blift.selfMultiply(cadet))
                  .selfAdd(clift.selfMultiply(abdet));

    return sum.doubleValue();
  }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public DDNormalized () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
