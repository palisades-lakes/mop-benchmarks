package mop.java.test.geometry.triangles;

import mop.java.geometry.triangle.*;
import mop.java.geometry.triangle.jts.*;
import mop.java.geometry.triangle.macro.*;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

import java.util.List;

//----------------------------------------------------------------
/** Common code for 2D geometry predicate tests.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-07
 */

public class TriangleTest {

  // ground truth predicate.
  public static final Triangle2D truth (final Vector2D a,
                                        final Vector2D b,
                                        final Vector2D c) {
    return BigFloatTriangle2D.of(a,b,c); }

  public static final List<Triangle2D> makeTriangles (final Vector2D a,
                                                      final Vector2D b,
                                                      final Vector2D c) {
    final Triangle2D ddFast = DDFast.of(a,b,c);
    final Triangle2D ddNormalized = DDNormalized.of(a,b,c);
    final Triangle2D ddSlow = DDSlow.of(a,b,c);
//    final Triangle2D inCircleCC = InCircleCC.of(a,b,c);
    final Triangle2D doubleNonRobust = DoubleNonRobust.of(a,b,c);
    final Triangle2D inCircleNormalized = InCircleNormalized.of(a,b,c);
    final Triangle2D bigFloat = BigFloatTriangle2D.of(a,b,c);
    final Triangle2D rationalFloat = RationalFloatTriangle2D.of(a,b,c);
    final Triangle2D adapt = Adapt.of(a,b,c);
    final Triangle2D exact = Exact.of(a,b,c);
    final Triangle2D fast = Fast.of(a,b,c);
    final Triangle2D slow = Slow.of(a,b,c);
    final Triangle2D adaptMacro = AdaptMacro.of(a,b,c);
    final Triangle2D defaultMacro = DefaultMacro.of(a,b,c);
    final Triangle2D exactMacro = ExactMacro.of(a,b,c);
    final Triangle2D fastMacro = FastMacro.of(a,b,c);
    final Triangle2D slowMacro = SlowMacro.of(a,b,c);
    return List.of(
      // JTS
      ddFast,ddNormalized,ddSlow,doubleNonRobust,inCircleNormalized,
      // mine
      rationalFloat,bigFloat,
      // Shewchuk predicates.c
      exact, adapt,fast ,slow,
      exactMacro, adaptMacro, defaultMacro, fastMacro, slowMacro); }

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
          msg.append(Double.toHexString(t.inCircle(p3))); }
        else {
          msg.append(Double.toHexString(t.signedArea())); }}}
    return msg + "\n"; }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
