package mop.java.test.geometry.delaunay.lischinski;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import mop.java.geometry.Generators;
import mop.java.geometry.delaunay.lischinski.Subdivision;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.junit.jupiter.api.Test;

/** <pre>
 * mvn -Dtest=mop.java.test.geometry.delaunay.lischinski.InsertTests test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-10-03
 */

public final class LischinskiTest {

  //--------------------------------------------------------------

  @Test
  public final void singularDelaunay () {

    // Construct a triangle containing the unit square:
    final VectorD2 p1 = new VectorD2( -1.0, -1.0);
    final VectorD2 p2 = new VectorD2(  2.0, -1.0);
    final VectorD2 p3 = new VectorD2(  0.5,  3.0);

    final Subdivision mesh = new Subdivision(p1,p2,p3);
    mesh.InsertSite(p1);
    mesh.InsertSite(new VectorD2(0.0,-1.0));
    final Color color = new Color(0.0,0.0,0.0,1.0);
    final Group triangles = mesh.jfxTriangles(color); }

  //--------------------------------------------------------------

  @Test
  public final void uniform01Delaunay () {

    // Construct a triangle containing the unit square:
    final VectorD2 p1 = new VectorD2( -1.0, -1.0);
    final VectorD2 p2 = new VectorD2(  2.0, -1.0);
    final VectorD2 p3 = new VectorD2(  0.5,  3.0);

    final Subdivision mesh = new Subdivision(p1,p2,p3);

    final Generator pointGenerator = Generators.vectorD2Generator(
      Doubles.uniformGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        0.0, 1.0));

    final int nsites = 16;
    for (int i=0;i<nsites;i++) {
      mesh.InsertSite((VectorD2) pointGenerator.next()); } }

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
//    final Subdivision mesh = new Subdivision(p1,p2,p3);
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
