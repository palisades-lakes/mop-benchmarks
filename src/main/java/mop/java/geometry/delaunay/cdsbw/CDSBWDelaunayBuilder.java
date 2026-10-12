package mop.java.geometry.delaunay.cdsbw;

import clojure.lang.IFn;
import mop.java.cmplx.SimplicialComplex2D;
import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.ZeroSimplex;
import mop.java.geom.mesh.TriangleMesh;
import mop.java.geometry.euclidean.VectorD2;

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
 * @version 2026-10-11
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
  @Override
  public final SortedSet<ZeroSimplex> vertices () { return _vertices; }

  //--------------------------------------------------------------------

  private final SortedMap<ZeroSimplex, VectorD2> _embedding
    = new TreeMap<>();
  @Override
  public final SortedMap<ZeroSimplex, VectorD2> embedding () {
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

  @Override
  public final VectorD2 embedding (final ZeroSimplex z) {
    assert (null != z) : "No embedding for null";
    assert embedding().containsKey(z) : "No embedding for " + z;
    final VectorD2 p = embedding().get(z);
    assert (null != p);
    return p; }

  //--------------------------------------------------------------------

  private final SortedSet<TwoSimplex> _faces = new TreeSet<>();
  @Override
  public final SortedSet<TwoSimplex> faces () { return _faces; }

  //--------------------------------------------------------------------
  // Object
  //--------------------------------------------------------------------

  @Override
  public final String toString () {
    return
      getClass().getSimpleName() + ":\n"
        + "faces:\n" + faces() + "\n"
        + "vertices:\n" + vertices() + "\n"
        + "embedding:\n" + embedding(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private CDSBWDelaunayBuilder () { }

  public static final DelaunayBuilder make () {
    return new CDSBWDelaunayBuilder(); }

  /** Use <code>mesh</code> as the initial frame for the builder. */
  public static final DelaunayBuilder make (final TriangleMesh mesh) {
    final DelaunayBuilder b = new CDSBWDelaunayBuilder();
    final SimplicialComplex2D c = (SimplicialComplex2D) mesh.cmplx();
    final IFn e = mesh.embedding();
    for (final ZeroSimplex z : c.vertices()) {
      b.embed(z,(VectorD2) e.invoke(z)); }
    b.vertices().addAll(c.vertices());
    b.faces().addAll(c.faces());

    return b; }

//--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------

