package mop.java.geometry.delaunay.qedge;

import mop.java.cmplx.ZeroSimplex;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/** Delaunay triangulation, after
 * <a href="https://www.researchgate.net/publication/262235495_Incremental_Delaunay_Triangulation">
 *  Dani Lischinski
 *  <i>Incremental Delaunay triangulation</i>
 *  <b>Graphics Gems IV</b> Academic Press 1994 </a>
 * <p>
 * Changes from Graphics Gems version:
 * <<ul>
 *   <li>More meaningful names (at least for me).</li>
 *   <li>Delete separate QuadEdge class holding 4 related edges,
 *   like JTS.</li>
 *   <li>Simplify edge algebra.</li>
 * </ul>
 * </p>
 * Note: <br>
 * One of the strengths/weaknesses of the
 * <code>QEdge</code> representation is that there is no explicit
 * representation for the faces;
 * they are just defined implicitly as a ring of
 * <code>QEdge</code>s;
 * and the implicit faces can be any polygon.
 * This allows the same representation to be used for a triangulation
 * and it dual polygonal mesh (ie Delaunay vs Voronoi).
 * But it means that triangulation code must maintain an implicit
 * constraint that the faces are triangular.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-24
 */
public final class QEdge {

  // TODO: separate primal and dual edges, primal with reference to
  //  origin point, dual reference to explicit origin face?
  //  G+S's edge algebra has separate finite sets E and E* for the
  //  primal and dual edges.
  // TODO: this is actually G+S Rot operation, not dual,
  //  which is why dual().dual() == reverse(), not identity.

  private QEdge _rot;
  /** The dual edge, directed from the right face to the left. */
  public final QEdge rot () { return _rot; }
//  /** The dual edge, directed from the right face to the left. */
//  public final void setRot (final QEdge e) { _rot = e; }

  private ZeroSimplex _origin;
  public final ZeroSimplex origin () { return _origin; }
  public final void setOrigin (final ZeroSimplex p) {
    assert _origin != null;
    _origin = p; }

  /** The next edge ccw around the origin. */
  private QEdge _next;
  /** The next edge ccw around the origin. */
  public final QEdge next () { return _next; }
  /** The next edge ccw around the origin. */
  public final void setNext (final QEdge e) { _next = e; }

  //--------------------------------------------------------------------
  // minimal edge 'algebra'
  //--------------------------------------------------------------------

  /** The edge from the destination to the origin. */
  public final QEdge reverse () { return rot().rot(); }

  /** The next edge cw around the origin. */
  public final QEdge prev () { return rot().next().rot(); }

  /** The next edge ccw around the (left) face. */
  public final QEdge faceNext () {
    return rot().reverse().next().rot(); }

//  /** The previous edge ccw around the (left) face. */
//  public final QEdge facePrev () {
//    return next().reverse(); }

  //--------------------------------------------------------------------
  // Basic Topological Operators
  //--------------------------------------------------------------------

  private final void swapNext (final QEdge that) {
    final QEdge n0 = this.next();
    final QEdge n1 = that.next();
    this.setNext(n1);
    that.setNext(n0); }

  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * This operator affects the two edge rings around the origins
   * of a and b, and, independently, the two edge rings around
   * the left faces of a and b. In each case,
   * <ol>
   * <li> if the two rings are distinct, Splice will combine
   * them into one;
   * </li>
   * <li> if the two are the same ring, Splice will break it
   * into two separate pieces.
   * </li>
   * </ol>
   * Thus, splice can be used both to attach the two edges together,
   * and to break them apart. <br>
   * See Guibas and Stolfi (1985) p.96 for more details
   * and illustrations.
   */

  public final void splice (final QEdge that) {
    swapNext(that);
    next().rot().swapNext(that.next().rot()); }

  //--------------------------------------------------------------------

  public final void delete () {
    splice(prev());
    reverse().splice(reverse().prev()); }

  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * Add a new edge e connecting
   * the destination of <code>this</code>
   * to the origin of <code>that</code></code>,
   * in such a way that all three have the same
   * left face after the connection is complete.
   * Additionally, the data pointers of the new edge are set.
   */

  public final QEdge connect (final QEdge that) {
    final QEdge e = make(reverse().origin(), that.origin());
    e.splice(faceNext());
    e.reverse().splice(that);
    return e; }

//  public final QEdge connect (final QEdge that, final String prefix) {
//    final QEdge e = make(prefix,reverse().origin(), that.origin());
//    e.splice(faceNext());
//    e.reverse().splice(that);
//    return e; }

  /** Lischinski: <br>
   * Essentially turns edge e counterclockwise inside its enclosing
   * quadrilateral. The data pointers are modified accordingly.
   */

  public final void swap () {
    // TODO: can we replace with a new edge, rather than modifying?
    // TODO: what happens if it's not in a quadrilateral? -> unit test!
    final QEdge a = prev();
    final QEdge b = reverse().prev();
    splice(a);
    reverse().splice(b);
    splice(a.faceNext());
    reverse().splice(b.faceNext());
    setOrigin(a.reverse().origin());
    reverse().setOrigin(b.reverse().origin()); }

  //--------------------------------------------------------------------
  // collect mesh elements
  //--------------------------------------------------------------------

  private final boolean noElements (final Set<ZeroSimplex> exclude,
                                    final ZeroSimplex[] points) {
    for (final ZeroSimplex p : points) {
      if (exclude.contains(p)) { return false; } }
    return true; }

  //--------------------------------------------------------------------
  /** Collect the vertices as a set of points.
   * Skip elements of <code>exclude</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public final SortedSet<ZeroSimplex> vertices (final Set<ZeroSimplex> exclude) {
    final SortedSet<ZeroSimplex> t = new TreeSet<>();
    final Set<QEdge> visited = new HashSet<>();
    final Stack<QEdge> toVisit = new Stack<>();
    toVisit.push(this);
    while (! toVisit.empty()) {
      final QEdge e0 = toVisit.pop();
      if (! visited.contains(e0)) {
        visited.add(e0);
        toVisit.push(e0.reverse());
        toVisit.push(e0.faceNext());
        final ZeroSimplex p = e0.origin();
        if (!exclude.contains(p)) { t.add(p); } } }
    return t; }

  public final SortedSet<ZeroSimplex> vertices () {
    return vertices(new TreeSet<>()); }

  /** Collect the edges as a set of pairs of coordinates.
   * Skip edges that have any vertices in <code>exclude</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public final Set<ZeroSimplex[]> edges (final Set<ZeroSimplex> exclude) {
    final Set<ZeroSimplex[]> t = new HashSet<>();
    final Set<QEdge> visited = new HashSet<>();
    final Stack<QEdge> toVisit = new Stack<>();
    toVisit.push(this);
    while (! toVisit.empty()) {
      final QEdge e0 = toVisit.pop();
      if (! visited.contains(e0)) {
        final QEdge e1 = e0.reverse();
        visited.add(e0);
        visited.add(e1);
        toVisit.push(e0.faceNext());
        toVisit.push(e1.faceNext());
        // TODO: sort, preserving orientation, so that same edge
        //  only returned once.
        final ZeroSimplex[] points = new ZeroSimplex[] {
          e0.origin(), e1.origin(), };
        Arrays.sort(points);
        if (noElements(exclude,points)) { t.add(points); } } }
    return t; }

  //--------------------------------------------------------------------
  /** Collect the triangles as a set of triples of coordinates.
   * Skip triangles that have any vertices in <code>exclude</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public final Set<ZeroSimplex[]> triangles (final Set<ZeroSimplex> exclude) {
    final Set<ZeroSimplex[]> t = new HashSet<>();
    final Set<QEdge> visited = new HashSet<>();
    final Stack<QEdge> toVisit = new Stack<>();
    toVisit.push(this);
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
        final ZeroSimplex[] vtxs = new ZeroSimplex[] {
          e0.origin(), e1.origin(), e2.origin(), };
        if (noElements(exclude,vtxs)) { t.add(vtxs); } } }
    return t; }

  //--------------------------------------------------------------------
  // Object
  //--------------------------------------------------------------------

  @Override
  public final String toString () {
    return
      name()
        + "[rot:" + rot().name() + "->" + rot().next().name()
        + ", next:" + next().name()
        + ", fNext:" + faceNext().name()
        + "]"; }

  //--------------------------------------------------------------------
  // debugging
  //--------------------------------------------------------------------

  private final String _name;
  private final String name () { return _name; }

  public static final void printFace (final Set<QEdge> visited,
                                      final Stack<QEdge> toVisit) {
    final QEdge e0 = toVisit.pop();
    if ((null!=e0) && (! visited.contains(e0))) {
      System.out.println("face(" + e0 + ")");
      QEdge e1 = e0;
      do {
        visited.add(e1);
        toVisit.remove(e1);
        final QEdge ri = e1.reverse();
        if (! visited.contains(ri)) { toVisit.push(ri); }
        System.out.println(e1);
        e1 = e1.faceNext();
        assert null != e1;
      } while (e0 != e1); } }

  public final void printMesh () {
    final Set<QEdge> visited = new HashSet<>();
    final Stack<QEdge> toVisit = new Stack<>();
    toVisit.push(this);
    while (!toVisit.empty()) { printFace(visited,toVisit); } }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // Mutable mesh wired together by setting _next

  private QEdge (final String name,
                 final ZeroSimplex origin) {
    _name = name;
    _origin = origin;
    _next = this; }

  /** Construct a group of 4 circularly linked <code>QEdge</code>s,
   * returning the one from <code>p0</code> to <code>p1</code>,
   * as a representative for the group.
   */

  private static final AtomicInteger _counter = new AtomicInteger(0);
  private static int counter () { return _counter.getAndIncrement(); }

  public static final QEdge make (final String prefix,
                                  final ZeroSimplex p0,
                                  final ZeroSimplex p1) {

    final QEdge primal = new QEdge(prefix + ".pri", p0);
    final QEdge reverse = new QEdge(prefix + ".rev", p1);
    final QEdge rot = new QEdge(prefix + ".rot",null);
    final QEdge reverseRot = new QEdge(prefix + ".rer",null);

    primal._rot = rot;
    reverse._rot = reverseRot;
    rot._rot = reverse;
    reverseRot._rot = primal;

    rot.setNext(reverseRot);
    reverseRot.setNext(rot);

    return primal; }

  public static final QEdge make (final ZeroSimplex p0,
                                  final ZeroSimplex p1) {

    return make("q" + counter(),p0,p1); }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
