package mop.java.geometry.predicates;

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
 * @version 2026-05-24
 */

public final class Exact implements Predicate {

  private static final double SPLITTER = 0x1.0000002p27;

  public final boolean isExact () { return true; }


  //--------------------------------------------------------------------

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

    final XDouble cda = cd.add(da).add(ac);
    final XDouble dab = da.add(ab).add(bd);
    final XDouble abc = ab.add(bc).subtract(ac);
    final XDouble bcd = bc.add(cd).subtract(bd);


    final double ax = a[0], ay = a[1];
    final XDouble adet =
      (bcd.scale(ax).scale(ax)).add(bcd.scale(ay).scale(ay));

    final double bx = b[0], by = b[1];
    final XDouble bdet =
      (cda.scale(bx).scale(-bx)).add(cda.scale(by).scale(-by));

    final double cx = c[0], cy = c[1];
    final XDouble cdet =
      (dab.scale(cx).scale(cx)).add(dab.scale(cy).scale(cy));

    final double dx = d[0], dy = d[1];
    final XDouble ddet =
      (abc.scale(dx).scale(-dx)).add(abc.scale(dy).scale(-dy));

    // this change fixes current test cases.
    // shouldn't matter, XDouble addition should be associative
    //final XDouble det = adet.add(bdet).add(cdet.add(ddet));
    final XDouble det = adet.add(bdet).add(cdet).add(ddet);
//    System.out.println();
//    System.out.println(
//      toHexString(a) + "->" + toHexString(b) + "->" + toHexString(c));
//    System.out.println(toHexString(d));
//    System.out.println(det);
    return det.estimate(); }
    //return det.term(det.nterms() - 1); }

  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------
  private final double[] twoProduct (final double a,
                                     final double b) {
    final double x = (a * b);
    // Two_Product_Tail(a, b, x, y)
    //-----------------------------
    // Split(a, ahi, alo);
    final double ca = (SPLITTER * a);
    final double abiga = (ca - a);
    final double ahi = ca - abiga;
    final double alo = a - ahi;
    // Split(b, bhi, blo);
    final double cb = (SPLITTER * b);
    final double abigb = (cb - b);
    final double bhi = cb - abigb;
    final double blo = b - bhi;
    //-------------------------
    final double err1 = x - (ahi * bhi);
    final double err2 = err1 - (alo * bhi);
    final double err3 = err2 - (ahi * blo);
    final double y = (alo * blo) - err3;
    // TODO: compare performance to a XY record class
    // TODO: pre-allocate array/instance for return values?
    // TODO: reverse order of returned value and treat as an Expansion?
    return new double[] { x, y, };
  }
  //--------------------------------------------------------------------
  private static final double[] twoSum (final double a,
                                         final double b) {
    final double x = (a + b);
    //  Two_Sum_Tail(a, b, x, y)
    final double bvirt = (x - a);
    final double avirt = x - bvirt;
    final double bround = b - bvirt;
    final double around = a - avirt;
    final double y = around + bround;
    return new double[] { x, y, }; }
  //--------------------------------------------------------------------
  private static final double[] twoDiff (final double a,
                                         final double b) {
    final double x = (a - b);
    //Two_Diff_Tail(a, b, x, y)
    final double bvirt = (a - x);
    final double avirt = x + bvirt;
    final double bround = bvirt - b;
    final double around = a - avirt;
    final double y = around + bround;
    return new double[] { x, y, }; }
  //--------------------------------------------------------------------
  private static final double[] twoOneDiff (final double a1,
                                            final double a0,
                                            final double b) {

    // Two_Diff (a0, b, _i, x0);
    final double _i, x0;
    { final double[] td = twoDiff(a0,b); _i = td[0]; x0 = td[1]; }
    // Two_Sum (a1, _i, x2, x1)
    final double x2, x1;
    { final double[] ts = twoSum(a1,_i); x2 = ts[0]; x1 = ts[1]; }
    return new double[] {x2,x1,x0}; }
  //--------------------------------------------------------------------
  // NOTE: order of args is reversed from Expansion array elements.
  // Here it's most significant first, Expansion is most significant
  // last (not counting implied array length and trailing zeros!).
  private static final double[] twoTwoDiff (final double a1,
                                            final double a0,
                                            final double b1,
                                            final double b0) {
    // Two_One_Diff(a1, a0, b0, _j, _0, x0); \
    final double _j, _0, x0;
    { final double[] tod = twoOneDiff(a1,a0,b0);
      _j = tod[0]; _0 = tod[1]; x0 =  tod[2]; }
    // Two_One_Diff(_j, _0, b1, x3, x2, x1)
    final double x3, x2, x1;
    { final double[] tod = twoOneDiff(_j,_0,b1);
      x3 = tod[0]; x2 = tod[1]; x1 =  tod[2]; }
    return new double[] {x3,x2,x1,x0}; }
  //--------------------------------------------------------------------
  // TODO: seems to return signed area, not 2xsigned area
  // TODO: returns 1.0 for a co-linear triangle,
  //  where one vtx is the mean of the other 2.

  public final double orient2d (final double[] pa,
                                final double[] pb,
                                final double[] pc) {
    // Two_Product(pa[0], pb[1], axby1, axby0);
    final double axby1, axby0;
    { final double[] tp = twoProduct(pa[0],pb[1]);
      axby1 = tp[0]; axby0 = tp[1]; }
    // Two_Product(pa[0], pc[1], axcy1, axcy0);
    final double axcy1, axcy0;
    { final double[] tp = twoProduct(pa[0], pc[1]);
      axcy1 = tp[0]; axcy0 = tp[1]; }
    // Two_Two_Diff(axby1, axby0, axcy1, axcy0,
    //              aterms3, aterms[2], aterms[1], aterms[0]);
    final double[] aterms = new double[4];
    { final double[] ttd = twoTwoDiff(axby1, axby0, axcy1, axcy0);
      aterms[3] = ttd[0];
      aterms[2] = ttd[1]; aterms[1] = ttd[2]; aterms[0] = ttd[3]; }

    //Two_Product(pb[0], pc[1], bxcy1, bxcy0);
    final double bxcy1, bxcy0;
    { final double[] tp = twoProduct(pb[0], pc[1]);
      bxcy1 = tp[0]; bxcy0 = tp[1]; }
    //Two_Product(pb[0], pa[1], bxay1, bxay0);
    final double bxay1, bxay0;
    { final double[] tp = twoProduct(pb[0], pa[1]);
      bxay1 = tp[0]; bxay0 = tp[1]; }

    //Two_Two_Diff(bxcy1, bxcy0, bxay1, bxay0,
    //             bterms3, bterms[2], bterms[1], bterms[0]);
    final double[] bterms = new double[4];
    { final double[] ttd = twoTwoDiff(bxcy1, bxcy0, bxay1, bxay0);
      bterms[3] = ttd[0];
      bterms[2] = ttd[1]; bterms[1] = ttd[2]; bterms[0] = ttd[3]; }

    //Two_Product(pc[0], pa[1], cxay1, cxay0);
    final double cxay1, cxay0;
    { final double[] tp = twoProduct(pc[0], pa[1]);
      cxay1 = tp[0]; cxay0 = tp[1]; }

    //Two_Product(pc[0], pb[1], cxby1, cxby0);
    final double cxby1, cxby0;
    { final double[] tp = twoProduct(pc[0], pb[1]);
      cxby1 = tp[0]; cxby0 = tp[1]; }

    //Two_Two_Diff(cxay1, cxay0, cxby1, cxby0,
    //             cterms3, cterms[2], cterms[1], cterms[0]);
    final double[] cterms = new double[4];
    { final double[] ttd = twoTwoDiff(cxay1, cxay0, cxby1, cxby0);
      cterms[3] = ttd[0];
      cterms[2] = ttd[1]; cterms[1] = ttd[2]; cterms[0] = ttd[3]; }

    final double[] v = new double[8];
    final int vlength = Expansion.sum(4, aterms, 4, bterms, v);
    final double[] w = new double[12];
    final int wlength = Expansion.sum(vlength, v, 4, cterms, w);
    return w[wlength - 1]; }

  //--------------------------------------------------------------------
  // orient3d
  //--------------------------------------------------------------------
  public final double orient3d (final double[] pa, final double[] pb,
                                final double[] pc, final double[] pd) {
    double axby1, bxcy1, cxdy1, dxay1, axcy1, bxdy1;
    double bxay1, cxby1, dxcy1, axdy1, cxay1, dxby1;
    double axby0, bxcy0, cxdy0, dxay0, axcy0, bxdy0;
    double bxay0, cxby0, dxcy0, axdy0, cxay0, dxby0;
    double[] ab = new double[4], bc = new double[4], cd = new double[4],
      da = new double[4], ac = new double[4], bd = new double[4];
    double[] temp8 = new double[8]; int templen;
    double[] abc = new double[12], bcd = new double[12], cda =
      new double[12], dab = new double[12];
    int abclen, bcdlen, cdalen, dablen;
    double[] adet = new double[24], bdet = new double[24], cdet =
      new double[24], ddet = new double[24]; int alen, blen, clen, dlen;
    double[] abdet = new double[48], cddet = new double[48];
    int ablen, cdlen; double[] deter = new double[96]; int deterlen;
    int i;

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

    templen =Expansion.sum(4, cd, 4, da, temp8);
    cdalen =Expansion.sum(templen, temp8, 4, ac, cda);
    templen =Expansion.sum(4, da, 4, ab, temp8);
    dablen =Expansion.sum(templen, temp8, 4, bd, dab);
    for (i = 0; i < 4; i++) {
      bd[i] = -bd[i]; ac[i] = -ac[i];
    } templen =Expansion.sum(4, ab, 4, bc, temp8);
    abclen =Expansion.sum(templen, temp8, 4, ac, abc);
    templen =Expansion.sum(4, bc, 4, cd, temp8);
    bcdlen =Expansion.sum(templen, temp8, 4, bd, bcd);

    alen =Expansion.scale(bcdlen, bcd, pa[2], adet);
    blen =Expansion.scale(cdalen, cda, -pb[2], bdet);
    clen =Expansion.scale(dablen, dab, pc[2], cdet);
    dlen =Expansion.scale(abclen, abc, -pd[2], ddet);

    ablen =Expansion.sum(alen, adet, blen, bdet, abdet);
    cdlen =Expansion.sum(clen, cdet, dlen, ddet, cddet);
    deterlen =
     Expansion.sum(ablen, abdet, cdlen, cddet, deter);

    return deter[deterlen - 1];
  }

  //--------------------------------------------------------------------
  // incircle

//  public final double incircle (final double[] pa,
//                                final double[] pb,
//                                final double[] pc,
//                                final double[] pd) {
//    double[] temp8 = new double[8];
//    double[] abc = new double[12],
//      bcd = new double[12],
//      cda = new double[12], dab = new double[12];
//    double[] det24x = new double[24], det24y = new double[24],
//      det48x = new double[48], det48y = new double[48];
//    double[] adet = new double[96], bdet = new double[96],
//      cdet = new double[96], ddet = new double[96];
//    double[] abdet = new double[192], cddet = new double[192];
//    double[] deter = new double[384];
//
//    // Two_Product(pa[0], pb[1], axby1, axby0);
//    // Two_Product(pb[0], pa[1], bxay1, bxay0);
//    //Two_Two_Diff(axby1, axby0, bxay1, bxay0,
//    //             ab[3], ab[2], ab[1], ab[0]);
//    final Hilo axby = Hilo.twoProduct(pa[0], pb[1]);
//    final Hilo bxay = Hilo.twoProduct(pb[0], pa[1]);
//    final double[] ab = Hilo.twoTwoDiff(axby, bxay);
//
//    // Two_Product(pb[0], pc[1], bxcy1, bxcy0);
//    // Two_Product(pc[0], pb[1], cxby1, cxby0);
//    // Two_Two_Diff(bxcy1, bxcy0, cxby1, cxby0,
//    //              bc[3], bc[2], bc[1], bc[0]);
//    final Hilo bxcy = Hilo.twoProduct(pb[0], pc[1]);
//    final Hilo cxby = Hilo.twoProduct(pc[0], pb[1]);
//    final double[] bc = Hilo.twoTwoDiff(bxcy, cxby);
//
//    // Two_Product(pc[0], pd[1], cxdy1, cxdy0);
//    // Two_Product(pd[0], pc[1], dxcy1, dxcy0);
//    // Two_Two_Diff(cxdy1, cxdy0, dxcy1, dxcy0,
//    //              cd[3], cd[2], cd[1], cd[0]);
//    final Hilo cxdy = Hilo.twoProduct(pc[0], pd[1]);
//    final Hilo dxcy = Hilo.twoProduct(pd[0], pc[1]);
//    final double[] cd = Hilo.twoTwoDiff(cxdy, dxcy);
//
//    // Two_Product(pd[0], pa[1], dxay1, dxay0);
//    // Two_Product(pa[0], pd[1], axdy1, axdy0);
//    // Two_Two_Diff(dxay1, dxay0, axdy1, axdy0,
//    //             da[3], da[2], da[1], da[0]);
//    final Hilo dxay = Hilo.twoProduct(pd[0], pa[1]);
//    final Hilo axdy = Hilo.twoProduct(pa[0], pd[1]);
//    final double[] da = Hilo.twoTwoDiff(dxay, axdy);
//
//    //  Two_Product(pa[0], pc[1], axcy1, axcy0);
//    //  Two_Product(pc[0], pa[1], cxay1, cxay0);
//    //  Two_Two_Diff(axcy1, axcy0, cxay1, cxay0,
//    //               ac[3], ac[2], ac[1], ac[0]);
//    final Hilo axcy = Hilo.twoProduct(pa[0], pc[1]);
//    final Hilo cxay = Hilo.twoProduct(pc[0], pa[1]);
//    final double[] ac = Hilo.twoTwoDiff(axcy, cxay);
//
//    //  Two_Product(pb[0], pd[1], bxdy1, bxdy0);
//    //  Two_Product(pd[0], pb[1], dxby1, dxby0);
//    //  Two_Two_Diff(bxdy1, bxdy0, dxby1, dxby0,
//    //               bd[3], bd[2], bd[1], bd[0]);
//    final Hilo bxdy = Hilo.twoProduct(pb[0], pd[1]);
//    final Hilo dxby = Hilo.twoProduct(pd[0], pb[1]);
//    final double[] bd = Hilo.twoTwoDiff(bxdy, dxby);
//
//
//    int templen = Expansion.sum(4, cd, 4, da, temp8);
//    int cdalen = Expansion.sum(templen, temp8, 4, ac, cda);
//    templen = Expansion.sum(4, da, 4, ab, temp8);
//    int dablen = Expansion.sum(templen, temp8, 4, bd, dab);
//    for (int i = 0; i < 4; i++) { bd[i] = -bd[i]; ac[i] = -ac[i]; }
//    templen = Expansion.sum(4, ab, 4, bc, temp8);
//    int abclen = Expansion.sum(templen, temp8, 4, ac, abc);
//    templen = Expansion.sum(4, bc, 4, cd, temp8);
//    int bcdlen = Expansion.sum(templen, temp8, 4, bd, bcd);
//
//    int xlen = Expansion.scale(bcdlen, bcd, pa[0], det24x);
//    xlen = Expansion.scale(xlen, det24x, pa[0], det48x);
//    int ylen =Expansion.scale(bcdlen, bcd, pa[1], det24y);
//    ylen =Expansion.scale(ylen, det24y, pa[1], det48y);
//    int alen = Expansion.sum(xlen, det48x, ylen, det48y, adet);
//
//    xlen =Expansion.scale(cdalen, cda, pb[0], det24x);
//    xlen =Expansion.scale(xlen, det24x, -pb[0], det48x);
//    ylen =Expansion.scale(cdalen, cda, pb[1], det24y);
//    ylen =Expansion.scale(ylen, det24y, -pb[1], det48y);
//    int blen = Expansion.sum(xlen, det48x, ylen, det48y, bdet);
//
//    xlen =Expansion.scale(dablen, dab, pc[0], det24x);
//    xlen =Expansion.scale(xlen, det24x, pc[0], det48x);
//    ylen =Expansion.scale(dablen, dab, pc[1], det24y);
//    ylen =Expansion.scale(ylen, det24y, pc[1], det48y);
//    int clen = Expansion.sum(xlen, det48x, ylen, det48y, cdet);
//
//    xlen =Expansion.scale(abclen, abc, pd[0], det24x);
//    xlen =Expansion.scale(xlen, det24x, -pd[0], det48x);
//    ylen =Expansion.scale(abclen, abc, pd[1], det24y);
//    ylen =Expansion.scale(ylen, det24y, -pd[1], det48y);
//    int dlen = Expansion.sum(xlen, det48x, ylen, det48y, ddet);
//
//    int ablen =Expansion.sum(alen, adet, blen, bdet, abdet);
//    int cdlen =Expansion.sum(clen, cdet, dlen, ddet, cddet);
//    int deterlen = Expansion.sum(ablen, abdet, cdlen, cddet, deter);
//System.out.println(deterlen);
//    return deter[deterlen - 1]; }

//  public final double incircle (final double[] pa, final double[] pb,
//                                final double[] pc, final double[] pd) {
//    double axby1, bxcy1, cxdy1, dxay1, axcy1, bxdy1;
//    double bxay1, cxby1, dxcy1, axdy1, cxay1, dxby1;
//    double axby0, bxcy0, cxdy0, dxay0, axcy0, bxdy0;
//    double bxay0, cxby0, dxcy0, axdy0, cxay0, dxby0;
//    double[] ab = new double[4], bc = new double[4], cd = new double[4],
//      da = new double[4], ac = new double[4], bd = new double[4];
//    double[] temp8 = new double[8]; int templen;
//    double[] abc = new double[12], bcd = new double[12], cda =
//      new double[12], dab = new double[12];
//    int abclen, bcdlen, cdalen, dablen;
//    double[] det24x = new double[24], det24y = new double[24], det48x =
//      new double[48], det48y = new double[48]; int xlen, ylen;
//    double[] adet = new double[96], bdet = new double[96], cdet =
//      new double[96], ddet = new double[96]; int alen, blen, clen, dlen;
//    double[] abdet = new double[192], cddet = new double[192];
//    int ablen, cdlen; double[] deter = new double[384]; int deterlen;
//    int i;
//
//    double bvirt; double avirt, bround, around; double c; double abig;
//    double ahi, alo, bhi, blo; double err1, err2, err3; double _i, _j;
//    double _0;
//
//    axby1 = (pa[0] * pb[1]); c = (SPLITTER * pa[0]); abig = (c - pa[0]);
//    ahi = c - abig; alo = pa[0] - ahi; c = (SPLITTER * pb[1]);
//    abig = (c - pb[1]); bhi = c - abig; blo = pb[1] - bhi;
//    err1 = axby1 - (ahi * bhi); err2 = err1 - (alo * bhi);
//    err3 = err2 - (ahi * blo); axby0 = (alo * blo) - err3;
//    bxay1 = (pb[0] * pa[1]); c = (SPLITTER * pb[0]); abig = (c - pb[0]);
//    ahi = c - abig; alo = pb[0] - ahi; c = (SPLITTER * pa[1]);
//    abig = (c - pa[1]); bhi = c - abig; blo = pa[1] - bhi;
//    err1 = bxay1 - (ahi * bhi); err2 = err1 - (alo * bhi);
//    err3 = err2 - (ahi * blo); bxay0 = (alo * blo) - err3;
//    _i = (axby0 - bxay0); bvirt = (axby0 - _i); avirt = _i + bvirt;
//    bround = bvirt - bxay0; around = axby0 - avirt;
//    ab[0] = around + bround; _j = (axby1 + _i); bvirt = (_j - axby1);
//    avirt = _j - bvirt; bround = _i - bvirt; around = axby1 - avirt;
//    _0 = around + bround; _i = (_0 - bxay1); bvirt = (_0 - _i);
//    avirt = _i + bvirt; bround = bvirt - bxay1; around = _0 - avirt;
//    ab[1] = around + bround; ab[3] = (_j + _i); bvirt = (ab[3] - _j);
//    avirt = ab[3] - bvirt; bround = _i - bvirt; around = _j - avirt;
//    ab[2] = around + bround;
//
//    bxcy1 = (pb[0] * pc[1]); c = (SPLITTER * pb[0]); abig = (c - pb[0]);
//    ahi = c - abig; alo = pb[0] - ahi; c = (SPLITTER * pc[1]);
//    abig = (c - pc[1]); bhi = c - abig; blo = pc[1] - bhi;
//    err1 = bxcy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
//    err3 = err2 - (ahi * blo); bxcy0 = (alo * blo) - err3;
//    cxby1 = (pc[0] * pb[1]); c = (SPLITTER * pc[0]); abig = (c - pc[0]);
//    ahi = c - abig; alo = pc[0] - ahi; c = (SPLITTER * pb[1]);
//    abig = (c - pb[1]); bhi = c - abig; blo = pb[1] - bhi;
//    err1 = cxby1 - (ahi * bhi); err2 = err1 - (alo * bhi);
//    err3 = err2 - (ahi * blo); cxby0 = (alo * blo) - err3;
//    _i = (bxcy0 - cxby0); bvirt = (bxcy0 - _i); avirt = _i + bvirt;
//    bround = bvirt - cxby0; around = bxcy0 - avirt;
//    bc[0] = around + bround; _j = (bxcy1 + _i); bvirt = (_j - bxcy1);
//    avirt = _j - bvirt; bround = _i - bvirt; around = bxcy1 - avirt;
//    _0 = around + bround; _i = (_0 - cxby1); bvirt = (_0 - _i);
//    avirt = _i + bvirt; bround = bvirt - cxby1; around = _0 - avirt;
//    bc[1] = around + bround; bc[3] = (_j + _i); bvirt = (bc[3] - _j);
//    avirt = bc[3] - bvirt; bround = _i - bvirt; around = _j - avirt;
//    bc[2] = around + bround;
//
//    cxdy1 = (pc[0] * pd[1]); c = (SPLITTER * pc[0]); abig = (c - pc[0]);
//    ahi = c - abig; alo = pc[0] - ahi; c = (SPLITTER * pd[1]);
//    abig = (c - pd[1]); bhi = c - abig; blo = pd[1] - bhi;
//    err1 = cxdy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
//    err3 = err2 - (ahi * blo); cxdy0 = (alo * blo) - err3;
//    dxcy1 = (pd[0] * pc[1]); c = (SPLITTER * pd[0]); abig = (c - pd[0]);
//    ahi = c - abig; alo = pd[0] - ahi; c = (SPLITTER * pc[1]);
//    abig = (c - pc[1]); bhi = c - abig; blo = pc[1] - bhi;
//    err1 = dxcy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
//    err3 = err2 - (ahi * blo); dxcy0 = (alo * blo) - err3;
//    _i = (cxdy0 - dxcy0); bvirt = (cxdy0 - _i); avirt = _i + bvirt;
//    bround = bvirt - dxcy0; around = cxdy0 - avirt;
//    cd[0] = around + bround; _j = (cxdy1 + _i); bvirt = (_j - cxdy1);
//    avirt = _j - bvirt; bround = _i - bvirt; around = cxdy1 - avirt;
//    _0 = around + bround; _i = (_0 - dxcy1); bvirt = (_0 - _i);
//    avirt = _i + bvirt; bround = bvirt - dxcy1; around = _0 - avirt;
//    cd[1] = around + bround; cd[3] = (_j + _i); bvirt = (cd[3] - _j);
//    avirt = cd[3] - bvirt; bround = _i - bvirt; around = _j - avirt;
//    cd[2] = around + bround;
//
//    dxay1 = (pd[0] * pa[1]); c = (SPLITTER * pd[0]); abig = (c - pd[0]);
//    ahi = c - abig; alo = pd[0] - ahi; c = (SPLITTER * pa[1]);
//    abig = (c - pa[1]); bhi = c - abig; blo = pa[1] - bhi;
//    err1 = dxay1 - (ahi * bhi); err2 = err1 - (alo * bhi);
//    err3 = err2 - (ahi * blo); dxay0 = (alo * blo) - err3;
//    axdy1 = (pa[0] * pd[1]); c = (SPLITTER * pa[0]); abig = (c - pa[0]);
//    ahi = c - abig; alo = pa[0] - ahi; c = (SPLITTER * pd[1]);
//    abig = (c - pd[1]); bhi = c - abig; blo = pd[1] - bhi;
//    err1 = axdy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
//    err3 = err2 - (ahi * blo); axdy0 = (alo * blo) - err3;
//    _i = (dxay0 - axdy0); bvirt = (dxay0 - _i); avirt = _i + bvirt;
//    bround = bvirt - axdy0; around = dxay0 - avirt;
//    da[0] = around + bround; _j = (dxay1 + _i); bvirt = (_j - dxay1);
//    avirt = _j - bvirt; bround = _i - bvirt; around = dxay1 - avirt;
//    _0 = around + bround; _i = (_0 - axdy1); bvirt = (_0 - _i);
//    avirt = _i + bvirt; bround = bvirt - axdy1; around = _0 - avirt;
//    da[1] = around + bround; da[3] = (_j + _i); bvirt = (da[3] - _j);
//    avirt = da[3] - bvirt; bround = _i - bvirt; around = _j - avirt;
//    da[2] = around + bround;
//
//    axcy1 = (pa[0] * pc[1]); c = (SPLITTER * pa[0]); abig = (c - pa[0]);
//    ahi = c - abig; alo = pa[0] - ahi; c = (SPLITTER * pc[1]);
//    abig = (c - pc[1]); bhi = c - abig; blo = pc[1] - bhi;
//    err1 = axcy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
//    err3 = err2 - (ahi * blo); axcy0 = (alo * blo) - err3;
//    cxay1 = (pc[0] * pa[1]); c = (SPLITTER * pc[0]); abig = (c - pc[0]);
//    ahi = c - abig; alo = pc[0] - ahi; c = (SPLITTER * pa[1]);
//    abig = (c - pa[1]); bhi = c - abig; blo = pa[1] - bhi;
//    err1 = cxay1 - (ahi * bhi); err2 = err1 - (alo * bhi);
//    err3 = err2 - (ahi * blo); cxay0 = (alo * blo) - err3;
//    _i = (axcy0 - cxay0); bvirt = (axcy0 - _i); avirt = _i + bvirt;
//    bround = bvirt - cxay0; around = axcy0 - avirt;
//    ac[0] = around + bround; _j = (axcy1 + _i); bvirt = (_j - axcy1);
//    avirt = _j - bvirt; bround = _i - bvirt; around = axcy1 - avirt;
//    _0 = around + bround; _i = (_0 - cxay1); bvirt = (_0 - _i);
//    avirt = _i + bvirt; bround = bvirt - cxay1; around = _0 - avirt;
//    ac[1] = around + bround; ac[3] = (_j + _i); bvirt = (ac[3] - _j);
//    avirt = ac[3] - bvirt; bround = _i - bvirt; around = _j - avirt;
//    ac[2] = around + bround;
//
//    bxdy1 = (pb[0] * pd[1]); c = (SPLITTER * pb[0]); abig = (c - pb[0]);
//    ahi = c - abig; alo = pb[0] - ahi; c = (SPLITTER * pd[1]);
//    abig = (c - pd[1]); bhi = c - abig; blo = pd[1] - bhi;
//    err1 = bxdy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
//    err3 = err2 - (ahi * blo); bxdy0 = (alo * blo) - err3;
//    dxby1 = (pd[0] * pb[1]); c = (SPLITTER * pd[0]); abig = (c - pd[0]);
//    ahi = c - abig; alo = pd[0] - ahi; c = (SPLITTER * pb[1]);
//    abig = (c - pb[1]); bhi = c - abig; blo = pb[1] - bhi;
//    err1 = dxby1 - (ahi * bhi); err2 = err1 - (alo * bhi);
//    err3 = err2 - (ahi * blo); dxby0 = (alo * blo) - err3;
//    _i = (bxdy0 - dxby0); bvirt = (bxdy0 - _i); avirt = _i + bvirt;
//    bround = bvirt - dxby0; around = bxdy0 - avirt;
//    bd[0] = around + bround; _j = (bxdy1 + _i); bvirt = (_j - bxdy1);
//    avirt = _j - bvirt; bround = _i - bvirt; around = bxdy1 - avirt;
//    _0 = around + bround; _i = (_0 - dxby1); bvirt = (_0 - _i);
//    avirt = _i + bvirt; bround = bvirt - dxby1; around = _0 - avirt;
//    bd[1] = around + bround; bd[3] = (_j + _i); bvirt = (bd[3] - _j);
//    avirt = bd[3] - bvirt; bround = _i - bvirt; around = _j - avirt;
//    bd[2] = around + bround;
//
//    templen =Expansion.sum(4, cd, 4, da, temp8);
//    cdalen =Expansion.sum(templen, temp8, 4, ac, cda);
//    templen =Expansion.sum(4, da, 4, ab, temp8);
//    dablen =Expansion.sum(templen, temp8, 4, bd, dab);
//    for (i = 0; i < 4; i++) {
//      bd[i] = -bd[i]; ac[i] = -ac[i];
//    } templen =Expansion.sum(4, ab, 4, bc, temp8);
//    abclen =Expansion.sum(templen, temp8, 4, ac, abc);
//    templen =Expansion.sum(4, bc, 4, cd, temp8);
//    bcdlen =Expansion.sum(templen, temp8, 4, bd, bcd);
//
//    xlen =Expansion.scale(bcdlen, bcd, pa[0], det24x);
//    xlen =Expansion.scale(xlen, det24x, pa[0], det48x);
//    ylen =Expansion.scale(bcdlen, bcd, pa[1], det24y);
//    ylen =Expansion.scale(ylen, det24y, pa[1], det48y);alen =
//      Expansion.sum(xlen, det48x, ylen, det48y, adet);
//
//    xlen =Expansion.scale(cdalen, cda, pb[0], det24x);
//    xlen =Expansion.scale(xlen, det24x, -pb[0], det48x);
//    ylen =Expansion.scale(cdalen, cda, pb[1], det24y);
//    ylen =Expansion.scale(ylen, det24y, -pb[1], det48y);
//    blen =
//      Expansion.sum(xlen, det48x, ylen, det48y, bdet);
//
//    xlen =Expansion.scale(dablen, dab, pc[0], det24x);
//    xlen =Expansion.scale(xlen, det24x, pc[0], det48x);
//    ylen =Expansion.scale(dablen, dab, pc[1], det24y);
//    ylen =Expansion.scale(ylen, det24y, pc[1], det48y);clen =
//      Expansion.sum(xlen, det48x, ylen, det48y, cdet);
//
//    xlen =Expansion.scale(abclen, abc, pd[0], det24x);
//    xlen =Expansion.scale(xlen, det24x, -pd[0], det48x);
//    ylen =Expansion.scale(abclen, abc, pd[1], det24y);
//    ylen =Expansion.scale(ylen, det24y, -pd[1], det48y);
//    dlen =
//      Expansion.sum(xlen, det48x, ylen, det48y, ddet);
//
//    ablen =Expansion.sum(alen, adet, blen, bdet, abdet);
//    cdlen =Expansion.sum(clen, cdet, dlen, ddet, cddet);
//    deterlen =
//      Expansion.sum(ablen, abdet, cdlen, cddet, deter);
//
//    return deter[deterlen - 1];
//  }

  //--------------------------------------------------------------------
  // insphere
  //--------------------------------------------------------------------
  public final double insphere (final double[] pa, final double[] pb,
                                final double[] pc, final double[] pd,
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
