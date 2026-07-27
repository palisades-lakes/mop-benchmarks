package mop.java.geometry.triangle.macro;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into Expansion manipulation and fast, slow, exact, adaptive
// algorithm classes

import mop.java.geometry.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

import static mop.java.geometry.Expansion.EPSILON;

/** Adaptive tests.  Robust.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-27
 */

// strictfp unnecessary for JDK17 and later
public final class DefaultMacro extends Triangle2D {

  //--------------------------------------------------------------------
  private static final double ccwerrboundA =
    (3.0 + 16.0 * EPSILON) * EPSILON;

  public final double signedArea () {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();
    double detleft, detright, det;
    double detsum, errbound;

    detleft = (pa.getX() - pc.getX()) * (pb.getY() - pc.getY());
    detright = (pa.getY() - pc.getY()) * (pb.getX() - pc.getX());
    det = detleft - detright;

    if (detleft > 0.0) {
      if (detright <= 0.0) { return det; }
      else { detsum = detleft + detright; } }
    else if (detleft < 0.0) {
      if (detright >= 0.0) { return det; }
      else { detsum = -detleft - detright; }
    }
    else {
      return det;
    }

    errbound = ccwerrboundA * detsum;
    if ((det >= errbound) || (-det >= errbound)) {
      return det;
    }

    return AdaptMacro.signedArea(getP0(),getP1(),getP2(), detsum); }

  //--------------------------------------------------------------------
  // inCircle
  //--------------------------------------------------------------------
  private static final double iccerrboundA =
    (10.0 + 96.0 * EPSILON) * EPSILON;

  public final double inCircle (final Vector2D p) {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();

    double adx, bdx, cdx, ady, bdy, cdy;
    double bdxcdy, cdxbdy, cdxady, adxcdy, adxbdy, bdxady;
    double alift, blift, clift;
    double det;
    double permanent, errbound;

    adx = pa.getX() - p.getX();
    bdx = pb.getX() - p.getX();
    cdx = pc.getX() - p.getX();
    ady = pa.getY() - p.getY();
    bdy = pb.getY() - p.getY();
    cdy = pc.getY() - p.getY();

    bdxcdy = bdx * cdy;
    cdxbdy = cdx * bdy;
    alift = adx * adx + ady * ady;

    cdxady = cdx * ady;
    adxcdy = adx * cdy;
    blift = bdx * bdx + bdy * bdy;

    adxbdy = adx * bdy;
    bdxady = bdx * ady;
    clift = cdx * cdx + cdy * cdy;

    det = alift * (bdxcdy - cdxbdy)
      + blift * (cdxady - adxcdy)
      + clift * (adxbdy - bdxady);

    permanent =
      (((bdxcdy) >= 0.0 ? (bdxcdy) : -(bdxcdy)) + ((cdxbdy) >= 0.0
                                                   ? (cdxbdy)
                                                   : -(cdxbdy))) * alift
        + (((cdxady) >= 0.0 ? (cdxady) : -(cdxady)) + ((adxcdy) >= 0.0
                                                       ? (adxcdy)
                                                       : -(adxcdy))) * blift
        + (((adxbdy) >= 0.0 ? (adxbdy) : -(adxbdy)) + ((bdxady) >= 0.0
                                                       ? (bdxady)
                                                       : -(bdxady))) * clift;
    errbound = iccerrboundA * permanent;
    if ((det > errbound) || (-det > errbound)) {
      return det;
    }

    return AdaptMacro.inCircle(getP0(),getP1(),getP2(), p, permanent);
  }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private DefaultMacro (final Vector2D a,
                final Vector2D b,
                final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new DefaultMacro(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
