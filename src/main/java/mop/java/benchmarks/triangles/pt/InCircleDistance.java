package mop.java.benchmarks.triangles.pt;

import mop.java.benchmarks.triangles.Defaults;
import mop.java.geometry.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** <pre>
 * mvn clean install && jmh mop.java.benchmarks.triangles.pt.InCircleDistance
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-01
 */

public class InCircleDistance extends Base {

  @Override
  public final double operation (final Triangle2D t,
                                 final Vector2D p) {
    return t.inCircleDistance(p); }

  @SuppressWarnings("unused")
  public static final void main (final String[] args)  {
    Defaults.run("InCircle"); } }
