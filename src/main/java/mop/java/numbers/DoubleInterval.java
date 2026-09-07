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
    if (isNaN()) { return NaN; }
    final double z0 = Math.abs(min());
    final double z1 = Math.abs(max());
    if (containsZero()) {
      return new DoubleInterval(0.0,Math.max(z0,z1)); }
    if (z0<=z1) { return new DoubleInterval(z0,z1); }
    return new DoubleInterval(z1,z0); }

  //--------------------------------------------------------------
  // TODO: do we need Hilo.sum()? expand intervals for
  //  rounding in adds?

  @Override
  public final DoubleInterval add (final DoubleInterval q) {
    if (isNaN() || q.isNaN()) { return NaN; }
    return new DoubleInterval(min()+q.min(),max()+q.max()); }

  //--------------------------------------------------------------
  // TODO: do we need Hilo.sum()? expand intervals for
  //  rounding in subtracts?

  @Override
  public final DoubleInterval subtract (final DoubleInterval q) {
    if (isNaN() || q.isNaN()) { return NaN; }
    return new DoubleInterval(min()-q.max(),max()-q.min()); }

  //--------------------------------------------------------------
  /** Return the double interval covering the error in
   *  <code>z0-z1</code>,
   */

//  public static final DoubleInterval dif (final double z0,
//                                          final double z1) {
//    // TODO: twice the real width?
//    return new DoubleInterval(Math.nextDown(z0-z1),Math.nextUp(z0-z1)); }

  public static final DoubleInterval dif (final double z0,
                                          final double z1) {
    // TODO: is this correct?
    //  Assuming z0, z1 'exact',
    //  construct a small interval with double bounds guaranteed
    //  to include the exact, rational difference.
    final Hilo z01 = Hilo.sum(z0,-z1);
    final double hi = z01.hi();
    final double lo = z01.lo();
    // no rounding
    if (0.0==lo) { return new DoubleInterval(hi,hi); }
    // round down
    if (0.0<lo) {
      assert hi+lo <= Math.nextUp(hi);
      return new DoubleInterval(hi,Math.nextUp(hi)); }
    // round up
    //else if (0.0>lo) {
    assert hi+lo >= Math.nextDown(hi) :
      "\n\nhi: " + Double.toHexString(hi) +
        "\nlo: " + Double.toHexString(lo) +
        "\nhi+lo: " + Double.toHexString(hi+lo) +
        "\nnextDown(hi): " + Double.toHexString(Math.nextDown(hi)) +
        "\n\n";
    return new DoubleInterval(Math.nextDown(hi), hi); }

  //--------------------------------------------------------------
  // TODO: do we need Hilo.product()? expand intervals for
  //  rounding in multiplies?

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
  // TODO: do we need Hilo.square()? expand intervals for
  //  rounding in multiplies?

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

//  public DoubleInterval {
//    System.out.println(
//      "[" +
//        Double.toHexString(min) + ", " +
//        Double.toHexString(max) + "]");
//  }
//  public static final DoubleInterval valueOf (final double z)  {
//    return new DoubleInterval(Math.nextDown(z),Math.nextUp(z)); }

  public static final DoubleInterval plusOrMinus (final double z,
                                                  final double e)  {
    final double ae = Math.abs(e);
    return new DoubleInterval(z-ae,z+ae); }

  //--------------------------------------------------------------
} // end class
//--------------------------------------------------------------
