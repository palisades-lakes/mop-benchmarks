/*
 * palisades dot lakes at gmail dot com, 2026-05-05
 * modified from JTS for benchmarking relative to mop.
 * TODO: Split into immutable and mutable classes.
 *  TODO: distinguish positive and negative zero?
 * JTS version has some additions (determinant, correction (?)
 * to parse(String)) relative to Tinfour version, but otherwise
 * identical, except for comment formatting.
 */
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

import mop.java.accumulators.ZhuHayesAccumulator;

import java.io.Serializable;

/**
 * Implements extended-precision floating-point numbers which maintain
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
 *     DD a = new DD(2.0);
 *     DD b = new DD(3.0);
 *     DD c = a.add(b);
 * </pre>
 * In contrast, the following approach uses only one object:
 * <pre>
 *     DD a = new DD(2.0);
 *     a.selfAdd(3.0);
 * </pre>
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
 */
public final class DD implements Serializable, Comparable, Cloneable {

  //--------------------------------------------------------------------
  // class singletons
  // TODO: immutable!
  //--------------------------------------------------------------------
  /**  The value nearest to the constant Pi.
   */
  public static final DD PI = new DD(
    3.141592653589793116e+00,
    1.224646799147353207e-16);

  /** The value nearest to the constant 2 * Pi.
   */
  public static final DD TWO_PI = new DD(
    6.283185307179586232e+00,
    2.449293598294706414e-16);

  /** The value nearest to the constant Pi / 2.
   */
  public static final DD PI_2 = new DD(
    1.570796326794896558e+00,
    6.123233995736766036e-17);

  /** The value nearest to the constant e (the natural logarithm base).
   */
  public static final DD E = new DD(
    2.718281828459045091e+00,
    1.445646891729250158e-16);

  // TODO: move to construction section of file

  private static final DD createNaN () {
    return new DD(Double.NaN, Double.NaN); }

  /** A value representing the result of an operation which does not
   * return a valid number.
   */
  public static final DD NaN = createNaN();

  /** The smallest representable relative difference between two
   * {link @DD} values
   */
  public static final double EPS = 1.23259516440783e-32;  /* = 2^-106 */

  //--------------------------------------------------------------------
  // class methods
  //--------------------------------------------------------------------
  /** Converts the <tt>double</tt> argument to a DD number.
   *
   * @param x a numeric value
   *
   * @return the extended precision version of the value
   */
  public static final DD valueOf (final double x) { return new DD(x); }

  /** Converts the <tt>double</tt> argument to a DD number.
   *
   * @param x a numeric value
   *
   * @return the extended precision version of the value
   */
  public static final DD valueOf (float x) { return new DD(x); }

  //--------------------------------------------------------------------
  // instance slots
  // TODO: immutable, access methods
  //--------------------------------------------------------------------
  /** The high-order component of the double-double precision value.
   */
  private double hi = 0.0;

  /** The low-order component of the double-double precision value.
   */
  private double lo = 0.0;

  //--------------------------------------------------------------------
  // instance methods
  //--------------------------------------------------------------------
  // TODO: eliminate mutating methods
  //--------------------------------------------------------------------
  /** Set the value for the DD object. This method supports the mutating
   * operations concept described in the class documentation (see
   * above).
   *
   * @param value a DD instance supplying an extended-precision value.
   *
   * @return a self-reference to the DD instance.
   */
  public final DD setValue (final DD value) {
    init(value.hi, value.lo);
    return this; }

  /** Set the value for the DD object. This method supports the mutating
   * operations concept described in the class documentation (see
   * above).
   *
   * @param value a floating point value to be stored in the instance.
   *
   * @return a self-reference to the DD instance.
   */
  public final DD setValue (final double value) {
    init(value,0.0);
    return this; }

  //--------------------------------------------------------------------
  // arithmetic
  //--------------------------------------------------------------------

  /** Returns a new DD whose value is <tt>(this + y)</tt>.
   *
   * @param y the addend
   *
   * @return <tt>(this + y)</tt>
   */
  public final DD add (final DD y) {
    return copy(this).selfAdd(y); }

  /** Returns a new DD whose value is <tt>(this + y)</tt>.
   *
   * @param y the addend
   *
   * @return <tt>(this + y)</tt>
   */
  public final DD add (final double y) {
    return copy(this).selfAdd(y);
  }

  /** Adds the argument to the value of <tt>this</tt>. To prevent
   * altering constants, this method <b>must only</b> be used on values
   * known to be newly created.
   *
   * @param y the addend
   *
   * @return this object, increased by y
   */
  public final DD selfAdd (final DD y) {
    return selfAdd(y.hi, y.lo);
  }

  /** Adds the argument to the value of <tt>this</tt>. To prevent
   * altering constants, this method <b>must only</b> be used on values
   * known to be newly created.
   *
   * @param y the addend
   *
   * @return this object, increased by y
   */
  public final DD selfAdd (final double y) {
    double H, h, S, s, e, f;
    S = hi + y;
    e = S - hi;
    s = S - e;
    s = (y - e) + (hi - s);
    f = s + lo;
    H = S + f;
    h = f + (S - H);
    hi = H + h;
    lo = h + (H - hi);
    return this;  }

  private final DD selfAdd (final double yhi, double ylo) {
    double H, h, T, t, S, s, e, f;
    S = hi + yhi;
    T = lo + ylo;
    e = S - hi;
    f = T - lo;
    s = S - e;
    t = T - f;
    s = (yhi - e) + (hi - s);
    t = (ylo - f) + (lo - t);
    e = s + T; H = S + e; h = e + (S - H); e = t + h;

    double zhi = H + e;
    double zlo = e + (H - zhi);
    hi = zhi;
    lo = zlo;
    return this;
  }

  /** Computes a new DD object whose value is <tt>(this -
   * y)</tt>.
   *
   * @param y the subtrahend
   *
   * @return <tt>(this - y)</tt>
   */
  public final DD subtract (final DD y) { return add(y.negate()); }

  /** Computes a new DD object whose value is <tt>(this -
   * y)</tt>.
   *
   * @param y the subtrahend
   *
   * @return <tt>(this - y)</tt>
   */
  public final DD subtract (final double y) { return add(-y); }

  /** Subtracts the argument from the value of <tt>this</tt>. To prevent
   * altering constants, this method <b>must only</b> be used on values
   * known to be newly created.
   *
   * @param y the addend
   *
   * @return this object, decreased by y
   */
  public final DD selfSubtract (final DD y) {
    if (isNaN()) { return this; }
    return selfAdd(-y.hi, -y.lo); }

  /** Subtracts the argument from the value of <tt>this</tt>. To prevent
   * altering constants, this method <b>must only</b> be used on values
   * known to be newly created.
   *
   * @param y the addend
   *
   * @return this object, decreased by y
   */
  public final DD selfSubtract (final double y) {
    if (isNaN()) { return this; }
    return selfAdd(-y, 0.0); }

  /** Returns a new DD whose value is <tt>-this</tt>.
   *
   * @return <tt>-this</tt>
   */
  public final DD negate () {
    if (isNaN()) { return this; }
    return new DD(-hi, -lo); }

  //-------------------------------------------------------------------
  // multiplication
  //-------------------------------------------------------------------
  /** The value to split a double-precision value on during
   * multiplication.
   * use a hex string for clarity
   */
  private static final double SPLIT = 1.0 + 0x1.0p27;
//  private static final double SPLIT = 134217729.0D;
  // 2^27+1, for IEEE double

  private final DD selfMultiply (final double yhi, final double ylo) {
    // TODO: danger until immutable!
    if (isNaN()) { return NaN; }
    if (Double.isNaN(yhi)) { return NaN; }
    // TODO: shouldn't be possible!
    if (Double.isNaN(ylo)) { return NaN; }
    final double hiTest = hi * yhi;
    // TODO: is this right? safe to ignore lo and ylo?
    // if so, then check sign and return POSITIVE or NEGATIVE INFINITY
    // singletons.
    if (Double.isInfinite(hiTest)) { return new DD(hiTest); }
    double C = SPLIT * hi;
    assert Double.isFinite(C);
    double hx = C - hi;
    double c = SPLIT * yhi;
    hx = C - hx;
    double tx = hi - hx;
    double hy = c - yhi;
    C = hi * yhi;
    hy = c - hy;
    assert Double.isFinite(hy);
    final double ty = yhi - hy;
    assert Double.isFinite(ty);
    c = hx * hy;
    assert Double.isFinite(c) :
      toHexString() +
        "\n * \n" +
        "<" + Double.toHexString(yhi) + " + " + Double.toHexString(ylo) + ">\n"
       + Double.toHexString(hx) + " * " + Double.toHexString(hy);
    c -= C;
    assert Double.isFinite(c) :
      "C=" + Double.toHexString(C);
    c += hx * ty;
    assert (! Double.isNaN(c));
    c += tx * hy;
    assert (! Double.isNaN(c));
    c += tx * ty;
    assert (! Double.isNaN(c));
    c += (hi * ylo + lo * yhi);
    assert (! Double.isNaN(C));
    assert (! Double.isNaN(c));
    final double zhi = C + c;
    hx = C - zhi;
    final double zlo = c + hx;
    assert (! Double.isNaN(zhi));
    assert (! Double.isNaN(zlo));
    hi = zhi;
    lo = zlo;
    return this; }

  /** Multiplies this object by the argument, returning <tt>this</tt>.
   * To prevent altering constants, this method <b>must only</b> be
   * used on values known to be newly created.
   *
   * @param y the value to multiply by
   *
   * @return this object, multiplied by y
   */
  public final DD selfMultiply (final DD y) {
    return selfMultiply(y.hi, y.lo); }

  /** Multiplies this object by the argument, returning <tt>this</tt>.
   * To prevent altering constants, this method <b>must only</b> be
   * used on values known to be newly created.
   *
   * @param y the value to multiply by
   *
   * @return this object, multiplied by y
   */
  public final DD selfMultiply (final double y) {
    return selfMultiply(y, 0.0); }

  /** Returns a new DD whose value is <tt>(this * y)</tt>.
   *
   * @param y the multiplicand
   *
   * @return <tt>(this * y)</tt>
   */
  public final DD multiply (final DD y) {
    return copy(this).selfMultiply(y); }

  /** Returns a new DD whose value is <tt>(this * y)</tt>.
   *
   * @param y the multiplicand
   *
   * @return <tt>(this * y)</tt>
   */
  public final DD multiply (final double y) {
    if (Double.isNaN(y)) { return createNaN(); }
    return copy(this).selfMultiply(y, 0.0); }

  //-------------------------------------------------------------------
  // division
  //-------------------------------------------------------------------
  /** Computes a new DD whose value is <tt>(this / y)</tt>.
   *
   * @param y the divisor
   *
   * @return a new object with the value <tt>(this / y)</tt>
   */
  public final DD divide (final DD y) {
    double hc, tc, hy, ty, C, c, U, u;
    C = hi / y.hi; c = SPLIT * C; hc = c - C; u = SPLIT * y.hi;
    hc = c - hc;
    tc = C - hc; hy = u - y.hi; U = C * y.hi; hy = u - hy;
    ty = y.hi - hy;
    u = (((hc * hy - U) + hc * ty) + tc * hy) + tc * ty;
    c = ((((hi - U) - u) + lo) - C * y.lo) / y.hi;
    u = C + c;

    double zhi = u;
    double zlo = (C - u) + c;
    return new DD(zhi, zlo); }

  /** Computes a new DD whose value is <tt>(this / y)</tt>.
   *
   * @param y the divisor
   *
   * @return a new object with the value <tt>(this / y)</tt>
   */
  public final DD divide (final double y) {
    if (Double.isNaN(y)) { return createNaN(); }
    return copy(this).selfDivide(y, 0.0); }

  /** Divides this object by the argument, returning <tt>this</tt>. To
   * prevent altering constants, this method <b>must only</b> be used on
   * values known to be newly created.
   *
   * @param y the value to divide by
   *
   * @return this object, divided by y
   */
  public final DD selfDivide (final DD y) {
    return selfDivide(y.hi, y.lo); }

  /** Divides this object by the argument, returning <tt>this</tt>. To
   * prevent altering constants, this method <b>must only</b> be used on
   * values known to be newly created.
   *
   * @param y the value to divide by
   *
   * @return this object, divided by y
   */
  public final DD selfDivide (final double y) {
    return selfDivide(y, 0.0); }

  private final DD selfDivide (final double yhi, double ylo) {
    double hc, tc, hy, ty, C, c, U, u;
    C = hi / yhi; c = SPLIT * C; hc = c - C; u = SPLIT * yhi;
    hc = c - hc;
    tc = C - hc; hy = u - yhi; U = C * yhi; hy = u - hy; ty = yhi - hy;
    u = (((hc * hy - U) + hc * ty) + tc * hy) + tc * ty;
    c = ((((hi - U) - u) + lo) - C * ylo) / yhi;
    u = C + c;

    hi = u;
    lo = (C - u) + c;
    return this; }

  /** Returns a DD whose value is  <tt>1 / this</tt>.
   *
   * @return the reciprocal of this value
   */
  public final DD reciprocal () {
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
    return new DD(zhi, zlo); }

  /** Returns the largest (closest to positive infinity) value that is
   * not greater than the argument and is equal to a mathematical
   * integer. Special cases:
   * <ul>
   * <li>If this value is NaN, returns NaN.
   * </ul>
   *
   * @return the largest (closest to positive infinity) value that is
   * not greater than the argument and is equal to a mathematical
   * integer.
   */
  public final DD floor () {
    if (isNaN()) { return NaN; }
    double fhi = Math.floor(hi);
    double flo = 0.0;
    // Hi is already integral.  Floor the low word
    if (fhi == hi) { flo = Math.floor(lo); }
    // do we need to renormalize here?
    return new DD(fhi, flo); }

  /** Returns the smallest (closest to negative infinity) value that is
   * not less than the argument and is equal to a mathematical integer.
   * Special cases:
   * <ul>
   * <li>If this value is NaN, returns NaN.
   * </ul>
   *
   * @return the smallest (closest to negative infinity) value that is
   * not less than the argument and is equal to a mathematical integer.
   */
  public final DD ceil () {
    if (isNaN()) { return NaN; }
    double fhi = Math.ceil(hi);
    double flo = 0.0;
    // Hi is already integral.  Ceil the low word
    // do we need to renormalize here?
    if (fhi == hi) { flo = Math.ceil(lo); }
    return new DD(fhi, flo); }

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

  /** Rounds this value to the nearest integer. The value is rounded to
   * an integer by adding 1/2 and taking the floor of the result.
   * Special cases:
   * <ul>
   * <li>If this value is NaN, returns NaN.
   * </ul>
   *
   * @return this value rounded to the nearest integer
   */
  public final DD rint () {
    if (isNaN()) { return this; }
    // may not be 100% correct
    DD plus5 = this.add(0.5);
    return plus5.floor(); }

  /** Returns the integer which is largest in absolute value and not
   * further from zero than this value. Special cases:
   * <ul>
   * <li>If this value is NaN, returns NaN.
   * </ul>
   *
   * @return the integer which is largest in absolute value and not
   * further from zero than this value
   */
  public final DD trunc () {
    if (isNaN()) { return NaN; }
    if (isPositive()) { return floor(); }
    else { return ceil(); } }

  /** Returns the absolute value of this value. Special cases:
   * <ul>
   * <li>If this value is NaN, it is returned.
   * </ul>
   *
   * @return the absolute value of this value
   */
  public final DD abs () {
    if (isNaN()) { return NaN; }
    if (isNegative()) { return negate(); }
    return new DD(this); }

  /** Computes the square of this value.
   *
   * @return the square of this value.
   */
  public final DD sqr () { return this.multiply(this); }

  /** Squares this object. To prevent altering constants, this method
   * <b>must only</b> be used on values known to be newly created.
   *
   * @return the square of this value.
   */
  public final DD selfSqr () { return this.selfMultiply(this); }

  /** Computes the square of this value.
   *
   * @return the square of this value.
   */
  public static final DD sqr (final double x) {
    return valueOf(x).selfMultiply(x); }

  /**
   * Computes the positive square root of this value. If the number is
   * NaN or negative, NaN is returned.
   *
   * @return the positive square root of this number. If the argument is
   * NaN or less than zero, the result is NaN.
   */
  public final DD sqrt () {
    /* Strategy:  Use Karp's trick:  if x is an approximation
    to sqrt(a), then

       sqrt(a) = a*x + [a - (a*x)^2] * x / 2   (approx)

    The approximation is accurate to twice the accuracy of x.
    Also, the multiplication (a*x) and [-]*x can be done with
    only half the precision.
 */

    if (isZero()) { return valueOf(0.0); }
    if (isNegative()) { return NaN; }

    double x = 1.0 / Math.sqrt(hi);
    double ax = hi * x;
    DD axdd = valueOf(ax);
    DD diffSq = this.subtract(axdd.sqr());
    double d2 = diffSq.hi * (x * 0.5);

    return axdd.add(d2); }

  public static final DD sqrt (final double x) {
    return valueOf(x).sqrt(); }

  /** Computes the value of this number raised to an integral power.
   * Follows semantics of Java Math.pow as closely as possible.
   *
   * @param exp the integer exponent
   *
   * @return x raised to the integral power exp
   */
  public final DD pow (final int exp) {

    // See java.lang.Math.pow(double,double)
    // TODO: return immutable singleton
    if (0 == exp) { return valueOf(1.0); }
    // TODO: return immutable this
    if (1 == exp) { return new DD(this); }
    // TODO: return immutable singleton
    if (isNaN()) { return createNaN(); }
    // TODO: distinguish positive and negative zero cases to match
    // java.lang.Math.pow(double,double)
    if (isZero()) {
      // TODO: return immutable this
      if (0 < exp) { return new DD(this); }
      // TODO: return immutable singleton
      return new DD(Double.POSITIVE_INFINITY); }

    // TODO: use mutable instances
    DD r = new DD(this);
    DD s = valueOf(1.0);
    int n = Math.abs(exp);

    if (n > 1) {
      // Use binary exponentiation
      while (n > 0) {
        if (n % 2 == 1) { s.selfMultiply(r); }
        n /= 2;
        if (n > 0) { r = r.sqr(); } } }
    else { s = r; }

    /* Compute the reciprocal if n is negative. */
    if (exp < 0) { s = s.reciprocal(); }
    assert (!s.isNaN()) : "NaN:" + s.toHexString();
    return s; }

  /** Computes the determinant of the 2x2 matrix with the given entries.
   *
   * @param x1 a matrix entry
   * @param y1 a matrix entry
   * @param x2 a matrix entry
   * @param y2 a matrix entry
   *
   * @return the determinant of the matrix of values
   */
  public static final DD determinant (final DD x1,
                                      final DD y1,
                                      final DD x2,
                                      final DD y2) {
    return x1.multiply(y2).selfSubtract(y1.multiply(x2)); }

  /** Computes the determinant of the 2x2 matrix with the given entries.
   *
   * @param x1 a double value
   * @param y1 a double value
   * @param x2 a double value
   * @param y2 a double value
   *
   * @return the determinant of the values
   */
  public static final DD determinant (final double x1,
                                      final double y1,
                                      final double x2,
                                      final double y2) {
    return determinant(valueOf(x1), valueOf(y1),
                       valueOf(x2), valueOf(y2)); }

  //-------------------------------------------------------------------
  // Ordering Functions
  //-------------------------------------------------------------------
  /** Computes the minimum of this and another DD number.
   *
   * @param x a DD number
   *
   * @return the minimum of the two numbers
   */
  public final DD min (final DD x) {
    if (this.le(x)) { return this; }
    else { return x; } }

  /** Computes the maximum of this and another DD number.
   *
   * @param x a DD number
   *
   * @return the maximum of the two numbers
   */
  public final DD max (final DD x) {
    if (this.ge(x)) { return this; }
    else { return x; } }

  //-------------------------------------------------------------------
  //  java.lang.Number
  //-------------------------------------------------------------------
  /** Converts this value to the nearest double-precision number.
   *
   * @return the nearest double-precision number to this value
   */
  public final double doubleValue () { return hi + lo; }

  // TODO: is this correct?
  /** Converts this value to the nearest single precision number.
   *
   * @return the nearest float
   */
  public final float floatValue () { return (float) (hi + lo); }

  /** Converts this value to the nearest integer.
   *
   * @return the nearest integer to this value
   */
  public final int intValue () { return (int) hi; }

  //-------------------------------------------------------------------
  // Predicates
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

  /** Tests whether this value is greater than 0.
   *
   * @return true if this value is greater than 0
   */
  public final boolean isPositive () {
    return hi > 0.0 || (hi == 0.0 && lo > 0.0); }

  /** Tests whether this value is NaN.
   *
   * @return true if this value is NaN
   */
  public final boolean isNaN () { return Double.isNaN(hi); }

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
    // TODO: assuming obeys constraint that abs(hi) > abs(lo)
    return Double.isInfinite(hi); }

  /** Tests whether this value is equal to another <tt>DD</tt>
   * value.
   *
   * @param y a DD value
   *
   * @return true if this value = y
   */
  public final boolean equals (final DD y) {
    return hi == y.hi && lo == y.lo; }

  /** Tests whether this value is greater than another
   * <tt>DD</tt> value.
   *
   * @param y a DD value
   *
   * @return true if this value &gt; y
   */
  public final boolean gt (final DD y) {
    return (hi > y.hi) || (hi == y.hi && lo > y.lo); }

  /** Tests whether this value is greater than or equals to another
   * <tt>DD</tt> value.
   *
   * @param y a DD value
   *
   * @return true if this value &gt;= y
   */
  public final boolean ge (final DD y) {
    return (hi > y.hi) || (hi == y.hi && lo >= y.lo); }

  /** Tests whether this value is less than another <tt>DD</tt>  value.
   *
   * @param y a DD value
   *
   * @return true if this value &lt; y
   */
  public final boolean lt (final DD y) {
    return (hi < y.hi) || (hi == y.hi && lo < y.lo); }

  /** Tests whether this value is less than or equal to another
   * <tt>DD</tt> value.
   *
   * @param y a DD value
   *
   * @return true if this value &lt;= y
   */
  public final boolean le (final DD y) {
    return (hi < y.hi) || (hi == y.hi && lo <= y.lo); }

  /** Compares two DD objects numerically.
   *
   * @return -1,0 or 1 depending on whether this value is less than,
   * equal to or greater than the value of <tt>o</tt>
   */
  public final int compareTo (final Object o) {
    final DD other = (DD) o;
    if (hi < other.hi) { return -1; }
    if (hi > other.hi) { return 1; }
    return Double.compare(lo, other.lo); }

  //-------------------------------------------------------------------
  // Output
  //-------------------------------------------------------------------
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
  // TODO: hide constructors, only one constructor
  //--------------------------------------------------------------------
  // TODO: enforce constraint in single constructor.
  // Later: enforce constraint in mutating methods for mutable instances:
  //
  // A <code>DD</code>> uses a representation containing two double-precision
  // values. A number x is represented as a pair of doubles, x.hi and
  // x.lo, such that the number represented by x is x.hi + x.lo, where
  // <pre>
  //    |x.lo| &lt;= 0.5 * ulp(x.hi)
  // </pre>
  // and ulp(y) means "unit in the last place of y". The basic
  // arithmetic
  // operations are implemented using convenient properties of IEEE-754
  // floating-point arithmetic.

  /** Enforce <pre>hi = a + b</pre>, <pre>lo = hi - (a + b)</pre>
   * so that <pre>|lo| &lt;= 0.5 * ulp(hi)</pre>.
   * @see <a href="https://en.wikipedia.org/wiki/2Sum"Fast2Sum</a>
   */
  private final void init (final double a, final double b) {
    // TODO: any speed implications of earlier assignment to slots?
    final double s = a+b;
    final double z = s-a;
    final double t = b-z;
    hi = s; lo = t; }

  /** Creates a new DD with value (hi, lo).
   *
   * @param hi the high-order component
   * @param lo the high-order component
   */
  public DD (final double hi, final double lo) { init(hi, lo); }

//  /** Creates a new DD with value 0.0.
//   * TODO: replace with MutableDD.zero()
//   */
//  public DD () { this(0.0, 0.0); }

  /** Creates a new DD with value x.
   *
   * @param x the value to initialize
   */
  public DD (final double x) { this(x,0.0); }

  /** Creates a new DD with value equal to the argument.
   *
   * @param dd the value to initialize
   */
  public DD (final DD dd) { this(dd.hi,dd.lo); }

  /** Creates a new DD with the value of the argument.
   *
   * @param dd the DD value to copy
   *
   * @return a copy of the input value
   */
  public static final DD copy (final DD dd) { return new DD(dd); }

  /** Creates and returns a copy of this value.
   * @return a copy of this value
   */
  public final Object clone () {
    try { return super.clone(); }
    catch (final CloneNotSupportedException ex) {
      // should never reach here
      return null; } }

  //--------------------------------------------------------------------
  // palisades dot lakes at gmail dot com, 2026-05-05
  //
  // Removed methods related to DD <-> decimal string.
  //
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
  //
  // The problems seem to be expected to some degree, given the comments
  // in the code.
  // For my purposes, approximating a number with a decimal string is
  // rarely useful. I'll just remove this code for now.
  // The <code>toString</code> printed representation will display
  // the 2 terms separately, using default Java decimal formatting.
  // And I will add <code>toHexString</code>, which I find more useful
  // for debugging.
  //
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


  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
