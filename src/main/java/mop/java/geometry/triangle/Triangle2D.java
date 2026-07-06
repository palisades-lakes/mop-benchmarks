package mop.java.geometry.triangle;

import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Triangles "embedded" in Vector2D.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-06
 */

public abstract class Triangle2D {

//  private final Vector2D p0;
//  private final Vector2D p1;
//  private final Vector2D p2;
//  public final Vector2D getP0 () { return p0; }
//  public final Vector2D getP1 () { return p1; }
//  public final Vector2D getP3 () { return p2; }

  //--------------------------------------------------------------------
  // TODO: an estimate of accuracy for each operation would be better.
  /** Is this algorithm exact (to the resolution expansions)
   * or approximate?
   */
  public boolean signedAreaExact() { return false; }

  //--------------------------------------------------------------------
  /** Return a positive value if the points pa, pb, and pc occur in
   * counterclockwise order; a negative value if they occur in clockwise
   * order; and zero if they are collinear.  The result is also a rough
   * approximation of twice the signed area of the triangle defined by
   * the three points.
   * <br>
   * Only Fast and Default should be used; the other two are for
   * timings.
   * <br>
   * Exact, Slow, and Default use exact arithmetic to ensure a correct
   * answer. The result returned is the determinant of a matrix.  In
   * signedArea() only, this determinant is computed adaptively, in the
   * sense that exact arithmetic is used only to the degree it is needed
   * to ensure that the returned value has the correct sign.  Hence,
   * signedArea() is usually quite fast, but will run more slowly when the
   * input points are collinear or nearly so.
   */

  public double signedArea (final Vector2D pa,
                            final Vector2D pb,
                            final Vector2D pc) {
    throw new UnsupportedOperationException(
      getClass().getSimpleName()); }

  //--------------------------------------------------------------------

  //public abstract boolean inCircleExact();
  public boolean inCircleExact () { return false; }

  /** Return a positive value if the point pd lies inside the circle
   * passing through pa, pb, and pc; a negative value if it lies
   * outside; and zero if the four points are cocircular. The points pa,
   * pb, and pc must be in counterclockwise order, or the sign of the
   * result will be reversed.
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
  public double inCircle (final Vector2D pa,
                          final Vector2D pb,
                          final Vector2D pc,
                          final Vector2D p) {
    throw new UnsupportedOperationException(getClass().getSimpleName()); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

//  private Triangle2D (final Vector2D a,
//                      final Vector2D b,
//                      final Vector2D c) {
//    super();
//    this.p0 = a; this.p1 = b; this.p2 = c;
//  }

//  public Triangle2D () {
//    this(Vector2D.ZERO,Vector2D.ZERO,Vector2D.ZERO); }

  public Triangle2D () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
