/** A somewhat cleaned up translation into Java of cpp code from<br>
 * <a href="https://www.researchgate.net/publication/262235495_Incremental_Delaunay_Triangulation">
 *  Dani Lischinski<br
 *  <i>Incremental Delaunay triangulation</i><br>
 *  <b>Graphics Gems IV</b> Academic Press 1994 </a>
 *  <br>
 *  See also:
 *  <a href="https://github.com/erich666/GraphicsGems">Graphics Gems github</a>
 * See also:
 * <ul>
 * <li><a href="https://github.com/erich666/GraphicsGems">Graphics Gems github</a></li>
 * <li>
 * <a href="https://dl.acm.org/doi/10.1145/282918.282923">
 *   Leonidas Guibas and Jorge Stolfi.
 *   1985.
 *   <i>Primitives for the manipulation of general subdivisions and
 *   the computation of Voronoi Diagrams.</i>
 *   <b>ACM Trans. Graph. 4</b>, 2 (April 1985), 74–123.
 * </a>
 * (<a href="https://doi.org/10.1145/282918.282923">(DOI)</a>
 * </li>
 * </ul>
 * The main difference from Lischinski is that <code>QEdge</code> is
 * independent of embedding, a representation of the simplicial/cell
 * complex connecting abstract
 * <code>ZeroSimplex</code>s.
 * The embedding in some 2d space is held in the <code>QMesh</code>,
 * as in <code>SimplicialComplex2D</code> and <code>TriangelMesh</code>.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-15
 */

package mop.java.geometry.delaunay.aqedge;

