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
 * @version 2026-07-06
 */

// strictfp unnecessary for JDK17 and later
public final class FastMacro extends Tetrahedron3D {

  //--------------------------------------------------------------------

  public final double signedVolume (final Vector3D pa,
                                    final Vector3D pb,
                                    final Vector3D pc,
                                    final Vector3D pd) {
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

  public final double inSphere (final Vector3D pa,
                                final Vector3D pb,
                                final Vector3D pc,
                                final Vector3D pd,
                                final Vector3D pe) {
    double aex, bex, cex, dex;
    double aey, bey, cey, dey;
    double aez, bez, cez, dez;
    double alift, blift, clift, dlift;
    double ab, bc, cd, da, ac, bd;
    double abc, bcd, cda, dab;

    aex = pa.getX() - pe.getX();
    bex = pb.getX() - pe.getX();
    cex = pc.getX() - pe.getX();
    dex = pd.getX() - pe.getX();
    aey = pa.getY() - pe.getY();
    bey = pb.getY() - pe.getY();
    cey = pc.getY() - pe.getY();
    dey = pd.getY() - pe.getY();
    aez = pa.getZ() - pe.getZ();
    bez = pb.getZ() - pe.getZ();
    cez = pc.getZ() - pe.getZ();
    dez = pd.getZ() - pe.getZ();

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
  // TODO: singleton?

  public FastMacro () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
