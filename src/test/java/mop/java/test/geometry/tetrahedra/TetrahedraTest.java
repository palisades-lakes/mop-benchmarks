package mop.java.test.geometry.tetrahedra;

import mop.java.geometry.tetrahedron.Adapt;
import mop.java.geometry.tetrahedron.BigFloatTetrahedron3D;
import mop.java.geometry.tetrahedron.RationalFloatTetrahedron3D;
import mop.java.geometry.tetrahedron.Tetrahedron3D;
import mop.java.geometry.tetrahedron.macro.AdaptMacro;
import mop.java.geometry.tetrahedron.Exact;
import mop.java.geometry.tetrahedron.Fast;
import mop.java.geometry.tetrahedron.Slow;
import mop.java.geometry.tetrahedron.macro.DefaultMacro;
import mop.java.geometry.tetrahedron.macro.ExactMacro;
import mop.java.geometry.tetrahedron.macro.FastMacro;
import mop.java.geometry.tetrahedron.macro.SlowMacro;
import org.apache.commons.geometry.euclidean.threed.Vector3D;

import java.util.List;

//----------------------------------------------------------------

/** Common code for 2D geometry predicate tests.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-07
 */

public class TetrahedraTest {

  // ground truth predicate.
  public static final Tetrahedron3D truth (final Vector3D a,
                                           final Vector3D b,
                                           final Vector3D c,
                                           final Vector3D d) {
    return BigFloatTetrahedron3D.of(a,b,c,d); }

  public static final List<Tetrahedron3D> makeTetrahedra (final Vector3D a,
                                                          final Vector3D b,
                                                          final Vector3D c,
                                                          final Vector3D d) {
    final Tetrahedron3D bigFloat = BigFloatTetrahedron3D.of(a,b,c,d);
    final Tetrahedron3D rationalFloat = RationalFloatTetrahedron3D.of(a,b,c,d);
    final Tetrahedron3D adapt = Adapt.of(a, b, c, d);
    final Tetrahedron3D exact = Exact.of(a, b, c, d);
    final Tetrahedron3D fast = Fast.of(a, b, c, d);
    final Tetrahedron3D slow = Slow.of(a, b, c, d);
    final Tetrahedron3D adaptMacro = AdaptMacro.of(a, b, c, d);
    final Tetrahedron3D defaultMacro = DefaultMacro.of(a, b, c, d);
    final Tetrahedron3D exactMacro = ExactMacro.of(a, b, c, d);
    final Tetrahedron3D fastMacro = FastMacro.of(a, b, c, d);
    final Tetrahedron3D slowMacro = SlowMacro.of(a,b,c,d);
    return List.of(
      // mine
      rationalFloat,bigFloat,
      // Shewchuk predicates.c
      exact, adapt,fast ,slow,
      exactMacro, adaptMacro, defaultMacro, fastMacro, slowMacro); }
  //--------------------------------------------------------------
//  private static final String debugMsg (final double truth,
//                                        final double check,
//                                        final Tetrahedron3D gold,
//                                        final Tetrahedron3D pred,
//                                        final Vector3D p0,
//                                        final Vector3D p1,
//                                        final Vector3D p2,
//                                        final Vector3D p3) {
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
                                         final Tetrahedron3D gold,
                                         final Tetrahedron3D pred,
                                         final List<Tetrahedron3D> tetrahedra,
                                         final Vector3D p) {
    final StringBuilder msg = new StringBuilder(
      "\n" + name +
        "\ngold=" + gold + " -> " + Double.toHexString(truth) +
        "\npred=" + pred + " -> " + Double.toHexString(check));
    msg.append("\ndiff=").append(Double.toHexString(truth-check));
    msg.append("\nulp=").append(Double.toHexString(Math.ulp(truth)));
    if (null != tetrahedra) {
      for (final Tetrahedron3D t : tetrahedra) {
        msg.append("\n").append(t).append(" ->\n");
        if (null!=p) {
          msg.append(Double.toHexString(t.inSphere(p))); }
        else {
          msg.append(Double.toHexString(t.signedVolume())); }}}
    return msg + "\n"; }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
