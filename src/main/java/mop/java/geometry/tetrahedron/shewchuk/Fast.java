package mop.java.geometry.tetrahedron.shewchuk;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.geometry.tetrahedron.Tetrahedron3D;
import org.apache.commons.geometry.euclidean.threed.Vector3D;

/** Approximate predicates, nonrobust.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-27
 */

public final class Fast extends Tetrahedron3D {

  //--------------------------------------------------------------------

  public final double signedVolume () {
    final Vector3D pa = getP0();
    final Vector3D pb = getP1();
    final Vector3D pc = getP2();
    final Vector3D pd = getP3();

    final double adx = pa.getX() - pd.getX();
    final double bdx = pb.getX() - pd.getX();
    final double cdx = pc.getX() - pd.getX();
    final double ady = pa.getY() - pd.getY();
    final double bdy = pb.getY() - pd.getY();
    final double cdy = pc.getY() - pd.getY();
    final double adz = pa.getZ() - pd.getZ();
    final double bdz = pb.getZ() - pd.getZ();
    final double cdz = pc.getZ() - pd.getZ();

    return (adx * ((bdy * cdz) - (bdz * cdy))) +
      (bdx * ((cdy * adz) - (cdz * ady))) +
      (cdx * ((ady * bdz) - (adz * bdy))); }


  //--------------------------------------------------------------------

  public final double inSphere (final Vector3D p) {
    final Vector3D pa = getP0();
    final Vector3D pb = getP1();
    final Vector3D pc = getP2();
    final Vector3D pd = getP3();

    final double aex = pa.getX() - p.getX();
    final double bex = pb.getX() - p.getX();
    final double cex = pc.getX() - p.getX();
    final double dex = pd.getX() - p.getX();
    final double aey = pa.getY() - p.getY();
    final double bey = pb.getY() - p.getY();
    final double cey = pc.getY() - p.getY();
    final double dey = pd.getY() - p.getY();
    final double aez = pa.getZ() - p.getZ();
    final double bez = pb.getZ() - p.getZ();
    final double cez = pc.getZ() - p.getZ();
    final double dez = pd.getZ() - p.getZ();

    final double ab = (aex * bey) - (bex * aey);
    final double bc = (bex * cey) - (cex * bey);
    final double cd = (cex * dey) - (dex * cey);
    final double da = (dex * aey) - (aex * dey);

    final double ac = (aex * cey) - (cex * aey);
    final double bd = (bex * dey) - (dex * bey);

    final double abc = (aez * bc) - (bez * ac) + (cez * ab);
    final double bcd = (bez * cd) - (cez * bd) + (dez * bc);
    final double cda = (cez * da) + (dez * ac) + (aez * cd);
    final double dab = (dez * ab) + (aez * bd) + (bez * da);

    final double alift = (aex * aex) + (aey * aey) + (aez * aez);
    final double blift = (bex * bex) + (bey * bey) + (bez * bez);
    final double clift = (cex * cex) + (cey * cey) + (cez * cez);
    final double dlift = (dex * dex) + (dey * dey) + (dez * dez);

    return
      ((dlift*abc) - (clift*dab)) + ((blift*cda) - (alift*bcd)); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private Fast (final Vector3D a,
                 final Vector3D b,
                 final Vector3D c,
                 final Vector3D d)  {
    super(a,b,c,d); }

  public static final Tetrahedron3D of (final Vector3D a,
                                        final Vector3D b,
                                        final Vector3D c,
                                        final Vector3D d) {
    return new Fast(a, b, c, d); }

  /** Convert between tetrahedra classes. */
  public static final Tetrahedron3D from (final Tetrahedron3D t) {
    return of(t.getP0(),t.getP1(),t.getP2(),t.getP3()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
