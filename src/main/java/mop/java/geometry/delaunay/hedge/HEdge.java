package mop.java.geometry.delaunay.hedge;

import mop.java.geometry.delaunay.Util;
import mop.java.geometry.euclidean.VectorD2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

/** Delaunay triangulation, after
 * <a href="https://www.researchgate.net/publication/262235495_Incremental_Delaunay_Triangulation">
 *  Dani Lischinski
 *  <i>Incremental Delaunay triangulation</i>
 *  <b>Graphics Gems IV</b> Academic Press 1994 </a>
 * <p>
 * Changes from Graphics Gems version:
 * <<ul>
 *   <li> use half edge rather than quad.</li>
 *   <li>More meaningful names (at least for me).</li>
 *   <li>Delete separate QuadEdge class holding 4 related edges,
 *   like JTS.</li>
 *   <li>Simplify edge algebra.</li>
 * </ul>
 * </p>
 * Note: <br>
 * One of the strengths/weaknesses of the
 * <code>HEdge</code> representation is that there is no explicit
 * representation for the faces;
 * they are just defined implicitly as a ring of
 * <code>HEdge</code>s;
 * and the implicit faces can be any polygon.
 * This allows the same representation to be used for a triangulation
 * and it dual polygonal mesh (ie Delaunay vs Voronoi).
 * But it means that triangulation code must maintain an implicit
 * constraint that the faces are triangular.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-24
 */
public final class HEdge {

  private HEdge _reverse;
  public final HEdge reverse () { return _reverse; }
  public final void setReverse (final HEdge e) { _reverse = e; }

  private VectorD2 _origin;
  public final VectorD2 origin () { return _origin; }
  public final void setOrigin (final VectorD2 p) {
    assert _origin != null;
    _origin = p; }

  /** The next edge ccw around the origin. */
  private HEdge _next;
  /** The next edge ccw around the origin. */
  public final HEdge next () { return _next; }
  /** The next edge ccw around the origin. */
  public final void setNext (final HEdge e) { _next = e; }

  //--------------------------------------------------------------------
  // minimal edge 'algebra'
  //--------------------------------------------------------------------

  /** The next edge cw around the origin. */
  public final HEdge prev () { return reverse().faceNext(); }

  /** The next edge ccw around the (left) face. */
  public final HEdge faceNext () {
    return reverse().next().reverse(); }

  /** The previous edge ccw around the (left) face. */
  public final HEdge facePrev () {
    return next().reverse(); }

  //--------------------------------------------------------------------
  // geometric predicates
  //--------------------------------------------------------------------

  /** Is <code>p</code> on the right side of this directed edge? */
  public final boolean isRight (final VectorD2 p) {
    return Util.ccw(p, reverse().origin(), origin()); }

  /** Is <code>p</code> on the left side of this directed edge? */
  public final boolean isLeft (final VectorD2 p) {
    return Util.ccw(p, origin(), reverse().origin()); }

  //--------------------------------------------------------------------
  // Basic Topological Operators
  //--------------------------------------------------------------------

  private final void swapNext (final HEdge that) {
    final HEdge n0 = this.next();
    final HEdge n1 = that.next();
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

  public final void splice (final HEdge that) {
    swapNext(that); }

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

  public final HEdge connect (final HEdge that) {
    final HEdge e = make(reverse().origin(), that.origin());
    e.splice(faceNext());
    e.reverse().splice(that);
    return e; }

  public final HEdge connect (final HEdge that, final String prefix) {
    final HEdge e = make(prefix, reverse().origin(), that.origin());
    e.splice(faceNext());
    e.reverse().splice(that);
    return e; }

  /** Lischinski: <br>
   * Essentially turns edge e counterclockwise inside its enclosing
   * quadrilateral. The data pointers are modified accordingly.
   */

  public final void swap () {
    // TODO: can we replace with a new edge, rather than modifying?
    final HEdge a = prev();
    final HEdge b = reverse().prev();
    splice(a);
    reverse().splice(b);
    splice(a.faceNext());
    reverse().splice(b.faceNext());
    setOrigin(a.reverse().origin());
    reverse().setOrigin(b.reverse().origin()); }

  //--------------------------------------------------------------------
  // collect mesh elements
  //--------------------------------------------------------------------

  private final boolean noElements (final Set<VectorD2> exclude,
                                    final VectorD2[] points) {
    for (final VectorD2 p : points) {
      if (exclude.contains(p)) { return false; } }
    return true; }

  //--------------------------------------------------------------------
  /** Collect the vertices as a set of points.
   * Skip elements of <code>exclude</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public final Set<VectorD2> vertices (final Set<VectorD2> exclude) {
    final Set<VectorD2> t = new HashSet<>();
    final Set<HEdge> visited = new HashSet<>();
    final Stack<HEdge> toVisit = new Stack<>();
    toVisit.push(this);
    while (! toVisit.empty()) {
      final HEdge e0 = toVisit.pop();
      if (! visited.contains(e0)) {
        visited.add(e0);
        toVisit.push(e0.reverse());
        toVisit.push(e0.faceNext());
        final VectorD2 p = e0.origin();
        if (!exclude.contains(p)) { t.add(p); } } }
    return t; }

  public final Set<VectorD2> vertices () {
    return vertices(new HashSet<>()); }

  /** Collect the edges as a set of pairs of coordinates.
   * Skip edges that have any vertices in <code>exclude</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public final Set<VectorD2[]> edges (final Set<VectorD2> exclude) {
    final Set<VectorD2[]> t = new HashSet<>();
    final Set<HEdge> visited = new HashSet<>();
    final Stack<HEdge> toVisit = new Stack<>();
    toVisit.push(this);
    while (! toVisit.empty()) {
      final HEdge e0 = toVisit.pop();
      if (! visited.contains(e0)) {
        final HEdge e1 = e0.reverse();
        visited.add(e0);
        visited.add(e1);
        toVisit.push(e0.faceNext());
        toVisit.push(e1.faceNext());
        // TODO: sort, preserving orientation, so that same edge
        //  only returned once.
        final VectorD2[] points = new VectorD2[] {
          e0.origin(), e1.origin(), };
        Arrays.sort(points);
        if (noElements(exclude,points)) { t.add(points); } } }
    return t; }

  //--------------------------------------------------------------------
  /** Collect the triangles as a set of triples of coordinates.
   * Skip triangles that have any vertices in <code>exclude</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public final Set<VectorD2[]> triangles (final Set<VectorD2> exclude) {
    final Set<VectorD2[]> t = new HashSet<>();
    final Set<HEdge> visited = new HashSet<>();
    final Stack<HEdge> toVisit = new Stack<>();
    toVisit.push(this);
    while (! toVisit.empty()) {
      final HEdge e0 = toVisit.pop();
      if (! visited.contains(e0)) {
        visited.add(e0);
        final HEdge e1 = e0.faceNext();
        final HEdge e2 = e1.faceNext();
        toVisit.push(e0.reverse());
        toVisit.push(e1.reverse());
        toVisit.push(e2.reverse());
        // TODO: sort, preserving orientation, so that same triangle
        //  only returned once.
        final VectorD2[] points = new VectorD2[] {
          e0.origin(), e1.origin(), e2.origin(), };
        if (noElements(exclude,points)) { t.add(points); } } }
    return t; }

  //--------------------------------------------------------------------
  // Object
  //--------------------------------------------------------------------

  @Override
  public final String toString () {
    return
      name()
        + "[rev:" + reverse().name()
        + ", next:" + next().name()
        + ", fNext:" + faceNext().name()
        + "]"; }

  //--------------------------------------------------------------------
  // debugging
  //--------------------------------------------------------------------

  private final String _name;
  private final String name () { return _name; }

  public static final void printFace (final Set<HEdge> visited,
                                       final Stack<HEdge> toVisit) {
    final HEdge e0 = toVisit.pop();
    if ((null!=e0) && (! visited.contains(e0))) {
      System.out.println("face(" + e0 + ")");
      HEdge e1 = e0;
      do {
        visited.add(e1);
        toVisit.remove(e1);
        final HEdge ri = e1.reverse();
        if (! visited.contains(ri)) { toVisit.push(ri); }
        System.out.println(e1);
        e1 = e1.faceNext();
        assert null != e1;
      } while (e0 != e1); } }

  public final void printMesh () {
    final Set<HEdge> visited = new HashSet<>();
    final Stack<HEdge> toVisit = new Stack<>();
    toVisit.push(this);
    while (!toVisit.empty()) {
      System.out.println(toVisit);
      printFace(visited,toVisit); } }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // Mutable mesh wired together by setting _next

  private HEdge (final String name,
                 final VectorD2 origin) {
    _name = name;
    _origin = origin;
    _next = this; }

  /** Construct a group of 4 circularly linked <code>HEdge</code>s,
   * returning the one from <code>p0</code> to <code>p1</code>,
   * as a representative for the group.
   */

  private static final AtomicInteger _counter = new AtomicInteger(0);
  private static int counter () { return _counter.getAndIncrement(); }

  public static final HEdge make (final String prefix,
                                  final VectorD2 p0,
                                  final VectorD2 p1) {

    final HEdge primal = new HEdge(prefix + ".pri", p0);
    final HEdge reverse = new HEdge(prefix + ".rev", p1);

    primal._reverse = reverse;
    reverse._reverse = primal;

    return primal; }

  public static final HEdge make (final VectorD2 p0,
                                  final VectorD2 p1) {

    return make("q" + counter(),p0,p1); }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
