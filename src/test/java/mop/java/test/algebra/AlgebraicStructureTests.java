package mop.java.test.algebra;

import com.google.common.collect.ImmutableMap;
import mop.java.algebra.Set;
import mop.java.algebra.Structure;
import mop.java.numbers.*;
import mop.java.prng.PRNG;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertTrue;

//----------------------------------------------------------------
/** <pre>
 * mvn -Dtest=mop.java.test.algebra.AlgebraicStructureTests test
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-24
 */


public final class AlgebraicStructureTests {
  private static final int TRYS = 31;
  //static final int SPACE_TRYS = 5;

  //--------------------------------------------------------------

  @SuppressWarnings("unchecked")
  private static final void
  structureTests (final Structure s,
                  final int n) {
    SetTests.tests(s,n);
    final Map<Set,Supplier> generators =
      s.generators(
        ImmutableMap.of(
          Set.URP,
          PRNG.well44497b("seeds/Well44497b-2019-01-09.txt")));
    for(final Predicate law : s.laws()) {
      for (int i=0; i<n; i++) {
        final boolean result = law.test(generators);
        assertTrue(result,
          s.getClass().getName() + " : " + law); } } }

  //--------------------------------------------------------------

  @SuppressWarnings({ "static-method" })
  @Test
  public final void tests () {

    //Debug.DEBUG=false;

    structureTests(Naturals.ADDITION_MONOID,2*TRYS);
    structureTests(Naturals.MULTIPLICATIVE_MAGMA,TRYS);
    structureTests(Naturals.RING,TRYS);

    structureTests(BigFloats.ADDITIVE_MAGMA,TRYS);
    structureTests(BigFloats.MULTIPLICATIVE_MAGMA,TRYS);
    structureTests(BigFloats.RING,TRYS);

    structureTests(RationalFloats.ADDITIVE_MAGMA,TRYS);
    structureTests(RationalFloats.MULTIPLICATIVE_MAGMA,TRYS);
    structureTests(RationalFloats.FIELD,TRYS);

    structureTests(Rationals.FIELD, TRYS);

    structureTests(Floats.ADDITIVE_MAGMA,TRYS);
    structureTests(Floats.MULTIPLICATIVE_MAGMA,TRYS);
    structureTests(Floats.FLOATING_POINT,TRYS);

    structureTests(Doubles.ADDITIVE_MAGMA,TRYS);
    structureTests(Doubles.MULTIPLICATIVE_MAGMA,TRYS);
    structureTests(Doubles.FLOATING_POINT,TRYS);

    //Debug.DEBUG=false;
  }


  //--------------------------------------------------------------
}
//--------------------------------------------------------------
