package mop.java.geometry.triangle;

import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Same calculations as <code>EagerTriangle2D</code>,
 *  implemented in <code>Double</code>>.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-17
 */

public final class DoubleTriangle2D extends Triangle2D {

  // cache vector result of translating p0 to origin,
  // and related quantities

  private final Double _x10;
  private final Double _y10;
  private final Double _v10Norm2;
  private final Double getX10 () { return _x10; }
  private final Double getY10 () { return _y10; }
  private final Double getV10Norm2 () { return _v10Norm2; }

  private final Double _x20;
  private final Double _y20;
  private final Double _v20Norm2;
  private final Double getX20 () {  return _x20; }
  private final Double getY20 () {  return _y20; }
  private final Double getV20Norm2 () { return _v20Norm2; }

  private final Double _V20xV10;
  private final Double getV20xV10 () {  return _V20xV10; }

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return false; }

  public final double twiceSignedArea () { return -getV20xV10(); }

  //--------------------------------------------------------------------

  private static final Double crossProduct (final Double x0,
                                            final Double y0,
                                            final Double x1,
                                            final Double y1) {
    return x0*y1 - x1*y0; }

  private static final Double l2norm2 (final Double x,
                                       final Double y) {
    return x*x + y*y; }

  private static final Double dot (final Double x0,
                                   final Double y0,
                                   final Double z0,
                                   final Double x1,
                                   final Double y1,
                                   final Double z1) {
    return x0*x1 + y0*y1 + z0*z1; }

  //--------------------------------------------------------------------

  public final boolean inCircleDistanceExact () { return false; }

  public final double inCircleDistance (final Vector2D p) {

    final Double xp0 = p.getX() - getP0().getX();
    final Double yp0 = p.getY() - getP0().getY();

    final Double bxp = crossProduct(getX10(),getY10(),xp0,yp0);
    final Double bxc = getV20xV10();
    final Double pxc = crossProduct(xp0,yp0,getX20(),getY20());

    final Double p2 = l2norm2(xp0,yp0);
    final Double b2 = getV10Norm2();
    final Double c2 = getV20Norm2();

    return dot(p2,b2,c2,bxc,pxc,bxp); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private DoubleTriangle2D (final Vector2D a,
                            final Vector2D b,
                            final Vector2D c)  {
    super(a,b,c);

    final double ax = a.getX();
    final double ay = a.getY();

    _x10 = b.getX() - ax;
    _y10 = b.getY() - ay;
    _v10Norm2 = l2norm2(_x10,_y10);

    _x20 = c.getX() - ax;
    _y20 = c.getY() - ay;

    _v20Norm2 = l2norm2(_x20,_y20);
    _V20xV10 = crossProduct(_x20, _y20,_x10, _y10); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new DoubleTriangle2D(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
