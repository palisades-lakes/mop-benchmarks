package mop.java.numbers;

import com.carrotsearch.hppc.DoubleArrayList;
import com.carrotsearch.hppc.procedures.DoubleProcedure;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Major difference from predicates.c is attempt to handle
 * non-finite values, under- and over-flow, etc.
 * <br>
 * TODO: better testing of non-finite and under/over float cases.
 * <br>
 * Also fix a number of places where predicates.c
 * make unchecked assumptions about, for example, always having at
 * least 2 terms. It also goes off the end of arrays
 * in a number of places, getting away with that in C because the
 * illegal values are never used.
 * <br>
 * NOTE: Different sequences of terms can represent the same rational
 * number, so <code>equals()</code>, etc., need to take that into
 * account. Shewchuk's versions of round to double
 * (either naive sum of doubles, or just the highest order term)
 * depend on the specific terms, and thus are is not consistent for
 * different representations of the same rational.
 * <br>
 * TODO: is there a way to standardize the terms to be unique
 *  for a given rational value and round correctly.
 *  See compress(). Not clear if this is the answer.
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
 * </a></li>
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
 * @version 2026-08-25
 */

public final class XDouble
  implements Comparable<XDouble>, Ringlike<XDouble> {

  /** Non-overlapping doubles, lowest order term first.
   */
  private final DoubleArrayList _terms;
  private final DoubleArrayList terms () { return _terms; }

  public final int nterms () { return _terms.size(); }
  public final double term (final int i) { return _terms.get(i); }
  //  public final double mostSignificantTerm () {
//    return _terms.get(_terms.size()-1); }
  public final double term3 () {
    return (4 > nterms()) ? 0.0 : term(3); }

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
    if (nterms()>0) {
      sb.deleteCharAt(sb.length()-1);
      sb.deleteCharAt(sb.length()-1); }
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

  public static final XDouble NaN =
    new XDouble(DoubleArrayList.from(Double.NaN));

  public final boolean isNaN () {
    return (1 == nterms()) && Double.isNaN(term(0)); }

  // TODO: is this correct?
  public final boolean isPositive () {
    return (1 <= nterms()) && (0.0 < term(nterms()-1)); }

  public final boolean isNegative () {
    return (1 <= nterms()) && (0.0 > term(nterms()-1)); }

  public static final XDouble POSITIVE_INFINITY =
    new XDouble(DoubleArrayList.from(Double.POSITIVE_INFINITY));

  public final boolean isPositiveInfinity () {
    return (1 == nterms()) && (Double.POSITIVE_INFINITY==term(0)); }

  public static final XDouble NEGATIVE_INFINITY =
    new XDouble(DoubleArrayList.from(Double.NEGATIVE_INFINITY));

  public final boolean isNegativeInfinity () {
    return (1 == nterms()) && (Double.NEGATIVE_INFINITY==term(0)); }

  public static final boolean isFinite (final DoubleArrayList terms) {
    for (int i = 0; i < terms.size()-1; i++) {
      if (! Double.isFinite(terms.get(i))) { return false; } }
    return true; }

  public final boolean isFinite () {
    return isFinite(terms()); }

  //--------------------------------------------------------------------
  // #define Two_One_Sum(a1, a0, b, x2, x1, x0) \
//  Two_Sum(a0, b , _i, x0); \
//  Two_Sum(a1, _i, x2, x1)

  private static final double[] twoOneSum (final double a1,
                                           final double a0,
                                           final double b) {
    final Hilo _ix0 = Hilo.sum(a0, b);
    final Hilo x1x2 = Hilo.sum(a1, _ix0.hi());
    return new double[] { _ix0.lo(), x1x2.lo(), x1x2.hi(), }; }

// #define Two_Two_Sum(a1, a0, b1, b0, x3, x2, x1, x0) \
//  Two_One_Sum(a1, a0, b0, _j, _0, x0); \
//  Two_One_Sum(_j, _0, b1, x3, x2, x1)

  public static final XDouble sum (final Hilo a,
                                   final Hilo b) {
    final double[] ab0 = twoOneSum(a.hi(),a.lo(),b.lo());
    final double[] ab1 = twoOneSum(ab0[0],ab0[1],b.hi());
    final DoubleArrayList terms = DoubleArrayList.from(
      ab0[2],ab1[0],ab1[1],ab1[2]);
    //System.out.println(terms);
//    try {
      unsafeCompress(terms);
//    }
//    catch (final Throwable e) {
//      System.err.println("error:" + terms);
//      throw e; }
    return unsafe(terms); }

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
    if (Double.POSITIVE_INFINITY==b) { return POSITIVE_INFINITY; }
    if (Double.NEGATIVE_INFINITY==b) { return NEGATIVE_INFINITY; }
    if (isZero()) { return valueOf(b); }
    //assert isFinite();
    final DoubleArrayList sum =
      new DoubleArrayList(nterms()+1);
    double Q = b;
    for (int i = 0; i < nterms(); i++) {
      final Hilo fts = Hilo.sum(Q, term(i));
      Q = fts.hi();
      final double hh = fts.lo();
      if (hh != 0.0) { sum.add(hh); } }
    if (Q != 0.0)  { sum.add(Q); }
    unsafeCompress(sum);
    return unsafe(sum); }

  //--------------------------------------------------------------------
  /** See fast_expansion_sum_zeroelim in Shewchuk's predicates.c.
   * Assumes inputs are finite.
   * Public (safe) interface is XDouble.add(XDouble).
   */

  private static final DoubleArrayList
  fastAdd  (final DoubleArrayList e,
            final DoubleArrayList f) {

    final int elen = e.size();
    if (0==elen) { return f.clone(); }
    final int flen = f.size();
    if (0==flen) { return e.clone(); }
    final DoubleArrayList h =
      new DoubleArrayList(elen + flen);
    double enow = e.get(0);
    double fnow = f.get(0);
    int eindex = 0;
    int findex = 0;
    double Q;
    if ((fnow > enow) == (fnow > -enow)) {
      Q = enow;
      eindex++;
      if (eindex<e.size()) { enow = e.get(eindex); } }
    else {
      Q = fnow;
      findex++;
      if (findex<f.size()) { fnow = f.get(findex); } }

    if ((eindex < elen) && (findex < flen)) {
      Hilo Qnew_hh;
      if ((fnow > enow) == (fnow > -enow)) {
        //Fast_Two_Sum(enow, Q, Qnew, hh);
        Qnew_hh = Hilo.sum(enow,Q);
        eindex++;
        if (eindex<e.size()) { enow = e.get(eindex); } }
      else {
        //Fast_Two_Sum(fnow, Q, Qnew, hh);
        Qnew_hh = Hilo.sum(fnow,Q);
        findex++;
        if (findex<f.size()) { fnow = f.get(findex); } }
      Q = Qnew_hh.hi();
      double hh = Qnew_hh.lo();
      if (hh != 0.0) { h.add(hh); }

      while ((eindex < elen) && (findex < flen)) {
        if ((fnow > enow) == (fnow > -enow)) {
          //Two_Sum(Q, enow, Qnew, hh);
          Qnew_hh = Hilo.sum(Q,enow);
          eindex++;
          if (eindex<e.size()) { enow = e.get(eindex); } }
        else {
          //Two_Sum(Q, fnow, Qnew, hh);
          Qnew_hh = Hilo.sum(Q,fnow);
          findex++;
          if (findex<f.size()) { fnow = f.get(findex); } }
        Q = Qnew_hh.hi();
        hh = Qnew_hh.lo();
        if (hh != 0.0) { h.add(hh); } } }
    while (eindex < elen) {
      //Two_Sum(Q, enow, Qnew, hh);
      final Hilo Qnew_hh = Hilo.sum(Q,enow);
      eindex++;
      if (eindex<e.size()) { enow = e.get(eindex); }
      Q = Qnew_hh.hi();
      final double hh = Qnew_hh.lo();
      if (hh != 0.0) { h.add(hh); } }
    while (findex < flen) {
      //Two_Sum(Q, fnow, Qnew, hh);
      final Hilo Qnew_hh = Hilo.sum(Q,fnow);
      findex++;
      if (findex<f.size()) { fnow = f.get(findex); }
      Q = Qnew_hh.hi();
      final double hh = Qnew_hh.lo();
      if (hh != 0.0) { h.add(hh); } }
    if ((Q != 0.0) || h.isEmpty()) { h.add(Q); }
    return h; }

  /** See fast_expansion_sum_zeroelim in Shewchuk's predicates.c
   * <br>
   * Difference is checking for and handling overflows.
   * TODO: underflows?
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
    final DoubleArrayList sum = fastAdd(terms(),b.terms());
    unsafeCompress(sum);
    return unsafe(sum); }

//  /** To start, just call add(double) iteratively.
//   * <br>
//   * TODO: optimize based on long version of Shewchuk's paper and
//   *   fast_expansion_sum_zeroelim in predicates.c
//   */
//
//  public final XDouble add (final XDouble b) {
//    if (isZero()) { return b; }
//    if (b.isZero()) { return this; }
//    if (isNaN() || b.isNaN()) { return NaN; }
//    if (isPositiveInfinity()) {
//      if (b.isNegativeInfinity()) { return NaN; }
//      return POSITIVE_INFINITY; }
//    if (isNegativeInfinity()) {
//      if (b.isPositiveInfinity()) { return NaN; }
//      return NEGATIVE_INFINITY; }
//    // this is finite
//    if (b.isPositiveInfinity()) { return POSITIVE_INFINITY; }
//    if (b.isNegativeInfinity()) { return NEGATIVE_INFINITY; }
//    // both are finite
//    XDouble sum = this;
//    for (int i = 0; i < b.nterms(); i++) { sum = sum.add(b.term(i)); }
//    // might not be true if there's an overflow
//    assert sum.isFinite();
//    return sum; }

  // TODO: vargs?

  public final static XDouble sum (final XDouble a,
                                   final XDouble b,
                                   final XDouble c,
                                   final XDouble d) {
    final DoubleArrayList abcd = fastAdd(fastAdd(a.terms(),b.terms()),
                                         fastAdd(c.terms(),d.terms()));
    unsafeCompress(abcd);
    return unsafe(abcd); }

  //--------------------------------------------------------------------
  // #define Two_One_Diff(a1, a0, b, x2, x1, x0) \
  //  Two_Diff(a0, b , _i, x0); \
  //  Two_Sum( a1, _i, x2, x1)

  private static final double[] twoOneDiff (final double ahi,
                                            final double alo,
                                            final double b) {
    final Hilo _ix0 = Hilo.subtract(alo, b);
    final Hilo x2x1 = Hilo.sum(ahi, _ix0.hi());
    return new double[] { _ix0.lo(), x2x1.lo(), x2x1.hi(), }; }

  public static final XDouble subtract (final Hilo a,
                                        final Hilo b) {
    // Two_One_Diff(a1, a0, b0, _j, _0, x0);
    final double[] _j_0x0 = twoOneDiff(a.hi(),a.lo(),b.lo());
    // Two_One_Diff(_j, _0, b1, x3, x2, x1)
    final double[] x123 = twoOneDiff(_j_0x0[2],_j_0x0[1],b.hi());
    // TODO: special case 4 terms
    final DoubleArrayList terms =
      DoubleArrayList.from(_j_0x0[0],x123[0],x123[1],x123[2]);
    unsafeCompress(terms);
    return unsafe(terms); }

  //--------------------------------------------------------------------
  // saving negate and instance creation

  private final DoubleArrayList fastSubtract  (final DoubleArrayList e,
                                               final DoubleArrayList f) {

    final int elen = e.size();
    final int flen = f.size();
    final DoubleArrayList h =
      new DoubleArrayList(elen + flen);
    double enow = e.get(0);
    double fnow = -f.get(0);
    int eindex = 0;
    int findex = 0;
    double Q;
    if ((fnow > enow) == (fnow > -enow)) {
      Q = enow;
      eindex++;
      if (eindex<e.size()) { enow = e.get(eindex); } }
    else {
      Q = fnow;
      findex++;
      if (findex<f.size()) { fnow = -f.get(findex); } }

    if ((eindex < elen) && (findex < flen)) {
      Hilo Qnew_hh;
      if ((fnow > enow) == (fnow > -enow)) {
        //Fast_Two_Sum(enow, Q, Qnew, hh);
        Qnew_hh = Hilo.sum(enow,Q);
        eindex++;
        if (eindex<e.size()) { enow = e.get(eindex); } }
      else {
        //Fast_Two_Sum(fnow, Q, Qnew, hh);
        Qnew_hh = Hilo.sum(fnow,Q);
        findex++;
        if (findex<f.size()) { fnow = -f.get(findex); } }
      Q = Qnew_hh.hi();
      double hh = Qnew_hh.lo();
      if (hh != 0.0) { h.add(hh); }

      while ((eindex < elen) && (findex < flen)) {
        if ((fnow > enow) == (fnow > -enow)) {
          //Two_Sum(Q, enow, Qnew, hh);
          Qnew_hh = Hilo.sum(Q,enow);
          eindex++;
          if (eindex<e.size()) { enow = e.get(eindex); } }
        else {
          //Two_Sum(Q, fnow, Qnew, hh);
          Qnew_hh = Hilo.sum(Q,fnow);
          findex++;
          if (findex<f.size()) { fnow = -f.get(findex); } }
        Q = Qnew_hh.hi();
        hh = Qnew_hh.lo();
        if (hh != 0.0) { h.add(hh); } } }
    while (eindex < elen) {
      //Two_Sum(Q, enow, Qnew, hh);
      final Hilo Qnew_hh = Hilo.sum(Q,enow);
      eindex++;
      if (eindex<e.size()) { enow = e.get(eindex); }
      Q = Qnew_hh.hi();
      final double hh = Qnew_hh.lo();
      if (hh != 0.0) { h.add(hh); } }
    while (findex < flen) {
      //Two_Sum(Q, fnow, Qnew, hh);
      final Hilo Qnew_hh = Hilo.sum(Q,fnow);
      findex++;
      if (findex<f.size()) { fnow = -f.get(findex); }
      Q = Qnew_hh.hi();
      final double hh = Qnew_hh.lo();
      if (hh != 0.0) { h.add(hh); } }
    if ((Q != 0.0) || h.isEmpty()) { h.add(Q); }
    return h; }

  public final XDouble subtract (final XDouble b) {
    if (isZero()) { return b.negate(); }
    if (b.isZero()) { return this; }
    if (isNaN() || b.isNaN()) { return NaN; }
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
    final DoubleArrayList dif = fastSubtract(terms(),b.terms());
    unsafeCompress(dif);
    return unsafe(dif); }

  //--------------------------------------------------------------------
  // additive identity
  //--------------------------------------------------------------------
  // empty terms for ZERO

  public static final XDouble ZERO =
    new XDouble(new DoubleArrayList(0));

  public final boolean isZero () { return terms().isEmpty(); }

  //--------------------------------------------------------------------
  // additive inverse
  //--------------------------------------------------------------------

  public final XDouble negate () {
    if (isZero()) { return ZERO; }
    if (isNaN()) { return NaN; }
    if (isPositiveInfinity()) { return NEGATIVE_INFINITY; }
    if (isNegativeInfinity()) { return POSITIVE_INFINITY; }
    final DoubleArrayList h = terms().clone();
    for (int i=0;i<nterms();i++) {  h.set(i,-h.get(i)); }
    return unsafe(h); }

  // TODO: correct isPositive
  public final XDouble abs () {
    if (isNaN()) { return NaN; }
    if (isZero()) { return ZERO; }
    if (isPositive()) { return this; }
    return negate(); }

  //--------------------------------------------------------------------
  // multiplication
  //--------------------------------------------------------------------
  // multiplicative identity
  //--------------------------------------------------------------------

  public static final XDouble ONE =
    new XDouble(DoubleArrayList.from(1.0));

  public final boolean isOne () {
    return (1 == terms().size()) && (1.0 == terms().get(0)); }

//    public static final XDouble MINUS_ONE =
//    new XDouble(DoubleArrayList.from(-1.0));

  public final boolean isMinusOne () {
    return (1 == terms().size()) && (-1.0 == terms().get(0)); }

  //--------------------------------------------------------------------

  public static final XDouble product (final double a,
                                       final double b) {
    final Hilo hilo = Hilo.product(a, b);
    final DoubleArrayList terms =
      DoubleArrayList.from(hilo.lo(), hilo.hi());
    // TODO: this should be unnecessary, or have a simple alternative
    unsafeCompress(terms);
    return unsafe(terms); }

//#define Two_Product_Presplit(a, b, bhi, blo, x, y) \
//  x = (double) (a * b); \
//  Split(a, ahi, alo); \
//  err1 = x - (ahi * bhi); \
//  err2 = err1 - (alo * bhi); \
//  err3 = err2 - (ahi * blo); \
//  y = (alo * blo) - err3

  // TODO: call twoProduct2PreSplit?
//  private static final Hilo twoProductPresplit (final double a,
//                                                final double b,
//                                                final Hilo bhilo) {
//    final double x = a * b;
//    final Hilo ahilo = Hilo.split (a);
//    final double err1 = x - (ahilo.hi() * bhilo.hi());
//    final double err2 = err1 - (ahilo.lo() * bhilo.hi());
//    final double err3 = err2 - (ahilo.hi() * bhilo.lo());
//    final double y = (ahilo.lo() * bhilo.lo()) - err3;
//
//    return new Hilo(x,y);}

  //  #define Two_Product_2Presplit(a, ahi, alo, b, bhi, blo, x, y) \
  //  x = (double) (a * b); \
  //  err1 = x - (ahi * bhi); \
  //  err2 = err1 - (alo * bhi); \
  //  err3 = err2 - (ahi * blo); \
  //  y = (alo * blo) - err3

  // TODO: is this worth the complexity?
  //  Check whether aSplit and bSplit are used anywhere else.
//  private static final Hilo twoProduct2Presplit (final double a,
//                                                 final Hilo aSplit,
//                                                 final double b,
//                                                 final Hilo bSplit) {
//    final double hi = a * b;
//    final double err1 = hi - (aSplit.hi() * bSplit.hi());
//    final double err2 = err1 - (aSplit.lo() * bSplit.hi());
//    final double err3 = err2 - (aSplit.hi() * bSplit.lo());
//    final double lo = (aSplit.lo() * bSplit.lo()) - err3;
//    // TODO: call twoSum to enforce ulp constraint?
//    //  or replace ulp constraint --- is non-overlapping different?
//    return new Hilo(hi,lo); }

  //--------------------------------------------------------------------

//  public static final XDouble product (final Hilo a,
//                                       final double b) {
// //    Split(b, bhi, blo); \
//    final Hilo bHilo = Hilo.split(b);
// //    Two_Product_Presplit(a0, b, bhi, blo, _i, x0); \
//    final Hilo _ix0 = twoProductPresplit(a.lo(), b, bHilo);
// //    Two_Product_Presplit(a1, b, bhi, blo, _j, _0); \
//    final Hilo _j_0 = twoProductPresplit(a.hi(), b, bHilo);
// //    Two_Sum(_i, _0, _k, x1);
//    final Hilo _kx1 = Hilo.sum(_ix0.hi(), _j_0.lo());
// //    Fast_Two_Sum(_j, _k, x3, x2)
//    final Hilo x3x2 = Hilo.fastSum(_j_0.hi(), _kx1.hi());
//    final DoubleArrayList terms = DoubleArrayList.from(
//      _ix0.lo(), _kx1.lo(), x3x2.lo(), x3x2.hi());
//    // TODO: special case 4 terms
//    unsafeCompress(terms);
//    return unsafe(terms); }

  // FMA version
  public static final XDouble product (final Hilo a,
                                       final double b) {
    final Hilo _ix0 = Hilo.product(a.lo(), b);
    final Hilo _j_0 = Hilo.product(a.hi(), b);
//    Two_Sum(_i, _0, _k, x1);
    final Hilo _kx1 = Hilo.sum(_ix0.hi(), _j_0.lo());
//    Fast_Two_Sum(_j, _k, x3, x2)
    final Hilo x3x2 = Hilo.fastSum(_j_0.hi(), _kx1.hi());
    final DoubleArrayList terms = DoubleArrayList.from(
      _ix0.lo(), _kx1.lo(), x3x2.lo(), x3x2.hi());
    // TODO: special case 4 terms
    unsafeCompress(terms);
    return unsafe(terms); }

  //--------------------------------------------------------------------
  // FMA version
  public static final XDouble product (final Hilo a,
                                       final Hilo b) {
    final double[] ab = new double[8];
    final double a0 = a.lo();
    final double b0 = b.lo();
    final double a1 = a.hi();
    final double b1 = b.hi();
    final Hilo a0b0 = Hilo.product(a0,b0);
    final Hilo a1b0 = Hilo.product(a1,b0);
    final Hilo a0b1 = Hilo.product(a0,b1);
    final Hilo a1b1 = Hilo.product(a1,b1);
    ab[0] = a0b0.lo();
    final Hilo s0 = Hilo.sum(a0b0.hi(), a1b0.lo());
    final Hilo s1 = Hilo.fastSum(a1b0.hi(), s0.hi());
    final Hilo s2 = Hilo.sum(s0.lo(), a0b1.lo());
    ab[1] = s2.lo();
    final Hilo s3 = Hilo.sum(s1.lo(), s2.hi());
    final Hilo s4 = Hilo.sum(s1.hi(), s3.hi());
    final Hilo s5 = Hilo.sum(a0b1.hi(), a1b1.lo());
    final Hilo s6 = Hilo.sum(s3.lo(), s5.lo());
    ab[2] = s6.lo();
    final Hilo s7 = Hilo.sum(s4.lo(), s6.hi());
    final Hilo s8 = Hilo.sum(s4.hi(), s7.hi());
    final Hilo s9 = Hilo.sum(a1b1.hi(), s5.hi());
    final Hilo s10 = Hilo.sum(s7.lo(), s9.lo());
    ab[3] = s10.lo();
    final Hilo s11 = Hilo.sum(s8.lo(), s10.hi());
    final Hilo s12 = Hilo.sum(s8.hi(), s11.hi());
    final Hilo s13 = Hilo.sum(s11.lo(), s9.hi());
    ab[4] = s13.lo();
    final Hilo s14 = Hilo.sum(s12.lo(), s13.hi());
    ab[5] = s14.lo();
    final Hilo s15 = Hilo.sum(s12.hi(), s14.hi());
    ab[7] = s15.hi();
    ab[6] = s15.lo();
    final DoubleArrayList terms = DoubleArrayList.from(ab);
    unsafeCompress(terms);
    return unsafe(terms); }

//  public static final XDouble product (final Hilo a,
//                                       final Hilo b) {
//    final double[] ab = new double[8];
//    final double a0 = a.lo();
//    final double b0 = b.lo();
//    final double a1 = a.hi();
//    final double b1 = b.hi();
//    final Hilo a1S = Hilo.split(a1);
//    final Hilo a0S = Hilo.split(a0);
//    final Hilo b1S = Hilo.split(b1);
//    final Hilo b0S = Hilo.split(b0);
//    final Hilo a0b0 = twoProduct2Presplit(a0,a0S,b0,b0S);
//    final Hilo a1b0 = twoProduct2Presplit(a1,a1S,b0,b0S);
//    final Hilo a0b1 = twoProduct2Presplit(a0,a0S,b1,b1S);
//    final Hilo a1b1 = twoProduct2Presplit(a1,a1S,b1,b1S);
//    ab[0] = a0b0.lo();
//    final Hilo s0 = Hilo.sum(a0b0.hi(), a1b0.lo());
//    final Hilo s1 = Hilo.fastSum(a1b0.hi(), s0.hi());
//    final Hilo s2 = Hilo.sum(s0.lo(), a0b1.lo());
//    ab[1] = s2.lo();
//    final Hilo s3 = Hilo.sum(s1.lo(), s2.hi());
//    final Hilo s4 = Hilo.sum(s1.hi(), s3.hi());
//    final Hilo s5 = Hilo.sum(a0b1.hi(), a1b1.lo());
//    final Hilo s6 = Hilo.sum(s3.lo(), s5.lo());
//    ab[2] = s6.lo();
//    final Hilo s7 = Hilo.sum(s4.lo(), s6.hi());
//    final Hilo s8 = Hilo.sum(s4.hi(), s7.hi());
//    final Hilo s9 = Hilo.sum(a1b1.hi(), s5.hi());
//    final Hilo s10 = Hilo.sum(s7.lo(), s9.lo());
//    ab[3] = s10.lo();
//    final Hilo s11 = Hilo.sum(s8.lo(), s10.hi());
//    final Hilo s12 = Hilo.sum(s8.hi(), s11.hi());
//    final Hilo s13 = Hilo.sum(s11.lo(), s9.hi());
//    ab[4] = s13.lo();
//    final Hilo s14 = Hilo.sum(s12.lo(), s13.hi());
//    ab[5] = s14.lo();
//    final Hilo s15 = Hilo.sum(s12.hi(), s14.hi());
//    ab[7] = s15.hi();
//    ab[6] = s15.lo();
//    final DoubleArrayList terms = DoubleArrayList.from(ab);
//    unsafeCompress(terms);
//    return unsafe(terms); }

  //--------------------------------------------------------------------
  /** See <code>scale_expansion_zeroelim</code>
   * from Shewchuk's predicates.c.
   * Assuming zeros and non-finite cases are already handled.
   * */
  private static final DoubleArrayList
  fastMultiply (final DoubleArrayList e,
                final double b) {
    final DoubleArrayList h =
      new DoubleArrayList(2*e.size());

    Hilo Q_hh = Hilo.product(e.get(0),b);
    double Q = Q_hh.hi();
    double hh = Q_hh.lo();
    if (hh != 0) { h.add(hh); }

    for (int eindex = 1; eindex < e.size(); eindex++) {
      final double enow = e.get(eindex);
      final Hilo product10 = Hilo.product(enow,b);
      final Hilo sum_hh = Hilo.sum(Q,product10.lo());
      hh = sum_hh.lo();
      if (hh != 0) { h.add(hh); }
      //Fast_Two_Sum(product1, sum, Q, hh);
      Q_hh = Hilo.fastSum(product10.hi(),sum_hh.hi());
      Q = Q_hh.hi();
      hh = Q_hh.lo();
      if (hh != 0) {  h.add(hh); } }
    if ((Q != 0.0) || h.isEmpty()) { h.add(Q); }
    return h; }

//  private static final DoubleArrayList
//  fastMultiply (final DoubleArrayList e,
//                final double b) {
//    final DoubleArrayList h =
//      new DoubleArrayList(2*e.size());
//
//  //    Split(b, bhi, blo);
// //    Two_Product_Presplit(e[0], b, bhi, blo, Q, hh);
//    final Hilo bhilo = Hilo.split(b);
//    Hilo Q_hh = twoProductPresplit(e.get(0),b,bhilo);
//    double Q = Q_hh.hi();
//    double hh = Q_hh.lo();
//    if (hh != 0) { h.add(hh); }
//
//    for (int eindex = 1; eindex < e.size(); eindex++) {
//      final double enow = e.get(eindex);
// //      Two_Product_Presplit(enow, b, bhi, blo, product1, product0);
// //      Two_Sum(Q, product0, sum, hh);
//      final Hilo product10 = twoProductPresplit(enow,b,bhilo);
//      final Hilo sum_hh = Hilo.sum(Q,product10.lo());
//      hh = sum_hh.lo();
//      if (hh != 0) { h.add(hh); }
//      //Fast_Two_Sum(product1, sum, Q, hh);
//      Q_hh = Hilo.fastSum(product10.hi(),sum_hh.hi());
//      Q = Q_hh.hi();
//      hh = Q_hh.lo();
//      if (hh != 0) {  h.add(hh); } }
//    if ((Q != 0.0) || h.isEmpty()) { h.add(Q); }
//    return h; }

  //--------------------------------------------------------------------

  public final XDouble multiply (final double b) {
    if (0.0==b) { return ZERO; }
    if (1.0==b) { return this; }
    if (-1.0==b) { return this.negate(); }
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
    //assert isFinite() : "\n" + this;
    final DoubleArrayList terms = fastMultiply(terms(),b);
    unsafeCompress(terms);
    return unsafe(terms); }

  //--------------------------------------------------------------------

  public final XDouble multiplyBySq (final int flip,
                                     final double b) {
    assert flip==1 || flip==-1;
    final double bbflip = b*b*flip;
    if (0.0==bbflip) { return ZERO; }
    if (1.0==bbflip) { return this; }
    if (-1.0==bbflip) { return this.negate(); }
    if (Double.isNaN(bbflip)) { return NaN; }
    if (isZero()) { return ZERO; }
    if (isNaN()) { return NaN; }
    if (Double.POSITIVE_INFINITY == bbflip) {
      if (isPositive()) { return POSITIVE_INFINITY; }
      if (isNegative()) { return NEGATIVE_INFINITY; }
      return NaN; }
    if (Double.NEGATIVE_INFINITY == bbflip) {
      if (isPositive()) { return NEGATIVE_INFINITY; }
      if (isNegative()) { return POSITIVE_INFINITY; }
      return NaN; }
    assert Double.isFinite(bbflip);
    if (isPositiveInfinity()) {
      if (0.0<bbflip) { return POSITIVE_INFINITY; }
      if (0.0>bbflip) { return NEGATIVE_INFINITY; }
      return NaN; }
    if (isNegativeInfinity()) {
      if (0.0<bbflip) { return NEGATIVE_INFINITY; }
      if (0.0>bbflip) { return POSITIVE_INFINITY; }
      return NaN; }
    assert isFinite() : "\n" + this;
    assert !isZero() : "\n" + this;
    final DoubleArrayList tmp = fastMultiply(terms(),b);
    unsafeCompress(tmp);
    final DoubleArrayList terms = fastMultiply(tmp,b*flip);
    unsafeCompress(terms);
    return unsafe(terms); }

  //--------------------------------------------------------------------

  public final XDouble multiply (final Hilo b) {
    return multiply(b.hi()).add(multiply(b.lo())); }

  //--------------------------------------------------------------------

  private static final DoubleArrayList
  fastMultiply (final DoubleArrayList a,
                final DoubleArrayList b) {
    if (b.size() > a.size()) { return fastMultiply(b, a); }
    DoubleArrayList result = new DoubleArrayList(0);
    for (int i = 0; i < b.size(); i++) {
      result = fastAdd(result,fastMultiply(a, b.get(i))); }
    return result; }

  //--------------------------------------------------------------------

  public final XDouble multiply (final XDouble b) {
    if (isZero() || b.isZero()) { return ZERO; }
    if (isNaN() || b.isNaN()) { return NaN; }
    if (isOne()) { return b; }
    if (isMinusOne()) { return b.negate(); }
    if (b.isOne()) { return this; }
    if (b.isMinusOne()) { return negate(); }
    if (b.isPositiveInfinity()) {
      if (isPositive()) { return POSITIVE_INFINITY; }
      if (isNegative()) { return NEGATIVE_INFINITY; }
      return NaN; }
    if (b.isNegativeInfinity()) {
      if (isPositive()) { return NEGATIVE_INFINITY; }
      if (isNegative()) { return POSITIVE_INFINITY; }
      return NaN; }
    if (isPositiveInfinity()) {
      if (b.isPositive()) { return POSITIVE_INFINITY; }
      if (b.isNegative()) { return NEGATIVE_INFINITY; }
      return NaN; }
    if (isNegativeInfinity()) {
      if (b.isPositive()) { return NEGATIVE_INFINITY; }
      if (b.isNegative()) { return POSITIVE_INFINITY; }
      return NaN; }
    final DoubleArrayList terms = fastMultiply(terms(), b.terms());
    unsafeCompress(terms);
    return unsafe(terms); }

  //--------------------------------------------------------------------
  // TODO: check if this is different from scale(2.0)?

  @SuppressWarnings("unused")
  public final XDouble fast2x () {
    if (isZero()) { return ZERO; }
    if (isNaN()) { return NaN; }
    if (isPositiveInfinity()) { return POSITIVE_INFINITY; }
    if (isNegativeInfinity()) { return NEGATIVE_INFINITY; }
    final DoubleArrayList x2 = terms().clone();
    // This works because multiply by power of 2
    // (within over/under flow) is exact.
    // TODO: is it possible that directly modifying the exponent,
    //  via doubleToLongBits, is faster than float multiply?
    for  (int i=0;i<nterms();i++) { x2.set(i,2*x2.get(i)); }
    return unsafe(x2); }

  //--------------------------------------------------------------------
  // geometry
  //--------------------------------------------------------------------

  public static final XDouble l2norm2 (final double x,
                                       final double y) {
    return sum(Hilo.square(x), Hilo.square(y)); }

  public static final XDouble l2norm2 (final Vector2D v) {
    return l2norm2(v.getX(), v.getY()); }

  public static final XDouble crossProduct (final double x0,
                                            final double y0,
                                            final double x1,
                                            final double y1) {
    final Hilo x0y1 = Hilo.product(x0,y1);
    final Hilo x1y0 = Hilo.product(x1,y0);
    return subtract(x0y1, x1y0); }

  public static final XDouble crossProduct (final Vector2D a,
                                            final Vector2D b) {
    // TODO: next breaks Exact.inCircle!?
//    return crossProduct(a[0],a[1],b[0],b[1]); }
    final XDouble axby = product(a.getX(), b.getY());
    final XDouble bxay = product(b.getX(), a.getY());
    return axby.subtract(bxay); }


  public static final XDouble crossProduct (final Hilo x0,
                                            final Hilo y0,
                                            final Hilo x1,
                                            final Hilo y1) {
    final XDouble x0y1 = product(x0, y1);
    final XDouble x1y0 = product(x1, y0);
    return x0y1.subtract(x1y0); }

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
  /** Copy the terms to an array. */

  //public final double[] toArray () { return terms().toArray(); }

  //--------------------------------------------------------------------
  // private construction
  //--------------------------------------------------------------------

  //  private static final boolean
  //  increasingNonOverlapping (final DoubleArrayList t) {
  //    for (int i=0;i<t.size()-1;i++) {
  //      if ((2*Math.abs(t.get(i))) >= Math.ulp(t.get(i+1))) {
  //        return false; } }
  //    return true; }

  private static final void
  assertIncreasingNonOverlapping (final DoubleArrayList t) {
    for (int i=0;i<t.size()-1;i++) {
      final double a = Math.abs(t.get(i));
      // TODO: failing this constraint,
      //  which means I am not understanding about "non-overlapping",
      //  "non-adjacent", and what to expect from the various
      //  arithmetic ops.
      //  final double u = 0.5*Math.ulp(t.get(i+1));
      //  passing the following:
      final double u = 2*Math.ulp(t.get(i+1));
      assert (a <= u) :
        "\n" + t + "\n" +
          i + " : " + "\n" +
          Double.toHexString(a) +
          " >= " + Double.toHexString(u) + "\n" +
          Double.toHexString(t.get(i+1)) + ", " +
          Double.toHexString(t.get(i)) + "\n" +
          Hilo.sum(t.get(i+1), t.get(i)); } }

  private XDouble (final DoubleArrayList terms) {
    assertIncreasingNonOverlapping(terms);
    // DANGER: terms is mutable!!!!
    _terms = terms; }

  // TODO: make this private, public version should clone terms.
  public static final XDouble unsafe (final DoubleArrayList terms) {
    if (terms.isEmpty()) { return ZERO; }
    //assert isFinite(terms);
    // TODO: is compress() a good idea?
    //unsafeCompress(terms);
    return new XDouble(terms); }

  // TODO: make this private, public version should clone terms.
//  public static final XDouble unsafe (final double[] terms) {
//    return unsafe(DoubleArrayList.from(terms)); }

  // Not really safe unless non-overlapping is enforced
//  private static final XDouble safe (final DoubleArrayList terms) {
//    return unsafe(terms.clone()); }

  public static final XDouble valueOf (final double x) {
    // unsafe calls compress, not needed.
    if (0.0==x) { return ZERO; }
    if (Double.isNaN(x)) { return NaN; }
    if (Double.POSITIVE_INFINITY == x) { return POSITIVE_INFINITY; }
    if (Double.NEGATIVE_INFINITY == x) { return NEGATIVE_INFINITY; }
    return new XDouble(DoubleArrayList.from(x)); }

  public static final XDouble unsafe (final Hilo hilo) {
    if (hilo.isZero()) { return ZERO; }
    if (hilo.isNaN()) { return NaN; }
    // TODO: infinities?
    final double lo = hilo.lo();
    if (0.0 == lo) { return valueOf(hilo.hi()); }
    return new XDouble(DoubleArrayList.from(lo,hilo.hi())); }

  public static final XDouble sum (final double x0,
                                   final double x1) {
    return valueOf(Hilo.sum(x0, x1)); }

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

  //-------------------------------------------------------------------
  /** Modifies the DoubleArrayList <code>h</code> in place! */

  public static final void
  unsafeCompress (final DoubleArrayList h) {
    // predicates.c: "e and h may be the same."
    // and 2nd loop modifies h in place anyway.
    h.removeAll(0.0);
    // predicates.c assumes silently that e has at least 2 terms!!!
    if (1>=h.size()) { return; }
    int bottom = h.size() - 1;
    double Q = h.get(bottom);
    for (int eindex = h.size() - 2; eindex >= 0; eindex--) {
      final double enow = h.get(eindex);
      final Hilo Qnewq = Hilo.fastSum(Q, enow);
      if (Qnewq.lo() != 0) {
        h.set(bottom--,Qnewq.hi()); Q = Qnewq.lo(); }
      else { Q = Qnewq.hi(); } }
    int top = 0;
    for (int hindex = bottom + 1; hindex < h.size(); hindex++) {
      final double hnow = h.get(hindex);
      final Hilo Qnewq = Hilo.fastSum(hnow, Q);
      if (Qnewq.lo() != 0) { h.set(top++,Qnewq.lo()); }
      Q = Qnewq.hi(); }
    h.set(top,Q);
    assert top < h.size();
    h.resize(top+1); }

  // skip removeAll(0.0) not worth the extra complexity?
  // better to reduce the number of calls to unsafeCompress?

//  public static final void
//  unsafeCompress (final DoubleArrayList h) {
//    // predicates.c: "e and h may be the same."
//    // and 2nd loop modifies h in place anyway.
//    //h.removeAll(0.0);
//    // predicates.c assumes silently that e has at least 2 terms!!!
//    if (1>=h.size()) { return; }
//    int bottom = h.size() - 1;
//    double Q = h.get(bottom);
//    while ((0.0==Q) && (0<bottom)) { bottom--; Q = h.get(bottom); }
//    for (int eindex = bottom-1; eindex >= 0; eindex--) {
//      final double enow = h.get(eindex);
//      final Hilo Qnewq = Hilo.fastSum(Q, enow);
//      if (Qnewq.lo() != 0.0) {
//        h.set(bottom--,Qnewq.hi()); Q = Qnewq.lo(); }
//      else { Q = Qnewq.hi(); } }
//    int top = 0;
//    for (int hindex = bottom + 1; hindex < h.size(); hindex++) {
//      final double hnow = h.get(hindex);
//      if (0.0!=hnow) {
//        final Hilo Qnewq = Hilo.fastSum(hnow, Q);
//        if (Qnewq.lo() != 0.0) { h.set(top++,Qnewq.lo()); }
//        Q = Qnewq.hi(); } }
//    h.set(top,Q);
//    assert top < h.size();
//    h.resize(top+1); }

  //  compress()   Compress an expansion.
  //
  //  See the long version of Shewchuk's 1997 paper for details.
  //
  //  "Maintains the nonoverlapping property.  If round-to-even is used (as
  //  with IEEE 754), then any nonoverlapping expansion is converted to a
  //  nonadjacent expansion."
  //
  // TODO: is this a substitute for conversion to BigFloat?
  //  ie, do all expansions that represent the same rational number
  //  compress to the same sequence?
  // TODO: worth creating a version that modifies its input list?

  private static final DoubleArrayList
  compress (final DoubleArrayList e) {
    final DoubleArrayList h = e.clone();
    unsafeCompress(h);
    return h; }

  /** May return <code>this</code> instance in some cases. */
  public final XDouble compress () {
    if (1>=nterms()) { return this; }
    final DoubleArrayList terms = compress(terms());
    return unsafe(terms); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
