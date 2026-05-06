package mop.java.benchmarks.accumulate;

import mop.java.accumulators.Accumulator;

/** <pre>
 * java -cp target\benchmarks.jar mop.java.PartialSums
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2019-07-27
 */

@SuppressWarnings("unchecked")
public class PartialSums extends Base {

  @Override
  public final double[] operation (final Accumulator ac,
                                   final double[] z0,
                                   final double[] z1) {
    return ac.clear().partialSums(z0); }

  public static final void main (final String[] args)  {
    Defaults.run("PartialSums"); } }
