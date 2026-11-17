package mop.java.geometry.delaunay;

import java.util.Set;

/** Convert the two-simplexes (faces)
 * of a <code>SimpllcialComplex2D</code>
 * into some other structure, eg, quad or half edge representation.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-11-16
 */
public interface MeshTranslator {

  //--------------------------------------------------------------------

  public MeshIndex index ();

//--------------------------------------------------------------------
  /** Return the primal edges representing a completed
   * version of the input <code>SimplicialComplex2D</code>.
   * <br>
   * Usually one edge is enough to link into the mesh,
   * but multiple may be necessary if the corresponding manifold is
   * not connected.
   */
  public Set<Edge> build () ;

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
