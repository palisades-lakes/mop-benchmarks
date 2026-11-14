package mop.java.geometry.delaunay.hedge;

import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.ZeroSimplex;

import java.util.*;

/** Delaunay triangulation, after
 * <a href="https://www.researchgate.net/publication/262235495_Incremental_Delaunay_Triangulation">
 *  Dani Lischinski
 *  <i>Incremental Delaunay triangulation</i>
 *  <b>Graphics Gems IV</b> Academic Press 1994 </a>
 * <p>
 * Changes from Graphics Gems version:
 * <<ul>
 *   <li>More meaningful names (at least for me).</li>
 *   <li>Delete separate QuadEdge class holding 4 related edges,
 *   like JTS.</li>
 *   <li>Simplify edge algebra.</li>
 *   <li>Drop dual (rot) edges.</li>
 *   <li>Remove <code>splie</code>, explicit <code>faceNext</code>
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
 * @version 2026-11-10
 */
public final class HEdge implements Comparable<HEdge> {

  // TODO: half edge structure typically have slots:
  //  origin
  //  reverse
  //  faceNExt
  //  face

  private HEdge _reverse;
  /** The edge from the destination to the origin. */
  public final HEdge reverse () { return _reverse; }

  public final String name () {
    return origin() + "->" + dest(); }

  private ZeroSimplex _origin;
  public final ZeroSimplex origin () { return _origin; }
//  public final void setOrigin (final ZeroSimplex p) {
//    assert _origin != null;
//    assert Objects.equals(p,facePrev().dest());
//    _origin = p; }
  public final void clearOrigin () {
    assert _origin != null;
    _origin = null; }

  public final ZeroSimplex dest () { return reverse().origin(); }

  /** The next edge ccw in the left face. */
  private HEdge _faceNext;

  /** The next edge ccw in the left face. */
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
  public final HEdge next () { return facePrev().reverse(); }

  /** The next edge cw around the origin. */
  public final HEdge prev () { return reverse().faceNext(); }

  //--------------------------------------------------------------------
  // Basic Topological Operators
  //--------------------------------------------------------------------
  /** Break all the links from <code>this</code> pair to any
   * other <code>HEdge</code>.
   * Repair the <code>faceNext</code> links in neighbors.
   */

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

//  private final void swapFaceNext (final HEdge that) {
//    final HEdge n0 = this.faceNext();
//    final HEdge n1 = that.faceNext();
//    this.setFaceNext(n1);
//    that.setFaceNext(n0); }

  //--------------------------------------------------------------------
  // https://github.com/MeshInspector/MeshLib#readme
  // https://www.ic.unicamp.br/~stolfi/voynich/projects/voynich/Notes/053/work/programs/c/JSLIBS/libquad/haf.h
  // https://www.ic.unicamp.br/~stolfi/voynich/projects/voynich/Notes/053/work/programs/c/JSLIBS/libquad/haf.c
  // https://github.com/JorgeStolfi/JSLIBS
//  /** Lischinski: <br>
//   * This operator affects the two edge rings around the origins
//   * of a and b, and, independently, the two edge rings around
//   * the left faces of a and b. In each case,
//   * <ol>
//   * <li> if the two rings are distinct, Splice will combine
//   * them into one;
//   * </li>
//   * <li> if the two are the same ring, Splice will break it
//   * into two separate pieces.
//   * </li>
//   * </ol>
//   * Thus, splice can be used both to attach the two edges together,
//   * and to break them apart. <br>
//   * See Guibas and Stolfi (1985) p.96 for more details
//   * and illustrations.
//   */
//
//  public final void splice (final HEdge that) {
//    swapFaceNext(that);
//    reverse().swapFaceNext(that.reverse());
//  }

  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * Add a new edge e connecting
   * the destination of <code>this</code>
   * to the origin of <code>that</code></code>,
   * in such a way that all three have the same
   * left face after the connection is complete.
   * Additionally, the data pointers of the new edge are set.
   */

  public final HEdge connect (final HEdge that) {
    final ZeroSimplex z0 = dest();
    final ZeroSimplex z1 = that.origin();
    assert ! Objects.equals(z0,z1);
    // should ensure no existing edge btwn z0 and z1!
    final HEdge e = make(dest(), that.origin());
    final HEdge r = e.reverse();
    final HEdge n = faceNext();
    final HEdge p = that.facePrev();
    e.setFaceNext(that);
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
  // collect mesh elements
  //--------------------------------------------------------------------

// --Commented out by Inspection START (10/25/2026 14:09):
//  private final boolean noElements (final Set<ZeroSimplex> exclude,
//                                    final ZeroSimplex[] points) {
//    for (final ZeroSimplex p : points) {
//      if (exclude.contains(p)) { return false; } }
//    return true; }
// --Commented out by Inspection STOP (10/25/2026 14:09)

  //--------------------------------------------------------------------
  /** Collect the vertices as a set of points.
   * Skip elements of <code>exclude</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public final SortedSet<ZeroSimplex>
  zeroSimplexes (final Set<ZeroSimplex> exclude) {
    final SortedSet<ZeroSimplex> t = new TreeSet<>();
    final Set<HEdge> visited = new HashSet<>();
    final Stack<HEdge> toVisit = new Stack<>();
    toVisit.push(this);
    while (! toVisit.empty()) {
      final HEdge e0 = toVisit.pop();
      if (! visited.contains(e0)) {
        visited.add(e0);
        toVisit.push(e0.reverse());
        toVisit.push(e0.faceNext());
        final ZeroSimplex p = e0.origin();
        if (!exclude.contains(p)) { t.add(p); } } }
    return t; }

  public final SortedSet<ZeroSimplex> zeroSimplexes () {
    return zeroSimplexes(new TreeSet<>()); }

//  /** Collect the edges as a set of pairs of coordinates.
//   * Skip edges that have any vertices in <code>exclude</code>.
//   * <b>Assumes mesh is connected!</b>
//   */
//
//  public final SortedSet<ZeroSimplex[]> edges (final Set<ZeroSimplex> exclude) {
//    final Set<ZeroSimplex[]> t = new HashSet<>();
//    final Set<HEdge> visited = new HashSet<>();
//    final Stack<HEdge> toVisit = new Stack<>();
//    toVisit.push(this);
//    while (! toVisit.empty()) {
//      final HEdge e0 = toVisit.pop();
//      if (! visited.contains(e0)) {
//        final HEdge e1 = e0.reverse();
//        visited.add(e0);
//        visited.add(e1);
//        toVisit.push(e0.faceNext());
//        toVisit.push(e1.faceNext());
//        // TODO: sort, preserving orientation, so that same edge
//        //  only returned once.
//        final ZeroSimplex[] points = new ZeroSimplex[] {
//          e0.origin(), e1.origin(), };
//        Arrays.sort(points);
//        if (noElements(exclude,points)) { t.add(points); } } }
//    return t; }

  /** Collect connected triangular faces, skip others. */
  public final SortedSet<TwoSimplex> twoSimplexes () {
    validateMesh();
    final SortedSet<TwoSimplex> faces = new TreeSet<>();
    final Set<HEdge> visited = new HashSet<>();
    final Stack<HEdge> toVisit = new Stack<>();
    toVisit.push(this);
    while (! toVisit.empty()) {
      final HEdge e0 = toVisit.pop();
      if (! visited.contains(e0)) {
        visited.add(e0);
        final HEdge e1 = e0.faceNext(); visited.add(e1);
        final HEdge e2 = e1.faceNext(); visited.add(e2);
        assert ! (e0.equals(e1) || e0.equals(e2) || (e1.equals(e2)));
        toVisit.push(e0.reverse());
        toVisit.push(e1.reverse());
        toVisit.push(e2.reverse());
        HEdge e3 = e2.faceNext();
        if (e0.equals(e3)) { // triangular
          final ZeroSimplex z0 = e0.origin();
          final ZeroSimplex z1 = e1.origin();
          final ZeroSimplex z2 = e2.origin();
          assert (! z0.equals(z1)) :
            "e0 <> e1" + e0 + "\n" + e1
            + "\n" + z0 + "<>" + z1;
          assert ! z1.equals(z2) : "e1 <> e2" + e1 + "\n" + e2;
          assert ! z2.equals(z0) : "e2 <> e0" + e2 + "\n" + e0;
          final TwoSimplex face = TwoSimplex.make(z0,z1,z2);
          faces.add(face); }
        else {
          while (! e0.equals(e3)) {
            visited.add(e3); toVisit.push(e3.reverse());
            e3 = e3.faceNext(); } } } }
    return faces; }

  //--------------------------------------------------------------------
  // Comparable
  //--------------------------------------------------------------------

  @Override
  public int compareTo (final HEdge h) {
    final int c = origin().compareTo(h.origin());
    if (0 != c) { return c; }
    return dest().compareTo(h.dest()); }

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
  // debugging
  //--------------------------------------------------------------------


  public final void validate () {
    assert Objects.equals(origin(),next().origin());
    assert Objects.equals(origin(),prev().origin());
    assert Objects.equals(origin(),facePrev().dest());
    assert Objects.equals(dest(),faceNext().origin());
    assert Objects.equals(this,reverse().reverse()); }

  public final void validateMesh () {
    final Set<HEdge> visited = new HashSet<>();
    final Stack<HEdge> toVisit = new Stack<>();
    toVisit.push(this);
    while (! toVisit.empty()) {
      final HEdge e = toVisit.pop();
      if (! visited.contains(e)) {
        e.validate();
        visited.add(e);
        toVisit.push(e.next());
        toVisit.push(e.reverse());
      }}
  }
  public static final void printFace (final Set<HEdge> visited,
                                      final Stack<HEdge> toVisit) {
    final HEdge e0 = toVisit.pop();
    if ((null!=e0) && (! visited.contains(e0))) {
      System.out.println("face:" );
      HEdge e1 = e0;
      do {
        toVisit.remove(e1);
        if (! visited.contains(e1)) {
          System.out.println(e1);
          visited.add(e1);
          final HEdge ri = e1.reverse();
          if (! visited.contains(ri)) { toVisit.push(ri); } }
        e1 = e1.faceNext();
        assert null != e1;
      } while (e0 != e1); } }

  public final void printMesh () {
    final Set<HEdge> visited = new HashSet<>();
    final Stack<HEdge> toVisit = new Stack<>();
    toVisit.push(this);
    while (!toVisit.empty()) { printFace(visited,toVisit); } }

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
