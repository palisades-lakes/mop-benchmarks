/** Delaunay triangulation
 * following the
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
 * <li><a"href=https://cse.hkust.edu.hk/~scheng/meshbook.html">
 * S. Cheng website, with a few PDF chapters from the book,
 * unfortunately not Ch 3.</a>
 * </li>
 * <li><a href="https://people.eecs.berkeley.edu/~jrs/meshpapers/delnotes.pdf">
 * Shewchuk's <i>Lecture notes on Delaunay Mesh Generation</i>,
 * 2012</a>
 * <br> Has a very similar version of Ch 3.
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
 * <li>
 * Seems pretty close to existing mostly immutable mop implementation:<br>
 * <a href="https://www.cs.cmu.edu/~rwh/papers/triangulations/waaapl.pdf">
 * Guy Blelloch, Hal Burch, Karl Crary, Robert Harper, Gary Miller,
 * and Noel Walkington,
 * <i>Persistent triangulations</i>
 * <b>Journal of Functional Programming 11</b>
 * (5):441–466, September 2001.</a>
 * </li>
 * <li>
 * Note: not likely relevant. Basic idea is to label vertices so that
 * general pointers can be replaced with 'difference encoded' indexing.
 * Very unlikely extra complexity worthwhile.<br>
 * <a href="https://kilthub.cmu.edu/articles/Compact_Representations_of_Simplicial_Meshes_in_Two_and_Three_Dimensions/6604226/files/12094628.pdf">
 * Daniel K. Blandford, Guy E. Blelloch, David E. Cardoze,
 * and Clemens Kadow.
 * <i>Compact Representations of Simplicial Meshes
 * in Two and Three Dimensions.</i>
 * <b>International Journal of Computational
 * Geometry and Applications 15</b>(1):3–24, February 2005.
 * </a>
 * </li>
 * <li>Related to Blandford et al 2005. Concerning "well-shaped meshes":<br>
 * <a href="https://dl.acm.org/doi/abs/10.1145/256292.256294">
 * Gary L. Miller, Shang-Hua Teng, William Thurston, and Stephen A. Vavasis.
 * 1997.
 * <i>Separators for sphere-packings and nearest neighbor graphs.</i>
 * <b>J. ACM 44</b> 1 (Jan. 1997), 1–29.   </a>
 * <a href="https://doi.org/10.1145/256292.256294">(DOI)</a>
 * </li>
 * </ul>
 * </p>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-07
 */

package mop.java.geometry.delaunay.cdsbw;

