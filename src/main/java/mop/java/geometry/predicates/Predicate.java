package mop.java.geometry.predicates;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

/**
 * Specify the geometry predicates.
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
 * @version 2026-06-30
 */

public interface Predicate {

  // TODO: replace double[] with Point/Vector objects

  //--------------------------------------------------------------------
  // TODO: algorithm might be exact for some operations and not others.
  // TODO: an estimate of accuracy for each operation would be better.
  /** Is this algorithm exact (to the resolution expansions)
   * or approximate?
   */
  public default boolean isExact() { return false; }

  //--------------------------------------------------------------------
  /** Return a positive value if the points pa, pb, and pc occur in
   * counterclockwise order; a negative value if they occur in clockwise
   * order; and zero if they are collinear.  The result is also a rough
   * approximation of twice the signed area of the triangle defined by
   * the three points.
   * <br>
   * Only Fast and Default should be used; the other two are for
   * timings.
   * <br>
   * Exact, Slow, and Default use exact arithmetic to ensure a correct
   * answer. The result returned is the determinant of a matrix.  In
   * orient2d() only, this determinant is computed adaptively, in the
   * sense that exact arithmetic is used only to the degree it is needed
   * to ensure that the returned value has the correct sign.  Hence,
   * orient2d() is usually quite fast, but will run more slowly when the
   * input points are collinear or nearly so.
   */

  default double orient2d (final double[] pa,
                           final double[] pb,
                           final double[] pc) {
    throw new UnsupportedOperationException(
      getClass().getSimpleName()); }

  //--------------------------------------------------------------------
  /** Return a positive value if the point pd lies inside the circle
   * passing through pa, pb, and pc; a negative value if it lies
   * outside; and zero if the four points are cocircular. The points pa,
   * pb, and pc must be in counterclockwise order, or the sign of the
   * result will be reversed.
   * <br>
   * Only Fast and Default should be used; the other two are for
   * timings.
   * <br>
   * Exact, Slow, and Default use exact arithmetic to ensure a correct
   * answer. The result returned is the determinant of a matrix.  In
   * orient3d() only, this determinant is computed adaptively, in the
   * sense that exact arithmetic is used only to the degree it is needed
   * to ensure that the returned value has the correct sign.  Hence,
   * incircle() is usually quite fast, but will run more slowly when the
   * input points are cocircular or nearly so.
   */
  default double incircle (final double[] pa,
                           final double[] pb,
                           final double[] pc,
                           final double[] pd) {
    throw new UnsupportedOperationException(getClass().getSimpleName()); }

  //--------------------------------------------------------------------
  /** Return a positive value if the point pd lies inside the circle
   * passing through pa, pb, and pc; a negative value if it lies
   * outside; and zero if the four points are cocircular. The points pa,
   * pb, and pc must be in counterclockwise order, or the sign of the
   * result will be reversed.
   * <br>
   * Only Fast and Default should be used; the other two are for
   * timings.
   * <br>
   * Exact, Slow, and Default use exact arithmetic to ensure a correct
   * answer. The result returned is the determinant of a matrix.  In
   * orient3d() only, this determinant is computed adaptively, in the
   * sense that exact arithmetic is used only to the degree it is needed
   * to ensure that the returned value has the correct sign.  Hence,
   * orient3d() is usually quite fast, but will run more slowly when the
   * input points are coplanar or nearly so.
   */
  default double orient3d (final double[] pa,
                           final double[] pb,
                           final double[] pc,
                           final double[] pd) {
    throw new UnsupportedOperationException(
      getClass().getSimpleName()); }

  //--------------------------------------------------------------------

  /** Return a positive value if the point pe lies inside the sphere
   * passing through pa, pb, pc, and pd; a negative value if it lies
   * outside; and zero if the five points are co-spherical.  The points
   * pa, pb, pc, and pd must be ordered so that they have a positive
   * orientation (as defined by orient3d()), or the sign of the result
   * will be reversed.
   * <br>
   * Only Fast and Default should be used; the other two are for
   * timings.
   * <br>
   * Exact, Slow, and Default use exact arithmetic to ensure a correct
   * answer. The result returned is the determinant of a matrix.  In
   * orient3d() only, this determinant is computed adaptively, in the
   * sense that exact arithmetic is used only to the degree it is needed
   * to ensure that the returned value has the correct sign.  Hence,
   * incircle() is usually quite fast, but will run more slowly when the
   * input points are co-spherical or nearly so.
   */

  default double insphere (final double[] pa,
                           final double[] pb,
                           final double[] pc,
                           final double[] pd,
                           final double[] pe) {
    throw new UnsupportedOperationException(getClass().getSimpleName()); }
  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
