package mop.java.test.geometry.delaunay.cdsbw;

import mop.java.Classes;
import mop.java.cmplx.OneSimplex;
import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.ZeroSimplex;
import mop.java.geom.mesh.TriangleMesh;
import mop.java.geometry.Generators;
import mop.java.geometry.delaunay.Util;
import mop.java.geometry.delaunay.cdsbw.CDSBWDelaunayBuilder;
import mop.java.geometry.delaunay.cdsbw.DelaunayBuilder;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

/** <pre>
 * mvn -Dtest=mop.java.test.geometry.delaunay.cdsbw.DelaunayBuilderTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-10-11
 */

public final class DelaunayBuilderTest {

  //--------------------------------------------------------------

  // TODO: pass BuilderBuilder Function to each test,
  //  and iterate over DelaunayBuilder.make() implementations
  //  Also, iterate over frames.
  @Test
  public final void singleTriangle () {

    // Construct a triangle containing the unit square:
    final ZeroSimplex z0 = ZeroSimplex.make("z0");
    final ZeroSimplex z1 = ZeroSimplex.make("z1");
    final ZeroSimplex z2 = ZeroSimplex.make("z2");
    final TwoSimplex t0 = TwoSimplex.make(z0, z1, z2);
    final TwoSimplex t1 = TwoSimplex.make(z1,z2,z0);
    Assertions.assertEquals(t0,t1);

    final VectorD2 p0 = new VectorD2( -1.0, -1.0);
    final VectorD2 p1 = new VectorD2(  2.0, -1.0);
    final VectorD2 p2 = new VectorD2(  0.5,  3.0);

    final DelaunayBuilder builder = CDSBWDelaunayBuilder.make();
    builder.embed(z0,p0);
    builder.embed(z1,p1);
    builder.embed(z2,p2);

    Assertions.assertThrows(
      AssertionError.class,
      () -> builder.embed(z0, p1),
      Classes.className(builder));

    builder.addTriangle(z0,z1,z2);
    Assertions.assertEquals(1,builder.nFaces());

    builder.deleteTriangle(z0,z1,z2);
    Assertions.assertEquals(0,builder.nFaces());
    Assertions.assertThrows(
      AssertionError.class, () -> builder.deleteTriangle(z0, z1, z2),
      Classes.className(builder));

    final TwoSimplex t = builder.addTriangle(z0,z1,z2);
    Assertions.assertEquals(1,builder.nFaces());

    final ZeroSimplex z01 = builder.adjacent(z0,z1);
    Assertions.assertEquals(z2,z01);
    final ZeroSimplex z12 = builder.adjacent(z1,z2);
    Assertions.assertEquals(z0,z12);
    final ZeroSimplex z20 = builder.adjacent(z2,z0);
    Assertions.assertEquals(z1,z20);

    final OneSimplex e0 = builder.adjacent(z0);
    Assertions.assertEquals(z1,e0.z0());
    Assertions.assertEquals(z2,e0.z1());
    final OneSimplex e1 = builder.adjacent(z1);
    Assertions.assertEquals(z2,e1.z0());
    Assertions.assertEquals(z0,e1.z1());
    final OneSimplex e2 = builder.adjacent(z2);
    Assertions.assertEquals(z0,e2.z0());
    Assertions.assertEquals(z1,e2.z1());

    final ZeroSimplex z = ZeroSimplex.make("z");
    final VectorD2 p = new VectorD2(0.5,0.5);
    builder.embed(z,p);
    Assertions.assertTrue(Util.inCircle(z0, z1, z2, z,builder.embedding()));
    Assertions.assertEquals(t,builder.locate(z));

    builder.insertVertex(z,z0,z1,z2);
    Assertions.assertEquals(3,builder.nFaces()); }

  @Test
  public final void splitRectangle () {

    // Construct a unit square as 2 triangles:
    final ZeroSimplex z0 = ZeroSimplex.make("z0");
    final ZeroSimplex z1 = ZeroSimplex.make("z1");
    final ZeroSimplex z2 = ZeroSimplex.make("z2");
    final ZeroSimplex z3 = ZeroSimplex.make("z3");

    final VectorD2 p0 = new VectorD2(0.0,0.0);
    final VectorD2 p1 = new VectorD2(1.0,0.0);
    final VectorD2 p2 = new VectorD2(1.0,1.0);
    final VectorD2 p3 = new VectorD2(0.0,1.0);

    final DelaunayBuilder builder = CDSBWDelaunayBuilder.make();
    builder.embed(z0,p0);
    builder.embed(z1,p1);
    builder.embed(z2,p2);
    builder.embed(z3,p3);

    Assertions.assertThrows(
      AssertionError.class,
      () -> builder.embed(z0, p1),
      Classes.className(builder));

    builder.addTriangle(z0,z1,z2);
    Assertions.assertEquals(1,builder.nFaces());
    builder.addTriangle(z2,z3,z0);
    Assertions.assertEquals(2,builder.nFaces());

    builder.deleteTriangle(z1,z2,z0);
    Assertions.assertEquals(1,builder.nFaces());
    Assertions.assertThrows(
      AssertionError.class, () -> builder.deleteTriangle(z0,z1,z2),
      Classes.className(builder));
    builder.deleteTriangle(z0,z2,z3);
    Assertions.assertEquals(0,builder.nFaces());
    Assertions.assertThrows(
      AssertionError.class, () -> builder.deleteTriangle(z3,z0,z2),
      Classes.className(builder));

    final TwoSimplex t012 = builder.addTriangle(z0,z1,z2);
    final TwoSimplex t023 = builder.addTriangle(z0,z2,z3);
    Assertions.assertEquals(2,builder.nFaces());

    final ZeroSimplex z01 = builder.adjacent(z0,z1);
    Assertions.assertEquals(z2,z01);
    final ZeroSimplex z12 = builder.adjacent(z1,z2);
    Assertions.assertEquals(z0,z12);
    final ZeroSimplex z20 = builder.adjacent(z2,z0);
    Assertions.assertEquals(z1,z20);

    final ZeroSimplex z02 = builder.adjacent(z0,z2);
    Assertions.assertEquals(z3,z02);
    final ZeroSimplex z30 = builder.adjacent(z3,z0);
    Assertions.assertEquals(z2,z30);
    final ZeroSimplex z23 = builder.adjacent(z2,z3);
    Assertions.assertEquals(z0,z23);

    // unique opposite edge in these 2
    { final OneSimplex e1 = builder.adjacent(z1);
      Assertions.assertEquals(z2,e1.z0());
      Assertions.assertEquals(z0,e1.z1()); }
    { final OneSimplex e3 = builder.adjacent(z3);
      Assertions.assertEquals(z0,e3.z0());
      Assertions.assertEquals(z2,e3.z1()); }

    // 2 possible answers in these 2
    // determined by ordering of face iteration,
    // but don't want to assume that:
    { final OneSimplex e0 = builder.adjacent(z0);
      Assertions.assertTrue(
        (z1.equals(e0.z0()) && z2.equals(e0.z1()) ||
          (z2.equals(e0.z0())) && z3.equals(e0.z1()))); }
    { final OneSimplex e2 = builder.adjacent(z2);
      Assertions.assertTrue(
        (z0.equals(e2.z0()) && z1.equals(e2.z1()) ||
          (z3.equals(e2.z0())) && z0.equals(e2.z1()))); }

    final ZeroSimplex z = ZeroSimplex.make("z");
    final VectorD2 p = new VectorD2(0.5,0.25);
    builder.embed(z,p);
    Assert.assertEquals(t012,builder.locate(z));
    Assertions.assertTrue(Util.inCircle(t012, z, builder.embedding()));
    Assertions.assertTrue(Util.inCircle(t023, z, builder.embedding()));
    builder.insertVertex(z,z0,z1,z2);
    Assertions.assertEquals(
      4, builder.nFaces(), builder.toString());
  }
  @Test
  public final void splitRectangleFrame () {

    final TriangleMesh m =
      Util.rectangleFrame(0.0,1.0,0.0,1.0);
    final DelaunayBuilder builder = CDSBWDelaunayBuilder.make(m);

    final Generator pointGenerator = Generators.vectorD2Generator(
      Doubles.uniformGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        0.0, 1.0));

    final int nsites = 128;
    for (int i=0;i<nsites;i++) {
      final ZeroSimplex zi = ZeroSimplex.make("a" + i);
      final VectorD2 pi = (VectorD2) pointGenerator.next();
      builder.embed(zi,pi);
      builder.insert(zi); }
    // TODO: checkDelaunay checkOrientation assertions
  }

 //--------------------------------------------------------------

  @Test
  public final void uniform01Triangle () {

    // Construct a triangle containing the unit square:
    final ZeroSimplex z0 = ZeroSimplex.make("z0");
    final ZeroSimplex z1 = ZeroSimplex.make("z1");
    final ZeroSimplex z2 = ZeroSimplex.make("z2");
    final TwoSimplex t0 = TwoSimplex.make(z0, z1, z2);
    final TwoSimplex t1 = TwoSimplex.make(z1,z2,z0);
    Assertions.assertEquals(t0,t1);

    final VectorD2 p0 = new VectorD2( -1.0, -1.0);
    final VectorD2 p1 = new VectorD2(  2.0, -1.0);
    final VectorD2 p2 = new VectorD2(  0.5,  3.0);

    final DelaunayBuilder builder = CDSBWDelaunayBuilder.make();
    builder.embed(z0,p0);
    builder.embed(z1,p1);
    builder.embed(z2,p2);
    builder.addTriangle(z0,z1,z2); }

  @Test
  public final void uniform01TriangleFrame () {

    final TriangleMesh m = Util.triangleFrame01();
    final DelaunayBuilder builder = CDSBWDelaunayBuilder.make(m);

    final Generator pointGenerator = Generators.vectorD2Generator(
      Doubles.uniformGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        0.0, 1.0));

    final int nsites = 128;
    for (int i=0;i<nsites;i++) {
      final ZeroSimplex zi = ZeroSimplex.make("a" + i);
      final VectorD2 pi = (VectorD2) pointGenerator.next();
      builder.embed(zi,pi);
      builder.insert(zi); }
    // TODO: checkDelaunay checkOrientation assertions
    }

  //--------------------------------------------------------------

//  @Test
//  public final void uniform01RectangleFrame () {
//
//    final QMesh mesh = QMesh.rectangleFrame(
//      0.0,1.0,0.0,1.0);
//
//    final Generator pointGenerator = Generators.vectorD2Generator(
//      Doubles.uniformGenerator(
//        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
//        0.0, 1.0));
//
//    final int nsites = 16;
//    for (int i=0;i<nsites;i++) {
//      LischinskiIncremental.insertSite(mesh,(VectorD2) pointGenerator.next()); } }

  //--------------------------------------------------------------
// goes into infinite loop!!!
//  @Test
//  public final void laplaceDelaunay () {
//
//    // Construct a triangle containing the unit square:
//    final VectorD2 p1 = new VectorD2( -1.0, -1.0);
//    final VectorD2 p2 = new VectorD2(  2.0, -1.0);
//    final VectorD2 p3 = new VectorD2(  0.5,  3.0);
//
//    final QMesh mesh = QMesh.triangleFrame(p1,p2,p3);
//
//    final double pMu = 0.0;
//    final double pSigma = 3.0;
//    final Generator pointGenerator = Generators.vectorD2Generator(
//      Doubles.laplaceGenerator(
//        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
//        pMu,pSigma));
//
//    final int nsites = 16;
//    for (int i=0;i<nsites;i++) {
//      mesh.InsertSite((VectorD2) pointGenerator.next()); }
//  }


  //--------------------------------------------------------------
}
//--------------------------------------------------------------
