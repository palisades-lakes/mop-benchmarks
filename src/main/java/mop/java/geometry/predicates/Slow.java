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

    // TODO: XDouble.cross2d?
    final XDouble axby =  XDouble.twoTwoProduct(aex,bey);
    final XDouble bxay =  XDouble.twoTwoProduct(bex,aey.negate());
    final XDouble ab =  axby.add(bxay);

    final XDouble bxcy = XDouble.twoTwoProduct(bex,cey);
    final XDouble cxby = XDouble.twoTwoProduct(cex,bey.negate());
    final XDouble bc = bxcy.add(cxby);

    final XDouble cxdy = XDouble.twoTwoProduct(cex,dey);
    final XDouble dxcy = XDouble.twoTwoProduct(dex,cey.negate());
    final XDouble cd = cxdy.add(dxcy);

    final XDouble dxay = XDouble.twoTwoProduct(dex,aey);
    final XDouble axdy = XDouble.twoTwoProduct(aex,dey.negate());
    final XDouble da = dxay.add(axdy);

    final XDouble axcy = XDouble.twoTwoProduct(aex,cey);
    final XDouble cxay = XDouble.twoTwoProduct(cex,aey.negate());
    final XDouble ac = axcy.add(cxay);

    final XDouble bxdy = XDouble.twoTwoProduct(bex,dey);
    final XDouble dxby = XDouble.twoTwoProduct(dex,bey.negate());
    final XDouble bd = bxdy.add(dxby);

    XDouble temp64a, temp64b, temp64c, temp192;
    temp64a = cd.multiply(bez.negate());
    temp64b = bd.multiply(cez);
    temp64c = bc.multiply(dez.negate());
    temp192 = temp64a.add(temp64b).add(temp64c);

    XDouble detx, detxx, detxt, detxxt, detxtxt, x1, x2;
    XDouble dety, detyy, detyt, detyyt, detytyt, y1, y2;
    XDouble detxy;
    XDouble detz, detzz, detzt, detzzt, detztzt, z1, z2;

    detx = temp192.multiply(aex.hi());
    detxx = detx.multiply(aex.hi());
    detxt = temp192.multiply(aex.lo());
    detxxt = detxt.multiply(aex.hi()).fast2x();
    detxtxt = detxt.multiply(aex.lo());
    x1 = detxx.add(detxxt);
    x2 = x1.add(detxtxt);

    dety = temp192.multiply(aey.hi());
    detyy = dety.multiply(aey.hi());
    detyt = temp192.multiply(aey.lo());
    detyyt = detyt.multiply(aey.hi()).fast2x();
    detytyt = detyt.multiply(aey.lo());
    y1 = detyy.add(detyyt);
    y2 = y1.add(detytyt);

    detz = temp192.multiply(aez.hi());
    detzz = detz.multiply(aez.hi());
    detzt = temp192.multiply(aez.lo());
    detzzt = detzt.multiply(aez.hi()).fast2x();
    detztzt = detzt.multiply(aez.lo());
    z1 = detzz.add(detzzt);
    z2 = z1.add(detztzt);

    detxy = x2.add(y2);
    final XDouble adet = z2.add(detxy);

    temp64a = da.multiply(cez);
    temp64b = ac.multiply(dez);
    temp64c = cd.multiply(aez);
    temp192 = temp64a.add(temp64b).add(temp64c);

    detx = temp192.multiply(bex.hi());
    detxx = detx.multiply(bex.hi());
    detxt = temp192.multiply(bex.lo());
    detxxt = detxt.multiply(bex.hi()).fast2x();
    detxtxt = detxt.multiply(bex.lo());
    x1 = detxx.add(detxxt);
    x2 = x1.add(detxtxt);

    dety = temp192.multiply(bey.hi());
    detyy = dety.multiply(bey.hi());
    detyt = temp192.multiply(bey.lo());
    detyyt = detyt.multiply(bey.hi()).fast2x();
    detytyt = detyt.multiply(bey.lo());
    y1 = detyy.add(detyyt);
    y2 = y1.add(detytyt);

    detz = temp192.multiply(bez.hi());
    detzz = detz.multiply(bez.hi());
    detzt = temp192.multiply(bez.lo());
    detzzt = detzt.multiply(bez.hi()).fast2x();
    detztzt = detzt.multiply(bez.lo());
    z1 = detzz.add(detzzt);
    z2 = z1.add(detztzt);

    detxy = x2.add(y2);
    final XDouble bdet = z2.add(detxy);

    temp64a = ab.multiply(dez.negate());
    temp64b = bd.multiply(aez.negate());
    temp64c = da.multiply(bez.negate());
    temp192 = temp64a.add(temp64b).add(temp64c);

    detx = temp192.multiply(cex.hi());
    detxx = detx.multiply(cex.hi());
    detxt = temp192.multiply(cex.lo());
    detxxt = detxt.multiply(cex.hi()).fast2x();
    detxtxt = detxt.multiply(cex.lo());
    x1 = detxx.add(detxxt);
    x2 = x1.add(detxtxt);

    dety = temp192.multiply(cey.hi());
    detyy = dety.multiply(cey.hi());
    detyt = temp192.multiply(cey.lo());
    detyyt = detyt.multiply(cey.hi()).fast2x();
    detytyt = detyt.multiply(cey.lo());
    y1 = detyy.add(detyyt);
    y2 = y1.add(detytyt);

    detz = temp192.multiply(cez.hi());
    detzz = detz.multiply(cez.hi());
    detzt = temp192.multiply(cez.lo());
    detzzt = detzt.multiply(cez.hi()).fast2x();
    detztzt = detzt.multiply(cez.lo());
    z1 = detzz.add(detzzt);
    z2 = z1.add(detztzt);

    detxy = x2.add(y2);
    final XDouble cdet = z2.add(detxy);

    temp64a = bc.multiply(aez);
    temp64b = ac.multiply(bez.negate());
    temp64c = ab.multiply(cez);
    temp192 = temp64a.add(temp64b).add(temp64c);

    detx = temp192.multiply(dex.hi());
    detxx = detx.multiply(dex.hi());
    detxt = temp192.multiply(dex.lo());
    detxxt = detxt.multiply(dex.hi()).fast2x();
    detxtxt = detxt.multiply(dex.lo());
    x1 = detxx.add(detxxt);
    x2 = x1.add(detxtxt);

    dety = temp192.multiply(dey.hi());
    detyy = dety.multiply(dey.hi());
    detyt = temp192.multiply(dey.lo());
    detyyt = detyt.multiply(dey.hi()).fast2x();
    detytyt = detyt.multiply(dey.lo());
    y1 = detyy.add(detyyt);
    y2 = y1.add(detytyt);

    detz = temp192.multiply(dez.hi());
    detzz = detz.multiply(dez.hi());
    detzt = temp192.multiply(dez.lo());
    detzzt = detzt.multiply(dez.hi()).fast2x();
    detztzt = detzt.multiply(dez.lo());
    z1 = detzz.add(detzzt);
    z2 = z1.add(detztzt);

    detxy = x2.add(y2);
    final XDouble ddet = z2.add(detxy);

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
