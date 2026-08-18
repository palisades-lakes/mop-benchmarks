
package mop.java.geometry.triangle;

import mop.java.numbers.DoubleInterval0;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Computed error in <code>double</code> calculations
 * using <code>DoubleInterval0</code>.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-08-15
 */

public final class DoubleIntervalTriangle2D0 extends Triangle2D {

  // TODO: DoubleInterval0 vectors
  // cache vector result of translating p0 to origin,
  // and related quantities
  private final DoubleInterval0 _x10;
  private final DoubleInterval0 _y10;
  private final DoubleInterval0 _v10Norm2;
  private final DoubleInterval0 getX10 () { return _x10; }
  private final DoubleInterval0 getY10 () { return _y10; }
  private final DoubleInterval0 getV10Norm2 () { return _v10Norm2; }

  private final DoubleInterval0 _x20;
  private final DoubleInterval0 _y20;
  private final DoubleInterval0 _v20Norm2;
  private final DoubleInterval0 getX20 () {  return _x20; }
  private final DoubleInterval0 getY20 () {  return _y20; }
  private final DoubleInterval0 getV20Norm2 () { return _v20Norm2; }

  private final DoubleInterval0 _V20xV10;
  private final DoubleInterval0 getV20xV10 () {  return _V20xV10; }

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return false; }

  public final DoubleInterval0 twiceSignedAreaInterval () {
    return getV20xV10().negate(); }

  public final double twiceSignedArea () {
    return twiceSignedAreaInterval().doubleValue(); }

  //--------------------------------------------------------------------

  public final boolean inCircleExact () { return false; }

  public final DoubleInterval0 inCircleInterval (final Vector2D p) {

    // TODO: DoubleInterval0Vector operations
    final DoubleInterval0 xp0 = DoubleInterval0.dif(p.getX(),getP0().getX());
    final DoubleInterval0 yp0 = DoubleInterval0.dif(p.getY(),getP0().getY());

    final DoubleInterval0 bxp = DoubleInterval0.crossProduct(getX10(),getY10(),xp0,yp0);
    final DoubleInterval0 bxc = getV20xV10();
    final DoubleInterval0 pxc = DoubleInterval0.crossProduct(xp0,yp0,getX20(),getY20());

    final DoubleInterval0 p2 = DoubleInterval0.l2norm2(xp0,yp0);
    final DoubleInterval0 b2 = getV10Norm2();
    final DoubleInterval0 c2 = getV20Norm2();

    return DoubleInterval0.dot(p2,b2,c2,bxc,pxc,bxp); }

  public final double inCircle (final Vector2D p) {
    return inCircleInterval(p).doubleValue(); }

  //--------------------------------------------------------------------

  public final String toString () {
    return toHexString() + ":" + twiceSignedAreaInterval(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private DoubleIntervalTriangle2D0 (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c)  {
    super(a,b,c);
    final double ax = a.getX();
    final double ay = a.getY();

    _x10 = DoubleInterval0.dif(b.getX(),ax);
    _y10 = DoubleInterval0.dif(b.getY(),ay);
    _v10Norm2 = DoubleInterval0.l2norm2(_x10,_y10);

    _x20 = DoubleInterval0.dif(c.getX(),ax);
    _y20 = DoubleInterval0.dif(c.getY(),ay);

    _v20Norm2 = DoubleInterval0.l2norm2(_x20,_y20);
    _V20xV10 = DoubleInterval0.crossProduct(_x20, _y20,_x10, _y10); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new DoubleIntervalTriangle2D0(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
