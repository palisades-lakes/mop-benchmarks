package mop.java.benchmarks.triangles;

import mop.java.geometry.triangle.Triangle2D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** <pre>
 * mvn clean install && java -cp target\benchmarks.jar mop.java.benchmarks.triangles.Orientation
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-29
 */

public class Orientation extends Base {

  @Override
  public final double operation (final Triangle2D t,
                                 final Vector2D p) {
    return t.orientation(); }

  @SuppressWarnings("unused")
  public static final void main (final String[] args)  {
    Defaults.run("Orientation"); } }
