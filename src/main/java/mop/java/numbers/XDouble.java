package mop.java.numbers;

import com.carrotsearch.hppc.DoubleArrayList;
import com.carrotsearch.hppc.procedures.DoubleProcedure;

/** NOTE: Different sequences of terms can represent the same rational
 * number, so <code>equals()</code>, etc., need to take that into
 * account. Shewchuk's versions of round to double
 * (either naive sum of doubles, or just the highest order term)
 * depend * on the specific terms, and thus are is not consistent for
 * different representations of the same rational.
 * <br>
 * TODO: is there a way to standardize the terms to be unique
 *  for a given rational value and round correctly.
 * <br>
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
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-05-25
 */

//@SuppressWarnings("unused")
public final class XDouble implements Comparable<XDouble> {

  /** Non-overlapping doubles, lowest order term first.
   */
  private final DoubleArrayList _terms;
  private final DoubleArrayList terms () { return _terms; }

  public final int nterms () { return _terms.size(); }
  public final double term (final int i) { return _terms.get(i); }

  //-------------------------------------------------------------------
  // DoubleArrayList
  //-------------------------------------------------------------------

  public final void forEach (final DoubleProcedure dp) {
    terms().forEach(dp); }

  //-------------------------------------------------------------------
  // Object
  //-------------------------------------------------------------------
  // Note: convert to BigFloat to get (my) expected behavior, so very
  // inefficient!
  // TODO: find some faster way to get 'correct' behavior.

  public final int hashCode () {
    return bigFloatValue().hashCode(); }

  public final boolean equals (final Object o) {
    if  (o == this) { return true; }
    if (o == null) { return false; }
    if (! (o instanceof XDouble)) { return false; }
    return bigFloatValue().equals(((XDouble) o).bigFloatValue()); }

  public final String toString () {
    final StringBuilder sb = new StringBuilder("XD(");
    forEach(x -> { sb.append(Double.toHexString(x)); sb.append(", ");});
    sb.deleteCharAt(sb.length()-1);
    sb.deleteCharAt(sb.length()-1);
    sb.append(")");
    return sb.toString(); }

  //-------------------------------------------------------------------
  // Comparable<XDouble>
  //-------------------------------------------------------------------

  @Override
  public final int compareTo (final XDouble that) {
    return bigFloatValue().compareTo(that.bigFloatValue()); }

  //--------------------------------------------------------------------
  // require IEEE 754
  //--------------------------------------------------------------------
  // Java equivalent is <code>strictfp</code> on class or methods,
  // but that has no effect after Java 17, which requires
  // all floating point calculations to have IEEE 754 semantics.
  static {
    assert (0 > "17".compareTo(System.getProperty("java.version")))
      : "Java: " + System.getProperty("java.version") +
      " not supported"; }

  //--------------------------------------------------------------------
  // finite, non-finite rational values
  //--------------------------------------------------------------------

  public static final XDouble NaN = valueOf(Double.NaN);

  public final boolean isNaN () {
    return (1 == nterms()) && Double.isNaN(term(0)); }

  // TODO: is this correct?
  public final boolean isPositive () {
    return (1 <= nterms()) && (0.0 < term(nterms()-1)); }

  public final boolean isNegative () {
    return (1 <= nterms()) && (0.0 > term(nterms()-1)); }

  public static final XDouble POSITIVE_INFINITY =
    valueOf(Double.POSITIVE_INFINITY);

  public final boolean isPositiveInfinity () {
    return (1 == nterms()) && (Double.POSITIVE_INFINITY==term(0)); }

  public static final XDouble NEGATIVE_INFINITY =
    valueOf(Double.NEGATIVE_INFINITY);

  public final boolean isNegativeInfinity () {
    return (1 == nterms()) && (Double.NEGATIVE_INFINITY==term(0)); }

  public static final boolean isFinite (final DoubleArrayList terms) {
    for (int i = 0; i < terms.size()-1; ++i) {
      if (! Double.isFinite(terms.get(i))) { return false; } }
    return true; }

  public final boolean isFinite () {
    return isFinite(terms()); }

  //--------------------------------------------------------------------

  public final XDouble add (final double b) {
    if (0.0==b) { return this; }
    if (Double.isNaN(b) || isNaN()) { return NaN; }
    if (isPositiveInfinity()) {
      if (Double.isFinite(b) || (Double.POSITIVE_INFINITY==b)) {
        return POSITIVE_INFINITY; }
      return NaN; }
    if (isNegativeInfinity()) {
      if (Double.isFinite(b) || (Double.NEGATIVE_INFINITY==b)) {
        return NEGATIVE_INFINITY; }
      return NaN; }
    assert isFinite();
    if (Double.POSITIVE_INFINITY==b) { return POSITIVE_INFINITY; }
    if (Double.NEGATIVE_INFINITY==b) { return NEGATIVE_INFINITY; }
    final DoubleArrayList sum =
      new DoubleArrayList(nterms()+1);
    double r = b;
    for (int i = 0; i < nterms(); i++) {
      final Hilo fts = Hilo.twoSum(term(i),r);
      final double s = fts.lo();
      r = fts.hi();
      if (s != 0.0) { sum.add(s); } }
    if (r != 0.0) { sum.add(r); }
    assert isFinite(sum);
    return unsafe(sum); }

  //--------------------------------------------------------------------
  /** To start, just call add(double) iteratively.
   * <br>
   * TODO: optimize based on long version of Shewchuk's paper and
   *   fast_expansion_sum_zeroelim in predicates.c
   * <br>
   * TODO: cleanup non-finite cases
   */

  public final XDouble add (final XDouble b) {
    if (isZero()) { return b; }
    if (b.isZero()) { return this; }
    if (isNaN() || b.isNaN()) { return NaN; }
    if (isPositiveInfinity()) {
      if (b.isNegativeInfinity()) { return NaN; }
      return POSITIVE_INFINITY; }
    if (isNegativeInfinity()) {
      if (b.isPositiveInfinity()) { return NaN; }
      return NEGATIVE_INFINITY; }
    // this is finite
    if (b.isPositiveInfinity()) { return POSITIVE_INFINITY; }
    if (b.isNegativeInfinity()) { return NEGATIVE_INFINITY; }
    // both are finite
    XDouble sum = this;
    for (int i = 0; i < b.nterms(); i++) {
      assert sum.isFinite();
      sum = sum.add(b.term(i)); }
    assert sum.isFinite();
    return sum; }

  // TODO: modify add to avoid instance creation
  // TODO: cleanup non-finite cases
  public final XDouble subtract (final XDouble b) {
    if (isNaN() || b.isNaN()) { return NaN; }
    if (isZero()) { return b; }
    if (b.isZero()) { return this; }
    if (isPositiveInfinity()) {
      if (b.isPositiveInfinity()) { return NaN; }
      return POSITIVE_INFINITY; }
    if (isNegativeInfinity()) {
      if (b.isNegativeInfinity()) { return NaN; }
      return NEGATIVE_INFINITY; }
    // this is finite
    if (b.isPositiveInfinity()) { return NEGATIVE_INFINITY; }
    if (b.isNegativeInfinity()) { return POSITIVE_INFINITY; }
    // both are finite
    XDouble sum = this;
    for (int i = 0; i < b.nterms(); i++) { sum = sum.add(-b.term(i)); }
    assert null != sum;
    assert sum.isFinite();
    return sum; }

  //--------------------------------------------------------------------
  // additive identity
  //--------------------------------------------------------------------
  // empty terms for ZERO

  public static final XDouble ZERO =
    new XDouble(new DoubleArrayList(0));

  public final boolean isZero () { return terms().isEmpty(); }

  //--------------------------------------------------------------------

  public final XDouble negate () {
    if (isZero()) { return ZERO; }
    if (isNaN()) { return NaN; }
    if (isPositiveInfinity()) { return NEGATIVE_INFINITY; }
    if (isNegativeInfinity()) { return POSITIVE_INFINITY; }
    final DoubleArrayList h = new DoubleArrayList(nterms());
    for (int i=0;i<nterms();i++) { h.add(-term(i)); }
    return unsafe(h); }

  // TODO: correct isPositive
  public final XDouble abs () {
    if (isNaN()) { return NaN; }
    if (isZero()) { return ZERO; }
    if (isPositive()) { return this; }
    return negate(); }

  //--------------------------------------------------------------------
  // scalar multiplication
  //--------------------------------------------------------------------
  // TODO: translate Shewchuk code to more efficient version
  // TODO: cleanup non-finite cases

  public final XDouble scale (final double b) {
    if (0.0==b) { return ZERO; }
    if (1.0==b) { return this; }
    if (Double.isNaN(b)) { return NaN; }
    if (isZero()) { return ZERO; }
    if (isNaN()) { return NaN; }
    if (Double.POSITIVE_INFINITY == b) {
      if (isPositive()) { return POSITIVE_INFINITY; }
      if (isNegative()) { return NEGATIVE_INFINITY; }
      return NaN; }
    if (Double.NEGATIVE_INFINITY == b) {
      if (isPositive()) { return NEGATIVE_INFINITY; }
      if (isNegative()) { return POSITIVE_INFINITY; }
      return NaN; }
    assert Double.isFinite(b);
    if (isPositiveInfinity()) {
      if (0.0<b) { return POSITIVE_INFINITY; }
      if (0.0>b) { return NEGATIVE_INFINITY; }
      return NaN; }
    if (isNegativeInfinity()) {
      if (0.0<b) { return NEGATIVE_INFINITY; }
      if (0.0>b) { return POSITIVE_INFINITY; }
      return NaN; }
    assert isFinite() : "\n" + this;
    XDouble result = ZERO;
    for  (int i=0;i<nterms();i++) {
      final Hilo ab = Hilo.twoProduct(term(i),b);
      if (ab.isNaN()) { return NaN; }
      result = result.add(ab.hi());
      result = result.add(ab.lo()); }
    return result; }

  //--------------------------------------------------------------------
  /** See either version of Shewchuk's paper for details.
   */

  public final double estimate () {
    // sum smallest to largest
    double sum =0.0;
    for (int i = 0; i < nterms(); i++) { sum += term(i); }
    return sum; }

  // TODO: might be different from Shewchuk's estimate
  public final double doubleValue () {
    return bigFloatValue().doubleValue(); }

  // TODO: might be different from Shewchuk's estimate
  public final float floatValue () {
    return bigFloatValue().floatValue(); }

  //--------------------------------------------------------------------
  // TODO: isZero, isFinite, isNaN methods
  //  This is used a lot, so look into how to make it faster
  //  (or unnecessary).

  public final BigFloat bigFloatValue () {
    if (isZero()) { return BigFloat.ZERO; }
    BigFloat sum = BigFloat.valueOf(term(0));
    for (int i = 1; i < nterms(); i++) { sum = sum.add(term(i)); }
    return sum; }

  //--------------------------------------------------------------------
  // private construction
  //--------------------------------------------------------------------

  private static final boolean
  increasingNonOverlapping (final DoubleArrayList t) {
    for (int i=0;i<t.size()-1;i++) {
      if ((2*Math.abs(t.get(i+1))) <= Math.ulp(t.get(i))) {
        return false; } }
    return true; }

  private XDouble (final DoubleArrayList terms) {
    assert increasingNonOverlapping(terms) : terms;
    // DANGER: terms is mutable!!!!
    _terms = terms; }

  private static final XDouble unsafe (final DoubleArrayList terms) {
    terms.removeAll(0.0);
    if (terms.isEmpty()) { assert null != ZERO; return ZERO; }
    return new XDouble(terms); }

  // Not really safe unless non-overlapping is enforced
//  private static final XDouble safe (final DoubleArrayList terms) {
//    return unsafe(terms.clone()); }

  public static final XDouble valueOf (final double x) {
    return unsafe(DoubleArrayList.from(x)); }

  public static final XDouble valueOf (final double x0,
                                       final double x1) {
    final Hilo xx = Hilo.twoSum(x0,x1);
    return unsafe(DoubleArrayList.from(xx.lo(),xx.hi())); }

  public static final XDouble valueOf (final Hilo x) {
    return unsafe(DoubleArrayList.from(x.lo(),x.hi())); }

  // TODO: better way to do this, or to generate high precision values
  //  directly
//  public static final XDouble valueOf (final BigFloat bf) {
//    double a = bf.doubleValue();
//    XDouble x = valueOf(a);
//    if (! Double.isFinite(a)) { return x; }
//    while (0.0 != (a = bf.add(-a).doubleValue())) {
//      x = x.add(a); }
//    return x; }

  // returns double[3]
  // #define Two_One_Diff(a1, a0, b, x2, x1, x0) \
  //  Two_Diff(a0, b , _i, x0); \
  //  Two_Sum( a1, _i, x2, x1)
//  private static final double[] twoOneDiff (final double ahi,
//                                            final double alo,
//                                            final double b) {
//    final Hilo ix0 = Hilo.twoDiff(alo, b);
//    final Hilo x2x1 = Hilo.twoSum(ahi, ix0.hi());
//    return new double[] {ix0.lo(), x2x1.lo(), x2x1.hi(), }; }

  // Two_Two_Diff(axby1, axby0, bxay1, bxay0,
  //              ab[3], ab[2], ab[1], ab[0]);

//  public static final XDouble twoTwoDiff (final Hilo a,
//                                          final Hilo b) {
//    // Two_One_Diff(a1, a0, b0, _j, _0, x0);
//    final double[] x00j = twoOneDiff(a.hi(),a.lo(),b.lo());
//    // Two_One_Diff(_j, _0, b.hi(), x3, x2, x1)
//    final double[] x123 = twoOneDiff(x00j[2],x00j[1],b.hi());
//    final DoubleArrayList terms = new DoubleArrayList(4);
//    if (0.0!=x00j[0]) { terms.add(x00j[0]); }
//    for (int i=1;i<x123.length;i++) {
//      if (0.0!= x123[i]) { terms.add(x123[i]); }  }
//    if (terms.isEmpty()) { return ZERO; }
//    return unsafe(terms); }

  // FMA version
  public static final XDouble twoProduct (final double a,
                                          final double b) {
    final double hi = (a * b);
    final double lo = Math.fma(a,b,-hi);
    if (0.0==hi)  { return ZERO; }
    return unsafe(DoubleArrayList.from(lo, hi)); }

  public static final XDouble crossProduct (final double[] a,
                                            final double[]  b) {
    final XDouble axby = twoProduct(a[0], b[1]);
    final XDouble bxay = twoProduct(b[0], a[1]);
    return axby.subtract(bxay); }

//  #define Two_Two_Product(a1, a0, b1, b0, x7, x6, x5, x4, x3, x2, x1, x0) \
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
//  Two_Sum(_2, _j, _i, _1); \(
//  Two_Sum(_l, _i, _m, _2); \
//  Two_Sum(_1, _k, _i, x4); \
//  Two_Sum(_2, _i, _k, x5); \
//  Two_Sum(_m, _k, x7, x6)

  public static final XDouble twoTwoProduct (final Hilo a,
                                             final Hilo b) {
    final Hilo a1 = Hilo.split(a.hi());
    final Hilo a0 = Hilo.split(a.lo());
    final Hilo b1 = Hilo.split(b.hi());
    final Hilo b0 = Hilo.split(b.lo());

    final Hilo _ix0 = Hilo.twoProduct2Presplit(a.lo(),a0,b.lo(),b0);

    Hilo _j_0 = Hilo.twoProduct2Presplit(a.hi(),a1,b.lo(),b0);
    Hilo _k_1 = Hilo.twoSum(_ix0.hi(), _j_0.lo());
    Hilo _l_2 = Hilo.fastTwoSum(_j_0.hi(), _k_1.hi());

    final Hilo _i_0 = Hilo.twoProduct2Presplit(a.lo(),a0,b.hi(),b1);
    final Hilo _kx1 = Hilo.twoSum(_k_1.lo(), _i_0.lo());
    final Hilo _j_1 = Hilo.twoSum(_l_2.lo(), _kx1.hi());
    Hilo _m_2 = Hilo.twoSum(_l_2.hi(), _j_1.hi());

    _j_0 = Hilo.twoProduct2Presplit(a.hi(),a1,b.hi(),b1);
    final Hilo _n_0 = Hilo.twoSum(_i_0.hi(),_j_0.lo());
    final Hilo _ix2 = Hilo.twoSum(_j_1.lo(),_n_0.lo());
    _k_1 = Hilo.twoSum(_m_2.lo(),_ix2.hi());
    _l_2 = Hilo.twoSum(_m_2.hi(),_k_1.hi());
    final Hilo _k_0 = Hilo.twoSum(_j_0.hi(),_n_0.hi());
    final Hilo _jx3 = Hilo.twoSum(_k_1.lo(),_k_0.lo());
    final Hilo _i_1 = Hilo.twoSum(_l_2.hi(),_jx3.hi());
    _m_2 = Hilo.twoSum(_l_2.hi(), _i_1.hi());
    final Hilo _ix4 = Hilo.twoSum(_i_1.lo(),_k_0.hi());
    final Hilo _kx5 = Hilo.twoSum(_l_2.lo(),_ix4.hi());
    final Hilo x7x6 = Hilo.twoSum(_m_2.hi(),_kx5.hi());

    return unsafe(DoubleArrayList.from(
      _ix0.lo(),
      _kx1.lo(),
      _ix2.lo(),
      _jx3.lo(),
      _ix4.lo(),
      _kx5.lo(),
      x7x6.lo(),
      x7x6.hi())); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
