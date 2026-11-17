package mop.java.test.geometry.delaunay.gg4;

import mop.java.geometry.Generators;
import mop.java.geometry.delaunay.gg4.SubdivisionBuilder;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.junit.jupiter.api.Test;

/** <pre>
 * mvn -Dtest=mop.java.test.geometry.delaunay.gg4.LischinskiTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-11-16
 */

public final class LischinskiTest {

  //--------------------------------------------------------------------

  @Test
  public final void singularDelaunay () {

    // Construct a triangle containing the unit square:
    final VectorD2 p0 = new VectorD2( -1.0, -1.0);
    final VectorD2 p1 = new VectorD2(  2.0, -1.0);
    final VectorD2 p2 = new VectorD2(  0.5,  3.0);

    final SubdivisionBuilder mesh = SubdivisionBuilder.triangleFrame(p0, p1, p2);
    mesh.insertPoint(new VectorD2(0.0, 0.0)); }

  //--------------------------------------------------------------------

  @Test
  public final void uniform01Delaunay () {

    // Construct a triangle containing the unit square:
    final VectorD2 p0 = new VectorD2( -1.0, -1.0);
    final VectorD2 p1 = new VectorD2(  2.0, -1.0);
    final VectorD2 p2 = new VectorD2(  0.5,  3.0);

    final SubdivisionBuilder mesh = SubdivisionBuilder.triangleFrame(p0,p1,p2);

    final Generator pointGenerator = Generators.vectorD2Generator(
      Doubles.uniformGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        0.0, 1.0));

    final int nsites = 16;
    for (int i=0;i<nsites;i++) {
      mesh.insertPoint((VectorD2) pointGenerator.next()); } }

  //--------------------------------------------------------------------
}
//----------------------------------------------------------------------
