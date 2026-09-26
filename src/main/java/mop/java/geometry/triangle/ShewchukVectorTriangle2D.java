package mop.java.geometry.triangle;

import mop.java.geometry.euclidean.VectorD2;
import mop.java.numbers.RelaxedInterval;

/** Same calculations as <code>TriangleD2Eager</code>,
 * converted to intervals using error bounds in
 *  <a href="https://www.cs.cmu.edu/~quake/robust.html">
 * "Adaptive Precision Floating-Point Arithmetic
 *  and Fast Robust Geometric Predicates",<br>
 *  Jonathan Richard Shewchuk<br>
 *  October 1, 1997<br>
 *  CMU-CS-96-140R<br>
 *  From Discrete & Computational Geometry 18(3):305–363, October 1997.
 *  </a>
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-26
 */

public final class ShewchukVectorTriangle2D extends AbstractTriangle2D {

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
  public final double getV20xV10 () {  return _V20xV10; }

  private final double _areaBound;
  public final double areaBound () {  return _areaBound; }

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return false; }

  public final double twiceSignedArea () { return -getV20xV10(); }

  public final RelaxedInterval twiceSignedAreaInterval () {
   //    return RelaxedInterval.plusOrMinus(twiceSignedArea(),areaBound()); }
    // TODO: already nonnegative?
  final double ae = Math.abs(areaBound());
    return new RelaxedInterval(twiceSignedArea()-ae, twiceSignedArea()+ae); }

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

  private static final double EPSILON = 0x1.0p-53;
  private static final double AREA_FACTOR =
    16 * 8 * (EPSILON * (3.0 + 16.0 * EPSILON));
  private static final double INCIRCLE_FACTOR =
    (10.0 + 96.0 * EPSILON) * EPSILON;

  public final double inCircleBound (final VectorD2 p) {

    final VectorD2 p0 = p.subtract(getP0());

    final double p2 = p0.l2norm2();
    final double b2 = getV10Norm2();
    final double c2 = getV20Norm2();

    final double xy21 = Math.abs(getV10().x() * getV20().y());
    final double xy12 = Math.abs(getV20().x() * getV10().y());
    final double xyp2 = Math.abs(p0.x() * getV20().y());
    final double xy2p = Math.abs(getV20().x() * p0.y());
    final double xy1p = Math.abs(getV10().x() * p0.y());
    final double xyp1 = Math.abs(p0.x() * getV10().y());
    return INCIRCLE_FACTOR *
      ((p2 * (xy21+xy12)) + (b2 * (xyp2+xy2p)) + (c2 * (xy1p+xyp1))); }

  public final boolean inCircleIntervals () { return true; }

  public final RelaxedInterval inCircleInterval (final VectorD2 p) {
    final double z = inCircleDistance(p);
    // TODO: already nonnegative?
    final double e = Math.abs(inCircleBound(p));
    return new RelaxedInterval(z-e, z+e); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private ShewchukVectorTriangle2D (final VectorD2 a,
                                    final VectorD2 b,
                                    final VectorD2 c)  {
    super(a,b,c);
    _v10 = getP1().subtract(getP0());
    _v10Norm2 = getV10().l2norm2();
    _v20 = getP2().subtract(getP0());
    _v20Norm2 = getV20().l2norm2();
    _V20xV10 = getV20().wedge(getV10());
    final double xy21 = Math.abs(getV10().x() * getV20().y());
    final double xy12 = Math.abs(getV20().x() * getV10().y());
    _areaBound = AREA_FACTOR * (xy21 + xy12); }

  public static final Triangle2D of (final VectorD2 a,
                                             final VectorD2 b,
                                             final VectorD2 c) {
    return new ShewchukVectorTriangle2D(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
