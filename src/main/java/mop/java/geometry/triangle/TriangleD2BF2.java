package mop.java.geometry.triangle;

import mop.java.geometry.euclidean.VectorD2;

/** Two delegate triangles, one fast and approximate,
 * one slow and exact.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-29
 */

public final class TriangleD2BF2 implements TriangleR2 {

  // eager
  private final TriangleD2 _triangleD2;
  // fast and approximate
  private final TriangleD2 getTriangleD2 () { return _triangleD2; }

  public final VectorD2 getP0 () { return _triangleD2.getP0(); }
  public final VectorD2 getP1 () { return _triangleD2.getP1(); }
  public final VectorD2 getP2 () { return _triangleD2.getP2(); }

  // lazy
  private TriangleBF2 _triangleBF2 = null;
  // slow and exact
  private final TriangleBF2 getTriangleBF2 () {
    if (null == _triangleBF2) {
      _triangleBF2 = (TriangleBF2) TriangleBF2.from(this); }
    return _triangleBF2; }

  //--------------------------------------------------------------------

  public final double twiceSignedArea () {
    if (isOrientationRobust()) { return getTriangleD2().twiceSignedArea(); }
    return getTriangleBF2().twiceSignedArea(); }

  //--------------------------------------------------------------------

  public final double inCircleDistance (final VectorD2 p) {

    if (inCircleRobust(p)) { return getTriangleD2().inCircleDistance(p); }
    return getTriangleBF2().inCircleDistance(p); }

//--------------------------------------------------------------------

  public final String toString () { return toHexString(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public TriangleD2BF2 (final VectorD2 a,
                        final VectorD2 b,
                        final VectorD2 c)  {
    super();
   _triangleD2 = (TriangleD2) TriangleD2Lazy.of(a,b,c); }

  public static final TriangleR2 of (final VectorD2 a,
                                     final VectorD2 b,
                                     final VectorD2 c) {
    return new TriangleD2BF2(a, b, c); }

  /** Convert other triangle classes. */

  public static final TriangleR2 from (final TriangleR2 t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
