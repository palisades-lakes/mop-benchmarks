package mop.java.accumulators;

import mop.java.accumulators.Accumulator;

/** Naive sum of <code>double</code> values with float
 * accumulator (for testing).
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-04-21
 */
public final class FloatAccumulator

implements Accumulator<FloatAccumulator> {

  private float _sum;

  //--------------------------------------------------------------

  @Override
  public final boolean isExact () { return false; }

  @Override
  public final boolean noOverflow () { return false; }

  @Override
  public final double doubleValue () { return _sum; }

  @Override
  public final Object value () { return Float.valueOf(_sum); }

  @Override
  public final FloatAccumulator clear () { _sum = 0.0F; return this; }

  @Override
  public final FloatAccumulator add (final double z) {
    assert Double.isFinite(z);
    _sum += (float) z;
    return this; }

  @Override
  public final FloatAccumulator add2 (final double z) {
    assert Double.isFinite(z);
    _sum += ((float) z)*((float) z);
    return this; }

  @Override
  public final FloatAccumulator addProduct (final double z0,
                                            final double z1) {
    assert Double.isFinite(z0);
    assert Double.isFinite(z1);
    _sum += ((float) z0)*((float) z1);
    return this; }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private FloatAccumulator () { super(); _sum = 0.0F; }

  public static final FloatAccumulator make () {
    return new FloatAccumulator(); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
