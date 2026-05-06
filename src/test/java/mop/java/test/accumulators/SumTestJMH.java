package mop.java.test.accumulators;

import static mop.java.test.accumulators.Shared.accumulators;

import mop.java.accumulators.EFloatAccumulator;
import org.junit.jupiter.api.Test;

import mop.java.benchmarks.accumulate.Common;

//----------------------------------------------------------------
/** Test summation algorithms.
 * <p>
 * <pre>
 * mvn -q test -Dtest=nzqr/jmh/test/accumulators/SumTest test > SumTest.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-10-15
 */

public final class SumTestJMH {

  @SuppressWarnings("static-method")
  @Test
  public final void infiniteSum () {
    //Debug.DEBUG=false;
    //Debug.println();
    //Debug.println("infinite");
    Common.infinityTests(
      Common.makeAccumulators(accumulators())); }

  @SuppressWarnings("static-method")
  @Test
  public final void overflowSum () {
    //Debug.DEBUG=false;
    //Debug.println();
    //Debug.println("overflow");
    Common.overflowTests(
      Common.makeAccumulators(accumulators())); }

  @SuppressWarnings("static-method")
  @Test
  public final void zeroSum () {
    //Debug.DEBUG=false;
    //Debug.println();
    //Debug.println("zeroSum");
    Common.zeroSumTests(
      Common.zeroSumGenerators(Shared.TEST_DIM),
      Common.makeAccumulators(accumulators())); }

  @SuppressWarnings("static-method")
  @Test
  public final void sum () {
    //Debug.DEBUG=false;
    //Debug.println();
    //Debug.println("sum");
    Common.sumTests(
      Common.generators(Shared.TEST_DIM),
      Common.makeAccumulators(accumulators()),
      EFloatAccumulator.make()); }

  @SuppressWarnings("static-method")
  @Test
  public final void l2 () {
    //Debug.DEBUG=false;
    //Debug.println();
    //Debug.println("l2");
    Common.l2Tests(
      Common.generators(Shared.TEST_DIM),
      Common.makeAccumulators(accumulators()),
      EFloatAccumulator.make()); }

  @SuppressWarnings("static-method")
  @Test
  public final void dot () {
    //Debug.DEBUG=false;
    //Debug.println();
    //Debug.println("dot");
    Common.dotTests(
      Common.generators(Shared.TEST_DIM),
      Common.makeAccumulators(accumulators()),
      EFloatAccumulator.make()); }

  // TODO: pick expected behavior for non-finite input
  //  @SuppressWarnings("static-method")
  //  @Test
  //  public final void nanSum () {
  //    //Debug.DEBUG=false;
  //    //Debug.println();
  //    //Debug.println("infinite");
  //    Common.nonFiniteTests(
  //      Common.makeAccumulators(accumulators())); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
