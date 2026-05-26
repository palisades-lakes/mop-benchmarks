package mop.java.geometry.predicates;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.numbers.Hilo;
import mop.java.numbers.XDouble;

import static mop.java.geometry.predicates.Expansion.*;

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
 * @version 2026-05-26
 */

// strictfp unnecessary for JDK17 and later
public final class Slow implements Predicate {

  //--------------------------------------------------------------------

  public final boolean isExact () { return true; }

  //--------------------------------------------------------------------
  // incircle
  //--------------------------------------------------------------------

  private static final XDouble det (final Hilo ax,
                                    final Hilo ay,
                                    final Hilo bx,
                                    final Hilo by,
                                    final Hilo cx,
                                    final Hilo cy) {

    final XDouble axby = XDouble.twoTwoProduct(ax, by);
    final XDouble bxay = XDouble.twoTwoProduct(bx, ay);
    final XDouble sum = axby.subtract(bxay);

    final XDouble sxhihi = sum.scale(cx.hi()).scale(cx.hi());
    final XDouble sxlo = sum.scale(cx.lo());
    // TODO: guess simple loop works because exact if 2x
    //  could implement a faster exponent add version for powers of 2?
    final XDouble sxlohi2 = sxlo.scale(cx.hi()).fast2x();
    //for (i = 0; i < xxtlen; i++) { detxxt[i] *= 2.0; }
    final XDouble sxlolo = sxlo.scale(cx.lo());
    final XDouble detx = sxhihi.add(sxlohi2).add(sxlolo);

    final XDouble syhihi = sum.scale(cy.hi()).scale(cy.hi());
    final XDouble sylo = sum.scale(cy.lo());
    final XDouble sylohi2 = sylo.scale(cy.hi()).fast2x();
    //for (i = 0; i < yytlen; i++) { detyyt[i] *= 2.0; }
    final XDouble sylolo = sylo.scale(cy.lo());
    final XDouble dety = syhihi.add(sylohi2).add(sylolo);

    return detx.add(dety); }

  //--------------------------------------------------------------------
  /** signed distance of <code>pd</code> from the circumcircle thru
   * <code>pa,pb,pc</code>, negative means outside.
   */

  public final double incircle (final double[] pa,
                                final double[] pb,
                                final double[] pc,
                                final double[] pd) {

    final Hilo ax = Hilo.twoDiff(pa[0], pd[0]);
    final Hilo ay = Hilo.twoDiff(pa[1], pd[1]);
    final Hilo bx = Hilo.twoDiff(pb[0], pd[0]);
    final Hilo by = Hilo.twoDiff(pb[1], pd[1]);
    final Hilo cx = Hilo.twoDiff(pc[0], pd[0]);
    final Hilo cy = Hilo.twoDiff(pc[1], pd[1]);

    final XDouble ad = det(bx,by,cx,cy,ax,ay);
    final XDouble bd = det(cx,cy,ax,ay,bx,by);
    final XDouble cd = det(ax,ay,bx,by,cx,cy);

    final XDouble d = cd.add(bd).add(ad);
    //new Throwable().printStackTrace(System.out);
    System.out.println(d);
    return d.doubleValue();
//    return d.estimate();
  }

// from macro expanded C code:
//
//  public final double incircle (final double[] pa, final double[] pb,
//                                final double[] pc, final double[] pd) {
//    double adx, bdx, cdx, ady, bdy, cdy;
//    double adxtail, bdxtail, cdxtail; double adytail, bdytail, cdytail;
//    double negate, negatetail;
//    double axby7, bxcy7, axcy7, bxay7, cxby7, cxay7;
//    double[] axby = new double[8], bxcy = new double[8], axcy =
//      new double[8], bxay = new double[8], cxby = new double[8], cxay =
//      new double[8]; double[] temp16 = new double[16]; int temp16len;
//    double[] detx = new double[32], detxx = new double[64], detxt =
//      new double[32], detxxt = new double[64], detxtxt = new double[64];
//    int xlen, xxlen, xtlen, xxtlen, xtxtlen;
//    double[] x1 = new double[128], x2 = new double[192];
//    int x1len, x2len;
//    double[] dety = new double[32], detyy = new double[64], detyt =
//      new double[32], detyyt = new double[64], detytyt = new double[64];
//    int ylen, yylen, ytlen, yytlen, ytytlen;
//    double[] y1 = new double[128], y2 = new double[192];
//    int y1len, y2len;
//    double[] adet = new double[384], bdet = new double[384], cdet =
//      new double[384], abdet = new double[768], deter =
//      new double[1152]; int alen, blen, clen, ablen, deterlen; int i;
//
//    double bvirt; double avirt, bround, around; double c; double abig;
//    double a0hi, a0lo, a1hi, a1lo, bhi, blo; double err1, err2, err3;
//    double _i, _j, _k, _l, _m, _n; double _0, _1, _2;
//
//    adx = (pa[0] - pd[0]); bvirt = (pa[0] - adx); avirt = adx + bvirt;
//    bround = bvirt - pd[0]; around = pa[0] - avirt;
//    adxtail = around + bround; ady = (pa[1] - pd[1]);
//    bvirt = (pa[1] - ady); avirt = ady + bvirt; bround = bvirt - pd[1];
//    around = pa[1] - avirt; adytail = around + bround;
//    bdx = (pb[0] - pd[0]); bvirt = (pb[0] - bdx); avirt = bdx + bvirt;
//    bround = bvirt - pd[0]; around = pb[0] - avirt;
//    bdxtail = around + bround; bdy = (pb[1] - pd[1]);
//    bvirt = (pb[1] - bdy); avirt = bdy + bvirt; bround = bvirt - pd[1];
//    around = pb[1] - avirt; bdytail = around + bround;
//    cdx = (pc[0] - pd[0]); bvirt = (pc[0] - cdx); avirt = cdx + bvirt;
//    bround = bvirt - pd[0]; around = pc[0] - avirt;
//    cdxtail = around + bround; cdy = (pc[1] - pd[1]);
//    bvirt = (pc[1] - cdy); avirt = cdy + bvirt; bround = bvirt - pd[1];
//    around = pc[1] - avirt; cdytail = around + bround;
//
//    c = (SPLITTER * adxtail); abig = (c - adxtail); a0hi = c - abig;
//    a0lo = adxtail - a0hi; c = (SPLITTER * bdytail);
//    abig = (c - bdytail); bhi = c - abig; blo = bdytail - bhi;
//    _i = (adxtail * bdytail); err1 = _i - (a0hi * bhi);
//    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
//    axby[0] = (a0lo * blo) - err3; c = (SPLITTER * adx);
//    abig = (c - adx); a1hi = c - abig; a1lo = adx - a1hi;
//    _j = (adx * bdytail); err1 = _j - (a1hi * bhi);
//    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
//    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
//    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
//    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
//    _2 = _k - bvirt; c = (SPLITTER * bdy); abig = (c - bdy);
//    bhi = c - abig; blo = bdy - bhi; _i = (adxtail * bdy);
//    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
//    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
//    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
//    bround = _0 - bvirt; around = _1 - avirt; axby[1] = around + bround;
//    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
//    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
//    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
//    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
//    _j = (adx * bdy); err1 = _j - (a1hi * bhi);
//    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
//    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
//    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
//    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
//    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
//    axby[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
//    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
//    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
//    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
//    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
//    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
//    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
//    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
//    axby[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
//    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
//    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
//    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
//    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
//    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
//    axby[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
//    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
//    axby[5] = around + bround; axby7 = (_m + _k); bvirt = (axby7 - _m);
//    avirt = axby7 - bvirt; bround = _k - bvirt; around = _m - avirt;
//    axby[6] = around + bround
//
//    ; axby[7] = axby7; negate = -ady; negatetail = -adytail;
//    c = (SPLITTER * bdxtail); abig = (c - bdxtail); a0hi = c - abig;
//    a0lo = bdxtail - a0hi; c = (SPLITTER * negatetail);
//    abig = (c - negatetail); bhi = c - abig; blo = negatetail - bhi;
//    _i = (bdxtail * negatetail); err1 = _i - (a0hi * bhi);
//    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
//    bxay[0] = (a0lo * blo) - err3; c = (SPLITTER * bdx);
//    abig = (c - bdx); a1hi = c - abig; a1lo = bdx - a1hi;
//    _j = (bdx * negatetail); err1 = _j - (a1hi * bhi);
//    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
//    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
//    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
//    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
//    _2 = _k - bvirt; c = (SPLITTER * negate); abig = (c - negate);
//    bhi = c - abig; blo = negate - bhi; _i = (bdxtail * negate);
//    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
//    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
//    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
//    bround = _0 - bvirt; around = _1 - avirt; bxay[1] = around + bround;
//    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
//    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
//    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
//    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
//    _j = (bdx * negate); err1 = _j - (a1hi * bhi);
//    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
//    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
//    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
//    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
//    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
//    bxay[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
//    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
//    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
//    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
//    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
//    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
//    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
//    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
//    bxay[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
//    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
//    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
//    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
//    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
//    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
//    bxay[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
//    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
//    bxay[5] = around + bround; bxay7 = (_m + _k); bvirt = (bxay7 - _m);
//    avirt = bxay7 - bvirt; bround = _k - bvirt; around = _m - avirt;
//    bxay[6] = around + bround
//
//    ; bxay[7] = bxay7; c = (SPLITTER * bdxtail); abig = (c - bdxtail);
//    a0hi = c - abig; a0lo = bdxtail - a0hi; c = (SPLITTER * cdytail);
//    abig = (c - cdytail); bhi = c - abig; blo = cdytail - bhi;
//    _i = (bdxtail * cdytail); err1 = _i - (a0hi * bhi);
//    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
//    bxcy[0] = (a0lo * blo) - err3; c = (SPLITTER * bdx);
//    abig = (c - bdx); a1hi = c - abig; a1lo = bdx - a1hi;
//    _j = (bdx * cdytail); err1 = _j - (a1hi * bhi);
//    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
//    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
//    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
//    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
//    _2 = _k - bvirt; c = (SPLITTER * cdy); abig = (c - cdy);
//    bhi = c - abig; blo = cdy - bhi; _i = (bdxtail * cdy);
//    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
//    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
//    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
//    bround = _0 - bvirt; around = _1 - avirt; bxcy[1] = around + bround;
//    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
//    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
//    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
//    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
//    _j = (bdx * cdy); err1 = _j - (a1hi * bhi);
//    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
//    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
//    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
//    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
//    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
//    bxcy[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
//    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
//    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
//    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
//    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
//    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
//    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
//    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
//    bxcy[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
//    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
//    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
//    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
//    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
//    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
//    bxcy[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
//    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
//    bxcy[5] = around + bround; bxcy7 = (_m + _k); bvirt = (bxcy7 - _m);
//    avirt = bxcy7 - bvirt; bround = _k - bvirt; around = _m - avirt;
//    bxcy[6] = around + bround
//
//    ; bxcy[7] = bxcy7; negate = -bdy; negatetail = -bdytail;
//    c = (SPLITTER * cdxtail); abig = (c - cdxtail); a0hi = c - abig;
//    a0lo = cdxtail - a0hi; c = (SPLITTER * negatetail);
//    abig = (c - negatetail); bhi = c - abig; blo = negatetail - bhi;
//    _i = (cdxtail * negatetail); err1 = _i - (a0hi * bhi);
//    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
//    cxby[0] = (a0lo * blo) - err3; c = (SPLITTER * cdx);
//    abig = (c - cdx); a1hi = c - abig; a1lo = cdx - a1hi;
//    _j = (cdx * negatetail); err1 = _j - (a1hi * bhi);
//    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
//    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
//    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
//    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
//    _2 = _k - bvirt; c = (SPLITTER * negate); abig = (c - negate);
//    bhi = c - abig; blo = negate - bhi; _i = (cdxtail * negate);
//    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
//    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
//    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
//    bround = _0 - bvirt; around = _1 - avirt; cxby[1] = around + bround;
//    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
//    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
//    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
//    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
//    _j = (cdx * negate); err1 = _j - (a1hi * bhi);
//    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
//    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
//    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
//    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
//    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
//    cxby[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
//    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
//    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
//    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
//    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
//    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
//    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
//    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
//    cxby[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
//    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
//    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
//    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
//    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
//    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
//    cxby[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
//    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
//    cxby[5] = around + bround; cxby7 = (_m + _k); bvirt = (cxby7 - _m);
//    avirt = cxby7 - bvirt; bround = _k - bvirt; around = _m - avirt;
//    cxby[6] = around + bround
//
//    ; cxby[7] = cxby7; c = (SPLITTER * cdxtail); abig = (c - cdxtail);
//    a0hi = c - abig; a0lo = cdxtail - a0hi; c = (SPLITTER * adytail);
//    abig = (c - adytail); bhi = c - abig; blo = adytail - bhi;
//    _i = (cdxtail * adytail); err1 = _i - (a0hi * bhi);
//    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
//    cxay[0] = (a0lo * blo) - err3; c = (SPLITTER * cdx);
//    abig = (c - cdx); a1hi = c - abig; a1lo = cdx - a1hi;
//    _j = (cdx * adytail); err1 = _j - (a1hi * bhi);
//    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
//    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
//    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
//    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
//    _2 = _k - bvirt; c = (SPLITTER * ady); abig = (c - ady);
//    bhi = c - abig; blo = ady - bhi; _i = (cdxtail * ady);
//    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
//    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
//    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
//    bround = _0 - bvirt; around = _1 - avirt; cxay[1] = around + bround;
//    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
//    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
//    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
//    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
//    _j = (cdx * ady); err1 = _j - (a1hi * bhi);
//    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
//    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
//    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
//    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
//    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
//    cxay[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
//    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
//    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
//    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
//    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
//    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
//    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
//    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
//    cxay[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
//    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
//    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
//    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
//    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
//    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
//    cxay[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
//    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
//    cxay[5] = around + bround; cxay7 = (_m + _k); bvirt = (cxay7 - _m);
//    avirt = cxay7 - bvirt; bround = _k - bvirt; around = _m - avirt;
//    cxay[6] = around + bround
//
//    ; cxay[7] = cxay7; negate = -cdy; negatetail = -cdytail;
//    c = (SPLITTER * adxtail); abig = (c - adxtail); a0hi = c - abig;
//    a0lo = adxtail - a0hi; c = (SPLITTER * negatetail);
//    abig = (c - negatetail); bhi = c - abig; blo = negatetail - bhi;
//    _i = (adxtail * negatetail); err1 = _i - (a0hi * bhi);
//    err2 = err1 - (a0lo * bhi); err3 = err2 - (a0hi * blo);
//    axcy[0] = (a0lo * blo) - err3; c = (SPLITTER * adx);
//    abig = (c - adx); a1hi = c - abig; a1lo = adx - a1hi;
//    _j = (adx * negatetail); err1 = _j - (a1hi * bhi);
//    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
//    _0 = (a1lo * blo) - err3; _k = (_i + _0); bvirt = (_k - _i);
//    avirt = _k - bvirt; bround = _0 - bvirt; around = _i - avirt;
//    _1 = around + bround; _l = (_j + _k); bvirt = _l - _j;
//    _2 = _k - bvirt; c = (SPLITTER * negate); abig = (c - negate);
//    bhi = c - abig; blo = negate - bhi; _i = (adxtail * negate);
//    err1 = _i - (a0hi * bhi); err2 = err1 - (a0lo * bhi);
//    err3 = err2 - (a0hi * blo); _0 = (a0lo * blo) - err3;
//    _k = (_1 + _0); bvirt = (_k - _1); avirt = _k - bvirt;
//    bround = _0 - bvirt; around = _1 - avirt; axcy[1] = around + bround;
//    _j = (_2 + _k); bvirt = (_j - _2); avirt = _j - bvirt;
//    bround = _k - bvirt; around = _2 - avirt; _1 = around + bround;
//    _m = (_l + _j); bvirt = (_m - _l); avirt = _m - bvirt;
//    bround = _j - bvirt; around = _l - avirt; _2 = around + bround;
//    _j = (adx * negate); err1 = _j - (a1hi * bhi);
//    err2 = err1 - (a1lo * bhi); err3 = err2 - (a1hi * blo);
//    _0 = (a1lo * blo) - err3; _n = (_i + _0); bvirt = (_n - _i);
//    avirt = _n - bvirt; bround = _0 - bvirt; around = _i - avirt;
//    _0 = around + bround; _i = (_1 + _0); bvirt = (_i - _1);
//    avirt = _i - bvirt; bround = _0 - bvirt; around = _1 - avirt;
//    axcy[2] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
//    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
//    _1 = around + bround; _l = (_m + _k); bvirt = (_l - _m);
//    avirt = _l - bvirt; bround = _k - bvirt; around = _m - avirt;
//    _2 = around + bround; _k = (_j + _n); bvirt = (_k - _j);
//    avirt = _k - bvirt; bround = _n - bvirt; around = _j - avirt;
//    _0 = around + bround; _j = (_1 + _0); bvirt = (_j - _1);
//    avirt = _j - bvirt; bround = _0 - bvirt; around = _1 - avirt;
//    axcy[3] = around + bround; _i = (_2 + _j); bvirt = (_i - _2);
//    avirt = _i - bvirt; bround = _j - bvirt; around = _2 - avirt;
//    _1 = around + bround; _m = (_l + _i); bvirt = (_m - _l);
//    avirt = _m - bvirt; bround = _i - bvirt; around = _l - avirt;
//    _2 = around + bround; _i = (_1 + _k); bvirt = (_i - _1);
//    avirt = _i - bvirt; bround = _k - bvirt; around = _1 - avirt;
//    axcy[4] = around + bround; _k = (_2 + _i); bvirt = (_k - _2);
//    avirt = _k - bvirt; bround = _i - bvirt; around = _2 - avirt;
//    axcy[5] = around + bround; axcy7 = (_m + _k); bvirt = (axcy7 - _m);
//    avirt = axcy7 - bvirt; bround = _k - bvirt; around = _m - avirt;
//    axcy[6] = around + bround
//
//    ; axcy[7] = axcy7;
//
//    temp16len = sum(8, bxcy, 8, cxby, temp16);
//
//    xlen = scale(temp16len, temp16, adx, detx);
//    xxlen = scale(xlen, detx, adx, detxx);
//    xtlen = scale(temp16len, temp16, adxtail, detxt);
//    xxtlen = scale(xtlen, detxt, adx, detxxt);
//    for (i = 0; i < xxtlen; i++) {
//      detxxt[i] *= 2.0;
//    }
//    xtxtlen = scale(xtlen, detxt, adxtail, detxtxt);
//    x1len =
//      sum(xxlen, detxx, xxtlen, detxxt, x1);
//    x2len =
//      sum(x1len, x1, xtxtlen, detxtxt, x2);
//
//    ylen = scale(temp16len, temp16, ady, dety);
//    yylen = scale(ylen, dety, ady, detyy);
//    ytlen = scale(temp16len, temp16, adytail, detyt);
//    yytlen = scale(ytlen, detyt, ady, detyyt);
//    for (i = 0; i < yytlen; i++) {
//      detyyt[i] *= 2.0;
//    }
//    ytytlen = scale(ytlen, detyt, adytail, detytyt);
//    y1len =
//      sum(yylen, detyy, yytlen, detyyt, y1);
//    y2len =
//      sum(y1len, y1, ytytlen, detytyt, y2);
//
//    alen = sum(x2len, x2, y2len, y2, adet);
//
//    temp16len = sum(8, cxay, 8, axcy, temp16);
//
//    xlen = scale(temp16len, temp16, bdx, detx);
//    xxlen = scale(xlen, detx, bdx, detxx);
//    xtlen = scale(temp16len, temp16, bdxtail, detxt);
//    xxtlen = scale(xtlen, detxt, bdx, detxxt);
//    for (i = 0; i < xxtlen; i++) {
//      detxxt[i] *= 2.0;
//    }
//    xtxtlen = scale(xtlen, detxt, bdxtail, detxtxt);
//    x1len =
//      sum(xxlen, detxx, xxtlen, detxxt, x1);
//    x2len =
//      sum(x1len, x1, xtxtlen, detxtxt, x2);
//
//    ylen = scale(temp16len, temp16, bdy, dety);
//    yylen = scale(ylen, dety, bdy, detyy);
//    ytlen = scale(temp16len, temp16, bdytail, detyt);
//    yytlen = scale(ytlen, detyt, bdy, detyyt);
//    for (i = 0; i < yytlen; i++) {
//      detyyt[i] *= 2.0;
//    }
//    ytytlen = scale(ytlen, detyt, bdytail, detytyt);
//    y1len =
//      sum(yylen, detyy, yytlen, detyyt, y1);
//    y2len =
//      sum(y1len, y1, ytytlen, detytyt, y2);
//
//    blen = sum(x2len, x2, y2len, y2, bdet);
//
//    temp16len = sum(8, axby, 8, bxay, temp16);
//
//    xlen = scale(temp16len, temp16, cdx, detx);
//    xxlen = scale(xlen, detx, cdx, detxx);
//    xtlen = scale(temp16len, temp16, cdxtail, detxt);
//    xxtlen = scale(xtlen, detxt, cdx, detxxt);
//    for (i = 0; i < xxtlen; i++) {
//      detxxt[i] *= 2.0;
//    }
//    xtxtlen = scale(xtlen, detxt, cdxtail, detxtxt);
//    x1len =
//      sum(xxlen, detxx, xxtlen, detxxt, x1);
//    x2len =
//      sum(x1len, x1, xtxtlen, detxtxt, x2);
//
//    ylen = scale(temp16len, temp16, cdy, dety);
//    yylen = scale(ylen, dety, cdy, detyy);
//    ytlen = scale(temp16len, temp16, cdytail, detyt);
//    yytlen = scale(ytlen, detyt, cdy, detyyt);
//    for (i = 0; i < yytlen; i++) {
//      detyyt[i] *= 2.0;
//    }
//    ytytlen = scale(ytlen, detyt, cdytail, detytyt);
//    y1len =
//      sum(yylen, detyy, yytlen, detyyt, y1);
//    y2len =
//      sum(y1len, y1, ytytlen, detytyt, y2);
//
//    clen = sum(x2len, x2, y2len, y2, cdet);
//
//    ablen = sum(alen, adet, blen, bdet, abdet);
//    deterlen =
//      sum(ablen, abdet, clen, cdet, deter);
//
//    return deter[deterlen - 1];
//  }

  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------
  // TODO: seems to return 2xsigned area
  public final double orient2d (final double[] pa, final double[] pb,
                                final double[] pc) {
    double acx, acy, bcx, bcy; double acxtail, acytail;
    double bcxtail, bcytail; double negate, negatetail;
    double[] axby = new double[8]; double[] bxay = new double[8];
    double axby7, bxay7; double[] deter = new double[16]; int deterlen;

    double bvirt; double avirt, bround, around; double c; double abig;
    double a0hi, a0lo, a1hi, a1lo, bhi, blo; double err1, err2, err3;
    double _i, _j, _k, _l, _m, _n; double _0, _1, _2;

    acx = (pa[0] - pc[0]); bvirt = (pa[0] - acx); avirt = acx + bvirt;
    bround = bvirt - pc[0]; around = pa[0] - avirt;
    acxtail = around + bround; acy = (pa[1] - pc[1]);
    bvirt = (pa[1] - acy); avirt = acy + bvirt; bround = bvirt - pc[1];
    around = pa[1] - avirt; acytail = around + bround;
    bcx = (pb[0] - pc[0]); bvirt = (pb[0] - bcx); avirt = bcx + bvirt;
    bround = bvirt - pc[0]; around = pb[0] - avirt;
    bcxtail = around + bround; bcy = (pb[1] - pc[1]);
    bvirt = (pb[1] - bcy); avirt = bcy + bvirt; bround = bvirt - pc[1];
    around = pb[1] - avirt; bcytail = around + bround;

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

    deterlen = sum(8, axby, 8, bxay, deter);

    return deter[deterlen - 1];
  }

  //--------------------------------------------------------------------
  // orient3d
  //--------------------------------------------------------------------
  public final double orient3d (final double[] pa, final double[] pb,
                                final double[] pc, final double[] pd) {
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

    adx = (pa[0] - pd[0]); bvirt = (pa[0] - adx); avirt = adx + bvirt;
    bround = bvirt - pd[0]; around = pa[0] - avirt;
    adxtail = around + bround; ady = (pa[1] - pd[1]);
    bvirt = (pa[1] - ady); avirt = ady + bvirt; bround = bvirt - pd[1];
    around = pa[1] - avirt; adytail = around + bround;
    adz = (pa[2] - pd[2]); bvirt = (pa[2] - adz); avirt = adz + bvirt;
    bround = bvirt - pd[2]; around = pa[2] - avirt;
    adztail = around + bround; bdx = (pb[0] - pd[0]);
    bvirt = (pb[0] - bdx); avirt = bdx + bvirt; bround = bvirt - pd[0];
    around = pb[0] - avirt; bdxtail = around + bround;
    bdy = (pb[1] - pd[1]); bvirt = (pb[1] - bdy); avirt = bdy + bvirt;
    bround = bvirt - pd[1]; around = pb[1] - avirt;
    bdytail = around + bround; bdz = (pb[2] - pd[2]);
    bvirt = (pb[2] - bdz); avirt = bdz + bvirt; bround = bvirt - pd[2];
    around = pb[2] - avirt; bdztail = around + bround;
    cdx = (pc[0] - pd[0]); bvirt = (pc[0] - cdx); avirt = cdx + bvirt;
    bround = bvirt - pd[0]; around = pc[0] - avirt;
    cdxtail = around + bround; cdy = (pc[1] - pd[1]);
    bvirt = (pc[1] - cdy); avirt = cdy + bvirt; bround = bvirt - pd[1];
    around = pc[1] - avirt; cdytail = around + bround;
    cdz = (pc[2] - pd[2]); bvirt = (pc[2] - cdz); avirt = cdz + bvirt;
    bround = bvirt - pd[2]; around = pc[2] - avirt;
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

    temp16len = sum(8, bxcy, 8, cxby, temp16);
    temp32len =
      scale(temp16len, temp16, adz, temp32);
    temp32tlen =
      scale(temp16len, temp16, adztail, temp32t);
    alen = sum(temp32len, temp32, temp32tlen,
               temp32t, adet);

    temp16len = sum(8, cxay, 8, axcy, temp16);
    temp32len =
      scale(temp16len, temp16, bdz, temp32);
    temp32tlen =
      scale(temp16len, temp16, bdztail, temp32t);
    blen = sum(temp32len, temp32, temp32tlen,
               temp32t, bdet);

    temp16len = sum(8, axby, 8, bxay, temp16);
    temp32len =
      scale(temp16len, temp16, cdz, temp32);
    temp32tlen =
      scale(temp16len, temp16, cdztail, temp32t);
    clen = sum(temp32len, temp32, temp32tlen,
               temp32t, cdet);

    ablen = sum(alen, adet, blen, bdet, abdet);
    deterlen =
      sum(ablen, abdet, clen, cdet, deter);

    return deter[deterlen - 1];
  }

  //--------------------------------------------------------------------
  // insphere
  //--------------------------------------------------------------------
  public final double insphere (final double[] pa, final double[] pb,
                                final double[] pc, final double[] pd,
                                final double[] pe) {
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

    aex = (pa[0] - pe[0]); bvirt = (pa[0] - aex); avirt = aex + bvirt;
    bround = bvirt - pe[0]; around = pa[0] - avirt;
    aextail = around + bround; aey = (pa[1] - pe[1]);
    bvirt = (pa[1] - aey); avirt = aey + bvirt; bround = bvirt - pe[1];
    around = pa[1] - avirt; aeytail = around + bround;
    aez = (pa[2] - pe[2]); bvirt = (pa[2] - aez); avirt = aez + bvirt;
    bround = bvirt - pe[2]; around = pa[2] - avirt;
    aeztail = around + bround; bex = (pb[0] - pe[0]);
    bvirt = (pb[0] - bex); avirt = bex + bvirt; bround = bvirt - pe[0];
    around = pb[0] - avirt; bextail = around + bround;
    bey = (pb[1] - pe[1]); bvirt = (pb[1] - bey); avirt = bey + bvirt;
    bround = bvirt - pe[1]; around = pb[1] - avirt;
    beytail = around + bround; bez = (pb[2] - pe[2]);
    bvirt = (pb[2] - bez); avirt = bez + bvirt; bround = bvirt - pe[2];
    around = pb[2] - avirt; beztail = around + bround;
    cex = (pc[0] - pe[0]); bvirt = (pc[0] - cex); avirt = cex + bvirt;
    bround = bvirt - pe[0]; around = pc[0] - avirt;
    cextail = around + bround; cey = (pc[1] - pe[1]);
    bvirt = (pc[1] - cey); avirt = cey + bvirt; bround = bvirt - pe[1];
    around = pc[1] - avirt; ceytail = around + bround;
    cez = (pc[2] - pe[2]); bvirt = (pc[2] - cez); avirt = cez + bvirt;
    bround = bvirt - pe[2]; around = pc[2] - avirt;
    ceztail = around + bround; dex = (pd[0] - pe[0]);
    bvirt = (pd[0] - dex); avirt = dex + bvirt; bround = bvirt - pe[0];
    around = pd[0] - avirt; dextail = around + bround;
    dey = (pd[1] - pe[1]); bvirt = (pd[1] - dey); avirt = dey + bvirt;
    bround = bvirt - pe[1]; around = pd[1] - avirt;
    deytail = around + bround; dez = (pd[2] - pe[2]);
    bvirt = (pd[2] - dez); avirt = dez + bvirt; bround = bvirt - pe[2];
    around = pd[2] - avirt; deztail = around + bround;

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
    ablen = sum(8, axby, 8, bxay, ab);
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
    bclen = sum(8, bxcy, 8, cxby, bc);
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
    cdlen = sum(8, cxdy, 8, dxcy, cd);
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
    dalen = sum(8, dxay, 8, axdy, da);
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
    aclen = sum(8, axcy, 8, cxay, ac);
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
    bdlen = sum(8, bxdy, 8, dxby, bd);

    temp32alen = scale(cdlen, cd, -bez, temp32a);
    temp32blen = scale(cdlen, cd, -beztail, temp32b);
    temp64alen =
      sum(temp32alen, temp32a, temp32blen,
          temp32b, temp64a);
    temp32alen = scale(bdlen, bd, cez, temp32a);
    temp32blen = scale(bdlen, bd, ceztail, temp32b);
    temp64blen =
      sum(temp32alen, temp32a, temp32blen,
          temp32b, temp64b);
    temp32alen = scale(bclen, bc, -dez, temp32a);
    temp32blen = scale(bclen, bc, -deztail, temp32b);
    temp64clen =
      sum(temp32alen, temp32a, temp32blen,
          temp32b, temp64c); temp128len =
      sum(temp64alen, temp64a, temp64blen,
          temp64b, temp128); temp192len =
      sum(temp64clen, temp64c, temp128len,
          temp128, temp192);
    xlen = scale(temp192len, temp192, aex, detx);
    xxlen = scale(xlen, detx, aex, detxx);xtlen =
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
    yylen = scale(ylen, dety, aey, detyy);ytlen =
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
    zzlen = scale(zlen, detz, aez, detzz);ztlen =
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
    temp64alen =
      sum(temp32alen, temp32a, temp32blen,
          temp32b, temp64a);
    temp32alen = scale(aclen, ac, dez, temp32a);
    temp32blen = scale(aclen, ac, deztail, temp32b);
    temp64blen =
      sum(temp32alen, temp32a, temp32blen,
          temp32b, temp64b);
    temp32alen = scale(cdlen, cd, aez, temp32a);
    temp32blen = scale(cdlen, cd, aeztail, temp32b);
    temp64clen =
      sum(temp32alen, temp32a, temp32blen,
          temp32b, temp64c); temp128len =
      sum(temp64alen, temp64a, temp64blen,
          temp64b, temp128); temp192len =
      sum(temp64clen, temp64c, temp128len,
          temp128, temp192);
    xlen = scale(temp192len, temp192, bex, detx);
    xxlen = scale(xlen, detx, bex, detxx);xtlen =
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
    yylen = scale(ylen, dety, bey, detyy);ytlen =
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
    zzlen = scale(zlen, detz, bez, detzz);ztlen =
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
    temp64alen =
      sum(temp32alen, temp32a, temp32blen,
          temp32b, temp64a);
    temp32alen = scale(bdlen, bd, -aez, temp32a);
    temp32blen = scale(bdlen, bd, -aeztail, temp32b);
    temp64blen =
      sum(temp32alen, temp32a, temp32blen,
          temp32b, temp64b);
    temp32alen = scale(dalen, da, -bez, temp32a);
    temp32blen = scale(dalen, da, -beztail, temp32b);
    temp64clen =
      sum(temp32alen, temp32a, temp32blen,
          temp32b, temp64c); temp128len =
      sum(temp64alen, temp64a, temp64blen,
          temp64b, temp128); temp192len =
      sum(temp64clen, temp64c, temp128len,
          temp128, temp192);
    xlen = scale(temp192len, temp192, cex, detx);
    xxlen = scale(xlen, detx, cex, detxx);xtlen =
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
    yylen = scale(ylen, dety, cey, detyy);ytlen =
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
    zzlen = scale(zlen, detz, cez, detzz);ztlen =
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
    temp64alen =
      sum(temp32alen, temp32a, temp32blen,
          temp32b, temp64a);
    temp32alen = scale(aclen, ac, -bez, temp32a);
    temp32blen = scale(aclen, ac, -beztail, temp32b);
    temp64blen =
      sum(temp32alen, temp32a, temp32blen,
          temp32b, temp64b);
    temp32alen = scale(ablen, ab, cez, temp32a);
    temp32blen = scale(ablen, ab, ceztail, temp32b);
    temp64clen =
      sum(temp32alen, temp32a, temp32blen,
          temp32b, temp64c); temp128len =
      sum(temp64alen, temp64a, temp64blen,
          temp64b, temp128); temp192len =
      sum(temp64clen, temp64c, temp128len,
          temp128, temp192);
    xlen = scale(temp192len, temp192, dex, detx);
    xxlen = scale(xlen, detx, dex, detxx);xtlen =
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
    yylen = scale(ylen, dety, dey, detyy);ytlen =
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
    zzlen = scale(zlen, detz, dez, detzz);ztlen =
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
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public Slow () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
