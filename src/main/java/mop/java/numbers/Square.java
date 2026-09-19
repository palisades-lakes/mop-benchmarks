package mop.java.numbers;

/** A clumsy substitute for a generic function (Common Lisp)
 * (aka 'multimethod' in Clojure) that permits defining new methods
 * for existing classes.
 */

public final class Square {

  // TODO: add cases as needed

  public static final Object square (final Object a) {
    return switch (a) {
      case Double aa -> aa*aa;
      case BigFloat aa -> aa.square();
      default -> throw new UnsupportedOperationException(
        "No method to square " + a.getClass().getName()); }; }

  //--------------------------------------------------------------
  // construction
  //-------------------------------------------------------------

  /** not instantiable. */
  private Square () {
    throw new
      UnsupportedOperationException(
      "can't instantiate " + getClass().getCanonicalName()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
