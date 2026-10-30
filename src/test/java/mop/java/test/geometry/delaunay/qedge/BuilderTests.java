package mop.java.test.geometry.delaunay.qedge;

import mop.java.cmplx.SimplicialComplex2D;
import mop.java.geom.mesh.TriangleMesh;
import mop.java.geometry.delaunay.Util;
import mop.java.geometry.delaunay.qedge.QEdgeBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** <pre>
 * mvn -Dtest=mop.java.test.geometry.delaunay.qedge.BuilderTests test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-10-29
 */

public final class BuilderTests {

  //--------------------------------------------------------------

  @Test
  public final void triangleFrame () {

    final TriangleMesh mesh = Util.triangleFrame01();
    final SimplicialComplex2D cmplx =
      (SimplicialComplex2D) mesh.cmplx();
    final QEdgeBuilder b = QEdgeBuilder.make(cmplx);
    // should be 3 left and 0 right,
    // but that makes assumptions about triangleFrame01
    Assertions.assertEquals(
      3, b.nLeftFaces() + b.nRightFaces());
    Assertions.assertEquals(3, b.nEdges());
    // should be 3 leftBoundary and 0 rightBoundary,
    // but that makes assumptions about triangleFrame01
    Assertions.assertEquals(3, b.nBoundary());
    Assertions.assertEquals(0, b.nInterior());
  }

  //--------------------------------------------------------------

  @Test
  public final void rectangleFrame () {

    final TriangleMesh mesh =
      Util.rectangleFrame(0.0,1.0,0.0,1.0);
    final SimplicialComplex2D cmplx =
      (SimplicialComplex2D) mesh.cmplx();
    final QEdgeBuilder b = QEdgeBuilder.make(cmplx);
    // should be 5 left and 1 right,
    // but that makes assumptions about rectangleFrame
    Assertions.assertEquals(
      6, b.nLeftFaces() + b.nRightFaces());
    Assertions.assertEquals(5, b.nEdges());
    Assertions.assertEquals(1, b.nInterior());
    // should be 4 left and 0 right,
    // but that makes assumptions about rectangleFrame
    Assertions.assertEquals(4, b.nBoundary());
  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
