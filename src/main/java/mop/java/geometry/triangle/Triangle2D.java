package mop.java.geometry.triangle;

import mop.java.geometry.triangle.jts.*;
import mop.java.geometry.triangle.macro.*;
import mop.java.geometry.triangle.shewchuk.*;
import mop.java.numbers.DoubleInterval;
import mop.java.numbers.RoundingInterval;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

import java.util.List;

/** Triangles "embedded" in <code>R<sup>2</sup></code>>.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-21
 */

public abstract class Triangle2D {

  private final Vector2D p0;
  private final Vector2D p1;
  private final Vector2D p2;
  public final Vector2D getP0 () { return p0; }
  public final Vector2D getP1 () { return p1; }
  public final Vector2D getP2 () { return p2; }

  public static final List<Triangle2D> makeTriangles (final Triangle2D t) {
    final Triangle2D triangleV2D = TriangleVector2DLazy.from(t);
    final Triangle2D triangleV2DE = TriangleVector2DEager.from(t);
    final Triangle2D lazyTriangle = LazyTriangle2D.from(t);
    final Triangle2D eagerTriangle = EagerTriangle2D.from(t);
    final Triangle2D doubleTriangle = DoubleTriangle2D.from(t);
    final Triangle2D doubleIntervalTriangle = RelaxedIntervalTriangle2D.from(t);
    final Triangle2D roundingIntervalTriangle = RoundingIntervalTriangle2D.from(t);
    final Triangle2D shewchukIntervalTriangle = ShewchukIntervalTriangle2D.from(t);
    final Triangle2D bigFloat = BigFloatTriangle2D.from(t);
    final Triangle2D bf2 = TriangleBF2.from(t);
    final Triangle2D d2eager = TriangleD2Eager.from(t);
    final Triangle2D rebf = ReBfTriangle2D.from(t);
    final Triangle2D robf = RoBfTriangle2D.from(t);
    final Triangle2D shbf = ShBFTriangle2D.from(t);
    final Triangle2D rationalFloat = RationalFloatTriangle2D.from(t);
    final Triangle2D ddFast = DDFast.from(t);
    final Triangle2D ddNormalized = DDNormalized.from(t);
    final Triangle2D ddSlow = DDSlow.from(t);
    final Triangle2D doubleNonRobust = DoubleNonRobust.from(t);
    final Triangle2D inCircleNormalized = InCircleNormalized.from(t);
    final Triangle2D adapt = Adapt.from(t);
    final Triangle2D exact = Exact.from(t);
    final Triangle2D exactCache = ExactCache.from(t);
    final Triangle2D fast = Fast.from(t);
    final Triangle2D slow = Slow.from(t);
    final Triangle2D adaptMacro = AdaptMacro.from(t);
    final Triangle2D defaultMacro = DefaultMacro.from(t);
    final Triangle2D exactMacro = ExactMacro.from(t);
    final Triangle2D fastMacro = FastMacro.from(t);
    final Triangle2D slowMacro = SlowMacro.from(t);
    return List.of(
      // mine
      triangleV2D, triangleV2DE, eagerTriangle, lazyTriangle,
      rationalFloat,
      doubleTriangle, doubleIntervalTriangle, roundingIntervalTriangle,
      shewchukIntervalTriangle,
      bigFloat, bf2, d2eager,
      rebf,robf,shbf,
      // JTS
      ddFast,ddNormalized,ddSlow,doubleNonRobust,inCircleNormalized,
      // Shewchuk predicates.c
      adapt,
      exact, exactCache
      ,
      fast ,slow,
      exactMacro, adaptMacro, defaultMacro, fastMacro, slowMacro
                  ); }

  /** ground truth predicate. */
  public static final Triangle2D truth (final Triangle2D t) {
    return BigFloatTriangle2D.from(t); }

  /** conversions from any Triangle2D to other Triangle classes. */

  public static final Triangle2D convertTriangle (final Triangle2D t,
                                                  final String dest) {
    // TODO: lookup method object rather than switch (String)
    return switch (dest) {
      case "TriangleVector2DLazy" -> TriangleVector2DLazy.from(t);
      case "TriangleVector2DEager" -> TriangleVector2DEager.from(t);
      case "LazyTriangle2D" -> LazyTriangle2D.from(t);
      case "EagerTriangle2D" -> EagerTriangle2D.from(t);
      case "DoubleTriangle2D" -> DoubleTriangle2D.from(t);
      case "RelaxedIntervalTriangle2D" -> RelaxedIntervalTriangle2D.from(t);
      case "RoundingIntervalTriangle2D" -> RoundingIntervalTriangle2D.from(t);
      case "ShewchukIntervalTriangle2D" -> ShewchukIntervalTriangle2D.from(t);
      case "BigFloatTriangle2D" ->  BigFloatTriangle2D.from(t);
      case "TriangleBF2" ->  TriangleBF2.from(t);
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
      case "Adapt" ->  Adapt.from(t);
      case "Exact" ->  Exact.from(t);
      case "ExactCache" ->  ExactCache.from(t);
      case "Fast" ->  Fast.from(t);
      case "Slow" ->  Slow.from(t);
      case "AdaptMacro" ->  AdaptMacro.from(t);
      case "DefaultMacro" ->  DefaultMacro.from(t);
      case "ExactMacro" ->  ExactMacro.from(t);
      case "FastMacro" ->  FastMacro.from(t);
      case "SlowMacro" ->  SlowMacro.from(t);
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

  public static final String toHexString (final Vector2D p) {
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

  public double inCircleDistance (final Vector2D p) {
    throw new UnsupportedOperationException(getClass().getSimpleName()); }

  public DoubleInterval inCircleInterval (final Vector2D p) {
    final double d = inCircleDistance(p);
    return new RoundingInterval(d,d); }

  public boolean inCircleIntervals () { return false; }

  //--------------------------------------------------------------------

//  public boolean inCircleRobust () { return inCircleDistanceExact(); }

  /** Return -1, 0, 1 if the point is
   * outside, on, or inside the circumcircle.
   */

  public double inCircle (final Vector2D p) {
    // TODO: what to do with NaN?
    final double a = inCircleDistance(p);
    if (Double.isNaN(a)) { return Double.NaN; }
    if (0.0 < a) { return 1.0; }
    if (0.0 > a) { return -1.0; }
    return 0.0; }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public Triangle2D (final Vector2D a,
                     final Vector2D b,
                     final Vector2D c) {
    super();
    this.p0 = a; this.p1 = b; this.p2 = c; }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
