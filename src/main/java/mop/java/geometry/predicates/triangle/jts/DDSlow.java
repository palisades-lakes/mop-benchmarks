package mop.java.geometry.predicates.triangle.jts;

import mop.java.geometry.predicates.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.locationtech.jts.math.DD;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-04
 */

public final class DDSlow extends Triangle2D {

//--------------------------------------------------------------------

  public static DD triAreaDDSlow(DD ax, DD ay,
                                 DD bx, DD by,
                                 DD cx, DD cy) {
    return (bx.subtract(ax).multiply(cy.subtract(ay))
              .subtract(
                by.subtract(ay).multiply(cx.subtract(ax))));
  }

  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------

  @Override
  public final double signedArea (final Vector2D a,
                                  final Vector2D b,
                                  final Vector2D c) {
    final DD ax = DD.valueOf(a.getX());
    final DD ay = DD.valueOf(a.getY());
    final DD bx = DD.valueOf(b.getX());
    final DD by = DD.valueOf(b.getY());
    final DD cx = DD.valueOf(c.getX());
    final DD cy = DD.valueOf(c.getY());

    return triAreaDDSlow(ax,ay,bx,by,cx,cy).doubleValue(); }

  //--------------------------------------------------------------------
  // inCircle
  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  public final double inCircle (final Vector2D a,
                                final Vector2D b,
                                final Vector2D c,
                                final Vector2D p) {
    DD px = DD.valueOf(p.getX());
    DD py = DD.valueOf(p.getY());
    DD ax = DD.valueOf(a.getX());
    DD ay = DD.valueOf(a.getY());
    DD bx = DD.valueOf(b.getX());
    DD by = DD.valueOf(b.getY());
    DD cx = DD.valueOf(c.getX());
    DD cy = DD.valueOf(c.getY());

    DD aTerm = (ax.multiply(ax).add(ay.multiply(ay)))
      .multiply(triAreaDDSlow(bx, by, cx, cy, px, py));
    DD bTerm = (bx.multiply(bx).add(by.multiply(by)))
      .multiply(triAreaDDSlow(ax, ay, cx, cy, px, py));
    DD cTerm = (cx.multiply(cx).add(cy.multiply(cy)))
      .multiply(triAreaDDSlow(ax, ay, bx, by, px, py));
    DD pTerm = (px.multiply(px).add(py.multiply(py)))
      .multiply(triAreaDDSlow(ax, ay, bx, by, cx, cy));

    DD sum = aTerm.subtract(bTerm).add(cTerm).subtract(pTerm);
    return sum.doubleValue();
  }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public DDSlow () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
