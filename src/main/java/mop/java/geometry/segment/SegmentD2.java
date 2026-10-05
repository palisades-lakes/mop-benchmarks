package mop.java.geometry.segment;

import mop.java.geometry.euclidean.VectorD2;

/** Minimal triangle with VectorD2 vertices, nothing cached.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-10-05
 */

public class SegmentD2 implements SegmentR2 {

  private final VectorD2 p0;
  private final VectorD2 p1;

  @Override
  public final VectorD2 getP0 () { return p0; }
  @Override
  public final VectorD2 getP1 () { return p1; }

  //--------------------------------------------------------------------

  public static final double EPSILON = 0x1.0p-53;

  public static final double AREA_FACTOR =
    16 * 8 * (EPSILON * (3.0 + 16.0 * EPSILON));

  /** Shewchuk error bound A for twiceSignedArea. */
  public double sideBound (final VectorD2 p) {
    final VectorD2 v10 = p1.subtract(p0);
    final VectorD2 v20 = p.subtract(p0);
    final double xy21 = Math.abs(v10.x() * v20.getY());
    final double xy12 = Math.abs(v20.x() * v10.getY());
    return AREA_FACTOR * (xy21 + xy12); }


  @Override
  public final boolean sideRobust (final VectorD2 p) {
    return Math.abs(side(p)) > sideBound(p); }

  @Override
  public double side (final VectorD2 p) {
    return p1.subtract(p0).wedge(p.subtract(p0)); }

  //--------------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------------
  // TODO: hashcode, equals

  @Override
  public final String toHexString () {
    return getClass().getSimpleName() + "[" +
      p0.toHexString() + ", " +
      p1.toHexString() + "]"; }

  @Override
  public String toString () { return toHexString(); }

  @Override
  public String description () { return toString(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private SegmentD2 (final VectorD2 a,
                    final VectorD2 b)  {
    super();
    this.p0 = a; this.p1 = b; }

  public static SegmentR2 of (final VectorD2 a,
                              final VectorD2 b) {
    return new SegmentD2(a, b); }

  /** Convert other triangle classes. */

  public static SegmentR2 from (final SegmentR2 t) {
    return of(t.getP0(), t.getP1()); }

//-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
