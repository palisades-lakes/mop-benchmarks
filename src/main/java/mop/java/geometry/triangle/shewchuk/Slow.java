package mop.java.geometry.triangle.shewchuk;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.geometry.triangle.Triangle2D;
import mop.java.numbers.Hilo;
import mop.java.numbers.XDouble;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** More exact tests.  Robust.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-27
 */

// strictfp unnecessary for JDK17 and later
public final class Slow extends Triangle2D {

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return true; }

  // TODO: seems to return 2xsigned area
  // TODO: XDoubleVector, XDoubleTriangle...

  public final double signedArea () {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();

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

  public final double inCircle (final Vector2D p) {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();

    final Hilo ax = Hilo.subtract(pa.getX(), p.getX());
    final Hilo ay = Hilo.subtract(pa.getY(), p.getY());
    final Hilo bx = Hilo.subtract(pb.getX(), p.getX());
    final Hilo by = Hilo.subtract(pb.getY(), p.getY());
    final Hilo cx = Hilo.subtract(pc.getX(), p.getX());
    final Hilo cy = Hilo.subtract(pc.getY(), p.getY());
    final XDouble ad = det(bx,by,cx,cy,ax,ay);
    final XDouble bd = det(cx,cy,ax,ay,bx,by);
    final XDouble cd = det(ax,ay,bx,by,cx,cy);
    return cd.add(bd).add(ad).doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private Slow (final Vector2D a,
                final Vector2D b,
                final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new Slow(a,b,c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
