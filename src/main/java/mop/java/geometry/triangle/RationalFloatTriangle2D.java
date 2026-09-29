package mop.java.geometry.triangle;

import mop.java.geometry.euclidean.VectorD2;
import mop.java.numbers.RationalFloat;

/** Standard calculations implemented in RationalFloat.
 * Should be exact, up to RationalFloat resolution.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 202-09-29
 */

public final class RationalFloatTriangle2D implements TriangleR2 {

  // TODO: only need p0
  private final VectorD2 p0;
  private final VectorD2 p1;
  private final VectorD2 p2;

  @Override
  public final VectorD2 getP0 () { return p0; }
  @Override
  public final VectorD2 getP1 () { return p1; }
  @Override
  public final VectorD2 getP2 () { return p2; }
  //--------------------------------------------------------------------

  @Override
  public final boolean signedAreaExact () { return true; }

  // TODO: reduce the number of RationalFloat instances.
  //  For example, implement RationalFloat.add/subtract(double,double);
  //  Also, triangle translation could be done just once.
  //  Consider boolean predicate, so can return the sign of the
  //  final RationalFloat.
  // TODO: RationalFloatVector, RationalFloatTriangle...

  @Override
  public final double twiceSignedArea () {
    final VectorD2 pa = getP0();
    final VectorD2 pb = getP1();
    final VectorD2 pc = getP2();
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

  @Override
  public final boolean inCircleDistanceExact () { return true; }

  @Override
  public final double inCircleDistance (final VectorD2 p) {
    final VectorD2 pa = getP0();
    final VectorD2 pb = getP1();
    final VectorD2 pc = getP2();
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

  private RationalFloatTriangle2D (final VectorD2 a,
                                   final VectorD2 b,
                                   final VectorD2 c)  {
    super();
    this.p0 = a; this.p1 = b; this.p2 = c; }

  public static final TriangleR2 of (final VectorD2 a,
                                     final VectorD2 b,
                                     final VectorD2 c) {
    return new RationalFloatTriangle2D(a,b,c); }

  /** Convert other triangle classes. */

  public static final TriangleR2 from (final TriangleR2 t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
