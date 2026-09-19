package mop.java.numbers;

/** A clumsy substitute for a generic function (Common Lisp)
 * (aka 'multimethod' in Clojure) that permits defining new methods
 * for existing classes.
 */

public final class DoubleValue {

  // TODO: add cases as needed

  public static final Object doubleValue (final Object a) {
    return switch (a) {
      case Double aa -> aa;
      case BigFloat aa -> aa.doubleValue();
      default -> throw new UnsupportedOperationException(
        "No method to round to double " + a.getClass().getName()); }; }

  //--------------------------------------------------------------
  // construction
  //-------------------------------------------------------------

  /** not instantiable. */
  private DoubleValue () {
    throw new
      UnsupportedOperationException(
      "can't instantiate " + getClass().getCanonicalName()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
