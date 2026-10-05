package mop.java.geometry.segment;

import mop.java.geometry.euclidean.VectorD2;

import java.util.List;

/** Line Segments "embedded" in <code>R<sup>2</sup></code>>.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-10-05
 */

public interface SegmentR2 {

  // TODO: replace with simplex and embedding map
  //  only cache the points if actually used
  public VectorD2 getP0 ();
  public VectorD2 getP1 ();

  //--------------------------------------------------------------------
  // TODO: an estimate of accuracy for each operation would be better.
  /** Is the area exact (assuming <code>double</code> inputs)?
   */

  public default boolean sideExact () { return false; }

  /** Is the sign correct even if the area value is not?
   */

  public default boolean sideRobust (final VectorD2 p) { return false; }

  //--------------------------------------------------------------------
  /** Return a positive value if <code>p</code> is
   * left of the oriented segment line;
   * a negative value if to the right;
   * and zero if on the line.
   * Is twice the signed area of the triangle formed by
   */

  public default double side (final VectorD2 p) {
    throw new UnsupportedOperationException(
      getClass().getSimpleName()); }

  //--------------------------------------------------------------------
  // debugging utilities
  //--------------------------------------------------------------------

  public default String toHexString () {
    return getClass().getSimpleName() + "[" +
      getP0().toHexString() + ", " +
      getP1().toHexString()  + "]"; }

  public default String description () { return toString(); }

  //--------------------------------------------------------------------
  // construction related
  //--------------------------------------------------------------------
  /** ground truth predicate. */
  public static SegmentR2 truth (final SegmentR2 t) {
    return SegmentBF2.from(t); }

  /** conversions from any SegmentD2 to other Triangle classes. */

  public static SegmentR2 convertSegment (final SegmentR2 t,
                                          final String dest) {
    // TODO: lookup method object rather than switch (String)
    return switch (dest) {
      case "SegmentD2" ->  SegmentD2.from(t);
      case "SegmentD2Eager" ->  SegmentD2Eager.from(t);
      case "SegmentD2Lazy" -> SegmentD2Lazy.from(t);
      case "SegmentD2BF2" -> SegmentD2BF2.from(t);
      case "SegmentBF2" ->  SegmentBF2.from(t);
      default -> throw new UnsupportedOperationException(); }; }

  public static SegmentR2[]
  convertSegments (final SegmentR2[] t,
                    final String dest) {
    for (int i=0; i<t.length; i++) {
      t[i] = convertSegment(t[i], dest); }
    return t;}

  //-------------------------------------------------------------------

  public static List<SegmentR2> makeSegments (final SegmentR2 t) {
    final SegmentR2 d2 = SegmentD2.from(t);
    final SegmentR2 d2eager = SegmentD2Eager.from(t);
    final SegmentR2 d2lazy = SegmentD2Lazy.from(t);
    final SegmentR2 bf2 = SegmentBF2.from(t);
    final SegmentR2 d2bf2 = SegmentD2BF2.from(t);
    return List.of(d2, d2eager, d2lazy, bf2, d2bf2); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
