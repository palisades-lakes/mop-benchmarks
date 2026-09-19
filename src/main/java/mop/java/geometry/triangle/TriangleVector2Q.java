package mop.java.geometry.triangle;

import mop.java.geometry.euclidean.Vector2Q;
import mop.java.numbers.DoubleValue;

/** Minimal triangle with Vector2Q vertices, caching reusable values.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-19
 */

public final class TriangleVector2Q extends Triangle2D {

  // cache vector result of translating p0 to origin,
  // and related quantities

  private Vector2Q _v10;
  private final Vector2Q getV10 () {
    if (null == _v10) { _v10 = getP1().subtract(getP0()); }
    return _v10; }

  private Object _v10Norm2;
  private final Object getV10Norm2 () {
    // TODO: what if computed norm is NaN?
    if (null == _v10Norm2)) { _v10Norm2 = getV10().l2norm2(); }
    return _v10Norm2; }

  private Vector2Q _v20;
  private final Vector2Q getV20 () {
    if (null == _v20) { _v20 = getP2().subtract(getP0()); }
    return _v20; }

  private Object _v20Norm2;
  private final Object getV20Norm2 () {
    // TODO: what if computed norm is NaN?
    if (null == _v20Norm2) { _v20Norm2 = getV20().l2norm2(); }
    return _v20Norm2; }

  private Object _V20xV10;
  private final Object getV20xV10 () {
    // TODO: what if computed corss product is NaN?
    if (null == _V20xV10)) { _V20xV10 = getV20().blade(getV10()); }
    return _V20xV10; }

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return false; }

  public final double twiceSignedArea () {
    return -(DoubleValue.doubleValue(getV20xV10()); }

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

  public final double inCircleDistance (final Vector2Q p) {

    final Vector2Q vp0 = p.subtract(getP0());

    final double bxp = getV10().blade(vp0);
    final double bxc = getV20xV10();
    final double pxc = vp0.blade(getV20());

    final double p2 = vp0.normSq();
    final double b2 = getV10Norm2();
    final double c2 = getV20Norm2();

    return dot(p2,b2,c2,bxc,pxc,bxp); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private TriangleVector2Q (final Vector2Q a,
                            final Vector2Q b,
                            final Vector2Q c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2Q a,
                                     final Vector2Q b,
                                     final Vector2Q c) {
    return new TriangleVector2Q(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
