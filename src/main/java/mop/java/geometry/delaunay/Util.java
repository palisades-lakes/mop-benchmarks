package mop.java.geometry.delaunay;

import clojure.lang.IFn;
import mop.java.cmplx.SimplicialComplex2D;
import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.ZeroSimplex;
import mop.java.geom.mesh.TriangleMesh;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.util.IFnWrapper;

import java.util.*;

/**
 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-14
 */

public final class Util {

  //--------------------------------------------------------------------
  // TODO: move to mop TriangleMesh instance methods
  //--------------------------------------------------------------------

  private static final VectorD2[] sortedPair (final VectorD2 p0,
                                              final VectorD2 p1) {
    final int c = p0.compareTo(p1);
    assert 0 != c;
    if (c < 0) { return new VectorD2[]{p0,p1}; }
    return new VectorD2[]{p1,p0}; }

  public static final Set<VectorD2[]>
  edgeCoordinates (final TriangleMesh mesh,
                   final Collection exclude) {
    final Set<VectorD2[]> pairs = new HashSet<>();
    final IFn e = mesh.embedding();
    final SimplicialComplex2D c = (SimplicialComplex2D) mesh.cmplx();
    for (final TwoSimplex f : c.faces()) {
      final ZeroSimplex z0 = f.z0();
      final ZeroSimplex z1 = f.z1();
      final ZeroSimplex z2 = f.z2();
     final boolean ok0 = ! exclude.contains(z0);
      final boolean ok1 = ! exclude.contains(z1);
      final boolean ok2 = ! exclude.contains(z2);
      final VectorD2 p1 = (VectorD2) e.invoke(f.z1());
      final VectorD2 p2 = (VectorD2) e.invoke(f.z2());
      if (ok0) {
        final VectorD2 p0 = (VectorD2) e.invoke(z0);
        if (ok1) { pairs.add(sortedPair(p0,p1)); }
        if (ok2) { pairs.add(sortedPair(p2,p0)); } }
      if (ok1 && ok2) { pairs.add(sortedPair(p1,p2)); } }
    return pairs; }

  public static final Set<VectorD2[]>
  edgeCoordinates (final TriangleMesh mesh) {
    return edgeCoordinates(mesh,Collections.EMPTY_SET); }

  //--------------------------------------------------------------------
  // geometric predicates
  //--------------------------------------------------------------------
  // orientation
  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * Returns twice the signed area of the oriented triangle (a, b, c),
   * i.e., the area is positive if the triangle is counterclockwise.
   */

  public static final double signedArea (final VectorD2 a,
                                         final VectorD2 b,
                                         final VectorD2 c) {

    return
      (b.x() - a.x())*(c.y() - a.y())
        -
        (b.y() - a.y())*(c.x() - a.x()); }

  public static final boolean ccw (final VectorD2 a,
                                   final VectorD2 b,
                                   final VectorD2 c) {
    return (signedArea(a, b, c) > 0); }


  //--------------------------------------------------------------------
  // inCircle
  //--------------------------------------------------------------------

  private static double inCircleDistance (final VectorD2 p0,
                                          final VectorD2 p1,
                                          final VectorD2 p2,
                                          final VectorD2 p) {

    final VectorD2 v0 = p0.subtract(p);
    final VectorD2 v1 = p1.subtract(p);
    final VectorD2 v2 = p2.subtract(p);

    final double v0xv1 = v0.wedge(v1);
    final double v1xv2 = v1.wedge(v2);
    final double v2xv0 = v2.wedge(v0);

    final double v02 = v0.l2norm2();
    final double v12 = v1.l2norm2();
    final double v22 = v2.l2norm2();

    return v22*v0xv1 + v02*v1xv2 + v12*v2xv0; }

  public static double inCircleDistance (final ZeroSimplex z0,
                                         final ZeroSimplex z1,
                                         final ZeroSimplex z2,
                                         final ZeroSimplex z,
                                         final Map<ZeroSimplex,VectorD2> embedding) {
    final VectorD2 p0 = embedding.get(z0);
    final VectorD2 p1 = embedding.get(z1);
    final VectorD2 p2 = embedding.get(z2);
    final VectorD2 p = embedding.get(z);
    return inCircleDistance(p0, p1, p2, p); }

  public static double inCircleDistance (final TwoSimplex t,
                                         final ZeroSimplex z,
                                         final Map<ZeroSimplex,VectorD2> embedding) {
    return inCircleDistance(t.z0(), t.z1(), t.z2(), z, embedding); }

  public static boolean inCircle (final VectorD2 p0,
                                  final VectorD2 p1,
                                  final VectorD2 p2,
                                  final VectorD2 p) {
    return 0.0 < inCircleDistance(p0,p1,p2,p); }

  /** Is the embedding of <code>z</code> in the circumcircle
   * of the embedding of <code>z0,z1,z2</code>?
   */
  public static boolean inCircle (final ZeroSimplex z0,
                                  final ZeroSimplex z1,
                                  final ZeroSimplex z2,
                                  final ZeroSimplex z,
                                  final Map<ZeroSimplex,VectorD2> embedding) {
    final VectorD2 p0 = embedding.get(z0);
    final VectorD2 p1 = embedding.get(z1);
    final VectorD2 p2 = embedding.get(z2);
    final VectorD2 p = embedding.get(z);
    return inCircle(p0,p1,p2,p); }

  public static boolean inCircle (final TwoSimplex t,
                                  final ZeroSimplex z,
                                  final Map<ZeroSimplex,VectorD2> embedding) {
    return inCircle(t.z0(), t.z1(), t.z2(), z, embedding); }

  //--------------------------------------------------------------------
  // Delaunay frame meshes
  //--------------------------------------------------------------------
  /** Triangle enclosing (0,1) square.
   * TODO: enclose arbitrary rectangle.
   */

  public static final TriangleMesh triangleFrame01 () {

    // Construct a triangle containing the unit square:
    final ZeroSimplex z0 = ZeroSimplex.make("f0");
    final ZeroSimplex z1 = ZeroSimplex.make("f1");
    final ZeroSimplex z2 = ZeroSimplex.make("f2");
    final TwoSimplex t012 = TwoSimplex.make(z0, z1, z2);
    final SimplicialComplex2D c =
      SimplicialComplex2D.make(List.of(t012));

    final VectorD2 p0 = new VectorD2(-1.0, -1.0);
    final VectorD2 p1 = new VectorD2(2.0, -1.0);
    final VectorD2 p2 = new VectorD2(0.5, 3.0);
    final IFn e = IFnWrapper.make(Map.of(z0, p0, z1, p1, z2, p2));

    return TriangleMesh.make(c,e); }

  //--------------------------------------------------------------------
  /** Rectangle <code>[xmin,xmax] X [ymin,ymax]</code>
   * frame made of 2 triangles,
   * to initialize a <code>DelaunayBuilder</code>
   */

  public static final TriangleMesh rectangleFrame (final double xmin,
                                                   final double xmax,
                                                   final double ymin,
                                                   final double ymax) {

    // Construct a unit square as 2 triangles:
    final ZeroSimplex z0 = ZeroSimplex.make("f0");
    final ZeroSimplex z1 = ZeroSimplex.make("f1");
    final ZeroSimplex z2 = ZeroSimplex.make("f2");
    final ZeroSimplex z3 = ZeroSimplex.make("f3");
    final TwoSimplex t012 = TwoSimplex.make(z0, z1, z2);
    final TwoSimplex t023 = TwoSimplex.make(z0, z2, z3);
    final SimplicialComplex2D c =
      SimplicialComplex2D.make(List.of(t012,t023));

    final VectorD2 p0 = new VectorD2(xmin,ymin);
    final VectorD2 p1 = new VectorD2(xmax,ymin);
    final VectorD2 p2 = new VectorD2(xmax,ymax);
    final VectorD2 p3 = new VectorD2(xmin,ymax);

    final IFn e = IFnWrapper.make(
      Map.of(z0, p0, z1, p1, z2, p2, z3, p3));

    return TriangleMesh.make(c,e); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  /** disabled constructor, class methods only. */

  private Util () {
    throw new UnsupportedOperationException(
      getClass().getSimpleName() + ": construction not allowed."); }

//--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------

