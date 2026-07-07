package mop.java.geometry.triangle;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.numbers.Hilo;
import mop.java.numbers.XDouble;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Adaptive 'exact' tests. Robust.
 * 'Exact' seems to mean boolean predicate, that is, the sign of the
 * returned value is correct, not its specific value.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-07
 */

// strictfp (may be) necessary for JDK16 and earlier
public final class Adapt extends Triangle2D {

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

  private static final double inCircle (final Vector2D pa,
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
    final XDouble bc = XDouble.subtract(
      Hilo.product(bdx, cdy),
      Hilo.product(cdx, bdy));
    // TODO: XDouble l2norm2, scale2
    final XDouble adet = bc.multiply(adx).multiply(adx)
                           .add(bc.multiply(ady).multiply(ady));

    final XDouble ca = XDouble.subtract(
      Hilo.product(cdx, ady),
      Hilo.product(adx, cdy));
    final XDouble bdet = ca.multiply(bdx).multiply(bdx)
                           .add(
                             ca.multiply(bdy).multiply(bdy));

    final XDouble ab = XDouble.subtract(
      Hilo.product(adx, bdy),
      Hilo.product(bdx, ady));
    final XDouble cdet = ab.multiply(cdx).multiply(cdx)
                           .add(ab.multiply(cdy).multiply(cdy));

    XDouble finnow =  adet.add(bdet).add(cdet);

    double det = finnow.doubleValue();
    if (Math.abs(det) >= iccerrboundB * permanent) { return det; }

    final double adxtail = Hilo.subtractTail(pa.getX(), pd.getX(), adx);
    final double adytail = Hilo.subtractTail(pa.getY(), pd.getY(), ady);
    final double bdxtail = Hilo.subtractTail(pb.getX(), pd.getX(), bdx);
    final double bdytail = Hilo.subtractTail(pb.getY(), pd.getY(), bdy);
    final double cdxtail = Hilo.subtractTail(pc.getX(), pd.getX(), cdx);
    final double cdytail = Hilo.subtractTail(pc.getY(), pd.getY(), cdy);

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
        final XDouble u = XDouble.sum(
          Hilo.product(bdxtail, cdy),
          Hilo.product(bdx, cdytail));
        final XDouble v = XDouble.sum(
          Hilo.product(cdxtail, -bdy),
          Hilo.product(cdx, -bdytail));
        bct = u.add(v);
        bctt = XDouble.subtract(
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
        final XDouble u = XDouble.sum(
          Hilo.product(cdxtail, ady),
          Hilo.product(cdx, adytail));
        final XDouble v = XDouble.sum(
          Hilo.product(adxtail, -cdy),
          Hilo.product(adx, -cdytail));
        cat = u.add(v);
        catt = XDouble.subtract(
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
        final XDouble u = XDouble.sum(
          Hilo.product(adxtail, bdy), Hilo.product(adx, bdytail));

        final XDouble v = XDouble.sum(
          Hilo.product(bdxtail, -ady), Hilo.product(bdx, -adytail));
        abt = u.add(v);
        abtt = XDouble.subtract(
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

  public final double inCircle (final Vector2D pd) {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();
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
    return inCircle(getP0(),getP1(),getP2(), pd, permanent); }

  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------
  private static final double resulterrbound =
    (3.0 + 8.0 * EPSILON) * EPSILON;

  private static final double ccwerrboundB =
    (2.0 + 12*EPSILON) * EPSILON;

  private static final double ccwerrboundC =
    (9.0 + 64.0 * EPSILON) * EPSILON * EPSILON;

  private static final double signedArea (final Vector2D pa,
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
    final XDouble B = XDouble.subtract(detleft, detright);

    double det = B.doubleValue();
    double errbound = ccwerrboundB * detsum;
    if (Math.abs(det) >= errbound) { return det; }

    final double acxtail = Hilo.subtractTail(pa.getX(), pc.getX(), acx);
    final double bcxtail = Hilo.subtractTail(pb.getX(), pc.getX(), bcx);
    final double acytail = Hilo.subtractTail(pa.getY(), pc.getY(), acy);
    final double bcytail = Hilo.subtractTail(pb.getY(), pc.getY(), bcy);
    if ((acxtail == 0.0) && (acytail == 0.0)
      && (bcxtail == 0.0) && (bcytail == 0.0)) {
      return det; }

    errbound = (ccwerrboundC*detsum) + (resulterrbound*Math.abs(det));
    det += (acx*bcytail + bcy*acxtail) - (acy*bcxtail + bcx*acytail);
    if (Math.abs(det) >= errbound) { return det; }

    return B.add(
              XDouble.subtract(
                Hilo.product(acxtail, bcy),
                Hilo.product(acytail, bcx)))
            .add(
              XDouble.subtract(
                Hilo.product(acx, bcytail),
                Hilo.product(acy, bcxtail)))
            .add(
              XDouble.subtract(
                Hilo.product(acxtail, bcytail),
                Hilo.product(acytail, bcxtail)))
            .doubleValue(); }

  //--------------------------------------------------------------------

  private static final double ccwerrboundA =
    (3.0 + 16.0 * EPSILON) * EPSILON;

  public final double signedArea () {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();

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

    return signedArea(getP0(),getP1(),getP2(), detsum); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private Adapt (final Vector2D a,
                  final Vector2D b,
                  final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new Adapt(a, b, c); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
