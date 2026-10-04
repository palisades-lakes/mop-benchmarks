package mop.java.geometry.delaunay.gg4;

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

  private final QuadEdge _Qedge;
  public final QuadEdge Qedge () { return _Qedge; }
  /** index of this edge in the Qedge's edge array. */
  int num;
  Edge next;
  private VectorD2 data;

  //--------------------------------------------------------------------
  // methods
  //--------------------------------------------------------------------

  public final Edge Rot() {
    // Return the dual of the current edge, directed from its right to its left.
    return Qedge().edge((num < 3) ? num + 1 : num - 3); }

  public final Edge invRot() {
    // Return the dual of the current edge, directed from its left to its right.
    return Qedge().edge((num > 0) ? num - 1 : num + 3); }

  public final Edge Sym() {
    // Return the edge from the destination to the origin of the current edge.
    return Qedge().edge((num < 2) ? num + 2 : num - 2); }

  public final Edge Onext() {
    // Return the next ccw edge around (from) the origin of the current edge.
    return next; }

  public final Edge Oprev() {
    // Return the next cw edge around (from) the origin of the current edge.
    return Rot().Onext().Rot(); }

//  public final QEdge Dnext() {
//    // Return the next ccw edge around (into) the destination of the current edge.
//    return Sym().Onext().Sym(); }

  public final Edge Dprev() {
    // Return the next cw edge around (into) the destination of the current edge.
    return invRot().Onext().invRot(); }

  public final Edge Lnext() {
    // Return the ccw edge around the left face following the current edge.
    return invRot().Onext().Rot(); }

  public final Edge Lprev() {
    // Return the ccw edge around the left face before the current edge.
    return Onext().Sym(); }

//  public final QEdge Rnext() {
//    // Return the edge around the right face ccw following the current edge.
//      return Rot().Onext().invRot(); }

//  public final QEdge Rprev() {
//    // Return the edge around the right face ccw before the current edge.
//    return Sym().Onext(); }

  public final VectorD2 Org() { return data; }

  public final VectorD2 Dest() { return Sym().data; }

  public final VectorD2 Org2d() { return data; }

  public final VectorD2 Dest2d() {
    return Qedge().edge((num < 2) ? (num + 2) : (num - 2)).data; }

  public final void EndPoints (final VectorD2 or,
                               final VectorD2 de) {
    data= or;
    Sym().data = de; }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public Edge (final QuadEdge q) {
    // cpp: data = 0;
    // same as default to null
    _Qedge = q;
  }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
