package mop.java.geometry.predicates.tetrahedron;

import org.apache.commons.geometry.euclidean.threed.Vector3D;

/** Tetrahedra "embedded" in Vector3D.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-06
 */

public abstract class Tetrahedron3D {

  //--------------------------------------------------------------------
  // TODO: algorithm might be exact for some operations and not others.
  // TODO: an estimate of accuracy for each operation would be better.
  /** Is this algorithm exact (to the resolution expansions)
   * or approximate?
   */
  public boolean signedVolumeExact() { return false; }
  public boolean inSphereExact() { return false; }

  //--------------------------------------------------------------------
  /** Return a positive value if the point pd lies inside the circle
   * passing through pa, pb, and pc; a negative value if it lies
   * outside; and zero if the four points are cocircular. The points pa,
   * pb, and pc must be in counterclockwise order, or the sign of the
   * result will be reversed.
   * <br>
   * Only Fast and Default should be used; the other two are for
   * timings.
   * <br>
   * Exact, Slow, and Default use exact arithmetic to ensure a correct
   * answer. The result returned is the determinant of a matrix.  In
   * signedVolume() only, this determinant is computed adaptively, in the
   * sense that exact arithmetic is used only to the degree it is needed
   * to ensure that the returned value has the correct sign.  Hence,
   * signedVolume() is usually quite fast, but will run more slowly when the
   * input points are coplanar or nearly so.
   */
  public double signedVolume (final Vector3D pa,
                               final Vector3D pb,
                               final Vector3D pc,
                               final Vector3D pd) {
    throw new UnsupportedOperationException(
      getClass().getSimpleName()); }

  //--------------------------------------------------------------------

  /** Return a positive value if the point pe lies inside the sphere
   * passing through pa, pb, pc, and pd; a negative value if it lies
   * outside; and zero if the five points are co-spherical.  The points
   * pa, pb, pc, and pd must be ordered so that they have a positive
   * orientation (as defined by signedVolume()), or the sign of the result
   * will be reversed.
   * <br>
   * Only Fast and Default should be used; the other two are for
   * timings.
   * <br>
   * Exact, Slow, and Default use exact arithmetic to ensure a correct
   * answer. The result returned is the determinant of a matrix.  In
   * signedVolume() only, this determinant is computed adaptively, in the
   * sense that exact arithmetic is used only to the degree it is needed
   * to ensure that the returned value has the correct sign.  Hence,
   * inCircle() is usually quite fast, but will run more slowly when the
   * input points are co-spherical or nearly so.
   */

  public double inSphere (final Vector3D pa,
                           final Vector3D pb,
                           final Vector3D pc,
                           final Vector3D pd,
                           final Vector3D pe) {
    throw new UnsupportedOperationException(getClass().getSimpleName()); }
  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
