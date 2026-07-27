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

/** <pre>
 * mvn -Dtest=mop.java.test.geometry.predicates.InSphereTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-27
 */

public final class InSphereTest extends TetrahedraTest {

  //--------------------------------------------------------------

  private static final void inSphere (final Vector3D p0,
                                      final Vector3D p1,
                                      final Vector3D p2,
                                      final Vector3D p3,
                                      final Vector3D p4) {
    final Tetrahedron3D gold = truth(p0,p1,p2,p3);
    final double trueInc = gold.inSphere(p4);
    final List<Tetrahedron3D> tetrahedra = makeTetrahedra(p0,p1,p2,p3);
    for (final Tetrahedron3D t : tetrahedra) {
      final double inc = t.inSphere(p4);
      if (t.inSphereExact()) {
        // with delta=0.0 handles +0 vs -0 'correctly'
        Assertions.assertEquals(
          trueInc, inc, 0.0,
          failureMsg(
            "inSphere",trueInc,inc,gold,t,tetrahedra,p4)); }
      else {
        Assertions.assertEquals(
          Math.signum(trueInc), Math.signum(inc), 0.0,
          failureMsg(
            "inSphere",trueInc,inc,gold,t,tetrahedra,p4)); } } }

  @Test
  public final void testInSphere () {
    final Vector3D p0 = Vector3D.of(0.0, 0.0, 0.0);
    final Vector3D p1 = Vector3D.of(1.0, 0.0, 0.0);
    final Vector3D p2 = Vector3D.of(0.0, 1.0, 0.0);
    final Vector3D p3 = Vector3D.of(0.0, 0.0, 1.0);
    final Vector3D p4 = Vector3D.of(1.0, 1.0, 1.0);
    inSphere(p0, p1, p2, p3, p4); }

  @Test
  public final void laplaceTest () {
    final int n = 55;
    final UniformRandomProvider urp =
      PRNG.well44497b("seeds/Well44497b-2019-01-11.txt");
    final Generator laplaceGenerator =
      Doubles.laplaceGenerator(urp, 0.0, 1.0);
    final Generator vGenerator =
      Generators.vector3dGenerator(n, laplaceGenerator);
    final Vector3D[] p = (Vector3D[]) vGenerator.next();
    for (int i = 0; i < n-4; i++) {
      inSphere(p[i],p[i+1],p[i+2],p[i+3],p[i+4]);} }

//--------------------------------------------------------------
} // end class
//--------------------------------------------------------------
