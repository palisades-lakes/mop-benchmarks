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
 * @version 2026-10-31
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
    final DirectedOneSimplex r = reverse().get(e);
    assert null != r;
    return r; }

  private final void addReverse (final DirectedOneSimplex e) {
    final DirectedOneSimplex r = DirectedOneSimplex.make(e.z1(),e.z0());
    if (reverse().containsKey(e)) {
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

  private final void facePrev (final DirectedOneSimplex e,
                               final DirectedOneSimplex prev) {
    assert ! facePrev().containsKey(e);
    facePrev().put(e,prev); }

  private final Map<DirectedOneSimplex,DirectedOneSimplex> _faceNext =
    new HashMap<>();

  private final Map<DirectedOneSimplex,DirectedOneSimplex> faceNext () {
    return _faceNext; }

  private final DirectedOneSimplex
  faceNext (final DirectedOneSimplex e) { return faceNext().get(e); }

  private final void faceNext (final DirectedOneSimplex e,
                               final DirectedOneSimplex next) {
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
  prev (final DirectedOneSimplex e) { return prev().get(e); }

  private final void prev (final DirectedOneSimplex e,
                           final DirectedOneSimplex prev) {
    assert ! prev().containsKey(e);
    assert null != prev;
    prev().put(e,prev); }

  private final Map<DirectedOneSimplex,DirectedOneSimplex> _next =
    new HashMap<>();

  private final Map<DirectedOneSimplex,DirectedOneSimplex> next () {
    return _next; }

  private final DirectedOneSimplex
  next (final DirectedOneSimplex e) { return next().get(e); }

  private final void next (final DirectedOneSimplex e,
                           final DirectedOneSimplex next) {
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
    return null == face(e); }

  private final boolean interior (final DirectedOneSimplex e) {
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
    final DirectedOneSimplex e01 = DirectedOneSimplex.make(z0,z1);
    final DirectedOneSimplex e12 = DirectedOneSimplex.make(z1,z2);
    final DirectedOneSimplex e20 = DirectedOneSimplex.make(z2,z0);
    addReverse(e01); addReverse(e12); addReverse(e20);
    face(e01,f); face(e12, f); face(e20,f);
    facePrev(e01,e20); facePrev(e12,e01); facePrev(e20,e12);
    faceNext(e01,e12); faceNext(e12,e20); faceNext(e20,e01); }

  private final void primalNeighbors (final DirectedOneSimplex e) {
    final DirectedOneSimplex r = reverse(e);
    if (interior(e)) {
      DirectedOneSimplex n = reverse(facePrev(e));
      next(e,n);
      if (interior(r)) { prev(e,faceNext(r)); }
      else if (boundary(r)) {
        while (! boundary(n)) { n = reverse(facePrev(n)); }
        prev(e,n); } }
    else {
      assert boundary(e);
      assert interior(r);
      DirectedOneSimplex p = faceNext(r);
      prev(e,p);
      while (! boundary(p)) { p = faceNext(reverse(p)); }
      next(e,p); } }

  //--------------------------------------------------------------------

  private final void index () {
    for (final TwoSimplex f : cmplx().faces()) { addFace(f); }
    // stitch primal QEdges
    for (final DirectedOneSimplex e : reverse().keySet()) {
      primalNeighbors(e); } }

  //--------------------------------------------------------------------
  // map DirectedOneSimplex's to corresponding primal QEdges

  private final Map<DirectedOneSimplex,QEdge> _qedge = new HashMap<>();
  private final Map<DirectedOneSimplex,QEdge> qedge () { return _qedge; }
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
    return QEdge.make(e.toString(),e.z0(),e.z1()); }

  /** Return the primal <code>QEdges</code>s representing a completed
   * version of the input <code>SimplicialComplex2D</code>.
   * <br>
   * Usually one <code>QEdge</code> is enough,
   * but multiple may be necessary if the corresponding manifold is
   * not connected.
   */
  public final Set<QEdge>
  build () {
    index();
    final Set<QEdge> qEdges = new HashSet<>();
    // initial QEdge's only carry the origin and reverse links
    for (final DirectedOneSimplex e : reverse().keySet()) {
      final DirectedOneSimplex r  = reverse(e);
      if (! (qedge().containsKey(e) || qedge().containsKey(r))) {
        final QEdge q = makeQuartet(e);
        qedge(e,q);
        qedge(r,q.reverse());
        qEdges.add(q); } }
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
