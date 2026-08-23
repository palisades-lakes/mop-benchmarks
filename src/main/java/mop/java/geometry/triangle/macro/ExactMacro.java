package mop.java.geometry.triangle.macro;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import com.carrotsearch.hppc.DoubleArrayList;
import mop.java.geometry.triangle.Triangle2D;
import mop.java.numbers.XDouble;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

import static mop.java.geometry.Expansion.*;

/**
 * Exact tests.  Robust.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-07
 */

// strictfp unnecessary for JDK17 and later
public final class ExactMacro extends Triangle2D {

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return true; }

  public final double twiceSignedArea () {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();

    double axby1, axcy1, bxcy1, bxay1, cxay1, cxby1;
    double axby0, axcy0, bxcy0, bxay0, cxay0, cxby0;
    double[] aterms = new double[4];
    double[] bterms = new double[4];
    double[] cterms = new double[4];
    double aterms3, bterms3, cterms3;
    double[] v = new double[8];
    double[] w = new double[12];
    int vlength;

    double bvirt;
    double avirt, bround, around;
    double c;
    double abig;
    double ahi, alo, bhi, blo;
    double err1, err2, err3;
    double _i, _j;
    double _0;

    axby1 = (pa.getX() * pb.getY()); c = (SPLITTER * pa.getX());
    abig = (c - pa.getX()); ahi = c - abig; alo = pa.getX() - ahi;
    c = (SPLITTER * pb.getY()); abig = (c - pb.getY());
    bhi = c - abig; blo = pb.getY() - bhi; err1 = axby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axby0 = (alo * blo) - err3;
    axcy1 = (pa.getX() * pc.getY()); c = (SPLITTER * pa.getX());
    abig = (c - pa.getX()); ahi = c - abig; alo = pa.getX() - ahi;
    c = (SPLITTER * pc.getY()); abig = (c - pc.getY());
    bhi = c - abig; blo = pc.getY() - bhi; err1 = axcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axcy0 = (alo * blo) - err3;
    _i = (axby0 - axcy0); bvirt = (axby0 - _i);
    avirt = _i + bvirt; bround = bvirt - axcy0; around = axby0 - avirt;
    aterms[0] = around + bround; _j = (axby1 + _i);
    bvirt = (_j - axby1); avirt = _j - bvirt;
    bround = _i - bvirt; around = axby1 - avirt;
    _0 = around + bround; _i = (_0 - axcy1);
    bvirt = (_0 - _i); avirt = _i + bvirt;
    bround = bvirt - axcy1; around = _0 - avirt;
    aterms[1] = around + bround; aterms3 = (_j + _i);
    bvirt = (aterms3 - _j); avirt = aterms3 - bvirt;
    bround = _i - bvirt; around = _j - avirt;
    aterms[2] = around + bround;
    aterms[3] = aterms3;

    bxcy1 = (pb.getX() * pc.getY()); c = (SPLITTER * pb.getX());
    abig = (c - pb.getX()); ahi = c - abig; alo = pb.getX() - ahi;
    c = (SPLITTER * pc.getY()); abig = (c - pc.getY());
    bhi = c - abig; blo = pc.getY() - bhi; err1 = bxcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxcy0 = (alo * blo) - err3;
    bxay1 = (pb.getX() * pa.getY()); c = (SPLITTER * pb.getX());
    abig = (c - pb.getX()); ahi = c - abig; alo = pb.getX() - ahi;
    c = (SPLITTER * pa.getY()); abig = (c - pa.getY());
    bhi = c - abig; blo = pa.getY() - bhi; err1 = bxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxay0 = (alo * blo) - err3;
    _i = (bxcy0 - bxay0); bvirt = (bxcy0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxay0; around = bxcy0 - avirt;
    bterms[0] = around + bround; _j = (bxcy1 + _i);
    bvirt = (_j - bxcy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = bxcy1 - avirt; _0 = around + bround;
    _i = (_0 - bxay1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxay1; around = _0 - avirt;
    bterms[1] = around + bround; bterms3 = (_j + _i);
    bvirt = (bterms3 - _j); avirt = bterms3 - bvirt;
    bround = _i - bvirt; around = _j - avirt;
    bterms[2] = around + bround;
    bterms[3] = bterms3;

    cxay1 = (pc.getX() * pa.getY()); c = (SPLITTER * pc.getX());
    abig = (c - pc.getX()); ahi = c - abig; alo = pc.getX() - ahi;
    c = (SPLITTER * pa.getY()); abig = (c - pa.getY());
    bhi = c - abig; blo = pa.getY() - bhi; err1 = cxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxay0 = (alo * blo) - err3;
    cxby1 = (pc.getX() * pb.getY()); c = (SPLITTER * pc.getX());
    abig = (c - pc.getX()); ahi = c - abig; alo = pc.getX() - ahi;
    c = (SPLITTER * pb.getY()); abig = (c - pb.getY());
    bhi = c - abig; blo = pb.getY() - bhi; err1 = cxby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxby0 = (alo * blo) - err3;
    _i = (cxay0 - cxby0); bvirt = (cxay0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxby0; around = cxay0 - avirt;
    cterms[0] = around + bround; _j = (cxay1 + _i);
    bvirt = (_j - cxay1); avirt = _j - bvirt;
    bround = _i - bvirt; around = cxay1 - avirt; _0 = around + bround;
    _i = (_0 - cxby1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxby1; around = _0 - avirt;
    cterms[1] = around + bround; cterms3 = (_j + _i);
    bvirt = (cterms3 - _j); avirt = cterms3 - bvirt;
    bround = _i - bvirt; around = _j - avirt;
    cterms[2] = around + bround;
    cterms[3] = cterms3;

    vlength = fast_expansion_sum_zeroelim(4, aterms, 4, bterms, v);
    fast_expansion_sum_zeroelim(vlength, v, 4, cterms, w);

    // TODO: doubleValue direct from double[]
    final DoubleArrayList terms = DoubleArrayList.from(w);
    XDouble.unsafeCompress(terms);
    return XDouble.unsafe(terms).doubleValue(); }

  public final boolean isOrientationRobust () { return true; }

  //--------------------------------------------------------------------

  public final boolean inCircleDistanceExact () { return true; }

  public final double inCircleDistance (final Vector2D pd) {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();

    double axby1, bxcy1, cxdy1, dxay1, axcy1, bxdy1;
    double bxay1, cxby1, dxcy1, axdy1, cxay1, dxby1;
    double axby0, bxcy0, cxdy0, dxay0, axcy0, bxdy0;
    double bxay0, cxby0, dxcy0, axdy0, cxay0, dxby0;
    double[] ab = new double[4], bc = new double[4],
      cd = new double[4], da = new double[4],
      ac = new double[4], bd = new double[4];
    double[] temp8 = new double[8];
    int templen;
    double[] abc = new double[12],
      bcd = new double[12],
      cda = new double[12],
      dab = new double[12];
    int abclen, bcdlen, cdalen, dablen;
    double[] det24x = new double[24],
      det24y = new double[24],
      det48x = new double[48],
      det48y = new double[48];
    int xlen, ylen;
    double[] adet = new double[96],
      bdet = new double[96],
      cdet = new double[96],
      ddet = new double[96];
    int alen, blen, clen, dlen;
    double[] abdet = new double[192], cddet = new double[192];
    int ablen, cdlen;
    double[] deter = new double[384];
    int i;

    double bvirt;
    double avirt, bround, around;
    double c;
    double abig;
    double ahi, alo, bhi, blo;
    double err1, err2, err3;
    double _i, _j;
    double _0;

    axby1 = (pa.getX() * pb.getY()); c = (SPLITTER * pa.getX());
    abig = (c - pa.getX()); ahi = c - abig; alo = pa.getX() - ahi;
    c = (SPLITTER * pb.getY()); abig = (c - pb.getY()); bhi = c - abig;
    blo = pb.getY() - bhi; err1 = axby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axby0 = (alo * blo) - err3;
    bxay1 = (pb.getX() * pa.getY()); c = (SPLITTER * pb.getX());
    abig = (c - pb.getX()); ahi = c - abig; alo = pb.getX() - ahi;
    c = (SPLITTER * pa.getY()); abig = (c - pa.getY()); bhi = c - abig;
    blo = pa.getY() - bhi; err1 = bxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxay0 = (alo * blo) - err3;
    _i = (axby0 - bxay0); bvirt = (axby0 - _i); avirt = _i + bvirt;
    bround = bvirt - bxay0; around = axby0 - avirt;
    ab[0] = around + bround; _j = (axby1 + _i); bvirt = (_j - axby1);
    avirt = _j - bvirt; bround = _i - bvirt; around = axby1 - avirt;
    _0 = around + bround; _i = (_0 - bxay1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxay1; around = _0 - avirt;
    ab[1] = around + bround; ab[3] = (_j + _i);
    bvirt = (ab[3] - _j); avirt = ab[3] - bvirt; bround = _i - bvirt;
    around = _j - avirt; ab[2] = around + bround;

    bxcy1 = (pb.getX() * pc.getY()); c = (SPLITTER * pb.getX());
    abig = (c - pb.getX()); ahi = c - abig; alo = pb.getX() - ahi;
    c = (SPLITTER * pc.getY()); abig = (c - pc.getY()); bhi = c - abig;
    blo = pc.getY() - bhi; err1 = bxcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxcy0 = (alo * blo) - err3;
    cxby1 = (pc.getX() * pb.getY()); c = (SPLITTER * pc.getX());
    abig = (c - pc.getX()); ahi = c - abig; alo = pc.getX() - ahi;
    c = (SPLITTER * pb.getY()); abig = (c - pb.getY()); bhi = c - abig;
    blo = pb.getY() - bhi; err1 = cxby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxby0 = (alo * blo) - err3;
    _i = (bxcy0 - cxby0); bvirt = (bxcy0 - _i); avirt = _i + bvirt;
    bround = bvirt - cxby0; around = bxcy0 - avirt;
    bc[0] = around + bround; _j = (bxcy1 + _i);
    bvirt = (_j - bxcy1); avirt = _j - bvirt; bround = _i - bvirt;
    around = bxcy1 - avirt; _0 = around + bround; _i = (_0 - cxby1);
    bvirt = (_0 - _i); avirt = _i + bvirt; bround = bvirt - cxby1;
    around = _0 - avirt; bc[1] = around + bround; bc[3] = (_j + _i);
    bvirt = (bc[3] - _j); avirt = bc[3] - bvirt; bround = _i - bvirt;
    around = _j - avirt; bc[2] = around + bround;

    cxdy1 = (pc.getX() * pd.getY());c = (SPLITTER * pc.getX());
    abig = (c - pc.getX()); ahi = c - abig; alo = pc.getX() - ahi;
    c = (SPLITTER * pd.getY());abig = (c - pd.getY());bhi = c - abig;
    blo = pd.getY() - bhi;err1 = cxdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxdy0 = (alo * blo) - err3;
    dxcy1 = (pd.getX() * pc.getY());c = (SPLITTER * pd.getX());
    abig = (c - pd.getX());ahi = c - abig;alo = pd.getX() - ahi;
    c = (SPLITTER * pc.getY()); abig = (c - pc.getY()); bhi = c - abig;
    blo = pc.getY() - bhi; err1 = dxcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxcy0 = (alo * blo) - err3;
    _i = (cxdy0 - dxcy0); bvirt = (cxdy0 - _i); avirt = _i + bvirt;
    bround = bvirt - dxcy0; around = cxdy0 - avirt;
    cd[0] = around + bround; _j = (cxdy1 + _i);
    bvirt = (_j - cxdy1); avirt = _j - bvirt; bround = _i - bvirt;
    around = cxdy1 - avirt; _0 = around + bround; _i = (_0 - dxcy1);
    bvirt = (_0 - _i); avirt = _i + bvirt; bround = bvirt - dxcy1;
    around = _0 - avirt; cd[1] = around + bround; cd[3] = (_j + _i);
    bvirt = (cd[3] - _j); avirt = cd[3] - bvirt; bround = _i - bvirt;
    around = _j - avirt; cd[2] = around + bround;

    dxay1 = (pd.getX() * pa.getY());c = (SPLITTER * pd.getX());
    abig = (c - pd.getX());ahi = c - abig;alo = pd.getX() - ahi;
    c = (SPLITTER * pa.getY()); abig = (c - pa.getY()); bhi = c - abig;
    blo = pa.getY() - bhi; err1 = dxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxay0 = (alo * blo) - err3;
    axdy1 = (pa.getX() * pd.getY());c = (SPLITTER * pa.getX());
    abig = (c - pa.getX()); ahi = c - abig; alo = pa.getX() - ahi;
    c = (SPLITTER * pd.getY());abig = (c - pd.getY());bhi = c - abig;
    blo = pd.getY() - bhi;err1 = axdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axdy0 = (alo * blo) - err3;
    _i = (dxay0 - axdy0); bvirt = (dxay0 - _i); avirt = _i + bvirt;
    bround = bvirt - axdy0; around = dxay0 - avirt;
    da[0] = around + bround; _j = (dxay1 + _i);
    bvirt = (_j - dxay1); avirt = _j - bvirt; bround = _i - bvirt;
    around = dxay1 - avirt; _0 = around + bround; _i = (_0 - axdy1);
    bvirt = (_0 - _i); avirt = _i + bvirt; bround = bvirt - axdy1;
    around = _0 - avirt; da[1] = around + bround; da[3] = (_j + _i);
    bvirt = (da[3] - _j); avirt = da[3] - bvirt; bround = _i - bvirt;
    around = _j - avirt; da[2] = around + bround;

    axcy1 = (pa.getX() * pc.getY()); c = (SPLITTER * pa.getX());
    abig = (c - pa.getX()); ahi = c - abig; alo = pa.getX() - ahi;
    c = (SPLITTER * pc.getY()); abig = (c - pc.getY()); bhi = c - abig;
    blo = pc.getY() - bhi; err1 = axcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axcy0 = (alo * blo) - err3;
    cxay1 = (pc.getX() * pa.getY()); c = (SPLITTER * pc.getX());
    abig = (c - pc.getX()); ahi = c - abig; alo = pc.getX() - ahi;
    c = (SPLITTER * pa.getY()); abig = (c - pa.getY()); bhi = c - abig;
    blo = pa.getY() - bhi; err1 = cxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxay0 = (alo * blo) - err3;
    _i = (axcy0 - cxay0); bvirt = (axcy0 - _i); avirt = _i + bvirt;
    bround = bvirt - cxay0; around = axcy0 - avirt;
    ac[0] = around + bround; _j = (axcy1 + _i);
    bvirt = (_j - axcy1); avirt = _j - bvirt; bround = _i - bvirt;
    around = axcy1 - avirt; _0 = around + bround; _i = (_0 - cxay1);
    bvirt = (_0 - _i); avirt = _i + bvirt; bround = bvirt - cxay1;
    around = _0 - avirt; ac[1] = around + bround; ac[3] = (_j + _i);
    bvirt = (ac[3] - _j); avirt = ac[3] - bvirt; bround = _i - bvirt;
    around = _j - avirt; ac[2] = around + bround;

    bxdy1 = (pb.getX() * pd.getY());c = (SPLITTER * pb.getX());
    abig = (c - pb.getX()); ahi = c - abig; alo = pb.getX() - ahi;
    c = (SPLITTER * pd.getY());abig = (c - pd.getY());bhi = c - abig;
    blo = pd.getY() - bhi;err1 = bxdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxdy0 = (alo * blo) - err3;
    dxby1 = (pd.getX() * pb.getY());c = (SPLITTER * pd.getX());
    abig = (c - pd.getX());ahi = c - abig;alo = pd.getX() - ahi;
    c = (SPLITTER * pb.getY()); abig = (c - pb.getY()); bhi = c - abig;
    blo = pb.getY() - bhi; err1 = dxby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxby0 = (alo * blo) - err3;
    _i = (bxdy0 - dxby0); bvirt = (bxdy0 - _i); avirt = _i + bvirt;
    bround = bvirt - dxby0; around = bxdy0 - avirt;
    bd[0] = around + bround; _j = (bxdy1 + _i);
    bvirt = (_j - bxdy1); avirt = _j - bvirt; bround = _i - bvirt;
    around = bxdy1 - avirt; _0 = around + bround; _i = (_0 - dxby1);
    bvirt = (_0 - _i); avirt = _i + bvirt; bround = bvirt - dxby1;
    around = _0 - avirt; bd[1] = around + bround; bd[3] = (_j + _i);
    bvirt = (bd[3] - _j); avirt = bd[3] - bvirt; bround = _i - bvirt;
    around = _j - avirt; bd[2] = around + bround;

    templen = fast_expansion_sum_zeroelim(4, cd, 4, da, temp8);
    cdalen = fast_expansion_sum_zeroelim(templen, temp8, 4, ac, cda);
    templen = fast_expansion_sum_zeroelim(4, da, 4, ab, temp8);
    dablen = fast_expansion_sum_zeroelim(templen, temp8, 4, bd, dab);
    for (i = 0; i < bd.length; i++) { bd[i] = -bd[i]; }
    for (i = 0; i < ac.length; i++) { ac[i] = -ac[i]; }
    templen = fast_expansion_sum_zeroelim(4, ab, 4, bc, temp8);
    abclen = fast_expansion_sum_zeroelim(templen, temp8, 4, ac, abc);
    templen = fast_expansion_sum_zeroelim(4, bc, 4, cd, temp8);
    bcdlen = fast_expansion_sum_zeroelim(templen, temp8, 4, bd, bcd);

    xlen = scale_expansion_zeroelim(bcdlen, bcd, pa.getX(), det24x);
    xlen = scale_expansion_zeroelim(xlen, det24x, pa.getX(), det48x);
    ylen = scale_expansion_zeroelim(bcdlen, bcd, pa.getY(), det24y);
    ylen = scale_expansion_zeroelim(ylen, det24y, pa.getY(), det48y);
    alen = fast_expansion_sum_zeroelim(xlen, det48x, ylen, det48y, adet);

    xlen = scale_expansion_zeroelim(cdalen, cda, pb.getX(), det24x);
    xlen = scale_expansion_zeroelim(xlen, det24x, -pb.getX(), det48x);
    ylen = scale_expansion_zeroelim(cdalen, cda, pb.getY(), det24y);
    ylen = scale_expansion_zeroelim(ylen, det24y, -pb.getY(), det48y);
    blen = fast_expansion_sum_zeroelim(xlen, det48x, ylen, det48y, bdet);

    xlen = scale_expansion_zeroelim(dablen, dab, pc.getX(), det24x);
    xlen = scale_expansion_zeroelim(xlen, det24x, pc.getX(), det48x);
    ylen = scale_expansion_zeroelim(dablen, dab, pc.getY(), det24y);
    ylen = scale_expansion_zeroelim(ylen, det24y, pc.getY(), det48y);
    clen = fast_expansion_sum_zeroelim(xlen, det48x, ylen, det48y, cdet);

    xlen = scale_expansion_zeroelim(abclen, abc, pd.getX(), det24x);
    xlen = scale_expansion_zeroelim(xlen, det24x, -pd.getX(), det48x);
    ylen = scale_expansion_zeroelim(abclen, abc, pd.getY(), det24y);
    ylen = scale_expansion_zeroelim(ylen, det24y, -pd.getY(), det48y);
    dlen = fast_expansion_sum_zeroelim(xlen, det48x, ylen, det48y, ddet);

    ablen = fast_expansion_sum_zeroelim(alen, adet, blen, bdet, abdet);
    cdlen = fast_expansion_sum_zeroelim(clen, cdet, dlen, ddet, cddet);

    // This doesn't round to double correctly!
    // int deterlen = fast_expansion_sum_zeroelim(ablen, abdet, cdlen, cddet, deter);
    // return deter[deterlen - 1];
    fast_expansion_sum_zeroelim(ablen, abdet, cdlen, cddet, deter);
    // TODO: doubleValue direct from double[]
    final DoubleArrayList terms = DoubleArrayList.from(deter);
    XDouble.unsafeCompress(terms);
    return XDouble.unsafe(terms).doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private ExactMacro (final Vector2D a,
                      final Vector2D b,
                      final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new ExactMacro(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
