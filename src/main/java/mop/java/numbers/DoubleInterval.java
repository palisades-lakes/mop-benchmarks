package mop.java.numbers;

import mop.java.Exceptions;

//----------------------------------------------------------------------
// TODO: implement as Record?

/** A <code>double</code> interval.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-14
 */

public final class DoubleInterval implements Ringlike<DoubleInterval> {

  //--------------------------------------------------------------
  // instance fields and methods
  //--------------------------------------------------------------

  private final double _min;
  public final double min () { return _min; }

  private final double _max;
  public final double max () { return _max; }

  //--------------------------------------------------------------
  // Ringlike
  //--------------------------------------------------------------
  // TODO: NaN, infinities

  public static final DoubleInterval ZERO =
    new DoubleInterval(0.0,0.0);

  public static final DoubleInterval ONE =
    new DoubleInterval(1.0,1.0);

  public static final DoubleInterval NaN =
    new DoubleInterval(Double.NaN,Double.NaN);

  @Override
  public final boolean isZero () {
    return 0.0==min() && 0.0==max(); }

  @Override
  public final boolean isOne () {
    return 1.0==min() && 1.0==max(); }

  public final boolean isNaN () {
    return Double.isNaN(min()) && Double.isNaN(max()); }

  //--------------------------------------------------------------

  @Override
  public final DoubleInterval negate () {
    if (isZero()) { return this; }
    return safe(-max(),-min()); }

  @Override
  public final DoubleInterval abs () {
    return safe(Math.abs(min()),Math.abs(max())); }

  //--------------------------------------------------------------

  @Override
  public final DoubleInterval add (final DoubleInterval q) {
    if (isNaN()) { return NaN; }
    if (isZero()) { return q; }
    if (q.isNaN()) { return NaN; }
    if (q.isZero()) { return this; }
    return safe(min()+q.min(),max()+q.max()); }

  //--------------------------------------------------------------

  public final DoubleInterval add (final double z) {
    if (0.0==z) { return this; }
    return add(valueOf(z)); }

  //--------------------------------------------------------------

  @Override
  public final DoubleInterval subtract (final DoubleInterval q) {
    return add(q.negate()); }

  //--------------------------------------------------------------
  /** Return the double error interval value of <code>z0+z1</code>,
   * without intermediate <code>BigFloat</code> instances.
   */

  public static final DoubleInterval sum (final double z0,
                                          final double z1) {
    return valueOf(z0).add(valueOf(z1)); }

//  /** Return the double error interval value of <code>z0-z1</code>,
//   * without intermediate <code>BigFloat</code> instances.
//   */
//
//  public static final DoubleInterval dif (final double z0,
//                                          final double z1) {
//    // TODO: expand this? probably not worth while
//    return sum(z0,-z1); }

  //--------------------------------------------------------------

  @Override
  public final DoubleInterval multiply (final DoubleInterval q) {
    if (isNaN()) { return NaN; }
    if (isZero()) { return ZERO; }
    if (isOne()) { return q; }
    if (q.isNaN()) { return NaN; }
    if (q.isZero()) { return ZERO; }
    if (q.isOne()) { return this; }
    return safe(min()*q.min(),
                min()*q.max(),
                max()*q.min(),
                max()*q.max());  }

  public final DoubleInterval
  multiply (final double z) { return safe(z*min(),z*max()); }

  //--------------------------------------------------------------

  @Override
  public final DoubleInterval
  square () { return multiply(this); }

  //--------------------------------------------------------------
  // geometry
  //--------------------------------------------------------------
  /** Compute squared l2norm without intermediate instances. */

  public static final DoubleInterval l2norm2 (final DoubleInterval x,
                                              final DoubleInterval y) {
    return x.square().add(y.square()); }

  //--------------------------------------------------------------

  public static final DoubleInterval
  crossProduct (final DoubleInterval x0,
                final DoubleInterval y0,
                final DoubleInterval x1,
                final DoubleInterval y1) {
    return x0.multiply(y1).subtract(x1.multiply(y0)); }

  //--------------------------------------------------------------

  public static final DoubleInterval
  dot (final DoubleInterval x0,
       final DoubleInterval y0,
       final DoubleInterval z0,
       final DoubleInterval x1,
       final DoubleInterval y1,
       final DoubleInterval z1) {

    return
      x0.multiply(x1)
        .add(y0.multiply(y1))
        .add(z0.multiply(z1)); }

  //--------------------------------------------------------------
  // Number methods
  //--------------------------------------------------------------
  /** Unsupported.
   * <br>
   * TODO: should it really truncate or round instead? Or
   *  should there be more explicit round, floor, ceil, etc.?
   */
  @Override
  public final int intValue () {
    throw Exceptions.unsupportedOperation(
      this,"intValue"); }

  /** Unsupported.
   * <br>
   * TODO: should it really truncate or round instead? Or
   *  should there be more explicit round, floor, ceil, etc.?
   */
  @Override
  public final long longValue () {
    throw Exceptions.unsupportedOperation(
      this,"longValue"); }


  /** Round midpoint to nearest <code>float</code>.
   */

  @Override
  public final float floatValue () {
    return (float) doubleValue(); }

  /** Return midpoint as approximation. */

  @Override
  public final double doubleValue () { return (min()+max())/2; }

  //--------------------------------------------------------------
  // Comparable methods
  //--------------------------------------------------------------

  /** Unsupported, no simple answer for overlapping intervals. */
  @Override
  public final int compareTo (final DoubleInterval q) {
    throw Exceptions.unsupportedOperation(
      this,"compareTo", q); }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------

  public final boolean equals (final DoubleInterval q) {
    if (this==q) { return true; }
    if (Double.isNaN(min())) {
      assert Double.isNaN(max());
      return Double.isNaN(q.min()) && Double.isNaN(q.max()); }
    return (min()==q.min()) && (max() == q.max()); }

  @Override
  public final boolean equals (final Object o) {
    if (!(o instanceof DoubleInterval)) { return false; }
    return equals((DoubleInterval) o); }

  @Override
  public final int hashCode () {
    int h = 17;
    h = (31*h) + Double.hashCode(min());
    h = (31*h) + Double.hashCode(max());
    return h; }

  public final String toHexString () {
    return
      "[" + Double.toHexString(min()) + "," +
        Double.toHexString(max()) + "]";  }

  @Override
  public final String toString () { return toHexString(); }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private DoubleInterval (final double min,
                          final double max) {
    // reverse test for NaN
    assert ! (min>max) :
      "[" + Double.toHexString(min)+","+Double.toHexString(max)+"]";
    assert !Double.isNaN(min) || Double.isNaN(max);
    _min = min; _max = max; }

  public static final DoubleInterval unsafe (final double d0,
                                             final double d1) {
    return new DoubleInterval(d0,d1);  }

  public static final DoubleInterval safe (final double d0,
                                           final double d1) {
    if (Double.isNaN(d0) ||  Double.isNaN(d1)) { return NaN; }
    return unsafe(Double.min(d0,d1),Double.max(d0,d1));  }

  public static final DoubleInterval valueOf (final double z)  {
    return unsafe(Math.nextDown(z),Math.nextUp(z)); }

  // TODO: optimize
  private static final double min (final double d0,
                                   final double d1,
                                   final double d2,
                                   final double d3) {
    return Double.min(d0,Double.min(d1,Double.min(d2,d3))); }

  private static final double max (final double d0,
                                   final double d1,
                                   final double d2,
                                   final double d3) {
    return Double.max(d0,Double.max(d1,Double.max(d2,d3))); }

  public static final DoubleInterval safe (final double d0,
                                           final double d1,
                                           final double d2,
                                           final double d3) {
    return unsafe(min(d0,d1,d2,d3),max(d0,d1,d2,d3));  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
