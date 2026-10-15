package mop.java.geometry.delaunay.aqedge;

import clojure.lang.IFn;
import mop.java.cmplx.OneSimplex;
import mop.java.cmplx.SimplicialComplex2D;
import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.ZeroSimplex;
import mop.java.geom.mesh.TriangleMesh;
import mop.java.geometry.delaunay.Util;
import mop.java.geometry.delaunay.cdsbw.DelaunayBuilder;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.util.IFnWrapper;

import java.util.*;

/** Algorithm for Delaunay triangulation, after
 * <a href="https://www.researchgate.net/publication/262235495_Incremental_Delaunay_Triangulation">
 *  Dani Lischinski
 *  <i>Incremental Delaunay triangulation</i>
 *  <b>Graphics Gems IV</b> Academic Press 1994 </a>
 * <p>
 * <b>NOTE: </b> all inserted points must fall within the existing mesh!
 * </p>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-15
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
  final QEdge startingEdge () { return _startingEdge; }
  /** The entry into the abstract cell complex represented
   * by the <code>QEdege</code>s.
   */
  final void setStartingEdge (final QEdge e) { _startingEdge = e; }

  //--------------------------------------------------------------------

  @Override
  public final SortedSet<ZeroSimplex> vertices () {
    final SortedSet<ZeroSimplex> vertices = new TreeSet<>();
    final Set<QEdge> visited = new HashSet<>();
    final Stack<QEdge> toVisit = new Stack<>();
    toVisit.push(startingEdge());
    while (! toVisit.empty()) {
      final QEdge e0 = toVisit.pop();
      if (! visited.contains(e0)) {
        visited.add(e0);
        toVisit.push(e0.reverse());
        toVisit.push(e0.faceNext());
        vertices.add(e0.origin()); } }
    return vertices; }

  //--------------------------------------------------------------------

  @Override
  public final SortedSet<TwoSimplex> faces () {
    final SortedSet<TwoSimplex> faces = new TreeSet<>();
    final Set<QEdge> visited = new HashSet<>();
    final Stack<QEdge> toVisit = new Stack<>();
    toVisit.push(startingEdge());
    while (! toVisit.empty()) {
      final QEdge e0 = toVisit.pop();
      if (! visited.contains(e0)) {
        visited.add(e0);
        final QEdge e1 = e0.faceNext();
        final QEdge e2 = e1.faceNext();
        toVisit.push(e0.reverse());
        toVisit.push(e1.reverse());
        toVisit.push(e2.reverse());
        // TODO: sort, preserving orientation, so that same triangle
        //  only returned once.
        final TwoSimplex face = TwoSimplex.make(
          e0.origin(), e1.origin(), e2.origin());
        faces.add(face); } }
    return faces; }

  //--------------------------------------------------------------------

  private final Map<OneSimplex,QEdge> cache = new HashMap<>();

  public final void addTriangle (final ZeroSimplex z0,
                                 final ZeroSimplex z1,
                                 final ZeroSimplex z2) {
    final OneSimplex s10 = OneSimplex.make(z1,z0);
    final QEdge e01;
    if (cache.containsKey(s10)) {
      e01 = cache.get(s10).reverse(); }
    else {
      e01 = QEdge.make(z0,z1);
      setStartingEdge(e01);
      cache.put(OneSimplex.make(z0,z1),e01); }

    final OneSimplex s21 = OneSimplex.make(z2,z1);
    final QEdge e12;
    if (cache.containsKey(s21)) {
      e12 = cache.get(s21).reverse(); }
    else {
      e12 = QEdge.make(z1,z2);
      setStartingEdge(e12);
      cache.put(OneSimplex.make(z1,z2),e12); }

    final OneSimplex s02 = OneSimplex.make(z0,z2);
    final QEdge e20;
    if (cache.containsKey(s02)) {
      e20 = cache.get(s02).reverse(); }
    else {
      e20 = QEdge.make(z2,z0);
      setStartingEdge(e20);
      cache.put(OneSimplex.make(z2,z0),e20); }

    // TODO: what if quad mesh already has face?
    e01.splice(e20.reverse());
    e12.splice(e01.reverse());
    e20.splice(e12.reverse()); }

  public final void addTriangle (final TwoSimplex f) {
    addTriangle(f.z0(),f.z1(),f.z2()); }

  public final void deleteTriangle (final ZeroSimplex z0,
                                    final ZeroSimplex z1,
                                    final ZeroSimplex z2) {
    throw new UnsupportedOperationException(); }

  // TODO: how to make a hole in a QEdge mesh?
  public final void deleteTriangle (final TwoSimplex t) {
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
                    embedding(q.origin())); }

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
    return TriangleMesh.make(cmplx,z2p); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  /** disabled constructor, class methods only. */

  private LischinskiIncrementalBuilder (
    final SortedMap<ZeroSimplex, VectorD2> embedding) {
    _embedding = embedding;
  }

  public static final LischinskiIncrementalBuilder
  unsafe (final SortedMap<ZeroSimplex, VectorD2> embedding) {
    return new LischinskiIncrementalBuilder(embedding); }

  public static final DelaunayBuilder make () {
    return unsafe(new TreeMap<>()); }

  public static final DelaunayBuilder
  make (final SortedMap<ZeroSimplex, VectorD2> embedding) {
    // defensive copy
    return unsafe(new TreeMap<>(embedding)); }

  /** Use <code>mesh</code> as the initial frame for the builder. */

  public static final DelaunayBuilder make (final TriangleMesh mesh) {
    final SimplicialComplex2D c = (SimplicialComplex2D) mesh.cmplx();
    final SortedMap<ZeroSimplex, VectorD2> embedding = new TreeMap<>();
    final IFn e = mesh.embedding();
    for (final ZeroSimplex z : c.vertices()) {
      embedding.put(z,(VectorD2) e.invoke(z)); }
    final LischinskiIncrementalBuilder b = unsafe(embedding);
    for (final TwoSimplex f : c.faces()) { b.addTriangle(f); }
    return b; }


//--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------

