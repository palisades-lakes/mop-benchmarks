package mop.java.test.geometry.delaunay;

import mop.java.cmplx.SimplicialComplex2D;
import mop.java.geom.mesh.TriangleMesh;
import mop.java.geometry.delaunay.Edge;
import mop.java.geometry.delaunay.MeshTranslator;
import mop.java.geometry.delaunay.Util;
import mop.java.geometry.delaunay.hedge.HEdgeTranslator;
import mop.java.geometry.delaunay.qedge.QEdgeTranslator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

/** <pre>
 * mvn -Dtest=mop.java.test.geometry.delaunay.hedge.BuilderTests test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-11-16
 */

public final class BuilderTests {

  //--------------------------------------------------------------------

  private final void triangleFrame
    (final Function<SimplicialComplex2D, MeshTranslator> maker) {

    final TriangleMesh mesh = Util.triangleFrame01();
    final SimplicialComplex2D cmplx =
      (SimplicialComplex2D) mesh.cmplx();
    final MeshTranslator b = maker.apply(cmplx);
    final Set<Edge> primal = b.build();
    Assertions.assertEquals(3, primal.size());
    Assertions.assertEquals(1, b.index().nFaces());
    Assertions.assertEquals(6, b.index().nDEdges());
    Assertions.assertEquals(3, b.index().nBoundary());
    Assertions.assertEquals(3, b.index().nInterior());

  }

  //--------------------------------------------------------------------

private final void rectangleFrame
  (final Function<SimplicialComplex2D, MeshTranslator> maker) {

    final TriangleMesh mesh =
      Util.rectangleFrame(0.0,1.0,0.0,1.0);
    final SimplicialComplex2D cmplx =
      (SimplicialComplex2D) mesh.cmplx();
    final MeshTranslator b = maker.apply(cmplx);
    final Set<Edge> primal = b.build();
    Assertions.assertEquals(5, primal.size());
    Assertions.assertEquals(2, b.index().nFaces());
    Assertions.assertEquals(10, b.index().nDEdges());
    Assertions.assertEquals(6, b.index().nInterior());
    Assertions.assertEquals(4, b.index().nBoundary());
//    for (final HEdge q : primal) { System.out.println(q); }
  }

  //--------------------------------------------------------------------

  private final void hexagonFrame
    (final Function<SimplicialComplex2D, MeshTranslator> maker) {

    final TriangleMesh mesh =
      Util.hexagonFrame(0.0,1.0,0.0,1.0);
    final SimplicialComplex2D cmplx =
      (SimplicialComplex2D) mesh.cmplx();
    final MeshTranslator b = maker.apply(cmplx);
    final Set<Edge> primal = b.build();
    Assertions.assertEquals(9, primal.size());
    Assertions.assertEquals(4, b.index().nFaces());
    Assertions.assertEquals(18, b.index().nDEdges());
    Assertions.assertEquals(12, b.index().nInterior());
    Assertions.assertEquals(6, b.index().nBoundary());
//    for (final HEdge q : primal) { System.out.println(q); }
  }

  //--------------------------------------------------------------------

  private static final List<Function<SimplicialComplex2D, MeshTranslator>>
    delaunayMakers =
    List.of(
      HEdgeTranslator::make,
      QEdgeTranslator::make);

  @Test
  public final void runTests () {
    for (Function<SimplicialComplex2D, MeshTranslator> maker :
      delaunayMakers) {
      triangleFrame(maker);
      rectangleFrame(maker);
      hexagonFrame(maker); } }

  //--------------------------------------------------------------
} // end class
//--------------------------------------------------------------
