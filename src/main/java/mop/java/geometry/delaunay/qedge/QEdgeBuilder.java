package mop.java.geometry.delaunay.qedge;

import mop.java.cmplx.DirectedOneSimplex;
import mop.java.cmplx.SimplicialComplex2D;
import mop.java.geometry.delaunay.MeshIndex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Convert the two-simplexes (faces)
 * of a <code>SimpllcialComplex2D</code>
 * into a <code>QEdge</code> structure.
 * Dangling one and zero simplexes are ignored.
 * The faces must form an oriented manifold with boundary.
 * The resulting <code>QEdge</code> structure will be a connected,
 * oriented manifold <em>without</em> boundary, with triangular faces
 * corresponding to the simplicial complex's faces,
 * and possibly non-triangular 'exterior' faces closing boundary
 * loops.
 * <br>
 * TODO: is it really true that the resulting <code>QEdge</code>
 *  will always be connected, and so can be represented by one
 *  <code>QEdge</code>?
 * <br>
 * Essentially constructing a half-edge mesh,
 * via <code>Map</code>s rather than direct references.
 * <br>
 * TODO: Allow building both quad-edge and half-edge meshes.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-11-14
 */
public final class QEdgeBuilder {

  //--------------------------------------------------------------------

  private final MeshIndex _index;
  public final MeshIndex index () { return _index; }

  //--------------------------------------------------------------------
  // map DirectedOneSimplex's to corresponding primal QEdges

  private final Map<DirectedOneSimplex,QEdge> _qedge = new HashMap<>();
  private final Map<DirectedOneSimplex,QEdge> qedge () {
    return _qedge; }
  private final QEdge qedge (final DirectedOneSimplex e) {
    return qedge().get(e); }
  private final void qedge (final DirectedOneSimplex e, final QEdge q) {
    if (! qedge().containsKey(e)) { qedge().put(e,q); } }

//--------------------------------------------------------------------
  /** create the <code>QEdge</code> quartet corresponding to the
   * <code>DirectedOneSimplex</code>;
   * return the primal <code>QEdge</code>,
   * which links to the other 3.
   */

  private final QEdge makeQuartet (final DirectedOneSimplex e) {
    return QEdge.make(e.z0(),e.z1()); }

  /** Return the primal <code>QEdges</code>s representing a completed
   * version of the input <code>SimplicialComplex2D</code>.
   * <br>
   * Usually one <code>QEdge</code> is enough to link into the mesh,
   * but multiple may be necessary if the corresponding manifold is
   * not connected.
   */
  public final Set<QEdge> build () {
    index().indexFaces();
    index().linkVertexNeighbors();
    final Set<QEdge> qEdges = new HashSet<>();
    // initial QEdge's only carry the origin and reverse links
    for (final DirectedOneSimplex e : index().dedges()) {
      // create quartets for interior edges only
      // necessary for LischinskiIncrementalBuilder to correclty
      // distinguish 'inside' vs 'outside' initial frame.
      // TODO: modify LischinskiIncrementalBuilder to work without
      //  an initial frame enclosing all future site insertions!
      if (index().isInterior(e)) {
        final DirectedOneSimplex r  = index().getReverse(e);
        if (! (qedge().containsKey(e) || qedge().containsKey(r))) {
          final QEdge q = makeQuartet(e);
          qedge(e,q);
          qedge(r,q.reverse());
          qEdges.add(q); } } }
    // Stitch QEdges together via next links
    for (final DirectedOneSimplex e : index().dedges()) {
      final QEdge q = qedge(e);
      final QEdge qn = qedge(index().getNext(e));
      assert null != qn;
      q.setNext(qn);
      final QEdge r = q.rot();
      final QEdge p = qedge(index().getPrev(e));
      assert null != p;
      final QEdge rn = p.rot().reverse();
      r.setNext(rn); }
    return qEdges; }

//--------------------------------------------------------------------
// construction
//--------------------------------------------------------------------

  private QEdgeBuilder (final MeshIndex index) {
    this._index = index; }

  public static final QEdgeBuilder
  make (final SimplicialComplex2D cmplx) {
    return new QEdgeBuilder(MeshIndex.make(cmplx)); }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
