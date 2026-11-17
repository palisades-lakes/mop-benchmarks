package mop.java.geometry.delaunay.qedge;

import mop.java.cmplx.ZeroSimplex;
import mop.java.geometry.delaunay.Edge;

import java.util.Objects;

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
 * @version 2026-11-16
 */

public final class QEdge implements Edge {

  @Override
  public final String name () {
    return origin() + "->" + dest(); }

  private ZeroSimplex _origin;
  @Override
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

  private QEdge _rot;
  /** The dual edge, directed from the right face to the left. */
  public final QEdge rot () { return _rot; }

  /** The next edge ccw around the origin. */
  private QEdge _next;
  /** The next edge ccw around the origin. */
  @Override
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
  @Override
  public final QEdge reverse () { return rot().rot(); }

  public final ZeroSimplex dest () { return reverse().origin(); }

  /** The next edge cw around the origin. */
  @Override
  public final QEdge prev () { return rot().next().rot(); }

  /** The next edge ccw around the (left) face. */
  @Override
  public final QEdge faceNext () {
    return rot().reverse().next().rot(); }

  /** The next edge cw around the (left) face. */
  @Override
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

  @Override
  public final Edge connect (final Edge that) {
    return connect((QEdge) that); }

    /** Lischinski: <br>
     * Essentially turns edge e counterclockwise inside its enclosing
     * quadrilateral.
     * <br>
     * <em>Rewritten to return a new QEdge, to maintain that constraint
     * between <code>next()</code> and <code>origin()</code></em>.
     */

  @Override
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
  // construction
  //--------------------------------------------------------------------
  // Mutable mesh wired together by setting _next

  private QEdge (final ZeroSimplex origin) {
    _origin = origin; }

  /** Construct a group of 4 circularly linked <code>QEdge</code>s,
   * returning the one from <code>p0</code> to <code>p1</code>,
   * as a representative for the group.
   */

  public static final QEdge make (final ZeroSimplex z0,
                                  final ZeroSimplex z1) {

    final QEdge primal = new QEdge(z0);
    final QEdge reverse = new QEdge(z1);
    final QEdge rot = new QEdge(null);
    final QEdge reverseRot = new QEdge(null);

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
