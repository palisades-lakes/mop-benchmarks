package mop.java.geometry.delaunay.cdsbw;

import clojure.lang.IFn;
import mop.java.cmplx.OneSimplex;
import mop.java.cmplx.SimplicialComplex2D;
import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.ZeroSimplex;
import mop.java.geom.mesh.TriangleMesh;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.util.IFnWrapper;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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
 * @version 2026-10-10
 */

public final class CDSBWDelaunayBuilder implements DelaunayBuilder {

  //--------------------------------------------------------------------
  // The current mesh
  //--------------------------------------------------------------------
  // TODO: Is SortedSet/Map a good idea?
  //  Begin with that to enable some simple tests.
  // TODO: use sets/maps with soft references?
  // TODO: extract embedding keys when needed?

  private final SortedSet<ZeroSimplex> _vertices = new TreeSet<>();
  private final SortedSet<ZeroSimplex> vertices () { return _vertices; }

  //--------------------------------------------------------------------

  private final SortedMap<ZeroSimplex, VectorD2> _embedding
    = new TreeMap<>();
  private final SortedMap<ZeroSimplex, VectorD2> embedding () {
    return _embedding; }

  // TODO: Map implementation that allows adding.
  //  but not modifying entries?
  @Override
  public final void embed (final ZeroSimplex z,
                           final VectorD2 p) {
    // just in case...
    vertices().add(z);
    final Object previous = embedding().putIfAbsent(z,p);
    assert (null == previous); }

  public final VectorD2 embedding (final ZeroSimplex z) {
    final VectorD2 p = embedding().get(z);
    assert (null != p);
    return p; }

  //--------------------------------------------------------------------

  private final SortedSet<TwoSimplex> _faces = new TreeSet<>();
  public final SortedSet<TwoSimplex> faces () { return _faces; }

  public final int nFaces () { return faces().size(); }

  public final void addTriangle (final ZeroSimplex z0,
                                 final ZeroSimplex z1,
                                 final ZeroSimplex z2) {
    // TODO: check orientation?
    final TwoSimplex t = TwoSimplex.make(z0,z1,z2);
    assert ! faces().contains(t);
    vertices().add(z0);
    vertices().add(z1);
    vertices().add(z2);
    faces().add(t); }

  public final void deleteTriangle (final ZeroSimplex z0,
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

  public final ZeroSimplex adjacent (final ZeroSimplex z0,
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

  public final OneSimplex adjacent (final ZeroSimplex z) {
    for (final TwoSimplex t : faces()) {
      if (t.z0().equals(z)) { return OneSimplex.make(t.z1(),t.z2()); }
      if (t.z1().equals(z)) { return OneSimplex.make(t.z2(),t.z0()); }
      if (t.z2().equals(z)) { return OneSimplex.make(t.z0(),t.z1()); } }
    throw new UnsupportedOperationException(
      "no face with vertex:" + z); }

  //--------------------------------------------------------------------
  // Object
  //--------------------------------------------------------------------

  public final String toString () {
    return
      getClass().getSimpleName() + ":\n"
        + "faces:\n" + faces() + "\n"
        + "vertices:\n" + vertices() + "\n"
      + "embedding:\n" + embedding() + "\n"; }

  //--------------------------------------------------------------------
  // emit mesh
  //--------------------------------------------------------------------
  /** Emit the triangle mesh corresponding to the current state of the
   * builder.
   * <br>
   * TODO: cache last value and stateChanged to avoid rebuilding
   *  rebuilding the same mesh? Not really a likely scenario.
   */

  public final TriangleMesh build () {
    final SimplicialComplex2D cmplx = SimplicialComplex2D.make(faces());
    final IFn z2p = IFnWrapper.make(embedding());
    return TriangleMesh.make(cmplx,z2p); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private CDSBWDelaunayBuilder () { }

  public static final DelaunayBuilder make () {
    return new CDSBWDelaunayBuilder(); }

//--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------

