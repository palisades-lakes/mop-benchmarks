package mop.java.functions;

import java.util.HashMap;
import java.util.Map;

import mop.java.Exceptions;

/** Function spaces.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2018-09-13
 */
@SuppressWarnings("unchecked")
public final class Fnm implements Domain {

  private final Domain _domain;
  private final Domain _codomain;

  //--------------------------------------------------------------
  // methods
  //--------------------------------------------------------------

  public final boolean contains (final Function f) {
    return
      _domain.equals(f.domain())
      &&
      _codomain.equals(f.codomain()); }

  //--------------------------------------------------------------
  // Domain methods
  //--------------------------------------------------------------
  // dimension countably infinite, when domain and codomain
  // are linear spaces.

  //  @Override
  //  public final int dimension () {
  //    return _dimension; }

  @Override
  public final boolean contains (final Object x) {
    if (x instanceof Function) { return contains((Function) x); }
    throw Exceptions.unsupportedOperation(this,"contains",x); }

  @Override
  public final boolean contains (final double x) { return false; }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------

  @Override
  public int hashCode () {
    final int prime = 31;
    int result = 1;
    result = (prime * result) +  _codomain.hashCode();
    result = (prime * result) + _domain.hashCode();
    return result; }

  @Override
  public boolean equals (final Object obj) {
    if (this == obj) { return true; }
    if ((obj == null) || !(obj instanceof Fnm)) { return false; }
    final Fnm other = (Fnm) obj;
    if (!_codomain.equals(other._codomain) || !_domain.equals(other._domain)) { return false; }
    return true; }

  @Override
  public String toString () {
    return "F[" + _domain + " -> " + _codomain + "]"; }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private Fnm (final Domain domain,
               final Domain codomain) {
    assert null != domain;
    assert null != codomain;
    _domain = domain;
    _codomain = codomain; }

  //--------------------------------------------------------------
  // TODO: does caching actually help anything?

  private static final Map _cache = new HashMap();

  public static final Fnm get (final Domain domain,
                               final Domain codomain) {
    final Fnm t = new Fnm(domain,codomain);
    final Fnm f = (Fnm) _cache.get(t);
    if (null != f) { return f; }
    _cache.put(t,t);
    return t; }

  //--------------------------------------------------------------
}
