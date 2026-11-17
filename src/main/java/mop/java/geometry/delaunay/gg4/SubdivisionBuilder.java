package mop.java.geometry.delaunay.gg4;

import clojure.lang.IFn;
import mop.java.cmplx.SimplicialComplex2D;
import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.ZeroSimplex;
import mop.java.geom.mesh.TriangleMesh;
import mop.java.geometry.delaunay.DelaunayBuilder;
import mop.java.geometry.delaunay.Edge;
import mop.java.geometry.euclidean.VectorD2;

import java.util.*;

/** Delaunay triangulation, after
 * <a href="https://www.researchgate.net/publication/262235495_Incremental_Delaunay_Triangulation">
 *  Dani Lischinski
 *  <i>Incremental Delaunay triangulation</i>
 *  <b>Graphics Gems IV</b> Academic Press 1994 </a>

 * @author palisades dot lakes at gmail dot com
 * @version 2026-11-16
 */

public final class SubdivisionBuilder implements DelaunayBuilder {

  //--------------------------------------------------------------------

  private final Map<ZeroSimplex, VectorD2> _embedding;
  @Override
  public final Map<ZeroSimplex, VectorD2> embedding () {
    return _embedding; }

  // TODO: Map implementation that allows adding.
  //  but not modifying entries?
  @Override
  public final void embed (final ZeroSimplex z,
                           final VectorD2 p) {
    final Object previous = embedding().putIfAbsent(z,p);
    assert (null == previous); }

  @Override
  public final VectorD2 embedding (final ZeroSimplex z) {
    assert (null != z) : "No embedding for null";
    assert embedding().containsKey(z) : "No embedding for " + z;
    final VectorD2 p = embedding().get(z);
    assert (null != p);
    return p; }

  //--------------------------------------------------------------------

  private LEdge _startingEdge;
  /** The entry into the abstract cell complex represented
   * by the <code>QEdege</code>s.
   */
  public final LEdge startingEdge () { return _startingEdge; }
  /** The entry into the abstract cell complex represented
   * by the <code>QEdege</code>s.
   */
  final void setStartingEdge (final LEdge e) { _startingEdge = e; }

  //--------------------------------------------------------------------

  @Override
  public final SortedSet<ZeroSimplex> vertices () {
    return startingEdge().zeroSimplexes(); }

  @Override
  public final SortedSet<TwoSimplex> faces () {
    return startingEdge().twoSimplexes(); }

  //--------------------------------------------------------------------
  // Basic Topological Operators
  //--------------------------------------------------------------------

  private static final LEdge MakeEdge () {
    // TODO: should this return the edge array or the 0th edge?
    final Quartet ql = new Quartet();
    return ql.edge(0); }

  //--------------------------------------------------------------------

  private static final void Splice (final LEdge a, final LEdge b) {
    // This operator affects the two edge rings around the origins of a and b,
    // and, independently, the two edge rings around the left faces of a and b.
    // In each case, (i) if the two rings are distinct, Splice will combine
    // them into one; (ii) if the two are the same ring, Splice will break it
    // into two separate pieces.
    // Thus, Splice can be used both to attach the two edges together, and
    // to break them apart. See Guibas and Stolfi (1985) p.96 for more details
    // and illustrations.
    final LEdge alpha = a.Onext().Rot();
    final LEdge beta = b.Onext().Rot();
    final LEdge t1 = b.Onext();
    final LEdge t2 = a.Onext();
    final LEdge t3 = beta.Onext();
    final LEdge t4 = alpha.Onext();
    a.next = t1;
    b.next = t2;
    alpha.next = t3;
    beta.next = t4; }

  //--------------------------------------------------------------------

  static final void DeleteEdge (final LEdge e) {
    Splice(e, e.Oprev());
    Splice(e.Sym(), e.Sym().Oprev());
    // garbage collection!
    // delete e.Qedge();
  }

  //--------------------------------------------------------------------

  static final LEdge Connect (final LEdge a, final LEdge b) {
    // Add a new edge e connecting the destination of a to the
    // origin of b, in such a way that all three have the same
    // left face after the connection is complete.
    // Additionally, the data pointers of the new edge are set.
    final LEdge e = MakeEdge();
    Splice(e, a.Lnext());
    Splice(e.Sym(), b);
    e.EndPoints(a.Dest(), b.Org());
    return e; }

  //--------------------------------------------------------------------

  static final LEdge Swap (final LEdge e) {
    // Essentially turns edge e counterclockwise inside its enclosing
    // quadrilateral. The data pointers are modified accordingly.
    final LEdge a = e.Oprev();
    final LEdge b = e.Sym().Oprev();
    Splice(e, a);
    Splice(e.Sym(), b);
    Splice(e, a.Lnext());
    Splice(e.Sym(), b.Lnext());
    e.EndPoints(a.Dest(), b.Dest());
    return e; }

  //--------------------------------------------------------------------
  // geometric predicates
  //--------------------------------------------------------------------

  private static final double TriArea (final VectorD2 a,
                                       final VectorD2 b,
                                       final VectorD2 c) {

    // Returns twice the signed area of the oriented triangle (a, b, c),
    // i.e., the area is positive if the triangle is oriented
    // counterclockwise.

    return
      (b.x() - a.x())*(c.y() - a.y())
        -
        (b.y() - a.y())*(c.x() - a.x()); }

  private static final boolean InCircle (final VectorD2 a,
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

  private final boolean InCircle (final ZeroSimplex a,
                                  final ZeroSimplex b,
                                  final ZeroSimplex c,
                                  final ZeroSimplex d) {
    return
      InCircle(embedding(a), embedding(b), embedding(c), embedding(d)); }

  private static final boolean ccw (final VectorD2 a,
                                    final VectorD2 b,
                                    final VectorD2 c) {
    // Returns true if the points a, b, c are in a counterclockwise order
    return (TriArea(a, b, c) > 0); }

  private final boolean RightOf (final ZeroSimplex z, final LEdge e) {
    return ccw(embedding(z), embedding(e.Dest()), embedding(e.Org())); }

//  private static final boolean LeftOf (final VectorD2 x, final  HEdge e) {
//    return ccw(x, e.Org2d(), e.Dest2d()); }

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

//     final int classify (final VectorD2 p) {
//      // Returns -1, 0, or 1, if p is to the left of, on,
//      // or right of the line, respectively.
//      final double d = eval(p);
//      return (d < -EPS) ? -1 : (d > EPS ? 1 : 0); }

    Line (final VectorD2 p, final VectorD2 q) {
      final VectorD2 t = q.subtract(p);
      final double len = Math.sqrt(t.l2norm2());
      a =   t.y() / len;
      b = - t.x() / len;
      c = -(a*p.x() + b*p.y()); } }

  //--------------------------------------------------------------------

  private final boolean OnEdge (final ZeroSimplex z, final LEdge e) {
    // A predicate that determines if the point x is on the edge e.
    // The point is considered on if it is in the EPS-neighborhood
    // of the edge.
    // NOTE: Lischinski uses norm rather than norm2.
    // This requires a sqrt call, costing time and adding rounding error
    final VectorD2 x = embedding(z);
    final VectorD2 p0 = embedding(e.origin());
    final VectorD2 p1 = embedding(e.dest());
    final double t1 = x.subtract(p0).l2norm2();
    final double t2 = x.subtract(p1).l2norm2();
    if (t1 < EPS2 || t2 < EPS2) { return true; }
    final double t3 = p0.subtract(p1).l2norm2();
    if (t1 > t3 || t2 > t3) { return false; }
    final Line line = new Line(p0, p1);
    return (Math.abs(line.eval(x)) < EPS); }

  //--------------------------------------------------------------------
  // Incremental delaunay
  //--------------------------------------------------------------------

  private final LEdge Locate (final ZeroSimplex z) {
    // Returns an edge e, s.t. either x is on e, or e is an edge of
    // a triangle containing x. The search starts from startingEdge
    // and proceeds in the general direction of x. Based on the
    // pseudocode in Guibas and Stolfi (1985) p.121.
    LEdge e = startingEdge();
    while (true) {
      assert ! e.origin().equals(e.dest());
      assert null != embedding(e.dest());
      assert null != embedding(e.dest());
      if (z.equals(e.origin())) { return e; }
      if (z.equals(e.dest())) { return e; }
      else if (RightOf(z, e)) { e = e.Sym(); }
      else if (!RightOf(z, e.Onext())) { e = e.Onext(); }
      else if (!RightOf(z, e.Dprev())) { e = e.Dprev(); }
      else { return e; }
    } }

  //--------------------------------------------------------------------

  public final void insert (final ZeroSimplex z) {

    assert embedding().containsKey(z) :
      "No embedding for " + z + "\n" + embedding();

    // Inserts a new point into a subdivision representing a Delaunay
    // triangulation, and fixes the affected edges so that the result
    // is still a Delaunay triangulation. This is based on the
    // pseudocode from Guibas and Stolfi (1985) p.120, with slight
    // modifications and a bug fix.

    LEdge e = Locate(z);
    // point is already in QMesh
    if (z.equals(e.origin())) { return; }
    if (z.equals(e.dest())) { return; }
    if (OnEdge(z, e)) { e = e.Oprev(); DeleteEdge(e.Onext()); }

    // Connect the new point to the vertices of the containing
    // triangle (or quadrilateral, if the new point fell on an
    // existing edge.)
    LEdge base = MakeEdge();
    base.EndPoints(e.Org(), z);
    Splice(base, e);
    _startingEdge = base;
    do {
      base = Connect(e, base.Sym());
      e = base.Oprev();
    } while (e.Lnext() != _startingEdge);
    // Examine suspect edges to ensure that the Delaunay condition
    // is satisfied.
    do {
      LEdge t = e.Oprev();
      if (RightOf(t.dest(), e)
        && InCircle(e.origin(), t.dest(), e.dest(), z)) {
        Swap(e); e = e.Oprev(); }
      // no more suspect edges
      else if (e.Onext() == _startingEdge) { return; }
      // pop a suspect edge
      else { e = e.Onext().Lprev(); }
    } while (true); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private SubdivisionBuilder (final Map<ZeroSimplex, VectorD2> embedding) {
    _embedding = embedding; }

  public static final SubdivisionBuilder
  unsafe (final Map<ZeroSimplex, VectorD2> embedding) {
    return new SubdivisionBuilder(embedding); }

  public static final SubdivisionBuilder
  make (final Map<ZeroSimplex, VectorD2> embedding,
        final LEdge startingEdge) {
    // defensive copy
    final SubdivisionBuilder builder = unsafe(new TreeMap<>(embedding));
    builder.setStartingEdge(startingEdge);
    return builder; }

  public static final SubdivisionBuilder make (final TriangleMesh mesh) {
    final IFn embed = mesh.embedding();
    // TODO: handle general cell complexes?
    final SimplicialComplex2D cmplx =
      (SimplicialComplex2D) mesh.cmplx();
    final Map<ZeroSimplex, VectorD2> embedding = new HashMap<>();
    for (final ZeroSimplex z : cmplx.vertices()) {
      embedding.put(z,(VectorD2) embed.invoke(z)); }
    final SubdivisionBuilder builder = unsafe(embedding);
    // TODO: handle multiple starting edges
    final Set<Edge> primal = LEdgeTranslator.make(cmplx).build();
    builder.setStartingEdge((LEdge) primal.iterator().next());
    return builder; }

  //--------------------------------------------------------------------
  /** All inserted points must lie in the triangle formed by
   * <code>p0, p1, p2</code>.
   */


  public static final SubdivisionBuilder triangleFrame (final VectorD2 p0,
                                                        final VectorD2 p1,
                                                        final VectorD2 p2) {
    // this creates a closed manifold, a triangle and its 'reverse'
    final ZeroSimplex z0 = ZeroSimplex.make("f");
    final ZeroSimplex z1 = ZeroSimplex.make("f");
    final ZeroSimplex z2 = ZeroSimplex.make("f");
    final Map<ZeroSimplex,VectorD2> embedding =
      Map.of(z0,p0,z1,p1,z2,p2);
    final LEdge ea = MakeEdge();
    ea.EndPoints(z0,z1);
    final LEdge eb = MakeEdge();
    Splice(ea.Sym(), eb);
    eb.EndPoints(z1,z2);
    final LEdge ec = MakeEdge();
    Splice(eb.Sym(), ec);
    ec.EndPoints(z2,z0);
    Splice(ec.Sym(), ea);
    return make(embedding,ea); }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------

