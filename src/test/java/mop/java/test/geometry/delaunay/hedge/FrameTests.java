package mop.java.test.geometry.delaunay.hedge;

import mop.java.geometry.delaunay.hedge.LischinskiIncrementalBuilder;
import mop.java.geometry.euclidean.VectorD2;
import org.junit.jupiter.api.Test;

/** <pre>
 * mvn -q -Dtest=mop.java.test.geometry.delaunay.hedge.FrameTests test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-11-10
 */

public final class FrameTests {

  //--------------------------------------------------------------

//  @Test
//  public final void spliceTest () {
//
//    // Construct a triangle containing the unit square:
//    final VectorD2 p0 = new VectorD2( -1.0, -1.0);
//    final VectorD2 p1 = new VectorD2(  2.0, -1.0);
//    final VectorD2 p2 = new VectorD2(  0.5,  3.0);
//
//    final ZeroSimplex z0 = ZeroSimplex.make("z0");
//    final ZeroSimplex z1 = ZeroSimplex.make("z1");
//    final ZeroSimplex z2 = ZeroSimplex.make("z2");
//    final Map<ZeroSimplex,VectorD2> embedding =
//      Map.of(z0,p0,z1,p1,z2,p2);
//
//    final HEdge e01 = HEdge.make(z0, z1);
//    final HEdge e12 = HEdge.make(z1, z2);
//    final HEdge e20 = HEdge.make(z2, z0);
//
//    System.out.println("\ne01:" + e01);
//    System.out.println("\ne12:" + e12);
//    System.out.println("\ne20:" + e20);
//
//    e01.splice(e20.reverse());
//    System.out.println("\nsplice e01,e20.reverse:");
//    System.out.println("\ne01:" + e01);
//    System.out.println("\ne12:" + e12);
//    System.out.println("\ne20:" + e20);
//
//    e12.splice(e01.reverse());
//    System.out.println("\nsplice e12,e01.reverse:");
//    System.out.println("\ne01:" + e01);
//    System.out.println("\ne12:" + e12);
//    System.out.println("\ne20:" + e20);
//
//    e20.splice(e12.reverse());
//    System.out.println("\nsplice e20,e12.reverse:");
//    System.out.println("\ne01:" + e01);
//    System.out.println("\ne12:" + e12);
//    System.out.println("\ne20:" + e20);
//
//    System.out.println("\nmesh:");
//    e01.printMesh();
//
//    final LischinskiIncrementalBuilder builder =
//      LischinskiIncrementalBuilder.make(embedding,e01);
//
//    System.out.println(builder.build());
//  }

  //--------------------------------------------------------------

  @Test
  public final void triangleFrame () {

    // Construct a triangle containing the unit square:
    final VectorD2 p0 = new VectorD2( -1.0, -1.0);
    final VectorD2 p1 = new VectorD2(  2.0, -1.0);
    final VectorD2 p2 = new VectorD2(  0.5,  3.0);

    final LischinskiIncrementalBuilder frame =
      LischinskiIncrementalBuilder.triangleFrame(p0, p1, p2);
    frame.startingEdge().validateMesh();
//    System.out.println("triangle:");
//    frame.startingEdge().printMesh();
    // Note: returns two triangular faces
    // in opposite orientation
    // TODO: how does the delaunay algorithm
    //  determine which is 'inside' and which 'outside'?
    //  Is it the embedding?
    //  Wouldn't have any inside/outside in spherical
    //  delaunay.
//    System.out.println();
//    System.out.println(frame.build());
  }

  //--------------------------------------------------------------

  @Test
  public final void rectangleFrame () {

    final LischinskiIncrementalBuilder frame =
      LischinskiIncrementalBuilder.rectangleFrame(
        0.0,1.0,0.0,1.0);
    frame.startingEdge().validateMesh();
//    System.out.println("\nrectangle:");
//    frame.startingEdge().printMesh();
    // Note: returns the 2 triangular faces, excludes the
    // 'reverse' quad face closing the manifold, because
    // it is not a simplex.
    // TODO: how does the delaunay algorithm
    //  determine which is 'inside' and which 'outside'?
    //  Is it the embedding?
//    System.out.println();
//    System.out.println(frame.build());
  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
