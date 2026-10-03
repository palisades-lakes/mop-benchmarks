package mop.java.geometry.delaunay.clean;

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
  // construction
  //--------------------------------------------------------------------

  private QEdge () { }

  /** Construct a group of 4 circularly linked <code>QEdge</code>s,
   * returning one as a representative for the group.
   */
  public static final QEdge make (final VectorD2 a,
                                  final VectorD2 b) {

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

    q0.setOrigin(a);
    q0.reverse().setOrigin(b);
    return q0; }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
