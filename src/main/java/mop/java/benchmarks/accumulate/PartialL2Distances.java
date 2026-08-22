package mop.java.benchmarks.accumulate;

import mop.java.accumulators.Accumulator;

/** <pre>
 * java -cp target\benchmarks.jar mop.java.PartialL2Distances
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */

public class PartialL2Distances extends Base {

  @Override
  public final double[] operation (final Accumulator ac,
                                   final double[] z0,
                                   final double[] z1) {
    return ac.clear().partialL2Distances(z0,z1); }

  @SuppressWarnings("unused")
  public static final void main (final String[] args)  {
    Defaults.run("PartialL2Distances"); } }
