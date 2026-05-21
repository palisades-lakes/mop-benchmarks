/*
 * Copyright (c) 2016 Martin Davis.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this
 * distribution.
 * The Eclipse Public License is available at http://www.eclipse
 * .org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package mop.java.numbers;

import org.jspecify.annotations.NonNull;

import java.io.Serializable;

/** Implements extended-precision floating-point numbers which maintain
 * 106 bits (approximately 30 decimal digits) of precision.
 * <p>
 * A <code>DD</code>> uses a representation containing two double-precision
 * values. A number x is represented as a pair of doubles, x.hi and
 * x.lo, such that the number represented by x is x.hi + x.lo, where
 * <pre>
 *    |x.lo| &lt;= 0.5*ulp(x.hi)
 * </pre>
 * and ulp(y) means "unit in the last place of y". The basic arithmetic
 * operations are implemented using convenient properties of IEEE-754
 * floating-point arithmetic.
 * <p>
 * The range of values which can be represented is the same as in
 * IEEE-754. The precision of the representable numbers is twice as
 * great as IEEE-754 double precision.
 * <p>
 * The correctness of the arithmetic algorithms relies on operations
 * being performed with standard IEEE-754 double precision and rounding.
 * This is the Java standard arithmetic model, but for performance
 * reasons Java implementations are not constrained to using this
 * standard by default. Some processors (notably the Intel Pentium
 * architecture) perform floating point operations in
 * (non-IEEE-754-standard) extended-precision. A JVM implementation may
 * choose to use the non-standard extended-precision as its default
 * arithmetic mode. To prevent this from happening, this code uses the
 * Java <tt>strictfp</tt> modifier, which forces all operations to take
 * place in the standard IEEE-754 rounding model.
 * <p>
 * The API provides both a set of value-oriented operations and a set of
 * mutating operations. Value-oriented operations treat DD
 * values as immutable; operations on them return new objects carrying
 * the result of the operation.  This provides a simple and safe
 * semantics for writing DD expressions.  However, there is a
 * performance penalty for the object allocations required. The mutable
 * interface updates object values in-place. It provides optimum memory
 * performance, but requires care to ensure that aliasing errors are not
 * created and constant values are not changed.
 * <p>
 * For example, the following code example constructs three DD
 * instances: two to hold the input values and one to hold the result of
 * the addition.
 * <pre>
 *     DD a = DD.make(2.0);
 *     DD b = DD.make(3.0);
 *     DD c = a.add(b);
 * </pre>
 * In contrast, the following approach uses only one object:
 * <pre>
 *     DD a = DD.make(2.0);
 *     a.selfAdd(3.0);
 * </pre>
 * Note (2026-05-11): simpler to use DD a = twoSum(2.0,3.0)
 * <p>
 * This implementation uses algorithms originally designed variously by
 * Knuth, Kahan, Dekker, and Linnainmaa. Douglas Priest developed the
 * first C implementation of these techniques. Other more recent C++
 * implementation are due to Keith M. Briggs and David Bailey et al.
 * <p>
 * <h3>References</h3>
 * <ul>
 * <li>Priest, D., <i>Algorithms for Arbitrary Precision Floating
 * Point Arithmetic</i>,
 * in P. Kornerup and D. Matula, Eds., Proc. 10th Symposium on
 * Computer Arithmetic,
 * IEEE Computer Society Press, Los Alamitos, Calif., 1991.
 * <li>Yozo Hida, Xiaoye S. Li and David H. Bailey,
 * <i>Quad-Double Arithmetic: Algorithms, Implementation, and
 * Application</i>,
 * manuscript, Oct 2000; Lawrence Berkeley National Laboratory Report
 * BNL-46996.
 * <li>David Bailey,
 * <a href="https://crd.lbl.gov/~dhbailey/mpdist/index.html">
 *   <i>High Precision Software Directory</i></a>
 * </ul>
 *
 * @author Martin Davis
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-05-11
 * <br>
 * modified from JTS for benchmarking relative to mop.
 * JTS version has some additions (determinant, correction (?)
 * to parse(String)) relative to Tinfour version, but otherwise
 * identical, except for comment formatting.
 * <ul>
 *   <li> Handle special cases in multiply to avoid overflows.
 *   <li> Split into immutable and mutable classes.
 *   <li> single construction path to enforce hi,lo constraints
 *   <li> TODO: distinguish positive and negative zero?
 * </ul>
 */

//@SuppressWarnings("unused")
public record Hilo (double hi, double lo)
  implements Serializable, Comparable {

  //--------------------------------------------------------------------
  // class singletons
  //--------------------------------------------------------------------

  public static final Hilo NaN = new Hilo(Double.NaN, Double.NaN);

  public static final Hilo POSITIVE_INFINITY =
    new Hilo(Double.POSITIVE_INFINITY, 0.0);

  public static final Hilo NEGATIVE_INFINITY =
    new Hilo(Double.NEGATIVE_INFINITY, 0.0);

  /** additive identity
   */
  public static final Hilo ZERO = twoSum(0.0, 0.0);

  /** multiplicative identity
   */
  public static final Hilo ONE = twoSum(1.0, 0.0);

  //--------------------------------------------------------------------
  // instance methods
  //-------------------------------------------------------------------
  /** Returns the absolute value of this value. Special cases:
   * <ul>
   * <li>If this value is NaN, it is returned.
   * </ul>
   *
   * @return the absolute value of this value
   */
  public final Hilo abs () {
    if (isNaN()) { return NaN; }
    if (isNegative()) { return twoSum(-hi, -lo); }
    return this; }

  //--------------------------------------------------------------------
  // arithmetic
  //--------------------------------------------------------------------
  // TODO: check Shewchuk, etc., and add references.

  /** @return <tt>(this + y)</tt>
   */
  public final Hilo add (final double y) {
    final double S = hi + y;
    final double e = S - hi;
    double s = S - e;
    s = (y - e) + (hi - s);
    final double f = s + lo;
    final double H = S + f;
    final double h = f + (S - H);
    return twoSum(H + h, h + (H - hi)); }

  private final Hilo add (final double yhi, double ylo) {
    final double S = hi + yhi;
    final double T = lo + ylo;
    double e = S - hi;
    final double f = T - lo;
    double s = S - e;
    double t = T - f;
    s = (yhi - e) + (hi - s);
    t = (ylo - f) + (lo - t);
    e = s + T;
    final double H = S + e;
    final double h = e + (S - H);
    e = t + h;
    final double zhi = H + e;
    final double zlo = e + (H - zhi);
    return twoSum(zhi, zlo); }

  /** @return <tt>(this + y)</tt>
   */
  public final Hilo add (final Hilo y) {
    return add(y.hi,y.lo); }

  /** @return <tt>(this - y)</tt>
   */
  public final Hilo subtract (final Hilo y) { return add(-y.hi, -y.lo); }

  /** @return <tt>(this - y)</tt>
   */
  public final Hilo subtract (final double y) { return add(-y); }

  /** @return <tt>(this - y)</tt>
   */
  public final Hilo negate () {
    // TODO: sum probably not necessary
    return twoSum(-hi, -lo); }

  //-------------------------------------------------------------------
  // multiplication
  //-------------------------------------------------------------------
  /** The value to split a double-precision value on during
   * multiplication.
   * TODO: should this be an int?<br>
   * Use hex string for clarity
   */
  private static final double SPLIT = 1.0 + 0x1.0p27;

  public final Hilo multiply (final double yhi, final double ylo) {
    // TODO: check whether all these edge cases are necessary
    if (ZERO.equals(this)) { return ZERO; }
    if ((0.0==yhi) && (0.0==ylo)) { return ZERO; }
    if (ONE.equals(this)) { return twoSum(yhi, ylo); }
    if ((1.0==yhi) && (0.0==ylo)) { return this; }
    if (isNaN()) { return NaN; }
    if (Double.isNaN(yhi)) {return NaN; }
    assert ! Double.isNaN(ylo);
    final double hiTest = hi * yhi;
    // TODO: is this right? safe to ignore lo and ylo?
    if (Double.isInfinite(hiTest)) {
      return (0 < hiTest) ? POSITIVE_INFINITY : NEGATIVE_INFINITY; }
    double C = SPLIT * hi;
    double hx = C - hi;
    double c = SPLIT * yhi;
    hx = C - hx;
    double tx = hi - hx;
    double hy = c - yhi;
    C = hi * yhi;
    hy = c - hy;
    final double ty = yhi - hy;
    c = hx * hy;
    c -= C;
    c += hx * ty;
    c += tx * hy;
    c += tx * ty;
    c += (hi * ylo) + (lo * yhi);
    final double zhi = C + c;
    hx = C - zhi;
    final double zlo = c + hx;
    return twoSum(zhi, zlo); }

  public final Hilo multiply (final Hilo that) {
    return multiply(that.hi, that.lo); }

  public final Hilo multiply (final double y) {
    // TODO: optimize simple double case
    return multiply(y, 0.0); }

  //-------------------------------------------------------------------
  // division
  //-------------------------------------------------------------------

   public final Hilo divide (final double yhi, final double ylo) {
     double hc, tc, hy, ty, C, c, U, u;
     C = hi / yhi; c = SPLIT * C; hc = c - C; u = SPLIT * yhi;
     hc = c - hc;
     tc = C - hc; hy = u - yhi; U = C * yhi; hy = u - hy;
     ty = yhi - hy;
     u = (((hc * hy - U) + hc * ty) + tc * hy) + tc * ty;
     c = ((((hi - U) - u) + lo) - C * ylo) / yhi;
     u = C + c;
     return twoSum(u, (C - u) + c); }

  /** Computes a new DD whose value is <tt>(this / y)</tt>.
   *
   * @param y the divisor
   *
   * @return a new object with the value <tt>(this / y)</tt>
   */
  public final Hilo divide (final Hilo y) {
   return divide(y.hi, y.lo); }

  /** Computes a new DD whose value is <tt>(this / y)</tt>.
   *
   * @param y the divisor
   *
   * @return a new object with the value <tt>(this / y)</tt>
   */
  public final Hilo divide (final double y) {
    if (Double.isNaN(y)) { return NaN; }
    // TODO: optimize single double case
    return divide(y, 0.0); }

  //-------------------------------------------------------------------
  /** Returns a DD whose value is  <tt>1 / this</tt>.
   *
   * @return the reciprocal of this value
   */
  public final Hilo reciprocal () {
    // TODO: unit test ONE.divide(x) == x.reciprocal() == x.pow(-1)
    double hc, tc, hy, ty, C, c, U, u;
    C = 1.0 / hi;
    c = SPLIT * C;
    hc = c - C;
    u = SPLIT * hi;
    hc = c - hc; tc = C - hc; hy = u - hi; U = C * hi; hy = u - hy;
    ty = hi - hy;
    u = (((hc * hy - U) + hc * ty) + tc * hy) + tc * ty;
    c = ((((1.0 - U) - u)) - C * lo) / hi;

    double zhi = C + c;
    double zlo = (C - zhi) + c;
    return twoSum(zhi, zlo); }

  //-------------------------------------------------------------------
  // truncation and rounding
  //-------------------------------------------------------------------
//  /** Returns the largest (closest to positive infinity) value that is
//   * not greater than the argument and is equal to a mathematical
//   * integer. Special cases:
//   * <ul>
//   * <li>If this value is NaN, returns NaN.
//   * </ul>
//   *
//   * @return the largest (closest to positive infinity) value that is
//   * not greater than the argument and is equal to a mathematical
//   * integer.
//   */
//  public final DD floor () {
//    if (isNaN()) { return NaN; }
//    double fhi = Math.floor(hi);
//    double flo = 0.0;
//    // Hi is already integral.  Floor the low word
//    if (fhi == hi) { flo = Math.floor(lo); }
//    // do we need to renormalize here?
//    return sum(fhi, flo); }

//  /** Returns the smallest (closest to negative infinity) value that is
//   * not less than the argument and is equal to a mathematical integer.
//   * Special cases:
//   * <ul>
//   * <li>If this value is NaN, returns NaN.
//   * </ul>
//   *
//   * @return the smallest (closest to negative infinity) value that is
//   * not less than the argument and is equal to a mathematical integer.
//   */
//  public final DD ceil () {
//    if (isNaN()) { return NaN; }
//    double fhi = Math.ceil(hi);
//    double flo = 0.0;
//    // Hi is already integral.  Ceil the low word
//    // do we need to renormalize here?
//    if (fhi == hi) { flo = Math.ceil(lo); }
//    return sum(fhi, flo); }

  //-------------------------------------------------------------------
//  /** Rounds this value to the nearest integer. The value is rounded to
//   * an integer by adding 1/2 and taking the floor of the result.
//   * Special cases:
//   * <ul>
//   * <li>If this value is NaN, returns NaN.
//   * </ul>
//   *
//   * @return this value rounded to the nearest integer
//   */
//  public final DD rint () {
//    if (isNaN()) { return this; }
//    // may not be 100% correct
//    DD plus5 = this.add(0.5);
//    return plus5.floor(); }

  //-------------------------------------------------------------------
//  /** Returns the integer which is largest in absolute value and not
//   * further from zero than this value. Special cases:
//   * <ul>
//   * <li>If this value is NaN, returns NaN.
//   * </ul>
//   *
//   * @return the integer which is largest in absolute value and not
//   * further from zero than this value
//   */
//  public final DD trunc () {
//    if (isNaN()) { return NaN; }
//    if (isPositive()) { return floor(); }
//    else { return ceil(); } }

  //-------------------------------------------------------------------
//  /** Computes the determinant of the 2x2 matrix with the given entries.
//   *
//   * @param x1 a matrix entry
//   * @param y1 a matrix entry
//   * @param x2 a matrix entry
//   * @param y2 a matrix entry
//   *
//   * @return the determinant of the matrix of values
//   */
//  public static final DD determinant (final DD x1,
//                                      final DD y1,
//                                      final DD x2,
//                                      final DD y2) {
//    // TODO: expand to eliminate temp DD instances
//    return x1.multiply(y2).subtract(y1.multiply(x2)); }

//  /** Computes the determinant of the 2x2 matrix with the given entries.
//   *
//   * @param x1 a double value
//   * @param y1 a double value
//   * @param x2 a double value
//   * @param y2 a double value
//   *
//   * @return the determinant of the values
//   */
//  public static final DD determinant (final double x1,
//                                      final double y1,
//                                      final double x2,
//                                      final double y2) {
//    // TODO: expand to eliminate temp DD instances
//    return determinant(valueOf(x1), valueOf(y1),
//                       valueOf(x2), valueOf(y2)); }
//
  //-------------------------------------------------------------------
  // Ordering Functions
  //-------------------------------------------------------------------
  /** Computes the minimum of this and another DD number.
   *
   * @param x a DD number
   *
   * @return the minimum of the two numbers
   */
  public final Hilo min (final Hilo x) {
    if (le(x)) { return this; }
    else { return x; } }

  /** Computes the maximum of this and another DD number.
   *
   * @param x a DD number
   *
   * @return the maximum of the two numbers
   */
  public final Hilo max (final Hilo x) {
    if (ge(x)) { return this; }
    else { return x; } }

  //-------------------------------------------------------------------
  //  java.lang.Number
  //-------------------------------------------------------------------
  // TODO: just return hi?
  /** Converts this value to the nearest double-precision number.
   *
   * @return the nearest double-precision number to this value
   */
  public final double doubleValue () { return hi + lo; }

  // TODO: is this correct? return (float) hi? (float) hi + (float) lo?
  /** Converts this value to the nearest single precision number.
   *
   * @return the nearest float
   */
  public final float floatValue () { return (float) (hi + lo); }

  // TODO: is this correct? return (int) hi + (int) lo?
  // TODO: throw exception for NaN, infinity?
  /** Converts this value to the nearest integer.
   *
   * @return the nearest integer to this value
   */
  public final int intValue () { return (int) hi; }

  //-------------------------------------------------------------------
  // mop.java.numbers.predicates.Predicates
  //-------------------------------------------------------------------
  /** Tests whether this value is equal to 0.
   *
   * @return true if this value is equal to 0
   */
  public final boolean isZero () {
    return hi == 0.0 && lo == 0.0; }

  /** Tests whether this value is less than 0.
   *
   * @return true if this value is less than 0
   */
  public final boolean isNegative () {
    return hi < 0.0 || (hi == 0.0 && lo < 0.0); }
//
//  /** Tests whether this value is greater than 0.
//   *
//   * @return true if this value is greater than 0
//   */
//  public final boolean isPositive () {
//    return hi > 0.0 || (hi == 0.0 && lo > 0.0); }

  /** Tests whether this value is NaN.
   *
   * @return true if this value is NaN
   */
  public final boolean isNaN () {
    // TODO: what about lo? Constrain both to be NaN?
    return Double.isNaN(hi); }

  /** Tests whether this value is finite.
   *
   * @return true if this value is finite
   */
  public final boolean isFinite () {
    return Double.isFinite(hi) && Double.isFinite(lo); }

  /** Tests whether this value is finite.
   *
   * @return true if this value is finite
   */
  public final boolean isInfinite () {
    // TODO: what about lo? Constrain both to be same sign infinite?
    // TODO: assuming obeys constraint that abs(hi) > abs(lo)
    return Double.isInfinite(hi); }

  /** Tests whether this value is equal to another <tt>DD</tt>
   * value.
   *
   * @param y a DD value
   *
   * @return true if this value = y
   */
  public final boolean equals (final Hilo y) {
    return hi == y.hi && lo == y.lo; }

//  /** Tests whether this value is greater than another
//   * <tt>DD</tt> value.
//   *
//   * @param y a DD value
//   *
//   * @return true if this value &gt; y
//   */
//  public final boolean gt (final DD y) {
//    return (hi > y.hi) || (hi == y.hi && lo > y.lo); }

  /** Tests whether this value is greater than or equals to another
   * <tt>DD</tt> value.
   *
   * @param that a DD value
   *
   * @return true if this value &gt;= y
   */
  public final boolean ge (final Hilo that) {
    return (hi > that.hi) || (hi == that.hi && lo >= that.lo); }

//  /** Tests whether this value is less than another <tt>DD</tt>  value.
//   *
//   * @param y a DD value
//   *
//   * @return true if this value &lt; y
//   */
//  public final boolean lt (final DD y) {
//    return (hi < y.hi) || (hi == y.hi && lo < y.lo); }

  /** Tests whether this value is less than or equal to another
   * <tt>DD</tt> value.
   *
   * @param that a DD value
   *
   * @return true if this value &lt;= y
   */
  public final boolean le (final Hilo that) {
    return (hi < that.hi) || (hi == that.hi && lo <= that.lo); }

  /** Compares two DD objects numerically.
   *
   * @return -1, 0, or 1 depending on whether this value is less than,
   * equal to or greater than the value of <tt>o</tt>
   */
  @Override
  public final int compareTo (final @NonNull Object o) {
    final Hilo other = (Hilo) o;
    if (hi < other.hi) { return -1; }
    if (hi > other.hi) { return 1; }
    return Double.compare(lo, other.lo); }

  //-------------------------------------------------------------------
  // Output
  //-------------------------------------------------------------------
  /** Returns a string representation of this number, as 2 terms printed
   * by <code>Double.toHexString(double)</code>. This string
   * representation should be lossless.
   *
   * @return a string representation of this number
   */
  public final String toHexString () {
    return "Hilo("
      + Double.toHexString(hi) + " + "
      + Double.toHexString(lo) + ")"; }

  /** Returns a string representation of this number, as 2 terms printed
   * by <code>Double.toHexString(double)</code>. This string
   * representation should be lossless.
   *
   * @return a string representation of this number
   */
  public final @NonNull String toString () { return toHexString(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  /** A <code>DD</code>> uses a representation containing two double-precision
  * values. A number x is represented as a pair of doubles, x.hi and
  * x.lo, such that the number represented by x is x.hi + x.lo, where
  * <pre>
  *    |x.lo| &lt;= 0.5 * ulp(x.hi)
  * </pre>
  * and ulp(y) means "unit in the last place of y". The basic
  * arithmetic
  * operations are implemented using convenient properties of IEEE-754
  * floating-point arithmetic.
   */

  private final boolean checkUlp (final double s, final double t) {
    // reverse test to handle NaN
    return ! ((2*Math.abs(t)) > Math.ulp(s)); }

  /** Enforce <pre>hi = a + b</pre>, <pre>lo = hi - (a + b)</pre>
   * so that <pre>|lo| &lt;= 0.5 * ulp(hi)</pre>.
   * @see <a href="https://en.wikipedia.org/wiki/2Sum"Fast2Sum</a>
   */
  public Hilo {
    assert checkUlp(hi, lo) :
      "\nLow order term too large:" +
        "\nhi= " + Double.toHexString(hi) +
        "\nlo= " + Double.toHexString(lo) +
        "\nulp(hi)= " + Double.toHexString(Math.ulp(hi));
  }

  public static final Hilo twoSum (final double a, final double b) {
    final double x = (a + b);
    //Two_Sum_Tail(a, b, x, y);
    final double bvirt = (x - a);
    final double avirt = x - bvirt;
    final double bround = b - bvirt;
    final double around = a - avirt;
    final double y = around + bround;
    return new Hilo(x,y); }

  public static final Hilo fastTwoSum (final double a,
                                       final double b) {
    final double x = (a + b);
    //Fast_Two_Sum_Tail(a, b, x, y)
    final double bvirt = x - a;
    final double y = b - bvirt;
    return new Hilo(x, y);
  }

  public static final Hilo valueOf (final double a) {
    return new Hilo(a, 0.0);  }

  public static final Hilo valueOf (final float a) {
    return new Hilo(a, 0.0);  }


  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
