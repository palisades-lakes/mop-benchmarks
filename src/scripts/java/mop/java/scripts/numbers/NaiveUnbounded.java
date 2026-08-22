package mop.java.scripts.numbers;

import mop.java.numbers.UnboundedNatural;
import mop.java.numbers.NaturalInts;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;

//----------------------------------------------------------------
/** Profiling {@link UnboundedNatural}.
 * <p>
 * <pre>
 * j src/scripts/java/mop/java/scripts/numbers/NaiveUnbounded.java
 * jy src/scripts/java/mop/java/scripts/numbers/NaiveUnbounded.java
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */

@SuppressWarnings("unused")
public final class NaiveUnbounded {

  /** passes in a 12g JVM:
   * <table>
   *  <tr> <th>nwords</th>     <th>sec</th> </tr>
   *  <tr> <td>1L+     MAX_WORDS</td>  <td>  5</td> </tr>
   *  <tr> <td>1L+( 2L*MAX_WORDS)</td> <td> 30</td> </tr>
   *  <tr> <td>1L+( 4L*MAX_WORDS)</td> <td>OOM</td> </tr>
   * </table>
   */

  private static final void noOverflow () {
    final long t0 = System.nanoTime();
    try {
      final long n = 1L+(NaturalInts.MAX_WORDS<<2);
      System.out.println("n=" + n);
      System.out.flush();
      final Generator g =
        UnboundedNatural.randomBitsGenerator(
          n,PRNG.well44497b("seeds/Well44497b-2019-01-05.txt"));

      final UnboundedNatural u0 =
        (UnboundedNatural) g.next();
      //    final NaiveUnboundedNatural u1 =
      //      (NaiveUnboundedNatural) g.next();
      // no overflow from add
      final UnboundedNatural v = u0.add(u0);
      final int c0 = u0.compareTo(v);
      assert (c0 < 0) :
        "\nadd u0 doesn't increase value\n"
        + "compareTo -> " + c0;
      //    final int c1 = u0.compareTo(v);
      //    assert (c1 < 0) :
      //      "\nadd u1 doesn't increase value\n"
      //          + "compareTo -> " + c1;
    }
    finally {
      System.out.printf("Total seconds: %4.3f\n",
                        (System.nanoTime() - t0) * 1.0e-9); } }

//  /** passes in a 56g JVM:
//   * <table>
//   *  <tr> <th>nwords</th>     <th>sec</th> </tr>
//   *  <tr> <td>1L+     MAX_WORDS</td>  <td>  9</td> </tr>
//   *  <tr> <td>1L+( 2L*MAX_WORDS)</td> <td> 28</td> </tr>
//   *  <tr> <td>1L+( 4L*MAX_WORDS)</td> <td> 70</td> </tr>
//   *  <tr> <td>1L+( 8L*MAX_WORDS)</td> <td>150</td> </tr>
//   *  <tr> <td>1L+(16L*MAX_WORDS)</td> <td>1207 (OOM)</td> </tr>
//   * </table>
//   */
//
//  private static final void noOverflow () {
//    final long t0 = System.nanoTime();
//    try {
//      final long n = 1L+(BoundedNatural.MAX_WORDS<<4);
//      System.out.println("n=" + n);
//      System.out.flush();
//      final Generator g =
//        NaiveUnboundedNatural.randomBitsGenerator(
//          n,PRNG.well44497b("seeds/Well44497b-2019-01-05.txt"));
//
//      final NaiveUnboundedNatural u0 =
//        (NaiveUnboundedNatural) g.next();
//      //    final NaiveUnboundedNatural u1 =
//      //      (NaiveUnboundedNatural) g.next();
//      // no overflow from add
//      final NaiveUnboundedNatural v = u0.add(u0);
//      final int c0 = u0.compareTo(v);
//      assert (c0 < 0) :
//        "\nadd u0 doesn't increase value\n"
//        + "compareTo -> " + c0;
//      //    final int c1 = u0.compareTo(v);
//      //    assert (c1 < 0) :
//      //      "\nadd u1 doesn't increase value\n"
//      //          + "compareTo -> " + c1;
//    }
//    finally {
//      System.out.printf("Total seconds: %4.3f\n",
//        Double.valueOf((System.nanoTime()-t0)*1.0e-9)); } }

  /** passes in a 56g JVM:
   * <table>
   *  <tr> <th>nwords</th>     <th>sec</th> </tr>
   *  <tr> <td>1L+     MAX_WORDS</td>  <td> 176</td> </tr>
   *  <tr> <td>1L+( 2L*MAX_WORDS)</td> <td> 590</td> </tr>
   *  <tr> <td>1L+( 4L*MAX_WORDS)</td> <td>1439</td> </tr>
   *  <tr> <td>1L+( 8L*MAX_WORDS)</td> <td>1752 OOM</td> </tr>
   * </table>
   */

//  private static final void monoid () {
//
//    final Structure monoid = NaiveUnboundedNatural.MONOID;
//    final Supplier g = new Supplier () {
//      final Generator rbg =
//        NaiveUnboundedNatural.randomBitsGenerator(
//          1L+(BoundedNatural.MAX_WORDS<<3),
//          PRNG.well44497b("seeds/Well44497b-2019-01-05.txt"));
//      @Override
//      public final Object get () { return rbg.next(); } };
//      final Map generators =
//        ImmutableMap.of(NaiveUnboundedNatural.SET,g);
//
//      final int trys = 2;
//      final long t0 = System.nanoTime();
//      try {
//        SetTests.tests(monoid,trys,g);
//        for(final Predicate law : monoid.laws()) {
//          for (int i=0; i<trys; i++) {
//            final boolean result = law.test(generators);
//            assert result:
//              monoid.toString() + " : " + law.toString(); } } }
//      finally {
//        System.out.printf("Total seconds: %4.3f\n",
//          Double.valueOf((System.nanoTime()-t0)*1.0e-9)); } }

  public static final void main (final String[] args) {
    noOverflow();
//    monoid();
  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
