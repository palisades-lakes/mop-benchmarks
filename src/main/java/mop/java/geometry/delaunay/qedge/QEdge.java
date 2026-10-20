package mop.java.geometry.delaunay.qedge;

import mop.java.geometry.delaunay.Util;
import mop.java.geometry.euclidean.VectorD2;

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
 * @version 2026-10-19
 */
public final class QEdge {

  // TODO: separate primal and dual edges, primal with reference to
  //  origin point, dual reference to explicit origin face?

  private QEdge _dual;
  /** The dual edge, directed from the right face to the left. */
  public final QEdge dual () { return _dual; }
  /** The dual edge, directed from the right face to the left. */
  public final void setDual (final QEdge e) { _dual = e; }

  private VectorD2 _origin;
  public final VectorD2 origin () { return _origin; }
  public final void setOrigin (final VectorD2 p) {
    assert _origin != null;
    _origin = p; }

  /** The next edge ccw around the origin.
   * TODO: origin is null for dual edges?
   */
  private QEdge _next;
  /** The next edge ccw around the origin. */
  public final QEdge next () { return _next; }
  /** The next edge ccw around the origin. */
  public final void setNext (final QEdge e) {
    //assert null != _origin;
    _next = e; }

  //--------------------------------------------------------------------
  // minimal edge 'algebra'
  //--------------------------------------------------------------------

  /** The edge from the destination to the origin. */
  public final QEdge reverse () { return dual().dual(); }

  /** The next edge cw around the origin. */
  public final QEdge prev () { return dual().next().dual(); }

  /** The next edge ccw around the (left) face. */
  public final QEdge faceNext () {
    return dual().reverse().next().dual(); }

  /** The previous edge ccw around the (left) face. */
  public final QEdge facePrev () {
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
    next().dual().swapNext(that.next().dual()); }

  //--------------------------------------------------------------------
  // TODO: is this some version of splice?

  public final QEdge glue (final QEdge that) {
    System.out.println("\nBefore glue");
    System.out.println("this:" + this);
    System.out.println(meshString());
    System.out.println("\nthat:" + that);
    System.out.println(that.meshString());
    final QEdge r0 = this.reverse();
    final QEdge r1 = that.reverse();
    assert r0.origin() == that.origin();
    assert r1.origin() == this.origin();
    final QEdge d0 = this.dual();
    final QEdge d1 = that.dual();
    final QEdge f0 = this.faceNext();
    final QEdge f1 = that.faceNext();
    final QEdge p0 = this.facePrev().reverse();
    final QEdge p1 = that.facePrev().reverse();
    this.setNext(f1);
    that.setNext(f0);
    d0.setNext(f1.dual());
    d1.setNext(f0.dual());
    f0.setNext(p1);
    f1.setNext(p0);
    f0.dual().setNext(p1.dual());
    f1.dual().setNext(p0.dual());
    d0.setDual(that);
    d1.setDual(this);
    System.out.println("\nAfter glue");
    System.out.println("this:" + this);
    System.out.println(meshString());
    System.out.println("\nthat:" + that);
    System.out.println(that.meshString());
    return this; }

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

  public final QEdge connect (final QEdge that, final String prefix) {
    final QEdge e = make(prefix,reverse().origin(), that.origin());
    e.splice(faceNext());
    e.reverse().splice(that);
    return e; }

  /** Lischinski: <br>
   * Essentially turns edge e counterclockwise inside its enclosing
   * quadrilateral. The data pointers are modified accordingly.
   */

  public final void swap () {
    // TODO: can we replace with a new edge, rather than modifying?
    final QEdge a = prev();
    final QEdge b = reverse().prev();
    splice(a);
    reverse().splice(b);
    splice(a.faceNext());
    reverse().splice(b.faceNext());
    setOrigin(a.reverse().origin());
    reverse().setOrigin(b.reverse().origin()); }

  //--------------------------------------------------------------------
  // Object
  //--------------------------------------------------------------------

  @Override
  public final String toString () {
    return
      name()
        + "[d:" + dual().name()
        + ", n:" + next().name()
        + ", f:" + faceNext().name()
        + "]"; }

  //--------------------------------------------------------------------
  // debugging
  //--------------------------------------------------------------------

  private final String _name;
  private final String name () { return _name; }

  public static final void appendFace (final Set<QEdge> visited,
                                       final Stack<QEdge> toVisit,
                                       final StringBuilder sb) {
    QEdge e = toVisit.pop();
    if ((null!=e) && (! visited.contains(e))) {
    sb.append("face(" + e + ")\n");
      QEdge e0;
      QEdge e1 = e;
    do {
      visited.add(e1);
      toVisit.remove(e1);
        final QEdge ri = e1.reverse();
        if (! visited.contains(ri)) { toVisit.push(ri); }
      sb.append("  "); sb.append(e1); sb.append("\n");
        e0 = e1;;
      e1 = e1.faceNext();
      } while (! ((null==e1) || (e0==e1)|| (e==e1))); } }

  public final String meshString () {
    final Set<QEdge> visited = new HashSet<>();
    final Stack<QEdge> toVisit = new Stack<>();
    final StringBuilder sb = new StringBuilder("mesh:\n\n");
    toVisit.push(this);
    while (!toVisit.empty()) { appendFace(visited,toVisit,sb); }
    return sb.toString(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // Mutable mesh wired together by setting _next

  private QEdge (final String name,
                 final VectorD2 origin) {
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
                                  final VectorD2 p0,
                                  final VectorD2 p1) {

    final QEdge primal = new QEdge(prefix + ".p", p0);
    final QEdge reverse = new QEdge(prefix + ".r", p1);
    final QEdge dual = new QEdge(prefix + ".d",null);
    final QEdge reverseDual = new QEdge(prefix + ".s",null);

    primal._dual = dual;
    reverse._dual = reverseDual;
    dual._dual = reverse;
    reverseDual._dual = primal;

    dual.setNext(reverseDual);
    reverseDual.setNext(dual);

    return primal; }

  public static final QEdge make (final VectorD2 p0,
                                  final VectorD2 p1) {

    return make("q" + counter(),p0,p1); }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
