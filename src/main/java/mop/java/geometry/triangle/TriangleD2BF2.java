package mop.java.geometry.triangle;

import mop.java.geometry.euclidean.VectorD2;

/** Minimal triangle with VectorD2 vertices, caching reusable values.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-27
 */

public final class TriangleD2BF2 extends TriangleD2Lazy {

  private TriangleBF2 _triangleBF2;
  private final TriangleBF2 getTriangleBF2 () {
    if (null == _triangleBF2) {
      _triangleBF2 = (TriangleBF2) TriangleBF2.from(this); }
    return _triangleBF2; }

  private double _areaBound = Double.NaN;
  public final double areaBound () {
    if (Double.isNaN(_areaBound)) {
      _areaBound = super.areaBound(); }
    return _areaBound; }

  //--------------------------------------------------------------------

  public final double twiceSignedArea () {
    final double a = super.twiceSignedArea();
    if (Math.abs(a) > areaBound()) { return a; }
    return getTriangleBF2().twiceSignedArea(); }

  //--------------------------------------------------------------------

  public final double inCircleDistance (final VectorD2 p) {

    final double d = super.inCircleDistance(p);
    final double e = inCircleBound(p);
    if (Math.abs(d) > e) { return d; }
    return getTriangleBF2().inCircleDistance(p); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public TriangleD2BF2 (final VectorD2 a,
                        final VectorD2 b,
                        final VectorD2 c)  {
    super(a,b,c); }

  public static final TriangleR2 of (final VectorD2 a,
                                     final VectorD2 b,
                                     final VectorD2 c) {
    return new TriangleD2BF2(a, b, c); }

  /** Convert other triangle classes. */

  public static final TriangleR2 from (final TriangleR2 t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
