package mop.java.geometry.predicates.tetrahedron.macro;

// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.geometry.predicates.tetrahedron.Tetrahedron3D;
import org.apache.commons.geometry.euclidean.threed.Vector3D;

import static mop.java.geometry.predicates.Expansion.EPSILON;
import static mop.java.geometry.predicates.Expansion.SPLITTER;
import static mop.java.geometry.predicates.Expansion.estimate;
import static mop.java.geometry.predicates.Expansion.fast_expansion_sum_zeroelim;
import static mop.java.geometry.predicates.Expansion.resulterrbound;
import static mop.java.geometry.predicates.Expansion.scale_expansion_zeroelim;

/**
 * Adaptive exact tests. Robust.
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-06
 */

// strictfp unnecessary for JDK17 and later
public final class AdaptMacro extends Tetrahedron3D {

  //--------------------------------------------------------------------
  // signedVolume
  //--------------------------------------------------------------------

  private static final double o3derrboundB =
    (3.0 + 28.0 * EPSILON) * EPSILON;
  private static final double o3derrboundC =
    (26.0 + 288.0 * EPSILON) * EPSILON * EPSILON;

  //--------------------------------------------------------------------

  public final double signedVolume (final Vector3D pa,
                                    final Vector3D pb,
                                    final Vector3D pc,
                                    final Vector3D pd) {
    return new DefaultMacro().signedVolume(pa, pb, pc, pd); }

  public final double signedVolume (final Vector3D pa,
                                    final Vector3D pb,
                                    final Vector3D pc,
                                    final Vector3D pd,
                                    final double permanent) {
    double adx, bdx, cdx, ady, bdy, cdy, adz, bdz, cdz;
    double det, errbound;

    double bdxcdy1, cdxbdy1, cdxady1, adxcdy1, adxbdy1, bdxady1;
    double bdxcdy0, cdxbdy0, cdxady0, adxcdy0, adxbdy0, bdxady0;
    double[] bc = new double[4], ca = new double[4], ab = new double[4];
    double bc3, ca3, ab3;
    double[] adet = new double[8], bdet = new double[8], cdet =
      new double[8];
    int alen, blen, clen;
    double[] abdet = new double[16];
    int ablen;
    double[] finnow, finother, finswap;
    double[] fin1 = new double[192], fin2 = new double[192];
    int finlength;

    double adxtail, bdxtail, cdxtail;
    double adytail, bdytail, cdytail;
    double adztail, bdztail, cdztail;
    double at_blarge, at_clarge;
    double bt_clarge, bt_alarge;
    double ct_alarge, ct_blarge;
    double[] at_b = new double[4], at_c = new double[4],
      bt_c = new double[4], bt_a = new double[4],
      ct_a = new double[4], ct_b = new double[4];
    int at_blen, at_clen, bt_clen, bt_alen, ct_alen, ct_blen;
    double bdxt_cdy1, cdxt_bdy1, cdxt_ady1;
    double adxt_cdy1, adxt_bdy1, bdxt_ady1;
    double bdxt_cdy0, cdxt_bdy0, cdxt_ady0;
    double adxt_cdy0, adxt_bdy0, bdxt_ady0;
    double bdyt_cdx1, cdyt_bdx1, cdyt_adx1;
    double adyt_cdx1, adyt_bdx1, bdyt_adx1;
    double bdyt_cdx0, cdyt_bdx0, cdyt_adx0;
    double adyt_cdx0, adyt_bdx0, bdyt_adx0;
    double[] bct = new double[8], cat = new double[8],
      abt = new double[8];
    int bctlen, catlen, abtlen;
    double bdxt_cdyt1, cdxt_bdyt1, cdxt_adyt1;
    double adxt_cdyt1, adxt_bdyt1, bdxt_adyt1;
    double bdxt_cdyt0, cdxt_bdyt0, cdxt_adyt0;
    double adxt_cdyt0, adxt_bdyt0, bdxt_adyt0;
    double[] u = new double[4], v = new double[12], w = new double[16];
    double u3;
    int vlength, wlength;
    double negate;

    double bvirt;
    double avirt, bround, around;
    double c;
    double abig;
    double ahi, alo, bhi, blo;
    double err1, err2, err3;
    double _i, _j, _k;
    double _0;

    adx = (pa.getX() - pd.getX());
    bdx = (pb.getX() - pd.getX());
    cdx = (pc.getX() - pd.getX());
    ady = (pa.getY() - pd.getY());
    bdy = (pb.getY() - pd.getY());
    cdy = (pc.getY() - pd.getY());
    adz = (pa.getZ() - pd.getZ());
    bdz = (pb.getZ() - pd.getZ());
    cdz = (pc.getZ() - pd.getZ());

    bdxcdy1 = (bdx * cdy);
    c = SPLITTER * bdx;
    abig = (c - bdx);
    ahi = c - abig;
    alo = bdx - ahi;
    c = (SPLITTER * cdy);
    abig = (c - cdy);
    bhi = c - abig;
    blo = cdy - bhi;
    err1 = bdxcdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    bdxcdy0 = (alo * blo) - err3;
    cdxbdy1 = (cdx * bdy);
    c = (SPLITTER * cdx);
    abig = (c - cdx);
    ahi = c - abig;
    alo = cdx - ahi;
    c = (SPLITTER * bdy);
    abig = (c - bdy);
    bhi = c - abig;
    blo = bdy - bhi;
    err1 = cdxbdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    cdxbdy0 = (alo * blo) - err3;
    _i = (bdxcdy0 - cdxbdy0);
    bvirt = (bdxcdy0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - cdxbdy0;
    around = bdxcdy0 - avirt;
    bc[0] = around + bround;
    _j = (bdxcdy1 + _i);
    bvirt = (_j - bdxcdy1);
    avirt = _j - bvirt;
    bround = _i - bvirt;
    around = bdxcdy1 - avirt;
    _0 = around + bround;
    _i = (_0 - cdxbdy1);
    bvirt = (_0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - cdxbdy1;
    around = _0 - avirt;
    bc[1] = around + bround;
    bc3 = (_j + _i);
    bvirt = (bc3 - _j);
    avirt = bc3 - bvirt;
    bround = _i - bvirt;
    around = _j - avirt;
    bc[2] = around + bround;
    bc[3] = bc3;
    alen = scale_expansion_zeroelim(4, bc, adz, adet);

    cdxady1 = (cdx * ady);
    c = (SPLITTER * cdx);
    abig = (c - cdx);
    ahi = c - abig;
    alo = cdx - ahi;
    c = (SPLITTER * ady);
    abig = (c - ady);
    bhi = c - abig;
    blo = ady - bhi;
    err1 = cdxady1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    cdxady0 = (alo * blo) - err3;
    adxcdy1 = (adx * cdy);
    c = (SPLITTER * adx);
    abig = (c - adx);
    ahi = c - abig;
    alo = adx - ahi;
    c = (SPLITTER * cdy);
    abig = (c - cdy);
    bhi = c - abig;
    blo = cdy - bhi;
    err1 = adxcdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    adxcdy0 = (alo * blo) - err3;
    _i = (cdxady0 - adxcdy0);
    bvirt = (cdxady0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - adxcdy0;
    around = cdxady0 - avirt;
    ca[0] = around + bround;
    _j = (cdxady1 + _i);
    bvirt = (_j - cdxady1);
    avirt = _j - bvirt;
    bround = _i - bvirt;
    around = cdxady1 - avirt;
    _0 = around + bround;
    _i = (_0 - adxcdy1);
    bvirt = (_0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - adxcdy1;
    around = _0 - avirt;
    ca[1] = around + bround;
    ca3 = (_j + _i);
    bvirt = (ca3 - _j);
    avirt = ca3 - bvirt;
    bround = _i - bvirt;
    around = _j - avirt;
    ca[2] = around + bround;
    ca[3] = ca3;
    blen = scale_expansion_zeroelim(4, ca, bdz, bdet);

    adxbdy1 = (adx * bdy);
    c = (SPLITTER * adx);
    abig = (c - adx);
    ahi = c - abig;
    alo = adx - ahi;
    c = (SPLITTER * bdy);
    abig = (c - bdy);
    bhi = c - abig;
    blo = bdy - bhi;
    err1 = adxbdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    adxbdy0 = (alo * blo) - err3;
    bdxady1 = (bdx * ady);
    c = (SPLITTER * bdx);
    abig = (c - bdx);
    ahi = c - abig;
    alo = bdx - ahi;
    c = (SPLITTER * ady);
    abig = (c - ady);
    bhi = c - abig;
    blo = ady - bhi;
    err1 = bdxady1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    bdxady0 = (alo * blo) - err3;
    _i = (adxbdy0 - bdxady0);
    bvirt = (adxbdy0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - bdxady0;
    around = adxbdy0 - avirt;
    ab[0] = around + bround;
    _j = (adxbdy1 + _i);
    bvirt = (_j - adxbdy1);
    avirt = _j - bvirt;
    bround = _i - bvirt;
    around = adxbdy1 - avirt;
    _0 = around + bround;
    _i = (_0 - bdxady1);
    bvirt = (_0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - bdxady1;
    around = _0 - avirt;
    ab[1] = around + bround;
    ab3 = (_j + _i);
    bvirt = (ab3 - _j);
    avirt = ab3 - bvirt;
    bround = _i - bvirt;
    around = _j - avirt;
    ab[2] = around + bround;
    ab[3] = ab3;
    clen = scale_expansion_zeroelim(4, ab, cdz, cdet);

    ablen = fast_expansion_sum_zeroelim(alen, adet, blen, bdet, abdet);
    finlength =
      fast_expansion_sum_zeroelim(ablen, abdet, clen, cdet, fin1);

    det = estimate(finlength, fin1);
    errbound = o3derrboundB * permanent;
    if ((det >= errbound) || (-det >= errbound)) { return det; }

    bvirt = (pa.getX() - adx);
    avirt = adx + bvirt;
    bround = bvirt - pd.getX();
    around = pa.getX() - avirt;
    adxtail = around + bround;
    bvirt = (pb.getX() - bdx);
    avirt = bdx + bvirt;
    bround = bvirt - pd.getX();
    around = pb.getX() - avirt;
    bdxtail = around + bround;
    bvirt = (pc.getX() - cdx);
    avirt = cdx + bvirt;
    bround = bvirt - pd.getX();
    around = pc.getX() - avirt;
    cdxtail = around + bround;
    bvirt = (pa.getY() - ady);
    avirt = ady + bvirt;
    bround = bvirt - pd.getY();
    around = pa.getY() - avirt;
    adytail = around + bround;
    bvirt = (pb.getY() - bdy);
    avirt = bdy + bvirt;
    bround = bvirt - pd.getY();
    around = pb.getY() - avirt;
    bdytail = around + bround;
    bvirt = (pc.getY() - cdy);
    avirt = cdy + bvirt;
    bround = bvirt - pd.getY();
    around = pc.getY() - avirt;
    cdytail = around + bround;
    bvirt = (pa.getZ() - adz);
    avirt = adz + bvirt;
    bround = bvirt - pd.getZ();
    around = pa.getZ() - avirt;
    adztail = around + bround;
    bvirt = (pb.getZ() - bdz);
    avirt = bdz + bvirt;
    bround = bvirt - pd.getZ();
    around = pb.getZ() - avirt;
    bdztail = around + bround;
    bvirt = (pc.getZ() - cdz);
    avirt = cdz + bvirt;
    bround = bvirt - pd.getZ();
    around = pc.getZ() - avirt;
    cdztail = around + bround;

    if ((adxtail == 0.0) && (bdxtail == 0.0) && (cdxtail == 0.0)
      && (adytail == 0.0) && (bdytail == 0.0) && (cdytail == 0.0)
      && (adztail == 0.0) && (bdztail == 0.0) && (cdztail == 0.0)) {
      return det;
    }

    errbound =
      o3derrboundC * permanent + resulterrbound * ((det) >= 0.0 ? (det)
                                                                :
                                                   -(det));
    det += (adz * ((bdx * cdytail + cdy * bdxtail)
      - (bdy * cdxtail + cdx * bdytail))
      + adztail * (bdx * cdy - bdy * cdx))
      + (bdz * ((cdx * adytail + ady * cdxtail)
      - (cdy * adxtail + adx * cdytail))
      + bdztail * (cdx * ady - cdy * adx))
      + (cdz * ((adx * bdytail + bdy * adxtail)
      - (ady * bdxtail + bdx * adytail))
      + cdztail * (adx * bdy - ady * bdx));
    if ((det >= errbound) || (-det >= errbound)) { return det; }

    finnow = fin1;
    finother = fin2;

    if (adxtail == 0.0) {
      if (adytail == 0.0) {
        at_b[0] = 0.0;
        at_blen = 1;
        at_c[0] = 0.0;
        at_clen = 1;
      }
      else {
        negate = -adytail;
        at_blarge = (negate * bdx);
        c = (SPLITTER * negate);
        abig = (c - negate);
        ahi = c - abig;
        alo = negate - ahi;
        c = (SPLITTER * bdx);
        abig = (c - bdx);
        bhi = c - abig;
        blo = bdx - bhi;
        err1 = at_blarge - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        at_b[0] = (alo * blo) - err3;
        at_b[1] = at_blarge;
        at_blen = 2;
        at_clarge = (adytail * cdx);
        c = (SPLITTER * adytail);
        abig = (c - adytail);
        ahi = c - abig;
        alo = adytail - ahi;
        c = (SPLITTER * cdx);
        abig = (c - cdx);
        bhi = c - abig;
        blo = cdx - bhi;
        err1 = at_clarge - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        at_c[0] = (alo * blo) - err3;
        at_c[1] = at_clarge;
        at_clen = 2;
      }
    }
    else {
      if (adytail == 0.0) {
        at_blarge = (adxtail * bdy);
        c = (SPLITTER * adxtail);
        abig = (c - adxtail);
        ahi = c - abig;
        alo = adxtail - ahi;
        c = (SPLITTER * bdy);
        abig = (c - bdy);
        bhi = c - abig;
        blo = bdy - bhi;
        err1 = at_blarge - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        at_b[0] = (alo * blo) - err3;
        at_b[1] = at_blarge;
        at_blen = 2;
        negate = -adxtail;
        at_clarge = (negate * cdy);
        c = (SPLITTER * negate);
        abig = (c - negate);
        ahi = c - abig;
        alo = negate - ahi;
        c = (SPLITTER * cdy);
        abig = (c - cdy);
        bhi = c - abig;
        blo = cdy - bhi;
        err1 = at_clarge - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        at_c[0] = (alo * blo) - err3;
        at_c[1] = at_clarge;
        at_clen = 2;
      }
      else {
        adxt_bdy1 = (adxtail * bdy);
        c = (SPLITTER * adxtail);
        abig = (c - adxtail);
        ahi = c - abig;
        alo = adxtail - ahi;
        c = (SPLITTER * bdy);
        abig = (c - bdy);
        bhi = c - abig;
        blo = bdy - bhi;
        err1 = adxt_bdy1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        adxt_bdy0 = (alo * blo) - err3;
        adyt_bdx1 = (adytail * bdx);
        c = (SPLITTER * adytail);
        abig = (c - adytail);
        ahi = c - abig;
        alo = adytail - ahi;
        c = (SPLITTER * bdx);
        abig = (c - bdx);
        bhi = c - abig;
        blo = bdx - bhi;
        err1 = adyt_bdx1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        adyt_bdx0 = (alo * blo) - err3;
        _i = (adxt_bdy0 - adyt_bdx0);
        bvirt = (adxt_bdy0 - _i);
        avirt = _i + bvirt;
        bround = bvirt - adyt_bdx0;
        around = adxt_bdy0 - avirt;
        at_b[0] = around + bround;
        _j = (adxt_bdy1 + _i);
        bvirt = (_j - adxt_bdy1);
        avirt = _j - bvirt;
        bround = _i - bvirt;
        around = adxt_bdy1 - avirt;
        _0 = around + bround;
        _i = (_0 - adyt_bdx1);
        bvirt = (_0 - _i);
        avirt = _i + bvirt;
        bround = bvirt - adyt_bdx1;
        around = _0 - avirt;
        at_b[1] = around + bround;
        at_blarge = (_j + _i);
        bvirt = (at_blarge - _j);
        avirt = at_blarge - bvirt;
        bround = _i - bvirt;
        around = _j - avirt;
        at_b[2] = around + bround
        ;
        at_b[3] = at_blarge;
        at_blen = 4;
        adyt_cdx1 = (adytail * cdx);
        c = (SPLITTER * adytail);
        abig = (c - adytail);
        ahi = c - abig;
        alo = adytail - ahi;
        c = (SPLITTER * cdx);
        abig = (c - cdx);
        bhi = c - abig;
        blo = cdx - bhi;
        err1 = adyt_cdx1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        adyt_cdx0 = (alo * blo) - err3;
        adxt_cdy1 = (adxtail * cdy);
        c = (SPLITTER * adxtail);
        abig = (c - adxtail);
        ahi = c - abig;
        alo = adxtail - ahi;
        c = (SPLITTER * cdy);
        abig = (c - cdy);
        bhi = c - abig;
        blo = cdy - bhi;
        err1 = adxt_cdy1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        adxt_cdy0 = (alo * blo) - err3;
        _i = (adyt_cdx0 - adxt_cdy0);
        bvirt = (adyt_cdx0 - _i);
        avirt = _i + bvirt;
        bround = bvirt - adxt_cdy0;
        around = adyt_cdx0 - avirt;
        at_c[0] = around + bround;
        _j = (adyt_cdx1 + _i);
        bvirt = (_j - adyt_cdx1);
        avirt = _j - bvirt;
        bround = _i - bvirt;
        around = adyt_cdx1 - avirt;
        _0 = around + bround;
        _i = (_0 - adxt_cdy1);
        bvirt = (_0 - _i);
        avirt = _i + bvirt;
        bround = bvirt - adxt_cdy1;
        around = _0 - avirt;
        at_c[1] = around + bround;
        at_clarge = (_j + _i);
        bvirt = (at_clarge - _j);
        avirt = at_clarge - bvirt;
        bround = _i - bvirt;
        around = _j - avirt;
        at_c[2] = around + bround
        ;
        at_c[3] = at_clarge;
        at_clen = 4;
      }
    }
    if (bdxtail == 0.0) {
      if (bdytail == 0.0) {
        bt_c[0] = 0.0;
        bt_clen = 1;
        bt_a[0] = 0.0;
        bt_alen = 1;
      }
      else {
        negate = -bdytail;
        bt_clarge = (negate * cdx);
        c = (SPLITTER * negate);
        abig = (c - negate);
        ahi = c - abig;
        alo = negate - ahi;
        c = (SPLITTER * cdx);
        abig = (c - cdx);
        bhi = c - abig;
        blo = cdx - bhi;
        err1 = bt_clarge - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        bt_c[0] = (alo * blo) - err3;
        bt_c[1] = bt_clarge;
        bt_clen = 2;
        bt_alarge = (bdytail * adx);
        c = (SPLITTER * bdytail);
        abig = (c - bdytail);
        ahi = c - abig;
        alo = bdytail - ahi;
        c = (SPLITTER * adx);
        abig = (c - adx);
        bhi = c - abig;
        blo = adx - bhi;
        err1 = bt_alarge - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        bt_a[0] = (alo * blo) - err3;
        bt_a[1] = bt_alarge;
        bt_alen = 2;
      }
    }
    else {
      if (bdytail == 0.0) {
        bt_clarge = (bdxtail * cdy);
        c = (SPLITTER * bdxtail);
        abig = (c - bdxtail);
        ahi = c - abig;
        alo = bdxtail - ahi;
        c = (SPLITTER * cdy);
        abig = (c - cdy);
        bhi = c - abig;
        blo = cdy - bhi;
        err1 = bt_clarge - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        bt_c[0] = (alo * blo) - err3;
        bt_c[1] = bt_clarge;
        bt_clen = 2;
        negate = -bdxtail;
        bt_alarge = (negate * ady);
        c = (SPLITTER * negate);
        abig = (c - negate);
        ahi = c - abig;
        alo = negate - ahi;
        c = (SPLITTER * ady);
        abig = (c - ady);
        bhi = c - abig;
        blo = ady - bhi;
        err1 = bt_alarge - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        bt_a[0] = (alo * blo) - err3;
        bt_a[1] = bt_alarge;
        bt_alen = 2;
      }
      else {
        bdxt_cdy1 = (bdxtail * cdy);
        c = (SPLITTER * bdxtail);
        abig = (c - bdxtail);
        ahi = c - abig;
        alo = bdxtail - ahi;
        c = (SPLITTER * cdy);
        abig = (c - cdy);
        bhi = c - abig;
        blo = cdy - bhi;
        err1 = bdxt_cdy1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        bdxt_cdy0 = (alo * blo) - err3;
        bdyt_cdx1 = (bdytail * cdx);
        c = (SPLITTER * bdytail);
        abig = (c - bdytail);
        ahi = c - abig;
        alo = bdytail - ahi;
        c = (SPLITTER * cdx);
        abig = (c - cdx);
        bhi = c - abig;
        blo = cdx - bhi;
        err1 = bdyt_cdx1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        bdyt_cdx0 = (alo * blo) - err3;
        _i = (bdxt_cdy0 - bdyt_cdx0);
        bvirt = (bdxt_cdy0 - _i);
        avirt = _i + bvirt;
        bround = bvirt - bdyt_cdx0;
        around = bdxt_cdy0 - avirt;
        bt_c[0] = around + bround;
        _j = (bdxt_cdy1 + _i);
        bvirt = (_j - bdxt_cdy1);
        avirt = _j - bvirt;
        bround = _i - bvirt;
        around = bdxt_cdy1 - avirt;
        _0 = around + bround;
        _i = (_0 - bdyt_cdx1);
        bvirt = (_0 - _i);
        avirt = _i + bvirt;
        bround = bvirt - bdyt_cdx1;
        around = _0 - avirt;
        bt_c[1] = around + bround;
        bt_clarge = (_j + _i);
        bvirt = (bt_clarge - _j);
        avirt = bt_clarge - bvirt;
        bround = _i - bvirt;
        around = _j - avirt;
        bt_c[2] = around + bround
        ;
        bt_c[3] = bt_clarge;
        bt_clen = 4;
        bdyt_adx1 = (bdytail * adx);
        c = (SPLITTER * bdytail);
        abig = (c - bdytail);
        ahi = c - abig;
        alo = bdytail - ahi;
        c = (SPLITTER * adx);
        abig = (c - adx);
        bhi = c - abig;
        blo = adx - bhi;
        err1 = bdyt_adx1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        bdyt_adx0 = (alo * blo) - err3;
        bdxt_ady1 = (bdxtail * ady);
        c = (SPLITTER * bdxtail);
        abig = (c - bdxtail);
        ahi = c - abig;
        alo = bdxtail - ahi;
        c = (SPLITTER * ady);
        abig = (c - ady);
        bhi = c - abig;
        blo = ady - bhi;
        err1 = bdxt_ady1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        bdxt_ady0 = (alo * blo) - err3;
        _i = (bdyt_adx0 - bdxt_ady0);
        bvirt = (bdyt_adx0 - _i);
        avirt = _i + bvirt;
        bround = bvirt - bdxt_ady0;
        around = bdyt_adx0 - avirt;
        bt_a[0] = around + bround;
        _j = (bdyt_adx1 + _i);
        bvirt = (_j - bdyt_adx1);
        avirt = _j - bvirt;
        bround = _i - bvirt;
        around = bdyt_adx1 - avirt;
        _0 = around + bround;
        _i = (_0 - bdxt_ady1);
        bvirt = (_0 - _i);
        avirt = _i + bvirt;
        bround = bvirt - bdxt_ady1;
        around = _0 - avirt;
        bt_a[1] = around + bround;
        bt_alarge = (_j + _i);
        bvirt = (bt_alarge - _j);
        avirt = bt_alarge - bvirt;
        bround = _i - bvirt;
        around = _j - avirt;
        bt_a[2] = around + bround
        ;
        bt_a[3] = bt_alarge;
        bt_alen = 4;
      }
    }
    if (cdxtail == 0.0) {
      if (cdytail == 0.0) {
        ct_a[0] = 0.0;
        ct_alen = 1;
        ct_b[0] = 0.0;
        ct_blen = 1;
      }
      else {
        negate = -cdytail;
        ct_alarge = (negate * adx);
        c = (SPLITTER * negate);
        abig = (c - negate);
        ahi = c - abig;
        alo = negate - ahi;
        c = (SPLITTER * adx);
        abig = (c - adx);
        bhi = c - abig;
        blo = adx - bhi;
        err1 = ct_alarge - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        ct_a[0] = (alo * blo) - err3;
        ct_a[1] = ct_alarge;
        ct_alen = 2;
        ct_blarge = (cdytail * bdx);
        c = (SPLITTER * cdytail);
        abig = (c - cdytail);
        ahi = c - abig;
        alo = cdytail - ahi;
        c = (SPLITTER * bdx);
        abig = (c - bdx);
        bhi = c - abig;
        blo = bdx - bhi;
        err1 = ct_blarge - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        ct_b[0] = (alo * blo) - err3;
        ct_b[1] = ct_blarge;
        ct_blen = 2;
      }
    }
    else {
      if (cdytail == 0.0) {
        ct_alarge = (cdxtail * ady);
        c = (SPLITTER * cdxtail);
        abig = (c - cdxtail);
        ahi = c - abig;
        alo = cdxtail - ahi;
        c = (SPLITTER * ady);
        abig = (c - ady);
        bhi = c - abig;
        blo = ady - bhi;
        err1 = ct_alarge - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        ct_a[0] = (alo * blo) - err3;
        ct_a[1] = ct_alarge;
        ct_alen = 2;
        negate = -cdxtail;
        ct_blarge = (negate * bdy);
        c = (SPLITTER * negate);
        abig = (c - negate);
        ahi = c - abig;
        alo = negate - ahi;
        c = (SPLITTER * bdy);
        abig = (c - bdy);
        bhi = c - abig;
        blo = bdy - bhi;
        err1 = ct_blarge - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        ct_b[0] = (alo * blo) - err3;
        ct_b[1] = ct_blarge;
        ct_blen = 2;
      }
      else {
        cdxt_ady1 = (cdxtail * ady);
        c = (SPLITTER * cdxtail);
        abig = (c - cdxtail);
        ahi = c - abig;
        alo = cdxtail - ahi;
        c = (SPLITTER * ady);
        abig = (c - ady);
        bhi = c - abig;
        blo = ady - bhi;
        err1 = cdxt_ady1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        cdxt_ady0 = (alo * blo) - err3;
        cdyt_adx1 = (cdytail * adx);
        c = (SPLITTER * cdytail);
        abig = (c - cdytail);
        ahi = c - abig;
        alo = cdytail - ahi;
        c = (SPLITTER * adx);
        abig = (c - adx);
        bhi = c - abig;
        blo = adx - bhi;
        err1 = cdyt_adx1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        cdyt_adx0 = (alo * blo) - err3;
        _i = (cdxt_ady0 - cdyt_adx0);
        bvirt = (cdxt_ady0 - _i);
        avirt = _i + bvirt;
        bround = bvirt - cdyt_adx0;
        around = cdxt_ady0 - avirt;
        ct_a[0] = around + bround;
        _j = (cdxt_ady1 + _i);
        bvirt = (_j - cdxt_ady1);
        avirt = _j - bvirt;
        bround = _i - bvirt;
        around = cdxt_ady1 - avirt;
        _0 = around + bround;
        _i = (_0 - cdyt_adx1);
        bvirt = (_0 - _i);
        avirt = _i + bvirt;
        bround = bvirt - cdyt_adx1;
        around = _0 - avirt;
        ct_a[1] = around + bround;
        ct_alarge = (_j + _i);
        bvirt = (ct_alarge - _j);
        avirt = ct_alarge - bvirt;
        bround = _i - bvirt;
        around = _j - avirt;
        ct_a[2] = around + bround
        ;
        ct_a[3] = ct_alarge;
        ct_alen = 4;
        cdyt_bdx1 = (cdytail * bdx);
        c = (SPLITTER * cdytail);
        abig = (c - cdytail);
        ahi = c - abig;
        alo = cdytail - ahi;
        c = (SPLITTER * bdx);
        abig = (c - bdx);
        bhi = c - abig;
        blo = bdx - bhi;
        err1 = cdyt_bdx1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        cdyt_bdx0 = (alo * blo) - err3;
        cdxt_bdy1 = (cdxtail * bdy);
        c = (SPLITTER * cdxtail);
        abig = (c - cdxtail);
        ahi = c - abig;
        alo = cdxtail - ahi;
        c = (SPLITTER * bdy);
        abig = (c - bdy);
        bhi = c - abig;
        blo = bdy - bhi;
        err1 = cdxt_bdy1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        cdxt_bdy0 = (alo * blo) - err3;
        _i = (cdyt_bdx0 - cdxt_bdy0);
        bvirt = (cdyt_bdx0 - _i);
        avirt = _i + bvirt;
        bround = bvirt - cdxt_bdy0;
        around = cdyt_bdx0 - avirt;
        ct_b[0] = around + bround;
        _j = (cdyt_bdx1 + _i);
        bvirt = (_j - cdyt_bdx1);
        avirt = _j - bvirt;
        bround = _i - bvirt;
        around = cdyt_bdx1 - avirt;
        _0 = around + bround;
        _i = (_0 - cdxt_bdy1);
        bvirt = (_0 - _i);
        avirt = _i + bvirt;
        bround = bvirt - cdxt_bdy1;
        around = _0 - avirt;
        ct_b[1] = around + bround;
        ct_blarge = (_j + _i);
        bvirt = (ct_blarge - _j);
        avirt = ct_blarge - bvirt;
        bround = _i - bvirt;
        around = _j - avirt;
        ct_b[2] = around + bround
        ;
        ct_b[3] = ct_blarge;
        ct_blen = 4;
      }
    }

    bctlen =
      fast_expansion_sum_zeroelim(bt_clen, bt_c, ct_blen, ct_b, bct);
    wlength = scale_expansion_zeroelim(bctlen, bct, adz, w);
    finlength =
      fast_expansion_sum_zeroelim(finlength, finnow, wlength, w,
                                  finother);
    finswap = finnow;
    finnow = finother;
    finother = finswap;

    catlen =
      fast_expansion_sum_zeroelim(ct_alen, ct_a, at_clen, at_c, cat);
    wlength = scale_expansion_zeroelim(catlen, cat, bdz, w);
    finlength =
      fast_expansion_sum_zeroelim(finlength, finnow, wlength, w,
                                  finother);
    finswap = finnow;
    finnow = finother;
    finother = finswap;

    abtlen =
      fast_expansion_sum_zeroelim(at_blen, at_b, bt_alen, bt_a, abt);
    wlength = scale_expansion_zeroelim(abtlen, abt, cdz, w);
    finlength =
      fast_expansion_sum_zeroelim(finlength, finnow, wlength, w,
                                  finother);
    finswap = finnow;
    finnow = finother;
    finother = finswap;

    if (adztail != 0.0) {
      vlength = scale_expansion_zeroelim(4, bc, adztail, v);
      finlength =
        fast_expansion_sum_zeroelim(finlength, finnow, vlength, v,
                                    finother);
      finswap = finnow;
      finnow = finother;
      finother = finswap;
    }
    if (bdztail != 0.0) {
      vlength = scale_expansion_zeroelim(4, ca, bdztail, v);
      finlength =
        fast_expansion_sum_zeroelim(finlength, finnow, vlength, v,
                                    finother);
      finswap = finnow;
      finnow = finother;
      finother = finswap;
    }
    if (cdztail != 0.0) {
      vlength = scale_expansion_zeroelim(4, ab, cdztail, v);
      finlength =
        fast_expansion_sum_zeroelim(finlength, finnow, vlength, v,
                                    finother);
      finswap = finnow;
      finnow = finother;
      finother = finswap;
    }

    if (adxtail != 0.0) {
      if (bdytail != 0.0) {
        adxt_bdyt1 = (adxtail * bdytail);
        c = (SPLITTER * adxtail);
        abig = (c - adxtail);
        ahi = c - abig;
        alo = adxtail - ahi;
        c = (SPLITTER * bdytail);
        abig = (c - bdytail);
        bhi = c - abig;
        blo = bdytail - bhi;
        err1 = adxt_bdyt1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        adxt_bdyt0 = (alo * blo) - err3;
        c = (SPLITTER * cdz);
        abig = (c - cdz);
        bhi = c - abig;
        blo = cdz - bhi;
        _i = (adxt_bdyt0 * cdz);
        c = (SPLITTER * adxt_bdyt0);
        abig = (c - adxt_bdyt0);
        ahi = c - abig;
        alo = adxt_bdyt0 - ahi;
        err1 = _i - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        u[0] = (alo * blo) - err3;
        _j = (adxt_bdyt1 * cdz);
        c = (SPLITTER * adxt_bdyt1);
        abig = (c - adxt_bdyt1);
        ahi = c - abig;
        alo = adxt_bdyt1 - ahi;
        err1 = _j - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        _0 = (alo * blo) - err3;
        _k = (_i + _0);
        bvirt = (_k - _i);
        avirt = _k - bvirt;
        bround = _0 - bvirt;
        around = _i - avirt;
        u[1] = around + bround;
        u3 = (_j + _k);
        bvirt = u3 - _j;
        u[2] = _k - bvirt;
        u[3] = u3;
        finlength = fast_expansion_sum_zeroelim(finlength, finnow, 4, u,
                                                finother);
        finswap = finnow;
        finnow = finother;
        finother = finswap;
        if (cdztail != 0.0) {
          c = (SPLITTER * cdztail);
          abig = (c - cdztail);
          bhi = c - abig;
          blo = cdztail - bhi;
          _i = (adxt_bdyt0 * cdztail);
          c = (SPLITTER * adxt_bdyt0);
          abig = (c - adxt_bdyt0);
          ahi = c - abig;
          alo = adxt_bdyt0 - ahi;
          err1 = _i - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          u[0] = (alo * blo) - err3;
          _j = (adxt_bdyt1 * cdztail);
          c = (SPLITTER * adxt_bdyt1);
          abig = (c - adxt_bdyt1);
          ahi = c - abig;
          alo = adxt_bdyt1 - ahi;
          err1 = _j - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          _0 = (alo * blo) - err3;
          _k = (_i + _0);
          bvirt = (_k - _i);
          avirt = _k - bvirt;
          bround = _0 - bvirt;
          around = _i - avirt;
          u[1] = around + bround;
          u3 = (_j + _k);
          bvirt = u3 - _j;
          u[2] = _k - bvirt;
          u[3] = u3;
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, 4, u,
                                        finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;
        }
      }
      if (cdytail != 0.0) {
        negate = -adxtail;
        adxt_cdyt1 = (negate * cdytail);
        c = (SPLITTER * negate);
        abig = (c - negate);
        ahi = c - abig;
        alo = negate - ahi;
        c = (SPLITTER * cdytail);
        abig = (c - cdytail);
        bhi = c - abig;
        blo = cdytail - bhi;
        err1 = adxt_cdyt1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        adxt_cdyt0 = (alo * blo) - err3;
        c = (SPLITTER * bdz);
        abig = (c - bdz);
        bhi = c - abig;
        blo = bdz - bhi;
        _i = (adxt_cdyt0 * bdz);
        c = (SPLITTER * adxt_cdyt0);
        abig = (c - adxt_cdyt0);
        ahi = c - abig;
        alo = adxt_cdyt0 - ahi;
        err1 = _i - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        u[0] = (alo * blo) - err3;
        _j = (adxt_cdyt1 * bdz);
        c = (SPLITTER * adxt_cdyt1);
        abig = (c - adxt_cdyt1);
        ahi = c - abig;
        alo = adxt_cdyt1 - ahi;
        err1 = _j - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        _0 = (alo * blo) - err3;
        _k = (_i + _0);
        bvirt = (_k - _i);
        avirt = _k - bvirt;
        bround = _0 - bvirt;
        around = _i - avirt;
        u[1] = around + bround;
        u3 = (_j + _k);
        bvirt = u3 - _j;
        u[2] = _k - bvirt;
        u[3] = u3;
        finlength = fast_expansion_sum_zeroelim(finlength, finnow, 4, u,
                                                finother);
        finswap = finnow;
        finnow = finother;
        finother = finswap;
        if (bdztail != 0.0) {
          c = (SPLITTER * bdztail);
          abig = (c - bdztail);
          bhi = c - abig;
          blo = bdztail - bhi;
          _i = (adxt_cdyt0 * bdztail);
          c = (SPLITTER * adxt_cdyt0);
          abig = (c - adxt_cdyt0);
          ahi = c - abig;
          alo = adxt_cdyt0 - ahi;
          err1 = _i - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          u[0] = (alo * blo) - err3;
          _j = (adxt_cdyt1 * bdztail);
          c = (SPLITTER * adxt_cdyt1);
          abig = (c - adxt_cdyt1);
          ahi = c - abig;
          alo = adxt_cdyt1 - ahi;
          err1 = _j - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          _0 = (alo * blo) - err3;
          _k = (_i + _0);
          bvirt = (_k - _i);
          avirt = _k - bvirt;
          bround = _0 - bvirt;
          around = _i - avirt;
          u[1] = around + bround;
          u3 = (_j + _k);
          bvirt = u3 - _j;
          u[2] = _k - bvirt;
          u[3] = u3;
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, 4, u,
                                        finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;
        }
      }
    }
    if (bdxtail != 0.0) {
      if (cdytail != 0.0) {
        bdxt_cdyt1 = (bdxtail * cdytail);
        c = (SPLITTER * bdxtail);
        abig = (c - bdxtail);
        ahi = c - abig;
        alo = bdxtail - ahi;
        c = (SPLITTER * cdytail);
        abig = (c - cdytail);
        bhi = c - abig;
        blo = cdytail - bhi;
        err1 = bdxt_cdyt1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        bdxt_cdyt0 = (alo * blo) - err3;
        c = (SPLITTER * adz);
        abig = (c - adz);
        bhi = c - abig;
        blo = adz - bhi;
        _i = (bdxt_cdyt0 * adz);
        c = (SPLITTER * bdxt_cdyt0);
        abig = (c - bdxt_cdyt0);
        ahi = c - abig;
        alo = bdxt_cdyt0 - ahi;
        err1 = _i - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        u[0] = (alo * blo) - err3;
        _j = (bdxt_cdyt1 * adz);
        c = (SPLITTER * bdxt_cdyt1);
        abig = (c - bdxt_cdyt1);
        ahi = c - abig;
        alo = bdxt_cdyt1 - ahi;
        err1 = _j - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        _0 = (alo * blo) - err3;
        _k = (_i + _0);
        bvirt = (_k - _i);
        avirt = _k - bvirt;
        bround = _0 - bvirt;
        around = _i - avirt;
        u[1] = around + bround;
        u3 = (_j + _k);
        bvirt = u3 - _j;
        u[2] = _k - bvirt;
        u[3] = u3;
        finlength = fast_expansion_sum_zeroelim(finlength, finnow, 4, u,
                                                finother);
        finswap = finnow;
        finnow = finother;
        finother = finswap;
        if (adztail != 0.0) {
          c = (SPLITTER * adztail);
          abig = (c - adztail);
          bhi = c - abig;
          blo = adztail - bhi;
          _i = (bdxt_cdyt0 * adztail);
          c = (SPLITTER * bdxt_cdyt0);
          abig = (c - bdxt_cdyt0);
          ahi = c - abig;
          alo = bdxt_cdyt0 - ahi;
          err1 = _i - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          u[0] = (alo * blo) - err3;
          _j = (bdxt_cdyt1 * adztail);
          c = (SPLITTER * bdxt_cdyt1);
          abig = (c - bdxt_cdyt1);
          ahi = c - abig;
          alo = bdxt_cdyt1 - ahi;
          err1 = _j - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          _0 = (alo * blo) - err3;
          _k = (_i + _0);
          bvirt = (_k - _i);
          avirt = _k - bvirt;
          bround = _0 - bvirt;
          around = _i - avirt;
          u[1] = around + bround;
          u3 = (_j + _k);
          bvirt = u3 - _j;
          u[2] = _k - bvirt;
          u[3] = u3;
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, 4, u,
                                        finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;
        }
      }
      if (adytail != 0.0) {
        negate = -bdxtail;
        bdxt_adyt1 = (negate * adytail);
        c = (SPLITTER * negate);
        abig = (c - negate);
        ahi = c - abig;
        alo = negate - ahi;
        c = (SPLITTER * adytail);
        abig = (c - adytail);
        bhi = c - abig;
        blo = adytail - bhi;
        err1 = bdxt_adyt1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        bdxt_adyt0 = (alo * blo) - err3;
        c = (SPLITTER * cdz);
        abig = (c - cdz);
        bhi = c - abig;
        blo = cdz - bhi;
        _i = (bdxt_adyt0 * cdz);
        c = (SPLITTER * bdxt_adyt0);
        abig = (c - bdxt_adyt0);
        ahi = c - abig;
        alo = bdxt_adyt0 - ahi;
        err1 = _i - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        u[0] = (alo * blo) - err3;
        _j = (bdxt_adyt1 * cdz);
        c = (SPLITTER * bdxt_adyt1);
        abig = (c - bdxt_adyt1);
        ahi = c - abig;
        alo = bdxt_adyt1 - ahi;
        err1 = _j - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        _0 = (alo * blo) - err3;
        _k = (_i + _0);
        bvirt = (_k - _i);
        avirt = _k - bvirt;
        bround = _0 - bvirt;
        around = _i - avirt;
        u[1] = around + bround;
        u3 = (_j + _k);
        bvirt = u3 - _j;
        u[2] = _k - bvirt;
        u[3] = u3;
        finlength = fast_expansion_sum_zeroelim(finlength, finnow, 4, u,
                                                finother);
        finswap = finnow;
        finnow = finother;
        finother = finswap;
        if (cdztail != 0.0) {
          c = (SPLITTER * cdztail);
          abig = (c - cdztail);
          bhi = c - abig;
          blo = cdztail - bhi;
          _i = (bdxt_adyt0 * cdztail);
          c = (SPLITTER * bdxt_adyt0);
          abig = (c - bdxt_adyt0);
          ahi = c - abig;
          alo = bdxt_adyt0 - ahi;
          err1 = _i - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          u[0] = (alo * blo) - err3;
          _j = (bdxt_adyt1 * cdztail);
          c = (SPLITTER * bdxt_adyt1);
          abig = (c - bdxt_adyt1);
          ahi = c - abig;
          alo = bdxt_adyt1 - ahi;
          err1 = _j - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          _0 = (alo * blo) - err3;
          _k = (_i + _0);
          bvirt = (_k - _i);
          avirt = _k - bvirt;
          bround = _0 - bvirt;
          around = _i - avirt;
          u[1] = around + bround;
          u3 = (_j + _k);
          bvirt = u3 - _j;
          u[2] = _k - bvirt;
          u[3] = u3;
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, 4, u,
                                        finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;
        }
      }
    }
    if (cdxtail != 0.0) {
      if (adytail != 0.0) {
        cdxt_adyt1 = (cdxtail * adytail);
        c = (SPLITTER * cdxtail);
        abig = (c - cdxtail);
        ahi = c - abig;
        alo = cdxtail - ahi;
        c = (SPLITTER * adytail);
        abig = (c - adytail);
        bhi = c - abig;
        blo = adytail - bhi;
        err1 = cdxt_adyt1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        cdxt_adyt0 = (alo * blo) - err3;
        c = (SPLITTER * bdz);
        abig = (c - bdz);
        bhi = c - abig;
        blo = bdz - bhi;
        _i = (cdxt_adyt0 * bdz);
        c = (SPLITTER * cdxt_adyt0);
        abig = (c - cdxt_adyt0);
        ahi = c - abig;
        alo = cdxt_adyt0 - ahi;
        err1 = _i - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        u[0] = (alo * blo) - err3;
        _j = (cdxt_adyt1 * bdz);
        c = (SPLITTER * cdxt_adyt1);
        abig = (c - cdxt_adyt1);
        ahi = c - abig;
        alo = cdxt_adyt1 - ahi;
        err1 = _j - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        _0 = (alo * blo) - err3;
        _k = (_i + _0);
        bvirt = (_k - _i);
        avirt = _k - bvirt;
        bround = _0 - bvirt;
        around = _i - avirt;
        u[1] = around + bround;
        u3 = (_j + _k);
        bvirt = u3 - _j;
        u[2] = _k - bvirt;
        u[3] = u3;
        finlength = fast_expansion_sum_zeroelim(finlength, finnow, 4, u,
                                                finother);
        finswap = finnow;
        finnow = finother;
        finother = finswap;
        if (bdztail != 0.0) {
          c = (SPLITTER * bdztail);
          abig = (c - bdztail);
          bhi = c - abig;
          blo = bdztail - bhi;
          _i = (cdxt_adyt0 * bdztail);
          c = (SPLITTER * cdxt_adyt0);
          abig = (c - cdxt_adyt0);
          ahi = c - abig;
          alo = cdxt_adyt0 - ahi;
          err1 = _i - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          u[0] = (alo * blo) - err3;
          _j = (cdxt_adyt1 * bdztail);
          c = (SPLITTER * cdxt_adyt1);
          abig = (c - cdxt_adyt1);
          ahi = c - abig;
          alo = cdxt_adyt1 - ahi;
          err1 = _j - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          _0 = (alo * blo) - err3;
          _k = (_i + _0);
          bvirt = (_k - _i);
          avirt = _k - bvirt;
          bround = _0 - bvirt;
          around = _i - avirt;
          u[1] = around + bround;
          u3 = (_j + _k);
          bvirt = u3 - _j;
          u[2] = _k - bvirt;
          u[3] = u3;
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, 4, u,
                                        finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;
        }
      }
      if (bdytail != 0.0) {
        negate = -cdxtail;
        cdxt_bdyt1 = (negate * bdytail);
        c = (SPLITTER * negate);
        abig = (c - negate);
        ahi = c - abig;
        alo = negate - ahi;
        c = (SPLITTER * bdytail);
        abig = (c - bdytail);
        bhi = c - abig;
        blo = bdytail - bhi;
        err1 = cdxt_bdyt1 - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        cdxt_bdyt0 = (alo * blo) - err3;
        c = (SPLITTER * adz);
        abig = (c - adz);
        bhi = c - abig;
        blo = adz - bhi;
        _i = (cdxt_bdyt0 * adz);
        c = (SPLITTER * cdxt_bdyt0);
        abig = (c - cdxt_bdyt0);
        ahi = c - abig;
        alo = cdxt_bdyt0 - ahi;
        err1 = _i - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        u[0] = (alo * blo) - err3;
        _j = (cdxt_bdyt1 * adz);
        c = (SPLITTER * cdxt_bdyt1);
        abig = (c - cdxt_bdyt1);
        ahi = c - abig;
        alo = cdxt_bdyt1 - ahi;
        err1 = _j - (ahi * bhi);
        err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo);
        _0 = (alo * blo) - err3;
        _k = (_i + _0);
        bvirt = (_k - _i);
        avirt = _k - bvirt;
        bround = _0 - bvirt;
        around = _i - avirt;
        u[1] = around + bround;
        u3 = (_j + _k);
        bvirt = u3 - _j;
        u[2] = _k - bvirt;
        u[3] = u3;
        finlength = fast_expansion_sum_zeroelim(finlength, finnow, 4, u,
                                                finother);
        finswap = finnow;
        finnow = finother;
        finother = finswap;
        if (adztail != 0.0) {
          c = (SPLITTER * adztail);
          abig = (c - adztail);
          bhi = c - abig;
          blo = adztail - bhi;
          _i = (cdxt_bdyt0 * adztail);
          c = (SPLITTER * cdxt_bdyt0);
          abig = (c - cdxt_bdyt0);
          ahi = c - abig;
          alo = cdxt_bdyt0 - ahi;
          err1 = _i - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          u[0] = (alo * blo) - err3;
          _j = (cdxt_bdyt1 * adztail);
          c = (SPLITTER * cdxt_bdyt1);
          abig = (c - cdxt_bdyt1);
          ahi = c - abig;
          alo = cdxt_bdyt1 - ahi;
          err1 = _j - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          _0 = (alo * blo) - err3;
          _k = (_i + _0);
          bvirt = (_k - _i);
          avirt = _k - bvirt;
          bround = _0 - bvirt;
          around = _i - avirt;
          u[1] = around + bround;
          u3 = (_j + _k);
          bvirt = u3 - _j;
          u[2] = _k - bvirt;
          u[3] = u3;
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, 4, u,
                                        finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;
        }
      }
    }

    if (adztail != 0.0) {
      wlength = scale_expansion_zeroelim(bctlen, bct, adztail, w);
      finlength =
        fast_expansion_sum_zeroelim(finlength, finnow, wlength, w,
                                    finother);
      finswap = finnow;
      finnow = finother;
      finother = finswap;
    }
    if (bdztail != 0.0) {
      wlength = scale_expansion_zeroelim(catlen, cat, bdztail, w);
      finlength =
        fast_expansion_sum_zeroelim(finlength, finnow, wlength, w,
                                    finother);
      finswap = finnow;
      finnow = finother;
      finother = finswap;
    }
    if (cdztail != 0.0) {
      wlength = scale_expansion_zeroelim(abtlen, abt, cdztail, w);
      finlength =
        fast_expansion_sum_zeroelim(finlength, finnow, wlength, w,
                                    finother);
      //finswap = finnow;
      finnow = finother;
      // TODO: unused?
      //finother = finswap;
    }

    return finnow[finlength - 1];
  }


  //--------------------------------------------------------------------
  // inSphere
  //--------------------------------------------------------------------
  private static final double isperrboundB =
    (5.0 + 72.0 * EPSILON) * EPSILON;
  private static final double isperrboundC =
    (71.0 + 1408.0 * EPSILON) * EPSILON * EPSILON;

  public final boolean inSphereExact () { return false; }

  public final double inSphere (final Vector3D pa,
                                final Vector3D pb,
                                final Vector3D pc,
                                final Vector3D pd,
                                final Vector3D pe) {
    return new DefaultMacro().inSphere(pa, pb, pc, pd, pe); }

  final double inSphere (final Vector3D pa,
                         final Vector3D pb,
                         final Vector3D pc,
                         final Vector3D pd,
                         final Vector3D pe,
                         final double permanent) {
    double aex, bex, cex, dex, aey, bey, cey, dey, aez, bez, cez, dez;
    double det, errbound;

    double aexbey1, bexaey1, bexcey1, cexbey1;
    double cexdey1, dexcey1, dexaey1, aexdey1;
    double aexcey1, cexaey1, bexdey1, dexbey1;
    double aexbey0, bexaey0, bexcey0, cexbey0;
    double cexdey0, dexcey0, dexaey0, aexdey0;
    double aexcey0, cexaey0, bexdey0, dexbey0;
    double[] ab = new double[4], bc = new double[4],
      cd = new double[4], da = new double[4], ac = new double[4],
      bd = new double[4];
    double ab3, bc3, cd3, da3, ac3, bd3;
    double abeps, bceps, cdeps, daeps, aceps, bdeps;
    double[] temp8a = new double[8],
      temp8b = new double[8], temp8c = new double[8],
      temp16 = new double[16], temp24 = new double[24],
      temp48 = new double[48];
    int temp8alen, temp8blen, temp8clen, temp16len, temp24len,
      temp48len;
    double[] xdet = new double[96], ydet = new double[96],
      zdet = new double[96], xydet = new double[192];
    int xlen, ylen, zlen, xylen;
    double[] adet = new double[288], bdet = new double[288],
      cdet = new double[288], ddet = new double[288];
    int alen, blen, clen, dlen;
    double[] abdet = new double[576], cddet = new double[576];
    int ablen, cdlen;
    double[] fin1 = new double[1152];
    int finlength;

    double aextail, bextail, cextail, dextail;
    double aeytail, beytail, ceytail, deytail;
    double aeztail, beztail, ceztail, deztail;

    double bvirt;
    double avirt, bround, around;
    double c;
    double abig;
    double ahi, alo, bhi, blo;
    double err1, err2, err3;
    double _i, _j;
    double _0;

    aex = (pa.getX() - pe.getX());
    bex = (pb.getX() - pe.getX());
    cex = (pc.getX() - pe.getX());
    dex = (pd.getX() - pe.getX());
    aey = (pa.getY() - pe.getY());
    bey = (pb.getY() - pe.getY());
    cey = (pc.getY() - pe.getY());
    dey = (pd.getY() - pe.getY());
    aez = (pa.getZ() - pe.getZ());
    bez = (pb.getZ() - pe.getZ());
    cez = (pc.getZ() - pe.getZ());
    dez = (pd.getZ() - pe.getZ());

    aexbey1 = (aex * bey);
    c = (SPLITTER * aex);
    abig = (c - aex);
    ahi = c - abig;
    alo = aex - ahi;
    c = (SPLITTER * bey);
    abig = (c - bey);
    bhi = c - abig;
    blo = bey - bhi;
    err1 = aexbey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    aexbey0 = (alo * blo) - err3;
    bexaey1 = (bex * aey);
    c = (SPLITTER * bex);
    abig = (c - bex);
    ahi = c - abig;
    alo = bex - ahi;
    c = (SPLITTER * aey);
    abig = (c - aey);
    bhi = c - abig;
    blo = aey - bhi;
    err1 = bexaey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    bexaey0 = (alo * blo) - err3;
    _i = (aexbey0 - bexaey0);
    bvirt = (aexbey0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - bexaey0;
    around = aexbey0 - avirt;
    ab[0] = around + bround;
    _j = (aexbey1 + _i);
    bvirt = (_j - aexbey1);
    avirt = _j - bvirt;
    bround = _i - bvirt;
    around = aexbey1 - avirt;
    _0 = around + bround;
    _i = (_0 - bexaey1);
    bvirt = (_0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - bexaey1;
    around = _0 - avirt;
    ab[1] = around + bround;
    ab3 = (_j + _i);
    bvirt = (ab3 - _j);
    avirt = ab3 - bvirt;
    bround = _i - bvirt;
    around = _j - avirt;
    ab[2] = around + bround;
    ab[3] = ab3;

    bexcey1 = (bex * cey);
    c = (SPLITTER * bex);
    abig = (c - bex);
    ahi = c - abig;
    alo = bex - ahi;
    c = (SPLITTER * cey);
    abig = (c - cey);
    bhi = c - abig;
    blo = cey - bhi;
    err1 = bexcey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    bexcey0 = (alo * blo) - err3;
    cexbey1 = (cex * bey);
    c = (SPLITTER * cex);
    abig = (c - cex);
    ahi = c - abig;
    alo = cex - ahi;
    c = (SPLITTER * bey);
    abig = (c - bey);
    bhi = c - abig;
    blo = bey - bhi;
    err1 = cexbey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    cexbey0 = (alo * blo) - err3;
    _i = (bexcey0 - cexbey0);
    bvirt = (bexcey0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - cexbey0;
    around = bexcey0 - avirt;
    bc[0] = around + bround;
    _j = (bexcey1 + _i);
    bvirt = (_j - bexcey1);
    avirt = _j - bvirt;
    bround = _i - bvirt;
    around = bexcey1 - avirt;
    _0 = around + bround;
    _i = (_0 - cexbey1);
    bvirt = (_0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - cexbey1;
    around = _0 - avirt;
    bc[1] = around + bround;
    bc3 = (_j + _i);
    bvirt = (bc3 - _j);
    avirt = bc3 - bvirt;
    bround = _i - bvirt;
    around = _j - avirt;
    bc[2] = around + bround;
    bc[3] = bc3;

    cexdey1 = (cex * dey);
    c = (SPLITTER * cex);
    abig = (c - cex);
    ahi = c - abig;
    alo = cex - ahi;
    c = (SPLITTER * dey);
    abig = (c - dey);
    bhi = c - abig;
    blo = dey - bhi;
    err1 = cexdey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    cexdey0 = (alo * blo) - err3;
    dexcey1 = (dex * cey);
    c = (SPLITTER * dex);
    abig = (c - dex);
    ahi = c - abig;
    alo = dex - ahi;
    c = (SPLITTER * cey);
    abig = (c - cey);
    bhi = c - abig;
    blo = cey - bhi;
    err1 = dexcey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    dexcey0 = (alo * blo) - err3;
    _i = (cexdey0 - dexcey0);
    bvirt = (cexdey0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - dexcey0;
    around = cexdey0 - avirt;
    cd[0] = around + bround;
    _j = (cexdey1 + _i);
    bvirt = (_j - cexdey1);
    avirt = _j - bvirt;
    bround = _i - bvirt;
    around = cexdey1 - avirt;
    _0 = around + bround;
    _i = (_0 - dexcey1);
    bvirt = (_0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - dexcey1;
    around = _0 - avirt;
    cd[1] = around + bround;
    cd3 = (_j + _i);
    bvirt = (cd3 - _j);
    avirt = cd3 - bvirt;
    bround = _i - bvirt;
    around = _j - avirt;
    cd[2] = around + bround;
    cd[3] = cd3;

    dexaey1 = (dex * aey);
    c = (SPLITTER * dex);
    abig = (c - dex);
    ahi = c - abig;
    alo = dex - ahi;
    c = (SPLITTER * aey);
    abig = (c - aey);
    bhi = c - abig;
    blo = aey - bhi;
    err1 = dexaey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    dexaey0 = (alo * blo) - err3;
    aexdey1 = (aex * dey);
    c = (SPLITTER * aex);
    abig = (c - aex);
    ahi = c - abig;
    alo = aex - ahi;
    c = (SPLITTER * dey);
    abig = (c - dey);
    bhi = c - abig;
    blo = dey - bhi;
    err1 = aexdey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    aexdey0 = (alo * blo) - err3;
    _i = (dexaey0 - aexdey0);
    bvirt = (dexaey0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - aexdey0;
    around = dexaey0 - avirt;
    da[0] = around + bround;
    _j = (dexaey1 + _i);
    bvirt = (_j - dexaey1);
    avirt = _j - bvirt;
    bround = _i - bvirt;
    around = dexaey1 - avirt;
    _0 = around + bround;
    _i = (_0 - aexdey1);
    bvirt = (_0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - aexdey1;
    around = _0 - avirt;
    da[1] = around + bround;
    da3 = (_j + _i);
    bvirt = (da3 - _j);
    avirt = da3 - bvirt;
    bround = _i - bvirt;
    around = _j - avirt;
    da[2] = around + bround;
    da[3] = da3;

    aexcey1 = (aex * cey);
    c = (SPLITTER * aex);
    abig = (c - aex);
    ahi = c - abig;
    alo = aex - ahi;
    c = (SPLITTER * cey);
    abig = (c - cey);
    bhi = c - abig;
    blo = cey - bhi;
    err1 = aexcey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    aexcey0 = (alo * blo) - err3;
    cexaey1 = (cex * aey);
    c = (SPLITTER * cex);
    abig = (c - cex);
    ahi = c - abig;
    alo = cex - ahi;
    c = (SPLITTER * aey);
    abig = (c - aey);
    bhi = c - abig;
    blo = aey - bhi;
    err1 = cexaey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    cexaey0 = (alo * blo) - err3;
    _i = (aexcey0 - cexaey0);
    bvirt = (aexcey0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - cexaey0;
    around = aexcey0 - avirt;
    ac[0] = around + bround;
    _j = (aexcey1 + _i);
    bvirt = (_j - aexcey1);
    avirt = _j - bvirt;
    bround = _i - bvirt;
    around = aexcey1 - avirt;
    _0 = around + bround;
    _i = (_0 - cexaey1);
    bvirt = (_0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - cexaey1;
    around = _0 - avirt;
    ac[1] = around + bround;
    ac3 = (_j + _i);
    bvirt = (ac3 - _j);
    avirt = ac3 - bvirt;
    bround = _i - bvirt;
    around = _j - avirt;
    ac[2] = around + bround;
    ac[3] = ac3;

    bexdey1 = (bex * dey);
    c = (SPLITTER * bex);
    abig = (c - bex);
    ahi = c - abig;
    alo = bex - ahi;
    c = (SPLITTER * dey);
    abig = (c - dey);
    bhi = c - abig;
    blo = dey - bhi;
    err1 = bexdey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    bexdey0 = (alo * blo) - err3;
    dexbey1 = (dex * bey);
    c = (SPLITTER * dex);
    abig = (c - dex);
    ahi = c - abig;
    alo = dex - ahi;
    c = (SPLITTER * bey);
    abig = (c - bey);
    bhi = c - abig;
    blo = bey - bhi;
    err1 = dexbey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    dexbey0 = (alo * blo) - err3;
    _i = (bexdey0 - dexbey0);
    bvirt = (bexdey0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - dexbey0;
    around = bexdey0 - avirt;
    bd[0] = around + bround;
    _j = (bexdey1 + _i);
    bvirt = (_j - bexdey1);
    avirt = _j - bvirt;
    bround = _i - bvirt;
    around = bexdey1 - avirt;
    _0 = around + bround;
    _i = (_0 - dexbey1);
    bvirt = (_0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - dexbey1;
    around = _0 - avirt;
    bd[1] = around + bround;
    bd3 = (_j + _i);
    bvirt = (bd3 - _j);
    avirt = bd3 - bvirt;
    bround = _i - bvirt;
    around = _j - avirt;
    bd[2] = around + bround;
    bd[3] = bd3;

    temp8alen = scale_expansion_zeroelim(4, cd, bez, temp8a);
    temp8blen = scale_expansion_zeroelim(4, bd, -cez, temp8b);
    temp8clen = scale_expansion_zeroelim(4, bc, dez, temp8c);
    temp16len = fast_expansion_sum_zeroelim(temp8alen, temp8a,
                                            temp8blen, temp8b, temp16);
    temp24len = fast_expansion_sum_zeroelim(temp8clen, temp8c,
                                            temp16len, temp16, temp24);
    temp48len =
      scale_expansion_zeroelim(temp24len, temp24, aex, temp48);
    xlen = scale_expansion_zeroelim(temp48len, temp48, -aex, xdet);
    temp48len =
      scale_expansion_zeroelim(temp24len, temp24, aey, temp48);
    ylen = scale_expansion_zeroelim(temp48len, temp48, -aey, ydet);
    temp48len =
      scale_expansion_zeroelim(temp24len, temp24, aez, temp48);
    zlen = scale_expansion_zeroelim(temp48len, temp48, -aez, zdet);
    xylen = fast_expansion_sum_zeroelim(xlen, xdet, ylen, ydet, xydet);
    alen = fast_expansion_sum_zeroelim(xylen, xydet, zlen, zdet, adet);

    temp8alen = scale_expansion_zeroelim(4, da, cez, temp8a);
    temp8blen = scale_expansion_zeroelim(4, ac, dez, temp8b);
    temp8clen = scale_expansion_zeroelim(4, cd, aez, temp8c);
    temp16len = fast_expansion_sum_zeroelim(temp8alen, temp8a,
                                            temp8blen, temp8b, temp16);
    temp24len = fast_expansion_sum_zeroelim(temp8clen, temp8c,
                                            temp16len, temp16, temp24);
    temp48len =
      scale_expansion_zeroelim(temp24len, temp24, bex, temp48);
    xlen = scale_expansion_zeroelim(temp48len, temp48, bex, xdet);
    temp48len =
      scale_expansion_zeroelim(temp24len, temp24, bey, temp48);
    ylen = scale_expansion_zeroelim(temp48len, temp48, bey, ydet);
    temp48len =
      scale_expansion_zeroelim(temp24len, temp24, bez, temp48);
    zlen = scale_expansion_zeroelim(temp48len, temp48, bez, zdet);
    xylen = fast_expansion_sum_zeroelim(xlen, xdet, ylen, ydet, xydet);
    blen = fast_expansion_sum_zeroelim(xylen, xydet, zlen, zdet, bdet);

    temp8alen = scale_expansion_zeroelim(4, ab, dez, temp8a);
    temp8blen = scale_expansion_zeroelim(4, bd, aez, temp8b);
    temp8clen = scale_expansion_zeroelim(4, da, bez, temp8c);
    temp16len = fast_expansion_sum_zeroelim(temp8alen, temp8a,
                                            temp8blen, temp8b, temp16);
    temp24len = fast_expansion_sum_zeroelim(temp8clen, temp8c,
                                            temp16len, temp16, temp24);
    temp48len =
      scale_expansion_zeroelim(temp24len, temp24, cex, temp48);
    xlen = scale_expansion_zeroelim(temp48len, temp48, -cex, xdet);
    temp48len =
      scale_expansion_zeroelim(temp24len, temp24, cey, temp48);
    ylen = scale_expansion_zeroelim(temp48len, temp48, -cey, ydet);
    temp48len =
      scale_expansion_zeroelim(temp24len, temp24, cez, temp48);
    zlen = scale_expansion_zeroelim(temp48len, temp48, -cez, zdet);
    xylen = fast_expansion_sum_zeroelim(xlen, xdet, ylen, ydet, xydet);
    clen = fast_expansion_sum_zeroelim(xylen, xydet, zlen, zdet, cdet);

    temp8alen = scale_expansion_zeroelim(4, bc, aez, temp8a);
    temp8blen = scale_expansion_zeroelim(4, ac, -bez, temp8b);
    temp8clen = scale_expansion_zeroelim(4, ab, cez, temp8c);
    temp16len = fast_expansion_sum_zeroelim(temp8alen, temp8a,
                                            temp8blen, temp8b, temp16);
    temp24len = fast_expansion_sum_zeroelim(temp8clen, temp8c,
                                            temp16len, temp16, temp24);
    temp48len =
      scale_expansion_zeroelim(temp24len, temp24, dex, temp48);
    xlen = scale_expansion_zeroelim(temp48len, temp48, dex, xdet);
    temp48len =
      scale_expansion_zeroelim(temp24len, temp24, dey, temp48);
    ylen = scale_expansion_zeroelim(temp48len, temp48, dey, ydet);
    temp48len =
      scale_expansion_zeroelim(temp24len, temp24, dez, temp48);
    zlen = scale_expansion_zeroelim(temp48len, temp48, dez, zdet);
    xylen = fast_expansion_sum_zeroelim(xlen, xdet, ylen, ydet, xydet);
    dlen = fast_expansion_sum_zeroelim(xylen, xydet, zlen, zdet, ddet);

    ablen = fast_expansion_sum_zeroelim(alen, adet, blen, bdet, abdet);
    cdlen = fast_expansion_sum_zeroelim(clen, cdet, dlen, ddet, cddet);
    finlength =
      fast_expansion_sum_zeroelim(ablen, abdet, cdlen, cddet, fin1);

    det = estimate(finlength, fin1);
    errbound = isperrboundB * permanent;
    if ((det >= errbound) || (-det >= errbound)) {
      return det;
    }

    bvirt = (pa.getX() - aex);
    avirt = aex + bvirt;
    bround = bvirt - pe.getX();
    around = pa.getX() - avirt;
    aextail = around + bround;
    bvirt = (pa.getY() - aey);
    avirt = aey + bvirt;
    bround = bvirt - pe.getY();
    around = pa.getY() - avirt;
    aeytail = around + bround;
    bvirt = (pa.getZ() - aez);
    avirt = aez + bvirt;
    bround = bvirt - pe.getZ();
    around = pa.getZ() - avirt;
    aeztail = around + bround;
    bvirt = (pb.getX() - bex);
    avirt = bex + bvirt;
    bround = bvirt - pe.getX();
    around = pb.getX() - avirt;
    bextail = around + bround;
    bvirt = (pb.getY() - bey);
    avirt = bey + bvirt;
    bround = bvirt - pe.getY();
    around = pb.getY() - avirt;
    beytail = around + bround;
    bvirt = (pb.getZ() - bez);
    avirt = bez + bvirt;
    bround = bvirt - pe.getZ();
    around = pb.getZ() - avirt;
    beztail = around + bround;
    bvirt = (pc.getX() - cex);
    avirt = cex + bvirt;
    bround = bvirt - pe.getX();
    around = pc.getX() - avirt;
    cextail = around + bround;
    bvirt = (pc.getY() - cey);
    avirt = cey + bvirt;
    bround = bvirt - pe.getY();
    around = pc.getY() - avirt;
    ceytail = around + bround;
    bvirt = (pc.getZ() - cez);
    avirt = cez + bvirt;
    bround = bvirt - pe.getZ();
    around = pc.getZ() - avirt;
    ceztail = around + bround;
    bvirt = (pd.getX() - dex);
    avirt = dex + bvirt;
    bround = bvirt - pe.getX();
    around = pd.getX() - avirt;
    dextail = around + bround;
    bvirt = (pd.getY() - dey);
    avirt = dey + bvirt;
    bround = bvirt - pe.getY();
    around = pd.getY() - avirt;
    deytail = around + bround;
    bvirt = (pd.getZ() - dez);
    avirt = dez + bvirt;
    bround = bvirt - pe.getZ();
    around = pd.getZ() - avirt;
    deztail = around + bround;
    if ((aextail == 0.0) && (aeytail == 0.0) && (aeztail == 0.0)
      && (bextail == 0.0) && (beytail == 0.0) && (beztail == 0.0)
      && (cextail == 0.0) && (ceytail == 0.0) && (ceztail == 0.0)
      && (dextail == 0.0) && (deytail == 0.0) && (deztail == 0.0)) {
      return det;
    }

    errbound =
      isperrboundC * permanent + resulterrbound * ((det) >= 0.0 ? (det)
                                                                :
                                                   -(det));
    abeps = (aex * beytail + bey * aextail)
      - (aey * bextail + bex * aeytail);
    bceps = (bex * ceytail + cey * bextail)
      - (bey * cextail + cex * beytail);
    cdeps = (cex * deytail + dey * cextail)
      - (cey * dextail + dex * ceytail);
    daeps = (dex * aeytail + aey * dextail)
      - (dey * aextail + aex * deytail);
    aceps = (aex * ceytail + cey * aextail)
      - (aey * cextail + cex * aeytail);
    bdeps = (bex * deytail + dey * bextail)
      - (bey * dextail + dex * beytail);
    det += (((bex * bex + bey * bey + bez * bez)
      * ((cez * daeps + dez * aceps + aez * cdeps)
      + (ceztail * da3 + deztail * ac3 + aeztail * cd3))
      + (dex * dex + dey * dey + dez * dez)
      * ((aez * bceps - bez * aceps + cez * abeps)
      + (aeztail * bc3 - beztail * ac3 + ceztail * ab3)))
      - ((aex * aex + aey * aey + aez * aez)
      * ((bez * cdeps - cez * bdeps + dez * bceps)
      + (beztail * cd3 - ceztail * bd3 + deztail * bc3))
      + (cex * cex + cey * cey + cez * cez)
      * ((dez * abeps + aez * bdeps + bez * daeps)
      + (deztail * ab3 + aeztail * bd3 + beztail * da3))))
      + 2.0 * (((bex * bextail + bey * beytail + bez * beztail)
      * (cez * da3 + dez * ac3 + aez * cd3)
      + (dex * dextail + dey * deytail + dez * deztail)
      * (aez * bc3 - bez * ac3 + cez * ab3))
      - ((aex * aextail + aey * aeytail + aez * aeztail)
      * (bez * cd3 - cez * bd3 + dez * bc3)
      + (cex * cextail + cey * ceytail + cez * ceztail)
      * (dez * ab3 + aez * bd3 + bez * da3)));
    if ((det >= errbound) || (-det >= errbound)) { return det; }

    return new ExactMacro().inSphere(pa, pb, pc, pd, pe); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public AdaptMacro () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
