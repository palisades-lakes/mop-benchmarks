package mop.java.accumulators;

import mop.java.accumulators.ExactAccumulator;
import mop.java.numbers.SpireReals;
import spire.math.Real;

/** Naive sum of <code>double</code> values with Real
 * accumulator (for testing).
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-12-02
 */
public final class SpireRealAccumulator

extends ExactAccumulator<SpireRealAccumulator> {

  private Real _sum;

  //--------------------------------------------------------------

  @Override
  public final boolean noOverflow () { return true; }

  @Override
  public final Object value () { return _sum; }

  @Override
  public final double doubleValue () {
    return _sum.doubleValue(); }

  @Override
  public final SpireRealAccumulator clear () {
    _sum = SpireReals.ZERO;
    return this; }

  @Override
  public final SpireRealAccumulator add (final double z) {
    //assert Double.isFinite(z);
    _sum = _sum.$plus(SpireReals.toReal(z));
    return this; }

  @Override
  public final SpireRealAccumulator add2 (final double z) {
    //assert Double.isFinite(z);
    final Real zz = SpireReals.toReal(z);
    _sum = _sum.$plus(zz.$times(zz));
    return this; }

  @Override
  public final SpireRealAccumulator addL2 (final double z0,
                                           final double z1) {
    //assert Double.isFinite(z0);
    //assert Double.isFinite(z1);
    final Real zz0 = SpireReals.toReal(z0);
    final Real zz1 = SpireReals.toReal(z1);
    final Real dz = zz0.$minus(zz1);
    final Real dz2 = dz.$times(dz);
    _sum = _sum.$plus(dz2);
    return this; }

  @Override
  public final SpireRealAccumulator addProduct (final double z0,
                                                final double z1) {
    //assert Double.isFinite(z0);
    //assert Double.isFinite(z1);
    _sum = _sum.$plus(
      SpireReals.toReal(z0)
      .$times(SpireReals.toReal(z1)));
    return this; }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------

  @Override
  public final String toString () { return _sum.toString(); }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private SpireRealAccumulator () { super(); clear(); }

  public static final SpireRealAccumulator make () {
    return new SpireRealAccumulator(); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
