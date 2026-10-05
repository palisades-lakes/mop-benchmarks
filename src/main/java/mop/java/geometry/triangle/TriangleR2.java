package mop.java.geometry.triangle;

import mop.java.geometry.euclidean.VectorD2;

import java.util.List;

/** Triangles "embedded" in <code>R<sup>2</sup></code>>.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-29
 */

public interface TriangleR2 {

  // TODO: replace with simplex and embedding map
  //  only cache the points if actually used
  public VectorD2 getP0 ();
  public VectorD2 getP1 ();
  public VectorD2 getP2 ();

  //--------------------------------------------------------------------
  // orientation and related methods
  //--------------------------------------------------------------------

  // TODO: an estimate of accuracy for each operation would be better.
  /** Is this algorithm exact (assuming <code>double</code> inputs)?
   * Does not depend on point configuration.
   */

  public default boolean signedAreaExact() { return false; }

  //--------------------------------------------------------------------
  /** Return a positive value if the points pa, pb, and pc occur in
   * counterclockwise order; a negative value if they occur in clockwise
   * order; and zero if they are collinear.  The result is also a rough
   * approximation of twice the signed area of the triangle defined by
   * the three points.
   */

  public default double twiceSignedArea () {
    throw new UnsupportedOperationException(
      getClass().getSimpleName()); }

  //--------------------------------------------------------------------
  /** Is the orientation expected to be correct?
   * May use an error bound, so answer depends on point configuration.
   */

  public default boolean isOrientationRobust () {
    return signedAreaExact(); }

  //--------------------------------------------------------------------
  /** Return +1.0 if the points pa, pb, and pc occur in
   * counterclockwise order; -1.0 if they occur in clockwise
   * order; and +/-0.0 if they are collinear.
   * Nonfinite values
   * <br>
   * Separating this from <code>twiceSignedArea</code>
   * permits classes that do more precise calculation to get the sign
   * from that, rather than forcing a round to <code>double</code>.
   */

  public default double orientation () {
    // NOTE: Double.compare() doesn't handle +-0.0 correctly.
    final double a = twiceSignedArea();
    if (! Double.isFinite(a)) { return a; }
    if (0.0 < a) { return 1; }
    if (0.0 > a) { return -1; }
    return 0; }

  //--------------------------------------------------------------------
  // inCircle and related methods
  //--------------------------------------------------------------------

  /** Is this algorithm exact (assuming <code>double</code> inputs)?
   * Does not depend on point configuration.
   */

  public default boolean inCircleDistanceExact () { return false; }

  /** Return a positive value if the point pd lies inside the circle
   * passing through p0, p1, and p2; a negative value if it lies
   * outside; and zero if the four points are cocircular. The points pa,
   * pb, and pc must be in counterclockwise order, or the sign of the
   * result will be reversed.
   * <br>
   * (Shewchuk predicate.c)
   * <br>
   * Only Fast and Default should be used; the other two are for
   * timings.
   * <br>
   * Exact, Slow, and Default use exact arithmetic to ensure a correct
   * answer. The result returned is the determinant of a matrix.  In
   * signedVolume() only, this determinant is computed adaptively, in the
   * sense that exact arithmetic is used only to the degree it is needed
   * to ensure that the returned value has the correct sign.  Hence,
   * inCircle() is usually quite fast, but will run more slowly when the
   * input points are cocircular or nearly so.
   */

  public default double inCircleDistance (final VectorD2 p) {
    throw new UnsupportedOperationException(getClass().getSimpleName()); }

  //--------------------------------------------------------------------
  /** Is the sign expected to be correct?
   * May use an error bound, so answer depends on point configuration.
   */

  public default boolean inCircleRobust (final VectorD2 ignore) {
    return inCircleDistanceExact(); }

  /** Return -1, 0, 1 if the point is
   * outside, on, or inside the circumcircle.
   */

  public default double inCircle (final VectorD2 p) {
    // TODO: what to do with NaN?
    final double a = inCircleDistance(p);
    // NOTE: <code>compareTo()</code> doesn't handle
    // signed zeros and NaN 'correctly' for this purpose.
    if (Double.isNaN(a)) { return Double.NaN; }
    if (0.0 < a) { return 1.0; }
    if (0.0 > a) { return -1.0; }
    return 0.0; }

  //--------------------------------------------------------------------
  // debugging utilities
  //--------------------------------------------------------------------

  public default String toHexString () {
    return getClass().getSimpleName() + "[" +
      getP0().toHexString() + ", " +
      getP1().toHexString() + ", " +
      getP2().toHexString() + "]"; }

  public default String description () { return toString(); }

  //--------------------------------------------------------------------
  // construction related
  //--------------------------------------------------------------------
  /** ground truth predicate. */
  public static TriangleR2 truth (final TriangleR2 t) {
    return TriangleBF2.from(t); }

  /** conversions from any SegmentD2 to other Triangle classes. */

  public static TriangleR2 convertTriangle (final TriangleR2 t,
                                            final String dest) {
    // TODO: lookup method object rather than switch (String)
    return switch (dest) {
      case "SegmentD2" ->  TriangleD2.from(t);
      case "SegmentD2Eager" ->  TriangleD2Eager.from(t);
      case "SegmentD2Lazy" -> TriangleD2Lazy.from(t);
      case "SegmentD2BF2" -> TriangleD2BF2.from(t);
      case "SegmentBF2" ->  TriangleBF2.from(t);
      case "TriangleBF2X" ->  TriangleBF2X.from(t);
      case "TriangleRF2" ->  TriangleRF2.from(t);
      default -> throw new UnsupportedOperationException(); }; }

  public static TriangleR2[]
  convertTriangles (final TriangleR2[] t,
                    final String dest) {
    for (int i=0; i<t.length; i++) {
      t[i] = convertTriangle(t[i],dest); }
    return t;}

  //-------------------------------------------------------------------

  public static List<TriangleR2> makeTriangles (final TriangleR2 t) {
    final TriangleR2 d2 = TriangleD2.from(t);
    final TriangleR2 d2eager = TriangleD2Eager.from(t);
    final TriangleR2 d2lazy = TriangleD2Lazy.from(t);
    final TriangleR2 bf2 = TriangleBF2.from(t);
    final TriangleR2 rf2 = TriangleRF2.from(t);
    final TriangleR2 d2bf2 = TriangleD2BF2.from(t);
    return List.of(d2, d2eager, d2lazy, bf2, rf2, d2bf2); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
