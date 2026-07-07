package mop.java.geometry.triangle.macro;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.geometry.triangle.Triangle2D;
import mop.java.numbers.XDouble;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

import static mop.java.geometry.Expansion.SPLITTER;
import static mop.java.geometry.Expansion.scale_expansion_zeroelim;
import static mop.java.geometry.Expansion.fast_expansion_sum_zeroelim;

/**
 * More exact tests.  Robust.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-07
 */

// strictfp unnecessary for JDK17 and later
public final class SlowMacro extends Triangle2D {

  //--------------------------------------------------------------------

  public final boolean inCircleExact () { return true; }

  // from macro expanded C code:

  public final double inCircle (final Vector2D pd) {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();

    double adx, bdx, cdx, ady, bdy, cdy;
    double adxtail, bdxtail, cdxtail; double adytail, bdytail, cdytail;
    double negate, negatetail;
    double axby7, bxcy7, axcy7, bxay7, cxby7, cxay7;
    double[] axby = new double[8], bxcy = new double[8], axcy =
      new double[8], bxay = new double[8], cxby = new double[8], cxay =
      new double[8]; double[] temp16 = new double[16]; int temp16len;
    double[] detx = new double[32], detxx = new double[64], detxt =
      new double[32], detxxt = new double[64], detxtxt = new double[64];
    int xlen, xxlen, xtlen, xxtlen, xtxtlen;
    double[] x1 = new double[128], x2 = new double[192];
    int x1len, x2len;
    double[] dety = new double[32], detyy = new double[64], detyt =
      new double[32], detyyt = new double[64], detytyt = new double[64];
    int ylen, yylen, ytlen, yytlen, ytytlen;
    double[] y1 = new double[128], y2 = new double[192];
    int y1len, y2len;
    double[] adet = new double[384], bdet = new double[384], cdet =
      new double[384], abdet = new double[768], deter =
      new double[1152]; int alen, blen, clen, ablen;
    int i;

    double bvirt; double avirt, bround, around; double c; double abig;
    double a0hi, a0lo, a1hi, a1lo, bhi, blo; double err1, err2, err3;
    double _i, _j, _k, _l, _m, _n; double _0, _1, _2;

    adx = (pa.getX() - pd.getX());bvirt = (pa.getX() - adx);avirt = adx + bvirt;
    bround = bvirt - pd.getX();around = pa.getX() - avirt;
    adxtail = around + bround; ady = (pa.getY() - pd.getY());
    bvirt = (pa.getY() - ady); avirt = ady + bvirt; bround = bvirt - pd.getY();
    around = pa.getY() - avirt; adytail = around + bround;
    bdx = (pb.getX() - pd.getX());bvirt = (pb.getX() - bdx);avirt = bdx + bvirt;
    bround = bvirt - pd.getX();around = pb.getX() - avirt;
    bdxtail = around + bround; bdy = (pb.getY() - pd.getY());
    bvirt = (pb.getY() - bdy); avirt = bdy + bvirt; bround = bvirt - pd.getY();
    around = pb.getY() - avirt; bdytail = around + bround;
    cdx = (pc.getX() - pd.getX());bvirt = (pc.getX() - cdx);avirt = cdx + bvirt;
    bround = bvirt - pd.getX();around = pc.getX() - avirt;
    cdxtail = around + bround; cdy = (pc.getY() - pd.getY());
    bvirt = (pc.getY() - cdy); avirt = cdy + bvirt; bround = bvirt - pd.getY();
    around = pc.getY() - avirt; cdytail = around + bround;

    c = (SPLITTER * adxtail); abig = (c - adxtail); a0hi = c - abig;
    a0lo = adxtail - a0hi; c = (SPLITTER * bdytail);
    abig = (c - bdytail); bhi = c - abig; blo = bdytail - bhi;
    _i = (adxtail * bdytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    axby[0] = (a0lo * blo) - err3; c = (SPLITTER * adx);
    abig = (c - adx); a1hi = c - abig; a1lo = adx - a1hi;
    _j = (adx * bdytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * bdy); abig = (c - bdy);
    bhi = c - abig; blo = bdy - bhi; _i = (adxtail * bdy);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; axby[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (adx * bdy); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    axby[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    axby[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
    axby[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    axby[5] = around + bround; axby7 = (_m + _k); bvirt = (axby7 - _m);
    avirt = axby7 - bvirt; bround = _k - bvirt; around = _m - avirt;
    axby[6] = around + bround

    ; axby[7] = axby7; negate = -ady; negatetail = -adytail;
    c = (SPLITTER * bdxtail); abig = (c - bdxtail); a0hi = c - abig;
    a0lo = bdxtail - a0hi; c = (SPLITTER * negatetail);
    abig = (c - negatetail); bhi = c - abig; blo = negatetail - bhi;
    _i = (bdxtail * negatetail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    bxay[0] = (a0lo * blo) - err3; c = (SPLITTER * bdx);
    abig = (c - bdx); a1hi = c - abig; a1lo = bdx - a1hi;
    _j = (bdx * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * negate); abig = (c - negate);
    bhi = c - abig; blo = negate - bhi; _i = (bdxtail * negate);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; bxay[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (bdx * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    bxay[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    bxay[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
    bxay[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    bxay[5] = around + bround; bxay7 = (_m + _k); bvirt = (bxay7 - _m);
    avirt = bxay7 - bvirt; bround = _k - bvirt; around = _m - avirt;
    bxay[6] = around + bround

    ; bxay[7] = bxay7; c = (SPLITTER * bdxtail); abig = (c - bdxtail);
    a0hi = c - abig; a0lo = bdxtail - a0hi; c = (SPLITTER * cdytail);
    abig = (c - cdytail); bhi = c - abig; blo = cdytail - bhi;
    _i = (bdxtail * cdytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    bxcy[0] = (a0lo * blo) - err3; c = (SPLITTER * bdx);
    abig = (c - bdx); a1hi = c - abig; a1lo = bdx - a1hi;
    _j = (bdx * cdytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * cdy); abig = (c - cdy);
    bhi = c - abig; blo = cdy - bhi; _i = (bdxtail * cdy);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; bxcy[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (bdx * cdy); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    bxcy[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    bxcy[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
    bxcy[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    bxcy[5] = around + bround; bxcy7 = (_m + _k); bvirt = (bxcy7 - _m);
    avirt = bxcy7 - bvirt; bround = _k - bvirt; around = _m - avirt;
    bxcy[6] = around + bround

    ; bxcy[7] = bxcy7; negate = -bdy; negatetail = -bdytail;
    c = (SPLITTER * cdxtail); abig = (c - cdxtail); a0hi = c - abig;
    a0lo = cdxtail - a0hi; c = (SPLITTER * negatetail);
    abig = (c - negatetail); bhi = c - abig; blo = negatetail - bhi;
    _i = (cdxtail * negatetail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    cxby[0] = (a0lo * blo) - err3; c = (SPLITTER * cdx);
    abig = (c - cdx); a1hi = c - abig; a1lo = cdx - a1hi;
    _j = (cdx * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * negate); abig = (c - negate);
    bhi = c - abig; blo = negate - bhi; _i = (cdxtail * negate);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; cxby[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (cdx * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    cxby[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    cxby[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
    cxby[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    cxby[5] = around + bround; cxby7 = (_m + _k); bvirt = (cxby7 - _m);
    avirt = cxby7 - bvirt; bround = _k - bvirt; around = _m - avirt;
    cxby[6] = around + bround

    ; cxby[7] = cxby7; c = (SPLITTER * cdxtail); abig = (c - cdxtail);
    a0hi = c - abig; a0lo = cdxtail - a0hi; c = (SPLITTER * adytail);
    abig = (c - adytail); bhi = c - abig; blo = adytail - bhi;
    _i = (cdxtail * adytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    cxay[0] = (a0lo * blo) - err3; c = (SPLITTER * cdx);
    abig = (c - cdx); a1hi = c - abig; a1lo = cdx - a1hi;
    _j = (cdx * adytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * ady); abig = (c - ady);
    bhi = c - abig; blo = ady - bhi; _i = (cdxtail * ady);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; cxay[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (cdx * ady); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    cxay[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    cxay[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
    cxay[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    cxay[5] = around + bround; cxay7 = (_m + _k); bvirt = (cxay7 - _m);
    avirt = cxay7 - bvirt; bround = _k - bvirt; around = _m - avirt;
    cxay[6] = around + bround

    ; cxay[7] = cxay7; negate = -cdy; negatetail = -cdytail;
    c = (SPLITTER * adxtail); abig = (c - adxtail); a0hi = c - abig;
    a0lo = adxtail - a0hi; c = (SPLITTER * negatetail);
    abig = (c - negatetail); bhi = c - abig; blo = negatetail - bhi;
    _i = (adxtail * negatetail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    axcy[0] = (a0lo * blo) - err3; c = (SPLITTER * adx);
    abig = (c - adx); a1hi = c - abig; a1lo = adx - a1hi;
    _j = (adx * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * negate); abig = (c - negate);
    bhi = c - abig; blo = negate - bhi; _i = (adxtail * negate);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; axcy[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (adx * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    axcy[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    axcy[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
    axcy[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    axcy[5] = around + bround; axcy7 = (_m + _k); bvirt = (axcy7 - _m);
    avirt = axcy7 - bvirt; bround = _k - bvirt; around = _m - avirt;
    axcy[6] = around + bround

    ; axcy[7] = axcy7;

    temp16len = fast_expansion_sum_zeroelim(8, bxcy, 8, cxby, temp16);

    xlen = scale_expansion_zeroelim(temp16len, temp16, adx, detx);
    xxlen = scale_expansion_zeroelim(xlen, detx, adx, detxx);
    xtlen = scale_expansion_zeroelim(temp16len, temp16, adxtail, detxt);
    xxtlen = scale_expansion_zeroelim(xtlen, detxt, adx, detxxt);
    for (i = 0; i < xxtlen; i++) {
      detxxt[i] *= 2.0;
    }
    xtxtlen = scale_expansion_zeroelim(xtlen, detxt, adxtail, detxtxt);
    x1len =
      fast_expansion_sum_zeroelim(xxlen, detxx, xxtlen, detxxt, x1);
    x2len =
      fast_expansion_sum_zeroelim(x1len, x1, xtxtlen, detxtxt, x2);

    ylen = scale_expansion_zeroelim(temp16len, temp16, ady, dety);
    yylen = scale_expansion_zeroelim(ylen, dety, ady, detyy);
    ytlen = scale_expansion_zeroelim(temp16len, temp16, adytail, detyt);
    yytlen = scale_expansion_zeroelim(ytlen, detyt, ady, detyyt);
    for (i = 0; i < yytlen; i++) {
      detyyt[i] *= 2.0;
    }
    ytytlen = scale_expansion_zeroelim(ytlen, detyt, adytail, detytyt);
    y1len =
      fast_expansion_sum_zeroelim(yylen, detyy, yytlen, detyyt, y1);
    y2len =
      fast_expansion_sum_zeroelim(y1len, y1, ytytlen, detytyt, y2);

    alen = fast_expansion_sum_zeroelim(x2len, x2, y2len, y2, adet);

    temp16len = fast_expansion_sum_zeroelim(8, cxay, 8, axcy, temp16);

    xlen = scale_expansion_zeroelim(temp16len, temp16, bdx, detx);
    xxlen = scale_expansion_zeroelim(xlen, detx, bdx, detxx);
    xtlen = scale_expansion_zeroelim(temp16len, temp16, bdxtail, detxt);
    xxtlen = scale_expansion_zeroelim(xtlen, detxt, bdx, detxxt);
    for (i = 0; i < xxtlen; i++) {
      detxxt[i] *= 2.0;
    }
    xtxtlen = scale_expansion_zeroelim(xtlen, detxt, bdxtail, detxtxt);
    x1len =
      fast_expansion_sum_zeroelim(xxlen, detxx, xxtlen, detxxt, x1);
    x2len =
      fast_expansion_sum_zeroelim(x1len, x1, xtxtlen, detxtxt, x2);

    ylen = scale_expansion_zeroelim(temp16len, temp16, bdy, dety);
    yylen = scale_expansion_zeroelim(ylen, dety, bdy, detyy);
    ytlen = scale_expansion_zeroelim(temp16len, temp16, bdytail, detyt);
    yytlen = scale_expansion_zeroelim(ytlen, detyt, bdy, detyyt);
    for (i = 0; i < yytlen; i++) {
      detyyt[i] *= 2.0;
    }
    ytytlen = scale_expansion_zeroelim(ytlen, detyt, bdytail, detytyt);
    y1len =
      fast_expansion_sum_zeroelim(yylen, detyy, yytlen, detyyt, y1);
    y2len =
      fast_expansion_sum_zeroelim(y1len, y1, ytytlen, detytyt, y2);

    blen = fast_expansion_sum_zeroelim(x2len, x2, y2len, y2, bdet);

    temp16len = fast_expansion_sum_zeroelim(8, axby, 8, bxay, temp16);

    xlen = scale_expansion_zeroelim(temp16len, temp16, cdx, detx);
    xxlen = scale_expansion_zeroelim(xlen, detx, cdx, detxx);
    xtlen = scale_expansion_zeroelim(temp16len, temp16, cdxtail, detxt);
    xxtlen = scale_expansion_zeroelim(xtlen, detxt, cdx, detxxt);
    for (i = 0; i < xxtlen; i++) {
      detxxt[i] *= 2.0;
    }
    xtxtlen = scale_expansion_zeroelim(xtlen, detxt, cdxtail, detxtxt);
    x1len =
      fast_expansion_sum_zeroelim(xxlen, detxx, xxtlen, detxxt, x1);
    x2len =
      fast_expansion_sum_zeroelim(x1len, x1, xtxtlen, detxtxt, x2);

    ylen = scale_expansion_zeroelim(temp16len, temp16, cdy, dety);
    yylen = scale_expansion_zeroelim(ylen, dety, cdy, detyy);
    ytlen = scale_expansion_zeroelim(temp16len, temp16, cdytail, detyt);
    yytlen = scale_expansion_zeroelim(ytlen, detyt, cdy, detyyt);
    for (i = 0; i < yytlen; i++) {
      detyyt[i] *= 2.0;
    }
    ytytlen = scale_expansion_zeroelim(ytlen, detyt, cdytail, detytyt);
    y1len =
      fast_expansion_sum_zeroelim(yylen, detyy, yytlen, detyyt, y1);
    y2len =
      fast_expansion_sum_zeroelim(y1len, y1, ytytlen, detytyt, y2);

    clen = fast_expansion_sum_zeroelim(x2len, x2, y2len, y2, cdet);

    ablen = fast_expansion_sum_zeroelim(alen, adet, blen, bdet, abdet);
    fast_expansion_sum_zeroelim(ablen, abdet, clen, cdet, deter);

   return XDouble.unsafe(deter).doubleValue(); }

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return true; }

  // TODO: seems to return 2xsigned area
  public final double signedArea () {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();

    double acx, acy, bcx, bcy; double acxtail, acytail;
    double bcxtail, bcytail; double negate, negatetail;
    double[] axby = new double[8]; double[] bxay = new double[8];
    double axby7, bxay7; double[] deter = new double[16];

    double bvirt; double avirt, bround, around; double c; double abig;
    double a0hi, a0lo, a1hi, a1lo, bhi, blo; double err1, err2, err3;
    double _i, _j, _k, _l, _m, _n; double _0, _1, _2;

    acx = (pa.getX() - pc.getX()); bvirt = (pa.getX() - acx); avirt = acx + bvirt;
    bround = bvirt - pc.getX(); around = pa.getX() - avirt;
    acxtail = around + bround; acy = (pa.getY() - pc.getY());
    bvirt = (pa.getY() - acy); avirt = acy + bvirt; bround = bvirt - pc.getY();
    around = pa.getY() - avirt; acytail = around + bround;
    bcx = (pb.getX() - pc.getX()); bvirt = (pb.getX() - bcx); avirt = bcx + bvirt;
    bround = bvirt - pc.getX(); around = pb.getX() - avirt;
    bcxtail = around + bround; bcy = (pb.getY() - pc.getY());
    bvirt = (pb.getY() - bcy); avirt = bcy + bvirt; bround = bvirt - pc.getY();
    around = pb.getY() - avirt; bcytail = around + bround;

    c = (SPLITTER * acxtail); abig = (c - acxtail); a0hi = c - abig;
    a0lo = acxtail - a0hi; c = (SPLITTER * bcytail);
    abig = (c - bcytail); bhi = c - abig; blo = bcytail - bhi;
    _i = (acxtail * bcytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    axby[0] = (a0lo * blo) - err3; c = (SPLITTER * acx);
    abig = (c - acx); a1hi = c - abig; a1lo = acx - a1hi;
    _j = (acx * bcytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * bcy); abig = (c - bcy);
    bhi = c - abig; blo = bcy - bhi; _i = (acxtail * bcy);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; axby[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (acx * bcy); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    axby[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    axby[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
    axby[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    axby[5] = around + bround; axby7 = (_m + _k); bvirt = (axby7 - _m);
    avirt = axby7 - bvirt; bround = _k - bvirt; around = _m - avirt;
    axby[6] = around + bround; axby[7] = axby7; negate = -acy;
    negatetail = -acytail; c = (SPLITTER * bcxtail);
    abig = (c - bcxtail); a0hi = c - abig; a0lo = bcxtail - a0hi;
    c = (SPLITTER * negatetail); abig = (c - negatetail);
    bhi = c - abig; blo = negatetail - bhi; _i = (bcxtail * negatetail);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); bxay[0] = (a0lo * blo) - err3;
    c = (SPLITTER * bcx); abig = (c - bcx); a1hi = c - abig;
    a1lo = bcx - a1hi; _j = (bcx * negatetail);
    err1 = _j - (a1hi * bhi); err2 = err1 - (a1lo * bhi);
    err3 = err2 - (a1hi * blo); _0 = (a1lo * blo) - err3;
    _k = (_i + _0); bvirt = (_k - _i); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _i - avirt; _1 = around + bround;
    _l = (_j + _k); bvirt = _l - _j; _2 = _k - bvirt;
    c = (SPLITTER * negate); abig = (c - negate); bhi = c - abig;
    blo = negate - bhi; _i = (bcxtail * negate);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; bxay[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (bcx * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    bxay[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    bxay[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
    bxay[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    bxay[5] = around + bround; bxay7 = (_m + _k); bvirt = (bxay7 - _m);
    avirt = bxay7 - bvirt; bround = _k - bvirt; around = _m - avirt;
    bxay[6] = around + bround; bxay[7] = bxay7;

    fast_expansion_sum_zeroelim(8, axby, 8, bxay, deter);

    return XDouble.unsafe(deter).doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private SlowMacro (final Vector2D a,
                final Vector2D b,
                final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new SlowMacro(a, b, c); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
