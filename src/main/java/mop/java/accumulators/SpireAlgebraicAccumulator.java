package mop.java.accumulators;

import mop.java.accumulators.ExactAccumulator;
import mop.java.numbers.SpireAlgebraics;
import spire.math.Algebraic;

/** Naive sum of <code>double</code> values with Algebraic
 * accumulator (for testing).
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-12-01
 */
public final class SpireAlgebraicAccumulator

extends ExactAccumulator<SpireAlgebraicAccumulator> {

  private Algebraic _sum;

  //--------------------------------------------------------------

  @Override
  public final boolean noOverflow () { return true; }

  @Override
  public final Object value () { return _sum; }

  @Override
  public final double doubleValue () {
    return _sum.doubleValue(); }

  @Override
  public final SpireAlgebraicAccumulator clear () {
    _sum = SpireAlgebraics.ZERO;
    return this; }

  @Override
  public final SpireAlgebraicAccumulator add (final double z) {
    //assert Double.isFinite(z);
    _sum = _sum.$plus(SpireAlgebraics.toAlgebraic(z));
    return this; }

  @Override
  public final SpireAlgebraicAccumulator add2 (final double z) {
    //assert Double.isFinite(z);
    final Algebraic zz = SpireAlgebraics.toAlgebraic(z);
    _sum = _sum.$plus(zz.$times(zz));
    return this; }

  @Override
  public final SpireAlgebraicAccumulator addL2 (final double z0,
                                                final double z1) {
    //assert Double.isFinite(z0);
    //assert Double.isFinite(z1);
    final Algebraic zz0 = SpireAlgebraics.toAlgebraic(z0);
    final Algebraic zz1 = SpireAlgebraics.toAlgebraic(z1);
    final Algebraic dz = zz0.$minus(zz1);
    final Algebraic dz2 = dz.$times(dz);
    _sum = _sum.$plus(dz2);
    return this; }

  @Override
  public final SpireAlgebraicAccumulator addProduct (final double z0,
                                                     final double z1) {
    //assert Double.isFinite(z0);
    //assert Double.isFinite(z1);
    _sum = _sum.$plus(
      SpireAlgebraics.toAlgebraic(z0)
      .$times(SpireAlgebraics.toAlgebraic(z1)));
    return this; }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------

  @Override
  public final String toString () { return _sum.toString(); }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private SpireAlgebraicAccumulator () { super(); clear(); }

  public static final SpireAlgebraicAccumulator make () {
    return new SpireAlgebraicAccumulator(); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
