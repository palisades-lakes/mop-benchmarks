package mop.java.geometry.triangle.macro;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.geometry.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-08-18
 */

// strictfp unnecessary for JDK17 and later
public final class FastMacro extends Triangle2D {

  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------

  public final double twiceSignedArea () {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();
    final double acx = pa.getX() - pc.getX();
    final double bcx = pb.getX() - pc.getX();
    final double acy = pa.getY() - pc.getY();
    final double bcy = pb.getY() - pc.getY();
    return acx * bcy - acy * bcx; }

  //--------------------------------------------------------------------
  // inCircle
  //--------------------------------------------------------------------

  public final double inCircleDistance (final Vector2D p) {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();
    double adx, ady, bdx, bdy, cdx, cdy;
    double abdet, bcdet, cadet;
    double alift, blift, clift;

    adx = pa.getX() - p.getX();
    ady = pa.getY() - p.getY();
    bdx = pb.getX() - p.getX();
    bdy = pb.getY() - p.getY();
    cdx = pc.getX() - p.getX();
    cdy = pc.getY() - p.getY();

    abdet = adx * bdy - bdx * ady;
    bcdet = bdx * cdy - cdx * bdy;
    cadet = cdx * ady - adx * cdy;
    alift = adx * adx + ady * ady;
    blift = bdx * bdx + bdy * bdy;
    clift = cdx * cdx + cdy * cdy;

    return alift * bcdet + blift * cadet + clift * abdet; }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private FastMacro (final Vector2D a,
                     final Vector2D b,
                     final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new FastMacro(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
