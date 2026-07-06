package mop.java.test.geometry.predicates;

import mop.java.geometry.tetrahedron.Adapt;
import mop.java.geometry.tetrahedron.BigFloatTetrahedron3D;
import mop.java.geometry.tetrahedron.Exact;
import mop.java.geometry.tetrahedron.Fast;
import mop.java.geometry.tetrahedron.RationalFloatTetrahedron3D;
import mop.java.geometry.tetrahedron.Slow;
import mop.java.geometry.tetrahedron.Tetrahedron3D;
import mop.java.geometry.tetrahedron.macro.AdaptMacro;
import mop.java.geometry.tetrahedron.macro.DefaultMacro;
import mop.java.geometry.tetrahedron.macro.ExactMacro;
import mop.java.geometry.tetrahedron.macro.FastMacro;
import mop.java.geometry.tetrahedron.macro.SlowMacro;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.rng.UniformRandomProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

//----------------------------------------------------------------
/** Common code for geometry predicate tests.
 * <pre>
 * mvn -Dtest=mop.java.test.geometry.predicates.SignedVolumeTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-06
 */

public final class SignedVolumeTest {

  // ground truth predicate.
  // TODO: may be different for different problems
  public static final Tetrahedron3D truth () {
    return new BigFloatTetrahedron3D(); }

  public static final List<Tetrahedron3D> signedVolumeTetrahedra () {
    final Tetrahedron3D bigFloat = new BigFloatTetrahedron3D();
    final Tetrahedron3D rationalFloat = new RationalFloatTetrahedron3D();
    final Tetrahedron3D adapt = new Adapt();
    final Tetrahedron3D exact = new Exact();
    final Tetrahedron3D fast = new Fast();
    final Tetrahedron3D slow = new Slow();
    final Tetrahedron3D adaptMacro = new AdaptMacro();
    final Tetrahedron3D defaultMacro = new DefaultMacro();
    final Tetrahedron3D exactMacro = new ExactMacro();
    final Tetrahedron3D fastMacro = new FastMacro();
    final Tetrahedron3D slowMacro = new SlowMacro();
    return List.of(
      // mine
      rationalFloat,bigFloat,
      // Shewchuk predicates.c
      exact,
      adapt,fast,slow,
      exactMacro, adaptMacro, defaultMacro, fastMacro, slowMacro); }

  //--------------------------------------------------------------
  private static final String failureMsg (final double truth,
                                          final Tetrahedron3D gold,
                                          final Tetrahedron3D pred,
                                          final List<Tetrahedron3D> predicates,
                                          final Vector3D p0,
                                          final Vector3D p1,
                                          final Vector3D p2,
                                          final Vector3D p3) {
    final StringBuilder msg = new StringBuilder(
      "\norient3d(" +
        (p0) + "," +
        (p1) + "," +
        (p2) + "," +
        (p3) + ")" +
        "\norient3d(" + (p0) + "," +
        (p1) + "," +
        (p2) + "," +
        (p3) + ")" +
        "\ngold=" + gold + "\n-> " + Double.toHexString(truth) +
        "\npred=" + pred + "\n-> " + Double.toHexString(
    pred.signedVolume(p0, p1, p2, p3)));
    for (final Tetrahedron3D p : predicates) {
      msg.append("\n").append(p).append(" -> ")
         .append(Double.toHexString(p.signedVolume(p0, p1, p2, p3))); }
    return msg + "\n"; }

  //--------------------------------------------------------------

  private static final void signedVolume (final List<Tetrahedron3D> predicates,
                                          final Vector3D p0,
                                          final Vector3D p1,
                                          final Vector3D p2,
                                          final Vector3D p3) {
    final Tetrahedron3D gold = truth();
    final double trueVol = gold.signedVolume(p0, p1, p2, p3);
    for (final Tetrahedron3D p : predicates) {
      final double vol = p.signedVolume(p0, p1, p2, p3);
      if (p.signedVolumeExact()) {
        // with delta=0.0 handles +0 vs -0 'correctly'
        Assertions.assertEquals(
          trueVol, vol, 0.0,
          failureMsg(trueVol,gold,p,predicates,p0,p1,p2,p3)); }
      else {
        Assertions.assertEquals(

          Math.signum(trueVol), Math.signum(vol), 0.0,
          failureMsg(trueVol,gold,p,predicates,p0,p1,p2,p3)); } } }

  @Test
  public final void testSignedVolume () {
    final Vector3D p0 =Vector3D.of(0.0, 0.0, 0.0);
    final Vector3D p1 =Vector3D.of(1.0, 0.0, 0.0);
    final Vector3D p2 =Vector3D.of(0.0, 1.0, 0.0);
    final Vector3D p3 =Vector3D.of(0.0, 0.0, 1.0);
    signedVolume(signedVolumeTetrahedra(), p0, p1, p2, p3); }

  @Test
  public final void laplaceTest () {
    final List<Tetrahedron3D> predicates = signedVolumeTetrahedra();
    final int n = 21;
    final UniformRandomProvider urp =
      PRNG.well44497b("seeds/Well44497b-2019-01-05.txt");
    final Generator laplaceGenerator =
      Doubles.laplaceGenerator(n, 3, urp, 0.0, 1.0);
    final double[][] p = (double[][]) laplaceGenerator.next();
    for (int i = 0; i < n-3; i++) {
      signedVolume(predicates,
                   Vector3D.of(p[i]),
                   Vector3D.of(p[i+1]),
                   Vector3D.of(p[i+2]),
                   Vector3D.of(p[i+3]));} }
//--------------------------------------------------------------
}
//--------------------------------------------------------------
