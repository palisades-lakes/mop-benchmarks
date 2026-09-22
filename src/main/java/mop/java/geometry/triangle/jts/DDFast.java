package mop.java.geometry.triangle.jts;

import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.triangle.Triangle2D;
import org.locationtech.jts.math.DD;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-21
 */

public final class DDFast extends Triangle2D {

//--------------------------------------------------------------------

  public static DD triAreaDDFast (final VectorD2 a,
                                  final VectorD2 b,
                                  final VectorD2 c) {

    DD t1 = DD.valueOf(b.getX()).selfSubtract(a.getX())
              .selfMultiply(
                DD.valueOf(c.getY()).selfSubtract(a.getY()));

    DD t2 = DD.valueOf(b.getY()).selfSubtract(a.getY())
              .selfMultiply(
                DD.valueOf(c.getX()).selfSubtract(a.getX()));

    return t1.selfSubtract(t2); }


  @Override
  public final double twiceSignedArea () {
    return triAreaDDFast(getP0(),getP1(),getP2()).doubleValue(); }

  public final boolean isOrientationRobust () { return true; }

  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  @Override
  public final double inCircleDistance (final VectorD2 p) {
    final VectorD2 pa = getP0();
    final VectorD2 pb = getP1();
    final VectorD2 pc = getP2();
    DD aTerm = (DD.sqr(pa.getX()).selfAdd(DD.sqr(pa.getY())))
      .selfMultiply(triAreaDDFast(pb, pc, p));
    DD bTerm = (DD.sqr(pb.getX()).selfAdd(DD.sqr(pb.getY())))
      .selfMultiply(triAreaDDFast(pa, pc, p));
    DD cTerm = (DD.sqr(pc.getX()).selfAdd(DD.sqr(pc.getY())))
      .selfMultiply(triAreaDDFast(pa, pb, p));
    DD pTerm = (DD.sqr(p.getX()).selfAdd(DD.sqr(p.getY())))
      .selfMultiply(triAreaDDFast(getP0(),getP1(),getP2()));

    DD sum = aTerm.selfSubtract(bTerm).selfAdd(cTerm).selfSubtract(pTerm);
    return sum.doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private DDFast (final VectorD2 a,
                  final VectorD2 b,
                  final VectorD2 c)  {
    super(a,b,c); }

  public static final Triangle2D of (final VectorD2 a,
                                     final VectorD2 b,
                                     final VectorD2 c) {
    return new DDFast(a,b,c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
