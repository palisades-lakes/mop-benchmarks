package mop.java.geometry.triangle;

import mop.java.numbers.BigFloat;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Standard calculations implemented in BigFloat.
 * Should be exact, up to BigFloat resolution.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-01
 */

public final class BigFloatTriangle2D extends Triangle2D {

  // TODO: BigFloat vectors
  // cache vector result of translating p0 to origin,
  // and related quantities

  private BigFloat _x10;
  private final BigFloat getX10 () {
    if (null == _x10) {
      _x10 = BigFloat.dif(getP1().getX(),getP0().getX()); }
    return _x10; }

  private BigFloat _y10;
  private final BigFloat getY10 () {
    if (null == _y10) {
      _y10 = BigFloat.dif(getP1().getY(),getP0().getY()); }
    return _y10; }

  private BigFloat _v10Norm2;
  private final BigFloat getV10Norm2 () {
    if (null==_v10Norm2) {
      _v10Norm2 = BigFloat.l2norm2(getX10(),getY10()); }
    return _v10Norm2; }

  private BigFloat _x20;
  private final BigFloat getX20 () {
    if (null == _x20) {
      _x20 = BigFloat.dif(getP2().getX(),getP0().getX()); }
    return _x20; }

  private BigFloat _y20;
  private final BigFloat getY20 () {
    if (null == _y20) {
      _y20 = BigFloat.dif(getP2().getY(),getP0().getY()); }
    return _y20; }

  private BigFloat _v20Norm2;
  private final BigFloat getV20Norm2 () {
    if (null==_v20Norm2) {
      _v20Norm2 = BigFloat.l2norm2(getX20(),getY20()); }
    return _v20Norm2; }

  private BigFloat _V20xV10;
  private final BigFloat getV20xV10 () {
    if (null==_V20xV10) {
      _V20xV10 = BigFloat.crossProduct(getX20(), getY20(),
                                       getX10(), getY10()); }
    return _V20xV10; }

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return true; }

  public final double twiceSignedArea () {
    return -(getV20xV10().doubleValue()); }

  //--------------------------------------------------------------------

  public final boolean isOrientationRobust () { return true; }

  /** More exact than rounding to <code>double</code>. */

  public final double orientation () {
    if (! getV20xV10().isFinite()) {
      return getV20xV10().doubleValue(); }
    if (getV20xV10().isZero()) { return 0.0; }
    if (getV20xV10().nonNegative()) { return -1.0; }
    return 1.0; }

  //--------------------------------------------------------------------

  public final boolean inCircleDistanceExact () { return true; }

  public final double inCircleDistance (final Vector2D p) {

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
    final BigFloat icd = BigFloat.dot(p2,b2,c2,bxc,pxc,bxp);
    if (! icd.isFinite()) { return icd.doubleValue(); }
    if (icd.isZero()) { return 0.0; }
    if (icd.nonNegative()) { return 1.0; }
    return -1.0; }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private BigFloatTriangle2D (final Vector2D a,
                              final Vector2D b,
                              final Vector2D c)  {
    super(a,b,c);
     }

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
