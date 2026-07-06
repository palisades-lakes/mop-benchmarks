package mop.java.geometry.triangle.macro;

// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.geometry.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

import static mop.java.geometry.Expansion.*;

/**
   * Adaptive exact tests.  Robust.
   *
   * @author palisades dot lakes at gmail dot com,
   * @version 2026-07-06
   */

// strictfp unnecessary for JDK17 and later
  public final class AdaptMacro extends Triangle2D {

    //--------------------------------------------------------------------
    // orient2d
    //--------------------------------------------------------------------
    private static final double ccwerrboundB =
      (2.0 + 12.0 * EPSILON) * EPSILON;

    private static final double ccwerrboundC =
      (9.0 + 64.0 * EPSILON) * EPSILON * EPSILON;

    final double signedArea (final Vector2D pa,
                             final Vector2D pb,
                             final Vector2D pc,
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

      acx = (pa.getX() - pc.getX());
      bcx = (pb.getX() - pc.getX());
      acy = (pa.getY() - pc.getY());
      bcy = (pb.getY() - pc.getY());

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
      if ((det >= errbound) || (-det >= errbound)) {
        return det;
      }

      bvirt = (pa.getX() - acx);
      avirt = acx + bvirt;
      bround = bvirt - pc.getX();
      around = pa.getX() - avirt;
      acxtail = around + bround;
      bvirt = (pb.getX() - bcx);
      avirt = bcx + bvirt;
      bround = bvirt - pc.getX();
      around = pb.getX() - avirt;
      bcxtail = around + bround;
      bvirt = (pa.getY() - acy);
      avirt = acy + bvirt;
      bround = bvirt - pc.getY();
      around = pa.getY() - avirt;
      acytail = around + bround;
      bvirt = (pb.getY() - bcy);
      avirt = bcy + bvirt;
      bround = bvirt - pc.getY();
      around = pb.getY() - avirt;
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
      C1length = fast_expansion_sum_zeroelim(4, B, 4, u, C1);

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
      C2length = fast_expansion_sum_zeroelim(C1length, C1, 4, u, C2);

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
      Dlength = fast_expansion_sum_zeroelim(C2length, C2, 4, u, D);

      return (D[Dlength - 1]);
    }

    //--------------------------------------------------------------------

    public final double signedArea (final Vector2D pa,
                                    final Vector2D pb,
                                    final Vector2D pc) {
      return new DefaultMacro().signedArea(pa, pb, pc); }

    //--------------------------------------------------------------------
    // inCircle
    //--------------------------------------------------------------------
    private static final double iccerrboundC =
      (44.0 + 576.0 * EPSILON) * EPSILON * EPSILON;

    private static final double iccerrboundB =
      (4.0 + 48.0 * EPSILON) * EPSILON;

    public final double inCircle (final Vector2D pa,
                                  final Vector2D pb,
                                  final Vector2D pc,
                                  final Vector2D p) {
      return new DefaultMacro().inCircle(pa, pb, pc, p); }

    final double inCircle (final Vector2D pa,
                           final Vector2D pb,
                           final Vector2D pc,
                           final Vector2D pd,
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

      adx = (pa.getX() - pd.getX());
      bdx = (pb.getX() - pd.getX());
      cdx = (pc.getX() - pd.getX());
      ady = (pa.getY() - pd.getY());
      bdy = (pb.getY() - pd.getY());
      cdy = (pc.getY() - pd.getY());

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
      axbclen = scale_expansion_zeroelim(4, bc, adx, axbc);
      axxbclen = scale_expansion_zeroelim(axbclen, axbc, adx, axxbc);
      aybclen = scale_expansion_zeroelim(4, bc, ady, aybc);
      ayybclen = scale_expansion_zeroelim(aybclen, aybc, ady, ayybc);
      alen = fast_expansion_sum_zeroelim(axxbclen, axxbc, ayybclen, ayybc,
                                         adet);

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
      bxcalen = scale_expansion_zeroelim(4, ca, bdx, bxca);
      bxxcalen = scale_expansion_zeroelim(bxcalen, bxca, bdx, bxxca);
      bycalen = scale_expansion_zeroelim(4, ca, bdy, byca);
      byycalen = scale_expansion_zeroelim(bycalen, byca, bdy, byyca);
      blen = fast_expansion_sum_zeroelim(bxxcalen, bxxca, byycalen, byyca,
                                         bdet);

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
      cxablen = scale_expansion_zeroelim(4, ab, cdx, cxab);
      cxxablen = scale_expansion_zeroelim(cxablen, cxab, cdx, cxxab);
      cyablen = scale_expansion_zeroelim(4, ab, cdy, cyab);
      cyyablen = scale_expansion_zeroelim(cyablen, cyab, cdy, cyyab);
      clen = fast_expansion_sum_zeroelim(cxxablen, cxxab, cyyablen, cyyab,
                                         cdet);

      ablen = fast_expansion_sum_zeroelim(alen, adet, blen, bdet, abdet);
      finlength =
        fast_expansion_sum_zeroelim(ablen, abdet, clen, cdet, fin1);

      det = estimate(finlength, fin1);
      errbound = iccerrboundB * permanent;
      if ((det >= errbound) || (-det >= errbound)) {
        return det;
      }

      bvirt = (pa.getX() - adx);
      avirt = adx + bvirt;
      bround = bvirt - pd.getX();
      around = pa.getX() - avirt;
      adxtail = around + bround;
      bvirt = (pa.getY() - ady);
      avirt = ady + bvirt;
      bround = bvirt - pd.getY();
      around = pa.getY() - avirt;
      adytail = around + bround;
      bvirt = (pb.getX() - bdx);
      avirt = bdx + bvirt;
      bround = bvirt - pd.getX();
      around = pb.getX() - avirt;
      bdxtail = around + bround;
      bvirt = (pb.getY() - bdy);
      avirt = bdy + bvirt;
      bround = bvirt - pd.getY();
      around = pb.getY() - avirt;
      bdytail = around + bround;
      bvirt = (pc.getX() - cdx);
      avirt = cdx + bvirt;
      bround = bvirt - pd.getX();
      around = pc.getX() - avirt;
      cdxtail = around + bround;
      bvirt = (pc.getY() - cdy);
      avirt = cdy + bvirt;
      bround = bvirt - pd.getY();
      around = pc.getY() - avirt;
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
        adxadx1 = (adx * adx);
        c = (SPLITTER * adx);
        abig = (c - adx);
        ahi = c - abig;
        alo = adx - ahi;
        err1 = adxadx1 - (ahi * ahi);
        err3 = err1 - ((ahi + ahi) * alo);
        adxadx0 = (alo * alo) - err3;
        adyady1 = (ady * ady);
        c = (SPLITTER * ady);
        abig = (c - ady);
        ahi = c - abig;
        alo = ady - ahi;
        err1 = adyady1 - (ahi * ahi);
        err3 = err1 - ((ahi + ahi) * alo);
        adyady0 = (alo * alo) - err3;
        _i = (adxadx0 + adyady0);
        bvirt = (_i - adxadx0);
        avirt = _i - bvirt;
        bround = adyady0 - bvirt;
        around = adxadx0 - avirt;
        aa[0] = around + bround;
        _j = (adxadx1 + _i);
        bvirt = (_j - adxadx1);
        avirt = _j - bvirt;
        bround = _i - bvirt;
        around = adxadx1 - avirt;
        _0 = around + bround;
        _i = (_0 + adyady1);
        bvirt = (_i - _0);
        avirt = _i - bvirt;
        bround = adyady1 - bvirt;
        around = _0 - avirt;
        aa[1] = around + bround;
        aa3 = (_j + _i);
        bvirt = (aa3 - _j);
        avirt = aa3 - bvirt;
        bround = _i - bvirt;
        around = _j - avirt;
        aa[2] = around + bround;
        aa[3] = aa3;
      }
      if ((cdxtail != 0.0) || (cdytail != 0.0)
        || (adxtail != 0.0) || (adytail != 0.0)) {
        bdxbdx1 = (bdx * bdx);
        c = (SPLITTER * bdx);
        abig = (c - bdx);
        ahi = c - abig;
        alo = bdx - ahi;
        err1 = bdxbdx1 - (ahi * ahi);
        err3 = err1 - ((ahi + ahi) * alo);
        bdxbdx0 = (alo * alo) - err3;
        bdybdy1 = (bdy * bdy);
        c = (SPLITTER * bdy);
        abig = (c - bdy);
        ahi = c - abig;
        alo = bdy - ahi;
        err1 = bdybdy1 - (ahi * ahi);
        err3 = err1 - ((ahi + ahi) * alo);
        bdybdy0 = (alo * alo) - err3;
        _i = (bdxbdx0 + bdybdy0);
        bvirt = (_i - bdxbdx0);
        avirt = _i - bvirt;
        bround = bdybdy0 - bvirt;
        around = bdxbdx0 - avirt;
        bb[0] = around + bround;
        _j = (bdxbdx1 + _i);
        bvirt = (_j - bdxbdx1);
        avirt = _j - bvirt;
        bround = _i - bvirt;
        around = bdxbdx1 - avirt;
        _0 = around + bround;
        _i = (_0 + bdybdy1);
        bvirt = (_i - _0);
        avirt = _i - bvirt;
        bround = bdybdy1 - bvirt;
        around = _0 - avirt;
        bb[1] = around + bround;
        bb3 = (_j + _i);
        bvirt = (bb3 - _j);
        avirt = bb3 - bvirt;
        bround = _i - bvirt;
        around = _j - avirt;
        bb[2] = around + bround;
        bb[3] = bb3;
      }
      if ((adxtail != 0.0) || (adytail != 0.0)
        || (bdxtail != 0.0) || (bdytail != 0.0)) {
        cdxcdx1 = (cdx * cdx);
        c = (SPLITTER * cdx);
        abig = (c - cdx);
        ahi = c - abig;
        alo = cdx - ahi;
        err1 = cdxcdx1 - (ahi * ahi);
        err3 = err1 - ((ahi + ahi) * alo);
        cdxcdx0 = (alo * alo) - err3;
        cdycdy1 = (cdy * cdy);
        c = (SPLITTER * cdy);
        abig = (c - cdy);
        ahi = c - abig;
        alo = cdy - ahi;
        err1 = cdycdy1 - (ahi * ahi);
        err3 = err1 - ((ahi + ahi) * alo);
        cdycdy0 = (alo * alo) - err3;
        _i = (cdxcdx0 + cdycdy0);
        bvirt = (_i - cdxcdx0);
        avirt = _i - bvirt;
        bround = cdycdy0 - bvirt;
        around = cdxcdx0 - avirt;
        cc[0] = around + bround;
        _j = (cdxcdx1 + _i);
        bvirt = (_j - cdxcdx1);
        avirt = _j - bvirt;
        bround = _i - bvirt;
        around = cdxcdx1 - avirt;
        _0 = around + bround;
        _i = (_0 + cdycdy1);
        bvirt = (_i - _0);
        avirt = _i - bvirt;
        bround = cdycdy1 - bvirt;
        around = _0 - avirt;
        cc[1] = around + bround;
        cc3 = (_j + _i);
        bvirt = (cc3 - _j);
        avirt = cc3 - bvirt;
        bround = _i - bvirt;
        around = _j - avirt;
        cc[2] = around + bround;
        cc[3] = cc3;
      }

      if (adxtail != 0.0) {
        axtbclen = scale_expansion_zeroelim(4, bc, adxtail, axtbc);
        temp16alen = scale_expansion_zeroelim(axtbclen, axtbc, 2.0 * adx,
                                              temp16a);

        axtcclen = scale_expansion_zeroelim(4, cc, adxtail, axtcc);
        temp16blen =
          scale_expansion_zeroelim(axtcclen, axtcc, bdy, temp16b);

        axtbblen = scale_expansion_zeroelim(4, bb, adxtail, axtbb);
        temp16clen =
          scale_expansion_zeroelim(axtbblen, axtbb, -cdy, temp16c);

        temp32alen = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                 temp16blen, temp16b,
                                                 temp32a);
        temp48len = fast_expansion_sum_zeroelim(temp16clen, temp16c,
                                                temp32alen, temp32a,
                                                temp48);
        finlength =
          fast_expansion_sum_zeroelim(finlength, finnow, temp48len,
                                      temp48, finother);
        finswap = finnow;
        finnow = finother;
        finother = finswap;
      }
      if (adytail != 0.0) {
        aytbclen = scale_expansion_zeroelim(4, bc, adytail, aytbc);
        temp16alen = scale_expansion_zeroelim(aytbclen, aytbc, 2.0 * ady,
                                              temp16a);

        aytbblen = scale_expansion_zeroelim(4, bb, adytail, aytbb);
        temp16blen =
          scale_expansion_zeroelim(aytbblen, aytbb, cdx, temp16b);

        aytcclen = scale_expansion_zeroelim(4, cc, adytail, aytcc);
        temp16clen =
          scale_expansion_zeroelim(aytcclen, aytcc, -bdx, temp16c);

        temp32alen = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                 temp16blen, temp16b,
                                                 temp32a);
        temp48len = fast_expansion_sum_zeroelim(temp16clen, temp16c,
                                                temp32alen, temp32a,
                                                temp48);
        finlength =
          fast_expansion_sum_zeroelim(finlength, finnow, temp48len,
                                      temp48, finother);
        finswap = finnow;
        finnow = finother;
        finother = finswap;
      }
      if (bdxtail != 0.0) {
        bxtcalen = scale_expansion_zeroelim(4, ca, bdxtail, bxtca);
        temp16alen = scale_expansion_zeroelim(bxtcalen, bxtca, 2.0 * bdx,
                                              temp16a);

        bxtaalen = scale_expansion_zeroelim(4, aa, bdxtail, bxtaa);
        temp16blen =
          scale_expansion_zeroelim(bxtaalen, bxtaa, cdy, temp16b);

        bxtcclen = scale_expansion_zeroelim(4, cc, bdxtail, bxtcc);
        temp16clen =
          scale_expansion_zeroelim(bxtcclen, bxtcc, -ady, temp16c);

        temp32alen = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                 temp16blen, temp16b,
                                                 temp32a);
        temp48len = fast_expansion_sum_zeroelim(temp16clen, temp16c,
                                                temp32alen, temp32a,
                                                temp48);
        finlength =
          fast_expansion_sum_zeroelim(finlength, finnow, temp48len,
                                      temp48, finother);
        finswap = finnow;
        finnow = finother;
        finother = finswap;
      }
      if (bdytail != 0.0) {
        bytcalen = scale_expansion_zeroelim(4, ca, bdytail, bytca);
        temp16alen = scale_expansion_zeroelim(bytcalen, bytca, 2.0 * bdy,
                                              temp16a);

        bytcclen = scale_expansion_zeroelim(4, cc, bdytail, bytcc);
        temp16blen =
          scale_expansion_zeroelim(bytcclen, bytcc, adx, temp16b);

        bytaalen = scale_expansion_zeroelim(4, aa, bdytail, bytaa);
        temp16clen =
          scale_expansion_zeroelim(bytaalen, bytaa, -cdx, temp16c);

        temp32alen = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                 temp16blen, temp16b,
                                                 temp32a);
        temp48len = fast_expansion_sum_zeroelim(temp16clen, temp16c,
                                                temp32alen, temp32a,
                                                temp48);
        finlength =
          fast_expansion_sum_zeroelim(finlength, finnow, temp48len,
                                      temp48, finother);
        finswap = finnow;
        finnow = finother;
        finother = finswap;
      }
      if (cdxtail != 0.0) {
        cxtablen = scale_expansion_zeroelim(4, ab, cdxtail, cxtab);
        temp16alen = scale_expansion_zeroelim(cxtablen, cxtab, 2.0 * cdx,
                                              temp16a);

        cxtbblen = scale_expansion_zeroelim(4, bb, cdxtail, cxtbb);
        temp16blen =
          scale_expansion_zeroelim(cxtbblen, cxtbb, ady, temp16b);

        cxtaalen = scale_expansion_zeroelim(4, aa, cdxtail, cxtaa);
        temp16clen =
          scale_expansion_zeroelim(cxtaalen, cxtaa, -bdy, temp16c);

        temp32alen = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                 temp16blen, temp16b,
                                                 temp32a);
        temp48len = fast_expansion_sum_zeroelim(temp16clen, temp16c,
                                                temp32alen, temp32a,
                                                temp48);
        finlength =
          fast_expansion_sum_zeroelim(finlength, finnow, temp48len,
                                      temp48, finother);
        finswap = finnow;
        finnow = finother;
        finother = finswap;
      }
      if (cdytail != 0.0) {
        cytablen = scale_expansion_zeroelim(4, ab, cdytail, cytab);
        temp16alen = scale_expansion_zeroelim(cytablen, cytab, 2.0 * cdy,
                                              temp16a);

        cytaalen = scale_expansion_zeroelim(4, aa, cdytail, cytaa);
        temp16blen =
          scale_expansion_zeroelim(cytaalen, cytaa, bdx, temp16b);

        cytbblen = scale_expansion_zeroelim(4, bb, cdytail, cytbb);
        temp16clen =
          scale_expansion_zeroelim(cytbblen, cytbb, -adx, temp16c);

        temp32alen = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                 temp16blen, temp16b,
                                                 temp32a);
        temp48len = fast_expansion_sum_zeroelim(temp16clen, temp16c,
                                                temp32alen, temp32a,
                                                temp48);
        finlength =
          fast_expansion_sum_zeroelim(finlength, finnow, temp48len,
                                      temp48, finother);
        finswap = finnow;
        finnow = finother;
        finother = finswap;
      }

      if ((adxtail != 0.0) || (adytail != 0.0)) {
        if ((bdxtail != 0.0) || (bdytail != 0.0)
          || (cdxtail != 0.0) || (cdytail != 0.0)) {
          ti1 = (bdxtail * cdy);
          c = (SPLITTER * bdxtail);
          abig = (c - bdxtail);
          ahi = c - abig;
          alo = bdxtail - ahi;
          c = (SPLITTER * cdy);
          abig = (c - cdy);
          bhi = c - abig;
          blo = cdy - bhi;
          err1 = ti1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          ti0 = (alo * blo) - err3;
          tj1 = (bdx * cdytail);
          c = (SPLITTER * bdx);
          abig = (c - bdx);
          ahi = c - abig;
          alo = bdx - ahi;
          c = (SPLITTER * cdytail);
          abig = (c - cdytail);
          bhi = c - abig;
          blo = cdytail - bhi;
          err1 = tj1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          tj0 = (alo * blo) - err3;
          _i = (ti0 + tj0);
          bvirt = (_i - ti0);
          avirt = _i - bvirt;
          bround = tj0 - bvirt;
          around = ti0 - avirt;
          u[0] = around + bround;
          _j = (ti1 + _i);
          bvirt = (_j - ti1);
          avirt = _j - bvirt;
          bround = _i - bvirt;
          around = ti1 - avirt;
          _0 = around + bround;
          _i = (_0 + tj1);
          bvirt = (_i - _0);
          avirt = _i - bvirt;
          bround = tj1 - bvirt;
          around = _0 - avirt;
          u[1] = around + bround;
          u3 = (_j + _i);
          bvirt = (u3 - _j);
          avirt = u3 - bvirt;
          bround = _i - bvirt;
          around = _j - avirt;
          u[2] = around + bround;
          u[3] = u3;
          negate = -bdy;
          ti1 = (cdxtail * negate);
          c = (SPLITTER * cdxtail);
          abig = (c - cdxtail);
          ahi = c - abig;
          alo = cdxtail - ahi;
          c = (SPLITTER * negate);
          abig = (c - negate);
          bhi = c - abig;
          blo = negate - bhi;
          err1 = ti1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          ti0 = (alo * blo) - err3;
          negate = -bdytail;
          tj1 = (cdx * negate);
          c = (SPLITTER * cdx);
          abig = (c - cdx);
          ahi = c - abig;
          alo = cdx - ahi;
          c = (SPLITTER * negate);
          abig = (c - negate);
          bhi = c - abig;
          blo = negate - bhi;
          err1 = tj1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          tj0 = (alo * blo) - err3;
          _i = (ti0 + tj0);
          bvirt = (_i - ti0);
          avirt = _i - bvirt;
          bround = tj0 - bvirt;
          around = ti0 - avirt;
          v[0] = around + bround;
          _j = (ti1 + _i);
          bvirt = (_j - ti1);
          avirt = _j - bvirt;
          bround = _i - bvirt;
          around = ti1 - avirt;
          _0 = around + bround;
          _i = (_0 + tj1);
          bvirt = (_i - _0);
          avirt = _i - bvirt;
          bround = tj1 - bvirt;
          around = _0 - avirt;
          v[1] = around + bround;
          v3 = (_j + _i);
          bvirt = (v3 - _j);
          avirt = v3 - bvirt;
          bround = _i - bvirt;
          around = _j - avirt;
          v[2] = around + bround;
          v[3] = v3;
          bctlen = fast_expansion_sum_zeroelim(4, u, 4, v, bct);

          ti1 = (bdxtail * cdytail);
          c = (SPLITTER * bdxtail);
          abig = (c - bdxtail);
          ahi = c - abig;
          alo = bdxtail - ahi;
          c = (SPLITTER * cdytail);
          abig = (c - cdytail);
          bhi = c - abig;
          blo = cdytail - bhi;
          err1 = ti1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          ti0 = (alo * blo) - err3;
          tj1 = (cdxtail * bdytail);
          c = (SPLITTER * cdxtail);
          abig = (c - cdxtail);
          ahi = c - abig;
          alo = cdxtail - ahi;
          c = (SPLITTER * bdytail);
          abig = (c - bdytail);
          bhi = c - abig;
          blo = bdytail - bhi;
          err1 = tj1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          tj0 = (alo * blo) - err3;
          _i = (ti0 - tj0);
          bvirt = (ti0 - _i);
          avirt = _i + bvirt;
          bround = bvirt - tj0;
          around = ti0 - avirt;
          bctt[0] = around + bround;
          _j = (ti1 + _i);
          bvirt = (_j - ti1);
          avirt = _j - bvirt;
          bround = _i - bvirt;
          around = ti1 - avirt;
          _0 = around + bround;
          _i = (_0 - tj1);
          bvirt = (_0 - _i);
          avirt = _i + bvirt;
          bround = bvirt - tj1;
          around = _0 - avirt;
          bctt[1] = around + bround;
          bctt3 = (_j + _i);
          bvirt = (bctt3 - _j);
          avirt = bctt3 - bvirt;
          bround = _i - bvirt;
          around = _j - avirt;
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
            scale_expansion_zeroelim(axtbclen, axtbc, adxtail, temp16a);
          axtbctlen =
            scale_expansion_zeroelim(bctlen, bct, adxtail, axtbct);
          temp32alen =
            scale_expansion_zeroelim(axtbctlen, axtbct, 2.0 * adx,
                                     temp32a);
          temp48len = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                  temp32alen, temp32a,
                                                  temp48);
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, temp48len,
                                        temp48, finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;
          if (bdytail != 0.0) {
            temp8len = scale_expansion_zeroelim(4, cc, adxtail, temp8);
            temp16alen =
              scale_expansion_zeroelim(temp8len, temp8, bdytail,
                                       temp16a);
            finlength =
              fast_expansion_sum_zeroelim(finlength, finnow, temp16alen,
                                          temp16a, finother);
            finswap = finnow;
            finnow = finother;
            finother = finswap;
          }
          if (cdytail != 0.0) {
            temp8len = scale_expansion_zeroelim(4, bb, -adxtail, temp8);
            temp16alen =
              scale_expansion_zeroelim(temp8len, temp8, cdytail,
                                       temp16a);
            finlength =
              fast_expansion_sum_zeroelim(finlength, finnow, temp16alen,
                                          temp16a, finother);
            finswap = finnow;
            finnow = finother;
            finother = finswap;
          }

          temp32alen =
            scale_expansion_zeroelim(axtbctlen, axtbct, adxtail,
                                     temp32a);
          axtbcttlen =
            scale_expansion_zeroelim(bcttlen, bctt, adxtail, axtbctt);
          temp16alen =
            scale_expansion_zeroelim(axtbcttlen, axtbctt, 2.0 * adx,
                                     temp16a);
          temp16blen =
            scale_expansion_zeroelim(axtbcttlen, axtbctt, adxtail,
                                     temp16b);
          temp32blen = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                   temp16blen, temp16b,
                                                   temp32b);
          temp64len = fast_expansion_sum_zeroelim(temp32alen, temp32a,
                                                  temp32blen, temp32b,
                                                  temp64);
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, temp64len,
                                        temp64, finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;
        }
// TODO: aytbclen not initialized!!!
        if (adytail != 0.0) {
          temp16alen =
            scale_expansion_zeroelim(aytbclen, aytbc, adytail, temp16a);
          aytbctlen =
            scale_expansion_zeroelim(bctlen, bct, adytail, aytbct);
          temp32alen =
            scale_expansion_zeroelim(aytbctlen, aytbct, 2.0 * ady,
                                     temp32a);
          temp48len = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                  temp32alen, temp32a,
                                                  temp48);
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, temp48len,
                                        temp48, finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;

          temp32alen =
            scale_expansion_zeroelim(aytbctlen, aytbct, adytail,
                                     temp32a);
          aytbcttlen =
            scale_expansion_zeroelim(bcttlen, bctt, adytail, aytbctt);
          temp16alen =
            scale_expansion_zeroelim(aytbcttlen, aytbctt, 2.0 * ady,
                                     temp16a);
          temp16blen =
            scale_expansion_zeroelim(aytbcttlen, aytbctt, adytail,
                                     temp16b);
          temp32blen = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                   temp16blen, temp16b,
                                                   temp32b);
          temp64len = fast_expansion_sum_zeroelim(temp32alen, temp32a,
                                                  temp32blen, temp32b,
                                                  temp64);
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, temp64len,
                                        temp64, finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;
        }
      }
      if ((bdxtail != 0.0) || (bdytail != 0.0)) {
        if ((cdxtail != 0.0) || (cdytail != 0.0)
          || (adxtail != 0.0) || (adytail != 0.0)) {
          ti1 = (cdxtail * ady);
          c = (SPLITTER * cdxtail);
          abig = (c - cdxtail);
          ahi = c - abig;
          alo = cdxtail - ahi;
          c = (SPLITTER * ady);
          abig = (c - ady);
          bhi = c - abig;
          blo = ady - bhi;
          err1 = ti1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          ti0 = (alo * blo) - err3;
          tj1 = (cdx * adytail);
          c = (SPLITTER * cdx);
          abig = (c - cdx);
          ahi = c - abig;
          alo = cdx - ahi;
          c = (SPLITTER * adytail);
          abig = (c - adytail);
          bhi = c - abig;
          blo = adytail - bhi;
          err1 = tj1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          tj0 = (alo * blo) - err3;
          _i = (ti0 + tj0);
          bvirt = (_i - ti0);
          avirt = _i - bvirt;
          bround = tj0 - bvirt;
          around = ti0 - avirt;
          u[0] = around + bround;
          _j = (ti1 + _i);
          bvirt = (_j - ti1);
          avirt = _j - bvirt;
          bround = _i - bvirt;
          around = ti1 - avirt;
          _0 = around + bround;
          _i = (_0 + tj1);
          bvirt = (_i - _0);
          avirt = _i - bvirt;
          bround = tj1 - bvirt;
          around = _0 - avirt;
          u[1] = around + bround;
          u3 = (_j + _i);
          bvirt = (u3 - _j);
          avirt = u3 - bvirt;
          bround = _i - bvirt;
          around = _j - avirt;
          u[2] = around + bround;
          u[3] = u3;
          negate = -cdy;
          ti1 = (adxtail * negate);
          c = (SPLITTER * adxtail);
          abig = (c - adxtail);
          ahi = c - abig;
          alo = adxtail - ahi;
          c = (SPLITTER * negate);
          abig = (c - negate);
          bhi = c - abig;
          blo = negate - bhi;
          err1 = ti1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          ti0 = (alo * blo) - err3;
          negate = -cdytail;
          tj1 = (adx * negate);
          c = (SPLITTER * adx);
          abig = (c - adx);
          ahi = c - abig;
          alo = adx - ahi;
          c = (SPLITTER * negate);
          abig = (c - negate);
          bhi = c - abig;
          blo = negate - bhi;
          err1 = tj1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          tj0 = (alo * blo) - err3;
          _i = (ti0 + tj0);
          bvirt = (_i - ti0);
          avirt = _i - bvirt;
          bround = tj0 - bvirt;
          around = ti0 - avirt;
          v[0] = around + bround;
          _j = (ti1 + _i);
          bvirt = (_j - ti1);
          avirt = _j - bvirt;
          bround = _i - bvirt;
          around = ti1 - avirt;
          _0 = around + bround;
          _i = (_0 + tj1);
          bvirt = (_i - _0);
          avirt = _i - bvirt;
          bround = tj1 - bvirt;
          around = _0 - avirt;
          v[1] = around + bround;
          v3 = (_j + _i);
          bvirt = (v3 - _j);
          avirt = v3 - bvirt;
          bround = _i - bvirt;
          around = _j - avirt;
          v[2] = around + bround;
          v[3] = v3;
          catlen = fast_expansion_sum_zeroelim(4, u, 4, v, cat);

          ti1 = (cdxtail * adytail);
          c = (SPLITTER * cdxtail);
          abig = (c - cdxtail);
          ahi = c - abig;
          alo = cdxtail - ahi;
          c = (SPLITTER * adytail);
          abig = (c - adytail);
          bhi = c - abig;
          blo = adytail - bhi;
          err1 = ti1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          ti0 = (alo * blo) - err3;
          tj1 = (adxtail * cdytail);
          c = (SPLITTER * adxtail);
          abig = (c - adxtail);
          ahi = c - abig;
          alo = adxtail - ahi;
          c = (SPLITTER * cdytail);
          abig = (c - cdytail);
          bhi = c - abig;
          blo = cdytail - bhi;
          err1 = tj1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          tj0 = (alo * blo) - err3;
          _i = (ti0 - tj0);
          bvirt = (ti0 - _i);
          avirt = _i + bvirt;
          bround = bvirt - tj0;
          around = ti0 - avirt;
          catt[0] = around + bround;
          _j = (ti1 + _i);
          bvirt = (_j - ti1);
          avirt = _j - bvirt;
          bround = _i - bvirt;
          around = ti1 - avirt;
          _0 = around + bround;
          _i = (_0 - tj1);
          bvirt = (_0 - _i);
          avirt = _i + bvirt;
          bround = bvirt - tj1;
          around = _0 - avirt;
          catt[1] = around + bround;
          catt3 = (_j + _i);
          bvirt = (catt3 - _j);
          avirt = catt3 - bvirt;
          bround = _i - bvirt;
          around = _j - avirt;
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
            scale_expansion_zeroelim(bxtcalen, bxtca, bdxtail, temp16a);
          bxtcatlen =
            scale_expansion_zeroelim(catlen, cat, bdxtail, bxtcat);
          temp32alen =
            scale_expansion_zeroelim(bxtcatlen, bxtcat, 2.0 * bdx,
                                     temp32a);
          temp48len = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                  temp32alen, temp32a,
                                                  temp48);
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, temp48len,
                                        temp48, finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;
          if (cdytail != 0.0) {
            temp8len = scale_expansion_zeroelim(4, aa, bdxtail, temp8);
            temp16alen =
              scale_expansion_zeroelim(temp8len, temp8, cdytail,
                                       temp16a);
            finlength =
              fast_expansion_sum_zeroelim(finlength, finnow, temp16alen,
                                          temp16a, finother);
            finswap = finnow;
            finnow = finother;
            finother = finswap;
          }
          if (adytail != 0.0) {
            temp8len = scale_expansion_zeroelim(4, cc, -bdxtail, temp8);
            temp16alen =
              scale_expansion_zeroelim(temp8len, temp8, adytail,
                                       temp16a);
            finlength =
              fast_expansion_sum_zeroelim(finlength, finnow, temp16alen,
                                          temp16a, finother);
            finswap = finnow;
            finnow = finother;
            finother = finswap;
          }

          temp32alen =
            scale_expansion_zeroelim(bxtcatlen, bxtcat, bdxtail,
                                     temp32a);
          bxtcattlen =
            scale_expansion_zeroelim(cattlen, catt, bdxtail, bxtcatt);
          temp16alen =
            scale_expansion_zeroelim(bxtcattlen, bxtcatt, 2.0 * bdx,
                                     temp16a);
          temp16blen =
            scale_expansion_zeroelim(bxtcattlen, bxtcatt, bdxtail,
                                     temp16b);
          temp32blen = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                   temp16blen, temp16b,
                                                   temp32b);
          temp64len = fast_expansion_sum_zeroelim(temp32alen, temp32a,
                                                  temp32blen, temp32b,
                                                  temp64);
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, temp64len,
                                        temp64, finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;
        }
        // TODO: bytcalen not initialized!!!
        if (bdytail != 0.0) {
          temp16alen =
            scale_expansion_zeroelim(bytcalen, bytca, bdytail, temp16a);
          bytcatlen =
            scale_expansion_zeroelim(catlen, cat, bdytail, bytcat);
          temp32alen =
            scale_expansion_zeroelim(bytcatlen, bytcat, 2.0 * bdy,
                                     temp32a);
          temp48len = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                  temp32alen, temp32a,
                                                  temp48);
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, temp48len,
                                        temp48, finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;

          temp32alen =
            scale_expansion_zeroelim(bytcatlen, bytcat, bdytail,
                                     temp32a);
          bytcattlen =
            scale_expansion_zeroelim(cattlen, catt, bdytail, bytcatt);
          temp16alen =
            scale_expansion_zeroelim(bytcattlen, bytcatt, 2.0 * bdy,
                                     temp16a);
          temp16blen =
            scale_expansion_zeroelim(bytcattlen, bytcatt, bdytail,
                                     temp16b);
          temp32blen = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                   temp16blen, temp16b,
                                                   temp32b);
          temp64len = fast_expansion_sum_zeroelim(temp32alen, temp32a,
                                                  temp32blen, temp32b,
                                                  temp64);
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, temp64len,
                                        temp64, finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;
        }
      }
      if ((cdxtail != 0.0) || (cdytail != 0.0)) {
        if ((adxtail != 0.0) || (adytail != 0.0)
          || (bdxtail != 0.0) || (bdytail != 0.0)) {
          ti1 = (adxtail * bdy);
          c = (SPLITTER * adxtail);
          abig = (c - adxtail);
          ahi = c - abig;
          alo = adxtail - ahi;
          c = (SPLITTER * bdy);
          abig = (c - bdy);
          bhi = c - abig;
          blo = bdy - bhi;
          err1 = ti1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          ti0 = (alo * blo) - err3;
          tj1 = (adx * bdytail);
          c = (SPLITTER * adx);
          abig = (c - adx);
          ahi = c - abig;
          alo = adx - ahi;
          c = (SPLITTER * bdytail);
          abig = (c - bdytail);
          bhi = c - abig;
          blo = bdytail - bhi;
          err1 = tj1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          tj0 = (alo * blo) - err3;
          _i = (ti0 + tj0);
          bvirt = (_i - ti0);
          avirt = _i - bvirt;
          bround = tj0 - bvirt;
          around = ti0 - avirt;
          u[0] = around + bround;
          _j = (ti1 + _i);
          bvirt = (_j - ti1);
          avirt = _j - bvirt;
          bround = _i - bvirt;
          around = ti1 - avirt;
          _0 = around + bround;
          _i = (_0 + tj1);
          bvirt = (_i - _0);
          avirt = _i - bvirt;
          bround = tj1 - bvirt;
          around = _0 - avirt;
          u[1] = around + bround;
          u3 = (_j + _i);
          bvirt = (u3 - _j);
          avirt = u3 - bvirt;
          bround = _i - bvirt;
          around = _j - avirt;
          u[2] = around + bround;
          u[3] = u3;
          negate = -ady;
          ti1 = (bdxtail * negate);
          c = (SPLITTER * bdxtail);
          abig = (c - bdxtail);
          ahi = c - abig;
          alo = bdxtail - ahi;
          c = (SPLITTER * negate);
          abig = (c - negate);
          bhi = c - abig;
          blo = negate - bhi;
          err1 = ti1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          ti0 = (alo * blo) - err3;
          negate = -adytail;
          tj1 = (bdx * negate);
          c = (SPLITTER * bdx);
          abig = (c - bdx);
          ahi = c - abig;
          alo = bdx - ahi;
          c = (SPLITTER * negate);
          abig = (c - negate);
          bhi = c - abig;
          blo = negate - bhi;
          err1 = tj1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          tj0 = (alo * blo) - err3;
          _i = (ti0 + tj0);
          bvirt = (_i - ti0);
          avirt = _i - bvirt;
          bround = tj0 - bvirt;
          around = ti0 - avirt;
          v[0] = around + bround;
          _j = (ti1 + _i);
          bvirt = (_j - ti1);
          avirt = _j - bvirt;
          bround = _i - bvirt;
          around = ti1 - avirt;
          _0 = around + bround;
          _i = (_0 + tj1);
          bvirt = (_i - _0);
          avirt = _i - bvirt;
          bround = tj1 - bvirt;
          around = _0 - avirt;
          v[1] = around + bround;
          v3 = (_j + _i);
          bvirt = (v3 - _j);
          avirt = v3 - bvirt;
          bround = _i - bvirt;
          around = _j - avirt;
          v[2] = around + bround;
          v[3] = v3;
          abtlen = fast_expansion_sum_zeroelim(4, u, 4, v, abt);

          ti1 = (adxtail * bdytail);
          c = (SPLITTER * adxtail);
          abig = (c - adxtail);
          ahi = c - abig;
          alo = adxtail - ahi;
          c = (SPLITTER * bdytail);
          abig = (c - bdytail);
          bhi = c - abig;
          blo = bdytail - bhi;
          err1 = ti1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          ti0 = (alo * blo) - err3;
          tj1 = (bdxtail * adytail);
          c = (SPLITTER * bdxtail);
          abig = (c - bdxtail);
          ahi = c - abig;
          alo = bdxtail - ahi;
          c = (SPLITTER * adytail);
          abig = (c - adytail);
          bhi = c - abig;
          blo = adytail - bhi;
          err1 = tj1 - (ahi * bhi);
          err2 = err1 - (alo * bhi);
          err3 = err2 - (ahi * blo);
          tj0 = (alo * blo) - err3;
          _i = (ti0 - tj0);
          bvirt = (ti0 - _i);
          avirt = _i + bvirt;
          bround = bvirt - tj0;
          around = ti0 - avirt;
          abtt[0] = around + bround;
          _j = (ti1 + _i);
          bvirt = (_j - ti1);
          avirt = _j - bvirt;
          bround = _i - bvirt;
          around = ti1 - avirt;
          _0 = around + bround;
          _i = (_0 - tj1);
          bvirt = (_0 - _i);
          avirt = _i + bvirt;
          bround = bvirt - tj1;
          around = _0 - avirt;
          abtt[1] = around + bround;
          abtt3 = (_j + _i);
          bvirt = (abtt3 - _j);
          avirt = abtt3 - bvirt;
          bround = _i - bvirt;
          around = _j - avirt;
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
            scale_expansion_zeroelim(cxtablen, cxtab, cdxtail, temp16a);
          cxtabtlen =
            scale_expansion_zeroelim(abtlen, abt, cdxtail, cxtabt);
          temp32alen =
            scale_expansion_zeroelim(cxtabtlen, cxtabt, 2.0 * cdx,
                                     temp32a);
          temp48len = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                  temp32alen, temp32a,
                                                  temp48);
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, temp48len,
                                        temp48, finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;
          if (adytail != 0.0) {
            temp8len = scale_expansion_zeroelim(4, bb, cdxtail, temp8);
            temp16alen =
              scale_expansion_zeroelim(temp8len, temp8, adytail,
                                       temp16a);
            finlength =
              fast_expansion_sum_zeroelim(finlength, finnow, temp16alen,
                                          temp16a, finother);
            finswap = finnow;
            finnow = finother;
            finother = finswap;
          }
          if (bdytail != 0.0) {
            temp8len = scale_expansion_zeroelim(4, aa, -cdxtail, temp8);
            temp16alen =
              scale_expansion_zeroelim(temp8len, temp8, bdytail,
                                       temp16a);
            finlength =
              fast_expansion_sum_zeroelim(finlength, finnow, temp16alen,
                                          temp16a, finother);
            finswap = finnow;
            finnow = finother;
            finother = finswap;
          }

          temp32alen =
            scale_expansion_zeroelim(cxtabtlen, cxtabt, cdxtail,
                                     temp32a);
          cxtabttlen =
            scale_expansion_zeroelim(abttlen, abtt, cdxtail, cxtabtt);
          temp16alen =
            scale_expansion_zeroelim(cxtabttlen, cxtabtt, 2.0 * cdx,
                                     temp16a);
          temp16blen =
            scale_expansion_zeroelim(cxtabttlen, cxtabtt, cdxtail,
                                     temp16b);
          temp32blen = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                   temp16blen, temp16b,
                                                   temp32b);
          temp64len = fast_expansion_sum_zeroelim(temp32alen, temp32a,
                                                  temp32blen, temp32b,
                                                  temp64);
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, temp64len,
                                        temp64, finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;
        }
// TODO: cytablen not initialized!!!
        if (cdytail != 0.0) {
          temp16alen =
            scale_expansion_zeroelim(cytablen, cytab, cdytail, temp16a);
          cytabtlen =
            scale_expansion_zeroelim(abtlen, abt, cdytail, cytabt);
          temp32alen =
            scale_expansion_zeroelim(cytabtlen, cytabt, 2.0 * cdy,
                                     temp32a);
          temp48len = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                  temp32alen, temp32a,
                                                  temp48);
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, temp48len,
                                        temp48, finother);
          finswap = finnow;
          finnow = finother;
          finother = finswap;

          temp32alen =
            scale_expansion_zeroelim(cytabtlen, cytabt, cdytail,
                                     temp32a);
          cytabttlen =
            scale_expansion_zeroelim(abttlen, abtt, cdytail, cytabtt);
          temp16alen =
            scale_expansion_zeroelim(cytabttlen, cytabtt, 2.0 * cdy,
                                     temp16a);
          temp16blen =
            scale_expansion_zeroelim(cytabttlen, cytabtt, cdytail,
                                     temp16b);
          temp32blen = fast_expansion_sum_zeroelim(temp16alen, temp16a,
                                                   temp16blen, temp16b,
                                                   temp32b);
          temp64len = fast_expansion_sum_zeroelim(temp32alen, temp32a,
                                                  temp32blen, temp32b,
                                                  temp64);
          finlength =
            fast_expansion_sum_zeroelim(finlength, finnow, temp64len,
                                        temp64, finother);
          // TODO: unused?
          //finswap = finnow;
          finnow = finother;
          //finother = finswap;
        }
      }

      return finnow[finlength - 1];
    }

    //--------------------------------------------------------------------
    // construction
    //--------------------------------------------------------------------
    // TODO: singleton?

    public AdaptMacro () { super(); }

    //-------------------------------------------------------------------
  } // end class
//-------------------------------------------------------------------
