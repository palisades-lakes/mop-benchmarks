package mop.java.geometry.delaunay.qedge;

import mop.java.cmplx.SimplicialComplex2D;
import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.UndirectedOneSimplex;
import mop.java.cmplx.ZeroSimplex;

import java.util.HashMap;
import java.util.Map;

/** Convert the two simplexes (faces)
 * of a <code>SimpllcialComplex2D</code>
 * into a <code>QEdge</code> structure.
 * Dangling one and zero simplexes are ignored.
 * The faces must form an oriented manifold with boundary.
 * The resulting <code>QEdge</code> structure will be a connected,
 * oriented manifold <em>without</em> boundary, with triangular faces
 * corresponding to the simplicial complex's faces,
 * and possibly non-triangular 'exterior' faces closing the boundary
 * loops.
 * <br>
 * TODO: is it really true that the resulting <code>QEdge</code>
 *  will always be connected, and so can be represented by one
 *  <code>QEdge</code>?
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-25
 */
public final class QEdgeBuilder {

  private final Map<UndirectedOneSimplex, TwoSimplex> _leftFaces =
    new HashMap<>();
  public final Map<UndirectedOneSimplex, TwoSimplex> leftFaces () {
    return _leftFaces; }
  private final TwoSimplex leftFace (final UndirectedOneSimplex e) {
    return leftFaces().get(e); }
  private final void leftFace (final UndirectedOneSimplex e,
                               final TwoSimplex f) {
    assert ! leftFaces().containsKey(e);
    leftFaces().put(e,f); }

  private final Map<UndirectedOneSimplex, TwoSimplex> _rightFaces =
    new HashMap<>();
  public final Map<UndirectedOneSimplex, TwoSimplex> rightFaces () {
    return _rightFaces; }
  private final TwoSimplex rightFace (final UndirectedOneSimplex e) {
    return rightFaces().get(e); }
  private final void rightFace (final UndirectedOneSimplex e,
                               final TwoSimplex f) {
    assert ! rightFaces().containsKey(e);
    rightFaces().put(e,f); }

  public final void addFace (final TwoSimplex f) {
    final ZeroSimplex z0 = f.z0();
    final ZeroSimplex z1 = f.z1();
    final ZeroSimplex z2 = f.z2();
    final UndirectedOneSimplex e01 = UndirectedOneSimplex.make(z0,z1);
    if (z0.compareTo(z1) < 0) { leftFace(e01,f); }
    else { rightFace(e01,f); }
    final UndirectedOneSimplex e12 = UndirectedOneSimplex.make(z1,z2);
    if (z1.compareTo(z2) < 0) { leftFace(e12,f); }
    else { rightFace(e12,f); }
    final UndirectedOneSimplex e20 = UndirectedOneSimplex.make(z2,z0);
    if (z2.compareTo(z0) < 0) { leftFace(e20,f); }
    else { rightFace(e20,f); } }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private QEdgeBuilder () {
     }

  public static final QEdgeBuilder make (final SimplicialComplex2D cmplx) {
    final QEdgeBuilder b = new QEdgeBuilder();
    for (final TwoSimplex f : cmplx.faces()) { b.addFace(f); }
    return b; }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
