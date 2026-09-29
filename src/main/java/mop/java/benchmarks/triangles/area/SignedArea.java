package mop.java.benchmarks.triangles.area;

import mop.java.benchmarks.triangles.Defaults;
import mop.java.geometry.triangle.TriangleR2;

/** <pre>
 * mvn -q install && jmh mop.java.benchmarks.triangles.area.SignedArea
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-26
 */

public class SignedArea extends Base {

  @Override
  public final double operation (final TriangleR2 t) {
    return t.twiceSignedArea(); }

  public static final void main (final String[] ignore)  {
    Defaults.run("SignedArea"); } }
