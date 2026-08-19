package mop.java.geometry.triangle;

import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Triangles "embedded" in Vector2D.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-27
 */

public abstract class Triangle2D {

  private final Vector2D p0;
  private final Vector2D p1;
  private final Vector2D p2;
  public final Vector2D getP0 () { return p0; }
  public final Vector2D getP1 () { return p1; }
  public final Vector2D getP2 () { return p2; }

//  public final Vector2D getP (final int i) {
//    return switch (i) {
//      case 0 -> p0;
//      case 1 -> p1;
//      case 2 -> p2;
//      default -> throw new IndexOutOfBoundsException(); }; }

  //--------------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------------
  // TODO: hashcode, equals

  public static final String toHexString (final Vector2D p) {
    return "(" +
      Double.toHexString(p.getX()) + "," +
      Double.toHexString(p.getY()) + ")"; }

  public final String toHexString () {
    return getClass().getSimpleName() + "[" +
      toHexString(p0) + ", " +
      toHexString(p1) + ", " +
      toHexString(p2) + "]"; }

  public String toString () { return toHexString(); }

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
   */

  public double twiceSignedArea () {
    throw new UnsupportedOperationException(
      getClass().getSimpleName()); }

  //--------------------------------------------------------------------
  /** Not clear exactly what I want here. For now, indicate whether
   * all Kettner orientation tests should pass.
   */
  public boolean isOrientationRobust () { return signedAreaExact(); }

  //--------------------------------------------------------------------
  /** Return +1  if the points pa, pb, and pc occur in
   * counterclockwise order; -1 if they occur in clockwise
   * order; and zero if they are collinear.
   * <br>
   * Separating this from <code>twiceSignedArea</code>
   * permits classes that do more precise calculation to get the sign
   * from that, rather than forcing a round to <code>double</code>.
   */

  public int orientation () {
    // TODO: what to do with NaN? Return 0 meaning 'not oriented'?
    // NOTE: Double.compare() doesn't handle +-0.0 correctly.
    final double a = twiceSignedArea();
    if (0.0 < a) { return 1; }
    if (0.0 > a) { return -1; }
    return 0; }

  //--------------------------------------------------------------------

  public boolean inCircleExact () { return false; }

  /** Return a positive value if the point pd lies inside the circle
   * passing through p0, p1, and p2; a negative value if it lies
   * outside; and zero if the four points are cocircular. The points pa,
   * pb, and pc must be in counterclockwise order, or the sign of the
   * result will be reversed.
   * <br>
   * (Shewchuk predicate.c)
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
  public double inCircle (final Vector2D p) {
    throw new UnsupportedOperationException(getClass().getSimpleName()); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public Triangle2D (final Vector2D a,
                     final Vector2D b,
                     final Vector2D c) {
    super();
    this.p0 = a; this.p1 = b; this.p2 = c; }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
