package mop.java.geometry.triangle;

import mop.java.geometry.euclidean.VectorD2;

/** Triangle with VectorD2 vertices, precomputing reusable values.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-27
 */

public final record TriangleD2Eager (VectorD2 p0,
                                     // precomputed vector result
                                     // of translating vertices to origin,
                                     // and related quantities
                                     VectorD2 v10,
                                     VectorD2 v20,
                                     double v10Norm2,
                                     double v20Norm2,
                                     double v20Xv10)
  implements Triangle2D {

  @Override
  public final VectorD2 getP0 () { return p0; }
  @Override
  public final VectorD2 getP1 () { return null; }
  @Override
  public final VectorD2 getP2 () { return null; }

  //--------------------------------------------------------------------

  @Override
  public final boolean signedAreaExact () { return false; }

  @Override
  public final double twiceSignedArea () { return -v20Xv10; }

  //--------------------------------------------------------------------

  private static final double dot (final double x0,
                                   final double y0,
                                   final double z0,
                                   final double x1,
                                   final double y1,
                                   final double z1) {
    return x0*x1 + y0*y1 + z0*z1; }

  //--------------------------------------------------------------------

  @Override
  public final boolean inCircleDistanceExact () { return false; }

  @Override
  public final double inCircleDistance (final VectorD2 p) {

    final VectorD2 vp0 = p.subtract(p0);

    return dot(vp0.dL2norm2(), v10Norm2, v20Norm2,
               v20Xv10, vp0.wedge(v20), v10.wedge(vp0)); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public static final Triangle2D of (final VectorD2 p0,
                                     final VectorD2 p1,
                                     final VectorD2 p2) {
    final VectorD2 v10 = p1.subtract(p0);
    final VectorD2 v20 = p2.subtract(p0);
    return new TriangleD2Eager(
      p0, v10, v20,
      v10.dL2norm2(), v20.dL2norm2(), v20.wedge(v10)); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
