package mop.java.test.geometry.delaunay.hedge;

/** <pre>
 * mvn -Dtest=mop.java.test.geometry.delaunay.hedge.BuilderInsertTests test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-11-05
 */

public final class BuilderInsertTests {

  //--------------------------------------------------------------

//  @Test
//  public final void singularTriangleFrame () {
//
//    // Construct a triangle containing the unit square:
//    final VectorD2 p0 = new VectorD2(-1.0, -1.0);
//    final VectorD2 p1 = new VectorD2(2.0, -1.0);
//    final VectorD2 p2 = new VectorD2(0.5,  3.0);
//
//    final TriangleMesh frame = Util.triangleFrame(p0, p1, p2);
//    final LischinskiIncrementalBuilder builder =
//      LischinskiIncrementalBuilder.make(frame);
//    final SortedSet<ZeroSimplex> vertices = builder.vertices();
//    builder.insert(vertices);
////    builder.insertPoint(new VectorD2(0.5, 0.5));
//  }

  //--------------------------------------------------------------

//  @Test
//  public final void uniform01TriangleFrame () {
//
//    // Construct a triangle containing the unit square:
//    final VectorD2 p0 = new VectorD2( -1.0, -1.0);
//    final VectorD2 p1 = new VectorD2(  2.0, -1.0);
//    final VectorD2 p2 = new VectorD2(  0.5,  3.0);
//
//    final LischinskiIncrementalBuilder builder =
//      LischinskiIncrementalBuilder.make(Util.triangleFrame(p0,p1,p2));
//
//    final Generator pointGenerator = Generators.vectorD2Generator(
//      Doubles.uniformGenerator(
//        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
//        0.0, 1.0));
//
//    final int nsites = 16;
//    for (int i=0;i<nsites;i++) {
//      builder.insertPoint((VectorD2) pointGenerator.next()); } }
//
//  //--------------------------------------------------------------
//
//  @Test
//  public final void singularRectangleFrame () {
//
//    final LischinskiIncrementalBuilder builder =
//      LischinskiIncrementalBuilder.make(
//        Util.rectangleFrame(0.0,1.0,0.0,1.0));
//    final Set<ZeroSimplex> frame = builder.vertices();
//    for (final ZeroSimplex z : frame) { builder.insert(z); }
//    builder.insertPoint(new VectorD2(0.5, 0.5)); }
//
//  //--------------------------------------------------------------
//
//  @Test
//  public final void uniform01RectangleFrame () {
//
//    final LischinskiIncrementalBuilder builder =
//      LischinskiIncrementalBuilder.make(
//        Util.rectangleFrame(0.0,1.0,0.0,1.0));
//    final Generator pointGenerator = Generators.vectorD2Generator(
//      Doubles.uniformGenerator(
//        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
//        0.0, 1.0));
//
//    final int nsites = 16;
//    for (int i=0;i<nsites;i++) {
//      builder.insertPoint((VectorD2) pointGenerator.next()); } }
//
//  //--------------------------------------------------------------
//
//  @Test
//  public final void singularHexagonFrame () {
//
//    final LischinskiIncrementalBuilder builder =
//      LischinskiIncrementalBuilder.make(
//        Util.hexagonFrame(0.0,1.0,0.0,1.0));
//    final Set<ZeroSimplex> frame = builder.vertices();
//    for (final ZeroSimplex z : frame) { builder.insert(z); }
//    builder.insertPoint(new VectorD2(0.5, 0.5)); }
//
//  //--------------------------------------------------------------
//
//  @Test
//  public final void uniform01HexagonFrame () {
//
//    final LischinskiIncrementalBuilder builder =
//      LischinskiIncrementalBuilder.make(
//        Util.hexagonFrame(0.0,1.0,0.0,1.0));
//    final Generator pointGenerator = Generators.vectorD2Generator(
//      Doubles.uniformGenerator(
//        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
//        0.0, 1.0));
//
//    final int nsites = 16;
//    for (int i=0;i<nsites;i++) {
//      builder.insertPoint((VectorD2) pointGenerator.next()); } }
//
//  //--------------------------------------------------------------
}
//--------------------------------------------------------------
