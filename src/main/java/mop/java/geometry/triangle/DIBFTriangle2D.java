package mop.java.geometry.triangle;

import mop.java.numbers.DoubleInterval;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Compute intervals using a <code>DoubleIntervalTrianmgle2D</code>.
 * If interval contains 0.0, fall back to lazy cached
 * <code>BigFloatTriangle</code>
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-08-18
 */

public final class DIBFTriangle2D extends Triangle2D {

  // Wrap an instance of DoubleIntervalTriangle2D, so that this gets
  // any performance improvements without having to repeat the edits
  // here.

  private final DoubleIntervalTriangle2D diTriangle;
  private final DoubleIntervalTriangle2D getDiTriangle () {
    return diTriangle; }

  private BigFloatTriangle2D bfTriangle;
  private final BigFloatTriangle2D getBfTriangle () {
    if (null==bfTriangle) {
      bfTriangle = (BigFloatTriangle2D) BigFloatTriangle2D.from(this); }
    return bfTriangle; }

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return false; }

   public final double twiceSignedArea () {
    final DoubleInterval interval =
      getDiTriangle().twiceSignedAreaInterval();
    if (interval.containsZero()) {
//      System.out.println("twiceSignedArea: " + interval);
//      System.out.println(this);
//      System.out.println("twiceSignedArea: " + interval);
      return getBfTriangle().twiceSignedArea(); }
    return interval.doubleValue(); }

  public final boolean isOrientationRobust () { return true; }

  //--------------------------------------------------------------------

  public final boolean inCircleExact () { return false; }

  public final double inCircle (final Vector2D p) {
    final DoubleInterval interval =
      getDiTriangle().inCircleInterval(p);
    if (interval.containsZero()) {
//      System.out.println("inCircle: " + interval);
//      System.out.println(this);
//      System.out.println(p);
      return getBfTriangle().inCircle(p); }
    return interval.doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private DIBFTriangle2D (final Vector2D a,
                          final Vector2D b,
                          final Vector2D c)  {
    super(a,b,c);
    diTriangle =
      (DoubleIntervalTriangle2D) DoubleIntervalTriangle2D.of(a, b, c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new DIBFTriangle2D(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
