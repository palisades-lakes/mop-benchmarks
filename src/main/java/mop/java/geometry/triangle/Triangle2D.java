package mop.java.geometry.triangle;

import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.triangle.jts.*;
import mop.java.numbers.DoubleInterval;
import mop.java.numbers.RoundingInterval;

import java.util.List;

/** Triangles "embedded" in <code>R<sup>2</sup></code>>.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-25
 */

public abstract class Triangle2D {

  private final VectorD2 p0;
  private final VectorD2 p1;
  private final VectorD2 p2;
  public final VectorD2 getP0 () { return p0; }
  public final VectorD2 getP1 () { return p1; }
  public final VectorD2 getP2 () { return p2; }

  public static final List<Triangle2D> makeTriangles (final Triangle2D t) {
    final Triangle2D
      doubleIntervalTriangle = RelaxedIntervalTriangle2D.from(t);
    final Triangle2D roundingIntervalTriangle = RoundingIntervalTriangle2D.from(t);
    final Triangle2D shewchukIntervalTriangle = ShewchukIntervalTriangle2D.from(t);
    final Triangle2D bf2 = TriangleBF2.from(t);
    final Triangle2D d2eager = TriangleD2Eager.from(t);
    final Triangle2D d2lazy = TriangleD2Lazy.from(t);
    final Triangle2D rebf = ReBfTriangle2D.from(t);
    final Triangle2D robf = RoBfTriangle2D.from(t);
    final Triangle2D shbf = ShBFTriangle2D.from(t);
    final Triangle2D rationalFloat = RationalFloatTriangle2D.from(t);
    final Triangle2D ddFast = DDFast.from(t);
    final Triangle2D ddNormalized = DDNormalized.from(t);
    final Triangle2D ddSlow = DDSlow.from(t);
    final Triangle2D doubleNonRobust = DoubleNonRobust.from(t);
    final Triangle2D inCircleNormalized = InCircleNormalized.from(t);
    return List.of(
      // mine
      d2eager, d2lazy,
      rationalFloat,
      doubleIntervalTriangle, roundingIntervalTriangle,
      shewchukIntervalTriangle,
      bf2,
      rebf,robf,shbf,
      // JTS
      ddFast,ddNormalized,ddSlow,doubleNonRobust,inCircleNormalized); }

  /** ground truth predicate. */
  public static final Triangle2D truth (final Triangle2D t) {
    return TriangleBF2.from(t); }

  /** conversions from any Triangle2D to other Triangle classes. */

  public static final Triangle2D convertTriangle (final Triangle2D t,
                                                  final String dest) {
    // TODO: lookup method object rather than switch (String)
    return switch (dest) {
      case "TriangleD2Lazy" -> TriangleD2Lazy.from(t);
      case "RelaxedIntervalTriangle2D" -> RelaxedIntervalTriangle2D.from(t);
      case "RoundingIntervalTriangle2D" -> RoundingIntervalTriangle2D.from(t);
      case "ShewchukIntervalTriangle2D" -> ShewchukIntervalTriangle2D.from(t);
      case "TriangleBF2" ->  TriangleBF2.from(t);
      case "TriangleBF2X" ->  TriangleBF2X.from(t);
      case "TriangleD2Eager" ->  TriangleD2Eager.from(t);
      case "ReBfTriangle2D" ->  ReBfTriangle2D.from(t);
      case "RoBfTriangle2D" ->  RoBfTriangle2D.from(t);
      case "ShBFTriangle2D" ->  ShBFTriangle2D.from(t);
      case "RationalFloatTriangle2D" ->  RationalFloatTriangle2D.from(t);
      case "DDFast" ->  DDFast.from(t);
      case "DDNormalized" ->  DDNormalized.from(t);
      case "DDSlow" ->  DDSlow.from(t);
//    case "InCircleCC" ->  InCircleCC.from(t);
      case "DoubleNonRobust" ->  DoubleNonRobust.from(t);
      case "InCircleNormalized" ->  InCircleNormalized.from(t);
      default -> throw new UnsupportedOperationException(); }; }

  public static final Triangle2D[]
  convertTriangles (final Triangle2D[] t,
                    final String dest) {
    for (int i=0; i<t.length; i++) {
      t[i] = convertTriangle(t[i],dest); }
    return t;}

  //--------------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------------
  // TODO: hashcode, equals

  public static final String toHexString (final VectorD2 p) {
    return "(" +
      Double.toHexString(p.getX()) + "," +
      Double.toHexString(p.getY()) + ")"; }

  public final String toHexString () {
    return getClass().getSimpleName() + "[" +
      toHexString(p0) + ", " +
      toHexString(p1) + ", " +
      toHexString(p2) + "]"; }

  public String toString () { return toHexString(); }
  public String description () { return toString(); }

  //--------------------------------------------------------------------
  // TODO: an estimate of accuracy for each operation would be better.
  /** Is this algorithm exact (to the resolution expansions)
   * or approximate?
   */
  public boolean signedAreaExact() { return false; }

  //--------------------------------------------------------------------
  /** Return a positive value if the points pa, pb, and pc occur in
   * counterclockwise order; a negative value if they occur in clockwise
   * order; and zero if they are collinear.  The result is also a rough
   * approximation of twice the signed area of the triangle defined by
   * the three points.
   */

  public double twiceSignedArea () {
    throw new UnsupportedOperationException(
      getClass().getSimpleName()); }

  public Object twiceSignedAreaInterval () {
    return Double.toHexString(twiceSignedArea()); }

  //--------------------------------------------------------------------
  /** Not clear exactly what I want here. For now, indicate whether
   * all Kettner orientation tests should pass.
   */
  public boolean isOrientationRobust () { return signedAreaExact(); }

  //--------------------------------------------------------------------
  /** Return +1.0 if the points pa, pb, and pc occur in
   * counterclockwise order; -1.0 if they occur in clockwise
   * order; and +/-0.0 if they are collinear.
   * Nonfinite values
   * <br>
   * Separating this from <code>twiceSignedArea</code>
   * permits classes that do more precise calculation to get the sign
   * from that, rather than forcing a round to <code>double</code>.
   */

  public double orientation () {
    // NOTE: Double.compare() doesn't handle +-0.0 correctly.
    final double a = twiceSignedArea();
    if (! Double.isFinite(a)) { return a; }
    if (0.0 < a) { return 1; }
    if (0.0 > a) { return -1; }
    return 0; }

  //--------------------------------------------------------------------

  public boolean inCircleDistanceExact () { return false; }

  /** Return a positive value if the point pd lies inside the circle
   * passing through p0, p1, and p2; a negative value if it lies
   * outside; and zero if the four points are cocircular. The points pa,
   * pb, and pc must be in counterclockwise order, or the sign of the
   * result will be reversed.
   * <br>
   * (Shewchuk predicate.c)
   * <br>
   * Only Fast and Default should be used; the other two are for
   * timings.
   * <br>
   * Exact, Slow, and Default use exact arithmetic to ensure a correct
   * answer. The result returned is the determinant of a matrix.  In
   * signedVolume() only, this determinant is computed adaptively, in the
   * sense that exact arithmetic is used only to the degree it is needed
   * to ensure that the returned value has the correct sign.  Hence,
   * inCircle() is usually quite fast, but will run more slowly when the
   * input points are cocircular or nearly so.
   */

  public double inCircleDistance (final VectorD2 p) {
    throw new UnsupportedOperationException(getClass().getSimpleName()); }

  public DoubleInterval inCircleInterval (final VectorD2 p) {
    final double d = inCircleDistance(p);
    return new RoundingInterval(d,d); }

  public boolean inCircleIntervals () { return false; }

  //--------------------------------------------------------------------

//  public boolean inCircleRobust () { return inCircleDistanceExact(); }

  /** Return -1, 0, 1 if the point is
   * outside, on, or inside the circumcircle.
   */

  public double inCircle (final VectorD2 p) {
    // TODO: what to do with NaN?
    final double a = inCircleDistance(p);
    if (Double.isNaN(a)) { return Double.NaN; }
    if (0.0 < a) { return 1.0; }
    if (0.0 > a) { return -1.0; }
    return 0.0; }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public Triangle2D (final VectorD2 a,
                     final VectorD2 b,
                     final VectorD2 c) {
    super();
    this.p0 = a; this.p1 = b; this.p2 = c; }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
