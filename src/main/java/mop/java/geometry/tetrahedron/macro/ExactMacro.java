package mop.java.geometry.tetrahedron.macro;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.geometry.tetrahedron.Tetrahedron3D;
import mop.java.numbers.XDouble;
import org.apache.commons.geometry.euclidean.threed.Vector3D;

import static mop.java.geometry.Expansion.SPLITTER;
import static mop.java.geometry.Expansion.fast_expansion_sum_zeroelim;
import static mop.java.geometry.Expansion.scale_expansion_zeroelim;

/** Exact tests.  Robust.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-07
 */

// strictfp unnecessary for JDK17 and later
public final class ExactMacro extends Tetrahedron3D {

  //--------------------------------------------------------------------

  public final boolean signedVolumeExact () { return true; }

  public final double signedVolume () {
    final Vector3D pa = getP0();
    final Vector3D pb = getP1();
    final Vector3D pc = getP2();
    final Vector3D pd = getP3();

    double axby1, bxcy1, cxdy1, dxay1, axcy1, bxdy1;
    double bxay1, cxby1, dxcy1, axdy1, cxay1, dxby1;
    double axby0, bxcy0, cxdy0, dxay0, axcy0, bxdy0;
    double bxay0, cxby0, dxcy0, axdy0, cxay0, dxby0;
    double[] ab = new double[4],
      bc = new double[4], cd = new double[4],
      da = new double[4], ac = new double[4],
      bd = new double[4];
    double[] temp8 = new double[8];
    int templen;
    double[] abc = new double[12], bcd = new double[12],
      cda = new double[12], dab = new double[12];
    int abclen, bcdlen, cdalen, dablen;
    double[] adet = new double[24], bdet = new double[24],
      cdet = new double[24], ddet = new double[24];
    int alen, blen, clen, dlen;
    double[] abdet = new double[48], cddet = new double[48];
    int ablen, cdlen;
    double[] deter = new double[96];
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
    c = (SPLITTER * pb.getY()); abig = (c - pb.getY());
    bhi = c - abig; blo = pb.getY() - bhi; err1 = axby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axby0 = (alo * blo) - err3;
    bxay1 = (pb.getX() * pa.getY()); c = (SPLITTER * pb.getX());
    abig = (c - pb.getX()); ahi = c - abig; alo = pb.getX() - ahi;
    c = (SPLITTER * pa.getY()); abig = (c - pa.getY());
    bhi = c - abig; blo = pa.getY() - bhi; err1 = bxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxay0 = (alo * blo) - err3;
    _i = (axby0 - bxay0); bvirt = (axby0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxay0; around = axby0 - avirt;
    ab[0] = around + bround; _j = (axby1 + _i);
    bvirt = (_j - axby1); avirt = _j - bvirt;
    bround = _i - bvirt; around = axby1 - avirt; _0 = around + bround;
    _i = (_0 - bxay1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxay1; around = _0 - avirt;
    ab[1] = around + bround; ab[3] = (_j + _i);
    bvirt = (ab[3] - _j); avirt = ab[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; ab[2] = around + bround;

    bxcy1 = (pb.getX() * pc.getY()); c = (SPLITTER * pb.getX());
    abig = (c - pb.getX()); ahi = c - abig; alo = pb.getX() - ahi;
    c = (SPLITTER * pc.getY()); abig = (c - pc.getY());
    bhi = c - abig; blo = pc.getY() - bhi; err1 = bxcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxcy0 = (alo * blo) - err3;
    cxby1 = (pc.getX() * pb.getY()); c = (SPLITTER * pc.getX());
    abig = (c - pc.getX()); ahi = c - abig; alo = pc.getX() - ahi;
    c = (SPLITTER * pb.getY()); abig = (c - pb.getY());
    bhi = c - abig; blo = pb.getY() - bhi; err1 = cxby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxby0 = (alo * blo) - err3;
    _i = (bxcy0 - cxby0); bvirt = (bxcy0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxby0; around = bxcy0 - avirt;
    bc[0] = around + bround; _j = (bxcy1 + _i);
    bvirt = (_j - bxcy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = bxcy1 - avirt; _0 = around + bround;
    _i = (_0 - cxby1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxby1; around = _0 - avirt;
    bc[1] = around + bround; bc[3] = (_j + _i);
    bvirt = (bc[3] - _j); avirt = bc[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; bc[2] = around + bround;

    cxdy1 = (pc.getX() * pd.getY()); c = (SPLITTER * pc.getX());
    abig = (c - pc.getX()); ahi = c - abig; alo = pc.getX() - ahi;
    c = (SPLITTER * pd.getY()); abig = (c - pd.getY());
    bhi = c - abig; blo = pd.getY() - bhi; err1 = cxdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxdy0 = (alo * blo) - err3;
    dxcy1 = (pd.getX() * pc.getY()); c = (SPLITTER * pd.getX());
    abig = (c - pd.getX()); ahi = c - abig; alo = pd.getX() - ahi;
    c = (SPLITTER * pc.getY()); abig = (c - pc.getY());
    bhi = c - abig; blo = pc.getY() - bhi; err1 = dxcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxcy0 = (alo * blo) - err3;
    _i = (cxdy0 - dxcy0); bvirt = (cxdy0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxcy0; around = cxdy0 - avirt;
    cd[0] = around + bround; _j = (cxdy1 + _i);
    bvirt = (_j - cxdy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = cxdy1 - avirt; _0 = around + bround;
    _i = (_0 - dxcy1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxcy1; around = _0 - avirt;
    cd[1] = around + bround; cd[3] = (_j + _i);
    bvirt = (cd[3] - _j); avirt = cd[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; cd[2] = around + bround;

    dxay1 = (pd.getX() * pa.getY()); c = (SPLITTER * pd.getX());
    abig = (c - pd.getX()); ahi = c - abig; alo = pd.getX() - ahi;
    c = (SPLITTER * pa.getY()); abig = (c - pa.getY());
    bhi = c - abig; blo = pa.getY() - bhi; err1 = dxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxay0 = (alo * blo) - err3;
    axdy1 = (pa.getX() * pd.getY()); c = (SPLITTER * pa.getX());
    abig = (c - pa.getX()); ahi = c - abig; alo = pa.getX() - ahi;
    c = (SPLITTER * pd.getY()); abig = (c - pd.getY());
    bhi = c - abig; blo = pd.getY() - bhi; err1 = axdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axdy0 = (alo * blo) - err3;
    _i = (dxay0 - axdy0); bvirt = (dxay0 - _i);
    avirt = _i + bvirt; bround = bvirt - axdy0; around = dxay0 - avirt;
    da[0] = around + bround; _j = (dxay1 + _i);
    bvirt = (_j - dxay1); avirt = _j - bvirt;
    bround = _i - bvirt; around = dxay1 - avirt; _0 = around + bround;
    _i = (_0 - axdy1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - axdy1; around = _0 - avirt;
    da[1] = around + bround; da[3] = (_j + _i);
    bvirt = (da[3] - _j); avirt = da[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; da[2] = around + bround;

    axcy1 = (pa.getX() * pc.getY()); c = (SPLITTER * pa.getX());
    abig = (c - pa.getX()); ahi = c - abig; alo = pa.getX() - ahi;
    c = (SPLITTER * pc.getY()); abig = (c - pc.getY());
    bhi = c - abig; blo = pc.getY() - bhi; err1 = axcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axcy0 = (alo * blo) - err3;
    cxay1 = (pc.getX() * pa.getY()); c = (SPLITTER * pc.getX());
    abig = (c - pc.getX()); ahi = c - abig; alo = pc.getX() - ahi;
    c = (SPLITTER * pa.getY()); abig = (c - pa.getY());
    bhi = c - abig; blo = pa.getY() - bhi; err1 = cxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxay0 = (alo * blo) - err3;
    _i = (axcy0 - cxay0); bvirt = (axcy0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxay0; around = axcy0 - avirt;
    ac[0] = around + bround; _j = (axcy1 + _i);
    bvirt = (_j - axcy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = axcy1 - avirt; _0 = around + bround;
    _i = (_0 - cxay1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxay1; around = _0 - avirt;
    ac[1] = around + bround; ac[3] = (_j + _i);
    bvirt = (ac[3] - _j); avirt = ac[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; ac[2] = around + bround;

    bxdy1 = (pb.getX() * pd.getY()); c = (SPLITTER * pb.getX());
    abig = (c - pb.getX()); ahi = c - abig; alo = pb.getX() - ahi;
    c = (SPLITTER * pd.getY()); abig = (c - pd.getY());
    bhi = c - abig; blo = pd.getY() - bhi; err1 = bxdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxdy0 = (alo * blo) - err3;
    dxby1 = (pd.getX() * pb.getY()); c = (SPLITTER * pd.getX());
    abig = (c - pd.getX()); ahi = c - abig; alo = pd.getX() - ahi;
    c = (SPLITTER * pb.getY()); abig = (c - pb.getY());
    bhi = c - abig; blo = pb.getY() - bhi; err1 = dxby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxby0 = (alo * blo) - err3;
    _i = (bxdy0 - dxby0); bvirt = (bxdy0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxby0; around = bxdy0 - avirt;
    bd[0] = around + bround; _j = (bxdy1 + _i);
    bvirt = (_j - bxdy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = bxdy1 - avirt; _0 = around + bround;
    _i = (_0 - dxby1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxby1; around = _0 - avirt;
    bd[1] = around + bround; bd[3] = (_j + _i);
    bvirt = (bd[3] - _j); avirt = bd[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; bd[2] = around + bround;

    templen = fast_expansion_sum_zeroelim(4, cd, 4, da, temp8);
    cdalen = fast_expansion_sum_zeroelim(templen, temp8, 4, ac, cda);
    templen = fast_expansion_sum_zeroelim(4, da, 4, ab, temp8);
    dablen = fast_expansion_sum_zeroelim(templen, temp8, 4, bd, dab);
    for (i = 0; i < 4; i++) {
      bd[i] = -bd[i];
      ac[i] = -ac[i];
    }
    templen = fast_expansion_sum_zeroelim(4, ab, 4, bc, temp8);
    abclen = fast_expansion_sum_zeroelim(templen, temp8, 4, ac, abc);
    templen = fast_expansion_sum_zeroelim(4, bc, 4, cd, temp8);
    bcdlen = fast_expansion_sum_zeroelim(templen, temp8, 4, bd, bcd);

    alen = scale_expansion_zeroelim(bcdlen, bcd, pa.getZ(), adet);
    blen = scale_expansion_zeroelim(cdalen, cda, -pb.getZ(), bdet);
    clen = scale_expansion_zeroelim(dablen, dab, pc.getZ(), cdet);
    dlen = scale_expansion_zeroelim(abclen, abc, -pd.getZ(), ddet);

    ablen = fast_expansion_sum_zeroelim(alen, adet, blen, bdet, abdet);
    cdlen = fast_expansion_sum_zeroelim(clen, cdet, dlen, ddet, cddet);
    fast_expansion_sum_zeroelim(ablen, abdet, cdlen, cddet, deter);

    return XDouble.unsafe(deter).doubleValue();
  }

  //--------------------------------------------------------------------
  public final boolean inSphereExact () { return true; }

  public final double inSphere (final Vector3D p) {
    final Vector3D pa = getP0();
    final Vector3D pb = getP1();
    final Vector3D pc = getP2();
    final Vector3D pd = getP3();

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
    double[] temp8a = new double[8], temp8b = new double[8],
      temp16 = new double[16];
    int temp8alen, temp8blen, temp16len;
    double[] abc = new double[24], bcd = new double[24], cde =
      new double[24],
      dea = new double[24], eab = new double[24];
    double[] abd = new double[24], bce = new double[24], cda =
      new double[24],
      deb = new double[24], eac = new double[24];
    int abclen, bcdlen, cdelen, dealen, eablen;
    int abdlen, bcelen, cdalen, deblen, eaclen;
    double[] temp48a = new double[48], temp48b = new double[48];
    int temp48alen, temp48blen;
    double[] abcd = new double[96], bcde = new double[96],
      cdea = new double[96], deab = new double[96], eabc =
      new double[96];
    int abcdlen, bcdelen, cdealen, deablen, eabclen;
    double[] temp192 = new double[192];
    double[] det384x = new double[384], det384y = new double[384],
      det384z = new double[384];
    int xlen, ylen, zlen;
    double[] detxy = new double[768];
    int xylen;
    double[] adet = new double[1152], bdet = new double[1152],
      cdet = new double[1152], ddet = new double[1152],
      edet = new double[1152];
    int alen, blen, clen, dlen, elen;
    double[] abdet = new double[2304], cddet = new double[2304],
      cdedet = new double[3456];
    int ablen, cdlen;
    double[] deter = new double[5760];
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
    c = (SPLITTER * pb.getY()); abig = (c - pb.getY());
    bhi = c - abig; blo = pb.getY() - bhi; err1 = axby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axby0 = (alo * blo) - err3;
    bxay1 = (pb.getX() * pa.getY()); c = (SPLITTER * pb.getX());
    abig = (c - pb.getX()); ahi = c - abig; alo = pb.getX() - ahi;
    c = (SPLITTER * pa.getY()); abig = (c - pa.getY());
    bhi = c - abig; blo = pa.getY() - bhi; err1 = bxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxay0 = (alo * blo) - err3;
    _i = (axby0 - bxay0); bvirt = (axby0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxay0; around = axby0 - avirt;
    ab[0] = around + bround; _j = (axby1 + _i);
    bvirt = (_j - axby1); avirt = _j - bvirt;
    bround = _i - bvirt; around = axby1 - avirt; _0 = around + bround;
    _i = (_0 - bxay1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxay1; around = _0 - avirt;
    ab[1] = around + bround; ab[3] = (_j + _i);
    bvirt = (ab[3] - _j); avirt = ab[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; ab[2] = around + bround;

    bxcy1 = (pb.getX() * pc.getY()); c = (SPLITTER * pb.getX());
    abig = (c - pb.getX()); ahi = c - abig; alo = pb.getX() - ahi;
    c = (SPLITTER * pc.getY()); abig = (c - pc.getY());
    bhi = c - abig; blo = pc.getY() - bhi; err1 = bxcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxcy0 = (alo * blo) - err3;
    cxby1 = (pc.getX() * pb.getY()); c = (SPLITTER * pc.getX());
    abig = (c - pc.getX()); ahi = c - abig; alo = pc.getX() - ahi;
    c = (SPLITTER * pb.getY()); abig = (c - pb.getY());
    bhi = c - abig; blo = pb.getY() - bhi; err1 = cxby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxby0 = (alo * blo) - err3;
    _i = (bxcy0 - cxby0); bvirt = (bxcy0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxby0; around = bxcy0 - avirt;
    bc[0] = around + bround; _j = (bxcy1 + _i);
    bvirt = (_j - bxcy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = bxcy1 - avirt; _0 = around + bround;
    _i = (_0 - cxby1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxby1; around = _0 - avirt;
    bc[1] = around + bround; bc[3] = (_j + _i);
    bvirt = (bc[3] - _j); avirt = bc[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; bc[2] = around + bround;

    cxdy1 = (pc.getX() * pd.getY()); c = (SPLITTER * pc.getX());
    abig = (c - pc.getX()); ahi = c - abig; alo = pc.getX() - ahi;
    c = (SPLITTER * pd.getY()); abig = (c - pd.getY());
    bhi = c - abig; blo = pd.getY() - bhi; err1 = cxdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxdy0 = (alo * blo) - err3;
    dxcy1 = (pd.getX() * pc.getY()); c = (SPLITTER * pd.getX());
    abig = (c - pd.getX()); ahi = c - abig; alo = pd.getX() - ahi;
    c = (SPLITTER * pc.getY()); abig = (c - pc.getY());
    bhi = c - abig; blo = pc.getY() - bhi; err1 = dxcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxcy0 = (alo * blo) - err3;
    _i = (cxdy0 - dxcy0); bvirt = (cxdy0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxcy0; around = cxdy0 - avirt;
    cd[0] = around + bround; _j = (cxdy1 + _i);
    bvirt = (_j - cxdy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = cxdy1 - avirt; _0 = around + bround;
    _i = (_0 - dxcy1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxcy1; around = _0 - avirt;
    cd[1] = around + bround; cd[3] = (_j + _i);
    bvirt = (cd[3] - _j); avirt = cd[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; cd[2] = around + bround;

    dxey1 = (pd.getX() * p.getY());c = (SPLITTER * pd.getX());
    abig = (c - pd.getX()); ahi = c - abig; alo = pd.getX() - ahi;
    c = (SPLITTER * p.getY());abig = (c - p.getY());
    bhi = c - abig; blo = p.getY() - bhi;err1 = dxey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxey0 = (alo * blo) - err3;
    exdy1 = (p.getX() * pd.getY());c = (SPLITTER * p.getX());
    abig = (c - p.getX());ahi = c - abig;alo = p.getX() - ahi;
    c = (SPLITTER * pd.getY()); abig = (c - pd.getY());
    bhi = c - abig; blo = pd.getY() - bhi; err1 = exdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    exdy0 = (alo * blo) - err3;
    _i = (dxey0 - exdy0); bvirt = (dxey0 - _i);
    avirt = _i + bvirt; bround = bvirt - exdy0; around = dxey0 - avirt;
    de[0] = around + bround; _j = (dxey1 + _i);
    bvirt = (_j - dxey1); avirt = _j - bvirt;
    bround = _i - bvirt; around = dxey1 - avirt; _0 = around + bround;
    _i = (_0 - exdy1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - exdy1; around = _0 - avirt;
    de[1] = around + bround; de[3] = (_j + _i);
    bvirt = (de[3] - _j); avirt = de[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; de[2] = around + bround;

    exay1 = (p.getX() * pa.getY());c = (SPLITTER * p.getX());
    abig = (c - p.getX());ahi = c - abig;alo = p.getX() - ahi;
    c = (SPLITTER * pa.getY()); abig = (c - pa.getY());
    bhi = c - abig; blo = pa.getY() - bhi; err1 = exay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    exay0 = (alo * blo) - err3;
    axey1 = (pa.getX() * p.getY());c = (SPLITTER * pa.getX());
    abig = (c - pa.getX()); ahi = c - abig; alo = pa.getX() - ahi;
    c = (SPLITTER * p.getY());abig = (c - p.getY());
    bhi = c - abig; blo = p.getY() - bhi;err1 = axey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axey0 = (alo * blo) - err3;
    _i = (exay0 - axey0); bvirt = (exay0 - _i);
    avirt = _i + bvirt; bround = bvirt - axey0; around = exay0 - avirt;
    ea[0] = around + bround; _j = (exay1 + _i);
    bvirt = (_j - exay1); avirt = _j - bvirt;
    bround = _i - bvirt; around = exay1 - avirt; _0 = around + bround;
    _i = (_0 - axey1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - axey1; around = _0 - avirt;
    ea[1] = around + bround; ea[3] = (_j + _i);
    bvirt = (ea[3] - _j); avirt = ea[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; ea[2] = around + bround;

    axcy1 = (pa.getX() * pc.getY()); c = (SPLITTER * pa.getX());
    abig = (c - pa.getX()); ahi = c - abig; alo = pa.getX() - ahi;
    c = (SPLITTER * pc.getY()); abig = (c - pc.getY());
    bhi = c - abig; blo = pc.getY() - bhi; err1 = axcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axcy0 = (alo * blo) - err3;
    cxay1 = (pc.getX() * pa.getY()); c = (SPLITTER * pc.getX());
    abig = (c - pc.getX()); ahi = c - abig; alo = pc.getX() - ahi;
    c = (SPLITTER * pa.getY()); abig = (c - pa.getY());
    bhi = c - abig; blo = pa.getY() - bhi; err1 = cxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxay0 = (alo * blo) - err3;
    _i = (axcy0 - cxay0); bvirt = (axcy0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxay0; around = axcy0 - avirt;
    ac[0] = around + bround; _j = (axcy1 + _i);
    bvirt = (_j - axcy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = axcy1 - avirt; _0 = around + bround;
    _i = (_0 - cxay1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxay1; around = _0 - avirt;
    ac[1] = around + bround; ac[3] = (_j + _i);
    bvirt = (ac[3] - _j); avirt = ac[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; ac[2] = around + bround;

    bxdy1 = (pb.getX() * pd.getY()); c = (SPLITTER * pb.getX());
    abig = (c - pb.getX()); ahi = c - abig; alo = pb.getX() - ahi;
    c = (SPLITTER * pd.getY()); abig = (c - pd.getY());
    bhi = c - abig; blo = pd.getY() - bhi; err1 = bxdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxdy0 = (alo * blo) - err3;
    dxby1 = (pd.getX() * pb.getY()); c = (SPLITTER * pd.getX());
    abig = (c - pd.getX()); ahi = c - abig; alo = pd.getX() - ahi;
    c = (SPLITTER * pb.getY()); abig = (c - pb.getY());
    bhi = c - abig; blo = pb.getY() - bhi; err1 = dxby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxby0 = (alo * blo) - err3;
    _i = (bxdy0 - dxby0); bvirt = (bxdy0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxby0; around = bxdy0 - avirt;
    bd[0] = around + bround; _j = (bxdy1 + _i);
    bvirt = (_j - bxdy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = bxdy1 - avirt; _0 = around + bround;
    _i = (_0 - dxby1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxby1; around = _0 - avirt;
    bd[1] = around + bround; bd[3] = (_j + _i);
    bvirt = (bd[3] - _j); avirt = bd[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; bd[2] = around + bround;

    cxey1 = (pc.getX() * p.getY());c = (SPLITTER * pc.getX());
    abig = (c - pc.getX()); ahi = c - abig; alo = pc.getX() - ahi;
    c = (SPLITTER * p.getY());abig = (c - p.getY());
    bhi = c - abig; blo = p.getY() - bhi;err1 = cxey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxey0 = (alo * blo) - err3;
    excy1 = (p.getX() * pc.getY());c = (SPLITTER * p.getX());
    abig = (c - p.getX());ahi = c - abig;alo = p.getX() - ahi;
    c = (SPLITTER * pc.getY()); abig = (c - pc.getY());
    bhi = c - abig; blo = pc.getY() - bhi; err1 = excy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    excy0 = (alo * blo) - err3;
    _i = (cxey0 - excy0); bvirt = (cxey0 - _i);
    avirt = _i + bvirt; bround = bvirt - excy0; around = cxey0 - avirt;
    ce[0] = around + bround; _j = (cxey1 + _i);
    bvirt = (_j - cxey1); avirt = _j - bvirt;
    bround = _i - bvirt; around = cxey1 - avirt; _0 = around + bround;
    _i = (_0 - excy1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - excy1; around = _0 - avirt;
    ce[1] = around + bround; ce[3] = (_j + _i);
    bvirt = (ce[3] - _j); avirt = ce[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; ce[2] = around + bround;

    dxay1 = (pd.getX() * pa.getY()); c = (SPLITTER * pd.getX());
    abig = (c - pd.getX()); ahi = c - abig; alo = pd.getX() - ahi;
    c = (SPLITTER * pa.getY()); abig = (c - pa.getY());
    bhi = c - abig; blo = pa.getY() - bhi; err1 = dxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxay0 = (alo * blo) - err3;
    axdy1 = (pa.getX() * pd.getY()); c = (SPLITTER * pa.getX());
    abig = (c - pa.getX()); ahi = c - abig; alo = pa.getX() - ahi;
    c = (SPLITTER * pd.getY()); abig = (c - pd.getY());
    bhi = c - abig; blo = pd.getY() - bhi; err1 = axdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axdy0 = (alo * blo) - err3;
    _i = (dxay0 - axdy0); bvirt = (dxay0 - _i);
    avirt = _i + bvirt; bround = bvirt - axdy0; around = dxay0 - avirt;
    da[0] = around + bround; _j = (dxay1 + _i);
    bvirt = (_j - dxay1); avirt = _j - bvirt;
    bround = _i - bvirt; around = dxay1 - avirt; _0 = around + bround;
    _i = (_0 - axdy1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - axdy1; around = _0 - avirt;
    da[1] = around + bround; da[3] = (_j + _i);
    bvirt = (da[3] - _j); avirt = da[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; da[2] = around + bround;

    exby1 = (p.getX() * pb.getY());c = (SPLITTER * p.getX());
    abig = (c - p.getX());ahi = c - abig;alo = p.getX() - ahi;
    c = (SPLITTER * pb.getY()); abig = (c - pb.getY());
    bhi = c - abig; blo = pb.getY() - bhi; err1 = exby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    exby0 = (alo * blo) - err3;
    bxey1 = (pb.getX() * p.getY());c = (SPLITTER * pb.getX());
    abig = (c - pb.getX()); ahi = c - abig; alo = pb.getX() - ahi;
    c = (SPLITTER * p.getY());abig = (c - p.getY());
    bhi = c - abig; blo = p.getY() - bhi;err1 = bxey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxey0 = (alo * blo) - err3;
    _i = (exby0 - bxey0); bvirt = (exby0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxey0; around = exby0 - avirt;
    eb[0] = around + bround; _j = (exby1 + _i);
    bvirt = (_j - exby1); avirt = _j - bvirt;
    bround = _i - bvirt; around = exby1 - avirt; _0 = around + bround;
    _i = (_0 - bxey1); bvirt = (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxey1; around = _0 - avirt;
    eb[1] = around + bround; eb[3] = (_j + _i);
    bvirt = (eb[3] - _j); avirt = eb[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; eb[2] = around + bround;

    temp8alen = scale_expansion_zeroelim(4, bc, pa.getZ(), temp8a);
    temp8blen = scale_expansion_zeroelim(4, ac, -pb.getZ(), temp8b);
    temp16len =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
                                  temp16);
    temp8alen = scale_expansion_zeroelim(4, ab, pc.getZ(), temp8a);
    abclen =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
                                  abc);

    temp8alen = scale_expansion_zeroelim(4, cd, pb.getZ(), temp8a);
    temp8blen = scale_expansion_zeroelim(4, bd, -pc.getZ(), temp8b);
    temp16len =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
                                  temp16);
    temp8alen = scale_expansion_zeroelim(4, bc, pd.getZ(), temp8a);
    bcdlen =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
                                  bcd);

    temp8alen = scale_expansion_zeroelim(4, de, pc.getZ(), temp8a);
    temp8blen = scale_expansion_zeroelim(4, ce, -pd.getZ(), temp8b);
    temp16len =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
                                  temp16);
    temp8alen = scale_expansion_zeroelim(4, cd, p.getZ(), temp8a);
    cdelen =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
                                  cde);

    temp8alen = scale_expansion_zeroelim(4, ea, pd.getZ(), temp8a);
    temp8blen = scale_expansion_zeroelim(4, da, -p.getZ(), temp8b);
    temp16len =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
                                  temp16);
    temp8alen = scale_expansion_zeroelim(4, de, pa.getZ(), temp8a);
    dealen =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
                                  dea);

    temp8alen = scale_expansion_zeroelim(4, ab, p.getZ(), temp8a);
    temp8blen = scale_expansion_zeroelim(4, eb, -pa.getZ(), temp8b);
    temp16len =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
                                  temp16);
    temp8alen = scale_expansion_zeroelim(4, ea, pb.getZ(), temp8a);
    eablen =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
                                  eab);

    temp8alen = scale_expansion_zeroelim(4, bd, pa.getZ(), temp8a);
    temp8blen = scale_expansion_zeroelim(4, da, pb.getZ(), temp8b);
    temp16len =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
                                  temp16);
    temp8alen = scale_expansion_zeroelim(4, ab, pd.getZ(), temp8a);
    abdlen =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
                                  abd);

    temp8alen = scale_expansion_zeroelim(4, ce, pb.getZ(), temp8a);
    temp8blen = scale_expansion_zeroelim(4, eb, pc.getZ(), temp8b);
    temp16len =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
                                  temp16);
    temp8alen = scale_expansion_zeroelim(4, bc, p.getZ(), temp8a);
    bcelen =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
                                  bce);

    temp8alen = scale_expansion_zeroelim(4, da, pc.getZ(), temp8a);
    temp8blen = scale_expansion_zeroelim(4, ac, pd.getZ(), temp8b);
    temp16len =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
                                  temp16);
    temp8alen = scale_expansion_zeroelim(4, cd, pa.getZ(), temp8a);
    cdalen =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
                                  cda);

    temp8alen = scale_expansion_zeroelim(4, eb, pd.getZ(), temp8a);
    temp8blen = scale_expansion_zeroelim(4, bd, p.getZ(), temp8b);
    temp16len =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
                                  temp16);
    temp8alen = scale_expansion_zeroelim(4, de, pb.getZ(), temp8a);
    deblen =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
                                  deb);

    temp8alen = scale_expansion_zeroelim(4, ac, p.getZ(), temp8a);
    temp8blen = scale_expansion_zeroelim(4, ce, pa.getZ(), temp8b);
    temp16len =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp8blen, temp8b,
                                  temp16);
    temp8alen = scale_expansion_zeroelim(4, ea, pc.getZ(), temp8a);
    eaclen =
      fast_expansion_sum_zeroelim(temp8alen, temp8a, temp16len, temp16,
                                  eac);

    temp48alen =
      fast_expansion_sum_zeroelim(cdelen, cde, bcelen, bce, temp48a);
    temp48blen =
      fast_expansion_sum_zeroelim(deblen, deb, bcdlen, bcd, temp48b);
    for (i = 0; i < temp48blen; i++) {
      temp48b[i] = -temp48b[i];
    }
    bcdelen = fast_expansion_sum_zeroelim(temp48alen, temp48a,
                                          temp48blen, temp48b, bcde);
    xlen = scale_expansion_zeroelim(bcdelen, bcde, pa.getX(), temp192);
    xlen = scale_expansion_zeroelim(xlen, temp192, pa.getX(), det384x);
    ylen = scale_expansion_zeroelim(bcdelen, bcde, pa.getY(), temp192);
    ylen = scale_expansion_zeroelim(ylen, temp192, pa.getY(), det384y);
    zlen = scale_expansion_zeroelim(bcdelen, bcde, pa.getZ(), temp192);
    zlen = scale_expansion_zeroelim(zlen, temp192, pa.getZ(), det384z);
    xylen =
      fast_expansion_sum_zeroelim(xlen, det384x, ylen, det384y, detxy);
    alen =
      fast_expansion_sum_zeroelim(xylen, detxy, zlen, det384z, adet);

    temp48alen =
      fast_expansion_sum_zeroelim(dealen, dea, cdalen, cda, temp48a);
    temp48blen =
      fast_expansion_sum_zeroelim(eaclen, eac, cdelen, cde, temp48b);
    for (i = 0; i < temp48blen; i++) {
      temp48b[i] = -temp48b[i];
    }
    cdealen = fast_expansion_sum_zeroelim(temp48alen, temp48a,
                                          temp48blen, temp48b, cdea);
    xlen = scale_expansion_zeroelim(cdealen, cdea, pb.getX(), temp192);
    xlen = scale_expansion_zeroelim(xlen, temp192, pb.getX(), det384x);
    ylen = scale_expansion_zeroelim(cdealen, cdea, pb.getY(), temp192);
    ylen = scale_expansion_zeroelim(ylen, temp192, pb.getY(), det384y);
    zlen = scale_expansion_zeroelim(cdealen, cdea, pb.getZ(), temp192);
    zlen = scale_expansion_zeroelim(zlen, temp192, pb.getZ(), det384z);
    xylen =
      fast_expansion_sum_zeroelim(xlen, det384x, ylen, det384y, detxy);
    blen =
      fast_expansion_sum_zeroelim(xylen, detxy, zlen, det384z, bdet);

    temp48alen =
      fast_expansion_sum_zeroelim(eablen, eab, deblen, deb, temp48a);
    temp48blen =
      fast_expansion_sum_zeroelim(abdlen, abd, dealen, dea, temp48b);
    for (i = 0; i < temp48blen; i++) {
      temp48b[i] = -temp48b[i];
    }
    deablen = fast_expansion_sum_zeroelim(temp48alen, temp48a,
                                          temp48blen, temp48b, deab);
    xlen = scale_expansion_zeroelim(deablen, deab, pc.getX(), temp192);
    xlen = scale_expansion_zeroelim(xlen, temp192, pc.getX(), det384x);
    ylen = scale_expansion_zeroelim(deablen, deab, pc.getY(), temp192);
    ylen = scale_expansion_zeroelim(ylen, temp192, pc.getY(), det384y);
    zlen = scale_expansion_zeroelim(deablen, deab, pc.getZ(), temp192);
    zlen = scale_expansion_zeroelim(zlen, temp192, pc.getZ(), det384z);
    xylen =
      fast_expansion_sum_zeroelim(xlen, det384x, ylen, det384y, detxy);
    clen =
      fast_expansion_sum_zeroelim(xylen, detxy, zlen, det384z, cdet);

    temp48alen =
      fast_expansion_sum_zeroelim(abclen, abc, eaclen, eac, temp48a);
    temp48blen =
      fast_expansion_sum_zeroelim(bcelen, bce, eablen, eab, temp48b);
    for (i = 0; i < temp48blen; i++) {
      temp48b[i] = -temp48b[i];
    }
    eabclen = fast_expansion_sum_zeroelim(temp48alen, temp48a,
                                          temp48blen, temp48b, eabc);
    xlen = scale_expansion_zeroelim(eabclen, eabc, pd.getX(), temp192);
    xlen = scale_expansion_zeroelim(xlen, temp192, pd.getX(), det384x);
    ylen = scale_expansion_zeroelim(eabclen, eabc, pd.getY(), temp192);
    ylen = scale_expansion_zeroelim(ylen, temp192, pd.getY(), det384y);
    zlen = scale_expansion_zeroelim(eabclen, eabc, pd.getZ(), temp192);
    zlen = scale_expansion_zeroelim(zlen, temp192, pd.getZ(), det384z);
    xylen =
      fast_expansion_sum_zeroelim(xlen, det384x, ylen, det384y, detxy);
    dlen =
      fast_expansion_sum_zeroelim(xylen, detxy, zlen, det384z, ddet);

    temp48alen =
      fast_expansion_sum_zeroelim(bcdlen, bcd, abdlen, abd, temp48a);
    temp48blen =
      fast_expansion_sum_zeroelim(cdalen, cda, abclen, abc, temp48b);
    for (i = 0; i < temp48blen; i++) {
      temp48b[i] = -temp48b[i];
    }
    abcdlen = fast_expansion_sum_zeroelim(temp48alen, temp48a,
                                          temp48blen, temp48b, abcd);
    xlen = scale_expansion_zeroelim(abcdlen, abcd, p.getX(), temp192);
    xlen = scale_expansion_zeroelim(xlen, temp192, p.getX(), det384x);
    ylen = scale_expansion_zeroelim(abcdlen, abcd, p.getY(), temp192);
    ylen = scale_expansion_zeroelim(ylen, temp192, p.getY(), det384y);
    zlen = scale_expansion_zeroelim(abcdlen, abcd, p.getZ(), temp192);
    zlen = scale_expansion_zeroelim(zlen, temp192, p.getZ(), det384z);
    xylen = fast_expansion_sum_zeroelim(xlen, det384x, ylen, det384y, detxy);
    elen = fast_expansion_sum_zeroelim(xylen, detxy, zlen, det384z, edet);
    ablen = fast_expansion_sum_zeroelim(alen, adet, blen, bdet, abdet);
    cdlen = fast_expansion_sum_zeroelim(clen, cdet, dlen, ddet, cddet);
    cdelen = fast_expansion_sum_zeroelim(cdlen, cddet, elen, edet, cdedet);

    // not correct rounding to double!
    // int deterlen = fast_expansion_sum_zeroelim(ablen, abdet, cdelen, cdedet, deter);
    // return deter[deterlen - 1];
    fast_expansion_sum_zeroelim(ablen, abdet, cdelen, cdedet, deter);
    return XDouble.unsafe(deter).doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private ExactMacro (final Vector3D a,
                      final Vector3D b,
                      final Vector3D c,
                      final Vector3D d)  {
    super(a,b,c,d); }

  public static final Tetrahedron3D of (final Vector3D a,
                                        final Vector3D b,
                                        final Vector3D c,
                                        final Vector3D d) {
    return new ExactMacro(a, b, c, d); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
