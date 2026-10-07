package mop.java.geometry.delaunay.cdsbw;


import clojure.lang.IFn;
import mop.java.cmplx.OneSimplex;
import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.ZeroSimplex;

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
 * @version 2026-10-07
 */

public final class CDSBWDelaunayBuilder implements DelaunayBuilder {

  private final IFn _embedding;

  public final IFn embedding () { return _embedding; }

  /** CDS: Add positively oriented triangle. */
  public final void addTriangle (final ZeroSimplex z0,
                                 final ZeroSimplex z1,
                                 final ZeroSimplex z2) { }

  /** CDS: Delete positively oriented triangle. */
  public final void deleteTriangle (final ZeroSimplex z0,
                                 final ZeroSimplex z1,
                                 final ZeroSimplex z2) { }

  // TODO: is 'opposite' a better name than 'adjacent'?
  /** CDS: Return a vertex <code>z2</code>
   * such that <code>(z0,z1,z2)</code>
   * is a positively oriented triangle.
   * <br>TODO: return a <code>OneSimplex</code> for symmetry?
   */
  public final TwoSimplex adjacent (final ZeroSimplex z0,
                                    final ZeroSimplex z1) {
    return null; }

  /** CDS: Return an edge <code>z1,z2</code>
   * such that <code>(z0,z1,z2)</code>
   * is a positively oriented triangle.
   */
  public final OneSimplex adjacent (final ZeroSimplex z0) {
    return null; }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private CDSBWDelaunayBuilder (final IFn embedding) {
    _embedding = embedding; }

//--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------

