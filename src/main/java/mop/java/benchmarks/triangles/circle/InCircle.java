package mop.java.benchmarks.triangles.circle;

import mop.java.benchmarks.triangles.Defaults;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.triangle.Triangle2D;

/** <pre>
 * mvn clean install && jmh mop.java.benchmarks.triangles.circle.InCircle
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-01
 */

public class InCircle extends Base {

  @Override
  public final double operation (final Triangle2D t,
                                 final VectorD2 p) {
    return t.inCircle(p); }

  @SuppressWarnings("unused")
  public static final void main (final String[] args)  {
    Defaults.run("InCircle"); } }
