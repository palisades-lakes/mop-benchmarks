package mop.java.geometry.predicates.tetrahedron;

// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.numbers.Hilo;
import mop.java.numbers.XDouble;
import org.apache.commons.geometry.euclidean.threed.Vector3D;

/**
 * More exact tests.  Robust.
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
 * This version's priority is correctness, and simplicity.
 * Later versions can optimize guided by benchmarks and profiling.
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
@SuppressWarnings("unused")
public final class Slow extends Tetrahedron3D {

  //--------------------------------------------------------------------

  public final boolean signedVolumeExact () { return true; }

  public final double signedVolume (final Vector3D pa,
                                    final Vector3D pb,
                                    final Vector3D pc,
                                    final Vector3D pd) {
    final Hilo adx = Hilo.subtract(pa.getX(), pd.getX());
    final Hilo ady = Hilo.subtract(pa.getY(), pd.getY());
    final Hilo adz = Hilo.subtract(pa.getZ(), pd.getZ());
    final Hilo bdx = Hilo.subtract(pb.getX(), pd.getX());
    final Hilo bdy = Hilo.subtract(pb.getY(), pd.getY());
    final Hilo bdz = Hilo.subtract(pb.getZ(), pd.getZ());
    final Hilo cdx = Hilo.subtract(pc.getX(), pd.getX());
    final Hilo cdy = Hilo.subtract(pc.getY(), pd.getY());
    final Hilo cdz = Hilo.subtract(pc.getZ(), pd.getZ());

    final XDouble axby = XDouble.product(adx, bdy);
    final XDouble bxay = XDouble.product(bdx, ady.negate());
    final XDouble bxcy = XDouble.product(bdx, cdy);
    final XDouble cxby = XDouble.product(cdx, bdy.negate());
    final XDouble cxay = XDouble.product(cdx, ady);
    final XDouble axcy = XDouble.product(adx, cdy.negate());

    final XDouble adet = bxcy.add(cxby).multiply(adz);
    final XDouble bdet = cxay.add(axcy).multiply(bdz);
    final XDouble cdet = axby.add(bxay).multiply(cdz);

    return adet.add(bdet).add(cdet).doubleValue();  }

  //--------------------------------------------------------------------
  // inSphere
  //--------------------------------------------------------------------

  public final boolean inSphereExact () { return true; }

  private static final XDouble inSphereDet (final XDouble cd,
                                            final Hilo bez,
                                            final XDouble bd,
                                            final Hilo cez,
                                            final XDouble bc,
                                            final Hilo dez,
                                            final Hilo aex,
                                            final Hilo aey,
                                            final Hilo aez) {

    final XDouble temp192 = cd.multiply(bez)
                              .add(bd.multiply(cez))
                              .add(bc.multiply(dez));

    final XDouble detxx = temp192.multiply(aex.hi()).multiply(aex.hi());
    final XDouble detxt = temp192.multiply(aex.lo());
    final XDouble detxxt = detxt.multiply(aex.hi()).fast2x();
    final XDouble detxtxt = detxt.multiply(aex.lo());

    final XDouble detyy = temp192.multiply(aey.hi()).multiply(aey.hi());
    final XDouble detyt = temp192.multiply(aey.lo());
    final XDouble detyyt = detyt.multiply(aey.hi()).fast2x();
    final XDouble detytyt = detyt.multiply(aey.lo());

    final XDouble detzz = temp192.multiply(aez.hi()).multiply(aez.hi());
    final XDouble detzt = temp192.multiply(aez.lo());
    final XDouble detzzt = detzt.multiply(aez.hi()).fast2x();
    final XDouble detztzt = detzt.multiply(aez.lo());

    return
      detxx.add(detxxt).add(detxtxt)
           .add(detyy).add(detyyt).add(detytyt)
           .add(detzz).add(detzzt).add(detztzt); }

  //--------------------------------------------------------------------
  public final double inSphere (final Vector3D pa,
                                final Vector3D pb,
                                final Vector3D pc,
                                final Vector3D pd,
                                final Vector3D pe) {

    final Hilo aex = Hilo.subtract(pa.getX(), pe.getX());
    final Hilo aey = Hilo.subtract(pa.getY(), pe.getY());
    final Hilo aez = Hilo.subtract(pa.getZ(), pe.getZ());
    final Hilo bex = Hilo.subtract(pb.getX(), pe.getX());
    final Hilo bey = Hilo.subtract(pb.getY(), pe.getY());
    final Hilo bez = Hilo.subtract(pb.getZ(), pe.getZ());
    final Hilo cex = Hilo.subtract(pc.getX(), pe.getX());
    final Hilo cey = Hilo.subtract(pc.getY(), pe.getY());
    final Hilo cez = Hilo.subtract(pc.getZ(), pe.getZ());
    final Hilo dex = Hilo.subtract(pd.getX(), pe.getX());
    final Hilo dey = Hilo.subtract(pd.getY(), pe.getY());
    final Hilo dez = Hilo.subtract(pd.getZ(), pe.getZ());

    final XDouble ab = XDouble.crossProduct(aex,aey,bex,bey);
    final XDouble bc = XDouble.crossProduct(bex,bey,cex,cey);
    final XDouble cd = XDouble.crossProduct(cex,cey,dex,dey);
    final XDouble da = XDouble.crossProduct(dex,dey,aex,aey);
    final XDouble ac = XDouble.crossProduct(aex,aey,cex,cey);
    final XDouble bd = XDouble.crossProduct(bex,bey,dex,dey);

    final XDouble adet =
      inSphereDet(cd,bez.negate(),bd,cez,bc,dez.negate(),aex,aey,aez);
    final XDouble bdet =
      inSphereDet(da,cez,ac,dez,cd,aez,bex,bey,bez);
    final XDouble cdet =
      inSphereDet(ab,dez.negate(),bd,aez.negate(),da,bez.negate(),
                  cex,cey,cez);
    final XDouble ddet =
      inSphereDet(bc,aez,ac,bez.negate(),ab,cez,dex,dey,dez);

    final XDouble deter = adet.add(bdet).add(cdet).add(ddet);
    return deter.doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public Slow () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
