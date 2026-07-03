package mop.java.geometry.predicates.macro;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.geometry.predicates.Predicate;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

import static mop.java.geometry.predicates.macro.Expansion.EPSILON;

/**
 * AdaptMacroive tests.  Robust.
 * <br>
 * AdaptMacroive precision floating point based on:
 * <ul>
 * <li><a href="https://www.cs.cmu.edu/~quake/robust.html">
 * Jonathan Shewchuk, website:
 * AdaptMacroive Precision Floating-Point Arithmetic and Fast Robust
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
 * AdaptMacroive Precision Floating-Point Arithmetic and Fast Robust
 * mop.java.numbers.predicates.Predicates for Computational Geometry
 * (53 pages, published)
 * </a></li>
 * <li>
 * <a href="https://people.eecs.berkeley.edu/~jrs/papers/robustr.pdf">
 * Jonathan Shewchuk, 1997,
 * AdaptMacroive Precision Floating-Point Arithmetic and Fast Robust
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
 * @version 2026-07-03
 */

// strictfp unnecessary for JDK17 and later
public final class DefaultMacro implements Predicate {

  //--------------------------------------------------------------------

  public final boolean isExact () { return false; }

  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------
  private static final double ccwerrboundA =
    (3.0 + 16.0 * EPSILON) * EPSILON;

  public final double orient2d (final Vector2D pa,
                                final Vector2D pb,
                                final Vector2D pc) {
    double detleft, detright, det;
    double detsum, errbound;

    detleft = (pa.getX() - pc.getX()) * (pb.getY() - pc.getY());
    detright = (pa.getY() - pc.getY()) * (pb.getX() - pc.getX());
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

    return new AdaptMacro().orient2d(pa, pb, pc, detsum);
  }

  //--------------------------------------------------------------------
  // orient3d
  //--------------------------------------------------------------------
  private static final double o3derrboundA =
    (7.0 + 56.0 * EPSILON) * EPSILON;

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

    return new AdaptMacro().orient3d(pa, pb, pc, pd, permanent);
  }

  //--------------------------------------------------------------------
  // incircle
  //--------------------------------------------------------------------
  private static final double iccerrboundA =
    (10.0 + 96.0 * EPSILON) * EPSILON;

  public final double incircle (final double[] pa,
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

    return new AdaptMacro().incircle(pa, pb, pc, pd, permanent);
  }

  //--------------------------------------------------------------------
  // insphere
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

    return new AdaptMacro().insphere(pa, pb, pc, pd, pe, permanent);
  }
  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public DefaultMacro () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
