package mop.java.geometry.segment;

import mop.java.geometry.euclidean.VectorD2;

/** Minimal triangle with VectorD2 vertices, caching reusable values.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-10-05
 */

public final class SegmentD2Lazy implements SegmentR2 {

  // cache vector result of translating p0 to origin,
  // and related quantities

  private final VectorD2 p0;
  private final VectorD2 p1;

  @Override
  public final VectorD2 getP0 () { return p0; }
  @Override
  public final VectorD2 getP1 () { return p1; }

  private VectorD2 v10;

  //--------------------------------------------------------------------

  public static final double EPSILON = 0x1.0p-53;

  public static final double AREA_FACTOR =
    16 * 8 * (EPSILON * (3.0 + 16.0 * EPSILON));

  /** Shewchuk error bound A for twiceSignedArea. */
  public double sideBound (final VectorD2 p) {
    final VectorD2 v20 = p.subtract(p0);
    final double xy21 = Math.abs(v10.x() * v20.getY());
    final double xy12 = Math.abs(v20.x() * v10.getY());
    return AREA_FACTOR * (xy21 + xy12); }

  @Override
  public final boolean sideRobust (final VectorD2 p) {
    return Math.abs(side(p)) > sideBound(p); }

  @Override
  public final double side (final VectorD2 p) {
    return v10.wedge(p.subtract(p0)); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private SegmentD2Lazy (final VectorD2 p0,
                        final VectorD2 p1)  {
    super();
    this.p0 = p0;
    this.p1 = p1; }

  public static SegmentR2 of (final VectorD2 p0,
                              final VectorD2 p1) {
    return new SegmentD2Lazy(p0,p1); }

  /** Convert other triangle classes. */

  public static SegmentR2 from (final SegmentR2 t) {
    return of(t.getP0(), t.getP1()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
