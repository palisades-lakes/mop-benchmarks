package mop.java.test.geometry.delaunay;

import mop.java.cmplx.ZeroSimplex;
import mop.java.geom.mesh.TriangleMesh;
import mop.java.geometry.Generators;
import mop.java.geometry.delaunay.DelaunayBuilder;
import mop.java.geometry.delaunay.Util;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

/** <pre>
 * mvn -Dtest=mop.java.test.geometry.delaunay.BuilderInsertTests test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-11-16
 */

public final class BuilderInsertTests {

  //--------------------------------------------------------------------

  private final void singularTriangleFrame
    (final Function<TriangleMesh, DelaunayBuilder> maker) {

    // Construct a triangle containing the unit square:
    final VectorD2 p0 = new VectorD2(-1.0, -1.0);
    final VectorD2 p1 = new VectorD2(2.0, -1.0);
    final VectorD2 p2 = new VectorD2(0.5,  3.0);

    final TriangleMesh frame = Util.triangleFrame(p0, p1, p2);
    final DelaunayBuilder builder = maker.apply(frame);
    final Set<ZeroSimplex> vertices = builder.vertices();
    builder.insert(vertices);
    builder.insertPoint(new VectorD2(0.5, 0.5)); }

  //--------------------------------------------------------------------

  private final void uniform01TriangleFrame
    (final Function<TriangleMesh, DelaunayBuilder> maker) {

    // Construct a triangle containing the unit square:
    final VectorD2 p0 = new VectorD2( -1.0, -1.0);
    final VectorD2 p1 = new VectorD2(  2.0, -1.0);
    final VectorD2 p2 = new VectorD2(  0.5,  3.0);

    final DelaunayBuilder builder =
      maker.apply(Util.triangleFrame(p0,p1,p2));

    final Generator pointGenerator = Generators.vectorD2Generator(
      Doubles.uniformGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        0.0, 1.0));

    final int nsites = 16;
    for (int i=0;i<nsites;i++) {
      builder.insertPoint((VectorD2) pointGenerator.next()); } }

  //--------------------------------------------------------------------

  private final void singularRectangleFrame
    (final Function<TriangleMesh, DelaunayBuilder> maker) {

    final DelaunayBuilder builder =
      maker.apply(
        Util.rectangleFrame(0.0,1.0,0.0,1.0));
    final Set<ZeroSimplex> frame = builder.vertices();
    for (final ZeroSimplex z : frame) { builder.insert(z); }
    builder.insertPoint(new VectorD2(0.5, 0.5)); }

  //--------------------------------------------------------------------

  private final void uniform01RectangleFrame
    (final Function<TriangleMesh, DelaunayBuilder> maker) {

    final DelaunayBuilder builder =
      maker.apply(
        Util.rectangleFrame(0.0,1.0,0.0,1.0));
    final Generator pointGenerator = Generators.vectorD2Generator(
      Doubles.uniformGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        0.0, 1.0));

    final int nsites = 16;
    for (int i=0;i<nsites;i++) {
      builder.insertPoint((VectorD2) pointGenerator.next()); } }

  //--------------------------------------------------------------------

  private final void singularHexagonFrame
    (final Function<TriangleMesh, DelaunayBuilder> maker) {

    final DelaunayBuilder builder =
      maker.apply(
        Util.hexagonFrame(0.0,1.0,0.0,1.0));
    final Set<ZeroSimplex> frame = builder.vertices();
    for (final ZeroSimplex z : frame) { builder.insert(z); }
    builder.insertPoint(new VectorD2(0.5, 0.5)); }

  //--------------------------------------------------------------------

  private final void uniform01HexagonFrame
    (final Function<TriangleMesh, DelaunayBuilder> maker) {

    final DelaunayBuilder builder =
      maker.apply(
        Util.hexagonFrame(0.0,1.0,0.0,1.0));
    final Generator pointGenerator = Generators.vectorD2Generator(
      Doubles.uniformGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        0.0, 1.0));

    final int nsites = 16;
    for (int i=0;i<nsites;i++) {
      builder.insertPoint((VectorD2) pointGenerator.next()); } }

  //--------------------------------------------------------------------

  private static final List<Function<TriangleMesh, DelaunayBuilder>>
    delaunayMakers =
    List.of(
      mop.java.geometry.delaunay.gg4.SubdivisionBuilder::make,
      mop.java.geometry.delaunay.hedge.LischinskiIncrementalBuilder::make,
      mop.java.geometry.delaunay.qedge.LischinskiIncrementalBuilder::make);

  @Test
  public final void runTests () {
    for (Function<TriangleMesh, DelaunayBuilder> maker :
      delaunayMakers) {
      singularTriangleFrame(maker);
      uniform01TriangleFrame(maker);
      singularRectangleFrame(maker);
      uniform01RectangleFrame(maker);
      singularHexagonFrame(maker);
      uniform01HexagonFrame(maker); } }

  //--------------------------------------------------------------------
}
//--------------------------------------------------------------------
