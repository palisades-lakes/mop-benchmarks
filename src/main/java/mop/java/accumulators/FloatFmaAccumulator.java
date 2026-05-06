package mop.java.accumulators;

import mop.java.accumulators.Accumulator;

/** Naive sum of <code>double</code> values with float
 * accumulator (for testing).
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-04-21
 */
public final class FloatFmaAccumulator

implements Accumulator<FloatFmaAccumulator> {

  private float _sum;

  //--------------------------------------------------------------

  @Override
  public final boolean isExact () { return false; }

  @Override
  public final boolean noOverflow () { return false; }

  @Override
  public final Object value () { return Float.valueOf(_sum); }

  @Override
  public final double doubleValue () { return _sum; }

  @Override
  public final FloatFmaAccumulator clear () { _sum = 0.0F; return this; }

  @Override
  public final FloatFmaAccumulator add (final double z) {
    assert Double.isFinite(z);
    _sum += (float) z;
    return this; }

  @Override
  public final FloatFmaAccumulator add2 (final double z) {
    assert Double.isFinite(z);
    _sum = Math.fma((float) z, (float) z, _sum);
    return this; }

  @Override
  public final FloatFmaAccumulator addProduct (final double z0,
                                               final double z1) {
    assert Double.isFinite(z0);
    assert Double.isFinite(z1);
    _sum = Math.fma((float) z0, (float) z1, _sum);
    return this; }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private FloatFmaAccumulator () { super(); _sum = 0.0F; }

  public static final FloatFmaAccumulator make () {
    return new FloatFmaAccumulator(); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
