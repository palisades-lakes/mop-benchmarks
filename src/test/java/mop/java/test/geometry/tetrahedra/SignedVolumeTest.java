package mop.java.test.geometry.tetrahedra;

import mop.java.geometry.Generators;
import mop.java.geometry.tetrahedron.Tetrahedron3D;
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
 * @version 2026-07-07
 */

public final class SignedVolumeTest extends TetrahedraTest {

  //--------------------------------------------------------------

  private static final void signedVolume (final Vector3D p0,
                                          final Vector3D p1,
                                          final Vector3D p2,
                                          final Vector3D p3) {
    final Tetrahedron3D gold = truth(p0,p1,p2,p3);
    final double trueVol = gold.signedVolume();
    final List<Tetrahedron3D> tetrahedra = makeTetrahedra(p0,p1,p2,p3);
    for (final Tetrahedron3D p : tetrahedra) {
      final double vol = p.signedVolume();
      if (p.signedVolumeExact()) {
        // with delta=0.0 handles +0 vs -0 'correctly'
        Assertions.assertEquals(
          trueVol, vol, 0.0,
          failureMsg(
            "signedVolume",trueVol,vol,gold,p,tetrahedra,null)); }
      else {
        Assertions.assertEquals(

          Math.signum(trueVol), Math.signum(vol), 0.0,
          failureMsg(
            "signedVolume",trueVol,vol,gold,p,tetrahedra,null)); } } }

  @Test
  public final void testSignedVolume () {
    final Vector3D p0 =Vector3D.of(0.0, 0.0, 0.0);
    final Vector3D p1 =Vector3D.of(1.0, 0.0, 0.0);
    final Vector3D p2 =Vector3D.of(0.0, 1.0, 0.0);
    final Vector3D p3 =Vector3D.of(0.0, 0.0, 1.0);
    signedVolume(p0, p1, p2, p3); }

  @Test
  public final void laplaceTest () {
    final int n = 32;
    final UniformRandomProvider urp =
      PRNG.well44497b("seeds/Well44497b-2019-01-09.txt");
    final Generator laplaceGenerator =
      Doubles.laplaceGenerator(urp, 0.0, 1.0);
    final Generator vGenerator =
      Generators.vector3dGenerator(n, laplaceGenerator);
    final Vector3D[] p = (Vector3D[]) vGenerator.next();
    for (int i = 0; i < n-3; i++) {
      signedVolume(p[i],p[i+1],p[i+2],p[i+3]); } }

//--------------------------------------------------------------
} // end class
//--------------------------------------------------------------
