package mop.java.geometry.triangle;

import mop.java.numbers.RoundingInterval;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Computed error in <code>double</code> calculations
 * using <code>RoundingInterval</code>.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-12
 */

public final class RoundingIntervalTriangle2D extends Triangle2D {

  // TODO: RoundingInterval vectors
  // cache vector result of translating p0 to origin,
  // and related quantities
  private RoundingInterval _x10;
  private final RoundingInterval getX10 () {
    if (null == _x10) {
      _x10 = RoundingInterval.dif(getP1().getX(), getP0().getX()); }
    return _x10; }

  private RoundingInterval _y10;
  private final RoundingInterval getY10 () {
    if (null == _y10) {
      _y10 = RoundingInterval.dif(getP1().getY(), getP0().getY()); }
    return _y10; }

  private RoundingInterval _v10Norm2;
  private final RoundingInterval getV10Norm2 () {
    if (null==_v10Norm2) {
      _v10Norm2 = RoundingInterval.l2norm2(getX10(), getY10()); }
    return _v10Norm2; }

  private RoundingInterval _x20;
  private final RoundingInterval getX20 () {
    if (null == _x20) {
      _x20 = RoundingInterval.dif(getP2().getX(), getP0().getX()); }
    return _x20; }

  private RoundingInterval _y20;
  private final RoundingInterval getY20 () {
    if (null == _y20) {
      _y20 = RoundingInterval.dif(getP2().getY(), getP0().getY()); }
    return _y20; }

  private RoundingInterval _v20Norm2;
  private final RoundingInterval getV20Norm2 () {
    if (null==_v20Norm2) {
      _v20Norm2 = RoundingInterval.l2norm2(getX20(), getY20()); }
    return _v20Norm2; }

  private RoundingInterval _V20xV10;
  public final RoundingInterval getV20xV10 () {
    if (null==_V20xV10) {
      _V20xV10 = RoundingInterval.crossProduct(getX20(), getY20(),
                                               getX10(), getY10()); }
    return _V20xV10; }

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return false; }

  public final RoundingInterval twiceSignedAreaInterval () {
    return getV20xV10().negate(); }

  public final double twiceSignedArea () {
    return twiceSignedAreaInterval().doubleValue(); }

  //--------------------------------------------------------------------
//  /** More exact than rounding to <code>double</code>. */
//
//  public final int orientation () {
//    final RoundingInterval i = getV20xV10();
//    if (getV20xV10().containsZero()) { return 0; }
//    if (0.0 < getV20xV10().min()) { return -1; }
//    return 1; }

  //--------------------------------------------------------------------

  public final boolean inCircleDistanceExact () { return false; }

  public final boolean inCircleIntervals () { return true; }

  public final RoundingInterval inCircleInterval (final Vector2D p) {

    // TODO: RelaxedIntervalVector operations
    final RoundingInterval
      xp0 = RoundingInterval.dif(p.getX(), getP0().getX());
    final RoundingInterval
      yp0 = RoundingInterval.dif(p.getY(), getP0().getY());

    final RoundingInterval
      bxp = RoundingInterval.crossProduct(getX10(), getY10(), xp0, yp0);
    final RoundingInterval bxc = getV20xV10();
    final RoundingInterval
      pxc = RoundingInterval.crossProduct(xp0, yp0, getX20(), getY20());

    final RoundingInterval p2 = RoundingInterval.l2norm2(xp0, yp0);
    final RoundingInterval b2 = getV10Norm2();
    final RoundingInterval c2 = getV20Norm2();

    return RoundingInterval.dot(p2, b2, c2, bxc, pxc, bxp); }

  public final double inCircleDistance (final Vector2D p) {
    return inCircleInterval(p).doubleValue(); }

  public final double inCircle (final Vector2D p) {
    final RoundingInterval icd = inCircleInterval(p);
    // TODO: non-finite intervals
    //if (! icd.isFinite()) { return icd.doubleValue(); }
    if (icd.min()>0.0) { return 1.0; }
    if (icd.max()<0.0) { return -1.0; }
    return 0.0; }

  //--------------------------------------------------------------------

  public final String description () {
    return
      toHexString() +
      "\nv10: [" + getX10()  + ", " + getY10() + "]"  +
        "\n|v10|^2: " + getV10Norm2() +
        "\nv20: [" + getX20()  + ", " + getY20() + "]" +
        "\n|v20|^2: " + getV20Norm2() +
        "\nv20Xv10= " + getV20xV10() + "\n"; }

  //--------------------------------------------------------------------

  public final String toString () {
    return toHexString(); }
  //+ " 2*area: " + twiceSignedAreaInterval(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private RoundingIntervalTriangle2D (final Vector2D a,
                                      final Vector2D b,
                                      final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new RoundingIntervalTriangle2D(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
