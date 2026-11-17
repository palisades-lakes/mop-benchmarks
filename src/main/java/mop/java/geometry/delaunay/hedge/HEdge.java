package mop.java.geometry.delaunay.hedge;

import mop.java.cmplx.ZeroSimplex;
import mop.java.geometry.delaunay.Edge;

import java.util.Objects;

/** Half edge version of quad edge based Delaunay triangulation, after
 * <a href="https://www.researchgate.net/publication/262235495_Incremental_Delaunay_Triangulation">
 *  Dani Lischinski
 *  <i>Incremental Delaunay triangulation</i>
 *  <b>Graphics Gems IV</b> Academic Press 1994 </a>
 * <p>
 * Changes from Graphics Gems version:
 * <<ul>
 *   <li>More meaningful names (at least for me).</li>
 *   <li>Delete separate Quartet class holding 4 related edges,
 *   like JTS.</li>
 *   <li>Simplify edge algebra.</li>
 *   <li>Drop dual (rot) edges.</li>
 *   <li>Remove <code>splice</code>, explicit <code>faceNext</code>
 *   updates instead.</li>
 * </ul>
 * </p>
 * Note: <br>
 * One of the strengths/weaknesses of the
 * <code>HEdge</code> representation is that there is no explicit
 * representation for the faces;
 * they are just defined implicitly as a ring of
 * <code>HEdge</code>s;
 * and the implicit faces can be any polygon.
 * This allows the same representation to be used for a triangulation
 * and it dual polygonal mesh (ie Delaunay vs Voronoi).
 * But it means that triangulation code must maintain an implicit
 * constraint that the faces are triangular.
 * <p>
 * <em>NOTE:</em> Not suitable to be used as keys in <code>Map</code>s.
 * Identity <code>hashCode</code> and <code>equals</code>,
 * due to circular <code>rot()</code> and <code>next()</code>
 * references, make it difficult to determine if 2 instances
 * are topologically equivalent.
 * </p>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-11-17
 */
public final class HEdge implements Edge {

  private HEdge _reverse;
  /** The edge from the destination to the origin. */
  @Override
  public final HEdge reverse () { return _reverse; }

  private ZeroSimplex _origin;
  @Override
  public final ZeroSimplex origin () { return _origin; }
//  public final void setOrigin (final ZeroSimplex p) {
//    assert _origin != null;
//    assert Objects.equals(p,facePrev().dest());
//    _origin = p; }
  public final void clearOrigin () {
    assert _origin != null;
    _origin = null; }

  @Override
  public final ZeroSimplex dest () { return reverse().origin(); }

  /** The next edge ccw in the left face. */
  private HEdge _faceNext;

  /** The next edge ccw in the left face. */
  @Override
  public final HEdge faceNext () { return _faceNext; }

  /** The next edge ccw in the left face. */
  public final void setFaceNext (final HEdge e) {
    assert null != e;
    assert Objects.equals(dest(),e.origin()) :
      "\nsetFaceNext:\n" + this +  "\n" + e;
    _faceNext = e; }

  /** The next edge cw in the face.
   * TODO: cache?
   */
  @Override
  public final HEdge facePrev () {
    HEdge p = this;
    for (;;) {
      final HEdge n = p.faceNext();
      if (this.equals(n)) { return p; }
      p = n; } }

  //--------------------------------------------------------------------
  // vertex neighborhood
  //--------------------------------------------------------------------

  /** The next edge ccw around the origin. */
  @Override
  public final HEdge next () { return facePrev().reverse(); }

  /** The next edge cw around the origin. */
  @Override
  public final HEdge prev () { return reverse().faceNext(); }

  //--------------------------------------------------------------------
  // Basic Topological Operators
  //--------------------------------------------------------------------
  /** Break all the links from <code>this</code> pair to any
   * other <code>HEdge</code>.
   * Repair the <code>faceNext</code> links in neighbors.
   */

  @Override
  public final void delete () {
    final HEdge pr = this;
    final HEdge re = reverse();
    final HEdge pn = pr.faceNext();
    final HEdge rn = re.faceNext();
    final HEdge pp = pr.facePrev();
    final HEdge rp = re.facePrev();
    pr.setFaceNext(re);
    re.setFaceNext(pr);
    pp.setFaceNext(rn);
    rp.setFaceNext(pn);
    pr.clearOrigin();
    re.clearOrigin(); }

  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * Add a new edge e connecting
   * the destination of <code>this</code>
   * to the origin of <code>that</code></code>,
   * in such a way that all three have the same
   * left face after the connection is complete.
   * Additionally, the data pointers of the new edge are set.
   */

  @Override
  public final HEdge connect (final Edge that) {
    assert that instanceof HEdge;
    final ZeroSimplex z0 = dest();
    final ZeroSimplex z1 = that.origin();
    assert ! Objects.equals(z0,z1);
    // should ensure no existing edge btwn z0 and z1!
    final HEdge e = make(dest(), that.origin());
    final HEdge r = e.reverse();
    final HEdge n = faceNext();
    final HEdge p = (HEdge) that.facePrev();
    e.setFaceNext((HEdge) that);
    r.setFaceNext(n);
    setFaceNext(e);
    p.setFaceNext(r);
    return e; }

  /** Lischinski: <br>
   * Essentially turns edge e counterclockwise inside its enclosing
   * face (at least quadrilateral).
   * <br>
   * <em>Rewritten to return a new HEdge, to maintain that constraint
   * between <code>next()</code> and <code>origin()</code></em>.
   */

  @Override
  public final HEdge swap () {
    final HEdge n0 = faceNext();
    final HEdge n1 = reverse().faceNext();
    final HEdge nn0 = n0.faceNext();
    final HEdge nn1 = n1.faceNext();
    final HEdge p0 = facePrev();
    final HEdge p1 = reverse().facePrev();
    final HEdge e0 = make(nn1.origin(),nn0.origin());
    final HEdge e1 = e0.reverse();
    delete();
    e0.setFaceNext(nn0);
    n1.setFaceNext(e0);
    p0.setFaceNext(n1);
    e1.setFaceNext(nn1);
    n0.setFaceNext(e1);
    p1.setFaceNext(n0);
    return e0; }

  //--------------------------------------------------------------------
  // Object
  //--------------------------------------------------------------------

  @Override
  public final String toString () {
    return
//      "\n" +
        name()
//        + "\norigin:" + origin()
//        + "\ndest:" + dest()
//        + "\nnext:" + next().name()
//        + "\nprev:" + prev().name()
//        + "\nfaceNext:" + faceNext().name()
//        + "\nfacePrev:" + facePrev().name()
//        + "\nreverse:" + reverse().name()
//        + "\nreverse.next():" + reverse().next().name()
  ; }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // Mutable mesh wired together by setting _next

  private HEdge (final ZeroSimplex origin) { _origin = origin; }

  /** Construct a group of 2 linked <code>HEdge</code>s,
   * returning the one from <code>p0</code> to <code>p1</code>,
   * as a representative for the pair.
   */

  public static final HEdge make (final ZeroSimplex z0,
                                  final ZeroSimplex z1) {

    final HEdge h0 = new HEdge(z0);
    final HEdge h1 = new HEdge(z1);

    h0._reverse = h1;
    h1._reverse = h0;

    h0.setFaceNext(h1);
    h1.setFaceNext(h0);

    return h0; }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
