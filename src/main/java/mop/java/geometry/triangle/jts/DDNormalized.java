package mop.java.geometry.triangle.jts;

import mop.java.geometry.triangle.Triangle2D;
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
  public final double inCircle (final Vector2D pa,
                                final Vector2D pb,
                                final Vector2D pc,
                                final Vector2D p) {
    DD adx = DD.valueOf(pa.getX()).selfSubtract(p.getX());
    DD ady = DD.valueOf(pa.getY()).selfSubtract(p.getY());
    DD bdx = DD.valueOf(pb.getX()).selfSubtract(p.getX());
    DD bdy = DD.valueOf(pb.getY()).selfSubtract(p.getY());
    DD cdx = DD.valueOf(pc.getX()).selfSubtract(p.getX());
    DD cdy = DD.valueOf(pc.getY()).selfSubtract(p.getY());

    DD abdet = adx.multiply(bdy).selfSubtract(bdx.multiply(ady));
    DD bcdet = bdx.multiply(cdy).selfSubtract(cdx.multiply(bdy));
    DD cadet = cdx.multiply(ady).selfSubtract(adx.multiply(cdy));
    DD alift = adx.multiply(adx).selfAdd(ady.multiply(ady));
    DD blift = bdx.multiply(bdx).selfAdd(bdy.multiply(bdy));
    DD clift = cdx.multiply(cdx).selfAdd(cdy.multiply(cdy));

    DD sum = alift.selfMultiply(bcdet)
                  .selfAdd(blift.selfMultiply(cadet))
                  .selfAdd(clift.selfMultiply(abdet));

    return sum.doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public DDNormalized () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
