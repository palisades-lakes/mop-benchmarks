package mop.java.test.geometry.predicates;

import mop.java.geometry.predicates.*;

import java.util.List;

//----------------------------------------------------------------
/** Common code for geometry predicate tests.
  *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-19
 */

public final class Common {

  //--------------------------------------------------------------
  // TODO: setup and tear down

  public static final List<Predicate> makePredicates () {
    final Predicate bigFloat = new BigFloatPredicate();
    final Predicate rationalFloat = new RationalFloatPredicate();
    final Predicate adapt = new Adapt();
    final Predicate exact = new Exact();
    final Predicate fast = new Fast();
    final Predicate slow = new Slow();
    return List.of(rationalFloat,bigFloat,exact,adapt,fast,slow); }

  // ground truth predicate.
  // TODO: may be different for different problems
  public static final Predicate truth () { return new Exact(); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
