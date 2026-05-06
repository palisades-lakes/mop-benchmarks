package mop.java.test.numbers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import mop.java.benchmarks.accumulate.Common;
import mop.java.numbers.SpireAlgebraics;
import spire.math.Algebraic;

//----------------------------------------------------------------
/** Test desired properties of <code>spire.math.Algebraic</code>.
 * <p>
 * <pre>
 * mvn -c clean -Dtest=nzqr/jmh/test/numbers/SpireAlgebraicTest test > SpireAlgebraicTest.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-12-02
 */

public final class SpireAlgebraicTest {

  @SuppressWarnings({ "static-method" })
  @Test
  public final void testRounding () {

    Assertions.assertThrows(
      AssertionFailedError.class,
      () -> {
        Common.doubleRoundingTests(
          SpireAlgebraics::toAlgebraic,
          SpireAlgebraics::toAlgebraic,
          q -> ((Algebraic) q).doubleValue(),
          (q0,q1) -> ((Algebraic) q0).$minus((Algebraic) q1).abs(),
          q -> q.toString(),
          SpireAlgebraics::compareTo,
          SpireAlgebraics::compareTo);
      },
      "spire.math.Algebraic doesn't round to double correctly");

    Assertions.assertThrows(
      AssertionFailedError.class,
      () -> {
        Common.floatRoundingTests(
          SpireAlgebraics::toAlgebraic,
          SpireAlgebraics::toAlgebraic,
          q -> ((Algebraic) q).floatValue(),
          (q0,q1) -> ((Algebraic) q0).$minus((Algebraic) q1).abs(),
          q -> q.toString(),
          SpireAlgebraics::compareTo,
          SpireAlgebraics::compareTo);
      },
      "spire.math.Algebraic doesn't round to float correctly");

  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
