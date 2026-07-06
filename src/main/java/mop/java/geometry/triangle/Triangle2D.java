package mop.java.geometry.triangle;

import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Triangles "embedded" in Vector2D.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-06
 */

public abstract class Triangle2D {

  //--------------------------------------------------------------------
  // TODO: algorithm might be exact for some operations and not others.
  // TODO: an estimate of accuracy for each operation would be better.
  /** Is this algorithm exact (to the resolution expansions)
   * or approximate?
   */
  public boolean signedAreaExact() { return false; }
  //public abstract boolean signedAreaExact();

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
                          final Vector2D pd) {
    throw new UnsupportedOperationException(getClass().getSimpleName()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
