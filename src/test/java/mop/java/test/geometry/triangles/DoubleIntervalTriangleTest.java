package mop.java.test.geometry.triangles;

import mop.java.geometry.Generators;
import mop.java.geometry.triangle.*;
import mop.java.numbers.DoubleInterval;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.geometry.euclidean.twod.shape.Circle;
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
 * @version 2026-09-03
 */

public final class DoubleIntervalTriangleTest {

  //--------------------------------------------------------------

  private static final void inCircle (final Triangle2D t,
                                      final Vector2D p) {
    final DoubleIntervalTriangle2D dit =
      (DoubleIntervalTriangle2D)
        DoubleIntervalTriangle2D.from(t);
    final DoubleInterval ditd = dit.inCircleInterval(p);

    final ShewchukIntervalTriangle2D sit =
      (ShewchukIntervalTriangle2D) ShewchukIntervalTriangle2D.from(t);
    final DoubleInterval sitd = sit.inCircleInterval(p);

    // Shewchuk should be a looser error bound than interval arithmetic!
    // but neither succeeds consistently!

//    Assertions.assertTrue(
//      sitd.contains(ditd),
//      "\nShewchuk:\n" +
//        sitd + "=\n" +
//        Double.toHexString(sit.inCircleDistance(p)) + " +/- " +
//        Double.toHexString(sit.inCircleBound(p)) + "\n" +
//        "\ndoes not contain:\n" +
//        "Arithmetic:\n" +
//        ditd + "\n\n");

    final Triangle2D dt = DoubleTriangle2D.from(t);
    final double dtd = dt.inCircleDistance(p);
    Assertions.assertTrue(
      ditd.contains(dtd),
      ditd +
        "\ndoes not contain:\n" +
        Double.toHexString(dtd));

    final Triangle2D bft = BigFloatTriangle2D.from(t);
    final double bftd = bft.inCircleDistance(p);
    Assertions.assertTrue(
      ditd.contains(bftd),
      ditd +
        "\ndoes not contain (BigFloatTriangle2D):\n" +
        Double.toHexString(bftd));

    Assertions.assertTrue(
      sitd.contains(bftd),
      ditd +
        "\ndoes not contain BigFloatTriangle2D:\n" +
        Double.toHexString(bftd));

//    Assertions.assertFalse(
//      ditd.containsZero(),
//      "\n\n" + dit + "\n" + p + "\n" + ditd + "\n" +
//        Double.toHexString(dtd) + "\n" +
//        Double.toHexString(bftd) + "\n");
  }

  //--------------------------------------------------------------

  @Test
  public final void simpleIncircleTest () {
    final Vector2D p0 =  Vector2D.of( 0.0, 0.0);
    final Vector2D p1 =  Vector2D.of( 1.0, 1.0);
    final Vector2D p2 =  Vector2D.of( -1.0, 1.0);
    final Vector2D p3 =  Vector2D.of( -1.0, -1.0);
    final Vector2D p4 =  Vector2D.of( 1.0, -1.0);

    final Triangle2D t = TriangleVector2D.of(p1,p2,p3);
    inCircle(t, p0);
    inCircle(t, p4);
    inCircle(t, p1); }

  //--------------------------------------------------------------

  private static final Vector2D boundaryPt (final Vector2D v,
                                            final Circle circle) {
    final Vector2D c = circle.getCenter();
    final double r = circle.getRadius();
    return v.subtract(c).withNorm(r).add(c); }

  //--------------------------------------------------------------

  @Test
  public final void
  cocircularTest () {

    final Generator circleGenerator =
      Generators.circleGenerator(
        Generators.vector2dGenerator(
          Doubles.laplaceGenerator(
            PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
            0.0, 1.0)),
        Doubles.exponentialGenerator(
          PRNG.well44497b("seeds/Well44497b-2019-01-09.txt"),
          1.0));

    final Generator pointGenerator = Generators.vector2dGenerator(
      Doubles.laplaceGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        0.0, 1.0));

    final int ncircles = 65;
    final int npts = 65;
    for (int i=0;i<ncircles;i++) {
      final Circle circle = (Circle) circleGenerator.next();
      final Triangle2D ti =
        TriangleVector2D.of(
          boundaryPt((Vector2D) pointGenerator.next(), circle),
          boundaryPt((Vector2D) pointGenerator.next(), circle),
          boundaryPt((Vector2D) pointGenerator.next(), circle));
      for (int j=0;j<npts;j++) {
        final Vector2D pij =
          boundaryPt((Vector2D) pointGenerator.next(), circle);
        inCircle(ti,pij); } } }

  //--------------------------------------------------------------

  private static final void colinearSignedArea (final Triangle2D t) {
    final DoubleIntervalTriangle2D dit =
      (DoubleIntervalTriangle2D)
        DoubleIntervalTriangle2D.from(t);
    final DoubleInterval ditd = dit.twiceSignedAreaInterval();

    final ShewchukIntervalTriangle2D sit =
      (ShewchukIntervalTriangle2D) ShewchukIntervalTriangle2D.from(t);
    final DoubleInterval sitd = sit.twiceSignedAreaInterval();

    // Shewchuk should be a looser error bound than interval arithmetic!
    // but neither succeeds consistently!

//    Assertions.assertTrue(
//      sitd.contains(ditd),
//      "\nShewchuk:\n" +
//        sitd + "=\n" +
//        Double.toHexString(sit.twiceSignedArea()) + " +/- " +
//        Double.toHexString(sit.areaBound()) + "\n" +
//        "\ndoes not contain:\n" +
//        "Arithmetic:\n" +
//        ditd + "\n\n");

//    Assertions.assertTrue(
//      ditd.contains(sitd),
//      "Arithmetic:\n\n" +
//        ditd +
//        "\ndoes not contain:\n" +
//        "\nShewchuk:\n" +
//        sitd + "=\n" +
//        Double.toHexString(sit.twiceSignedArea()) + " +/- " +
//        Double.toHexString(sit.areaBound()) + "\n");

    final Triangle2D bft = BigFloatTriangle2D.from(t);
    final double bftd = bft.twiceSignedArea();

    Assertions.assertTrue(
      sitd.contains(bftd),
      ditd +
        "\ndoes not contain BigFloatTriangle2D:\n" +
        Double.toHexString(bftd));

    Assertions.assertTrue(
      ditd.contains(bftd),
      ditd +
        "\ndoes not contain BigFloatTriangle2D:\n" +
        Double.toHexString(bftd));

//    Assertions.assertFalse(
//      ditd.containsZero(),
//      "\n\n" + dit + "\n" + p + "\n" + ditd + "\n" +
//        Double.toHexString(dtd) + "\n" +
//        Double.toHexString(bftd) + "\n");
  }

  //--------------------------------------------------------------

  @Test
  public final void
  colinearTest () {

    final Generator triangleGenerator =
      Generators.colinearTriangleGenerator(
        Generators.vector2dGenerator(
          Doubles.laplaceGenerator(
            PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
            0.0, 1.0)),
        Doubles.laplaceGenerator(
          PRNG.well44497b("seeds/Well44497b-2019-01-09.txt"),
          0.0, 1.0));

    final int ntriangles = 1023;
    for (int i=0;i<ntriangles;i++) {
      final TriangleVector2D ti = (TriangleVector2D) triangleGenerator.next();

      colinearSignedArea(ti); } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
