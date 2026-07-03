package mop.java.geometry.predicates.jts;

import mop.java.geometry.predicates.Predicate;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.locationtech.jts.math.DD;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-03
 */

public final class DDSlow implements Predicate {

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
  public final double orient2d (final Vector2D a,
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
  // incircle
  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  public final double incircle (final double[] a,
                                final double[] b,
                                final double[] c,
                                final double[] p) {
    DD px = DD.valueOf(p[0]);
    DD py = DD.valueOf(p[1]);
    DD ax = DD.valueOf(a[0]);
    DD ay = DD.valueOf(a[1]);
    DD bx = DD.valueOf(b[0]);
    DD by = DD.valueOf(b[1]);
    DD cx = DD.valueOf(c[0]);
    DD cy = DD.valueOf(c[1]);

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
