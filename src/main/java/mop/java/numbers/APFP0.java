package mop.java.numbers;

/**
 * Adaptive precision floating point based on:
 * <ul>
 * <li><a href="https://www.cs.cmu.edu/~quake/robust.html">
 * Jonathan Shewchuk, website:
 * Adaptive Precision Floating-Point Arithmetic and Fast Robust
 * mop.java.numbers.predicates.Predicates for Computational Geometry
 * </a></li>
 * <li>
 * <a href="https://www.cs.cmu.edu/afs/cs/project/quake/public/code/predicates.c">
 * Jonathan Shewchuk, predicates.c
 * </a></li>
 * <li>
 * <a href="https://github.com/libigl/libigl-predicates/blob/master/predicates.c">
 * libigl-predicates github
 * </a></li>
 * <li><a href="https://link.springer.com/article/10.1007/PL00009321">
 * Jonathan Shewchuk, 1997,
 * Adaptive Precision Floating-Point Arithmetic and Fast Robust
 * mop.java.numbers.predicates.Predicates for Computational Geometry
 * (53 pages, published)
 * </a></li>
 * <li>
 * <a href="https://people.eecs.berkeley.edu/~jrs/papers/robustr.pdf">
 * Jonathan Shewchuk, 1997,
 * Adaptive Precision Floating-Point Arithmetic and Fast Robust
 * mop.java.numbers.predicates.Predicates for Computational Geometry
 * (59 pages, tech report)
 * </a></li>
 * <li>
 * <a href="https://people.eecs.berkeley.edu/~jrs/papers/robust-predicates.pdf">
 * Jonathan Shewchuk, 1996,
 * Robust adaptive floating-point geometric predicates,
 * </a>
 * </li>
 * <li><a href="https://dl.acm.org/doi/10.1145/237218.237337">
 * Jonathan Shewchuk, 1996,
 * Robust adaptive floating-point geometric predicates,
 * SCG '96: Proceedings of the twelfth annual symposium on
 * Computational geometry,
 * (10 pages)
 * </a?</li>
 * *</ul>
 * <p>
 * Data Structures:
 * <a href="https://github.com/carrotsearch/hppc">hppc</a>
 * </p>
 * <p>
 *   This version's priority is correctness, and simplicity.
 *   Later versions can optimize guided by benchmarks and
 *   profiling.
 * </p>
 * <p>
 *   Basic idea: a finite subset of the rationals is represented
 *   by an implied
 *   sum of <i>non-overlapping</i> <code>double</code> terms.
 *   This set has the same range as the set of <code>double</code>s,
 *   with finer precision.
 *   Finite cardinality because <code>double</code> is finite
 *   and the number of terms is limited by the maximum array length.
 *   <br>
 *   TODO: work out the precision: equivalent number of bits
 *   <br>
 *   TODO: what is the maximum number of non-overlapping terms?
 *   <br>
 *   Possible extension: add an exponent (<code>long</code> or
 *   even <code>BigInteger</code> to extend range.
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-05-15
 */

// strictfp unnecessary for JDK17 and later
public final class APFP0

  // TODO: Iterable<double>
  implements Comparable {

  //--------------------------------------------------------------------
  // require IEEE 754
  //--------------------------------------------------------------------
  /* On some machines, the exact arithmetic routines might be defeated by the  */
  /*   use of internal extended precision floating-point registers.  Sometimes */
  /*   this problem can be fixed by defining certain values to be volatile,    */
  /*   thus forcing them to be stored to memory and rounded off.  This isn't   */
  /*   a great solution, though, as it slows the arithmetic down.              */
  /*                                                                           */
  /* To try this out, write "#define INEXACT volatile" below.  Normally,       */
  /*   however, INEXACT should be defined to be nothing.  ("#define INEXACT".) */

  //  #define INEXACT                          /* Nothing */
  /* #define INEXACT volatile */

// Java equivalent is <code>strictfp</code> on class or methods,
// but that has no effect after Java 17, which requires
// all floating point calculations to have IEEE 754 semantics.
{ assert 0 < Integer.getInteger("java.version").compareTo(17)
  : "Java: " + System.getProperty("java.version") +
  " not supported"; }

  //--------------------------------------------------------------------
  // translation of C macros
  //--------------------------------------------------------------------
  // TODO: any benefit over Math.abs()?
  /* Which of the following two methods of finding the absolute values is      */
  /* fastest is compiler-dependent.  A few compilers can inline and optimize */
  /* the fabs() call; but most will incur the overhead of a function call,   */
  /* which is disastrously slow.  A faster way on IEEE machines might be to  */
  /* mask the appropriate bit, but that's difficult to do in C.              */

  //#define Absolute(a)  ((a) >= 0.0 ? (a) : -(a))
  /* #define Absolute(a)  fabs(a) */

  //--------------------------------------------------------------------
  /* Many of the operations are broken up into two pieces, a main part that    */
  /*   performs an approximate operation, and a "tail" that computes the       */
  /*   roundoff error of that operation.                                       */
  /*                                                                           */
  /* The operations Fast_Two_Sum(), Fast_Two_Diff(), Two_Sum(), Two_Diff(),    */
  /*   Split(), and Two_Product() are all implemented as described in the      */
  /*   reference.  Each of these macros requires certain variables to be       */
  /*   defined in the calling routine.  The variables `bvirt', `c', `abig',    */
  /*   `_i', `_j', `_k', `_l', `_m', and `_n' are declared `INEXACT' because   */
  /*   they store the result of an operation that may incur roundoff error.    */
  /*   The input parameter `x' (or the highest numbered `x_' parameter) must   */
  /*   also be declared `INEXACT'.                                             */
  //--------------------------------------------------------------------
  // The core functionality of predicates.c is implemented with C macros.
  // The macros do multiple assignments, effectively doing a multiple
  // value return, binding to some of the variables passed to the macro.
  // In addition, they make assignments to variables
  // not defined in the macro, assumed to be defined in the scope
  // in which the macro is expanded.
  //
  // One option for translation is to represent the in-out bindings
  // with some kind of <code>String -> double</code> map,
  // like <code>com.carrotsearch.hppc.ObjectDoubleMap<KType></code>.
  // This would no doubt be very slow, but might pay to get a 1st pass
  // reliable version.
  //
  // TODO: are all the values in the macros <code>double</code>?
  //
  // Another option is to mechanically expand all the macros.
  //
  // To begin, expand by hand:
  // Expand *_Tail macros at their single acll sites.

  //--------------------------------------------------------------------
  //#define Fast_Two_Sum(a, b, x, y) \
  //  x = (REAL) (a + b); \
  //  bvirt = x - a; \
  //  y = b - bvirt

  /** Return double[]{x, y, bvirt}
   * TODO: pass in a,b in array?
   * TODO: does it make sense to return an APFP0 instance?
   */
  private static final double[] fastTwoSum (final double a,
                                            final double b) {
    final double x = a + b;
    final double bvirt = x - a;
    final double y = b - bvirt;
    return new double[] { x, y, bvirt, }; }

  //--------------------------------------------------------------------
  //#define Fast_Two_Diff(a, b, x, y) \
  //  x = (REAL) (a - b); \
  //  bvirt = a - x; \
  //  y = bvirt - b

  /** Return double[]{x, y, bvirt}
   */
  private static final double[] fastTwoDiff (final double a,
                                             final double b) {
    final double x = a - b;
    final double bvirt = a - x;
    final double y = bvirt - b;
    return new double[] { x,  y, bvirt}; }

  //--------------------------------------------------------------------
  //#define Two_Sum(a, b, x, y) \
  //  x = (REAL) (a + b); \
  //  bvirt = (REAL) (x - a); \
  //  avirt = x - bvirt; \
  //  bround = b - bvirt; \
  //  around = a - avirt; \
  //  y = around + bround

  /** Return double[]{x, y, bvirt, avirt, bround, around}
   */
  private static final double[] twoSum (final double a,
                                        final double b) {
    final double x = a + b;
    final double bvirt = x - a;
    final double avirt = x - bvirt;
    final double bround = b - bvirt;
    final double around = a - avirt;
    final double y = around + bround;
    return new double[] { x,  y, bvirt, avirt, bround, around,}; }

  //--------------------------------------------------------------------
//#define Two_Diff_Tail(a, b, x, y) \
//  bvirt = (REAL) (a - x); \
//  avirt = x + bvirt; \
//  bround = bvirt - b; \
//  around = a - avirt; \
//  y = around + bround
//
//#define Two_Diff(a, b, x, y) \
//  x = (REAL) (a - b); \
//  Two_Diff_Tail(a, b, x, y)
//
//#define Split(a, ahi, alo) \
//  c = (REAL) (splitter * a); \
//  abig = (REAL) (c - a); \
//  ahi = c - abig; \
//  alo = a - ahi
//
//#define Two_Product_Tail(a, b, x, y) \
//  Split(a, ahi, alo); \
//  Split(b, bhi, blo); \
//  err1 = x - (ahi * bhi); \
//  err2 = err1 - (alo * bhi); \
//  err3 = err2 - (ahi * blo); \
//  y = (alo * blo) - err3
//
//#define Two_Product(a, b, x, y) \
//  x = (REAL) (a * b); \
//  Two_Product_Tail(a, b, x, y)
//
//  /* Two_Product_Presplit() is Two_Product() where one of the inputs has       */
//  /*   already been split.  Avoids redundant splitting.                        */
//
//#define Two_Product_Presplit(a, b, bhi, blo, x, y) \
//  x = (REAL) (a * b); \
//  Split(a, ahi, alo); \
//  err1 = x - (ahi * bhi); \
//  err2 = err1 - (alo * bhi); \
//  err3 = err2 - (ahi * blo); \
//  y = (alo * blo) - err3
//
//  /* Two_Product_2Presplit() is Two_Product() where both of the inputs have    */
//  /*   already been split.  Avoids redundant splitting.                        */
//
//#define Two_Product_2Presplit(a, ahi, alo, b, bhi, blo, x, y) \
//  x = (REAL) (a * b); \
//  err1 = x - (ahi * bhi); \
//  err2 = err1 - (alo * bhi); \
//  err3 = err2 - (ahi * blo); \
//  y = (alo * blo) - err3
//
//  /* Square() can be done more quickly than Two_Product().                     */
//
//#define Square_Tail(a, x, y) \
//  Split(a, ahi, alo); \
//  err1 = x - (ahi * ahi); \
//  err3 = err1 - ((ahi + ahi) * alo); \
//  y = (alo * alo) - err3
//
//#define Square(a, x, y) \
//  x = (REAL) (a * a); \
//  Square_Tail(a, x, y)
//
//  /* Macros for summing expansions of various fixed lengths.  These are all    */
//  /*   unrolled versions of Expansion_Sum().                                   */
//
//#define Two_One_Sum(a1, a0, b, x2, x1, x0) \
//  Two_Sum(a0, b , _i, x0); \
//  Two_Sum(a1, _i, x2, x1)
//
//#define Two_One_Diff(a1, a0, b, x2, x1, x0) \
//  Two_Diff(a0, b , _i, x0); \
//  Two_Sum( a1, _i, x2, x1)
//
//#define Two_Two_Sum(a1, a0, b1, b0, x3, x2, x1, x0) \
//  Two_One_Sum(a1, a0, b0, _j, _0, x0); \
//  Two_One_Sum(_j, _0, b1, x3, x2, x1)
//
//#define Two_Two_Diff(a1, a0, b1, b0, x3, x2, x1, x0) \
//  Two_One_Diff(a1, a0, b0, _j, _0, x0); \
//  Two_One_Diff(_j, _0, b1, x3, x2, x1)
//
//#define Four_One_Sum(a3, a2, a1, a0, b, x4, x3, x2, x1, x0) \
//  Two_One_Sum(a1, a0, b , _j, x1, x0); \
//  Two_One_Sum(a3, a2, _j, x4, x3, x2)
//
//#define Four_Two_Sum(a3, a2, a1, a0, b1, b0, x5, x4, x3, x2, x1, x0) \
//  Four_One_Sum(a3, a2, a1, a0, b0, _k, _2, _1, _0, x0); \
//  Four_One_Sum(_k, _2, _1, _0, b1, x5, x4, x3, x2, x1)
//
//#define Four_Four_Sum(a3, a2, a1, a0, b4, b3, b1, b0, x7, x6, x5, x4, x3, x2, \
//                      x1, x0) \
//  Four_Two_Sum(a3, a2, a1, a0, b1, b0, _l, _2, _1, _0, x1, x0); \
//  Four_Two_Sum(_l, _2, _1, _0, b4, b3, x7, x6, x5, x4, x3, x2)
//
//#define Eight_One_Sum(a7, a6, a5, a4, a3, a2, a1, a0, b, x8, x7, x6, x5, x4, \
//                      x3, x2, x1, x0) \
//  Four_One_Sum(a3, a2, a1, a0, b , _j, x3, x2, x1, x0); \
//  Four_One_Sum(a7, a6, a5, a4, _j, x8, x7, x6, x5, x4)
//
//#define Eight_Two_Sum(a7, a6, a5, a4, a3, a2, a1, a0, b1, b0, x9, x8, x7, \
//                      x6, x5, x4, x3, x2, x1, x0) \
//  Eight_One_Sum(a7, a6, a5, a4, a3, a2, a1, a0, b0, _k, _6, _5, _4, _3, _2, \
//                _1, _0, x0); \
//  Eight_One_Sum(_k, _6, _5, _4, _3, _2, _1, _0, b1, x9, x8, x7, x6, x5, x4, \
//                x3, x2, x1)
//
//#define Eight_Four_Sum(a7, a6, a5, a4, a3, a2, a1, a0, b4, b3, b1, b0, x11, \
//                       x10, x9, x8, x7, x6, x5, x4, x3, x2, x1, x0) \
//  Eight_Two_Sum(a7, a6, a5, a4, a3, a2, a1, a0, b1, b0, _l, _6, _5, _4, _3, \
//                _2, _1, _0, x1, x0); \
//  Eight_Two_Sum(_l, _6, _5, _4, _3, _2, _1, _0, b4, b3, x11, x10, x9, x8, \
//                x7, x6, x5, x4, x3, x2)
//
//  /* Macros for multiplying expansions of various fixed lengths.               */
//
//#define Two_One_Product(a1, a0, b, x3, x2, x1, x0) \
//  Split(b, bhi, blo); \
//  Two_Product_Presplit(a0, b, bhi, blo, _i, x0); \
//  Two_Product_Presplit(a1, b, bhi, blo, _j, _0); \
//  Two_Sum(_i, _0, _k, x1); \
//  Fast_Two_Sum(_j, _k, x3, x2)
//
//#define Four_One_Product(a3, a2, a1, a0, b, x7, x6, x5, x4, x3, x2, x1, x0) \
//  Split(b, bhi, blo); \
//  Two_Product_Presplit(a0, b, bhi, blo, _i, x0); \
//  Two_Product_Presplit(a1, b, bhi, blo, _j, _0); \
//  Two_Sum(_i, _0, _k, x1); \
//  Fast_Two_Sum(_j, _k, _i, x2); \
//  Two_Product_Presplit(a2, b, bhi, blo, _j, _0); \
//  Two_Sum(_i, _0, _k, x3); \
//  Fast_Two_Sum(_j, _k, _i, x4); \
//  Two_Product_Presplit(a3, b, bhi, blo, _j, _0); \
//  Two_Sum(_i, _0, _k, x5); \
//  Fast_Two_Sum(_j, _k, x7, x6)
//
//#define Two_Two_Product(a1, a0, b1, b0, x7, x6, x5, x4, x3, x2, x1, x0) \
//  Split(a0, a0hi, a0lo); \
//  Split(b0, bhi, blo); \
//  Two_Product_2Presplit(a0, a0hi, a0lo, b0, bhi, blo, _i, x0); \
//  Split(a1, a1hi, a1lo); \
//  Two_Product_2Presplit(a1, a1hi, a1lo, b0, bhi, blo, _j, _0); \
//  Two_Sum(_i, _0, _k, _1); \
//  Fast_Two_Sum(_j, _k, _l, _2); \
//  Split(b1, bhi, blo); \
//  Two_Product_2Presplit(a0, a0hi, a0lo, b1, bhi, blo, _i, _0); \
//  Two_Sum(_1, _0, _k, x1); \
//  Two_Sum(_2, _k, _j, _1); \
//  Two_Sum(_l, _j, _m, _2); \
//  Two_Product_2Presplit(a1, a1hi, a1lo, b1, bhi, blo, _j, _0); \
//  Two_Sum(_i, _0, _n, _0); \
//  Two_Sum(_1, _0, _i, x2); \
//  Two_Sum(_2, _i, _k, _1); \
//  Two_Sum(_m, _k, _l, _2); \
//  Two_Sum(_j, _n, _k, _0); \
//  Two_Sum(_1, _0, _j, x3); \
//  Two_Sum(_2, _j, _i, _1); \
//  Two_Sum(_l, _i, _m, _2); \
//  Two_Sum(_1, _k, _i, x4); \
//  Two_Sum(_2, _i, _k, x5); \
//  Two_Sum(_m, _k, x7, x6)
//
//  /* An expansion of length two can be squared more quickly than finding the   */
//  /*   product of two different expansions of length two, and the result is    */
//  /*   guaranteed to have no more than six (rather than eight) components.     */
//
//#define Two_Square(a1, a0, x5, x4, x3, x2, x1, x0) \
//  Square(a0, _j, x0); \
//  _0 = a0 + a0; \
//  Two_Product(a1, _0, _k, _1); \
//  Two_One_Sum(_k, _1, _j, _l, _2, x1); \
//  Square(a1, _j, _1); \
//  Two_Two_Sum(_j, _1, _l, _2, x5, x4, x3, x2)
  //--------------------------------------------------------------------
  // singletons
  //--------------------------------------------------------------------

//  public static final APFP0 NaN = valueOf(Double.NaN);
//
//  public static final APFP0 POSITIVE_INFINITY =
//    valueOf(Double.POSITIVE_INFINITY);
//
//  public static final APFP0 NEGATIVE_INFINITY =
//    valueOf(Double.NEGATIVE_INFINITY);
//
//  public static final APFP0 ZERO = twoSum(0.0, 0.0);
//
//  public static final APFP0 ONE = twoSum(1.0, 0.0);

  //--------------------------------------------------------------------
  // instance methods
  //--------------------------------------------------------------------

  //--------------------------------------------------------------------
  // arithmetic
  //--------------------------------------------------------------------

  /** @return <tt>(this + y)</tt>
   */
//  public final APFP0 add (final double y) {
//    final double S = hi + y;
//    final double e = S - hi;
//    double s = S - e;
//    s = (y - e) + (hi - s);
//    final double f = s + lo;
//    final double H = S + f;
//    final double h = f + (S - H);
//    return twoSum(H + h, h + (H - hi));
//  }
//
//  private final APFP0 add (final double yhi, double ylo) {
//    final double S = hi + yhi;
//    final double T = lo + ylo;
//    double e = S - hi;
//    final double f = T - lo;
//    double s = S - e;
//    double t = T - f;
//    s = (yhi - e) + (hi - s);
//    t = (ylo - f) + (lo - t);
//    e = s + T;
//    final double H = S + e;
//    final double h = e + (S - H);
//    e = t + h;
//    final double zhi = H + e;
//    final double zlo = e + (H - zhi);
//    return twoSum(zhi, zlo);
//  }
//
//  /**
//   * @return <tt>(this + y)</tt>
//   */
//  public final APFP0 add (final APFP0 y) {
//    return add(y.hi, y.lo);
//  }
//
//  /**
//   * @return <tt>(this - y)</tt>
//   */
//  public final APFP0 subtract (final APFP0 y) { return add(-y.hi, -y.lo); }
//
//  /**
//   * @return <tt>(this - y)</tt>
//   */
//  public final APFP0 subtract (final double y) { return add(-y); }
//
//  /**
//   * @return <tt>(this - y)</tt>
//   */
//  public final APFP0 negate () {
//    // TODO: sum probably not necessary
//    return twoSum(-hi, -lo);
//  }

  //-------------------------------------------------------------------
  // multiplication
  //-------------------------------------------------------------------
//  /**
//   * The value to split a double-precision value on during
//   * multiplication.
//   * TODO: should this be an int?<br>
//   * Use hex string for clarity
//   */
//  private static final double SPLIT = 1.0 + 0x1.0p27;
//
//  public final APFP0 multiply (final double yhi, final double ylo) {
//    // TODO: check whether all these edge cases are necessary
//    if (ZERO.equals(this)) { return ZERO; }
//    if ((0.0 == yhi) && (0.0 == ylo)) { return ZERO; }
//    if (ONE.equals(this)) { return twoSum(yhi, ylo); }
//    if ((1.0 == yhi) && (0.0 == ylo)) { return this; }
//    if (isNaN()) { return NaN; }
//    if (Double.isNaN(yhi)) { return NaN; }
//    assert !Double.isNaN(ylo);
//    final double hiTest = hi * yhi;
//    // TODO: is this right? safe to ignore lo and ylo?
//    if (Double.isInfinite(hiTest)) {
//      return (0 < hiTest) ? POSITIVE_INFINITY : NEGATIVE_INFINITY;
//    }
//    double C = SPLIT * hi;
//    double hx = C - hi;
//    double c = SPLIT * yhi;
//    hx = C - hx;
//    double tx = hi - hx;
//    double hy = c - yhi;
//    C = hi * yhi;
//    hy = c - hy;
//    final double ty = yhi - hy;
//    c = hx * hy;
//    c -= C;
//    c += hx * ty;
//    c += tx * hy;
//    c += tx * ty;
//    c += (hi * ylo) + (lo * yhi);
//    final double zhi = C + c;
//    hx = C - zhi;
//    final double zlo = c + hx;
//    return twoSum(zhi, zlo);
//  }
//
//  /**
//   * @return <tt>(this *y)</tt>
//   */
//  public final APFP0 multiply (final APFP0 that) {
//    return multiply(that.hi, that.lo);
//  }
//
//  /**
//   * @return <tt>(this *y)</tt>
//   */
//  public final APFP0 multiply (final double y) {
//    // TODO: optimize simple double case
//    return multiply(y, 0.0);
//  }
//
  //-------------------------------------------------------------------
  // division
  //-------------------------------------------------------------------
//
//  public final APFP0 divide (final double yhi, final double ylo) {
//    double hc, tc, hy, ty, C, c, U, u;
//    C = hi / yhi; c = SPLIT * C; hc = c - C; u = SPLIT * yhi;
//    hc = c - hc;
//    tc = C - hc; hy = u - yhi; U = C * yhi; hy = u - hy;
//    ty = yhi - hy;
//    u = (((hc * hy - U) + hc * ty) + tc * hy) + tc * ty;
//    c = ((((hi - U) - u) + lo) - C * ylo) / yhi;
//    u = C + c;
//    return twoSum(u, (C - u) + c);
//  }
//
//  /**
//   * Computes a new DD whose value is <tt>(this / y)</tt>.
//   *
//   * @param y the divisor
//   *
//   * @return a new object with the value <tt>(this / y)</tt>
//   */
//  public final APFP0 divide (final APFP0 y) {
//    return divide(y.hi, y.lo);
//  }
//
//  /**
//   * Computes a new DD whose value is <tt>(this / y)</tt>.
//   *
//   * @param y the divisor
//   *
//   * @return a new object with the value <tt>(this / y)</tt>
//   */
//  public final APFP0 divide (final double y) {
//    if (Double.isNaN(y)) { return NaN; }
//    // TODO: optimize single double case
//    return divide(y, 0.0);
//  }

  //-------------------------------------------------------------------
//  /**
//   * Returns a DD whose value is  <tt>1 / this</tt>.
//   *
//   * @return the reciprocal of this value
//   */
//  public final APFP0 reciprocal () {
//    // TODO: unit test ONE.divide(x) == x.reciprocal() == x.pow(-1)
//    double hc, tc, hy, ty, C, c, U, u;
//    C = 1.0 / hi;
//    c = SPLIT * C;
//    hc = c - C;
//    u = SPLIT * hi;
//    hc = c - hc; tc = C - hc; hy = u - hi; U = C * hi; hy = u - hy;
//    ty = hi - hy;
//    u = (((hc * hy - U) + hc * ty) + tc * hy) + tc * ty;
//    c = ((((1.0 - U) - u)) - C * lo) / hi;
//
//    double zhi = C + c;
//    double zlo = (C - zhi) + c;
//    return twoSum(zhi, zlo);
//  }

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

//  /** Returns the smallest (closest to negative infinity) value
//  that is
//   * not less than the argument and is equal to a mathematical
//   integer.
//   * Special cases:
//   * <ul>
//   * <li>If this value is NaN, returns NaN.
//   * </ul>
//   *
//   * @return the smallest (closest to negative infinity) value that is
//   * not less than the argument and is equal to a mathematical
//   integer.
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
//  /** Rounds this value to the nearest integer. The value is
//  rounded to
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
//  /** Computes the determinant of the 2x2 matrix with the given
//  entries.
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

//  /** Computes the determinant of the 2x2 matrix with the given
//  entries.
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
//
//  /**
//   * Computes the minimum of this and another DD number.
//   *
//   * @param x a DD number
//   *
//   * @return the minimum of the two numbers
//   */
//  public final APFP0 min (final APFP0 x) {
//    if (le(x)) { return this; }
//    else { return x; }
//  }
//
//  /**
//   * Computes the maximum of this and another DD number.
//   *
//   * @param x a DD number
//   *
//   * @return the maximum of the two numbers
//   */
//  public final APFP0 max (final APFP0 x) {
//    if (ge(x)) { return this; }
//    else { return x; }
//  }

  //-------------------------------------------------------------------
  //  java.lang.Number
  //-------------------------------------------------------------------
//  // TODO: just return hi?
//
//  /**
//   * Converts this value to the nearest double-precision number.
//   *
//   * @return the nearest double-precision number to this value
//   */
//  public final double doubleValue () { return hi + lo; }
//
//  // TODO: is this correct? return (float) hi? (float) hi + (float) lo?
//
//  /**
//   * Converts this value to the nearest single precision number.
//   *
//   * @return the nearest float
//   */
//  public final float floatValue () { return (float) (hi + lo); }
//
//  // TODO: is this correct? return (int) hi + (int) lo?
//  // TODO: throw exception for NaN, infinity?
//
//  /**
//   * Converts this value to the nearest integer.
//   *
//   * @return the nearest integer to this value
//   */
//  public final int intValue () { return (int) hi; }
//
  //-------------------------------------------------------------------
  // mop.java.numbers.predicates.Predicates
  //-------------------------------------------------------------------
//
//  /**
//   * Tests whether this value is equal to 0.
//   *
//   * @return true if this value is equal to 0
//   */
//  public final boolean isZero () {
//    return hi == 0.0 && lo == 0.0;
//  }
//
//  /**
//   * Tests whether this value is less than 0.
//   *
//   * @return true if this value is less than 0
//   */
//  public final boolean isNegative () {
//    return hi < 0.0 || (hi == 0.0 && lo < 0.0);
//  }
////
////  /** Tests whether this value is greater than 0.
////   *
////   * @return true if this value is greater than 0
////   */
////  public final boolean isPositive () {
////    return hi > 0.0 || (hi == 0.0 && lo > 0.0); }
//
//  /**
//   * Tests whether this value is NaN.
//   *
//   * @return true if this value is NaN
//   */
//  public final boolean isNaN () {
//    // TODO: what about lo? Constrain both to be NaN?
//    return Double.isNaN(hi);
//  }
//
//  /**
//   * Tests whether this value is finite.
//   *
//   * @return true if this value is finite
//   */
//  public final boolean isFinite () {
//    return Double.isFinite(hi) && Double.isFinite(lo);
//  }
//
//  /**
//   * Tests whether this value is finite.
//   *
//   * @return true if this value is finite
//   */
//  public final boolean isInfinite () {
//    // TODO: what about lo? Constrain both to be same sign infinite?
//    // TODO: assuming obeys constraint that abs(hi) > abs(lo)
//    return Double.isInfinite(hi);
//  }
//
//  /**
//   * Tests whether this value is equal to another <tt>DD</tt> value.
//   *
//   * @param y a DD value
//   *
//   * @return true if this value = y
//   */
//  public final boolean equals (final APFP0 y) {
//    return hi == y.hi && lo == y.lo;
//  }

  //-------------------------------------------------------------------
  // Comparable
  //-------------------------------------------------------------------
  /** Return -1, 0, or 1 depending on whether this value is less than,
   * equal to or greater than the value of <tt>o</tt>
   */
  @Override
  public final int compareTo (final Object o) {
    throw new UnsupportedOperationException();
    // TODO: compare comparable terms in order
//    final APFP0 other = (APFP0) o;
//    if (hi < other.hi) { return -1; }
//    if (hi > other.hi) { return 1; }
//    return Double.compare(lo, other.lo);
  }

  //-------------------------------------------------------------------
  // Output
  //-------------------------------------------------------------------
//
//  /**
//   * Returns a string representation of this number, as 2 terms printed
//   * by <code>Double.toHexString(double)</code>. This string
//   * representation should be lossless.
//   *
//   * @return a string representation of this number
//   */
//  public final String toHexString () {
//    return "APFP0<"
//      + Double.toHexString(hi) + " + "
//      + Double.toHexString(lo) + ">";
//  }

//  /**
//   * Returns a string representation of this number, as 2 terms printed
//   * by <code>Double.toHexString(double)</code>. This string
//   * representation should be lossless.
//   *
//   * @return a string representation of this number
//   */
//  public final @NonNull String toString () { return toHexString(); }
//
  //--------------------------------------------------------------------
  // state
  //--------------------------------------------------------------------
  // TODO: terms as double[] or primitive sequence?

  private final double[] terms;
  public final double term (final int i) { return terms[i]; }
  public final int nterms() { return terms.length; }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  /**
   * A <code>DD</code>> uses a representation containing two
   * double-precision values. A number x is represented as a pair of
   * doubles, x.hi and x.lo, such that the number represented by x is
   * x.hi + x.lo, where
   * <pre>
   *  |x.lo| &lt;= 0.5 *ulp(x.hi)
   * </pre>
   * and ulp(y) means "unit in the last place of y". The basic
   * arithmetic operations are implemented using convenient properties
   * of IEEE-754 floating-point arithmetic.
   */

  private final boolean checkUlp (final double s, final double t) {
    // reverse test to handle NaN
    return !((2 * Math.abs(t)) > Math.ulp(s));
  }

  /**
   * Enforce <pre>hi = a + b</pre>, <pre>lo = hi - (a + b)</pre> so that
   * <pre>|lo| &lt;= 0.5 *ulp(hi)</pre>.
   *
   * @see <a href="https://en.wikipedia.org/wiki/2Sum"Fast2Sum</a>
   */
  private APFP0 (final double... elements) {
    terms = elements.clone(); }

//    /**
//   * @link mop.java.accumulators.ZhuHayesAccumulator#twoSum
//   * <br>
//   * See <a href="https://pavpanchekha.com/blog/fast-two-sum.html>fast
//   * two sum</a>
//   * <br>
//   * See <a href="https://en.wikipedia.org/wiki/2Sum>2Sum</a>
//   */
//  public static final APFP0 twoSum (final double a, final double b) {
//    final double hi = a + b;
//    final double delta = hi - a;
//    final double lo = (a - (hi - delta)) + (b - delta);
//    return new APFP0(hi, lo);
//  }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
