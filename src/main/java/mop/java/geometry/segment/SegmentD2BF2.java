package mop.java.geometry.segment;

import mop.java.geometry.euclidean.VectorD2;

/** Two delegate segments, one fast and approximate,
 * one slow and exact.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-29
 */

public final class SegmentD2BF2 implements SegmentR2 {

  // eager
  private final SegmentD2Lazy _segmentD2;
  // fast and approximate
  private final SegmentD2Lazy getSegmentD2 () { return _segmentD2; }

  @Override
  public final VectorD2 getP0 () { return _segmentD2.getP0(); }
  @Override
  public final VectorD2 getP1 () { return _segmentD2.getP1(); }

  // lazy
  private SegmentBF2 _segmentBF2 = null;
  // slow and exact
  private final SegmentBF2 getSegmentBF2 () {
    if (null == _segmentBF2) {
      _segmentBF2 = (SegmentBF2) SegmentBF2.from(this); }
    return _segmentBF2; }

  //--------------------------------------------------------------------

  @Override
  public final boolean sideRobust (final VectorD2 ignore) {
    return true; }

  @Override
  public final double side (final VectorD2 p) {
    if (getSegmentD2().sideRobust(p)) {
      return getSegmentD2().side(p); }
    return getSegmentBF2().side(p); }

  //--------------------------------------------------------------------

  public final String toString () { return toHexString(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private SegmentD2BF2 (final VectorD2 p0,
                       final VectorD2 p1)  {
    super();
   _segmentD2 = (SegmentD2Lazy) SegmentD2Lazy.of(p0, p1); }

  public static final SegmentR2 of (final VectorD2 p0,
                                    final VectorD2 p1) {
    return new SegmentD2BF2(p0, p1); }

  /** Convert other segment classes. */

  public static final SegmentR2 from (final SegmentR2 t) {
    return of(t.getP0(), t.getP1()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
