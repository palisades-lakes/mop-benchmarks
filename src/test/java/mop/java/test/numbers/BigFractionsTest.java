package mop.java.test.numbers;

import org.junit.jupiter.api.Test;

import mop.java.numbers.BigFractions;
import mop.java.test.algebra.SetTests;
//----------------------------------------------------------------
/** Test <code>BigFractions</code> set.
 * <p>
 * <pre>
 * mvn -q -Dtest=nzqr/java/test/sets/BigFractionsTest test > BigFractionsTest.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2021-05-11
 */

public final class BigFractionsTest {

  @SuppressWarnings({ "static-method" })
  @Test
  public final void setTests () {
    SetTests.tests(BigFractions.get()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
