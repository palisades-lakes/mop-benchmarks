package mop.java.numbers;

import java.util.Arrays;

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
 *   TODO: what is the maximum possible number of non-overlapping terms?
 *   <br>
 *   Possible extension: add an exponent (<code>long</code> or
 *   even <code>BigInteger</code> to extend range.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-05-24
 */

@SuppressWarnings("unused")
public final class XDoubleArray implements Comparable<XDoubleArray> {

  private final int _nterms;

  public final int  nterms () { return _nterms; }

  /** Non-overlapping doubles, highest order term last.
   */
  private final double[] _terms;

  public final double term (final int i) {
    assert _nterms >= i;
    return _terms[i]; }

  //-------------------------------------------------------------------
  // Comparable<Hilo>
  //-------------------------------------------------------------------
  // TODO: reverse order of terms? Most significant first?

  @Override
  public final int compareTo (final XDoubleArray other) {
    int i = this._nterms;
    int j = other._nterms;
    while (--i >= 0 && --j >= 0) {
      final double a = _terms[j];
      final double b = other._terms[j];
      if (a < b) { return -1; }
      if (a > b) { return 1; } }
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
  // TODO: can this be derived from Double constants?

  private static final double EPSILON = 0x1.0p-53;

  // grow_expansion_zeroelim
  // TODO: would it make sense to implement add(XDouble) by calling this?
  public final XDoubleArray add (final double b) {
    final int elen = _nterms;
    final double[] h = new double[elen+1];
    int hindex = 0;
    double Q = b;
    for (int eindex = 0; eindex < elen; eindex++) {
      double enow = _terms[eindex];
      //Two_Sum(Q, enow, Qnew, hh);
      double hh, Qnew;
      { final Hilo fts = Hilo.twoSum(Q,enow);
        Qnew = fts.hi(); hh = fts.lo(); }
      Q = Qnew;
      if (hh != 0.0) { h[hindex++] = hh; } }
    if ((Q != 0.0) || (hindex == 0)) { h[hindex++] = Q; }

    return new XDoubleArray(hindex, h); }

  //--------------------------------------------------------------------
  /** Sum two expansions, eliminating zero components from the output
   * expansion.
   * <br>
   * Return <pre>this + f</pre>.
   * See the long version of Shewchuk's paper for details.
   * <br>
   * If round-to-even is used (as with IEEE 754), maintains the strongly
   * nonoverlapping property.  (That is, if e is strongly
   * nonoverlapping, h will be also.) Does NOT maintain the
   * nonoverlapping or nonadjacent properties.
   * <br>
   * Was: fast_expansion_sum_zeroelim in predicates.c
   */

  public final XDoubleArray add (final XDoubleArray f) {
    final double[] h = new double[_nterms + f._nterms];
    double Q;
    double Qnew;
    double hh;
    int i = 0;
    int j = 0;
    int k = 0;

    double ti = _terms[0];
    double fj = f._terms[0];

    // TODO: assuming both expansions have at least 2 terms!!!
    //  Can get away with this reference off the end of the array in C,
    //  but dangerous!!!
    if ((fj > ti) == (fj > -ti)) { Q = ti; ti = _terms[++i]; }
    else { Q = fj; fj = f._terms[++j]; }

    if ((i < _nterms) && (j < f._nterms)) {
      if ((fj > ti) == (fj > -ti)) {
        // Fast_Two_Sum(ti, Q, Qnew, hh);
        { final Hilo fts = Hilo.fastTwoSum(ti, Q);
          Qnew = fts.hi(); hh = fts.lo(); }
        ti = _terms[++i]; }
      else {
        // Fast_Two_Sum(fj, Q, Qnew, hh);
        { final Hilo fts = Hilo.fastTwoSum(fj, Q);
          Qnew = fts.hi(); hh = fts.lo(); }
        fj = f._terms[++j]; }

      Q = Qnew;

      if (hh != 0.0) { h[k++] = hh; }

      while ((i < _nterms) && (j < f._nterms)) {
        if ((fj > ti) == (fj > -ti)) {
          //Two_Sum(Q, ti, Qnew, hh);
          { final Hilo ts = Hilo.twoSum(Q, ti);
            Qnew = ts.hi(); hh = ts.lo(); }
          // original code goes off the end of _terms[]
          i++;
          if (i < _nterms) { ti = _terms[i]; } }
        else {
          //Two_Sum(Q, fj, Qnew, hh);
          { final Hilo ts = Hilo.twoSum(Q, fj);
            Qnew = ts.hi(); hh = ts.lo(); }
          // original code goes of the end of f._terms[]
          j++;
          if (j < f._nterms) { fj = f._terms[j]; } }
        Q = Qnew;
        if (hh != 0.0) { h[k++] = hh; } } }

    while (i < _nterms) {
      //Two_Sum(Q, ti, Qnew, hh);
      { final Hilo ts = Hilo.twoSum(Q, ti);
        Qnew = ts.hi(); hh = ts.lo(); }
      // original code goes of the end of _terms[], but value never used
      i++;
      if (i < _nterms) { ti = _terms[i]; }
      Q = Qnew;
      if (hh != 0.0) { h[k++] = hh; } }

    while (j < f._nterms) {
      //Two_Sum(Q, fj, Qnew, hh);
      { final Hilo ts = Hilo.twoSum(Q, fj);
        Qnew = ts.hi(); hh = ts.lo(); }
      //---------------------------
      // original code goes of the end of f._terms[], but value never
      // used
      j++;
      if (j < f._nterms) { fj = f._terms[j]; }
      Q = Qnew;
      if (hh != 0.0) { h[k++] = hh; } }

    if ((Q != 0.0) || (k == 0)) { h[k++] = Q; }

    return new XDoubleArray(k, h); }

  //--------------------------------------------------------------------

  public final XDoubleArray negate () {
    final double[] h = new double[_nterms];
    for (int i=0;i<_nterms;i++) { h[i] = -_terms[i]; }
    return new XDoubleArray(_nterms, h); }

  public final XDoubleArray abs () {
    final double hi = _terms[_nterms - 1];
    if ((! Double.isFinite(hi)) || (hi >= 0)) { return this; }
    return negate(); }

  public final XDoubleArray subtract (final XDoubleArray f) {
    return add(f.negate());  }

  //--------------------------------------------------------------------
  /** Multiply an expansion by a scalar, eliminating zero components from
   * the output expansion.
   * <br>
   * Sets h = be.  See either version of my paper for details.
   * <br>
   * Maintains the nonoverlapping property.  If round-to-even is used
   * (as with IEEE 754), maintains the strongly nonoverlapping and
   * nonadjacent properties as well.  (That is, if e has one of these
   * properties, so will h.)
   * <br>
   * was scale_expansion() in predicates.c
   */

  public final XDoubleArray scale (final double b) {
    double Q, sum;
    double hh;
    double product1;
    double product0;
    int i, k;
    double ti;
    double bvirt;
    double avirt, bround, around;
    double c;
    double abig;
    double ahi, alo, bhi, blo;
    double err1, err2, err3;
    // TODO: check this. COuld it be _nterms+1?
    final double[] h = new double[_nterms + 1];
    //Split(b, bhi, blo);
    c = (Hilo.SPLIT * b); abig = (c - b);
    bhi = c - abig;
    blo = b - bhi;
    //------------------------------------------------
    //Two_Product_Presplit(e[0], b, bhi, blo, Q, hh);
    Q = (_terms[0] * b); c = (Hilo.SPLIT * _terms[0]);
    abig = (c - _terms[0]); ahi = c - abig; alo = _terms[0] - ahi;
    err1 = Q - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); hh = (alo * blo) - err3;
    //------------------------------------------------
    k = 0;
    if (hh != 0) { h[k++] = hh; }
    for (i = 1; i < _nterms; i++) {
      ti = _terms[i];
      //Two_Product_Presplit(ti, b, bhi, blo, product1, product0);
      //Two_Sum(Q, product0, sum, hh);
      product1 = (ti * b); c = (Hilo.SPLIT * ti);
      abig = (c - ti); ahi = c - abig; alo = ti - ahi;
      err1 = product1 - (ahi * bhi); err2 = err1 - (alo * bhi);
      err3 = err2 - (ahi * blo); product0 = (alo * blo) - err3;
      sum = (Q + product0); bvirt = (sum - Q);
      avirt = sum - bvirt; bround = product0 - bvirt;
      around = Q - avirt; hh = around + bround;
      //-------------------------------------------------------
      if (hh != 0) { h[k++] = hh; }
      //  Fast_Two_Sum(product1, sum, Q, hh)
      Q = (product1 + sum); bvirt = Q - product1;
      hh = sum - bvirt;
      //------------------------------------------------------
      if (hh != 0) { h[k++] = hh; } }
    if ((Q != 0.0) || (k == 0)) { h[k++] = Q; }
    return new XDoubleArray(k-1, h);
  }  //--------------------------------------------------------------------
  /** Produce a one-word estimate of an expansion's value.
   * <br>
   * See either version of Shewchuk's paper for details.
   */

  public final double estimate () {
    double Q = _terms[0];
    for (int i = 1; i < _nterms; i++) { Q += _terms[i]; }
    return Q; }

  // TODO: might be different from Shewchuk's estimate
  public final double doubleValue () { return estimate(); }

  // TODO: might be different from Shewchuk's estimate
  public final float floatValue () { return (float) doubleValue(); }

  //--------------------------------------------------------------------
  // private construction
  //--------------------------------------------------------------------

  private static final boolean
  increasingNonOverlapping (final int n, final double[] t) {
    for (int i=0;i<n-1;i++) {
      if ((2*Math.abs(t[i])) >= Math.ulp(t[i+1])) { return false; } }
    return true; }

  private XDoubleArray (final int nterms,
                        final double[] terms) {
    assert terms.length >= nterms : terms.length + " <= " + nterms;
    assert increasingNonOverlapping(nterms,terms) :
      nterms + ":" + Arrays.toString(terms);
    _nterms = nterms;
    _terms = terms; }

  // add requires at least 2 terms, larger magnitude 2nd
  public static final XDoubleArray valueOf (final double x) {
    return make(new double[]{0.0, x,}); }

  public static final XDoubleArray ZERO = valueOf(0.0);

  public static final XDoubleArray NaN =
    make(new double[] {Double.NaN,Double.NaN,});

  public static final XDoubleArray POSITIVE_INFINITY =
    valueOf(Double.POSITIVE_INFINITY);

  public static final XDoubleArray NEGATIVE_INFINITY =
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

  private static final XDoubleArray make (final double[] terms) {

    return new XDoubleArray(terms.length, terms); }

  // returns double[3]
  // #define Two_One_Diff(a1, a0, b, x2, x1, x0) \
  //  Two_Diff(a0, b , _i, x0); \
  //  Two_Sum( a1, _i, x2, x1)
  private static final double[] twoOneDiff(final double ahi,
                                           final double alo,
                                           final double b) {
    final Hilo ix0 = Hilo.twoDiff(alo, b);
    final Hilo x2x1 = Hilo.twoSum(ahi, ix0.hi());
    return new double[] {ix0.lo(), x2x1.lo(), x2x1.hi(), }; }

  // Two_Two_Diff(axby1, axby0, bxay1, bxay0,
  //              ab[3], ab[2], ab[1], ab[0]);
  public static final XDoubleArray twoTwoDiff (final Hilo a,
                                               final Hilo b) {
    // Two_One_Diff(a1, a0, b0, _j, _0, x0);
    final double[] x00j = twoOneDiff(a.hi(),a.lo(),b.lo());
    // Two_One_Diff(_j, _0, b.hi(), x3, x2, x1)
    final double[] x123 = twoOneDiff(x00j[2],x00j[1],b.hi());
    return make(new double[] { x00j[0],x123[0],x123[1],x123[2], });  }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
