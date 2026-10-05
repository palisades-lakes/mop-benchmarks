package mop.java.test.geometry.segments;

import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.segment.SegmentR2;

import java.util.List;

//----------------------------------------------------------------
/** Common code for 2D geometry predicate tests.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-10-06
 */

public abstract class SegmentTest {

  //--------------------------------------------------------------

  public static final String failureMsg (final String name,
                                         final double truth,
                                         final double check,
                                         final SegmentR2 gold,
                                         final SegmentR2 pred,
                                         final List<SegmentR2> segments,
                                         final VectorD2 p) {
    final StringBuilder msg = new StringBuilder(
      "\n\n" + name +
        "\ngold=" + gold + " -> " + Double.toHexString(truth) +
        "\npred=" + pred + " -> " + Double.toHexString(check));
    msg.append("\ndiff=").append(Double.toHexString(truth-check));
    msg.append("\nulp=").append(Double.toHexString(Math.ulp(truth)));
    if (null != segments) {
      for (final SegmentR2 t : segments) {
        msg.append("\n\n").append(t.description()).append(" ->\n");
        if (null!=p) { msg.append(p).append(" \n"); } } }
    return msg + "\n"; }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
