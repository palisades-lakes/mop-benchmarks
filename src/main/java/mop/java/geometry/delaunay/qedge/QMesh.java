package mop.java.geometry.delaunay.qedge;

import mop.java.geometry.euclidean.VectorD2;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/** Quad Edge representation of a triangle mesh, after
 * <a href="https://www.researchgate.net/publication/262235495_Incremental_Delaunay_Triangulation">
 *  Dani Lischinski
 *  <i>Incremental Delaunay triangulation</i>
 *  <b>Graphics Gems IV</b> Academic Press 1994 </a>
 *  <p>
 *
 *  </p>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-04
 */

public final class QMesh {

  private QEdge _startingEdge;
  final QEdge startingEdge () { return _startingEdge; }
  final void setStartingEdge (final QEdge e) { _startingEdge = e; }

  //--------------------------------------------------------------------

  private final boolean noElements (final Set<VectorD2> exclude,
                                    final VectorD2[] points) {
    for (final VectorD2 p : points) {
      if (exclude.contains(p)) { return false; } }
    return true; }

  //--------------------------------------------------------------------
  /** Collect the vertices as a set of points.
   * Skip elements of <code>exclude</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public final Set<VectorD2> vertices (final Set<VectorD2> exclude) {
    final Set<VectorD2> t = new HashSet<>();
    final Set<QEdge> visited = new HashSet<>();
    final Stack<QEdge> toVisit = new Stack<>();
    toVisit.push(startingEdge());
    while (! toVisit.empty()) {
      final QEdge e0 = toVisit.pop();
      if (! visited.contains(e0)) {
        visited.add(e0);
        toVisit.push(e0.reverse());
        toVisit.push(e0.faceNext());
        final VectorD2 p = e0.origin();
        if (!exclude.contains(p)) { t.add(p); } } }
    return t; }

  public final Set<VectorD2> vertices () {
    return vertices(new HashSet<>()); }

  //--------------------------------------------------------------------
  /** Collect the edges as a set of pairs of coordinates.
   * Skip edges that have any vertices in <code>exclude</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public final Set<VectorD2[]> edges (final Set<VectorD2> exclude) {
    final Set<VectorD2[]> t = new HashSet<>();
    final Set<QEdge> visited = new HashSet<>();
    final Stack<QEdge> toVisit = new Stack<>();
    toVisit.push(startingEdge());
    while (! toVisit.empty()) {
      final QEdge e0 = toVisit.pop();
      if (! visited.contains(e0)) {
        final QEdge e1 = e0.reverse();
        visited.add(e0);
        visited.add(e1);
        toVisit.push(e0.faceNext());
        toVisit.push(e1.faceNext());
        // TODO: sort, preserving orientation, so that same triangle
        //  only returned once.
        final VectorD2[] points = new VectorD2[] {
          e0.origin(), e1.origin(), };
        if (noElements(exclude,points)) { t.add(points); } } }
    return t; }

  //--------------------------------------------------------------------
  /** Collect the triangles as a set of triples of coordinates.
   * Skip triangles that have any vertices in <code>exclude</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public final Set<VectorD2[]> triangles (final Set<VectorD2> exclude) {
    final Set<VectorD2[]> t = new HashSet<>();
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
        // TODO: sort, preserving orientation, so that same triangle
        //  only returned once.
        final VectorD2[] points = new VectorD2[] {
          e0.origin(), e1.origin(), e2.origin(), };
        if (noElements(exclude,points)) { t.add(points); } } }
    return t; }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private QMesh (final QEdge startingEdge) {
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
    e01.splice(e20.reverse());
    e12.splice(e01.reverse());
    e20.splice(e12.reverse());
    return new QMesh(e01); }

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
    e01.splice(e20.reverse());
    e20.reverse().splice(e30.reverse());
    // around p1
    e12.splice(e01.reverse());
    // around p2
    e20.splice(e23);
    e20.splice(e12.reverse());
    // around p3
    e23.reverse().splice(e30);

    return new QMesh(e01); }

//--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------

