package mop.java.functions;

/** An affine function from <b>R</b><sup>n</sup> to
 * <b>R</b><sup>m</sup>.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2018-09-13
 */

public final class AffineFunction implements Function {

  //--------------------------------------------------------------
  // fields
  //--------------------------------------------------------------

  // Assuming the linear part is immutable!

  private final Function _linear;
  public final Function linear () { return _linear; }

  private final Vektor _translation;
  public final Vektor translation () { return _translation; }

  //--------------------------------------------------------------
  // Function methods
  //--------------------------------------------------------------

  @Override
  public final Domain domain () {
    return _linear.domain(); }

  @Override
  public final Domain codomain () {
    return _linear.codomain(); }

  //--------------------------------------------------------------

  @Override
  public final Vektor value (final Vektor x) {
    return _translation.add(_linear.value(x)); }

  @Override
  @SuppressWarnings("unused")
  public final Function derivativeAt (final Vektor x) {
    return _linear; }

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
    b.append(_linear.toString());
    b.append(":\n");
    b.append(_translation.toString());
    return b.toString(); }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private AffineFunction (final Function linear,
                          final Vektor translation) {
    _linear =  linear;
    _translation = translation; }

  public static final AffineFunction
  make (final Function linear,
        final Vektor translation) {
    return new AffineFunction(linear,translation); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------

