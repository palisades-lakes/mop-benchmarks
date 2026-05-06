package mop.java.benchmarks.accumulate;

import mop.java.accumulators.Accumulator;

/** <pre>
 * java -cp target\benchmarks.jar mop.java.TotalL2Norm
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2019-07-27
 */

public class TotalL2Norm extends Base {

  @Override
  public final double[] operation (final Accumulator ac,
                                   final double[] z0,
                                   final double[] z1) {
    return new double[]
      {  ac.clear().add2All(z0).doubleValue() }; }

  public static final void main (final String[] args)  {
    Defaults.run("TotalL2Norm"); } }
