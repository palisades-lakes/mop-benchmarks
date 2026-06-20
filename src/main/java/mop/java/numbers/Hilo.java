package mop.java.numbers;

import java.io.Serializable;

/** Minimal representation of the exact value of the sum or product
 * of 2 <code>double</code> values, another subset of the rationals.
 * <br>
 * Essentially the same as
 * <a href="https://github.com/locationtech/jts/blob/master/modules/core/src/main/java/org/locationtech/jts/math/DD.java">
 *  org.locationtech.jts.math.DD</a>
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-06-20
 */

public record Hilo (double hi, double lo)
  implements Serializable, Comparable<Hilo>, Ringlike<Hilo> {

  //-------------------------------------------------------------------
  //  java.lang.Double 'interface'
  //-------------------------------------------------------------------

  public static final Hilo NaN = new Hilo(Double.NaN, 0.0);

  public static final Hilo POSITIVE_INFINITY =
    new Hilo(Double.POSITIVE_INFINITY, 0.0);

  public static final Hilo NEGATIVE_INFINITY =
    new Hilo(Double.NEGATIVE_INFINITY, 0.0);

  public final boolean isNaN () {
    // TODO: what about lo? Constrain both to be NaN?
    return Double.isNaN(hi);
  }

  public final boolean isFinite () {
    return Double.isFinite(hi) && Double.isFinite(lo);
  }

  public final boolean isNegative () {
    return hi < 0.0 || (hi == 0.0 && lo < 0.0);
  }

  //--------------------------------------------------------------------
  // Ringlike addition
  //--------------------------------------------------------------------
  // TODO: check Shewchuk, etc., and add references to JTS and Shewchuk

  public final Hilo add (final double y) {
    final double S = hi + y;
    final double e = S - hi;
    double s = S - e;
    s = (y - e) + (hi - s);
    final double f = s + lo;
    final double H = S + f;
    final double h = f + (S - H);
    return twoSum(H + h, h + (H - hi));
  }

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
    return twoSum(zhi, zlo);
  }

  @Override
  public final Hilo add (final Hilo y) {
    return add(y.hi, y.lo);
  }

  //--------------------------------------------------------------------

  public final Hilo subtract (final double y) { return add(-y); }

  @Override
  public final Hilo subtract (final Hilo y) {
    return add(-y.hi, -y.lo);
  }

  //--------------------------------------------------------------------

  @Override
  public final Hilo negate () {
    // TODO: sum probably not necessary
    return twoSum(-hi, -lo);
  }

  //--------------------------------------------------------------------

  public static final Hilo ZERO = twoSum(0.0, 0.0);

  @Override
  public final Hilo zero () { return ZERO; }

  // TODO: shouldn't need to check lo
  @Override
  public final boolean isZero () { return hi == 0.0 && lo == 0.0; }

  @Override
  public final Hilo abs () {
    if (isNaN()) { return NaN; }
    // TODO: is sum necessary?
    if (isNegative()) { return twoSum(-hi, -lo); }
    return this;
  }

  @Override
  public final Hilo absDiff (final Hilo y) {
    return subtract(y).abs();
  }

  //--------------------------------------------------------------------
  // Ringlike multiplication
  //--------------------------------------------------------------------

  private static final double SPLIT = 1.0 + 0x1.0p27;

  public final Hilo multiply (final double yhi, final double ylo) {
    // TODO: check whether all these edge cases are necessary
    if (ZERO.equals(this)) { return ZERO; }
    if ((0.0 == yhi) && (0.0 == ylo)) { return ZERO; }
    if (ONE.equals(this)) { return twoSum(yhi, ylo); }
    if ((1.0 == yhi) && (0.0 == ylo)) { return this; }
    if (isNaN()) { return NaN; }
    if (Double.isNaN(yhi)) { return NaN; }
    assert !Double.isNaN(ylo) : toHexString();
    final double hiTest = hi * yhi;
    // TODO: is this right? safe to ignore lo and ylo?
    if (Double.isInfinite(hiTest)) {
      return (0 < hiTest) ? POSITIVE_INFINITY : NEGATIVE_INFINITY;
    }
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
    return twoSum(zhi, zlo);
  }

  public final Hilo multiply (final double y) {
    // TODO: optimize simple double case
    return multiply(y, 0.0);
  }

  @Override
  public final Hilo multiply (final Hilo that) {
    return multiply(that.hi, that.lo);
  }

  @Override
  public final Hilo square () { return multiply(this); }

  //-------------------------------------------------------------------

  public final Hilo divide (final double y) {
    if (Double.isNaN(y)) { return NaN; }
    // TODO: optimize single double case
    return divide(y, 0.0);
  }

  public final Hilo divide (final double yhi, final double ylo) {
    double hc, tc, hy, ty, C, c, U, u;
    C = hi / yhi;
    c = SPLIT * C;
    hc = c - C;
    u = SPLIT * yhi;
    hc = c - hc;
    tc = C - hc;
    hy = u - yhi;
    U = C * yhi;
    hy = u - hy;
    ty = yhi - hy;
    u = (((hc * hy - U) + hc * ty) + tc * hy) + tc * ty;
    c = ((((hi - U) - u) + lo) - C * ylo) / yhi;
    u = C + c;
    return twoSum(u, (C - u) + c);
  }

  @Override
  public final Hilo divide (final Hilo y) {
    return divide(y.hi, y.lo);
  }

  @Override
  public final Hilo invert () {
    // TODO: unit test ONE.divide(x) == x.reciprocal() == x.pow(-1)
    double hc, tc, hy, ty, C, c, U, u;
    C = 1.0 / hi;
    c = SPLIT * C;
    hc = c - C;
    u = SPLIT * hi;
    hc = c - hc;
    tc = C - hc;
    hy = u - hi;
    U = C * hi;
    hy = u - hy;
    ty = hi - hy;
    u = (((hc * hy - U) + hc * ty) + tc * hy) + tc * ty;
    c = ((((1.0 - U) - u)) - C * lo) / hi;

    double zhi = C + c;
    double zlo = (C - zhi) + c;
    return twoSum(zhi, zlo);
  }

  //-------------------------------------------------------------------

  public static final Hilo ONE = twoSum(1.0, 0.0);

  @Override
  public final Hilo one () { return ONE; }

  @Override
  public final boolean isOne () { return equals(ONE); }

  //-------------------------------------------------------------------
  //  java.lang.Number 'interface'
  //-------------------------------------------------------------------

  public final double doubleValue () { return hi + lo; }

  public final float floatValue () { return (float) doubleValue(); }

  public final int intValue () { return (int) doubleValue(); }

  //-------------------------------------------------------------------
  // Comparable<Hilo>
  //-------------------------------------------------------------------

  @Override
  public final int compareTo (final Hilo other) {
    if (hi < other.hi) { return -1; }
    if (hi > other.hi) { return 1; }
    return Double.compare(lo, other.lo);
  }

  //-------------------------------------------------------------------
  // Object methods
  //-------------------------------------------------------------------
  @Override
  public final boolean equals (final Object that) {
    if (!(that instanceof Hilo(double hi1, double lo1))) {
      return false;
    }
    return hi == hi1 && lo == lo1;
  }

  @Override
  public final int hashCode () {
    int h = 17;
    h = (31 * h) + Double.hashCode(hi);
    h = (31 * h) + Double.hashCode(lo);
    return h;
  }

  /**
   * Two terms via <code>Double.toHexString(double)</code>, lossless
   * roundtrips.
   */
  public final String toHexString () {
    return "Hilo("
      + Double.toHexString(hi) + " + "
      + Double.toHexString(lo) + ")";
  }

  @Override
  public final String toString () { return toHexString(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  /**
   * A <code>DD</code>> uses a representation containing two
   * double-precision values. A number x is represented as a pair of
   * doubles, x.hi and x.lo, such that the number represented by x is
   * x.hi + x.lo, where
   * <pre>
   *    |x.lo| &lt;= 0.5 * ulp(x.hi)
   * </pre>
   * and ulp(y) means "unit in the last place of y". The basic
   * arithmetic operations are implemented using convenient properties
   * of IEEE-754 floating-point arithmetic.
   */

  @SuppressWarnings("unused")
  private final boolean checkUlp (final double s, final double t) {
    // reverse test to handle NaN
    return !((2 * Math.abs(t)) > Math.ulp(s));
  }

  /** Enforce <pre>hi = a + b</pre>, <pre>lo = hi - (a + b)</pre> so
   * that <pre>|lo| &lt;= 0.5 * ulp(hi)</pre>.
   *
   * @see <a href="https://en.wikipedia.org/wiki/2Sum"Fast2Sum</a>
   */
  public Hilo {
    // TODO: implement correct test for non-overlapping!
    //  split output violates this
    //    assert checkUlp(hi, lo) :
    //      "\nLow order term too large:" +
    //        "\nhi= " + Double.toHexString(hi) +
    //        "\nlo= " + Double.toHexString(lo) +
    //        "\nulp(hi)= " + Double.toHexString(Math.ulp(hi));
    // a very weak check for now:
    // reverse test for NaN
    assert ! (Math.abs(hi) < Math.abs(lo)) :
      "\nLow order term too large:" +
        "\nhi= " + Double.toHexString(hi) +
        "\nlo= " + Double.toHexString(lo) +
        "\nulp(hi)= " + Double.toHexString(Math.ulp(hi));
  }

//  #define Square_Tail(a, x, y) \
//  Split(a, ahi, alo); \
//  err1 = x - (ahi * ahi); \
//  err3 = err1 - ((ahi + ahi) * alo); \
//  y = (alo * alo) - err3

  private static final double squareTail (final double a,
                                        final double hi) {
    final Hilo ahilo = split(a);
    final double err1 = hi - (ahilo.hi() * ahilo.hi());
    final double err3 = err1 - ((ahilo.hi() + ahilo.hi()) * ahilo.lo());
    return (ahilo.lo() * ahilo.lo()) - err3; }

  //  #define Square(a, x, y) \
//  x = (REAL) (a * a); \
//  Square_Tail(a, x, y)
  public static final Hilo square (final double a) {
  final double x =  (a * a);
  final double y = squareTail(a, x);
  return new Hilo(x, y); }

  public static final Hilo twoSum (final double a, final double b) {
    final double x = (a + b);
    final double bvirt = (x - a);
    final double avirt = x - bvirt;
    final double bround = b - bvirt;
    final double around = a - avirt;
    final double y = around + bround;
    return new Hilo(x, y);
  }

  public static final Hilo fastTwoSum (final double a,
                                       final double b) {
    assert Math.abs(a) >= Math.abs(b) :
      "fastTwoSum(" +
        Double.toHexString(a) + ", " +
        Double.toHexString(b) + ")";
    final double x = (a + b);
    //Fast_Two_Sum_Tail(a, b, x, y)
    final double bvirt = x - a;
    final double y = b - bvirt;
    return new Hilo(x, y);
  }

  @SuppressWarnings("unused")
  public static final Hilo twoDiff (final double a,
                                    final double b) {
//    return twoSum(a, -b);
    final double x = (a - b);
    //Two_Diff_Tail(a, b, x, y)
    final double bvirt = (a - x);
    final double avirt = x + bvirt;
    final double bround = bvirt - b;
    final double around = a - avirt;
    final double y = around + bround;
    return new Hilo(x, y);
  }

  // TODO: move somewhere else?
//#define Two_Diff_Tail(a, b, x, y) \
//  bvirt = (REAL) (a - x); \
//  avirt = x + bvirt; \
//  bround = bvirt - b; \
//  around = a - avirt; \
//  y = around + bround
  public static final double twoDiffTail (final double a,
                                        final double b,
                                        final double hi) {

  final double bvirt = (a - hi);
  final double avirt = hi + bvirt;
  final double bround = bvirt - b;
  final double around = a - avirt;
  return around + bround; }

  // TODO: move to Expansion or XDouble
  // returns double[3]
  // #define Two_One_Diff(a1, a0, b, x2, x1, x0) \
  //  Two_Diff(a0, b , _i, x0); \
  //  Two_Sum( a1, _i, x2, x1)
//  public static final double[] twoOneDiff(final double ahi,
//                                          final double alo,
//                                          final double b) {
//    final Hilo ix0 = twoDiff(alo, b);
//    final Hilo x2x1 = twoSum(ahi, ix0.hi());
//    return new double[] {ix0.lo(), x2x1.lo(), x2x1.hi(), }; }

  // TODO: move to Expansion or XDouble
  // returns double[4]
  // Two_Two_Diff(axby1, axby0, bxay1, bxay0,
  //              ab[3], ab[2], ab[1], ab[0]);
//  public static final double[] twoTwoDiff (final Hilo a,
//                                           final Hilo b) {
//    // Two_One_Diff(a1, a0, b0, _j, _0, x0);
//    final double[] x00j = twoOneDiff(a.hi(),a.lo(),b.lo());
//    // Two_One_Diff(_j, _0, b.hi(), x3, x2, x1)
//    final double[] x123 = twoOneDiff(x00j[2],x00j[1],b.hi);
//    return new double[] { x00j[0],x123[0],x123[1],x123[2], };  }

  // Shewchuk version
  /**
   * Theorem 17 in Shewchuk.
   */
  public static final Hilo split (final double a) {
    final double c = SPLIT * a;
    final double big = c - a;
    final double hi = c - big;
    final double lo = a - hi;
    // TODO: call twoSum to enforce ulp constraint?
    //  or replace ulp constraint --- is non-overlapping different?
//    return twoSum(hi, lo);
    return new Hilo(hi,lo);
  }

//#define Two_Product_Presplit(a, b, bhi, blo, x, y) \
//  x = (REAL) (a * b); \
//  Split(a, ahi, alo); \
//  err1 = x - (ahi * bhi); \
//  err2 = err1 - (alo * bhi); \
//  err3 = err2 - (ahi * blo); \
//  y = (alo * blo) - err3

  // TODO: call twoProduct2PreSplit?
  @SuppressWarnings("unused")
  public static final Hilo twoProductPresplit (final double a,
                                               final double b,
                                               final Hilo bhilo) {

    final double x = a * b;

    final Hilo ahilo = split (a);
    final double err1 = x - (ahilo.hi() * bhilo.hi());
    final double err2 = err1 - (ahilo.lo() * bhilo.hi());
    final double err3 = err2 - (ahilo.hi() * bhilo.lo());
    final double y = (ahilo.lo() * bhilo.lo()) - err3;

    return new Hilo(x,y);}

  //  #define Two_Product_2Presplit(a, ahi, alo, b, bhi, blo, x, y) \
  //  x = (REAL) (a * b); \
  //  err1 = x - (ahi * bhi); \
  //  err2 = err1 - (alo * bhi); \
  //  err3 = err2 - (ahi * blo); \
  //  y = (alo * blo) - err3

  // TODO: is this worth the complexity?
  //  Check whether aSplit and bSplit are used anywhere else.
  public static final Hilo twoProduct2Presplit (final double a,
                                                final Hilo aSplit,
                                                final double b,
                                                final Hilo bSplit) {
    final double hi = a * b;
    final double err1 = hi - (aSplit.hi() * bSplit.hi());
    final double err2 = err1 - (aSplit.lo() * bSplit.hi());
    final double err3 = err2 - (aSplit.hi() * bSplit.lo());
    final double lo = (aSplit.lo() * bSplit.lo()) - err3;
    // TODO: call twoSum to enforce ulp constraint?
    //  or replace ulp constraint --- is non-overlapping different?
    return new Hilo(hi,lo); }


  // modular predicates.c version: breaks Exact.incircle()
//  public static final Hilo twoProduct (final double a,
//                                       final double b) {
//    final double x = (a * b);
//    //Two_Product_Tail(a, b, x, y)
//    // TODO: inline to avoid instance creation?
//    //   call twoProductPresplit?
//    final Hilo ahilo = split(a);
//    final Hilo bhilo = split(b);
//    final double err1 = x - (ahilo.hi() * bhilo.hi());
//    final double err2 = err1 - (ahilo.lo() * bhilo.hi());
//    final double err3 = err2 - (ahilo.hi() * bhilo.lo());
//    final double y = (ahilo.lo() * bhilo.lo()) - err3;
//    return new Hilo(x, y); }

  public static final Hilo twoProduct (final double a,
                                       final double b) {
    final double x = (a * b);
    //Two_Product_Tail(a, b, x, y)
    // TODO: inline to avoid instance creation?
    //   call twoProductPresplit?
    final double ca = SPLIT * a;
    final double abig = (ca - a);
    final double ahi = ca - abig;
    final double alo = a - ahi;
    final double cb = SPLIT * b;
    final double bbig = (cb - b);
    final double bhi = cb - bbig;
    final double blo = b - bhi;
    final double err1 = x - (ahi * bhi);
    final double err2 = err1 - (alo * bhi);
    final double err3 = err2 - (ahi * blo);
    final double y = (alo * blo) - err3;
    return new Hilo(x, y); }


  // FMA version
//  public static final Hilo twoProduct (final double a,
//                                       final double b) {
//    final double x = (a * b);
//    if (! Double.isFinite(x)) { return new Hilo(x,0.0); }
//    final double y = Math.fma(a,b,-x);
//    return new Hilo(x, y); }

  public static final Hilo valueOf (final double a) {
    return new Hilo(a, 0.0);  }

  public static final Hilo valueOf (final float a) {
    return new Hilo(a, 0.0);  }

  public static final Hilo valueOf (final BigFloat bf) {
    final double a = bf.doubleValue();
    if (Double.isFinite(a)) {
      final double b = bf.add(-a).doubleValue();
      // TODO: use fastTwoSUm?
      return twoSum(a, b);  }
    if (Double.isNaN(a)) { return NaN; }
    if (0.0 <= a) { return POSITIVE_INFINITY; }
    return NEGATIVE_INFINITY;  }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
