package mop.java.geometry.triangle;

import mop.java.numbers.DoubleInterval;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Same calculations as <code>DoubleTriangle2D</code>,
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
 * @version 2026-09-03
 */

public final class ShewchukIntervalTriangle2D extends Triangle2D {

  // cache vector result of translating p0 to origin,
  // and related quantities

  private final double _x10;
  private final double _y10;
  private final double _v10Norm2;
  private final double getX10 () { return _x10; }
  private final double getY10 () { return _y10; }
  private final double getV10Norm2 () { return _v10Norm2; }

  private final double _x20;
  private final double _y20;
  private final double _v20Norm2;
  private final double getX20 () {  return _x20; }
  private final double getY20 () {  return _y20; }
  private final double getV20Norm2 () { return _v20Norm2; }

  private final double _V20xV10;
  private final double getV20xV10 () {  return _V20xV10; }

  private final double _areaBound;
  public final double areaBound () {  return _areaBound; }

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return false; }

  public final double twiceSignedArea () { return -getV20xV10(); }

  public final DoubleInterval twiceSignedAreaInterval () {
    return DoubleInterval.plusOrMinus(twiceSignedArea(),areaBound()); }


  //--------------------------------------------------------------------

  private static final double crossProduct (final double x0,
                                            final double y0,
                                            final double x1,
                                            final double y1) {
    return x0*y1 - x1*y0; }

  private static final double l2norm2 (final double x,
                                       final double y) {
    return x*x + y*y; }

  private static final double dot (final double x0,
                                   final double y0,
                                   final double z0,
                                   final double x1,
                                   final double y1,
                                   final double z1) {
    return x0*x1 + y0*y1 + z0*z1; }

  //--------------------------------------------------------------------

  public final boolean inCircleDistanceExact () { return false; }

  public final double inCircleDistance (final Vector2D p) {

    final double xp0 = p.getX() - getP0().getX();
    final double yp0 = p.getY() - getP0().getY();

    final double bxp = crossProduct(getX10(),getY10(),xp0,yp0);
    final double bxc = getV20xV10();
    final double pxc = crossProduct(xp0,yp0,getX20(),getY20());

    final double p2 = l2norm2(xp0,yp0);
    final double b2 = getV10Norm2();
    final double c2 = getV20Norm2();

    return dot(p2,b2,c2,bxc,pxc,bxp); }

  public final double inCircleBound (final Vector2D p) {

    final double xp0 = p.getX() - getP0().getX();
    final double yp0 = p.getY() - getP0().getY();

    final double p2 = l2norm2(xp0,yp0);
    final double b2 = getV10Norm2();
    final double c2 = getV20Norm2();

    final double factor = (10.0 + 96.0 * EPSILON) * EPSILON;
    return factor *
      ((p2 * (Math.abs(_x20*_y10) + Math.abs(_y20*_x10))) +
        (b2 * (Math.abs(xp0*_y20) + Math.abs(yp0*_x20))) +
        (c2 * (Math.abs(_x10*yp0) + Math.abs(_y10*xp0)))); }

  public final DoubleInterval inCircleInterval (final Vector2D p) {

    return DoubleInterval.plusOrMinus(
      inCircleDistance(p),
      inCircleBound(p)); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private static final double EPSILON = 0x1.0p-53;

  private ShewchukIntervalTriangle2D (final Vector2D a,
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

    _V20xV10 = crossProduct(_x20, _y20, _x10, _y10);

    _areaBound =
      16 * 8 * (EPSILON * (3.0 + 16.0 * EPSILON)) *
        (Math.abs(_x20*_y10) + Math.abs(_y20*_x10));
  }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new ShewchukIntervalTriangle2D(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
