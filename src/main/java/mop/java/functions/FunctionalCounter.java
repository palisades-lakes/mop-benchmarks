package mop.java.functions;

/** Wrapper that counts function and gradient evaluations,
 * and caches last position, value, and gradient.
 *
 * <em>WARNING:</em> not `thread safe!
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2018-09-07
 */

public final class FunctionalCounter implements Function {

  //--------------------------------------------------------------
  // fields
  //--------------------------------------------------------------

  private final Function _inner;
  public final Function inner () { return _inner; }

  private int _nf = 0;
  public final int nf () { return _nf; }

  private int _ng = 0;
  public final int ng () { return _ng; }

  //--------------------------------------------------------------
  // Function methods
  //--------------------------------------------------------------

  @Override
  public final Domain domain () { return inner().domain(); }

  @Override
  public final Domain codomain () { return Dn.D1; }

  @Override
  public final double doubleValue (final Vektor x) {
    _nf++; return _inner.doubleValue(x); }

  @Override
  public final Vektor gradient (final Vektor x) {
    _ng++; return _inner.gradient(x); }

  @Override
  public final Function tangentAt (final Vektor x) {
    _nf++; _ng++; return _inner.tangentAt(x); }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private FunctionalCounter (final Function inner) {
    super();
    _inner = inner; }

  public static final FunctionalCounter
  wrap (final Function inner) {
    // assert 1 == inner.domain().dimension();
    return new FunctionalCounter(inner); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
