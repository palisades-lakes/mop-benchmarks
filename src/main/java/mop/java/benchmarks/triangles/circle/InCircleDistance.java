package mop.java.benchmarks.triangles.circle;

import mop.java.benchmarks.triangles.Defaults;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.triangle.Triangle2D;

/** <pre>
 * mvn -q install && jmh mop.java.benchmarks.triangles.circle.InCircleDistance
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-23
 */

public class InCircleDistance extends Base {

  @Override
  public final double operation (final Triangle2D t,
                                 final VectorD2 p) {
    return t.inCircleDistance(p); }

  @SuppressWarnings("unused")
  public static final void main (final String[] args)  {
    Defaults.run("RandomInCircle"); } }
