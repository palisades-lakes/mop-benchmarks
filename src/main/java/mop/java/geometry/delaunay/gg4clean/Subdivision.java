package mop.java.geometry.delaunay.gg4clean;

import mop.java.geometry.euclidean.VectorD2;

/** Delaunay triangulation, after
 * <a href="https://www.researchgate.net/publication/262235495_Incremental_Delaunay_Triangulation">
 *  Dani Lischinski
 *  <i>Incremental Delaunay triangulation</i>
 *  <b>Graphics Gems IV</b> Academic Press 1994 </a>

 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-01
 */

public final class Subdivision {

  private Edge startingEdge;

  //--------------------------------------------------------------------
  // Basic Topological Operators
  //--------------------------------------------------------------------

  private static final Edge makeEdge () {
    // TODO: should this return the edge array or the 0th edge?
    final QuadEdge q = new QuadEdge();
    return q.edge(0); }

  private static final Edge makeEdge (final VectorD2 a,
                                      final VectorD2 b) {
    // TODO: should this return the edge array or the 0th edge?
    final Edge e = makeEdge();
    e.setEndpoints(a,b);
    return e; }

  //--------------------------------------------------------------------

  private static final void splice (final Edge a, final Edge b) {
    // This operator affects the two edge rings around the origins of a and b,
    // and, independently, the two edge rings around the left faces of a and b.
    // In each case, (i) if the two rings are distinct, Splice will combine
    // them into one; (ii) if the two are the same ring, Splice will break it
    // into two separate pieces.
    // Thus, Splice can be used both to attach the two edges together, and
    // to break them apart. See Guibas and Stolfi (1985) p.96 for more details
    // and illustrations.
    final Edge alpha = a.srcNext().dual();
    final Edge beta = b.srcNext().dual();
    final Edge t1 = b.srcNext();
    final Edge t2 = a.srcNext();
    final Edge t3 = beta.srcNext();
    final Edge t4 = alpha.srcNext();
    a.next = t1;
    b.next = t2;
    alpha.next = t3;
    beta.next = t4; }

  //--------------------------------------------------------------------

  private static final void deleteEdge (final Edge e) {
    splice(e, e.srcPrev());
    splice(e.reverse(), e.reverse().srcPrev());
    // garbage collection!
    // delete e.Qedge();
  }

  //--------------------------------------------------------------------

  private static final Edge connect (final Edge a, final Edge b) {
    // Add a new edge e connecting the destination of a to the
    // origin of b, in such a way that all three have the same
    // left face after the connection is complete.
    // Additionally, the data pointers of the new edge are set.
    final Edge e = makeEdge();
    splice(e, a.leftNext());
    splice(e.reverse(), b);
    e.setEndpoints(a.dst(), b.src());
    return e; }

  //--------------------------------------------------------------------

  private static final void swap (final Edge e) {
    // Essentially turns edge e counterclockwise inside its enclosing
    // quadrilateral. The data pointers are modified accordingly.
    final Edge a = e.srcPrev();
    final Edge b = e.reverse().srcPrev();
    splice(e, a);
    splice(e.reverse(), b);
    splice(e, a.leftNext());
    splice(e.reverse(), b.leftNext());
    e.setEndpoints(a.dst(), b.dst()); }

  //--------------------------------------------------------------------
  // geometric predicates
  //--------------------------------------------------------------------

  private static final double triArea (final VectorD2 a,
                                       final VectorD2 b,
                                       final VectorD2 c) {
    // Returns twice the signed area of the oriented triangle (a, b, c), i.e., the
    // area is positive if the triangle is oriented counterclockwise.

    return
      (b.x() - a.x())*(c.y() - a.y())
        -
        (b.y() - a.y())*(c.x() - a.x()); }

  private static final boolean inCircle (final VectorD2 a,
                                         final VectorD2 b,
                                         final VectorD2 c,
                                         final VectorD2 d) {
    // Returns true if the point d is inside the circle defined by the
    // points a, b, c. See Guibas and Stolfi (1985) p.107.

    return
      ((a.x()*a.x() + a.y()*a.y()) * triArea(b, c, d) -
        (b.x()*b.x() + b.y()*b.y()) * triArea(a, c, d) +
        (c.x()*c.x() + c.y()*c.y()) * triArea(a, b, d) -
        (d.x()*d.x() + d.y()*d.y()) * triArea(a, b, c))
        > 0.0; }

  private static final boolean ccw (final VectorD2 a,
                                    final VectorD2 b,
                                    final VectorD2 c) {
    // Returns true if the points a, b, c are in a counterclockwise order
    return (triArea(a, b, c) > 0); }

  private static final boolean rightOf (final VectorD2 x,
                                        final Edge e) {
    return ccw(x, e.dst(), e.src()); }

//  private static final boolean leftOf (final VectorD2 x,
//                                       final  Edge e) {
//    return ccw(x, e.src(), e.dst()); }

  private static final double EPS = 1.0e-6;
  private static final double EPS2 = EPS*EPS;

  //--------------------------------------------------------------------
  /** Used to test for point on line */

  private static final class Line {

    private final double a;
    private final double b;
    private final double c;

    final double eval (final VectorD2 p) {
      // Plugs point p into the line equation.
      return (a * p.x() + b * p.y() + c); }

    Line (final VectorD2 p, final VectorD2 q) {
      final VectorD2 t = q.subtract(p);
      final double len = Math.sqrt(t.l2norm2());
      a =   t.y() / len;
      b = - t.x() / len;
      c = -(a*p.x() + b*p.y()); } }

  //--------------------------------------------------------------------

  private static final boolean onEdge (final VectorD2 x,
                                       final Edge e) {
    // A predicate that determines if the point x is on the edge e.
    // The point is considered on if it is in the EPS-neighborhood
    // of the edge.
    // NOTE: Lischinski uses norm rather than norm2.
    // This requires a sqrt call, costing time and adding rounding error
    final double t1 = x.subtract(e.src()).l2norm2();
    final double t2 = x.subtract(e.dst()).l2norm2();
    if (t1 < EPS2 || t2 < EPS2) { return true; }
    final double t3 = e.src().subtract(e.dst()).l2norm2();
    if (t1 > t3 || t2 > t3) { return false; }
    final Line line = new Line(e.src(), e.dst());
    return (Math.abs(line.eval(x)) < EPS); }

  //--------------------------------------------------------------------
  // Incremental delaunay
  //--------------------------------------------------------------------

  private final Edge locate (final VectorD2 x) {
    // Returns an edge e, s.t. either x is on e, or e is an edge of
    // a triangle containing x. The search starts from startingEdge
    // and proceeds in the general direction of x. Based on the
    // pseudocode in Guibas and Stolfi (1985) p.121.
    Edge e = startingEdge;
    while (true) {
      if (x == e.src() || x == e.dst()) { return e; }
      else if (rightOf(x, e)) { e = e.reverse(); }
      else if (!rightOf(x, e.srcNext())) { e = e.srcNext(); }
      else if (!rightOf(x, e.dstPrev())) { e = e.dstPrev(); }
      else { return e; } } }

  //--------------------------------------------------------------------

  public final void insertSite (final VectorD2 x) {

    // Inserts a new point into a subdivision representing a Delaunay
    // triangulation, and fixes the affected edges so that the result
    // is still a Delaunay triangulation. This is based on the
    // pseudocode from Guibas and Stolfi (1985) p.120, with slight
    // modifications and a bug fix.

    Edge e = locate(x);
    // point is already in Subdivision
    if ((x == e.src()) || (x == e.dst()))  { return; }
    else if (onEdge(x, e)) {
      e = e.srcPrev();
      deleteEdge(e.srcNext()); }
    // Connect the new point to the vertices of the containing
    // triangle (or quadrilateral, if the new point fell on an
    // existing edge.)
    Edge base = makeEdge();
    base.setEndpoints(e.src(), x);
    splice(base, e);
    startingEdge = base;
    do {
      base = connect(e, base.reverse());
      e = base.srcPrev();
    } while (e.leftNext() != startingEdge);
    // Examine suspect edges to ensure that the Delaunay condition
    // is satisfied.
    do {
      Edge t = e.srcPrev();
      if (rightOf(t.dst(), e) &&
        inCircle(e.src(), t.dst(), e.dst(), x)) {
        swap(e);
        e = e.srcPrev(); }
      // no more suspect edges
      else if (e.srcNext() == startingEdge) { return; }
      // pop a suspect edge
      else { e = e.srcNext().leftPrev(); }
    } while (true); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  /** All inserted points must lie in the triangle formed by
   * <code>a, b, c</code>.
   */

  public Subdivision (final VectorD2 a,
                      final VectorD2 b,
                      final VectorD2 c) {
    // Initialize a subdivision to the triangle
    // defined by the points a, b, c.
    // TODO: check that changing the order of splice() and setEndpoints()
    //  is ok
    final Edge ab = makeEdge(a,b);
    final Edge bc = makeEdge(b,c);
    final Edge ca = makeEdge(c,a);
    splice(ab.reverse(), bc);
    splice(bc.reverse(), ca);
    splice(ca.reverse(), ab);
    startingEdge = ab; }

//--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------

