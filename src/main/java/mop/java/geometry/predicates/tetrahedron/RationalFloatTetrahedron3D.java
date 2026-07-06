package mop.java.geometry.predicates.tetrahedron;

import mop.java.numbers.RationalFloat;
import org.apache.commons.geometry.euclidean.threed.Vector3D;

/** Standard calculations implemented in RationalFloat.
 * Should be exact, up to RationalFloat resolution.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-06
 */

public final class RationalFloatTetrahedron3D extends Tetrahedron3D {

  //--------------------------------------------------------------------
  // TODO: rewrite as vector operations

  public final boolean signedVolumeExact () { return true; }

  public final double signedVolume (final Vector3D pa,
                                    final Vector3D pb,
                                    final Vector3D pc,
                                    final Vector3D pd) {
    final RationalFloat ax = RationalFloat.valueOf(pa.getX());
    final RationalFloat ay = RationalFloat.valueOf(pa.getY());
    final RationalFloat az = RationalFloat.valueOf(pa.getZ());
    final RationalFloat bx = RationalFloat.valueOf(pb.getX());
    final RationalFloat by = RationalFloat.valueOf(pb.getY());
    final RationalFloat bz = RationalFloat.valueOf(pb.getZ());
    final RationalFloat cx = RationalFloat.valueOf(pc.getX());
    final RationalFloat cy = RationalFloat.valueOf(pc.getY());
    final RationalFloat cz = RationalFloat.valueOf(pc.getZ());
    final RationalFloat dx = RationalFloat.valueOf(pd.getX());
    final RationalFloat dy = RationalFloat.valueOf(pd.getY());
    final RationalFloat dz = RationalFloat.valueOf(pd.getZ());
    final RationalFloat adx = ax.subtract(dx);
    final RationalFloat bdx = bx.subtract(dx);
    final RationalFloat cdx = cx.subtract(dx);
    final RationalFloat ady = ay.subtract(dy);
    final RationalFloat bdy = by.subtract(dy);
    final RationalFloat cdy = cy.subtract(dy);
    final RationalFloat adz = az.subtract(dz);
    final RationalFloat bdz = bz.subtract(dz);
    final RationalFloat cdz = cz.subtract(dz);

    return
      adx.multiply(bdy.multiply(cdz).subtract(bdz.multiply(cdy)))
         .add(
           bdx.multiply(cdy.multiply(adz).subtract(cdz.multiply(ady))))
         .add(
           cdx.multiply(ady.multiply(bdz).subtract(adz.multiply(bdy))))
         .doubleValue(); }

  //--------------------------------------------------------------------
  // inSphere
  //--------------------------------------------------------------------

  public final boolean inSphereExact () { return true; }

  public final double inSphere (final Vector3D pa,
                                final Vector3D pb,
                                final Vector3D pc,
                                final Vector3D pd,
                                final Vector3D pe) {
    final RationalFloat ax = RationalFloat.valueOf(pa.getX());
    final RationalFloat ay = RationalFloat.valueOf(pa.getY());
    final RationalFloat az = RationalFloat.valueOf(pa.getZ());
    final RationalFloat bx = RationalFloat.valueOf(pb.getX());
    final RationalFloat by = RationalFloat.valueOf(pb.getY());
    final RationalFloat bz = RationalFloat.valueOf(pb.getZ());
    final RationalFloat cx = RationalFloat.valueOf(pc.getX());
    final RationalFloat cy = RationalFloat.valueOf(pc.getY());
    final RationalFloat cz = RationalFloat.valueOf(pc.getZ());
    final RationalFloat dx = RationalFloat.valueOf(pd.getX());
    final RationalFloat dy = RationalFloat.valueOf(pd.getY());
    final RationalFloat dz = RationalFloat.valueOf(pd.getZ());
    final RationalFloat ex = RationalFloat.valueOf(pe.getX());
    final RationalFloat ey = RationalFloat.valueOf(pe.getY());
    final RationalFloat ez = RationalFloat.valueOf(pe.getZ());
    final RationalFloat aex = ax.subtract(ex);
    final RationalFloat bex = bx.subtract(ex);
    final RationalFloat cex = cx.subtract(ex);
    final RationalFloat dex = dx.subtract(ex);
    final RationalFloat aey = ay.subtract(ey);
    final RationalFloat bey = by.subtract(ey);
    final RationalFloat cey = cy.subtract(ey);
    final RationalFloat dey = dy.subtract(ey);
    final RationalFloat aez = az.subtract(ez);
    final RationalFloat bez = bz.subtract(ez);
    final RationalFloat cez = cz.subtract(ez);
    final RationalFloat dez = dz.subtract(ez);

    final RationalFloat ab = aex.multiply(bey).subtract(bex.multiply(aey));
    final RationalFloat bc = bex.multiply(cey).subtract(cex.multiply(bey));
    final RationalFloat cd = cex.multiply(dey).subtract(dex.multiply(cey));
    final RationalFloat da = dex.multiply(aey).subtract(aex.multiply(dey));

    final RationalFloat ac = aex.multiply(cey).subtract(cex.multiply(aey));
    final RationalFloat bd = bex.multiply(dey).subtract(dex.multiply(bey));

    final RationalFloat abc =
      aez.multiply(bc).subtract(bez.multiply(ac)).add(cez.multiply(ab));
    final RationalFloat bcd = bez.multiply(cd).subtract(
      cez.multiply(bd)).add(dez.multiply(bc));
    final RationalFloat cda = cez.multiply(da).add(
      dez.multiply(ac)).add(aez.multiply(cd));
    final RationalFloat dab = dez.multiply(ab).add(
      aez.multiply(bd)).add(bez.multiply(da));

    final RationalFloat alift = aex.square().add(aey.square().add(aez.square()));
    final RationalFloat blift = bex.square().add(bey.square().add(bez.square()));
    final RationalFloat clift = cex.square().add(cey.square().add(cez.square()));
    final RationalFloat dlift = dex.square().add(dey.square().add(dez.square()));

    return dlift.multiply(abc)
           .subtract(clift.multiply(dab))
           .add(blift.multiply(cda))
           .subtract(alift.multiply(bcd))
           .doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public RationalFloatTetrahedron3D () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
