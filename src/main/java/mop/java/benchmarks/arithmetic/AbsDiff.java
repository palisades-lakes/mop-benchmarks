package mop.java.benchmarks.arithmetic;

/** <pre>
 * java --enable-preview -cp target\benchmarks.jar mop.java.benchmarks.arithmetic.AbsDiff
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */

public class AbsDiff extends Base {

  @Override
  public final Object operation (final Object z0,
                                 final Object z1) {
    return Naturals.get().absDiff(z0,z1); }

  @SuppressWarnings("unused")
  public static final void main (final String[] args)  {
    Defaults.run("AbsDiff"); } }
