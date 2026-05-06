package mop.java.functions;

/** A linear function from <b>R</b><sup>n</sup> to <b>R</b>.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2018-09-07
 */

public final class LinearFunctional extends Functional {

  //--------------------------------------------------------------
  // fields
  //--------------------------------------------------------------

  private final Domain _domain;

  private final Vektor _dual;
  public final Vektor dual () { return _dual; }

  //--------------------------------------------------------------
  // Function methods
  //--------------------------------------------------------------

  @Override
  public final Domain domain () { return _domain; }

  //--------------------------------------------------------------
  // Function methods
  //--------------------------------------------------------------

  @Override
  public final double doubleValue (final Vektor x) {
    return _dual.dot(x); }

  @Override
  @SuppressWarnings("unused")
  public final Vektor gradient (final Vektor x) {
    return _dual; }

  @Override
  @SuppressWarnings("unused")
  public final Function tangentAt (final Vektor x) {
    return this; }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------
  // TODO: too long for toString..

  @Override
  public final String toString () {
    final StringBuilder b = new StringBuilder();
    b.append(getClass().getSimpleName());
    b.append(":\n");
    b.append(_dual.toString());
    return b.toString(); }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private LinearFunctional (final Vektor dual) {
    _domain = Dn.get(dual.dimension());
    _dual = dual; }

  public static final LinearFunctional make (final Vektor dual) {
    return new LinearFunctional(dual); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------

