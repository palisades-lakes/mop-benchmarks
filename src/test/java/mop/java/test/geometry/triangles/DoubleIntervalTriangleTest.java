package mop.java.test.geometry.triangles;

import mop.java.geometry.triangle.*;
import mop.java.numbers.DoubleInterval;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

//----------------------------------------------------------------
/** check that the intervals contain the corresponding
 * <code>DoubleTriangle2D</code> and <code>BigFloatTriangle2D</code>
 * quantities.
 * <pre>
 * mvn -Dtest=mop.java.test.geometry.triangles.DoubleIntervalTriangleTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-01
 */

public final class DoubleIntervalTriangleTest extends TriangleTest {


  //--------------------------------------------------------------

  private static final void inCircle (final Triangle2D t,
                                      final Vector2D p) {
    final DoubleIntervalTriangle2D dit =
      (DoubleIntervalTriangle2D)
        DoubleIntervalTriangle2D.from(t);
    final DoubleInterval ditd = dit.inCircleInterval(p);
    final Triangle2D dt = DoubleTriangle2D.from(t);
    final double dtd = dt.inCircleDistance(p);
    Assertions.assertTrue(
      ditd.contains(dtd),
      ditd +
        "\ndoes not contain DoubleTriangle2D:\n" +
        Double.toHexString(dtd));
    final Triangle2D bft = BigFloatTriangle2D.from(t);
    final double bftd = dt.inCircleDistance(p);
    Assertions.assertTrue(
      ditd.contains(bftd),
      ditd +
        "\ndoes not contain BigFloatTriangle2D:\n" +
        Double.toHexString(bftd));
  }

  //--------------------------------------------------------------

  @Test
  public final void simpleTest () {
    final Vector2D p0 =  Vector2D.of( 0.0, 0.0);
    final Vector2D p1 =  Vector2D.of( 1.0, 1.0);
    final Vector2D p2 =  Vector2D.of( -1.0, 1.0);
    final Vector2D p3 =  Vector2D.of( -1.0, -1.0);
    final Vector2D p4 =  Vector2D.of( 1.0, -1.0);

    final Triangle2D t = TriangleVector2D.of(p1,p2,p3);
    inCircle(t, p0);
    inCircle(t, p4);
    inCircle(t, p1);
  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
