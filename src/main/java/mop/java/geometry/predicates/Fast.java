package mop.java.geometry.predicates;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Approximate predicates, nonrobust.
 * <p>>
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

public final class Fast implements Predicate {

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
    return (acx * bcy) - (acy * bcx); }

  //--------------------------------------------------------------------
  // orient3d
  //--------------------------------------------------------------------

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

    return (adx * ((bdy * cdz) - (bdz * cdy))) +
      (bdx * ((cdy * adz) - (cdz * ady))) +
      (cdx * ((ady * bdz) - (adz * bdy))); }


  //--------------------------------------------------------------------
  // incircle
  //--------------------------------------------------------------------

  public final double incircle (final Vector2D pa,
                                final Vector2D pb,
                                final Vector2D pc,
                                final Vector2D pd) {
    final double adx = pa.getX() - pd.getX();
    final double ady = pa.getY() - pd.getY();
    final double bdx = pb.getX() - pd.getX();
    final double bdy = pb.getY() - pd.getY();
    final double cdx = pc.getX() - pd.getX();
    final double cdy = pc.getY() - pd.getY();

    final double abdet = (adx * bdy) - (bdx * ady);
    final double bcdet = (bdx * cdy) - (cdx * bdy);
    final double cadet = (cdx * ady) - (adx * cdy);
    final double alift = (adx * adx) + (ady * ady);
    final double blift = (bdx * bdx) + (bdy * bdy);
    final double clift = (cdx * cdx) + (cdy * cdy);

    return (alift * bcdet) + (blift * cadet) + (clift * abdet); }

  //--------------------------------------------------------------------
  // insphere
  //--------------------------------------------------------------------

  public final double insphere (final double[] pa,
                                final double[] pb,
                                final double[] pc,
                                final double[] pd,
                                final double[] pe) {
    final double aex = pa[0] - pe[0];
    final double bex = pb[0] - pe[0];
    final double cex = pc[0] - pe[0];
    final double dex = pd[0] - pe[0];
    final double aey = pa[1] - pe[1];
    final double bey = pb[1] - pe[1];
    final double cey = pc[1] - pe[1];
    final double dey = pd[1] - pe[1];
    final double aez = pa[2] - pe[2];
    final double bez = pb[2] - pe[2];
    final double cez = pc[2] - pe[2];
    final double dez = pd[2] - pe[2];

    final double ab = (aex * bey) - (bex * aey);
    final double bc = (bex * cey) - (cex * bey);
    final double cd = (cex * dey) - (dex * cey);
    final double da = (dex * aey) - (aex * dey);

    final double ac = (aex * cey) - (cex * aey);
    final double bd = (bex * dey) - (dex * bey);

    final double abc = (aez * bc) - (bez * ac) + (cez * ab);
    final double bcd = (bez * cd) - (cez * bd) + (dez * bc);
    final double cda = (cez * da) + (dez * ac) + (aez * cd);
    final double dab = (dez * ab) + (aez * bd) + (bez * da);

    final double alift = (aex * aex) + (aey * aey) + (aez * aez);
    final double blift = (bex * bex) + (bey * bey) + (bez * bez);
    final double clift = (cex * cex) + (cey * cey) + (cez * cez);
    final double dlift = (dex * dex) + (dey * dey) + (dez * dez);

    return
      ((dlift*abc) - (clift*dab)) + ((blift*cda) - (alift*bcd)); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public Fast () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
