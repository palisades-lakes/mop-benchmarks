package mop.java.test.accumulators;

import mop.java.accumulators.BigFloatAccumulator;
import mop.java.benchmarks.accumulate.Common;
import org.junit.jupiter.api.Test;

import java.util.List;

//----------------------------------------------------------------
/** Test summation algorithms.
 * <p>
 * <pre>
 * mvn -q test -Dtest=xfp/java/test/accumulators/RationalFloatAccumulatorTest > RFAT.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-19
 */

public final class RationalFloatAccumulatorTest {

  //--------------------------------------------------------------
  private static final int DIM = 517;
  private static final List<String> accumulators =
    List.of("mop.java.accumulators.RationalFloatAccumulator");

  @SuppressWarnings("static-method")
  @Test
  public final void tests () {
    //Debug.DEBUG=true;
    //Debug.println();
    //Debug.println(Classes.className(this));
    Common.l2Tests(
      Common.generators(DIM),
      Common.makeAccumulators(accumulators),
      BigFloatAccumulator.make());
    Common.sumTests(
      Common.generators(DIM),
      Common.makeAccumulators(accumulators),
      BigFloatAccumulator.make());
    Common.dotTests(
      Common.generators(DIM),
      Common.makeAccumulators(accumulators),
      BigFloatAccumulator.make());
    //Debug.DEBUG=false;
  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
