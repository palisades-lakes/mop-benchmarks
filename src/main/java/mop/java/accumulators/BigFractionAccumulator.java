package mop.java.accumulators;

import org.apache.commons.math3.fraction.BigFraction;

import mop.java.accumulators.Accumulator;

/** Naive sum of <code>double</code> values with BigFraction
 * accumulator (for testing).
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-04-21
 */
public final class BigFractionAccumulator

implements Accumulator<BigFractionAccumulator> {

  private BigFraction _sum;

  //--------------------------------------------------------------

  @Override
  public final boolean isExact () { return true; }

  @Override
  public final boolean noOverflow () { return true; }

  @Override
  public final Object value () { return _sum; }

  @Override
  public final double doubleValue () {
    return _sum.doubleValue(); }

  @Override
  public final BigFractionAccumulator clear () {
    _sum = BigFraction.ZERO;
    return this; }

  @Override
  public final BigFractionAccumulator add (final double z) {
    assert Double.isFinite(z);
    _sum = _sum.add(new BigFraction(z));
    return this; }

  @Override
  public final BigFractionAccumulator add2 (final double z) {
    assert Double.isFinite(z);
    final BigFraction zz = new BigFraction(z);
    _sum = _sum.add(zz.multiply(zz));
    return this; }

  @Override
  public final BigFractionAccumulator addProduct (final double z0,
                                                  final double z1) {
    assert Double.isFinite(z0);
    assert Double.isFinite(z1);
    _sum = _sum.add(
      new BigFraction(z0)
      .multiply(
        new BigFraction(z1)));
    return this; }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private BigFractionAccumulator () { super(); clear(); }

  public static final BigFractionAccumulator make () {
    return new BigFractionAccumulator(); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
