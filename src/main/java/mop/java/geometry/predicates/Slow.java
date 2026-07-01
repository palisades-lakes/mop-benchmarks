package mop.java.geometry.predicates;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.numbers.Hilo;
import mop.java.numbers.XDouble;

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
 * @version 2026-06-20
 */

// strictfp unnecessary for JDK17 and later
@SuppressWarnings("unused")
public final class Slow implements Predicate {

  //--------------------------------------------------------------------

  public final boolean isExact () { return true; }

  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------
  // TODO: seems to return 2xsigned area

  public final double orient2d (final double[] pa,
                                final double[] pb,
                                final double[] pc) {
    final Hilo ax = Hilo.twoDiff(pa[0], pc[0]);
    final Hilo ay = Hilo.twoDiff(pa[1], pc[1]);
    final Hilo bx = Hilo.twoDiff(pb[0], pc[0]);
    final Hilo by = Hilo.twoDiff(pb[1], pc[1]);

    final XDouble axby = XDouble.twoTwoProduct(ax, by);
    final XDouble bxay = XDouble.twoTwoProduct(bx, ay);
    return axby.subtract(bxay).doubleValue(); }

  //--------------------------------------------------------------------
  // incircle
  //--------------------------------------------------------------------

  private static final XDouble det (final Hilo ax,
                                    final Hilo ay,
                                    final Hilo bx,
                                    final Hilo by,
                                    final Hilo cx,
                                    final Hilo cy) {

    final XDouble axby = XDouble.twoTwoProduct(ax, by);
    final XDouble bxay = XDouble.twoTwoProduct(bx, ay);
    final XDouble sum = axby.subtract(bxay);

    final XDouble sxhihi = sum.multiply(cx.hi()).multiply(cx.hi());
    final XDouble sxlo = sum.multiply(cx.lo());
    final XDouble sxlohi2 = sxlo.multiply(cx.hi()).fast2x();
    final XDouble sxlolo = sxlo.multiply(cx.lo());
    final XDouble detx = sxhihi.add(sxlohi2).add(sxlolo);

    final XDouble syhihi = sum.multiply(cy.hi()).multiply(cy.hi());
    final XDouble sylo = sum.multiply(cy.lo());
    final XDouble sylohi2 = sylo.multiply(cy.hi()).fast2x();
    final XDouble sylolo = sylo.multiply(cy.lo());
    final XDouble dety = syhihi.add(sylohi2).add(sylolo);

    return detx.add(dety); }

  //--------------------------------------------------------------------
  /** signed distance of <code>pd</code> from the circumcircle thru
   * <code>pa,pb,pc</code>, negative means outside.
   */

  public final double incircle (final double[] pa,
                                final double[] pb,
                                final double[] pc,
                                final double[] pd) {

    final Hilo ax = Hilo.twoDiff(pa[0], pd[0]);
    final Hilo ay = Hilo.twoDiff(pa[1], pd[1]);
    final Hilo bx = Hilo.twoDiff(pb[0], pd[0]);
    final Hilo by = Hilo.twoDiff(pb[1], pd[1]);
    final Hilo cx = Hilo.twoDiff(pc[0], pd[0]);
    final Hilo cy = Hilo.twoDiff(pc[1], pd[1]);
    final XDouble ad = det(bx,by,cx,cy,ax,ay);
    final XDouble bd = det(cx,cy,ax,ay,bx,by);
    final XDouble cd = det(ax,ay,bx,by,cx,cy);
    return cd.add(bd).add(ad).doubleValue(); }

  //--------------------------------------------------------------------
  // orient3d
  //--------------------------------------------------------------------
  public final double orient3d (final double[] pa,
                                final double[] pb,
                                final double[] pc,
                                final double[] pd) {
    final Hilo adx = Hilo.twoDiff(pa[0], pd[0]);
    final Hilo ady = Hilo.twoDiff(pa[1], pd[1]);
    final Hilo adz = Hilo.twoDiff(pa[2], pd[2]);
    final Hilo bdx = Hilo.twoDiff(pb[0], pd[0]);
    final Hilo bdy = Hilo.twoDiff(pb[1], pd[1]);
    final Hilo bdz = Hilo.twoDiff(pb[2], pd[2]);
    final Hilo cdx = Hilo.twoDiff(pc[0], pd[0]);
    final Hilo cdy = Hilo.twoDiff(pc[1], pd[1]);
    final Hilo cdz = Hilo.twoDiff(pc[2], pd[2]);

    final XDouble axby = XDouble.twoTwoProduct(adx, bdy);
    final XDouble bxay = XDouble.twoTwoProduct(bdx, ady.negate());
    final XDouble bxcy = XDouble.twoTwoProduct(bdx, cdy);
    final XDouble cxby = XDouble.twoTwoProduct(cdx, bdy.negate());
    final XDouble cxay = XDouble.twoTwoProduct(cdx, ady);
    final XDouble axcy = XDouble.twoTwoProduct(adx, cdy.negate());

    final XDouble adet = bxcy.add(cxby).multiply(adz);
    final XDouble bdet = cxay.add(axcy).multiply(bdz);
    final XDouble cdet = axby.add(bxay).multiply(cdz);

    return adet.add(bdet).add(cdet).doubleValue();  }

  //--------------------------------------------------------------------
  // insphere
  //--------------------------------------------------------------------

  private static final XDouble insphereDet (final XDouble cd,
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
  public final double insphere (final double[] pa,
                                final double[] pb,
                                final double[] pc,
                                final double[] pd,
                                final double[] pe) {

    final Hilo aex = Hilo.twoDiff(pa[0], pe[0]);
    final Hilo aey = Hilo.twoDiff(pa[1], pe[1]);
    final Hilo aez = Hilo.twoDiff(pa[2], pe[2]);
    final Hilo bex = Hilo.twoDiff(pb[0], pe[0]);
    final Hilo bey = Hilo.twoDiff(pb[1], pe[1]);
    final Hilo bez = Hilo.twoDiff(pb[2], pe[2]);
    final Hilo cex = Hilo.twoDiff(pc[0], pe[0]);
    final Hilo cey = Hilo.twoDiff(pc[1], pe[1]);
    final Hilo cez = Hilo.twoDiff(pc[2], pe[2]);
    final Hilo dex = Hilo.twoDiff(pd[0], pe[0]);
    final Hilo dey = Hilo.twoDiff(pd[1], pe[1]);
    final Hilo dez = Hilo.twoDiff(pd[2], pe[2]);

    final XDouble ab = XDouble.crossProduct(aex,aey,bex,bey);
    final XDouble bc = XDouble.crossProduct(bex,bey,cex,cey);
    final XDouble cd = XDouble.crossProduct(cex,cey,dex,dey);
    final XDouble da = XDouble.crossProduct(dex,dey,aex,aey);
    final XDouble ac = XDouble.crossProduct(aex,aey,cex,cey);
    final XDouble bd = XDouble.crossProduct(bex,bey,dex,dey);

    final XDouble adet =
      insphereDet(cd,bez.negate(),bd,cez,bc,dez.negate(),aex,aey,aez);
    final XDouble bdet =
      insphereDet(da,cez,ac,dez,cd,aez,bex,bey,bez);
    final XDouble cdet =
      insphereDet(ab,dez.negate(),bd,aez.negate(),da,bez.negate(),
                  cex,cey,cez);
    final XDouble ddet =
      insphereDet(bc,aez,ac,bez.negate(),ab,cez,dex,dey,dez);

    final XDouble deter = adet.add(bdet).add(cdet).add(ddet);
    return deter.doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public Slow () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
