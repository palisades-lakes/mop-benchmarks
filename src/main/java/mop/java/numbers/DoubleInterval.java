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
 * @version 2026-08-17
 */

public record DoubleInterval (double min, double max)
  implements Ringlike<DoubleInterval> {

  //--------------------------------------------------------------

  public final boolean containsZero () {
    return (0.0>=min()) && (0.0<=max()); }

  //--------------------------------------------------------------
  // Ringlike
  //--------------------------------------------------------------
  // TODO: infinities?

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
    if (isNaN()) { return NaN; }
    return new DoubleInterval(-max(),-min()); }

  @Override
  public final DoubleInterval abs () {
    if (isNaN()) { return NaN; }
    final double z0 = Math.abs(min());
    final double z1 = Math.abs(max());
    if (z0<=z1) { return new DoubleInterval(z0,z1); }
    return new DoubleInterval(z1,z0); }

  //--------------------------------------------------------------

  @Override
  public final DoubleInterval add (final DoubleInterval q) {
    if (isNaN() || q.isNaN()) { return NaN; }
    return new DoubleInterval(min()+q.min(),max()+q.max()); }

  //--------------------------------------------------------------

  @Override
  public final DoubleInterval subtract (final DoubleInterval q) {
    if (isNaN() || q.isNaN()) { return NaN; }
    return new DoubleInterval(min()-q.max(),max()-q.min()); }

  //--------------------------------------------------------------
//  /** Return the double error interval value of <code>z0+z1</code>,
//   * without intermediate <code>BigFloat</code> instances.
//   */
//
//  public static final DoubleInterval sum (final double z0,
//                                          final double z1) {
//    return new DoubleInterval(Math.nextDown(z0)+Math.nextDown(z1),
//                              Math.nextUp(z0)+Math.nextUp(z1)); }

  /** Return the double error interval value of <code>z0-z1</code>,
   * without intermediate <code>BigFloat</code> instances.
   */

  public static final DoubleInterval dif (final double z0,
                                          final double z1) {
    final double mz1 = -z1;
    return new DoubleInterval(Math.nextDown(z0)+Math.nextDown(mz1),
                              Math.nextUp(z0)+Math.nextUp(mz1)); }

  //--------------------------------------------------------------

  @Override
  public final DoubleInterval multiply (final DoubleInterval q) {
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
    return new DoubleInterval(zmin,zmax);  }

  //--------------------------------------------------------------

  @Override
  public final DoubleInterval
  square () {
    if (isNaN()) { return NaN; }
    final double z0 = min()*min();
    final double z1 = max()*max();
    if (containsZero()) {
      if (z0<=z1) { return new DoubleInterval(0.0,z1); }
      return new DoubleInterval(0.0,z0); }
    if (z0<=z1) { return new DoubleInterval(z0,z1); }
    return new DoubleInterval(z1,z0); }

  //--------------------------------------------------------------
  // geometry
  //--------------------------------------------------------------
// calling new instances from square() vs inlining
// makes no difference in benchmark

//  public static final DoubleInterval l2norm2 (final DoubleInterval x,
//                                              final DoubleInterval y) {
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
//    return new DoubleInterval(xx0+yy0,xx1+yy1); }

  public static final DoubleInterval l2norm2 (final DoubleInterval x,
                                              final DoubleInterval y) {
    if (x.isNaN() || y.isNaN()) { return NaN; }

    final DoubleInterval xx = x.square();
    final DoubleInterval yy = y.square();

    return new DoubleInterval(xx.min()+yy.min(),xx.max()+yy.max()); }

  //--------------------------------------------------------------

  public static final DoubleInterval
  crossProduct (final DoubleInterval x0,
                final DoubleInterval y0,
                final DoubleInterval x1,
                final DoubleInterval y1) {

    final DoubleInterval x0y1 = x0.multiply(y1);
    final DoubleInterval x1y0 = x1.multiply(y0);
    return new DoubleInterval(
      x0y1.min()-x1y0.max(),
      x0y1.max()-x1y0.min()); }

  //--------------------------------------------------------------

  // 157/147 relative to version with multiply
//  public static final DoubleInterval
//  dot (final DoubleInterval x0,
//       final DoubleInterval y0,
//       final DoubleInterval z0,
//       final DoubleInterval x1,
//       final DoubleInterval y1,
//       final DoubleInterval z1) {
//
//    final double xx00 = x0.min() * x1.min();
//    final double xx01 = x0.min() * x1.max();
//    final double xx10 = x0.max() * x1.min();
//    final double xx11 = x0.max() * x1.max();
//    double xxmin, xxmax;
//    if (xx00<=xx01) { xxmin = xx00; xxmax = xx01; }
//    else { xxmin = xx01; xxmax = xx00; }
//    if (xx10<xxmin) { xxmin = xx10; }
//    else if (xxmax<xx10) { xxmax = xx10; }
//    if (xx11<xxmin) { xxmin = xx11; }
//    else if (xxmax<xx11) { xxmax = xx11; }
//
//    final double yy00 = y0.min() * y1.min();
//    final double yy01 = y0.min() * y1.max();
//    final double yy10 = y0.max() * y1.min();
//    final double yy11 = y0.max() * y1.max();
//    double yymin, yymax;
//    if (yy00<=yy01) { yymin = yy00; yymax = yy01; }
//    else { yymin = yy01; yymax = yy00; }
//    if (yy10<yymin) { yymin = yy10; }
//    else if (yymax<yy10) { yymax = yy10; }
//    if (yy11<yymin) { yymin = yy11; }
//    else if (yymax<yy11) { yymax = yy11; }
//
//    final double zz00 = z0.min() * z1.min();
//    final double zz01 = z0.min() * z1.max();
//    final double zz10 = z0.max() * z1.min();
//    final double zz11 = z0.max() * z1.max();
//    double zzmin, zzmax;
//    if (zz00<=zz01) { zzmin = zz00; zzmax = zz01; }
//    else { zzmin = zz01; zzmax = zz00; }
//    if (zz10<zzmin) { zzmin = zz10; }
//    else if (zzmax<zz10) { zzmax = zz10; }
//    if (zz11<zzmin) { zzmin = zz11; }
//    else if (zzmax<zz11) { zzmax = zz11; }
//
//    return new DoubleInterval(xxmin+yymin+zzmin, xxmax+yymax+zzmax); }

  public static final DoubleInterval
  dot (final DoubleInterval x0,
       final DoubleInterval y0,
       final DoubleInterval z0,
       final DoubleInterval x1,
       final DoubleInterval y1,
       final DoubleInterval z1) {
    final DoubleInterval x01 = x0.multiply(x1);
    final DoubleInterval y01 = y0.multiply(y1);
    final DoubleInterval z01 = z0.multiply(z1);
    return new DoubleInterval(
      x01.min()+y01.min()+z01.min(),
      x01.max()+y01.max()+z01.max()); }

  //--------------------------------------------------------------
  // Number methods
  //--------------------------------------------------------------

  /** Return midpoint as approximation. */
  @Override
  public final double doubleValue () { return (min()+max())/2; }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------
  // TODO: OK to use default record hashcode?

  /** Implement to handle NaN. */
  public final boolean equals (final DoubleInterval di) {
    if (isNaN()) { return di.isNaN(); }
    return (min()==di.min()) && (max()==di.max()); }

  @Override
  public final boolean equals (final Object o) {
    if (this==o) { return true; }
    if (!(o instanceof DoubleInterval)) { return false; }
    return equals((DoubleInterval) o); }

  public final String toHexString () {
    return
      "[" + Double.toHexString(min()) + "," +
        Double.toHexString(max()) + "]";  }

  @Override
  public final String toString () { return toHexString(); }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  public static final DoubleInterval valueOf (final double z)  {
    return new DoubleInterval(Math.nextDown(z),Math.nextUp(z)); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
