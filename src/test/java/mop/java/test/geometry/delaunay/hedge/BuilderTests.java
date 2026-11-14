package mop.java.test.geometry.delaunay.hedge;

import mop.java.cmplx.SimplicialComplex2D;
import mop.java.geom.mesh.TriangleMesh;
import mop.java.geometry.delaunay.Util;
import mop.java.geometry.delaunay.hedge.HEdge;
import mop.java.geometry.delaunay.hedge.HEdgeBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

/** <pre>
 * mvn -Dtest=mop.java.test.geometry.delaunay.hedge.BuilderTests test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-11-10
 */

public final class BuilderTests {

  //--------------------------------------------------------------

  @Test
  public final void triangleFrame () {

    final TriangleMesh mesh = Util.triangleFrame01();
    final SimplicialComplex2D cmplx =
      (SimplicialComplex2D) mesh.cmplx();
    final HEdgeBuilder b = HEdgeBuilder.make(cmplx);
    final Set<HEdge> primal = b.build();
    Assertions.assertEquals(3, primal.size());
    Assertions.assertEquals(1, b.index().nFaces());
    Assertions.assertEquals(6, b.index().nDEdges());
    Assertions.assertEquals(3, b.index().nBoundary());
    Assertions.assertEquals(3, b.index().nInterior());

//  for (final HEdge q : primal) { System.out.println(q); }
  }

  //--------------------------------------------------------------

  @Test
  public final void rectangleFrame () {

    final TriangleMesh mesh =
      Util.rectangleFrame(0.0,1.0,0.0,1.0);
    final SimplicialComplex2D cmplx =
      (SimplicialComplex2D) mesh.cmplx();
    final HEdgeBuilder b = HEdgeBuilder.make(cmplx);
    final Set<HEdge> primal = b.build();
    Assertions.assertEquals(5, primal.size());
    Assertions.assertEquals(2, b.index().nFaces());
    Assertions.assertEquals(10, b.index().nDEdges());
    Assertions.assertEquals(6, b.index().nInterior());
    Assertions.assertEquals(4, b.index().nBoundary());
//    for (final HEdge q : primal) { System.out.println(q); }
  }

  //--------------------------------------------------------------

  @Test
  public final void hexagonFrame () {

    final TriangleMesh mesh =
      Util.hexagonFrame(0.0,1.0,0.0,1.0);
    final SimplicialComplex2D cmplx =
      (SimplicialComplex2D) mesh.cmplx();
    final HEdgeBuilder b = HEdgeBuilder.make(cmplx);
    final Set<HEdge> primal = b.build();
    Assertions.assertEquals(9, primal.size());
    Assertions.assertEquals(4, b.index().nFaces());
    Assertions.assertEquals(18, b.index().nDEdges());
    Assertions.assertEquals(12, b.index().nInterior());
    Assertions.assertEquals(6, b.index().nBoundary());
//    for (final HEdge q : primal) { System.out.println(q); }
  }

  //--------------------------------------------------------------
} // end class
//--------------------------------------------------------------
