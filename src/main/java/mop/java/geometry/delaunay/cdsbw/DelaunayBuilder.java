package mop.java.geometry.delaunay.cdsbw;

import clojure.lang.IFn;
import mop.java.cmplx.OneSimplex;
import mop.java.cmplx.SimplicialComplex2D;
import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.ZeroSimplex;
import mop.java.geom.mesh.TriangleMesh;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.util.IFnWrapper;

import java.util.Collection;
import java.util.Map;

/** Delaunay triangulation, after
 * Cheng, Dey, Shewchuk discussion of Boyer-Watson, <br>
 * in Ch 3 of<br>
 * <a href="https://www.routledge.com/Delaunay-Mesh-Generation/Cheng-Dey-Shewchuk/p/book/9781584887300">
 * Siu-wing Cheng,
 * Tamal Krishna Dey,
 * Jonathan Richard Shewchuk,
 * <b>Delaunay Mesh Generation</b>, 2013.
 * </a>
 * <p>
 * See also
 * <ul>
 * <li><a href="https://people.eecs.berkeley.edu/~jrs/meshpapers/delnotes.pdf">
 * Shewchuk's <i>Lecture notes on Delaunay Mesh Generation</i>,
 * 2012</a>
 * <br> Has a very similar version of Ch 3.
 * </li>
 * <li><a"href=https://cse.hkust.edu.hk/~scheng/meshbook.html">
 * S. Cheng website, with a few PDF chapters from the book,
 * unfortunately not Ch 3.</a>
 * </li>
 * <li>
 * <a href="https://www.wias-berlin.de/people/si/course/files/Shewchuk96-Triangle.pdf">
 * Jonathan Richard Shewchuk. 1996.
 * <i>Triangle: Engineering a 2D Quality Mesh Generator
 * and Delaunay Triangulator.</i>
 * In <b>Selected papers from the
 * Workshop on Applied Computational Geometry,
 * Towards Geometric Engineering (FCRC '96/WACG '96).</b>
 * Springer-Verlag, 203–222.
 * </a>
 * </li>
 * <li>
 * <a href="https://www.cs.cmu.edu/~quake/triangle.html">Shewchuk's Triangle website</a>
 * </li>
 * </ul>
 * </p>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-11
 */

public interface DelaunayBuilder {

  public Collection<ZeroSimplex> vertices ();
  public Collection<TwoSimplex> faces ();
  public default int nFaces () { return faces().size(); }

  /** Add an embedding for <code>z</code>. <br>
   * Throw an exception if embedding already exists.<br>
   * TODO: no exception if matches existing embedding?
   */
  public void embed (final ZeroSimplex z,
                     final VectorD2 p);

  /** Get the embedding for <code>z</code>. <br>
   * Throw an exception if no embedding exists.<br>
   */
  public VectorD2 embedding (final ZeroSimplex z);
  public Map<ZeroSimplex, VectorD2> embedding ();

  /** CDS: Add positively oriented triangle.
   * <br>
   * Throw an exception if triangle already present.
   * TODO: Ignore repeated additions of the same triangle?
   */

  public default void addTriangle (final ZeroSimplex z0,
                                   final ZeroSimplex z1,
                                   final ZeroSimplex z2) {
    // TODO: check orientation?
    final TwoSimplex t = TwoSimplex.make(z0,z1,z2);
    assert ! faces().contains(t);
    vertices().add(z0);
    vertices().add(z1);
    vertices().add(z2);
    faces().add(t); }


  /** CDS: Delete positively oriented triangle. */
  public default void deleteTriangle (final ZeroSimplex z0,
                                      final ZeroSimplex z1,
                                      final ZeroSimplex z2) {
    // TODO: eliminate ephemeral instance?
    // NOTE: only one face in set for any even permutation of vertices
    final TwoSimplex t = TwoSimplex.make(z0,z1,z2);
    assert faces().contains(t);
    faces().remove(TwoSimplex.make(z0, z1, z2)); }

  //--------------------------------------------------------------------
  // TODO: is 'opposite' a better name than 'adjacent'?
  // TODO: memoize?
  /** CDS: Return a vertex <code>z2</code>
   * such that <code>(z0,z1,z2)</code>
   * is a positively oriented triangle.
   * <br>
   * Throw an exception if no such face.
   */

  public default ZeroSimplex adjacent (final ZeroSimplex z0,
                                       final ZeroSimplex z1) {
    // TODO: should be unique --- unit test to check
    for (final TwoSimplex t : faces()) {
      if (z0.equals(t.z0()) && z1.equals(t.z1())) { return t.z2(); }
      if (z0.equals(t.z1()) && z1.equals(t.z2())) { return t.z0(); }
      if (z0.equals(t.z2()) && z1.equals(t.z0())) { return t.z1(); } }
    return null; }

  //--------------------------------------------------------------------
  // TODO: memoize vertex to edge?
  // TODO: soft cache edge objects independent of faces?
  /** CDS: Return an edge <code>z1,z2</code>
   * such that <code>(z0,z1,z2)</code>
   * is a positively oriented triangle.
   * <br>
   * Throw an exception if no such face.
   */

  public default OneSimplex adjacent (final ZeroSimplex z) {
    for (final TwoSimplex t : faces()) {
      if (t.z0().equals(z)) { return OneSimplex.make(t.z1(),t.z2()); }
      if (t.z1().equals(z)) { return OneSimplex.make(t.z2(),t.z0()); }
      if (t.z2().equals(z)) { return OneSimplex.make(t.z0(),t.z1()); } }
    throw new UnsupportedOperationException(
      "no face with vertex:" + z); }

  //--------------------------------------------------------------------
  // inCircle
  //--------------------------------------------------------------------

  private static double inCircle (final VectorD2 p0,
                                  final VectorD2 p1,
                                  final VectorD2 p2,
                                  final VectorD2 p) {

    final VectorD2 v0 = p0.subtract(p);
    final VectorD2 v1 = p1.subtract(p);
    final VectorD2 v2 = p2.subtract(p);

    final double v0xv2 = v0.wedge(v2);
    final double v1xv0 = v1.wedge(v0);
    final double v2xv1 = v2.wedge(v1);

    final double v02 = v0.l2norm2();
    final double v12 = v1.l2norm2();
    final double v22 = v2.l2norm2();

    return v22*v1xv0 + v02*v2xv1 + v12*v0xv2; }

  public default double inCircle (final ZeroSimplex z0,
                                  final ZeroSimplex z1,
                                  final ZeroSimplex z2,
                                  final ZeroSimplex z) {
    final VectorD2 p0 = embedding(z0);
    final VectorD2 p1 = embedding(z1);
    final VectorD2 p2 = embedding(z2);
    final VectorD2 p = embedding(z);
    return inCircle(p0, p1, p2, p); }

  //--------------------------------------------------------------------

  public default void digCavity (final ZeroSimplex u,
                                 final ZeroSimplex v,
                                 final ZeroSimplex w) {
    final ZeroSimplex x = adjacent(w,v);
    if (null != x) {
      if (inCircle(u,v,w,x)<0) {
        deleteTriangle(w,v,x);
        digCavity(u,v,x);
        digCavity(u,x,w); } }
    else { addTriangle(u,v,w); } }

  //--------------------------------------------------------------------
  // TODO: reorder args to match (face, site) convention
  /** <code>z</code> is the vertex to insert.
   * <code>z0,z1,z2</code> the vertices of a (positively)
   * oriented face whose circumcircle includes <code>z</code>
   * (using the current embedding of the vertices).
   */

  public default void insertVertex (final ZeroSimplex u,
                                    final ZeroSimplex v,
                                    final ZeroSimplex w,
                                    final ZeroSimplex x) {
    deleteTriangle(v,w,x);
    digCavity(u,v,w);
    digCavity(u,w,x);
    digCavity(u,x,v); }

  //--------------------------------------------------------------------
  /** Emit the triangle mesh corresponding to the current state of the
   * builder.
   * <br>
   * TODO: cache last value and stateChanged to avoid rebuilding
   *  rebuilding the same mesh? Not really a likely scenario.
   */

  public default TriangleMesh build () {
    final SimplicialComplex2D cmplx = SimplicialComplex2D.make(faces());
    final IFn z2p = IFnWrapper.make(embedding());
    return TriangleMesh.make(cmplx,z2p); }

  //--------------------------------------------------------------------
} // end interface
//--------------------------------------------------------------------

