package mop.java.test.numbers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import mop.java.benchmarks.accumulate.Common;
import mop.java.numbers.SpireReals;
import spire.math.Real;

//----------------------------------------------------------------
/** Test desired properties of ERational.
 * <p>
 * <pre>
 * mvn -c clean -Dtest=nzqr/jmh/test/numbers/SpireRealTest test > SpireRealTest.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-12-02
 */

public final class SpireRealTest {

  @SuppressWarnings({ "static-method" })
  @Test
  public final void testRounding () {

    Assertions.assertThrows(
      AssertionFailedError.class,
      () -> {
        Common.doubleRoundingTests(
          SpireReals::toReal,
          SpireReals::toReal,
          q -> ((Real) q).doubleValue(),
          (q0,q1) -> ((Real) q0).$minus((Real) q1).abs(),
          q -> q.toString(),
          SpireReals::compareTo,
          SpireReals::compareTo);
      },
      "spire.math.Real doesn't round to double correctly");

    //    Assertions.assertThrows(
    //      AssertionFailedError.class,
    //      () -> {
    Common.floatRoundingTests(
      SpireReals::toReal,
      SpireReals::toReal,
      q -> ((Real) q).floatValue(),
      (q0,q1) -> ((Real) q0).$minus((Real) q1).abs(),
      q -> q.toString(),
      SpireReals::compareTo,
      SpireReals::compareTo);
    //      },
    //      "spire.math.Real doesn't round to float correctly");

  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
