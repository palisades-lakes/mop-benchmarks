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
 * @version 2026-06-29
 */

public final class Exact implements Predicate {

  private static final double SPLITTER = 0x1.0000002p27;

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
    double axby1, bxcy1, cxdy1, dxey1, exay1;
    double bxay1, cxby1, dxcy1, exdy1, axey1;
    double axcy1, bxdy1, cxey1, dxay1, exby1;
    double cxay1, dxby1, excy1, axdy1, bxey1;
    double axby0, bxcy0, cxdy0, dxey0, exay0;
    double bxay0, cxby0, dxcy0, exdy0, axey0;
    double axcy0, bxdy0, cxey0, dxay0, exby0;
    double cxay0, dxby0, excy0, axdy0, bxey0;
    double[] ab = new double[4], bc = new double[4], cd = new double[4],
      de = new double[4], ea = new double[4];
    double[] ac = new double[4], bd = new double[4], ce = new double[4],
      da = new double[4], eb = new double[4];
    double[] temp8a = new double[8], temp8b = new double[8], temp16 =
      new double[16]; int temp8alen, temp8blen, temp16len;
    double[] abc = new double[24], bcd = new double[24], cde =
      new double[24], dea = new double[24], eab = new double[24];
    double[] abd = new double[24], bce = new double[24], cda =
      new double[24], deb = new double[24], eac = new double[24];
    int abclen, bcdlen, cdelen, dealen, eablen;
    int abdlen, bcelen, cdalen, deblen, eaclen;
    double[] temp48a = new double[48], temp48b = new double[48];
    int temp48alen, temp48blen;
    double[] abcd = new double[96], bcde = new double[96], cdea =
      new double[96], deab = new double[96], eabc = new double[96];
    int abcdlen, bcdelen, cdealen, deablen, eabclen;
    double[] temp192 = new double[192];
    double[] det384x = new double[384], det384y = new double[384],
      det384z = new double[384]; int xlen, ylen, zlen;
    double[] detxy = new double[768]; int xylen;
    double[] adet = new double[1152], bdet = new double[1152], cdet =
      new double[1152], ddet = new double[1152], edet =
      new double[1152]; int alen, blen, clen, dlen, elen;
    double[] abdet = new double[2304], cddet = new double[2304],
      cdedet = new double[3456]; int ablen, cdlen;
    double[] deter = new double[5760]; int deterlen; int i;

    double bvirt; double avirt, bround, around; double c; double abig;
    double ahi, alo, bhi, blo; double err1, err2, err3; double _i, _j;
    double _0;

    axby1 = (pa[0] * pb[1]); c = (SPLITTER * pa[0]); abig = (c - pa[0]);
    ahi = c - abig; alo = pa[0] - ahi; c = (SPLITTER * pb[1]);
    abig = (c - pb[1]); bhi = c - abig; blo = pb[1] - bhi;
    err1 = axby1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); axby0 = (alo * blo) - err3;
    bxay1 = (pb[0] * pa[1]); c = (SPLITTER * pb[0]); abig = (c - pb[0]);
    ahi = c - abig; alo = pb[0] - ahi; c = (SPLITTER * pa[1]);
    abig = (c - pa[1]); bhi = c - abig; blo = pa[1] - bhi;
    err1 = bxay1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); bxay0 = (alo * blo) - err3;
    _i = (axby0 - bxay0); bvirt = (axby0 - _i); avirt = _i + bvirt;
    bround = bvirt - bxay0; around = axby0 - avirt;
    ab[0] = around + bround; _j = (axby1 + _i); bvirt = (_j - axby1);
    avirt = _j - bvirt; bround = _i - bvirt; around = axby1 - avirt;
    _0 = around + bround; _i = (_0 - bxay1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxay1; around = _0 - avirt;
    ab[1] = around + bround; ab[3] = (_j + _i); bvirt = (ab[3] - _j);
    avirt = ab[3] - bvirt; bround = _i - bvirt; around = _j - avirt;
    ab[2] = around + bround;

    bxcy1 = (pb[0] * pc[1]); c = (SPLITTER * pb[0]); abig = (c - pb[0]);
    ahi = c - abig; alo = pb[0] - ahi; c = (SPLITTER * pc[1]);
    abig = (c - pc[1]); bhi = c - abig; blo = pc[1] - bhi;
    err1 = bxcy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); bxcy0 = (alo * blo) - err3;
    cxby1 = (pc[0] * pb[1]); c = (SPLITTER * pc[0]); abig = (c - pc[0]);
    ahi = c - abig; alo = pc[0] - ahi; c = (SPLITTER * pb[1]);
    abig = (c - pb[1]); bhi = c - abig; blo = pb[1] - bhi;
    err1 = cxby1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); cxby0 = (alo * blo) - err3;
    _i = (bxcy0 - cxby0); bvirt = (bxcy0 - _i); avirt = _i + bvirt;
    bround = bvirt - cxby0; around = bxcy0 - avirt;
    bc[0] = around + bround; _j = (bxcy1 + _i); bvirt = (_j - bxcy1);
    avirt = _j - bvirt; bround = _i - bvirt; around = bxcy1 - avirt;
    _0 = around + bround; _i = (_0 - cxby1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxby1; around = _0 - avirt;
    bc[1] = around + bround; bc[3] = (_j + _i); bvirt = (bc[3] - _j);
    avirt = bc[3] - bvirt; bround = _i - bvirt; around = _j - avirt;
    bc[2] = around + bround;

    cxdy1 = (pc[0] * pd[1]); c = (SPLITTER * pc[0]); abig = (c - pc[0]);
    ahi = c - abig; alo = pc[0] - ahi; c = (SPLITTER * pd[1]);
    abig = (c - pd[1]); bhi = c - abig; blo = pd[1] - bhi;
    err1 = cxdy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); cxdy0 = (alo * blo) - err3;
    dxcy1 = (pd[0] * pc[1]); c = (SPLITTER * pd[0]); abig = (c - pd[0]);
    ahi = c - abig; alo = pd[0] - ahi; c = (SPLITTER * pc[1]);
    abig = (c - pc[1]); bhi = c - abig; blo = pc[1] - bhi;
    err1 = dxcy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); dxcy0 = (alo * blo) - err3;
    _i = (cxdy0 - dxcy0); bvirt = (cxdy0 - _i); avirt = _i + bvirt;
    bround = bvirt - dxcy0; around = cxdy0 - avirt;
    cd[0] = around + bround; _j = (cxdy1 + _i); bvirt = (_j - cxdy1);
    avirt = _j - bvirt; bround = _i - bvirt; around = cxdy1 - avirt;
    _0 = around + bround; _i = (_0 - dxcy1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxcy1; around = _0 - avirt;
    cd[1] = around + bround; cd[3] = (_j + _i); bvirt = (cd[3] - _j);
    avirt = cd[3] - bvirt; bround = _i - bvirt; around = _j - avirt;
    cd[2] = around + bround;

    dxey1 = (pd[0] * pe[1]); c = (SPLITTER * pd[0]); abig = (c - pd[0]);
    ahi = c - abig; alo = pd[0] - ahi; c = (SPLITTER * pe[1]);
    abig = (c - pe[1]); bhi = c - abig; blo = pe[1] - bhi;
    err1 = dxey1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); dxey0 = (alo * blo) - err3;
    exdy1 = (pe[0] * pd[1]); c = (SPLITTER * pe[0]); abig = (c - pe[0]);
    ahi = c - abig; alo = pe[0] - ahi; c = (SPLITTER * pd[1]);
    abig = (c - pd[1]); bhi = c - abig; blo = pd[1] - bhi;
    err1 = exdy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); exdy0 = (alo * blo) - err3;
    _i = (dxey0 - exdy0); bvirt = (dxey0 - _i); avirt = _i + bvirt;
    bround = bvirt - exdy0; around = dxey0 - avirt;
    de[0] = around + bround; _j = (dxey1 + _i); bvirt = (_j - dxey1);
    avirt = _j - bvirt; bround = _i - bvirt; around = dxey1 - avirt;
    _0 = around + bround; _i = (_0 - exdy1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - exdy1; around = _0 - avirt;
    de[1] = around + bround; de[3] = (_j + _i); bvirt = (de[3] - _j);
    avirt = de[3] - bvirt; bround = _i - bvirt; around = _j - avirt;
    de[2] = around + bround;

    exay1 = (pe[0] * pa[1]); c = (SPLITTER * pe[0]); abig = (c - pe[0]);
    ahi = c - abig; alo = pe[0] - ahi; c = (SPLITTER * pa[1]);
    abig = (c - pa[1]); bhi = c - abig; blo = pa[1] - bhi;
    err1 = exay1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); exay0 = (alo * blo) - err3;
    axey1 = (pa[0] * pe[1]); c = (SPLITTER * pa[0]); abig = (c - pa[0]);
    ahi = c - abig; alo = pa[0] - ahi; c = (SPLITTER * pe[1]);
    abig = (c - pe[1]); bhi = c - abig; blo = pe[1] - bhi;
    err1 = axey1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); axey0 = (alo * blo) - err3;
    _i = (exay0 - axey0); bvirt = (exay0 - _i); avirt = _i + bvirt;
    bround = bvirt - axey0; around = exay0 - avirt;
    ea[0] = around + bround; _j = (exay1 + _i); bvirt = (_j - exay1);
    avirt = _j - bvirt; bround = _i - bvirt; around = exay1 - avirt;
    _0 = around + bround; _i = (_0 - axey1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - axey1; around = _0 - avirt;
    ea[1] = around + bround; ea[3] = (_j + _i); bvirt = (ea[3] - _j);
    avirt = ea[3] - bvirt; bround = _i - bvirt; around = _j - avirt;
    ea[2] = around + bround;

    axcy1 = (pa[0] * pc[1]); c = (SPLITTER * pa[0]); abig = (c - pa[0]);
    ahi = c - abig; alo = pa[0] - ahi; c = (SPLITTER * pc[1]);
    abig = (c - pc[1]); bhi = c - abig; blo = pc[1] - bhi;
    err1 = axcy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); axcy0 = (alo * blo) - err3;
    cxay1 = (pc[0] * pa[1]); c = (SPLITTER * pc[0]); abig = (c - pc[0]);
    ahi = c - abig; alo = pc[0] - ahi; c = (SPLITTER * pa[1]);
    abig = (c - pa[1]); bhi = c - abig; blo = pa[1] - bhi;
    err1 = cxay1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); cxay0 = (alo * blo) - err3;
    _i = (axcy0 - cxay0); bvirt = (axcy0 - _i); avirt = _i + bvirt;
    bround = bvirt - cxay0; around = axcy0 - avirt;
    ac[0] = around + bround; _j = (axcy1 + _i); bvirt = (_j - axcy1);
    avirt = _j - bvirt; bround = _i - bvirt; around = axcy1 - avirt;
    _0 = around + bround; _i = (_0 - cxay1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxay1; around = _0 - avirt;
    ac[1] = around + bround; ac[3] = (_j + _i); bvirt = (ac[3] - _j);
    avirt = ac[3] - bvirt; bround = _i - bvirt; around = _j - avirt;
    ac[2] = around + bround;

    bxdy1 = (pb[0] * pd[1]); c = (SPLITTER * pb[0]); abig = (c - pb[0]);
    ahi = c - abig; alo = pb[0] - ahi; c = (SPLITTER * pd[1]);
    abig = (c - pd[1]); bhi = c - abig; blo = pd[1] - bhi;
    err1 = bxdy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); bxdy0 = (alo * blo) - err3;
    dxby1 = (pd[0] * pb[1]); c = (SPLITTER * pd[0]); abig = (c - pd[0]);
    ahi = c - abig; alo = pd[0] - ahi; c = (SPLITTER * pb[1]);
    abig = (c - pb[1]); bhi = c - abig; blo = pb[1] - bhi;
    err1 = dxby1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); dxby0 = (alo * blo) - err3;
    _i = (bxdy0 - dxby0); bvirt = (bxdy0 - _i); avirt = _i + bvirt;
    bround = bvirt - dxby0; around = bxdy0 - avirt;
    bd[0] = around + bround; _j = (bxdy1 + _i); bvirt = (_j - bxdy1);
    avirt = _j - bvirt; bround = _i - bvirt; around = bxdy1 - avirt;
    _0 = around + bround; _i = (_0 - dxby1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxby1; around = _0 - avirt;
    bd[1] = around + bround; bd[3] = (_j + _i); bvirt = (bd[3] - _j);
    avirt = bd[3] - bvirt; bround = _i - bvirt; around = _j - avirt;
    bd[2] = around + bround;

    cxey1 = (pc[0] * pe[1]); c = (SPLITTER * pc[0]); abig = (c - pc[0]);
    ahi = c - abig; alo = pc[0] - ahi; c = (SPLITTER * pe[1]);
    abig = (c - pe[1]); bhi = c - abig; blo = pe[1] - bhi;
    err1 = cxey1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); cxey0 = (alo * blo) - err3;
    excy1 = (pe[0] * pc[1]); c = (SPLITTER * pe[0]); abig = (c - pe[0]);
    ahi = c - abig; alo = pe[0] - ahi; c = (SPLITTER * pc[1]);
    abig = (c - pc[1]); bhi = c - abig; blo = pc[1] - bhi;
    err1 = excy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); excy0 = (alo * blo) - err3;
    _i = (cxey0 - excy0); bvirt = (cxey0 - _i); avirt = _i + bvirt;
    bround = bvirt - excy0; around = cxey0 - avirt;
    ce[0] = around + bround; _j = (cxey1 + _i); bvirt = (_j - cxey1);
    avirt = _j - bvirt; bround = _i - bvirt; around = cxey1 - avirt;
    _0 = around + bround; _i = (_0 - excy1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - excy1; around = _0 - avirt;
    ce[1] = around + bround; ce[3] = (_j + _i); bvirt = (ce[3] - _j);
    avirt = ce[3] - bvirt; bround = _i - bvirt; around = _j - avirt;
    ce[2] = around + bround;

    dxay1 = (pd[0] * pa[1]); c = (SPLITTER * pd[0]); abig = (c - pd[0]);
    ahi = c - abig; alo = pd[0] - ahi; c = (SPLITTER * pa[1]);
    abig = (c - pa[1]); bhi = c - abig; blo = pa[1] - bhi;
    err1 = dxay1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); dxay0 = (alo * blo) - err3;
    axdy1 = (pa[0] * pd[1]); c = (SPLITTER * pa[0]); abig = (c - pa[0]);
    ahi = c - abig; alo = pa[0] - ahi; c = (SPLITTER * pd[1]);
    abig = (c - pd[1]); bhi = c - abig; blo = pd[1] - bhi;
    err1 = axdy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); axdy0 = (alo * blo) - err3;
    _i = (dxay0 - axdy0); bvirt = (dxay0 - _i); avirt = _i + bvirt;
    bround = bvirt - axdy0; around = dxay0 - avirt;
    da[0] = around + bround; _j = (dxay1 + _i); bvirt = (_j - dxay1);
    avirt = _j - bvirt; bround = _i - bvirt; around = dxay1 - avirt;
    _0 = around + bround; _i = (_0 - axdy1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - axdy1; around = _0 - avirt;
    da[1] = around + bround; da[3] = (_j + _i); bvirt = (da[3] - _j);
    avirt = da[3] - bvirt; bround = _i - bvirt; around = _j - avirt;
    da[2] = around + bround;

    exby1 = (pe[0] * pb[1]); c = (SPLITTER * pe[0]); abig = (c - pe[0]);
    ahi = c - abig; alo = pe[0] - ahi; c = (SPLITTER * pb[1]);
    abig = (c - pb[1]); bhi = c - abig; blo = pb[1] - bhi;
    err1 = exby1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); exby0 = (alo * blo) - err3;
    bxey1 = (pb[0] * pe[1]); c = (SPLITTER * pb[0]); abig = (c - pb[0]);
    ahi = c - abig; alo = pb[0] - ahi; c = (SPLITTER * pe[1]);
    abig = (c - pe[1]); bhi = c - abig; blo = pe[1] - bhi;
    err1 = bxey1 - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); bxey0 = (alo * blo) - err3;
    _i = (exby0 - bxey0); bvirt = (exby0 - _i); avirt = _i + bvirt;
    bround = bvirt - bxey0; around = exby0 - avirt;
    eb[0] = around + bround; _j = (exby1 + _i); bvirt = (_j - exby1);
    avirt = _j - bvirt; bround = _i - bvirt; around = exby1 - avirt;
    _0 = around + bround; _i = (_0 - bxey1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxey1; around = _0 - avirt;
    eb[1] = around + bround; eb[3] = (_j + _i); bvirt = (eb[3] - _j);
    avirt = eb[3] - bvirt; bround = _i - bvirt; around = _j - avirt;
    eb[2] = around + bround;

    temp8alen =Expansion.scale(4, bc, pa[2], temp8a);
    temp8blen =Expansion.scale(4, ac, -pb[2], temp8b);
    temp16len =
      Expansion.sum(temp8alen, temp8a, temp8blen, temp8b,
                    temp16);
    temp8alen =Expansion.scale(4, ab, pc[2], temp8a);abclen =
      Expansion.sum(temp8alen, temp8a, temp16len, temp16,
                    abc);

    temp8alen =Expansion.scale(4, cd, pb[2], temp8a);
    temp8blen =Expansion.scale(4, bd, -pc[2], temp8b);
    temp16len =
      Expansion.sum(temp8alen, temp8a, temp8blen, temp8b,
                    temp16);
    temp8alen =Expansion.scale(4, bc, pd[2], temp8a);bcdlen =
      Expansion.sum(temp8alen, temp8a, temp16len, temp16,
                    bcd);

    temp8alen =Expansion.scale(4, de, pc[2], temp8a);
    temp8blen =Expansion.scale(4, ce, -pd[2], temp8b);
    temp16len =
      Expansion.sum(temp8alen, temp8a, temp8blen, temp8b,
                    temp16);
    temp8alen =Expansion.scale(4, cd, pe[2], temp8a);cdelen =
      Expansion.sum(temp8alen, temp8a, temp16len, temp16,
                    cde);

    temp8alen =Expansion.scale(4, ea, pd[2], temp8a);
    temp8blen =Expansion.scale(4, da, -pe[2], temp8b);
    temp16len =
      Expansion.sum(temp8alen, temp8a, temp8blen, temp8b,
                    temp16);
    temp8alen =Expansion.scale(4, de, pa[2], temp8a);dealen =
      Expansion.sum(temp8alen, temp8a, temp16len, temp16,
                    dea);

    temp8alen =Expansion.scale(4, ab, pe[2], temp8a);
    temp8blen =Expansion.scale(4, eb, -pa[2], temp8b);
    temp16len =
      Expansion.sum(temp8alen, temp8a, temp8blen, temp8b,
                    temp16);
    temp8alen =Expansion.scale(4, ea, pb[2], temp8a);eablen =
      Expansion.sum(temp8alen, temp8a, temp16len, temp16,
                    eab);

    temp8alen =Expansion.scale(4, bd, pa[2], temp8a);
    temp8blen =Expansion.scale(4, da, pb[2], temp8b);
    temp16len =
      Expansion.sum(temp8alen, temp8a, temp8blen, temp8b,
                    temp16);
    temp8alen =Expansion.scale(4, ab, pd[2], temp8a);abdlen =
      Expansion.sum(temp8alen, temp8a, temp16len, temp16,
                    abd);

    temp8alen =Expansion.scale(4, ce, pb[2], temp8a);
    temp8blen =Expansion.scale(4, eb, pc[2], temp8b);
    temp16len =
      Expansion.sum(temp8alen, temp8a, temp8blen, temp8b,
                    temp16);
    temp8alen =Expansion.scale(4, bc, pe[2], temp8a);bcelen =
      Expansion.sum(temp8alen, temp8a, temp16len, temp16,
                    bce);

    temp8alen =Expansion.scale(4, da, pc[2], temp8a);
    temp8blen =Expansion.scale(4, ac, pd[2], temp8b);
    temp16len =
      Expansion.sum(temp8alen, temp8a, temp8blen, temp8b,
                    temp16);
    temp8alen =Expansion.scale(4, cd, pa[2], temp8a);cdalen =
      Expansion.sum(temp8alen, temp8a, temp16len, temp16,
                    cda);

    temp8alen =Expansion.scale(4, eb, pd[2], temp8a);
    temp8blen =Expansion.scale(4, bd, pe[2], temp8b);
    temp16len =
      Expansion.sum(temp8alen, temp8a, temp8blen, temp8b,
                    temp16);
    temp8alen =Expansion.scale(4, de, pb[2], temp8a);deblen =
      Expansion.sum(temp8alen, temp8a, temp16len, temp16,
                    deb);

    temp8alen =Expansion.scale(4, ac, pe[2], temp8a);
    temp8blen =Expansion.scale(4, ce, pa[2], temp8b);
    temp16len =
      Expansion.sum(temp8alen, temp8a, temp8blen, temp8b,
                    temp16);
    temp8alen =Expansion.scale(4, ea, pc[2], temp8a);eaclen =
      Expansion.sum(temp8alen, temp8a, temp16len, temp16,
                    eac);

    temp48alen =
      Expansion.sum(cdelen, cde, bcelen, bce, temp48a);
    temp48blen =
      Expansion.sum(deblen, deb, bcdlen, bcd, temp48b);
    for (i = 0; i < temp48blen; i++) {
      temp48b[i] = -temp48b[i];
    } bcdelen =
      Expansion.sum(temp48alen, temp48a, temp48blen,
                    temp48b, bcde);
    xlen =Expansion.scale(bcdelen, bcde, pa[0], temp192);
    xlen =Expansion.scale(xlen, temp192, pa[0], det384x);
    ylen =Expansion.scale(bcdelen, bcde, pa[1], temp192);
    ylen =Expansion.scale(ylen, temp192, pa[1], det384y);
    zlen =Expansion.scale(bcdelen, bcde, pa[2], temp192);
    zlen =Expansion.scale(zlen, temp192, pa[2], det384z);
    xylen =
      Expansion.sum(xlen, det384x, ylen, det384y, detxy);
    alen =
      Expansion.sum(xylen, detxy, zlen, det384z, adet);

    temp48alen =
      Expansion.sum(dealen, dea, cdalen, cda, temp48a);
    temp48blen =
      Expansion.sum(eaclen, eac, cdelen, cde, temp48b);
    for (i = 0; i < temp48blen; i++) {
      temp48b[i] = -temp48b[i];
    } cdealen =
      Expansion.sum(temp48alen, temp48a, temp48blen,
                    temp48b, cdea);
    xlen =Expansion.scale(cdealen, cdea, pb[0], temp192);
    xlen =Expansion.scale(xlen, temp192, pb[0], det384x);
    ylen =Expansion.scale(cdealen, cdea, pb[1], temp192);
    ylen =Expansion.scale(ylen, temp192, pb[1], det384y);
    zlen =Expansion.scale(cdealen, cdea, pb[2], temp192);
    zlen =Expansion.scale(zlen, temp192, pb[2], det384z);
    xylen =
      Expansion.sum(xlen, det384x, ylen, det384y, detxy);
    blen =
      Expansion.sum(xylen, detxy, zlen, det384z, bdet);

    temp48alen =
      Expansion.sum(eablen, eab, deblen, deb, temp48a);
    temp48blen =
      Expansion.sum(abdlen, abd, dealen, dea, temp48b);
    for (i = 0; i < temp48blen; i++) {
      temp48b[i] = -temp48b[i];
    } deablen =
      Expansion.sum(temp48alen, temp48a, temp48blen,
                    temp48b, deab);
    xlen =Expansion.scale(deablen, deab, pc[0], temp192);
    xlen =Expansion.scale(xlen, temp192, pc[0], det384x);
    ylen =Expansion.scale(deablen, deab, pc[1], temp192);
    ylen =Expansion.scale(ylen, temp192, pc[1], det384y);
    zlen =Expansion.scale(deablen, deab, pc[2], temp192);
    zlen =Expansion.scale(zlen, temp192, pc[2], det384z);
    xylen =
      Expansion.sum(xlen, det384x, ylen, det384y, detxy);
    clen =
      Expansion.sum(xylen, detxy, zlen, det384z, cdet);

    temp48alen =
      Expansion.sum(abclen, abc, eaclen, eac, temp48a);
    temp48blen =
      Expansion.sum(bcelen, bce, eablen, eab, temp48b);
    for (i = 0; i < temp48blen; i++) {
      temp48b[i] = -temp48b[i];
    } eabclen =
      Expansion.sum(temp48alen, temp48a, temp48blen,
                    temp48b, eabc);
    xlen =Expansion.scale(eabclen, eabc, pd[0], temp192);
    xlen =Expansion.scale(xlen, temp192, pd[0], det384x);
    ylen =Expansion.scale(eabclen, eabc, pd[1], temp192);
    ylen =Expansion.scale(ylen, temp192, pd[1], det384y);
    zlen =Expansion.scale(eabclen, eabc, pd[2], temp192);
    zlen =Expansion.scale(zlen, temp192, pd[2], det384z);
    xylen =
      Expansion.sum(xlen, det384x, ylen, det384y, detxy);
    dlen =
      Expansion.sum(xylen, detxy, zlen, det384z, ddet);

    temp48alen =
      Expansion.sum(bcdlen, bcd, abdlen, abd, temp48a);
    temp48blen =
      Expansion.sum(cdalen, cda, abclen, abc, temp48b);
    for (i = 0; i < temp48blen; i++) {
      temp48b[i] = -temp48b[i];
    } abcdlen =
      Expansion.sum(temp48alen, temp48a, temp48blen,
                    temp48b, abcd);
    xlen =Expansion.scale(abcdlen, abcd, pe[0], temp192);
    xlen =Expansion.scale(xlen, temp192, pe[0], det384x);
    ylen =Expansion.scale(abcdlen, abcd, pe[1], temp192);
    ylen =Expansion.scale(ylen, temp192, pe[1], det384y);
    zlen =Expansion.scale(abcdlen, abcd, pe[2], temp192);
    zlen =Expansion.scale(zlen, temp192, pe[2], det384z);
    xylen =
      Expansion.sum(xlen, det384x, ylen, det384y, detxy);
    elen =
      Expansion.sum(xylen, detxy, zlen, det384z, edet);

    ablen =Expansion.sum(alen, adet, blen, bdet, abdet);
    cdlen =Expansion.sum(clen, cdet, dlen, ddet, cddet);
    cdelen =
      Expansion.sum(cdlen, cddet, elen, edet, cdedet);
    deterlen =
      Expansion.sum(ablen, abdet, cdelen, cdedet, deter);

    return deter[deterlen - 1];
  }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public Exact () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
