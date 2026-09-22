package mop.java.geometry.triangle;

import mop.java.geometry.euclidean.VectorD2;

/** Triangle with VectorD2 vertices, precomputing reusable values.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-21
 */

public final class TriangleD2Eager extends Triangle2D {

  // precomputed vector result of translating p0 to origin,
  // and related quantities

  private final VectorD2 _v10;
  private final VectorD2 getV10 () { return _v10; }

  private final double _v10Norm2;
  private final double getV10Norm2 () { return _v10Norm2; }

  private final VectorD2 _v20;
  private final VectorD2 getV20 () { return _v20; }

  private final double _v20Norm2;
  private final double getV20Norm2 () { return _v20Norm2; }

  private final double _V20xV10;
  private final double getV20xV10 () { return _V20xV10; }

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return false; }

  public final double twiceSignedArea () { return -getV20xV10(); }

  //--------------------------------------------------------------------

  private static final double dot (final double x0,
                                   final double y0,
                                   final double z0,
                                   final double x1,
                                   final double y1,
                                   final double z1) {
    return x0*x1 + y0*y1 + z0*z1; }

  //--------------------------------------------------------------------

  public final boolean inCircleDistanceExact () { return false; }

  public final double inCircleDistance (final VectorD2 p) {

    final VectorD2 vp0 = p.subtract(getP0());

    final double bxp = getV10().wedge(vp0);
    final double bxc = getV20xV10();
    final double pxc = vp0.wedge(getV20());

    final double p2 = vp0.l2norm2();
    final double b2 = getV10Norm2();
    final double c2 = getV20Norm2();

    return dot(p2,b2,c2,bxc,pxc,bxp); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private TriangleD2Eager (final VectorD2 a,
                           final VectorD2 b,
                           final VectorD2 c)  {
    super(a,b,c);
    _v10 = getP1().subtract(getP0());
    _v10Norm2 = getV10().l2norm2();
    _v20 = getP2().subtract(getP0());
    _v20Norm2 = getV20().l2norm2();
    _V20xV10 = getV20().wedge(getV10()); }

  public static final Triangle2D of (final VectorD2 a,
                                     final VectorD2 b,
                                     final VectorD2 c) {
    return new TriangleD2Eager(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
