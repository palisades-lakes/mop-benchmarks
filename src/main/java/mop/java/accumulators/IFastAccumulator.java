package mop.java.accumulators;

import com.carrotsearch.hppc.DoubleArrayList;

import mop.java.accumulators.Accumulator;
import mop.java.numbers.Doubles;

//----------------------------------------------------------------
/** Exact iterative distillation algorithm.
 * Best for summing relatively small collections of numbers
 * via {@link #addAll(double[])}.
 * <p>
 * <b>TODO:</b> measure cross-over size where this beats
 * {@link mop.java.accumulators.ZhuHayesAccumulator}.
 * <p>
 * This uses the 'no branch' version of 'twoSum'.
 * <p>
 * Primary reference:
 * <p>
 * <a href="http://epubs.siam.org/doi/abs/10.1137/070710020?journalCode=sjoce3" >
 * Yong Kang Zhu and Wayne B. Hayes,
 * "Correct Rounding and a Hybrid Approach to Exact Floating-Point Summation,"
 * SIAM J. Sci. Comput.,  31(4), p 2981–3001, Jun 2009. (21 pages)</a>
 * </p>
 * Also see:
 * <p>
 * <a href="http://dl.acm.org/citation.cfm?id=1824815" >
 * Yong Kang Zhu and Wayne B. Hayes,
 * "Algorithm 908: Online Exact Summation of Floating-Point Streams",
 * ACM TOMS Volume 37 Issue 3, September 2010</a>
 * <p>
 * <p>
 * <a href="https://macsphere.mcmaster.ca/bitstream/11375/9258/1/fulltext.pdf" >
 * Yuhang Zhao, "Some Highly Accurate Basic Linear Algebra Subroutines",
 * MS Thesis, McMaster U , Computing and Software, 2010.</a>
 * <p>
 * This implementation based on the code with TOMS 908 and:
 * <p>
 * <a href="https://github.com/bsteinb/accurate/blob/master/src/sum/onlineexactsum.rs">
 * Benedikt Steinbusch,
 * "(More or less) accurate floating point algorithms"</a>
 * (Apache 2.0 or MIT license, visited 2017-05-01)
 * <p>
 * <em>NOT</em> thread safe!
 * <p>
 * @author palisades dot lakes at gmail dot com
 * @version 2019-04-12
 */

public final class IFastAccumulator
implements Accumulator<IFastAccumulator> {

  //--------------------------------------------------------------
  // IFastSum
  //--------------------------------------------------------------

  private static final boolean isHalfUlp (final double x) {
    // TODO: do we need to check for NaN and infinity?
    return (0.0 != x) && (0L == Doubles.significand(x)); }

  //--------------------------------------------------------------

  private static final double halfUlp (final double x) {
    // TODO: do we need to check for NaN and infinity?
    // TODO: compare to c++ implementation
    // TODO: return zero when x is zero?
    if (0.0 == x) { return 0.0; }
    return 0.5 * Math.ulp(x); }

  //--------------------------------------------------------------
  /** Return correctly rounded sum of 3 non-overlapping doubles.
   * <p>
   * See <a href="https://github.com/Jeffrey-Sarnoff/IFastSum.jl">
   * IFastSum.jl</a> (visited 2017-05-01, MIT License)
   */

  private static final double round3 (final double s0,
                                      final double s1,
                                      final double s2) {

    // non-overlapping here means:
    //    assert Math.abs(s0) > Math.abs(s1);
    //    assert Math.abs(s1) > Math.abs(s2) :
    //      Double.toHexString(s1) + " <= " + Double.toHexString(s2);
    assert s0 == (s0 + s1) :
      Double.toHexString(s0) + " + " + Double.toHexString(s1) +
      " -> " + Double.toHexString(s0+s1);
    assert s1 == (s1 + s2);

    if ((isHalfUlp(s1)) &&
      (Math.signum(s1) == Math.signum(s2))) {
      return s0 + Math.nextUp(s1); }
    return s0; }

  //--------------------------------------------------------------

  private double sumTwo = Double.NaN;
  private double errTwo = Double.NaN;

  /** Update {@link #sumTwo} and {@link #errTwo} so that
   * <code>{@link #sumTwo} == x0 + x1</code>
   * (sum rounded to nearest double), and
   * <code>rationalSum({@link #sumTwo},{@link #errTwo})
   * == rationalSum(x0,x1)</code>
   * (exact sums, implemented, for example, with arbitrary
   * precision rationals)
   */

  private final void twoSum (final double x0,
                             final double x1) {
    // might get +/- Infinity due to overflow
    sumTwo = x0 + x1;
    final double z = sumTwo - x0;
    errTwo = (x0 - (sumTwo - z)) + (x1 - z); }

  //------------------------------------------------------------

  private final double iFastSum (final double[] x,
                                 final int[] n,
                                 final boolean recurse) {
    // Step 1
    double s = 0.0;

    // Step 2
    for (int ii=0;ii<n[0]; ii++) {
      // needed to pass nonFinite test.
      // could be dropped if we don't require
      assert Double.isFinite(x[ii]);
      twoSum(s,x[ii]);
      s = sumTwo;
      if (! Double.isFinite(s)) { return s; }
      x[ii] = errTwo; }
    // Step 3
    for(;;) {
      // Step 3(1)
      int count = 0; // slices are indexed from 0
      double st = 0.0;
      double sm = 0.0;
      // Step 3(2)
      for (int ii=0;ii<n[0];ii++) {
        // Step 3(2)(a)
        twoSum(st, x[ii]);
        st = sumTwo;
        final double b = errTwo;
        // Step 3(2)(b)
        if (0.0 != b) {
          x[count] = b;
          // Step 3(2)(b)(i)
          // throw exception on overflow:
          count = Math.addExact(count,1);
          // Step 3(2)(b)(ii)
          sm = Math.max(sm,Math.abs(st)); } }
      // Step 3(3)
      // check count exact double
      final double dcount = count;
      assert count == (int) dcount;
      final double em = dcount * halfUlp(sm);
      // Step 3(4)
      twoSum(s, st);
      s = sumTwo;
      if (! Double.isFinite(s)) { return s; }
      st = errTwo;
      x[count] = st;
      n[0] = Math.addExact(count,1);
      // Step 3(5)
      if ((em == 0.0) || (em < halfUlp(s))) {
        // Step 3(5)(a)
        if (! recurse) { return s; }
        // Step 3(5)(b)
        twoSum(st, em);
        final double w1 = sumTwo;
        final double e1 = errTwo;
        // Step 3(5)(c)
        twoSum(st, -em);
        final double w2 = sumTwo;
        final double e2 = errTwo;
        // Step 3(5)(d)
        if (((w1 + s) != s)
          || ((w2 + s) != s)
          || (round3(s, w1, e1) != s)
          || (round3(s, w2, e2) != s)) {
          // Step 3(5)(d)(i)
          double s1 = iFastSum(x, n, false);
          // Step 3(5)(d)(ii)
          twoSum(s, s1);
          s = sumTwo;
          if (! Double.isFinite(s)) { return s; }
          s1 = errTwo;
          // Step 3(5)(d)(iii)
          final double s2 = iFastSum(x, n, false);
          // Step 3(5)(d)(iv)
          s = round3(s, s1, s2);
          if (! Double.isFinite(s)) { return s; } }
        // Step 3(5)(e)
        return s; } } }

  //--------------------------------------------------------------
  // Accumulator interface
  //--------------------------------------------------------------

  @Override
  public final boolean isExact () { return true; }

  @Override
  public final boolean noOverflow () { return false; }

  //--------------------------------------------------------------

  private final DoubleArrayList _z;

  //--------------------------------------------------------------

  @Override
  public final IFastAccumulator clear () {
    _z.clear();
    return this; }

  //--------------------------------------------------------------

  @Override
  public final Object value () {
    return Double.valueOf(doubleValue()); }

  @Override
  public final double doubleValue () {
    // TODO: is the state of z valid after iFastSum?
    // could we use it to accelerate the iteration after
    // adding more values, rather that starting from scratch with
    // all the original values each time?
    final double[] z = _z.toArray();
    final int[] n = new int[1];
    n[0] = z.length;
    return iFastSum(z,n,true); }

  //--------------------------------------------------------------

  @Override
  public final IFastAccumulator add (final double x) {
    assert Double.isFinite(x);
    _z.add(x);
    return this; }

  //--------------------------------------------------------------

  @Override
  public final IFastAccumulator addAll (final double[] x) {
    _z.add(x);
    return this; }

  //--------------------------------------------------------------

  @Override
  public final IFastAccumulator add2 (final double x) {
    assert Double.isFinite(x);
    final double x2 = x*x;
    final double e = Math.fma(x,x,-x2);
    add(x2);
    add(e);
    return this; }

  //--------------------------------------------------------------

  @Override
  public final IFastAccumulator addProduct (final double x0,
                                            final double x1) {
    assert Double.isFinite(x0);
    assert Double.isFinite(x1);
    final double x01 = x0*x1;
    final double e = Math.fma(x0,x1,-x01);
    add(x01);
    add(e);
    return this; }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private IFastAccumulator () { _z = new DoubleArrayList(); }


  public static final IFastAccumulator make () {
    return new IFastAccumulator(); }

  //--------------------------------------------------------------
} // end of class
//----------------------------------------------------------------
