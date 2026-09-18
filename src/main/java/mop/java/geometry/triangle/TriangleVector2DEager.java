package mop.java.geometry.triangle;

import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Triangle with Vector2D vertices, precomputing reusable values.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-17
 */

public final class TriangleVector2DEager extends Triangle2D {

  // precomputed vector result of translating p0 to origin,
  // and related quantities

  private final Vector2D _v10;
  private final Vector2D getV10 () { return _v10; }

  private final double _v10Norm2;
  private final double getV10Norm2 () { return _v10Norm2; }

  private final Vector2D _v20;
  private final Vector2D getV20 () { return _v20; }

  private final double _v20Norm2;
  private final double getV20Norm2 () { return _v20Norm2; }

  private final double _V20xV10;
  private final double getV20xV10 () { return _V20xV10; }

  //--------------------------------------------------------------------

  /** AKA wedge product, cross product (in 3D), ... */
  private static final double blade (final Vector2D v0,
                                     final Vector2D v1) {
    // TODO: more accurate version via fma?
    return (v0.getX()*v1.getY()) - (v0.getY()*v1.getX()); }

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return false; }

  public final double twiceSignedArea () { return -getV20xV10(); }

  //--------------------------------------------------------------------

  private static final double dot (final double x0,
                                   final double y0,
                                   final double z0,
                                   final double x1,
                                   final double y1,
                                   final double z1) {
    return x0*x1 + y0*y1 + z0*z1; }

  //--------------------------------------------------------------------

  public final boolean inCircleDistanceExact () { return false; }

  public final double inCircleDistance (final Vector2D p) {

    final Vector2D vp0 = p.subtract(getP0());

    final double bxp = blade(getV10(),vp0);
    final double bxc = getV20xV10();
    final double pxc = blade(vp0,getV20());

    final double p2 = vp0.normSq();
    final double b2 = getV10Norm2();
    final double c2 = getV20Norm2();

    return dot(p2,b2,c2,bxc,pxc,bxp); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private TriangleVector2DEager (final Vector2D a,
                                 final Vector2D b,
                                 final Vector2D c)  {
    super(a,b,c);
    _v10 = getP1().subtract(getP0());
    _v10Norm2 = getV10().normSq();
    _v20 = getP2().subtract(getP0());
    _v20Norm2 = getV20().normSq();
    _V20xV10 = blade(getV20(),getV10()); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new TriangleVector2DEager(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
