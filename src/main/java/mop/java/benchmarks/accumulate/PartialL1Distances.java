package mop.java.benchmarks.accumulate;

import mop.java.accumulators.Accumulator;

/** <pre>
 * java -cp target\benchmarks.jar mop.java.PartialL1Distances
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2019-07-27
 */

@SuppressWarnings("unchecked")
public class PartialL1Distances extends Base {

  @Override
  public final double[] operation (final Accumulator ac,
                                   final double[] z0,
                                   final double[] z1) {
    return ac.clear().partialL1Distances(z0,z1); }

  public static final void main (final String[] args)  {
    Defaults.run("PartialL1Distances"); } }
