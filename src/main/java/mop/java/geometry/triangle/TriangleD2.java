package mop.java.geometry.triangle;

import mop.java.geometry.euclidean.VectorD2;

/** Minimal triangle with VectorD2 vertices, nothing cached.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-21
 */

public final class TriangleD2 extends Triangle2D {

  //--------------------------------------------------------------------

  private static double triArea (final VectorD2 a,
                                 final VectorD2 b,
                                 final VectorD2 c) {
    // TODO: cache difference vectors
    return b.subtract(a).wedge(c.subtract(a)); }

  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------
  // cache?

  @Override
  public final double twiceSignedArea () {
    return triArea(getP0(),getP1(),getP2()); }

  //--------------------------------------------------------------------
  // inCircle
  //--------------------------------------------------------------------
  // TODO: permute area calls to use cached differences?

  @Override
  public final double inCircleDistance (final VectorD2 p) {
    // TODO: cache l2norm2?
    final VectorD2 a = getP0();
    final VectorD2 b = getP1();
    final VectorD2 c = getP2();

    return
      a.l2norm2()*triArea(b,c,p) - b.l2norm2()*triArea(a,c,p)
        + c.l2norm2()*triArea(a,b,p) - p.l2norm2()*triArea(a,b,c); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private TriangleD2 (final VectorD2 a,
                      final VectorD2 b,
                      final VectorD2 c)  {
    super(a,b,c); }

  public static final Triangle2D of (final VectorD2 a,
                                     final VectorD2 b,
                                     final VectorD2 c) {
    return new TriangleD2(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
