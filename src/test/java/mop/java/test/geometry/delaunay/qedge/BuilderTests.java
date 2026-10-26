package mop.java.test.geometry.delaunay.qedge;

import mop.java.cmplx.SimplicialComplex2D;
import mop.java.geom.mesh.TriangleMesh;
import mop.java.geometry.delaunay.Util;
import mop.java.geometry.delaunay.qedge.QEdgeBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** <pre>
 * mvn -Dtest=mop.java.test.geometry.delaunay.qedge.FrameTests test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-10-25
 */

public final class BuilderTests {

  //--------------------------------------------------------------

  @Test
  public final void triangleFrame () {

    final TriangleMesh mesh = Util.triangleFrame01();
    final SimplicialComplex2D cmplx =
      (SimplicialComplex2D) mesh.cmplx();
    final QEdgeBuilder b = QEdgeBuilder.make(cmplx);
    // should be 3 left and 0 right, but that uses facts about
    // triangleFrame01
    Assertions.assertEquals(
      3,
      b.leftFaces().size() + b.rightFaces().size()); }

  //--------------------------------------------------------------

  @Test
  public final void rectangleFrame () {

    final TriangleMesh mesh =
      Util.rectangleFrame(0.0,1.0,0.0,1.0);
    final SimplicialComplex2D cmplx =
      (SimplicialComplex2D) mesh.cmplx();
    final QEdgeBuilder b = QEdgeBuilder.make(cmplx);
    // should be 5 left and 1 right, but that uses facts about
    // rectangleFrame
    Assertions.assertEquals(
      6,
      b.leftFaces().size() + b.rightFaces().size()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
