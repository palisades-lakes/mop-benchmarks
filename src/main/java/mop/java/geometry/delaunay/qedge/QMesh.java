package mop.java.geometry.delaunay.qedge;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import mop.java.geometry.euclidean.VectorD2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/** Delaunay triangulation, after
 * <a href="https://www.researchgate.net/publication/262235495_Incremental_Delaunay_Triangulation">
 *  Dani Lischinski
 *  <i>Incremental Delaunay triangulation</i>
 *  <b>Graphics Gems IV</b> Academic Press 1994 </a>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-04
 */

public final class QMesh {

  private QEdge _startingEdge;
  private final QEdge startingEdge () { return _startingEdge; }
  private final void setStartingEdge (final QEdge e) {
    _startingEdge = e; }

  private final Set<VectorD2> _framePoints;
  private final Set<VectorD2> framePoints () { return _framePoints; }
  private final boolean noFramePoints (final VectorD2 p0,
                                       final VectorD2 p1,
                                       final VectorD2 p2) {
    return
      ! (framePoints().contains(p0)
        || framePoints().contains(p1)
        || framePoints().contains(p2)); }

   //--------------------------------------------------------------------
  // Basic Topological Operators
  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * This operator affects the two edge rings around the origins
   * of a and b, and, independently, the two edge rings around
   * the left faces of a and b. In each case,
   * <ol>
   * <li> if the two rings are distinct, Splice will combine
   * them into one;
   * </li>
   * <li> if the two are the same ring, Splice will break it
   * into two separate pieces.
   * </li>
   * </ol>
   * Thus, splice can be used both to attach the two edges together,
   * and to break them apart. <br>
   * See Guibas and Stolfi (1985) p.96 for more details
   * and illustrations.
   */
  private static final void splice (final QEdge a,
                                    final QEdge b) {
    final QEdge alpha = a.next().dual();
    final QEdge beta = b.next().dual();
    final QEdge t1 = b.next();
    final QEdge t2 = a.next();
    final QEdge t3 = beta.next();
    final QEdge t4 = alpha.next();
    a.setNext(t1);
    b.setNext(t2);
    alpha.setNext(t3);
    beta.setNext(t4); }

  //--------------------------------------------------------------------

  private static final void deleteEdge (final QEdge e) {
    splice(e, e.prev());
    splice(e.reverse(), e.reverse().prev()); }

  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * Add a new edge e connecting the destination of <code>e0</code> to the
   * origin of <code>e1</code></code>,
   * in such a way that all three have the same
   * left face after the connection is complete.
   * Additionally, the data pointers of the new edge are set.
   */

  private static final QEdge connect (final QEdge e0,
                                      final QEdge e1) {
    final QEdge e = QEdge.make(e0.reverse().origin(), e1.origin());
    splice(e, e0.faceNext());
    splice(e.reverse(), e1);
    return e; }

  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * Essentially turns edge e counterclockwise inside its enclosing
   * quadrilateral. The data pointers are modified accordingly.
   */

  private static final void swap (final QEdge e) {
    // TODO: can we replace with a new edge, rather than modifying?
    final QEdge a = e.prev();
    final QEdge b = e.reverse().prev();
    splice(e, a);
    splice(e.reverse(), b);
    splice(e, a.faceNext());
    splice(e.reverse(), b.faceNext());
    e.setOrigin(a.reverse().origin());
    e.reverse().setOrigin(b.reverse().origin()); }

  //--------------------------------------------------------------------
  // geometric predicates
  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * Returns twice the signed area of the oriented triangle (a, b, c),
   * i.e., the area is positive if the triangle is counterclockwise.
   */

  private static final double signedArea (final VectorD2 a,
                                          final VectorD2 b,
                                          final VectorD2 c) {

    return
      (b.x() - a.x())*(c.y() - a.y())
        -
        (b.y() - a.y())*(c.x() - a.x()); }

  /** Lischinski: <br>
   * Returns true if the point d is inside the circle defined by the
   * points a, b, c. See Guibas and Stolfi (1985) p.107.
   */

  private static final boolean inCircle (final VectorD2 a,
                                         final VectorD2 b,
                                         final VectorD2 c,
                                         final VectorD2 d) {

    return
      ((a.x()*a.x() + a.y()*a.y()) * signedArea(b, c, d) -
        (b.x()*b.x() + b.y()*b.y()) * signedArea(a, c, d) +
        (c.x()*c.x() + c.y()*c.y()) * signedArea(a, b, d) -
        (d.x()*d.x() + d.y()*d.y()) * signedArea(a, b, c))
        > 0.0; }

  /** Are a, b, c counterclockwise? */
  private static final boolean ccw (final VectorD2 a,
                                    final VectorD2 b,
                                    final VectorD2 c) {
    return (signedArea(a, b, c) > 0); }

  private static final boolean rightOf (final VectorD2 x,
                                        final QEdge e) {
    return ccw(x, e.reverse().origin(), e.origin()); }

  private static final boolean leftOf (final VectorD2 x,
                                       final  QEdge e) {
    return ccw(x, e.origin(), e.reverse().origin()); }

  private static final double EPS = 1.0e-6;
  private static final double EPS2 = EPS*EPS;

  //--------------------------------------------------------------------
  /** Used to test for point on line */

  private static final class Line {

    private final double a;
    private final double b;
    private final double c;

    final double eval (final VectorD2 p) {
      // Plugs point p into the line equation.
      return (a * p.x() + b * p.y() + c); }

    Line (final VectorD2 p, final VectorD2 q) {
      final VectorD2 t = q.subtract(p);
      final double len = Math.sqrt(t.l2norm2());
      a =   t.y() / len;
      b = - t.x() / len;
      c = -(a*p.x() + b*p.y()); } }

  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * A predicate that determines if the point x is on the edge e.
   * The point is considered on if it is in the EPS-neighborhood
   * of the edge.
   * <br>
   * NOTE: Lischinski uses norm rather than norm2.
   * This requires a sqrt call, costing time and adding rounding error
   */

  private static final boolean onEdge (final VectorD2 x,
                                       final QEdge e) {


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
   */

  private final QEdge locate (final VectorD2 x) {

    QEdge e = startingEdge();
    while (true) {
      if (x.equals(e.origin())) { return e; }
      if (x.equals(e.reverse().origin())) { return e; }

      if (rightOf(x, e)) { e = e.reverse(); }
      else if (! rightOf(x, e.next())) { e = e.next(); }
      else if (! leftOf(x, e.faceNext())) {
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

  public final void insertSite (final VectorD2 x) {

    QEdge e = locate(x);
    // point is already in QMesh
    if ((x == e.origin()) || (x == e.reverse().origin()))  { return; }
    else if (onEdge(x, e)) {
      e = e.prev();
      deleteEdge(e.next()); }
    // Connect the new point to the vertices of the containing
    // triangle (or quadrilateral, if the new point fell on an
    // existing edge.)
    QEdge base = QEdge.make(e.origin(), x);
    splice(base, e);
    setStartingEdge(base);
    do {
      base = connect(e, base.reverse());
      e = base.prev();
    } while (e.faceNext() != startingEdge());
    // Examine suspect edges to ensure that the Delaunay condition
    // is satisfied.
    do {
      QEdge t = e.prev();
      if (rightOf(t.reverse().origin(), e) &&
        inCircle(e.origin(),
                 t.reverse().origin(),
                 e.reverse().origin(),
                 x)) {
        swap(e);
        e = e.prev(); }
      // no more suspect edges
      else if (e.next() == startingEdge()) { return; }
      // pop a suspect edge
      else { e = e.next().next().reverse(); }
    } while (true); }

  //--------------------------------------------------------------------

  /** JFX triangle, */

  public static final Shape jfxTriangle (final VectorD2 p0,
                                         final VectorD2 p1,
                                         final VectorD2 p2,
                                         final Color color) {
    final Shape t = new Polyline(p0.x(), p0.y(),
                                 p1.x(), p1.y(),
                                 p2.x(), p2.y());
    t.setStroke(color);
    t.setStrokeWidth(1);
    t.setStrokeType(StrokeType.CENTERED);
    t.setStrokeLineCap(StrokeLineCap.ROUND);
    t.setStrokeLineJoin(StrokeLineJoin.ROUND);
    t.setStrokeMiterLimit(1.0);
    return t; }

  //--------------------------------------------------------------------
  private static final Color FRAME_COLOR = Color.web("#AA000088");
  private static final Color MESH_COLOR = Color.web("#0000AAFF");
  /** Collect the triangles as a JFX Group Node for display.
   * Skip triangles that have any vertices in <code>frame</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public final Group jfxTriangles (final boolean withFrame,
                                   final String id) {
    final Group group = new Group();
    final List<Node> children = group.getChildren();

    final Set<QEdge> visited = new HashSet<>();
    final Stack<QEdge> toVisit = new Stack<>();
    toVisit.push(startingEdge());
    while (! toVisit.empty()) {
      final QEdge e0 = toVisit.pop();
      if (! visited.contains(e0)) {
        visited.add(e0);
        final QEdge e1 = e0.faceNext();
        final QEdge e2 = e1.faceNext();
        toVisit.push(e0.reverse());
        toVisit.push(e1.reverse());
        toVisit.push(e2.reverse());
        final VectorD2 p0 = e0.origin();
        final VectorD2 p1 = e1.origin();
        final VectorD2 p2 = e2.origin();
        if (noFramePoints(p0,p1,p2)) {
          children.add(jfxTriangle(p0,p1,p2,MESH_COLOR)); }
        else if (withFrame) {
          children.add(jfxTriangle(p0,p1,p2,FRAME_COLOR)); } } }
    group.setId(id);
    return group; }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  /** All inserted points must lie in the initial mesh
   * (entered from <code>startingEdge</code>, created from
   * the <code>framePoints.</code>
   */
  private QMesh (final Set<VectorD2> framePoints,
                 final QEdge startingEdge) {
    _framePoints = framePoints;
    _startingEdge = startingEdge; }

  /** All inserted points must lie in the triangle formed by
   * <code>p0, p1, p2</code>.
   */

  public static final QMesh
  triangleFrame (final VectorD2 p0,
                 final VectorD2 p1,
                 final VectorD2 p2) {
    final QEdge e01 = QEdge.make(p0, p1);
    final QEdge e12 = QEdge.make(p1, p2);
    final QEdge e20 = QEdge.make(p2, p0);
    splice(e01,e20.reverse());
    splice(e12,e01.reverse());
    splice(e20,e12.reverse());
    return new QMesh(Set.of(p0, p1, p2), e01); }

  /** All inserted points must lie in the triangle formed by
   * <code>p0, p1, p2</code>.
   */

  public static final QMesh
  rectangleFrame (final double xmin,
                  final double xmax,
                  final double ymin,
                  final double ymax) {
    final VectorD2 p0 = new VectorD2(xmin,ymin);
    final VectorD2 p1 = new VectorD2(xmax,ymin);
    final VectorD2 p2 = new VectorD2(xmax,ymax);
    final VectorD2 p3 = new VectorD2(xmin,ymax);
    final QEdge e01 = QEdge.make(p0, p1);
    final QEdge e12 = QEdge.make(p1, p2);
    final QEdge e20 = QEdge.make(p2, p0);
    final QEdge e23 = QEdge.make(p2, p3);
    final QEdge e30 = QEdge.make(p3, p0);
    // around p0
    splice(e01,e20.reverse());
    splice(e20.reverse(),e30.reverse());
    // around p1
    splice(e12,e01.reverse());
    // around p2
    splice(e20,e23);
    splice(e20,e12.reverse());
    // around p3
    splice(e23.reverse(),e30);

    return new QMesh(Set.of(p0, p1, p2, p3), e01); }

//--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------

