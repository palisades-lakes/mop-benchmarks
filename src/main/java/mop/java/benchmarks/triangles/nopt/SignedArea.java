package mop.java.benchmarks.triangles.nopt;

import mop.java.benchmarks.triangles.Defaults;
import mop.java.geometry.triangle.Triangle2D;

/** <pre>
 * mvn clean install && java -cp target\benchmarks.jar mop.java.benchmarks.triangles.nopt.SignedArea
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-31
 */

public class SignedArea extends Base {

  @Override
  public final double operation (final Triangle2D t) {
    return t.twiceSignedArea(); }

  @SuppressWarnings("unused")
  public static final void main (final String[] args)  {
    Defaults.run("SignedArea"); } }
