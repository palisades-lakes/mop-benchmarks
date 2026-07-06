package mop.java.geometry.predicates.tetrahedron.macro;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.geometry.predicates.tetrahedron.Tetrahedron3D;
import org.apache.commons.geometry.euclidean.threed.Vector3D;

import static mop.java.geometry.predicates.Expansion.EPSILON;

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
 * @version 2026-07-06
 */

// strictfp unnecessary for JDK17 and later
public final class DefaultMacro extends Tetrahedron3D {

  //--------------------------------------------------------------------
  // signedVolume
  //--------------------------------------------------------------------
  private static final double o3derrboundA =
    (7.0 + 56.0 * EPSILON) * EPSILON;

  public final double signedVolume (final Vector3D pa,
                                    final Vector3D pb,
                                    final Vector3D pc,
                                    final Vector3D pd) {
    double adx, bdx, cdx, ady, bdy, cdy, adz, bdz, cdz;
    double bdxcdy, cdxbdy, cdxady, adxcdy, adxbdy, bdxady;
    double det;
    double permanent, errbound;

    adx = pa.getX() - pd.getX();
    bdx = pb.getX() - pd.getX();
    cdx = pc.getX() - pd.getX();
    ady = pa.getY() - pd.getY();
    bdy = pb.getY() - pd.getY();
    cdy = pc.getY() - pd.getY();
    adz = pa.getZ() - pd.getZ();
    bdz = pb.getZ() - pd.getZ();
    cdz = pc.getZ() - pd.getZ();

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

    return new AdaptMacro().signedVolume(pa, pb, pc, pd, permanent);
  }

  //--------------------------------------------------------------------
  // inSphere
  //--------------------------------------------------------------------

  private static final double isperrboundA =
    (16.0 + 224.0 * EPSILON) * EPSILON;

  public final double inSphere (final Vector3D pa,
                                final Vector3D pb,
                                final Vector3D pc,
                                final Vector3D pd,
                                final Vector3D pe) {
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

    aex = pa.getX() - pe.getX();
    bex = pb.getX() - pe.getX();
    cex = pc.getX() - pe.getX();
    dex = pd.getX() - pe.getX();
    aey = pa.getY() - pe.getY();
    bey = pb.getY() - pe.getY();
    cey = pc.getY() - pe.getY();
    dey = pd.getY() - pe.getY();
    aez = pa.getZ() - pe.getZ();
    bez = pb.getZ() - pe.getZ();
    cez = pc.getZ() - pe.getZ();
    dez = pd.getZ() - pe.getZ();

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

    return new AdaptMacro().inSphere(pa, pb, pc, pd, pe, permanent);
  }
  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public DefaultMacro () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
