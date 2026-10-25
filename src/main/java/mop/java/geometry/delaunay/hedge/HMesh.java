package mop.java.geometry.delaunay.hedge;

import mop.java.geometry.euclidean.VectorD2;

import java.util.HashSet;
import java.util.Set;

/** Quad Edge representation of a triangle mesh, after
 * <a href="https://www.researchgate.net/publication/262235495_Incremental_Delaunay_Triangulation">
 *  Dani Lischinski
 *  <i>Incremental Delaunay triangulation</i>
 *  <b>Graphics Gems IV</b> Academic Press 1994 </a>
 *  <p>
 *
 *  </p>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-24
 */

public final class HMesh {

  private HEdge _startingEdge;
  public final HEdge startingEdge () { return _startingEdge; }
  final void setStartingEdge (final HEdge e) { _startingEdge = e; }

  //--------------------------------------------------------------------
  /** Collect the vertices as a set of points.
   * Skip elements of <code>exclude</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public final Set<VectorD2> vertices (final Set<VectorD2> exclude) {
    return startingEdge().vertices(exclude); }

  public final Set<VectorD2> vertices () {
    return vertices(new HashSet<>()); }

  //--------------------------------------------------------------------
  /** Collect the edges as a set of pairs of coordinates.
   * Skip edges that have any vertices in <code>exclude</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public final Set<VectorD2[]> edges (final Set<VectorD2> exclude) {
    return startingEdge().edges(exclude); }

  //--------------------------------------------------------------------
  /** Collect the triangles as a set of triples of coordinates.
   * Skip triangles that have any vertices in <code>exclude</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public final Set<VectorD2[]> triangles (final Set<VectorD2> exclude) {
    return startingEdge().triangles(exclude); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private HMesh (final HEdge startingEdge) {
    _startingEdge = startingEdge; }

  /** All inserted points must lie in the triangle formed by
   * <code>p0, p1, p2</code>.
   */

  public static final HMesh
  triangleFrame (final VectorD2 p0,
                 final VectorD2 p1,
                 final VectorD2 p2) {
   // this creates a closed manifold, a triangle and its 'reverse'
    final HEdge e01 = HEdge.make("e01", p0, p1);
    final HEdge e12 = HEdge.make("e12", p1, p2);
    final HEdge e20 = HEdge.make("e20", p2, p0);
    e01.splice(e20.reverse());
    e12.splice(e01.reverse());
    e20.splice(e12.reverse());
    System.out.println("triangleFrame:");
    e01.printMesh();
    return new HMesh(e01); }

  //--------------------------------------------------------------------

  public static final HMesh
  rectangleFrame (final double xmin,
                  final double xmax,
                  final double ymin,
                  final double ymax) {
    final VectorD2 p0 = new VectorD2(xmin,ymin);
    final VectorD2 p1 = new VectorD2(xmax,ymin);
    final VectorD2 p2 = new VectorD2(xmax,ymax);
    final VectorD2 p3 = new VectorD2(xmin,ymax);
    // face012
    final HEdge e01 = HEdge.make("e01",p0, p1);
    final HEdge e12 = HEdge.make("e12",p1, p2);
    final HEdge e20 = HEdge.make("e20",p2, p0);
    e01.splice(e20.reverse());
    e12.splice(e01.reverse());
    e20.splice(e12.reverse());
   // face023
    final HEdge e02 = e20.reverse();
    final HEdge e23 = HEdge.make("e23",p2, p3);
    final HEdge e30 = HEdge.make("e30",p3, p0);
    e02.splice(e30.reverse());
    e23.splice(e02.reverse());
    e30.splice(e23.reverse());
    System.out.println("rectangleFrame:");
    e01.printMesh();
    return new HMesh(e01); }


//--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------

