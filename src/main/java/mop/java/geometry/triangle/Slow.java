package mop.java.geometry.triangle;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.numbers.Hilo;
import mop.java.numbers.XDouble;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** More exact tests.  Robust.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-06
 */

// strictfp unnecessary for JDK17 and later
public final class Slow extends Triangle2D {

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return true; }

  // TODO: seems to return 2xsigned area
  // TODO: XDoubleVector, XDoubleTriangle...

  public final double signedArea (final Vector2D pa,
                                  final Vector2D pb,
                                  final Vector2D pc) {
    final Hilo ax = Hilo.subtract(pa.getX(), pc.getX());
    final Hilo ay = Hilo.subtract(pa.getY(), pc.getY());
    final Hilo bx = Hilo.subtract(pb.getX(), pc.getX());
    final Hilo by = Hilo.subtract(pb.getY(), pc.getY());
    final XDouble axby = XDouble.product(ax, by);
    final XDouble bxay = XDouble.product(bx, ay);
    return axby.subtract(bxay).doubleValue(); }

  //--------------------------------------------------------------------

  private static final XDouble det (final Hilo ax,
                                    final Hilo ay,
                                    final Hilo bx,
                                    final Hilo by,
                                    final Hilo cx,
                                    final Hilo cy) {

    final XDouble axby = XDouble.product(ax, by);
    final XDouble bxay = XDouble.product(bx, ay);
    final XDouble sum = axby.subtract(bxay);

    final XDouble sxhihi = sum.multiply(cx.hi()).multiply(cx.hi());
    final XDouble sxlo = sum.multiply(cx.lo());
    final XDouble sxlohi2 = sxlo.multiply(cx.hi()).fast2x();
    final XDouble sxlolo = sxlo.multiply(cx.lo());
    final XDouble detx = sxhihi.add(sxlohi2).add(sxlolo);

    final XDouble syhihi = sum.multiply(cy.hi()).multiply(cy.hi());
    final XDouble sylo = sum.multiply(cy.lo());
    final XDouble sylohi2 = sylo.multiply(cy.hi()).fast2x();
    final XDouble sylolo = sylo.multiply(cy.lo());
    final XDouble dety = syhihi.add(sylohi2).add(sylolo);

    return detx.add(dety); }

  public final boolean inCircleExact () { return true; }

  /** signed distance of <code>pd</code> from the circumcircle thru
   * <code>pa,pb,pc</code>, negative means outside.
   */

  public final double inCircle (final Vector2D pa,
                                final Vector2D pb,
                                final Vector2D pc,
                                final Vector2D pd) {

    final Hilo ax = Hilo.subtract(pa.getX(), pd.getX());
    final Hilo ay = Hilo.subtract(pa.getY(), pd.getY());
    final Hilo bx = Hilo.subtract(pb.getX(), pd.getX());
    final Hilo by = Hilo.subtract(pb.getY(), pd.getY());
    final Hilo cx = Hilo.subtract(pc.getX(), pd.getX());
    final Hilo cy = Hilo.subtract(pc.getY(), pd.getY());
    final XDouble ad = det(bx,by,cx,cy,ax,ay);
    final XDouble bd = det(cx,cy,ax,ay,bx,by);
    final XDouble cd = det(ax,ay,bx,by,cx,cy);
    return cd.add(bd).add(ad).doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public Slow () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
