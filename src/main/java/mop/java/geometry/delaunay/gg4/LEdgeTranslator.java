package mop.java.geometry.delaunay.gg4;

import mop.java.cmplx.DirectedOneSimplex;
import mop.java.cmplx.SimplicialComplex2D;
import mop.java.geometry.delaunay.Edge;
import mop.java.geometry.delaunay.MeshIndex;
import mop.java.geometry.delaunay.MeshTranslator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Convert the two-simplexes (faces)
 * of a <code>SimpllcialComplex2D</code>
 * into a <code>LEdge</code> structure.
 * Dangling one and zero simplexes are ignored.
 * The faces must form an oriented manifold with boundary.
 * The resulting <code>LEdge</code> structure will be a connected,
 * oriented manifold <em>without</em> boundary, with triangular faces
 * corresponding to the simplicial complex's faces,
 * and possibly non-triangular 'exterior' faces closing boundary
 * loops.
 * <br>
 * TODO: is it really true that the resulting <code>LEdge</code>
 *  will always be connected, and so can be represented by one
 *  <code>LEdge</code>?
 * <br>
 * Essentially constructing a half-edge mesh,
 * via <code>Map</code>s rather than direct references.
 * <br>
 * TODO: Allow building both quad-edge and half-edge meshes.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-11-16
 */
public final class LEdgeTranslator implements MeshTranslator {

  //--------------------------------------------------------------------

  private final MeshIndex _index;
  public final MeshIndex index () { return _index; }

//--------------------------------------------------------------------
// map DirectedOneSimplex's to corresponding LEdges

  private final Map<DirectedOneSimplex, LEdge> _ledge = new HashMap<>();
  private final Map<DirectedOneSimplex, LEdge> ledge () {
    return _ledge; }
  private final LEdge getLEdge (final DirectedOneSimplex e) {
    return ledge().get(e); }
  private final void putLEdge (final DirectedOneSimplex e,
                               final LEdge h) {
    if (! ledge().containsKey(e)) { ledge().put(e, h); } }

//--------------------------------------------------------------------
  /** create the <code>LEdge</code> pair corresponding to the
   * <code>DirectedOneSimplex</code>;
   * return the primal/interior <code>LEdge</code>,
   * which links to the reverse.
   */

  private final LEdge makeQuartet (final DirectedOneSimplex e) {
    final Quartet q = new Quartet();
    final LEdge l = q.edge(0);
    l.EndPoints(e.z0(),e.z1());
    return l; }

  //--------------------------------------------------------------------
  /** Return the primal <code>LEdges</code>s representing a completed
   * version of the input <code>SimplicialComplex2D</code>.
   * <br>
   * Usually one <code>LEdge</code> is enough to link into the mesh,
   * but multiple may be necessary if the corresponding manifold is
   * not connected.
   */
  public final Set<Edge> build () {
    index().indexFaces();
    index().linkVertexNeighbors();
    final Set<Edge> qEdges = new HashSet<>();
    // initial LEdge's only carry the origin and reverse links
    for (final DirectedOneSimplex e : index().dedges()) {
      // create quartets for interior edges only
      // necessary for LischinskiIncrementalBuilder to correclty
      // distinguish 'inside' vs 'outside' initial frame.
      // TODO: modify LischinskiIncrementalBuilder to work without
      //  an initial frame enclosing all future site insertions!
      if (index().isInterior(e)) {
        final DirectedOneSimplex r  = index().getReverse(e);
        if (! (ledge().containsKey(e) || ledge().containsKey(r))) {
          final LEdge q = makeQuartet(e);
          putLEdge(e, q);
          putLEdge(r, q.reverse());
          qEdges.add(q); } } }
    // Stitch LEdges together via next links
    for (final DirectedOneSimplex e : index().dedges()) {
      final LEdge q = getLEdge(e);
      final LEdge qn = getLEdge(index().getNext(e));
      assert null != qn;
      q.setNext(qn);
      final LEdge r = q.rot();
      final LEdge p = getLEdge(index().getPrev(e));
      assert null != p;
      final LEdge rn = p.rot().reverse();
      r.setNext(rn); }
    return qEdges; }


//--------------------------------------------------------------------
// construction
//--------------------------------------------------------------------

  private LEdgeTranslator (final MeshIndex index) {
    this._index = index; }

  public static final LEdgeTranslator
  make (final SimplicialComplex2D cmplx) {
    return new LEdgeTranslator(MeshIndex.make(cmplx)); }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
