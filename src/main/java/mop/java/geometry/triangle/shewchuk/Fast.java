package mop.java.geometry.triangle.shewchuk;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.geometry.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Approximate predicates, nonrobust.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-08-18
 */

public final class Fast extends Triangle2D {

  //--------------------------------------------------------------------

  public final double twiceSignedArea () {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();

    final double acx = pa.getX() - pc.getX();
    final double bcx = pb.getX() - pc.getX();
    final double acy = pa.getY() - pc.getY();
    final double bcy = pb.getY() - pc.getY();
    return (acx * bcy) - (acy * bcx); }

  //--------------------------------------------------------------------

  public final double inCircleDistance (final Vector2D pd) {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();

    final double adx = pa.getX() - pd.getX();
    final double ady = pa.getY() - pd.getY();
    final double bdx = pb.getX() - pd.getX();
    final double bdy = pb.getY() - pd.getY();
    final double cdx = pc.getX() - pd.getX();
    final double cdy = pc.getY() - pd.getY();

    final double abdet = (adx * bdy) - (bdx * ady);
    final double bcdet = (bdx * cdy) - (cdx * bdy);
    final double cadet = (cdx * ady) - (adx * cdy);
    final double alift = (adx * adx) + (ady * ady);
    final double blift = (bdx * bdx) + (bdy * bdy);
    final double clift = (cdx * cdx) + (cdy * cdy);

    return (alift * bcdet) + (blift * cadet) + (clift * abdet); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private Fast (final Vector2D a,
                final Vector2D b,
                final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new Fast(a,b,c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
