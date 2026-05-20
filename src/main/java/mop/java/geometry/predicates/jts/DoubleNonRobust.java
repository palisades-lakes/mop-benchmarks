package mop.java.geometry.predicates.jts;

import mop.java.geometry.predicates.Predicate;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-05-20
 */

public final class DoubleNonRobust implements Predicate {

  //--------------------------------------------------------------------
  /** TrianglePredicate.triArea
   * Computes twice the area of the oriented triangle (a, b, c), i.e., the area is positive if the
   * triangle is oriented counterclockwise.
   *
   * @param a a vertex of the triangle
   * @param b a vertex of the triangle
   * @param c a vertex of the triangle
   */
  private static double triArea (double[] a, double[] b, double[] c) {
    return (b[0] - a[0]) * (c[1] - a[1])
      - (b[1] - a[1]) * (c[0] - a[0]);
  }
  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------

  @Override
  public final double orient2d (final double[] a,
                                final double[] b,
                                final double[] c) {
    return triArea(a,b,c); }

  //--------------------------------------------------------------------
  // incircle
  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  public final double incircle (final double[] a,
                                final double[] b,
                                final double[] c,
                                final double[] p) {
    return (a[0] * a[0] + a[1] * a[1]) * triArea(b, c, p)
        - (b[0] * b[0] + b[1] * b[1]) * triArea(a, c, p)
        + (c[0] * c[0] + c[1] * c[1]) * triArea(a, b, p)
        - (p[0] * p[0] + p[1] * p[1]) * triArea(a, b, c); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public DoubleNonRobust () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
