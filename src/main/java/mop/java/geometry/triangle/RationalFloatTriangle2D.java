package mop.java.geometry.triangle;

import mop.java.numbers.RationalFloat;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Standard calculations implemented in RationalFloat.
 * Should be exact, up to RationalFloat resolution.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-27
 */

public final class RationalFloatTriangle2D extends Triangle2D {

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return true; }

  // TODO: reduce the number of RationalFloat instances.
  //  For example, implement RationalFloat.add/subtract(double,double);
  //  Also, triangle translation could be done just once.
  //  Consider boolean predicate, so can return the sign of the
  //  final RationalFloat.
  // TODO: RationalFloatVector, RationalFloatTriangle...

  public final double signedArea () {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();
    final RationalFloat ax = RationalFloat.valueOf(pa.getX());
    final RationalFloat ay = RationalFloat.valueOf(pa.getY());
    final RationalFloat bx = RationalFloat.valueOf(pb.getX());
    final RationalFloat by = RationalFloat.valueOf(pb.getY());
    final RationalFloat cx = RationalFloat.valueOf(pc.getX());
    final RationalFloat cy = RationalFloat.valueOf(pc.getY());
    final RationalFloat acx = ax.subtract(cx);
    final RationalFloat acy = ay.subtract(cy);
    final RationalFloat bcx = bx.subtract(cx);
    final RationalFloat bcy = by.subtract(cy);
    return
      ((acx.multiply(bcy)).subtract(acy.multiply(bcx))).doubleValue(); }

  //--------------------------------------------------------------------

  public final boolean inCircleExact () { return true; }

  public final double inCircle (final Vector2D p) {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();
    final RationalFloat ax = RationalFloat.valueOf(pa.getX());
    final RationalFloat ay = RationalFloat.valueOf(pa.getY());
    final RationalFloat bx = RationalFloat.valueOf(pb.getX());
    final RationalFloat by = RationalFloat.valueOf(pb.getY());
    final RationalFloat cx = RationalFloat.valueOf(pc.getX());
    final RationalFloat cy = RationalFloat.valueOf(pc.getY());
    final RationalFloat dx = RationalFloat.valueOf(p.getX());
    final RationalFloat dy = RationalFloat.valueOf(p.getY());
    final RationalFloat adx = ax.subtract(dx);
    final RationalFloat bdx = bx.subtract(dx);
    final RationalFloat cdx = cx.subtract(dx);
    final RationalFloat ady = ay.subtract(dy);
    final RationalFloat bdy = by.subtract(dy);
    final RationalFloat cdy = cy.subtract(dy);

    final RationalFloat abdet = adx.multiply(bdy).subtract(bdx.multiply(ady));
    final RationalFloat bcdet = bdx.multiply(cdy).subtract(cdx.multiply(bdy));
    final RationalFloat cadet = cdx.multiply(ady).subtract(adx.multiply(cdy));
    final RationalFloat alift = adx.multiply(adx).add(ady.multiply(ady));
    final RationalFloat blift = bdx.multiply(bdx).add(bdy.multiply(bdy));
    final RationalFloat clift = cdx.multiply(cdx).add(cdy.multiply(cdy));

    return alift.multiply(bcdet)
                .add(blift.multiply(cadet))
                .add(clift.multiply(abdet))
                .doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private RationalFloatTriangle2D (final Vector2D a,
                              final Vector2D b,
                              final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new RationalFloatTriangle2D(a,b,c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
