package mop.java.numbers;

/** A BoundedNatural plus an overflow integer,
 * Probably overflow will only be zero or one,
 * but that's still to be verified.
 * 
 * This is meant to used use to hold intermediate results
 * for unbounded arithmetic built from bounded representations.
 * 
 * A package local class --- could be internal to BoundedNatural
 * and private.
 * 
 * @author palisades dot lakes at gmail dot com
 * @version 2022-08-25
 */

@SuppressWarnings("unchecked")
final class BoundedNaturalWithOverflow {

  //--------------------------------------------------------------
  // fields
  //--------------------------------------------------------------

  private final BoundedNatural _base;
  public final BoundedNatural base () { return _base; }

  private final long _overflow;
  public final long overflow () { return _overflow; }

  //--------------------------------------------------------------
  // arithmetic
  //-------------------------------------------------------------

  //--------------------------------------------------------------
  // construction
  //-------------------------------------------------------------

  private BoundedNaturalWithOverflow (final BoundedNatural base,
                                      final long overflow) {
    _base = base;
    _overflow = overflow; }

  static final BoundedNaturalWithOverflow 
  make (final BoundedNatural base,
        final long overflow) {
    return new BoundedNaturalWithOverflow(base,overflow); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
