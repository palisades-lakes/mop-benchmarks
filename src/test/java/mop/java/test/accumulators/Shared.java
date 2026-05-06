package mop.java.test.accumulators;

import java.util.List;

/** Shared data/code for accumulator tests.
 * Not instantiable. Class slots/methods only.
 *
 * @author mcdonald dot john dot alan at gmail dot com
 * @version 2024-01-16
 */
public final class Shared {

  public static final int TEST_DIM = 256; //(1 * 64 * 1024) - 1;
  //1 << 14;

  public static final List<String> accumulators () {
    return
      List.of(
        //,
        "mop.java.accumulators.IFastAccumulator",
        "mop.java.accumulators.ZhuHayesGCAccumulator",
        "mop.java.accumulators.ZhuHayesGCBranch",
        "mop.java.accumulators.ZhuHayesBranch"
        // "mop.java.accumulators.RationalFloatAccumulator"
        // ,
        // // Same as non-strict, just slower
        // "mop.java.accumulators.DoubleAccumulator",
        // "mop.java.accumulators.StrictDoubleFmaAccumulator",
        // // overflow unless values more limited
        // "mop.java.accumulators.FloatAccumulator",
        // "mop.java.accumulators.FloatFmaAccumulator",
        // // Too slow to keep testing
        // "mop.java.accumulators.BigDecimalAccumulator",
        // "mop.java.accumulators.BigFractionAccumulator",
        // "mop.java.accumulators.DoubleFmaAccumulator",
        // "mop.java.accumulators.KahanFmaAccumulator",
        // //,
        // // Broken in many ways.
        // // Doesn't overflow to infinity, or accumulate extreme
        // // values correctly.
        // // Slow as well.
        // //"mop.java.accumulators.RatioAccumulator"
        ); }

  //--------------------------------------------------------------
  // disable constructor
  //--------------------------------------------------------------

  private Shared () {
    throw new UnsupportedOperationException(
      "can't instantiate " + getClass()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
