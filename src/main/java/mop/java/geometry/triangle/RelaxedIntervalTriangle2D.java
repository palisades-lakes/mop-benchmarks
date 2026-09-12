package mop.java.geometry.triangle;

import mop.java.numbers.RelaxedInterval;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Computed error in <code>double</code> calculations
 * using <code>RelaxedInterval</code>.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-12
 */

public final class RelaxedIntervalTriangle2D extends Triangle2D {

  // TODO: RelaxedInterval vectors
  // cache vector result of translating p0 to origin,
  // and related quantities
  private RelaxedInterval _x10;
  private final RelaxedInterval getX10 () {
    if (null == _x10) {
      _x10 = RelaxedInterval.dif(getP1().getX(), getP0().getX()); }
    return _x10; }

  private RelaxedInterval _y10;
  private final RelaxedInterval getY10 () {
    if (null == _y10) {
      _y10 = RelaxedInterval.dif(getP1().getY(), getP0().getY()); }
    return _y10; }

  private RelaxedInterval _v10Norm2;
  private final RelaxedInterval getV10Norm2 () {
    if (null==_v10Norm2) {
      _v10Norm2 = RelaxedInterval.l2norm2(getX10(), getY10()); }
    return _v10Norm2; }

  private RelaxedInterval _x20;
  private final RelaxedInterval getX20 () {
    if (null == _x20) {
      _x20 = RelaxedInterval.dif(getP2().getX(), getP0().getX()); }
    return _x20; }

  private RelaxedInterval _y20;
  private final RelaxedInterval getY20 () {
    if (null == _y20) {
      _y20 = RelaxedInterval.dif(getP2().getY(), getP0().getY()); }
    return _y20; }

  private RelaxedInterval _v20Norm2;
  private final RelaxedInterval getV20Norm2 () {
    if (null==_v20Norm2) {
      _v20Norm2 = RelaxedInterval.l2norm2(getX20(), getY20()); }
    return _v20Norm2; }

  private RelaxedInterval _V20xV10;
  public final RelaxedInterval getV20xV10 () {
    if (null==_V20xV10) {
      _V20xV10 = RelaxedInterval.crossProduct(getX20(), getY20(),
                                             getX10(), getY10()); }
    return _V20xV10; }

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return false; }

  public final RelaxedInterval twiceSignedAreaInterval () {
    return getV20xV10().negate(); }

  public final double twiceSignedArea () {
    return twiceSignedAreaInterval().doubleValue(); }

  //--------------------------------------------------------------------
//  /** More exact than rounding to <code>double</code>. */
//
//  public final int orientation () {
//    final RelaxedInterval i = getV20xV10();
//    if (getV20xV10().containsZero()) { return 0; }
//    if (0.0 < getV20xV10().min()) { return -1; }
//    return 1; }

  //--------------------------------------------------------------------

  public final boolean inCircleDistanceExact () { return false; }

  public final boolean inCircleIntervals () { return true; }

  public final RelaxedInterval inCircleInterval (final Vector2D p) {

    // TODO: RelaxedIntervalVector operations
    final RelaxedInterval
      xp0 = RelaxedInterval.dif(p.getX(), getP0().getX());
    final RelaxedInterval
      yp0 = RelaxedInterval.dif(p.getY(), getP0().getY());

    final RelaxedInterval
      bxp = RelaxedInterval.crossProduct(getX10(), getY10(), xp0, yp0);
    final RelaxedInterval bxc = getV20xV10();
    final RelaxedInterval
      pxc = RelaxedInterval.crossProduct(xp0, yp0, getX20(), getY20());

    final RelaxedInterval p2 = RelaxedInterval.l2norm2(xp0, yp0);
    final RelaxedInterval b2 = getV10Norm2();
    final RelaxedInterval c2 = getV20Norm2();

    return RelaxedInterval.dot(p2, b2, c2, bxc, pxc, bxp); }

  public final double inCircleDistance (final Vector2D p) {
    return inCircleInterval(p).doubleValue(); }

  public final double inCircle (final Vector2D p) {
    final RelaxedInterval icd = inCircleInterval(p);
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

  public final String toString () { return toHexString(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private RelaxedIntervalTriangle2D (final Vector2D a,
                                    final Vector2D b,
                                    final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new RelaxedIntervalTriangle2D(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
