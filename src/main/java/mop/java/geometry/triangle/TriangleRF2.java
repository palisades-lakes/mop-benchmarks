package mop.java.geometry.triangle;

import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.euclidean.VectorRF2;
import mop.java.numbers.RationalFloat;

/** Standard calculations implemented in RationalFloat.
 * Should be exact, up to RationalFloat resolution.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 202-09-29
 */

public final class TriangleRF2 implements TriangleR2 {

  // TODO: only need p0
  private final VectorD2 p0;
  private final VectorD2 p1;
  private final VectorD2 p2;

  @Override
  public final VectorD2 getP0 () { return p0; }
  @Override
  public final VectorD2 getP1 () { return p1; }
  @Override
  public final VectorD2 getP2 () { return p2; }

  // cache vector result of translating p0 to origin,
  // and related quantities

  private VectorRF2 _v10;
  public final VectorRF2 getV10 () {
    if (null == _v10) {
      _v10 = VectorRF2.dif(getP1(),getP0()); }
    return _v10; }

  private RationalFloat _v10Norm2;
  public final RationalFloat getV10Norm2 () {
    if (null==_v10Norm2) { _v10Norm2 = getV10().l2norm2(); }
    return _v10Norm2; }

  private VectorRF2 _v20;
  public final VectorRF2 getV20 () {
    if (null == _v20) {
      _v20 = VectorRF2.dif(getP2(),getP0()); }
    return _v20; }

  private RationalFloat _v20Norm2;
  public final RationalFloat getV20Norm2 () {
    if (null==_v20Norm2) { _v20Norm2 = getV20().l2norm2(); }
    return _v20Norm2; }

  private RationalFloat _v20Xv10;
  public final RationalFloat getV20xV10 () {
    if (null== _v20Xv10) { _v20Xv10 = getV20().wedge(getV10()); }
    return _v20Xv10; }

  /** force cache calculation when profiling */
  @SuppressWarnings("unused")
  public final void clearCaches () {
    _v10 = null;
    _v10Norm2 = null;
    _v20 = null;
    _v20Norm2 = null;
    _v20Xv10 = null; }

  //--------------------------------------------------------------------

  @Override
  public final boolean signedAreaExact () { return true; }

  @Override
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

  public final RationalFloat inCircleDistanceRF (final VectorD2 p) {

    final VectorRF2 p0 = VectorRF2.dif(p, getP0());

    final RationalFloat bxp = getV10().wedge(p0);
    final RationalFloat bxc = getV20xV10();
    final RationalFloat pxc = p0.wedge(getV20());

    final RationalFloat p2 = p0.l2norm2();
    final RationalFloat b2 = getV10Norm2();
    final RationalFloat c2 = getV20Norm2();

    // TODO: reverse crossProducts
    return RationalFloat.dot(p2,b2,c2,bxc,pxc,bxp); }

  public final double inCircleDistance (final VectorD2 p) {
    return inCircleDistanceRF(p).doubleValue(); }

  //--------------------------------------------------------------------

  public final double inCircle (final VectorD2 p) {

    final RationalFloat icd = inCircleDistanceRF(p);
    if (! icd.isFinite()) { return icd.doubleValue(); }
    if (icd.isZero()) { return 0.0; }
    if (icd.nonNegative()) { return 1.0; }
    return -1.0; }

  //--------------------------------------------------------------------

  public final String toString () {
    return toHexString() +
      "\nv10: " + getV10().toHexString() +
      "\nv20: " + getV20().toHexString() +
      "\n|v10|^2: " + getV10Norm2().toHexString() +
      "\n|v20|^2: " + getV20Norm2().toHexString() +
      "\nv20 X v10: " + getV20xV10().toHexString() +
      "\n" ; }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private TriangleRF2 (final VectorD2 a,
                       final VectorD2 b,
                       final VectorD2 c)  {
    super();
    this.p0 = a; this.p1 = b; this.p2 = c; }

  public static final TriangleR2 of (final VectorD2 a,
                                     final VectorD2 b,
                                     final VectorD2 c) {
    return new TriangleRF2(a, b, c); }

  /** Convert other triangle classes. */

  public static final TriangleR2 from (final TriangleR2 t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
