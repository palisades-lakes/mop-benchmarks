package mop.java.benchmarks.triangles.area;

import mop.java.benchmarks.triangles.Defaults;
import mop.java.geometry.triangle.Triangle2D;

/** <pre>
 * mvn -q install && jmh mop.java.benchmarks.triangles.area.SignedArea
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-23
 */

public class SignedArea extends Base {

  @Override
  public final double operation (final Triangle2D t) {
    return t.twiceSignedArea(); }

  @SuppressWarnings("unused")
  public static final void main (final String[] args)  {
    Defaults.run("SignedArea"); } }
