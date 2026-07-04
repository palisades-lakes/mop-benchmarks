package mop.java.geometry.predicates;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.numbers.Hilo;
import mop.java.numbers.XDouble;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

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
 * @version 2026-07-04
 */

// strictfp (may be) necessary for JDK16 and earlier
public final class Adapt implements Predicate {

  //--------------------------------------------------------------------

  public final boolean isExact () { return false; }

  //--------------------------------------------------------------------
  // incircle
  //--------------------------------------------------------------------
  // TODO: determine from system constants/IEEE 754?
  private static final double EPSILON = 0x1.0p-53;

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

  private static final double incircle (final Vector2D pa,
                                        final Vector2D pb,
                                        final Vector2D pc,
                                        final Vector2D pd,
                                        final double permanent) {
    // TODO: should this be Hilo.twoDiff? see calls to towDiffTail below
    // TODO: convert to vector ops.
    // TODO: subtract d from other vecs before calling
    final double adx = (pa.getX() - pd.getX());
    final double ady = (pa.getY() - pd.getY());
    final double bdx = (pb.getX() - pd.getX());
    final double bdy = (pb.getY() - pd.getY());
    final double cdx = (pc.getX() - pd.getX());
    final double cdy = (pc.getY() - pd.getY());

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

    final double adxtail = Hilo.twoDiffTail(pa.getX(), pd.getX(), adx);
    final double adytail = Hilo.twoDiffTail(pa.getY(), pd.getY(), ady);
    final double bdxtail = Hilo.twoDiffTail(pb.getX(), pd.getX(), bdx);
    final double bdytail = Hilo.twoDiffTail(pb.getY(), pd.getY(), bdy);
    final double cdxtail = Hilo.twoDiffTail(pc.getX(), pd.getX(), cdx);
    final double cdytail = Hilo.twoDiffTail(pc.getY(), pd.getY(), cdy);

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

  public final double incircle (final Vector2D pa,
                                final Vector2D pb,
                                final Vector2D pc,
                                final Vector2D pd) {
    final double adx = pa.getX() - pd.getX();
    final double ady = pa.getY() - pd.getY();
    final double alift = (adx * adx) + (ady * ady);

    final double bdx = pb.getX() - pd.getX();
    final double bdy = pb.getY() - pd.getY();
    final double blift = (bdx * bdx) + (bdy * bdy);

    final double cdx = pc.getX() - pd.getX();
    final double cdy = pc.getY() - pd.getY();
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

  private static final double orient2d (final Vector2D pa,
                                        final Vector2D pb,
                                        final Vector2D pc,
                                        final double detsum) {
  // TODO: difference vectors cached in Triangle object
    final Vector2D ac = pa.subtract(pc);
    final Vector2D bc = pb.subtract(pc);
    final double acx = ac.getX();
    final double acy = ac.getY();
    final double bcx = bc.getX();
    final double bcy = bc.getY();

    final Hilo detleft = Hilo.product(acx,bcy);
    final Hilo detright = Hilo.product(acy,bcx);
    final XDouble B = XDouble.twoTwoDiff(detleft,detright);

    double det = B.doubleValue();
    double errbound = ccwerrboundB * detsum;
    if (Math.abs(det) >= errbound) { return det; }

    final double acxtail = Hilo.twoDiffTail(pa.getX(),pc.getX(),acx);
    final double bcxtail = Hilo.twoDiffTail(pb.getX(),pc.getX(),bcx);
    final double acytail = Hilo.twoDiffTail(pa.getY(),pc.getY(),acy);
    final double bcytail = Hilo.twoDiffTail(pb.getY(),pc.getY(),bcy);
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

  public final double orient2d (final Vector2D pa,
                                final Vector2D pb,
                                final Vector2D pc) {

    // TODO: difference vectors cached in Triangle object
    final Vector2D ac = pa.subtract(pc);
    final Vector2D bc = pb.subtract(pc);

    final double detleft = ac.getX() * bc.getY();
    final double detright = ac.getY() * bc.getX();
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

  public final double orient3d (final Vector3D pa,
                                final Vector3D pb,
                                final Vector3D pc,
                                final Vector3D pd,
                                final double permanent) {
    final double adx = (pa.getX() - pd.getX());
    final double bdx = (pb.getX() - pd.getX());
    final double cdx = (pc.getX() - pd.getX());
    final double ady = (pa.getY() - pd.getY());
    final double bdy = (pb.getY() - pd.getY());
    final double cdy = (pc.getY() - pd.getY());
    final double adz = (pa.getZ() - pd.getZ());
    final double bdz = (pb.getZ() - pd.getZ());
    final double cdz = (pc.getZ() - pd.getZ());

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

    final double adxtail = Hilo.twoDiffTail(pa.getX(),pd.getX(),adx);
    final double bdxtail = Hilo.twoDiffTail(pb.getX(),pd.getX(),bdx);
    final double cdxtail = Hilo.twoDiffTail(pc.getX(),pd.getX(),cdx);
    final double adytail = Hilo.twoDiffTail(pa.getY(),pd.getY(),ady);
    final double bdytail = Hilo.twoDiffTail(pb.getY(),pd.getY(),bdy);
    final double cdytail = Hilo.twoDiffTail(pc.getY(),pd.getY(),cdy);
    final double adztail = Hilo.twoDiffTail(pa.getZ(),pd.getZ(),adz);
    final double bdztail = Hilo.twoDiffTail(pb.getZ(),pd.getZ(),bdz);
    final double cdztail = Hilo.twoDiffTail(pc.getZ(),pd.getZ(),cdz);

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

  public final double orient3d (final Vector3D pa,
                                final Vector3D pb,
                                final Vector3D pc,
                                final Vector3D pd) {


    final double adx = pa.getX() - pd.getX();
    final double bdx = pb.getX() - pd.getX();
    final double cdx = pc.getX() - pd.getX();
    final double ady = pa.getY() - pd.getY();
    final double bdy = pb.getY() - pd.getY();
    final double cdy = pc.getY() - pd.getY();
    final double adz = pa.getZ() - pd.getZ();
    final double bdz = pb.getZ() - pd.getZ();
    final double cdz = pc.getZ() - pd.getZ();

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

  private static final XDouble insphereDet (final XDouble cd,
                                            final double bez,
                                            final XDouble bd,
                                            final double cez,
                                            final XDouble bc,
                                            final double dez,
                                            final int sgn,
                                            final double aex,
                                            final double aey,
                                            final double aez) {
    final XDouble temp8a = cd.multiply(bez);
    final XDouble temp8b = bd.multiply(cez);
    final XDouble temp8c = bc.multiply(dez);
    final XDouble temp24 = temp8a.add(temp8b).add(temp8c);
    final XDouble xdet = temp24.multiply(aex).multiply(sgn*aex);
    final XDouble ydet = temp24.multiply(aey).multiply(sgn*aey);
    final XDouble zdet = temp24.multiply(aez).multiply(sgn*aez);
    return xdet.add(ydet).add(zdet); }

  //--------------------------------------------------------------------
  private static final double isperrboundB =
    (5.0 + 72.0 * EPSILON) * EPSILON;
  private static final double isperrboundC =
    (71.0 + 1408.0 * EPSILON) * EPSILON * EPSILON;

  private static final double insphere (final Vector3D pa,
                                        final Vector3D pb,
                                        final Vector3D pc,
                                        final Vector3D pd,
                                        final Vector3D pe,
                                        final double permanent) {
    final double aex = (pa.getX() - pe.getX());
    final double aey = (pa.getY() - pe.getY());
    final double aez = (pa.getZ() - pe.getZ());

    final double bex = (pb.getX() - pe.getX());
    final double bey = (pb.getY() - pe.getY());
    final double bez = (pb.getZ() - pe.getZ());

    final double cex = (pc.getX() - pe.getX());
    final double cey = (pc.getY() - pe.getY());
    final double cez = (pc.getZ() - pe.getZ());

    final double dex = (pd.getX() - pe.getX());
    final double dey = (pd.getY() - pe.getY());
    final double dez = (pd.getZ() - pe.getZ());

    final XDouble ab = XDouble.crossProduct(aex,aey,bex,bey);
    final XDouble bc = XDouble.crossProduct(bex,bey,cex,cey);
    final XDouble cd = XDouble.crossProduct(cex,cey,dex,dey);
    final XDouble da = XDouble.crossProduct(dex,dey,aex,aey);
    final XDouble ac = XDouble.crossProduct(aex,aey,cex,cey);
    final XDouble bd = XDouble.crossProduct(bex,bey,dex,dey);
    final XDouble adet =
      insphereDet(cd,bez,bd,-cez,bc,dez,-1,aex,aey,aez);
    final XDouble bdet =
      insphereDet(da,cez,ac,dez,cd,aez,1,bex,bey,bez);
    final XDouble cdet =
      insphereDet(ab,dez,bd,aez,da,bez,-1,cex,cey,cez);
    final XDouble ddet =
      insphereDet(bc,aez,ac,-bez,ab,cez,1,dex,dey,dez);

    final XDouble fin1 = adet.add(bdet).add(cdet).add(ddet);

    double det = fin1.doubleValue();
    double errbound = isperrboundB * permanent;
    if (Math.abs(det) >= errbound) { return det; }

    final double aextail = Hilo.twoDiffTail(pa.getX(), pe.getX(), aex);
    final double aeytail = Hilo.twoDiffTail(pa.getY(), pe.getY(), aey);
    final double aeztail = Hilo.twoDiffTail(pa.getZ(), pe.getZ(), aez);
    final double bextail = Hilo.twoDiffTail(pb.getX(), pe.getX(), bex);
    final double beytail = Hilo.twoDiffTail(pb.getY(), pe.getY(), bey);
    final double beztail = Hilo.twoDiffTail(pb.getZ(), pe.getZ(), bez);
    final double cextail = Hilo.twoDiffTail(pc.getX(), pe.getX(), cex);
    final double ceytail = Hilo.twoDiffTail(pc.getY(), pe.getY(), cey);
    final double ceztail = Hilo.twoDiffTail(pc.getZ(), pe.getZ(), cez);
    final double dextail = Hilo.twoDiffTail(pd.getX(), pe.getX(), dex);
    final double deytail = Hilo.twoDiffTail(pd.getY(), pe.getY(), dey);
    final double deztail = Hilo.twoDiffTail(pd.getZ(), pe.getZ(), dez);

    if ((aextail == 0.0) && (aeytail == 0.0) && (aeztail == 0.0)
      && (bextail == 0.0) && (beytail == 0.0) && (beztail == 0.0)
      && (cextail == 0.0) && (ceytail == 0.0) && (ceztail == 0.0)
      && (dextail == 0.0) && (deytail == 0.0) && (deztail == 0.0)) {
      return det; }

    errbound = isperrboundC * permanent + resulterrbound * Math.abs(det);
    final double abeps = (aex * beytail + bey * aextail)
      - (aey * bextail + bex * aeytail);
    final double bceps = (bex * ceytail + cey * bextail)
      - (bey * cextail + cex * beytail);
    final double cdeps = (cex * deytail + dey * cextail)
      - (cey * dextail + dex * ceytail);
    final double daeps = (dex * aeytail + aey * dextail)
      - (dey * aextail + aex * deytail);
    final double aceps = (aex * ceytail + cey * aextail)
      - (aey * cextail + cex * aeytail);
    final double bdeps = (bex * deytail + dey * bextail)
      - (bey * dextail + dex * beytail);

    final double da3 = da.term3();
    final double ac3 = ac.term3();
    final double cd3 = cd.term3();
    final double bc3 = bc.term3();
    final double ab3 = ab.term3();
    final double bd3 = bd.term3();

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

    if (Math.abs(det) >= errbound) { return det; }
    return new Exact().insphere(pa, pb, pc, pd, pe); }

  //--------------------------------------------------------------------

  private static final double isperrboundA =
    (16.0 + 224.0 * EPSILON) * EPSILON;

  public final double insphere (final Vector3D pa,
                                final Vector3D pb,
                                final Vector3D pc,
                                final Vector3D pd,
                                final Vector3D pe) {
    final double aex = pa.getX() - pe.getX();
    final double bex = pb.getX() - pe.getX();
    final double cex = pc.getX() - pe.getX();
    final double dex = pd.getX() - pe.getX();
    final double aey = pa.getY() - pe.getY();
    final double bey = pb.getY() - pe.getY();
    final double cey = pc.getY() - pe.getY();
    final double dey = pd.getY() - pe.getY();
    final double aez = pa.getZ() - pe.getZ();
    final double bez = pb.getZ() - pe.getZ();
    final double cez = pc.getZ() - pe.getZ();
    final double dez = pd.getZ() - pe.getZ();

    // TODO: simple double crossProduct(double,double,double,double)
    final double aexbey = aex * bey;
    final double bexaey = bex * aey;
    final double ab = aexbey - bexaey;
    final double bexcey = bex * cey;
    final double cexbey = cex * bey;
    final double bc = bexcey - cexbey;
    final double cexdey = cex * dey;
    final double dexcey = dex * cey;
    final double cd = cexdey - dexcey;
    final double dexaey = dex * aey;
    final double aexdey = aex * dey;
    final double da = dexaey - aexdey;
    final double aexcey = aex * cey;
    final double cexaey = cex * aey;
    final double ac = aexcey - cexaey;
    final double bexdey = bex * dey;
    final double dexbey = dex * bey;
    final double bd = bexdey - dexbey;

    final double abc = aez * bc - bez * ac + cez * ab;
    final double bcd = bez * cd - cez * bd + dez * bc;
    final double cda = cez * da + dez * ac + aez * cd;
    final double dab = dez * ab + aez * bd + bez * da;

    final double alift = aex * aex + aey * aey + aez * aez;
    final double blift = bex * bex + bey * bey + bez * bez;
    final double clift = cex * cex + cey * cey + cez * cez;
    final double dlift = dex * dex + dey * dey + dez * dez;

    final double det =
      (dlift * abc - clift * dab)
      + (blift * cda - alift * bcd);

    final double aezplus = Math.abs(aez);
    final double bezplus = Math.abs(bez);
    final double cezplus = Math.abs(cez);
    final double dezplus = Math.abs(dez);
    final double aexbeyplus = Math.abs(aexbey);
    final double bexaeyplus = Math.abs(bexaey);
    final double bexceyplus = Math.abs(bexcey);
    final double cexbeyplus = Math.abs(cexbey);
    final double cexdeyplus = Math.abs(cexdey);
    final double dexceyplus = Math.abs(dexcey);
    final double dexaeyplus = Math.abs(dexaey);
    final double aexdeyplus = Math.abs(aexdey);
    final double aexceyplus = Math.abs(aexcey);
    final double cexaeyplus = Math.abs(cexaey);
    final double bexdeyplus = Math.abs(bexdey);
    final double dexbeyplus = Math.abs(dexbey);
    final double permanent = ((cexdeyplus + dexceyplus) * bezplus
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
    final double errbound = isperrboundA * permanent;
    if (Math.abs(det) > errbound) { return det; }

    return insphere(pa, pb, pc, pd, pe, permanent); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public Adapt () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
