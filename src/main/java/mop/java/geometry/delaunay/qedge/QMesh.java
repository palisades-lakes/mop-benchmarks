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
   // this creates a closed manifold, a triangle and its 'reverse'
    final QEdge e01 = QEdge.make("e01", p0, p1);
    final QEdge e12 = QEdge.make("e12", p1, p2);
    final QEdge e20 = QEdge.make("e20", p2, p0);
    e01.splice(e20.reverse());
    e12.splice(e01.reverse());
    e20.splice(e12.reverse());
    System.out.println("triangleFrame:");
    System.out.println(e01.meshString());
    return new QMesh(e01); }

//  // follow Lischinski code more closely
//  public static final QMesh
//  triangleFrame (final VectorD2 p0,
//                 final VectorD2 p1,
//                 final VectorD2 p2) {
//    final QEdge e01 = QEdge.make("e01", p0, p1);
//    final QEdge e12 = QEdge.make("e12", p1, p2);
//    e01.reverse().splice(e12);
//    e12.setOrigin(p1);
//    e12.reverse().setOrigin(p2);
//    final QEdge e20 = QEdge.make("e20", p2, p0);
//    e12.reverse().splice(e20);
//    e20.setOrigin(p2);
//    e20.reverse().setOrigin(p0);
//    e20.reverse().splice(e01);
//    System.out.println("triangleFrame:");
//    System.out.println(e01.meshString());
//    return new QMesh(e01); }

//  // fails tests, merges triangle and reverse into one face
//  public static final QMesh
//  triangleFrame (final VectorD2 p0,
//                 final VectorD2 p1,
//                 final VectorD2 p2) {
//    final QEdge e01 = QEdge.make("e01", p0, p1);
//    final QEdge e12 = QEdge.make("e12", p1, p2);
//    final QEdge e20 = e01.connect(e12,"e20");
//    System.out.println("triangleFrame:");
//    System.out.println(e01.meshString());
//    return new QMesh(e01); }

  // trying to create just one face, without reverse
//  public static final QMesh
//  triangleFrame (final VectorD2 p0,
//                 final VectorD2 p1,
//                 final VectorD2 p2) {
//    final QEdge e01 = QEdge.make("e01", p0, p1);
//    final QEdge e12 = QEdge.make("e12", p1, p2);
//    final QEdge e20 = QEdge.make("e20", p2, p0);
//    final QEdge e10 = e01.reverse();
//    final QEdge e21 = e12.reverse();
//    final QEdge e02 = e20.reverse();
//    e01.setNext(e02);
//    e12.setNext(e10);
//    e20.setNext(e21);
//    e02.dual().setNext(e10.dual());
//    e10.dual().setNext(e21.dual());
//    e21.dual().setNext(e02.dual());
//    System.out.println("triangleFrame:");
//    System.out.println(e01.meshString());
//    return new QMesh(e01); }

  //--------------------------------------------------------------------

  public static final QMesh
  rectangleFrame (final double xmin,
                  final double xmax,
                  final double ymin,
                  final double ymax) {
    final VectorD2 p0 = new VectorD2(xmin,ymin);
    final VectorD2 p1 = new VectorD2(xmax,ymin);
    final VectorD2 p2 = new VectorD2(xmax,ymax);
    final VectorD2 p3 = new VectorD2(xmin,ymax);
    // face012
    final QEdge e01 = QEdge.make("e01",p0, p1);
    final QEdge e12 = QEdge.make("e12",p1, p2);
    final QEdge e20 = QEdge.make("e20",p2, p0);
    e01.splice(e20.reverse());
    e12.splice(e01.reverse());
    e20.splice(e12.reverse());
    // face023
    final QEdge e02 = e20.reverse();
    final QEdge e23 = QEdge.make("e23",p2, p3);
    final QEdge e30 = QEdge.make("e30",p3, p0);
    e02.splice(e30.reverse());
    e23.splice(e02.reverse());
    e30.splice(e23.reverse());
    System.out.println("rectangleFrame:");
    System.out.println(e01.meshString());
    return new QMesh(e01); }

//  public static final QMesh
//  rectangleFrame (final double xmin,
//                  final double xmax,
//                  final double ymin,
//                  final double ymax) {
//    final VectorD2 p0 = new VectorD2(xmin,ymin);
//    final VectorD2 p1 = new VectorD2(xmax,ymin);
//    final VectorD2 p2 = new VectorD2(xmax,ymax);
//    final VectorD2 p3 = new VectorD2(xmin,ymax);
//    // face012
//    final QEdge e01 = QEdge.make(p0, p1);
//    final QEdge e12 = QEdge.make(p1, p2);
//    final QEdge e20 = QEdge.make(p2, p0);
//    e01.splice(e20.reverse());
//    e12.splice(e01.reverse());
//    e20.splice(e12.reverse());
//    // face023
//    final QEdge e02 = QEdge.make(p0,p2);
//    final QEdge e23 = QEdge.make(p2, p3);
//    final QEdge e30 = QEdge.make(p3, p0);
//    e02.splice(e30.reverse());
//    e23.splice(e02.reverse());
//    e30.splice(e23.reverse());
//
//    final QEdge e = e20.glue(e02);
//
//    return new QMesh(e); }

//--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------

