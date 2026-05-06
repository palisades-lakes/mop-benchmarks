package mop.java.test.numbers;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import clojure.lang.Numbers;
import clojure.lang.Ratio;
import mop.java.benchmarks.accumulate.Common;
import mop.java.numbers.Ratios;

//----------------------------------------------------------------
/** Tests showing why I use <code>BigFraction</code> for
 * rationals rather than <code>clojure.lang.Ratio</code>.
 * <p>
 * <pre>
 * mvn -c -Dtest=nzqr/java/test/sets/RatioTest test > RatioTest.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-12-01
 */

public final class RatioTest {

  // (1) equals is wrong, unless always in reduced form?
  // not true if you call the public constructor
  //
  // (2) need clojure tests to demonstrate surprising coercions

  @SuppressWarnings({ "static-method" })
  @Test
  public final void equivalenceFailure () {
    final Ratio q0 = new Ratio(
      java.math.BigInteger.ONE,java.math.BigInteger.ONE);
    final Ratio q1 = new Ratio(
      java.math.BigInteger.TWO,java.math.BigInteger.TWO);
    // WRONG: this should be true, but clojure Ratio is broken.
    assertFalse(q0.equals(q1)); }

  //--------------------------------------------------------------

  @SuppressWarnings({ "static-method" })
  @Test
  public final void testRounding () {

    Assertions.assertThrows(
      AssertionFailedError.class,
      () -> {
        Common.doubleRoundingTests(
          (i0,i1) -> new Ratio(i0,i1),
          x -> Numbers.toRatio(Double.valueOf(x)),
          q -> ((Ratio) q).doubleValue(),
          (q0,q1) -> Ratios.abs((Ratio) Numbers.minus(q0,q1)),
          Object::toString,
          Common::compareTo, Common::compareTo); },
      "Ratio doesn't round correctly");

    Assertions.assertThrows(
      AssertionFailedError.class,
      () -> {
        Common.floatRoundingTests(
          (i0,i1) -> new Ratio(i0,i1),
          x -> Numbers.toRatio(Float.valueOf(x)),
          q -> ((Ratio) q).floatValue(),
          (q0,q1) -> Ratios.abs((Ratio) Numbers.minus(q0,q1)),
          Object::toString,
          Common::compareTo, Common::compareTo); },
      "Ratio doesn't round correctly");

  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
