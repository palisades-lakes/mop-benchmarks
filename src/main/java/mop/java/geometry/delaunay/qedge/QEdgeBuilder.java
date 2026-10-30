package mop.java.geometry.delaunay.qedge;

import mop.java.cmplx.SimplicialComplex2D;
import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.UndirectedOneSimplex;
import mop.java.cmplx.ZeroSimplex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
 * @version 2026-10-29
 */
public final class QEdgeBuilder {

  // TODO: is there a benefit to using sorted set/maps?

  //--------------------------------------------------------------------
  // undirected one simplexes
  //--------------------------------------------------------------------

  private final Set<UndirectedOneSimplex> _edges = new HashSet<>();

  private final Set<UndirectedOneSimplex> edges () { return _edges; }

  // For testing.
  public final int nEdges () { return edges().size(); }

  // TODO: return updated Set?
  private final void addEdge (final UndirectedOneSimplex e) {
    edges().add(e); }

  //--------------------------------------------------------------------
  // face indexes
  //--------------------------------------------------------------------

  private final Map<UndirectedOneSimplex, TwoSimplex> _leftFaces =
    new HashMap<>();

  private final Map<UndirectedOneSimplex, TwoSimplex> leftFaces () {
    return _leftFaces; }

  public final int nLeftFaces () { return leftFaces().size(); }

  private final TwoSimplex leftFace (final UndirectedOneSimplex e) {
    return leftFaces().get(e); }

  private final void leftFace (final UndirectedOneSimplex e,
                               final TwoSimplex f) {
    assert ! leftFaces().containsKey(e);
    leftFaces().put(e,f); }

  //--------------------------------------------------------------------

  private final Map<UndirectedOneSimplex, TwoSimplex> _rightFaces =
    new HashMap<>();

  private final Map<UndirectedOneSimplex, TwoSimplex> rightFaces () {
    return _rightFaces; }

  public final int nRightFaces () { return rightFaces().size(); }

  private final TwoSimplex rightFace (final UndirectedOneSimplex e) {
    return rightFaces().get(e); }


  private final void rightFace (final UndirectedOneSimplex e,
                                final TwoSimplex f) {
    assert ! rightFaces().containsKey(e);
    rightFaces().put(e,f); }

  //--------------------------------------------------------------------

  public final void addFace (final TwoSimplex f) {
    final ZeroSimplex z0 = f.z0();
    final ZeroSimplex z1 = f.z1();
    final ZeroSimplex z2 = f.z2();
    final UndirectedOneSimplex e01 = UndirectedOneSimplex.make(z0,z1);
    addEdge(e01);
    if (z0.compareTo(z1) < 0) { leftFace(e01,f); }
    else { rightFace(e01,f); }
    final UndirectedOneSimplex e12 = UndirectedOneSimplex.make(z1,z2);
    addEdge(e12);
    if (z1.compareTo(z2) < 0) { leftFace(e12,f); }
    else { rightFace(e12,f); }
    final UndirectedOneSimplex e20 = UndirectedOneSimplex.make(z2,z0);
    addEdge(e20);
    if (z2.compareTo(z0) < 0) { leftFace(e20,f); }
    else { rightFace(e20,f); } }

  //--------------------------------------------------------------------
  // edge classification
  //--------------------------------------------------------------------

  /** Interior is to the 'right' of this edge. **/
  private final boolean rightBoundary (final UndirectedOneSimplex e) {
    return (null == leftFace(e)) && (null != rightFace(e)); }

  /** Interior is to the 'left' of this edge. **/
  private final boolean leftBoundary (final UndirectedOneSimplex e) {
    return (null != leftFace(e)) && (null == rightFace(e)); }

  private final boolean boundary (final UndirectedOneSimplex e) {
    return leftBoundary(e) || rightBoundary(e); }

  private final boolean interior (final UndirectedOneSimplex e) {
    return (null != leftFace(e)) && (null != rightFace(e)); }

  // for testing

  public final int nRightBoundary () {
    int n = 0;
    for (final UndirectedOneSimplex e : edges()) {
      if (rightBoundary(e)) { n++; } }
    return n; }

  public final int nLeftBoundary () {
    int n = 0;
    for (final UndirectedOneSimplex e : edges()) {
      if (leftBoundary(e)) { n++; } }
    return n; }

  public final int nBoundary () {
    int n = 0;
    for (final UndirectedOneSimplex e : edges()) {
      if (boundary(e)) { n++; } }
    return n; }

  public final int nInterior () {
    int n = 0;
    for (final UndirectedOneSimplex e : edges()) {
      if (interior(e)) { n++; } }
    return n; }

  //--------------------------------------------------------------------
  // edge indexes
  //--------------------------------------------------------------------
  // cache one simplex to primal qedge; access to other 3 in quartet
  // via the primal...

  private final Map<UndirectedOneSimplex,QEdge> _primal =
    new HashMap<>();

  private final Map<UndirectedOneSimplex,QEdge> primal () {
    return _primal; }

  private final void primal (final UndirectedOneSimplex e,
                             final QEdge q) {
    assert ! primal().containsKey(e);
    primal().put(e,q); }

  private final QEdge primal (final UndirectedOneSimplex e) {
    return primal().get(e); }

  private final QEdge rot (final UndirectedOneSimplex e) {
    return primal(e).rot(); }

  private final QEdge reverse (final UndirectedOneSimplex e) {
    return primal(e).reverse(); }

  private final QEdge reverseRot (final UndirectedOneSimplex e) {
    return primal(e).reverse().rot(); }

  //--------------------------------------------------------------------
  /** create the <code>QEdge</code> quartet corresponding to the
   * <code>UndirectedOneSimplex</code>;
   * return the primal <code>QEdge</code>,
   * which links to the other 3.
   */
  private final QEdge makeQuartet (final UndirectedOneSimplex e) {
    return QEdge.make(e.toString(),e.z0(),e.z1()); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private QEdgeBuilder () { }

  public static final QEdgeBuilder
  make (final SimplicialComplex2D cmplx) {
    final QEdgeBuilder b = new QEdgeBuilder();
    for (final TwoSimplex f : cmplx.faces()) { b.addFace(f); }
    System.out.println();
    for (final UndirectedOneSimplex e : b.edges()) {
      b.primal(e,b.makeQuartet(e));
      System.out.println(e);
      System.out.println(b.primal(e)); }

    return b; }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
