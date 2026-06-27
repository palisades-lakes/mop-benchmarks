package mop.java.geometry.predicates;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.numbers.Hilo;
import mop.java.numbers.XDouble;

import static mop.java.geometry.predicates.Expansion.EPSILON;
import static mop.java.geometry.predicates.Expansion.SPLITTER;
import static mop.java.geometry.predicates.Expansion.estimate;
import static mop.java.geometry.predicates.Expansion.scale;
import static mop.java.geometry.predicates.Expansion.sum;

/** Adaptive 'exact' tests. Robust.
 * 'Exact' seems to mean boolean predicate, that is, the sign of the
 * returned value is correct, not its specific value.
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
 * @version 2026-06-27
 */

// strictfp (may be) necessary for JDK16 and earlier
public final class Adapt implements Predicate {

  //--------------------------------------------------------------------

  public final boolean isExact () { return false; }

  //--------------------------------------------------------------------
  // incircle
  //--------------------------------------------------------------------

  private static final double iccerrboundC =
    (44.0 + 576.0 * EPSILON) * EPSILON * EPSILON;

  private static final double iccerrboundB =
    (4.0 + 48.0 * EPSILON) * EPSILON;

  private static final double iccerrboundA =
    (10.0 + 96.0 * EPSILON) * EPSILON;

  private static final double incircle (final double[] pa,
                                        final double[] pb,
                                        final double[] pc,
                                        final double[] pd,
                                        final double permanent) {
    // TODO: should this be Hilo.twoDiff? see calls to towDiffTail below
    final double adx = (pa[0] - pd[0]);
    final double ady = (pa[1] - pd[1]);
    final double bdx = (pb[0] - pd[0]);
    final double bdy = (pb[1] - pd[1]);
    final double cdx = (pc[0] - pd[0]);
    final double cdy = (pc[1] - pd[1]);

    //    Two_Product(bdx, cdy, bdxcdy1, bdxcdy0);
    //    Two_Product(cdx, bdy, cdxbdy1, cdxbdy0);
    //    Two_Two_Diff(bdxcdy1, bdxcdy0, cdxbdy1, cdxbdy0, bc3, bc[2], bc[1], bc[0]);
    final Hilo bdxcdy = Hilo.twoProduct(bdx,cdy);
    final Hilo cdxbdy = Hilo.twoProduct(cdx,bdy);
    final XDouble bc = XDouble.twoTwoDiff(bdxcdy,cdxbdy);
    // TODO: XDouble l2norm2
    final XDouble axbc = bc.scale(adx);
    final XDouble axxbc = axbc.scale(adx);
    final XDouble aybc = bc.scale(ady);
    final XDouble ayybc = aybc.scale(ady);
    final XDouble adet = axxbc.add(ayybc);

    //    Two_Product(cdx, ady, cdxady1, cdxady0);
    //    Two_Product(adx, cdy, adxcdy1, adxcdy0);
    //    Two_Two_Diff(cdxady1, cdxady0, adxcdy1, adxcdy0, ca3, ca[2], ca[1], ca[0]);
    final Hilo cdxady = Hilo.twoProduct(cdx,ady);
    final Hilo adxcdy = Hilo.twoProduct(adx,cdy);
    final XDouble ca = XDouble.twoTwoDiff(cdxady,adxcdy);
    final XDouble bxca = ca.scale(bdx);
    final XDouble bxxca = bxca.scale(bdx);
    final XDouble byca = ca.scale(bdy);
    final XDouble byyca = byca.scale(bdy);
    final XDouble bdet = bxxca.add(byyca);

//    Two_Product(adx, bdy, adxbdy1, adxbdy0);
//    Two_Product(bdx, ady, bdxady1, bdxady0);
//    Two_Two_Diff(adxbdy1, adxbdy0, bdxady1, bdxady0, ab3, ab[2], ab[1], ab[0]);
    final Hilo adxbdy = Hilo.twoProduct(adx,bdy);
    final Hilo bdxady = Hilo.twoProduct(bdx,ady);
    final XDouble ab = XDouble.twoTwoDiff(adxbdy,bdxady);
    final XDouble cxab = ab.scale(cdx);
    final XDouble cxxab = cxab.scale(cdx);
    final XDouble cyab = ab.scale(cdy);
    final XDouble cyyab = cyab.scale(cdy);
    final XDouble cdet = cxxab.add(cyyab);
    final XDouble abdet = adet.add(bdet);
    XDouble finnow = abdet.add(cdet);

    //det = fin1x.estimate();
    double det = finnow.doubleValue();
    if (Math.abs(det) >= iccerrboundB * permanent) { return det; }

//    Two_Diff_Tail(pa[0], pd[0], adx, adxtail);
//    Two_Diff_Tail(pa[1], pd[1], ady, adytail);
//    Two_Diff_Tail(pb[0], pd[0], bdx, bdxtail);
//    Two_Diff_Tail(pb[1], pd[1], bdy, bdytail);
//    Two_Diff_Tail(pc[0], pd[0], cdx, cdxtail);
//    Two_Diff_Tail(pc[1], pd[1], cdy, cdytail);

    final double adxtail = Hilo.twoDiffTail(pa[0], pd[0], adx);
    final double adytail = Hilo.twoDiffTail(pa[1], pd[1], ady);
    final double bdxtail = Hilo.twoDiffTail(pb[0], pd[0], bdx);
    final double bdytail = Hilo.twoDiffTail(pb[1], pd[1], bdy);
    final double cdxtail = Hilo.twoDiffTail(pc[0], pd[0], cdx);
    final double cdytail = Hilo.twoDiffTail(pc[1], pd[1], cdy);
    if ((adxtail == 0.0) && (bdxtail == 0.0) && (cdxtail == 0.0)
      && (adytail == 0.0) && (bdytail == 0.0) && (cdytail == 0.0)) {
      return det; }

    final double errbound = (iccerrboundC*permanent)
      + (resulterrbound*Math.abs(det));
    det +=
      (((adx*adx) + (ady*ady))
        * ((bdx*cdytail + cdy*bdxtail) - (bdy*cdxtail + cdx*bdytail))
        + 2.0*(adx*adxtail + ady*adytail) * (bdx*cdy - bdy*cdx))
        + ((bdx*bdx + bdy*bdy) * ((cdx*adytail + ady*cdxtail)
        - (cdy*adxtail + adx*cdytail))
        + 2.0*(bdx*bdxtail + bdy*bdytail)*(cdx*ady - cdy*adx))
        + ((cdx*cdx + cdy*cdy)*((adx*bdytail + bdy*adxtail)
        - (ady*bdxtail + bdx*adytail))
        + 2.0*(cdx*cdxtail + cdy*cdytail)*(adx*bdy - ady*bdx));
    if (Math.abs(det) >= errbound) { return det; }

    final XDouble aa;
    if ((bdxtail != 0.0) || (bdytail != 0.0)
      || (cdxtail != 0.0) || (cdytail != 0.0)) {
      aa = XDouble.twoTwoSum(Hilo.square(adx), Hilo.square(ady)); }
    else {
      aa = XDouble.ZERO; }

    final XDouble bb;
    if ((cdxtail != 0.0) || (cdytail != 0.0)
      || (adxtail != 0.0) || (adytail != 0.0)) {
      bb = XDouble.twoTwoSum(Hilo.square(bdx), Hilo.square(bdy)); }
    else {
      bb = XDouble.ZERO; }

    final XDouble cc;
    if ((adxtail != 0.0) || (adytail != 0.0)
      || (bdxtail != 0.0) || (bdytail != 0.0)) {
      cc = XDouble.twoTwoSum(Hilo.square(cdx), Hilo.square(cdy)); }
    else {
      cc  = XDouble.ZERO; }

    final XDouble axtbc;
    if (adxtail != 0.0) {
      axtbc = bc.scale(adxtail);
      final XDouble temp16a = axtbc.scale(2*adx);
      final XDouble axtcc = cc.scale(adxtail);
      final XDouble temp16b = axtcc.scale(bdy);
      final XDouble axtbb = bb.scale(adxtail);
      final XDouble temp16c = axtbb.scale(-cdy);
      final XDouble temp32a = temp16a.add(temp16b);
      final XDouble temp48 = temp16c.add(temp32a);
      finnow = finnow.add(temp48); }
    else {
      axtbc = XDouble.ZERO; }

    final XDouble aytbc;
    if (adytail != 0.0) {
      aytbc = bc.scale(adytail);
      final XDouble temp16a = aytbc.scale(2*ady);
      final XDouble aytbb = bb.scale(adytail);
      final XDouble temp16b = aytbb.scale(adytail);
      final XDouble aytcc = cc.scale(adytail);
      final XDouble temp16c = aytcc.scale(-bdx);
      final XDouble temp32a = temp16a.add(temp16b);
      final XDouble temp48 = temp16c.add(temp32a);
      finnow = finnow.add(temp48); }
    else {
      aytbc = XDouble.ZERO; }

    final XDouble bxtca;
    if (bdxtail != 0.0) {
      bxtca = ca.scale(bdxtail);
      final XDouble temp16a = bxtca.scale(2*bdx);
      final XDouble bxtaa = aa.scale(bdxtail);
      final XDouble temp16b = bxtaa.scale(cdy);
      final XDouble bxtcc = cc.scale(bdxtail);
      final XDouble temp16c = bxtcc.scale(-ady);
      final XDouble temp32a = temp16a.add(temp16b);
      final XDouble temp48 = temp16c.add(temp32a);
      finnow = finnow.add(temp48); }
    else {
      bxtca = XDouble.ZERO; }

    final XDouble bytca;
    if (bdytail != 0.0) {
      bytca = ca.scale(bdytail);
      final XDouble temp16a = bytca.scale(2*bdy);
      final XDouble bytcc = cc.scale(bdytail);
      final XDouble temp16b = bytcc.scale(adx);
      final XDouble bytaa = aa.scale(bdytail);
      final XDouble temp16c = bytaa.scale(-cdx);
      final XDouble temp32a = temp16a.add(temp16b);
      final XDouble temp48 = temp16c.add(temp32a);
      finnow = finnow.add(temp48);
    }
    else { bytca = XDouble.ZERO; }

    final XDouble cxtab;
    if (cdxtail != 0.0) {
      cxtab = ab.scale(cdxtail);
      final XDouble temp16a = cxtab.scale(2*cdx);
      final XDouble cxtbb = bb.scale(cdxtail);
      final XDouble temp16b = cxtbb.scale(ady);
      final XDouble cxtaa = aa.scale(cdxtail);
      final XDouble temp16c = cxtaa.scale(-bdy);
      final XDouble temp32a = temp16a.add(temp16b);
      final XDouble temp48 = temp16c.add(temp32a);
      finnow = finnow.add(temp48); }
    else { cxtab = XDouble.ZERO; }

    final XDouble cytab;
    if (cdytail != 0.0) {
      cytab = ab.scale(cdytail);
      final XDouble temp16a = cytab.scale(2*cdy);
      final XDouble cytaa = aa.scale(cdytail);
      final XDouble temp16b = cytaa.scale(bdx);
      final XDouble cytbb = bb.scale(cdytail);
      final XDouble temp16c = cytbb.scale(-adx);
      final XDouble temp32a = temp16a.add(temp16b);
      final XDouble temp48 = temp16c.add(temp32a);
      finnow = finnow.add(temp48); }
    else { cytab = XDouble.ZERO; }

    final XDouble bct, bctt;
    if ((adxtail != 0.0) || (adytail != 0.0)) {
      if ((bdxtail != 0.0) || (bdytail != 0.0)
        || (cdxtail != 0.0) || (cdytail != 0.0)) {
//        Two_Product(bdxtail, cdy, ti1, ti0);
//        Two_Product(bdx, cdytail, tj1, tj0);
//        Two_Two_Sum(ti1, ti0, tj1, tj0, u3, u[2], u[1], u[0]);
//        u[3] = u3;
        final XDouble u = XDouble.twoTwoSum(
          Hilo.twoProduct(bdxtail,cdy),
          Hilo.twoProduct(bdx,cdytail));

//        negate = -bdy;
//        Two_Product(cdxtail, negate, ti1, ti0);
//        negate = -bdytail;
//        Two_Product(cdx, negate, tj1, tj0);
//        Two_Two_Sum(ti1, ti0, tj1, tj0, v3, v[2], v[1], v[0]);
//        v[3] = v3;
        final XDouble v = XDouble.twoTwoSum(
          Hilo.twoProduct(cdxtail,-bdy),
          Hilo.twoProduct(cdx,-bdytail));

//        bctlen = fast_expansion_sum_zeroelim(4, u, 4, v, bct);
        bct = u.add(v);

//        Two_Product(bdxtail, cdytail, ti1, ti0);
//        Two_Product(cdxtail, bdytail, tj1, tj0);
//        Two_Two_Diff(ti1, ti0, tj1, tj0, bctt3, bctt[2], bctt[1], bctt[0]);
//        bctt[3] = bctt3;
//        bcttlen = 4;
        bctt = XDouble.twoTwoDiff(
          Hilo.twoProduct(bdxtail,cdytail),
          Hilo.twoProduct(cdxtail,bdytail)); }
      else {
        bct = XDouble.ZERO;
        bctt = XDouble.ZERO; }

      if (adxtail != 0.0) {
        { final XDouble axtbct = bct.scale(adxtail);
          { final XDouble temp32a = axtbct.scale(2*adx);
            final XDouble temp48 = axtbc.scale(adxtail).add(temp32a);
            finnow = finnow.add(temp48); }

          if (bdytail != 0.0) {
            final XDouble temp8 = cc.scale(adxtail);
            finnow = finnow.add(temp8.scale(bdytail)); }
          if (cdytail != 0.0) {
            final XDouble temp8 = bb.scale(-adxtail);
            final XDouble temp16a = temp8.scale(cdytail);
            finnow = finnow.add(temp16a); }
          final XDouble temp32a = axtbct.scale(adxtail);
          final XDouble axtbctt = bctt.scale(adxtail);
          final XDouble temp16a = axtbctt.scale(2*adx);
          final XDouble temp16b = axtbctt.scale(adxtail);
          final XDouble temp32b = temp16a.add(temp16b);
          final XDouble temp64 = temp32a.add(temp32b);
          finnow = finnow.add(temp64); }

        if (adytail != 0.0) {
          final XDouble aytbct = bct.scale(adytail);
          { final XDouble temp16a = aytbc.scale(adxtail);
            final XDouble temp32a = aytbct.scale(2*ady);
            final XDouble temp48 = temp16a.add(temp32a);
            finnow = finnow.add(temp48); }


          final XDouble temp32a = aytbct.scale(adytail);
          final XDouble aytbctt = bctt.scale(adytail);
          final XDouble temp16a = aytbctt.scale(2*ady);
          final XDouble temp16b = aytbctt.scale(adytail);
          final XDouble temp32b = temp16a.add(temp16b);
          final XDouble temp64 = temp32a.add(temp32b);
          finnow = finnow.add(temp64); } } }

    final XDouble cat, catt;
    if ((bdxtail != 0.0) || (bdytail != 0.0)) {
      if ((cdxtail != 0.0) || (cdytail != 0.0)
        || (adxtail != 0.0) || (adytail != 0.0)) {
//        Two_Product(cdxtail, ady, ti1, ti0);
//        Two_Product(cdx, adytail, tj1, tj0);
//        Two_Two_Sum(ti1, ti0, tj1, tj0, u3, u[2], u[1], u[0]);
//        u[3] = u3;
        final XDouble u = XDouble.twoTwoSum(
          Hilo.twoProduct(cdxtail,ady),
          Hilo.twoProduct(cdx,adytail));

//        negate = -cdy;
//        Two_Product(adxtail, negate, ti1, ti0);
//        negate = -cdytail;
//        Two_Product(adx, negate, tj1, tj0);
//        Two_Two_Sum(ti1, ti0, tj1, tj0, v3, v[2], v[1], v[0]);
//        v[3] = v3;
        final XDouble v = XDouble.twoTwoSum(
          Hilo.twoProduct(adxtail,-cdy),
          Hilo.twoProduct(adx,-cdytail));

//        catlen = fast_expansion_sum_zeroelim(4, u, 4, v, cat);
        cat = u.add(v);

//        Two_Product(cdxtail, adytail, ti1, ti0);
//        Two_Product(adxtail, cdytail, tj1, tj0);
//        Two_Two_Diff(ti1, ti0, tj1, tj0, catt3, catt[2], catt[1], catt[0]);
//        catt[3] = catt3;
//        cattlen = 4;
        catt = XDouble.twoTwoDiff(
          Hilo.twoProduct(cdxtail,adytail),
          Hilo.twoProduct(adxtail,cdytail)); }
      else {
        cat= XDouble.ZERO;
        catt = XDouble.ZERO; }

      if (bdxtail != 0.0) {
        final XDouble bxtcat = cat.scale(bdxtail);
        { final XDouble temp32a = bxtcat.scale(2.0*bdx);
          final XDouble temp48 = bxtca.scale(bdxtail).add(temp32a);
          finnow = finnow.add(temp48); }

        if (cdytail != 0.0) {
          final XDouble temp8 = aa.scale(bdxtail);
          final XDouble temp16a = temp8.scale(cdytail);
          finnow = finnow.add(temp16a); }
        if (adytail != 0.0) {
          final XDouble temp8 = cc.scale(-bdxtail);
          final XDouble temp16a = temp8.scale(adytail);
          finnow = finnow.add(temp16a); }

        final XDouble temp32a = bxtcat.scale(bdxtail);
        final XDouble bxtcatt = catt.scale(bdxtail);
        final XDouble temp16a = bxtcatt.scale(2*bdx);
        final XDouble temp16b = bxtcatt.scale(bdxtail);
        final XDouble temp32b = temp16a.add(temp16b);
        final XDouble temp64 = temp32a.add(temp32b);
        finnow = finnow.add(temp64); }

      if (bdytail != 0.0) {
        final XDouble bytcat = cat.scale(bdytail);
        {final XDouble temp16a = bytca.scale(bdytail);
          final XDouble temp32a = bytcat.scale(2*bdy);
          final XDouble temp48 = temp16a.add(temp32a);
          finnow = finnow.add(temp48); }
        final XDouble temp32a = bytcat.scale(bdytail);
        final XDouble bytcatt = catt.scale(bdytail);
        final XDouble temp16a = bytcatt.scale(2*bdy);
        final XDouble temp16b = bytcatt.scale(bdytail);
        final XDouble temp32b = temp16a.add(temp16b);
        final XDouble temp64 = temp32a.add(temp32b);
        finnow = finnow.add(temp64); } }

    final XDouble abt, abtt;
    if ((cdxtail != 0.0) || (cdytail != 0.0)) {
      if ((adxtail != 0.0) || (adytail != 0.0)
        || (bdxtail != 0.0) || (bdytail != 0.0)) {
//        Two_Product(adxtail, bdy, ti1, ti0);
//        Two_Product(adx, bdytail, tj1, tj0);
//        Two_Two_Sum(ti1, ti0, tj1, tj0, u3, u[2], u[1], u[0]);
//        u[3] = u3;
        final XDouble u = XDouble.twoTwoSum(
          Hilo.twoProduct(adxtail,bdy),
          Hilo.twoProduct(adx,bdytail));

//        negate = -ady;
//        Two_Product(bdxtail, negate, ti1, ti0);
//        negate = -adytail;
//        Two_Product(bdx, negate, tj1, tj0);
//        Two_Two_Sum(ti1, ti0, tj1, tj0, v3, v[2], v[1], v[0]);
//        v[3] = v3;
        final XDouble v = XDouble.twoTwoSum(
          Hilo.twoProduct(bdxtail,-ady),
          Hilo.twoProduct(bdx,-adytail));

//        abtlen = fast_expansion_sum_zeroelim(4, u, 4, v, abt);
        abt = u.add(v);

//        Two_Product(adxtail, bdytail, ti1, ti0);
//        Two_Product(bdxtail, adytail, tj1, tj0);
//        Two_Two_Diff(ti1, ti0, tj1, tj0, abtt3, abtt[2], abtt[1], abtt[0]);
//        abtt[3] = abtt3;
//        abttlen = 4;
        abtt = XDouble.twoTwoDiff(
          Hilo.twoProduct(adxtail,bdytail),
          Hilo.twoProduct(bdxtail,adytail)); }
      else {
        abt = XDouble.ZERO;
        abtt = XDouble.ZERO; }

      if (cdxtail != 0.0) {
        final XDouble cxtabt = abt.scale(cdxtail);
        { final XDouble temp16a = cxtab.scale(cdxtail);
          final XDouble temp32a = cxtabt.scale(2*cdx);
          final XDouble temp48 = temp16a.add(temp32a);
          finnow = finnow.add(temp48); }
        if (adytail != 0.0) {
          final XDouble temp8 = bb.scale(cdxtail);
          final XDouble temp16a = temp8.scale(adytail);
          finnow = finnow.add(temp16a); }
        if (bdytail != 0.0) {
          final XDouble temp8 = aa.scale(-cdxtail);
          final XDouble temp16a = temp8.scale(bdytail);
          finnow = finnow.add(temp16a); }

        final XDouble temp32a = cxtabt.scale(cdxtail);
        final XDouble cxtabtt = abtt.scale(cdxtail);
        final XDouble temp16a = cxtabtt.scale(2*cdx);
        final XDouble temp16b = cxtabtt.scale(cdxtail);
        final XDouble temp32b = temp16a.add(temp16b);
        final XDouble temp64 = temp32a.add(temp32b);
        finnow = finnow.add(temp64); }

      if (cdytail != 0.0) {
        final XDouble cytabt = abt.scale(cdytail);
        { final XDouble temp16a = cytab.scale(cdytail);
          final XDouble temp32a = cytabt.scale(2*cdy);
          final XDouble temp48 = temp16a.add(temp32a);
          finnow = finnow.add(temp48); }

        final XDouble temp32a = cytabt.scale(cdytail);
        final XDouble cytabtt = abtt.scale(cdytail);
        final XDouble temp16a = cytabtt.scale(2*cdy);
        final XDouble temp16b = cytabtt.scale(cdytail);
        final XDouble temp32b = temp16a.add(temp16b);
        final XDouble temp64 = temp32a.add(temp32b);
        finnow = finnow.add(temp64); } }

    return finnow.doubleValue(); }

  //--------------------------------------------------------------------

  public final double incircle (final double[] pa,
                                final double[] pb,
                                final double[] pc,
                                final double[] pd) {
    final double adx = pa[0] - pd[0];
    final double ady = pa[1] - pd[1];
    final double alift = (adx * adx) + (ady * ady);

    final double bdx = pb[0] - pd[0];
    final double bdy = pb[1] - pd[1];
    final double blift = (bdx * bdx) + (bdy * bdy);

    final double cdx = pc[0] - pd[0];
    final double cdy = pc[1] - pd[1];
    final double clift = (cdx * cdx) + (cdy * cdy);

    final double bdxcdy = bdx * cdy;
    final double cdxbdy = cdx * bdy;

    final double cdxady = cdx * ady;
    final double adxcdy = adx * cdy;

    final double adxbdy = adx * bdy;
    final double bdxady = bdx * ady;

    final double det
      = alift * (bdxcdy - cdxbdy)
      + blift * (cdxady - adxcdy)
      + clift * (adxbdy - bdxady);

    final double permanent
      = ((Math.abs(bdxcdy) + Math.abs(cdxbdy) ) * alift)
      + ((Math.abs(cdxady) + Math.abs(adxcdy)) * blift)
      + ((Math.abs(adxbdy) + Math.abs(bdxady)) * clift);

    final double errbound = iccerrboundA * permanent;
    if ((det > errbound) || (-det > errbound)) { return det; }
    return incircle(pa, pb, pc, pd, permanent); }

  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------
  private static final double resulterrbound =
    (3.0 + 8.0 * EPSILON) * EPSILON;

  private static final double ccwerrboundB =
    (2.0 + 12*EPSILON) * EPSILON;

  private static final double ccwerrboundC =
    (9.0 + 64.0 * EPSILON) * EPSILON * EPSILON;

  private static final double orient2d (final double[] pa,
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

    acx = (pa[0] - pc[0]);
    bcx = (pb[0] - pc[0]);
    acy = (pa[1] - pc[1]);
    bcy = (pb[1] - pc[1]);

    detleft = (acx * bcy);
    c = (SPLITTER * acx);
    abig = (c - acx);
    ahi = c - abig;
    alo = acx - ahi;
    c = (SPLITTER * bcy);
    abig = (c - bcy);
    bhi = c - abig;
    blo = bcy - bhi;
    err1 = detleft - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    detlefttail = (alo * blo) - err3;
    detright = (acy * bcx);
    c = (SPLITTER * acy);
    abig = (c - acy);
    ahi = c - abig;
    alo = acy - ahi;
    c = (SPLITTER * bcx);
    abig = (c - bcx);
    bhi = c - abig;
    blo = bcx - bhi;
    err1 = detright - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    detrighttail = (alo * blo) - err3;

    _i = (detlefttail - detrighttail);
    bvirt = (detlefttail - _i);
    avirt = _i + bvirt;
    bround = bvirt - detrighttail;
    around = detlefttail - avirt;
    B[0] = around + bround;
    _j = (detleft + _i);
    bvirt = (_j - detleft);
    avirt = _j - bvirt;
    bround = _i - bvirt;
    around = detleft - avirt;
    _0 = around + bround;
    _i = (_0 - detright);
    bvirt = (_0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - detright;
    around = _0 - avirt;
    B[1] = around + bround;
    B3 = (_j + _i);
    bvirt = (B3 - _j);
    avirt = B3 - bvirt;
    bround = _i - bvirt;
    around = _j - avirt;
    B[2] = around + bround
    ;
    B[3] = B3;

    det = estimate(4, B);
    errbound = ccwerrboundB * detsum;
    if (Math.abs(det) >= errbound) {
      return det;
    }

    bvirt = (pa[0] - acx);
    avirt = acx + bvirt;
    bround = bvirt - pc[0];
    around = pa[0] - avirt;
    acxtail = around + bround;
    bvirt = (pb[0] - bcx);
    avirt = bcx + bvirt;
    bround = bvirt - pc[0];
    around = pb[0] - avirt;
    bcxtail = around + bround;
    bvirt = (pa[1] - acy);
    avirt = acy + bvirt;
    bround = bvirt - pc[1];
    around = pa[1] - avirt;
    acytail = around + bround;
    bvirt = (pb[1] - bcy);
    avirt = bcy + bvirt;
    bround = bvirt - pc[1];
    around = pb[1] - avirt;
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
    if (Math.abs(det) >= errbound) {
      return det;
    }

    s1 = (acxtail * bcy);
    c = (SPLITTER * acxtail);
    abig = (c - acxtail);
    ahi = c - abig;
    alo = acxtail - ahi;
    c = (SPLITTER * bcy);
    abig = (c - bcy);
    bhi = c - abig;
    blo = bcy - bhi;
    err1 = s1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    s0 = (alo * blo) - err3;
    t1 = (acytail * bcx);
    c = (SPLITTER * acytail);
    abig = (c - acytail);
    ahi = c - abig;
    alo = acytail - ahi;
    c = (SPLITTER * bcx);
    abig = (c - bcx);
    bhi = c - abig;
    blo = bcx - bhi;
    err1 = t1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    t0 = (alo * blo) - err3;
    _i = (s0 - t0);
    bvirt = (s0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - t0;
    around = s0 - avirt;
    u[0] = around + bround;
    _j = (s1 + _i);
    bvirt = (_j - s1);
    avirt = _j - bvirt;
    bround = _i - bvirt;
    around = s1 - avirt;
    _0 = around + bround;
    _i = (_0 - t1);
    bvirt = (_0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - t1;
    around = _0 - avirt;
    u[1] = around + bround;
    u3 = (_j + _i);
    bvirt = (u3 - _j);
    avirt = u3 - bvirt;
    bround = _i - bvirt;
    around = _j - avirt;
    u[2] = around + bround;
    u[3] = u3;
    C1length = sum(4, B, 4, u, C1);

    s1 = (acx * bcytail);
    c = (SPLITTER * acx);
    abig = (c - acx);
    ahi = c - abig;
    alo = acx - ahi;
    c = (SPLITTER * bcytail);
    abig = (c - bcytail);
    bhi = c - abig;
    blo = bcytail - bhi;
    err1 = s1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    s0 = (alo * blo) - err3;
    t1 = (acy * bcxtail);
    c = (SPLITTER * acy);
    abig = (c - acy);
    ahi = c - abig;
    alo = acy - ahi;
    c = (SPLITTER * bcxtail);
    abig = (c - bcxtail);
    bhi = c - abig;
    blo = bcxtail - bhi;
    err1 = t1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    t0 = (alo * blo) - err3;
    _i = (s0 - t0);
    bvirt = (s0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - t0;
    around = s0 - avirt;
    u[0] = around + bround;
    _j = (s1 + _i);
    bvirt = (_j - s1);
    avirt = _j - bvirt;
    bround = _i - bvirt;
    around = s1 - avirt;
    _0 = around + bround;
    _i = (_0 - t1);
    bvirt = (_0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - t1;
    around = _0 - avirt;
    u[1] = around + bround;
    u3 = (_j + _i);
    bvirt = (u3 - _j);
    avirt = u3 - bvirt;
    bround = _i - bvirt;
    around = _j - avirt;
    u[2] = around + bround;
    u[3] = u3;
    C2length = sum(C1length, C1, 4, u, C2);

    s1 = (acxtail * bcytail);
    c = (SPLITTER * acxtail);
    abig = (c - acxtail);
    ahi = c - abig;
    alo = acxtail - ahi;
    c = (SPLITTER * bcytail);
    abig = (c - bcytail);
    bhi = c - abig;
    blo = bcytail - bhi;
    err1 = s1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    s0 = (alo * blo) - err3;
    t1 = (acytail * bcxtail);
    c = (SPLITTER * acytail);
    abig = (c - acytail);
    ahi = c - abig;
    alo = acytail - ahi;
    c = (SPLITTER * bcxtail);
    abig = (c - bcxtail);
    bhi = c - abig;
    blo = bcxtail - bhi;
    err1 = t1 - (ahi * bhi);
    err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo);
    t0 = (alo * blo) - err3;
    _i = (s0 - t0);
    bvirt = (s0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - t0;
    around = s0 - avirt;
    u[0] = around + bround;
    _j = (s1 + _i);
    bvirt = (_j - s1);
    avirt = _j - bvirt;
    bround = _i - bvirt;
    around = s1 - avirt;
    _0 = around + bround;
    _i = (_0 - t1);
    bvirt = (_0 - _i);
    avirt = _i + bvirt;
    bround = bvirt - t1;
    around = _0 - avirt;
    u[1] = around + bround;
    u3 = (_j + _i);
    bvirt = (u3 - _j);
    avirt = u3 - bvirt;
    bround = _i - bvirt;
    around = _j - avirt;
    u[2] = around + bround;
    u[3] = u3;
    Dlength = sum(C2length, C2, 4, u, D);

    return (D[Dlength - 1]);
  }

  //--------------------------------------------------------------------
  private static final double ccwerrboundA =
    (3.0 + 16.0 * EPSILON) * EPSILON;

  private static final double o3derrboundA =
    (7.0 + 56.0 * EPSILON) * EPSILON;

  public final double orient2d (final double[] pa,
                                final double[] pb,
                                final double[] pc) {

    final double detleft = (pa[0] - pc[0]) * (pb[1] - pc[1]);
    final double detright = (pa[1] - pc[1]) * (pb[0] - pc[0]);
    final double det = detleft - detright;
    final double detsum;
    if (detleft > 0.0) {
      if (detright <= 0.0) { return det; }
      else { detsum = detleft + detright; } }
    else if (detleft < 0.0) {
      if (detright >= 0.0) { return det; }
      else { detsum = - (detleft + detright); } }
    else { return det; }

    final double errbound = ccwerrboundA * detsum;
    if (Math.abs(det) >= errbound) { return det; }

    return orient2d(pa, pb, pc, detsum); }

  //--------------------------------------------------------------------
  // orient3d
  //--------------------------------------------------------------------
  private static final double o3derrboundB =
    (3.0 + 28.0 * EPSILON) * EPSILON;
  private static final double o3derrboundC =
    (26.0 + 288.0 * EPSILON) * EPSILON * EPSILON;

  //--------------------------------------------------------------------

  public final double orient3d (final double[] pa,
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

    adx = (pa[0] - pd[0]);
    bdx = (pb[0] - pd[0]);
    cdx = (pc[0] - pd[0]);
    ady = (pa[1] - pd[1]);
    bdy = (pb[1] - pd[1]);
    cdy = (pc[1] - pd[1]);
    adz = (pa[2] - pd[2]);
    bdz = (pb[2] - pd[2]);
    cdz = (pc[2] - pd[2]);

    bdxcdy1 = (bdx * cdy);
    c = (SPLITTER * bdx);
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
    alen = scale(4, bc, adz, adet);

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
    blen = scale(4, ca, bdz, bdet);

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
    clen = scale(4, ab, cdz, cdet);

    ablen = sum(alen, adet, blen, bdet, abdet);
    finlength =
      sum(ablen, abdet, clen, cdet, fin1);

    det = estimate(finlength, fin1);
    errbound = o3derrboundB * permanent;
    if (Math.abs(det) >= errbound) {
      return det;
    }

    bvirt = (pa[0] - adx);
    avirt = adx + bvirt;
    bround = bvirt - pd[0];
    around = pa[0] - avirt;
    adxtail = around + bround;
    bvirt = (pb[0] - bdx);
    avirt = bdx + bvirt;
    bround = bvirt - pd[0];
    around = pb[0] - avirt;
    bdxtail = around + bround;
    bvirt = (pc[0] - cdx);
    avirt = cdx + bvirt;
    bround = bvirt - pd[0];
    around = pc[0] - avirt;
    cdxtail = around + bround;
    bvirt = (pa[1] - ady);
    avirt = ady + bvirt;
    bround = bvirt - pd[1];
    around = pa[1] - avirt;
    adytail = around + bround;
    bvirt = (pb[1] - bdy);
    avirt = bdy + bvirt;
    bround = bvirt - pd[1];
    around = pb[1] - avirt;
    bdytail = around + bround;
    bvirt = (pc[1] - cdy);
    avirt = cdy + bvirt;
    bround = bvirt - pd[1];
    around = pc[1] - avirt;
    cdytail = around + bround;
    bvirt = (pa[2] - adz);
    avirt = adz + bvirt;
    bround = bvirt - pd[2];
    around = pa[2] - avirt;
    adztail = around + bround;
    bvirt = (pb[2] - bdz);
    avirt = bdz + bvirt;
    bround = bvirt - pd[2];
    around = pb[2] - avirt;
    bdztail = around + bround;
    bvirt = (pc[2] - cdz);
    avirt = cdz + bvirt;
    bround = bvirt - pd[2];
    around = pc[2] - avirt;
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
    if (Math.abs(det) >= errbound) {
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
      sum(bt_clen, bt_c, ct_blen, ct_b, bct);
    wlength = scale(bctlen, bct, adz, w);
    finlength =
      sum(finlength, finnow, wlength, w,
          finother);
    finswap = finnow;
    finnow = finother;
    finother = finswap;

    catlen =
      sum(ct_alen, ct_a, at_clen, at_c, cat);
    wlength = scale(catlen, cat, bdz, w);
    finlength =
      sum(finlength, finnow, wlength, w,
          finother);
    finswap = finnow;
    finnow = finother;
    finother = finswap;

    abtlen =
      sum(at_blen, at_b, bt_alen, bt_a, abt);
    wlength = scale(abtlen, abt, cdz, w);
    finlength =
      sum(finlength, finnow, wlength, w,
          finother);
    finswap = finnow;
    finnow = finother;
    finother = finswap;

    if (adztail != 0.0) {
      vlength = scale(4, bc, adztail, v);
      finlength =
        sum(finlength, finnow, vlength, v,
            finother);
      finswap = finnow;
      finnow = finother;
      finother = finswap;
    }
    if (bdztail != 0.0) {
      vlength = scale(4, ca, bdztail, v);
      finlength =
        sum(finlength, finnow, vlength, v,
            finother);
      finswap = finnow;
      finnow = finother;
      finother = finswap;
    }
    if (cdztail != 0.0) {
      vlength = scale(4, ab, cdztail, v);
      finlength =
        sum(finlength, finnow, vlength, v,
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
        finlength = sum(finlength, finnow, 4, u,
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
            sum(finlength, finnow, 4, u,
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
        finlength = sum(finlength, finnow, 4, u,
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
            sum(finlength, finnow, 4, u,
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
        finlength = sum(finlength, finnow, 4, u,
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
            sum(finlength, finnow, 4, u,
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
        finlength = sum(finlength, finnow, 4, u,
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
            sum(finlength, finnow, 4, u,
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
        finlength = sum(finlength, finnow, 4, u,
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
            sum(finlength, finnow, 4, u,
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
        finlength = sum(finlength, finnow, 4, u,
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
            sum(finlength, finnow, 4, u,
                finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;
        }
      }
    }

    if (adztail != 0.0) {
      wlength = scale(bctlen, bct, adztail, w);
      finlength =
        sum(finlength, finnow, wlength, w,
            finother);
      finswap = finnow;
      finnow = finother;
      finother = finswap;
    }
    if (bdztail != 0.0) {
      wlength = scale(catlen, cat, bdztail, w);
      finlength =
        sum(finlength, finnow, wlength, w,
            finother);
      finswap = finnow;
      finnow = finother;
      finother = finswap;
    }
    if (cdztail != 0.0) {
      wlength = scale(abtlen, abt, cdztail, w);
      finlength =
        sum(finlength, finnow, wlength, w,
            finother);
      //finswap = finnow;
      finnow = finother;
      // TODO: unused?
      //finother = finswap;
    }

    return finnow[finlength - 1];
  }

  public final double orient3d (final double[] pa,
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

    return new Adapt().orient3d(pa, pb, pc, pd, permanent);
  }

  //--------------------------------------------------------------------
  // insphere
  //--------------------------------------------------------------------
  private static final double isperrboundB =
    (5.0 + 72.0 * EPSILON) * EPSILON;
  private static final double isperrboundC =
    (71.0 + 1408.0 * EPSILON) * EPSILON * EPSILON;

  private static final double insphere (final double[] pa,
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

    aex = (pa[0] - pe[0]);
    bex = (pb[0] - pe[0]);
    cex = (pc[0] - pe[0]);
    dex = (pd[0] - pe[0]);
    aey = (pa[1] - pe[1]);
    bey = (pb[1] - pe[1]);
    cey = (pc[1] - pe[1]);
    dey = (pd[1] - pe[1]);
    aez = (pa[2] - pe[2]);
    bez = (pb[2] - pe[2]);
    cez = (pc[2] - pe[2]);
    dez = (pd[2] - pe[2]);

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
    if (Math.abs(det) >= errbound) {
      return det;
    }

    bvirt = (pa[0] - aex);
    avirt = aex + bvirt;
    bround = bvirt - pe[0];
    around = pa[0] - avirt;
    aextail = around + bround;
    bvirt = (pa[1] - aey);
    avirt = aey + bvirt;
    bround = bvirt - pe[1];
    around = pa[1] - avirt;
    aeytail = around + bround;
    bvirt = (pa[2] - aez);
    avirt = aez + bvirt;
    bround = bvirt - pe[2];
    around = pa[2] - avirt;
    aeztail = around + bround;
    bvirt = (pb[0] - bex);
    avirt = bex + bvirt;
    bround = bvirt - pe[0];
    around = pb[0] - avirt;
    bextail = around + bround;
    bvirt = (pb[1] - bey);
    avirt = bey + bvirt;
    bround = bvirt - pe[1];
    around = pb[1] - avirt;
    beytail = around + bround;
    bvirt = (pb[2] - bez);
    avirt = bez + bvirt;
    bround = bvirt - pe[2];
    around = pb[2] - avirt;
    beztail = around + bround;
    bvirt = (pc[0] - cex);
    avirt = cex + bvirt;
    bround = bvirt - pe[0];
    around = pc[0] - avirt;
    cextail = around + bround;
    bvirt = (pc[1] - cey);
    avirt = cey + bvirt;
    bround = bvirt - pe[1];
    around = pc[1] - avirt;
    ceytail = around + bround;
    bvirt = (pc[2] - cez);
    avirt = cez + bvirt;
    bround = bvirt - pe[2];
    around = pc[2] - avirt;
    ceztail = around + bround;
    bvirt = (pd[0] - dex);
    avirt = dex + bvirt;
    bround = bvirt - pe[0];
    around = pd[0] - avirt;
    dextail = around + bround;
    bvirt = (pd[1] - dey);
    avirt = dey + bvirt;
    bround = bvirt - pe[1];
    around = pd[1] - avirt;
    deytail = around + bround;
    bvirt = (pd[2] - dez);
    avirt = dez + bvirt;
    bround = bvirt - pe[2];
    around = pd[2] - avirt;
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
    if (Math.abs(det) >= errbound) {
      return det;
    }

    return new Exact().insphere(pa, pb, pc, pd, pe);
  }

  //--------------------------------------------------------------------
  private static final double isperrboundA =
    (16.0 + 224.0 * EPSILON) * EPSILON;

  public final double insphere (final double[] pa,
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

    return insphere(pa, pb, pc, pd, pe, permanent);
  }
  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public Adapt () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
