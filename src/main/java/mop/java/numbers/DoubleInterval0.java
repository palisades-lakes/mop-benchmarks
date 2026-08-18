package mop.java.numbers;

import mop.java.Exceptions;

//----------------------------------------------------------------------
// TODO: implement as Record?

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
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-17
 */

public final class DoubleInterval0 implements Ringlike<DoubleInterval0> {

  //--------------------------------------------------------------
  // instance fields and methods
  //--------------------------------------------------------------

  private final double _min;
  public final double min () { return _min; }

  private final double _max;
  public final double max () { return _max; }

  //--------------------------------------------------------------

  public final boolean containsZero () {
    return (0.0>=min()) && (0.0<=max()); }

//  private final boolean isNonNegative () {
//    return 0.0<=min(); }

  //--------------------------------------------------------------
  // Ringlike
  //--------------------------------------------------------------
  // TODO: NaN, infinities

  public static final DoubleInterval0 ZERO =
    new DoubleInterval0(0.0, 0.0);

  public static final DoubleInterval0 ONE =
    new DoubleInterval0(1.0, 1.0);

  public static final DoubleInterval0 NaN =
    new DoubleInterval0(Double.NaN, Double.NaN);

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
  public final DoubleInterval0 negate () {
    return new DoubleInterval0(-max(), -min()); }

  @Override
  public final DoubleInterval0 abs () {
    final double z0 = Math.abs(min());
    final double z1 = Math.abs(max());
    if (z0<=z1) { return new DoubleInterval0(z0, z1); }
    return new DoubleInterval0(z1, z0); }

  //--------------------------------------------------------------

  @Override
  public final DoubleInterval0 add (final DoubleInterval0 q) {
    if (isNaN()) { return NaN; }
    if (q.isNaN()) { return NaN; }
    // TODO: worth more special cases?
    //if (isZero()) { return q; }
    //if (q.isZero()) { return this; }
    return new DoubleInterval0(min()+q.min(), max()+q.max()); }

  //--------------------------------------------------------------

  public final DoubleInterval0 add (final double z) {
    if (0.0==z) { return this; }
    return new DoubleInterval0(min()+Math.nextDown(z),
                               max()+Math.nextUp(z)); }
  //--------------------------------------------------------------

  @Override
  public final DoubleInterval0 subtract (final DoubleInterval0 q) {
    if (isNaN()) { return NaN; }
    if (q.isNaN()) { return NaN; }
    // TODO: worth more special cases?
    //if (isZero()) { return q; }
    //if (q.isZero()) { return this; }
    return new DoubleInterval0(min()-q.max(), max()-q.min()); }

  //--------------------------------------------------------------
  /** Return the double error interval value of <code>z0+z1</code>,
   * without intermediate <code>BigFloat</code> instances.
   */

  public static final DoubleInterval0 sum (final double z0,
                                           final double z1) {
    return new DoubleInterval0(Math.nextDown(z0)+Math.nextDown(z1),
                               Math.nextUp(z0)+Math.nextUp(z1)); }

  /** Return the double error interval value of <code>z0-z1</code>,
   * without intermediate <code>BigFloat</code> instances.
   */

  public static final DoubleInterval0 dif (final double z0,
                                           final double z1) {
    final double mz1 = -z1;
    return new DoubleInterval0(Math.nextDown(z0)+Math.nextDown(mz1),
                               Math.nextUp(z0)+Math.nextUp(mz1)); }

  //--------------------------------------------------------------

  @Override
  public final DoubleInterval0 multiply (final DoubleInterval0 q) {
    if (isNaN()) { return NaN; }
    if (q.isNaN()) { return NaN; }
    final double z00 = min()*q.min();
    final double z01 = min()*q.max();
    final double z10 = max()*q.min();
    final double z11 = max()*q.max();
    double zmin, zmax;
    if (z00<=z01) { zmin = z00; zmax = z01; }
    else { zmin = z01; zmax = z00; }
    if (z10<zmin) { zmin = z10; }
    else if (zmax<z10) { zmax = z10; }
    if (z11<zmin) { zmin = z11; }
    else if (zmax<z11) { zmax = z11; }
    return new DoubleInterval0(zmin, zmax);  }

  public final DoubleInterval0
  multiply (final double z) {
    return multiply(valueOf(z)); }

  //--------------------------------------------------------------

  @Override
  public final DoubleInterval0
  square () {
    if (isNaN()) { return NaN; }
    final double z0 = min()*min();
    final double z1 = max()*max();
    if (containsZero()) {
      if (z0<=z1) { return new DoubleInterval0(0.0, z1); }
      return new DoubleInterval0(0.0, z0); }
    if (z0<=z1) { return new DoubleInterval0(z0, z1); }
    return new DoubleInterval0(z1, z0); }

  //--------------------------------------------------------------
  // geometry
  //--------------------------------------------------------------

//  public static final DoubleInterval0 l2norm2 (final DoubleInterval0 x,
//                                               final DoubleInterval0 y) {
//    if (x.isNaN() || y.isNaN()) { return NaN; }
//
//    final double xxmin = x.min()*x.min();
//    final double xxmax = x.max()*x.max();
//    final double xx0, xx1;
//    if (x.containsZero()) {
//      xx0 = 0.0;
//      xx1 = Math.max(xxmin,xxmax); }
//    else if (xxmin<=xxmax) {
//      xx0 = xxmin;
//      xx1 = xxmax; }
//    else {
//      xx0 = xxmax;
//      xx1 = xxmin; }
//
//    final double yymin = y.min()*y.min();
//    final double yymax = y.max()*y.max();
//    final double yy0, yy1;
//    if (y.containsZero()) {
//      yy0 = 0.0;
//      yy1 = Math.max(yymin,yymax); }
//    else if (yymin<=yymax) {
//      yy0 = yymin;
//      yy1 = yymax; }
//    else {
//      yy0 = yymax;
//      yy1 = yymin; }
//
//    return new DoubleInterval0(xx0+yy0, xx1+yy1); }
//
public static final DoubleInterval0 l2norm2 (final DoubleInterval0 x,
                                            final DoubleInterval0 y) {
  if (x.isNaN() || y.isNaN()) { return NaN; }

  final DoubleInterval0 xx = x.square();
  final DoubleInterval0 yy = y.square();

  return new DoubleInterval0(xx.min()+yy.min(),xx.max()+yy.max()); }


  //--------------------------------------------------------------

  public static final DoubleInterval0
  crossProduct (final DoubleInterval0 x0,
                final DoubleInterval0 y0,
                final DoubleInterval0 x1,
                final DoubleInterval0 y1) {

    final DoubleInterval0 x0y1 = x0.multiply(y1);
    final DoubleInterval0 x1y0 = x1.multiply(y0);
    return new DoubleInterval0(
      x0y1.min()-x1y0.max(),
      x0y1.max()-x1y0.min()); }

  //--------------------------------------------------------------

  public static final DoubleInterval0
  dot (final DoubleInterval0 x0,
       final DoubleInterval0 y0,
       final DoubleInterval0 z0,
       final DoubleInterval0 x1,
       final DoubleInterval0 y1,
       final DoubleInterval0 z1) {
    final DoubleInterval0 x01 = x0.multiply(x1);
    final DoubleInterval0 y01 = y0.multiply(y1);
    final DoubleInterval0 z01 = z0.multiply(z1);
    return new DoubleInterval0(
      x01.min()+y01.min()+z01.min(),
      x01.max()+y01.max()+z01.max()); }

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
  public final int compareTo (final DoubleInterval0 q) {
    throw Exceptions.unsupportedOperation(
      this,"compareTo", q); }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------

  public final boolean equals (final DoubleInterval0 q) {
    if (this==q) { return true; }
    if (Double.isNaN(min())) {
      assert Double.isNaN(max());
      return Double.isNaN(q.min()) && Double.isNaN(q.max()); }
    return (min()==q.min()) && (max() == q.max()); }

  @Override
  public final boolean equals (final Object o) {
    if (!(o instanceof DoubleInterval0)) { return false; }
    return equals((DoubleInterval0) o); }

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

  private DoubleInterval0 (final double min,
                           final double max) {
    // reverse test for NaN
//    assert ! (min>max) :
//      "[" + Double.toHexString(min)+","+Double.toHexString(max)+"]";
//    assert !Double.isNaN(min) || Double.isNaN(max);
    _min = min; _max = max; }

  public static final DoubleInterval0 unsafe (final double d0,
                                              final double d1) {
    return new DoubleInterval0(d0, d1);  }

  public static final DoubleInterval0 safe (final double d0,
                                            final double d1) {
    if (Double.isNaN(d0) ||  Double.isNaN(d1)) { return NaN; }
    return unsafe(Double.min(d0,d1),Double.max(d0,d1));  }

  public static final DoubleInterval0 valueOf (final double z)  {
    return unsafe(Math.nextDown(z),Math.nextUp(z)); }

//  // TODO: optimize
//  private static final double min (final double d0,
//                                   final double d1,
//                                   final double d2,
//                                   final double d3) {
//    return Double.min(d0,Double.min(d1,Double.min(d2,d3))); }
//
//  private static final double max (final double d0,
//                                   final double d1,
//                                   final double d2,
//                                   final double d3) {
//    return Double.max(d0,Double.max(d1,Double.max(d2,d3))); }
//
//  public static final DoubleInterval0 safe (final double d0,
//                                            final double d1,
//                                            final double d2,
//                                            final double d3) {
//    return unsafe(min(d0,d1,d2,d3),max(d0,d1,d2,d3));  }
//
  //--------------------------------------------------------------
}
//--------------------------------------------------------------
