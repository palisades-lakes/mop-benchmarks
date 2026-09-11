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
 * @version 2026-09-11
 */

public record RoundingInterval(double min, double max)
//  implements Ringlike<RoundingInterval>
{

  //--------------------------------------------------------------

  public final boolean containsZero () {
    return (min<=0.0) && (0.0<=max); }

  public final boolean contains (final double z) {
    return (min<=z) && (z<=max); }

  public final boolean contains (final RoundingInterval interval) {
    return (min<=interval.min) && (interval.max<=max); }

  public final boolean contains (final BigFloat bf) {
    return bf.opGE(min) && bf.opLE(max); }

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

//  @Override
  public final boolean isZero () {
    return 0.0==min && 0.0==max; }

//  @Override
//  public final boolean isOne () {
//    return 1.0==min && 1.0==max; }

  public final boolean isNaN () {
    return Double.isNaN(min) && Double.isNaN(max); }

  //--------------------------------------------------------------

//  @Override
  public final RoundingInterval negate () {
    if (isNaN()) { return NaN; }
    return new RoundingInterval(-max, -min); }

//  @Override
//  public final RoundingInterval abs () {
//    // assuming Math.abs() is exact, just flips sign bit, so no rounding
//    if (isNaN()) { return NaN; }
//    final double z0 = Math.abs(min);
//    final double z1 = Math.abs(max);
//    if (containsZero()) {
//      return new RoundingInterval(0.0,Math.max(z0,z1)); }
//    if (z0<=z1) { return new RoundingInterval(z0,z1); }
//    return new RoundingInterval(z1,z0); }

  //--------------------------------------------------------------
  /** Return the smallest double interval covering the exact value
   * of <code>z0+z1</code>,
   */

  private static final RoundingInterval sum (final double z0,
                                             final double z1) {
    //if (Double.isNaN(z0) || Double.isNaN(z1)) { return NaN; }
    final Hilo s = Hilo.sum(z0,z1);
//    assert hilo.hi() + hilo.lo() == hilo.hi() :
//      "\nz0= " + Double.toHexString(z0) +
//        "\nz1= " + Double.toHexString(z1) +
//        "\nhi= " + Double.toHexString(hilo.hi()) +
//        "\nz1= " + Double.toHexString(hilo.lo());
    final double hi = s.hi();
    final double lo = s.lo();
    // no rounding
    if (0.0==lo) { return new RoundingInterval(hi, hi); }
    // rounded down
    if (0.0<lo) { return new RoundingInterval(hi, Math.nextUp(hi)); }
    // rounded up
    return new RoundingInterval(Math.nextDown(hi), hi); }

  private static final RoundingInterval sum (final RoundingInterval i,
                                             final double z) {
    return cover(sum(i.min, z), sum(i.max, z)); }

  private static final RoundingInterval sum (final double z0,
                                             final double z1,
                                             final double z2) {
//    final Hilo s01 = Hilo.sum(z0,z1);
//    final Hilo h012 = Hilo.sum(s01.hi(),z2);
//    final Hilo l012 = Hilo.sum(s01.lo(),z2);
//    return sum(s01,z2); }
  return sum(sum(z0,z1),z2); }

  //--------------------------------------------------------------
  /** Return the smallest double interval covering the exact value
   * of <code>z0-z1</code>,
   */

  public static final RoundingInterval dif (final double z0,
                                            final double z1) {
    // Assuming subtraction is exact.
    return sum(z0,-z1); }

  //--------------------------------------------------------------
//  /** Return the smallest double interval covering the exact value
//   * of <code>z0*z1</code>,
//   */
//
//  public static final RoundingInterval square (final double z) {
//    return coverExactSum(Hilo.square(z)); }

  //--------------------------------------------------------------
  // TODO: do we need Hilo.sum()? expand intervals for
  //  rounding in adds?

//  @Override
  public final RoundingInterval add (final RoundingInterval q) {
    if (isNaN() || q.isNaN()) { return NaN; }
    return cover(sum(min, q.min), sum(max, q.max)); }

  //--------------------------------------------------------------
  // TODO: do we need Hilo.sum()? expand intervals for
  //  rounding in subtracts?

//  @Override
//  public final RoundingInterval subtract (final RoundingInterval q) {
//    if (isNaN() || q.isNaN()) { return NaN; }
//    if (isNaN() || q.isNaN()) { return NaN; }
//    return cover(
//      dif(min, q.max),
//      dif(max, q.min)); }

  //--------------------------------------------------------------
//  /** Return the smallest double interval covering the exact value
//   * of <code>z0*z1</code>,
//   */
//
//  private static final RoundingInterval product (final double z0,
//                                              final double z1) {
//    return coverExactSum(Hilo.product(z0, z1)); }

  //--------------------------------------------------------------
  // TODO: do we need Hilo.product()? expand intervals for
  //  rounding in multiplies?

//  @Override
  public final RoundingInterval multiply (final RoundingInterval q) {
    if (isNaN() || q.isNaN()) { return NaN; }
    final Hilo z00 = Hilo.product(min,q.min);
    final Hilo z01 = Hilo.product(min,q.max);
    final Hilo z10 = Hilo.product(max,q.min);
    final Hilo z11 = Hilo.product(max,q.max);
    Hilo zmin,zmax;
    if (z00.compareTo(z01) <= 0) { zmin = z00; zmax = z01; }
    else { zmin = z01; zmax = z00; }
    if (z10.compareTo(zmin) < 0) { zmin = z10; }
    else if (z10.compareTo(zmax) > 0) { zmax = z10; }
    if (z11.compareTo(zmin) < 0) { zmin = z11; }
    else if (z11.compareTo(zmax) > 0) { zmax = z11; }
    final double dmin =
      (zmin.lo() >= 0.0) ? zmin.hi() : Math.nextDown(zmin.hi());
    final double dmax =
      (zmax.lo() <= 0.0) ? zmax.hi() : Math.nextUp(zmax.hi());
    return new RoundingInterval(dmin, dmax);  }

  //--------------------------------------------------------------

//  @Override
  public final RoundingInterval square () {
    if (isNaN()) { return NaN; }
    final Hilo z0 = Hilo.square(min);
    final Hilo z1 = Hilo.square(max);
    if (containsZero()) {
      final Hilo zmax = (0 >= z0.compareTo(z1)) ? z1 : z0;
      final double dmax =
        (zmax.lo() <= 0.0) ? zmax.hi() : Math.nextUp(zmax.hi());
      return new RoundingInterval(0.0, dmax); }
    final Hilo zmin,zmax;
    if (z0.compareTo(z1) <= 0) { zmin = z0; zmax = z1; }
    else { zmin = z1; zmax = z0; }
    final double dmin =
      (zmin.lo() >= 0.0) ? zmin.hi() : Math.nextDown(zmin.hi());
    final double dmax =
      (zmax.lo() <= 0.0) ? zmax.hi() : Math.nextUp(zmax.hi());
    return new RoundingInterval(dmin, dmax);  }

  //--------------------------------------------------------------
  // geometry
  //--------------------------------------------------------------

  public static final RoundingInterval l2norm2 (final RoundingInterval x,
                                                final RoundingInterval y) {
    final RoundingInterval xx = x.square();
    final RoundingInterval yy = y.square();
    return cover(
      sum(xx.min,yy.min),
      sum(xx.max,yy.max)); }

  //--------------------------------------------------------------

  public static final RoundingInterval
  crossProduct (final RoundingInterval x0,
                final RoundingInterval y0,
                final RoundingInterval x1,
                final RoundingInterval y1) {
    final RoundingInterval x0y1 = x0.multiply(y1);
    final RoundingInterval x1y0 = x1.multiply(y0);
    return cover(
      dif(x0y1.min,x1y0.max),
      dif(x0y1.max,x1y0.min)); }

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
    return cover(
      sum(x01.min,y01.min,z01.min),
      sum(x01.max,y01.max,z01.max)); }

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
  // construction
  //--------------------------------------------------------------

//  public RoundingInterval {
//    System.out.println(
//      "[" +
//        Double.toHexString(min) + ", " +
//        Double.toHexString(max) + "]");
//  }
//  public static final RoundingInterval valueOf (final double z)  {
//    return new RoundingInterval(Math.nextDown(z),Math.nextUp(z)); }

  /** Return the smallest <code>double</code> interval that covers
   * both <code>i0</code> and <code>i1</code>
   */

  private static final RoundingInterval cover (final RoundingInterval i0,
                                               final RoundingInterval i1) {
    return new RoundingInterval(Math.min(i0.min, i1.min),
                                Math.max(i0.max,i1.max)); }

//  /** Return the smallest <code>double</code> interval that covers
//   * the exact value of <code>hi+lo</code>.
//   * Expects that, in <code>double</code> arithmetic,
//   * <code>hi+lo==hi</code>
//   */
//
//  private static final RoundingInterval coverExactSum (final double hi,
//                                                     final double lo) {
//    assert hi == hi+lo :
//      "\nhi= " + Double.toHexString(hi) +
//        "\nlo= "  + Double.toHexString(lo) +
//        "\n";
//    // no rounding
//    if (0.0==lo) { return new RoundingInterval(hi,hi); }
//    // rounded down
//    if (0.0<lo) {
//      return new RoundingInterval(hi,Math.nextUp(hi)); }
//    // rounded up
//    return new RoundingInterval(Math.nextDown(hi), hi); }

//  /** Return the smallest <code>double</code> interval that covers
//   * the exact value of <code>hi+lo</code>.
//   * Assumes that, in <code>double</code> arithmetic,
//   * <code>hi+lo==hi</code>
//   */
//
//  private static final RoundingInterval coverExactSum (final Hilo hilo) {
//    return coverExactSum(hilo.hi(), hilo.lo()); }

//  public static final RoundingInterval plusOrMinus (final double z,
//                                                  final double e)  {
//    final double ae = Math.abs(e);
//    return new RoundingInterval(z-ae,z+ae); }

//--------------------------------------------------------------
} // end class
//--------------------------------------------------------------
