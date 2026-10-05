package mop.java.geometry.segment;

import mop.java.geometry.euclidean.VectorBF2;
import mop.java.geometry.euclidean.VectorD2;

/** Standard calculations implemented in BigFloat.
 * Should be exact, with <code>double</code> inputs.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 202-10-05
 */

public final class SegmentBF2 implements SegmentR2 {

  // TODO: only need p0
  private final VectorD2 p0;
  private final VectorD2 p1;

  @Override
  public final VectorD2 getP0 () { return p0; }
  @Override
  public final VectorD2 getP1 () { return p1; }

  // cache vector result of translating p0 to origin,
  // and related quantities

  private VectorBF2 v10;
  public final VectorBF2 getV10 () {
    if (null == v10) { v10 = VectorBF2.dif(p1, p0); }
    return v10; }

  /** force cache calculation when profiling */
  public final void clearCaches () {
    v10 = null; }

  @Override
  public final boolean sideExact () { return true; }

  @Override
  public final boolean sideRobust (final VectorD2 ignore) {
    return true; }

  @Override
  public final double side (final VectorD2 p) {
    return getV10().wedge(VectorBF2.dif(p,p0)).doubleValue(); }

//--------------------------------------------------------------------

  public final String toString () {
    return toHexString() +
      "\nv10: " + getV10().toHexString() +
      "\n" ; }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private SegmentBF2 (final VectorD2 p0,
                      final VectorD2 p1)  {
    super();
    this.p0 = p0; this.p1 = p1; }

  public static final SegmentR2 of (final VectorD2 p0,
                                    final VectorD2 p1) {
    return new SegmentBF2(p0, p1); }

  public static final SegmentR2 from (final SegmentR2 t) {
    return of(t.getP0(), t.getP1()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
