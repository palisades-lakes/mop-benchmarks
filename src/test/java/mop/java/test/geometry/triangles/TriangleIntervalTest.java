package mop.java.test.geometry.triangles;

import mop.java.geometry.Generators;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.triangle.*;
import mop.java.numbers.BigFloat;
import mop.java.numbers.Doubles;
import mop.java.numbers.RelaxedInterval;
import mop.java.numbers.RoundingInterval;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

//----------------------------------------------------------------
/** Check that the intervals contain the corresponding
 * <code>TriangleD2Eager</code> and <code>TriangleBF2</code>
 * quantities.
 * <pre>
 * mvn -Dtest=mop.java.test.geometry.triangles.TriangleIntervalTest test
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-21
 */

public final class TriangleIntervalTest {

  //--------------------------------------------------------------

  private static final void inCircle (final Triangle2D t,
                                      final VectorD2 p) {
    final TriangleBF2 bft =
      (TriangleBF2) TriangleBF2.from(t);
    final BigFloat bftbf = bft.inCircleDistanceBF(p).reduce();
    final double bftd = bftbf.doubleValue();

    final RelaxedIntervalTriangle2D dit =
      (RelaxedIntervalTriangle2D)
        RelaxedIntervalTriangle2D.from(t);
    final RelaxedInterval ditd = dit.inCircleInterval(p);

    final RoundingIntervalTriangle2D rit =
      (RoundingIntervalTriangle2D)
        RoundingIntervalTriangle2D.from(t);
    final RoundingInterval ritd = rit.inCircleInterval(p);

    final ShewchukIntervalTriangle2D sit =
      (ShewchukIntervalTriangle2D) ShewchukIntervalTriangle2D.from(t);
    final RelaxedInterval sitd = sit.inCircleInterval(p);

    final Triangle2D dt = TriangleD2Eager.from(t);
    final double dtd = dt.inCircleDistance(p);

    // Shewchuk error should be looser than minimal intervals
    Assertions.assertTrue(
      sitd.contains(ritd),
      "\n\n" + t + "\n" +
        "\n" + p + "\n" +
        "\nShewchuk:\n" +
        sitd + "=\n" +
        Double.toHexString(sit.inCircleDistance(p)) + " +/- " +
        Double.toHexString(sit.inCircleBound(p)) + "\n" +
        "\ndoes not contain:\n" +
        "\nRounding:\n" +
        ritd + "\n" +
        "\nBF distance:\n" +
        bftbf + "\n" +
        Double.toHexString(bftd) + "\n\n");

    // not true for relaxed intervals
//    Assertions.assertTrue(
//      sitd.contains(ditd),
//      "\n\n" + t + "\n" +
//        "\n" + p + "\n" +
//        "\nShewchuk:\n" +
//        sitd + "=\n" +
//        Double.toHexString(sit.inCircleDistance(p)) + " +/- " +
//        Double.toHexString(sit.inCircleBound(p)) + "\n" +
//        "\ndoes not contain:\n" +
//        "\nRelaxed:\n" +
//        ditd + "\n" +
//        dit.description() +
//        "\nBF distance:\n" +
//        bftbf + "\n" +
//        Double.toHexString(bftd) +
//        "\n\nRound:\n" +
//        ritd + "\n" +
//        rit.description() +
//        "\n\n");

    Assertions.assertTrue(
      ditd.contains(dtd),
      ditd +
        "\ndoes not contain:\n" +
        Double.toHexString(dtd));

    // TODO: issues in arithmetic for single point RoundingInterval
    Assertions.assertTrue(
      ditd.contains(bftd),
      ditd +
        "\ndoes not contain (TriangleBF2):\n" +
        Double.toHexString(bftd));

    Assertions.assertTrue(
      sitd.contains(bftd),
      ditd +
        "\ndoes not contain TriangleBF2:\n" +
        Double.toHexString(bftd)); }

  private static final void coCircular (final Triangle2D t,
                                        final VectorD2 p) {
    inCircle(t,p);

//    final TriangleBF2 bft =
//      (TriangleBF2) TriangleBF2.from(t);
//    final BigFloat bftbf = bft.inCircleDistanceBF(p).reduce();
//    final double bftd = bftbf.doubleValue();
//
//    final RoundingIntervalTriangle2D rit =
//      (RoundingIntervalTriangle2D)
//        RoundingIntervalTriangle2D.from(t);
//    final RoundingInterval ritd = rit.inCircleInterval(p);
//
//    final RelaxedIntervalTriangle2D dit =
//      (RelaxedIntervalTriangle2D)
//        RelaxedIntervalTriangle2D.from(t);
//    final RelaxedInterval ditd = dit.inCircleInterval(p);
//
//    final ShewchukIntervalTriangle2D sit =
//      (ShewchukIntervalTriangle2D) ShewchukIntervalTriangle2D.from(t);
//    final RelaxedInterval sitd = sit.inCircleInterval(p);
//
//    final Triangle2D dt = TriangleD2Eager.from(t);
//    final double dtd = dt.inCircleDistance(p);
//
//    // TODO: some generated <double> triangle plus point cases are not
    //  cocircular even in BigFloat. Generate and collect 4 pt sets
    //  which are cocircular in BigFloat precision
//    Assertions.assertTrue(
//      ditd.containsZero(),
//      "\n\nNot cocircular <double>:" + "\n" +
//        dit + "\n" + p + "\n" + ditd + "\n" +
//        Double.toHexString(dtd) + "\n" +
//        bftbf + "\n" +
//        Double.toHexString(bftd) + "\n");
//
//    Assertions.assertTrue(
//      sitd.containsZero(),
//      "\n\nNot cocircular <Shewchuk>:" + "\n" +
//        sit + "\n" + p + "\n" + sitd + "\n" +
//        Double.toHexString(dtd) + "\n" +
//        bftbf + "\n" +
//        Double.toHexString(bftd) + "\n");
  }

  //--------------------------------------------------------------

  @Test
  public final void simpleIncircleTest () {
    final VectorD2 p0 =  new VectorD2( 0.0, 0.0);
    final VectorD2 p1 =  new VectorD2( 1.0, 1.0);
    final VectorD2 p2 =  new VectorD2( -1.0, 1.0);
    final VectorD2 p3 =  new VectorD2( -1.0, -1.0);
    final VectorD2 p4 =  new VectorD2( 1.0, -1.0);

    final Triangle2D t = TriangleD2Lazy.of(p1, p2, p3);
    inCircle(t, p0);
    coCircular(t, p4);
    coCircular(t, p1); }

  //--------------------------------------------------------------

  @Test
  public final void
  cocircularTest () {

    final Generator centerGenerator = Generators.vectorD2Generator(
      Doubles.laplaceGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
        0.0, 100000.0));
    final Generator radiusGenerator = Doubles.exponentialGenerator(
      PRNG.well44497b("seeds/Well44497b-2019-01-09.txt"),
      1.0);

    final Generator pointGenerator = Generators.vectorD2Generator(
      Doubles.laplaceGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        0.0, 1.0));

    final int ncircles = 65;
    final int npts = 65;
    for (int i=0;i<ncircles;i++) {
      final VectorD2 c = (VectorD2) centerGenerator.next();
      final double r = radiusGenerator.nextDouble();
      final Triangle2D ti =
        TriangleD2Lazy.of(
          ((VectorD2) pointGenerator.next()).project(c,r),
          ((VectorD2) pointGenerator.next()).project(c,r),
          ((VectorD2) pointGenerator.next()).project(c,r));
      for (int j=0;j<npts;j++) {
        final VectorD2 pij =
          ((VectorD2) pointGenerator.next()).project(c,r);
        coCircular(ti,pij); } } }

  //--------------------------------------------------------------

  private static final void colinearSignedArea (final Triangle2D t) {

    final TriangleD2Eager dt =
      (TriangleD2Eager) TriangleD2Eager.from(t);
    final double dtd = dt.twiceSignedArea();

    final TriangleBF2 bft =
      (TriangleBF2) TriangleBF2.from(t);
    final BigFloat bftbf = bft.getV20xV10().negate();
    final double bftd = bft.twiceSignedArea();

    final RoundingIntervalTriangle2D rit =
      (RoundingIntervalTriangle2D)
        RoundingIntervalTriangle2D.from(t);
    final RoundingInterval ritd = rit.twiceSignedAreaInterval();

    final RelaxedIntervalTriangle2D dit =
      (RelaxedIntervalTriangle2D)
        RelaxedIntervalTriangle2D.from(t);
    final RelaxedInterval ditd = dit.twiceSignedAreaInterval();

    final ShewchukIntervalTriangle2D sit =
      (ShewchukIntervalTriangle2D) ShewchukIntervalTriangle2D.from(t);
    final RelaxedInterval sitd = sit.twiceSignedAreaInterval();

    // Shewchuk should be a looser error bound than interval arithmetic!
    Assertions.assertTrue(
      sitd.contains(ditd),
      "\nShewchuk:\n" +
        sitd + "=\n" +
        Double.toHexString(sit.twiceSignedArea()) + " +/- " +
        Double.toHexString(sit.areaBound()) + "\n" +
        "\ndoes not contain:\n" +
        "\nDouble:\n" +
        ditd + "\n" +
        "\nBF area:\n" + bft.getV20xV10().negate().reduce() + "\n" +
        Double.toHexString(
          bft.getV20xV10().negate().reduce().doubleValue()) + "\n\n");

    Assertions.assertTrue(
      sitd.contains(ritd),
      "\nShewchuk:\n" +
        sitd + "=\n" +
        Double.toHexString(sit.twiceSignedArea()) + " +/- " +
        Double.toHexString(sit.areaBound()) + "\n" +
        "\ndoes not contain:\n" +
        "\nRounding:\n" +
        ritd + "\n" +
        "\nBF area:\n" + bft.getV20xV10().negate().reduce() + "\n" +
        Double.toHexString(
          bft.getV20xV10().negate().reduce().doubleValue()) + "\n\n");

    Assertions.assertTrue(
      sitd.contains(bftd),
      sitd +
        "\ndoes not contain TriangleBF2:\n" +
        Double.toHexString(bftd));

    Assertions.assertTrue(
      ritd.contains(bftd),
      "\n" + rit + "\n" +
        ritd + "\n" +
        Double.toHexString(dtd) + "\n" +
        "\ndoes not contain TriangleBF2 area:\n" +
        bftbf.reduce() + "\n" +
        Double.toHexString(bftd) + "\n");

    Assertions.assertTrue(
      ditd.contains(bftd),
      "\n" + dit + "\n" +
        ditd + "\n" +
        Double.toHexString(dtd) + "\n" +
        "\ndoes not contain TriangleBF2 area:\n" +
        bftbf.reduce() + "\n" +
        Double.toHexString(bftd) + "\n" +
        "\nRelaxedInterval\n" +
        ditd + "\n" +
        dit.description() + "\n" +
        "\nRoundingInterval\n" +
        ritd + "\n" +
        rit.description());

    Assertions.assertTrue(
      sitd.contains(dtd),
      sitd +
        "\ndoes not contain TriangleD2Eager:\n" +
        Double.toHexString(dtd));

    Assertions.assertTrue(
      ditd.contains(dtd),
      ditd +
        "\ndoes not contain TriangleD2Eager:\n" +
        Double.toHexString(dtd));


    Assertions.assertTrue(
      sitd.contains(dtd),
      sitd +
        "\ndoes not contain TriangleD2Eager:\n" +
        Double.toHexString(dtd));

    Assertions.assertTrue(
      ritd.contains(dtd),
      ritd +
        "\ndoes not contain TriangleD2Eager:\n" +
        Double.toHexString(dtd));

    // TODO: some generated <double> 3 pt sets are not
    //  colinear even in BigFloat. Generate and collect 3 pt sets
    //  which are colinear in BigFloat precision
//    Assertions.assertTrue(
//      ditd.containsZero(),
//      "\n\nNot colinear <double>:" + "\n" +
//        dit + "\n" + ditd + "\n" +
//        Double.toHexString(dtd) + "\n" +
//        bftbf + "\n" +
//        Double.toHexString(bftd) + "\n");
//
//    Assertions.assertTrue(
//      sitd.containsZero(),
//      "\n\nNot colinear <Shewchuk>:" + "\n" +
//        sit + "\n" + sitd + "\n" +
//        Double.toHexString(dtd) + "\n" +
//        Double.toHexString(bftd) + "\n");
  }

  //--------------------------------------------------------------

  @Test
  public final void
  colinearTest () {

    final Generator triangleGenerator =
      Generators.colinearTriangleGenerator(
        Generators.vectorD2Generator(
          Doubles.laplaceGenerator(
            PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
            0.0, 1.0)),
        Doubles.laplaceGenerator(
          PRNG.well44497b("seeds/Well44497b-2019-01-09.txt"),
          0.0, 1.0));

    final int ntriangles = 1023;
    for (int i=0;i<ntriangles;i++) {
      final Triangle2D ti = (Triangle2D) triangleGenerator.next();
      colinearSignedArea(ti); } }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
