package mop.java.geometry.triangle;

import mop.java.numbers.BigFloat;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Standard calculations implemented in BigFloat.
 * Should be exact, up to BigFloat resolution.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-08-18
 */

public final class BigFloatTriangle2D extends Triangle2D {

  // TODO: BigFloat vectors
  // cache vector result of translating p0 to origin,
  // and related quantities
  private final BigFloat _x10;
  private final BigFloat _y10;
  private final BigFloat _v10Norm2;
  private final BigFloat getX10 () { return _x10; }
  private final BigFloat getY10 () { return _y10; }
  private final BigFloat getV10Norm2 () { return _v10Norm2; }

  private final BigFloat _x20;
  private final BigFloat _y20;
  private final BigFloat _v20Norm2;
  private final BigFloat getX20 () {  return _x20; }
  private final BigFloat getY20 () {  return _y20; }
  private final BigFloat getV20Norm2 () { return _v20Norm2; }

  private final BigFloat _V20xV10;
  private final BigFloat getV20xV10 () {  return _V20xV10; }

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return true; }

  public final double twiceSignedArea () {
    return -(getV20xV10().doubleValue()); }

  //--------------------------------------------------------------------

  public final boolean isOrientationRobust () { return true; }

  /** More exact than rounding to <code>double</code>. */

  public final int orientation () {
    if (getV20xV10().isZero()) { return 0; }
    if (getV20xV10().nonNegative()) { return -1; }
    return 1; }

  //--------------------------------------------------------------------

  public final boolean inCircleExact () { return true; }

  public final double inCircle (final Vector2D p) {

    // TODO: BigFloatVector operations
    final BigFloat xp0 = BigFloat.dif(p.getX(),getP0().getX());
    final BigFloat yp0 = BigFloat.dif(p.getY(),getP0().getY());

    final BigFloat bxp = BigFloat.crossProduct(getX10(),getY10(),xp0,yp0);
    final BigFloat bxc = getV20xV10();
    final BigFloat pxc = BigFloat.crossProduct(xp0,yp0,getX20(),getY20());

    final BigFloat p2 = BigFloat.l2norm2(xp0,yp0);
    final BigFloat b2 = getV10Norm2();
    final BigFloat c2 = getV20Norm2();

    // TODO: reverse crossProducts
    return BigFloat.dot(p2,b2,c2,bxc,pxc,bxp).doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private BigFloatTriangle2D (final Vector2D a,
                              final Vector2D b,
                              final Vector2D c)  {
    super(a,b,c);
    final double ax = a.getX();
    final double ay = a.getY();

    _x10 = BigFloat.dif(b.getX(),ax);
    _y10 = BigFloat.dif(b.getY(),ay);
    _v10Norm2 = BigFloat.l2norm2(_x10,_y10);

    _x20 = BigFloat.dif(c.getX(),ax);
    _y20 = BigFloat.dif(c.getY(),ay);

    _v20Norm2 = BigFloat.l2norm2(_x20,_y20);
    _V20xV10 = BigFloat.crossProduct(_x20, _y20,_x10, _y10); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new BigFloatTriangle2D(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
