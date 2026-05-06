package mop.java.functions;

import mop.java.Exceptions;

// TODO:
// should FUnctions be required/allowed to return correct limiting
// values for non finite arguments?

/** A (optionally differentiable) function between linear spaces,
 * from <b>R</b><sup>n</sup> (domain)
 * to <b>R</b><sup>m</sup> (codomain).
 * May expand to cover other spaces in the future.
 * <p>
 * Special case methods are provided for three cases related to
 * identifying <b>R</b> and <b>R<b><sup>1</sup>, using
 * <code>double</code> for inputs and outputs:
 * <ul>
 * <li> Codomain is <b>R</b> (= <b>R<b><sup>1</sup>).
 * Such functions are often called 'functionals'. Note that I
 * use 'functional' to mean any real-valued function, rather than
 * just linear real-valued functions, which is another common
 * usage.
 * <li> Domain is <b>R</b>. A parameterized curve in
 * <b>R<b><sup>n</sup>.
 * <li> Both <b>R</b>. I call these scalar functionals.
 * </ul>
 * (I am open to a better naming scheme.)
 * <p>
 * <b>Design note:</b>
 * This is a loose interface, with (almost) all operations are
 * optional, like the Java Collection classes.
 * There are 2 main reasons:
 * <ul>
 * <li> Over-use of static, compile-time type constraints leads to
 * brittle, complex code that simply fails in the application
 * areas of interest here (see below).
 * Dynamic (runtime) validation and tests are preferred.
 * <li> Suppose we moved, for example, <code>doubleValue()</code>
 * and <code>gradient()</code>
 * to a Functional interface. An important operation on functions
 * is composition. Whether the composed function is a functional,
 * or a scalar functional, etc.,
 * depends on the two terms. Which means
 * we need at least 3 implementation classes for the output of
 * <code>compose</code>.
 * The same will be true of any other property of a function that
 * might or might not be true of a composition, or a sum, leading
 * to a * combinatorial explosion of implementation classes.
 * </ul>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-01-07
 */
public interface Function

// TODO: is it better to use adaptor classes?
// One large, maybe too large, dynamic interface vs many adaptor
// classes?
extends java.util.function.Function
//, clojure.lang.IFn
{

  /** Valid inputs to the function belong to the domain.
   */

  public Domain domain ();

  /** Values of the function are elements of the
   * <code>codomain</code>, which is expected to be a 'complete'
   * space in some sense. The image of the domain will NOT in
   * general cover the codomain.
   * <p>
   * Common use is to check if 2 functions can be composed.
   */
  public Domain codomain ();

  /** An 'interesting' subset of the domain, in some sense.
   * Typically where the function is not zero, or null, infinite,
   * NaN, etc.
   * <p>
   * Often null, meaning unspecified.
   * <p>
   * TODO: is this too vague to be useful?
   */

  public default Domain support () {
    return null; }
  //    throw Exceptions.unsupportedOperation(this,"support"); }

  /** The image of the domain.
   * <p>
   * Often null, meaning unspecified/unknown rather than empty.
   * In general, it is difficult to determine if an
   * arbitrary object, especially with <code>double</code>
   * coordinates, is in the range, or just a
   * floating point approximation to an element of the range, etc.
   */
  public default Domain range () {
    return null; }
  //    throw Exceptions.unsupportedOperation(this,"range"); }

  //--------------------------------------------------------------
  // general methods
  //--------------------------------------------------------------
  /** Return <code>f(x)</code>
   */

  public default Vektor value (final Vektor x) {
    assert domain().contains(x);
    throw Exceptions.unsupportedOperation(this,"value",x); }

  //--------------------------------------------------------------
  /** General case: Return a function on the domain whose values
   * are linear functions from the domain to the codomain.
   * <p>
   * <code>derivative().value(x) = derivativeAt(x)</code>.
   * <p>
   * Scalar case: Return a function whose value is the slope of
   * this function.
   * <p>
   * <code>derivative().doubleValue(x) = slopeAt(x)</code>.
   */

  public default Function derivative () {
    throw Exceptions.unsupportedOperation(
      getClass(),"derivative"); }

  //--------------------------------------------------------------
  /** General case: Return a function on the domain whose values
   * are affine functions from the domain to the codomain.
   * <p>
   * <code>tangent().value(x) = tangentAt(x)</code>.
   */

  public default Function tangent () {
    throw Exceptions.unsupportedOperation(
      getClass(),"tangent"); }

  //--------------------------------------------------------------
  /** Return the linear function that is the derivative of this
   * at <code>x</code>.
   */

  public default Function derivativeAt (final Vektor x) {
    assert domain().contains(x);
    throw Exceptions.unsupportedOperation(
      this,"derivativeAt",x); }

  //--------------------------------------------------------------
  /** Return the affine function that is the tangent of this
   * at <code>x</code>.
   */

  public default Function tangentAt (final Vektor x) {
    assert domain().contains(x);
    return AffineFunction.make(derivativeAt(x),value(x));}

  //--------------------------------------------------------------
  // 1d domain methods
  //--------------------------------------------------------------
  /** Return <code>f(x)</code>
   */

  @SuppressWarnings("unused")
  public default Vektor value (final double x) {
    assert 1 == domain().dimension();
    throw Exceptions.unsupportedOperation(
      getClass(),"value",Double.TYPE); }

  //--------------------------------------------------------------
  /** Return the linear function that is the derivative of this
   * at <code>x</code>.
   */

  @SuppressWarnings("unused")
  public default Function derivativeAt (final double x) {
    assert 1 == domain().dimension();
    throw Exceptions.unsupportedOperation(
      getClass(),"derivativeAt",Double.TYPE); }

  //--------------------------------------------------------------
  /** Return the affine function that is the tangent of this
   * at <code>x</code>.
   */

  public default Function tangentAt (final double x) {
    assert 1 == domain().dimension();
    return AffineFunction.make(derivativeAt(x),value(x));}

  //--------------------------------------------------------------
  // 1d codomain methods
  //--------------------------------------------------------------
  /** Return the value of the function at <code>x</code>.
   */

  public default double doubleValue (final Vektor x) {
    assert domain().contains(x);
    assert 1 == codomain().dimension();
    throw Exceptions.unsupportedOperation(
      this,"doubleValue",x); }

  //--------------------------------------------------------------
  /** Return the gradient of the function at <code>x</code>.
   */

  public default Vektor gradient (final Vektor x) {
    assert domain().contains(x);
    assert 1 == codomain().dimension();
    throw Exceptions.unsupportedOperation(
      this,"gradient",x); }

  //--------------------------------------------------------------
  // 1d domain and codomain (scalar) methods
  //--------------------------------------------------------------
  /** Return the value of the function at <code>x</code>
   * (1d domain case).
   */
  @SuppressWarnings("unused")
  public default double doubleValue (final double x) {
    assert 1 == domain().dimension();
    assert 1 == codomain().dimension();
    throw Exceptions.unsupportedOperation(
      getClass(),"doubleValue",Double.TYPE); }

  /** Return the slope of the function at <code>x</code>
   * (1d domain case).
   */
  @SuppressWarnings("unused")
  public default double slope (final double x) {
    assert 1 == domain().dimension();
    assert 1 == codomain().dimension();
    throw Exceptions.unsupportedOperation(
      getClass(),"slope",Double.TYPE); }

  /** Return a domain value at which the function
   * achieves an isolated local minimum, where the slope of
   * function is zero, in the (closure) of the
   * <code>support</code>. If there is more than one such domain
   * value, return any one of them. If there are no isolated
   * local minima, return NaN.
   * <p>
   * Only intended to be implemented where this can be calculated
   * efficiently, in something like closed form.
   */
  @SuppressWarnings("unused")
  public default double doubleArgmin (final Domain support) {
    assert 1 == domain().dimension();
    assert 1 == codomain().dimension();
    throw Exceptions.unsupportedOperation(
      this,"doubleArgmin",support); }

  //--------------------------------------------------------------
  // java.util.function methods
  //--------------------------------------------------------------
  /** Return <code>f(x)</code>
   */

  @Override
  public default Object apply (final Object x) {
    return value((Vektor) x); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------

