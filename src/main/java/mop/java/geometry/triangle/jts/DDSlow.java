package mop.java.geometry.triangle.jts;

import mop.java.geometry.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.locationtech.jts.math.DD;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-27
 */

public final class DDSlow extends Triangle2D {

//--------------------------------------------------------------------

  public static DD triAreaDDSlow(DD ax, DD ay,
                                 DD bx, DD by,
                                 DD cx, DD cy) {
    return (bx.subtract(ax).multiply(cy.subtract(ay))
              .subtract(
                by.subtract(ay).multiply(cx.subtract(ax)))); }

  //--------------------------------------------------------------------

  @Override
  public final double twiceSignedArea () {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();
    final DD ax = DD.valueOf(pa.getX());
    final DD ay = DD.valueOf(pa.getY());
    final DD bx = DD.valueOf(pb.getX());
    final DD by = DD.valueOf(pb.getY());
    final DD cx = DD.valueOf(pc.getX());
    final DD cy = DD.valueOf(pc.getY());

    return triAreaDDSlow(ax,ay,bx,by,cx,cy).doubleValue(); }

  //--------------------------------------------------------------------
  // inCircle
  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  public final double inCircle (final Vector2D p) {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();
    DD px = DD.valueOf(p.getX());
    DD py = DD.valueOf(p.getY());
    DD ax = DD.valueOf(pa.getX());
    DD ay = DD.valueOf(pa.getY());
    DD bx = DD.valueOf(pb.getX());
    DD by = DD.valueOf(pb.getY());
    DD cx = DD.valueOf(pc.getX());
    DD cy = DD.valueOf(pc.getY());

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

  private DDSlow (final Vector2D a,
                  final Vector2D b,
                  final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new DDSlow(a,b,c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
