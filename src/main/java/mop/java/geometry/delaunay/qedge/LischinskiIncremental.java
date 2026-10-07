package mop.java.geometry.delaunay.qedge;

import mop.java.geometry.euclidean.VectorD2;

/** Algorithm for Delaunay triangulation, after
 * <a href="https://www.researchgate.net/publication/262235495_Incremental_Delaunay_Triangulation">
 *  Dani Lischinski
 *  <i>Incremental Delaunay triangulation</i>
 *  <b>Graphics Gems IV</b> Academic Press 1994 </a>
 * <p>
 * <b>NOTE: </b> all inserted points must fall within the existing mesh!
 * </p>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-04
 */

public final class LischinskiIncremental {

  //--------------------------------------------------------------------
  // geometric predicates
  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * Returns twice the signed area of the oriented triangle (a, b, c),
   * i.e., the area is positive if the triangle is counterclockwise.
   */

  private static final double signedArea (final VectorD2 a,
                                          final VectorD2 b,
                                          final VectorD2 c) {

    return
      (b.x() - a.x())*(c.y() - a.y())
        -
        (b.y() - a.y())*(c.x() - a.x()); }

  /** Lischinski: <br>
   * Returns true if the point d is inside the circle defined by the
   * points a, b, c. See Guibas and Stolfi (1985) p.107.
   */

  private static final boolean inCircle (final VectorD2 a,
                                         final VectorD2 b,
                                         final VectorD2 c,
                                         final VectorD2 d) {
    return
      ((a.l2norm2() * signedArea(b, c, d))
        + (b.l2norm2() * signedArea(c, a, d))
        + (c.l2norm2() * signedArea(a, b, d))
        + (d.l2norm2() * signedArea(b, a, c)))
        > 0.0; }

  private static final boolean ccw (final VectorD2 a,
                                    final VectorD2 b,
                                    final VectorD2 c) {
    return (signedArea(a, b, c) > 0); }

  private static final boolean rightOf (final VectorD2 x,
                                        final QEdge e) {
    return ccw(x, e.reverse().origin(), e.origin()); }

  private static final boolean leftOf (final VectorD2 x,
                                       final QEdge e) {
    return ccw(x, e.origin(), e.reverse().origin()); }

  //--------------------------------------------------------------------
  /** Used to test for point on line. */

  private static final class Line {

    private final double a;
    private final double b;
    private final double c;

    private final double eval (final VectorD2 p) {
      // Plugs point p into the line equation.
      return (a * p.x() + b * p.y() + c); }

    Line (final VectorD2 p, final VectorD2 q) {
      final VectorD2 t = q.subtract(p);
      final double len = Math.sqrt(t.l2norm2());
      a =   t.y() / len;
      b = - t.x() / len;
      c = -(a*p.x() + b*p.y()); } }

  //--------------------------------------------------------------------
  private static final double EPS = 1.0e-6;
  private static final double EPS2 = EPS*EPS;

  /** Lischinski: <br>
   * A predicate that determines if the point x is on the edge e.
   * The point is considered on if it is in the EPS-neighborhood
   * of the edge.
   * <br>
   * NOTE: Lischinski uses norm rather than norm2.
   * This requires a sqrt call, costing time and adding rounding error
   */

  private static final boolean onEdge (final VectorD2 x,
                                       final QEdge e) {
    final double t1 = x.subtract(e.origin()).l2norm2();
    final double t2 = x.subtract(e.reverse().origin()).l2norm2();
    if (t1 < EPS2 || t2 < EPS2) { return true; }
    final double t3 = e.origin().subtract(e.reverse().origin()).l2norm2();
    if (t1 > t3 || t2 > t3) { return false; }
    final Line line = new Line(e.origin(), e.reverse().origin());
    return (Math.abs(line.eval(x)) < EPS); }

  //--------------------------------------------------------------------
  // Incremental delaunay
  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * Returns an edge e, s.t. either x is on e, or e is an edge of
   * a triangle containing x. The search starts from startingEdge
   * and proceeds in the general direction of x. Based on the
   * pseudocode in Guibas and Stolfi (1985) p.121.
   */

  private static final QEdge locate (final QMesh mesh,
                                     final VectorD2 x) {
    QEdge e = mesh.startingEdge();
    while (true) {
      if (x.equals(e.origin())) { return e; }
      if (x.equals(e.reverse().origin())) { return e; }

      if (rightOf(x, e)) { e = e.reverse(); }
      else if (! rightOf(x, e.next())) { e = e.next(); }
      else if (! leftOf(x, e.faceNext())) {
        e = e.faceNext().reverse(); }
      else { return e; } } }

  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * Inserts a new point into a subdivision representing a Delaunay
   * triangulation, and fixes the affected edges so that the result
   * is still a Delaunay triangulation. This is based on the
   * pseudocode from Guibas and Stolfi (1985) p.120, with slight
   * modifications and a bug fix.
   */

  public static final void insertSite (final QMesh mesh,
                                       final VectorD2 x) {

    QEdge e = locate(mesh,x);
    // point is already in QMesh
    if ((x.equals(e.origin())) || (x.equals(e.reverse().origin())))  {
      return; }
    else if (onEdge(x, e)) { e = e.prev(); e.next().delete(); }
    // Connect the new point to the vertices of the containing
    // triangle (or quadrilateral, if the new point fell on an
    // existing edge.)
    QEdge base = QEdge.make(e.origin(), x);
    base.splice(e);
    mesh.setStartingEdge(base);
    do {
      base = e.connect(base.reverse());
      e = base.prev();
    } while (e.faceNext() != mesh.startingEdge());
    // Examine suspect edges to ensure that the Delaunay condition
    // is satisfied.
    do {
      QEdge t = e.prev();
      if (rightOf(t.reverse().origin(), e) &&
        inCircle(e.origin(), t.reverse().origin(), e.reverse().origin(),
                 x)) {
        e.swap();
        e = e.prev(); }
      // no more suspect edges
      else if (e.next() == mesh.startingEdge()) { return; }
      // pop a suspect edge
      else { e = e.next().next().reverse(); }
    } while (true); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  /** disabled constructor, class methods only. */

  private LischinskiIncremental () {
   throw new UnsupportedOperationException(
     getClass().getSimpleName() + ": construction not allowed."); }

//--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------

