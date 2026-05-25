package mop.java.numbers;

import com.carrotsearch.hppc.DoubleArrayList;
import com.carrotsearch.hppc.procedures.DoubleProcedure;

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
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-05-22
 */

@SuppressWarnings("unused")
public final class XDouble implements Comparable<XDouble> {

  /** Non-overlapping doubles, lowest order term first.
   */
  private final DoubleArrayList _terms;

  public final int nterms () { return _terms.size(); }
  public final double term (final int i) { return _terms.get(i); }

  //-------------------------------------------------------------------
  // DoubleArrayList
  //-------------------------------------------------------------------

  public final void forEach (final DoubleProcedure dp) {
    _terms.forEach(dp); }

  //-------------------------------------------------------------------
  // Object
  //-------------------------------------------------------------------

  public final int hashCode () { return _terms.hashCode(); }

  public final boolean equals (final Object o) {
    if  (o == this) { return true; }
    if (o == null) { return false; }
    if (! (o instanceof XDouble)) { return false; }
    return _terms.equals(((XDouble) o)._terms); }

  public final String toString () {
    final StringBuilder sb = new StringBuilder("XDouble(");
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
    int i=nterms()-1;
    int j=that.nterms()-1;
    while (i>=0 && j>=0) {
      final double a = term(i);
      final double b = that.term(i);
      if (a < b) { return -1; }
      if (a > b) { return 1; }
      i--; j--; }
    while (i>=0) {
      final double a = term(i);
      if (a < 0.0) { return -1; }
      if (a > 0.0) { return 1; }
      i--; }
    while (j>=0) {
      final double b = that.term(i);
      if (0.0 < b) { return -1; }
      if (0.0 > b) { return 1; }
      j--; }
    return 0; }

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

  public final XDouble add (final double b) {
    final DoubleArrayList sum =
      new DoubleArrayList(nterms()+1);
    double r = b;
    for (int i = 0; i < nterms(); i++) {
      final Hilo fts = Hilo.twoSum(term(i),r);
      final double s = fts.lo();
      r = fts.hi();
      if (s != 0.0) { sum.add(s); } }
    if (r != 0.0) { sum.add(r); }
    if (sum.isEmpty()) { return ZERO; }
    return new XDouble(sum); }

  //--------------------------------------------------------------------
  /** To start, just call add(double) iteratively.
   * <br>
   * TODO: optimize based on long version of Shewchuk's paper and
   *   fast_expansion_sum_zeroelim in predicates.c
   */

  public final XDouble add (final XDouble b) {
    XDouble sum = this;
    for (int i = 0; i < b.nterms(); i++) { sum = sum.add(b.term(i)); }
    return sum; }

  // TODO: modify add to avoid instance creation
  public final XDouble subtract (final XDouble b) {
    XDouble sum = this;
    for (int i = 0; i < b.nterms(); i++) { sum = sum.add(-b.term(i)); }
    return sum; }

  //--------------------------------------------------------------------

  public final XDouble negate () {
    final DoubleArrayList h = new DoubleArrayList(nterms());
    for (int i=0;i<nterms();i++) { h.add(-term(i)); }
    return unsafe(h); }

  public final XDouble abs () {
    final double hi = term(nterms()-1);
    if (Double.isNaN(hi)) { return this; }
    if (hi >= 0) { return this; }
    return negate(); }

  //--------------------------------------------------------------------

  public final XDouble scale (final double b) {
    if (0.0==b) { return ZERO; }
    if (1.0==b) { return this; }
    if (ZERO.equals(this)) { return this; }
    final DoubleArrayList hi = new DoubleArrayList(nterms());
    final DoubleArrayList lo = new DoubleArrayList(nterms());
    for  (int i=0;i<nterms();i++) {
      final Hilo ab = Hilo.twoProduct(term(i),b);
      hi.add(ab.hi());
      lo.add(ab.lo()); }
    return unsafe(hi).add(unsafe(lo)); }

  //--------------------------------------------------------------------
  /** See either version of Shewchuk's paper for details.
   */

  public final double estimate () {
    // sum smallest to largest
    double sum =0.0;
    for (int i = 0; i < nterms(); i++) { sum += term(i); }
    return sum; }

  // TODO: might be different from Shewchuk's estimate
  public final double doubleValue () { return estimate(); }

  // TODO: might be different from Shewchuk's estimate
  public final float floatValue () { return (float) doubleValue(); }

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
    assert (! terms.isEmpty()) : terms;
    assert increasingNonOverlapping(terms) : terms;
    // DANGER: terms is mutable!!!!
    _terms = terms; }

  private static final XDouble unsafe (final DoubleArrayList terms) {
    terms.removeAll(0.0);
    if (terms.isEmpty()) { return ZERO; }
    return new XDouble(terms); }

  private static final XDouble safe (final DoubleArrayList terms) {
    return unsafe(terms.clone()); }

  public static final XDouble valueOf (final double x) {
    return unsafe(DoubleArrayList.from(x)); }

  public static final XDouble valueOf (final Hilo x) {
    return unsafe(DoubleArrayList.from(x.lo(),x.hi())); }

  // TODO: empty terms for ZERO?
  public static final XDouble ZERO =
    new XDouble(DoubleArrayList.from(0.0));

  public static final XDouble NaN = valueOf(Double.NaN);

  public static final XDouble POSITIVE_INFINITY =
    valueOf(Double.POSITIVE_INFINITY);

  public static final XDouble NEGATIVE_INFINITY =
    valueOf(Double.NEGATIVE_INFINITY);

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
  private static final double[] twoOneDiff (final double ahi,
                                            final double alo,
                                            final double b) {
    final Hilo ix0 = Hilo.twoDiff(alo, b);
    final Hilo x2x1 = Hilo.twoSum(ahi, ix0.hi());
    return new double[] {ix0.lo(), x2x1.lo(), x2x1.hi(), }; }

  // Two_Two_Diff(axby1, axby0, bxay1, bxay0,
  //              ab[3], ab[2], ab[1], ab[0]);

  public static final XDouble twoTwoDiff (final Hilo a,
                                          final Hilo b) {
    // Two_One_Diff(a1, a0, b0, _j, _0, x0);
    final double[] x00j = twoOneDiff(a.hi(),a.lo(),b.lo());
    // Two_One_Diff(_j, _0, b.hi(), x3, x2, x1)
    final double[] x123 = twoOneDiff(x00j[2],x00j[1],b.hi());
    final DoubleArrayList terms = new DoubleArrayList(4);
    if (0.0!=x00j[0]) { terms.add(x00j[0]); }
    for (int i=1;i<x123.length;i++) {
      if (0.0!= x123[i]) { terms.add(x123[i]); }  }
    if (terms.isEmpty()) { return ZERO; }
    return unsafe(terms); }

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


  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
