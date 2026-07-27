package mop.java.geometry.tetrahedron.macro;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.geometry.tetrahedron.Tetrahedron3D;
import org.apache.commons.geometry.euclidean.threed.Vector3D;

/**
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-27
 */

// strictfp unnecessary for JDK17 and later
public final class FastMacro extends Tetrahedron3D {

  //--------------------------------------------------------------------

  public final double signedVolume () {
    final Vector3D pa = getP0();
    final Vector3D pb = getP1();
    final Vector3D pc = getP2();
    final Vector3D pd = getP3();

    double adx, bdx, cdx;
    double ady, bdy, cdy;
    double adz, bdz, cdz;

    adx = pa.getX() - pd.getX();
    bdx = pb.getX() - pd.getX();
    cdx = pc.getX() - pd.getX();
    ady = pa.getY() - pd.getY();
    bdy = pb.getY() - pd.getY();
    cdy = pc.getY() - pd.getY();
    adz = pa.getZ() - pd.getZ();
    bdz = pb.getZ() - pd.getZ();
    cdz = pc.getZ() - pd.getZ();

    return adx * (bdy * cdz - bdz * cdy)
      + bdx * (cdy * adz - cdz * ady)
      + cdx * (ady * bdz - adz * bdy); }


  //--------------------------------------------------------------------

  public final double inSphere (final Vector3D p) {
    final Vector3D pa = getP0();
    final Vector3D pb = getP1();
    final Vector3D pc = getP2();
    final Vector3D pd = getP3();

    double aex, bex, cex, dex;
    double aey, bey, cey, dey;
    double aez, bez, cez, dez;
    double alift, blift, clift, dlift;
    double ab, bc, cd, da, ac, bd;
    double abc, bcd, cda, dab;

    aex = pa.getX() - p.getX();
    bex = pb.getX() - p.getX();
    cex = pc.getX() - p.getX();
    dex = pd.getX() - p.getX();
    aey = pa.getY() - p.getY();
    bey = pb.getY() - p.getY();
    cey = pc.getY() - p.getY();
    dey = pd.getY() - p.getY();
    aez = pa.getZ() - p.getZ();
    bez = pb.getZ() - p.getZ();
    cez = pc.getZ() - p.getZ();
    dez = pd.getZ() - p.getZ();

    ab = aex * bey - bex * aey;
    bc = bex * cey - cex * bey;
    cd = cex * dey - dex * cey;
    da = dex * aey - aex * dey;

    ac = aex * cey - cex * aey;
    bd = bex * dey - dex * bey;

    abc = aez * bc - bez * ac + cez * ab;
    bcd = bez * cd - cez * bd + dez * bc;
    cda = cez * da + dez * ac + aez * cd;
    dab = dez * ab + aez * bd + bez * da;

    alift = aex * aex + aey * aey + aez * aez;
    blift = bex * bex + bey * bey + bez * bez;
    clift = cex * cex + cey * cey + cez * cez;
    dlift = dex * dex + dey * dey + dez * dez;

    return (dlift * abc - clift * dab) + (blift * cda - alift * bcd);
  }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private FastMacro (final Vector3D a,
                     final Vector3D b,
                     final Vector3D c,
                     final Vector3D d)  {
    super(a,b,c,d); }

  public static final Tetrahedron3D of (final Vector3D a,
                                        final Vector3D b,
                                        final Vector3D c,
                                        final Vector3D d) {
    return new FastMacro(a, b, c, d); }

  /** Convert between tetrahedra classes. */
  public static final Tetrahedron3D from (final Tetrahedron3D t) {
    return of(t.getP0(),t.getP1(),t.getP2(),t.getP3()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
