package mop.java.test.numbers;

import com.google.common.collect.ImmutableMap;
import mop.java.algebra.Set;
import mop.java.algebra.Structure;
import mop.java.numbers.BoundedNatural;
import mop.java.numbers.UnboundedNatural;
import mop.java.prng.PRNG;
import mop.java.test.algebra.SetTests;
import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import java.math.BigInteger;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertTrue;

//----------------------------------------------------------------
/** <pre>
 * mvn -Dtest=mop/java/test/numbers/NaiveUnboundedNaturalTests test > UNT.txt
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */


public final class UnboundedNaturalTests {

  @SuppressWarnings({ "static-method" })
  @Test
  public final void testValueOf () {
    final BigInteger bi = new BigInteger(
      "744fe560022d5b34420df8e6bc5a25cbb19378b537a89241cf1d8b54b29bf2887af660876d52b97b7c37e552e259f01ba525b05b829f4be794bdf365a7941c698109f501e0a72b288c343436704cb00fbdbf690fa2296621820af9bd22d1e73446acdcce698f34a83978d538c4d4e45ad026034ae52cc34150511f3115b66942e21bf5142dfa6204c10661456230097d4a8fce0bcb64c9bc54861397d27d1a0aac41fa486f7f3e0f9f982764ddfd1841e8e694c814785997073d9e4cd140a6707ae0a73bf7bea5a489825564295462f2afb258c5d69b5dfa2dcb0a57ff270e1cc306f952156d36f64b3033f1b1e33712133d9fcb282cb07466f81bf4f4a6a54cec132e27e451b1655614d8d5a392de4fa9c892e0f5b611276c075265d64fa77f2293dd29b77900b1853da16136b8e196cd9bc8adac938593cc9e18850b8b0f64f11f3c2d4706a8fe00b38eccc971842e3ab118ff8a45dc49f8baebeef705745f5f43efa42026511ab9a43b0e4d1509fd0f7136c2b7a050cdfcf10b191d32c0ccbf6bc34a47b4980a6872200fea60d6d71597c40227539601a266c778048e2c1c0e7f85e0b7b03a4cec02abf2aba4a43409c7bb135cb88a26a2e8a1efefdeca956f18976474b6360b9abfc13982d8fc151a90b4a7cddd085af8cd68c18fd81d688c50e1fb4a7ae258af2253b655192ece12b523ea15633e1105c01bb5b5b6dcb9fd9bdc9f9011b6ffebc2db1b9f54d0c1a214fd1ecbfe74ee5520bf69444b74f2c0ae7b880f3c349f085a2cd9d19f0af8b33f5217c57b2aab46136c8942280cde57be7ba36d21cfd333a6d3ccc9b58e34eef1b8d18bd4d5f5d6dc4c9e5e548ee50dbfbbc71ce9fc9b455d81cbfd8f6327092e0ef2ce511d0b633157cb12cadfeb4f72cfea4c566d8ab98287119fa9d930b784a518b09d00f2e5bac6220988d5149e634a185ce28984a032dcc06c42b329c26aa4667b3db72a1c32a373a74aaacb52ebc202f2c5ebb0b0c37a9e22a96bc992da322f47470cdcb22a0b354395edbdca05206c19f2d65b4cc2b1693f33e620d495d5821e6dddaa9bd9acb61535f689ba2ad5f5401ebab8a250550e6d191b22e374cd7897504ad631760594a7c3f13be7a25b3ad22278c2d8f4dfd62ef5997f6aa0c6628c5518e12d1af147b06fb084471bf6a495d649f404c54215f52b2427729b15c69129c5ce1cd47e37e99ccb47ff5de8be8916632c40061fe5d4a7fa8d0543d17712172b53bf38690a39c7da393e606b87567edd3634d0bddbc448925d79cee59ccdfb4f175839f5466052337882613c1135d76d2ec11e5e53450c5bb37ca2e5073d14178535f258c5c8337afa5293bf24a504e5f1fd54f09ef5cf3b466e59bca5381f90522e88335687e931cf0f4c95f0fe9dd7400187d69d617e8260a3533a0b93ae2648bb8b67b686b50215",
      0x10);
    final BoundedNatural bn = BoundedNatural.valueOf(bi);

    final UnboundedNatural ui = UnboundedNatural.valueOf(bi);
    final UnboundedNatural un = UnboundedNatural.valueOf(bn);
    Assertions.assertEquals(ui, un, () ->
      "\nvalueOf BigInteger and BoundedNatural do not match.\n"
      + ui + "\n" + un + "\n"); }

  @SuppressWarnings({ "static-method" })
  @Test
  public final void noOverflow () {
    final UnboundedNatural u =
      UnboundedNatural.valueOf(BoundedNatural.maxValue());
    // no overflow from add
    final UnboundedNatural v = u.add(u);
    final int cmp = u.compareTo(v);
    Assertions.assertTrue(
      (cmp < 0),
      () -> "\nadd one doesn't increase value\ncompareTo -> " + cmp); }

  @SuppressWarnings({"static-method","unchecked"})
  @Test
  public final void monoid () {

    final Structure s = UnboundedNatural.MONOID;
    final int n = 2;
    SetTests.tests(s,n);
    final Map<Set,Supplier> generators =
      s.generators(
        ImmutableMap.of(
          Set.URP,
          PRNG.well44497b("seeds/Well44497b-2019-01-09.txt")));
    for(final Predicate law : s.laws()) {
      for (int i=0; i<n; i++) {
        final boolean result = law.test(generators);
        assertTrue(result,s + " : " + law); } } }


  //--------------------------------------------------------------
}
//--------------------------------------------------------------
