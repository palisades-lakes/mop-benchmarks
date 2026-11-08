package mop.java.geometry.delaunay.qedge;

import mop.java.cmplx.DirectedOneSimplex;
import mop.java.cmplx.SimplicialComplex2D;
import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.ZeroSimplex;

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
 * @version 2026-11-02
 */
public final class QEdgeBuilder {

  // TODO: is there a benefit to using sorted set/maps?

  private final SimplicialComplex2D _cmplx;
  private final SimplicialComplex2D cmplx () { return _cmplx; }

  //--------------------------------------------------------------------
  // directed one simplexes == half edges
  //--------------------------------------------------------------------

  private final Map<DirectedOneSimplex,DirectedOneSimplex>
    _reverse = new HashMap<>();

  private final Map<DirectedOneSimplex,DirectedOneSimplex>
  reverse () { return _reverse; }

  private final DirectedOneSimplex
  reverse (final DirectedOneSimplex e) {
    assert null != e;
    final DirectedOneSimplex r = reverse().get(e);
    assert null != r;
    return r; }

  private final void setReverse (final DirectedOneSimplex e,
                                 final DirectedOneSimplex r) {
    assert null != e;
    assert null != r;
    assert e.z0().equals(r.z1());
    assert e.z1().equals(r.z0());
    if (reverse().containsKey(e) || reverse().containsKey(r)) {
      assert r.equals(reverse().get(e));
      assert e.equals(reverse().get(r)); }
    else {
      reverse().put(e,r);
      reverse().put(r,e); } }

  // For testing.
  public final int nHalfEdges () { return reverse().size(); }

  //--------------------------------------------------------------------
  // Previous/next half edges within oriented face:

  private final Map<DirectedOneSimplex,DirectedOneSimplex> _facePrev =
    new HashMap<>();

  private final Map<DirectedOneSimplex,DirectedOneSimplex> facePrev () {
    return _facePrev; }

  private final DirectedOneSimplex
  facePrev (final DirectedOneSimplex e) { return facePrev().get(e); }

  private final void setFacePrev (final DirectedOneSimplex e,
                                  final DirectedOneSimplex prev) {
    assert null != e;
    assert ! facePrev().containsKey(e);
    facePrev().put(e,prev); }

  private final Map<DirectedOneSimplex,DirectedOneSimplex> _faceNext =
    new HashMap<>();

  private final Map<DirectedOneSimplex,DirectedOneSimplex> faceNext () {
    return _faceNext; }

  private final DirectedOneSimplex
  faceNext (final DirectedOneSimplex e) {
    assert null != e;
    return faceNext().get(e); }

  private final void setFaceNext (final DirectedOneSimplex e,
                                  final DirectedOneSimplex next) {
    assert null != e;
    assert ! faceNext().containsKey(e);
    faceNext().put(e,next); }

  //--------------------------------------------------------------------
  // Previous (CW) / next (CCW) half edges around their shared origin
  // vertex:

  private final Map<DirectedOneSimplex,DirectedOneSimplex> _prev =
    new HashMap<>();

  private final Map<DirectedOneSimplex,DirectedOneSimplex> prev () {
    return _prev; }

  private final DirectedOneSimplex
  prev (final DirectedOneSimplex e) {
    assert null != e;
    return prev().get(e); }

  private final void setPrev (final DirectedOneSimplex e,
                              final DirectedOneSimplex prev) {
    assert null != e;
    assert ! prev().containsKey(e);
    assert null != prev;
    prev().put(e,prev); }

  private final Map<DirectedOneSimplex,DirectedOneSimplex> _next =
    new HashMap<>();

  private final Map<DirectedOneSimplex,DirectedOneSimplex> next () {
    return _next; }

  private final DirectedOneSimplex
  next (final DirectedOneSimplex e) {
    assert null != e;
    return next().get(e); }

  private final void setNext (final DirectedOneSimplex e,
                              final DirectedOneSimplex next) {
    assert null != e;
    assert ! next().containsKey(e);
    assert null != next;
    next().put(e,next); }

  //--------------------------------------------------------------------
  // face indexes
  //--------------------------------------------------------------------

  private final Map<DirectedOneSimplex, TwoSimplex> _faces =
    new HashMap<>();

  private final Map<DirectedOneSimplex, TwoSimplex> faces () {
    return _faces; }

  //public final int nFaces () { return faces().size() / 3; }

  private final TwoSimplex face (final DirectedOneSimplex e) {
    assert null != e;
    return faces().get(e); }

  private final void face (final DirectedOneSimplex e,
                           final TwoSimplex f) {
    assert ! faces().containsKey(e);
    faces().put(e, f); }

  // for testing
  public final int nFaces () { return faces().size() / 3; }

  //--------------------------------------------------------------------
  // edge classification
  //--------------------------------------------------------------------

  private final boolean boundary (final DirectedOneSimplex e) {
    assert null != e;
    return null == face(e); }

  private final boolean interior (final DirectedOneSimplex e) {
    assert null != e;
    return ! boundary(e); }

  // for testing

  public final int nBoundary () {
    int n = 0;
    for (final DirectedOneSimplex e : reverse().keySet()) {
      if (boundary(e)) { n++; } }
    return n; }

  public final int nInterior () {
    int n = 0;
    for (final DirectedOneSimplex e : reverse().keySet()) {
      if (interior(e)) { n++; } }
    return n; }

  //--------------------------------------------------------------------

  private final void addFace (final TwoSimplex f) {
    final ZeroSimplex z0 = f.z0();
    final ZeroSimplex z1 = f.z1();
    final ZeroSimplex z2 = f.z2();
    // interior
    final DirectedOneSimplex e01 = DirectedOneSimplex.make(z0,z1);
    final DirectedOneSimplex e12 = DirectedOneSimplex.make(z1,z2);
    final DirectedOneSimplex e20 = DirectedOneSimplex.make(z2,z0);
    // possible boundary
    final DirectedOneSimplex e10 = DirectedOneSimplex.make(z1,z0);
    final DirectedOneSimplex e21 = DirectedOneSimplex.make(z2,z1);
    final DirectedOneSimplex e02 = DirectedOneSimplex.make(z0,z2);
    setReverse(e01,e10); setReverse(e12,e21); setReverse(e20,e02);
    face(e01,f); face(e12, f); face(e20,f);
    setFacePrev(e01, e20); setFacePrev(e12, e01); setFacePrev(e20, e12);
    setFaceNext(e01, e12); setFaceNext(e12, e20); setFaceNext(e20, e01);
  }

  // cache next and prev for e,
  // using previously cached reverse, faceNext, and facePrev
  // TODO: do reverse(e) at the same time?
  private final void primalNeighbors (final DirectedOneSimplex e) {

    // TODO: optimize once working

    if (interior(e) && interior(reverse(e))) {
      setNext(e, reverse(facePrev(e)));
      setPrev(e, faceNext(reverse(e)));
      return; }

    if (interior(e) && boundary(reverse(e))) {
      setNext(e, reverse(facePrev(e)));
      DirectedOneSimplex p = reverse(facePrev(e));
      while (! boundary(p)) { p = reverse(facePrev(p)); }
      setPrev(e, p);
      return; }

    assert boundary(e);
    assert interior(reverse(e));
    setPrev(e, faceNext(reverse(e)));
    DirectedOneSimplex n = faceNext(reverse(e));
    while (! boundary(reverse(n))) {
      n = faceNext(reverse(n)); }
    setNext(e,n); }

//--------------------------------------------------------------------

private final void index () {
  for (final TwoSimplex f : cmplx().faces()) { addFace(f); }
  // stitch primal QEdges
  for (final DirectedOneSimplex e : reverse().keySet()) {
    primalNeighbors(e); } }

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
/** create the <code>HEdge</code> quartet corresponding to the
 * <code>DirectedOneSimplex</code>;
 * return the primal <code>HEdge</code>,
 * which links to the other 3.
 */

private final QEdge makeQuartet (final DirectedOneSimplex e) {
  return QEdge.make(e.z0(),e.z1()); }

/** Return the primal <code>QEdges</code>s representing a completed
 * version of the input <code>SimplicialComplex2D</code>.
 * <br>
 * Usually one <code>HEdge</code> is enough to link into the mesh,
 * but multiple may be necessary if the corresponding manifold is
 * not connected.
 */
public final Set<QEdge> build () {
  index();
  final Set<QEdge> qEdges = new HashSet<>();
  // initial HEdge's only carry the origin and reverse links
  for (final DirectedOneSimplex e : reverse().keySet()) {
    // create quartets for interior edges only
    // necessary for LischinskiIncrementalBuilder to correclty
    // distinguish 'inside' vs 'outside' initial frame.
    // TODO: modify LischinskiIncrementalBuilder to work without
    //  an initial frame enclosing all future site insertions!
    if (interior(e)) {
    final DirectedOneSimplex r  = reverse(e);
    if (! (qedge().containsKey(e) || qedge().containsKey(r))) {
      final QEdge q = makeQuartet(e);
      qedge(e,q);
      qedge(r,q.reverse());
      qEdges.add(q); } } }
  // Stitch QEdges together via next links
  for (final DirectedOneSimplex e : reverse().keySet()) {
    final QEdge q = qedge(e);
    assert null != qedge(next(e));
    q.setNext(qedge(next(e)));
    final QEdge r = q.rot();
    assert null != qedge(prev(e));
    final QEdge n = qedge(prev(e)).rot().reverse();
    r.setNext(n); }
  return qEdges; }

//--------------------------------------------------------------------
// construction
//--------------------------------------------------------------------

private QEdgeBuilder (final SimplicialComplex2D cmplx) {
  this._cmplx = cmplx; }

public static final QEdgeBuilder
make (final SimplicialComplex2D cmplx) {
  return new QEdgeBuilder(cmplx); }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
