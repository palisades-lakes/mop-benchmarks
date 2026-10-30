package mop.java.test.geometry.delaunay.qedge;

import mop.java.geometry.delaunay.qedge.LischinskiIncrementalBuilder;
import mop.java.geometry.euclidean.VectorD2;
import org.junit.jupiter.api.Test;

/** <pre>
 * mvn -q -Dtest=mop.java.test.geometry.delaunay.qedge.FrameTests test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-10-30
 */

public final class FrameTests {

  //--------------------------------------------------------------

  @Test
  public final void triangleFrame () {

    // Construct a triangle containing the unit square:
    final VectorD2 p1 = new VectorD2( -1.0, -1.0);
    final VectorD2 p2 = new VectorD2(  2.0, -1.0);
    final VectorD2 p3 = new VectorD2(  0.5,  3.0);

    final LischinskiIncrementalBuilder frame =
      LischinskiIncrementalBuilder.triangleFrame(p1, p2, p3);
    System.out.println("triangle:");
    frame.startingEdge().printMesh();

  }

  //--------------------------------------------------------------

  @Test
  public final void rectangleFrame () {

    final LischinskiIncrementalBuilder frame =
      LischinskiIncrementalBuilder.rectangleFrame(
        0.0,1.0,0.0,1.0);
    System.out.println("\nrectangle:");
    frame.startingEdge().printMesh();
    System.out.println("mesh");
    System.out.println(frame.build()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
