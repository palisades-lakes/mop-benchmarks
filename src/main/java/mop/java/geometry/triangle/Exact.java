package mop.java.geometry.triangle;

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
 * @version 2026-07-06
 */

public final class Exact extends Triangle2D {

  public final boolean signedAreaExact () { return true; }

  public final double signedArea (final Vector2D pa,
                                  final Vector2D pb,
                                  final Vector2D pc) {

    final Hilo axby = Hilo.product(pa.getX(),pb.getY());
    final Hilo axcy = Hilo.product(pa.getX(),pc.getY());
    final XDouble aterms = XDouble.subtract(axby, axcy);

    final Hilo bxcy = Hilo.product(pb.getX(),pc.getY());
    final Hilo bxay = Hilo.product(pb.getX(),pa.getY());
    final XDouble bterms = XDouble.subtract(bxcy, bxay);

    final Hilo cxay = Hilo.product(pc.getX(),pa.getY());
    final Hilo cxby = Hilo.product(pc.getX(),pb.getY());
    final XDouble cterms = XDouble.subtract(cxay, cxby);

    return aterms.add(bterms).add(cterms).doubleValue(); }

  //--------------------------------------------------------------------

  private static final XDouble det (final Vector2D a,
                                    final boolean subtractFlag,
                                    final XDouble bc,
                                    final XDouble cd,
                                    final XDouble bd,
                                    final int flip) {
    final double ax = a.getX(), ay = a.getY();
    // TODO: XDouble.add(XDouble,XDouble) to skip one object creation?
    //  ...and XDouble.addSubtract(XDouble,XDouble)
    // TODO: XDouble.multiplyBySquare(double)?
    final XDouble bcd = subtractFlag
                        ? bc.add(cd).subtract(bd)
                        : bc.add(cd).add(bd);
    return
      (bcd.multiply(ax).multiply(flip*ax))
        .add(
          bcd.multiply(ay).multiply(flip*ay)); }

  public final boolean inCircleExact () { return true; }

  public final double inCircle (final Vector2D a,
                                final Vector2D b,
                                final Vector2D c,
                                final Vector2D d) {
    final XDouble ab = XDouble.crossProduct(a,b);
    final XDouble bc = XDouble.crossProduct(b,c);
    final XDouble cd = XDouble.crossProduct(c,d);
    final XDouble da = XDouble.crossProduct(d,a);
    final XDouble ac = XDouble.crossProduct(a,c);
    final XDouble bd = XDouble.crossProduct(b,d);
    final XDouble adet = det(a,true,bc,cd,bd, 1);
    final XDouble bdet = det(b,false,cd,da,ac,-1);
    final XDouble cdet = det(c,false,da,ab,bd, 1);
    final XDouble ddet = det(d,true,ab,bc,ac,-1);

    // TODO: resolve this!
    // this change fixes current test cases.
    // shouldn't matter, XDouble add should be associative
    //final XDouble det = adet.add(bdet).add(cdet.add(ddet));
    final XDouble det = adet.add(bdet).add(cdet).add(ddet);
    return det.doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public Exact () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
