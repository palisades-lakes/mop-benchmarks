package mop.java.accumulators;

import static clojure.lang.Numbers.toRatio;

import java.math.BigInteger;

import clojure.lang.Numbers;
import clojure.lang.Ratio;
import mop.java.accumulators.Accumulator;

/** Naive sum of <code>double</code> values with Ratio
 * accumulator (for testing).
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-04-21
 */
public final class RatioAccumulator

implements Accumulator<RatioAccumulator> {

  private static final Ratio add (final Ratio q0,
                                  final Ratio q1) {
    return toRatio(Numbers.add(q0,q1)); }

  private static final Ratio multiply (final Ratio q0,
                                       final Ratio q1) {
    return toRatio(Numbers.multiply(q0,q1)); }

  private static final Ratio ZERO =
    new Ratio(BigInteger.ZERO,BigInteger.ONE);

  //--------------------------------------------------------------

  private Ratio _sum;

  //--------------------------------------------------------------
  // Both should be true, but isn't!

  @Override
  public final boolean isExact () { return false; }

  @Override
  public final boolean noOverflow () { return false; }

  @Override
  public final Object value () { return _sum; }

  @Override
  public final double doubleValue () {
    return _sum.doubleValue(); }

  @Override
  public final RatioAccumulator clear () {
    _sum = ZERO; return this; }

  @Override
  public final RatioAccumulator add (final double z) {
    assert Double.isFinite(z);
    _sum = add(_sum,toRatio(Double.valueOf(z))); return this; }

  @Override
  public final RatioAccumulator add2 (final double z) {
    assert Double.isFinite(z);
    final Ratio zz = toRatio(Double.valueOf(z));
    _sum = add(_sum,multiply(zz,zz));
    return this; }

  @Override
  public final RatioAccumulator addProduct (final double z0,
                                            final double z1) {
    assert Double.isFinite(z0);
    assert Double.isFinite(z1);
    _sum = add(
      _sum,
      multiply(
        toRatio(Double.valueOf(z0)),
        toRatio(Double.valueOf(z1))));
    return this; }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private RatioAccumulator () { super(); clear(); }

  public static final RatioAccumulator make () { return new RatioAccumulator(); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
