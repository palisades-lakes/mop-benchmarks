package mop.java.geometry.triangle;

import mop.java.numbers.BigFloat;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Standard calculations implemented in BigFloat.
 * Should be exact, up to BigFloat resolution.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-07
 */

public final class BigFloatTriangle2D extends Triangle2D {

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return true; }

  // TODO: reduce the number of BigFloat instances.
  //  For example, implement BigFloat.sum(double,double);
  //  Also, triangle translation could be done just once.
  //  Consider boolean predicate, so can return the sign of the
  //  final BigFloat.
  // TODO: BigFloatVector, Vector<BigFloat>...

  public final double signedArea () {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();

    final BigFloat ax = BigFloat.valueOf(pa.getX());
    final BigFloat ay = BigFloat.valueOf(pa.getY());
    final BigFloat bx = BigFloat.valueOf(pb.getX());
    final BigFloat by = BigFloat.valueOf(pb.getY());
    final BigFloat cx = BigFloat.valueOf(pc.getX());
    final BigFloat cy = BigFloat.valueOf(pc.getY());
    final BigFloat acx = ax.subtract(cx);
    final BigFloat acy = ay.subtract(cy);
    final BigFloat bcx = bx.subtract(cx);
    final BigFloat bcy = by.subtract(cy);
    return
      ((acx.multiply(bcy)).subtract(acy.multiply(bcx))).doubleValue(); }

  //--------------------------------------------------------------------

  public final boolean inCircleExact () { return true; }

  public final double inCircle (final Vector2D p) {
    // TODO: move BigFloat creation to BigFloat.subtract(double,double)
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();

    final BigFloat ax = BigFloat.valueOf(pa.getX());
    final BigFloat ay = BigFloat.valueOf(pa.getY());
    final BigFloat bx = BigFloat.valueOf(pb.getX());
    final BigFloat by = BigFloat.valueOf(pb.getY());
    final BigFloat cx = BigFloat.valueOf(pc.getX());
    final BigFloat cy = BigFloat.valueOf(pc.getY());
    final BigFloat dx = BigFloat.valueOf(p.getX());
    final BigFloat dy = BigFloat.valueOf(p.getY());
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
  // construction
  //--------------------------------------------------------------------

  private BigFloatTriangle2D (final Vector2D a,
                              final Vector2D b,
                              final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new BigFloatTriangle2D(a,b,c); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
