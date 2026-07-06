package mop.java.geometry.predicates.triangle.macro;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.geometry.predicates.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-06
 */

// strictfp unnecessary for JDK17 and later
public final class FastMacro extends Triangle2D {

  //--------------------------------------------------------------------
  // orient2d
  //--------------------------------------------------------------------

  public final double signedArea (final Vector2D pa,
                                  final Vector2D pb,
                                  final Vector2D pc) {
    final double acx = pa.getX() - pc.getX();
    final double bcx = pb.getX() - pc.getX();
    final double acy = pa.getY() - pc.getY();
    final double bcy = pb.getY() - pc.getY();
    return acx * bcy - acy * bcx; }

  //--------------------------------------------------------------------
  // inCircle
  //--------------------------------------------------------------------

  public final double inCircle (final Vector2D pa,
                                final Vector2D pb,
                                final Vector2D pc,
                                final Vector2D pd) {
    double adx, ady, bdx, bdy, cdx, cdy;
    double abdet, bcdet, cadet;
    double alift, blift, clift;

    adx = pa.getX() - pd.getX();
    ady = pa.getY() - pd.getY();
    bdx = pb.getX() - pd.getX();
    bdy = pb.getY() - pd.getY();
    cdx = pc.getX() - pd.getX();
    cdy = pc.getY() - pd.getY();

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
  // TODO: singleton?

  public FastMacro () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
