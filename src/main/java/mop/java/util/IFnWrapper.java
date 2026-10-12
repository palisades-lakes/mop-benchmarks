package mop.java.util;

import java.util.Map;

/** Wrapper to allow Java <code>Map</code>s to be used as
 * clojure functions.
 * <br>
 * Probably already done somewhere, but can't find it.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-12
 */

@SuppressWarnings("unchecked")
public final class IFnWrapper extends IFnImplementation {

  private final Map _immutableMap;
  private final Map immutableMap () { return _immutableMap; }

  //--------------------------------------------------------------------
  // IFn
  //--------------------------------------------------------------------

  @Override
  public Object invoke (final Object key) {
    return immutableMap().get(key); }

  //--------------------------------------------------------------------
  // Object
  //--------------------------------------------------------------------

  @Override
  public final String toString () { return immutableMap().toString(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private IFnWrapper (final Map mutable) {
    super();
    // defensive copy
    _immutableMap = Map.copyOf(mutable); }

  public static final IFnWrapper make (final Map mutable) {
    return new IFnWrapper(mutable); }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
