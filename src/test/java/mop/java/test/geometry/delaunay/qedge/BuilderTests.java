package mop.java.test.geometry.delaunay.qedge;

import mop.java.cmplx.SimplicialComplex2D;
import mop.java.geom.mesh.TriangleMesh;
import mop.java.geometry.delaunay.Util;
import mop.java.geometry.delaunay.qedge.QEdge;
import mop.java.geometry.delaunay.qedge.QEdgeBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

/** <pre>
 * mvn -Dtest=mop.java.test.geometry.delaunay.qedge.BuilderTests test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-11-03
 */

public final class BuilderTests {

  //--------------------------------------------------------------

  @Test
  public final void triangleFrame () {

    final TriangleMesh mesh = Util.triangleFrame01();
    final SimplicialComplex2D cmplx =
      (SimplicialComplex2D) mesh.cmplx();
    final QEdgeBuilder b = QEdgeBuilder.make(cmplx);
    final Set<QEdge> primal = b.build();
    Assertions.assertEquals(3, primal.size());
    Assertions.assertEquals(1, b.nFaces());
    Assertions.assertEquals(6, b.nHalfEdges());
    Assertions.assertEquals(3, b.nBoundary());
    Assertions.assertEquals(3, b.nInterior());

//  for (final QEdge q : primal) { System.out.println(q); }
  }

  //--------------------------------------------------------------

  @Test
  public final void rectangleFrame () {

    final TriangleMesh mesh =
      Util.rectangleFrame(0.0,1.0,0.0,1.0);
    final SimplicialComplex2D cmplx =
      (SimplicialComplex2D) mesh.cmplx();
    final QEdgeBuilder b = QEdgeBuilder.make(cmplx);
    final Set<QEdge> primal = b.build();
    Assertions.assertEquals(5, primal.size());
    Assertions.assertEquals(2, b.nFaces());
    Assertions.assertEquals(10, b.nHalfEdges());
    Assertions.assertEquals(6, b.nInterior());
    Assertions.assertEquals(4, b.nBoundary());
//    for (final QEdge q : primal) { System.out.println(q); }
  }

  //--------------------------------------------------------------

  @Test
  public final void hexagonFrame () {

    final TriangleMesh mesh =
      Util.hexagonFrame(0.0,1.0,0.0,1.0);
    final SimplicialComplex2D cmplx =
      (SimplicialComplex2D) mesh.cmplx();
    final QEdgeBuilder b = QEdgeBuilder.make(cmplx);
    final Set<QEdge> primal = b.build();
    Assertions.assertEquals(9, primal.size());
    Assertions.assertEquals(4, b.nFaces());
    Assertions.assertEquals(18, b.nHalfEdges());
    Assertions.assertEquals(12, b.nInterior());
    Assertions.assertEquals(6, b.nBoundary());
//    for (final QEdge q : primal) { System.out.println(q); }
  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
