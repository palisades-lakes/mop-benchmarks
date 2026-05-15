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
 *
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
 *   <li>single construction path to enforce hi,lo constraints
 *   <li> TODO: distinguish positive and negative zero?
 * </ul>
 *
 */

//@SuppressWarnings("unused")
public final class DD0 implements Serializable, Comparable {

  //--------------------------------------------------------------------
  // class singletons
  //--------------------------------------------------------------------

  public static final DD0 NaN = new DD0(Double.NaN, Double.NaN);

  public static final DD0 POSITIVE_INFINITY =
    new DD0(Double.POSITIVE_INFINITY, 0.0);

  public static final DD0 NEGATIVE_INFINITY =
    new DD0(Double.NEGATIVE_INFINITY, 0.0);

  /** additive identity
   */
  public static final DD0 ZERO = sum(0.0, 0.0);

  /** multiplicative identity
   */
  public static final DD0 ONE = sum(1.0, 0.0);

  // TODO: re-write constants with hex strings
//  /**  The value nearest to the constant Pi.
//   */
//  public static final DD PI = sum(
//    3.141592653589793116e+00,
//    1.224646799147353207e-16);
//
//  /** The value nearest to the constant 2 * Pi.
//   */
//  public static final DD TWO_PI = sum(
//    6.283185307179586232e+00,
//    2.449293598294706414e-16);
//
//  /** The value nearest to the constant Pi / 2.
//   */
//  public static final DD PI_2 = sum(
//    1.570796326794896558e+00,
//    6.123233995736766036e-17);
//
//  /** The value nearest to the constant e (the natural logarithm base).
//   */
//  public static final DD E = sum(
//    2.718281828459045091e+00,
//    1.445646891729250158e-16);

  // TODO: verify this, use hex string
//  /** The smallest representable relative difference between two
//   * {link @DD} values
//   */
  //public static final double EPS = 1.23259516440783e-32;  /* = 2^-106 */
  //public static final double EPS = 0x1.0p-106;

  //--------------------------------------------------------------------
  // instance slots
  //--------------------------------------------------------------------
  /** The high-order component of the double-double precision value.
   */
  private final double hi;
  //public final double getHi() { return hi; }

  /** The low-order component of the double-double precision value.
   */
  private final double lo;
  //public final double getLo() { return lo; }

  //--------------------------------------------------------------------
  // instance methods
  //-------------------------------------------------------------------
  /** Returns an integer indicating the sign of this value.
   * <ul>
   * <li>if this value is &gt; 0, returns 1
   * <li>if this value is &lt; 0, returns -1
   * <li>if this value is = 0, returns 0
   * <li>if this value is NaN, returns 0
   * </ul>
   *
   * @return an integer indicating the sign of this value
   */
  public final int signum () {
    if (hi > 0) { return 1; }
    if (hi < 0) { return -1; }
    if (lo > 0) { return 1; }
    if (lo < 0) { return -1; }
    return 0; }

  //--------------------------------------------------------------------
  /** Returns the absolute value of this value. Special cases:
   * <ul>
   * <li>If this value is NaN, it is returned.
   * </ul>
   *
   * @return the absolute value of this value
   */
  public final DD0 abs () {
    if (isNaN()) { return NaN; }
    if (isNegative()) { return sum(-hi,-lo); }
    return this; }

  //--------------------------------------------------------------------
  // arithmetic
  //--------------------------------------------------------------------
  // TODO: check Shewchuk, etc., and add references.

  /** @return <tt>(this + y)</tt>
   */
  public final DD0 add (final double y) {
    final double S = hi + y;
    final double e = S - hi;
    double s = S - e;
    s = (y - e) + (hi - s);
    final double f = s + lo;
    final double H = S + f;
    final double h = f + (S - H);
    return sum(H + h, h + (H - hi)); }

  private final DD0 add (final double yhi, double ylo) {
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
    return sum(zhi, zlo); }

  /** @return <tt>(this + y)</tt>
   */
  public final DD0 add (final DD0 y) {
    return add(y.hi,y.lo); }

  /** @return <tt>(this - y)</tt>
   */
  public final DD0 subtract (final DD0 y) { return add(-y.hi, -y.lo); }

  /** @return <tt>(this - y)</tt>
   */
  public final DD0 subtract (final double y) { return add(-y); }

  /** @return <tt>(this - y)</tt>
   */
  public final DD0 negate () {
    // TODO: sum probably not necessary
    return sum(-hi,-lo); }

  //-------------------------------------------------------------------
  // multiplication
  //-------------------------------------------------------------------
  /** The value to split a double-precision value on during
   * multiplication.
   * TODO: should this be an int?<br>
   * Use hex string for clarity
   */
  private static final double SPLIT = 1.0 + 0x1.0p27;

  public final DD0 multiply (final double yhi, final double ylo) {
    // TODO: check whether all these edge cases are necessary
    if (ZERO.equals(this)) { return ZERO; }
    if ((0.0==yhi) && (0.0==ylo)) { return ZERO; }
    if (ONE.equals(this)) { return sum(yhi, ylo); }
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
    return sum(zhi, zlo); }

  /** @return <tt>(this * y)</tt>
   */
  public final DD0 multiply (final DD0 that) {
    return multiply(that.hi, that.lo); }

  /** @return <tt>(this * y)</tt>
   */
  public final DD0 multiply (final double y) {
    // TODO: optimize simple double case
    return multiply(y, 0.0); }

  //-------------------------------------------------------------------
//  /** @return the square of this value.
//   */
//  public final DD sqr () {
//    // TODO: optimized version of multiply
//    // TODO: unit test x.sqr() == x.multiply(x) == x.pow(2)
//    return multiply(this); }

//  /** @return the square of this value.
//   */
//  public static final DD sqr (final double z) {
//    // TODO: optimized version of multiply
//    return valueOf(z).multiply(z); }

  //-------------------------------------------------------------------
//  /** * Computes the positive square root of this value. If the number is
//   * NaN or negative, NaN is returned.
//   *
//   * @return the positive square root of this number. If the argument is
//   * NaN or less than zero, the result is NaN.
//   */
//  public final DD sqrt () {
//    /* Strategy:  Use Karp's trick:  if x is an approximation
//    to sqrt(a), then
//
//       sqrt(a) = a*x + [a - (a*x)^2] * x / 2   (approx)
//
//    The approximation is accurate to twice the accuracy of x.
//    Also, the multiplication (a*x) and [-]*x can be done with
//    only half the precision.
// */
//
//    if (isZero()) { return ZERO; }
//    if (isNegative()) { return NaN; }
//
//    double x = 1.0 / Math.sqrt(hi);
//    double ax = hi * x;
//    DD axdd = valueOf(ax);
//    DD diffSq = subtract(axdd.sqr());
//    double d2 = diffSq.hi * (x * 0.5);
//
//    return axdd.add(d2); }

//  public static final DD sqrt (final double x) {
//    // TODO: optimize instance creation
//    return valueOf(x).sqrt(); }

  //-------------------------------------------------------------------
//  /** Computes the value of this number raised to an integral power.
//   * Follows semantics of Java Math.pow as closely as possible.
//   *
//   * @param exp the integer exponent
//   *
//   * @return x raised to the integral power exp
//   */
//  public final DD pow (final int exp) {
//    // TODO: optimize out tmp instances
//    // TODO: unit test reciprocal() == pow(-1)
//    // See java.lang.Math.pow(double,double)
//    if (0 == exp) { return ONE; }
//    if (1 == exp) { return this; }
//    if (isNaN()) { return NaN; }
//    // TODO: distinguish positive and negative zero cases to match
//    // java.lang.Math.pow(double,double)
//    if (isZero()) {
//      // TODO: return immutable this
//      if (0 < exp) { return this; }
//      // TODO: return immutable singleton
//      return POSITIVE_INFINITY; }
//
//    // TODO: use mutable instances
//    DD r = this;
//    DD s = ONE;
//    int n = Math.abs(exp);
//
//    if (n > 1) {
//      // Use binary exponentiation
//      while (n > 0) {
//        if (n % 2 == 1) { s = s.multiply(r); }
//        n /= 2;
//        if (n > 0) { r = r.sqr(); } } }
//    else { s = r; }
//
//    /* Compute the reciprocal if n is negative. */
//    if (exp < 0) { s = s.reciprocal(); }
//    assert (!s.isNaN()) : "NaN:" + s.toHexString();
//    return s; }

  //-------------------------------------------------------------------
  // division
  //-------------------------------------------------------------------

   public final DD0 divide (final double yhi, final double ylo) {
     double hc, tc, hy, ty, C, c, U, u;
     C = hi / yhi; c = SPLIT * C; hc = c - C; u = SPLIT * yhi;
     hc = c - hc;
     tc = C - hc; hy = u - yhi; U = C * yhi; hy = u - hy;
     ty = yhi - hy;
     u = (((hc * hy - U) + hc * ty) + tc * hy) + tc * ty;
     c = ((((hi - U) - u) + lo) - C * ylo) / yhi;
     u = C + c;
     return sum(u,  (C - u) + c); }

  /** Computes a new DD whose value is <tt>(this / y)</tt>.
   *
   * @param y the divisor
   *
   * @return a new object with the value <tt>(this / y)</tt>
   */
  public final DD0 divide (final DD0 y) {
   return divide(y.hi, y.lo); }

  /** Computes a new DD whose value is <tt>(this / y)</tt>.
   *
   * @param y the divisor
   *
   * @return a new object with the value <tt>(this / y)</tt>
   */
  public final DD0 divide (final double y) {
    if (Double.isNaN(y)) { return NaN; }
    // TODO: optimize single double case
    return divide(y, 0.0); }

  //-------------------------------------------------------------------
  /** Returns a DD whose value is  <tt>1 / this</tt>.
   *
   * @return the reciprocal of this value
   */
  public final DD0 reciprocal () {
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
    return sum(zhi, zlo); }

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
  public final DD0 min (final DD0 x) {
    if (le(x)) { return this; }
    else { return x; } }

  /** Computes the maximum of this and another DD number.
   *
   * @param x a DD number
   *
   * @return the maximum of the two numbers
   */
  public final DD0 max (final DD0 x) {
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
  public final boolean equals (final DD0 y) {
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
  public final boolean ge (final DD0 that) {
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
  public final boolean le (final DD0 that) {
    return (hi < that.hi) || (hi == that.hi && lo <= that.lo); }

  /** Compares two DD objects numerically.
   *
   * @return -1, 0, or 1 depending on whether this value is less than,
   * equal to or greater than the value of <tt>o</tt>
   */
  @Override
  public final int compareTo (final @NonNull Object o) {
    final DD0 other = (DD0) o;
    if (hi < other.hi) { return -1; }
    if (hi > other.hi) { return 1; }
    return Double.compare(lo, other.lo); }

  //-------------------------------------------------------------------
  // Output
  //-------------------------------------------------------------------
  // Removed methods related to DD <-> decimal string.
  // <br>
  // DD to decimal string methods are broken in a number of ways,
  // at least for numbers
  // in the vicinity of 0x1.0p-976, 0x1.0p-977, etc.
  // For <hi,0.0> values, decimal string incorrectly has non-zero low
  // order decimal digits.
  // For some values, returns an incorrect string of all '0' digits,
  // throwing an exception. Seems to be related to
  // <code>TEN.pow(mag)</code> returning <code>NaN</code>, which may
  // reflect a problem with <code<pow</code> that should be addressed
  // independently.
  // <br>
  // The problems seem to be expected to some degree, given the comments
  // in the code.
  // For my purposes, approximating a number with a decimal string is
  // rarely useful. I'll just remove this code for now.
  // The <code>toString</code> printed representation will display
  // the 2 terms separately, using default Java decimal formatting.
  // And I will add <code>toHexString</code>, which I find more useful
  // for debugging.
  // <br>
  // In case I do decide to try a 1-term decimal representation:
  // <br>
  // References:  (palisades dot lakes at gmail dot com, 2026-05-08)
  // <ul>
  //   <li>Russ Cox,
  //   <a href="https://research.swtch.com/ftoa">
  //     Floating Point to Decimal Conversion is Easy</a>, 2011
  // <li>William Clinger,
  // How to Read Floating Point Numbers Accurately, PLDI 1990.
  // <li>Guy L. Steele Jr. and Jon L. White,
  // How to Print Floating Point Numbers Accurately, PLDI 1990.
  // <li>David M. Gay,
  // Correctly Rounded Binary-Decimal and Decimal-Binary Conversions,
  // AT&T Bell Laboratories, 1990.
  // <li>Vern Paxson,
  // A Program for Testing IEEE Decimal-Binary Conversion, May 1991.
  // <li>Robert Burger and R. Kent Dybvig,
  // Printing Floating-Point Numbers Quickly and Accurately,
  // SIGPLAN 1996.
  // <li>Florian Loitsch,
  // &ldquo;<a href="http://florian.loitsch.com/publications/dtoa-pldi2010.pdf">
  //   Printing Floating-Numbers Quickly and Accurately With Integers
  //   </a>&rdquo;,
  //   PLDI 2010.
  // <li>Aubrey Jaffer, <a href="https://arxiv.org/pdf/1310.8121">
  //   Easy Accurate Reading and Writing of Floating-Point Numbers</a>,
  //   2018
  // </ul>

//  /** Returns a string representation of this number, as 2 terms printed
//   * by <code>Double.toString(double)</code>.
//   *
//   * @return a string representation of this number
//   */
//  public final String toDecimalString () {
//    return "DD<" + hi + " + " + lo + ">"; }

  /** Returns a string representation of this number, as 2 terms printed
   * by <code>Double.toHexString(double)</code>. This string
   * representation should be lossless.
   *
   * @return a string representation of this number
   */
  public final String toHexString () {
    return "DD<"
      + Double.toHexString(hi) + " + "
      + Double.toHexString(lo) + ">"; }

  /** Returns a string representation of this number, as 2 terms printed
   * by <code>Double.toHexString(double)</code>. This string
   * representation should be lossless.
   *
   * @return a string representation of this number
   */
  public final String toString () { return toHexString(); }

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
  private DD0 (final double hi, final double lo) {
    assert checkUlp(hi, lo) :
      "\nLow order term too large:"  +
        "\nhi= " + Double.toHexString(hi) +
        "\nlo= " + Double.toHexString(lo) +
        "\nulp(hi)= " + Double.toHexString(Math.ulp(hi));
        this.hi = hi;this.lo = lo; }

  // TODO: benchmark twoSum vs fast2Sum
  // TODO: where can we use fast2Sum without magnitude test?
  // TODO: change magnitude test to exponent test? (Lange and Oishi 2020)
  // https://link.springer.com/article/10.1007/s00211-020-01114-2
//  private static final DD fast2Sum (final double a, final double b) {
//    // not so fast with magnitude test!
//    if (Math.abs(a) < Math.abs(b)) { return fast2Sum(b, a); }
//    final double hi = a+b;
//    final double delta = hi-a;
//    final double lo = b-delta;
//    return new DD(hi, lo);  }

  private static final DD0 twoSum (final double a, final double b) {
    final double hi = a+b;
    final double delta = hi-a;
    final double lo = (a-(hi-delta)) + (b-delta);
    return new DD0(hi, lo);  }

  /** @link mop.java.accumulators.ZhuHayesAccumulator#twoSum
   * <br>
   * See <a href="https://pavpanchekha.com/blog/fast-two-sum.html>fast two sum</a>
   * <br>
   * See <a href="https://en.wikipedia.org/wiki/2Sum>2Sum</a>
   */
  public static final DD0 sum (final double a, final double b) {
    return twoSum(a, b); }

  public static final DD0 valueOf (final double a) {
    return new DD0(a, 0.0);  }

  public static final DD0 valueOf (final float a) {
    return new DD0(a, 0.0);  }


  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
