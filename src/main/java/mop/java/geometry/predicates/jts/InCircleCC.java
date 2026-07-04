package mop.java.geometry.predicates.jts;

import mop.java.geometry.predicates.Predicate;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-04
 */

@SuppressWarnings("unused")
public final class InCircleCC implements Predicate {

  //--------------------------------------------------------------------
  /**
   * Computes the determinant of a 2x2 matrix. Uses standard double-precision
   * arithmetic, so is susceptible to round-off error.
   *
   * @param m00
   *          the [0,0] entry of the matrix
   * @param m01
   *          the [0,1] entry of the matrix
   * @param m10
   *          the [1,0] entry of the matrix
   * @param m11
   *          the [1,1] entry of the matrix
   * @return the determinant
   */
  private static double det (final double m00,
                             final double m01,
                             final double m10,
                             final double m11) {
    return m00 * m11 - m01 * m10; }
  //--------------------------------------------------------------------
  /** org.locationtech.jts.geom.Triangle
   * Computes the circumcentre of a triangle. The circumcentre is the centre of
   * the circumcircle, the smallest circle which encloses the triangle. It is
   * also the common intersection point of the perpendicular bisectors of the
   * sides of the triangle, and is the only point which has equal distance to
   * all three vertices of the triangle.
   * <p>
   * The circumcentre does not necessarily lie within the triangle. For example,
   * the circumcentre of an obtuse isosceles triangle lies outside the triangle.
   * <p>
   * This method uses an algorithm due to J.R.Shewchuk which uses normalization
   * to the origin to improve the accuracy of computation. (See <i>Lecture Notes
   * on Geometric Robustness</i>, Jonathan Richard Shewchuk, 1999).
   */
  private static final Vector2D circumcentre (final Vector2D a,
                                              final Vector2D b,
                                              final Vector2D c) {
    final double cx = c.getX();
    final double cy = c.getY();
    final double ax = a.getX() - cx;
    final double ay = a.getY() - cy;
    final double bx = b.getX() - cx;
    final double by = b.getY() - cy;

    final double denom = 2 * det(ax, ay, bx, by);
    // TODO: singular triangle => denom = 0
    //  What should the circumcenter and radius be?
    //  3 vertexes the same:
    //    any vtx is circumcenter, radius 0.0
    //  2 vertices the same, or 3 colinear:
    //    Infinite radius, infinite center?
    //  Try vtx mean as center, and zero or infinite radius depending
    if (0.0==denom) {
      if ((0.0==ax) && (0.0==ay) && (0.0==bx) && (0.0==by)) {
        // 1 pt triangle
        return a; }
      // else triangle is a line segment, center is pt at infinity
      // TODO: immutable singleton?
      return Vector2D.of(Double.POSITIVE_INFINITY,
                            Double.POSITIVE_INFINITY); }
    final double numx = det(ay,
                            ax * ax + ay * ay, by,
                            bx * bx + by * by);
    final double numy = det(ax,
                            ax * ax + ay * ay, bx,
                            bx * bx + by * by);

    final double ccx = cx - numx / denom;
    final double ccy = cy + numy / denom;

    return Vector2D.of(ccx, ccy); }

  /**
   * Computes the length of the vector (x,y).
   * This is the length of the hypotenuse of
   * a right triangle with sides of length x and y.
   * <br>
   * This function is faster than the standard Math.hypot function.
   *
   * @param x the x ordinate
   * @param y the y ordinate
   * @return the length of vector (x,y)
   */
  private static final double hypot (final double x, final double y) {
    return Math.sqrt(x * x +  y * y);
  }

  /**
   * Computes the 2-dimensional Euclidean distance to another location.
   * The Z-ordinate is ignored.
   *
   * @param c a point
   * @return the 2-dimensional Euclidean distance between the locations
   */
  private static double distance (final Vector2D a, final Vector2D c) {
    double dx = a.getX() - c.getX();
    double dy = a.getY() - c.getY();
    return hypot(dx, dy);
  }

  //--------------------------------------------------------------------
  // incircle
  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  public final double incircle (final Vector2D a,
                                final Vector2D b,
                                final Vector2D c,
                                final Vector2D p) {
    final Vector2D cc = circumcentre(a, b, c);
    // sign reversed from JTS for consistency with other predicates
    // TODO: could we use squared distance?
    return distance(a,cc) - distance(p, cc);
  }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public InCircleCC () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
