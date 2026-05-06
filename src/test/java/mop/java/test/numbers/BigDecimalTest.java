package mop.java.test.numbers;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import mop.java.numbers.Numbers;
import mop.java.benchmarks.accumulate.Common;

//----------------------------------------------------------------
/** Test desired properties of BigDecimal.
 * <p>
 * <pre>
 * mvn -Dtest=nzqr/jmh/test/numbers/BigDecimalTest test > BigDecimalTest.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-12-01
 */

public final class BigDecimalTest {

  @SuppressWarnings({ "static-method" })
  @Test
  public final void testRounding () {

    Common.doubleRoundingTests(
      null,
      BigDecimal::valueOf,
      Numbers::doubleValue,
      (q0,q1) -> ((BigDecimal) q0).subtract((BigDecimal) q1).abs(),
      Object::toString,
      Common::compareTo, Common::compareTo);

    Common.floatRoundingTests(
      null,
      BigDecimal::valueOf,
      Numbers::floatValue,
      (q0,q1) -> ((BigDecimal) q0).subtract((BigDecimal) q1).abs(),
      Object::toString,
      Common::compareTo, Common::compareTo);

  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
