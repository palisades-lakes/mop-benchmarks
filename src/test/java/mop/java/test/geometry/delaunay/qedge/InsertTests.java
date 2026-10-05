package mop.java.test.geometry.delaunay.qedge;

import mop.java.geometry.Generators;
import mop.java.geometry.delaunay.qedge.LischinskiIncremental;
import mop.java.geometry.delaunay.qedge.QMesh;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.junit.jupiter.api.Test;

import java.util.Set;

/** <pre>
 * mvn -Dtest=mop.java.test.geometry.delaunay.qedge.InsertTests test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-10-04
 */

public final class InsertTests {

  //--------------------------------------------------------------

  @Test
  public final void singularTriangleFrame () {

    // Construct a triangle containing the unit square:
    final VectorD2 p1 = new VectorD2( -1.0, -1.0);
    final VectorD2 p2 = new VectorD2(  2.0, -1.0);
    final VectorD2 p3 = new VectorD2(  0.5,  3.0);

    final QMesh mesh = QMesh.triangleFrame(p1, p2, p3);
    LischinskiIncremental.insertSite(mesh,p1);
    LischinskiIncremental.insertSite(mesh,new VectorD2(0.0, -1.0)); }

  //--------------------------------------------------------------

  @Test
  public final void uniform01TriangleFrame () {

    // Construct a triangle containing the unit square:
    final VectorD2 p1 = new VectorD2( -1.0, -1.0);
    final VectorD2 p2 = new VectorD2(  2.0, -1.0);
    final VectorD2 p3 = new VectorD2(  0.5,  3.0);

    final QMesh mesh = QMesh.triangleFrame(p1, p2, p3);

    final Generator pointGenerator = Generators.vectorD2Generator(
      Doubles.uniformGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        0.0, 1.0));

    final int nsites = 16;
    for (int i=0;i<nsites;i++) {
      LischinskiIncremental.insertSite(mesh,(VectorD2) pointGenerator.next()); } }

  @Test
  public final void singularRectangleFrame () {

    final QMesh mesh = QMesh.rectangleFrame(
      0.0,1.0,0.0,1.0);
    final Set<VectorD2> frame = mesh.vertices();
    for (final VectorD2 p : frame) {
      LischinskiIncremental.insertSite(mesh,p); }
    LischinskiIncremental.insertSite(mesh,new VectorD2(0.5,0.5)); }

  //--------------------------------------------------------------

  @Test
  public final void uniform01RectangleFrame () {

    final QMesh mesh = QMesh.rectangleFrame(
      0.0,1.0,0.0,1.0);

    final Generator pointGenerator = Generators.vectorD2Generator(
      Doubles.uniformGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        0.0, 1.0));

    final int nsites = 16;
    for (int i=0;i<nsites;i++) {
      LischinskiIncremental.insertSite(mesh,(VectorD2) pointGenerator.next()); } }

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
