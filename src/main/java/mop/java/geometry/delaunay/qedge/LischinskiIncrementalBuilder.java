package mop.java.geometry.delaunay.qedge;

import clojure.lang.IFn;
import mop.java.cmplx.SimplicialComplex2D;
import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.ZeroSimplex;
import mop.java.geom.mesh.TriangleMesh;
import mop.java.geometry.delaunay.DelaunayBuilder;
import mop.java.geometry.delaunay.Util;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.util.IFnWrapper;

import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

/** Algorithm for Delaunay triangulation, after
 * <a href="https://www.researchgate.net/publication/262235495_Incremental_Delaunay_Triangulation">
 *  Dani Lischinski
 *  <i>Incremental Delaunay triangulation</i>
 *  <b>Graphics Gems IV</b> Academic Press 1994 </a>
 * <p>
 * <b>NOTE: </b> all inserted points must fall within the existing mesh!
 * </p>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-24
 */

public final class LischinskiIncrementalBuilder
  implements DelaunayBuilder {

  //--------------------------------------------------------------------

  private final SortedMap<ZeroSimplex, VectorD2> _embedding;
  @Override
  public final SortedMap<ZeroSimplex, VectorD2> embedding () {
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

  private QEdge _startingEdge;
  /** The entry into the abstract cell complex represented
   * by the <code>QEdege</code>s.
   */
  public final QEdge startingEdge () { return _startingEdge; }
  /** The entry into the abstract cell complex represented
   * by the <code>QEdege</code>s.
   */
  final void setStartingEdge (final QEdge e) { _startingEdge = e; }

  //--------------------------------------------------------------------

  @Override
  public final SortedSet<ZeroSimplex> vertices () {
    return startingEdge().vertices(); }

  @Override
  public final SortedSet<TwoSimplex> faces () {
    return startingEdge().faces(); }

  //--------------------------------------------------------------------

  public final void addTriangle (final ZeroSimplex z0,
                                 final ZeroSimplex z1,
                                 final ZeroSimplex z2) {
    throw new UnsupportedOperationException(); }

  public final void deleteTriangle (final ZeroSimplex z0,
                                    final ZeroSimplex z1,
                                    final ZeroSimplex z2) {
    throw new UnsupportedOperationException(); }

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
  // geometric predicates
  //--------------------------------------------------------------------

  /** Is <code>p</code> on the right side of this directed edge? */
  public final boolean right (final QEdge q,
                              final ZeroSimplex z) {
    return Util.ccw(embedding(z),
                    embedding(q.reverse().origin()),
                    embedding(q.origin())); }

  /** Is <code>p</code> on the left side of this directed edge? */
  public final boolean left (final QEdge q,
                             final ZeroSimplex z) {
    return Util.ccw(embedding(z),
                    embedding(q.origin()),
                    embedding(q.reverse().origin())); }

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

  private final boolean onEdge (final ZeroSimplex z,
                                final QEdge e) {
    final VectorD2 x = embedding(z);
    final VectorD2 p0 = embedding(e.origin());
    final VectorD2 p1 = embedding(e.reverse().origin());
    final double t1 = x.subtract(p0).l2norm2();
    final double t2 = x.subtract(p1).l2norm2();
    if (t1 < EPS2 || t2 < EPS2) { return true; }
    final double t3 = p0.subtract(p1).l2norm2();
    if (t1 > t3 || t2 > t3) { return false; }
    final Line line = new Line(p0,p1);
    return (Math.abs(line.eval(x)) < EPS); }

  //--------------------------------------------------------------------
  // Incremental delaunay
  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * Returns an edge e, s.t. either x is on e, or e is an edge of
   * a triangle containing x. The search starts from startingEdge
   * and proceeds in the general direction of x. Based on the
   * pseudocode in Guibas and Stolfi (1985) p.121.
   * <br>
   * TODO: fix infinite loops!
   * <br>
   * See <a href="https://people.eecs.berkeley.edu/~jrs/meshpapers/GSflaws">
   * Shewchuk's GSFlaws:</a>
   * <pre>
   * Three fixes to Guibas and Stolfi's "Primitives for the Manipulation of
   * General Subdivisions and the Computation of Voronoi Diagrams":
   *
   * One, their Locate primitive always works in the interior of the mesh,
   * but can get stuck in an infinite loop on the convex hull.  Consider
   * this mesh:
   *                                y       z
   *                                o-------o
   *                               / \     / \
   *                              /   \   /   \
   *                             /     \ /     \
   *                            o--X----o-------o
   *                            u       v       w
   *
   * Suppose you're trying to locate the X, which is collinear with uvw.
   * Look at p. 121 of Guibas & Stolfi.  You are searching with an edge
   * e for the edge uv.  Suppose that e := wv (directed toward v).
   * RightOf[X, e] is false.  NOT RightOf[X, e.Onext] is true, so we
   * execute "e <- e.Onext", which sets e to wz.  On the next iteration,
   * we execute "e <- e.Onext", which sets e back to wv.  This continues
   * forever, an infinite loop.
   *
   * To fix this bug, changing both "Not RightOf" expressions to "LeftOf"
   * expressions is a good start, but doesn't fulfill the criterion that
   * "Locate returns an edge e of the current Delaunay diagram such that
   * the given point X is either on e or _strictly_ inside the left face
   * of e."  Rather, X might be on any edge of the left face; X might
   * even be the apex vertex.  An explicit test for these possibilities
   * is necessary.
   *
   * Two, I think there are other places in the paper where Guibas &
   * Stolfi sometime use RightOf when they mean NOT LeftOf, and vice
   * versa.  Give careful consideration in each case to what should
   * happen when the determinant is zero.
   *
   * Three, on p. 120, in their InsertSite procedure, the fifth-last line
   * is wrong.  The statement "e <- t" should read "e <- e.Oprev".
   *
   * If you're not using exact arithmetic for the CCW and InCircle tests,
   * other things can go wrong as well.  Some problem cases can be
   * avoided by changing the code a bit, and some can't.
   * </pre>
   */

  private final QEdge locateQ (final ZeroSimplex z) {
    QEdge e = startingEdge();
    while (true) {
      if (z.equals(e.origin())) { return e; }
      if (z.equals(e.reverse().origin())) { return e; }
      if (right(e,z)) { e = e.reverse(); }
      else if (! right(e.next(),z)) { e = e.next(); }
      else if (! left(e.faceNext(),z)) {
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

  @Override
  public final void insert (final ZeroSimplex z) {
    assert embedding().containsKey(z) :
      "No embedding for " + z + "\n" + embedding();

    QEdge e = locateQ(z);
    // point is already in QMesh
    if ((z.equals(e.origin())) || (z.equals(e.reverse().origin())))  {
      return; }
    else if (onEdge(z, e)) { e = e.prev(); e.next().delete(); }
    // Connect the new point to the vertices of the containing
    // triangle (or quadrilateral, if the new point fell on an
    // existing edge.)
    QEdge base = QEdge.make(e.origin(), z);
    base.splice(e);
    setStartingEdge(base);
    do {
      base = e.connect(base.reverse());
      e = base.prev();
    } while (e.faceNext() != startingEdge());
    // Examine suspect edges to ensure that the Delaunay condition
    // is satisfied.
    do {
      QEdge t = e.prev();
      if (right(e,t.reverse().origin()) &&
        Util.inCircle(e.origin(),
                      t.reverse().origin(),
                      e.reverse().origin(),
                      z,
                      embedding())) {
        e.swap();
        e = e.prev(); }
      // no more suspect edges
      else if (e.next() == startingEdge()) { return; }
      // pop a suspect edge
      else { e = e.next().next().reverse(); }
    } while (true); }

  //--------------------------------------------------------------------
  // emit mesh
  //--------------------------------------------------------------------
  /** Emit the triangle mesh corresponding to the current state of the
   * builder.
   * <br>
   * TODO: cache last value and stateChanged to avoid rebuilding
   *  rebuilding the same mesh? Not really a likely scenario.
   */

  public final TriangleMesh build () {
    final SimplicialComplex2D cmplx = SimplicialComplex2D.make(faces());
    final IFn z2p = IFnWrapper.make(embedding());
    return TriangleMesh.make(cmplx, z2p); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  /** disabled constructor, class methods only. */

  private LischinskiIncrementalBuilder (
    final SortedMap<ZeroSimplex, VectorD2> embedding) {
    _embedding = embedding; }

  public static final LischinskiIncrementalBuilder
  unsafe (final SortedMap<ZeroSimplex, VectorD2> embedding) {
    return new LischinskiIncrementalBuilder(embedding); }

// --Commented out by Inspection START (10/25/2026 14:09):
//  public static final LischinskiIncrementalBuilder make () {
//    return unsafe(new TreeMap<>()); }
// --Commented out by Inspection STOP (10/25/2026 14:09)

// --Commented out by Inspection START (10/25/2026 14:09):
//  public static final LischinskiIncrementalBuilder
//  make (final Map<ZeroSimplex, VectorD2> embedding) {
//    // defensive copy
//    return unsafe(new TreeMap<>(embedding)); }
// --Commented out by Inspection STOP (10/25/2026 14:09)

  public static final LischinskiIncrementalBuilder
  make (final Map<ZeroSimplex, VectorD2> embedding,
        final QEdge startingEdge) {
    // defensive copy
    final LischinskiIncrementalBuilder builder =
      unsafe(new TreeMap<>(embedding));
    builder.setStartingEdge(startingEdge);
    return builder; }

//  /** Use <code>mesh</code> as the initial frame for the builder. */
//
//  public static final DelaunayBuilder make (final TriangleMesh mesh) {
//    final SimplicialComplex2D c = (SimplicialComplex2D) mesh.cmplx();
//    final SortedMap<ZeroSimplex, VectorD2> embedding = new TreeMap<>();
//    final IFn e = mesh.embedding();
//    for (final ZeroSimplex z : c.vertices()) {
//      embedding.put(z,(VectorD2) e.invoke(z)); }
//    final LischinskiIncrementalBuilder b = unsafe(embedding);
//    for (final TwoSimplex f : c.faces()) { b.addTriangle(f); }
//    return b; }
//
  //--------------------------------------------------------------------
  /** All inserted points must lie in the triangle formed by
   * <code>p0, p1, p2</code>.
   */

  public static final LischinskiIncrementalBuilder
  triangleFrame (final VectorD2 p0,
                 final VectorD2 p1,
                 final VectorD2 p2) {
    // this creates a closed manifold, a triangle and its 'reverse'
    final ZeroSimplex z0 = ZeroSimplex.make("z0");
    final ZeroSimplex z1 = ZeroSimplex.make("z1");
    final ZeroSimplex z2 = ZeroSimplex.make("z2");
    final Map<ZeroSimplex,VectorD2> embedding =
      Map.of(z0,p0,z1,p1,z2,p2);

    final QEdge e01 = QEdge.make("e01", z0, z1);
    final QEdge e12 = QEdge.make("e12", z1, z2);
    final QEdge e20 = QEdge.make("e20", z2, z0);
    e01.splice(e20.reverse());
    e12.splice(e01.reverse());
    e20.splice(e12.reverse());
    return make(embedding,e01); }

  //--------------------------------------------------------------------

  public static final LischinskiIncrementalBuilder
  rectangleFrame (final double xmin,
                  final double xmax,
                  final double ymin,
                  final double ymax) {
    final ZeroSimplex z0 = ZeroSimplex.make("z0");
    final ZeroSimplex z1 = ZeroSimplex.make("z1");
    final ZeroSimplex z2 = ZeroSimplex.make("z2");
    final ZeroSimplex z3 = ZeroSimplex.make("z3");
    final VectorD2 p0 = new VectorD2(xmin,ymin);
    final VectorD2 p1 = new VectorD2(xmax,ymin);
    final VectorD2 p2 = new VectorD2(xmax,ymax);
    final VectorD2 p3 = new VectorD2(xmin,ymax);
    final Map<ZeroSimplex,VectorD2> embedding =
      Map.of(z0,p0,z1,p1,z2,p2,z3,p3);

    // face012
    final QEdge e01 = QEdge.make("e01",z0, z1);
    final QEdge e12 = QEdge.make("e12",z1, z2);
    final QEdge e20 = QEdge.make("e20",z2, z0);
    e01.splice(e20.reverse());
    e12.splice(e01.reverse());
    e20.splice(e12.reverse());
    // face023
    final QEdge e02 = e20.reverse();
    final QEdge e23 = QEdge.make("e23",z2, z3);
    final QEdge e30 = QEdge.make("e30",z3, z0);
    e02.splice(e30.reverse());
    e23.splice(e02.reverse());
    e30.splice(e23.reverse());

    return make(embedding,e01); }

  /** Use <code>mesh</code> as the initial frame for the builder.
   * TODO: check that it's delaunay.
   */
//  public static final DelaunayBuilder make (final TriangleMesh mesh) {
//    final DelaunayBuilder b = new CDSBWDelaunayBuilder();
//    final SimplicialComplex2D c = (SimplicialComplex2D) mesh.cmplx();
//    final IFn e = mesh.embedding();
//    for (final ZeroSimplex z : c.vertices()) {
//      b.embed(z,(VectorD2) e.invoke(z)); }
//    b.vertices().addAll(c.vertices());
//    b.faces().addAll(c.faces());
//
//    return b; }

//--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------

