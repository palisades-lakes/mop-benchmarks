package mop.java.test.accumulators;

import java.util.List;

import mop.java.accumulators.EFloatAccumulator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import mop.java.benchmarks.accumulate.Common;

//----------------------------------------------------------------
/** Test summation algorithms.
 * <p>
 * <pre>
 * mvn test -Dtest=nzqr/java/test/accumulators/SpireAlgebraicAccumulatorTest > SpireAlgebraicAccumulatorTest.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-12-02
 */

public final class SpireAlgebraicAccumulatorTest {

  //--------------------------------------------------------------
  private static final int DIM = 256;
  private static final List<String> accumulators =
    List.of("mop.java.accumulators.SpireAlgebraicAccumulator");

  @SuppressWarnings("static-method")
  @Test
  public final void tests () {
    Assertions.assertThrows(
      AssertionFailedError.class,
      () -> {
        Common.sumTests(
          Common.generators(DIM),
          Common.makeAccumulators(accumulators),
          EFloatAccumulator.make());
      },
      "fails because spire.math.Algebraic doesn't round to double correctly");

    Assertions.assertThrows(
      AssertionFailedError.class,
      () -> {
        Common.l2Tests(
          Common.generators(DIM),
          Common.makeAccumulators(accumulators),
          EFloatAccumulator.make());
      },
      "fails, reason unknown yet");

    Assertions.assertThrows(
      AssertionFailedError.class,
      () -> {
        Common.dotTests(
          Common.generators(DIM),
          Common.makeAccumulators(accumulators),
          EFloatAccumulator.make());
      },
      "fails, reason unknown yet");
  }
  //--------------------------------------------------------------
}
//--------------------------------------------------------------
