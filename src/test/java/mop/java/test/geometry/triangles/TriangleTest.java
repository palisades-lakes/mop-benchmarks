package mop.java.test.geometry.triangles;

import mop.java.geometry.triangle.*;
import mop.java.geometry.triangle.jts.*;
import mop.java.geometry.triangle.macro.*;
import mop.java.geometry.triangle.shewchuk.*;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

import java.util.List;

//----------------------------------------------------------------
/** Common code for 2D geometry predicate tests.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-07
 */

public abstract class TriangleTest {

  // ground truth predicate.
  public static final Triangle2D truth (final Triangle2D t) {
    return BigFloatTriangle2D.from(t); }

  public static final List<Triangle2D> makeTriangles (final Triangle2D t) {
    final Triangle2D triangleV2D = TriangleVector2D.from(t);
    final Triangle2D bigFloat = BigFloatTriangle2D.from(t);
    final Triangle2D rationalFloat = RationalFloatTriangle2D.from(t);
    final Triangle2D ddFast = DDFast.from(t);
    final Triangle2D ddNormalized = DDNormalized.from(t);
    final Triangle2D ddSlow = DDSlow.from(t);
//    final Triangle2D inCircleCC = InCircleCC.from(t);
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
      triangleV2D, rationalFloat, bigFloat,
      // JTS
      ddFast,ddNormalized,ddSlow,doubleNonRobust,inCircleNormalized,
      // Shewchuk predicates.c
      adapt,
      exact, exactCache
      ,
      fast ,slow,
      exactMacro, adaptMacro, defaultMacro, fastMacro, slowMacro
      ); }

  //--------------------------------------------------------------
//  private static final String debugMsg (final double truth,
//                                        final double check,
//                                        final Triangle2D gold,
//                                        final Triangle2D pred,
//                                        final Vector2D p0,
//                                        final Vector2D p1,
//                                        final Vector2D p2,
//                                        final Vector2D p3) {
//    final String msg = "\ninCircle(" +
//      p0 + "," + p1 + "," + p2 + "," + p3 + ")" +
//      "\ngold=" + gold + " -> " + Double.toHexString(truth) +
//      "\npred=" + pred + " -> " + Double.toHexString(check) +
//      "\ndiff=" + Double.toHexString(truth - check) +
//      "\nulp=" + Double.toHexString(Math.ulp(truth));
//    return msg + "\n"; }

  public static final String failureMsg (final String name,
                                         final double truth,
                                         final double check,
                                         final Triangle2D gold,
                                         final Triangle2D pred,
                                         final List<Triangle2D> triangles,
                                         final Vector2D p3) {
    final StringBuilder msg = new StringBuilder(
      "\n" + name +
        "\ngold=" + gold + " -> " + Double.toHexString(truth) +
        "\npred=" + pred + " -> " + Double.toHexString(check));
    msg.append("\ndiff=").append(Double.toHexString(truth-check));
    msg.append("\nulp=").append(Double.toHexString(Math.ulp(truth)));
    if (null != triangles) {
      for (final Triangle2D t : triangles) {
        msg.append("\n").append(t).append(" ->\n");
        if (null!=p3) {
          msg.append(p3).append(" \n");
          msg.append(Double.toHexString(t.inCircle(p3))); }
        else {
          msg.append(Double.toHexString(t.signedArea())); }}}
    return msg + "\n"; }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
