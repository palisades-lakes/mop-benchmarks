package mop.java.geometry.triangle;

import mop.java.geometry.euclidean.VectorD2;

/** Minimal triangle with VectorD2 vertices, caching reusable values.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-21
 */

public final class TriangleD2Lazy extends Triangle2D {

  // cache vector result of translating p0 to origin,
  // and related quantities

  private VectorD2 _v10;
  private final VectorD2 getV10 () {
    if (null == _v10) { _v10 = getP1().subtract(getP0()); }
    return _v10; }

  private double _v10Norm2 = Double.NaN;
  private final double getV10Norm2 () {
    // TODO: what if computed norm is NaN?
    if (Double.isNaN(_v10Norm2)) { _v10Norm2 = getV10().l2norm2(); }
    return _v10Norm2; }

  private VectorD2 _v20;
  private final VectorD2 getV20 () {
    if (null == _v20) { _v20 = getP2().subtract(getP0()); }
    return _v20; }

  private double _v20Norm2 = Double.NaN;
  private final double getV20Norm2 () {
    // TODO: what if computed norm is NaN?
    if (Double.isNaN(_v20Norm2)) { _v20Norm2 = getV20().l2norm2(); }
    return _v20Norm2; }

  /** AKA wedge product, cross product (in 3D), ... */
  private static final double wedge (final VectorD2 v0,
                                        final VectorD2 v1) {
    // TODO: more accurate version via fma?
    return (v0.getX()*v1.getY()) - (v0.getY()*v1.getX()); }

  private double _V20xV10 = Double.NaN;
  private final double getV20xV10 () {
    // TODO: what if computed corss product is NaN?
    if (Double.isNaN(_V20xV10)) { _V20xV10 = wedge(getV20(),getV10()); }
    return _V20xV10; }

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

  public final double inCircleDistance (final VectorD2 p) {

    final VectorD2 vp0 = p.subtract(getP0());

    final double bxp = wedge(getV10(),vp0);
    final double bxc = getV20xV10();
    final double pxc = wedge(vp0,getV20());

    final double p2 = vp0.l2norm2();
    final double b2 = getV10Norm2();
    final double c2 = getV20Norm2();

    return dot(p2,b2,c2,bxc,pxc,bxp); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private TriangleD2Lazy (final VectorD2 a,
                          final VectorD2 b,
                          final VectorD2 c)  {
    super(a,b,c); }

  public static final Triangle2D of (final VectorD2 a,
                                     final VectorD2 b,
                                     final VectorD2 c) {
    return new TriangleD2Lazy(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
