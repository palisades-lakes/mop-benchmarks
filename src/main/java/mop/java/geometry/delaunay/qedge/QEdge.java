package mop.java.geometry.delaunay.qedge;

import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.ZeroSimplex;

import java.util.*;

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
 * <p>
 * <em>NOTE:</em> Not suitable to be used as keys in <code>Map</code>s.
 * Identity <code>hashCode</code> and <code>equals</code>,
 * due to circular <code>rot()</code> and <code>next()</code>
 * references, make it difficult to determine if 2 instances
 * are topologically equivalent.
 * </p>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-11-12
 */

public final class QEdge implements Comparable<QEdge> {

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
  /** A basic weakness of the <code>QEdge</code> representation:
    * <code>origin()</code>> is a mutable field,
   * with a constraint relative the value of
   * <code>next()</code>, which is also mutable.
   * Also, <code>origin()</code> should always be null for
   * dual (<code>rot()</code>) edges.
    */
  public final void setOrigin (final ZeroSimplex p) {
    // dual/rot edges have null origins, which shouldn't  changed
    assert origin() != null;
    assert Objects.equals(origin(),next().origin());
    assert Objects.equals(p,next().origin());
    _origin = p; }

  /** The next edge ccw around the origin. */
  private QEdge _next;
  /** The next edge ccw around the origin. */
  public final QEdge next () { return _next; }

  /** The next edge ccw around the origin.
   * A basic weakness of the <code>QEdge</code> representation:
   * <code>next()</code>> is a mutable field,
   * with a constraint relative the value of
   * <code>origin()</code>, which is also mutable.
   */
  public final void setNext (final QEdge e) {
    assert Objects.equals(origin(),e.origin());
    _next = e; }

  //--------------------------------------------------------------------
  // minimal edge 'algebra'
  //--------------------------------------------------------------------

  /** The edge from the destination to the origin. */
  public final QEdge reverse () { return rot().rot(); }

  public final ZeroSimplex dest () { return reverse().origin(); }

  /** The next edge cw around the origin. */
  public final QEdge prev () { return rot().next().rot(); }

  /** The next edge ccw around the (left) face. */
  public final QEdge faceNext () {
    return rot().reverse().next().rot(); }

  /** The next edge cw around the (left) face. */
  public final QEdge facePrev () { return next().reverse(); }

  //--------------------------------------------------------------------
  // Basic Topological Operators
  //--------------------------------------------------------------------
  /** Break all the links from <code>this</code> quartet to any
   * other <code>QEdge</code>.
   * Unlike <code>delete</code>, <code>isolate</code> does nothing
   * to the neighbors.
   * To keep the mesh valid, no other <code>QEdge</code>s
   * can have references to
   * <code>this</code>.
   */

  private final void isolate () {
    final QEdge pr = this;
    final QEdge re = reverse();
    final QEdge ro = rot();
    final QEdge rr = rot().reverse();
    pr.setNext(pr);
    re.setNext(re);
    ro.setNext(ro);
    rr.setNext(rr); }

  //--------------------------------------------------------------------
  /** <em>NOTE:</em> <code>this and <code>that</code> must have the same
   * <code>origin()</code>, which may be <code>null</code> if
   * <code>this<</code> and <code>that</code> are dual (rot)
   * <code>QEdge</code>s.
   */

  private final void swapNext (final QEdge that) {
    assert Objects.equals(origin(),that.origin());
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
   * <br>
   * NOTE: <code>this</code> and <code>that</code> must have the same
   * <code>origin</code>, and the same for <code>this.rot()</code>
   * and <code>that.rot()</code>.
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
    final QEdge e = make(this.dest(), that.origin());
    e.splice(faceNext());
    e.reverse().splice(that);
    return e; }

  /** Lischinski: <br>
   * Essentially turns edge e counterclockwise inside its enclosing
   * quadrilateral.
   * <br>
   * <em>Rewritten to return a new QEdge, to maintain that constraint
   * between <code>next()</code> and <code>origin()</code></em>.
   */

  public final QEdge swap () {

    final ZeroSimplex z0 = reverse().faceNext().dest();
    final ZeroSimplex z1 = faceNext().dest();
    final QEdge e = make(z0,z1);

    final QEdge a = prev();
    final QEdge b = reverse().prev();
    splice(a);
    reverse().splice(b);

    e.splice(a.faceNext());
    e.reverse().splice(b.faceNext());

    isolate();

    return e; }

  //--------------------------------------------------------------------
  // collect mesh elements
  //--------------------------------------------------------------------

// --Commented out by Inspection START (10/25/2026 14:09):
//  private final boolean noElements (final Set<ZeroSimplex> exclude,
//                                    final ZeroSimplex[] points) {
//    for (final ZeroSimplex p : points) {
//      if (exclude.contains(p)) { return false; } }
//    return true; }
// --Commented out by Inspection STOP (10/25/2026 14:09)

  //--------------------------------------------------------------------
  /** Collect the vertices as a set of points.
   * Skip elements of <code>exclude</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public final SortedSet<ZeroSimplex>
  zeroSimplexes (final Set<ZeroSimplex> exclude) {
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

  public final SortedSet<ZeroSimplex> zeroSimplexes () {
    return zeroSimplexes(new TreeSet<>()); }

  /** Collect connected triangular faces, skip others. */
  public final SortedSet<TwoSimplex> twoSimplexes () {
    final SortedSet<TwoSimplex> faces = new TreeSet<>();
    final Set<QEdge> visited = new HashSet<>();
    final Stack<QEdge> toVisit = new Stack<>();
    toVisit.push(this);
    while (! toVisit.empty()) {
      final QEdge e0 = toVisit.pop();
      if (! visited.contains(e0)) {
        visited.add(e0);
        final QEdge e1 = e0.faceNext(); visited.add(e1);
        final QEdge e2 = e1.faceNext(); visited.add(e2);
        assert ! (e0.equals(e1) || e0.equals(e2) || (e1.equals(e2)));
        toVisit.push(e0.reverse());
        toVisit.push(e1.reverse());
        toVisit.push(e2.reverse());
        QEdge e3 = e2.faceNext();
        if (e0.equals(e3)) { // triangular
          final TwoSimplex face = TwoSimplex.make(
            e0.origin(), e1.origin(), e2.origin());
          faces.add(face); }
        else {
          while (! e0.equals(e3)) {
            visited.add(e3); toVisit.push(e3.reverse());
            e3 = e3.faceNext(); } } } }
    return faces; }

  //--------------------------------------------------------------------
  // Comparable
  //--------------------------------------------------------------------

  @Override
  public int compareTo (final QEdge q) {
    final int c = origin().compareTo(q.origin());
    if (0 != c) { return c; }
    return dest().compareTo(q.dest()); }

  //--------------------------------------------------------------------
  // Object
  //--------------------------------------------------------------------

  @Override
  public final String toString () {
    return
      "\n" + name()
        + "\nnext:" + next().name()
        + "\nprev:" + prev().name()
        + "\nfaceNext:" + faceNext().name()
        + "\nfacePrev:" + facePrev().name()
        + "\nreverse:" + reverse().name()
        + "\nreverse.next():" + reverse().next().name()
//        + "\nrot:" + rot().name()
//        + "\nrot.next:" + rot().next().name()
//        + "\nreverse:" + reverse().name()
//        + "\nrot.reverse:" + rot().reverse().name()
//        + "\nrot.reverse.next:" + rot().reverse().next().name()
      ; }

  //--------------------------------------------------------------------
  // debugging
  //--------------------------------------------------------------------

  public final void validate () {
    assert Objects.equals(origin(),next().origin());
    assert Objects.equals(origin(),prev().origin());
    assert Objects.equals(origin(),facePrev().dest());
    assert Objects.equals(dest(),faceNext().origin());
    assert Objects.equals(this,reverse().reverse()); }

  public final void validateMesh () {
    final Set<QEdge> visited = new HashSet<>();
    final Stack<QEdge> toVisit = new Stack<>();
    toVisit.push(this);
    while (! toVisit.empty()) {
      final QEdge e = toVisit.pop();
      if (! visited.contains(e)) {
        e.validate();
        visited.add(e);
        toVisit.push(e.next());
        toVisit.push(e.rot());
      }}
  }
  private final String _name;
  public final String name () { return _name; }

  public static final void printFace (final Set<QEdge> visited,
                                      final Stack<QEdge> toVisit) {
    final QEdge e0 = toVisit.pop();
    if ((null!=e0) && (! visited.contains(e0))) {
      System.out.println("\nface:" );
      QEdge e1 = e0;
      do {
        toVisit.remove(e1);
        if (! visited.contains(e1)) {
          System.out.println(e1);
          visited.add(e1);
          final QEdge ri = e1.reverse();
          if (! visited.contains(ri)) { toVisit.push(ri); } }
        e1 = e1.faceNext();
        assert null != e1;
      } while (e0 != e1); } }

  @SuppressWarnings("unused")
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
    _origin = origin; }

  /** Construct a group of 4 circularly linked <code>QEdge</code>s,
   * returning the one from <code>p0</code> to <code>p1</code>,
   * as a representative for the group.
   */

  public static final QEdge make (final ZeroSimplex z0,
                                  final ZeroSimplex z1) {

    final QEdge primal = new QEdge(z0 + "->" + z1, z0);
    final QEdge reverse = new QEdge(z1 + "->" + z0, z1);
    final QEdge rot = new QEdge(z0 + "/^" + z1,null);
    final QEdge reverseRot = new QEdge(z1 + "/^" + z0,null);

    primal._rot = rot;
    reverse._rot = reverseRot;
    rot._rot = reverse;
    reverseRot._rot = primal;

    primal.setNext(primal);
    reverse.setNext(reverse);
    rot.setNext(reverseRot);
    reverseRot.setNext(rot);

    return primal; }

    //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
