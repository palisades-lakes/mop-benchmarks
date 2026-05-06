package mop.java.benchmarks.arithmetic;

/** <pre>
 * java --enable-preview -cp target\benchmarks.jar mop.java.benchmarks.arithmetic.All
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2024-01-17
 */

public class All extends Base {

  @Override
  public final Object operation (final Object z0,
                                 final Object z1) {
    return Naturals.get().add(z0,z1); }

  public static final void main (final String[] args)  {
    Defaults.run("AbsDiff");
    Defaults.run("Add");
    Defaults.run("DivideAndRemainder");
    Defaults.run("Multiply");
  } }
