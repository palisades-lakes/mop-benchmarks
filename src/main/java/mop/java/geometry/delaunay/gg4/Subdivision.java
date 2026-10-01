package mop.java.geometry.delaunay.gg4;

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

  public static final Edge MakeEdge () {
    // TODO: should this return the edge array or the 0th edge?
    final QuadEdge ql = new QuadEdge();
    return ql.edge(0); }

  //--------------------------------------------------------------------

  public static final void Splice (final Edge a, final Edge b) {
    // This operator affects the two edge rings around the origins of a and b,
    // and, independently, the two edge rings around the left faces of a and b.
    // In each case, (i) if the two rings are distinct, Splice will combine
    // them into one; (ii) if the two are the same ring, Splice will break it
    // into two separate pieces.
    // Thus, Splice can be used both to attach the two edges together, and
    // to break them apart. See Guibas and Stolfi (1985) p.96 for more details
    // and illustrations.
    final Edge alpha = a.Onext().Rot();
    final Edge beta = b.Onext().Rot();
    final Edge t1 = b.Onext();
    final Edge t2 = a.Onext();
    final Edge t3 = beta.Onext();
    final Edge t4 = alpha.Onext();
    a.next = t1;
    b.next = t2;
    alpha.next = t3;
    beta.next = t4; }

  //--------------------------------------------------------------------

  public static final void DeleteEdge (final Edge e) {
    Splice(e, e.Oprev());
    Splice(e.Sym(), e.Sym().Oprev());
    // garbage collection!
    // delete e.Qedge();
  }

  //--------------------------------------------------------------------

  public static final Edge Connect (final Edge a, final Edge b) {
    // Add a new edge e connecting the destination of a to the
    // origin of b, in such a way that all three have the same
    // left face after the connection is complete.
    // Additionally, the data pointers of the new edge are set.
    final Edge e = MakeEdge();
    Splice(e, a.Lnext());
    Splice(e.Sym(), b);
    e.EndPoints(a.Dest(), b.Org());
    return e; }

  //--------------------------------------------------------------------

  public static final void Swap (final Edge e) {
    // Essentially turns edge e counterclockwise inside its enclosing
    // quadrilateral. The data pointers are modified accordingly.
    final Edge a = e.Oprev();
    final Edge b = e.Sym().Oprev();
    Splice(e, a);
    Splice(e.Sym(), b);
    Splice(e, a.Lnext());
    Splice(e.Sym(), b.Lnext());
    e.EndPoints(a.Dest(), b.Dest()); }

  //--------------------------------------------------------------------
  // geometric predicates
  //--------------------------------------------------------------------
  public static final double TriArea (final VectorD2 a,
                                      final VectorD2 b,
                                      final VectorD2 c) {
    // Returns twice the area of the oriented triangle (a, b, c), i.e., the
    // area is positive if the triangle is oriented counterclockwise.

    return
      (b.x() - a.x())*(c.y() - a.y())
        -
        (b.y() - a.y())*(c.x() - a.x()); }

  public static final boolean InCircle (final VectorD2 a,
                                        final VectorD2 b,
                                        final VectorD2 c,
                                        final VectorD2 d) {
    // Returns true if the point d is inside the circle defined by the
    // points a, b, c. See Guibas and Stolfi (1985) p.107.

    return
      ((a.x()*a.x() + a.y()*a.y()) * TriArea(b, c, d) -
        (b.x()*b.x() + b.y()*b.y()) * TriArea(a, c, d) +
        (c.x()*c.x() + c.y()*c.y()) * TriArea(a, b, d) -
        (d.x()*d.x() + d.y()*d.y()) * TriArea(a, b, c))
        > 0.0; }

  public static final boolean ccw (final VectorD2 a,
                                   final VectorD2 b,
                                   final VectorD2 c) {
    // Returns true if the points a, b, c are in a counterclockwise order
    return (TriArea(a, b, c) > 0); }

  public static final boolean RightOf (final VectorD2 x, final Edge e) {
    return ccw(x, e.Dest2d(), e.Org2d()); }

  public static final boolean LeftOf (final VectorD2 x, final  Edge e) {
    return ccw(x, e.Org2d(), e.Dest2d()); }

  private static final double EPS = 1.0e-6;
  private static final double EPS2 = EPS*EPS;

  //--------------------------------------------------------------------
  /** Used to test for point on line */

  private static final class Line {

    private final double a;
    private final double b;
    private final double c;

    public final double eval(final VectorD2 p) {
      // Plugs point p into the line equation.
      return (a * p.x() + b * p.y() + c); }

    public final int classify (final VectorD2 p) {
      // Returns -1, 0, or 1, if p is to the left of, on,
      // or right of the line, respectively.
      final double d = eval(p);
      return (d < -EPS) ? -1 : (d > EPS ? 1 : 0); }

    public Line (final VectorD2 p, final VectorD2 q) {
      final VectorD2 t = q.subtract(p);
      final double len = Math.sqrt(t.l2norm2());
      a =   t.y() / len;
      b = - t.x() / len;
      c = -(a*p.x() + b*p.y()); } }

  //--------------------------------------------------------------------

  public static final boolean OnEdge (final VectorD2 x, final  Edge e) {
    // A predicate that determines if the point x is on the edge e.
    // The point is considered on if it is in the EPS-neighborhood
    // of the edge.
    // NOTE: Lischinski uses norm rather than norm2.
    // This requires a sqrt call, costing time and adding rounding error
    final double t1 = x.subtract(e.Org2d()).l2norm2();
    final double t2 = x.subtract(e.Dest2d()).l2norm2();
    if (t1 < EPS2 || t2 < EPS2) { return true; }
    final double t3 = e.Org2d().subtract(e.Dest2d()).l2norm2();
    if (t1 > t3 || t2 > t3) { return false; }
    final Line line = new Line(e.Org2d(), e.Dest2d());
    return (Math.abs(line.eval(x)) < EPS);
  }
  //--------------------------------------------------------------------
  // Incremental delaunay
  //--------------------------------------------------------------------

  public final Edge Locate (final VectorD2 x) {
    // Returns an edge e, s.t. either x is on e, or e is an edge of
    // a triangle containing x. The search starts from startingEdge
    // and proceeds in the general direction of x. Based on the
    // pseudocode in Guibas and Stolfi (1985) p.121.

    Edge e = startingEdge;
    while (true) {
      if (x == e.Org2d() || x == e.Dest2d()) { return e; }
      else if (RightOf(x, e)) { e = e.Sym(); }
      else if (!RightOf(x, e.Onext())) { e = e.Onext(); }
      else if (!RightOf(x, e.Dprev())) { e = e.Dprev(); }
      else { return e; } } }

  //--------------------------------------------------------------------

  public final void InsertSite (final VectorD2 x) {
    // Inserts a new point into a subdivision representing a Delaunay
    // triangulation, and fixes the affected edges so that the result
    // is still a Delaunay triangulation. This is based on the
    // pseudocode from Guibas and Stolfi (1985) p.120, with slight
    // modifications and a bug fix.

    Edge e = Locate(x);
    if ((x == e.Org2d()) || (x == e.Dest2d())) // point is already in
      return;
    else if (OnEdge(x, e)) {
      e = e.Oprev();
      DeleteEdge(e.Onext());
    }
    // Connect the new point to the vertices of the containing
    // triangle (or quadrilateral, if the new point fell on an
    // existing edge.)
    Edge base = MakeEdge();
    base.EndPoints(e.Org(), x);
    Splice(base, e);
    startingEdge = base;
    do {
      base = Connect(e, base.Sym());
      e = base.Oprev();
    } while (e.Lnext() != startingEdge);
    // Examine suspect edges to ensure that the Delaunay condition
    // is satisfied.
    do {
      Edge t = e.Oprev();
      if (RightOf(t.Dest2d(), e) &&
        InCircle(e.Org2d(), t.Dest2d(), e.Dest2d(), x)) {
        Swap(e);
        e = e.Oprev();
      }
      else if (e.Onext() == startingEdge) // no more suspect edges
        return;
      else // pop a suspect edge
        e = e.Onext().Lprev();
    } while (true);
  }
  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public Subdivision (final VectorD2 a,
                      final VectorD2 b,
                      final VectorD2 c) {
    // Initialize a subdivision to the triangle defined by the points a, b, c.
    // Point2d *da, *db, *dc;
    // da = new Point2d(a), db = new Point2d(b), dc = new Point2d(c);
    // VectorD2 immutable!
    final VectorD2 da = a;
    final VectorD2 db = b;
    final VectorD2 dc = c;
    final Edge ea = MakeEdge();
    ea.EndPoints(da, db);
    final Edge eb = MakeEdge();
    Splice(ea.Sym(), eb);
    eb.EndPoints(db, dc);
    final Edge ec = MakeEdge();
    Splice(eb.Sym(), ec);
    ec.EndPoints(dc, da);
    Splice(ec.Sym(), ea);
    startingEdge = ea; }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------

