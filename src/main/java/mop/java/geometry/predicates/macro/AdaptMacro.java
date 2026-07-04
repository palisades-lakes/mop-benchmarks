package mop.java.geometry.predicates.macro;

// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.geometry.predicates.Predicate;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

import static mop.java.geometry.predicates.macro.Expansion.*;

/**
   * Adaptive exact tests.  Robust.
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
  public final class AdaptMacro implements Predicate {

    //--------------------------------------------------------------------

    public final boolean isExact () { return false; }

    //--------------------------------------------------------------------
    // orient2d
    //--------------------------------------------------------------------
    private static final double ccwerrboundB =
      (2.0 + 12.0 * EPSILON) * EPSILON;

    private static final double ccwerrboundC =
      (9.0 + 64.0 * EPSILON) * EPSILON * EPSILON;

    final double orient2d (final Vector2D pa,
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

    public final double orient2d (final Vector2D pa,
                                  final Vector2D pb,
                                  final Vector2D pc) {
      return new DefaultMacro().orient2d(pa, pb, pc); }

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
                                  final Vector3D pd) {
      return new DefaultMacro().orient3d(pa, pb, pc, pd); }

    public final double orient3d (final Vector3D pa,
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
      if ((det >= errbound) || (-det >= errbound)) {
        return det;
      }

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
    // incircle
    //--------------------------------------------------------------------
    private static final double iccerrboundC =
      (44.0 + 576.0 * EPSILON) * EPSILON * EPSILON;

    private static final double iccerrboundB =
      (4.0 + 48.0 * EPSILON) * EPSILON;

    public final double incircle (final Vector2D pa,
                                  final Vector2D pb,
                                  final Vector2D pc,
                                  final Vector2D pd) {
      return new DefaultMacro().incircle(pa, pb, pc, pd); }

    final double incircle (final Vector2D pa,
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
    // insphere
    //--------------------------------------------------------------------
    private static final double isperrboundB =
      (5.0 + 72.0 * EPSILON) * EPSILON;
    private static final double isperrboundC =
      (71.0 + 1408.0 * EPSILON) * EPSILON * EPSILON;

    public final double insphere (final double[] pa,
                                  final double[] pb,
                                  final double[] pc,
                                  final double[] pd,
                                  final double[] pe) {
      return new DefaultMacro().insphere(pa,pb,pc,pd,pe); }

    final double insphere (final double[] pa,
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
      if ((det >= errbound) || (-det >= errbound)) {
        return det;
      }

      return new ExactMacro().insphere(pa, pb, pc, pd, pe);
    }

    //--------------------------------------------------------------------
    // construction
    //--------------------------------------------------------------------
    // TODO: singleton?

    public AdaptMacro () { super(); }

    //-------------------------------------------------------------------
  } // end class
//-------------------------------------------------------------------
