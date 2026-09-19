package mop.java.numbers;

/** A clumsy substitute for a generic function (Common Lisp)
 * (aka 'multimethod' in Clojure) that chooses its methods
 * at based on the runtime types of all arguments.
 * <p>
 *   Java's original sin was perpetuating the bizarre C++ method lookup
 *   rules: <code>a.add(b)</code> chooses a method based on the
 *   runtime type of <code>a</code>, but the compile time of
 *   <code>b</code>.
 * </p>
 */

public final class Subtract {

  // TODO: add cases as needed
  public static final Object subtract (final Double a,
                                       final Object b) {
    return switch (b) {
      case Double bb -> a - bb;
      case BigFloat bb -> bb.negate().add(a);
      default ->
        throw new UnsupportedOperationException(
          "No method to subtract " + b.getClass().getName() +
            " from Double"); }; }

  public static final Object subtract (final Object a,
                                       final Object b) {
    return switch (a) {
      case Double aa -> subtract(aa,b);
      case BigFloat aa -> aa.subtract(b);
      default -> throw new UnsupportedOperationException(
        "No method to subtract " + a.getClass().getName() +
          " to anything." ); }; }

  //--------------------------------------------------------------
  // construction
  //-------------------------------------------------------------

  /** not instantiable. */
  private Subtract () {
    throw new
      UnsupportedOperationException(
      "can't instantiate " + getClass().getCanonicalName()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
