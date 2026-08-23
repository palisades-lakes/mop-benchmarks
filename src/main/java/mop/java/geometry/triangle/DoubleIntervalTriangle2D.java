package mop.java.geometry.triangle;

import mop.java.numbers.DoubleInterval;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Computed error in <code>double</code> calculations
 * using <code>DoubleInterval</code>.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-08-15
 */

public final class DoubleIntervalTriangle2D extends Triangle2D {

  // TODO: DoubleInterval vectors
  // cache vector result of translating p0 to origin,
  // and related quantities
  private final DoubleInterval _x10;
  private final DoubleInterval _y10;
  private final DoubleInterval _v10Norm2;
  private final DoubleInterval getX10 () { return _x10; }
  private final DoubleInterval getY10 () { return _y10; }
  private final DoubleInterval getV10Norm2 () { return _v10Norm2; }

  private final DoubleInterval _x20;
  private final DoubleInterval _y20;
  private final DoubleInterval _v20Norm2;
  private final DoubleInterval getX20 () {  return _x20; }
  private final DoubleInterval getY20 () {  return _y20; }
  private final DoubleInterval getV20Norm2 () { return _v20Norm2; }

  private final DoubleInterval _V20xV10;
  private final DoubleInterval getV20xV10 () {  return _V20xV10; }

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return false; }

  public final DoubleInterval twiceSignedAreaInterval () {
    return getV20xV10().negate(); }

  public final double twiceSignedArea () {
    return twiceSignedAreaInterval().doubleValue(); }

  //--------------------------------------------------------------------
//  /** More exact than rounding to <code>double</code>. */
//
//  public final int orientation () {
//    final DoubleInterval i = getV20xV10();
//    if (getV20xV10().containsZero()) { return 0; }
//    if (0.0 < getV20xV10().min()) { return -1; }
//    return 1; }

  //--------------------------------------------------------------------

  public final boolean inCircleDistanceExact () { return false; }

  public final DoubleInterval inCircleInterval (final Vector2D p) {

    // TODO: DoubleIntervalVector operations
    final DoubleInterval xp0 = DoubleInterval.dif(p.getX(),getP0().getX());
    final DoubleInterval yp0 = DoubleInterval.dif(p.getY(),getP0().getY());

    final DoubleInterval bxp = DoubleInterval.crossProduct(getX10(),getY10(),xp0,yp0);
    final DoubleInterval bxc = getV20xV10();
    final DoubleInterval pxc = DoubleInterval.crossProduct(xp0,yp0,getX20(),getY20());

    final DoubleInterval p2 = DoubleInterval.l2norm2(xp0,yp0);
    final DoubleInterval b2 = getV10Norm2();
    final DoubleInterval c2 = getV20Norm2();

    return DoubleInterval.dot(p2,b2,c2,bxc,pxc,bxp); }

  public final double inCircleDistance (final Vector2D p) {
    return inCircleInterval(p).doubleValue(); }

  //--------------------------------------------------------------------

  public final String toString () {
    return toHexString() + ":" + twiceSignedAreaInterval(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private DoubleIntervalTriangle2D (final Vector2D a,
                                    final Vector2D b,
                                    final Vector2D c)  {
    super(a,b,c);
    final double ax = a.getX();
    final double ay = a.getY();

    _x10 = DoubleInterval.dif(b.getX(),ax);
    _y10 = DoubleInterval.dif(b.getY(),ay);
    _v10Norm2 = DoubleInterval.l2norm2(_x10,_y10);

    _x20 = DoubleInterval.dif(c.getX(),ax);
    _y20 = DoubleInterval.dif(c.getY(),ay);

    _v20Norm2 = DoubleInterval.l2norm2(_x20,_y20);
    _V20xV10 = DoubleInterval.crossProduct(_x20, _y20,_x10, _y10); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new DoubleIntervalTriangle2D(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
