package mop.java.geometry.triangle;

import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.ZeroSimplex;
import mop.java.geometry.euclidean.VectorBF2;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.numbers.BigFloat;

import java.util.Map;

/** Standard calculations implemented in BigFloat.
 * Should be exact, with <code>double</code> inputs.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-10-14
 */

public final class TriangleBF2 implements TriangleR2 {

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

  private VectorBF2 _v10;
  public final VectorBF2 getV10 () {
    if (null == _v10) {
      _v10 = VectorBF2.dif(getP1(),getP0()); }
    return _v10; }

  private BigFloat _v10Norm2;
  public final BigFloat getV10Norm2 () {
    if (null==_v10Norm2) { _v10Norm2 = getV10().l2norm2(); }
    return _v10Norm2; }

  private VectorBF2 _v20;
  public final VectorBF2 getV20 () {
    if (null == _v20) {
      _v20 = VectorBF2.dif(getP2(),getP0()); }
    return _v20; }

  private BigFloat _v20Norm2;
  public final BigFloat getV20Norm2 () {
    if (null==_v20Norm2) { _v20Norm2 = getV20().l2norm2(); }
    return _v20Norm2; }

  private BigFloat _v20Xv10;
  public final BigFloat getV20xV10 () {
    if (null== _v20Xv10) { _v20Xv10 = getV20().wedge(getV10()); }
    return _v20Xv10; }

  /** force cache calculation when profiling */
  public final void clearCaches () {
    _v10 = null;
    _v10Norm2 = null;
    _v20 = null;
    _v20Norm2 = null;
    _v20Xv10 = null; }

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

  public final BigFloat inCircleDistanceBF (final VectorD2 p) {

    final VectorBF2 p0 = VectorBF2.dif(p,getP0());

    final BigFloat bxp = getV10().wedge(p0);
    final BigFloat bxc = getV20xV10();
    final BigFloat pxc = p0.wedge(getV20());

    final BigFloat p2 = p0.l2norm2();
    final BigFloat b2 = getV10Norm2();
    final BigFloat c2 = getV20Norm2();

    // TODO: reverse crossProducts
    return BigFloat.dot(p2,b2,c2,bxc,pxc,bxp); }

  public final double inCircleDistance (final VectorD2 p) {
    return inCircleDistanceBF(p).doubleValue(); }

  //--------------------------------------------------------------------

  public final double inCircle (final VectorD2 p) {

    final BigFloat icd = inCircleDistanceBF(p);
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

  private TriangleBF2 (final VectorD2 a,
                       final VectorD2 b,
                       final VectorD2 c)  {
    super();
    this.p0 = a; this.p1 = b; this.p2 = c; }

  public static final TriangleR2 of (final VectorD2 a,
                                     final VectorD2 b,
                                     final VectorD2 c) {
    return new TriangleBF2(a, b, c); }

  /** Convert other triangle classes. */

  public static final TriangleR2 from (final TriangleR2 t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  public static final TriangleR2 from (final TwoSimplex f,
                                       final Map<ZeroSimplex,VectorD2> e) {
    return of(e.get(f.z0()), e.get(f.z1()), e.get(f.z2())); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
