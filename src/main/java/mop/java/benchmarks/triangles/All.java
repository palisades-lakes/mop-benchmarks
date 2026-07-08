package mop.java.benchmarks.triangles;

import mop.java.geometry.triangle.Triangle2D;

/** <pre>
 * java --enable-preview -cp target\benchmarks.jar mop.java.benchmarks.triangles.All
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-08
 */

public class All extends Base {

  @Override
  public final Object operation (final Object t) {
    return ((Triangle2D) t).signedArea(); }

  public static final void main (final String[] args)  {
    Defaults.run("AbsDiff");
    Defaults.run("Add");
    Defaults.run("DivideAndRemainder");
    Defaults.run("Multiply");
  } }
