package mop.java.numbers;

//----------------------------------------------------------------------

/** A <code>double</code> interval, closed at both ends.
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
 * <br>
 * TODO: any advantage to switching to half-open intervals?
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-07
 */

public record DoubleInterval (double min, double max)
  implements Ringlike<DoubleInterval> {

  //--------------------------------------------------------------

  public final boolean containsZero () {
    return (min()<=0.0) && (0.0<=max()); }

  public final boolean contains (final double z) {
    return (min()<=z) && (z<=max()); }

  public final boolean contains (final DoubleInterval interval) {
    return (min()<=interval.min()) && (interval.max()<=max()); }

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
    // assuming Math.abs() is exact, just flips sign bit, so no rounding
    if (isNaN()) { return NaN; }
    final double z0 = Math.abs(min());
    final double z1 = Math.abs(max());
    if (containsZero()) {
      return new DoubleInterval(0.0,Math.max(z0,z1)); }
    if (z0<=z1) { return new DoubleInterval(z0,z1); }
    return new DoubleInterval(z1,z0); }

  //--------------------------------------------------------------
  /** Return the smallest double interval covering the exact value
   * of <code>z0+z1</code>,
   */

  public static final DoubleInterval sum (final double z0,
                                          final double z1) {
    if (Double.isNaN(z0) || Double.isNaN(z1)) { return NaN; }
    final Hilo hilo = Hilo.sum(z0,z1);
//    assert hilo.hi() + hilo.lo() == hilo.hi() :
//      "\nz0= " + Double.toHexString(z0) +
//        "\nz1= " + Double.toHexString(z1) +
//        "\nhi= " + Double.toHexString(hilo.hi()) +
//        "\nz1= " + Double.toHexString(hilo.lo());
    return cover(hilo); }

  public static final DoubleInterval sum (final DoubleInterval i,
                                          final double z) {
    return cover(sum(i.min,z),sum(i.max,z)); }

  public static final DoubleInterval sum (final double z0,
                                          final double z1,
                                          final double z2) {
    return sum(sum(z0,z1),z2); }

  //--------------------------------------------------------------
  /** Return the smallest double interval covering the exact value
   * of <code>z0-z1</code>,
   */

  public static final DoubleInterval dif (final double z0,
                                          final double z1) {
    // Assuming subtraction is exact.
    return sum(z0,-z1); }

  //--------------------------------------------------------------
  /** Return the smallest double interval covering the exact value
   * of <code>z0*z1</code>,
   */

  public static final DoubleInterval product (final double z0,
                                              final double z1) {
    return cover(Hilo.product(z0,z1)); }

  //--------------------------------------------------------------
  /** Return the smallest double interval covering the exact value
   * of <code>z0*z1</code>,
   */

  public static final DoubleInterval square (final double z) {
    return cover(Hilo.square(z)); }

  //--------------------------------------------------------------
  // TODO: do we need Hilo.sum()? expand intervals for
  //  rounding in adds?

  @Override
  public final DoubleInterval add (final DoubleInterval q) {
    if (isNaN() || q.isNaN()) { return NaN; }
    return cover(sum(min,q.min),sum(max,q.max)); }

  //--------------------------------------------------------------
  // TODO: do we need Hilo.sum()? expand intervals for
  //  rounding in subtracts?

  @Override
  public final DoubleInterval subtract (final DoubleInterval q) {
    if (isNaN() || q.isNaN()) { return NaN; }
    if (isNaN() || q.isNaN()) { return NaN; }
    return cover(dif(min,q.max),dif(max,q.min)); }

  //--------------------------------------------------------------
  // TODO: do we need Hilo.product()? expand intervals for
  //  rounding in multiplies?

  @Override
  public final DoubleInterval multiply (final DoubleInterval q) {
    if (isNaN()) { return NaN; }
    if (q.isNaN()) { return NaN; }
    // TODO: optimize
    final DoubleInterval z00 = product(min,q.min);
    final DoubleInterval z01 = product(min,q.max);
    final DoubleInterval z10 = product(max,q.min);
    final DoubleInterval z11 = product(max,q.max);
    return cover(cover(z00,z01),cover(z10,z11));  }

  //--------------------------------------------------------------
  // TODO: do we need Hilo.square()? expand intervals for
  //  rounding in multiplies?

  @Override
  public final DoubleInterval
  square () {
    if (isNaN()) { return NaN; }
    return cover(
      square(min),
      square(max)); }

  //--------------------------------------------------------------
  // geometry
  //--------------------------------------------------------------
  // calling new instances from square() vs inlining
  // makes no difference in benchmark


  public static final DoubleInterval l2norm2 (final DoubleInterval x,
                                              final DoubleInterval y) {
    final DoubleInterval xx = x.square();
    final DoubleInterval yy = y.square();
    return cover(
      sum(xx.min,yy.min),
      sum(xx.max,yy.max)); }

  //--------------------------------------------------------------

  public static final DoubleInterval
  crossProduct (final DoubleInterval x0,
                final DoubleInterval y0,
                final DoubleInterval x1,
                final DoubleInterval y1) {
    final DoubleInterval x0y1 = x0.multiply(y1);
    final DoubleInterval x1y0 = x1.multiply(y0);
    return cover(dif(x0y1.min,x1y0.max),
                 dif(x0y1.max,x1y0.min)); }

  //--------------------------------------------------------------

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
    return cover(
      sum(x01.min,y01.min,z01.min),
      sum(x01.max,y01.max,z01.max)); }

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

//  public DoubleInterval {
//    System.out.println(
//      "[" +
//        Double.toHexString(min) + ", " +
//        Double.toHexString(max) + "]");
//  }
//  public static final DoubleInterval valueOf (final double z)  {
//    return new DoubleInterval(Math.nextDown(z),Math.nextUp(z)); }

  /** Return the smallest <code>double</code> interval that covers
   * both <code>i0</code> and <code>i1</code>
   */

  private static final DoubleInterval cover (final DoubleInterval i0,
                                             final DoubleInterval i1) {
    return new DoubleInterval(Math.min(i0.min,i1.min),
                              Math.max(i0.max,i1.max)); }

  /** Return the smallest <code>double</code> interval that covers
   * the exact value of <code>hi+lo</code>.
   * Expects that, in <code>double</code> arithmetic,
   * <code>hi+lo==hi</code>
   */

  private static final DoubleInterval cover (final double hi,
                                             final double lo) {
    assert hi == hi+lo :
      "\nhi= " + Double.toHexString(hi) +
        "\nlo= "  + Double.toHexString(lo) +
        "\n";
    // no rounding
    if (0.0==lo) { return new DoubleInterval(hi,hi); }
    // rounded down
    if (0.0<lo) {
      return new DoubleInterval(hi,Math.nextUp(hi)); }
    // rounded up
    return new DoubleInterval(Math.nextDown(hi), hi); }

  /** Return the smallest <code>double</code> interval that covers
   * the exact value of <code>hi+lo</code>.
   * Assumes that, in <code>double</code> arithmetic,
   * <code>hi+lo==hi</code>
   */

  private static final DoubleInterval cover (final Hilo hilo) {
    return cover(hilo.hi(),hilo.lo()); }

  public static final DoubleInterval plusOrMinus (final double z,
                                                  final double e)  {
    final double ae = Math.abs(e);
    return new DoubleInterval(z-ae,z+ae); }

//--------------------------------------------------------------
} // end class
//--------------------------------------------------------------
