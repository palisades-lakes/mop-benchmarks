package mop.java.geometry.predicates;

import mop.java.numbers.Hilo;
import mop.java.numbers.XDouble;

/** Exact tests.  Robust.
 * <br>
 * Some unclarity about the meaning of 'exact' here.
 * <br>
 *   This version's priority is correctness, and simplicity.
 *   Later versions can optimize guided by benchmarks and
 *   profiling.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-06-30
 */

public final class Exact implements Predicate {

  public final boolean isExact () { return true; }

  //--------------------------------------------------------------------

  private static final XDouble det (final double[] a,
                                     final boolean subtractFlag,
                                     final XDouble bc,
                                     final XDouble cd,
                                     final XDouble bd,
                                   final int flip) {
    final double ax = a[0], ay = a[1];
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


  public final double incircle (final double[] a,
                                final double[] b,
                                final double[] c,
                                final double[] d) {
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
  // orient2d
  //--------------------------------------------------------------------
  // TODO: seems to return signed area, not 2xsigned area
  // TODO: returns 1.0 for a co-linear triangle,
  //  where one vtx is the mean of the other 2.

  public final double orient2d (final double[] pa,
                                final double[] pb,
                                final double[] pc) {
    final Hilo axby = Hilo.product(pa[0],pb[1]);
    final Hilo axcy = Hilo.product(pa[0],pc[1]);
    final XDouble aterms = XDouble.twoTwoDiff(axby,axcy);

    final Hilo bxcy = Hilo.product(pb[0],pc[1]);
    final Hilo bxay = Hilo.product(pb[0],pa[1]);
    final XDouble bterms = XDouble.twoTwoDiff(bxcy,bxay);

    final Hilo cxay = Hilo.product(pc[0],pa[1]);
    final Hilo cxby = Hilo.product(pc[0],pb[1]);
    final XDouble cterms = XDouble.twoTwoDiff(cxay,cxby);

    return aterms.add(bterms).add(cterms).doubleValue(); }

  //--------------------------------------------------------------------
  // orient3d
  //--------------------------------------------------------------------

  public final double orient3d (final double[] pa,
                                final double[] pb,
                                final double[] pc,
                                final double[] pd) {
    final Hilo axby = Hilo.product(pa[0],pb[1]);
    final Hilo bxay = Hilo.product(pb[0],pa[1]);
    final XDouble ab = XDouble.twoTwoDiff(axby,bxay);
    final Hilo bxcy = Hilo.product(pb[0],pc[1]);
    final Hilo cxby = Hilo.product(pc[0],pb[1]);
    final XDouble bc = XDouble.twoTwoDiff(bxcy,cxby);
    final Hilo cxdy = Hilo.product(pc[0],pd[1]);
    final Hilo dxcy = Hilo.product(pd[0],pc[1]);
    final XDouble cd = XDouble.twoTwoDiff(cxdy,dxcy);
    final Hilo dxay = Hilo.product(pd[0],pa[1]);
    final Hilo axdy = Hilo.product(pa[0],pd[1]);
    final XDouble da = XDouble.twoTwoDiff(dxay,axdy);
    final Hilo axcy = Hilo.product(pa[0],pc[1]);
    final Hilo cxay = Hilo.product(pc[0],pa[1]);
    final XDouble ac = XDouble.twoTwoDiff(axcy,cxay);
    final Hilo bxdy = Hilo.product(pb[0],pd[1]);
    final Hilo dxby = Hilo.product(pd[0],pb[1]);
    final XDouble bd = XDouble.twoTwoDiff(bxdy,dxby);

    final XDouble cda = cd.add(da).add(ac);
    final XDouble dab = da.add(ab).add(bd);
    final XDouble abc = ab.add(bc).subtract(ac);
    final XDouble bcd = bc.add(cd).subtract(bd);

    final XDouble adet = bcd.multiply(pa[2]);
    final XDouble bdet = cda.multiply(-pb[2]);
    final XDouble cdet = dab.multiply(pc[2]);
    final XDouble ddet = abc.multiply(-pd[2]);

    return adet.add(bdet).add(cdet).add(ddet).doubleValue(); }


  //--------------------------------------------------------------------
  // insphere
  //--------------------------------------------------------------------

  public final double insphere (final double[] pa,
                                final double[] pb,
                                final double[] pc,
                                final double[] pd,
                                final double[] pe) {

    // TODO: XDouble.cross2D?
    final XDouble ab = XDouble.twoTwoDiff(Hilo.product(pa[0],pb[1]),
                                          Hilo.product(pb[0],pa[1]));
    final XDouble bc = XDouble.twoTwoDiff(Hilo.product(pb[0],pc[1]),
                                          Hilo.product(pc[0],pb[1]));
    final XDouble cd = XDouble.twoTwoDiff(Hilo.product(pc[0],pd[1]),
                                          Hilo.product(pd[0],pc[1]));
    final XDouble de = XDouble.twoTwoDiff(Hilo.product(pd[0],pe[1]),
                                          Hilo.product(pe[0],pd[1]));
    final XDouble ea = XDouble.twoTwoDiff(Hilo.product(pe[0],pa[1]),
                                          Hilo.product(pa[0],pe[1]));
    final XDouble ac = XDouble.twoTwoDiff(Hilo.product(pa[0],pc[1]),
                                          Hilo.product(pc[0],pa[1]));
    final XDouble bd = XDouble.twoTwoDiff(Hilo.product(pb[0],pd[1]),
                                          Hilo.product(pd[0],pb[1]));
    final XDouble ce = XDouble.twoTwoDiff(Hilo.product(pc[0],pe[1]),
                                          Hilo.product(pe[0],pc[1]));
    final  XDouble da = XDouble.twoTwoDiff(Hilo.product(pd[0],pa[1]),
                                           Hilo.product(pa[0],pd[1]));
    final XDouble eb = XDouble.twoTwoDiff(Hilo.product(pe[0],pb[1]),
                                          Hilo.product(pb[0],pe[1]));
    final XDouble abc = ab.multiply(pc[2])
                          .add(bc.multiply(pa[2]))
                          .add(ac.multiply(-pb[2]));
    final XDouble bcd = bc.multiply(pd[2])
                          .add(cd.multiply(pb[2]))
                          .add(bd.multiply(-pc[2]));
    final XDouble cde = cd.multiply(pe[2])
                          .add(de.multiply(pc[2]))
                          .add(ce.multiply(-pd[2]));
    final XDouble dea = de.multiply(pa[2])
                          .add(ea.multiply(pd[2]))
                          .add(da.multiply(-pe[2]));
    final XDouble eab = ea.multiply(pb[2])
                          .add(ab.multiply(pe[2]))
                          .add(eb.multiply(-pa[2]));
    final XDouble abd = ab.multiply(pd[2])
                          .add(bd.multiply(pa[2]))
                          .add(da.multiply(pb[2]));
    final XDouble bce = bc.multiply(pe[2])
                          .add(ce.multiply(pb[2]))
                          .add(eb.multiply(pc[2]));
    final XDouble cda = cd.multiply(pa[2])
                          .add(da.multiply(pc[2]))
                          .add(ac.multiply(pd[2]));
    final XDouble deb = de.multiply(pb[2])
                          .add(eb.multiply(pd[2]))
                          .add(bd.multiply(pe[2]));
    final XDouble eac = ea.multiply(pc[2])
                          .add(ac.multiply(pe[2]))
                          .add(ce.multiply(pa[2]));
    final XDouble bcde = cde.add(bce).subtract(deb.add(bcd));
    final XDouble adet = bcde.multiply(pa[0]).multiply(pa[0])
                             .add(bcde.multiply(pa[1]).multiply(pa[1]))
                             .add(bcde.multiply(pa[2]).multiply(pa[2]));

    final XDouble cdea = dea.add(cda).subtract(eac.add(cde));
    final XDouble bdet = cdea.multiply(pb[0]).multiply(pb[0])
                             .add(cdea.multiply(pb[1]).multiply(pb[1]))
                             .add(cdea.multiply(pb[2]).multiply(pb[2]));

    final XDouble deab = eab.add(deb).subtract(abd.add(dea));
    final XDouble cdet = deab.multiply(pc[0]).multiply(pc[0])
                             .add(deab.multiply(pc[1]).multiply(pc[1]))
                             .add(deab.multiply(pc[2]).multiply(pc[2]));

    final XDouble eabc = abc.add(eac).subtract(bce.add(eab));
    final XDouble ddet = eabc.multiply(pd[0]).multiply(pd[0])
                             .add(eabc.multiply(pd[1]).multiply(pd[1]))
                             .add(eabc.multiply(pd[2]).multiply(pd[2]));

    final XDouble abcd = bcd.add(abd).subtract(cda.add(abc));
    final XDouble edet = abcd.multiply(pe[0]).multiply(pe[0])
                             .add(abcd.multiply(pe[1]).multiply(pe[1]))
                             .add(abcd.multiply(pe[2]).multiply(pe[2]));

    return adet.add(bdet).add(cdet).add(ddet).add(edet).doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public Exact () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
