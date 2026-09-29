package mop.java.geometry.triangle;

import mop.java.geometry.euclidean.VectorBF2X;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.numbers.BigFloatX;

/** Standard calculations implemented in BigFloatX.
 * Should be exact, up to BigFloatX resolution.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 202-09-27
 */

public final class TriangleBF2X implements TriangleR2 {

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

  private VectorBF2X _v10;
  public final VectorBF2X getV10 () {
    if (null == _v10) {
      _v10 = VectorBF2X.dif(getP1(),getP0()); }
    return _v10; }

  private BigFloatX _v10Norm2;
  public final BigFloatX getV10Norm2 () {
    if (null==_v10Norm2) { _v10Norm2 = getV10().l2norm2(); }
    return _v10Norm2; }

  private VectorBF2X _v20;
  public final VectorBF2X getV20 () {
    if (null == _v20) {
      _v20 = VectorBF2X.dif(getP2(),getP0()); }
    return _v20; }

  private BigFloatX _v20Norm2;
  public final BigFloatX getV20Norm2 () {
    if (null==_v20Norm2) { _v20Norm2 = getV20().l2norm2(); }
    return _v20Norm2; }

  private BigFloatX _v20Xv10;
  public final BigFloatX getV20xV10 () {
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

  public final BigFloatX inCircleDistanceBF (final VectorD2 p) {

    final VectorBF2X p0 = VectorBF2X.dif(p,getP0());

    final BigFloatX bxp = getV10().wedge(p0);
    final BigFloatX bxc = getV20xV10();
    final BigFloatX pxc = p0.wedge(getV20());

    final BigFloatX p2 = p0.l2norm2();
    final BigFloatX b2 = getV10Norm2();
    final BigFloatX c2 = getV20Norm2();

    // TODO: reverse crossProducts
    return BigFloatX.dot(p2,b2,c2,bxc,pxc,bxp); }

  public final double inCircleDistance (final VectorD2 p) {
    return inCircleDistanceBF(p).doubleValue(); }

  //--------------------------------------------------------------------

  public final double inCircle (final VectorD2 p) {

    // TODO: BigFloatXVector operations
    final VectorBF2X p0 = VectorBF2X.dif(p,getP0());

    final BigFloatX bxp = getV10().wedge(p0);
    final BigFloatX bxc = getV20xV10();
    final BigFloatX pxc = p0.wedge(getV20());

    final BigFloatX p2 = p0.l2norm2();
    final BigFloatX b2 = getV10Norm2();
    final BigFloatX c2 = getV20Norm2();

    // TODO: reverse crossProducts
    final BigFloatX icd = BigFloatX.dot(p2,b2,c2,bxc,pxc,bxp);
    if (! icd.isFinite()) { return icd.doubleValue(); }
    if (icd.isZero()) { return 0.0; }
    if (icd.nonNegative()) { return 1.0; }
    return -1.0; }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private TriangleBF2X (final VectorD2 a,
                        final VectorD2 b,
                        final VectorD2 c)  {
    super();
    this.p0 = a; this.p1 = b; this.p2 = c; }

  public static final TriangleR2 of (final VectorD2 a,
                                     final VectorD2 b,
                                     final VectorD2 c) {
    return new TriangleBF2X(a, b, c); }

  /** Convert other triangle classes. */

  public static final TriangleR2 from (final TriangleR2 t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
