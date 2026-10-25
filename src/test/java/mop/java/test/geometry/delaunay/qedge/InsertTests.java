package mop.java.test.geometry.delaunay.qedge;

import mop.java.cmplx.ZeroSimplex;
import mop.java.geometry.Generators;
import mop.java.geometry.delaunay.qedge.LischinskiIncrementalBuilder;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.SortedSet;

/** <pre>
 * mvn -Dtest=mop.java.test.geometry.delaunay.qedge.InsertTests test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-10-25
 */

public final class InsertTests {

  //--------------------------------------------------------------

//  @Test
//  public final void pointOnVertex () {
//
//    // Construct a triangle containing the unit square:
//    final VectorD2 p0 = new VectorD2( -1.0, -1.0);
//    final VectorD2 p1 = new VectorD2(  2.0, -1.0);
//    final VectorD2 p2 = new VectorD2(  0.5,  3.0);
//
//    final LischinskiIncrementalBuilder builder =
//      LischinskiIncrementalBuilder.triangleFrame(p0, p1, p2);
//    // TODO: infinite loop when new simplex mapped to same point!
//    //  that is, insert a point which is one of the existing vertices
//     builder.insert(p0);
//    builder.insert(new VectorD2(0.0, -1.0)); }

  //--------------------------------------------------------------

  @Test
  public final void singularTriangleFrame () {

    // Construct a triangle containing the unit square:
    final VectorD2 p0 = new VectorD2( -1.0, -1.0);
    final VectorD2 p1 = new VectorD2(  2.0, -1.0);
    final VectorD2 p2 = new VectorD2(  0.5,  3.0);

    final LischinskiIncrementalBuilder builder =
      LischinskiIncrementalBuilder.triangleFrame(p0, p1, p2);
//    // TODO: infinite loop
    final SortedSet<ZeroSimplex> zs = builder.vertices();
    builder.insert(zs);
    builder.insert(new VectorD2(0.0, -1.0)); }

  //--------------------------------------------------------------

  @Test
  public final void uniform01TriangleFrame () {

    // Construct a triangle containing the unit square:
    final VectorD2 p0 = new VectorD2( -1.0, -1.0);
    final VectorD2 p1 = new VectorD2(  2.0, -1.0);
    final VectorD2 p2 = new VectorD2(  0.5,  3.0);

    final LischinskiIncrementalBuilder builder =
      LischinskiIncrementalBuilder.triangleFrame(p0, p1, p2);

    final Generator pointGenerator = Generators.vectorD2Generator(
      Doubles.uniformGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        0.0, 1.0));

    final int nsites = 16;
    for (int i=0;i<nsites;i++) {
      builder.insert((VectorD2) pointGenerator.next()); } }

  //--------------------------------------------------------------

  @Test
  public final void singularRectangleFrame () {

    final LischinskiIncrementalBuilder builder =
      LischinskiIncrementalBuilder.rectangleFrame(0.0,1.0,0.0,1.0);
    final Set<ZeroSimplex> frame = builder.vertices();
    for (final ZeroSimplex z : frame) { builder.insert(z); }
    builder.insert(new VectorD2(0.5, 0.5)); }

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
//      LischinskiIncrementalBuilder.insert(mesh,(VectorD2) pointGenerator.next()); } }
//
  //--------------------------------------------------------------

//  @Test
//  public final void twoTriangles () {
//    final double xmin = 0.0;
//    final double xmax = 1.0;
//    final double ymin = 0.0;
//    final double ymax = 1.0;
//      final VectorD2 p0 = new VectorD2(xmin,ymin);
//      final VectorD2 p0 = new VectorD2(xmax,ymin);
//      final VectorD2 p1 = new VectorD2(xmax,ymax);
//      final VectorD2 p2 = new VectorD2(xmin,ymax);
//      // face012
//      final QEdge e01 = QEdge.make("e01", p0, p0);
//      final QEdge e12 = QEdge.make("e12", p0, p1);
//      final QEdge e20 = QEdge.make("e20", p1, p0);
//      e01.splice(e20.reverse());
//      e12.splice(e01.reverse());
//      e20.splice(e12.reverse());
//      // face023
//      final QEdge e02 = QEdge.make("e02", p0,p1);
//      final QEdge e23 = QEdge.make("e23", p1, p2);
//      final QEdge e30 = QEdge.make("e30", p2, p0);
//      e02.splice(e30.reverse());
//      e23.splice(e02.reverse());
//      e30.splice(e23.reverse());
//      System.out.println("two triangles:");
//    System.out.println(e01.meshString());
//    System.out.println(e02.meshString()); }

  //--------------------------------------------------------------
// goes into infinite loop!!!
//  @Test
//  public final void laplaceDelaunay () {
//
//    // Construct a triangle containing the unit square:
//    final VectorD2 p0 = new VectorD2( -1.0, -1.0);
//    final VectorD2 p1 = new VectorD2(  2.0, -1.0);
//    final VectorD2 p2 = new VectorD2(  0.5,  3.0);
//
//    final QMesh mesh = QMesh.triangleFrame(p0,p1,p2);
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
