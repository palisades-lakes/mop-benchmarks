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

/** <pre>
 * mvn -Dtest=mop.java.test.geometry.predicates.InSphereTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-27
 */

public final class InSphereTest extends TetrahedraTest {

  //--------------------------------------------------------------

  private static final void inSphere (final Tetrahedron3D t0,
                                      final Vector3D p) {
    final Tetrahedron3D gold = truth(t0);
    final double trueInc = gold.inSphere(p);
    final List<Tetrahedron3D> tetrahedra = makeTetrahedra(t0);
    for (final Tetrahedron3D t : tetrahedra) {
      final double inc = t.inSphere(p);
      if (t.inSphereExact()) {
        // with delta=0.0 handles +0 vs -0 'correctly'
        Assertions.assertEquals(
          trueInc, inc, 0.0,
          failureMsg(
            "inSphere",trueInc,inc,gold,t,tetrahedra,p)); }
      else {
        Assertions.assertEquals(
          Math.signum(trueInc), Math.signum(inc), 0.0,
          failureMsg(
            "inSphere",trueInc,inc,gold,t,tetrahedra,p)); } } }

  @Test
  public final void testInSphere () {
    final Vector3D p0 = Vector3D.of(0.0, 0.0, 0.0);
    final Vector3D p1 = Vector3D.of(1.0, 0.0, 0.0);
    final Vector3D p2 = Vector3D.of(0.0, 1.0, 0.0);
    final Vector3D p3 = Vector3D.of(0.0, 0.0, 1.0);
    final Tetrahedron3D t = TetrahedronVector3D.of(p0, p1, p2, p3);
    final Vector3D p = Vector3D.of(1.0, 1.0, 1.0);
    inSphere(t, p); }

  @Test
  public final void laplaceTest () {
    final int m = 32;
    final int n = 32;
    final UniformRandomProvider urp0 =
      PRNG.well44497b("seeds/Well44497b-2019-01-09.txt");
    final Generator tGenerator =
      Generators.tetrahedraGenerator(
        m, Generators.vector3dGenerator(
          Doubles.laplaceGenerator(urp0, 0.0, 1.0)));
    final Tetrahedron3D[] t = (Tetrahedron3D[]) tGenerator.next();
    final UniformRandomProvider urp1 =
      PRNG.well44497b("seeds/Well44497b-2019-01-11.txt");
    final Generator pGenerator =
      Generators.vector3dGenerator(
        n, Doubles.laplaceGenerator(urp1, 0.0, 1.0));
    final Vector3D[] p = (Vector3D[]) pGenerator.next();
    for (int i = 0; i < m; i++) {
      final Tetrahedron3D ti = t[i];
      for (int j=0;j<n;j++) { inSphere(ti,p[j]);} } }

//--------------------------------------------------------------
} // end class
//--------------------------------------------------------------
