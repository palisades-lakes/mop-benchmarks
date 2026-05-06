package mop.java.benchmarks.arithmetic;

/** <pre>
 * java --enable-preview -cp target\benchmarks.jar mop.java.benchmarks.arithmetic.DivideAndRemainder
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2023-12-31
 */

public class DivideAndRemainder extends Base {
  @Override
  public final Object operation (final Object z0,
                                 final Object z1) {
    return Naturals.get().divideAndRemainder(z0, z1); }
  public static final void main (final String[] args) {
    Defaults.run("DivideAndRemainder"); } }
