package mop.java.numbers;

//----------------------------------------------------------------------
/** A <code>double</code> interval, closed at both ends.
 * The interval will contain the exact rational
 * value of whatever is being calculated.
 * <br>
 * Ideally, this will create the smallest such closed
 * <code>double</code> interval.
 * In that case the end points would be the same <code>double</code>
 * (a single point interval), or a <code>double</code>
 * and its <code>nextUp</code> or <code>nextDown</code>.
 * <br>
 * At present, this is not true. What is actually produced is
 * a compromise, trading looser intervals for performance.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-12
 */

public record RoundingInterval(double min, double max)
  implements DoubleInterval {

  //--------------------------------------------------------------
  // Ringlike
  //--------------------------------------------------------------
  // TODO: infinities?

  public static final RoundingInterval ZERO =
    new RoundingInterval(0.0, 0.0);

  public static final RoundingInterval ONE =
    new RoundingInterval(1.0,1.0);

  public static final RoundingInterval NaN =
    new RoundingInterval(Double.NaN, Double.NaN);

  //--------------------------------------------------------------

  //  @Override
  public final RoundingInterval negate () {
    if (isNaN()) { return NaN; }
    return new RoundingInterval(-max, -min); }

  @Override
  public final RoundingInterval abs () {
    // assuming Math.abs() is exact, just flips sign bit, so no rounding
    if (isNaN()) { return NaN; }
    final double z0 = Math.abs(min);
    final double z1 = Math.abs(max);
    if (containsZero()) {
      return new RoundingInterval(0.0,Math.max(z0,z1)); }
    if (z0<=z1) { return new RoundingInterval(z0,z1); }
    return new RoundingInterval(z1,z0); }

  //--------------------------------------------------------------
  /** Return the smallest double interval covering the exact value
   * of <code>z0+z1</code>,
   */

  private static final RoundingInterval sum (final double z0,
                                             final double z1) {
    //if (Double.isNaN(z0) || Double.isNaN(z1)) { return NaN; }
    final double hi = z0 + z1;
    final double e1 = hi - z0;
    final double e0 = hi - e1;
    final double r1 = z1 - e1;
    final double r0 = z0 - e0;
    final double lo = r0 + r1;
    // rounded down
    if (0.0<lo) { return new RoundingInterval(hi, Math.nextUp(hi)); }
    // rounded up
    if (0.0>lo) { return new RoundingInterval(Math.nextDown(hi), hi); }
    // no rounding
    return new RoundingInterval(hi, hi); }

  private static final RoundingInterval sum (final RoundingInterval i,
                                             final double z) {
    final double zmin;
    { final double hi = i.min + z;
      final double e1 = hi - z;
      final double e0 = hi - e1;
      final double r1 = i.min - e1;
      final double r0 = z - e0;
      final double lo = r0 + r1;
      zmin = (lo < 0.0) ? Math.nextDown(hi) : hi; }

    final double zmax;
    { final double hi = i.max + z;
      final double e1 = hi - z;
      final double e0 = hi - e1;
      final double r1 = i.max - e1;
      final double r0 = z - e0;
      final double lo = r0 + r1;
      zmax = (lo > 0.0) ? Math.nextUp(hi) : hi; }

    return new RoundingInterval(zmin,zmax); }

//  private static final RoundingInterval sum (final RoundingInterval i,
//                                             final double z) {
//    return new RoundingInterval(
//      sum(i.min, z).min,
//      sum(i.max, z).max); }

  private static final RoundingInterval sum (final double z0,
                                             final double z1,
                                             final double z2) {
    return sum(sum(z0,z1),z2); }

  //--------------------------------------------------------------
  /** Return the smallest double interval covering the exact value
   * of <code>z0-z1</code>,
   */

  public static final RoundingInterval dif (final double z0,
                                            final double z1) {
    final double mz1 = -z1;
    final double hi = z0 + mz1;
    final double e1 = hi - z0;
    final double e0 = hi - e1;
    final double r1 = mz1 - e1;
    final double r0 = z0 - e0;
    final double lo = r0 + r1;
    // rounded down
    if (0.0<lo) { return new RoundingInterval(hi, Math.nextUp(hi)); }
    // rounded up
    if (0.0>lo) { return new RoundingInterval(Math.nextDown(hi), hi); }
    // no rounding
    return new RoundingInterval(hi, hi); }

  //--------------------------------------------------------------

  public final RoundingInterval add (final RoundingInterval q) {
    if (isNaN() || q.isNaN()) { return NaN; }
    return new RoundingInterval(
      sum(min, q.min).min,
      sum(max, q.max).max); }

  //--------------------------------------------------------------

  public final RoundingInterval subtract (final RoundingInterval q) {
    if (isNaN() || q.isNaN()) { return NaN; }
    return new RoundingInterval(
      dif(min, q.max).min,
      dif(max, q.min).max); }

  // FMA version
  public static final RoundingInterval product (final double z0,
                                                final double z1) {
    final double hi = (z0 * z1);
    final double lo = Math.fma(z0,z1,-hi);
    // rounded down
    if (0.0<lo) { return new RoundingInterval(hi, Math.nextUp(hi)); }
    // rounded up
    if (0.0>lo) { return new RoundingInterval(Math.nextDown(hi), hi); }
    // no rounding
    return new RoundingInterval(hi, hi); }

  //--------------------------------------------------------------

  public final RoundingInterval multiply (final RoundingInterval q) {
    //if (isNaN() || q.isNaN()) { return NaN; }
    final RoundingInterval z00 = product(min,q.min);
    final RoundingInterval z01 = product(min,q.max);
    final RoundingInterval z10 = product(max,q.min);
    final RoundingInterval z11 = product(max,q.max);
    double zmin = Math.min(z00.min,z01.min);
    if (z10.min < zmin) { zmin = z10.min; }
    if (z11.min < zmin) { zmin = z11.min; }
    double zmax = Math.max(z00.max,z01.max);
    if (z10.max > zmax) { zmax = z10.max; }
    if (z11.max > zmax) { zmax = z11.max; }
    return new RoundingInterval(zmin, zmax);  }

  //--------------------------------------------------------------

  // FMA version
  public static final RoundingInterval square (final double z) {
    final double hi =  (z * z);
    final double lo = Math.fma(z,z,-hi);
    // rounded down
    if (0.0<lo) { return new RoundingInterval(hi, Math.nextUp(hi)); }
    // rounded up
    if (0.0>lo) {
      final double min = Math.max(0.0,Math.nextDown(hi));
      return new RoundingInterval(min, hi); }
    // no rounding
    return new RoundingInterval(hi, hi); }

  //  @Override
  public final RoundingInterval square () {
    //if (isNaN()) { return NaN; }
    final RoundingInterval z0 = square(min);
    final RoundingInterval z1 = square(max);
    if (containsZero()) {
      return new RoundingInterval(0.0, Math.max(z0.max,z1.max)); }
    return new RoundingInterval(
      Math.min(z0.min,z1.min),
      Math.max(z0.max,z1.max));  }

  //--------------------------------------------------------------
  // geometry
  //--------------------------------------------------------------

  public static final RoundingInterval l2norm2 (final RoundingInterval x,
                                                final RoundingInterval y) {
    final RoundingInterval xx = x.square();
    final RoundingInterval yy = y.square();
    return new RoundingInterval(
      sum(xx.min,yy.min).min,
      sum(xx.max,yy.max).max); }

  //--------------------------------------------------------------

  public static final RoundingInterval
  crossProduct (final RoundingInterval x0,
                final RoundingInterval y0,
                final RoundingInterval x1,
                final RoundingInterval y1) {
    final RoundingInterval x0y1 = x0.multiply(y1);
    final RoundingInterval x1y0 = x1.multiply(y0);
    return new RoundingInterval(
      dif(x0y1.min,x1y0.max).min,
      dif(x0y1.max,x1y0.min).max); }

  //--------------------------------------------------------------

  public static final RoundingInterval
  dot (final RoundingInterval x0,
       final RoundingInterval y0,
       final RoundingInterval z0,
       final RoundingInterval x1,
       final RoundingInterval y1,
       final RoundingInterval z1) {
    final RoundingInterval x01 = x0.multiply(x1);
    final RoundingInterval y01 = y0.multiply(y1);
    final RoundingInterval z01 = z0.multiply(z1);
    return new RoundingInterval(
      sum(x01.min,y01.min,z01.min).min,
      sum(x01.max,y01.max,z01.max).max); }

  //--------------------------------------------------------------
  // Number methods
  //--------------------------------------------------------------
  /** Return midpoint as approximation. */

  //  @Override
  public final double doubleValue () { return (min+max)/2; }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------
  // TODO: OK to use default record hashcode?

  /** Implement to handle NaN. */
  public final boolean equals (final RoundingInterval di) {
    if (isNaN()) { return di.isNaN(); }
    return (min==di.min) && (max==di.max); }

  @Override
  public final boolean equals (final Object o) {
    if (this==o) { return true; }
    if (!(o instanceof RoundingInterval)) { return false; }
    return equals((RoundingInterval) o); }

  public final String toHexString () {
    return
      "[" + Double.toHexString(min) + "," +
        Double.toHexString(max) + "]";  }

  @Override
  public final String toString () { return toHexString(); }

//--------------------------------------------------------------
} // end class
//--------------------------------------------------------------
