package mop.java.geometry.triangle.jts;

import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.triangle.Triangle2D;
import org.locationtech.jts.math.DD;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-21
 */

public final class DDNormalized extends Triangle2D {

  // TODO: cache DD subtract during construction

//--------------------------------------------------------------------

  @Override
  public final double twiceSignedArea () {
    return DDFast.triAreaDDFast(getP0(),getP1(),getP2()).doubleValue(); }

  public final boolean isOrientationRobust () { return true; }

  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  @Override
  public final double inCircleDistance (final VectorD2 p) {
    final VectorD2 pa = getP0();
    final VectorD2 pb = getP1();
    final VectorD2 pc = getP2();
    DD adx = DD.valueOf(pa.getX()).selfSubtract(p.getX());
    DD ady = DD.valueOf(pa.getY()).selfSubtract(p.getY());
    DD bdx = DD.valueOf(pb.getX()).selfSubtract(p.getX());
    DD bdy = DD.valueOf(pb.getY()).selfSubtract(p.getY());
    DD cdx = DD.valueOf(pc.getX()).selfSubtract(p.getX());
    DD cdy = DD.valueOf(pc.getY()).selfSubtract(p.getY());

    DD abdet = adx.multiply(bdy).selfSubtract(bdx.multiply(ady));
    DD bcdet = bdx.multiply(cdy).selfSubtract(cdx.multiply(bdy));
    DD cadet = cdx.multiply(ady).selfSubtract(adx.multiply(cdy));
    DD alift = adx.multiply(adx).selfAdd(ady.multiply(ady));
    DD blift = bdx.multiply(bdx).selfAdd(bdy.multiply(bdy));
    DD clift = cdx.multiply(cdx).selfAdd(cdy.multiply(cdy));

    DD sum = alift.selfMultiply(bcdet)
                  .selfAdd(blift.selfMultiply(cadet))
                  .selfAdd(clift.selfMultiply(abdet));

    return sum.doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  private DDNormalized (final VectorD2 a,
                        final VectorD2 b,
                        final VectorD2 c)  {
    super(a,b,c); }

  public static final Triangle2D of (final VectorD2 a,
                                     final VectorD2 b,
                                     final VectorD2 c) {
    return new DDNormalized(a,b,c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
