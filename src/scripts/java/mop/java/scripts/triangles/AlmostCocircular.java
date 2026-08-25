package mop.java.scripts.triangles;

import com.carrotsearch.hppc.ObjectIntHashMap;
import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.procedures.ObjectIntProcedure;
import mop.java.geometry.Generators;
import mop.java.geometry.triangle.*;
import mop.java.geometry.triangle.jts.*;
import mop.java.geometry.triangle.macro.*;
import mop.java.geometry.triangle.shewchuk.*;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.geometry.euclidean.twod.shape.Circle;

import java.util.List;

/** <pre>
 * mvn clean install && j src/scripts/java/mop/java/scripts/triangles/AlmostCocircular.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-23
 */

public final class AlmostCocircular {

  // ground truth predicate.
  public static final Triangle2D truth (final Triangle2D t) {
    return BigFloatTriangle2D.from(t); }

  public static final List<Triangle2D> makeTriangles (final Triangle2D t) {
    final Triangle2D triangleV2D = TriangleVector2D.from(t);
    final Triangle2D doubleTriangle = DoubleTriangle2D.from(t);
    final Triangle2D doubleIntervalTriangle = DoubleIntervalTriangle2D.from(t);
    final Triangle2D bigFloat = BigFloatTriangle2D.from(t);
    final Triangle2D dibf = DIBFTriangle2D.from(t);
    final Triangle2D rationalFloat = RationalFloatTriangle2D.from(t);
    final Triangle2D ddFast = DDFast.from(t);
    final Triangle2D ddNormalized = DDNormalized.from(t);
    final Triangle2D ddSlow = DDSlow.from(t);
    final Triangle2D doubleNonRobust = DoubleNonRobust.from(t);
    final Triangle2D inCircleNormalized = InCircleNormalized.from(t);
    final Triangle2D adapt = Adapt.from(t);
    final Triangle2D exact = Exact.from(t);
    final Triangle2D exactCache = ExactCache.from(t);
    final Triangle2D fast = Fast.from(t);
    final Triangle2D slow = Slow.from(t);
    final Triangle2D adaptMacro = AdaptMacro.from(t);
    final Triangle2D defaultMacro = DefaultMacro.from(t);
    final Triangle2D exactMacro = ExactMacro.from(t);
    final Triangle2D fastMacro = FastMacro.from(t);
    final Triangle2D slowMacro = SlowMacro.from(t);
    return List.of(
      // mine
      triangleV2D, rationalFloat,
      doubleTriangle, doubleIntervalTriangle,
      bigFloat,
      dibf,
      // JTS
      ddFast,ddNormalized,ddSlow,doubleNonRobust,inCircleNormalized,
      // Shewchuk predicates.c
      adapt,
      exact, exactCache
      ,
      fast ,slow,
      exactMacro, adaptMacro, defaultMacro, fastMacro, slowMacro
                  ); }

  //--------------------------------------------------------------

  private static final void
  checkInCircle (final Triangle2D t0,
                 final Vector2D p,
                 final ObjectIntMap<Class> successes) {
    final List<Triangle2D> triangles = makeTriangles(t0);
    final Triangle2D gold = truth(t0);
    final int trueInCircle = gold.inCircle(p);
    for (final Triangle2D t : triangles) {
      final Class c = t.getClass();
      if (t.inCircle(p) == trueInCircle) {
        successes.addTo(c,1); } } }

  //--------------------------------------------------------------
  // see https://inria.hal.science/inria-00344310v1/document
  // fig 2

  public static final int
  checkInCircles (final Triangle2D t,
                  final Vector2D p,
                  final ObjectIntMap<Class> successes) {
    int ntrys = 0;
    final int n = 4;
    double px = p.getX();
    for (int i=0;i<n;i++) {
      double py = p.getY();
      for (int j=0;j<n;j++) {
        final Vector2D pij = Vector2D.of(px, py);
        checkInCircle(t,pij,successes);
        ntrys++;
        py = Math.nextUp(py); }
      py = p.getY();
      for (int j=0;j<n;j++) {
        final Vector2D pij = Vector2D.of(px, py);
        checkInCircle(t,pij,successes);
        ntrys++;
        py = Math.nextDown(py); }
      px = Math.nextUp(px); }

    for (int i=0;i<n;i++) {
      double py = p.getY();
      for (int j=0;j<n;j++) {
        final Vector2D pij = Vector2D.of(px, py);
        checkInCircle(t,pij,successes);
        ntrys++;
        py = Math.nextUp(py); }
      py = p.getY();
      for (int j=0;j<n;j++) {
        final Vector2D pij = Vector2D.of(px, py);
        checkInCircle(t,pij,successes);
        ntrys++;
        py = Math.nextDown(py); }
      px = Math.nextDown(px);}

    return ntrys; }

  public static final int
  nearlyCocircluar (final ObjectIntMap<Class> successes) {

    final int ncircles = 33;
    final int npts = 33;

    final Generator centerGenerator = Generators.vector2dGenerator(
      Doubles.laplaceGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
        0.0, 100000.0));
    final Generator radiusGenerator = Doubles.exponentialGenerator(
      PRNG.well44497b("seeds/Well44497b-2019-01-09.txt"),
      1.0);
    final Generator circleGenerator =
      Generators.circleGenerator(centerGenerator,radiusGenerator);
    final Generator pointGenerator = Generators.vector2dGenerator(
      Doubles.laplaceGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        0.0, 1000.0));
    int ntrys = 0;
    for (int i=0;i<ncircles;i++) {
      final Circle circle = (Circle) circleGenerator.next();
      final Triangle2D ti =
        TriangleVector2D.of(
          circle.project((Vector2D) pointGenerator.next()),
          circle.project((Vector2D) pointGenerator.next()),
          circle.project((Vector2D) pointGenerator.next()));
      for (int j=0;j<npts;j++) {
        final Vector2D pij =
          circle.project((Vector2D) pointGenerator.next());
        ntrys += checkInCircles(ti,pij,successes); } }

    return ntrys; }

  //--------------------------------------------------------------------

  @SuppressWarnings("unused")
  public static final void main (final String[] args) {

    final ObjectIntMap<Class> successes = new ObjectIntHashMap<>();
    final int ntrys = nearlyCocircluar(successes);
    final ObjectIntProcedure<Class> printEntry =
      (Class key, int value) -> System.out.println(
        key.getSimpleName() + ", " +
          value + ", " + (100*value)/ntrys);
    successes.forEach(printEntry); }

  //--------------------------------------------------------------------
  // disable construction
  //--------------------------------------------------------------------

  private AlmostCocircular () {
    throw new UnsupportedOperationException(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
