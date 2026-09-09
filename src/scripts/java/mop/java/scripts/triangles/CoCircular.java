package mop.java.scripts.triangles;

import com.carrotsearch.hppc.ObjectIntHashMap;
import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.procedures.ObjectIntProcedure;
import mop.java.geometry.Generators;
import mop.java.geometry.triangle.*;
import mop.java.geometry.triangle.jts.*;
import mop.java.geometry.triangle.shewchuk.*;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.geometry.euclidean.twod.shape.Circle;

import java.util.List;

import static mop.java.geometry.triangle.Triangle2D.makeTriangles;
import static mop.java.geometry.triangle.Triangle2D.truth;

/** <pre>
 * mvn clean install && j src/scripts/java/mop/java/scripts/triangles/Cocircular.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-09
 */

public final class CoCircular {

  //--------------------------------------------------------------

  private static final void
  checkInCircle (final Triangle2D t0,
                 final Vector2D p,
                 final ObjectIntMap<Class> successes,
                 final ObjectIntMap<Class> zeros) {
    final List<Triangle2D> triangles = makeTriangles(t0);
    final Triangle2D gold = truth(t0);
    final double trueInCircle = gold.inCircle(p);
    for (final Triangle2D t : triangles) {
      final Class c = t.getClass();
      // make sure there's an entry for every triangle class
      successes.putIfAbsent(c,0);
      zeros.putIfAbsent(c,0);
//      System.out.println(c.getSimpleName());
      final double tin = t.inCircle(p);
      if (trueInCircle==tin) { successes.addTo(c,1); }
      if (0.0==tin) { zeros.addTo(c,1); } } }

  //--------------------------------------------------------------

  private static final Vector2D boundaryPt (final Vector2D v,
                                            final Circle circle) {
    final Vector2D c = circle.getCenter();
    final double r = circle.getRadius();
    return v.subtract(c).withNorm(r).add(c); }

  //--------------------------------------------------------------

  public static final int
  coCircular (final ObjectIntMap<Class> successes,
             final ObjectIntMap<Class> zeros) {

    final int ncircles = 1023;
    final int npts = 1023;

    final Generator centerGenerator = Generators.vector2dGenerator(
      Doubles.laplaceGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
        0.0, 1.0));
    final Generator radiusGenerator = Doubles.exponentialGenerator(
      PRNG.well44497b("seeds/Well44497b-2019-01-09.txt"),
      1.0);
    final Generator circleGenerator =
      Generators.circleGenerator(centerGenerator,radiusGenerator);
    final Generator pointGenerator = Generators.vector2dGenerator(
      Doubles.laplaceGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        0.0, 1.0));
    int ntrys = 0;
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
        checkInCircle(ti,pij,successes,zeros);
        ntrys++; } }

    return ntrys; }

  //--------------------------------------------------------------------

  @SuppressWarnings("unused")
  public static final void main (final String[] args) {

    final ObjectIntMap<Class> successes = new ObjectIntHashMap<>();
    final ObjectIntMap<Class> zeros = new ObjectIntHashMap<>();
    final int ntrys = coCircular(successes,zeros);
    final ObjectIntProcedure<Class> printEntry =
      (final Class key, final int value) ->
        System.out.println(
          key.getSimpleName() + ", " +
            value + ", " + (100*value)/ntrys + "%");
    successes.forEach(printEntry);
    System.out.println();
    zeros.forEach(printEntry); }

  //--------------------------------------------------------------------
  // disable construction
  //--------------------------------------------------------------------

  private CoCircular () {
    throw new UnsupportedOperationException(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
