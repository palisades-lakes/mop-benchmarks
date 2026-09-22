package mop.java.geometry.triangle;

import mop.java.geometry.euclidean.VectorD2;
import mop.java.numbers.RoundingInterval;

/** Compute intervals using a <code>RelaxedIntervalTrianmgle2D</code>.
 * If interval contains 0.0, fall back to lazy cached
 * <code>BigFloatTriangle</code>
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-21
 */

public final class RoBfTriangle2D extends Triangle2D {

  // Wrap an instance of RoundingIntervalTriangle2D, so that this gets
  // any performance improvements without having to repeat the edits
  // here.

  private final RoundingIntervalTriangle2D diTriangle;
  private final RoundingIntervalTriangle2D getDiTriangle () {
    return diTriangle; }

  private TriangleBF2 bfTriangle;
  private final TriangleBF2 getBfTriangle () {
    if (null==bfTriangle) {
      bfTriangle = (TriangleBF2) TriangleBF2.from(this); }
    return bfTriangle; }

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return false; }

   public final double twiceSignedArea () {
    final RoundingInterval interval =
      getDiTriangle().twiceSignedAreaInterval();
    if (interval.containsZero()) {
//      System.out.println("twiceSignedArea: " + interval);
//      System.out.println(this);
//      System.out.println("twiceSignedArea: " + interval);
      return getBfTriangle().twiceSignedArea(); }
    return interval.doubleValue(); }

  public final boolean isOrientationRobust () { return true; }

  //--------------------------------------------------------------------

  public final boolean inCircleDistanceExact () { return false; }

  public final double inCircleDistance (final VectorD2 p) {
    final RoundingInterval interval =
      getDiTriangle().inCircleInterval(p);
    if (interval.containsZero()) {
//      System.out.println("inCircle: " + interval);
//      System.out.println(this);
//      System.out.println(p);
      return getBfTriangle().inCircleDistance(p); }
    return interval.doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private RoBfTriangle2D (final VectorD2 a,
                          final VectorD2 b,
                          final VectorD2 c)  {
    super(a,b,c);
    diTriangle =
      (RoundingIntervalTriangle2D) RoundingIntervalTriangle2D.of(a, b, c); }

  public static final Triangle2D of (final VectorD2 a,
                                     final VectorD2 b,
                                     final VectorD2 c) {
    return new RoBfTriangle2D(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
