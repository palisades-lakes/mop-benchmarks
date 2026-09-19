package mop.java.test.algebra;

import com.google.common.collect.ImmutableMap;
import mop.java.Classes;
import mop.java.algebra.Set;
import mop.java.algebra.Sets;
import mop.java.numbers.BigFloats;
import mop.java.numbers.Hilos;
import mop.java.numbers.Q;
import mop.java.numbers.RationalFloats;
import mop.java.prng.PRNG;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//----------------------------------------------------------------
/** Common code for testing sets.
 * <p>
 * <pre>
 * mvn -q -Dtest=xfp/java/test/algebra/SetTests test > Sets.txt
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-21
 */

public final class SetTests {

  private static final int TRYS = 1023;

  private static final void testMembership (final Set set,
                                            final int trys,
                                            final Supplier g) {
    assertNotNull(set);
    assertTrue(0 < trys);
    for (int i=0; i<trys; i++) {
      //System.out.println("set=" + set);
      final Object x = g.get();
      //System.out.println("element=" + x);
      assertTrue(
        set.contains(x),
        () -> set + "\n does not contain \n" +
          Classes.className(x) + ": " +
          x); } }

  private static final void testEquivalence (final Set set,
                                             final int trys,
                                             final Supplier g) {
    assertNotNull(set);
    assertTrue(0 < trys);
    for (int i=0; i<trys; i++) {
      assertTrue(Sets.isReflexive(set,g));
      assertTrue(Sets.isSymmetric(set,g)); } }

  public static final void tests (final Set set,
                                  final int trys,
                                  final Supplier g) {
    testMembership(set,trys,g);
    testEquivalence(set,trys,g); }

  public static final void tests (final Set set,
                                  final int trys) {
    assertNotNull(set);
    assertTrue(0 < trys);
    final Supplier g =
      set.generator(
        ImmutableMap.of(
          Set.URP,
          PRNG.well44497b("seeds/Well44497b-2019-01-07.txt")));
    tests(set,trys,g); }

  public static final void tests (final Set set) {
    assertNotNull(set);
    tests(set,TRYS); }

  //--------------------------------------------------------------

  @SuppressWarnings({ "static-method" })
  @Test
  public final void Q () { SetTests.tests(Q.get()); }

  @SuppressWarnings({ "static-method" })
  @Test
  public final void BigFloats () { SetTests.tests(BigFloats.get()); }

  @SuppressWarnings({ "static-method" })
  @Test
  public final void Hilos () { SetTests.tests(Hilos.get()); }

  @SuppressWarnings({ "static-method" })
  @Test
  public final void RationalFloats () {
    SetTests.tests(RationalFloats.get()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
