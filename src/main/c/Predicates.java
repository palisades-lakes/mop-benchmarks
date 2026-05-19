// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import static mop.java.geometry.predicates.Expansion.*;

/**
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
 * @version 2026-05-16
 */

//@SuppressWarnings("unused")
// strictfp unnecessary for JDK17 and later
public final class Predicates {

  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------
  public static final double orient2dfast (final double[] pa,
                                           final double[] pb,
                                           final double[] pc) {
    double acx, bcx, acy, bcy;

    acx = pa[0] - pc[0];
    bcx = pb[0] - pc[0];
    acy = pa[1] - pc[1];
    bcy = pb[1] - pc[1];
    return acx * bcy - acy * bcx;
  }

  //--------------------------------------------------------------------
  static final double SPLITTER = 0x1.0000002p27;

  public static final double orient2dexact (final double[] pa,
                                            final double[] pb,
                                            final double[] pc) {
    double axby1, axcy1, bxcy1, bxay1, cxay1, cxby1;
    double axby0, axcy0, bxcy0, bxay0, cxay0, cxby0;
    double[] aterms = new double[4];
    double[] bterms = new double[4];
    double[] cterms = new double[4];
    double aterms3, bterms3, cterms3;
    double[] v = new double[8];
    double[] w = new double[12];
    int vlength, wlength;

    double bvirt;
    double avirt, bround, around;
    double c;
    double abig;
    double ahi, alo, bhi, blo;
    double err1, err2, err3;
    double _i, _j;
    double _0;

    axby1 =   (pa[0] * pb[1]); c =   (SPLITTER * pa[0]);
    abig =   (c - pa[0]); ahi = c - abig; alo = pa[0] - ahi;
    c =   (SPLITTER * pb[1]); abig =   (c - pb[1]);
    bhi = c - abig; blo = pb[1] - bhi; err1 = axby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axby0 = (alo * blo) - err3;
    axcy1 =   (pa[0] * pc[1]); c =   (SPLITTER * pa[0]);
    abig =   (c - pa[0]); ahi = c - abig; alo = pa[0] - ahi;
    c =   (SPLITTER * pc[1]); abig =   (c - pc[1]);
    bhi = c - abig; blo = pc[1] - bhi; err1 = axcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axcy0 = (alo * blo) - err3;
    _i =   (axby0 - axcy0); bvirt =   (axby0 - _i);
    avirt = _i + bvirt; bround = bvirt - axcy0; around = axby0 - avirt;
    aterms[0] = around + bround; _j =   (axby1 + _i);
    bvirt =   (_j - axby1); avirt = _j - bvirt;
    bround = _i - bvirt; around = axby1 - avirt;
    _0 = around + bround; _i =   (_0 - axcy1);
    bvirt =   (_0 - _i); avirt = _i + bvirt;
    bround = bvirt - axcy1; around = _0 - avirt;
    aterms[1] = around + bround; aterms3 =   (_j + _i);
    bvirt =   (aterms3 - _j); avirt = aterms3 - bvirt;
    bround = _i - bvirt; around = _j - avirt;
    aterms[2] = around + bround;
    aterms[3] = aterms3;

    bxcy1 =   (pb[0] * pc[1]); c =   (SPLITTER * pb[0]);
    abig =   (c - pb[0]); ahi = c - abig; alo = pb[0] - ahi;
    c =   (SPLITTER * pc[1]); abig =   (c - pc[1]);
    bhi = c - abig; blo = pc[1] - bhi; err1 = bxcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxcy0 = (alo * blo) - err3;
    bxay1 =   (pb[0] * pa[1]); c =   (SPLITTER * pb[0]);
    abig =   (c - pb[0]); ahi = c - abig; alo = pb[0] - ahi;
    c =   (SPLITTER * pa[1]); abig =   (c - pa[1]);
    bhi = c - abig; blo = pa[1] - bhi; err1 = bxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxay0 = (alo * blo) - err3;
    _i =   (bxcy0 - bxay0); bvirt =   (bxcy0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxay0; around = bxcy0 - avirt;
    bterms[0] = around + bround; _j =   (bxcy1 + _i);
    bvirt =   (_j - bxcy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = bxcy1 - avirt; _0 = around + bround;
    _i =   (_0 - bxay1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxay1; around = _0 - avirt;
    bterms[1] = around + bround; bterms3 =   (_j + _i);
    bvirt =   (bterms3 - _j); avirt = bterms3 - bvirt;
    bround = _i - bvirt; around = _j - avirt;
    bterms[2] = around + bround;
    bterms[3] = bterms3;

    cxay1 =   (pc[0] * pa[1]); c =   (SPLITTER * pc[0]);
    abig =   (c - pc[0]); ahi = c - abig; alo = pc[0] - ahi;
    c =   (SPLITTER * pa[1]); abig =   (c - pa[1]);
    bhi = c - abig; blo = pa[1] - bhi; err1 = cxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxay0 = (alo * blo) - err3;
    cxby1 =   (pc[0] * pb[1]); c =   (SPLITTER * pc[0]);
    abig =   (c - pc[0]); ahi = c - abig; alo = pc[0] - ahi;
    c =   (SPLITTER * pb[1]); abig =   (c - pb[1]);
    bhi = c - abig; blo = pb[1] - bhi; err1 = cxby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxby0 = (alo * blo) - err3;
    _i =   (cxay0 - cxby0); bvirt =   (cxay0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxby0; around = cxay0 - avirt;
    cterms[0] = around + bround; _j =   (cxay1 + _i);
    bvirt =   (_j - cxay1); avirt = _j - bvirt;
    bround = _i - bvirt; around = cxay1 - avirt; _0 = around + bround;
    _i =   (_0 - cxby1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxby1; around = _0 - avirt;
    cterms[1] = around + bround; cterms3 =   (_j + _i);
    bvirt =   (cterms3 - _j); avirt = cterms3 - bvirt;
    bround = _i - bvirt; around = _j - avirt;
    cterms[2] = around + bround;
    cterms[3] = cterms3;

    vlength = sum(4, aterms, 4, bterms, v);
    wlength = sum(vlength, v, 4, cterms, w);

    return w[wlength - 1];
  }

  //--------------------------------------------------------------------
  public static final double orient2dslow (final double[] pa,
                                            final double[] pb,
                                            final double[] pc) {
    double acx, acy, bcx, bcy;
    double acxtail, acytail;
    double bcxtail, bcytail;
    double negate, negatetail;
    double[] axby = new double[8];
    double[] bxay = new double[8];
    double axby7, bxay7;
    double[] deter = new double[16];
    int deterlen;

    double bvirt;
    double avirt, bround, around;
    double c;
    double abig;
    double a0hi, a0lo, a1hi, a1lo, bhi, blo;
    double err1, err2, err3;
    double _i, _j, _k, _l, _m, _n;
    double _0, _1, _2;

    acx =   (pa[0] - pc[0]); bvirt =   (pa[0] - acx);
    avirt = acx + bvirt; bround = bvirt - pc[0]; around = pa[0] - avirt;
    acxtail = around + bround;
    acy =   (pa[1] - pc[1]); bvirt =   (pa[1] - acy);
    avirt = acy + bvirt; bround = bvirt - pc[1]; around = pa[1] - avirt;
    acytail = around + bround;
    bcx =   (pb[0] - pc[0]); bvirt =   (pb[0] - bcx);
    avirt = bcx + bvirt; bround = bvirt - pc[0]; around = pb[0] - avirt;
    bcxtail = around + bround;
    bcy =   (pb[1] - pc[1]); bvirt =   (pb[1] - bcy);
    avirt = bcy + bvirt; bround = bvirt - pc[1]; around = pb[1] - avirt;
    bcytail = around + bround;

    c =   (SPLITTER * acxtail); abig =   (c - acxtail);
    a0hi = c - abig; a0lo = acxtail - a0hi;
    c =   (SPLITTER * bcytail); abig =   (c - bcytail);
    bhi = c - abig; blo = bcytail - bhi;
    _i =   (acxtail * bcytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    axby[0] = (a0lo * blo) - err3; c =   (SPLITTER * acx);
    abig =   (c - acx); a1hi = c - abig; a1lo = acx - a1hi;
    _j =   (acx * bcytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * bcy);
    abig =   (c - bcy); bhi = c - abig; blo = bcy - bhi;
    _i =   (acxtail * bcy); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axby[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (acx * bcy); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axby[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axby[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; axby[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    axby[5] = around + bround; axby7 =   (_m + _k);
    bvirt =   (axby7 - _m); avirt = axby7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; axby[6] = around + bround;
    axby[7] = axby7;
    negate = -acy;
    negatetail = -acytail;
    c =   (SPLITTER * bcxtail); abig =   (c - bcxtail);
    a0hi = c - abig; a0lo = bcxtail - a0hi;
    c =   (SPLITTER * negatetail);
    abig =   (c - negatetail); bhi = c - abig;
    blo = negatetail - bhi; _i =   (bcxtail * negatetail);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); bxay[0] = (a0lo * blo) - err3;
    c =   (SPLITTER * bcx); abig =   (c - bcx);
    a1hi = c - abig; a1lo = bcx - a1hi;
    _j =   (bcx * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * negate);
    abig =   (c - negate); bhi = c - abig; blo = negate - bhi;
    _i =   (bcxtail * negate); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxay[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (bcx * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxay[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxay[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; bxay[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    bxay[5] = around + bround; bxay7 =   (_m + _k);
    bvirt =   (bxay7 - _m); avirt = bxay7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; bxay[6] = around + bround;
    bxay[7] = bxay7;

    deterlen = sum(8, axby, 8, bxay, deter);

    return deter[deterlen - 1];
  }

  //--------------------------------------------------------------------
  private static final double EPSILON = 0x1.0p-53;
  private static final double resulterrbound =
    (3.0 + 8.0 * EPSILON) * EPSILON;
  private static final double ccwerrboundB =
    (2.0 + 12.0 * EPSILON) * EPSILON;

  private static final double ccwerrboundC =
    (9.0 + 64.0 * EPSILON) * EPSILON * EPSILON;


  private static final double orient2dadapt (final double[] pa,
                                             final double[] pb,
                                             final double[] pc,
                                             final double detsum) {
    double acx, acy, bcx, bcy;
    double acxtail, acytail, bcxtail, bcytail;
    double detleft, detright;
    double detlefttail, detrighttail;
    double det, errbound;
    double[] B = new double[4];
    double[] C1 = new double[8];
    double[] C2 = new double[12];
    double[] D = new double[16];
    double B3;
    int C1length, C2length, Dlength;
    double[] u = new double[4];
    double u3;
    double s1, t1;
    double s0, t0;

    double bvirt;
    double avirt, bround, around;
    double c;
    double abig;
    double ahi, alo, bhi, blo;
    double err1, err2, err3;
    double _i, _j;
    double _0;

    acx =   (pa[0] - pc[0]);
    bcx =   (pb[0] - pc[0]);
    acy =   (pa[1] - pc[1]);
    bcy =   (pb[1] - pc[1]);

    detleft =   (acx * bcy); c =   (SPLITTER * acx);
    abig =   (c - acx); ahi = c - abig; alo = acx - ahi;
    c =   (SPLITTER * bcy); abig =   (c - bcy);
    bhi = c - abig; blo = bcy - bhi; err1 = detleft - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    detlefttail = (alo * blo) - err3;
    detright =   (acy * bcx); c =   (SPLITTER * acy);
    abig =   (c - acy); ahi = c - abig; alo = acy - ahi;
    c =   (SPLITTER * bcx); abig =   (c - bcx);
    bhi = c - abig; blo = bcx - bhi; err1 = detright - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    detrighttail = (alo * blo) - err3;

    _i =   (detlefttail - detrighttail);
    bvirt =   (detlefttail - _i); avirt = _i + bvirt;
    bround = bvirt - detrighttail; around = detlefttail - avirt;
    B[0] = around + bround; _j =   (detleft + _i);
    bvirt =   (_j - detleft); avirt = _j - bvirt;
    bround = _i - bvirt; around = detleft - avirt; _0 = around + bround;
    _i =   (_0 - detright); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - detright; around = _0 - avirt;
    B[1] = around + bround; B3 =   (_j + _i);
    bvirt =   (B3 - _j); avirt = B3 - bvirt; bround = _i - bvirt;
    around = _j - avirt; B[2] = around + bround
    ;
    B[3] = B3;

    det = estimate(4, B);
    errbound = ccwerrboundB * detsum;
    if ((det >= errbound) || (-det >= errbound)) {
      return det;
    }

    bvirt =   (pa[0] - acx); avirt = acx + bvirt;
    bround = bvirt - pc[0]; around = pa[0] - avirt;
    acxtail = around + bround;
    bvirt =   (pb[0] - bcx); avirt = bcx + bvirt;
    bround = bvirt - pc[0]; around = pb[0] - avirt;
    bcxtail = around + bround;
    bvirt =   (pa[1] - acy); avirt = acy + bvirt;
    bround = bvirt - pc[1]; around = pa[1] - avirt;
    acytail = around + bround;
    bvirt =   (pb[1] - bcy); avirt = bcy + bvirt;
    bround = bvirt - pc[1]; around = pb[1] - avirt;
    bcytail = around + bround;

    if ((acxtail == 0.0) && (acytail == 0.0)
      && (bcxtail == 0.0) && (bcytail == 0.0)) {
      return det;
    }

    errbound =
      ccwerrboundC * detsum + resulterrbound * ((det) >= 0.0 ? (det)
                                                             : -(det));
    det += (acx * bcytail + bcy * acxtail)
      - (acy * bcxtail + bcx * acytail);
    if ((det >= errbound) || (-det >= errbound)) {
      return det;
    }

    s1 =   (acxtail * bcy); c =   (SPLITTER * acxtail);
    abig =   (c - acxtail); ahi = c - abig; alo = acxtail - ahi;
    c =   (SPLITTER * bcy); abig =   (c - bcy);
    bhi = c - abig; blo = bcy - bhi; err1 = s1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    s0 = (alo * blo) - err3;
    t1 =   (acytail * bcx); c =   (SPLITTER * acytail);
    abig =   (c - acytail); ahi = c - abig; alo = acytail - ahi;
    c =   (SPLITTER * bcx); abig =   (c - bcx);
    bhi = c - abig; blo = bcx - bhi; err1 = t1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    t0 = (alo * blo) - err3;
    _i =   (s0 - t0); bvirt =   (s0 - _i);
    avirt = _i + bvirt; bround = bvirt - t0; around = s0 - avirt;
    u[0] = around + bround; _j =   (s1 + _i);
    bvirt =   (_j - s1); avirt = _j - bvirt; bround = _i - bvirt;
    around = s1 - avirt; _0 = around + bround; _i =   (_0 - t1);
    bvirt =   (_0 - _i); avirt = _i + bvirt; bround = bvirt - t1;
    around = _0 - avirt; u[1] = around + bround;
    u3 =   (_j + _i); bvirt =   (u3 - _j);
    avirt = u3 - bvirt; bround = _i - bvirt; around = _j - avirt;
    u[2] = around + bround;
    u[3] = u3;
    C1length = sum(4, B, 4, u, C1);

    s1 =   (acx * bcytail); c =   (SPLITTER * acx);
    abig =   (c - acx); ahi = c - abig; alo = acx - ahi;
    c =   (SPLITTER * bcytail); abig =   (c - bcytail);
    bhi = c - abig; blo = bcytail - bhi; err1 = s1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    s0 = (alo * blo) - err3;
    t1 =   (acy * bcxtail); c =   (SPLITTER * acy);
    abig =   (c - acy); ahi = c - abig; alo = acy - ahi;
    c =   (SPLITTER * bcxtail); abig =   (c - bcxtail);
    bhi = c - abig; blo = bcxtail - bhi; err1 = t1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    t0 = (alo * blo) - err3;
    _i =   (s0 - t0); bvirt =   (s0 - _i);
    avirt = _i + bvirt; bround = bvirt - t0; around = s0 - avirt;
    u[0] = around + bround; _j =   (s1 + _i);
    bvirt =   (_j - s1); avirt = _j - bvirt; bround = _i - bvirt;
    around = s1 - avirt; _0 = around + bround; _i =   (_0 - t1);
    bvirt =   (_0 - _i); avirt = _i + bvirt; bround = bvirt - t1;
    around = _0 - avirt; u[1] = around + bround;
    u3 =   (_j + _i); bvirt =   (u3 - _j);
    avirt = u3 - bvirt; bround = _i - bvirt; around = _j - avirt;
    u[2] = around + bround;
    u[3] = u3;
    C2length = sum(C1length, C1, 4, u, C2);

    s1 =   (acxtail * bcytail);
    c =   (SPLITTER * acxtail); abig =   (c - acxtail);
    ahi = c - abig; alo = acxtail - ahi;
    c =   (SPLITTER * bcytail); abig =   (c - bcytail);
    bhi = c - abig; blo = bcytail - bhi; err1 = s1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    s0 = (alo * blo) - err3;
    t1 =   (acytail * bcxtail);
    c =   (SPLITTER * acytail); abig =   (c - acytail);
    ahi = c - abig; alo = acytail - ahi;
    c =   (SPLITTER * bcxtail); abig =   (c - bcxtail);
    bhi = c - abig; blo = bcxtail - bhi; err1 = t1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    t0 = (alo * blo) - err3;
    _i =   (s0 - t0); bvirt =   (s0 - _i);
    avirt = _i + bvirt; bround = bvirt - t0; around = s0 - avirt;
    u[0] = around + bround; _j =   (s1 + _i);
    bvirt =   (_j - s1); avirt = _j - bvirt; bround = _i - bvirt;
    around = s1 - avirt; _0 = around + bround; _i =   (_0 - t1);
    bvirt =   (_0 - _i); avirt = _i + bvirt; bround = bvirt - t1;
    around = _0 - avirt; u[1] = around + bround;
    u3 =   (_j + _i); bvirt =   (u3 - _j);
    avirt = u3 - bvirt; bround = _i - bvirt; around = _j - avirt;
    u[2] = around + bround;
    u[3] = u3;
    Dlength = sum(C2length, C2, 4, u, D);

    return (D[Dlength - 1]);
  }

  //--------------------------------------------------------------------
  private static final double ccwerrboundA =
    (3.0 + 16.0 * EPSILON) * EPSILON;

  public static final double orient2d (final double[] pa,
                                        final double[] pb,
                                        final double[] pc) {
    double detleft, detright, det;
    double detsum, errbound;

    detleft = (pa[0] - pc[0]) * (pb[1] - pc[1]);
    detright = (pa[1] - pc[1]) * (pb[0] - pc[0]);
    det = detleft - detright;

    if (detleft > 0.0) {
      if (detright <= 0.0) {
        return det;
      }
      else {
        detsum = detleft + detright;
      }
    }
    else if (detleft < 0.0) {
      if (detright >= 0.0) {
        return det;
      }
      else {
        detsum = -detleft - detright;
      }
    }
    else {
      return det;
    }

    errbound = ccwerrboundA * detsum;
    if ((det >= errbound) || (-det >= errbound)) {
      return det;
    }

    return orient2dadapt(pa, pb, pc, detsum);
  }

  //--------------------------------------------------------------------
  // orient3d
  //--------------------------------------------------------------------
  public static final double orient3dfast (final double[] pa,
                                           final double[] pb,
                                           final double[] pc,
                                           final double[] pd) {
    double adx, bdx, cdx;
    double ady, bdy, cdy;
    double adz, bdz, cdz;

    adx = pa[0] - pd[0];
    bdx = pb[0] - pd[0];
    cdx = pc[0] - pd[0];
    ady = pa[1] - pd[1];
    bdy = pb[1] - pd[1];
    cdy = pc[1] - pd[1];
    adz = pa[2] - pd[2];
    bdz = pb[2] - pd[2];
    cdz = pc[2] - pd[2];

    return adx * (bdy * cdz - bdz * cdy)
      + bdx * (cdy * adz - cdz * ady)
      + cdx * (ady * bdz - adz * bdy);
  }

  //--------------------------------------------------------------------
  public static final double orient3dexact (final double[] pa,
                                            final double[] pb,
                                            final double[] pc,
                                            final double[] pd) {
    double axby1, bxcy1, cxdy1, dxay1, axcy1, bxdy1;
    double bxay1, cxby1, dxcy1, axdy1, cxay1, dxby1;
    double axby0, bxcy0, cxdy0, dxay0, axcy0, bxdy0;
    double bxay0, cxby0, dxcy0, axdy0, cxay0, dxby0;
    double[] ab = new double[4], bc = new double[4], cd = new double[4],
      da = new double[4], ac = new double[4], bd = new double[4];
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
    int deterlen;
    int i;

    double bvirt;
    double avirt, bround, around;
    double c;
    double abig;
    double ahi, alo, bhi, blo;
    double err1, err2, err3;
    double _i, _j;
    double _0;

    axby1 =   (pa[0] * pb[1]); c =   (SPLITTER * pa[0]);
    abig =   (c - pa[0]); ahi = c - abig; alo = pa[0] - ahi;
    c =   (SPLITTER * pb[1]); abig =   (c - pb[1]);
    bhi = c - abig; blo = pb[1] - bhi; err1 = axby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axby0 = (alo * blo) - err3;
    bxay1 =   (pb[0] * pa[1]); c =   (SPLITTER * pb[0]);
    abig =   (c - pb[0]); ahi = c - abig; alo = pb[0] - ahi;
    c =   (SPLITTER * pa[1]); abig =   (c - pa[1]);
    bhi = c - abig; blo = pa[1] - bhi; err1 = bxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxay0 = (alo * blo) - err3;
    _i =   (axby0 - bxay0); bvirt =   (axby0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxay0; around = axby0 - avirt;
    ab[0] = around + bround; _j =   (axby1 + _i);
    bvirt =   (_j - axby1); avirt = _j - bvirt;
    bround = _i - bvirt; around = axby1 - avirt; _0 = around + bround;
    _i =   (_0 - bxay1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxay1; around = _0 - avirt;
    ab[1] = around + bround; ab[3] =   (_j + _i);
    bvirt =   (ab[3] - _j); avirt = ab[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; ab[2] = around + bround;

    bxcy1 =   (pb[0] * pc[1]); c =   (SPLITTER * pb[0]);
    abig =   (c - pb[0]); ahi = c - abig; alo = pb[0] - ahi;
    c =   (SPLITTER * pc[1]); abig =   (c - pc[1]);
    bhi = c - abig; blo = pc[1] - bhi; err1 = bxcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxcy0 = (alo * blo) - err3;
    cxby1 =   (pc[0] * pb[1]); c =   (SPLITTER * pc[0]);
    abig =   (c - pc[0]); ahi = c - abig; alo = pc[0] - ahi;
    c =   (SPLITTER * pb[1]); abig =   (c - pb[1]);
    bhi = c - abig; blo = pb[1] - bhi; err1 = cxby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxby0 = (alo * blo) - err3;
    _i =   (bxcy0 - cxby0); bvirt =   (bxcy0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxby0; around = bxcy0 - avirt;
    bc[0] = around + bround; _j =   (bxcy1 + _i);
    bvirt =   (_j - bxcy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = bxcy1 - avirt; _0 = around + bround;
    _i =   (_0 - cxby1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxby1; around = _0 - avirt;
    bc[1] = around + bround; bc[3] =   (_j + _i);
    bvirt =   (bc[3] - _j); avirt = bc[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; bc[2] = around + bround;

    cxdy1 =   (pc[0] * pd[1]); c =   (SPLITTER * pc[0]);
    abig =   (c - pc[0]); ahi = c - abig; alo = pc[0] - ahi;
    c =   (SPLITTER * pd[1]); abig =   (c - pd[1]);
    bhi = c - abig; blo = pd[1] - bhi; err1 = cxdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxdy0 = (alo * blo) - err3;
    dxcy1 =   (pd[0] * pc[1]); c =   (SPLITTER * pd[0]);
    abig =   (c - pd[0]); ahi = c - abig; alo = pd[0] - ahi;
    c =   (SPLITTER * pc[1]); abig =   (c - pc[1]);
    bhi = c - abig; blo = pc[1] - bhi; err1 = dxcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxcy0 = (alo * blo) - err3;
    _i =   (cxdy0 - dxcy0); bvirt =   (cxdy0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxcy0; around = cxdy0 - avirt;
    cd[0] = around + bround; _j =   (cxdy1 + _i);
    bvirt =   (_j - cxdy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = cxdy1 - avirt; _0 = around + bround;
    _i =   (_0 - dxcy1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxcy1; around = _0 - avirt;
    cd[1] = around + bround; cd[3] =   (_j + _i);
    bvirt =   (cd[3] - _j); avirt = cd[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; cd[2] = around + bround;

    dxay1 =   (pd[0] * pa[1]); c =   (SPLITTER * pd[0]);
    abig =   (c - pd[0]); ahi = c - abig; alo = pd[0] - ahi;
    c =   (SPLITTER * pa[1]); abig =   (c - pa[1]);
    bhi = c - abig; blo = pa[1] - bhi; err1 = dxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxay0 = (alo * blo) - err3;
    axdy1 =   (pa[0] * pd[1]); c =   (SPLITTER * pa[0]);
    abig =   (c - pa[0]); ahi = c - abig; alo = pa[0] - ahi;
    c =   (SPLITTER * pd[1]); abig =   (c - pd[1]);
    bhi = c - abig; blo = pd[1] - bhi; err1 = axdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axdy0 = (alo * blo) - err3;
    _i =   (dxay0 - axdy0); bvirt =   (dxay0 - _i);
    avirt = _i + bvirt; bround = bvirt - axdy0; around = dxay0 - avirt;
    da[0] = around + bround; _j =   (dxay1 + _i);
    bvirt =   (_j - dxay1); avirt = _j - bvirt;
    bround = _i - bvirt; around = dxay1 - avirt; _0 = around + bround;
    _i =   (_0 - axdy1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - axdy1; around = _0 - avirt;
    da[1] = around + bround; da[3] =   (_j + _i);
    bvirt =   (da[3] - _j); avirt = da[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; da[2] = around + bround;

    axcy1 =   (pa[0] * pc[1]); c =   (SPLITTER * pa[0]);
    abig =   (c - pa[0]); ahi = c - abig; alo = pa[0] - ahi;
    c =   (SPLITTER * pc[1]); abig =   (c - pc[1]);
    bhi = c - abig; blo = pc[1] - bhi; err1 = axcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axcy0 = (alo * blo) - err3;
    cxay1 =   (pc[0] * pa[1]); c =   (SPLITTER * pc[0]);
    abig =   (c - pc[0]); ahi = c - abig; alo = pc[0] - ahi;
    c =   (SPLITTER * pa[1]); abig =   (c - pa[1]);
    bhi = c - abig; blo = pa[1] - bhi; err1 = cxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxay0 = (alo * blo) - err3;
    _i =   (axcy0 - cxay0); bvirt =   (axcy0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxay0; around = axcy0 - avirt;
    ac[0] = around + bround; _j =   (axcy1 + _i);
    bvirt =   (_j - axcy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = axcy1 - avirt; _0 = around + bround;
    _i =   (_0 - cxay1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxay1; around = _0 - avirt;
    ac[1] = around + bround; ac[3] =   (_j + _i);
    bvirt =   (ac[3] - _j); avirt = ac[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; ac[2] = around + bround;

    bxdy1 =   (pb[0] * pd[1]); c =   (SPLITTER * pb[0]);
    abig =   (c - pb[0]); ahi = c - abig; alo = pb[0] - ahi;
    c =   (SPLITTER * pd[1]); abig =   (c - pd[1]);
    bhi = c - abig; blo = pd[1] - bhi; err1 = bxdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxdy0 = (alo * blo) - err3;
    dxby1 =   (pd[0] * pb[1]); c =   (SPLITTER * pd[0]);
    abig =   (c - pd[0]); ahi = c - abig; alo = pd[0] - ahi;
    c =   (SPLITTER * pb[1]); abig =   (c - pb[1]);
    bhi = c - abig; blo = pb[1] - bhi; err1 = dxby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxby0 = (alo * blo) - err3;
    _i =   (bxdy0 - dxby0); bvirt =   (bxdy0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxby0; around = bxdy0 - avirt;
    bd[0] = around + bround; _j =   (bxdy1 + _i);
    bvirt =   (_j - bxdy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = bxdy1 - avirt; _0 = around + bround;
    _i =   (_0 - dxby1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxby1; around = _0 - avirt;
    bd[1] = around + bround; bd[3] =   (_j + _i);
    bvirt =   (bd[3] - _j); avirt = bd[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; bd[2] = around + bround;

    templen = sum(4, cd, 4, da, temp8);
    cdalen = sum(templen, temp8, 4, ac, cda);
    templen = sum(4, da, 4, ab, temp8);
    dablen = sum(templen, temp8, 4, bd, dab);
    for (i = 0; i < 4; i++) {
      bd[i] = -bd[i];
      ac[i] = -ac[i];
    }
    templen = sum(4, ab, 4, bc, temp8);
    abclen = sum(templen, temp8, 4, ac, abc);
    templen = sum(4, bc, 4, cd, temp8);
    bcdlen = sum(templen, temp8, 4, bd, bcd);

    alen = scale(bcdlen, bcd, pa[2], adet);
    blen = scale(cdalen, cda, -pb[2], bdet);
    clen = scale(dablen, dab, pc[2], cdet);
    dlen = scale(abclen, abc, -pd[2], ddet);

    ablen = sum(alen, adet, blen, bdet, abdet);
    cdlen = sum(clen, cdet, dlen, ddet, cddet);
    deterlen =
      sum(ablen, abdet, cdlen, cddet, deter);

    return deter[deterlen - 1];
  }

  //--------------------------------------------------------------------
  public static final double orient3dslow (final double[] pa,
                                            final double[] pb,
                                            final double[] pc,
                                            final double[] pd) {
    double adx, ady, adz, bdx, bdy, bdz, cdx, cdy, cdz;
    double adxtail, adytail, adztail;
    double bdxtail, bdytail, bdztail;
    double cdxtail, cdytail, cdztail;
    double negate, negatetail;
    double axby7, bxcy7, axcy7, bxay7, cxby7, cxay7;
    double[] axby = new double[8], bxcy = new double[8],
      axcy = new double[8], bxay = new double[8], cxby = new double[8],
      cxay = new double[8];
    double[] temp16 = new double[16],
      temp32 = new double[32], temp32t = new double[32];
    int temp16len, temp32len, temp32tlen;
    double[] adet = new double[64],
      bdet = new double[64],
      cdet = new double[64];
    int alen, blen, clen;
    double[] abdet = new double[128];
    int ablen;
    double[] deter = new double[192];
    int deterlen;

    double bvirt;
    double avirt, bround, around;
    double c;
    double abig;
    double a0hi, a0lo, a1hi, a1lo, bhi, blo;
    double err1, err2, err3;
    double _i, _j, _k, _l, _m, _n;
    double _0, _1, _2;

    adx =   (pa[0] - pd[0]); bvirt =   (pa[0] - adx);
    avirt = adx + bvirt; bround = bvirt - pd[0]; around = pa[0] - avirt;
    adxtail = around + bround;
    ady =   (pa[1] - pd[1]); bvirt =   (pa[1] - ady);
    avirt = ady + bvirt; bround = bvirt - pd[1]; around = pa[1] - avirt;
    adytail = around + bround;
    adz =   (pa[2] - pd[2]); bvirt =   (pa[2] - adz);
    avirt = adz + bvirt; bround = bvirt - pd[2]; around = pa[2] - avirt;
    adztail = around + bround;
    bdx =   (pb[0] - pd[0]); bvirt =   (pb[0] - bdx);
    avirt = bdx + bvirt; bround = bvirt - pd[0]; around = pb[0] - avirt;
    bdxtail = around + bround;
    bdy =   (pb[1] - pd[1]); bvirt =   (pb[1] - bdy);
    avirt = bdy + bvirt; bround = bvirt - pd[1]; around = pb[1] - avirt;
    bdytail = around + bround;
    bdz =   (pb[2] - pd[2]); bvirt =   (pb[2] - bdz);
    avirt = bdz + bvirt; bround = bvirt - pd[2]; around = pb[2] - avirt;
    bdztail = around + bround;
    cdx =   (pc[0] - pd[0]); bvirt =   (pc[0] - cdx);
    avirt = cdx + bvirt; bround = bvirt - pd[0]; around = pc[0] - avirt;
    cdxtail = around + bround;
    cdy =   (pc[1] - pd[1]); bvirt =   (pc[1] - cdy);
    avirt = cdy + bvirt; bround = bvirt - pd[1]; around = pc[1] - avirt;
    cdytail = around + bround;
    cdz =   (pc[2] - pd[2]); bvirt =   (pc[2] - cdz);
    avirt = cdz + bvirt; bround = bvirt - pd[2]; around = pc[2] - avirt;
    cdztail = around + bround;

    c =   (SPLITTER * adxtail); abig =   (c - adxtail);
    a0hi = c - abig; a0lo = adxtail - a0hi;
    c =   (SPLITTER * bdytail); abig =   (c - bdytail);
    bhi = c - abig; blo = bdytail - bhi;
    _i =   (adxtail * bdytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    axby[0] = (a0lo * blo) - err3; c =   (SPLITTER * adx);
    abig =   (c - adx); a1hi = c - abig; a1lo = adx - a1hi;
    _j =   (adx * bdytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * bdy);
    abig =   (c - bdy); bhi = c - abig; blo = bdy - bhi;
    _i =   (adxtail * bdy); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axby[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (adx * bdy); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axby[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axby[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; axby[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    axby[5] = around + bround; axby7 =   (_m + _k);
    bvirt =   (axby7 - _m); avirt = axby7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; axby[6] = around + bround

    ;
    axby[7] = axby7;
    negate = -ady;
    negatetail = -adytail;
    c =   (SPLITTER * bdxtail); abig =   (c - bdxtail);
    a0hi = c - abig; a0lo = bdxtail - a0hi;
    c =   (SPLITTER * negatetail);
    abig =   (c - negatetail); bhi = c - abig;
    blo = negatetail - bhi; _i =   (bdxtail * negatetail);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); bxay[0] = (a0lo * blo) - err3;
    c =   (SPLITTER * bdx); abig =   (c - bdx);
    a1hi = c - abig; a1lo = bdx - a1hi;
    _j =   (bdx * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * negate);
    abig =   (c - negate); bhi = c - abig; blo = negate - bhi;
    _i =   (bdxtail * negate); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxay[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (bdx * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxay[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxay[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; bxay[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    bxay[5] = around + bround; bxay7 =   (_m + _k);
    bvirt =   (bxay7 - _m); avirt = bxay7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; bxay[6] = around + bround

    ;
    bxay[7] = bxay7;
    c =   (SPLITTER * bdxtail); abig =   (c - bdxtail);
    a0hi = c - abig; a0lo = bdxtail - a0hi;
    c =   (SPLITTER * cdytail); abig =   (c - cdytail);
    bhi = c - abig; blo = cdytail - bhi;
    _i =   (bdxtail * cdytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    bxcy[0] = (a0lo * blo) - err3; c =   (SPLITTER * bdx);
    abig =   (c - bdx); a1hi = c - abig; a1lo = bdx - a1hi;
    _j =   (bdx * cdytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * cdy);
    abig =   (c - cdy); bhi = c - abig; blo = cdy - bhi;
    _i =   (bdxtail * cdy); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxcy[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (bdx * cdy); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxcy[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxcy[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; bxcy[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    bxcy[5] = around + bround; bxcy7 =   (_m + _k);
    bvirt =   (bxcy7 - _m); avirt = bxcy7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; bxcy[6] = around + bround

    ;
    bxcy[7] = bxcy7;
    negate = -bdy;
    negatetail = -bdytail;
    c =   (SPLITTER * cdxtail); abig =   (c - cdxtail);
    a0hi = c - abig; a0lo = cdxtail - a0hi;
    c =   (SPLITTER * negatetail);
    abig =   (c - negatetail); bhi = c - abig;
    blo = negatetail - bhi; _i =   (cdxtail * negatetail);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); cxby[0] = (a0lo * blo) - err3;
    c =   (SPLITTER * cdx); abig =   (c - cdx);
    a1hi = c - abig; a1lo = cdx - a1hi;
    _j =   (cdx * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * negate);
    abig =   (c - negate); bhi = c - abig; blo = negate - bhi;
    _i =   (cdxtail * negate); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxby[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (cdx * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxby[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxby[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; cxby[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    cxby[5] = around + bround; cxby7 =   (_m + _k);
    bvirt =   (cxby7 - _m); avirt = cxby7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; cxby[6] = around + bround

    ;
    cxby[7] = cxby7;
    c =   (SPLITTER * cdxtail); abig =   (c - cdxtail);
    a0hi = c - abig; a0lo = cdxtail - a0hi;
    c =   (SPLITTER * adytail); abig =   (c - adytail);
    bhi = c - abig; blo = adytail - bhi;
    _i =   (cdxtail * adytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    cxay[0] = (a0lo * blo) - err3; c =   (SPLITTER * cdx);
    abig =   (c - cdx); a1hi = c - abig; a1lo = cdx - a1hi;
    _j =   (cdx * adytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * ady);
    abig =   (c - ady); bhi = c - abig; blo = ady - bhi;
    _i =   (cdxtail * ady); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxay[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (cdx * ady); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxay[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxay[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; cxay[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    cxay[5] = around + bround; cxay7 =   (_m + _k);
    bvirt =   (cxay7 - _m); avirt = cxay7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; cxay[6] = around + bround

    ;
    cxay[7] = cxay7;
    negate = -cdy;
    negatetail = -cdytail;
    c =   (SPLITTER * adxtail); abig =   (c - adxtail);
    a0hi = c - abig; a0lo = adxtail - a0hi;
    c =   (SPLITTER * negatetail);
    abig =   (c - negatetail); bhi = c - abig;
    blo = negatetail - bhi; _i =   (adxtail * negatetail);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); axcy[0] = (a0lo * blo) - err3;
    c =   (SPLITTER * adx); abig =   (c - adx);
    a1hi = c - abig; a1lo = adx - a1hi;
    _j =   (adx * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * negate);
    abig =   (c - negate); bhi = c - abig; blo = negate - bhi;
    _i =   (adxtail * negate); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axcy[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (adx * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axcy[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axcy[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; axcy[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    axcy[5] = around + bround; axcy7 =   (_m + _k);
    bvirt =   (axcy7 - _m); avirt = axcy7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; axcy[6] = around + bround

    ;
    axcy[7] = axcy7;

    temp16len = sum(8, bxcy, 8, cxby, temp16);
    temp32len =
      scale(temp16len, temp16, adz, temp32);
    temp32tlen =
      scale(temp16len, temp16, adztail, temp32t);
    alen = sum(temp32len, temp32, temp32tlen,
               temp32t,
               adet);

    temp16len = sum(8, cxay, 8, axcy, temp16);
    temp32len =
      scale(temp16len, temp16, bdz, temp32);
    temp32tlen =
      scale(temp16len, temp16, bdztail, temp32t);
    blen = sum(temp32len, temp32, temp32tlen,
               temp32t,
               bdet);

    temp16len = sum(8, axby, 8, bxay, temp16);
    temp32len =
      scale(temp16len, temp16, cdz, temp32);
    temp32tlen =
      scale(temp16len, temp16, cdztail, temp32t);
    clen = sum(temp32len, temp32, temp32tlen,
               temp32t,
               cdet);

    ablen = sum(alen, adet, blen, bdet, abdet);
    deterlen =
      sum(ablen, abdet, clen, cdet, deter);

    return deter[deterlen - 1];
  }

  //--------------------------------------------------------------------
  private static final double o3derrboundB =
    (3.0 + 28.0 * EPSILON) * EPSILON;
  private static final double o3derrboundC =
    (26.0 + 288.0 * EPSILON) * EPSILON * EPSILON;

  private static final double orient3dadapt (final double[] pa,
                                             final double[] pb,
                                             final double[] pc,
                                             final double[] pd,
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

    adx =   (pa[0] - pd[0]);
    bdx =   (pb[0] - pd[0]);
    cdx =   (pc[0] - pd[0]);
    ady =   (pa[1] - pd[1]);
    bdy =   (pb[1] - pd[1]);
    cdy =   (pc[1] - pd[1]);
    adz =   (pa[2] - pd[2]);
    bdz =   (pb[2] - pd[2]);
    cdz =   (pc[2] - pd[2]);

    bdxcdy1 =   (bdx * cdy); c =   (SPLITTER * bdx);
    abig =   (c - bdx); ahi = c - abig; alo = bdx - ahi;
    c =   (SPLITTER * cdy); abig =   (c - cdy);
    bhi = c - abig; blo = cdy - bhi; err1 = bdxcdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bdxcdy0 = (alo * blo) - err3;
    cdxbdy1 =   (cdx * bdy); c =   (SPLITTER * cdx);
    abig =   (c - cdx); ahi = c - abig; alo = cdx - ahi;
    c =   (SPLITTER * bdy); abig =   (c - bdy);
    bhi = c - abig; blo = bdy - bhi; err1 = cdxbdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cdxbdy0 = (alo * blo) - err3;
    _i =   (bdxcdy0 - cdxbdy0); bvirt =   (bdxcdy0 - _i);
    avirt = _i + bvirt; bround = bvirt - cdxbdy0;
    around = bdxcdy0 - avirt; bc[0] = around + bround;
    _j =   (bdxcdy1 + _i); bvirt =   (_j - bdxcdy1);
    avirt = _j - bvirt; bround = _i - bvirt; around = bdxcdy1 - avirt;
    _0 = around + bround; _i =   (_0 - cdxbdy1);
    bvirt =   (_0 - _i); avirt = _i + bvirt;
    bround = bvirt - cdxbdy1; around = _0 - avirt;
    bc[1] = around + bround; bc3 =   (_j + _i);
    bvirt =   (bc3 - _j); avirt = bc3 - bvirt;
    bround = _i - bvirt; around = _j - avirt; bc[2] = around + bround;
    bc[3] = bc3;
    alen = scale(4, bc, adz, adet);

    cdxady1 =   (cdx * ady); c =   (SPLITTER * cdx);
    abig =   (c - cdx); ahi = c - abig; alo = cdx - ahi;
    c =   (SPLITTER * ady); abig =   (c - ady);
    bhi = c - abig; blo = ady - bhi; err1 = cdxady1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cdxady0 = (alo * blo) - err3;
    adxcdy1 =   (adx * cdy); c =   (SPLITTER * adx);
    abig =   (c - adx); ahi = c - abig; alo = adx - ahi;
    c =   (SPLITTER * cdy); abig =   (c - cdy);
    bhi = c - abig; blo = cdy - bhi; err1 = adxcdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    adxcdy0 = (alo * blo) - err3;
    _i =   (cdxady0 - adxcdy0); bvirt =   (cdxady0 - _i);
    avirt = _i + bvirt; bround = bvirt - adxcdy0;
    around = cdxady0 - avirt; ca[0] = around + bround;
    _j =   (cdxady1 + _i); bvirt =   (_j - cdxady1);
    avirt = _j - bvirt; bround = _i - bvirt; around = cdxady1 - avirt;
    _0 = around + bround; _i =   (_0 - adxcdy1);
    bvirt =   (_0 - _i); avirt = _i + bvirt;
    bround = bvirt - adxcdy1; around = _0 - avirt;
    ca[1] = around + bround; ca3 =   (_j + _i);
    bvirt =   (ca3 - _j); avirt = ca3 - bvirt;
    bround = _i - bvirt; around = _j - avirt; ca[2] = around + bround;
    ca[3] = ca3;
    blen = scale(4, ca, bdz, bdet);

    adxbdy1 =   (adx * bdy); c =   (SPLITTER * adx);
    abig =   (c - adx); ahi = c - abig; alo = adx - ahi;
    c =   (SPLITTER * bdy); abig =   (c - bdy);
    bhi = c - abig; blo = bdy - bhi; err1 = adxbdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    adxbdy0 = (alo * blo) - err3;
    bdxady1 =   (bdx * ady); c =   (SPLITTER * bdx);
    abig =   (c - bdx); ahi = c - abig; alo = bdx - ahi;
    c =   (SPLITTER * ady); abig =   (c - ady);
    bhi = c - abig; blo = ady - bhi; err1 = bdxady1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bdxady0 = (alo * blo) - err3;
    _i =   (adxbdy0 - bdxady0); bvirt =   (adxbdy0 - _i);
    avirt = _i + bvirt; bround = bvirt - bdxady0;
    around = adxbdy0 - avirt; ab[0] = around + bround;
    _j =   (adxbdy1 + _i); bvirt =   (_j - adxbdy1);
    avirt = _j - bvirt; bround = _i - bvirt; around = adxbdy1 - avirt;
    _0 = around + bround; _i =   (_0 - bdxady1);
    bvirt =   (_0 - _i); avirt = _i + bvirt;
    bround = bvirt - bdxady1; around = _0 - avirt;
    ab[1] = around + bround; ab3 =   (_j + _i);
    bvirt =   (ab3 - _j); avirt = ab3 - bvirt;
    bround = _i - bvirt; around = _j - avirt; ab[2] = around + bround;
    ab[3] = ab3;
    clen = scale(4, ab, cdz, cdet);

    ablen = sum(alen, adet, blen, bdet, abdet);
    finlength =
      sum(ablen, abdet, clen, cdet, fin1);

    det = estimate(finlength, fin1);
    errbound = o3derrboundB * permanent;
    if ((det >= errbound) || (-det >= errbound)) {
      return det;
    }

    bvirt =   (pa[0] - adx); avirt = adx + bvirt;
    bround = bvirt - pd[0]; around = pa[0] - avirt;
    adxtail = around + bround;
    bvirt =   (pb[0] - bdx); avirt = bdx + bvirt;
    bround = bvirt - pd[0]; around = pb[0] - avirt;
    bdxtail = around + bround;
    bvirt =   (pc[0] - cdx); avirt = cdx + bvirt;
    bround = bvirt - pd[0]; around = pc[0] - avirt;
    cdxtail = around + bround;
    bvirt =   (pa[1] - ady); avirt = ady + bvirt;
    bround = bvirt - pd[1]; around = pa[1] - avirt;
    adytail = around + bround;
    bvirt =   (pb[1] - bdy); avirt = bdy + bvirt;
    bround = bvirt - pd[1]; around = pb[1] - avirt;
    bdytail = around + bround;
    bvirt =   (pc[1] - cdy); avirt = cdy + bvirt;
    bround = bvirt - pd[1]; around = pc[1] - avirt;
    cdytail = around + bround;
    bvirt =   (pa[2] - adz); avirt = adz + bvirt;
    bround = bvirt - pd[2]; around = pa[2] - avirt;
    adztail = around + bround;
    bvirt =   (pb[2] - bdz); avirt = bdz + bvirt;
    bround = bvirt - pd[2]; around = pb[2] - avirt;
    bdztail = around + bround;
    bvirt =   (pc[2] - cdz); avirt = cdz + bvirt;
    bround = bvirt - pd[2]; around = pc[2] - avirt;
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
    if ((det >= errbound) || (-det >= errbound)) {
      return det;
    }

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
        at_blarge =   (negate * bdx);
        c =   (SPLITTER * negate); abig =   (c - negate);
        ahi = c - abig; alo = negate - ahi;
        c =   (SPLITTER * bdx); abig =   (c - bdx);
        bhi = c - abig; blo = bdx - bhi; err1 = at_blarge - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        at_b[0] = (alo * blo) - err3;
        at_b[1] = at_blarge;
        at_blen = 2;
        at_clarge =   (adytail * cdx);
        c =   (SPLITTER * adytail);
        abig =   (c - adytail); ahi = c - abig;
        alo = adytail - ahi; c =   (SPLITTER * cdx);
        abig =   (c - cdx); bhi = c - abig; blo = cdx - bhi;
        err1 = at_clarge - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); at_c[0] = (alo * blo) - err3;
        at_c[1] = at_clarge;
        at_clen = 2;
      }
    }
    else {
      if (adytail == 0.0) {
        at_blarge =   (adxtail * bdy);
        c =   (SPLITTER * adxtail);
        abig =   (c - adxtail); ahi = c - abig;
        alo = adxtail - ahi; c =   (SPLITTER * bdy);
        abig =   (c - bdy); bhi = c - abig; blo = bdy - bhi;
        err1 = at_blarge - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); at_b[0] = (alo * blo) - err3;
        at_b[1] = at_blarge;
        at_blen = 2;
        negate = -adxtail;
        at_clarge =   (negate * cdy);
        c =   (SPLITTER * negate); abig =   (c - negate);
        ahi = c - abig; alo = negate - ahi;
        c =   (SPLITTER * cdy); abig =   (c - cdy);
        bhi = c - abig; blo = cdy - bhi; err1 = at_clarge - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        at_c[0] = (alo * blo) - err3;
        at_c[1] = at_clarge;
        at_clen = 2;
      }
      else {
        adxt_bdy1 =   (adxtail * bdy);
        c =   (SPLITTER * adxtail);
        abig =   (c - adxtail); ahi = c - abig;
        alo = adxtail - ahi; c =   (SPLITTER * bdy);
        abig =   (c - bdy); bhi = c - abig; blo = bdy - bhi;
        err1 = adxt_bdy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); adxt_bdy0 = (alo * blo) - err3;
        adyt_bdx1 =   (adytail * bdx);
        c =   (SPLITTER * adytail);
        abig =   (c - adytail); ahi = c - abig;
        alo = adytail - ahi; c =   (SPLITTER * bdx);
        abig =   (c - bdx); bhi = c - abig; blo = bdx - bhi;
        err1 = adyt_bdx1 - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); adyt_bdx0 = (alo * blo) - err3;
        _i =   (adxt_bdy0 - adyt_bdx0);
        bvirt =   (adxt_bdy0 - _i); avirt = _i + bvirt;
        bround = bvirt - adyt_bdx0; around = adxt_bdy0 - avirt;
        at_b[0] = around + bround; _j =   (adxt_bdy1 + _i);
        bvirt =   (_j - adxt_bdy1); avirt = _j - bvirt;
        bround = _i - bvirt; around = adxt_bdy1 - avirt;
        _0 = around + bround; _i =   (_0 - adyt_bdx1);
        bvirt =   (_0 - _i); avirt = _i + bvirt;
        bround = bvirt - adyt_bdx1; around = _0 - avirt;
        at_b[1] = around + bround; at_blarge =   (_j + _i);
        bvirt =   (at_blarge - _j); avirt = at_blarge - bvirt;
        bround = _i - bvirt; around = _j - avirt;
        at_b[2] = around + bround
        ;
        at_b[3] = at_blarge;
        at_blen = 4;
        adyt_cdx1 =   (adytail * cdx);
        c =   (SPLITTER * adytail);
        abig =   (c - adytail); ahi = c - abig;
        alo = adytail - ahi; c =   (SPLITTER * cdx);
        abig =   (c - cdx); bhi = c - abig; blo = cdx - bhi;
        err1 = adyt_cdx1 - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); adyt_cdx0 = (alo * blo) - err3;
        adxt_cdy1 =   (adxtail * cdy);
        c =   (SPLITTER * adxtail);
        abig =   (c - adxtail); ahi = c - abig;
        alo = adxtail - ahi; c =   (SPLITTER * cdy);
        abig =   (c - cdy); bhi = c - abig; blo = cdy - bhi;
        err1 = adxt_cdy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); adxt_cdy0 = (alo * blo) - err3;
        _i =   (adyt_cdx0 - adxt_cdy0);
        bvirt =   (adyt_cdx0 - _i); avirt = _i + bvirt;
        bround = bvirt - adxt_cdy0; around = adyt_cdx0 - avirt;
        at_c[0] = around + bround; _j =   (adyt_cdx1 + _i);
        bvirt =   (_j - adyt_cdx1); avirt = _j - bvirt;
        bround = _i - bvirt; around = adyt_cdx1 - avirt;
        _0 = around + bround; _i =   (_0 - adxt_cdy1);
        bvirt =   (_0 - _i); avirt = _i + bvirt;
        bround = bvirt - adxt_cdy1; around = _0 - avirt;
        at_c[1] = around + bround; at_clarge =   (_j + _i);
        bvirt =   (at_clarge - _j); avirt = at_clarge - bvirt;
        bround = _i - bvirt; around = _j - avirt;
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
        bt_clarge =   (negate * cdx);
        c =   (SPLITTER * negate); abig =   (c - negate);
        ahi = c - abig; alo = negate - ahi;
        c =   (SPLITTER * cdx); abig =   (c - cdx);
        bhi = c - abig; blo = cdx - bhi; err1 = bt_clarge - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        bt_c[0] = (alo * blo) - err3;
        bt_c[1] = bt_clarge;
        bt_clen = 2;
        bt_alarge =   (bdytail * adx);
        c =   (SPLITTER * bdytail);
        abig =   (c - bdytail); ahi = c - abig;
        alo = bdytail - ahi; c =   (SPLITTER * adx);
        abig =   (c - adx); bhi = c - abig; blo = adx - bhi;
        err1 = bt_alarge - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); bt_a[0] = (alo * blo) - err3;
        bt_a[1] = bt_alarge;
        bt_alen = 2;
      }
    }
    else {
      if (bdytail == 0.0) {
        bt_clarge =   (bdxtail * cdy);
        c =   (SPLITTER * bdxtail);
        abig =   (c - bdxtail); ahi = c - abig;
        alo = bdxtail - ahi; c =   (SPLITTER * cdy);
        abig =   (c - cdy); bhi = c - abig; blo = cdy - bhi;
        err1 = bt_clarge - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); bt_c[0] = (alo * blo) - err3;
        bt_c[1] = bt_clarge;
        bt_clen = 2;
        negate = -bdxtail;
        bt_alarge =   (negate * ady);
        c =   (SPLITTER * negate); abig =   (c - negate);
        ahi = c - abig; alo = negate - ahi;
        c =   (SPLITTER * ady); abig =   (c - ady);
        bhi = c - abig; blo = ady - bhi; err1 = bt_alarge - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        bt_a[0] = (alo * blo) - err3;
        bt_a[1] = bt_alarge;
        bt_alen = 2;
      }
      else {
        bdxt_cdy1 =   (bdxtail * cdy);
        c =   (SPLITTER * bdxtail);
        abig =   (c - bdxtail); ahi = c - abig;
        alo = bdxtail - ahi; c =   (SPLITTER * cdy);
        abig =   (c - cdy); bhi = c - abig; blo = cdy - bhi;
        err1 = bdxt_cdy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); bdxt_cdy0 = (alo * blo) - err3;
        bdyt_cdx1 =   (bdytail * cdx);
        c =   (SPLITTER * bdytail);
        abig =   (c - bdytail); ahi = c - abig;
        alo = bdytail - ahi; c =   (SPLITTER * cdx);
        abig =   (c - cdx); bhi = c - abig; blo = cdx - bhi;
        err1 = bdyt_cdx1 - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); bdyt_cdx0 = (alo * blo) - err3;
        _i =   (bdxt_cdy0 - bdyt_cdx0);
        bvirt =   (bdxt_cdy0 - _i); avirt = _i + bvirt;
        bround = bvirt - bdyt_cdx0; around = bdxt_cdy0 - avirt;
        bt_c[0] = around + bround; _j =   (bdxt_cdy1 + _i);
        bvirt =   (_j - bdxt_cdy1); avirt = _j - bvirt;
        bround = _i - bvirt; around = bdxt_cdy1 - avirt;
        _0 = around + bround; _i =   (_0 - bdyt_cdx1);
        bvirt =   (_0 - _i); avirt = _i + bvirt;
        bround = bvirt - bdyt_cdx1; around = _0 - avirt;
        bt_c[1] = around + bround; bt_clarge =   (_j + _i);
        bvirt =   (bt_clarge - _j); avirt = bt_clarge - bvirt;
        bround = _i - bvirt; around = _j - avirt;
        bt_c[2] = around + bround
        ;
        bt_c[3] = bt_clarge;
        bt_clen = 4;
        bdyt_adx1 =   (bdytail * adx);
        c =   (SPLITTER * bdytail);
        abig =   (c - bdytail); ahi = c - abig;
        alo = bdytail - ahi; c =   (SPLITTER * adx);
        abig =   (c - adx); bhi = c - abig; blo = adx - bhi;
        err1 = bdyt_adx1 - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); bdyt_adx0 = (alo * blo) - err3;
        bdxt_ady1 =   (bdxtail * ady);
        c =   (SPLITTER * bdxtail);
        abig =   (c - bdxtail); ahi = c - abig;
        alo = bdxtail - ahi; c =   (SPLITTER * ady);
        abig =   (c - ady); bhi = c - abig; blo = ady - bhi;
        err1 = bdxt_ady1 - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); bdxt_ady0 = (alo * blo) - err3;
        _i =   (bdyt_adx0 - bdxt_ady0);
        bvirt =   (bdyt_adx0 - _i); avirt = _i + bvirt;
        bround = bvirt - bdxt_ady0; around = bdyt_adx0 - avirt;
        bt_a[0] = around + bround; _j =   (bdyt_adx1 + _i);
        bvirt =   (_j - bdyt_adx1); avirt = _j - bvirt;
        bround = _i - bvirt; around = bdyt_adx1 - avirt;
        _0 = around + bround; _i =   (_0 - bdxt_ady1);
        bvirt =   (_0 - _i); avirt = _i + bvirt;
        bround = bvirt - bdxt_ady1; around = _0 - avirt;
        bt_a[1] = around + bround; bt_alarge =   (_j + _i);
        bvirt =   (bt_alarge - _j); avirt = bt_alarge - bvirt;
        bround = _i - bvirt; around = _j - avirt;
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
        ct_alarge =   (negate * adx);
        c =   (SPLITTER * negate); abig =   (c - negate);
        ahi = c - abig; alo = negate - ahi;
        c =   (SPLITTER * adx); abig =   (c - adx);
        bhi = c - abig; blo = adx - bhi; err1 = ct_alarge - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        ct_a[0] = (alo * blo) - err3;
        ct_a[1] = ct_alarge;
        ct_alen = 2;
        ct_blarge =   (cdytail * bdx);
        c =   (SPLITTER * cdytail);
        abig =   (c - cdytail); ahi = c - abig;
        alo = cdytail - ahi; c =   (SPLITTER * bdx);
        abig =   (c - bdx); bhi = c - abig; blo = bdx - bhi;
        err1 = ct_blarge - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); ct_b[0] = (alo * blo) - err3;
        ct_b[1] = ct_blarge;
        ct_blen = 2;
      }
    }
    else {
      if (cdytail == 0.0) {
        ct_alarge =   (cdxtail * ady);
        c =   (SPLITTER * cdxtail);
        abig =   (c - cdxtail); ahi = c - abig;
        alo = cdxtail - ahi; c =   (SPLITTER * ady);
        abig =   (c - ady); bhi = c - abig; blo = ady - bhi;
        err1 = ct_alarge - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); ct_a[0] = (alo * blo) - err3;
        ct_a[1] = ct_alarge;
        ct_alen = 2;
        negate = -cdxtail;
        ct_blarge =   (negate * bdy);
        c =   (SPLITTER * negate); abig =   (c - negate);
        ahi = c - abig; alo = negate - ahi;
        c =   (SPLITTER * bdy); abig =   (c - bdy);
        bhi = c - abig; blo = bdy - bhi; err1 = ct_blarge - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        ct_b[0] = (alo * blo) - err3;
        ct_b[1] = ct_blarge;
        ct_blen = 2;
      }
      else {
        cdxt_ady1 =   (cdxtail * ady);
        c =   (SPLITTER * cdxtail);
        abig =   (c - cdxtail); ahi = c - abig;
        alo = cdxtail - ahi; c =   (SPLITTER * ady);
        abig =   (c - ady); bhi = c - abig; blo = ady - bhi;
        err1 = cdxt_ady1 - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); cdxt_ady0 = (alo * blo) - err3;
        cdyt_adx1 =   (cdytail * adx);
        c =   (SPLITTER * cdytail);
        abig =   (c - cdytail); ahi = c - abig;
        alo = cdytail - ahi; c =   (SPLITTER * adx);
        abig =   (c - adx); bhi = c - abig; blo = adx - bhi;
        err1 = cdyt_adx1 - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); cdyt_adx0 = (alo * blo) - err3;
        _i =   (cdxt_ady0 - cdyt_adx0);
        bvirt =   (cdxt_ady0 - _i); avirt = _i + bvirt;
        bround = bvirt - cdyt_adx0; around = cdxt_ady0 - avirt;
        ct_a[0] = around + bround; _j =   (cdxt_ady1 + _i);
        bvirt =   (_j - cdxt_ady1); avirt = _j - bvirt;
        bround = _i - bvirt; around = cdxt_ady1 - avirt;
        _0 = around + bround; _i =   (_0 - cdyt_adx1);
        bvirt =   (_0 - _i); avirt = _i + bvirt;
        bround = bvirt - cdyt_adx1; around = _0 - avirt;
        ct_a[1] = around + bround; ct_alarge =   (_j + _i);
        bvirt =   (ct_alarge - _j); avirt = ct_alarge - bvirt;
        bround = _i - bvirt; around = _j - avirt;
        ct_a[2] = around + bround
        ;
        ct_a[3] = ct_alarge;
        ct_alen = 4;
        cdyt_bdx1 =   (cdytail * bdx);
        c =   (SPLITTER * cdytail);
        abig =   (c - cdytail); ahi = c - abig;
        alo = cdytail - ahi; c =   (SPLITTER * bdx);
        abig =   (c - bdx); bhi = c - abig; blo = bdx - bhi;
        err1 = cdyt_bdx1 - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); cdyt_bdx0 = (alo * blo) - err3;
        cdxt_bdy1 =   (cdxtail * bdy);
        c =   (SPLITTER * cdxtail);
        abig =   (c - cdxtail); ahi = c - abig;
        alo = cdxtail - ahi; c =   (SPLITTER * bdy);
        abig =   (c - bdy); bhi = c - abig; blo = bdy - bhi;
        err1 = cdxt_bdy1 - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); cdxt_bdy0 = (alo * blo) - err3;
        _i =   (cdyt_bdx0 - cdxt_bdy0);
        bvirt =   (cdyt_bdx0 - _i); avirt = _i + bvirt;
        bround = bvirt - cdxt_bdy0; around = cdyt_bdx0 - avirt;
        ct_b[0] = around + bround; _j =   (cdyt_bdx1 + _i);
        bvirt =   (_j - cdyt_bdx1); avirt = _j - bvirt;
        bround = _i - bvirt; around = cdyt_bdx1 - avirt;
        _0 = around + bround; _i =   (_0 - cdxt_bdy1);
        bvirt =   (_0 - _i); avirt = _i + bvirt;
        bround = bvirt - cdxt_bdy1; around = _0 - avirt;
        ct_b[1] = around + bround; ct_blarge =   (_j + _i);
        bvirt =   (ct_blarge - _j); avirt = ct_blarge - bvirt;
        bround = _i - bvirt; around = _j - avirt;
        ct_b[2] = around + bround
        ;
        ct_b[3] = ct_blarge;
        ct_blen = 4;
      }
    }

    bctlen =
      sum(bt_clen, bt_c, ct_blen, ct_b, bct);
    wlength = scale(bctlen, bct, adz, w);
    finlength =
      sum(finlength, finnow, wlength, w,
          finother);
    finswap = finnow; finnow = finother; finother = finswap;

    catlen =
      sum(ct_alen, ct_a, at_clen, at_c, cat);
    wlength = scale(catlen, cat, bdz, w);
    finlength =
      sum(finlength, finnow, wlength, w,
          finother);
    finswap = finnow; finnow = finother; finother = finswap;

    abtlen =
      sum(at_blen, at_b, bt_alen, bt_a, abt);
    wlength = scale(abtlen, abt, cdz, w);
    finlength =
      sum(finlength, finnow, wlength, w,
          finother);
    finswap = finnow; finnow = finother; finother = finswap;

    if (adztail != 0.0) {
      vlength = scale(4, bc, adztail, v);
      finlength =
        sum(finlength, finnow, vlength, v,
            finother);
      finswap = finnow; finnow = finother; finother = finswap;
    }
    if (bdztail != 0.0) {
      vlength = scale(4, ca, bdztail, v);
      finlength =
        sum(finlength, finnow, vlength, v,
            finother);
      finswap = finnow; finnow = finother; finother = finswap;
    }
    if (cdztail != 0.0) {
      vlength = scale(4, ab, cdztail, v);
      finlength =
        sum(finlength, finnow, vlength, v,
            finother);
      finswap = finnow; finnow = finother; finother = finswap;
    }

    if (adxtail != 0.0) {
      if (bdytail != 0.0) {
        adxt_bdyt1 =   (adxtail * bdytail);
        c =   (SPLITTER * adxtail);
        abig =   (c - adxtail); ahi = c - abig;
        alo = adxtail - ahi; c =   (SPLITTER * bdytail);
        abig =   (c - bdytail); bhi = c - abig;
        blo = bdytail - bhi; err1 = adxt_bdyt1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        adxt_bdyt0 = (alo * blo) - err3;
        c =   (SPLITTER * cdz); abig =   (c - cdz);
        bhi = c - abig; blo = cdz - bhi;
        _i =   (adxt_bdyt0 * cdz);
        c =   (SPLITTER * adxt_bdyt0);
        abig =   (c - adxt_bdyt0); ahi = c - abig;
        alo = adxt_bdyt0 - ahi; err1 = _i - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        u[0] = (alo * blo) - err3; _j =   (adxt_bdyt1 * cdz);
        c =   (SPLITTER * adxt_bdyt1);
        abig =   (c - adxt_bdyt1); ahi = c - abig;
        alo = adxt_bdyt1 - ahi; err1 = _j - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        _0 = (alo * blo) - err3; _k =   (_i + _0);
        bvirt =   (_k - _i); avirt = _k - bvirt;
        bround = _0 - bvirt; around = _i - avirt;
        u[1] = around + bround; u3 =   (_j + _k);
        bvirt = u3 - _j; u[2] = _k - bvirt;
        u[3] = u3;
        finlength = sum(finlength, finnow, 4, u,
                        finother);
        finswap = finnow; finnow = finother; finother = finswap;
        if (cdztail != 0.0) {
          c =   (SPLITTER * cdztail);
          abig =   (c - cdztail); bhi = c - abig;
          blo = cdztail - bhi; _i =   (adxt_bdyt0 * cdztail);
          c =   (SPLITTER * adxt_bdyt0);
          abig =   (c - adxt_bdyt0); ahi = c - abig;
          alo = adxt_bdyt0 - ahi; err1 = _i - (ahi * bhi);
          err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
          u[0] = (alo * blo) - err3;
          _j =   (adxt_bdyt1 * cdztail);
          c =   (SPLITTER * adxt_bdyt1);
          abig =   (c - adxt_bdyt1); ahi = c - abig;
          alo = adxt_bdyt1 - ahi; err1 = _j - (ahi * bhi);
          err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
          _0 = (alo * blo) - err3; _k =   (_i + _0);
          bvirt =   (_k - _i); avirt = _k - bvirt;
          bround = _0 - bvirt; around = _i - avirt;
          u[1] = around + bround; u3 =   (_j + _k);
          bvirt = u3 - _j; u[2] = _k - bvirt;
          u[3] = u3;
          finlength =
            sum(finlength, finnow, 4, u,
                finother);
          finswap = finnow; finnow = finother; finother = finswap;
        }
      }
      if (cdytail != 0.0) {
        negate = -adxtail;
        adxt_cdyt1 =   (negate * cdytail);
        c =   (SPLITTER * negate); abig =   (c - negate);
        ahi = c - abig; alo = negate - ahi;
        c =   (SPLITTER * cdytail);
        abig =   (c - cdytail); bhi = c - abig;
        blo = cdytail - bhi; err1 = adxt_cdyt1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        adxt_cdyt0 = (alo * blo) - err3;
        c =   (SPLITTER * bdz); abig =   (c - bdz);
        bhi = c - abig; blo = bdz - bhi;
        _i =   (adxt_cdyt0 * bdz);
        c =   (SPLITTER * adxt_cdyt0);
        abig =   (c - adxt_cdyt0); ahi = c - abig;
        alo = adxt_cdyt0 - ahi; err1 = _i - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        u[0] = (alo * blo) - err3; _j =   (adxt_cdyt1 * bdz);
        c =   (SPLITTER * adxt_cdyt1);
        abig =   (c - adxt_cdyt1); ahi = c - abig;
        alo = adxt_cdyt1 - ahi; err1 = _j - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        _0 = (alo * blo) - err3; _k =   (_i + _0);
        bvirt =   (_k - _i); avirt = _k - bvirt;
        bround = _0 - bvirt; around = _i - avirt;
        u[1] = around + bround; u3 =   (_j + _k);
        bvirt = u3 - _j; u[2] = _k - bvirt;
        u[3] = u3;
        finlength = sum(finlength, finnow, 4, u,
                        finother);
        finswap = finnow; finnow = finother; finother = finswap;
        if (bdztail != 0.0) {
          c =   (SPLITTER * bdztail);
          abig =   (c - bdztail); bhi = c - abig;
          blo = bdztail - bhi; _i =   (adxt_cdyt0 * bdztail);
          c =   (SPLITTER * adxt_cdyt0);
          abig =   (c - adxt_cdyt0); ahi = c - abig;
          alo = adxt_cdyt0 - ahi; err1 = _i - (ahi * bhi);
          err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
          u[0] = (alo * blo) - err3;
          _j =   (adxt_cdyt1 * bdztail);
          c =   (SPLITTER * adxt_cdyt1);
          abig =   (c - adxt_cdyt1); ahi = c - abig;
          alo = adxt_cdyt1 - ahi; err1 = _j - (ahi * bhi);
          err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
          _0 = (alo * blo) - err3; _k =   (_i + _0);
          bvirt =   (_k - _i); avirt = _k - bvirt;
          bround = _0 - bvirt; around = _i - avirt;
          u[1] = around + bround; u3 =   (_j + _k);
          bvirt = u3 - _j; u[2] = _k - bvirt;
          u[3] = u3;
          finlength =
            sum(finlength, finnow, 4, u,
                finother);
          finswap = finnow; finnow = finother; finother = finswap;
        }
      }
    }
    if (bdxtail != 0.0) {
      if (cdytail != 0.0) {
        bdxt_cdyt1 =   (bdxtail * cdytail);
        c =   (SPLITTER * bdxtail);
        abig =   (c - bdxtail); ahi = c - abig;
        alo = bdxtail - ahi; c =   (SPLITTER * cdytail);
        abig =   (c - cdytail); bhi = c - abig;
        blo = cdytail - bhi; err1 = bdxt_cdyt1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        bdxt_cdyt0 = (alo * blo) - err3;
        c =   (SPLITTER * adz); abig =   (c - adz);
        bhi = c - abig; blo = adz - bhi;
        _i =   (bdxt_cdyt0 * adz);
        c =   (SPLITTER * bdxt_cdyt0);
        abig =   (c - bdxt_cdyt0); ahi = c - abig;
        alo = bdxt_cdyt0 - ahi; err1 = _i - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        u[0] = (alo * blo) - err3; _j =   (bdxt_cdyt1 * adz);
        c =   (SPLITTER * bdxt_cdyt1);
        abig =   (c - bdxt_cdyt1); ahi = c - abig;
        alo = bdxt_cdyt1 - ahi; err1 = _j - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        _0 = (alo * blo) - err3; _k =   (_i + _0);
        bvirt =   (_k - _i); avirt = _k - bvirt;
        bround = _0 - bvirt; around = _i - avirt;
        u[1] = around + bround; u3 =   (_j + _k);
        bvirt = u3 - _j; u[2] = _k - bvirt;
        u[3] = u3;
        finlength = sum(finlength, finnow, 4, u,
                        finother);
        finswap = finnow; finnow = finother; finother = finswap;
        if (adztail != 0.0) {
          c =   (SPLITTER * adztail);
          abig =   (c - adztail); bhi = c - abig;
          blo = adztail - bhi; _i =   (bdxt_cdyt0 * adztail);
          c =   (SPLITTER * bdxt_cdyt0);
          abig =   (c - bdxt_cdyt0); ahi = c - abig;
          alo = bdxt_cdyt0 - ahi; err1 = _i - (ahi * bhi);
          err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
          u[0] = (alo * blo) - err3;
          _j =   (bdxt_cdyt1 * adztail);
          c =   (SPLITTER * bdxt_cdyt1);
          abig =   (c - bdxt_cdyt1); ahi = c - abig;
          alo = bdxt_cdyt1 - ahi; err1 = _j - (ahi * bhi);
          err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
          _0 = (alo * blo) - err3; _k =   (_i + _0);
          bvirt =   (_k - _i); avirt = _k - bvirt;
          bround = _0 - bvirt; around = _i - avirt;
          u[1] = around + bround; u3 =   (_j + _k);
          bvirt = u3 - _j; u[2] = _k - bvirt;
          u[3] = u3;
          finlength =
            sum(finlength, finnow, 4, u,
                finother);
          finswap = finnow; finnow = finother; finother = finswap;
        }
      }
      if (adytail != 0.0) {
        negate = -bdxtail;
        bdxt_adyt1 =   (negate * adytail);
        c =   (SPLITTER * negate); abig =   (c - negate);
        ahi = c - abig; alo = negate - ahi;
        c =   (SPLITTER * adytail);
        abig =   (c - adytail); bhi = c - abig;
        blo = adytail - bhi; err1 = bdxt_adyt1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        bdxt_adyt0 = (alo * blo) - err3;
        c =   (SPLITTER * cdz); abig =   (c - cdz);
        bhi = c - abig; blo = cdz - bhi;
        _i =   (bdxt_adyt0 * cdz);
        c =   (SPLITTER * bdxt_adyt0);
        abig =   (c - bdxt_adyt0); ahi = c - abig;
        alo = bdxt_adyt0 - ahi; err1 = _i - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        u[0] = (alo * blo) - err3; _j =   (bdxt_adyt1 * cdz);
        c =   (SPLITTER * bdxt_adyt1);
        abig =   (c - bdxt_adyt1); ahi = c - abig;
        alo = bdxt_adyt1 - ahi; err1 = _j - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        _0 = (alo * blo) - err3; _k =   (_i + _0);
        bvirt =   (_k - _i); avirt = _k - bvirt;
        bround = _0 - bvirt; around = _i - avirt;
        u[1] = around + bround; u3 =   (_j + _k);
        bvirt = u3 - _j; u[2] = _k - bvirt;
        u[3] = u3;
        finlength = sum(finlength, finnow, 4, u,
                        finother);
        finswap = finnow; finnow = finother; finother = finswap;
        if (cdztail != 0.0) {
          c =   (SPLITTER * cdztail);
          abig =   (c - cdztail); bhi = c - abig;
          blo = cdztail - bhi; _i =   (bdxt_adyt0 * cdztail);
          c =   (SPLITTER * bdxt_adyt0);
          abig =   (c - bdxt_adyt0); ahi = c - abig;
          alo = bdxt_adyt0 - ahi; err1 = _i - (ahi * bhi);
          err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
          u[0] = (alo * blo) - err3;
          _j =   (bdxt_adyt1 * cdztail);
          c =   (SPLITTER * bdxt_adyt1);
          abig =   (c - bdxt_adyt1); ahi = c - abig;
          alo = bdxt_adyt1 - ahi; err1 = _j - (ahi * bhi);
          err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
          _0 = (alo * blo) - err3; _k =   (_i + _0);
          bvirt =   (_k - _i); avirt = _k - bvirt;
          bround = _0 - bvirt; around = _i - avirt;
          u[1] = around + bround; u3 =   (_j + _k);
          bvirt = u3 - _j; u[2] = _k - bvirt;
          u[3] = u3;
          finlength =
            sum(finlength, finnow, 4, u,
                finother);
          finswap = finnow; finnow = finother; finother = finswap;
        }
      }
    }
    if (cdxtail != 0.0) {
      if (adytail != 0.0) {
        cdxt_adyt1 =   (cdxtail * adytail);
        c =   (SPLITTER * cdxtail);
        abig =   (c - cdxtail); ahi = c - abig;
        alo = cdxtail - ahi; c =   (SPLITTER * adytail);
        abig =   (c - adytail); bhi = c - abig;
        blo = adytail - bhi; err1 = cdxt_adyt1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        cdxt_adyt0 = (alo * blo) - err3;
        c =   (SPLITTER * bdz); abig =   (c - bdz);
        bhi = c - abig; blo = bdz - bhi;
        _i =   (cdxt_adyt0 * bdz);
        c =   (SPLITTER * cdxt_adyt0);
        abig =   (c - cdxt_adyt0); ahi = c - abig;
        alo = cdxt_adyt0 - ahi; err1 = _i - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        u[0] = (alo * blo) - err3; _j =   (cdxt_adyt1 * bdz);
        c =   (SPLITTER * cdxt_adyt1);
        abig =   (c - cdxt_adyt1); ahi = c - abig;
        alo = cdxt_adyt1 - ahi; err1 = _j - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        _0 = (alo * blo) - err3; _k =   (_i + _0);
        bvirt =   (_k - _i); avirt = _k - bvirt;
        bround = _0 - bvirt; around = _i - avirt;
        u[1] = around + bround; u3 =   (_j + _k);
        bvirt = u3 - _j; u[2] = _k - bvirt;
        u[3] = u3;
        finlength = sum(finlength, finnow, 4, u,
                        finother);
        finswap = finnow; finnow = finother; finother = finswap;
        if (bdztail != 0.0) {
          c =   (SPLITTER * bdztail);
          abig =   (c - bdztail); bhi = c - abig;
          blo = bdztail - bhi; _i =   (cdxt_adyt0 * bdztail);
          c =   (SPLITTER * cdxt_adyt0);
          abig =   (c - cdxt_adyt0); ahi = c - abig;
          alo = cdxt_adyt0 - ahi; err1 = _i - (ahi * bhi);
          err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
          u[0] = (alo * blo) - err3;
          _j =   (cdxt_adyt1 * bdztail);
          c =   (SPLITTER * cdxt_adyt1);
          abig =   (c - cdxt_adyt1); ahi = c - abig;
          alo = cdxt_adyt1 - ahi; err1 = _j - (ahi * bhi);
          err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
          _0 = (alo * blo) - err3; _k =   (_i + _0);
          bvirt =   (_k - _i); avirt = _k - bvirt;
          bround = _0 - bvirt; around = _i - avirt;
          u[1] = around + bround; u3 =   (_j + _k);
          bvirt = u3 - _j; u[2] = _k - bvirt;
          u[3] = u3;
          finlength =
            sum(finlength, finnow, 4, u,
                finother);
          finswap = finnow; finnow = finother; finother = finswap;
        }
      }
      if (bdytail != 0.0) {
        negate = -cdxtail;
        cdxt_bdyt1 =   (negate * bdytail);
        c =   (SPLITTER * negate); abig =   (c - negate);
        ahi = c - abig; alo = negate - ahi;
        c =   (SPLITTER * bdytail);
        abig =   (c - bdytail); bhi = c - abig;
        blo = bdytail - bhi; err1 = cdxt_bdyt1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        cdxt_bdyt0 = (alo * blo) - err3;
        c =   (SPLITTER * adz); abig =   (c - adz);
        bhi = c - abig; blo = adz - bhi;
        _i =   (cdxt_bdyt0 * adz);
        c =   (SPLITTER * cdxt_bdyt0);
        abig =   (c - cdxt_bdyt0); ahi = c - abig;
        alo = cdxt_bdyt0 - ahi; err1 = _i - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        u[0] = (alo * blo) - err3; _j =   (cdxt_bdyt1 * adz);
        c =   (SPLITTER * cdxt_bdyt1);
        abig =   (c - cdxt_bdyt1); ahi = c - abig;
        alo = cdxt_bdyt1 - ahi; err1 = _j - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        _0 = (alo * blo) - err3; _k =   (_i + _0);
        bvirt =   (_k - _i); avirt = _k - bvirt;
        bround = _0 - bvirt; around = _i - avirt;
        u[1] = around + bround; u3 =   (_j + _k);
        bvirt = u3 - _j; u[2] = _k - bvirt;
        u[3] = u3;
        finlength = sum(finlength, finnow, 4, u,
                        finother);
        finswap = finnow; finnow = finother; finother = finswap;
        if (adztail != 0.0) {
          c =   (SPLITTER * adztail);
          abig =   (c - adztail); bhi = c - abig;
          blo = adztail - bhi; _i =   (cdxt_bdyt0 * adztail);
          c =   (SPLITTER * cdxt_bdyt0);
          abig =   (c - cdxt_bdyt0); ahi = c - abig;
          alo = cdxt_bdyt0 - ahi; err1 = _i - (ahi * bhi);
          err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
          u[0] = (alo * blo) - err3;
          _j =   (cdxt_bdyt1 * adztail);
          c =   (SPLITTER * cdxt_bdyt1);
          abig =   (c - cdxt_bdyt1); ahi = c - abig;
          alo = cdxt_bdyt1 - ahi; err1 = _j - (ahi * bhi);
          err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
          _0 = (alo * blo) - err3; _k =   (_i + _0);
          bvirt =   (_k - _i); avirt = _k - bvirt;
          bround = _0 - bvirt; around = _i - avirt;
          u[1] = around + bround; u3 =   (_j + _k);
          bvirt = u3 - _j; u[2] = _k - bvirt;
          u[3] = u3;
          finlength =
            sum(finlength, finnow, 4, u,
                finother);
          finswap = finnow; finnow = finother; finother = finswap;
        }
      }
    }

    if (adztail != 0.0) {
      wlength = scale(bctlen, bct, adztail, w);
      finlength =
        sum(finlength, finnow, wlength, w,
            finother);
      finswap = finnow; finnow = finother; finother = finswap;
    }
    if (bdztail != 0.0) {
      wlength = scale(catlen, cat, bdztail, w);
      finlength =
        sum(finlength, finnow, wlength, w,
            finother);
      finswap = finnow; finnow = finother; finother = finswap;
    }
    if (cdztail != 0.0) {
      wlength = scale(abtlen, abt, cdztail, w);
      finlength =
        sum(finlength, finnow, wlength, w,
            finother);
      //finswap = finnow;
      finnow = finother;
      //finother = finswap;
    }

    return finnow[finlength - 1];
  }

  //--------------------------------------------------------------------
  private static final double o3derrboundA =
    (7.0 + 56.0 * EPSILON) * EPSILON;

  public static final double orient3d (final double[] pa,
                                        final double[] pb,
                                        final double[] pc,
                                        final double[] pd) {
    double adx, bdx, cdx, ady, bdy, cdy, adz, bdz, cdz;
    double bdxcdy, cdxbdy, cdxady, adxcdy, adxbdy, bdxady;
    double det;
    double permanent, errbound;

    adx = pa[0] - pd[0];
    bdx = pb[0] - pd[0];
    cdx = pc[0] - pd[0];
    ady = pa[1] - pd[1];
    bdy = pb[1] - pd[1];
    cdy = pc[1] - pd[1];
    adz = pa[2] - pd[2];
    bdz = pb[2] - pd[2];
    cdz = pc[2] - pd[2];

    bdxcdy = bdx * cdy;
    cdxbdy = cdx * bdy;

    cdxady = cdx * ady;
    adxcdy = adx * cdy;

    adxbdy = adx * bdy;
    bdxady = bdx * ady;

    det = adz * (bdxcdy - cdxbdy)
      + bdz * (cdxady - adxcdy)
      + cdz * (adxbdy - bdxady);

    permanent =
      (((bdxcdy) >= 0.0 ? (bdxcdy) : -(bdxcdy)) + ((cdxbdy) >= 0.0
                                                   ? (cdxbdy)
                                                   : -(cdxbdy))) * (
        (adz) >= 0.0 ? (adz) : -(adz))
        + (((cdxady) >= 0.0 ? (cdxady) : -(cdxady)) + ((adxcdy) >= 0.0
                                                       ? (adxcdy)
                                                       : -(adxcdy))) * (
        (bdz) >= 0.0 ? (bdz) : -(bdz))
        + (((adxbdy) >= 0.0 ? (adxbdy) : -(adxbdy)) + ((bdxady) >= 0.0
                                                       ? (bdxady)
                                                       : -(bdxady))) * (
        (cdz) >= 0.0 ? (cdz) : -(cdz));
    errbound = o3derrboundA * permanent;
    if ((det > errbound) || (-det > errbound)) {
      return det;
    }

    return orient3dadapt(pa, pb, pc, pd, permanent);
  }

  //--------------------------------------------------------------------
  // incircle
  //--------------------------------------------------------------------
  public static final double incirclefast (final double[] pa,
                                           final double[] pb,
                                           final double[] pc,
                                           final double[] pd) {
    double adx, ady, bdx, bdy, cdx, cdy;
    double abdet, bcdet, cadet;
    double alift, blift, clift;

    adx = pa[0] - pd[0];
    ady = pa[1] - pd[1];
    bdx = pb[0] - pd[0];
    bdy = pb[1] - pd[1];
    cdx = pc[0] - pd[0];
    cdy = pc[1] - pd[1];

    abdet = adx * bdy - bdx * ady;
    bcdet = bdx * cdy - cdx * bdy;
    cadet = cdx * ady - adx * cdy;
    alift = adx * adx + ady * ady;
    blift = bdx * bdx + bdy * bdy;
    clift = cdx * cdx + cdy * cdy;

    return alift * bcdet + blift * cadet + clift * abdet;
  }

  //--------------------------------------------------------------------
  public static final double incircleexact (final double[] pa,
                                            final double[] pb,
                                            final double[] pc,
                                            final double[] pd) {
    double axby1, bxcy1, cxdy1, dxay1, axcy1, bxdy1;
    double bxay1, cxby1, dxcy1, axdy1, cxay1, dxby1;
    double axby0, bxcy0, cxdy0, dxay0, axcy0, bxdy0;
    double bxay0, cxby0, dxcy0, axdy0, cxay0, dxby0;
    double[] ab = new double[4], bc = new double[4], cd = new double[4],
      da = new double[4], ac = new double[4], bd = new double[4];
    double[] temp8 = new double[8];
    int templen;
    double[] abc = new double[12], bcd = new double[12],
      cda = new double[12], dab = new double[12];
    int abclen, bcdlen, cdalen, dablen;
    double[] det24x = new double[24],
      det24y = new double[24], det48x = new double[48],
      det48y = new double[48];
    int xlen, ylen;
    double[] adet = new double[96], bdet = new double[96],
      cdet = new double[96], ddet = new double[96];
    int alen, blen, clen, dlen;
    double[] abdet = new double[192], cddet = new double[192];
    int ablen, cdlen;
    double[] deter = new double[384];
    int deterlen;
    int i;

    double bvirt;
    double avirt, bround, around;
    double c;
    double abig;
    double ahi, alo, bhi, blo;
    double err1, err2, err3;
    double _i, _j;
    double _0;

    axby1 =   (pa[0] * pb[1]); c =   (SPLITTER * pa[0]);
    abig =   (c - pa[0]); ahi = c - abig; alo = pa[0] - ahi;
    c =   (SPLITTER * pb[1]); abig =   (c - pb[1]);
    bhi = c - abig; blo = pb[1] - bhi; err1 = axby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axby0 = (alo * blo) - err3;
    bxay1 =   (pb[0] * pa[1]); c =   (SPLITTER * pb[0]);
    abig =   (c - pb[0]); ahi = c - abig; alo = pb[0] - ahi;
    c =   (SPLITTER * pa[1]); abig =   (c - pa[1]);
    bhi = c - abig; blo = pa[1] - bhi; err1 = bxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxay0 = (alo * blo) - err3;
    _i =   (axby0 - bxay0); bvirt =   (axby0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxay0; around = axby0 - avirt;
    ab[0] = around + bround; _j =   (axby1 + _i);
    bvirt =   (_j - axby1); avirt = _j - bvirt;
    bround = _i - bvirt; around = axby1 - avirt; _0 = around + bround;
    _i =   (_0 - bxay1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxay1; around = _0 - avirt;
    ab[1] = around + bround; ab[3] =   (_j + _i);
    bvirt =   (ab[3] - _j); avirt = ab[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; ab[2] = around + bround;

    bxcy1 =   (pb[0] * pc[1]); c =   (SPLITTER * pb[0]);
    abig =   (c - pb[0]); ahi = c - abig; alo = pb[0] - ahi;
    c =   (SPLITTER * pc[1]); abig =   (c - pc[1]);
    bhi = c - abig; blo = pc[1] - bhi; err1 = bxcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxcy0 = (alo * blo) - err3;
    cxby1 =   (pc[0] * pb[1]); c =   (SPLITTER * pc[0]);
    abig =   (c - pc[0]); ahi = c - abig; alo = pc[0] - ahi;
    c =   (SPLITTER * pb[1]); abig =   (c - pb[1]);
    bhi = c - abig; blo = pb[1] - bhi; err1 = cxby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxby0 = (alo * blo) - err3;
    _i =   (bxcy0 - cxby0); bvirt =   (bxcy0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxby0; around = bxcy0 - avirt;
    bc[0] = around + bround; _j =   (bxcy1 + _i);
    bvirt =   (_j - bxcy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = bxcy1 - avirt; _0 = around + bround;
    _i =   (_0 - cxby1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxby1; around = _0 - avirt;
    bc[1] = around + bround; bc[3] =   (_j + _i);
    bvirt =   (bc[3] - _j); avirt = bc[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; bc[2] = around + bround;

    cxdy1 =   (pc[0] * pd[1]); c =   (SPLITTER * pc[0]);
    abig =   (c - pc[0]); ahi = c - abig; alo = pc[0] - ahi;
    c =   (SPLITTER * pd[1]); abig =   (c - pd[1]);
    bhi = c - abig; blo = pd[1] - bhi; err1 = cxdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxdy0 = (alo * blo) - err3;
    dxcy1 =   (pd[0] * pc[1]); c =   (SPLITTER * pd[0]);
    abig =   (c - pd[0]); ahi = c - abig; alo = pd[0] - ahi;
    c =   (SPLITTER * pc[1]); abig =   (c - pc[1]);
    bhi = c - abig; blo = pc[1] - bhi; err1 = dxcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxcy0 = (alo * blo) - err3;
    _i =   (cxdy0 - dxcy0); bvirt =   (cxdy0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxcy0; around = cxdy0 - avirt;
    cd[0] = around + bround; _j =   (cxdy1 + _i);
    bvirt =   (_j - cxdy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = cxdy1 - avirt; _0 = around + bround;
    _i =   (_0 - dxcy1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxcy1; around = _0 - avirt;
    cd[1] = around + bround; cd[3] =   (_j + _i);
    bvirt =   (cd[3] - _j); avirt = cd[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; cd[2] = around + bround;

    dxay1 =   (pd[0] * pa[1]); c =   (SPLITTER * pd[0]);
    abig =   (c - pd[0]); ahi = c - abig; alo = pd[0] - ahi;
    c =   (SPLITTER * pa[1]); abig =   (c - pa[1]);
    bhi = c - abig; blo = pa[1] - bhi; err1 = dxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxay0 = (alo * blo) - err3;
    axdy1 =   (pa[0] * pd[1]); c =   (SPLITTER * pa[0]);
    abig =   (c - pa[0]); ahi = c - abig; alo = pa[0] - ahi;
    c =   (SPLITTER * pd[1]); abig =   (c - pd[1]);
    bhi = c - abig; blo = pd[1] - bhi; err1 = axdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axdy0 = (alo * blo) - err3;
    _i =   (dxay0 - axdy0); bvirt =   (dxay0 - _i);
    avirt = _i + bvirt; bround = bvirt - axdy0; around = dxay0 - avirt;
    da[0] = around + bround; _j =   (dxay1 + _i);
    bvirt =   (_j - dxay1); avirt = _j - bvirt;
    bround = _i - bvirt; around = dxay1 - avirt; _0 = around + bround;
    _i =   (_0 - axdy1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - axdy1; around = _0 - avirt;
    da[1] = around + bround; da[3] =   (_j + _i);
    bvirt =   (da[3] - _j); avirt = da[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; da[2] = around + bround;

    axcy1 =   (pa[0] * pc[1]); c =   (SPLITTER * pa[0]);
    abig =   (c - pa[0]); ahi = c - abig; alo = pa[0] - ahi;
    c =   (SPLITTER * pc[1]); abig =   (c - pc[1]);
    bhi = c - abig; blo = pc[1] - bhi; err1 = axcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axcy0 = (alo * blo) - err3;
    cxay1 =   (pc[0] * pa[1]); c =   (SPLITTER * pc[0]);
    abig =   (c - pc[0]); ahi = c - abig; alo = pc[0] - ahi;
    c =   (SPLITTER * pa[1]); abig =   (c - pa[1]);
    bhi = c - abig; blo = pa[1] - bhi; err1 = cxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxay0 = (alo * blo) - err3;
    _i =   (axcy0 - cxay0); bvirt =   (axcy0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxay0; around = axcy0 - avirt;
    ac[0] = around + bround; _j =   (axcy1 + _i);
    bvirt =   (_j - axcy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = axcy1 - avirt; _0 = around + bround;
    _i =   (_0 - cxay1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxay1; around = _0 - avirt;
    ac[1] = around + bround; ac[3] =   (_j + _i);
    bvirt =   (ac[3] - _j); avirt = ac[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; ac[2] = around + bround;

    bxdy1 =   (pb[0] * pd[1]); c =   (SPLITTER * pb[0]);
    abig =   (c - pb[0]); ahi = c - abig; alo = pb[0] - ahi;
    c =   (SPLITTER * pd[1]); abig =   (c - pd[1]);
    bhi = c - abig; blo = pd[1] - bhi; err1 = bxdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxdy0 = (alo * blo) - err3;
    dxby1 =   (pd[0] * pb[1]); c =   (SPLITTER * pd[0]);
    abig =   (c - pd[0]); ahi = c - abig; alo = pd[0] - ahi;
    c =   (SPLITTER * pb[1]); abig =   (c - pb[1]);
    bhi = c - abig; blo = pb[1] - bhi; err1 = dxby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxby0 = (alo * blo) - err3;
    _i =   (bxdy0 - dxby0); bvirt =   (bxdy0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxby0; around = bxdy0 - avirt;
    bd[0] = around + bround; _j =   (bxdy1 + _i);
    bvirt =   (_j - bxdy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = bxdy1 - avirt; _0 = around + bround;
    _i =   (_0 - dxby1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxby1; around = _0 - avirt;
    bd[1] = around + bround; bd[3] =   (_j + _i);
    bvirt =   (bd[3] - _j); avirt = bd[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; bd[2] = around + bround;

    templen = sum(4, cd, 4, da, temp8);
    cdalen = sum(templen, temp8, 4, ac, cda);
    templen = sum(4, da, 4, ab, temp8);
    dablen = sum(templen, temp8, 4, bd, dab);
    for (i = 0; i < 4; i++) {
      bd[i] = -bd[i];
      ac[i] = -ac[i];
    }
    templen = sum(4, ab, 4, bc, temp8);
    abclen = sum(templen, temp8, 4, ac, abc);
    templen = sum(4, bc, 4, cd, temp8);
    bcdlen = sum(templen, temp8, 4, bd, bcd);

    xlen = scale(bcdlen, bcd, pa[0], det24x);
    xlen = scale(xlen, det24x, pa[0], det48x);
    ylen = scale(bcdlen, bcd, pa[1], det24y);
    ylen = scale(ylen, det24y, pa[1], det48y);
    alen =
      sum(xlen, det48x, ylen, det48y, adet);

    xlen = scale(cdalen, cda, pb[0], det24x);
    xlen = scale(xlen, det24x, -pb[0], det48x);
    ylen = scale(cdalen, cda, pb[1], det24y);
    ylen = scale(ylen, det24y, -pb[1], det48y);
    blen =
      sum(xlen, det48x, ylen, det48y, bdet);

    xlen = scale(dablen, dab, pc[0], det24x);
    xlen = scale(xlen, det24x, pc[0], det48x);
    ylen = scale(dablen, dab, pc[1], det24y);
    ylen = scale(ylen, det24y, pc[1], det48y);
    clen =
      sum(xlen, det48x, ylen, det48y, cdet);

    xlen = scale(abclen, abc, pd[0], det24x);
    xlen = scale(xlen, det24x, -pd[0], det48x);
    ylen = scale(abclen, abc, pd[1], det24y);
    ylen = scale(ylen, det24y, -pd[1], det48y);
    dlen =
      sum(xlen, det48x, ylen, det48y, ddet);

    ablen = sum(alen, adet, blen, bdet, abdet);
    cdlen = sum(clen, cdet, dlen, ddet, cddet);
    deterlen =
      sum(ablen, abdet, cdlen, cddet, deter);

    return deter[deterlen - 1];
  }

  //--------------------------------------------------------------------
  public static final double incircleslow (final double[] pa,
                                           final double[] pb,
                                           final double[] pc,
                                           final double[] pd) {
    double adx, bdx, cdx, ady, bdy, cdy;
    double adxtail, bdxtail, cdxtail;
    double adytail, bdytail, cdytail;
    double negate, negatetail;
    double axby7, bxcy7, axcy7, bxay7, cxby7, cxay7;
    double[] axby = new double[8], bxcy = new double[8],
      axcy = new double[8], bxay = new double[8], cxby = new double[8],
      cxay = new double[8];
    double[] temp16 = new double[16];
    int temp16len;
    double[] detx = new double[32], detxx = new double[64],
      detxt = new double[32], detxxt = new double[64], detxtxt =
      new double[64];
    int xlen, xxlen, xtlen, xxtlen, xtxtlen;
    double[] x1 = new double[128], x2 = new double[192];
    int x1len, x2len;
    double[] dety = new double[32], detyy = new double[64],
      detyt = new double[32], detyyt = new double[64],
      detytyt = new double[64];
    int ylen, yylen, ytlen, yytlen, ytytlen;
    double[] y1 = new double[128], y2 = new double[192];
    int y1len, y2len;
    double[] adet = new double[384], bdet = new double[384],
      cdet = new double[384], abdet = new double[768],
      deter = new double[1152];
    int alen, blen, clen, ablen, deterlen;
    int i;

    double bvirt;
    double avirt, bround, around;
    double c;
    double abig;
    double a0hi, a0lo, a1hi, a1lo, bhi, blo;
    double err1, err2, err3;
    double _i, _j, _k, _l, _m, _n;
    double _0, _1, _2;

    adx =   (pa[0] - pd[0]); bvirt =   (pa[0] - adx);
    avirt = adx + bvirt; bround = bvirt - pd[0]; around = pa[0] - avirt;
    adxtail = around + bround;
    ady =   (pa[1] - pd[1]); bvirt =   (pa[1] - ady);
    avirt = ady + bvirt; bround = bvirt - pd[1]; around = pa[1] - avirt;
    adytail = around + bround;
    bdx =   (pb[0] - pd[0]); bvirt =   (pb[0] - bdx);
    avirt = bdx + bvirt; bround = bvirt - pd[0]; around = pb[0] - avirt;
    bdxtail = around + bround;
    bdy =   (pb[1] - pd[1]); bvirt =   (pb[1] - bdy);
    avirt = bdy + bvirt; bround = bvirt - pd[1]; around = pb[1] - avirt;
    bdytail = around + bround;
    cdx =   (pc[0] - pd[0]); bvirt =   (pc[0] - cdx);
    avirt = cdx + bvirt; bround = bvirt - pd[0]; around = pc[0] - avirt;
    cdxtail = around + bround;
    cdy =   (pc[1] - pd[1]); bvirt =   (pc[1] - cdy);
    avirt = cdy + bvirt; bround = bvirt - pd[1]; around = pc[1] - avirt;
    cdytail = around + bround;

    c =   (SPLITTER * adxtail); abig =   (c - adxtail);
    a0hi = c - abig; a0lo = adxtail - a0hi;
    c =   (SPLITTER * bdytail); abig =   (c - bdytail);
    bhi = c - abig; blo = bdytail - bhi;
    _i =   (adxtail * bdytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    axby[0] = (a0lo * blo) - err3; c =   (SPLITTER * adx);
    abig =   (c - adx); a1hi = c - abig; a1lo = adx - a1hi;
    _j =   (adx * bdytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * bdy);
    abig =   (c - bdy); bhi = c - abig; blo = bdy - bhi;
    _i =   (adxtail * bdy); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axby[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (adx * bdy); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axby[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axby[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; axby[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    axby[5] = around + bround; axby7 =   (_m + _k);
    bvirt =   (axby7 - _m); avirt = axby7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; axby[6] = around + bround

    ;
    axby[7] = axby7;
    negate = -ady;
    negatetail = -adytail;
    c =   (SPLITTER * bdxtail); abig =   (c - bdxtail);
    a0hi = c - abig; a0lo = bdxtail - a0hi;
    c =   (SPLITTER * negatetail);
    abig =   (c - negatetail); bhi = c - abig;
    blo = negatetail - bhi; _i =   (bdxtail * negatetail);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); bxay[0] = (a0lo * blo) - err3;
    c =   (SPLITTER * bdx); abig =   (c - bdx);
    a1hi = c - abig; a1lo = bdx - a1hi;
    _j =   (bdx * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * negate);
    abig =   (c - negate); bhi = c - abig; blo = negate - bhi;
    _i =   (bdxtail * negate); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxay[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (bdx * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxay[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxay[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; bxay[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    bxay[5] = around + bround; bxay7 =   (_m + _k);
    bvirt =   (bxay7 - _m); avirt = bxay7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; bxay[6] = around + bround

    ;
    bxay[7] = bxay7;
    c =   (SPLITTER * bdxtail); abig =   (c - bdxtail);
    a0hi = c - abig; a0lo = bdxtail - a0hi;
    c =   (SPLITTER * cdytail); abig =   (c - cdytail);
    bhi = c - abig; blo = cdytail - bhi;
    _i =   (bdxtail * cdytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    bxcy[0] = (a0lo * blo) - err3; c =   (SPLITTER * bdx);
    abig =   (c - bdx); a1hi = c - abig; a1lo = bdx - a1hi;
    _j =   (bdx * cdytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * cdy);
    abig =   (c - cdy); bhi = c - abig; blo = cdy - bhi;
    _i =   (bdxtail * cdy); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxcy[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (bdx * cdy); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxcy[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxcy[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; bxcy[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    bxcy[5] = around + bround; bxcy7 =   (_m + _k);
    bvirt =   (bxcy7 - _m); avirt = bxcy7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; bxcy[6] = around + bround

    ;
    bxcy[7] = bxcy7;
    negate = -bdy;
    negatetail = -bdytail;
    c =   (SPLITTER * cdxtail); abig =   (c - cdxtail);
    a0hi = c - abig; a0lo = cdxtail - a0hi;
    c =   (SPLITTER * negatetail);
    abig =   (c - negatetail); bhi = c - abig;
    blo = negatetail - bhi; _i =   (cdxtail * negatetail);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); cxby[0] = (a0lo * blo) - err3;
    c =   (SPLITTER * cdx); abig =   (c - cdx);
    a1hi = c - abig; a1lo = cdx - a1hi;
    _j =   (cdx * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * negate);
    abig =   (c - negate); bhi = c - abig; blo = negate - bhi;
    _i =   (cdxtail * negate); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxby[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (cdx * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxby[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxby[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; cxby[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    cxby[5] = around + bround; cxby7 =   (_m + _k);
    bvirt =   (cxby7 - _m); avirt = cxby7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; cxby[6] = around + bround

    ;
    cxby[7] = cxby7;
    c =   (SPLITTER * cdxtail); abig =   (c - cdxtail);
    a0hi = c - abig; a0lo = cdxtail - a0hi;
    c =   (SPLITTER * adytail); abig =   (c - adytail);
    bhi = c - abig; blo = adytail - bhi;
    _i =   (cdxtail * adytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    cxay[0] = (a0lo * blo) - err3; c =   (SPLITTER * cdx);
    abig =   (c - cdx); a1hi = c - abig; a1lo = cdx - a1hi;
    _j =   (cdx * adytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * ady);
    abig =   (c - ady); bhi = c - abig; blo = ady - bhi;
    _i =   (cdxtail * ady); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxay[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (cdx * ady); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxay[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxay[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; cxay[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    cxay[5] = around + bround; cxay7 =   (_m + _k);
    bvirt =   (cxay7 - _m); avirt = cxay7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; cxay[6] = around + bround

    ;
    cxay[7] = cxay7;
    negate = -cdy;
    negatetail = -cdytail;
    c =   (SPLITTER * adxtail); abig =   (c - adxtail);
    a0hi = c - abig; a0lo = adxtail - a0hi;
    c =   (SPLITTER * negatetail);
    abig =   (c - negatetail); bhi = c - abig;
    blo = negatetail - bhi; _i =   (adxtail * negatetail);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); axcy[0] = (a0lo * blo) - err3;
    c =   (SPLITTER * adx); abig =   (c - adx);
    a1hi = c - abig; a1lo = adx - a1hi;
    _j =   (adx * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * negate);
    abig =   (c - negate); bhi = c - abig; blo = negate - bhi;
    _i =   (adxtail * negate); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axcy[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (adx * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axcy[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axcy[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; axcy[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    axcy[5] = around + bround; axcy7 =   (_m + _k);
    bvirt =   (axcy7 - _m); avirt = axcy7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; axcy[6] = around + bround

    ;
    axcy[7] = axcy7;

    temp16len = sum(8, bxcy, 8, cxby, temp16);

    xlen = scale(temp16len, temp16, adx, detx);
    xxlen = scale(xlen, detx, adx, detxx);
    xtlen = scale(temp16len, temp16, adxtail, detxt);
    xxtlen = scale(xtlen, detxt, adx, detxxt);
    for (i = 0; i < xxtlen; i++) {
      detxxt[i] *= 2.0;
    }
    xtxtlen = scale(xtlen, detxt, adxtail, detxtxt);
    x1len =
      sum(xxlen, detxx, xxtlen, detxxt, x1);
    x2len =
      sum(x1len, x1, xtxtlen, detxtxt, x2);

    ylen = scale(temp16len, temp16, ady, dety);
    yylen = scale(ylen, dety, ady, detyy);
    ytlen = scale(temp16len, temp16, adytail, detyt);
    yytlen = scale(ytlen, detyt, ady, detyyt);
    for (i = 0; i < yytlen; i++) {
      detyyt[i] *= 2.0;
    }
    ytytlen = scale(ytlen, detyt, adytail, detytyt);
    y1len =
      sum(yylen, detyy, yytlen, detyyt, y1);
    y2len =
      sum(y1len, y1, ytytlen, detytyt, y2);

    alen = sum(x2len, x2, y2len, y2, adet);

    temp16len = sum(8, cxay, 8, axcy, temp16);

    xlen = scale(temp16len, temp16, bdx, detx);
    xxlen = scale(xlen, detx, bdx, detxx);
    xtlen = scale(temp16len, temp16, bdxtail, detxt);
    xxtlen = scale(xtlen, detxt, bdx, detxxt);
    for (i = 0; i < xxtlen; i++) {
      detxxt[i] *= 2.0;
    }
    xtxtlen = scale(xtlen, detxt, bdxtail, detxtxt);
    x1len =
      sum(xxlen, detxx, xxtlen, detxxt, x1);
    x2len =
      sum(x1len, x1, xtxtlen, detxtxt, x2);

    ylen = scale(temp16len, temp16, bdy, dety);
    yylen = scale(ylen, dety, bdy, detyy);
    ytlen = scale(temp16len, temp16, bdytail, detyt);
    yytlen = scale(ytlen, detyt, bdy, detyyt);
    for (i = 0; i < yytlen; i++) {
      detyyt[i] *= 2.0;
    }
    ytytlen = scale(ytlen, detyt, bdytail, detytyt);
    y1len =
      sum(yylen, detyy, yytlen, detyyt, y1);
    y2len =
      sum(y1len, y1, ytytlen, detytyt, y2);

    blen = sum(x2len, x2, y2len, y2, bdet);

    temp16len = sum(8, axby, 8, bxay, temp16);

    xlen = scale(temp16len, temp16, cdx, detx);
    xxlen = scale(xlen, detx, cdx, detxx);
    xtlen = scale(temp16len, temp16, cdxtail, detxt);
    xxtlen = scale(xtlen, detxt, cdx, detxxt);
    for (i = 0; i < xxtlen; i++) {
      detxxt[i] *= 2.0;
    }
    xtxtlen = scale(xtlen, detxt, cdxtail, detxtxt);
    x1len =
      sum(xxlen, detxx, xxtlen, detxxt, x1);
    x2len =
      sum(x1len, x1, xtxtlen, detxtxt, x2);

    ylen = scale(temp16len, temp16, cdy, dety);
    yylen = scale(ylen, dety, cdy, detyy);
    ytlen = scale(temp16len, temp16, cdytail, detyt);
    yytlen = scale(ytlen, detyt, cdy, detyyt);
    for (i = 0; i < yytlen; i++) {
      detyyt[i] *= 2.0;
    }
    ytytlen = scale(ytlen, detyt, cdytail, detytyt);
    y1len =
      sum(yylen, detyy, yytlen, detyyt, y1);
    y2len =
      sum(y1len, y1, ytytlen, detytyt, y2);

    clen = sum(x2len, x2, y2len, y2, cdet);

    ablen = sum(alen, adet, blen, bdet, abdet);
    deterlen =
      sum(ablen, abdet, clen, cdet, deter);

    return deter[deterlen - 1];
  }

  //--------------------------------------------------------------------
  static final double iccerrboundC =
    (44.0 + 576.0 * EPSILON) * EPSILON * EPSILON;

  private static final double iccerrboundB =
    (4.0 + 48.0 * EPSILON) * EPSILON;

  public static final double incircleadapt (final double[] pa,
                                            final double[] pb,
                                            final double[] pc,
                                            final double[] pd,
                                            final double permanent) {
    double adx, bdx, cdx, ady, bdy, cdy;
    double det, errbound;

    double bdxcdy1, cdxbdy1, cdxady1, adxcdy1, adxbdy1, bdxady1;
    double bdxcdy0, cdxbdy0, cdxady0, adxcdy0, adxbdy0, bdxady0;
    double[] bc = new double[4], ca = new double[4], ab = new double[4];
    double bc3, ca3, ab3;
    double[] axbc = new double[8], axxbc = new double[16],
      aybc = new double[8], ayybc = new double[16], adet =
      new double[32];
    int axbclen, axxbclen, aybclen, ayybclen, alen;
    double[] bxca = new double[8], bxxca = new double[16],
      byca = new double[8], byyca = new double[16], bdet =
      new double[32];
    int bxcalen, bxxcalen, bycalen, byycalen, blen;
    double[] cxab = new double[8], cxxab = new double[16],
      cyab = new double[8], cyyab = new double[16], cdet =
      new double[32];
    int cxablen, cxxablen, cyablen, cyyablen, clen;
    double[] abdet = new double[64];
    int ablen;
    double[] fin1 = new double[1152], fin2 = new double[1152];
    double[] finnow, finother, finswap;
    int finlength;

    double adxtail, bdxtail, cdxtail, adytail, bdytail, cdytail;
    double adxadx1, adyady1, bdxbdx1, bdybdy1, cdxcdx1, cdycdy1;
    double adxadx0, adyady0, bdxbdx0, bdybdy0, cdxcdx0, cdycdy0;
    double[] aa = new double[4], bb = new double[4], cc = new double[4];
    double aa3, bb3, cc3;
    double ti1, tj1;
    double ti0, tj0;
    double[] u = new double[4], v = new double[4];
    double u3, v3;
    double[] temp8 = new double[8], temp16a = new double[16],
      temp16b = new double[16], temp16c = new double[16];
    double[] temp32a = new double[32], temp32b = new double[32],
      temp48 = new double[48], temp64 = new double[64];
    int temp8len, temp16alen, temp16blen, temp16clen;
    int temp32alen, temp32blen, temp48len, temp64len;
    double[] axtbb = new double[8], axtcc = new double[8],
      aytbb = new double[8], aytcc = new double[8];
    int axtbblen, axtcclen, aytbblen, aytcclen;
    double[] bxtaa = new double[8], bxtcc = new double[8],
      bytaa = new double[8], bytcc = new double[8];
    int bxtaalen, bxtcclen, bytaalen, bytcclen;
    double[] cxtaa = new double[8], cxtbb = new double[8],
      cytaa = new double[8], cytbb = new double[8];
    int cxtaalen, cxtbblen, cytaalen, cytbblen;
    double[] axtbc = new double[8], aytbc = new double[8],
      bxtca = new double[8], bytca = new double[8], cxtab =
      new double[8],
      cytab = new double[8];
    int axtbclen = -1, aytbclen = -1, bxtcalen = -1, bytcalen = -1,
      cxtablen = -1, cytablen = -1;
    double[] axtbct = new double[16],
      aytbct = new double[16], bxtcat = new double[16], bytcat =
      new double[16],
      cxtabt = new double[16], cytabt = new double[16];
    int axtbctlen, aytbctlen, bxtcatlen, bytcatlen, cxtabtlen,
      cytabtlen;
    double[] axtbctt = new double[8], aytbctt = new double[8],
      bxtcatt = new double[8];
    double[] bytcatt = new double[8], cxtabtt = new double[8],
      cytabtt = new double[8];
    int axtbcttlen, aytbcttlen, bxtcattlen, bytcattlen, cxtabttlen,
      cytabttlen;
    double[] abt = new double[8], bct = new double[8], cat =
      new double[8];
    int abtlen, bctlen, catlen;
    double[] abtt = new double[4], bctt = new double[4],
      catt = new double[4];
    int abttlen, bcttlen, cattlen;
    double abtt3, bctt3, catt3;
    double negate;

    double bvirt;
    double avirt, bround, around;
    double c;
    double abig;
    double ahi, alo, bhi, blo;
    double err1, err2, err3;
    double _i, _j;
    double _0;

    adx =   (pa[0] - pd[0]);
    bdx =   (pb[0] - pd[0]);
    cdx =   (pc[0] - pd[0]);
    ady =   (pa[1] - pd[1]);
    bdy =   (pb[1] - pd[1]);
    cdy =   (pc[1] - pd[1]);

    bdxcdy1 =   (bdx * cdy); c =   (SPLITTER * bdx);
    abig =   (c - bdx); ahi = c - abig; alo = bdx - ahi;
    c =   (SPLITTER * cdy); abig =   (c - cdy);
    bhi = c - abig; blo = cdy - bhi; err1 = bdxcdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bdxcdy0 = (alo * blo) - err3;
    cdxbdy1 =   (cdx * bdy); c =   (SPLITTER * cdx);
    abig =   (c - cdx); ahi = c - abig; alo = cdx - ahi;
    c =   (SPLITTER * bdy); abig =   (c - bdy);
    bhi = c - abig; blo = bdy - bhi; err1 = cdxbdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cdxbdy0 = (alo * blo) - err3;
    _i =   (bdxcdy0 - cdxbdy0); bvirt =   (bdxcdy0 - _i);
    avirt = _i + bvirt; bround = bvirt - cdxbdy0;
    around = bdxcdy0 - avirt; bc[0] = around + bround;
    _j =   (bdxcdy1 + _i); bvirt =   (_j - bdxcdy1);
    avirt = _j - bvirt; bround = _i - bvirt; around = bdxcdy1 - avirt;
    _0 = around + bround; _i =   (_0 - cdxbdy1);
    bvirt =   (_0 - _i); avirt = _i + bvirt;
    bround = bvirt - cdxbdy1; around = _0 - avirt;
    bc[1] = around + bround; bc3 =   (_j + _i);
    bvirt =   (bc3 - _j); avirt = bc3 - bvirt;
    bround = _i - bvirt; around = _j - avirt; bc[2] = around + bround;
    bc[3] = bc3;
    axbclen = scale(4, bc, adx, axbc);
    axxbclen = scale(axbclen, axbc, adx, axxbc);
    aybclen = scale(4, bc, ady, aybc);
    ayybclen = scale(aybclen, aybc, ady, ayybc);
    alen = sum(axxbclen, axxbc, ayybclen, ayybc,
               adet);

    cdxady1 =   (cdx * ady); c =   (SPLITTER * cdx);
    abig =   (c - cdx); ahi = c - abig; alo = cdx - ahi;
    c =   (SPLITTER * ady); abig =   (c - ady);
    bhi = c - abig; blo = ady - bhi; err1 = cdxady1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cdxady0 = (alo * blo) - err3;
    adxcdy1 =   (adx * cdy); c =   (SPLITTER * adx);
    abig =   (c - adx); ahi = c - abig; alo = adx - ahi;
    c =   (SPLITTER * cdy); abig =   (c - cdy);
    bhi = c - abig; blo = cdy - bhi; err1 = adxcdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    adxcdy0 = (alo * blo) - err3;
    _i =   (cdxady0 - adxcdy0); bvirt =   (cdxady0 - _i);
    avirt = _i + bvirt; bround = bvirt - adxcdy0;
    around = cdxady0 - avirt; ca[0] = around + bround;
    _j =   (cdxady1 + _i); bvirt =   (_j - cdxady1);
    avirt = _j - bvirt; bround = _i - bvirt; around = cdxady1 - avirt;
    _0 = around + bround; _i =   (_0 - adxcdy1);
    bvirt =   (_0 - _i); avirt = _i + bvirt;
    bround = bvirt - adxcdy1; around = _0 - avirt;
    ca[1] = around + bround; ca3 =   (_j + _i);
    bvirt =   (ca3 - _j); avirt = ca3 - bvirt;
    bround = _i - bvirt; around = _j - avirt; ca[2] = around + bround;
    ca[3] = ca3;
    bxcalen = scale(4, ca, bdx, bxca);
    bxxcalen = scale(bxcalen, bxca, bdx, bxxca);
    bycalen = scale(4, ca, bdy, byca);
    byycalen = scale(bycalen, byca, bdy, byyca);
    blen = sum(bxxcalen, bxxca, byycalen, byyca,
               bdet);

    adxbdy1 =   (adx * bdy); c =   (SPLITTER * adx);
    abig =   (c - adx); ahi = c - abig; alo = adx - ahi;
    c =   (SPLITTER * bdy); abig =   (c - bdy);
    bhi = c - abig; blo = bdy - bhi; err1 = adxbdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    adxbdy0 = (alo * blo) - err3;
    bdxady1 =   (bdx * ady); c =   (SPLITTER * bdx);
    abig =   (c - bdx); ahi = c - abig; alo = bdx - ahi;
    c =   (SPLITTER * ady); abig =   (c - ady);
    bhi = c - abig; blo = ady - bhi; err1 = bdxady1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bdxady0 = (alo * blo) - err3;
    _i =   (adxbdy0 - bdxady0); bvirt =   (adxbdy0 - _i);
    avirt = _i + bvirt; bround = bvirt - bdxady0;
    around = adxbdy0 - avirt; ab[0] = around + bround;
    _j =   (adxbdy1 + _i); bvirt =   (_j - adxbdy1);
    avirt = _j - bvirt; bround = _i - bvirt; around = adxbdy1 - avirt;
    _0 = around + bround; _i =   (_0 - bdxady1);
    bvirt =   (_0 - _i); avirt = _i + bvirt;
    bround = bvirt - bdxady1; around = _0 - avirt;
    ab[1] = around + bround; ab3 =   (_j + _i);
    bvirt =   (ab3 - _j); avirt = ab3 - bvirt;
    bround = _i - bvirt; around = _j - avirt; ab[2] = around + bround;
    ab[3] = ab3;
    cxablen = scale(4, ab, cdx, cxab);
    cxxablen = scale(cxablen, cxab, cdx, cxxab);
    cyablen = scale(4, ab, cdy, cyab);
    cyyablen = scale(cyablen, cyab, cdy, cyyab);
    clen = sum(cxxablen, cxxab, cyyablen, cyyab,
               cdet);

    ablen = sum(alen, adet, blen, bdet, abdet);
    finlength =
      sum(ablen, abdet, clen, cdet, fin1);

    det = estimate(finlength, fin1);
    errbound = iccerrboundB * permanent;
    if ((det >= errbound) || (-det >= errbound)) {
      return det;
    }

    bvirt =   (pa[0] - adx); avirt = adx + bvirt;
    bround = bvirt - pd[0]; around = pa[0] - avirt;
    adxtail = around + bround;
    bvirt =   (pa[1] - ady); avirt = ady + bvirt;
    bround = bvirt - pd[1]; around = pa[1] - avirt;
    adytail = around + bround;
    bvirt =   (pb[0] - bdx); avirt = bdx + bvirt;
    bround = bvirt - pd[0]; around = pb[0] - avirt;
    bdxtail = around + bround;
    bvirt =   (pb[1] - bdy); avirt = bdy + bvirt;
    bround = bvirt - pd[1]; around = pb[1] - avirt;
    bdytail = around + bround;
    bvirt =   (pc[0] - cdx); avirt = cdx + bvirt;
    bround = bvirt - pd[0]; around = pc[0] - avirt;
    cdxtail = around + bround;
    bvirt =   (pc[1] - cdy); avirt = cdy + bvirt;
    bround = bvirt - pd[1]; around = pc[1] - avirt;
    cdytail = around + bround;
    if ((adxtail == 0.0) && (bdxtail == 0.0) && (cdxtail == 0.0)
      && (adytail == 0.0) && (bdytail == 0.0) && (cdytail == 0.0)) {
      return det;
    }

    errbound =
      iccerrboundC * permanent + resulterrbound * ((det) >= 0.0 ? (det)
                                                                :
                                                   -(det));
    det += ((adx * adx + ady * ady) * ((bdx * cdytail + cdy * bdxtail)
      - (bdy * cdxtail + cdx * bdytail))
      + 2.0 * (adx * adxtail + ady * adytail) * (bdx * cdy - bdy * cdx))
      + ((bdx * bdx + bdy * bdy) * ((cdx * adytail + ady * cdxtail)
      - (cdy * adxtail + adx * cdytail))
      + 2.0 * (bdx * bdxtail + bdy * bdytail) * (cdx * ady - cdy * adx))
      + ((cdx * cdx + cdy * cdy) * ((adx * bdytail + bdy * adxtail)
      - (ady * bdxtail + bdx * adytail))
      + 2.0 * (cdx * cdxtail + cdy * cdytail) * (adx * bdy - ady * bdx));
    if ((det >= errbound) || (-det >= errbound)) {
      return det;
    }

    finnow = fin1;
    finother = fin2;

    if ((bdxtail != 0.0) || (bdytail != 0.0)
      || (cdxtail != 0.0) || (cdytail != 0.0)) {
      adxadx1 =   (adx * adx); c =   (SPLITTER * adx);
      abig =   (c - adx); ahi = c - abig; alo = adx - ahi;
      err1 = adxadx1 - (ahi * ahi); err3 = err1 - ((ahi + ahi) * alo);
      adxadx0 = (alo * alo) - err3;
      adyady1 =   (ady * ady); c =   (SPLITTER * ady);
      abig =   (c - ady); ahi = c - abig; alo = ady - ahi;
      err1 = adyady1 - (ahi * ahi); err3 = err1 - ((ahi + ahi) * alo);
      adyady0 = (alo * alo) - err3;
      _i =   (adxadx0 + adyady0);
      bvirt =   (_i - adxadx0); avirt = _i - bvirt;
      bround = adyady0 - bvirt; around = adxadx0 - avirt;
      aa[0] = around + bround; _j =   (adxadx1 + _i);
      bvirt =   (_j - adxadx1); avirt = _j - bvirt;
      bround = _i - bvirt; around = adxadx1 - avirt;
      _0 = around + bround; _i =   (_0 + adyady1);
      bvirt =   (_i - _0); avirt = _i - bvirt;
      bround = adyady1 - bvirt; around = _0 - avirt;
      aa[1] = around + bround; aa3 =   (_j + _i);
      bvirt =   (aa3 - _j); avirt = aa3 - bvirt;
      bround = _i - bvirt; around = _j - avirt; aa[2] = around + bround;
      aa[3] = aa3;
    }
    if ((cdxtail != 0.0) || (cdytail != 0.0)
      || (adxtail != 0.0) || (adytail != 0.0)) {
      bdxbdx1 =   (bdx * bdx); c =   (SPLITTER * bdx);
      abig =   (c - bdx); ahi = c - abig; alo = bdx - ahi;
      err1 = bdxbdx1 - (ahi * ahi); err3 = err1 - ((ahi + ahi) * alo);
      bdxbdx0 = (alo * alo) - err3;
      bdybdy1 =   (bdy * bdy); c =   (SPLITTER * bdy);
      abig =   (c - bdy); ahi = c - abig; alo = bdy - ahi;
      err1 = bdybdy1 - (ahi * ahi); err3 = err1 - ((ahi + ahi) * alo);
      bdybdy0 = (alo * alo) - err3;
      _i =   (bdxbdx0 + bdybdy0);
      bvirt =   (_i - bdxbdx0); avirt = _i - bvirt;
      bround = bdybdy0 - bvirt; around = bdxbdx0 - avirt;
      bb[0] = around + bround; _j =   (bdxbdx1 + _i);
      bvirt =   (_j - bdxbdx1); avirt = _j - bvirt;
      bround = _i - bvirt; around = bdxbdx1 - avirt;
      _0 = around + bround; _i =   (_0 + bdybdy1);
      bvirt =   (_i - _0); avirt = _i - bvirt;
      bround = bdybdy1 - bvirt; around = _0 - avirt;
      bb[1] = around + bround; bb3 =   (_j + _i);
      bvirt =   (bb3 - _j); avirt = bb3 - bvirt;
      bround = _i - bvirt; around = _j - avirt; bb[2] = around + bround;
      bb[3] = bb3;
    }
    if ((adxtail != 0.0) || (adytail != 0.0)
      || (bdxtail != 0.0) || (bdytail != 0.0)) {
      cdxcdx1 =   (cdx * cdx); c =   (SPLITTER * cdx);
      abig =   (c - cdx); ahi = c - abig; alo = cdx - ahi;
      err1 = cdxcdx1 - (ahi * ahi); err3 = err1 - ((ahi + ahi) * alo);
      cdxcdx0 = (alo * alo) - err3;
      cdycdy1 =   (cdy * cdy); c =   (SPLITTER * cdy);
      abig =   (c - cdy); ahi = c - abig; alo = cdy - ahi;
      err1 = cdycdy1 - (ahi * ahi); err3 = err1 - ((ahi + ahi) * alo);
      cdycdy0 = (alo * alo) - err3;
      _i =   (cdxcdx0 + cdycdy0);
      bvirt =   (_i - cdxcdx0); avirt = _i - bvirt;
      bround = cdycdy0 - bvirt; around = cdxcdx0 - avirt;
      cc[0] = around + bround; _j =   (cdxcdx1 + _i);
      bvirt =   (_j - cdxcdx1); avirt = _j - bvirt;
      bround = _i - bvirt; around = cdxcdx1 - avirt;
      _0 = around + bround; _i =   (_0 + cdycdy1);
      bvirt =   (_i - _0); avirt = _i - bvirt;
      bround = cdycdy1 - bvirt; around = _0 - avirt;
      cc[1] = around + bround; cc3 =   (_j + _i);
      bvirt =   (cc3 - _j); avirt = cc3 - bvirt;
      bround = _i - bvirt; around = _j - avirt; cc[2] = around + bround;
      cc[3] = cc3;
    }

    if (adxtail != 0.0) {
      axtbclen = scale(4, bc, adxtail, axtbc);
      temp16alen = scale(axtbclen, axtbc, 2.0 * adx,
                         temp16a);

      axtcclen = scale(4, cc, adxtail, axtcc);
      temp16blen =
        scale(axtcclen, axtcc, bdy, temp16b);

      axtbblen = scale(4, bb, adxtail, axtbb);
      temp16clen =
        scale(axtbblen, axtbb, -cdy, temp16c);

      temp32alen = sum(temp16alen, temp16a,
                       temp16blen, temp16b,
                       temp32a);
      temp48len = sum(temp16clen, temp16c,
                      temp32alen, temp32a,
                      temp48);
      finlength =
        sum(finlength, finnow, temp48len,
            temp48, finother);
      finswap = finnow; finnow = finother; finother = finswap;
    }
    if (adytail != 0.0) {
      aytbclen = scale(4, bc, adytail, aytbc);
      temp16alen = scale(aytbclen, aytbc, 2.0 * ady,
                         temp16a);

      aytbblen = scale(4, bb, adytail, aytbb);
      temp16blen =
        scale(aytbblen, aytbb, cdx, temp16b);

      aytcclen = scale(4, cc, adytail, aytcc);
      temp16clen =
        scale(aytcclen, aytcc, -bdx, temp16c);

      temp32alen = sum(temp16alen, temp16a,
                       temp16blen, temp16b,
                       temp32a);
      temp48len = sum(temp16clen, temp16c,
                      temp32alen, temp32a,
                      temp48);
      finlength =
        sum(finlength, finnow, temp48len,
            temp48, finother);
      finswap = finnow; finnow = finother; finother = finswap;
    }
    if (bdxtail != 0.0) {
      bxtcalen = scale(4, ca, bdxtail, bxtca);
      temp16alen = scale(bxtcalen, bxtca, 2.0 * bdx,
                         temp16a);

      bxtaalen = scale(4, aa, bdxtail, bxtaa);
      temp16blen =
        scale(bxtaalen, bxtaa, cdy, temp16b);

      bxtcclen = scale(4, cc, bdxtail, bxtcc);
      temp16clen =
        scale(bxtcclen, bxtcc, -ady, temp16c);

      temp32alen = sum(temp16alen, temp16a,
                       temp16blen, temp16b,
                       temp32a);
      temp48len = sum(temp16clen, temp16c,
                      temp32alen, temp32a,
                      temp48);
      finlength =
        sum(finlength, finnow, temp48len,
            temp48, finother);
      finswap = finnow; finnow = finother; finother = finswap;
    }
    if (bdytail != 0.0) {
      bytcalen = scale(4, ca, bdytail, bytca);
      temp16alen = scale(bytcalen, bytca, 2.0 * bdy,
                         temp16a);

      bytcclen = scale(4, cc, bdytail, bytcc);
      temp16blen =
        scale(bytcclen, bytcc, adx, temp16b);

      bytaalen = scale(4, aa, bdytail, bytaa);
      temp16clen =
        scale(bytaalen, bytaa, -cdx, temp16c);

      temp32alen = sum(temp16alen, temp16a,
                       temp16blen, temp16b,
                       temp32a);
      temp48len = sum(temp16clen, temp16c,
                      temp32alen, temp32a,
                      temp48);
      finlength =
        sum(finlength, finnow, temp48len,
            temp48, finother);
      finswap = finnow; finnow = finother; finother = finswap;
    }
    if (cdxtail != 0.0) {
      cxtablen = scale(4, ab, cdxtail, cxtab);
      temp16alen = scale(cxtablen, cxtab, 2.0 * cdx,
                         temp16a);

      cxtbblen = scale(4, bb, cdxtail, cxtbb);
      temp16blen =
        scale(cxtbblen, cxtbb, ady, temp16b);

      cxtaalen = scale(4, aa, cdxtail, cxtaa);
      temp16clen =
        scale(cxtaalen, cxtaa, -bdy, temp16c);

      temp32alen = sum(temp16alen, temp16a,
                       temp16blen, temp16b,
                       temp32a);
      temp48len = sum(temp16clen, temp16c,
                      temp32alen, temp32a,
                      temp48);
      finlength =
        sum(finlength, finnow, temp48len,
            temp48, finother);
      finswap = finnow; finnow = finother; finother = finswap;
    }
    if (cdytail != 0.0) {
      cytablen = scale(4, ab, cdytail, cytab);
      temp16alen = scale(cytablen, cytab, 2.0 * cdy,
                         temp16a);

      cytaalen = scale(4, aa, cdytail, cytaa);
      temp16blen =
        scale(cytaalen, cytaa, bdx, temp16b);

      cytbblen = scale(4, bb, cdytail, cytbb);
      temp16clen =
        scale(cytbblen, cytbb, -adx, temp16c);

      temp32alen = sum(temp16alen, temp16a,
                       temp16blen, temp16b,
                       temp32a);
      temp48len = sum(temp16clen, temp16c,
                      temp32alen, temp32a,
                      temp48);
      finlength =
        sum(finlength, finnow, temp48len,
            temp48, finother);
      finswap = finnow; finnow = finother; finother = finswap;
    }

    if ((adxtail != 0.0) || (adytail != 0.0)) {
      if ((bdxtail != 0.0) || (bdytail != 0.0)
        || (cdxtail != 0.0) || (cdytail != 0.0)) {
        ti1 =   (bdxtail * cdy);
        c =   (SPLITTER * bdxtail);
        abig =   (c - bdxtail); ahi = c - abig;
        alo = bdxtail - ahi; c =   (SPLITTER * cdy);
        abig =   (c - cdy); bhi = c - abig; blo = cdy - bhi;
        err1 = ti1 - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); ti0 = (alo * blo) - err3;
        tj1 =   (bdx * cdytail); c =   (SPLITTER * bdx);
        abig =   (c - bdx); ahi = c - abig; alo = bdx - ahi;
        c =   (SPLITTER * cdytail);
        abig =   (c - cdytail); bhi = c - abig;
        blo = cdytail - bhi; err1 = tj1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        tj0 = (alo * blo) - err3;
        _i =   (ti0 + tj0); bvirt =   (_i - ti0);
        avirt = _i - bvirt; bround = tj0 - bvirt; around = ti0 - avirt;
        u[0] = around + bround; _j =   (ti1 + _i);
        bvirt =   (_j - ti1); avirt = _j - bvirt;
        bround = _i - bvirt; around = ti1 - avirt; _0 = around + bround;
        _i =   (_0 + tj1); bvirt =   (_i - _0);
        avirt = _i - bvirt; bround = tj1 - bvirt; around = _0 - avirt;
        u[1] = around + bround; u3 =   (_j + _i);
        bvirt =   (u3 - _j); avirt = u3 - bvirt;
        bround = _i - bvirt; around = _j - avirt;
        u[2] = around + bround;
        u[3] = u3;
        negate = -bdy;
        ti1 =   (cdxtail * negate);
        c =   (SPLITTER * cdxtail);
        abig =   (c - cdxtail); ahi = c - abig;
        alo = cdxtail - ahi; c =   (SPLITTER * negate);
        abig =   (c - negate); bhi = c - abig;
        blo = negate - bhi; err1 = ti1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        ti0 = (alo * blo) - err3;
        negate = -bdytail;
        tj1 =   (cdx * negate); c =   (SPLITTER * cdx);
        abig =   (c - cdx); ahi = c - abig; alo = cdx - ahi;
        c =   (SPLITTER * negate); abig =   (c - negate);
        bhi = c - abig; blo = negate - bhi; err1 = tj1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        tj0 = (alo * blo) - err3;
        _i =   (ti0 + tj0); bvirt =   (_i - ti0);
        avirt = _i - bvirt; bround = tj0 - bvirt; around = ti0 - avirt;
        v[0] = around + bround; _j =   (ti1 + _i);
        bvirt =   (_j - ti1); avirt = _j - bvirt;
        bround = _i - bvirt; around = ti1 - avirt; _0 = around + bround;
        _i =   (_0 + tj1); bvirt =   (_i - _0);
        avirt = _i - bvirt; bround = tj1 - bvirt; around = _0 - avirt;
        v[1] = around + bround; v3 =   (_j + _i);
        bvirt =   (v3 - _j); avirt = v3 - bvirt;
        bround = _i - bvirt; around = _j - avirt;
        v[2] = around + bround;
        v[3] = v3;
        bctlen = sum(4, u, 4, v, bct);

        ti1 =   (bdxtail * cdytail);
        c =   (SPLITTER * bdxtail);
        abig =   (c - bdxtail); ahi = c - abig;
        alo = bdxtail - ahi; c =   (SPLITTER * cdytail);
        abig =   (c - cdytail); bhi = c - abig;
        blo = cdytail - bhi; err1 = ti1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        ti0 = (alo * blo) - err3;
        tj1 =   (cdxtail * bdytail);
        c =   (SPLITTER * cdxtail);
        abig =   (c - cdxtail); ahi = c - abig;
        alo = cdxtail - ahi; c =   (SPLITTER * bdytail);
        abig =   (c - bdytail); bhi = c - abig;
        blo = bdytail - bhi; err1 = tj1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        tj0 = (alo * blo) - err3;
        _i =   (ti0 - tj0); bvirt =   (ti0 - _i);
        avirt = _i + bvirt; bround = bvirt - tj0; around = ti0 - avirt;
        bctt[0] = around + bround; _j =   (ti1 + _i);
        bvirt =   (_j - ti1); avirt = _j - bvirt;
        bround = _i - bvirt; around = ti1 - avirt; _0 = around + bround;
        _i =   (_0 - tj1); bvirt =   (_0 - _i);
        avirt = _i + bvirt; bround = bvirt - tj1; around = _0 - avirt;
        bctt[1] = around + bround; bctt3 =   (_j + _i);
        bvirt =   (bctt3 - _j); avirt = bctt3 - bvirt;
        bround = _i - bvirt; around = _j - avirt;
        bctt[2] = around + bround;
        bctt[3] = bctt3;
        bcttlen = 4;
      }
      else {
        bct[0] = 0.0;
        bctlen = 1;
        bctt[0] = 0.0;
        bcttlen = 1;
      }
// TODO: axtbclen not initialized!!!
      if (adxtail != 0.0) {
        temp16alen =
          scale(axtbclen, axtbc, adxtail, temp16a);
        axtbctlen =
          scale(bctlen, bct, adxtail, axtbct);
        temp32alen =
          scale(axtbctlen, axtbct, 2.0 * adx,
                temp32a);
        temp48len = sum(temp16alen, temp16a,
                        temp32alen, temp32a,
                        temp48);
        finlength =
          sum(finlength, finnow, temp48len,
              temp48, finother);
        finswap = finnow; finnow = finother; finother = finswap;
        if (bdytail != 0.0) {
          temp8len = scale(4, cc, adxtail, temp8);
          temp16alen =
            scale(temp8len, temp8, bdytail,
                  temp16a);
          finlength =
            sum(finlength, finnow, temp16alen,
                temp16a, finother);
          finswap = finnow; finnow = finother; finother = finswap;
        }
        if (cdytail != 0.0) {
          temp8len = scale(4, bb, -adxtail, temp8);
          temp16alen =
            scale(temp8len, temp8, cdytail,
                  temp16a);
          finlength =
            sum(finlength, finnow, temp16alen,
                temp16a, finother);
          finswap = finnow; finnow = finother; finother = finswap;
        }

        temp32alen =
          scale(axtbctlen, axtbct, adxtail,
                temp32a);
        axtbcttlen =
          scale(bcttlen, bctt, adxtail, axtbctt);
        temp16alen =
          scale(axtbcttlen, axtbctt, 2.0 * adx,
                temp16a);
        temp16blen =
          scale(axtbcttlen, axtbctt, adxtail,
                temp16b);
        temp32blen = sum(temp16alen, temp16a,
                         temp16blen, temp16b,
                         temp32b);
        temp64len = sum(temp32alen, temp32a,
                        temp32blen, temp32b,
                        temp64);
        finlength =
          sum(finlength, finnow, temp64len,
              temp64, finother);
        finswap = finnow; finnow = finother; finother = finswap;
      }
// TODO: aytbclen not initialized!!!
      if (adytail != 0.0) {
        temp16alen =
          scale(aytbclen, aytbc, adytail, temp16a);
        aytbctlen =
          scale(bctlen, bct, adytail, aytbct);
        temp32alen =
          scale(aytbctlen, aytbct, 2.0 * ady,
                temp32a);
        temp48len = sum(temp16alen, temp16a,
                        temp32alen, temp32a,
                        temp48);
        finlength =
          sum(finlength, finnow, temp48len,
              temp48, finother);
        finswap = finnow; finnow = finother; finother = finswap;

        temp32alen =
          scale(aytbctlen, aytbct, adytail,
                temp32a);
        aytbcttlen =
          scale(bcttlen, bctt, adytail, aytbctt);
        temp16alen =
          scale(aytbcttlen, aytbctt, 2.0 * ady,
                temp16a);
        temp16blen =
          scale(aytbcttlen, aytbctt, adytail,
                temp16b);
        temp32blen = sum(temp16alen, temp16a,
                         temp16blen, temp16b,
                         temp32b);
        temp64len = sum(temp32alen, temp32a,
                        temp32blen, temp32b,
                        temp64);
        finlength =
          sum(finlength, finnow, temp64len,
              temp64, finother);
        finswap = finnow; finnow = finother; finother = finswap;
      }
    }
    if ((bdxtail != 0.0) || (bdytail != 0.0)) {
      if ((cdxtail != 0.0) || (cdytail != 0.0)
        || (adxtail != 0.0) || (adytail != 0.0)) {
        ti1 =   (cdxtail * ady);
        c =   (SPLITTER * cdxtail);
        abig =   (c - cdxtail); ahi = c - abig;
        alo = cdxtail - ahi; c =   (SPLITTER * ady);
        abig =   (c - ady); bhi = c - abig; blo = ady - bhi;
        err1 = ti1 - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); ti0 = (alo * blo) - err3;
        tj1 =   (cdx * adytail); c =   (SPLITTER * cdx);
        abig =   (c - cdx); ahi = c - abig; alo = cdx - ahi;
        c =   (SPLITTER * adytail);
        abig =   (c - adytail); bhi = c - abig;
        blo = adytail - bhi; err1 = tj1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        tj0 = (alo * blo) - err3;
        _i =   (ti0 + tj0); bvirt =   (_i - ti0);
        avirt = _i - bvirt; bround = tj0 - bvirt; around = ti0 - avirt;
        u[0] = around + bround; _j =   (ti1 + _i);
        bvirt =   (_j - ti1); avirt = _j - bvirt;
        bround = _i - bvirt; around = ti1 - avirt; _0 = around + bround;
        _i =   (_0 + tj1); bvirt =   (_i - _0);
        avirt = _i - bvirt; bround = tj1 - bvirt; around = _0 - avirt;
        u[1] = around + bround; u3 =   (_j + _i);
        bvirt =   (u3 - _j); avirt = u3 - bvirt;
        bround = _i - bvirt; around = _j - avirt;
        u[2] = around + bround;
        u[3] = u3;
        negate = -cdy;
        ti1 =   (adxtail * negate);
        c =   (SPLITTER * adxtail);
        abig =   (c - adxtail); ahi = c - abig;
        alo = adxtail - ahi; c =   (SPLITTER * negate);
        abig =   (c - negate); bhi = c - abig;
        blo = negate - bhi; err1 = ti1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        ti0 = (alo * blo) - err3;
        negate = -cdytail;
        tj1 =   (adx * negate); c =   (SPLITTER * adx);
        abig =   (c - adx); ahi = c - abig; alo = adx - ahi;
        c =   (SPLITTER * negate); abig =   (c - negate);
        bhi = c - abig; blo = negate - bhi; err1 = tj1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        tj0 = (alo * blo) - err3;
        _i =   (ti0 + tj0); bvirt =   (_i - ti0);
        avirt = _i - bvirt; bround = tj0 - bvirt; around = ti0 - avirt;
        v[0] = around + bround; _j =   (ti1 + _i);
        bvirt =   (_j - ti1); avirt = _j - bvirt;
        bround = _i - bvirt; around = ti1 - avirt; _0 = around + bround;
        _i =   (_0 + tj1); bvirt =   (_i - _0);
        avirt = _i - bvirt; bround = tj1 - bvirt; around = _0 - avirt;
        v[1] = around + bround; v3 =   (_j + _i);
        bvirt =   (v3 - _j); avirt = v3 - bvirt;
        bround = _i - bvirt; around = _j - avirt;
        v[2] = around + bround;
        v[3] = v3;
        catlen = sum(4, u, 4, v, cat);

        ti1 =   (cdxtail * adytail);
        c =   (SPLITTER * cdxtail);
        abig =   (c - cdxtail); ahi = c - abig;
        alo = cdxtail - ahi; c =   (SPLITTER * adytail);
        abig =   (c - adytail); bhi = c - abig;
        blo = adytail - bhi; err1 = ti1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        ti0 = (alo * blo) - err3;
        tj1 =   (adxtail * cdytail);
        c =   (SPLITTER * adxtail);
        abig =   (c - adxtail); ahi = c - abig;
        alo = adxtail - ahi; c =   (SPLITTER * cdytail);
        abig =   (c - cdytail); bhi = c - abig;
        blo = cdytail - bhi; err1 = tj1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        tj0 = (alo * blo) - err3;
        _i =   (ti0 - tj0); bvirt =   (ti0 - _i);
        avirt = _i + bvirt; bround = bvirt - tj0; around = ti0 - avirt;
        catt[0] = around + bround; _j =   (ti1 + _i);
        bvirt =   (_j - ti1); avirt = _j - bvirt;
        bround = _i - bvirt; around = ti1 - avirt; _0 = around + bround;
        _i =   (_0 - tj1); bvirt =   (_0 - _i);
        avirt = _i + bvirt; bround = bvirt - tj1; around = _0 - avirt;
        catt[1] = around + bround; catt3 =   (_j + _i);
        bvirt =   (catt3 - _j); avirt = catt3 - bvirt;
        bround = _i - bvirt; around = _j - avirt;
        catt[2] = around + bround;
        catt[3] = catt3;
        cattlen = 4;
      }
      else {
        cat[0] = 0.0;
        catlen = 1;
        catt[0] = 0.0;
        cattlen = 1;
      }
// TODO: bxtcalen not initialized!!!
      if (bdxtail != 0.0) {
        temp16alen =
          scale(bxtcalen, bxtca, bdxtail, temp16a);
        bxtcatlen =
          scale(catlen, cat, bdxtail, bxtcat);
        temp32alen =
          scale(bxtcatlen, bxtcat, 2.0 * bdx,
                temp32a);
        temp48len = sum(temp16alen, temp16a,
                        temp32alen, temp32a,
                        temp48);
        finlength =
          sum(finlength, finnow, temp48len,
              temp48, finother);
        finswap = finnow; finnow = finother; finother = finswap;
        if (cdytail != 0.0) {
          temp8len = scale(4, aa, bdxtail, temp8);
          temp16alen =
            scale(temp8len, temp8, cdytail,
                  temp16a);
          finlength =
            sum(finlength, finnow, temp16alen,
                temp16a, finother);
          finswap = finnow; finnow = finother; finother = finswap;
        }
        if (adytail != 0.0) {
          temp8len = scale(4, cc, -bdxtail, temp8);
          temp16alen =
            scale(temp8len, temp8, adytail,
                  temp16a);
          finlength =
            sum(finlength, finnow, temp16alen,
                temp16a, finother);
          finswap = finnow; finnow = finother; finother = finswap;
        }

        temp32alen =
          scale(bxtcatlen, bxtcat, bdxtail,
                temp32a);
        bxtcattlen =
          scale(cattlen, catt, bdxtail, bxtcatt);
        temp16alen =
          scale(bxtcattlen, bxtcatt, 2.0 * bdx,
                temp16a);
        temp16blen =
          scale(bxtcattlen, bxtcatt, bdxtail,
                temp16b);
        temp32blen = sum(temp16alen, temp16a,
                         temp16blen, temp16b,
                         temp32b);
        temp64len = sum(temp32alen, temp32a,
                        temp32blen, temp32b,
                        temp64);
        finlength =
          sum(finlength, finnow, temp64len,
              temp64, finother);
        finswap = finnow; finnow = finother; finother = finswap;
      }
      // TODO: bytcalen not initialized!!!
      if (bdytail != 0.0) {
        temp16alen =
          scale(bytcalen, bytca, bdytail, temp16a);
        bytcatlen =
          scale(catlen, cat, bdytail, bytcat);
        temp32alen =
          scale(bytcatlen, bytcat, 2.0 * bdy,
                temp32a);
        temp48len = sum(temp16alen, temp16a,
                        temp32alen, temp32a,
                        temp48);
        finlength =
          sum(finlength, finnow, temp48len,
              temp48, finother);
        finswap = finnow; finnow = finother; finother = finswap;

        temp32alen =
          scale(bytcatlen, bytcat, bdytail,
                temp32a);
        bytcattlen =
          scale(cattlen, catt, bdytail, bytcatt);
        temp16alen =
          scale(bytcattlen, bytcatt, 2.0 * bdy,
                temp16a);
        temp16blen =
          scale(bytcattlen, bytcatt, bdytail,
                temp16b);
        temp32blen = sum(temp16alen, temp16a,
                         temp16blen, temp16b,
                         temp32b);
        temp64len = sum(temp32alen, temp32a,
                        temp32blen, temp32b,
                        temp64);
        finlength =
          sum(finlength, finnow, temp64len,
              temp64, finother);
        finswap = finnow; finnow = finother; finother = finswap;
      }
    }
    if ((cdxtail != 0.0) || (cdytail != 0.0)) {
      if ((adxtail != 0.0) || (adytail != 0.0)
        || (bdxtail != 0.0) || (bdytail != 0.0)) {
        ti1 =   (adxtail * bdy);
        c =   (SPLITTER * adxtail);
        abig =   (c - adxtail); ahi = c - abig;
        alo = adxtail - ahi; c =   (SPLITTER * bdy);
        abig =   (c - bdy); bhi = c - abig; blo = bdy - bhi;
        err1 = ti1 - (ahi * bhi); err2 = err1 - (alo * bhi);
        err3 = err2 - (ahi * blo); ti0 = (alo * blo) - err3;
        tj1 =   (adx * bdytail); c =   (SPLITTER * adx);
        abig =   (c - adx); ahi = c - abig; alo = adx - ahi;
        c =   (SPLITTER * bdytail);
        abig =   (c - bdytail); bhi = c - abig;
        blo = bdytail - bhi; err1 = tj1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        tj0 = (alo * blo) - err3;
        _i =   (ti0 + tj0); bvirt =   (_i - ti0);
        avirt = _i - bvirt; bround = tj0 - bvirt; around = ti0 - avirt;
        u[0] = around + bround; _j =   (ti1 + _i);
        bvirt =   (_j - ti1); avirt = _j - bvirt;
        bround = _i - bvirt; around = ti1 - avirt; _0 = around + bround;
        _i =   (_0 + tj1); bvirt =   (_i - _0);
        avirt = _i - bvirt; bround = tj1 - bvirt; around = _0 - avirt;
        u[1] = around + bround; u3 =   (_j + _i);
        bvirt =   (u3 - _j); avirt = u3 - bvirt;
        bround = _i - bvirt; around = _j - avirt;
        u[2] = around + bround;
        u[3] = u3;
        negate = -ady;
        ti1 =   (bdxtail * negate);
        c =   (SPLITTER * bdxtail);
        abig =   (c - bdxtail); ahi = c - abig;
        alo = bdxtail - ahi; c =   (SPLITTER * negate);
        abig =   (c - negate); bhi = c - abig;
        blo = negate - bhi; err1 = ti1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        ti0 = (alo * blo) - err3;
        negate = -adytail;
        tj1 =   (bdx * negate); c =   (SPLITTER * bdx);
        abig =   (c - bdx); ahi = c - abig; alo = bdx - ahi;
        c =   (SPLITTER * negate); abig =   (c - negate);
        bhi = c - abig; blo = negate - bhi; err1 = tj1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        tj0 = (alo * blo) - err3;
        _i =   (ti0 + tj0); bvirt =   (_i - ti0);
        avirt = _i - bvirt; bround = tj0 - bvirt; around = ti0 - avirt;
        v[0] = around + bround; _j =   (ti1 + _i);
        bvirt =   (_j - ti1); avirt = _j - bvirt;
        bround = _i - bvirt; around = ti1 - avirt; _0 = around + bround;
        _i =   (_0 + tj1); bvirt =   (_i - _0);
        avirt = _i - bvirt; bround = tj1 - bvirt; around = _0 - avirt;
        v[1] = around + bround; v3 =   (_j + _i);
        bvirt =   (v3 - _j); avirt = v3 - bvirt;
        bround = _i - bvirt; around = _j - avirt;
        v[2] = around + bround;
        v[3] = v3;
        abtlen = sum(4, u, 4, v, abt);

        ti1 =   (adxtail * bdytail);
        c =   (SPLITTER * adxtail);
        abig =   (c - adxtail); ahi = c - abig;
        alo = adxtail - ahi; c =   (SPLITTER * bdytail);
        abig =   (c - bdytail); bhi = c - abig;
        blo = bdytail - bhi; err1 = ti1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        ti0 = (alo * blo) - err3;
        tj1 =   (bdxtail * adytail);
        c =   (SPLITTER * bdxtail);
        abig =   (c - bdxtail); ahi = c - abig;
        alo = bdxtail - ahi; c =   (SPLITTER * adytail);
        abig =   (c - adytail); bhi = c - abig;
        blo = adytail - bhi; err1 = tj1 - (ahi * bhi);
        err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
        tj0 = (alo * blo) - err3;
        _i =   (ti0 - tj0); bvirt =   (ti0 - _i);
        avirt = _i + bvirt; bround = bvirt - tj0; around = ti0 - avirt;
        abtt[0] = around + bround; _j =   (ti1 + _i);
        bvirt =   (_j - ti1); avirt = _j - bvirt;
        bround = _i - bvirt; around = ti1 - avirt; _0 = around + bround;
        _i =   (_0 - tj1); bvirt =   (_0 - _i);
        avirt = _i + bvirt; bround = bvirt - tj1; around = _0 - avirt;
        abtt[1] = around + bround; abtt3 =   (_j + _i);
        bvirt =   (abtt3 - _j); avirt = abtt3 - bvirt;
        bround = _i - bvirt; around = _j - avirt;
        abtt[2] = around + bround;
        abtt[3] = abtt3;
        abttlen = 4;
      }
      else {
        abt[0] = 0.0;
        abtlen = 1;
        abtt[0] = 0.0;
        abttlen = 1;
      }
// TODO: cxtablen not initialized!!!
      if (cdxtail != 0.0) {
        temp16alen =
          scale(cxtablen, cxtab, cdxtail, temp16a);
        cxtabtlen =
          scale(abtlen, abt, cdxtail, cxtabt);
        temp32alen =
          scale(cxtabtlen, cxtabt, 2.0 * cdx,
                temp32a);
        temp48len = sum(temp16alen, temp16a,
                        temp32alen, temp32a,
                        temp48);
        finlength =
          sum(finlength, finnow, temp48len,
              temp48, finother);
        finswap = finnow; finnow = finother; finother = finswap;
        if (adytail != 0.0) {
          temp8len = scale(4, bb, cdxtail, temp8);
          temp16alen =
            scale(temp8len, temp8, adytail,
                  temp16a);
          finlength =
            sum(finlength, finnow, temp16alen,
                temp16a, finother);
          finswap = finnow; finnow = finother; finother = finswap;
        }
        if (bdytail != 0.0) {
          temp8len = scale(4, aa, -cdxtail, temp8);
          temp16alen =
            scale(temp8len, temp8, bdytail,
                  temp16a);
          finlength =
            sum(finlength, finnow, temp16alen,
                temp16a, finother);
          finswap = finnow; finnow = finother; finother = finswap;
        }

        temp32alen =
          scale(cxtabtlen, cxtabt, cdxtail,
                temp32a);
        cxtabttlen =
          scale(abttlen, abtt, cdxtail, cxtabtt);
        temp16alen =
          scale(cxtabttlen, cxtabtt, 2.0 * cdx,
                temp16a);
        temp16blen =
          scale(cxtabttlen, cxtabtt, cdxtail,
                temp16b);
        temp32blen = sum(temp16alen, temp16a,
                         temp16blen, temp16b,
                         temp32b);
        temp64len = sum(temp32alen, temp32a,
                        temp32blen, temp32b,
                        temp64);
        finlength =
          sum(finlength, finnow, temp64len,
              temp64, finother);
        finswap = finnow; finnow = finother; finother = finswap;
      }
// TODO: cytablen not initialized!!!
      if (cdytail != 0.0) {
        temp16alen =
          scale(cytablen, cytab, cdytail, temp16a);
        cytabtlen =
          scale(abtlen, abt, cdytail, cytabt);
        temp32alen =
          scale(cytabtlen, cytabt, 2.0 * cdy,
                temp32a);
        temp48len = sum(temp16alen, temp16a,
                        temp32alen, temp32a,
                        temp48);
        finlength =
          sum(finlength, finnow, temp48len,
              temp48, finother);
        finswap = finnow; finnow = finother; finother = finswap;

        temp32alen =
          scale(cytabtlen, cytabt, cdytail,
                temp32a);
        cytabttlen =
          scale(abttlen, abtt, cdytail, cytabtt);
        temp16alen =
          scale(cytabttlen, cytabtt, 2.0 * cdy,
                temp16a);
        temp16blen =
          scale(cytabttlen, cytabtt, cdytail,
                temp16b);
        temp32blen = sum(temp16alen, temp16a,
                         temp16blen, temp16b,
                         temp32b);
        temp64len = sum(temp32alen, temp32a,
                        temp32blen, temp32b,
                        temp64);
        finlength =
          sum(finlength, finnow, temp64len,
              temp64, finother);
        //finswap = finnow;
        finnow = finother;
        //finother = finswap;
      }
    }

    return finnow[finlength - 1];
  }

  //--------------------------------------------------------------------
  private static final double iccerrboundA =
    (10.0 + 96.0 * EPSILON) * EPSILON;

  public static final double incircle (final double[] pa,
                                        final double[] pb,
                                        final double[] pc,
                                        final double[] pd) {
    double adx, bdx, cdx, ady, bdy, cdy;
    double bdxcdy, cdxbdy, cdxady, adxcdy, adxbdy, bdxady;
    double alift, blift, clift;
    double det;
    double permanent, errbound;

    adx = pa[0] - pd[0];
    bdx = pb[0] - pd[0];
    cdx = pc[0] - pd[0];
    ady = pa[1] - pd[1];
    bdy = pb[1] - pd[1];
    cdy = pc[1] - pd[1];

    bdxcdy = bdx * cdy;
    cdxbdy = cdx * bdy;
    alift = adx * adx + ady * ady;

    cdxady = cdx * ady;
    adxcdy = adx * cdy;
    blift = bdx * bdx + bdy * bdy;

    adxbdy = adx * bdy;
    bdxady = bdx * ady;
    clift = cdx * cdx + cdy * cdy;

    det = alift * (bdxcdy - cdxbdy)
      + blift * (cdxady - adxcdy)
      + clift * (adxbdy - bdxady);

    permanent =
      (((bdxcdy) >= 0.0 ? (bdxcdy) : -(bdxcdy)) + ((cdxbdy) >= 0.0
                                                   ? (cdxbdy)
                                                   : -(cdxbdy))) * alift
        + (((cdxady) >= 0.0 ? (cdxady) : -(cdxady)) + ((adxcdy) >= 0.0
                                                       ? (adxcdy)
                                                       : -(adxcdy))) * blift
        + (((adxbdy) >= 0.0 ? (adxbdy) : -(adxbdy)) + ((bdxady) >= 0.0
                                                       ? (bdxady)
                                                       : -(bdxady))) * clift;
    errbound = iccerrboundA * permanent;
    if ((det > errbound) || (-det > errbound)) {
      return det;
    }

    return incircleadapt(pa, pb, pc, pd, permanent);
  }

  //--------------------------------------------------------------------
  // insphere
  //--------------------------------------------------------------------
  public static final double inspherefast (final double[] pa,
                                            final double[] pb,
                                            final double[] pc,
                                            final double[] pd,
                                            final double[] pe) {
    double aex, bex, cex, dex;
    double aey, bey, cey, dey;
    double aez, bez, cez, dez;
    double alift, blift, clift, dlift;
    double ab, bc, cd, da, ac, bd;
    double abc, bcd, cda, dab;

    aex = pa[0] - pe[0];
    bex = pb[0] - pe[0];
    cex = pc[0] - pe[0];
    dex = pd[0] - pe[0];
    aey = pa[1] - pe[1];
    bey = pb[1] - pe[1];
    cey = pc[1] - pe[1];
    dey = pd[1] - pe[1];
    aez = pa[2] - pe[2];
    bez = pb[2] - pe[2];
    cez = pc[2] - pe[2];
    dez = pd[2] - pe[2];

    ab = aex * bey - bex * aey;
    bc = bex * cey - cex * bey;
    cd = cex * dey - dex * cey;
    da = dex * aey - aex * dey;

    ac = aex * cey - cex * aey;
    bd = bex * dey - dex * bey;

    abc = aez * bc - bez * ac + cez * ab;
    bcd = bez * cd - cez * bd + dez * bc;
    cda = cez * da + dez * ac + aez * cd;
    dab = dez * ab + aez * bd + bez * da;

    alift = aex * aex + aey * aey + aez * aez;
    blift = bex * bex + bey * bey + bez * bez;
    clift = cex * cex + cey * cey + cez * cez;
    dlift = dex * dex + dey * dey + dez * dez;

    return (dlift * abc - clift * dab) + (blift * cda - alift * bcd);
  }

  //--------------------------------------------------------------------
  private static final double insphereexact (final double[] pa,
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
    int deterlen;
    int i;

    double bvirt;
    double avirt, bround, around;
    double c;
    double abig;
    double ahi, alo, bhi, blo;
    double err1, err2, err3;
    double _i, _j;
    double _0;

    axby1 =   (pa[0] * pb[1]); c =   (SPLITTER * pa[0]);
    abig =   (c - pa[0]); ahi = c - abig; alo = pa[0] - ahi;
    c =   (SPLITTER * pb[1]); abig =   (c - pb[1]);
    bhi = c - abig; blo = pb[1] - bhi; err1 = axby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axby0 = (alo * blo) - err3;
    bxay1 =   (pb[0] * pa[1]); c =   (SPLITTER * pb[0]);
    abig =   (c - pb[0]); ahi = c - abig; alo = pb[0] - ahi;
    c =   (SPLITTER * pa[1]); abig =   (c - pa[1]);
    bhi = c - abig; blo = pa[1] - bhi; err1 = bxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxay0 = (alo * blo) - err3;
    _i =   (axby0 - bxay0); bvirt =   (axby0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxay0; around = axby0 - avirt;
    ab[0] = around + bround; _j =   (axby1 + _i);
    bvirt =   (_j - axby1); avirt = _j - bvirt;
    bround = _i - bvirt; around = axby1 - avirt; _0 = around + bround;
    _i =   (_0 - bxay1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxay1; around = _0 - avirt;
    ab[1] = around + bround; ab[3] =   (_j + _i);
    bvirt =   (ab[3] - _j); avirt = ab[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; ab[2] = around + bround;

    bxcy1 =   (pb[0] * pc[1]); c =   (SPLITTER * pb[0]);
    abig =   (c - pb[0]); ahi = c - abig; alo = pb[0] - ahi;
    c =   (SPLITTER * pc[1]); abig =   (c - pc[1]);
    bhi = c - abig; blo = pc[1] - bhi; err1 = bxcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxcy0 = (alo * blo) - err3;
    cxby1 =   (pc[0] * pb[1]); c =   (SPLITTER * pc[0]);
    abig =   (c - pc[0]); ahi = c - abig; alo = pc[0] - ahi;
    c =   (SPLITTER * pb[1]); abig =   (c - pb[1]);
    bhi = c - abig; blo = pb[1] - bhi; err1 = cxby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxby0 = (alo * blo) - err3;
    _i =   (bxcy0 - cxby0); bvirt =   (bxcy0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxby0; around = bxcy0 - avirt;
    bc[0] = around + bround; _j =   (bxcy1 + _i);
    bvirt =   (_j - bxcy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = bxcy1 - avirt; _0 = around + bround;
    _i =   (_0 - cxby1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxby1; around = _0 - avirt;
    bc[1] = around + bround; bc[3] =   (_j + _i);
    bvirt =   (bc[3] - _j); avirt = bc[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; bc[2] = around + bround;

    cxdy1 =   (pc[0] * pd[1]); c =   (SPLITTER * pc[0]);
    abig =   (c - pc[0]); ahi = c - abig; alo = pc[0] - ahi;
    c =   (SPLITTER * pd[1]); abig =   (c - pd[1]);
    bhi = c - abig; blo = pd[1] - bhi; err1 = cxdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxdy0 = (alo * blo) - err3;
    dxcy1 =   (pd[0] * pc[1]); c =   (SPLITTER * pd[0]);
    abig =   (c - pd[0]); ahi = c - abig; alo = pd[0] - ahi;
    c =   (SPLITTER * pc[1]); abig =   (c - pc[1]);
    bhi = c - abig; blo = pc[1] - bhi; err1 = dxcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxcy0 = (alo * blo) - err3;
    _i =   (cxdy0 - dxcy0); bvirt =   (cxdy0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxcy0; around = cxdy0 - avirt;
    cd[0] = around + bround; _j =   (cxdy1 + _i);
    bvirt =   (_j - cxdy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = cxdy1 - avirt; _0 = around + bround;
    _i =   (_0 - dxcy1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxcy1; around = _0 - avirt;
    cd[1] = around + bround; cd[3] =   (_j + _i);
    bvirt =   (cd[3] - _j); avirt = cd[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; cd[2] = around + bround;

    dxey1 =   (pd[0] * pe[1]); c =   (SPLITTER * pd[0]);
    abig =   (c - pd[0]); ahi = c - abig; alo = pd[0] - ahi;
    c =   (SPLITTER * pe[1]); abig =   (c - pe[1]);
    bhi = c - abig; blo = pe[1] - bhi; err1 = dxey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxey0 = (alo * blo) - err3;
    exdy1 =   (pe[0] * pd[1]); c =   (SPLITTER * pe[0]);
    abig =   (c - pe[0]); ahi = c - abig; alo = pe[0] - ahi;
    c =   (SPLITTER * pd[1]); abig =   (c - pd[1]);
    bhi = c - abig; blo = pd[1] - bhi; err1 = exdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    exdy0 = (alo * blo) - err3;
    _i =   (dxey0 - exdy0); bvirt =   (dxey0 - _i);
    avirt = _i + bvirt; bround = bvirt - exdy0; around = dxey0 - avirt;
    de[0] = around + bround; _j =   (dxey1 + _i);
    bvirt =   (_j - dxey1); avirt = _j - bvirt;
    bround = _i - bvirt; around = dxey1 - avirt; _0 = around + bround;
    _i =   (_0 - exdy1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - exdy1; around = _0 - avirt;
    de[1] = around + bround; de[3] =   (_j + _i);
    bvirt =   (de[3] - _j); avirt = de[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; de[2] = around + bround;

    exay1 =   (pe[0] * pa[1]); c =   (SPLITTER * pe[0]);
    abig =   (c - pe[0]); ahi = c - abig; alo = pe[0] - ahi;
    c =   (SPLITTER * pa[1]); abig =   (c - pa[1]);
    bhi = c - abig; blo = pa[1] - bhi; err1 = exay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    exay0 = (alo * blo) - err3;
    axey1 =   (pa[0] * pe[1]); c =   (SPLITTER * pa[0]);
    abig =   (c - pa[0]); ahi = c - abig; alo = pa[0] - ahi;
    c =   (SPLITTER * pe[1]); abig =   (c - pe[1]);
    bhi = c - abig; blo = pe[1] - bhi; err1 = axey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axey0 = (alo * blo) - err3;
    _i =   (exay0 - axey0); bvirt =   (exay0 - _i);
    avirt = _i + bvirt; bround = bvirt - axey0; around = exay0 - avirt;
    ea[0] = around + bround; _j =   (exay1 + _i);
    bvirt =   (_j - exay1); avirt = _j - bvirt;
    bround = _i - bvirt; around = exay1 - avirt; _0 = around + bround;
    _i =   (_0 - axey1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - axey1; around = _0 - avirt;
    ea[1] = around + bround; ea[3] =   (_j + _i);
    bvirt =   (ea[3] - _j); avirt = ea[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; ea[2] = around + bround;

    axcy1 =   (pa[0] * pc[1]); c =   (SPLITTER * pa[0]);
    abig =   (c - pa[0]); ahi = c - abig; alo = pa[0] - ahi;
    c =   (SPLITTER * pc[1]); abig =   (c - pc[1]);
    bhi = c - abig; blo = pc[1] - bhi; err1 = axcy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axcy0 = (alo * blo) - err3;
    cxay1 =   (pc[0] * pa[1]); c =   (SPLITTER * pc[0]);
    abig =   (c - pc[0]); ahi = c - abig; alo = pc[0] - ahi;
    c =   (SPLITTER * pa[1]); abig =   (c - pa[1]);
    bhi = c - abig; blo = pa[1] - bhi; err1 = cxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxay0 = (alo * blo) - err3;
    _i =   (axcy0 - cxay0); bvirt =   (axcy0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxay0; around = axcy0 - avirt;
    ac[0] = around + bround; _j =   (axcy1 + _i);
    bvirt =   (_j - axcy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = axcy1 - avirt; _0 = around + bround;
    _i =   (_0 - cxay1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - cxay1; around = _0 - avirt;
    ac[1] = around + bround; ac[3] =   (_j + _i);
    bvirt =   (ac[3] - _j); avirt = ac[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; ac[2] = around + bround;

    bxdy1 =   (pb[0] * pd[1]); c =   (SPLITTER * pb[0]);
    abig =   (c - pb[0]); ahi = c - abig; alo = pb[0] - ahi;
    c =   (SPLITTER * pd[1]); abig =   (c - pd[1]);
    bhi = c - abig; blo = pd[1] - bhi; err1 = bxdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxdy0 = (alo * blo) - err3;
    dxby1 =   (pd[0] * pb[1]); c =   (SPLITTER * pd[0]);
    abig =   (c - pd[0]); ahi = c - abig; alo = pd[0] - ahi;
    c =   (SPLITTER * pb[1]); abig =   (c - pb[1]);
    bhi = c - abig; blo = pb[1] - bhi; err1 = dxby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxby0 = (alo * blo) - err3;
    _i =   (bxdy0 - dxby0); bvirt =   (bxdy0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxby0; around = bxdy0 - avirt;
    bd[0] = around + bround; _j =   (bxdy1 + _i);
    bvirt =   (_j - bxdy1); avirt = _j - bvirt;
    bround = _i - bvirt; around = bxdy1 - avirt; _0 = around + bround;
    _i =   (_0 - dxby1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - dxby1; around = _0 - avirt;
    bd[1] = around + bround; bd[3] =   (_j + _i);
    bvirt =   (bd[3] - _j); avirt = bd[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; bd[2] = around + bround;

    cxey1 =   (pc[0] * pe[1]); c =   (SPLITTER * pc[0]);
    abig =   (c - pc[0]); ahi = c - abig; alo = pc[0] - ahi;
    c =   (SPLITTER * pe[1]); abig =   (c - pe[1]);
    bhi = c - abig; blo = pe[1] - bhi; err1 = cxey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cxey0 = (alo * blo) - err3;
    excy1 =   (pe[0] * pc[1]); c =   (SPLITTER * pe[0]);
    abig =   (c - pe[0]); ahi = c - abig; alo = pe[0] - ahi;
    c =   (SPLITTER * pc[1]); abig =   (c - pc[1]);
    bhi = c - abig; blo = pc[1] - bhi; err1 = excy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    excy0 = (alo * blo) - err3;
    _i =   (cxey0 - excy0); bvirt =   (cxey0 - _i);
    avirt = _i + bvirt; bround = bvirt - excy0; around = cxey0 - avirt;
    ce[0] = around + bround; _j =   (cxey1 + _i);
    bvirt =   (_j - cxey1); avirt = _j - bvirt;
    bround = _i - bvirt; around = cxey1 - avirt; _0 = around + bround;
    _i =   (_0 - excy1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - excy1; around = _0 - avirt;
    ce[1] = around + bround; ce[3] =   (_j + _i);
    bvirt =   (ce[3] - _j); avirt = ce[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; ce[2] = around + bround;

    dxay1 =   (pd[0] * pa[1]); c =   (SPLITTER * pd[0]);
    abig =   (c - pd[0]); ahi = c - abig; alo = pd[0] - ahi;
    c =   (SPLITTER * pa[1]); abig =   (c - pa[1]);
    bhi = c - abig; blo = pa[1] - bhi; err1 = dxay1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dxay0 = (alo * blo) - err3;
    axdy1 =   (pa[0] * pd[1]); c =   (SPLITTER * pa[0]);
    abig =   (c - pa[0]); ahi = c - abig; alo = pa[0] - ahi;
    c =   (SPLITTER * pd[1]); abig =   (c - pd[1]);
    bhi = c - abig; blo = pd[1] - bhi; err1 = axdy1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    axdy0 = (alo * blo) - err3;
    _i =   (dxay0 - axdy0); bvirt =   (dxay0 - _i);
    avirt = _i + bvirt; bround = bvirt - axdy0; around = dxay0 - avirt;
    da[0] = around + bround; _j =   (dxay1 + _i);
    bvirt =   (_j - dxay1); avirt = _j - bvirt;
    bround = _i - bvirt; around = dxay1 - avirt; _0 = around + bround;
    _i =   (_0 - axdy1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - axdy1; around = _0 - avirt;
    da[1] = around + bround; da[3] =   (_j + _i);
    bvirt =   (da[3] - _j); avirt = da[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; da[2] = around + bround;

    exby1 =   (pe[0] * pb[1]); c =   (SPLITTER * pe[0]);
    abig =   (c - pe[0]); ahi = c - abig; alo = pe[0] - ahi;
    c =   (SPLITTER * pb[1]); abig =   (c - pb[1]);
    bhi = c - abig; blo = pb[1] - bhi; err1 = exby1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    exby0 = (alo * blo) - err3;
    bxey1 =   (pb[0] * pe[1]); c =   (SPLITTER * pb[0]);
    abig =   (c - pb[0]); ahi = c - abig; alo = pb[0] - ahi;
    c =   (SPLITTER * pe[1]); abig =   (c - pe[1]);
    bhi = c - abig; blo = pe[1] - bhi; err1 = bxey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bxey0 = (alo * blo) - err3;
    _i =   (exby0 - bxey0); bvirt =   (exby0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxey0; around = exby0 - avirt;
    eb[0] = around + bround; _j =   (exby1 + _i);
    bvirt =   (_j - exby1); avirt = _j - bvirt;
    bround = _i - bvirt; around = exby1 - avirt; _0 = around + bround;
    _i =   (_0 - bxey1); bvirt =   (_0 - _i);
    avirt = _i + bvirt; bround = bvirt - bxey1; around = _0 - avirt;
    eb[1] = around + bround; eb[3] =   (_j + _i);
    bvirt =   (eb[3] - _j); avirt = eb[3] - bvirt;
    bround = _i - bvirt; around = _j - avirt; eb[2] = around + bround;

    temp8alen = scale(4, bc, pa[2], temp8a);
    temp8blen = scale(4, ac, -pb[2], temp8b);
    temp16len =
      sum(temp8alen, temp8a, temp8blen, temp8b,
          temp16);
    temp8alen = scale(4, ab, pc[2], temp8a);
    abclen =
      sum(temp8alen, temp8a, temp16len, temp16,
          abc);

    temp8alen = scale(4, cd, pb[2], temp8a);
    temp8blen = scale(4, bd, -pc[2], temp8b);
    temp16len =
      sum(temp8alen, temp8a, temp8blen, temp8b,
          temp16);
    temp8alen = scale(4, bc, pd[2], temp8a);
    bcdlen =
      sum(temp8alen, temp8a, temp16len, temp16,
          bcd);

    temp8alen = scale(4, de, pc[2], temp8a);
    temp8blen = scale(4, ce, -pd[2], temp8b);
    temp16len =
      sum(temp8alen, temp8a, temp8blen, temp8b,
          temp16);
    temp8alen = scale(4, cd, pe[2], temp8a);
    cdelen =
      sum(temp8alen, temp8a, temp16len, temp16,
          cde);

    temp8alen = scale(4, ea, pd[2], temp8a);
    temp8blen = scale(4, da, -pe[2], temp8b);
    temp16len =
      sum(temp8alen, temp8a, temp8blen, temp8b,
          temp16);
    temp8alen = scale(4, de, pa[2], temp8a);
    dealen =
      sum(temp8alen, temp8a, temp16len, temp16,
          dea);

    temp8alen = scale(4, ab, pe[2], temp8a);
    temp8blen = scale(4, eb, -pa[2], temp8b);
    temp16len =
      sum(temp8alen, temp8a, temp8blen, temp8b,
          temp16);
    temp8alen = scale(4, ea, pb[2], temp8a);
    eablen =
      sum(temp8alen, temp8a, temp16len, temp16,
          eab);

    temp8alen = scale(4, bd, pa[2], temp8a);
    temp8blen = scale(4, da, pb[2], temp8b);
    temp16len =
      sum(temp8alen, temp8a, temp8blen, temp8b,
          temp16);
    temp8alen = scale(4, ab, pd[2], temp8a);
    abdlen =
      sum(temp8alen, temp8a, temp16len, temp16,
          abd);

    temp8alen = scale(4, ce, pb[2], temp8a);
    temp8blen = scale(4, eb, pc[2], temp8b);
    temp16len =
      sum(temp8alen, temp8a, temp8blen, temp8b,
          temp16);
    temp8alen = scale(4, bc, pe[2], temp8a);
    bcelen =
      sum(temp8alen, temp8a, temp16len, temp16,
          bce);

    temp8alen = scale(4, da, pc[2], temp8a);
    temp8blen = scale(4, ac, pd[2], temp8b);
    temp16len =
      sum(temp8alen, temp8a, temp8blen, temp8b,
          temp16);
    temp8alen = scale(4, cd, pa[2], temp8a);
    cdalen =
      sum(temp8alen, temp8a, temp16len, temp16,
          cda);

    temp8alen = scale(4, eb, pd[2], temp8a);
    temp8blen = scale(4, bd, pe[2], temp8b);
    temp16len =
      sum(temp8alen, temp8a, temp8blen, temp8b,
          temp16);
    temp8alen = scale(4, de, pb[2], temp8a);
    deblen =
      sum(temp8alen, temp8a, temp16len, temp16,
          deb);

    temp8alen = scale(4, ac, pe[2], temp8a);
    temp8blen = scale(4, ce, pa[2], temp8b);
    temp16len =
      sum(temp8alen, temp8a, temp8blen, temp8b,
          temp16);
    temp8alen = scale(4, ea, pc[2], temp8a);
    eaclen =
      sum(temp8alen, temp8a, temp16len, temp16,
          eac);

    temp48alen =
      sum(cdelen, cde, bcelen, bce, temp48a);
    temp48blen =
      sum(deblen, deb, bcdlen, bcd, temp48b);
    for (i = 0; i < temp48blen; i++) {
      temp48b[i] = -temp48b[i];
    }
    bcdelen = sum(temp48alen, temp48a,
                  temp48blen, temp48b, bcde);
    xlen = scale(bcdelen, bcde, pa[0], temp192);
    xlen = scale(xlen, temp192, pa[0], det384x);
    ylen = scale(bcdelen, bcde, pa[1], temp192);
    ylen = scale(ylen, temp192, pa[1], det384y);
    zlen = scale(bcdelen, bcde, pa[2], temp192);
    zlen = scale(zlen, temp192, pa[2], det384z);
    xylen =
      sum(xlen, det384x, ylen, det384y, detxy);
    alen =
      sum(xylen, detxy, zlen, det384z, adet);

    temp48alen =
      sum(dealen, dea, cdalen, cda, temp48a);
    temp48blen =
      sum(eaclen, eac, cdelen, cde, temp48b);
    for (i = 0; i < temp48blen; i++) {
      temp48b[i] = -temp48b[i];
    }
    cdealen = sum(temp48alen, temp48a,
                  temp48blen, temp48b, cdea);
    xlen = scale(cdealen, cdea, pb[0], temp192);
    xlen = scale(xlen, temp192, pb[0], det384x);
    ylen = scale(cdealen, cdea, pb[1], temp192);
    ylen = scale(ylen, temp192, pb[1], det384y);
    zlen = scale(cdealen, cdea, pb[2], temp192);
    zlen = scale(zlen, temp192, pb[2], det384z);
    xylen =
      sum(xlen, det384x, ylen, det384y, detxy);
    blen =
      sum(xylen, detxy, zlen, det384z, bdet);

    temp48alen =
      sum(eablen, eab, deblen, deb, temp48a);
    temp48blen =
      sum(abdlen, abd, dealen, dea, temp48b);
    for (i = 0; i < temp48blen; i++) {
      temp48b[i] = -temp48b[i];
    }
    deablen = sum(temp48alen, temp48a,
                  temp48blen, temp48b, deab);
    xlen = scale(deablen, deab, pc[0], temp192);
    xlen = scale(xlen, temp192, pc[0], det384x);
    ylen = scale(deablen, deab, pc[1], temp192);
    ylen = scale(ylen, temp192, pc[1], det384y);
    zlen = scale(deablen, deab, pc[2], temp192);
    zlen = scale(zlen, temp192, pc[2], det384z);
    xylen =
      sum(xlen, det384x, ylen, det384y, detxy);
    clen =
      sum(xylen, detxy, zlen, det384z, cdet);

    temp48alen =
      sum(abclen, abc, eaclen, eac, temp48a);
    temp48blen =
      sum(bcelen, bce, eablen, eab, temp48b);
    for (i = 0; i < temp48blen; i++) {
      temp48b[i] = -temp48b[i];
    }
    eabclen = sum(temp48alen, temp48a,
                  temp48blen, temp48b, eabc);
    xlen = scale(eabclen, eabc, pd[0], temp192);
    xlen = scale(xlen, temp192, pd[0], det384x);
    ylen = scale(eabclen, eabc, pd[1], temp192);
    ylen = scale(ylen, temp192, pd[1], det384y);
    zlen = scale(eabclen, eabc, pd[2], temp192);
    zlen = scale(zlen, temp192, pd[2], det384z);
    xylen =
      sum(xlen, det384x, ylen, det384y, detxy);
    dlen =
      sum(xylen, detxy, zlen, det384z, ddet);

    temp48alen =
      sum(bcdlen, bcd, abdlen, abd, temp48a);
    temp48blen =
      sum(cdalen, cda, abclen, abc, temp48b);
    for (i = 0; i < temp48blen; i++) {
      temp48b[i] = -temp48b[i];
    }
    abcdlen = sum(temp48alen, temp48a,
                  temp48blen, temp48b, abcd);
    xlen = scale(abcdlen, abcd, pe[0], temp192);
    xlen = scale(xlen, temp192, pe[0], det384x);
    ylen = scale(abcdlen, abcd, pe[1], temp192);
    ylen = scale(ylen, temp192, pe[1], det384y);
    zlen = scale(abcdlen, abcd, pe[2], temp192);
    zlen = scale(zlen, temp192, pe[2], det384z);
    xylen =
      sum(xlen, det384x, ylen, det384y, detxy);
    elen =
      sum(xylen, detxy, zlen, det384z, edet);

    ablen = sum(alen, adet, blen, bdet, abdet);
    cdlen = sum(clen, cdet, dlen, ddet, cddet);
    cdelen =
      sum(cdlen, cddet, elen, edet, cdedet);
    deterlen =
      sum(ablen, abdet, cdelen, cdedet, deter);

    return deter[deterlen - 1];
  }

  //--------------------------------------------------------------------
  public static final double insphereslow (final double[] pa,
                                           final double[] pb,
                                           final double[] pc,
                                           final double[] pd,
                                           final double[] pe) {
    double aex, bex, cex, dex, aey, bey, cey, dey, aez, bez, cez, dez;
    double aextail, bextail, cextail, dextail;
    double aeytail, beytail, ceytail, deytail;
    double aeztail, beztail, ceztail, deztail;
    double negate, negatetail;
    double axby7, bxcy7, cxdy7, dxay7, axcy7, bxdy7;
    double bxay7, cxby7, dxcy7, axdy7, cxay7, dxby7;
    double[] axby = new double[8], bxcy = new double[8],
      cxdy = new double[8], dxay = new double[8], axcy = new double[8],
      bxdy = new double[8];
    double[] bxay = new double[8], cxby = new double[8],
      dxcy = new double[8], axdy = new double[8], cxay = new double[8],
      dxby = new double[8];
    double[] ab = new double[16], bc = new double[16], cd =
      new double[16],
      da = new double[16], ac = new double[16], bd = new double[16];
    int ablen, bclen, cdlen, dalen, aclen, bdlen;
    double[] temp32a = new double[32],
      temp32b = new double[32], temp64a = new double[64],
      temp64b = new double[64], temp64c = new double[64];
    int temp32alen, temp32blen, temp64alen, temp64blen, temp64clen;
    double[] temp128 = new double[128], temp192 = new double[192];
    int temp128len, temp192len;
    double[] detx = new double[384], detxx = new double[768],
      detxt = new double[384], detxxt = new double[768],
      detxtxt = new double[768];
    int xlen, xxlen, xtlen, xxtlen, xtxtlen;
    double[] x1 = new double[1536], x2 = new double[2304];
    int x1len, x2len;
    double[] dety = new double[384], detyy = new double[768],
      detyt = new double[384], detyyt = new double[768],
      detytyt = new double[768];
    int ylen, yylen, ytlen, yytlen, ytytlen;
    double[] y1 = new double[1536], y2 = new double[2304];
    int y1len, y2len;
    double[] detz = new double[384], detzz = new double[768],
      detzt = new double[384], detzzt = new double[768],
      detztzt = new double[768];
    int zlen, zzlen, ztlen, zztlen, ztztlen;
    double[] z1 = new double[1536], z2 = new double[2304];
    int z1len, z2len;
    double[] detxy = new double[4608];
    int xylen;
    double[] adet = new double[6912], bdet = new double[6912],
      cdet = new double[6912], ddet = new double[6912];
    int alen, blen, clen, dlen;
    double[] abdet = new double[13824], cddet = new double[13824],
      deter = new double[27648];
    int deterlen;
    int i;

    double bvirt;
    double avirt, bround, around;
    double c;
    double abig;
    double a0hi, a0lo, a1hi, a1lo, bhi, blo;
    double err1, err2, err3;
    double _i, _j, _k, _l, _m, _n;
    double _0, _1, _2;

    aex =   (pa[0] - pe[0]); bvirt =   (pa[0] - aex);
    avirt = aex + bvirt; bround = bvirt - pe[0]; around = pa[0] - avirt;
    aextail = around + bround;
    aey =   (pa[1] - pe[1]); bvirt =   (pa[1] - aey);
    avirt = aey + bvirt; bround = bvirt - pe[1]; around = pa[1] - avirt;
    aeytail = around + bround;
    aez =   (pa[2] - pe[2]); bvirt =   (pa[2] - aez);
    avirt = aez + bvirt; bround = bvirt - pe[2]; around = pa[2] - avirt;
    aeztail = around + bround;
    bex =   (pb[0] - pe[0]); bvirt =   (pb[0] - bex);
    avirt = bex + bvirt; bround = bvirt - pe[0]; around = pb[0] - avirt;
    bextail = around + bround;
    bey =   (pb[1] - pe[1]); bvirt =   (pb[1] - bey);
    avirt = bey + bvirt; bround = bvirt - pe[1]; around = pb[1] - avirt;
    beytail = around + bround;
    bez =   (pb[2] - pe[2]); bvirt =   (pb[2] - bez);
    avirt = bez + bvirt; bround = bvirt - pe[2]; around = pb[2] - avirt;
    beztail = around + bround;
    cex =   (pc[0] - pe[0]); bvirt =   (pc[0] - cex);
    avirt = cex + bvirt; bround = bvirt - pe[0]; around = pc[0] - avirt;
    cextail = around + bround;
    cey =   (pc[1] - pe[1]); bvirt =   (pc[1] - cey);
    avirt = cey + bvirt; bround = bvirt - pe[1]; around = pc[1] - avirt;
    ceytail = around + bround;
    cez =   (pc[2] - pe[2]); bvirt =   (pc[2] - cez);
    avirt = cez + bvirt; bround = bvirt - pe[2]; around = pc[2] - avirt;
    ceztail = around + bround;
    dex =   (pd[0] - pe[0]); bvirt =   (pd[0] - dex);
    avirt = dex + bvirt; bround = bvirt - pe[0]; around = pd[0] - avirt;
    dextail = around + bround;
    dey =   (pd[1] - pe[1]); bvirt =   (pd[1] - dey);
    avirt = dey + bvirt; bround = bvirt - pe[1]; around = pd[1] - avirt;
    deytail = around + bround;
    dez =   (pd[2] - pe[2]); bvirt =   (pd[2] - dez);
    avirt = dez + bvirt; bround = bvirt - pe[2]; around = pd[2] - avirt;
    deztail = around + bround;

    c =   (SPLITTER * aextail); abig =   (c - aextail);
    a0hi = c - abig; a0lo = aextail - a0hi;
    c =   (SPLITTER * beytail); abig =   (c - beytail);
    bhi = c - abig; blo = beytail - bhi;
    _i =   (aextail * beytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    axby[0] = (a0lo * blo) - err3; c =   (SPLITTER * aex);
    abig =   (c - aex); a1hi = c - abig; a1lo = aex - a1hi;
    _j =   (aex * beytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * bey);
    abig =   (c - bey); bhi = c - abig; blo = bey - bhi;
    _i =   (aextail * bey); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axby[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (aex * bey); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axby[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axby[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; axby[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    axby[5] = around + bround; axby7 =   (_m + _k);
    bvirt =   (axby7 - _m); avirt = axby7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; axby[6] = around + bround

    ;
    axby[7] = axby7;
    negate = -aey;
    negatetail = -aeytail;
    c =   (SPLITTER * bextail); abig =   (c - bextail);
    a0hi = c - abig; a0lo = bextail - a0hi;
    c =   (SPLITTER * negatetail);
    abig =   (c - negatetail); bhi = c - abig;
    blo = negatetail - bhi; _i =   (bextail * negatetail);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); bxay[0] = (a0lo * blo) - err3;
    c =   (SPLITTER * bex); abig =   (c - bex);
    a1hi = c - abig; a1lo = bex - a1hi;
    _j =   (bex * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * negate);
    abig =   (c - negate); bhi = c - abig; blo = negate - bhi;
    _i =   (bextail * negate); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxay[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (bex * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxay[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxay[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; bxay[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    bxay[5] = around + bround; bxay7 =   (_m + _k);
    bvirt =   (bxay7 - _m); avirt = bxay7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; bxay[6] = around + bround

    ;
    bxay[7] = bxay7;
    ablen = sum(8, axby, 8, bxay, ab);
    c =   (SPLITTER * bextail); abig =   (c - bextail);
    a0hi = c - abig; a0lo = bextail - a0hi;
    c =   (SPLITTER * ceytail); abig =   (c - ceytail);
    bhi = c - abig; blo = ceytail - bhi;
    _i =   (bextail * ceytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    bxcy[0] = (a0lo * blo) - err3; c =   (SPLITTER * bex);
    abig =   (c - bex); a1hi = c - abig; a1lo = bex - a1hi;
    _j =   (bex * ceytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * cey);
    abig =   (c - cey); bhi = c - abig; blo = cey - bhi;
    _i =   (bextail * cey); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxcy[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (bex * cey); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxcy[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxcy[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; bxcy[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    bxcy[5] = around + bround; bxcy7 =   (_m + _k);
    bvirt =   (bxcy7 - _m); avirt = bxcy7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; bxcy[6] = around + bround

    ;
    bxcy[7] = bxcy7;
    negate = -bey;
    negatetail = -beytail;
    c =   (SPLITTER * cextail); abig =   (c - cextail);
    a0hi = c - abig; a0lo = cextail - a0hi;
    c =   (SPLITTER * negatetail);
    abig =   (c - negatetail); bhi = c - abig;
    blo = negatetail - bhi; _i =   (cextail * negatetail);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); cxby[0] = (a0lo * blo) - err3;
    c =   (SPLITTER * cex); abig =   (c - cex);
    a1hi = c - abig; a1lo = cex - a1hi;
    _j =   (cex * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * negate);
    abig =   (c - negate); bhi = c - abig; blo = negate - bhi;
    _i =   (cextail * negate); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxby[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (cex * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxby[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxby[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; cxby[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    cxby[5] = around + bround; cxby7 =   (_m + _k);
    bvirt =   (cxby7 - _m); avirt = cxby7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; cxby[6] = around + bround

    ;
    cxby[7] = cxby7;
    bclen = sum(8, bxcy, 8, cxby, bc);
    c =   (SPLITTER * cextail); abig =   (c - cextail);
    a0hi = c - abig; a0lo = cextail - a0hi;
    c =   (SPLITTER * deytail); abig =   (c - deytail);
    bhi = c - abig; blo = deytail - bhi;
    _i =   (cextail * deytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    cxdy[0] = (a0lo * blo) - err3; c =   (SPLITTER * cex);
    abig =   (c - cex); a1hi = c - abig; a1lo = cex - a1hi;
    _j =   (cex * deytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * dey);
    abig =   (c - dey); bhi = c - abig; blo = dey - bhi;
    _i =   (cextail * dey); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxdy[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (cex * dey); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxdy[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxdy[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; cxdy[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    cxdy[5] = around + bround; cxdy7 =   (_m + _k);
    bvirt =   (cxdy7 - _m); avirt = cxdy7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; cxdy[6] = around + bround

    ;
    cxdy[7] = cxdy7;
    negate = -cey;
    negatetail = -ceytail;
    c =   (SPLITTER * dextail); abig =   (c - dextail);
    a0hi = c - abig; a0lo = dextail - a0hi;
    c =   (SPLITTER * negatetail);
    abig =   (c - negatetail); bhi = c - abig;
    blo = negatetail - bhi; _i =   (dextail * negatetail);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); dxcy[0] = (a0lo * blo) - err3;
    c =   (SPLITTER * dex); abig =   (c - dex);
    a1hi = c - abig; a1lo = dex - a1hi;
    _j =   (dex * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * negate);
    abig =   (c - negate); bhi = c - abig; blo = negate - bhi;
    _i =   (dextail * negate); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; dxcy[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (dex * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; dxcy[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; dxcy[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; dxcy[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    dxcy[5] = around + bround; dxcy7 =   (_m + _k);
    bvirt =   (dxcy7 - _m); avirt = dxcy7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; dxcy[6] = around + bround

    ;
    dxcy[7] = dxcy7;
    cdlen = sum(8, cxdy, 8, dxcy, cd);
    c =   (SPLITTER * dextail); abig =   (c - dextail);
    a0hi = c - abig; a0lo = dextail - a0hi;
    c =   (SPLITTER * aeytail); abig =   (c - aeytail);
    bhi = c - abig; blo = aeytail - bhi;
    _i =   (dextail * aeytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    dxay[0] = (a0lo * blo) - err3; c =   (SPLITTER * dex);
    abig =   (c - dex); a1hi = c - abig; a1lo = dex - a1hi;
    _j =   (dex * aeytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * aey);
    abig =   (c - aey); bhi = c - abig; blo = aey - bhi;
    _i =   (dextail * aey); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; dxay[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (dex * aey); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; dxay[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; dxay[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; dxay[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    dxay[5] = around + bround; dxay7 =   (_m + _k);
    bvirt =   (dxay7 - _m); avirt = dxay7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; dxay[6] = around + bround

    ;
    dxay[7] = dxay7;
    negate = -dey;
    negatetail = -deytail;
    c =   (SPLITTER * aextail); abig =   (c - aextail);
    a0hi = c - abig; a0lo = aextail - a0hi;
    c =   (SPLITTER * negatetail);
    abig =   (c - negatetail); bhi = c - abig;
    blo = negatetail - bhi; _i =   (aextail * negatetail);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); axdy[0] = (a0lo * blo) - err3;
    c =   (SPLITTER * aex); abig =   (c - aex);
    a1hi = c - abig; a1lo = aex - a1hi;
    _j =   (aex * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * negate);
    abig =   (c - negate); bhi = c - abig; blo = negate - bhi;
    _i =   (aextail * negate); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axdy[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (aex * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axdy[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axdy[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; axdy[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    axdy[5] = around + bround; axdy7 =   (_m + _k);
    bvirt =   (axdy7 - _m); avirt = axdy7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; axdy[6] = around + bround

    ;
    axdy[7] = axdy7;
    dalen = sum(8, dxay, 8, axdy, da);
    c =   (SPLITTER * aextail); abig =   (c - aextail);
    a0hi = c - abig; a0lo = aextail - a0hi;
    c =   (SPLITTER * ceytail); abig =   (c - ceytail);
    bhi = c - abig; blo = ceytail - bhi;
    _i =   (aextail * ceytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    axcy[0] = (a0lo * blo) - err3; c =   (SPLITTER * aex);
    abig =   (c - aex); a1hi = c - abig; a1lo = aex - a1hi;
    _j =   (aex * ceytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * cey);
    abig =   (c - cey); bhi = c - abig; blo = cey - bhi;
    _i =   (aextail * cey); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axcy[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (aex * cey); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axcy[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; axcy[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; axcy[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    axcy[5] = around + bround; axcy7 =   (_m + _k);
    bvirt =   (axcy7 - _m); avirt = axcy7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; axcy[6] = around + bround

    ;
    axcy[7] = axcy7;
    negate = -aey;
    negatetail = -aeytail;
    c =   (SPLITTER * cextail); abig =   (c - cextail);
    a0hi = c - abig; a0lo = cextail - a0hi;
    c =   (SPLITTER * negatetail);
    abig =   (c - negatetail); bhi = c - abig;
    blo = negatetail - bhi; _i =   (cextail * negatetail);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); cxay[0] = (a0lo * blo) - err3;
    c =   (SPLITTER * cex); abig =   (c - cex);
    a1hi = c - abig; a1lo = cex - a1hi;
    _j =   (cex * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * negate);
    abig =   (c - negate); bhi = c - abig; blo = negate - bhi;
    _i =   (cextail * negate); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxay[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (cex * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxay[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; cxay[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; cxay[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    cxay[5] = around + bround; cxay7 =   (_m + _k);
    bvirt =   (cxay7 - _m); avirt = cxay7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; cxay[6] = around + bround

    ;
    cxay[7] = cxay7;
    aclen = sum(8, axcy, 8, cxay, ac);
    c =   (SPLITTER * bextail); abig =   (c - bextail);
    a0hi = c - abig; a0lo = bextail - a0hi;
    c =   (SPLITTER * deytail); abig =   (c - deytail);
    bhi = c - abig; blo = deytail - bhi;
    _i =   (bextail * deytail); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    bxdy[0] = (a0lo * blo) - err3; c =   (SPLITTER * bex);
    abig =   (c - bex); a1hi = c - abig; a1lo = bex - a1hi;
    _j =   (bex * deytail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * dey);
    abig =   (c - dey); bhi = c - abig; blo = dey - bhi;
    _i =   (bextail * dey); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxdy[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (bex * dey); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxdy[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; bxdy[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; bxdy[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    bxdy[5] = around + bround; bxdy7 =   (_m + _k);
    bvirt =   (bxdy7 - _m); avirt = bxdy7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; bxdy[6] = around + bround

    ;
    bxdy[7] = bxdy7;
    negate = -bey;
    negatetail = -beytail;
    c =   (SPLITTER * dextail); abig =   (c - dextail);
    a0hi = c - abig; a0lo = dextail - a0hi;
    c =   (SPLITTER * negatetail);
    abig =   (c - negatetail); bhi = c - abig;
    blo = negatetail - bhi; _i =   (dextail * negatetail);
    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
    err3 = err2 - (a0hi * blo); dxby[0] = (a0lo * blo) - err3;
    c =   (SPLITTER * dex); abig =   (c - dex);
    a1hi = c - abig; a1lo = dex - a1hi;
    _j =   (dex * negatetail); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _k =   (_i + _0);
    bvirt =   (_k - _i); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _1 = around + bround; _l =   (_j + _k);
    bvirt = _l - _j; _2 = _k - bvirt; c =   (SPLITTER * negate);
    abig =   (c - negate); bhi = c - abig; blo = negate - bhi;
    _i =   (dextail * negate); err1 = _i - (a0hi * bhi);
    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
    _0 = (a0lo * blo) - err3; _k =   (_1 + _0);
    bvirt =   (_k - _1); avirt = _k - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; dxby[1] = around + bround;
    _j =   (_2 + _k); bvirt =   (_j - _2);
    avirt = _j - bvirt; bround = _k - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _j);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _j - bvirt;
    around = _l - avirt; _2 = around + bround;
    _j =   (dex * negate); err1 = _j - (a1hi * bhi);
    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
    _0 = (a1lo * blo) - err3; _n =   (_i + _0);
    bvirt =   (_n - _i); avirt = _n - bvirt; bround = _0 - bvirt;
    around = _i - avirt; _0 = around + bround; _i =   (_1 + _0);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; dxby[2] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    _1 = around + bround; _l =   (_m + _k);
    bvirt =   (_l - _m); avirt = _l - bvirt; bround = _k - bvirt;
    around = _m - avirt; _2 = around + bround; _k =   (_j + _n);
    bvirt =   (_k - _j); avirt = _k - bvirt; bround = _n - bvirt;
    around = _j - avirt; _0 = around + bround; _j =   (_1 + _0);
    bvirt =   (_j - _1); avirt = _j - bvirt; bround = _0 - bvirt;
    around = _1 - avirt; dxby[3] = around + bround;
    _i =   (_2 + _j); bvirt =   (_i - _2);
    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
    _1 = around + bround; _m =   (_l + _i);
    bvirt =   (_m - _l); avirt = _m - bvirt; bround = _i - bvirt;
    around = _l - avirt; _2 = around + bround; _i =   (_1 + _k);
    bvirt =   (_i - _1); avirt = _i - bvirt; bround = _k - bvirt;
    around = _1 - avirt; dxby[4] = around + bround;
    _k =   (_2 + _i); bvirt =   (_k - _2);
    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
    dxby[5] = around + bround; dxby7 =   (_m + _k);
    bvirt =   (dxby7 - _m); avirt = dxby7 - bvirt;
    bround = _k - bvirt; around = _m - avirt; dxby[6] = around + bround

    ;
    dxby[7] = dxby7;
    bdlen = sum(8, bxdy, 8, dxby, bd);

    temp32alen = scale(cdlen, cd, -bez, temp32a);
    temp32blen = scale(cdlen, cd, -beztail, temp32b);
    temp64alen = sum(temp32alen, temp32a,
                     temp32blen, temp32b,
                     temp64a);
    temp32alen = scale(bdlen, bd, cez, temp32a);
    temp32blen = scale(bdlen, bd, ceztail, temp32b);
    temp64blen = sum(temp32alen, temp32a,
                     temp32blen, temp32b,
                     temp64b);
    temp32alen = scale(bclen, bc, -dez, temp32a);
    temp32blen = scale(bclen, bc, -deztail, temp32b);
    temp64clen = sum(temp32alen, temp32a,
                     temp32blen, temp32b,
                     temp64c);
    temp128len = sum(temp64alen, temp64a,
                     temp64blen, temp64b,
                     temp128);
    temp192len = sum(temp64clen, temp64c,
                     temp128len, temp128,
                     temp192);
    xlen = scale(temp192len, temp192, aex, detx);
    xxlen = scale(xlen, detx, aex, detxx);
    xtlen =
      scale(temp192len, temp192, aextail, detxt);
    xxtlen = scale(xtlen, detxt, aex, detxxt);
    for (i = 0; i < xxtlen; i++) {
      detxxt[i] *= 2.0;
    }
    xtxtlen = scale(xtlen, detxt, aextail, detxtxt);
    x1len =
      sum(xxlen, detxx, xxtlen, detxxt, x1);
    x2len =
      sum(x1len, x1, xtxtlen, detxtxt, x2);
    ylen = scale(temp192len, temp192, aey, dety);
    yylen = scale(ylen, dety, aey, detyy);
    ytlen =
      scale(temp192len, temp192, aeytail, detyt);
    yytlen = scale(ytlen, detyt, aey, detyyt);
    for (i = 0; i < yytlen; i++) {
      detyyt[i] *= 2.0;
    }
    ytytlen = scale(ytlen, detyt, aeytail, detytyt);
    y1len =
      sum(yylen, detyy, yytlen, detyyt, y1);
    y2len =
      sum(y1len, y1, ytytlen, detytyt, y2);
    zlen = scale(temp192len, temp192, aez, detz);
    zzlen = scale(zlen, detz, aez, detzz);
    ztlen =
      scale(temp192len, temp192, aeztail, detzt);
    zztlen = scale(ztlen, detzt, aez, detzzt);
    for (i = 0; i < zztlen; i++) {
      detzzt[i] *= 2.0;
    }
    ztztlen = scale(ztlen, detzt, aeztail, detztzt);
    z1len =
      sum(zzlen, detzz, zztlen, detzzt, z1);
    z2len =
      sum(z1len, z1, ztztlen, detztzt, z2);
    xylen = sum(x2len, x2, y2len, y2, detxy);
    alen = sum(z2len, z2, xylen, detxy, adet);

    temp32alen = scale(dalen, da, cez, temp32a);
    temp32blen = scale(dalen, da, ceztail, temp32b);
    temp64alen = sum(temp32alen, temp32a,
                     temp32blen, temp32b,
                     temp64a);
    temp32alen = scale(aclen, ac, dez, temp32a);
    temp32blen = scale(aclen, ac, deztail, temp32b);
    temp64blen = sum(temp32alen, temp32a,
                     temp32blen, temp32b,
                     temp64b);
    temp32alen = scale(cdlen, cd, aez, temp32a);
    temp32blen = scale(cdlen, cd, aeztail, temp32b);
    temp64clen = sum(temp32alen, temp32a,
                     temp32blen, temp32b,
                     temp64c);
    temp128len = sum(temp64alen, temp64a,
                     temp64blen, temp64b,
                     temp128);
    temp192len = sum(temp64clen, temp64c,
                     temp128len, temp128,
                     temp192);
    xlen = scale(temp192len, temp192, bex, detx);
    xxlen = scale(xlen, detx, bex, detxx);
    xtlen =
      scale(temp192len, temp192, bextail, detxt);
    xxtlen = scale(xtlen, detxt, bex, detxxt);
    for (i = 0; i < xxtlen; i++) {
      detxxt[i] *= 2.0;
    }
    xtxtlen = scale(xtlen, detxt, bextail, detxtxt);
    x1len =
      sum(xxlen, detxx, xxtlen, detxxt, x1);
    x2len =
      sum(x1len, x1, xtxtlen, detxtxt, x2);
    ylen = scale(temp192len, temp192, bey, dety);
    yylen = scale(ylen, dety, bey, detyy);
    ytlen =
      scale(temp192len, temp192, beytail, detyt);
    yytlen = scale(ytlen, detyt, bey, detyyt);
    for (i = 0; i < yytlen; i++) {
      detyyt[i] *= 2.0;
    }
    ytytlen = scale(ytlen, detyt, beytail, detytyt);
    y1len =
      sum(yylen, detyy, yytlen, detyyt, y1);
    y2len =
      sum(y1len, y1, ytytlen, detytyt, y2);
    zlen = scale(temp192len, temp192, bez, detz);
    zzlen = scale(zlen, detz, bez, detzz);
    ztlen =
      scale(temp192len, temp192, beztail, detzt);
    zztlen = scale(ztlen, detzt, bez, detzzt);
    for (i = 0; i < zztlen; i++) {
      detzzt[i] *= 2.0;
    }
    ztztlen = scale(ztlen, detzt, beztail, detztzt);
    z1len =
      sum(zzlen, detzz, zztlen, detzzt, z1);
    z2len =
      sum(z1len, z1, ztztlen, detztzt, z2);
    xylen = sum(x2len, x2, y2len, y2, detxy);
    blen = sum(z2len, z2, xylen, detxy, bdet);

    temp32alen = scale(ablen, ab, -dez, temp32a);
    temp32blen = scale(ablen, ab, -deztail, temp32b);
    temp64alen = sum(temp32alen, temp32a,
                     temp32blen, temp32b,
                     temp64a);
    temp32alen = scale(bdlen, bd, -aez, temp32a);
    temp32blen = scale(bdlen, bd, -aeztail, temp32b);
    temp64blen = sum(temp32alen, temp32a,
                     temp32blen, temp32b,
                     temp64b);
    temp32alen = scale(dalen, da, -bez, temp32a);
    temp32blen = scale(dalen, da, -beztail, temp32b);
    temp64clen = sum(temp32alen, temp32a,
                     temp32blen, temp32b,
                     temp64c);
    temp128len = sum(temp64alen, temp64a,
                     temp64blen, temp64b,
                     temp128);
    temp192len = sum(temp64clen, temp64c,
                     temp128len, temp128,
                     temp192);
    xlen = scale(temp192len, temp192, cex, detx);
    xxlen = scale(xlen, detx, cex, detxx);
    xtlen =
      scale(temp192len, temp192, cextail, detxt);
    xxtlen = scale(xtlen, detxt, cex, detxxt);
    for (i = 0; i < xxtlen; i++) {
      detxxt[i] *= 2.0;
    }
    xtxtlen = scale(xtlen, detxt, cextail, detxtxt);
    x1len =
      sum(xxlen, detxx, xxtlen, detxxt, x1);
    x2len =
      sum(x1len, x1, xtxtlen, detxtxt, x2);
    ylen = scale(temp192len, temp192, cey, dety);
    yylen = scale(ylen, dety, cey, detyy);
    ytlen =
      scale(temp192len, temp192, ceytail, detyt);
    yytlen = scale(ytlen, detyt, cey, detyyt);
    for (i = 0; i < yytlen; i++) {
      detyyt[i] *= 2.0;
    }
    ytytlen = scale(ytlen, detyt, ceytail, detytyt);
    y1len =
      sum(yylen, detyy, yytlen, detyyt, y1);
    y2len =
      sum(y1len, y1, ytytlen, detytyt, y2);
    zlen = scale(temp192len, temp192, cez, detz);
    zzlen = scale(zlen, detz, cez, detzz);
    ztlen =
      scale(temp192len, temp192, ceztail, detzt);
    zztlen = scale(ztlen, detzt, cez, detzzt);
    for (i = 0; i < zztlen; i++) {
      detzzt[i] *= 2.0;
    }
    ztztlen = scale(ztlen, detzt, ceztail, detztzt);
    z1len =
      sum(zzlen, detzz, zztlen, detzzt, z1);
    z2len =
      sum(z1len, z1, ztztlen, detztzt, z2);
    xylen = sum(x2len, x2, y2len, y2, detxy);
    clen = sum(z2len, z2, xylen, detxy, cdet);

    temp32alen = scale(bclen, bc, aez, temp32a);
    temp32blen = scale(bclen, bc, aeztail, temp32b);
    temp64alen = sum(temp32alen, temp32a,
                     temp32blen, temp32b,
                     temp64a);
    temp32alen = scale(aclen, ac, -bez, temp32a);
    temp32blen = scale(aclen, ac, -beztail, temp32b);
    temp64blen = sum(temp32alen, temp32a,
                     temp32blen, temp32b,
                     temp64b);
    temp32alen = scale(ablen, ab, cez, temp32a);
    temp32blen = scale(ablen, ab, ceztail, temp32b);
    temp64clen = sum(temp32alen, temp32a,
                     temp32blen, temp32b,
                     temp64c);
    temp128len = sum(temp64alen, temp64a,
                     temp64blen, temp64b,
                     temp128);
    temp192len = sum(temp64clen, temp64c,
                     temp128len, temp128,
                     temp192);
    xlen = scale(temp192len, temp192, dex, detx);
    xxlen = scale(xlen, detx, dex, detxx);
    xtlen =
      scale(temp192len, temp192, dextail, detxt);
    xxtlen = scale(xtlen, detxt, dex, detxxt);
    for (i = 0; i < xxtlen; i++) {
      detxxt[i] *= 2.0;
    }
    xtxtlen = scale(xtlen, detxt, dextail, detxtxt);
    x1len =
      sum(xxlen, detxx, xxtlen, detxxt, x1);
    x2len =
      sum(x1len, x1, xtxtlen, detxtxt, x2);
    ylen = scale(temp192len, temp192, dey, dety);
    yylen = scale(ylen, dety, dey, detyy);
    ytlen =
      scale(temp192len, temp192, deytail, detyt);
    yytlen = scale(ytlen, detyt, dey, detyyt);
    for (i = 0; i < yytlen; i++) {
      detyyt[i] *= 2.0;
    }
    ytytlen = scale(ytlen, detyt, deytail, detytyt);
    y1len =
      sum(yylen, detyy, yytlen, detyyt, y1);
    y2len =
      sum(y1len, y1, ytytlen, detytyt, y2);
    zlen = scale(temp192len, temp192, dez, detz);
    zzlen = scale(zlen, detz, dez, detzz);
    ztlen =
      scale(temp192len, temp192, deztail, detzt);
    zztlen = scale(ztlen, detzt, dez, detzzt);
    for (i = 0; i < zztlen; i++) {
      detzzt[i] *= 2.0;
    }
    ztztlen = scale(ztlen, detzt, deztail, detztzt);
    z1len =
      sum(zzlen, detzz, zztlen, detzzt, z1);
    z2len =
      sum(z1len, z1, ztztlen, detztzt, z2);
    xylen = sum(x2len, x2, y2len, y2, detxy);
    dlen = sum(z2len, z2, xylen, detxy, ddet);

    ablen = sum(alen, adet, blen, bdet, abdet);
    cdlen = sum(clen, cdet, dlen, ddet, cddet);
    deterlen =
      sum(ablen, abdet, cdlen, cddet, deter);

    return deter[deterlen - 1];
  }

  //--------------------------------------------------------------------
  // inSphere
  //--------------------------------------------------------------------
  private static final double isperrboundB =
    (5.0 + 72.0 * EPSILON) * EPSILON;
  private static final double isperrboundC =
    (71.0 + 1408.0 * EPSILON) * EPSILON * EPSILON;

  public static final double insphereadapt (final double[] pa,
                                            final double[] pb,
                                            final double[] pc,
                                            final double[] pd,
                                            final double[] pe,
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

    aex =   (pa[0] - pe[0]);
    bex =   (pb[0] - pe[0]);
    cex =   (pc[0] - pe[0]);
    dex =   (pd[0] - pe[0]);
    aey =   (pa[1] - pe[1]);
    bey =   (pb[1] - pe[1]);
    cey =   (pc[1] - pe[1]);
    dey =   (pd[1] - pe[1]);
    aez =   (pa[2] - pe[2]);
    bez =   (pb[2] - pe[2]);
    cez =   (pc[2] - pe[2]);
    dez =   (pd[2] - pe[2]);

    aexbey1 =   (aex * bey); c =   (SPLITTER * aex);
    abig =   (c - aex); ahi = c - abig; alo = aex - ahi;
    c =   (SPLITTER * bey); abig =   (c - bey);
    bhi = c - abig; blo = bey - bhi; err1 = aexbey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    aexbey0 = (alo * blo) - err3;
    bexaey1 =   (bex * aey); c =   (SPLITTER * bex);
    abig =   (c - bex); ahi = c - abig; alo = bex - ahi;
    c =   (SPLITTER * aey); abig =   (c - aey);
    bhi = c - abig; blo = aey - bhi; err1 = bexaey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bexaey0 = (alo * blo) - err3;
    _i =   (aexbey0 - bexaey0); bvirt =   (aexbey0 - _i);
    avirt = _i + bvirt; bround = bvirt - bexaey0;
    around = aexbey0 - avirt; ab[0] = around + bround;
    _j =   (aexbey1 + _i); bvirt =   (_j - aexbey1);
    avirt = _j - bvirt; bround = _i - bvirt; around = aexbey1 - avirt;
    _0 = around + bround; _i =   (_0 - bexaey1);
    bvirt =   (_0 - _i); avirt = _i + bvirt;
    bround = bvirt - bexaey1; around = _0 - avirt;
    ab[1] = around + bround; ab3 =   (_j + _i);
    bvirt =   (ab3 - _j); avirt = ab3 - bvirt;
    bround = _i - bvirt; around = _j - avirt; ab[2] = around + bround;
    ab[3] = ab3;

    bexcey1 =   (bex * cey); c =   (SPLITTER * bex);
    abig =   (c - bex); ahi = c - abig; alo = bex - ahi;
    c =   (SPLITTER * cey); abig =   (c - cey);
    bhi = c - abig; blo = cey - bhi; err1 = bexcey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bexcey0 = (alo * blo) - err3;
    cexbey1 =   (cex * bey); c =   (SPLITTER * cex);
    abig =   (c - cex); ahi = c - abig; alo = cex - ahi;
    c =   (SPLITTER * bey); abig =   (c - bey);
    bhi = c - abig; blo = bey - bhi; err1 = cexbey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cexbey0 = (alo * blo) - err3;
    _i =   (bexcey0 - cexbey0); bvirt =   (bexcey0 - _i);
    avirt = _i + bvirt; bround = bvirt - cexbey0;
    around = bexcey0 - avirt; bc[0] = around + bround;
    _j =   (bexcey1 + _i); bvirt =   (_j - bexcey1);
    avirt = _j - bvirt; bround = _i - bvirt; around = bexcey1 - avirt;
    _0 = around + bround; _i =   (_0 - cexbey1);
    bvirt =   (_0 - _i); avirt = _i + bvirt;
    bround = bvirt - cexbey1; around = _0 - avirt;
    bc[1] = around + bround; bc3 =   (_j + _i);
    bvirt =   (bc3 - _j); avirt = bc3 - bvirt;
    bround = _i - bvirt; around = _j - avirt; bc[2] = around + bround;
    bc[3] = bc3;

    cexdey1 =   (cex * dey); c =   (SPLITTER * cex);
    abig =   (c - cex); ahi = c - abig; alo = cex - ahi;
    c =   (SPLITTER * dey); abig =   (c - dey);
    bhi = c - abig; blo = dey - bhi; err1 = cexdey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cexdey0 = (alo * blo) - err3;
    dexcey1 =   (dex * cey); c =   (SPLITTER * dex);
    abig =   (c - dex); ahi = c - abig; alo = dex - ahi;
    c =   (SPLITTER * cey); abig =   (c - cey);
    bhi = c - abig; blo = cey - bhi; err1 = dexcey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dexcey0 = (alo * blo) - err3;
    _i =   (cexdey0 - dexcey0); bvirt =   (cexdey0 - _i);
    avirt = _i + bvirt; bround = bvirt - dexcey0;
    around = cexdey0 - avirt; cd[0] = around + bround;
    _j =   (cexdey1 + _i); bvirt =   (_j - cexdey1);
    avirt = _j - bvirt; bround = _i - bvirt; around = cexdey1 - avirt;
    _0 = around + bround; _i =   (_0 - dexcey1);
    bvirt =   (_0 - _i); avirt = _i + bvirt;
    bround = bvirt - dexcey1; around = _0 - avirt;
    cd[1] = around + bround; cd3 =   (_j + _i);
    bvirt =   (cd3 - _j); avirt = cd3 - bvirt;
    bround = _i - bvirt; around = _j - avirt; cd[2] = around + bround;
    cd[3] = cd3;

    dexaey1 =   (dex * aey); c =   (SPLITTER * dex);
    abig =   (c - dex); ahi = c - abig; alo = dex - ahi;
    c =   (SPLITTER * aey); abig =   (c - aey);
    bhi = c - abig; blo = aey - bhi; err1 = dexaey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dexaey0 = (alo * blo) - err3;
    aexdey1 =   (aex * dey); c =   (SPLITTER * aex);
    abig =   (c - aex); ahi = c - abig; alo = aex - ahi;
    c =   (SPLITTER * dey); abig =   (c - dey);
    bhi = c - abig; blo = dey - bhi; err1 = aexdey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    aexdey0 = (alo * blo) - err3;
    _i =   (dexaey0 - aexdey0); bvirt =   (dexaey0 - _i);
    avirt = _i + bvirt; bround = bvirt - aexdey0;
    around = dexaey0 - avirt; da[0] = around + bround;
    _j =   (dexaey1 + _i); bvirt =   (_j - dexaey1);
    avirt = _j - bvirt; bround = _i - bvirt; around = dexaey1 - avirt;
    _0 = around + bround; _i =   (_0 - aexdey1);
    bvirt =   (_0 - _i); avirt = _i + bvirt;
    bround = bvirt - aexdey1; around = _0 - avirt;
    da[1] = around + bround; da3 =   (_j + _i);
    bvirt =   (da3 - _j); avirt = da3 - bvirt;
    bround = _i - bvirt; around = _j - avirt; da[2] = around + bround;
    da[3] = da3;

    aexcey1 =   (aex * cey); c =   (SPLITTER * aex);
    abig =   (c - aex); ahi = c - abig; alo = aex - ahi;
    c =   (SPLITTER * cey); abig =   (c - cey);
    bhi = c - abig; blo = cey - bhi; err1 = aexcey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    aexcey0 = (alo * blo) - err3;
    cexaey1 =   (cex * aey); c =   (SPLITTER * cex);
    abig =   (c - cex); ahi = c - abig; alo = cex - ahi;
    c =   (SPLITTER * aey); abig =   (c - aey);
    bhi = c - abig; blo = aey - bhi; err1 = cexaey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    cexaey0 = (alo * blo) - err3;
    _i =   (aexcey0 - cexaey0); bvirt =   (aexcey0 - _i);
    avirt = _i + bvirt; bround = bvirt - cexaey0;
    around = aexcey0 - avirt; ac[0] = around + bround;
    _j =   (aexcey1 + _i); bvirt =   (_j - aexcey1);
    avirt = _j - bvirt; bround = _i - bvirt; around = aexcey1 - avirt;
    _0 = around + bround; _i =   (_0 - cexaey1);
    bvirt =   (_0 - _i); avirt = _i + bvirt;
    bround = bvirt - cexaey1; around = _0 - avirt;
    ac[1] = around + bround; ac3 =   (_j + _i);
    bvirt =   (ac3 - _j); avirt = ac3 - bvirt;
    bround = _i - bvirt; around = _j - avirt; ac[2] = around + bround;
    ac[3] = ac3;

    bexdey1 =   (bex * dey); c =   (SPLITTER * bex);
    abig =   (c - bex); ahi = c - abig; alo = bex - ahi;
    c =   (SPLITTER * dey); abig =   (c - dey);
    bhi = c - abig; blo = dey - bhi; err1 = bexdey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    bexdey0 = (alo * blo) - err3;
    dexbey1 =   (dex * bey); c =   (SPLITTER * dex);
    abig =   (c - dex); ahi = c - abig; alo = dex - ahi;
    c =   (SPLITTER * bey); abig =   (c - bey);
    bhi = c - abig; blo = bey - bhi; err1 = dexbey1 - (ahi * bhi);
    err2 = err1 - (alo * bhi); err3 = err2 - (ahi * blo);
    dexbey0 = (alo * blo) - err3;
    _i =   (bexdey0 - dexbey0); bvirt =   (bexdey0 - _i);
    avirt = _i + bvirt; bround = bvirt - dexbey0;
    around = bexdey0 - avirt; bd[0] = around + bround;
    _j =   (bexdey1 + _i); bvirt =   (_j - bexdey1);
    avirt = _j - bvirt; bround = _i - bvirt; around = bexdey1 - avirt;
    _0 = around + bround; _i =   (_0 - dexbey1);
    bvirt =   (_0 - _i); avirt = _i + bvirt;
    bround = bvirt - dexbey1; around = _0 - avirt;
    bd[1] = around + bround; bd3 =   (_j + _i);
    bvirt =   (bd3 - _j); avirt = bd3 - bvirt;
    bround = _i - bvirt; around = _j - avirt; bd[2] = around + bround;
    bd[3] = bd3;

    temp8alen = scale(4, cd, bez, temp8a);
    temp8blen = scale(4, bd, -cez, temp8b);
    temp8clen = scale(4, bc, dez, temp8c);
    temp16len = sum(temp8alen, temp8a,
                    temp8blen, temp8b, temp16);
    temp24len = sum(temp8clen, temp8c,
                    temp16len, temp16, temp24);
    temp48len =
      scale(temp24len, temp24, aex, temp48);
    xlen = scale(temp48len, temp48, -aex, xdet);
    temp48len =
      scale(temp24len, temp24, aey, temp48);
    ylen = scale(temp48len, temp48, -aey, ydet);
    temp48len =
      scale(temp24len, temp24, aez, temp48);
    zlen = scale(temp48len, temp48, -aez, zdet);
    xylen = sum(xlen, xdet, ylen, ydet, xydet);
    alen = sum(xylen, xydet, zlen, zdet, adet);

    temp8alen = scale(4, da, cez, temp8a);
    temp8blen = scale(4, ac, dez, temp8b);
    temp8clen = scale(4, cd, aez, temp8c);
    temp16len = sum(temp8alen, temp8a,
                    temp8blen, temp8b, temp16);
    temp24len = sum(temp8clen, temp8c,
                    temp16len, temp16, temp24);
    temp48len =
      scale(temp24len, temp24, bex, temp48);
    xlen = scale(temp48len, temp48, bex, xdet);
    temp48len =
      scale(temp24len, temp24, bey, temp48);
    ylen = scale(temp48len, temp48, bey, ydet);
    temp48len =
      scale(temp24len, temp24, bez, temp48);
    zlen = scale(temp48len, temp48, bez, zdet);
    xylen = sum(xlen, xdet, ylen, ydet, xydet);
    blen = sum(xylen, xydet, zlen, zdet, bdet);

    temp8alen = scale(4, ab, dez, temp8a);
    temp8blen = scale(4, bd, aez, temp8b);
    temp8clen = scale(4, da, bez, temp8c);
    temp16len = sum(temp8alen, temp8a,
                    temp8blen, temp8b, temp16);
    temp24len = sum(temp8clen, temp8c,
                    temp16len, temp16, temp24);
    temp48len =
      scale(temp24len, temp24, cex, temp48);
    xlen = scale(temp48len, temp48, -cex, xdet);
    temp48len =
      scale(temp24len, temp24, cey, temp48);
    ylen = scale(temp48len, temp48, -cey, ydet);
    temp48len =
      scale(temp24len, temp24, cez, temp48);
    zlen = scale(temp48len, temp48, -cez, zdet);
    xylen = sum(xlen, xdet, ylen, ydet, xydet);
    clen = sum(xylen, xydet, zlen, zdet, cdet);

    temp8alen = scale(4, bc, aez, temp8a);
    temp8blen = scale(4, ac, -bez, temp8b);
    temp8clen = scale(4, ab, cez, temp8c);
    temp16len = sum(temp8alen, temp8a,
                    temp8blen, temp8b, temp16);
    temp24len = sum(temp8clen, temp8c,
                    temp16len, temp16, temp24);
    temp48len =
      scale(temp24len, temp24, dex, temp48);
    xlen = scale(temp48len, temp48, dex, xdet);
    temp48len =
      scale(temp24len, temp24, dey, temp48);
    ylen = scale(temp48len, temp48, dey, ydet);
    temp48len =
      scale(temp24len, temp24, dez, temp48);
    zlen = scale(temp48len, temp48, dez, zdet);
    xylen = sum(xlen, xdet, ylen, ydet, xydet);
    dlen = sum(xylen, xydet, zlen, zdet, ddet);

    ablen = sum(alen, adet, blen, bdet, abdet);
    cdlen = sum(clen, cdet, dlen, ddet, cddet);
    finlength =
      sum(ablen, abdet, cdlen, cddet, fin1);

    det = estimate(finlength, fin1);
    errbound = isperrboundB * permanent;
    if ((det >= errbound) || (-det >= errbound)) {
      return det;
    }

    bvirt =   (pa[0] - aex); avirt = aex + bvirt;
    bround = bvirt - pe[0]; around = pa[0] - avirt;
    aextail = around + bround;
    bvirt =   (pa[1] - aey); avirt = aey + bvirt;
    bround = bvirt - pe[1]; around = pa[1] - avirt;
    aeytail = around + bround;
    bvirt =   (pa[2] - aez); avirt = aez + bvirt;
    bround = bvirt - pe[2]; around = pa[2] - avirt;
    aeztail = around + bround;
    bvirt =   (pb[0] - bex); avirt = bex + bvirt;
    bround = bvirt - pe[0]; around = pb[0] - avirt;
    bextail = around + bround;
    bvirt =   (pb[1] - bey); avirt = bey + bvirt;
    bround = bvirt - pe[1]; around = pb[1] - avirt;
    beytail = around + bround;
    bvirt =   (pb[2] - bez); avirt = bez + bvirt;
    bround = bvirt - pe[2]; around = pb[2] - avirt;
    beztail = around + bround;
    bvirt =   (pc[0] - cex); avirt = cex + bvirt;
    bround = bvirt - pe[0]; around = pc[0] - avirt;
    cextail = around + bround;
    bvirt =   (pc[1] - cey); avirt = cey + bvirt;
    bround = bvirt - pe[1]; around = pc[1] - avirt;
    ceytail = around + bround;
    bvirt =   (pc[2] - cez); avirt = cez + bvirt;
    bround = bvirt - pe[2]; around = pc[2] - avirt;
    ceztail = around + bround;
    bvirt =   (pd[0] - dex); avirt = dex + bvirt;
    bround = bvirt - pe[0]; around = pd[0] - avirt;
    dextail = around + bround;
    bvirt =   (pd[1] - dey); avirt = dey + bvirt;
    bround = bvirt - pe[1]; around = pd[1] - avirt;
    deytail = around + bround;
    bvirt =   (pd[2] - dez); avirt = dez + bvirt;
    bround = bvirt - pe[2]; around = pd[2] - avirt;
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
    if ((det >= errbound) || (-det >= errbound)) {
      return det;
    }

    return insphereexact(pa, pb, pc, pd, pe);
  }

  //--------------------------------------------------------------------
  private static final double isperrboundA =
    (16.0 + 224.0 * EPSILON) * EPSILON;

  public static final double insphere (final double[] pa,
                                       final double[] pb,
                                       final double[] pc,
                                       final double[] pd,
                                       final double[] pe) {
    double aex, bex, cex, dex;
    double aey, bey, cey, dey;
    double aez, bez, cez, dez;
    double aexbey, bexaey, bexcey, cexbey, cexdey, dexcey, dexaey,
      aexdey;
    double aexcey, cexaey, bexdey, dexbey;
    double alift, blift, clift, dlift;
    double ab, bc, cd, da, ac, bd;
    double abc, bcd, cda, dab;
    double aezplus, bezplus, cezplus, dezplus;
    double aexbeyplus, bexaeyplus, bexceyplus, cexbeyplus;
    double cexdeyplus, dexceyplus, dexaeyplus, aexdeyplus;
    double aexceyplus, cexaeyplus, bexdeyplus, dexbeyplus;
    double det;
    double permanent, errbound;

    aex = pa[0] - pe[0];
    bex = pb[0] - pe[0];
    cex = pc[0] - pe[0];
    dex = pd[0] - pe[0];
    aey = pa[1] - pe[1];
    bey = pb[1] - pe[1];
    cey = pc[1] - pe[1];
    dey = pd[1] - pe[1];
    aez = pa[2] - pe[2];
    bez = pb[2] - pe[2];
    cez = pc[2] - pe[2];
    dez = pd[2] - pe[2];

    aexbey = aex * bey;
    bexaey = bex * aey;
    ab = aexbey - bexaey;
    bexcey = bex * cey;
    cexbey = cex * bey;
    bc = bexcey - cexbey;
    cexdey = cex * dey;
    dexcey = dex * cey;
    cd = cexdey - dexcey;
    dexaey = dex * aey;
    aexdey = aex * dey;
    da = dexaey - aexdey;

    aexcey = aex * cey;
    cexaey = cex * aey;
    ac = aexcey - cexaey;
    bexdey = bex * dey;
    dexbey = dex * bey;
    bd = bexdey - dexbey;

    abc = aez * bc - bez * ac + cez * ab;
    bcd = bez * cd - cez * bd + dez * bc;
    cda = cez * da + dez * ac + aez * cd;
    dab = dez * ab + aez * bd + bez * da;

    alift = aex * aex + aey * aey + aez * aez;
    blift = bex * bex + bey * bey + bez * bez;
    clift = cex * cex + cey * cey + cez * cez;
    dlift = dex * dex + dey * dey + dez * dez;

    det = (dlift * abc - clift * dab) + (blift * cda - alift * bcd);

    aezplus = ((aez) >= 0.0 ? (aez) : -(aez));
    bezplus = ((bez) >= 0.0 ? (bez) : -(bez));
    cezplus = ((cez) >= 0.0 ? (cez) : -(cez));
    dezplus = ((dez) >= 0.0 ? (dez) : -(dez));
    aexbeyplus = ((aexbey) >= 0.0 ? (aexbey) : -(aexbey));
    bexaeyplus = ((bexaey) >= 0.0 ? (bexaey) : -(bexaey));
    bexceyplus = ((bexcey) >= 0.0 ? (bexcey) : -(bexcey));
    cexbeyplus = ((cexbey) >= 0.0 ? (cexbey) : -(cexbey));
    cexdeyplus = ((cexdey) >= 0.0 ? (cexdey) : -(cexdey));
    dexceyplus = ((dexcey) >= 0.0 ? (dexcey) : -(dexcey));
    dexaeyplus = ((dexaey) >= 0.0 ? (dexaey) : -(dexaey));
    aexdeyplus = ((aexdey) >= 0.0 ? (aexdey) : -(aexdey));
    aexceyplus = ((aexcey) >= 0.0 ? (aexcey) : -(aexcey));
    cexaeyplus = ((cexaey) >= 0.0 ? (cexaey) : -(cexaey));
    bexdeyplus = ((bexdey) >= 0.0 ? (bexdey) : -(bexdey));
    dexbeyplus = ((dexbey) >= 0.0 ? (dexbey) : -(dexbey));
    permanent = ((cexdeyplus + dexceyplus) * bezplus
      + (dexbeyplus + bexdeyplus) * cezplus
      + (bexceyplus + cexbeyplus) * dezplus)
      * alift
      + ((dexaeyplus + aexdeyplus) * cezplus
      + (aexceyplus + cexaeyplus) * dezplus
      + (cexdeyplus + dexceyplus) * aezplus)
      * blift
      + ((aexbeyplus + bexaeyplus) * dezplus
      + (bexdeyplus + dexbeyplus) * aezplus
      + (dexaeyplus + aexdeyplus) * bezplus)
      * clift
      + ((bexceyplus + cexbeyplus) * aezplus
      + (cexaeyplus + aexceyplus) * bezplus
      + (aexbeyplus + bexaeyplus) * cezplus)
      * dlift;
    errbound = isperrboundA * permanent;
    if ((det > errbound) || (-det > errbound)) { return det; }

    return insphereadapt(pa, pb, pc, pd, pe, permanent);
  }
  //--------------------------------------------------------------------
  // disable construction
  //--------------------------------------------------------------------

  private Predicates () {
    throw new UnsupportedOperationException();
  }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
