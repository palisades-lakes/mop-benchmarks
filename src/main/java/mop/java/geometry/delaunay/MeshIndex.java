package mop.java.geometry.delaunay;

import mop.java.cmplx.DirectedOneSimplex;
import mop.java.cmplx.SimplicialComplex2D;
import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.ZeroSimplex;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Index connections between <code>TwoSimplex</code> faces
 * nad their bounding <code>DirectedOneSimplex</code> edges.
 * Also allow caching vertex neighborhoods.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-11-14
 */
public final class MeshIndex {


  private final SimplicialComplex2D _cmplx;
  private final SimplicialComplex2D cmplx () { return _cmplx; }

  //--------------------------------------------------------------------
  // directed one simplex pairs ~ half edges
  //--------------------------------------------------------------------

  private final Map<DirectedOneSimplex,DirectedOneSimplex>
    _reversed = new HashMap<>();

  private final Map<DirectedOneSimplex,DirectedOneSimplex>
  reversed () { return _reversed; }

  public final DirectedOneSimplex
  getReverse (final DirectedOneSimplex e) {
    assert null != e;
    final DirectedOneSimplex r = reversed().get(e);
    assert null != r;
    return r; }

  private final void putReverse (final DirectedOneSimplex e,
                                 final DirectedOneSimplex r) {
    assert null != e;
    assert null != r;
    assert e.z0().equals(r.z1());
    assert e.z1().equals(r.z0());
    if (reversed().containsKey(e) || reversed().containsKey(r)) {
      assert r.equals(reversed().get(e));
      assert e.equals(reversed().get(r)); }
    else {
      reversed().put(e,r);
      reversed().put(r,e); } }

  public final Set<DirectedOneSimplex> dedges () {
    return reversed().keySet(); }

  // For testing.
  public final int nDEdges () { return dedges().size(); }

  //--------------------------------------------------------------------
  // Face
  //--------------------------------------------------------------------
  // Previous/next half edges within oriented face:

  private final Map<DirectedOneSimplex,DirectedOneSimplex> _facePrev =
    new HashMap<>();

  private final Map<DirectedOneSimplex,DirectedOneSimplex> facePrev () {
    return _facePrev; }

  private final DirectedOneSimplex
  getFacePrev (final DirectedOneSimplex e) {
    assert null != e;
    return facePrev().get(e); }

  private final void putFacePrev (final DirectedOneSimplex e,
                                  final DirectedOneSimplex prev) {
    assert null != e;
    assert ! facePrev().containsKey(e);
    facePrev().put(e,prev); }

  //--------------------------------------------------------------------

  private final Map<DirectedOneSimplex,DirectedOneSimplex> _faceNext =
    new HashMap<>();

  private final Map<DirectedOneSimplex,DirectedOneSimplex> faceNext () {
    return _faceNext; }

  public final DirectedOneSimplex
  getFaceNext (final DirectedOneSimplex e) {
    assert null != e;
    return faceNext().get(e); }

  private final void putFaceNext (final DirectedOneSimplex e,
                                  final DirectedOneSimplex next) {
    assert null != e;
    if (! faceNext().containsKey(e)) { faceNext().put(e,next); }
    else {
      assert Objects.equals(next,getFaceNext(e)) :
        next + "\n" + getFaceNext(e); } }

  //--------------------------------------------------------------------
  // Vertex neighborhood (departing DirectedOneSimplex's)
  //--------------------------------------------------------------------
  // Previous (CW) / next (CCW) half edges around their shared origin
  // vertex:

  private final Map<DirectedOneSimplex,DirectedOneSimplex> _prev =
    new HashMap<>();

  private final Map<DirectedOneSimplex,DirectedOneSimplex> prev () {
    return _prev; }

  public final DirectedOneSimplex getPrev (final DirectedOneSimplex e) {
    assert null != e;
    return prev().get(e); }

  private final void putPrev (final DirectedOneSimplex e,
                              final DirectedOneSimplex prev) {
    assert null != e;
    assert ! prev().containsKey(e);
    assert null != prev;
    prev().put(e,prev); }

  //--------------------------------------------------------------------

  private final Map<DirectedOneSimplex,DirectedOneSimplex> _next =
    new HashMap<>();

  private final Map<DirectedOneSimplex,DirectedOneSimplex> next () {
    return _next; }

  public final DirectedOneSimplex getNext (final DirectedOneSimplex e) {
    assert null != e;
    return next().get(e); }

  private final void putNext (final DirectedOneSimplex e,
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

  private final TwoSimplex getFace (final DirectedOneSimplex e) {
    assert null != e;
    return faces().get(e); }

  private final void putFace (final DirectedOneSimplex e,
                              final TwoSimplex f) {
    assert ! faces().containsKey(e);
    faces().put(e, f); }

  // for testing
  public final int nFaces () { return faces().size() / 3; }

  //--------------------------------------------------------------------
  // edge classification
  //--------------------------------------------------------------------

  public final boolean isBoundary (final DirectedOneSimplex e) {
    assert null != e;
    return null == getFace(e); }

  public final boolean isInterior (final DirectedOneSimplex e) {
    assert null != e;
    return ! isBoundary(e); }

  // for testing

  public final int nBoundary () {
    int n = 0;
    for (final DirectedOneSimplex e : reversed().keySet()) {
      if (isBoundary(e)) { n++; } }
    return n; }

  public final int nInterior () {
    int n = 0;
    for (final DirectedOneSimplex e : reversed().keySet()) {
      if (isInterior(e)) { n++; } }
    return n; }

  //--------------------------------------------------------------------

  private final void addFace (final TwoSimplex f) {
    final ZeroSimplex z0 = f.z0();
    final ZeroSimplex z1 = f.z1();
    final ZeroSimplex z2 = f.z2();
    // edges interior to the face
    final DirectedOneSimplex e01 = DirectedOneSimplex.make(z0,z1);
    final DirectedOneSimplex e12 = DirectedOneSimplex.make(z1,z2);
    final DirectedOneSimplex e20 = DirectedOneSimplex.make(z2,z0);
    putFace(e01, f); putFace(e12, f); putFace(e20, f);
    putFacePrev(e01, e20); putFacePrev(e12, e01); putFacePrev(e20, e12);
    putFaceNext(e01, e12); putFaceNext(e12, e20); putFaceNext(e20, e01);
    // possible boundary
    final DirectedOneSimplex e10 = DirectedOneSimplex.make(z1,z0);
    final DirectedOneSimplex e21 = DirectedOneSimplex.make(z2,z1);
    final DirectedOneSimplex e02 = DirectedOneSimplex.make(z0,z2);
    putReverse(e10, e01); putReverse(e21, e12); putReverse(e02, e20);
  }

  public final void indexFaces () {
    for (final TwoSimplex f : cmplx().faces()) { addFace(f); } }

  //--------------------------------------------------------------------

  private final void boundaryFaceNext (final DirectedOneSimplex e) {
    if (isBoundary(e)) {
      DirectedOneSimplex r = getReverse(getFacePrev(getReverse(e)));
      while (! isBoundary(r)) { r = getReverse(getFacePrev(r)); }
      putFaceNext(e,r); } }

  public final void linkBoundaryFaces () {
    for (final DirectedOneSimplex e : reversed().keySet()) {
      boundaryFaceNext(e); } }

  //--------------------------------------------------------------------
  // cache next and prev for e,
  // using previously cached reverse, faceNext, and facePrev
  // TODO: do getReverse(e) at the same time?

  private final void vertexNeighbors (final DirectedOneSimplex e) {

    // TODO: optimize once working

    if (isInterior(e) && isInterior(getReverse(e))) {
      putNext(e, getReverse(getFacePrev(e)));
      putPrev(e, getFaceNext(getReverse(e)));
      return; }

    if (isInterior(e) && isBoundary(getReverse(e))) {
      putNext(e, getReverse(getFacePrev(e)));
      DirectedOneSimplex p = getReverse(getFacePrev(e));
      while (! isBoundary(p)) { p = getReverse(getFacePrev(p)); }
      putPrev(e, p);
      return; }

    assert isBoundary(e);
    assert isInterior(getReverse(e));
    putPrev(e, getFaceNext(getReverse(e)));
    DirectedOneSimplex n = getFaceNext(getReverse(e));
    while (! isBoundary(getReverse(n))) {
      n = getFaceNext(getReverse(n)); }
    putNext(e,n); }

  public final void linkVertexNeighbors () {
    for (final DirectedOneSimplex e : reversed().keySet()) {
      vertexNeighbors(e); }
  }
//--------------------------------------------------------------------
// construction
//--------------------------------------------------------------------

  private MeshIndex (final SimplicialComplex2D cmplx) {
    this._cmplx = cmplx; }

  public static final MeshIndex
  make (final SimplicialComplex2D cmplx) {
    return new MeshIndex(cmplx); }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
