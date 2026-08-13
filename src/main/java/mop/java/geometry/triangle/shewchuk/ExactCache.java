package mop.java.geometry.triangle.shewchuk;

import mop.java.geometry.triangle.Triangle2D;
import mop.java.numbers.Hilo;
import mop.java.numbers.XDouble;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Exact tests.  Robust.
 * Precompute triangle properties used by inCircle.
 * <br>
 * Some unclarity about the meaning of 'exact' here.
 * <br>
 *   This version's priority is correctness, and simplicity.
 *   Later versions can optimize guided by benchmarks and
 *   profiling.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-08-13
 */

public final class ExactCache extends Triangle2D {

  private final XDouble _axb;
  private final XDouble getAxB() { return _axb; }
  private final XDouble _bxc;
  private final XDouble getBxC() { return _bxc; }
  private final XDouble _axc;
  private final XDouble getAxC() { return _axc; }

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return true; }

  public final double twiceSignedArea () {

    final Vector2D a = getP0();
    final Vector2D b = getP1();
    final Vector2D c = getP2();

    // TODO: XDouble.crossProduct
    final Hilo axby = Hilo.product(a.getX(),b.getY());
    final Hilo axcy = Hilo.product(a.getX(),c.getY());
    final XDouble aterms = XDouble.subtract(axby, axcy);

    final Hilo bxcy = Hilo.product(b.getX(),c.getY());
    final Hilo bxay = Hilo.product(b.getX(),a.getY());
    final XDouble bterms = XDouble.subtract(bxcy, bxay);

    final Hilo cxay = Hilo.product(c.getX(),a.getY());
    final Hilo cxby = Hilo.product(c.getX(),b.getY());
    final XDouble cterms = XDouble.subtract(cxay, cxby);

    return aterms.add(bterms).add(cterms).doubleValue(); }

  //--------------------------------------------------------------------

  private static final XDouble det (final Vector2D a,
                                    final boolean subtractFlag,
                                    final XDouble bc,
                                    final XDouble cd,
                                    final XDouble bd,
                                    final int flip) {
    final double ax = a.getX();
    final double ay = a.getY();
    // TODO: XDouble.add(XDouble,XDouble) to skip one object creation?
    //  ...and XDouble.addSubtract(XDouble,XDouble)
    final XDouble bcd = subtractFlag
                        ? bc.add(cd).subtract(bd)
                        : bc.add(cd).add(bd);
    return
      (bcd.multiplyBySq(flip,ax))
        .add(bcd.multiplyBySq(flip,ay)); }

  public final boolean inCircleExact () { return true; }

  public final double inCircle (final Vector2D p) {
    final Vector2D a = getP0();
    final Vector2D b = getP1();
    final Vector2D c = getP2();

    final XDouble axb = getAxB();
    final XDouble bxc = getBxC();
    final XDouble axc = getAxC();

    final XDouble cxp = XDouble.crossProduct(c, p);
    final XDouble pxa = XDouble.crossProduct(p, a);
    final XDouble bxp = XDouble.crossProduct(b, p);

    final XDouble adet = det(a, true, bxc, cxp, bxp, 1);
    final XDouble bdet = det(b, false, cxp, pxa, axc, -1);
    final XDouble cdet = det(c, false, pxa, axb, bxp, 1);
    final XDouble ddet = det(p,true,axb,bxc,axc,-1);

    //final XDouble det = adet.add(bdet).add(cdet).add(ddet);
    final XDouble det = XDouble.sum(adet,bdet,cdet,ddet);
    return det.doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private ExactCache (final Vector2D a,
                      final Vector2D b,
                      final Vector2D c)  {
    super(a,b,c);
    _axb = XDouble.crossProduct(a, b);
    _bxc = XDouble.crossProduct(b, c);
    _axc = XDouble.crossProduct(a, c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new ExactCache(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
