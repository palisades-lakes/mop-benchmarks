package mop.java.geometry.predicates;

import mop.java.numbers.BigFloat;

/** Standard calculations implemented in BigFloat.
 * Should be exact, up to BigFloat resolution.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-06-9
 */

public final class BigFloatPredicate implements Predicate {

  //--------------------------------------------------------------------

  public final boolean isExact () { return true; }

  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------
  // TODO: reduce the number of BigFloat instances.
  //  For example, implement BigFloat.sum(double,double);
  //  Also, triangle translation could be done just once.
  //  Consider boolean predicate, so can return the sign of the
  //  final BigFloat.

  public final double orient2d (final double[] pa,
                                final double[] pb,
                                final double[] pc) {
    final BigFloat ax = BigFloat.valueOf(pa[0]);
    final BigFloat ay = BigFloat.valueOf(pa[1]);
    final BigFloat bx = BigFloat.valueOf(pb[0]);
    final BigFloat by = BigFloat.valueOf(pb[1]);
    final BigFloat cx = BigFloat.valueOf(pc[0]);
    final BigFloat cy = BigFloat.valueOf(pc[1]);
    final BigFloat acx = ax.subtract(cx);
    final BigFloat acy = ay.subtract(cy);
    final BigFloat bcx = bx.subtract(cx);
    final BigFloat bcy = by.subtract(cy);
    return
      ((acx.multiply(bcy)).subtract(acy.multiply(bcx))).doubleValue(); }

  //--------------------------------------------------------------------
  // orient3d
  //--------------------------------------------------------------------
  // TODO: rewrite as vector operations

  public final double orient3d (final double[] pa,
                                final double[] pb,
                                final double[] pc,
                                final double[] pd) {
    final BigFloat ax = BigFloat.valueOf(pa[0]);
    final BigFloat ay = BigFloat.valueOf(pa[1]);
    final BigFloat az = BigFloat.valueOf(pa[2]);
    final BigFloat bx = BigFloat.valueOf(pb[0]);
    final BigFloat by = BigFloat.valueOf(pb[1]);
    final BigFloat bz = BigFloat.valueOf(pb[2]);
    final BigFloat cx = BigFloat.valueOf(pc[0]);
    final BigFloat cy = BigFloat.valueOf(pc[1]);
    final BigFloat cz = BigFloat.valueOf(pc[2]);
    final BigFloat dx = BigFloat.valueOf(pd[0]);
    final BigFloat dy = BigFloat.valueOf(pd[1]);
    final BigFloat dz = BigFloat.valueOf(pd[2]);
    final BigFloat adx = ax.subtract(dx);
    final BigFloat bdx = bx.subtract(dx);
    final BigFloat cdx = cx.subtract(dx);
    final BigFloat ady = ay.subtract(dy);
    final BigFloat bdy = by.subtract(dy);
    final BigFloat cdy = cy.subtract(dy);
    final BigFloat adz = az.subtract(dz);
    final BigFloat bdz = bz.subtract(dz);
    final BigFloat cdz = cz.subtract(dz);

    return
      adx.multiply(bdy.multiply(cdz).subtract(bdz.multiply(cdy)))
         .add(
           bdx.multiply(cdy.multiply(adz).subtract(cdz.multiply(ady)))
             )
         .add(
           cdx.multiply(ady.multiply(bdz).subtract(adz.multiply(bdy)))
             )
         .doubleValue(); }


  //--------------------------------------------------------------------
  // incircle
  //--------------------------------------------------------------------

  public final double incircle (final double[] pa,
                                final double[] pb,
                                final double[] pc,
                                final double[] pd) {
    // TODO: move BigFloat creation to BigFloat.subtract(double,double)
    final BigFloat ax = BigFloat.valueOf(pa[0]);
    final BigFloat ay = BigFloat.valueOf(pa[1]);
    final BigFloat bx = BigFloat.valueOf(pb[0]);
    final BigFloat by = BigFloat.valueOf(pb[1]);
    final BigFloat cx = BigFloat.valueOf(pc[0]);
    final BigFloat cy = BigFloat.valueOf(pc[1]);
    final BigFloat dx = BigFloat.valueOf(pd[0]);
    final BigFloat dy = BigFloat.valueOf(pd[1]);
    final BigFloat adx = ax.subtract(dx);
    final BigFloat bdx = bx.subtract(dx);
    final BigFloat cdx = cx.subtract(dx);
    final BigFloat ady = ay.subtract(dy);
    final BigFloat bdy = by.subtract(dy);
    final BigFloat cdy = cy.subtract(dy);

    final BigFloat abdet = adx.multiply(bdy).subtract(bdx.multiply(ady));
    final BigFloat bcdet = bdx.multiply(cdy).subtract(cdx.multiply(bdy));
    final BigFloat cadet = cdx.multiply(ady).subtract(adx.multiply(cdy));
    final BigFloat alift = adx.multiply(adx).add(ady.multiply(ady));
    final BigFloat blift = bdx.multiply(bdx).add(bdy.multiply(bdy));
    final BigFloat clift = cdx.multiply(cdx).add(cdy.multiply(cdy));

    return alift.multiply(bcdet)
                .add(blift.multiply(cadet))
                .add(clift.multiply(abdet))
                .doubleValue(); }

  //--------------------------------------------------------------------
  // insphere
  //--------------------------------------------------------------------

  public final double insphere (final double[] pa,
                                final double[] pb,
                                final double[] pc,
                                final double[] pd,
                                final double[] pe) {
    final BigFloat ax = BigFloat.valueOf(pa[0]);
    final BigFloat ay = BigFloat.valueOf(pa[1]);
    final BigFloat az = BigFloat.valueOf(pa[2]);
    final BigFloat bx = BigFloat.valueOf(pb[0]);
    final BigFloat by = BigFloat.valueOf(pb[1]);
    final BigFloat bz = BigFloat.valueOf(pb[2]);
    final BigFloat cx = BigFloat.valueOf(pc[0]);
    final BigFloat cy = BigFloat.valueOf(pc[1]);
    final BigFloat cz = BigFloat.valueOf(pc[2]);
    final BigFloat dx = BigFloat.valueOf(pd[0]);
    final BigFloat dy = BigFloat.valueOf(pd[1]);
    final BigFloat dz = BigFloat.valueOf(pd[2]);
    final BigFloat ex = BigFloat.valueOf(pe[0]);
    final BigFloat ey = BigFloat.valueOf(pe[1]);
    final BigFloat ez = BigFloat.valueOf(pe[2]);
    final BigFloat aex = ax.subtract(ex);
    final BigFloat bex = bx.subtract(ex);
    final BigFloat cex = cx.subtract(ex);
    final BigFloat dex = dx.subtract(ex);
    final BigFloat aey = ay.subtract(ey);
    final BigFloat bey = by.subtract(ey);
    final BigFloat cey = cy.subtract(ey);
    final BigFloat dey = dy.subtract(ey);
    final BigFloat aez = az.subtract(ez);
    final BigFloat bez = bz.subtract(ez);
    final BigFloat cez = cz.subtract(ez);
    final BigFloat dez = dz.subtract(ez);

    final BigFloat ab = aex.multiply(bey).subtract(bex.multiply(aey));
    final BigFloat bc = bex.multiply(cey).subtract(cex.multiply(bey));
    final BigFloat cd = cex.multiply(dey).subtract(dex.multiply(cey));
    final BigFloat da = dex.multiply(aey).subtract(aex.multiply(dey));

    final BigFloat ac = aex.multiply(cey).subtract(cex.multiply(aey));
    final BigFloat bd = bex.multiply(dey).subtract(dex.multiply(bey));

    final BigFloat abc =
      aez.multiply(bc).subtract(bez.multiply(ac)).add(cez.multiply(ab));
    final BigFloat bcd = bez.multiply(cd).subtract(
      cez.multiply(bd)).add(dez.multiply(bc));
    final BigFloat cda = cez.multiply(da).add(
      dez.multiply(ac)).add(aez.multiply(cd));
    final BigFloat dab = dez.multiply(ab).add(
      aez.multiply(bd)).add(bez.multiply(da));

    final BigFloat alift = aex.square().add(aey.square().add(aez.square()));
    final BigFloat blift = bex.square().add(bey.square().add(bez.square()));
    final BigFloat clift = cex.square().add(cey.square().add(cez.square()));
    final BigFloat dlift = dex.square().add(dey.square().add(dez.square()));

    return
      dlift.multiply(abc).subtract(clift.multiply(dab))
           .add(blift.multiply(cda).subtract(alift.multiply(bcd)))
           .doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public BigFloatPredicate () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
