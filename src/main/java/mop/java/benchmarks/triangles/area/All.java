package mop.java.benchmarks.triangles.area;

import mop.java.benchmarks.triangles.Defaults;

/** <pre>
 * mvn -q install && jmh mop.java.benchmarks.triangles.area.SignedArea
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-25
 */

public class All extends Base {

  public static final void main (final String[] ignore)  {
    Defaults.run("ColinearOrientation");
    Defaults.run("RandomOrientation");
    Defaults.run("SignedArea"); } }
