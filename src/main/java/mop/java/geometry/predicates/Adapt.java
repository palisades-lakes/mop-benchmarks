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
 * @version 2026-06-29
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

  private static final XDouble fininc (final XDouble bb,
                                       final double adxtail,
                                       final double cdy,
                                       final XDouble axtbc,
                                       final double adx,
                                       final XDouble cc,
                                       final double bdy) {
    // TODO: XDouble.scaleTimes(double,double) ->
    //  XDouble.scale(Hilo.twoProduct(a,v))
    //  intermediate Hilo instance rather than XDouble
    return bb.multiply(adxtail).multiply(-cdy)
             .add(axtbc.multiply(2*adx))
             .add(cc.multiply(adxtail).multiply(bdy)); }

  private static final double incircle (final double[] pa,
                                        final double[] pb,
                                        final double[] pc,
                                        final double[] pd,
                                        final double permanent) {
    // TODO: should this be Hilo.twoDiff? see calls to towDiffTail below
    // TODO: convert to vector ops.
    // TODO: subtract d from other vecs before calling
    final double adx = (pa[0] - pd[0]);
    final double ady = (pa[1] - pd[1]);
    final double bdx = (pb[0] - pd[0]);
    final double bdy = (pb[1] - pd[1]);
    final double cdx = (pc[0] - pd[0]);
    final double cdy = (pc[1] - pd[1]);

    // TODO: XDouble.crossProduct?
    final XDouble bc = XDouble.twoTwoDiff(
      Hilo.product(bdx, cdy),
      Hilo.product(cdx, bdy));
    // TODO: XDouble l2norm2, scale2
    final XDouble adet = bc.multiply(adx).multiply(adx)
                           .add(bc.multiply(ady).multiply(ady));

    final XDouble ca = XDouble.twoTwoDiff(
      Hilo.product(cdx, ady),
      Hilo.product(adx, cdy));
    final XDouble bdet = ca.multiply(bdx).multiply(bdx)
                           .add(
                             ca.multiply(bdy).multiply(bdy));

    final XDouble ab = XDouble.twoTwoDiff(
      Hilo.product(adx, bdy),
      Hilo.product(bdx, ady));
    final XDouble cdet = ab.multiply(cdx).multiply(cdx)
                           .add(ab.multiply(cdy).multiply(cdy));

    XDouble finnow =  adet.add(bdet).add(cdet);

    double det = finnow.doubleValue();
    if (Math.abs(det) >= iccerrboundB * permanent) { return det; }

    final double adxtail = Hilo.twoDiffTail(pa[0], pd[0], adx);
    final double adytail = Hilo.twoDiffTail(pa[1], pd[1], ady);
    final double bdxtail = Hilo.twoDiffTail(pb[0], pd[0], bdx);
    final double bdytail = Hilo.twoDiffTail(pb[1], pd[1], bdy);
    final double cdxtail = Hilo.twoDiffTail(pc[0], pd[0], cdx);
    final double cdytail = Hilo.twoDiffTail(pc[1], pd[1], cdy);

    final boolean axtail = (adxtail != 0.0);
    final boolean aytail = (adytail != 0.0);
    final boolean atail = axtail || aytail;
    final boolean bxtail = (bdxtail != 0.0);
    final boolean bytail = (bdytail != 0.0);
    final boolean btail = bxtail || bytail;
    final boolean cxtail = (cdxtail != 0.0);
    final boolean cytail = (cdytail != 0.0);
    final boolean ctail = cxtail || cytail;

    if (! (atail || btail || ctail)) { return det; }

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

    final XDouble aa =
      (btail || ctail) ? XDouble.l2norm2(adx,ady) : XDouble.ZERO;

    final XDouble bb =
      (ctail || atail) ? XDouble.l2norm2(bdx,bdy) : XDouble.ZERO;

    final XDouble cc =
      (atail || btail) ? XDouble.l2norm2(cdx,cdy) : XDouble.ZERO;

    final XDouble axtbc;
    if (axtail) {
      axtbc = bc.multiply(adxtail);
      finnow = finnow.add(fininc(bb,adxtail,cdy,axtbc,adx,cc,bdy)); }
    else { axtbc = XDouble.ZERO; }

    final XDouble aytbc;
    if (aytail) {
      aytbc = bc.multiply(adytail);
      finnow = finnow.add( fininc(cc,adytail,cdy,aytbc,ady,bb,bdx)); }
    else { aytbc = XDouble.ZERO; }

    final XDouble bxtca;
    if (bxtail) {
      bxtca = ca.multiply(bdxtail);
      finnow = finnow.add(fininc(cc,bdxtail,ady,bxtca,bdx,aa,cdy)); }
    else { bxtca = XDouble.ZERO; }

    final XDouble bytca;
    if (bytail) {
      bytca = ca.multiply(bdytail);
      finnow = finnow.add(fininc(aa,bdytail,cdx,bytca,bdy,cc,adx)); }
    else { bytca = XDouble.ZERO; }

    final XDouble cxtab;
    if (cxtail) {
      cxtab = ab.multiply(cdxtail);
      finnow = finnow.add(fininc(aa,cdxtail,bdy,cxtab,cdx,bb,ady)); }
    else { cxtab = XDouble.ZERO; }

    final XDouble cytab;
    if (cytail) {
      cytab = ab.multiply(cdytail);
      finnow = finnow.add(fininc(bb,cdytail,adx,cytab,cdy,aa,bdx)); }
    else { cytab = XDouble.ZERO; }

    final XDouble bct, bctt;
    if (atail) {
      if (btail || ctail) {
        final XDouble u = XDouble.twoTwoSum(
          Hilo.product(bdxtail, cdy),
          Hilo.product(bdx, cdytail));
        final XDouble v = XDouble.twoTwoSum(
          Hilo.product(cdxtail, -bdy),
          Hilo.product(cdx, -bdytail));
        bct = u.add(v);
        bctt = XDouble.twoTwoDiff(
          Hilo.product(bdxtail, cdytail),
          Hilo.product(cdxtail, bdytail)); }
      else { bct = bctt = XDouble.ZERO; }

      if (axtail) {
        { final XDouble axtbct = bct.multiply(adxtail);
          finnow = finnow.add(
            axtbc.multiply(adxtail).add(axtbct.multiply(2*adx)));
          if (bytail) {
            finnow = finnow.add(
              cc.multiply(adxtail).multiply(bdytail)); }
          if (cytail) {
            finnow = finnow.add(
              bb.multiply(-adxtail).multiply(cdytail)); }

          final XDouble axtbctt = bctt.multiply(adxtail);
          // TODO: XDouble.linearCombination?
          finnow = finnow.add(
            axtbct.multiply(adxtail)
                  .add(axtbctt.multiply(2*adx))
                  .add(axtbctt.multiply(adxtail))); }

        if (aytail) {
          final XDouble aytbct = bct.multiply(adytail);
          finnow = finnow.add(
            aytbc.multiply(adxtail)
                 .add(aytbct.multiply(2*ady)));
          final XDouble aytbctt = bctt.multiply(adytail);
          finnow = finnow.add(
            aytbct.multiply(adytail)
                  .add(aytbctt.multiply(2*ady))
                  .add(aytbctt.multiply(adytail))); } } }

    final XDouble cat, catt;
    if (btail) {
      if (ctail || atail) {
        final XDouble u = XDouble.twoTwoSum(
          Hilo.product(cdxtail, ady),
          Hilo.product(cdx, adytail));
        final XDouble v = XDouble.twoTwoSum(
          Hilo.product(adxtail, -cdy),
          Hilo.product(adx, -cdytail));
        cat = u.add(v);
        catt = XDouble.twoTwoDiff(
          Hilo.product(cdxtail, adytail),
          Hilo.product(adxtail, cdytail)); }
      else { cat = catt = XDouble.ZERO; }

      if (bxtail) {
        final XDouble bxtcat = cat.multiply(bdxtail);
        finnow = finnow.add(
          bxtca.multiply(bdxtail).add(bxtcat.multiply(2.0*bdx)));
        if (cytail) {
          finnow = finnow.add(
            aa.multiply(bdxtail).multiply(cdytail)); }
        if (aytail) {
          finnow = finnow.add(
            cc.multiply(-bdxtail).multiply(adytail)); }

        final XDouble bxtcatt = catt.multiply(bdxtail);
        finnow = finnow.add(
          bxtcat.multiply(bdxtail)
                .add(bxtcatt.multiply(2*bdx))
                .add(bxtcatt.multiply(bdxtail))); }

      if (bytail) {
        final XDouble bytcat = cat.multiply(bdytail);
        finnow = finnow.add(
          bytca.multiply(bdytail).add(bytcat.multiply(2*bdy)));
        final XDouble bytcatt = catt.multiply(bdytail);
        // TODO: XDouble.scalePlus(double,double) ->
        //  XDouble.scale(Hilo.twoSum(a,v))
        //  intermediate Hilo instance rather than XDouble
        finnow = finnow.add(
          bytcat.multiply(bdytail)
                .add(bytcatt.multiply(2*bdy))
                .add(bytcatt.multiply(bdytail))); } }

    final XDouble abt, abtt;
    if (ctail) {
      if (atail || btail) {
        final XDouble u = XDouble.twoTwoSum(
          Hilo.product(adxtail, bdy), Hilo.product(adx, bdytail));

        final XDouble v = XDouble.twoTwoSum(
          Hilo.product(bdxtail, -ady), Hilo.product(bdx, -adytail));
        abt = u.add(v);
        abtt = XDouble.twoTwoDiff(
          Hilo.product(adxtail, bdytail),
          Hilo.product(bdxtail, adytail)); }
      else { abt = abtt = XDouble.ZERO; }

      if (cxtail) {
        final XDouble cxtabt = abt.multiply(cdxtail);
        finnow = finnow.add(
          cxtab.multiply(cdxtail).add(cxtabt.multiply(2*cdx)));
        if (aytail) {
          finnow = finnow.add(
            bb.multiply(cdxtail).multiply(adytail)); }
        if (bytail) {
          finnow = finnow.add(
            aa.multiply(-cdxtail).multiply(bdytail)); }

        final XDouble cxtabtt = abtt.multiply(cdxtail);
        finnow = finnow.add(
          cxtabt.multiply(cdxtail)
                .add(cxtabtt.multiply(2*cdx))
                .add(cxtabtt.multiply(cdxtail))); }

      if (cytail) {
        final XDouble cytabt = abt.multiply(cdytail);
        finnow = finnow.add(
          cytab.multiply(cdytail).add(cytabt.multiply(2*cdy)));
        final XDouble cytabtt = abtt.multiply(cdytail);
        finnow = finnow.add(
          cytabt.multiply(cdytail)
                .add(cytabtt.multiply(2*cdy))
                .add(cytabtt.multiply(cdytail))); } }

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
      = ((Math.abs(bdxcdy) + Math.abs(cdxbdy)) * alift)
      + ((Math.abs(cdxady) + Math.abs(adxcdy)) * blift)
      + ((Math.abs(adxbdy) + Math.abs(bdxady)) * clift);

    final double errbound = iccerrboundA * permanent;
    if (Math.abs(det) > errbound) { return det; }
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

    final double acx = (pa[0] - pc[0]);
    final double bcx = (pb[0] - pc[0]);
    final double acy = (pa[1] - pc[1]);
    final double bcy = (pb[1] - pc[1]);

    final Hilo detleft = Hilo.product(acx,bcy);
    final Hilo detright = Hilo.product(acy,bcx);
    final XDouble B = XDouble.twoTwoDiff(detleft,detright);

    double det = B.doubleValue();
    double errbound = ccwerrboundB * detsum;
    if (Math.abs(det) >= errbound) { return det; }

    final double acxtail = Hilo.twoDiffTail(pa[0],pc[0],acx);
    final double bcxtail = Hilo.twoDiffTail(pb[0],pc[0],bcx);
    final double acytail = Hilo.twoDiffTail(pa[1],pc[1],acy);
    final double bcytail = Hilo.twoDiffTail(pb[1],pc[1],bcy);
    if ((acxtail == 0.0) && (acytail == 0.0)
      && (bcxtail == 0.0) && (bcytail == 0.0)) {
      return det; }

    errbound = (ccwerrboundC*detsum) + (resulterrbound*Math.abs(det));
    det += (acx*bcytail + bcy*acxtail) - (acy*bcxtail + bcx*acytail);
    if (Math.abs(det) >= errbound) { return det; }

    return B.add(
              XDouble.twoTwoDiff(
                Hilo.product(acxtail, bcy),
                Hilo.product(acytail, bcx)))
            .add(
              XDouble.twoTwoDiff(
                Hilo.product(acx, bcytail),
                Hilo.product(acy, bcxtail)))
            .add(
              XDouble.twoTwoDiff(
                Hilo.product(acxtail, bcytail),
                Hilo.product(acytail, bcxtail)))
            .doubleValue(); }

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
    final double adx = (pa[0] - pd[0]);
    final double bdx = (pb[0] - pd[0]);
    final double cdx = (pc[0] - pd[0]);
    final double ady = (pa[1] - pd[1]);
    final double bdy = (pb[1] - pd[1]);
    final double cdy = (pc[1] - pd[1]);
    final double adz = (pa[2] - pd[2]);
    final double bdz = (pb[2] - pd[2]);
    final double cdz = (pc[2] - pd[2]);

    final Hilo bdxcdy = Hilo.product(bdx,cdy);
    final Hilo cdxbdy = Hilo.product(cdx,bdy);
    final XDouble bc = XDouble.twoTwoDiff(bdxcdy,cdxbdy);
    final XDouble adet = bc.multiply(adz);
    final Hilo cdxady = Hilo.product(cdx,ady);
    final Hilo adxcdy = Hilo.product(adx,cdy);
    final XDouble ca = XDouble.twoTwoDiff(cdxady,adxcdy);
    final XDouble bdet = ca.multiply(bdz);
    final Hilo adxbdy = Hilo.product(adx,bdy);
    final Hilo bdxady = Hilo.product(bdx,ady);
    final XDouble ab = XDouble.twoTwoDiff(adxbdy,bdxady);
    final XDouble cdet = ab.multiply(cdz);
    XDouble finnow = adet.add(bdet).add(cdet);
    double det = finnow.doubleValue();
    double errbound = o3derrboundB * permanent;
    if (Math.abs(det) >= errbound) { return det; }

    final double adxtail = Hilo.twoDiffTail(pa[0],pd[0],adx);
    final double bdxtail = Hilo.twoDiffTail(pb[0],pd[0],bdx);
    final double cdxtail = Hilo.twoDiffTail(pc[0],pd[0],cdx);
    final double adytail = Hilo.twoDiffTail(pa[1],pd[1],ady);
    final double bdytail = Hilo.twoDiffTail(pb[1],pd[1],bdy);
    final double cdytail = Hilo.twoDiffTail(pc[1],pd[1],cdy);
    final double adztail = Hilo.twoDiffTail(pa[2],pd[2],adz);
    final double bdztail = Hilo.twoDiffTail(pb[2],pd[2],bdz);
    final double cdztail = Hilo.twoDiffTail(pc[2],pd[2],cdz);

    if ((adxtail == 0.0) && (bdxtail == 0.0) && (cdxtail == 0.0)
      && (adytail == 0.0) && (bdytail == 0.0) && (cdytail == 0.0)
      && (adztail == 0.0) && (bdztail == 0.0) && (cdztail == 0.0)) {
      return det; }

    errbound = o3derrboundC*permanent + resulterrbound*Math.abs(det);
    det += (adz * ((bdx * cdytail + cdy * bdxtail)
      - (bdy * cdxtail + cdx * bdytail))
      + adztail * (bdx * cdy - bdy * cdx))
      + (bdz * ((cdx * adytail + ady * cdxtail)
      - (cdy * adxtail + adx * cdytail))
      + bdztail * (cdx * ady - cdy * adx))
      + (cdz * ((adx * bdytail + bdy * adxtail)
      - (ady * bdxtail + bdx * adytail))
      + cdztail * (adx * bdy - ady * bdx));
    if (Math.abs(det) >= errbound) { return det; }

    final XDouble at_b, at_c;
    if (adxtail == 0.0) {
      if (adytail == 0.0) { at_b = at_c = XDouble.ZERO; }
      else {
        at_b = XDouble.twoProduct(-adytail, bdx);
        at_c = XDouble.twoProduct(adytail, cdx); } }
    else {
      if (adytail == 0.0) {
        at_b = XDouble.twoProduct(adxtail, bdy);
        at_c = XDouble.twoProduct(-adxtail, cdy);}
      else {
        at_b = XDouble.twoTwoDiff(Hilo.product(adxtail, bdy),
                                  Hilo.product(adytail, bdx));
        at_c = XDouble.twoTwoDiff(Hilo.product(adytail, cdx),
                                  Hilo.product(adxtail, cdy)); } }

    final XDouble bt_c, bt_a;
    if (bdxtail == 0.0) {
      if (bdytail == 0.0) { bt_c = bt_a = XDouble.ZERO; }
      else {
        bt_c = XDouble.twoProduct(-bdytail, cdx);
        bt_a = XDouble.twoProduct(bdytail, adx); } }
    else {
      if (bdytail == 0.0) {
        bt_c = XDouble.twoProduct(bdxtail, cdy);
        bt_a = XDouble.twoProduct(-bdxtail, ady);}
      else {
        bt_c = XDouble.twoTwoDiff(Hilo.product(bdxtail, cdy),
                                  Hilo.product(bdytail, cdx));
        bt_a = XDouble.twoTwoDiff(Hilo.product(bdytail, adx),
                                  Hilo.product(bdxtail, ady)); } }

    final XDouble ct_a, ct_b;
    if (cdxtail == 0.0) {
      if (cdytail == 0.0) { ct_a = ct_b = XDouble.ZERO; }
      else {
        ct_a = XDouble.twoProduct(-cdytail, adx);
        ct_b = XDouble.twoProduct(cdytail, bdx); } }
    else {
      if (cdytail == 0.0) {
        ct_a = XDouble.twoProduct(cdxtail, ady);
        ct_b = XDouble.twoProduct(-cdxtail, bdy);}
      else {
        ct_a = XDouble.twoTwoDiff(Hilo.product(cdxtail, ady),
                                  Hilo.product(cdytail, adx));
        ct_b = XDouble.twoTwoDiff(Hilo.product(cdytail, bdx),
                                  Hilo.product(cdxtail, bdy)); } }

    final XDouble bct = bt_c.add(ct_b);
    final XDouble cat = ct_a.add(at_c);
    final XDouble abt = at_b.add(bt_a);
    finnow = finnow.add(bct.multiply(adz));
    finnow = finnow.add(cat.multiply(bdz));
    finnow = finnow.add(abt.multiply(cdz));

    // TODO: XDouble.fma ?
    if (adztail != 0.0) { finnow  = finnow.add(bc.multiply(adztail)); }
    if (bdztail != 0.0) { finnow  = finnow.add(ca.multiply(bdztail)); }
    if (cdztail != 0.0) { finnow  = finnow.add(ab.multiply(cdztail)); }

    if (adxtail != 0.0) {
      if (bdytail != 0.0) {
        final Hilo adxt_bdyt = Hilo.product(adxtail,bdytail);
        finnow = finnow.add(XDouble.twoOneProduct(adxt_bdyt,cdz));
        if (cdztail != 0.0) {
          finnow = finnow.add(
            XDouble.twoOneProduct(adxt_bdyt,cdztail)); } }

      if (cdytail != 0.0) {
        final Hilo adxt_cdyt =  Hilo.product(-adxtail,cdytail);
        finnow = finnow.add(XDouble.twoOneProduct(adxt_cdyt,bdz));
        if (bdztail != 0.0) {
          finnow = finnow.add(
            XDouble.twoOneProduct(adxt_cdyt,bdztail)); } } }

    if (bdxtail != 0.0) {
      if (cdytail != 0.0) {
        final Hilo bdxt_cdy = Hilo.product(bdxtail,cdytail);
        finnow = finnow.add(XDouble.twoOneProduct(bdxt_cdy,adz));
        if (adztail != 0.0) {
          finnow = finnow.add(
            XDouble.twoOneProduct(bdxt_cdy,adztail)); } }
      if (adytail != 0.0) {
        final Hilo bdxt_adyt =  Hilo.product(-bdxtail,adytail);
        finnow = finnow.add(XDouble.twoOneProduct(bdxt_adyt,cdz));
        if (cdztail != 0.0) {
          finnow = finnow.add(
            XDouble.twoOneProduct(bdxt_adyt,cdztail)); } } }
    if (cdxtail != 0.0) {
      if (adytail != 0.0) {
        final Hilo cdxt_adyt = Hilo.product(cdxtail,adytail);
        finnow = finnow.add(XDouble.twoOneProduct(cdxt_adyt,bdz));
        if (bdztail != 0.0) {
          finnow = finnow.add(
            XDouble.twoOneProduct(cdxt_adyt,bdztail)); } }
      if (bdytail != 0.0) {
        final Hilo cdxt_bdyt =  Hilo.product(-cdxtail,bdytail);
        finnow = finnow.add(XDouble.twoOneProduct(cdxt_bdyt,adz));
        if (adztail != 0.0) {
          finnow = finnow.add(
            XDouble.twoOneProduct(cdxt_bdyt,adztail)); } } }

    if (adztail != 0.0) { finnow = finnow.add(bct.multiply(adztail)); }
    if (bdztail != 0.0) { finnow = finnow.add(cat.multiply(bdztail)); }
    if (cdztail != 0.0) { finnow = finnow.add(abt.multiply(cdztail)); }

    return finnow.doubleValue(); }

  public final double orient3d (final double[] pa,
                                final double[] pb,
                                final double[] pc,
                                final double[] pd) {


    final double adx = pa[0] - pd[0];
    final double bdx = pb[0] - pd[0];
    final double cdx = pc[0] - pd[0];
    final double ady = pa[1] - pd[1];
    final double bdy = pb[1] - pd[1];
    final double cdy = pc[1] - pd[1];
    final double adz = pa[2] - pd[2];
    final double bdz = pb[2] - pd[2];
    final double cdz = pc[2] - pd[2];

    final double bdxcdy = bdx * cdy;
    final double cdxbdy = cdx * bdy;

    final double cdxady = cdx * ady;
    final double adxcdy = adx * cdy;

    final double adxbdy = adx * bdy;
    final double bdxady = bdx * ady;

    final double det =
      adz * (bdxcdy - cdxbdy)
        + bdz * (cdxady - adxcdy)
        + cdz * (adxbdy - bdxady);

    final double permanent
      = (Math.abs(bdxcdy) + Math.abs(cdxbdy)) * Math.abs(adz)
      + (Math.abs(cdxady) + Math.abs(adxcdy)) * Math.abs(bdz)
      + (Math.abs(adxbdy) + Math.abs(bdxady)) * Math.abs(cdz);
    final double errbound = o3derrboundA * permanent;
    if (Math.abs(det) > errbound) { return det; }
    return orient3d(pa, pb, pc, pd, permanent);}

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
