package mop.java.geometry.delaunay.hedge;

import mop.java.cmplx.DirectedOneSimplex;
import mop.java.cmplx.SimplicialComplex2D;
import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.ZeroSimplex;

import java.util.*;

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
 * @version 2026-11-10
 */
public final class HEdgeBuilder {

  // TODO: is there a benefit to using sorted set/maps?

  private final SimplicialComplex2D _cmplx;
  private final SimplicialComplex2D cmplx () { return _cmplx; }

  //--------------------------------------------------------------------
  // directed one simplex pairs == half edges
  //--------------------------------------------------------------------

  private final Map<DirectedOneSimplex,DirectedOneSimplex>
    _reverse = new HashMap<>();

  private final Map<DirectedOneSimplex,DirectedOneSimplex>
  reverse () { return _reverse; }

  private final DirectedOneSimplex
  getReverse (final DirectedOneSimplex e) {
    assert null != e;
    final DirectedOneSimplex r = reverse().get(e);
    assert null != r;
    return r; }

  private final void putReverse (final DirectedOneSimplex e,
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

  private final DirectedOneSimplex
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

//  //--------------------------------------------------------------------
//  // Previous (CW) / next (CCW) half edges around their shared origin
//  // vertex:
//
//  private final Map<DirectedOneSimplex,DirectedOneSimplex> _prev =
//    new HashMap<>();
//
//  private final Map<DirectedOneSimplex,DirectedOneSimplex> prev () {
//    return _prev; }
//
//  private final DirectedOneSimplex getPrev (final DirectedOneSimplex e) {
//    assert null != e;
//    return prev().get(e); }
//
//  private final void setPrev (final DirectedOneSimplex e,
//                              final DirectedOneSimplex prev) {
//    assert null != e;
//    assert ! prev().containsKey(e);
//    assert null != prev;
//    prev().put(e,prev); }
//
//  //--------------------------------------------------------------------
//
//  private final Map<DirectedOneSimplex,DirectedOneSimplex> _next =
//    new HashMap<>();
//
//  private final Map<DirectedOneSimplex,DirectedOneSimplex> next () {
//    return _next; }
//
//  private final DirectedOneSimplex getNext (final DirectedOneSimplex e) {
//    assert null != e;
//    return next().get(e); }
//
//  private final void setNext (final DirectedOneSimplex e,
//                              final DirectedOneSimplex next) {
//    assert null != e;
//    assert ! next().containsKey(e);
//    assert null != next;
//    next().put(e,next); }
//
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

  private final boolean isBoundary (final DirectedOneSimplex e) {
    assert null != e;
    return null == getFace(e); }

  private final boolean isInterior (final DirectedOneSimplex e) {
    assert null != e;
    return ! isBoundary(e); }

  // for testing

  public final int nBoundary () {
    int n = 0;
    for (final DirectedOneSimplex e : reverse().keySet()) {
      if (isBoundary(e)) { n++; } }
    return n; }

  public final int nInterior () {
    int n = 0;
    for (final DirectedOneSimplex e : reverse().keySet()) {
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

  private final void boundaryFaceNext (final DirectedOneSimplex e) {
    if (isBoundary(e)) {
      DirectedOneSimplex r = getReverse(getFacePrev(getReverse(e)));
      while (! isBoundary(r)) { r = getReverse(getFacePrev(r)); }
      putFaceNext(e,r); } }

//--------------------------------------------------------------------

  private final void index () {
    for (final TwoSimplex f : cmplx().faces()) { addFace(f); }
    for (final DirectedOneSimplex e : reverse().keySet()) {
      boundaryFaceNext(e); } }

//--------------------------------------------------------------------
// map DirectedOneSimplex's to corresponding primal HEdges

  private final Map<DirectedOneSimplex, HEdge> _hedge = new HashMap<>();
  private final Map<DirectedOneSimplex, HEdge> hedge () {
    return _hedge; }
  private final HEdge getHEdge (final DirectedOneSimplex e) {
    return hedge().get(e); }
  private final void putHEdge (final DirectedOneSimplex e,
                               final HEdge h) {
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
    index();
    final Set<HEdge> hEdges = new HashSet<>();
    // set origin and reverse (and dest=reverse.origin)
    // for interior HEdges
    for (final DirectedOneSimplex e : faces().keySet()) {
      final DirectedOneSimplex r  = getReverse(e);
      if (! (hedge().containsKey(e) || hedge().containsKey(r))) {
        final HEdge h = makePair(e);
        putHEdge(e, h);
        putHEdge(r, h.reverse());
        hEdges.add(h); } }
    // set faceNext for all HEdges
    for (final DirectedOneSimplex e : hedge().keySet()) {
      final HEdge h = getHEdge(e);
      assert null != getHEdge(getFaceNext(e));
      try {
        h.setFaceNext(getHEdge(getFaceNext(e)));
      }
      catch (final Throwable t) {
        System.out.println(cmplx());
        throw t;
      }
    }
    for (final HEdge h : hEdges) {
      h.validate(); }
    return hEdges; }

//--------------------------------------------------------------------
// construction
//--------------------------------------------------------------------

  private HEdgeBuilder (final SimplicialComplex2D cmplx) {
    this._cmplx = cmplx; }

  public static final HEdgeBuilder
  make (final SimplicialComplex2D cmplx) {
    return new HEdgeBuilder(cmplx); }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
