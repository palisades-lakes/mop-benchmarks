package mop.java.geometry.delaunay.gg4clean;

import mop.java.geometry.euclidean.VectorD2;

/** Delaunay triangulation, after
 * <a href="https://www.researchgate.net/publication/262235495_Incremental_Delaunay_Triangulation">
 *  Dani Lischinski
 *  <i>Incremental Delaunay triangulation</i>
 *  <b>Graphics Gems IV</b> Academic Press 1994 </a>

 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-01
 */
public final class Edge {

  private final QuadEdge _qedge;
  public final QuadEdge qedge () { return _qedge; }
  /** index of this edge in the qedge's edge array. */
  int num;
  Edge next;
  private VectorD2 origin;

  //--------------------------------------------------------------------
  // methods
  //--------------------------------------------------------------------

  public final Edge dual () {
    // Return the dual of the current edge, directed from its right to its left.
    return qedge().edge((num < 3) ? num + 1 : num - 3); }

  public final Edge dualReverse () {
    // Return the dual of the current edge, directed from its left to its right.
    return qedge().edge((num > 0) ? num - 1 : num + 3); }

  public final Edge reverse () {
    // Return the edge from the destination to the origin of the current edge.
    return qedge().edge((num < 2) ? num + 2 : num - 2); }

  public final Edge srcNext () {
    // Return the next ccw edge around (from) the origin of the current edge.
    return next; }

  public final Edge srcPrev () {
    // Return the next cw edge around (from) the origin of the current edge.
    return dual().srcNext().dual(); }

  public final Edge dstNext () {
    // Return the next ccw edge around (into) the destination of the current edge.
    return reverse().srcNext().reverse(); }

  public final Edge dstPrev () {
    // Return the next cw edge around (into) the destination of the current edge.
    return dualReverse().srcNext().dualReverse(); }

  public final Edge leftNext () {
    // Return the ccw edge around the left face following the current edge.
    return dualReverse().srcNext().dual(); }

  public final Edge leftPrev () {
    // Return the ccw edge around the left face before the current edge.
    return srcNext().reverse(); }

  public final Edge rightNext () {
    // Return the edge around the right face ccw following the current edge.
      return dual().srcNext().dualReverse(); }

  public final Edge rightPrev () {
    // Return the edge around the right face ccw before the current edge.
    return reverse().srcNext(); }

  public final VectorD2 src () { return origin; }

  public final VectorD2 dst () { return reverse().origin; }

  public final void setEndpoints (final VectorD2 src,
                                  final VectorD2 dst) {
    origin = src;
    reverse().origin = dst; }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public Edge (final QuadEdge q) {
    // cpp: data = 0;
    // same as default to null
    _qedge = q; }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
