package mop.java.test.geometry.tetrahedra;

import mop.java.geometry.Generators;
import mop.java.geometry.tetrahedron.Tetrahedron3D;
import mop.java.geometry.tetrahedron.TetrahedronVector3D;
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
 * @version 2026-07-27
 */

public final class SignedVolumeTest extends TetrahedraTest {

  //--------------------------------------------------------------

  private static final void signedVolume (final Tetrahedron3D t) {
    final Tetrahedron3D gold = truth(t);
    final double trueVol = gold.signedVolume();
    final List<Tetrahedron3D> tetrahedra = makeTetrahedra(t);
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
    final Tetrahedron3D t = TetrahedronVector3D.of(p0, p1, p2, p3);
    signedVolume(t); }

  @Test
  public final void laplaceTest () {
    final int n = 32;
    final UniformRandomProvider urp =
      PRNG.well44497b("seeds/Well44497b-2019-01-09.txt");
    final Generator laplaceGenerator =
      Doubles.laplaceGenerator(urp, 0.0, 1.0);
    final Generator vGenerator =
      Generators.vector3dGenerator(laplaceGenerator);
    final Generator tGenerator =
      Generators.tetrahedraGenerator(n,vGenerator);
    final Tetrahedron3D[] t = (Tetrahedron3D[]) tGenerator.next();
    for (int i = 0; i < n; i++) { signedVolume(t[i]); } }

//--------------------------------------------------------------
} // end class
//--------------------------------------------------------------
