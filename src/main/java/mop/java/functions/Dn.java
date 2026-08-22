package mop.java.functions;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;

import mop.java.Exceptions;

/** Approximate <b>R</b><sup>n</sup> with <code>double</code>s.
 * <p>
 * TODO: Special case D1, D2, D3, D4...?
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */

@SuppressWarnings("unchecked")
public final class Dn implements Domain {

  private final int _dimension;

  //--------------------------------------------------------------
  // methods
  //--------------------------------------------------------------

  public final boolean contains (final Vektor x) {
    return dimension() == x.dimension(); }

  //--------------------------------------------------------------
  // Domain methods
  //--------------------------------------------------------------

  @Override
  public final int dimension () { return _dimension; }

  @Override
  public final boolean contains (final Object x) {
    if (x instanceof Vektor) { return contains((Vektor) x); }
    throw Exceptions.unsupportedOperation(this,"contains",x); }

  @Override
  public final boolean contains (final double x) {
    return 1 == dimension(); }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------

  @Override
  public final int hashCode () {
    return Integer.hashCode(_dimension); }

  @Override
  public final boolean equals (final Object obj) {
    if (this == obj) { return true; }
    if (!(obj instanceof final Dn other)) { return false; }
    return _dimension == other._dimension; }

  @Override
  public final String toString () { return "D" + _dimension; }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private Dn (final int dimension) {
    assert dimension > 0;
    _dimension = dimension; }

  private static final IntObjectMap<Dn> _cache = new IntObjectHashMap();

  // TODO: test speed
  //  public static final Dn get (final int dimension) {
  //    final int index = _cache.indexOf(dimension);
  //    final Dn dn0 = _cache.indexGet(index);
  //    if (null != dn0) { return dn0; }
  //    final Dn dn1 = new Dn(dimension);
  //    _cache.indexInsert(index,dimension,dn1);
  //    return dn1; }

  //--------------------------------------------------------------

  public static final Dn get (final int dimension) {
    final Dn dn0 = _cache.get(dimension);
    if (null != dn0) { return dn0; }
    final Dn dn1 = new Dn(dimension);
    _cache.put(dimension,dn1);
    return dn1; }

  public static final Dn D1 = Dn.get(1);

  //--------------------------------------------------------------
}
