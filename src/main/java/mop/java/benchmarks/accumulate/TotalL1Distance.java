package mop.java.benchmarks.accumulate;

import mop.java.accumulators.Accumulator;

/** <pre>
 * j mop.java.TotalL1Distance
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2019-08-19
 */

public class TotalL1Distance extends Base {

  @Override
  public final double[] operation (final Accumulator ac,
                                   final double[] z0,
                                   final double[] z1) {
    return new double[]
      { ac.clear().addL1Distance(z0,z1).doubleValue() }; }

  public static final void main (final String[] args)  {
    Defaults.run("TotalL1Distance"); } }
