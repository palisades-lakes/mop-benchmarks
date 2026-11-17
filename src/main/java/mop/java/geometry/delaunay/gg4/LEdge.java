package mop.java.geometry.delaunay.gg4;

import mop.java.cmplx.ZeroSimplex;
import mop.java.geometry.delaunay.Edge;

/** "LEdge" == Lischinski quad edge, from Graphics Gems IV.
 * Used in Delaunay triangulation, after
 * <a href="https://www.researchgate.net/publication/262235495_Incremental_Delaunay_Triangulation">
 *  Dani Lischinski
 *  <i>Incremental Delaunay triangulation</i>
 *  <b>Graphics Gems IV</b> Academic Press 1994 </a>

 * @author palisades dot lakes at gmail dot com
 * @version 2026-11-16
 */
public final class LEdge implements Edge {

  private final Quartet _quartet;
  public final Quartet quartet () { return _quartet; }
  /** index of this edge in the Qedge's edge array. */
  int num;
  LEdge next;
  public final void setNext (final LEdge e) { next = e; }
  private ZeroSimplex data;

  //--------------------------------------------------------------------
  // methods
  //--------------------------------------------------------------------

  public final LEdge Rot () {
    // Return the dual of the current edge, directed from its right to its left.
    return quartet().edge((num < 3) ? num + 1 : num - 3); }

  public final LEdge invRot () {
    // Return the dual of the current edge, directed from its left to its right.
    return quartet().edge((num > 0) ? num - 1 : num + 3); }

  public final LEdge Sym () {
    // Return the edge from the destination to the origin of the current edge.
    return quartet().edge((num < 2) ? num + 2 : num - 2); }

  public final LEdge Onext () {
    // Return the next ccw edge around (from) the origin of the current edge.
    return next; }

  public final LEdge Oprev () {
    // Return the next cw edge around (from) the origin of the current edge.
    return Rot().Onext().Rot(); }

  @SuppressWarnings("unused")
  public final LEdge Dnext () {
    // Return the next ccw edge around (into) the destination of the current edge.
    return Sym().Onext().Sym(); }

  public final LEdge Dprev () {
    // Return the next cw edge around (into) the destination of the current edge.
    return invRot().Onext().invRot(); }

  public final LEdge Lnext () {
    // Return the ccw edge around the left face following the current edge.
    return invRot().Onext().Rot(); }

  public final LEdge Lprev () {
    // Return the ccw edge around the left face before the current edge.
    return Onext().Sym(); }

  @SuppressWarnings("unused")
  public final LEdge Rnext () {
    // Return the edge around the right face ccw following the current edge.
      return Rot().Onext().invRot(); }

  @SuppressWarnings("unused")
  public final LEdge Rprev () {
    // Return the edge around the right face ccw before the current edge.
    return Sym().Onext(); }

  public final ZeroSimplex Org() { return data; }

  public final ZeroSimplex Dest() { return Sym().data; }

//  public final VectorD2 Org2d() { return pt; }
//
//  public final VectorD2 Dest2d() {
//    return quartet().edge((num < 2) ? (num + 2) : (num - 2)).data; }

  public final void EndPoints (final ZeroSimplex or,
                               final ZeroSimplex de) {
    data = or;
    Sym().data = de; }

  //--------------------------------------------------------------------
  // Edge methods
  //--------------------------------------------------------------------
  // TODO: rename above for performance?

  @Override
  public ZeroSimplex origin () { return Org(); }

  @Override
  public ZeroSimplex dest () { return Dest(); }

  public final LEdge rot () { return Rot(); }

  @Override
  public LEdge reverse () { return Sym(); }

  @Override
  public LEdge faceNext () { return Lnext(); }

  @Override
  public LEdge facePrev () { return Lprev(); }

  @Override
  public LEdge next () { return Onext(); }

  @Override
  public LEdge prev () { return Oprev(); }

  @Override
  public void delete () { SubdivisionBuilder.DeleteEdge(this); }

  @Override
  public LEdge connect (final Edge that) {
    return SubdivisionBuilder.Connect(this, (LEdge) that); }

  @Override
  public LEdge swap () { return SubdivisionBuilder.Swap(this);  }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public LEdge (final Quartet q) {
    // cpp: data = 0;
    // same as default to null
    _quartet = q;
  }


  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
