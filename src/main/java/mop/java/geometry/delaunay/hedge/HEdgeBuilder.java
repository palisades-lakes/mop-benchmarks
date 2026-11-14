package mop.java.geometry.delaunay.hedge;

import mop.java.cmplx.DirectedOneSimplex;
import mop.java.cmplx.SimplicialComplex2D;
import mop.java.geometry.delaunay.MeshIndex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Convert the two-simplexes (faces)
 * of a <code>SimpllcialComplex2D</code>
 * into a <code>HEdge</code> structure.
 * Dangling one and zero simplexes are ignored.
 * The faces must form an oriented manifold with boundary.
 * The resulting <code>HEdge</code> structure will be a connected,
 * oriented manifold <em>without</em> boundary, with triangular faces
 * corresponding to the simplicial complex's faces,
 * and possibly non-triangular 'exterior' faces closing boundary
 * loops.
 * <br>
 * TODO: is it really true that the resulting <code>HEdge</code>
 *  will always be connected, and so can be represented by one
 *  <code>HEdge</code>?
 * <br>
 * Essentially constructing a half-edge mesh,
 * via <code>Map</code>s rather than direct references.
 * <br>
 * TODO: Allow building both quad-edge and half-edge meshes.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-11-14
 */
public final class HEdgeBuilder {

  //--------------------------------------------------------------------

  private final MeshIndex _index;
  public final MeshIndex index () { return _index; }

//--------------------------------------------------------------------
// map DirectedOneSimplex's to corresponding HEdges

  private final Map<DirectedOneSimplex, HEdge> _hedge = new HashMap<>();
  private final Map<DirectedOneSimplex, HEdge> hedge () {
    return _hedge; }
  private final HEdge getHEdge (final DirectedOneSimplex e) {
    return hedge().get(e); }
  private final void putHEdge (final DirectedOneSimplex e, final HEdge h) {
    if (! hedge().containsKey(e)) { hedge().put(e,h); } }

//--------------------------------------------------------------------
  /** create the <code>HEdge</code> pair corresponding to the
   * <code>DirectedOneSimplex</code>;
   * return the primal/interior <code>HEdge</code>,
   * which links to the reverse.
   */

  private final HEdge makePair (final DirectedOneSimplex e) {
    return HEdge.make(e.z0(), e.z1()); }

  /** Return the primal <code>HEdges</code>s representing a completed
   * version of the input <code>SimplicialComplex2D</code>.
   * <br>
   * Usually one <code>HEdge</code> is enough to link into the mesh,
   * but multiple may be necessary if the corresponding manifold is
   * not connected.
   */
  public final Set<HEdge> build () {
    index().indexFaces();
    index().linkBoundaryFaces();
    final Set<HEdge> hEdges = new HashSet<>();
    // set origin and reverse (and dest=reverse.origin)
    // for interior HEdges
    for (final DirectedOneSimplex e : index().dedges()) {
      final DirectedOneSimplex r  = index().getReverse(e);
      if (! (hedge().containsKey(e) || hedge().containsKey(r))) {
        final HEdge h = makePair(e);
        putHEdge(e, h);
        putHEdge(r, h.reverse());
        hEdges.add(h); } }
    // set faceNext for all HEdges
    for (final DirectedOneSimplex e : hedge().keySet()) {
      final HEdge h = getHEdge(e);
      final HEdge n = getHEdge(index().getFaceNext(e));
      assert null != n;
      h.setFaceNext(n); }
    for (final HEdge h : hEdges) { h.validate(); }
    return hEdges; }

//--------------------------------------------------------------------
// construction
//--------------------------------------------------------------------

  private HEdgeBuilder (final MeshIndex index) {
    this._index = index; }

  public static final HEdgeBuilder
  make (final SimplicialComplex2D cmplx) {
    return new HEdgeBuilder(MeshIndex.make(cmplx)); }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
