package mop.java.test.numbers;

import mop.java.benchmarks.accumulate.Common;
import mop.java.numbers.DD;
import org.junit.jupiter.api.Test;

//----------------------------------------------------------------
/** Test desired properties of JTS-derived DD.
 * <pre>
 * mvn -Dtest=mop/java/test/numbers/DDTest test > DDTest.txt
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-06
 */

public final class DDTest {

  @SuppressWarnings({ "static-method" })
  @Test
  public final void testRounding () {

    Common.doubleRoundingTests(
      null,
      DD::valueOf,
      q -> ((DD) q).doubleValue(),
      (q0,q1) -> ((DD) q0).subtract((DD) q1).abs(),
      dd -> ((DD) dd).dump(),
      Common::compareTo,
      Common::compareTo);

    Common.floatRoundingTests(
      null,
      DD::valueOf,
      q -> ((DD) q).floatValue(),
      (q0,q1) -> ((DD) q0).subtract((DD) q1).abs(),
      dd -> ((DD) dd).dump(),
      Common::compareTo,
      Common::compareTo);

  }
  //--------------------------------------------------------------
}
//--------------------------------------------------------------
