package mop.java.scripts.triangles;

import com.carrotsearch.hppc.ObjectIntHashMap;
import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.procedures.ObjectIntProcedure;
import mop.java.geometry.triangle.BigFloatTriangle2D;
import mop.java.geometry.triangle.DIBFTriangle2D;
import mop.java.geometry.triangle.DoubleIntervalTriangle2D;
import mop.java.geometry.triangle.DoubleTriangle2D;
import mop.java.geometry.triangle.RationalFloatTriangle2D;
import mop.java.geometry.triangle.Triangle2D;
import mop.java.geometry.triangle.TriangleVector2D;
import mop.java.geometry.triangle.jts.DDFast;
import mop.java.geometry.triangle.jts.DDNormalized;
import mop.java.geometry.triangle.jts.DDSlow;
import mop.java.geometry.triangle.jts.DoubleNonRobust;
import mop.java.geometry.triangle.jts.InCircleNormalized;
import mop.java.geometry.triangle.macro.AdaptMacro;
import mop.java.geometry.triangle.macro.DefaultMacro;
import mop.java.geometry.triangle.macro.ExactMacro;
import mop.java.geometry.triangle.macro.FastMacro;
import mop.java.geometry.triangle.macro.SlowMacro;
import mop.java.geometry.triangle.shewchuk.Adapt;
import mop.java.geometry.triangle.shewchuk.Exact;
import mop.java.geometry.triangle.shewchuk.ExactCache;
import mop.java.geometry.triangle.shewchuk.Fast;
import mop.java.geometry.triangle.shewchuk.Slow;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

import java.util.List;

/** <pre>
 * mvn clean install && j src/scripts/java/mop/java/scripts/triangles/KettnerOrientation.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-23
 */

public final class KettnerOrientation {

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

  private static final void
  checkOrientations (final Triangle2D t0,
                     final ObjectIntMap<Class> successes) {
    final List<Triangle2D> triangles = makeTriangles(t0);
    final Triangle2D gold = truth(t0);
    final int trueOrientation = gold.orientation();
    for (final Triangle2D t : triangles) {
      final Class c = t.getClass();
      if (t.orientation() == trueOrientation) {
        successes.addTo(c,1); } } }

  //--------------------------------------------------------------
  // see https://inria.hal.science/inria-00344310v1/document
  // fig 2

  public static final int
  checkKettnerTriangles (final Vector2D p,
                         final Vector2D q,
                         final Vector2D r,
                         final ObjectIntMap<Class>successes) {
    double px = p.getX();
    double py = p.getY();
    final double ux = 0x1.0p-53; //Math.ulp(px);
    final double uy = 0x1.0p-53; //Math.ulp(py);
    final int n = 255;
    for (int i=0;i<n;i++) {
      final double pxi = px + i*ux;
      for (int j=0;j<n;j++) {
        final double pyj = py + j*uy;
        final Vector2D pij = Vector2D.of(pxi, pyj);
        final Triangle2D tij = TriangleVector2D.of(pij, q, r);
        checkOrientations(tij,successes); } }
    return n*n; }

  public static final int kettnerTriangles
    (final ObjectIntMap<Class> successes) {
    final int n0 = checkKettnerTriangles(
      Vector2D.of(0.5,0.5),
      Vector2D.of( 12, 12),
      Vector2D.of( 24, 24),
      successes);

    final int n1 = checkKettnerTriangles(
      Vector2D.of(0.50000000000002531,0.5000000000000171),
      Vector2D.of( 17.300000000000001,17.300000000000001),
      Vector2D.of( 24.00000000000005, 24.0000000000000517765),
      successes);

    final int n2 = checkKettnerTriangles(
      Vector2D.of(0.5,0.5),
      Vector2D.of( 8.8000000000000007, 8.8000000000000007),
      Vector2D.of( 12.1, 12.1),
      successes);
    return n0 + n1 + n2; }

  //--------------------------------------------------------------------

  @SuppressWarnings("unused")
  public static final void main (final String[] args) {
    final ObjectIntMap<Class> successes = new ObjectIntHashMap<>();
    final int ntriangles = kettnerTriangles(successes);
    final ObjectIntProcedure<Class> printEntry =
      (key, value) -> System.out.println(
        key.getSimpleName() + ", " +
          value + ", " + (100*value)/ntriangles);
    successes.forEach(printEntry); }

  //--------------------------------------------------------------------
  // disable construction
  //--------------------------------------------------------------------

  private KettnerOrientation () {
    throw new UnsupportedOperationException(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
