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
 * @version 202-11-07
 */

public final class InsertTests {

  //--------------------------------------------------------------

  @Test
  public final void singularTriangleFrame () {

    // Construct a triangle containing the unit square:
    final VectorD2 p0 = new VectorD2(0.0,0.0);
    final VectorD2 p1 = new VectorD2(2.0,0.0);
    final VectorD2 p2 = new VectorD2(0.0,2.0);

    final LischinskiIncrementalBuilder builder =
      LischinskiIncrementalBuilder.triangleFrame(p0, p1, p2);
    //builder.startingEdge().printMesh();
    final SortedSet<ZeroSimplex> frame = builder.vertices();
    builder.insert(frame);
    builder.insertPoint(new VectorD2(0.5, 0.5));
  }

  //--------------------------------------------------------------

  @Test
  public final void uniform01TriangleFrame () {

    // Construct a triangle containing the unit square:
    final VectorD2 p0 = new VectorD2(0.0,0.0);
    final VectorD2 p1 = new VectorD2(2.0,0.0);
    final VectorD2 p2 = new VectorD2(0.0,2.0);

    final LischinskiIncrementalBuilder builder =
      LischinskiIncrementalBuilder.triangleFrame(p0, p1, p2);

    final Generator pointGenerator = Generators.vectorD2Generator(
      Doubles.uniformGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        0.0, 1.0));

    final int nsites = 16;
    for (int i=0;i<nsites;i++) {
      builder.insertPoint((VectorD2) pointGenerator.next()); } }

  //--------------------------------------------------------------

  @Test
  public final void singularRectangleFrame () {

    final LischinskiIncrementalBuilder builder =
      LischinskiIncrementalBuilder.rectangleFrame(0.0,1.0,0.0,1.0);
    final Set<ZeroSimplex> frame = builder.vertices();
    for (final ZeroSimplex z : frame) { builder.insert(z); }
    builder.insertPoint(new VectorD2(0.5, 0.5)); }

  //--------------------------------------------------------------

  @Test
  public final void uniform01RectangleFrame () {

    final LischinskiIncrementalBuilder builder =
      LischinskiIncrementalBuilder.rectangleFrame(0.0,1.0,0.0,1.0);
    final Generator pointGenerator = Generators.vectorD2Generator(
      Doubles.uniformGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        0.0, 1.0));

    final int nsites = 16;
    for (int i=0;i<nsites;i++) {
      builder.insertPoint((VectorD2) pointGenerator.next()); } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
