package mop.java.geometry.triangle.jts;

import mop.java.geometry.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.locationtech.jts.math.DD;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-27
 */

public final class DDFast extends Triangle2D {

//--------------------------------------------------------------------

  public static DD triAreaDDFast (final Vector2D a,
                                  final Vector2D b,
                                  final Vector2D c) {

    DD t1 = DD.valueOf(b.getX()).selfSubtract(a.getX())
              .selfMultiply(
                DD.valueOf(c.getY()).selfSubtract(a.getY()));

    DD t2 = DD.valueOf(b.getY()).selfSubtract(a.getY())
              .selfMultiply(
                DD.valueOf(c.getX()).selfSubtract(a.getX()));

    return t1.selfSubtract(t2); }


  @Override
  public final double signedArea () {
    return triAreaDDFast(getP0(),getP1(),getP2()).doubleValue(); }

  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  @Override
  public final double inCircle (final Vector2D p) {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();
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

  private DDFast (final Vector2D a,
                  final Vector2D b,
                  final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new DDFast(a,b,c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
