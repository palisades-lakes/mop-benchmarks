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
//    Two_Product(pa[0], pb[1], axby1, axby0);
//    Two_Product(pb[0], pa[1], bxay1, bxay0);
//    Two_Two_Diff(axby1, axby0, bxay1, bxay0, ab[3], ab[2], ab[1], ab[0]);
    final Hilo axby = Hilo.product(pa[0],pb[1]);
    final Hilo bxay = Hilo.product(pb[0],pa[1]);
    final XDouble ab = XDouble.twoTwoDiff(axby,bxay);
//    Two_Product(pb[0], pc[1], bxcy1, bxcy0);
//    Two_Product(pc[0], pb[1], cxby1, cxby0);
//    Two_Two_Diff(bxcy1, bxcy0, cxby1, cxby0, bc[3], bc[2], bc[1], bc[0]);
    final Hilo bxcy = Hilo.product(pb[0],pc[1]);
    final Hilo cxby = Hilo.product(pc[0],pb[1]);
    final XDouble bc = XDouble.twoTwoDiff(bxcy,cxby);
//    Two_Product(pc[0], pd[1], cxdy1, cxdy0);
//    Two_Product(pd[0], pc[1], dxcy1, dxcy0);
//    Two_Two_Diff(cxdy1, cxdy0, dxcy1, dxcy0, cd[3], cd[2], cd[1], cd[0]);
    final Hilo cxdy = Hilo.product(pc[0],pd[1]);
    final Hilo dxcy = Hilo.product(pd[0],pc[1]);
    final XDouble cd = XDouble.twoTwoDiff(cxdy,dxcy);
//    Two_Product(pd[0], pe[1], dxey1, dxey0);
//    Two_Product(pe[0], pd[1], exdy1, exdy0);
//    Two_Two_Diff(dxey1, dxey0, exdy1, exdy0, de[3], de[2], de[1], de[0]);
    final Hilo dxey = Hilo.product(pd[0],pe[1]);
    final Hilo exdy = Hilo.product(pe[0],pd[1]);
    final XDouble de = XDouble.twoTwoDiff(dxey,exdy);
//    Two_Product(pe[0], pa[1], exay1, exay0);
//    Two_Product(pa[0], pe[1], axey1, axey0);
//    Two_Two_Diff(exay1, exay0, axey1, axey0, ea[3], ea[2], ea[1], ea[0]);
    final Hilo exay = Hilo.product(pe[0],pa[1]);
    final Hilo axey = Hilo.product(pa[0],pe[1]);
    final XDouble ea = XDouble.twoTwoDiff(exay,axey);
//    Two_Product(pa[0], pc[1], axcy1, axcy0);
//    Two_Product(pc[0], pa[1], cxay1, cxay0);
//    Two_Two_Diff(axcy1, axcy0, cxay1, cxay0, ac[3], ac[2], ac[1], ac[0]);
    final Hilo axcy = Hilo.product(pa[0],pc[1]);
    final Hilo cxay = Hilo.product(pc[0],pa[1]);
    final XDouble ac = XDouble.twoTwoDiff(axcy,cxay);
//    Two_Product(pb[0], pd[1], bxdy1, bxdy0);
//    Two_Product(pd[0], pb[1], dxby1, dxby0);
//    Two_Two_Diff(bxdy1, bxdy0, dxby1, dxby0, bd[3], bd[2], bd[1], bd[0]);
    final Hilo bxdy = Hilo.product(pb[0],pd[1]);
    final Hilo dxby = Hilo.product(pd[0],pb[1]);
    final XDouble bd = XDouble.twoTwoDiff(bxdy,dxby);
//    Two_Product(pc[0], pe[1], cxey1, cxey0);
//    Two_Product(pe[0], pc[1], excy1, excy0);
//    Two_Two_Diff(cxey1, cxey0, excy1, excy0, ce[3], ce[2], ce[1], ce[0]);
    final Hilo cxey = Hilo.product(pc[0],pe[1]);
    final Hilo excy = Hilo.product(pe[0],pc[1]);
    final XDouble ce = XDouble.twoTwoDiff(cxey,excy);
//    Two_Product(pd[0], pa[1], dxay1, dxay0);
//    Two_Product(pa[0], pd[1], axdy1, axdy0);
//    Two_Two_Diff(dxay1, dxay0, axdy1, axdy0, da[3], da[2], da[1], da[0]);
    final Hilo dxay = Hilo.product(pd[0],pa[1]);
    final Hilo axdy = Hilo.product(pa[0],pd[1]);
    final  XDouble da = XDouble.twoTwoDiff(dxay,axdy);
//    Two_Product(pe[0], pb[1], exby1, exby0);
//    Two_Product(pb[0], pe[1], bxey1, bxey0);
//    Two_Two_Diff(exby1, exby0, bxey1, bxey0, eb[3], eb[2], eb[1], eb[0]);
    final Hilo exby = Hilo.product(pe[0],pb[1]);
    final Hilo bxey = Hilo.product(pb[0],pe[1]);
    final XDouble eb = XDouble.twoTwoDiff(exby,bxey);

//    temp8alen = scale_expansion_zeroelim(4, bc, pa[2], temp8a);
//    temp8blen = scale_expansion_zeroelim(4, ac, -pb[2], temp8b);
//    temp16len = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
//                                            temp16);
//    temp8alen = scale_expansion_zeroelim(4, ab, pc[2], temp8a);
//    abclen = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
//                                         abc);
    XDouble temp8a = bc.multiply(pa[2]);
    XDouble temp8b = ac.multiply(-pb[2]);
    XDouble temp16 = temp8a.add(temp8b);
    temp8a = ab.multiply(pc[2]);
    final XDouble abc = temp8a.add(temp16);

//    temp8alen = scale_expansion_zeroelim(4, cd, pb[2], temp8a);
//    temp8blen = scale_expansion_zeroelim(4, bd, -pc[2], temp8b);
//    temp16len = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
//                                            temp16);
//    temp8alen = scale_expansion_zeroelim(4, bc, pd[2], temp8a);
//    bcdlen = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
//                                         bcd);
    temp8a = cd.multiply(pb[2]);
    temp8b = bd.multiply(-pc[2]);
    temp16 = temp8a.add(temp8b);
    temp8a = bc.multiply(pd[2]);
    final XDouble bcd = temp8a.add(temp16);

//    temp8alen = scale_expansion_zeroelim(4, de, pc[2], temp8a);
//    temp8blen = scale_expansion_zeroelim(4, ce, -pd[2], temp8b);
//    temp16len = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
//                                            temp16);
//    temp8alen = scale_expansion_zeroelim(4, cd, pe[2], temp8a);
//    cdelen = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
//                                         cde);
    temp8a = de.multiply(pc[2]);
    temp8b = ce.multiply(-pd[2]);
    temp16 = temp8a.add(temp8b);
    temp8a = cd.multiply(pe[2]);
    final XDouble cde = temp8a.add(temp16);

//    temp8alen = scale_expansion_zeroelim(4, ea, pd[2], temp8a);
//    temp8blen = scale_expansion_zeroelim(4, da, -pe[2], temp8b);
//    temp16len = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
//                                            temp16);
//    temp8alen = scale_expansion_zeroelim(4, de, pa[2], temp8a);
//    dealen = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
//                                         dea);
    temp8a = ea.multiply(pd[2]);
    temp8b = da.multiply(-pe[2]);
    temp16 = temp8a.add(temp8b);
    temp8a = de.multiply(pa[2]);
    final XDouble dea = temp8a.add(temp16);

//    temp8alen = scale_expansion_zeroelim(4, ab, pe[2], temp8a);
//    temp8blen = scale_expansion_zeroelim(4, eb, -pa[2], temp8b);
//    temp16len = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
//                                            temp16);
//    temp8alen = scale_expansion_zeroelim(4, ea, pb[2], temp8a);
//    eablen = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
//                                         eab);
    temp8a = ab.multiply(pe[2]);
    temp8b = eb.multiply(-pa[2]);
    temp16 = temp8a.add(temp8b);
    temp8a = ea.multiply(pb[2]);
    final XDouble eab = temp8a.add(temp16);

//    temp8alen = scale_expansion_zeroelim(4, bd, pa[2], temp8a);
//    temp8blen = scale_expansion_zeroelim(4, da, pb[2], temp8b);
//    temp16len = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
//                                            temp16);
//    temp8alen = scale_expansion_zeroelim(4, ab, pd[2], temp8a);
//    abdlen = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
//                                         abd);
    temp8a = bd.multiply(pa[2]);
    temp8b = da.multiply(pb[2]);
    temp16 = temp8a.add(temp8b);
    temp8a = ab.multiply(pd[2]);
    final XDouble abd = temp8a.add(temp16);

//    temp8alen = scale_expansion_zeroelim(4, ce, pb[2], temp8a);
//    temp8blen = scale_expansion_zeroelim(4, eb, pc[2], temp8b);
//    temp16len = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
//                                            temp16);
//    temp8alen = scale_expansion_zeroelim(4, bc, pe[2], temp8a);
//    bcelen = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
//                                         bce);
    temp8a = ce.multiply(pb[2]);
    temp8b = eb.multiply(pc[2]);
    temp16 = temp8a.add(temp8b);
    temp8a = bc.multiply(pe[2]);
    final XDouble bce = temp8a.add(temp16);

//    temp8alen = scale_expansion_zeroelim(4, da, pc[2], temp8a);
//    temp8blen = scale_expansion_zeroelim(4, ac, pd[2], temp8b);
//    temp16len = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
//                                            temp16);
//    temp8alen = scale_expansion_zeroelim(4, cd, pa[2], temp8a);
//    cdalen = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
//                                         cda);
    temp8a = da.multiply(pc[2]);
    temp8b = ac.multiply(pd[2]);
    temp16 = temp8a.add(temp8b);
    temp8a = cd.multiply(pa[2]);
    final XDouble cda = temp8a.add(temp16);

//    temp8alen = scale_expansion_zeroelim(4, eb, pd[2], temp8a);
//    temp8blen = scale_expansion_zeroelim(4, bd, pe[2], temp8b);
//    temp16len = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
//                                            temp16);
//    temp8alen = scale_expansion_zeroelim(4, de, pb[2], temp8a);
//    deblen = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
//                                         deb);
    temp8a = eb.multiply(pd[2]);
    temp8b = bd.multiply(pe[2]);
    temp16 = temp8a.add(temp8b);
    temp8a = de.multiply(pb[2]);
    final XDouble deb = temp8a.add(temp16);

//    temp8alen = scale_expansion_zeroelim(4, ac, pe[2], temp8a);
//    temp8blen = scale_expansion_zeroelim(4, ce, pa[2], temp8b);
//    temp16len = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
//                                            temp16);
//    temp8alen = scale_expansion_zeroelim(4, ea, pc[2], temp8a);
//    eaclen = fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
//                                         eac);
    temp8a = ac.multiply(pe[2]);
    temp8b = ce.multiply(pa[2]);
    temp16 = temp8a.add(temp8b);
    temp8a = ea.multiply(pc[2]);
    final XDouble eac = temp8a.add(temp16);

//    temp48alen = fast_expansion_sum_zeroelim(cdelen, cde, bcelen, bce, temp48a);
//    temp48blen = fast_expansion_sum_zeroelim(deblen, deb, bcdlen, bcd, temp48b);
//    for (i = 0; i < temp48blen; i++) { temp48b[i] = -temp48b[i]; }
//    bcdelen = fast_expansion_sum_zeroelim(temp48alen, temp48a,
//                                          temp48blen, temp48b, bcde);
    XDouble temp48a = cde.add(bce);
    XDouble temp48b = deb.add(bcd);
    final XDouble bcde = temp48a.subtract(temp48b);
//    xlen = scale_expansion_zeroelim(bcdelen, bcde, pa[0], temp192);
    XDouble temp192 = bcde.multiply(pa[0]);
//    xlen = scale_expansion_zeroelim(xlen, temp192, pa[0], det384x);
    XDouble det384x = temp192.multiply(pa[0]);
//    ylen = scale_expansion_zeroelim(bcdelen, bcde, pa[1], temp192);
    temp192 = bcde.multiply(pa[1]);
//    ylen = scale_expansion_zeroelim(ylen, temp192, pa[1], det384y);
    XDouble det384y = temp192.multiply(pa[1]);
//    zlen = scale_expansion_zeroelim(bcdelen, bcde, pa[2], temp192);
    temp192 = bcde.multiply(pa[2]);
//    zlen = scale_expansion_zeroelim(zlen, temp192, pa[2], det384z);
    XDouble det384z = temp192.multiply(pa[2]);
//    xylen = fast_expansion_sum_zeroelim(xlen, det384x, ylen, det384y, detxy);
    XDouble detxy = det384x.add(det384y);
//    alen = fast_expansion_sum_zeroelim(xylen, detxy, zlen, det384z, adet);
    final XDouble adet = detxy.add(det384z);

//    temp48alen = fast_expansion_sum_zeroelim(dealen, dea, cdalen, cda, temp48a);
//    temp48blen = fast_expansion_sum_zeroelim(eaclen, eac, cdelen, cde, temp48b);
//    for (i = 0; i < temp48blen; i++) {
//      temp48b[i] = -temp48b[i];
//    }
//    cdealen = fast_expansion_sum_zeroelim(temp48alen, temp48a,
//                                          temp48blen, temp48b, cdea);
//    xlen = scale_expansion_zeroelim(cdealen, cdea, pb[0], temp192);
//    xlen = scale_expansion_zeroelim(xlen, temp192, pb[0], det384x);
//    ylen = scale_expansion_zeroelim(cdealen, cdea, pb[1], temp192);
//    ylen = scale_expansion_zeroelim(ylen, temp192, pb[1], det384y);
//    zlen = scale_expansion_zeroelim(cdealen, cdea, pb[2], temp192);
//    zlen = scale_expansion_zeroelim(zlen, temp192, pb[2], det384z);
//    xylen = fast_expansion_sum_zeroelim(xlen, det384x, ylen, det384y, detxy);
//    blen = fast_expansion_sum_zeroelim(xylen, detxy, zlen, det384z, bdet);
    temp48a = dea.add(cda);
    temp48b = eac.add(cde);
    final XDouble cdea = temp48a.subtract(temp48b);
    temp192 = cdea.multiply(pb[0]);
    det384x = temp192.multiply(pb[0]);
    temp192 = cdea.multiply(pb[1]);
    det384y = temp192.multiply(pb[1]);
    temp192 = cdea.multiply(pb[2]);
    det384z = temp192.multiply(pb[2]);
    detxy = det384x.add(det384y);
    final XDouble bdet = detxy.add(det384z);

//    temp48alen = fast_expansion_sum_zeroelim(eablen, eab, deblen, deb, temp48a);
//    temp48blen = fast_expansion_sum_zeroelim(abdlen, abd, dealen, dea, temp48b);
//    for (i = 0; i < temp48blen; i++) {
//      temp48b[i] = -temp48b[i];
//    }
//    deablen = fast_expansion_sum_zeroelim(temp48alen, temp48a,
//                                          temp48blen, temp48b, deab);
//    xlen = scale_expansion_zeroelim(deablen, deab, pc[0], temp192);
//    xlen = scale_expansion_zeroelim(xlen, temp192, pc[0], det384x);
//    ylen = scale_expansion_zeroelim(deablen, deab, pc[1], temp192);
//    ylen = scale_expansion_zeroelim(ylen, temp192, pc[1], det384y);
//    zlen = scale_expansion_zeroelim(deablen, deab, pc[2], temp192);
//    zlen = scale_expansion_zeroelim(zlen, temp192, pc[2], det384z);
//    xylen = fast_expansion_sum_zeroelim(xlen, det384x, ylen, det384y, detxy);
//    clen = fast_expansion_sum_zeroelim(xylen, detxy, zlen, det384z, cdet);
    temp48a = eab.add(deb);
    temp48b = abd.add(dea);
    final XDouble deab = temp48a.subtract(temp48b);
    temp192 = deab.multiply(pc[0]);
    det384x = temp192.multiply(pc[0]);
    temp192 = deab.multiply(pc[1]);
    det384y = temp192.multiply(pc[1]);
    temp192 = deab.multiply(pc[2]);
    det384z = temp192.multiply(pc[2]);
    detxy = det384x.add(det384y);
    final XDouble cdet = detxy.add(det384z);

//    temp48alen = fast_expansion_sum_zeroelim(abclen, abc, eaclen, eac, temp48a);
//    temp48blen = fast_expansion_sum_zeroelim(bcelen, bce, eablen, eab, temp48b);
//    for (i = 0; i < temp48blen; i++) {
//      temp48b[i] = -temp48b[i];
//    }
//    eabclen = fast_expansion_sum_zeroelim(temp48alen, temp48a,
//                                          temp48blen, temp48b, eabc);
//    xlen = scale_expansion_zeroelim(eabclen, eabc, pd[0], temp192);
//    xlen = scale_expansion_zeroelim(xlen, temp192, pd[0], det384x);
//    ylen = scale_expansion_zeroelim(eabclen, eabc, pd[1], temp192);
//    ylen = scale_expansion_zeroelim(ylen, temp192, pd[1], det384y);
//    zlen = scale_expansion_zeroelim(eabclen, eabc, pd[2], temp192);
//    zlen = scale_expansion_zeroelim(zlen, temp192, pd[2], det384z);
//    xylen = fast_expansion_sum_zeroelim(xlen, det384x, ylen, det384y, detxy);
//    dlen = fast_expansion_sum_zeroelim(xylen, detxy, zlen, det384z, ddet);
    temp48a = abc.add(eac);
    temp48b = bce.add(eab);
    final XDouble eabc = temp48a.subtract(temp48b);
    temp192 = eabc.multiply(pd[0]);
    det384x = temp192.multiply(pd[0]);
    temp192 = eabc.multiply(pd[1]);
    det384y = temp192.multiply(pd[1]);
    temp192 = eabc.multiply(pd[2]);
    det384z = temp192.multiply(pd[2]);
    detxy = det384x.add(det384y);
    final XDouble ddet = detxy.add(det384z);

//    temp48alen = fast_expansion_sum_zeroelim(bcdlen, bcd, abdlen, abd, temp48a);
//    temp48blen = fast_expansion_sum_zeroelim(cdalen, cda, abclen, abc, temp48b);
//    for (i = 0; i < temp48blen; i++) {
//      temp48b[i] = -temp48b[i];
//    }
//    abcdlen = fast_expansion_sum_zeroelim(temp48alen, temp48a,
//                                          temp48blen, temp48b, abcd);
//    xlen = scale_expansion_zeroelim(abcdlen, abcd, pe[0], temp192);
//    xlen = scale_expansion_zeroelim(xlen, temp192, pe[0], det384x);
//    ylen = scale_expansion_zeroelim(abcdlen, abcd, pe[1], temp192);
//    ylen = scale_expansion_zeroelim(ylen, temp192, pe[1], det384y);
//    zlen = scale_expansion_zeroelim(abcdlen, abcd, pe[2], temp192);
//    zlen = scale_expansion_zeroelim(zlen, temp192, pe[2], det384z);
//    xylen = fast_expansion_sum_zeroelim(xlen, det384x, ylen, det384y, detxy);
//    elen = fast_expansion_sum_zeroelim(xylen, detxy, zlen, det384z, edet);
    temp48a = bcd.add(abd);
    temp48b = cda.add(abc);
    final XDouble abcd = temp48a.subtract(temp48b);
    temp192 = abcd.multiply(pe[0]);
    det384x = temp192.multiply(pe[0]);
    temp192 = abcd.multiply(pe[1]);
    det384y = temp192.multiply(pe[1]);
    temp192 = abcd.multiply(pe[2]);
    det384z = temp192.multiply(pe[2]);
    detxy = det384x.add(det384y);
    final XDouble edet = detxy.add(det384z);

//    ablen = fast_expansion_sum_zeroelim(alen, adet, blen, bdet, abdet);
//    cdlen = fast_expansion_sum_zeroelim(clen, cdet, dlen, ddet, cddet);
//    cdelen = fast_expansion_sum_zeroelim(cdlen, cddet, elen, edet, cdedet);
//    deterlen = fast_expansion_sum_zeroelim(ablen, abdet, cdelen, cdedet, deter);

    final XDouble abdet = adet.add(bdet);
    final XDouble cddet = cdet.add(ddet);
    final XDouble cdedet = cddet.add(edet);
    final XDouble deter = abdet.add(cdedet);
    return deter.doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public Exact () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
