package mop.java.geometry.predicates.tetrahedron;

import mop.java.numbers.BigFloat;
import org.apache.commons.geometry.euclidean.threed.Vector3D;

/** Standard calculations implemented in BigFloat.
 * Should be exact, up to BigFloat resolution.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-06
 */

public final class BigFloatTetrahedron3D extends Tetrahedron3D {

  //--------------------------------------------------------------------

  public final boolean signedVolumeExact () { return true; }

  //--------------------------------------------------------------------
  // TODO: rewrite as vector operations

  public final double signedVolume (final Vector3D pa,
                                    final Vector3D pb,
                                    final Vector3D pc,
                                    final Vector3D pd) {
    final BigFloat ax = BigFloat.valueOf(pa.getX());
    final BigFloat ay = BigFloat.valueOf(pa.getY());
    final BigFloat az = BigFloat.valueOf(pa.getZ());
    final BigFloat bx = BigFloat.valueOf(pb.getX());
    final BigFloat by = BigFloat.valueOf(pb.getY());
    final BigFloat bz = BigFloat.valueOf(pb.getZ());
    final BigFloat cx = BigFloat.valueOf(pc.getX());
    final BigFloat cy = BigFloat.valueOf(pc.getY());
    final BigFloat cz = BigFloat.valueOf(pc.getZ());
    final BigFloat dx = BigFloat.valueOf(pd.getX());
    final BigFloat dy = BigFloat.valueOf(pd.getY());
    final BigFloat dz = BigFloat.valueOf(pd.getZ());
    final BigFloat adx = ax.subtract(dx);
    final BigFloat bdx = bx.subtract(dx);
    final BigFloat cdx = cx.subtract(dx);
    final BigFloat ady = ay.subtract(dy);
    final BigFloat bdy = by.subtract(dy);
    final BigFloat cdy = cy.subtract(dy);
    final BigFloat adz = az.subtract(dz);
    final BigFloat bdz = bz.subtract(dz);
    final BigFloat cdz = cz.subtract(dz);

    return
      adx.multiply(bdy.multiply(cdz).subtract(bdz.multiply(cdy)))
         .add(
           bdx.multiply(cdy.multiply(adz).subtract(cdz.multiply(ady)))
             )
         .add(
           cdx.multiply(ady.multiply(bdz).subtract(adz.multiply(bdy)))
             )
         .doubleValue(); }

  //--------------------------------------------------------------------

  public final boolean inSphereExact () { return true; }

  public final double inSphere (final Vector3D pa,
                                final Vector3D pb,
                                final Vector3D pc,
                                final Vector3D pd,
                                final Vector3D pe) {
    final BigFloat ax = BigFloat.valueOf(pa.getX());
    final BigFloat ay = BigFloat.valueOf(pa.getY());
    final BigFloat az = BigFloat.valueOf(pa.getZ());
    final BigFloat bx = BigFloat.valueOf(pb.getX());
    final BigFloat by = BigFloat.valueOf(pb.getY());
    final BigFloat bz = BigFloat.valueOf(pb.getZ());
    final BigFloat cx = BigFloat.valueOf(pc.getX());
    final BigFloat cy = BigFloat.valueOf(pc.getY());
    final BigFloat cz = BigFloat.valueOf(pc.getZ());
    final BigFloat dx = BigFloat.valueOf(pd.getX());
    final BigFloat dy = BigFloat.valueOf(pd.getY());
    final BigFloat dz = BigFloat.valueOf(pd.getZ());
    final BigFloat ex = BigFloat.valueOf(pe.getX());
    final BigFloat ey = BigFloat.valueOf(pe.getY());
    final BigFloat ez = BigFloat.valueOf(pe.getZ());
    final BigFloat aex = ax.subtract(ex);
    final BigFloat bex = bx.subtract(ex);
    final BigFloat cex = cx.subtract(ex);
    final BigFloat dex = dx.subtract(ex);
    final BigFloat aey = ay.subtract(ey);
    final BigFloat bey = by.subtract(ey);
    final BigFloat cey = cy.subtract(ey);
    final BigFloat dey = dy.subtract(ey);
    final BigFloat aez = az.subtract(ez);
    final BigFloat bez = bz.subtract(ez);
    final BigFloat cez = cz.subtract(ez);
    final BigFloat dez = dz.subtract(ez);

    final BigFloat ab = aex.multiply(bey).subtract(bex.multiply(aey));
    final BigFloat bc = bex.multiply(cey).subtract(cex.multiply(bey));
    final BigFloat cd = cex.multiply(dey).subtract(dex.multiply(cey));
    final BigFloat da = dex.multiply(aey).subtract(aex.multiply(dey));

    final BigFloat ac = aex.multiply(cey).subtract(cex.multiply(aey));
    final BigFloat bd = bex.multiply(dey).subtract(dex.multiply(bey));

    final BigFloat abc =
      aez.multiply(bc).subtract(bez.multiply(ac)).add(cez.multiply(ab));
    final BigFloat bcd = bez.multiply(cd).subtract(
      cez.multiply(bd)).add(dez.multiply(bc));
    final BigFloat cda = cez.multiply(da).add(
      dez.multiply(ac)).add(aez.multiply(cd));
    final BigFloat dab = dez.multiply(ab).add(
      aez.multiply(bd)).add(bez.multiply(da));

    final BigFloat alift = aex.square().add(aey.square().add(aez.square()));
    final BigFloat blift = bex.square().add(bey.square().add(bez.square()));
    final BigFloat clift = cex.square().add(cey.square().add(cez.square()));
    final BigFloat dlift = dex.square().add(dey.square().add(dez.square()));

    return
      dlift.multiply(abc).subtract(clift.multiply(dab))
           .add(blift.multiply(cda).subtract(alift.multiply(bcd)))
           .doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public BigFloatTetrahedron3D () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
