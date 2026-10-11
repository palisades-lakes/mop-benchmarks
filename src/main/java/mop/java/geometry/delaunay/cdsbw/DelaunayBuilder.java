package mop.java.geometry.delaunay.cdsbw;

import mop.java.cmplx.OneSimplex;
import mop.java.cmplx.ZeroSimplex;
import mop.java.geometry.euclidean.VectorD2;

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

public interface DelaunayBuilder {

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

  /** CDS: Add positively oriented triangle.
   * <br>
   * Throw an exception if triangle already present.
   * TODO: Ignore repeated additions of the same triangle?
   */
  public void addTriangle (final ZeroSimplex z0,
                           final ZeroSimplex z1,
                           final ZeroSimplex z2);

  /** CDS: Delete positively oriented triangle. */
  public void deleteTriangle (final ZeroSimplex z0,
                              final ZeroSimplex z1,
                              final ZeroSimplex z2);

  public int nFaces ();

  /** CDS: Return a vertex <code>z2</code>
   * such that <code>(z0,z1,z2)</code>
   * is a positively oriented triangle.
   * <br>
   * Throw an exception if no such face.
   */
  public ZeroSimplex adjacent (final ZeroSimplex z0,
                               final ZeroSimplex z1);

  /** CDS: Return an edge <code>z1,z2</code>
   * such that <code>(z0,z1,z2)</code>
   * is a positively oriented triangle.
   * <br>
   * Throw an exception if no such face.
   */
  public OneSimplex adjacent (final ZeroSimplex z0);

  //--------------------------------------------------------------------
  // inCircle
  //--------------------------------------------------------------------

  private static double inCircle (final VectorD2 p0,
                                  final VectorD2 p1,
                                  final VectorD2 p2,
                                  final VectorD2 p) {

    final VectorD2 v10 = p1.subtract(p0);
    final VectorD2 v20 = p2.subtract(p0);
    final VectorD2 vp0 = p.subtract(p0);

    final double bxp = v10.wedge(vp0);
    final double bxc = v20.wedge(v10);
    final double pxc = vp0.wedge(v20);

    final double a2 = vp0.l2norm2();
    final double b2 = v10.l2norm2();
    final double c2 = v20.l2norm2();

    return a2*bxc + b2*pxc + c2*bxp; }

  public default double inCircle (final ZeroSimplex z0,
                                   final ZeroSimplex z1,
                                   final ZeroSimplex z2,
                                   final ZeroSimplex z) {
    final VectorD2 p0 = embedding(z0);
    final VectorD2 p1 = embedding(z1);
    final VectorD2 p2 = embedding(z2);
    final VectorD2 p = embedding(z);
    return inCircle(p0,p1,p2,p); }

  //--------------------------------------------------------------------

  public default void digCavity (final ZeroSimplex u,
                                 final ZeroSimplex v,
                                 final ZeroSimplex w) {
    final ZeroSimplex x = adjacent(w,v);
    if (null == x) { return; }
    if (inCircle(w,v,x,u)>0) {
      deleteTriangle(w,v,x);
      digCavity(u,v,x);
      digCavity(u,x,w); }
    else { addTriangle(u,v,w); } }

  //--------------------------------------------------------------------
  // TODO: reorder args to match my face, site convention

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
} // end interface
//--------------------------------------------------------------------

