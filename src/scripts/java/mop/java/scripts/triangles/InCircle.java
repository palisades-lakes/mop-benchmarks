package mop.java.scripts.triangles;

import mop.java.accumulators.ZhuHayesAccumulator;
import mop.java.geometry.Generators;
import mop.java.geometry.triangle.DoubleIntervalTriangle2D;
import mop.java.geometry.triangle.Triangle2D;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** <pre>
 * mvn clean install && jy src/scripts/java/mop/java/scripts/triangles/InCircle.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-15
 */

public final class InCircle {

  @SuppressWarnings("unused")
  public static final void main (final String[] args) {

    final Generator pointGenerator =
      Generators.vector2dGenerator(
        8192,
        Doubles.laplaceGenerator(
          PRNG.well44497b("seeds/Well44497b-2019-01-05.txt"),
          0.0, 1.0));
    final Vector2D[] points = (Vector2D[]) pointGenerator.next();

    final Generator triangleGenerator =
      Generators.triangleGenerator(
        8192,
        //ExactCache::from,
        //BigFloatTriangle2D::from,
        DoubleIntervalTriangle2D::from,
        Generators.vector2dGenerator(
          Doubles.laplaceGenerator(
            PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
            0.0, 1.0)));
    final Triangle2D[] triangles =
      (Triangle2D[]) triangleGenerator.next();

    final double[] d = new double[points.length*triangles.length];
    System.out.println(points.length);
    System.out.println(triangles.length);
    System.out.println(Math.multiplyExact(points.length,triangles.length));
    System.out.println(d.length);

    final int nreps = 64;
    for (int i=0; i<nreps; i++) {
      int k=0;
      for (final Triangle2D t : triangles) {
        for (final Vector2D p : points) {
          d[k++] = t.inCircle(p); } }

      final ZhuHayesAccumulator zh = ZhuHayesAccumulator.make();
      zh.addAll(d);
      System.out.println(
        i + ": mean distance:" + (zh.doubleValue() / d.length)); } }


  //--------------------------------------------------------------------
  // disable construction
  //--------------------------------------------------------------------

  private InCircle () {
    throw new UnsupportedOperationException(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
