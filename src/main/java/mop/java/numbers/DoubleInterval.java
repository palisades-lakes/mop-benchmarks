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
 * { z*z : z in [min(),max()]} is different from
 * { z0*z1 : z0,z1 in [min(),max()]}.
 * More generally,
 * { f(z,z) : z in [min(),max()] } == { f(z0,z1) : z0,z1 in [min(),max()] }
 * only if f is monotone in both arguments over [min(),max()].
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-12
 */

public interface DoubleInterval extends Ringlike<DoubleInterval> {

  //--------------------------------------------------------------

  public double min();
  public double max();
  //--------------------------------------------------------------

  public default boolean containsZero () {
    return (min()<=0.0) && (0.0<=max()); }

  public default boolean contains (final double z) {
    return (min()<=z) && (z<=max()); }

  public default boolean contains (final DoubleInterval interval) {
    return (min()<=interval.min()) && (interval.max()<=max()); }

  public default boolean contains (final BigFloat bf) {
    return bf.opGE(min()) && bf.opLE(max()); }

  //--------------------------------------------------------------
  // Ringlike
  //--------------------------------------------------------------

   @Override
  public default boolean isZero () {
    return 0.0==min() && 0.0==max(); }

  @Override
  public default boolean isOne () {
    return 1.0==min() && 1.0==max(); }

  public default boolean isNaN () {
    return Double.isNaN(min()) && Double.isNaN(max()); }

//--------------------------------------------------------------
// Number methods
//--------------------------------------------------------------

  /** Return midpoint as approximation. */
  @Override
  public default double doubleValue () { return (min()+max())/2; }

  public default String toHexString () {
    return
      "[" + Double.toHexString(min()) + "," +
        Double.toHexString(max()) + "]";  }

//--------------------------------------------------------------
} // end class
//--------------------------------------------------------------
