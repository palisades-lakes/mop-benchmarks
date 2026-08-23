package mop.java.geometry.triangle;

import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Minimal triangle with double Vector2D vertices.
 * <b>
 * From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-08-18
 */

public final class TriangleVector2D extends Triangle2D {

  //--------------------------------------------------------------------

  private static double triArea (final Vector2D a,
                                 final Vector2D b,
                                 final Vector2D c) {
    // TODO: cache difference vectors
    return b.subtract(a).signedArea(c.subtract(a)); }

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
  public final double inCircleDistance (final Vector2D p) {
    // TODO: cache normSq?
    final Vector2D a = getP0();
    final Vector2D b = getP1();
    final Vector2D c = getP2();

    return
      a.normSq()*triArea(b,c,p) - b.normSq()*triArea(a,c,p)
        + c.normSq()*triArea(a,b,p) - p.normSq()*triArea(a,b,c); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private TriangleVector2D (final Vector2D a,
                            final Vector2D b,
                            final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new TriangleVector2D(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
