package mop.java.test.geometry.triangles;

import mop.java.geometry.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

import java.util.List;

//----------------------------------------------------------------
/** Common code for 2D geometry predicate tests.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-09
 */

public abstract class TriangleTest {

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
                                         final Vector2D p) {
    final StringBuilder msg = new StringBuilder(
      "\n\n" + name +
        "\ngold=" + gold + " -> " + Double.toHexString(truth) +
        "\npred=" + pred + " -> " + Double.toHexString(check));
    msg.append("\ndiff=").append(Double.toHexString(truth-check));
    msg.append("\nulp=").append(Double.toHexString(Math.ulp(truth)));
    if (null != triangles) {
      for (final Triangle2D t : triangles) {
        msg.append("\n").append(t.description()).append(" ->\n");
        if (null!=p) {
          msg.append(p).append(" \n");
          msg.append(t.inCircleInterval(p)); }
        else {
          msg.append(t.twiceSignedAreaInterval()); }}}
    return msg + "\n"; }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
