package mop.java.geometry.delaunay.aqedge;

import mop.java.cmplx.ZeroSimplex;

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
 * @version 2026-10-20
 */
public final class QEdge {

  // TODO: separate primal and dual edges, primal with reference to
  //  origin point, dual reference to explicit origin face?
  //  G+S's edge algebra has separate finite sets E and E* for the
  //  primal and dual edges.
  // TODO: this is actually G+S Rot operation, not dual,
  //  which is why dual().dual() == reverse(), not identity.

  private QEdge _dual;
  /** The dual edge, directed from the right face to the left. */
  public final QEdge dual () { return _dual; }
  /** The dual edge, directed from the right face to the left. */
  public final void setDual (final QEdge e) { _dual = e; }

  private ZeroSimplex _origin;
  public final ZeroSimplex origin () { return _origin; }
  public final void setOrigin (final ZeroSimplex p) {
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

  //--------------------------------------------------------------------
  // Basic Topological Operators
  //--------------------------------------------------------------------
  // TODO: is this some version of splice?

  public final void glue (final QEdge that) {
    final QEdge r = reverse();
    assert r.origin() == that.origin();
    assert origin() == that.reverse().origin();
    r.setNext(that.next());
    r.setDual(that.dual()); }

  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * This operator affects the two edge rings around the origins
   * of a and b, and, independently, the two edge rings around
   * the left faces of <code>this</code> and <code>that</code>.
   * In each case,
   * <ol>
   * <li> if the two rings are distinct, Splice will combine
   * them into one;
   * </li>
   * <li> if the two are the same ring, Splice will break it
   * into two separate pieces.
   * </li>
   * </ol>
   * Thus, splice can be used both to attach the two edges together,
   * and to break them apart.
   * <br>
   * See Guibas and Stolfi (1985) p.96 for more details
   * and illustrations.
   */
  public final void splice (final QEdge that) {

    final QEdge n0 = this.next();
    final QEdge n1 = that.next();
    this.setNext(n1);
    that.setNext(n0);

    final QEdge d0 = n0.dual();
    final QEdge d1 = n1.dual();
    d0.setNext(d1.next());
    d1.setNext(d0.next()); }

  //--------------------------------------------------------------------

  public final void delete () {
    splice(prev());
    reverse().splice(reverse().prev()); }

  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * Add a new edge e connecting
   * the destination of <code>this</code>
   * to the origin of <code>e1</code></code>,
   * in such a way that all three have the same
   * left face after the connection is complete.
   * Additionally, the data pointers of the new edge are set.
   */

  public final QEdge connect (final QEdge e1) {
    final QEdge e = make(reverse().origin(), e1.origin());
    e.splice(faceNext());
    e.reverse().splice(e1);
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
  // construction
  //--------------------------------------------------------------------
  // Mutable mesh wired together by setting _next

  private QEdge () { _next = this; }

  /** Construct a group of 4 circularly linked <code>QEdge</code>s,
   * returning the one from <code>p0</code> to <code>p1</code>,
   * as a representative for the group.
   */

  public static final QEdge make (final ZeroSimplex z0,
                                  final ZeroSimplex z1) {
    final QEdge primal0 = new QEdge();
    final QEdge primal1 = new QEdge();
    final QEdge dual0 = new QEdge();
    final QEdge dual1 = new QEdge();
    primal0._dual = dual0;
    primal1._dual = dual1;
    dual0._dual = primal1;
    dual1._dual = primal0;

    primal0._origin = z0;
    assert primal1.equals(primal0.reverse());
    primal0.reverse()._origin = z1;
    return primal0; }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
