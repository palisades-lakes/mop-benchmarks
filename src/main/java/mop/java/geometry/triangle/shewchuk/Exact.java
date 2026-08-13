package mop.java.geometry.triangle.shewchuk;

import mop.java.geometry.triangle.Triangle2D;
import mop.java.numbers.Hilo;
import mop.java.numbers.XDouble;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Exact tests.  Robust.
 * <br>
 * Some unclarity about the meaning of 'exact' here.
 * <br>
 *   This version's priority is correctness, and simplicity.
 *   Later versions can optimize guided by benchmarks and
 *   profiling.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-08-08
 */

public final class Exact extends Triangle2D {

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return true; }

  public final double twiceSignedArea () {

    final Vector2D a = getP0();
    final Vector2D b = getP1();
    final Vector2D c = getP2();

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
    // TODO: multiplyByL2Sq(flip,a)?
    return
      (bcd.multiplyBySq(flip,ax))
        .add(bcd.multiplyBySq(flip,ay)); }

  public final boolean inCircleExact () { return true; }

  public final double inCircle (final Vector2D p) {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();

    final XDouble ab = XDouble.crossProduct(pa, pb);
    final XDouble bc = XDouble.crossProduct(pb, pc);
    final XDouble cd = XDouble.crossProduct(pc, p);
    final XDouble da = XDouble.crossProduct(p, pa);
    final XDouble ac = XDouble.crossProduct(pa, pc);
    final XDouble bd = XDouble.crossProduct(pb, p);
    final XDouble adet = det(pa, true, bc, cd, bd, 1);
    final XDouble bdet = det(pb, false, cd, da, ac, -1);
    final XDouble cdet = det(pc, false, da, ab, bd, 1);
    final XDouble ddet = det(p,true,ab,bc,ac,-1);

    // TODO: resolve this!
    // this change fixes current test cases.
    // shouldn't matter, XDouble add should be associative
    //final XDouble det = adet.add(bdet).add(cdet.add(ddet));
    final XDouble det = adet.add(bdet).add(cdet).add(ddet);
    return det.doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private Exact (final Vector2D a,
                 final Vector2D b,
                 final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new Exact(a,b,c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
