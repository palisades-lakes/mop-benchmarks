package mop.java.geometry.predicates.macro;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.geometry.predicates.Predicate;
import mop.java.numbers.XDouble;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

import static mop.java.geometry.predicates.macro.Expansion.SPLITTER;
import static mop.java.geometry.predicates.macro.Expansion.scale_expansion_zeroelim;
import static mop.java.geometry.predicates.macro.Expansion.fast_expansion_sum_zeroelim;

/**
 * More exact tests.  Robust.
 * <br>
 * Adaptive precision floating point based on:
 * <ul>
 * <li><a href="https://www.cs.cmu.edu/~quake/robust.html">
 * Jonathan Shewchuk, website:
 * Adaptive Precision Floating-Point Arithmetic and Fast Robust
 * mop.java.numbers.predicates.Predicates for Computational Geometry
 * </a></li>
 * <li>
 * <a href="https://www.cs.cmu.edu/afs/cs/project/quake/public/code/predicates.c">
 * Jonathan Shewchuk, predicates.c
 * </a></li>
 * <li>
 * <a href="https://github.com/libigl/libigl-predicates/blob/master/predicates.c">
 * libigl-predicates github
 * </a></li>
 * <li><a href="https://link.springer.com/article/10.1007/PL00009321">
 * Jonathan Shewchuk, 1997,
 * Adaptive Precision Floating-Point Arithmetic and Fast Robust
 * mop.java.numbers.predicates.Predicates for Computational Geometry
 * (53 pages, published)
 * </a></li>
 * <li>
 * <a href="https://people.eecs.berkeley.edu/~jrs/papers/robustr.pdf">
 * Jonathan Shewchuk, 1997,
 * Adaptive Precision Floating-Point Arithmetic and Fast Robust
 * mop.java.numbers.predicates.Predicates for Computational Geometry
 * (59 pages, tech report)
 * </a></li>
 * <li>
 * <a href="https://people.eecs.berkeley.edu/~jrs/papers/robust-predicates.pdf">
 * Jonathan Shewchuk, 1996,
 * Robust adaptive floating-point geometric predicates,
 * </a>
 * </li>
 * <li><a href="https://dl.acm.org/doi/10.1145/237218.237337">
 * Jonathan Shewchuk, 1996,
 * Robust adaptive floating-point geometric predicates,
 * SCG '96: Proceedings of the twelfth annual symposium on
 * Computational geometry,
 * (10 pages)
 * </a?</li>
 * *</ul>
 * <p>
 * Data Structures:
 * <a href="https://github.com/carrotsearch/hppc">hppc</a>
 * </p>
 * <p>
 *   This version's priority is correctness, and simplicity.
 *   Later versions can optimize guided by benchmarks and
 *   profiling.
 * </p>
 * <p>
 *   Basic idea: a finite subset of the rationals is represented
 *   by an implied
 *   sum of <i>non-overlapping</i> <code>double</code> terms.
 *   This set has the same range as the set of <code>double</code>s,
 *   with finer precision.
 *   Finite cardinality because <code>double</code> is finite
 *   and the number of terms is limited by the maximum array length.
 *   <br>
 *   TODO: work out the precision: equivalent number of bits
 *   <br>
 *   TODO: what is the maximum number of non-overlapping terms?
 *   <br>
 *   Possible extension: add an exponent (<code>long</code> or
 *   even <code>BigInteger</code> to extend range.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-04
 */

// strictfp unnecessary for JDK17 and later
@SuppressWarnings("unused")
public final class SlowMacro implements Predicate {

  //--------------------------------------------------------------------

  public final boolean isExact () { return true; }

  //--------------------------------------------------------------------
  // incircle
  //--------------------------------------------------------------------
  // from macro expanded C code:

  public final double incircle (final Vector2D pa, final Vector2D pb,
                                final Vector2D pc, final Vector2D pd) {
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

    adx = (pa.getX() - pd.getX()); bvirt = (pa.getX() - adx); avirt = adx + bvirt;
    bround = bvirt - pd.getX(); around = pa.getX() - avirt;
    adxtail = around + bround; ady = (pa.getY() - pd.getY());
    bvirt = (pa.getY() - ady); avirt = ady + bvirt; bround = bvirt - pd.getY();
    around = pa.getY() - avirt; adytail = around + bround;
    bdx = (pb.getX() - pd.getX()); bvirt = (pb.getX() - bdx); avirt = bdx + bvirt;
    bround = bvirt - pd.getX(); around = pb.getX() - avirt;
    bdxtail = around + bround; bdy = (pb.getY() - pd.getY());
    bvirt = (pb.getY() - bdy); avirt = bdy + bvirt; bround = bvirt - pd.getY();
    around = pb.getY() - avirt; bdytail = around + bround;
    cdx = (pc.getX() - pd.getX()); bvirt = (pc.getX() - cdx); avirt = cdx + bvirt;
    bround = bvirt - pd.getX(); around = pc.getX() - avirt;
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
  // orient2d
  //--------------------------------------------------------------------
  // TODO: seems to return 2xsigned area
  public final double signedArea (final Vector2D pa,
                                  final Vector2D pb,
                                  final Vector2D pc) {
    double acx, acy, bcx, bcy; double acxtail, acytail;
    double bcxtail, bcytail; double negate, negatetail;
    double[] axby = new double[8]; double[] bxay = new double[8];
    double axby7, bxay7; double[] deter = new double[16]; int deterlen;

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

    return XDouble.unsafe(deter).doubleValue();
  }

  //--------------------------------------------------------------------
  // orient3d
  //--------------------------------------------------------------------
  public final double signedVolume (final Vector3D pa, final Vector3D pb,
                                    final Vector3D pc, final Vector3D pd) {
    double adx, ady, adz, bdx, bdy, bdz, cdx, cdy, cdz;
    double adxtail, adytail, adztail; double bdxtail, bdytail, bdztail;
    double cdxtail, cdytail, cdztail; double negate, negatetail;
    double axby7, bxcy7, axcy7, bxay7, cxby7, cxay7;
    double[] axby = new double[8], bxcy = new double[8], axcy =
      new double[8], bxay = new double[8], cxby = new double[8], cxay =
      new double[8];
    double[] temp16 = new double[16], temp32 = new double[32], temp32t =
      new double[32]; int temp16len, temp32len, temp32tlen;
    double[] adet = new double[64], bdet = new double[64], cdet =
      new double[64]; int alen, blen, clen;
    double[] abdet = new double[128]; int ablen;
    double[] deter = new double[192]; int deterlen;

    double bvirt; double avirt, bround, around; double c; double abig;
    double a0hi, a0lo, a1hi, a1lo, bhi, blo; double err1, err2, err3;
    double _i, _j, _k, _l, _m, _n; double _0, _1, _2;

    adx = (pa.getX() - pd.getX()); bvirt = (pa.getX() - adx); avirt = adx + bvirt;
    bround = bvirt - pd.getX(); around = pa.getX() - avirt;
    adxtail = around + bround; ady = (pa.getY() - pd.getY());
    bvirt = (pa.getY() - ady); avirt = ady + bvirt; bround = bvirt - pd.getY();
    around = pa.getY() - avirt; adytail = around + bround;
    adz = (pa.getZ() - pd.getZ()); bvirt = (pa.getZ() - adz); avirt = adz + bvirt;
    bround = bvirt - pd.getZ(); around = pa.getZ() - avirt;
    adztail = around + bround; bdx = (pb.getX() - pd.getX());
    bvirt = (pb.getX() - bdx); avirt = bdx + bvirt; bround = bvirt - pd.getX();
    around = pb.getX() - avirt; bdxtail = around + bround;
    bdy = (pb.getY() - pd.getY()); bvirt = (pb.getY() - bdy); avirt = bdy + bvirt;
    bround = bvirt - pd.getY(); around = pb.getY() - avirt;
    bdytail = around + bround; bdz = (pb.getZ() - pd.getZ());
    bvirt = (pb.getZ() - bdz); avirt = bdz + bvirt; bround = bvirt - pd.getZ();
    around = pb.getZ() - avirt; bdztail = around + bround;
    cdx = (pc.getX() - pd.getX()); bvirt = (pc.getX() - cdx); avirt = cdx + bvirt;
    bround = bvirt - pd.getX(); around = pc.getX() - avirt;
    cdxtail = around + bround; cdy = (pc.getY() - pd.getY());
    bvirt = (pc.getY() - cdy); avirt = cdy + bvirt; bround = bvirt - pd.getY();
    around = pc.getY() - avirt; cdytail = around + bround;
    cdz = (pc.getZ() - pd.getZ()); bvirt = (pc.getZ() - cdz); avirt = cdz + bvirt;
    bround = bvirt - pd.getZ(); around = pc.getZ() - avirt;
    cdztail = around + bround;

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
    temp32len =
      scale_expansion_zeroelim(temp16len, temp16, adz, temp32);
    temp32tlen =
      scale_expansion_zeroelim(temp16len, temp16, adztail, temp32t);
    alen = fast_expansion_sum_zeroelim(temp32len, temp32, temp32tlen,
                                       temp32t, adet);

    temp16len = fast_expansion_sum_zeroelim(8, cxay, 8, axcy, temp16);
    temp32len =
      scale_expansion_zeroelim(temp16len, temp16, bdz, temp32);
    temp32tlen =
      scale_expansion_zeroelim(temp16len, temp16, bdztail, temp32t);
    blen = fast_expansion_sum_zeroelim(temp32len, temp32, temp32tlen,
                                       temp32t, bdet);

    temp16len = fast_expansion_sum_zeroelim(8, axby, 8, bxay, temp16);
    temp32len =
      scale_expansion_zeroelim(temp16len, temp16, cdz, temp32);
    temp32tlen =
      scale_expansion_zeroelim(temp16len, temp16, cdztail, temp32t);
    clen = fast_expansion_sum_zeroelim(temp32len, temp32, temp32tlen,
                                       temp32t, cdet);

    ablen = fast_expansion_sum_zeroelim(alen, adet, blen, bdet, abdet);
    fast_expansion_sum_zeroelim(ablen, abdet, clen, cdet, deter);

    return XDouble.unsafe(deter).doubleValue();
  }

  //--------------------------------------------------------------------
  // insphere
  //--------------------------------------------------------------------
  public final double insphere (final Vector3D pa, final Vector3D pb,
                                final Vector3D pc, final Vector3D pd,
                                final Vector3D pe) {
    double aex, bex, cex, dex, aey, bey, cey, dey, aez, bez, cez, dez;
    double aextail, bextail, cextail, dextail;
    double aeytail, beytail, ceytail, deytail;
    double aeztail, beztail, ceztail, deztail;
    double negate, negatetail;
    double axby7, bxcy7, cxdy7, dxay7, axcy7, bxdy7;
    double bxay7, cxby7, dxcy7, axdy7, cxay7, dxby7;
    double[] axby = new double[8], bxcy = new double[8], cxdy =
      new double[8], dxay = new double[8], axcy = new double[8], bxdy =
      new double[8];
    double[] bxay = new double[8], cxby = new double[8], dxcy =
      new double[8], axdy = new double[8], cxay = new double[8], dxby =
      new double[8];
    double[] ab = new double[16], bc = new double[16], cd =
      new double[16], da = new double[16], ac = new double[16], bd =
      new double[16]; int ablen, bclen, cdlen, dalen, aclen, bdlen;
    double[] temp32a = new double[32], temp32b = new double[32],
      temp64a = new double[64], temp64b = new double[64], temp64c =
      new double[64];
    int temp32alen, temp32blen, temp64alen, temp64blen, temp64clen;
    double[] temp128 = new double[128], temp192 = new double[192];
    int temp128len, temp192len;
    double[] detx = new double[384], detxx = new double[768], detxt =
      new double[384], detxxt = new double[768], detxtxt =
      new double[768]; int xlen, xxlen, xtlen, xxtlen, xtxtlen;
    double[] x1 = new double[1536], x2 = new double[2304];
    int x1len, x2len;
    double[] dety = new double[384], detyy = new double[768], detyt =
      new double[384], detyyt = new double[768], detytyt =
      new double[768]; int ylen, yylen, ytlen, yytlen, ytytlen;
    double[] y1 = new double[1536], y2 = new double[2304];
    int y1len, y2len;
    double[] detz = new double[384], detzz = new double[768], detzt =
      new double[384], detzzt = new double[768], detztzt =
      new double[768]; int zlen, zzlen, ztlen, zztlen, ztztlen;
    double[] z1 = new double[1536], z2 = new double[2304];
    int z1len, z2len; double[] detxy = new double[4608]; int xylen;
    double[] adet = new double[6912], bdet = new double[6912], cdet =
      new double[6912], ddet = new double[6912];
    int alen, blen, clen, dlen;
    double[] abdet = new double[13824], cddet = new double[13824],
      deter = new double[27648]; int deterlen; int i;

    double bvirt; double avirt, bround, around; double c; double abig;
    double a0hi, a0lo, a1hi, a1lo, bhi, blo; double err1, err2, err3;
    double _i, _j, _k, _l, _m, _n; double _0, _1, _2;

    aex = (pa.getX() - pe.getX()); bvirt = (pa.getX() - aex); avirt = aex + bvirt;
    bround = bvirt - pe.getX(); around = pa.getX() - avirt;
    aextail = around + bround; aey = (pa.getY() - pe.getY());
    bvirt = (pa.getY() - aey); avirt = aey + bvirt; bround = bvirt - pe.getY();
    around = pa.getY() - avirt; aeytail = around + bround;
    aez = (pa.getZ() - pe.getZ()); bvirt = (pa.getZ() - aez); avirt = aez + bvirt;
    bround = bvirt - pe.getZ(); around = pa.getZ() - avirt;
    aeztail = around + bround; bex = (pb.getX() - pe.getX());
    bvirt = (pb.getX() - bex); avirt = bex + bvirt; bround = bvirt - pe.getX();
    around = pb.getX() - avirt; bextail = around + bround;
    bey = (pb.getY() - pe.getY()); bvirt = (pb.getY() - bey); avirt = bey + bvirt;
    bround = bvirt - pe.getY(); around = pb.getY() - avirt;
    beytail = around + bround; bez = (pb.getZ() - pe.getZ());
    bvirt = (pb.getZ() - bez); avirt = bez + bvirt; bround = bvirt - pe.getZ();
    around = pb.getZ() - avirt; beztail = around + bround;
    cex = (pc.getX() - pe.getX()); bvirt = (pc.getX() - cex); avirt = cex + bvirt;
    bround = bvirt - pe.getX(); around = pc.getX() - avirt;
    cextail = around + bround; cey = (pc.getY() - pe.getY());
    bvirt = (pc.getY() - cey); avirt = cey + bvirt; bround = bvirt - pe.getY();
    around = pc.getY() - avirt; ceytail = around + bround;
    cez = (pc.getZ() - pe.getZ()); bvirt = (pc.getZ() - cez); avirt = cez + bvirt;
    bround = bvirt - pe.getZ(); around = pc.getZ() - avirt;
    ceztail = around + bround; dex = (pd.getX() - pe.getX());
    bvirt = (pd.getX() - dex); avirt = dex + bvirt; bround = bvirt - pe.getX();
    around = pd.getX() - avirt; dextail = around + bround;
    dey = (pd.getY() - pe.getY()); bvirt = (pd.getY() - dey); avirt = dey + bvirt;
    bround = bvirt - pe.getY(); around = pd.getY() - avirt;
    deytail = around + bround; dez = (pd.getZ() - pe.getZ());
    bvirt = (pd.getZ() - dez); avirt = dez + bvirt; bround = bvirt - pe.getZ();
    around = pd.getZ() - avirt; deztail = around + bround;

    c = (SPLITTER * aextail); abig = (c - aextail); a0hi = c - abig;
    a0lo = aextail - a0hi; c = (SPLITTER * beytail);
    abig = (c - beytail); bhi = c - abig; blo = beytail - bhi;
    _i = (aextail * beytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    axby[0] = (a0lo * blo) - err3; c = (SPLITTER * aex);
    abig = (c - aex); a1hi = c - abig; a1lo = aex - a1hi;
    _j = (aex * beytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * bey); abig = (c - bey);
    bhi = c - abig; blo = bey - bhi; _i = (aextail * bey);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; axby[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (aex * bey); err1 = _j - (a1hi * bhi);
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

    ; axby[7] = axby7; negate = -aey; negatetail = -aeytail;
    c = (SPLITTER * bextail); abig = (c - bextail); a0hi = c - abig;
    a0lo = bextail - a0hi; c = (SPLITTER * negatetail);
    abig = (c - negatetail); bhi = c - abig; blo = negatetail - bhi;
    _i = (bextail * negatetail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    bxay[0] = (a0lo * blo) - err3; c = (SPLITTER * bex);
    abig = (c - bex); a1hi = c - abig; a1lo = bex - a1hi;
    _j = (bex * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * negate); abig = (c - negate);
    bhi = c - abig; blo = negate - bhi; _i = (bextail * negate);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; bxay[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (bex * negate); err1 = _j - (a1hi * bhi);
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

    ; bxay[7] = bxay7;
    ablen = fast_expansion_sum_zeroelim(8, axby, 8, bxay, ab);
    c = (SPLITTER * bextail); abig = (c - bextail); a0hi = c - abig;
    a0lo = bextail - a0hi; c = (SPLITTER * ceytail);
    abig = (c - ceytail); bhi = c - abig; blo = ceytail - bhi;
    _i = (bextail * ceytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    bxcy[0] = (a0lo * blo) - err3; c = (SPLITTER * bex);
    abig = (c - bex); a1hi = c - abig; a1lo = bex - a1hi;
    _j = (bex * ceytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * cey); abig = (c - cey);
    bhi = c - abig; blo = cey - bhi; _i = (bextail * cey);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; bxcy[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (bex * cey); err1 = _j - (a1hi * bhi);
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

    ; bxcy[7] = bxcy7; negate = -bey; negatetail = -beytail;
    c = (SPLITTER * cextail); abig = (c - cextail); a0hi = c - abig;
    a0lo = cextail - a0hi; c = (SPLITTER * negatetail);
    abig = (c - negatetail); bhi = c - abig; blo = negatetail - bhi;
    _i = (cextail * negatetail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    cxby[0] = (a0lo * blo) - err3; c = (SPLITTER * cex);
    abig = (c - cex); a1hi = c - abig; a1lo = cex - a1hi;
    _j = (cex * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * negate); abig = (c - negate);
    bhi = c - abig; blo = negate - bhi; _i = (cextail * negate);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; cxby[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (cex * negate); err1 = _j - (a1hi * bhi);
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

    ; cxby[7] = cxby7;
    bclen = fast_expansion_sum_zeroelim(8, bxcy, 8, cxby, bc);
    c = (SPLITTER * cextail); abig = (c - cextail); a0hi = c - abig;
    a0lo = cextail - a0hi; c = (SPLITTER * deytail);
    abig = (c - deytail); bhi = c - abig; blo = deytail - bhi;
    _i = (cextail * deytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    cxdy[0] = (a0lo * blo) - err3; c = (SPLITTER * cex);
    abig = (c - cex); a1hi = c - abig; a1lo = cex - a1hi;
    _j = (cex * deytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * dey); abig = (c - dey);
    bhi = c - abig; blo = dey - bhi; _i = (cextail * dey);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; cxdy[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (cex * dey); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    cxdy[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    cxdy[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
    cxdy[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    cxdy[5] = around + bround; cxdy7 = (_m + _k); bvirt = (cxdy7 - _m);
    avirt = cxdy7 - bvirt; bround = _k - bvirt; around = _m - avirt;
    cxdy[6] = around + bround

    ; cxdy[7] = cxdy7; negate = -cey; negatetail = -ceytail;
    c = (SPLITTER * dextail); abig = (c - dextail); a0hi = c - abig;
    a0lo = dextail - a0hi; c = (SPLITTER * negatetail);
    abig = (c - negatetail); bhi = c - abig; blo = negatetail - bhi;
    _i = (dextail * negatetail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    dxcy[0] = (a0lo * blo) - err3; c = (SPLITTER * dex);
    abig = (c - dex); a1hi = c - abig; a1lo = dex - a1hi;
    _j = (dex * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * negate); abig = (c - negate);
    bhi = c - abig; blo = negate - bhi; _i = (dextail * negate);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; dxcy[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (dex * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    dxcy[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    dxcy[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
    dxcy[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    dxcy[5] = around + bround; dxcy7 = (_m + _k); bvirt = (dxcy7 - _m);
    avirt = dxcy7 - bvirt; bround = _k - bvirt; around = _m - avirt;
    dxcy[6] = around + bround

    ; dxcy[7] = dxcy7;
    cdlen = fast_expansion_sum_zeroelim(8, cxdy, 8, dxcy, cd);
    c = (SPLITTER * dextail); abig = (c - dextail); a0hi = c - abig;
    a0lo = dextail - a0hi; c = (SPLITTER * aeytail);
    abig = (c - aeytail); bhi = c - abig; blo = aeytail - bhi;
    _i = (dextail * aeytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    dxay[0] = (a0lo * blo) - err3; c = (SPLITTER * dex);
    abig = (c - dex); a1hi = c - abig; a1lo = dex - a1hi;
    _j = (dex * aeytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * aey); abig = (c - aey);
    bhi = c - abig; blo = aey - bhi; _i = (dextail * aey);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; dxay[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (dex * aey); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    dxay[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    dxay[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
    dxay[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    dxay[5] = around + bround; dxay7 = (_m + _k); bvirt = (dxay7 - _m);
    avirt = dxay7 - bvirt; bround = _k - bvirt; around = _m - avirt;
    dxay[6] = around + bround

    ; dxay[7] = dxay7; negate = -dey; negatetail = -deytail;
    c = (SPLITTER * aextail); abig = (c - aextail); a0hi = c - abig;
    a0lo = aextail - a0hi; c = (SPLITTER * negatetail);
    abig = (c - negatetail); bhi = c - abig; blo = negatetail - bhi;
    _i = (aextail * negatetail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    axdy[0] = (a0lo * blo) - err3; c = (SPLITTER * aex);
    abig = (c - aex); a1hi = c - abig; a1lo = aex - a1hi;
    _j = (aex * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * negate); abig = (c - negate);
    bhi = c - abig; blo = negate - bhi; _i = (aextail * negate);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; axdy[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (aex * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    axdy[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    axdy[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
    axdy[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    axdy[5] = around + bround; axdy7 = (_m + _k); bvirt = (axdy7 - _m);
    avirt = axdy7 - bvirt; bround = _k - bvirt; around = _m - avirt;
    axdy[6] = around + bround

    ; axdy[7] = axdy7;
    dalen = fast_expansion_sum_zeroelim(8, dxay, 8, axdy, da);
    c = (SPLITTER * aextail); abig = (c - aextail); a0hi = c - abig;
    a0lo = aextail - a0hi; c = (SPLITTER * ceytail);
    abig = (c - ceytail); bhi = c - abig; blo = ceytail - bhi;
    _i = (aextail * ceytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    axcy[0] = (a0lo * blo) - err3; c = (SPLITTER * aex);
    abig = (c - aex); a1hi = c - abig; a1lo = aex - a1hi;
    _j = (aex * ceytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * cey); abig = (c - cey);
    bhi = c - abig; blo = cey - bhi; _i = (aextail * cey);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; axcy[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (aex * cey); err1 = _j - (a1hi * bhi);
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

    ; axcy[7] = axcy7; negate = -aey; negatetail = -aeytail;
    c = (SPLITTER * cextail); abig = (c - cextail); a0hi = c - abig;
    a0lo = cextail - a0hi; c = (SPLITTER * negatetail);
    abig = (c - negatetail); bhi = c - abig; blo = negatetail - bhi;
    _i = (cextail * negatetail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    cxay[0] = (a0lo * blo) - err3; c = (SPLITTER * cex);
    abig = (c - cex); a1hi = c - abig; a1lo = cex - a1hi;
    _j = (cex * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * negate); abig = (c - negate);
    bhi = c - abig; blo = negate - bhi; _i = (cextail * negate);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; cxay[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (cex * negate); err1 = _j - (a1hi * bhi);
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

    ; cxay[7] = cxay7;
    aclen = fast_expansion_sum_zeroelim(8, axcy, 8, cxay, ac);
    c = (SPLITTER * bextail); abig = (c - bextail); a0hi = c - abig;
    a0lo = bextail - a0hi; c = (SPLITTER * deytail);
    abig = (c - deytail); bhi = c - abig; blo = deytail - bhi;
    _i = (bextail * deytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    bxdy[0] = (a0lo * blo) - err3; c = (SPLITTER * bex);
    abig = (c - bex); a1hi = c - abig; a1lo = bex - a1hi;
    _j = (bex * deytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * dey); abig = (c - dey);
    bhi = c - abig; blo = dey - bhi; _i = (bextail * dey);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; bxdy[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (bex * dey); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    bxdy[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    bxdy[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
    bxdy[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    bxdy[5] = around + bround; bxdy7 = (_m + _k); bvirt = (bxdy7 - _m);
    avirt = bxdy7 - bvirt; bround = _k - bvirt; around = _m - avirt;
    bxdy[6] = around + bround

    ; bxdy[7] = bxdy7; negate = -bey; negatetail = -beytail;
    c = (SPLITTER * dextail); abig = (c - dextail); a0hi = c - abig;
    a0lo = dextail - a0hi; c = (SPLITTER * negatetail);
    abig = (c - negatetail); bhi = c - abig; blo = negatetail - bhi;
    _i = (dextail * negatetail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    dxby[0] = (a0lo * blo) - err3; c = (SPLITTER * dex);
    abig = (c - dex); a1hi = c - abig; a1lo = dex - a1hi;
    _j = (dex * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
    _2 = _k - bvirt; c = (SPLITTER * negate); abig = (c - negate);
    bhi = c - abig; blo = negate - bhi; _i = (dextail * negate);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
    bround = _0 - bvirt; around = _1 - avirt; dxby[1] = around + bround;
    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
    _j = (dex * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    dxby[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
    dxby[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
    dxby[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    dxby[5] = around + bround; dxby7 = (_m + _k); bvirt = (dxby7 - _m);
    avirt = dxby7 - bvirt; bround = _k - bvirt; around = _m - avirt;
    dxby[6] = around + bround

    ; dxby[7] = dxby7;
    bdlen = fast_expansion_sum_zeroelim(8, bxdy, 8, dxby, bd);

    temp32alen = scale_expansion_zeroelim(cdlen, cd, -bez, temp32a);
    temp32blen = scale_expansion_zeroelim(cdlen, cd, -beztail, temp32b);
    temp64alen =
      fast_expansion_sum_zeroelim(temp32alen, temp32a, temp32blen,
                                  temp32b, temp64a);
    temp32alen = scale_expansion_zeroelim(bdlen, bd, cez, temp32a);
    temp32blen = scale_expansion_zeroelim(bdlen, bd, ceztail, temp32b);
    temp64blen =
      fast_expansion_sum_zeroelim(temp32alen, temp32a, temp32blen,
                                  temp32b, temp64b);
    temp32alen = scale_expansion_zeroelim(bclen, bc, -dez, temp32a);
    temp32blen = scale_expansion_zeroelim(bclen, bc, -deztail, temp32b);
    temp64clen =
      fast_expansion_sum_zeroelim(temp32alen, temp32a, temp32blen,
                                  temp32b, temp64c); temp128len =
      fast_expansion_sum_zeroelim(temp64alen, temp64a, temp64blen,
                                  temp64b, temp128); temp192len =
      fast_expansion_sum_zeroelim(temp64clen, temp64c, temp128len,
                                  temp128, temp192);
    xlen = scale_expansion_zeroelim(temp192len, temp192, aex, detx);
    xxlen = scale_expansion_zeroelim(xlen, detx, aex, detxx);xtlen =
      scale_expansion_zeroelim(temp192len, temp192, aextail, detxt);
    xxtlen = scale_expansion_zeroelim(xtlen, detxt, aex, detxxt);
    for (i = 0; i < xxtlen; i++) {
      detxxt[i] *= 2.0;
    }
    xtxtlen = scale_expansion_zeroelim(xtlen, detxt, aextail, detxtxt);
    x1len =
      fast_expansion_sum_zeroelim(xxlen, detxx, xxtlen, detxxt, x1);
    x2len =
      fast_expansion_sum_zeroelim(x1len, x1, xtxtlen, detxtxt, x2);
    ylen = scale_expansion_zeroelim(temp192len, temp192, aey, dety);
    yylen = scale_expansion_zeroelim(ylen, dety, aey, detyy);ytlen =
      scale_expansion_zeroelim(temp192len, temp192, aeytail, detyt);
    yytlen = scale_expansion_zeroelim(ytlen, detyt, aey, detyyt);
    for (i = 0; i < yytlen; i++) {
      detyyt[i] *= 2.0;
    }
    ytytlen = scale_expansion_zeroelim(ytlen, detyt, aeytail, detytyt);
    y1len =
      fast_expansion_sum_zeroelim(yylen, detyy, yytlen, detyyt, y1);
    y2len =
      fast_expansion_sum_zeroelim(y1len, y1, ytytlen, detytyt, y2);
    zlen = scale_expansion_zeroelim(temp192len, temp192, aez, detz);
    zzlen = scale_expansion_zeroelim(zlen, detz, aez, detzz);ztlen =
      scale_expansion_zeroelim(temp192len, temp192, aeztail, detzt);
    zztlen = scale_expansion_zeroelim(ztlen, detzt, aez, detzzt);
    for (i = 0; i < zztlen; i++) {
      detzzt[i] *= 2.0;
    }
    ztztlen = scale_expansion_zeroelim(ztlen, detzt, aeztail, detztzt);
    z1len =
      fast_expansion_sum_zeroelim(zzlen, detzz, zztlen, detzzt, z1);
    z2len =
      fast_expansion_sum_zeroelim(z1len, z1, ztztlen, detztzt, z2);
    xylen = fast_expansion_sum_zeroelim(x2len, x2, y2len, y2, detxy);
    alen = fast_expansion_sum_zeroelim(z2len, z2, xylen, detxy, adet);

    temp32alen = scale_expansion_zeroelim(dalen, da, cez, temp32a);
    temp32blen = scale_expansion_zeroelim(dalen, da, ceztail, temp32b);
    temp64alen =
      fast_expansion_sum_zeroelim(temp32alen, temp32a, temp32blen,
                                  temp32b, temp64a);
    temp32alen = scale_expansion_zeroelim(aclen, ac, dez, temp32a);
    temp32blen = scale_expansion_zeroelim(aclen, ac, deztail, temp32b);
    temp64blen =
      fast_expansion_sum_zeroelim(temp32alen, temp32a, temp32blen,
                                  temp32b, temp64b);
    temp32alen = scale_expansion_zeroelim(cdlen, cd, aez, temp32a);
    temp32blen = scale_expansion_zeroelim(cdlen, cd, aeztail, temp32b);
    temp64clen =
      fast_expansion_sum_zeroelim(temp32alen, temp32a, temp32blen,
                                  temp32b, temp64c); temp128len =
      fast_expansion_sum_zeroelim(temp64alen, temp64a, temp64blen,
                                  temp64b, temp128); temp192len =
      fast_expansion_sum_zeroelim(temp64clen, temp64c, temp128len,
                                  temp128, temp192);
    xlen = scale_expansion_zeroelim(temp192len, temp192, bex, detx);
    xxlen = scale_expansion_zeroelim(xlen, detx, bex, detxx);xtlen =
      scale_expansion_zeroelim(temp192len, temp192, bextail, detxt);
    xxtlen = scale_expansion_zeroelim(xtlen, detxt, bex, detxxt);
    for (i = 0; i < xxtlen; i++) {
      detxxt[i] *= 2.0;
    }
    xtxtlen = scale_expansion_zeroelim(xtlen, detxt, bextail, detxtxt);
    x1len =
      fast_expansion_sum_zeroelim(xxlen, detxx, xxtlen, detxxt, x1);
    x2len =
      fast_expansion_sum_zeroelim(x1len, x1, xtxtlen, detxtxt, x2);
    ylen = scale_expansion_zeroelim(temp192len, temp192, bey, dety);
    yylen = scale_expansion_zeroelim(ylen, dety, bey, detyy);ytlen =
      scale_expansion_zeroelim(temp192len, temp192, beytail, detyt);
    yytlen = scale_expansion_zeroelim(ytlen, detyt, bey, detyyt);
    for (i = 0; i < yytlen; i++) {
      detyyt[i] *= 2.0;
    }
    ytytlen = scale_expansion_zeroelim(ytlen, detyt, beytail, detytyt);
    y1len =
      fast_expansion_sum_zeroelim(yylen, detyy, yytlen, detyyt, y1);
    y2len =
      fast_expansion_sum_zeroelim(y1len, y1, ytytlen, detytyt, y2);
    zlen = scale_expansion_zeroelim(temp192len, temp192, bez, detz);
    zzlen = scale_expansion_zeroelim(zlen, detz, bez, detzz);ztlen =
      scale_expansion_zeroelim(temp192len, temp192, beztail, detzt);
    zztlen = scale_expansion_zeroelim(ztlen, detzt, bez, detzzt);
    for (i = 0; i < zztlen; i++) {
      detzzt[i] *= 2.0;
    }
    ztztlen = scale_expansion_zeroelim(ztlen, detzt, beztail, detztzt);
    z1len =
      fast_expansion_sum_zeroelim(zzlen, detzz, zztlen, detzzt, z1);
    z2len =
      fast_expansion_sum_zeroelim(z1len, z1, ztztlen, detztzt, z2);
    xylen = fast_expansion_sum_zeroelim(x2len, x2, y2len, y2, detxy);
    blen = fast_expansion_sum_zeroelim(z2len, z2, xylen, detxy, bdet);

    temp32alen = scale_expansion_zeroelim(ablen, ab, -dez, temp32a);
    temp32blen = scale_expansion_zeroelim(ablen, ab, -deztail, temp32b);
    temp64alen =
      fast_expansion_sum_zeroelim(temp32alen, temp32a, temp32blen,
                                  temp32b, temp64a);
    temp32alen = scale_expansion_zeroelim(bdlen, bd, -aez, temp32a);
    temp32blen = scale_expansion_zeroelim(bdlen, bd, -aeztail, temp32b);
    temp64blen =
      fast_expansion_sum_zeroelim(temp32alen, temp32a, temp32blen,
                                  temp32b, temp64b);
    temp32alen = scale_expansion_zeroelim(dalen, da, -bez, temp32a);
    temp32blen = scale_expansion_zeroelim(dalen, da, -beztail, temp32b);
    temp64clen =
      fast_expansion_sum_zeroelim(temp32alen, temp32a, temp32blen,
                                  temp32b, temp64c); temp128len =
      fast_expansion_sum_zeroelim(temp64alen, temp64a, temp64blen,
                                  temp64b, temp128); temp192len =
      fast_expansion_sum_zeroelim(temp64clen, temp64c, temp128len,
                                  temp128, temp192);
    xlen = scale_expansion_zeroelim(temp192len, temp192, cex, detx);
    xxlen = scale_expansion_zeroelim(xlen, detx, cex, detxx);xtlen =
      scale_expansion_zeroelim(temp192len, temp192, cextail, detxt);
    xxtlen = scale_expansion_zeroelim(xtlen, detxt, cex, detxxt);
    for (i = 0; i < xxtlen; i++) {
      detxxt[i] *= 2.0;
    }
    xtxtlen = scale_expansion_zeroelim(xtlen, detxt, cextail, detxtxt);
    x1len =
      fast_expansion_sum_zeroelim(xxlen, detxx, xxtlen, detxxt, x1);
    x2len =
      fast_expansion_sum_zeroelim(x1len, x1, xtxtlen, detxtxt, x2);
    ylen = scale_expansion_zeroelim(temp192len, temp192, cey, dety);
    yylen = scale_expansion_zeroelim(ylen, dety, cey, detyy);ytlen =
      scale_expansion_zeroelim(temp192len, temp192, ceytail, detyt);
    yytlen = scale_expansion_zeroelim(ytlen, detyt, cey, detyyt);
    for (i = 0; i < yytlen; i++) {
      detyyt[i] *= 2.0;
    }
    ytytlen = scale_expansion_zeroelim(ytlen, detyt, ceytail, detytyt);
    y1len =
      fast_expansion_sum_zeroelim(yylen, detyy, yytlen, detyyt, y1);
    y2len =
      fast_expansion_sum_zeroelim(y1len, y1, ytytlen, detytyt, y2);
    zlen = scale_expansion_zeroelim(temp192len, temp192, cez, detz);
    zzlen = scale_expansion_zeroelim(zlen, detz, cez, detzz);ztlen =
      scale_expansion_zeroelim(temp192len, temp192, ceztail, detzt);
    zztlen = scale_expansion_zeroelim(ztlen, detzt, cez, detzzt);
    for (i = 0; i < zztlen; i++) {
      detzzt[i] *= 2.0;
    }
    ztztlen = scale_expansion_zeroelim(ztlen, detzt, ceztail, detztzt);
    z1len =
      fast_expansion_sum_zeroelim(zzlen, detzz, zztlen, detzzt, z1);
    z2len =
      fast_expansion_sum_zeroelim(z1len, z1, ztztlen, detztzt, z2);
    xylen = fast_expansion_sum_zeroelim(x2len, x2, y2len, y2, detxy);
    clen = fast_expansion_sum_zeroelim(z2len, z2, xylen, detxy, cdet);

    temp32alen = scale_expansion_zeroelim(bclen, bc, aez, temp32a);
    temp32blen = scale_expansion_zeroelim(bclen, bc, aeztail, temp32b);
    temp64alen =
      fast_expansion_sum_zeroelim(temp32alen, temp32a, temp32blen,
                                  temp32b, temp64a);
    temp32alen = scale_expansion_zeroelim(aclen, ac, -bez, temp32a);
    temp32blen = scale_expansion_zeroelim(aclen, ac, -beztail, temp32b);
    temp64blen =
      fast_expansion_sum_zeroelim(temp32alen, temp32a, temp32blen,
                                  temp32b, temp64b);
    temp32alen = scale_expansion_zeroelim(ablen, ab, cez, temp32a);
    temp32blen = scale_expansion_zeroelim(ablen, ab, ceztail, temp32b);
    temp64clen =
      fast_expansion_sum_zeroelim(temp32alen, temp32a, temp32blen,
                                  temp32b, temp64c); temp128len =
      fast_expansion_sum_zeroelim(temp64alen, temp64a, temp64blen,
                                  temp64b, temp128); temp192len =
      fast_expansion_sum_zeroelim(temp64clen, temp64c, temp128len,
                                  temp128, temp192);
    xlen = scale_expansion_zeroelim(temp192len, temp192, dex, detx);
    xxlen = scale_expansion_zeroelim(xlen, detx, dex, detxx);xtlen =
      scale_expansion_zeroelim(temp192len, temp192, dextail, detxt);
    xxtlen = scale_expansion_zeroelim(xtlen, detxt, dex, detxxt);
    for (i = 0; i < xxtlen; i++) {
      detxxt[i] *= 2.0;
    }
    xtxtlen = scale_expansion_zeroelim(xtlen, detxt, dextail, detxtxt);
    x1len =
      fast_expansion_sum_zeroelim(xxlen, detxx, xxtlen, detxxt, x1);
    x2len =
      fast_expansion_sum_zeroelim(x1len, x1, xtxtlen, detxtxt, x2);
    ylen = scale_expansion_zeroelim(temp192len, temp192, dey, dety);
    yylen = scale_expansion_zeroelim(ylen, dety, dey, detyy);ytlen =
      scale_expansion_zeroelim(temp192len, temp192, deytail, detyt);
    yytlen = scale_expansion_zeroelim(ytlen, detyt, dey, detyyt);
    for (i = 0; i < yytlen; i++) {
      detyyt[i] *= 2.0;
    }
    ytytlen = scale_expansion_zeroelim(ytlen, detyt, deytail, detytyt);
    y1len =
      fast_expansion_sum_zeroelim(yylen, detyy, yytlen, detyyt, y1);
    y2len =
      fast_expansion_sum_zeroelim(y1len, y1, ytytlen, detytyt, y2);
    zlen = scale_expansion_zeroelim(temp192len, temp192, dez, detz);
    zzlen = scale_expansion_zeroelim(zlen, detz, dez, detzz);ztlen =
      scale_expansion_zeroelim(temp192len, temp192, deztail, detzt);
    zztlen = scale_expansion_zeroelim(ztlen, detzt, dez, detzzt);
    for (i = 0; i < zztlen; i++) {
      detzzt[i] *= 2.0;
    }
    ztztlen = scale_expansion_zeroelim(ztlen, detzt, deztail, detztzt);
    z1len =
      fast_expansion_sum_zeroelim(zzlen, detzz, zztlen, detzzt, z1);
    z2len =
      fast_expansion_sum_zeroelim(z1len, z1, ztztlen, detztzt, z2);
    xylen = fast_expansion_sum_zeroelim(x2len, x2, y2len, y2, detxy);
    dlen = fast_expansion_sum_zeroelim(z2len, z2, xylen, detxy, ddet);

    ablen = fast_expansion_sum_zeroelim(alen, adet, blen, bdet, abdet);
    cdlen = fast_expansion_sum_zeroelim(clen, cdet, dlen, ddet, cddet);
    fast_expansion_sum_zeroelim(ablen, abdet, cdlen, cddet, deter);

    return XDouble.unsafe(deter).doubleValue();
  }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public SlowMacro () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
