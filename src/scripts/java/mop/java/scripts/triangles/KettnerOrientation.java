package mop.java.scripts.triangles;

import com.carrotsearch.hppc.ObjectIntHashMap;
import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.procedures.ObjectIntProcedure;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.triangle.Triangle2D;
import mop.java.geometry.triangle.TriangleD2Lazy;

import java.util.List;

/** <pre>
 * mvn clean install && j src/scripts/java/mop/java/scripts/triangles/KettnerOrientation.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-09
 */

public final class KettnerOrientation {

  private static final void
  checkOrientations (final Triangle2D t0,
                     final ObjectIntMap<Class> successes) {
    final List<Triangle2D> triangles = Triangle2D.makeTriangles(t0);
    final Triangle2D gold = Triangle2D.truth(t0);
    final double trueOrientation = gold.orientation();
    for (final Triangle2D t : triangles) {
      final Class c = t.getClass();
      if (t.orientation() == trueOrientation) {
        successes.addTo(c,1); } } }

  //--------------------------------------------------------------
  // see https://inria.hal.science/inria-00344310v1/document
  // fig 2

  public static final int
  checkKettnerTriangles (final VectorD2 p,
                         final VectorD2 q,
                         final VectorD2 r,
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
        final VectorD2 pij = new VectorD2(pxi, pyj);
        final Triangle2D tij = TriangleD2Lazy.of(pij, q, r);
        checkOrientations(tij,successes); } }
    return n*n; }

  public static final int kettnerTriangles
    (final ObjectIntMap<Class> successes) {
    final int n0 = checkKettnerTriangles(
      new VectorD2(0.5,0.5),
      new VectorD2( 12, 12),
      new VectorD2( 24, 24),
      successes);

    final int n1 = checkKettnerTriangles(
      new VectorD2(0.50000000000002531,0.5000000000000171),
      new VectorD2( 17.300000000000001,17.300000000000001),
      new VectorD2( 24.00000000000005, 24.0000000000000517765),
      successes);

    final int n2 = checkKettnerTriangles(
      new VectorD2(0.5,0.5),
      new VectorD2( 8.8000000000000007, 8.8000000000000007),
      new VectorD2( 12.1, 12.1),
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
