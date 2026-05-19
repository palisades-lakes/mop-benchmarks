package mop.java.test.geometry.predicates;

import mop.java.geometry.predicates.Adapt;
import mop.java.geometry.predicates.Exact;
import mop.java.geometry.predicates.Fast;
import mop.java.geometry.predicates.Predicate;
import mop.java.geometry.predicates.Slow;

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
    final Predicate adapt = new Adapt();
    final Predicate exact = new Exact();
    final Predicate fast = new Fast();
    final Predicate slow = new Slow();
    return List.of(exact,adapt,fast,slow); }

  // ground truth predicate.
  // TODO: may be different for different problems
  public static final Predicate truth () { return new Exact(); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
