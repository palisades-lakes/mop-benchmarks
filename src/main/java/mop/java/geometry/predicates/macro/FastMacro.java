package mop.java.geometry.predicates.macro;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.geometry.predicates.Predicate;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

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
 * @version 2026-07-04
 */

// strictfp unnecessary for JDK17 and later
public final class FastMacro implements Predicate {

  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------

  public final double orient2d (final Vector2D pa,
                                final Vector2D pb,
                                final Vector2D pc) {
    final double acx = pa.getX() - pc.getX();
    final double bcx = pb.getX() - pc.getX();
    final double acy = pa.getY() - pc.getY();
    final double bcy = pb.getY() - pc.getY();
    return acx * bcy - acy * bcx; }

  //--------------------------------------------------------------------
  // orient3d
  //--------------------------------------------------------------------

  public final double orient3d (final double[] pa,
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
  // incircle
  //--------------------------------------------------------------------
  public final double incircle (final Vector2D pa,
                                final Vector2D pb,
                                final Vector2D pc,
                                final Vector2D pd) {
    double adx, ady, bdx, bdy, cdx, cdy;
    double abdet, bcdet, cadet;
    double alift, blift, clift;

    adx = pa.getX() - pd.getX();
    ady = pa.getY() - pd.getY();
    bdx = pb.getX() - pd.getX();
    bdy = pb.getY() - pd.getY();
    cdx = pc.getX() - pd.getX();
    cdy = pc.getY() - pd.getY();

    abdet = adx * bdy - bdx * ady;
    bcdet = bdx * cdy - cdx * bdy;
    cadet = cdx * ady - adx * cdy;
    alift = adx * adx + ady * ady;
    blift = bdx * bdx + bdy * bdy;
    clift = cdx * cdx + cdy * cdy;

    return alift * bcdet + blift * cadet + clift * abdet;
  }

  //--------------------------------------------------------------------
  // insphere
  //--------------------------------------------------------------------

  public final double insphere (final double[] pa,
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
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public FastMacro () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
