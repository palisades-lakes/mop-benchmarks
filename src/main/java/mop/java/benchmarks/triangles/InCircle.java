package mop.java.benchmarks.triangles;


import mop.java.geometry.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** <pre>
 * mvn clean install && jmh mop.java.benchmarks.triangles.InCircle
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-17
 */

public class InCircle extends Base {

  @Override
  public final double operation (final Triangle2D t,
                                 final Vector2D p) {
    return t.inCircle(p); }

  @SuppressWarnings("unused")
  public static final void main (final String[] args)  {
    Defaults.run("InCircle"); } }
