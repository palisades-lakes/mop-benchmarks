package mop.java.geometry.delaunay.qedge;

import mop.java.geometry.euclidean.VectorD2;

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
 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-03
 */
public final class QEdge {

  private QEdge _next;
  /** The next edge ccw around the origin. */
  public final QEdge next () { return _next; }
  /** The next edge ccw around the origin. */
  public final void setNext (final QEdge e) { _next = e; }

  private QEdge _dual;
  /** The dual edge, directed from the right face to the left. */
  public final QEdge dual () { return _dual; }

  private VectorD2 _origin;
  public final VectorD2 origin () { return _origin; }
  public final void setOrigin (final VectorD2 p) { _origin = p; }

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
  final void splice (final QEdge b) {
    final QEdge alpha = this.next().dual();
    final QEdge beta = b.next().dual();
    final QEdge t1 = b.next();
    final QEdge t2 = this.next();
    final QEdge t3 = beta.next();
    final QEdge t4 = alpha.next();
    this.setNext(t1);
    b.setNext(t2);
    alpha.setNext(t3);
    beta.setNext(t4); }

  //--------------------------------------------------------------------

  final void delete () {
    splice(prev());
    reverse().splice(reverse().prev()); }

  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * Add a new edge e connecting the destination of <code>e0</code> to the
   * origin of <code>e1</code></code>,
   * in such a way that all three have the same
   * left face after the connection is complete.
   * Additionally, the data pointers of the new edge are set.
   */

  final QEdge connect (final QEdge e1) {
    final QEdge e = make(reverse().origin(), e1.origin());
    e.splice(faceNext());
    e.reverse().splice(e1);
    return e; }

  /** Lischinski: <br>
   * Essentially turns edge e counterclockwise inside its enclosing
   * quadrilateral. The data pointers are modified accordingly.
   */

  final void swap () {
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

  private QEdge () { }

  /** Construct a group of 4 circularly linked <code>QEdge</code>s,
   * returning the one from <code>p0</code> to <code>p1</code>,
   * as a representative for the group.
   */

  public static final QEdge make (final VectorD2 p0,
                                  final VectorD2 p1) {

    final QEdge q0 = new QEdge();
    final QEdge q1 = new QEdge();
    final QEdge q2 = new QEdge();
    final QEdge q3 = new QEdge();

    q0._dual = q1;
    q1._dual = q2;
    q2._dual = q3;
    q3._dual = q0;

    q0._next = q0;
    q1._next = q3;
    q2._next = q2;
    q3._next = q1;

    q0.setOrigin(p0);
    q0.reverse().setOrigin(p1);
    return q0; }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
