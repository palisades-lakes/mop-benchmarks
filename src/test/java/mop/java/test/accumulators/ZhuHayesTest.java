package mop.java.test.accumulators;

import mop.java.accumulators.BigFloatAccumulator;
import mop.java.benchmarks.accumulate.Common;
import org.junit.jupiter.api.Test;

import java.util.List;

//----------------------------------------------------------------
/** Test summation algorithms.
 * <p>
 * <pre>
 * mvn -q -Dtest=xfp/java/test/accumulators/ZhuHayesTest test > ZhuHayesTest.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-19
 */

public final class ZhuHayesTest {

  private static final List<String> accumulators =
    List.of(
      "mop.java.accumulators.ZhuHayesAccumulator");

  private static final int TEST_DIM = 3*1024;//(1 * 8 * 1024) - 1;

  @SuppressWarnings("static-method")
  @Test
  public final void zeroSum () {
    //Debug.DEBUG=false;
    //Debug.println();
    //Debug.println("zeroSum");
    Common.zeroSumTests(
      Common.zeroSumGenerators(TEST_DIM),
      Common.makeAccumulators(accumulators)); }

  @SuppressWarnings("static-method")
  @Test
  public final void sum () {
    //Debug.DEBUG=false;
    //Debug.println();
    //Debug.println("sum");
    Common.sumTests(
      Common.generators(TEST_DIM),
      Common.makeAccumulators(accumulators),
      BigFloatAccumulator.make()); }

  @SuppressWarnings("static-method")
  @Test
  public final void l2 () {
    //Debug.DEBUG=false;
    //Debug.println();
    //Debug.println("l2");
    Common.l2Tests(
      Common.generators(TEST_DIM),
      Common.makeAccumulators(accumulators),
      BigFloatAccumulator.make()); }

  @SuppressWarnings("static-method")
  @Test
  public final void dot () {
    //Debug.DEBUG=false;
    //Debug.println();
    //Debug.println("dot");
    Common.dotTests(
      Common.generators(TEST_DIM),
      Common.makeAccumulators(accumulators),
      BigFloatAccumulator.make()); }

  // TODO: decide on expected behavior with non-finite input
  //  @SuppressWarnings("static-method")
  //  @Test
  //  public final void nanSum () {
  //    //Debug.DEBUG=false;
  //    //Debug.println();
  //    //Debug.println("infinite");
  //    Common.nonFiniteTests(
  //      Common.makeAccumulators(accumulators)); }

  @SuppressWarnings("static-method")
  @Test
  public final void infiniteSum () {
    //Debug.DEBUG=false;
    //Debug.println();
    //Debug.println("infinite");
    Common.infinityTests(
      Common.makeAccumulators(accumulators)); }

  @SuppressWarnings("static-method")
  @Test
  public final void overflowSum () {
    //Debug.DEBUG=false;
    //Debug.println();
    //Debug.println("overflow");
    Common.overflowTests(
      Common.makeAccumulators(accumulators)); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
