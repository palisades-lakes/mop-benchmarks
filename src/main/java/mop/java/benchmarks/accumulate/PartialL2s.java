package mop.java.benchmarks.accumulate;

import mop.java.accumulators.Accumulator;

/** <pre>
 * java -cp target\benchmarks.jar mop.java.PartialL2s
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2019-07-27
 */

@SuppressWarnings("unchecked")
public class PartialL2s extends Base {

  @Override
  public final double[] operation (final Accumulator ac,
                                   final double[] z0,
                                   final double[] z1) {
    return ac.clear().partialL2s(z0); }

  public static final void main (final String[] args)  {
    Defaults.run("PartialL2s"); } }
