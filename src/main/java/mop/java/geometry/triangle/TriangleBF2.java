package mop.java.geometry.triangle;

import mop.java.geometry.euclidean.VectorBF2;
import mop.java.numbers.BigFloat;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Standard calculations implemented in BigFloat.
 * Should be exact, up to BigFloat resolution.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-21
 */

public final class TriangleBF2 extends Triangle2D {

  // cache vector result of translating p0 to origin,
  // and related quantities

  private VectorBF2 _v10;
  private final VectorBF2 getV10 () {
    if (null == _v10) {
      _v10 = VectorBF2.dif(getP1(),getP0()); }
    return _v10; }

  private BigFloat _v10Norm2;
  private final BigFloat getV10Norm2 () {
    if (null==_v10Norm2) { _v10Norm2 = getV10().l2norm2(); }
    return _v10Norm2; }

  private VectorBF2 _v20;
  private final VectorBF2 getV20 () {
    if (null == _v20) {
      _v20 = VectorBF2.dif(getP2(),getP0()); }
    return _v20; }

  private BigFloat _v20Norm2;
  private final BigFloat getV20Norm2 () {
    if (null==_v20Norm2) { _v20Norm2 = getV20().l2norm2(); }
    return _v20Norm2; }

  private BigFloat _V20xV10;
  public final BigFloat getV20xV10 () {
    if (null==_V20xV10) { _V20xV10 = getV20().wedge(getV10()); }
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

  public final BigFloat inCircleDistanceBF (final Vector2D p) {

    final VectorBF2 p0 = VectorBF2.dif(p,getP0());

    final BigFloat bxp = getV10().wedge(p0);
    final BigFloat bxc = getV20xV10();
    final BigFloat pxc = p0.wedge(getV20());

    final BigFloat p2 = p0.l2norm2();
    final BigFloat b2 = getV10Norm2();
    final BigFloat c2 = getV20Norm2();

    // TODO: reverse crossProducts
    return BigFloat.dot(p2,b2,c2,bxc,pxc,bxp); }

  public final double inCircleDistance (final Vector2D p) {
    return inCircleDistanceBF(p).doubleValue(); }

  //--------------------------------------------------------------------

  public final double inCircle (final Vector2D p) {

    // TODO: BigFloatVector operations
    final VectorBF2 p0 = VectorBF2.dif(p,getP0());

    final BigFloat bxp = getV10().wedge(p0);
    final BigFloat bxc = getV20xV10();
    final BigFloat pxc = p0.wedge(getV20());

    final BigFloat p2 = p0.l2norm2();
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

  private TriangleBF2 (final Vector2D a,
                       final Vector2D b,
                       final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new TriangleBF2(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
