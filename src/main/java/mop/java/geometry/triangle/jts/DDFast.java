package mop.java.geometry.triangle.jts;

import mop.java.geometry.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.locationtech.jts.math.DD;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-06
 */

public final class DDFast extends Triangle2D {

//--------------------------------------------------------------------

  public static DD triAreaDDFast (final Vector2D a,
                                  final Vector2D b,
                                  final Vector2D c) {

    DD t1 = DD.valueOf(b.getX()).selfSubtract(a.getX())
              .selfMultiply(
                DD.valueOf(c.getY()).selfSubtract(a.getY()));

    DD t2 = DD.valueOf(b.getY()).selfSubtract(a.getY())
              .selfMultiply(
                DD.valueOf(c.getX()).selfSubtract(a.getX()));

    return t1.selfSubtract(t2); }

  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------

  @Override
  public final double signedArea (final Vector2D pa,
                                  final Vector2D pb,
                                  final Vector2D pc) {
    return triAreaDDFast(pa, pb, pc).doubleValue(); }

  //--------------------------------------------------------------------
  // inCircle
  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  @Override
  public final double inCircle (final Vector2D pa,
                                final Vector2D pb,
                                final Vector2D pc,
                                final Vector2D p) {
    DD aTerm = (DD.sqr(pa.getX()).selfAdd(DD.sqr(pa.getY())))
      .selfMultiply(triAreaDDFast(pb, pc, p));
    DD bTerm = (DD.sqr(pb.getX()).selfAdd(DD.sqr(pb.getY())))
      .selfMultiply(triAreaDDFast(pa, pc, p));
    DD cTerm = (DD.sqr(pc.getX()).selfAdd(DD.sqr(pc.getY())))
      .selfMultiply(triAreaDDFast(pa, pb, p));
    DD pTerm = (DD.sqr(p.getX()).selfAdd(DD.sqr(p.getY())))
      .selfMultiply(triAreaDDFast(pa, pb, pc));

    DD sum = aTerm.selfSubtract(bTerm).selfAdd(cTerm).selfSubtract(pTerm);
    return sum.doubleValue();
  }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public DDFast () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
