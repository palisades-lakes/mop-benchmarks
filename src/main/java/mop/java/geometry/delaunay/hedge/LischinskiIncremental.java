package mop.java.geometry.delaunay.hedge;

import mop.java.geometry.delaunay.Util;
import mop.java.geometry.euclidean.VectorD2;

/** Algorithm for Delaunay triangulation, after
 * <a href="https://www.researchgate.net/publication/262235495_Incremental_Delaunay_Triangulation">
 *  Dani Lischinski
 *  <i>Incremental Delaunay triangulation</i>
 *  <b>Graphics Gems IV</b> Academic Press 1994 </a>,
 *  converted to half-edge representation.
 * <p>
 * <b>NOTE: </b> all inserted points must fall within the existing mesh!
 * </p>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-24
 */

public final class LischinskiIncremental {

  //--------------------------------------------------------------------
  /** Used to test for point on line. */

  private static final class Line {

    private final double a;
    private final double b;
    private final double c;

    private final double eval (final VectorD2 p) {
      // Plugs point p into the line equation.
      return (a * p.x() + b * p.y() + c); }

    Line (final VectorD2 p, final VectorD2 q) {
      final VectorD2 t = q.subtract(p);
      final double len = Math.sqrt(t.l2norm2());
      a =   t.y() / len;
      b = - t.x() / len;
      c = -(a*p.x() + b*p.y()); } }

  //--------------------------------------------------------------------
  private static final double EPS = 1.0e-6;
  private static final double EPS2 = EPS*EPS;

  /** Lischinski: <br>
   * A predicate that determines if the point x is on the edge e.
   * The point is considered on if it is in the EPS-neighborhood
   * of the edge.
   * <br>
   * NOTE: Lischinski uses norm rather than norm2.
   * This requires a sqrt call, costing time and adding rounding error
   */

  private static final boolean onEdge (final VectorD2 x,
                                       final HEdge e) {
    final double t1 = x.subtract(e.origin()).l2norm2();
    final double t2 = x.subtract(e.reverse().origin()).l2norm2();
    if (t1 < EPS2 || t2 < EPS2) { return true; }
    final double t3 = e.origin().subtract(e.reverse().origin()).l2norm2();
    if (t1 > t3 || t2 > t3) { return false; }
    final Line line = new Line(e.origin(), e.reverse().origin());
    return (Math.abs(line.eval(x)) < EPS); }

  //--------------------------------------------------------------------
  // Incremental delaunay
  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * Returns an edge e, s.t. either x is on e, or e is an edge of
   * a triangle containing x. The search starts from startingEdge
   * and proceeds in the general direction of x. Based on the
   * pseudocode in Guibas and Stolfi (1985) p.121.
   * <br>
   * TODO: fix infinite loops!
   * <br>
   * See <a href="https://people.eecs.berkeley.edu/~jrs/meshpapers/GSflaws">
   * Shewchuk's GSFlaws:</a>
   * <pre>
   * Three fixes to Guibas and Stolfi's "Primitives for the Manipulation of
   * General Subdivisions and the Computation of Voronoi Diagrams":
   *
   * One, their Locate primitive always works in the interior of the mesh,
   * but can get stuck in an infinite loop on the convex hull.  Consider
   * this mesh:
   *                                y       z
   *                                o-------o
   *                               / \     / \
   *                              /   \   /   \
   *                             /     \ /     \
   *                            o--X----o-------o
   *                            u       v       w
   *
   * Suppose you're trying to locate the X, which is collinear with uvw.
   * Look at p. 121 of Guibas & Stolfi.  You are searching with an edge
   * e for the edge uv.  Suppose that e := wv (directed toward v).
   * RightOf[X, e] is false.  NOT RightOf[X, e.Onext] is true, so we
   * execute "e <- e.Onext", which sets e to wz.  On the next iteration,
   * we execute "e <- e.Onext", which sets e back to wv.  This continues
   * forever, an infinite loop.
   *
   * To fix this bug, changing both "Not RightOf" expressions to "LeftOf"
   * expressions is a good start, but doesn't fulfill the criterion that
   * "Locate returns an edge e of the current Delaunay diagram such that
   * the given point X is either on e or _strictly_ inside the left face
   * of e."  Rather, X might be on any edge of the left face; X might
   * even be the apex vertex.  An explicit test for these possibilities
   * is necessary.
   *
   * Two, I think there are other places in the paper where Guibas &
   * Stolfi sometime use RightOf when they mean NOT LeftOf, and vice
   * versa.  Give careful consideration in each case to what should
   * happen when the determinant is zero.
   *
   * Three, on p. 120, in their InsertSite procedure, the fifth-last line
   * is wrong.  The statement "e <- t" should read "e <- e.Oprev".
   *
   * If you're not using exact arithmetic for the CCW and InCircle tests,
   * other things can go wrong as well.  Some problem cases can be
   * avoided by changing the code a bit, and some can't.
   * </pre>
   */

  private static final HEdge locate (final HMesh mesh,
                                     final VectorD2 x) {
    HEdge e = mesh.startingEdge();
    while (true) {
      if (x.equals(e.origin())) { return e; }
      if (x.equals(e.reverse().origin())) { return e; }

      if (e.isRight(x)) { e = e.reverse(); }
      else if (! e.next().isRight(x)) { e = e.next(); }
      else if (! e.faceNext().isLeft(x)) {
        e = e.faceNext().reverse(); }
      else { return e; } } }

  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * Inserts a new point into a subdivision representing a Delaunay
   * triangulation, and fixes the affected edges so that the result
   * is still a Delaunay triangulation. This is based on the
   * pseudocode from Guibas and Stolfi (1985) p.120, with slight
   * modifications and a bug fix.
   */

  public static final void insert (final HMesh mesh,
                                       final VectorD2 x) {

    HEdge e = locate(mesh, x);
    // point is already in QMesh
    if ((x.equals(e.origin())) || (x.equals(e.reverse().origin())))  {
      return; }
    else if (onEdge(x, e)) { e = e.prev(); e.next().delete(); }
    // Connect the new point to the vertices of the containing
    // triangle (or quadrilateral, if the new point fell on an
    // existing edge.)
    HEdge base = HEdge.make(e.origin(), x);
    base.splice(e);
    mesh.setStartingEdge(base);
    do {
      base = e.connect(base.reverse());
      e = base.prev();
    } while (e.faceNext() != mesh.startingEdge());
    // Examine suspect edges to ensure that the Delaunay condition
    // is satisfied.
    do {
      HEdge t = e.prev();
      if (e.isRight(t.reverse().origin()) &&
        Util.inCircle(e.origin(),
                      t.reverse().origin(),
                      e.reverse().origin(),
                      x)) {
        e.swap();
        e = e.prev(); }
      // no more suspect edges
      else if (e.next() == mesh.startingEdge()) { return; }
      // pop a suspect edge
      else { e = e.next().next().reverse(); }
    } while (true); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  /** disabled constructor, class methods only. */

  private LischinskiIncremental () {
    throw new UnsupportedOperationException(
      getClass().getSimpleName() + ": construction not allowed."); }

//--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------

