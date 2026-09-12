package mop.java.numbers;

//----------------------------------------------------------------------
/** A <code>double</code> interval.
 * <br>
 * See <a href="https://en.wikipedia.org/wiki/Interval_arithmetic">
 *   Interval Arithmetic</a>
 * <br>
 * Note the need to be careful when the same interval is both arguments
 * to an operation (eg <code>square</code> and <code>multiply</code>).
 * The set of values that result from
 * { z*z : z in [min,max]} is different from
 * { z0*z1 : z0,z1 in [min,max]}.
 * More generally,
 * { f(z,z) : z in [min,max] } == { f(z0,z1) : z0,z1 in [min,max] }
 * only if f is monotone in both arguments over [min,max].
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-12
 */

public record RelaxedInterval (double min, double max)
  implements DoubleInterval {

  //--------------------------------------------------------------
  // Ringlike
  //--------------------------------------------------------------
  // TODO: infinities?

  public static final RelaxedInterval ZERO =
    new RelaxedInterval(0.0,0.0);

  public static final RelaxedInterval ONE =
    new RelaxedInterval(1.0,1.0);

  public static final RelaxedInterval NaN =
    new RelaxedInterval(Double.NaN,Double.NaN);

  //--------------------------------------------------------------

  @Override
  public final RelaxedInterval negate () {
    if (isNaN()) { return NaN; }
    // TODO: no rounding, no need for nextUp,nextDown?
    return new RelaxedInterval(-max,-min); }

  @Override
  public final RelaxedInterval abs () {
    if (isNaN()) { return NaN; }
    final double z0 = Math.abs(min);
    final double z1 = Math.abs(max);
    // TODO: no rounding, no need for nextUp,nextDown?
    if (containsZero()) {
      return new RelaxedInterval(0.0,Math.max(z0,z1)); }
    if (z0<=z1) { return new RelaxedInterval(z0,z1); }
    return new RelaxedInterval(z1,z0); }

  //--------------------------------------------------------------

  public final RelaxedInterval add (final RelaxedInterval q) {
    if (isNaN() || q.isNaN()) { return NaN; }
    return new RelaxedInterval(
      Math.nextDown(min+q.min),
      Math.nextUp(max+q.max)); }

  //--------------------------------------------------------------

  public final RelaxedInterval subtract (final RelaxedInterval q) {
    if (isNaN() || q.isNaN()) { return NaN; }
    return new RelaxedInterval(
      Math.nextDown(min-q.max),
      Math.nextUp(max-q.min)); }

  //--------------------------------------------------------------

  public final RelaxedInterval multiply (final RelaxedInterval q) {
    if (isNaN() || q.isNaN()) { return NaN; }
    final double z00 = min*q.min;
    final double z01 = min*q.max;
    final double z10 = max*q.min;
    final double z11 = max*q.max;
    double zmin, zmax;
    if (z00<=z01) { zmin = z00; zmax = z01; }
    else { zmin = z01; zmax = z00; }
    if (z10<zmin) { zmin = z10; }
    else if (z10>zmax) { zmax = z10; }
    if (z11<zmin) { zmin = z11; }
    else if (z11>zmax) { zmax = z11; }
    return new RelaxedInterval(Math.nextDown(zmin),
                               Math.nextUp(zmax));  }

  //--------------------------------------------------------------

  @Override
  public final RelaxedInterval
  square () {
    if (isNaN()) { return NaN; }
    final double z0 = min*min;
    final double z1 = max*max;
    if (containsZero()) {
      if (z0<=z1) { return new RelaxedInterval(0.0,Math.nextUp(z1)); }
      return new RelaxedInterval(0.0,Math.nextUp(z0)); }
    if (z0<=z1) { return new RelaxedInterval(Math.nextDown(z0),
                                             Math.nextUp(z1)); }
    return new RelaxedInterval(Math.nextDown(z1),
                               Math.nextUp(z0)); }

  //--------------------------------------------------------------
  // geometry
  //--------------------------------------------------------------

  public static final RelaxedInterval l2norm2 (final RelaxedInterval x,
                                              final RelaxedInterval y) {
    final RelaxedInterval xx = x.square();
    final RelaxedInterval yy = y.square();
    return xx.add(yy); }

  //--------------------------------------------------------------

  public static final RelaxedInterval
  crossProduct (final RelaxedInterval x0,
                final RelaxedInterval y0,
                final RelaxedInterval x1,
                final RelaxedInterval y1) {

    final RelaxedInterval x0y1 = x0.multiply(y1);
    final RelaxedInterval x1y0 = x1.multiply(y0);
    return x0y1.subtract(x1y0); }

//--------------------------------------------------------------

  public static final RelaxedInterval
  dot (final RelaxedInterval x0,
       final RelaxedInterval y0,
       final RelaxedInterval z0,
       final RelaxedInterval x1,
       final RelaxedInterval y1,
       final RelaxedInterval z1) {
    final RelaxedInterval x01 = x0.multiply(x1);
    final RelaxedInterval y01 = y0.multiply(y1);
    final RelaxedInterval z01 = z0.multiply(z1);
    return x01.add(y01).add(z01); }

//--------------------------------------------------------------
// Number methods
//--------------------------------------------------------------

  /** Return midpoint as approximation. */
  @Override
  public final double doubleValue () { return (min+max)/2; }

//--------------------------------------------------------------
// Object methods
//--------------------------------------------------------------
// TODO: OK to use default record hashcode?

  /** Implement to handle NaN. */
  public final boolean equals (final RelaxedInterval di) {
    if (isNaN()) { return di.isNaN(); }
    return (min==di.min) && (max==di.max); }

  @Override
  public final boolean equals (final Object o) {
    if (this==o) { return true; }
    if (!(o instanceof RelaxedInterval)) { return false; }
    return equals((RelaxedInterval) o); }

  @Override
  public final String toString () { return toHexString(); }

//--------------------------------------------------------------
// construction
//--------------------------------------------------------------

  public static final RelaxedInterval dif (final double z0,
                                          final double z1) {
    final double z01 = z0 - z1;
    return new RelaxedInterval(Math.nextDown(z01),
                               Math.nextUp(z01)); }

//--------------------------------------------------------------
} // end class
//--------------------------------------------------------------
