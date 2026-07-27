package mop.java.geometry.tetrahedron.shewchuk;

import mop.java.geometry.tetrahedron.Tetrahedron3D;
import mop.java.numbers.Hilo;
import mop.java.numbers.XDouble;
import org.apache.commons.geometry.euclidean.threed.Vector3D;

/** Exact tests.  Robust.
 * <br>
 * Some unclarity about the meaning of 'exact' here.
 * <br>
 *   This version's priority is correctness, and simplicity.
 *   Later versions can optimize guided by benchmarks and
 *   profiling.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-27
 */

public final class Exact extends Tetrahedron3D {

  public final boolean signedVolumeExact () { return true; }

  public final double signedVolume () {
    final Vector3D pa = getP0();
    final Vector3D pb = getP1();
    final Vector3D pc = getP2();
    final Vector3D pd = getP3();

    final Hilo axby = Hilo.product(pa.getX(),pb.getY());
    final Hilo bxay = Hilo.product(pb.getX(),pa.getY());
    final XDouble ab = XDouble.subtract(axby, bxay);
    final Hilo bxcy = Hilo.product(pb.getX(),pc.getY());
    final Hilo cxby = Hilo.product(pc.getX(),pb.getY());
    final XDouble bc = XDouble.subtract(bxcy, cxby);
    final Hilo cxdy = Hilo.product(pc.getX(),pd.getY());
    final Hilo dxcy = Hilo.product(pd.getX(),pc.getY());
    final XDouble cd = XDouble.subtract(cxdy, dxcy);
    final Hilo dxay = Hilo.product(pd.getX(),pa.getY());
    final Hilo axdy = Hilo.product(pa.getX(),pd.getY());
    final XDouble da = XDouble.subtract(dxay, axdy);
    final Hilo axcy = Hilo.product(pa.getX(),pc.getY());
    final Hilo cxay = Hilo.product(pc.getX(),pa.getY());
    final XDouble ac = XDouble.subtract(axcy, cxay);
    final Hilo bxdy = Hilo.product(pb.getX(),pd.getY());
    final Hilo dxby = Hilo.product(pd.getX(),pb.getY());
    final XDouble bd = XDouble.subtract(bxdy, dxby);

    final XDouble cda = cd.add(da).add(ac);
    final XDouble dab = da.add(ab).add(bd);
    final XDouble abc = ab.add(bc).subtract(ac);
    final XDouble bcd = bc.add(cd).subtract(bd);

    final XDouble adet = bcd.multiply(pa.getZ());
    final XDouble bdet = cda.multiply(-pb.getZ());
    final XDouble cdet = dab.multiply(pc.getZ());
    final XDouble ddet = abc.multiply(-pd.getZ());

    return adet.add(bdet).add(cdet).add(ddet).doubleValue(); }


  //--------------------------------------------------------------------
  // inSphere
  //--------------------------------------------------------------------

  public final double inSphere (final Vector3D p) {

    final Vector3D pa = getP0();
    final Vector3D pb = getP1();
    final Vector3D pc = getP2();
    final Vector3D pd = getP3();

    // TODO: XDouble.cross2D?
    final XDouble ab = XDouble.subtract(Hilo.product(pa.getX(), pb.getY()),
                                        Hilo.product(pb.getX(),pa.getY()));
    final XDouble bc = XDouble.subtract(Hilo.product(pb.getX(), pc.getY()),
                                        Hilo.product(pc.getX(),pb.getY()));
    final XDouble cd = XDouble.subtract(Hilo.product(pc.getX(), pd.getY()),
                                        Hilo.product(pd.getX(),pc.getY()));
    final XDouble de = XDouble.subtract(Hilo.product(pd.getX(), p.getY()),
                                        Hilo.product(p.getX(), pd.getY()));
    final XDouble ea = XDouble.subtract(Hilo.product(p.getX(), pa.getY()),
                                        Hilo.product(pa.getX(),
                                                     p.getY()));
    final XDouble ac = XDouble.subtract(Hilo.product(pa.getX(), pc.getY()),
                                        Hilo.product(pc.getX(),pa.getY()));
    final XDouble bd = XDouble.subtract(Hilo.product(pb.getX(), pd.getY()),
                                        Hilo.product(pd.getX(),pb.getY()));
    final XDouble ce = XDouble.subtract(Hilo.product(pc.getX(), p.getY()),
                                        Hilo.product(p.getX(), pc.getY()));
    final  XDouble da = XDouble.subtract(Hilo.product(pd.getX(), pa.getY()),
                                         Hilo.product(pa.getX(),pd.getY()));
    final XDouble eb = XDouble.subtract(Hilo.product(p.getX(), pb.getY()),
                                        Hilo.product(pb.getX(),
                                                     p.getY()));
    final XDouble abc = ab.multiply(pc.getZ())
                          .add(bc.multiply(pa.getZ()))
                          .add(ac.multiply(-pb.getZ()));
    final XDouble bcd = bc.multiply(pd.getZ())
                          .add(cd.multiply(pb.getZ()))
                          .add(bd.multiply(-pc.getZ()));
    final XDouble cde = cd.multiply(p.getZ())
                          .add(de.multiply(pc.getZ()))
                          .add(ce.multiply(-pd.getZ()));
    final XDouble dea = de.multiply(pa.getZ())
                          .add(ea.multiply(pd.getZ()))
                          .add(da.multiply(-p.getZ()));
    final XDouble eab = ea.multiply(pb.getZ())
                          .add(ab.multiply(p.getZ()))
                          .add(eb.multiply(-pa.getZ()));
    final XDouble abd = ab.multiply(pd.getZ())
                          .add(bd.multiply(pa.getZ()))
                          .add(da.multiply(pb.getZ()));
    final XDouble bce = bc.multiply(p.getZ())
                          .add(ce.multiply(pb.getZ()))
                          .add(eb.multiply(pc.getZ()));
    final XDouble cda = cd.multiply(pa.getZ())
                          .add(da.multiply(pc.getZ()))
                          .add(ac.multiply(pd.getZ()));
    final XDouble deb = de.multiply(pb.getZ())
                          .add(eb.multiply(pd.getZ()))
                          .add(bd.multiply(p.getZ()));
    final XDouble eac = ea.multiply(pc.getZ())
                          .add(ac.multiply(p.getZ()))
                          .add(ce.multiply(pa.getZ()));
    final XDouble bcde = cde.add(bce).subtract(deb.add(bcd));
    final XDouble adet = bcde.multiply(pa.getX()).multiply(pa.getX())
                             .add(bcde.multiply(pa.getY()).multiply(pa.getY()))
                             .add(bcde.multiply(pa.getZ()).multiply(pa.getZ()));

    final XDouble cdea = dea.add(cda).subtract(eac.add(cde));
    final XDouble bdet = cdea.multiply(pb.getX()).multiply(pb.getX())
                             .add(cdea.multiply(pb.getY()).multiply(pb.getY()))
                             .add(cdea.multiply(pb.getZ()).multiply(pb.getZ()));

    final XDouble deab = eab.add(deb).subtract(abd.add(dea));
    final XDouble cdet = deab.multiply(pc.getX()).multiply(pc.getX())
                             .add(deab.multiply(pc.getY()).multiply(pc.getY()))
                             .add(deab.multiply(pc.getZ()).multiply(pc.getZ()));

    final XDouble eabc = abc.add(eac).subtract(bce.add(eab));
    final XDouble ddet = eabc.multiply(pd.getX()).multiply(pd.getX())
                             .add(eabc.multiply(pd.getY()).multiply(pd.getY()))
                             .add(eabc.multiply(pd.getZ()).multiply(pd.getZ()));

    final XDouble abcd = bcd.add(abd).subtract(cda.add(abc));
    final XDouble edet = abcd.multiply(p.getX()).multiply(p.getX())
                             .add(abcd.multiply(p.getY()).multiply(
                               p.getY()))
                             .add(abcd.multiply(p.getZ()).multiply(
                               p.getZ()));

    return adet.add(bdet).add(cdet).add(ddet).add(edet).doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private Exact (final Vector3D a,
                 final Vector3D b,
                 final Vector3D c,
                 final Vector3D d)  {
    super(a,b,c,d); }

  public static final Tetrahedron3D of (final Vector3D a,
                                        final Vector3D b,
                                        final Vector3D c,
                                        final Vector3D d) {
    return new Exact(a, b, c, d); }

  /** Convert between tetrahedra classes. */
  public static final Tetrahedron3D from (final Tetrahedron3D t) {
    return of(t.getP0(),t.getP1(),t.getP2(),t.getP3()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
