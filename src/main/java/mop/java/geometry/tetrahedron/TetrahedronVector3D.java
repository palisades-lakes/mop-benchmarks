package mop.java.geometry.tetrahedron;

import org.apache.commons.geometry.euclidean.threed.Vector3D;

/** Tetrahedra "embedded" in Vector3D.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-27
 */

public final class TetrahedronVector3D extends Tetrahedron3D {

  // TODO: minimal volume and inSphere methods...

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public TetrahedronVector3D (final Vector3D a,
                              final Vector3D b,
                              final Vector3D c,
                              final Vector3D d) {
    super(a,b,c,d); }

  public static final Tetrahedron3D of (final Vector3D a,
                                        final Vector3D b,
                                        final Vector3D c,
                                        final Vector3D d) {
    return new TetrahedronVector3D(a, b, c, d); }

  /** Convert between tetrahedra classes. */
  public static final Tetrahedron3D from (final Tetrahedron3D t) {
    return of(t.getP0(),t.getP1(),t.getP2(),t.getP3()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
