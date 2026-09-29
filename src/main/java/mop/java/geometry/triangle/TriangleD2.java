package mop.java.geometry.triangle;

import mop.java.geometry.euclidean.VectorD2;

/** Minimal triangle with VectorD2 vertices, nothing cached.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-27
 */

public class TriangleD2 implements TriangleR2 {

  private final VectorD2 p0;
  private final VectorD2 p1;
  private final VectorD2 p2;

  @Override
  public final VectorD2 getP0 () { return p0; }
  @Override
  public final VectorD2 getP1 () { return p1; }
  @Override
  public final VectorD2 getP2 () { return p2; }

  public VectorD2 getV10 () { return getP1().subtract(getP0()); }
  public double getV10Norm2 () { return getV10().l2norm2(); }
  public VectorD2 getV20 () { return getP1().subtract(getP0()); }
  public double getV20Norm2 () { return getV20().l2norm2(); }
  public double getV20xV10 () {  return getV20().wedge(getV10()); }

  public static final double EPSILON = 0x1.0p-53;

  //--------------------------------------------------------------------

  private static double triArea (final VectorD2 a,
                                 final VectorD2 b,
                                 final VectorD2 c) {
    // TODO: cache difference vectors
    return b.subtract(a).wedge(c.subtract(a)); }

  //--------------------------------------------------------------------
  // orient2d
  public static final double AREA_FACTOR =
    16 * 8 * (EPSILON * (3.0 + 16.0 * EPSILON));

  /** Shewchuk error bound A for twiceSignedArea. */
  public double areaBound () {
    final double xy21 = Math.abs(getV10().getX() * getV20().getY());
    final double xy12 = Math.abs(getV20().getX() * getV10().getY());
    return AREA_FACTOR * (xy21 + xy12); }


  //--------------------------------------------------------------------
  // cache?

  @Override
  public double twiceSignedArea () {
    return triArea(getP0(),getP1(),getP2()); }

  //--------------------------------------------------------------------
  // inCircle
  public static final double INCIRCLE_FACTOR =
    (10.0 + 96.0 * EPSILON) * EPSILON;

  public final double inCircleBound (final VectorD2 p) {

    final VectorD2 p0 = p.subtract(getP0());

    final double p2 = p0.l2norm2();
    final double b2 = getV10Norm2();
    final double c2 = getV20Norm2();

    final double xy21 = Math.abs(getV10().getX() * getV20().y());
    final double xy12 = Math.abs(getV20().getX() * getV10().y());
    final double xyp2 = Math.abs(p0.getX() * getV20().y());
    final double xy2p = Math.abs(getV20().getX() * p0.y());
    final double xy1p = Math.abs(getV10().getX() * p0.y());
    final double xyp1 = Math.abs(p0.getX() * getV10().y());
    return INCIRCLE_FACTOR *
      ((p2 * (xy21+xy12)) + (b2 * (xyp2+xy2p)) + (c2 * (xy1p+xyp1))); }

  //--------------------------------------------------------------------
  // TODO: permute area calls to use cached differences?

  @Override
  public double inCircleDistance (final VectorD2 p) {
    // TODO: cache l2norm2?
    final VectorD2 a = getP0();
    final VectorD2 b = getP1();
    final VectorD2 c = getP2();

    return
      a.l2norm2()*triArea(b,c,p) - b.l2norm2()*triArea(a,c,p)
        + c.l2norm2()*triArea(a,b,p) - p.l2norm2()*triArea(a,b,c); }

  //--------------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------------
  // TODO: hashcode, equals

  @Override
  public final String toHexString () {
    return getClass().getSimpleName() + "[" +
      p0.toHexString() + ", " +
      p1.toHexString() + ", " +
      p2.toHexString() + "]"; }

  @Override
  public String toString () { return toHexString(); }

  @Override
  public String description () { return toString(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public TriangleD2 (final VectorD2 a,
                      final VectorD2 b,
                      final VectorD2 c)  {
    super();
    this.p0 = a; this.p1 = b; this.p2 = c; }

  public static TriangleR2 of (final VectorD2 a,
                               final VectorD2 b,
                               final VectorD2 c) {
    return new TriangleD2(a, b, c); }

  /** Convert other triangle classes. */

  public static TriangleR2 from (final TriangleR2 t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

//-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
