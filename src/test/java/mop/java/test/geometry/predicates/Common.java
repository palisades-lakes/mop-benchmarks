package mop.java.test.geometry.predicates;

import mop.java.geometry.predicates.*;
import mop.java.geometry.predicates.jts.*;

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

  public static final List<Predicate> orient2dPredicates () {
    final Predicate ddFast = new DDFast();
    final Predicate ddSlow = new DDSlow();
    final Predicate doubleNonRobust = new DoubleNonRobust();
    final Predicate bigFloat = new BigFloatPredicate();
    final Predicate rationalFloat = new RationalFloatPredicate();
    final Predicate adapt = new Adapt();
    final Predicate exact = new Exact();
    final Predicate fast = new Fast();
    final Predicate slow = new Slow();
    return List.of(
      // JTS
      ddFast,ddSlow,doubleNonRobust,
      // mine
      rationalFloat,bigFloat,
      // Shewchuk predicates.c
      exact,adapt,fast,slow); }

  public static final List<Predicate> inCirclePredicates () {
    final Predicate ddFast = new DDFast();
    final Predicate ddNormalized = new DDNormalized();
    final Predicate ddSlow = new DDSlow();
    //final Predicate inCircleCC = new InCircleCC();
    final Predicate doubleNonRobust = new DoubleNonRobust();
    final Predicate inCircleNormalized = new InCircleNormalized();
    final Predicate bigFloat = new BigFloatPredicate();
    final Predicate rationalFloat = new RationalFloatPredicate();
    final Predicate adapt = new Adapt();
    final Predicate exact = new Exact();
    final Predicate fast = new Fast();
    final Predicate slow = new Slow();
    return List.of(
      // JTS
      ddFast,ddNormalized,ddSlow,doubleNonRobust,inCircleNormalized,
      // mine
      rationalFloat,bigFloat,
      // Shewchuk predicates.c
      exact,
      adapt,fast
      ,slow
                  ); }


  public static final List<Predicate> makePredicates () {
    final Predicate bigFloat = new BigFloatPredicate();
    final Predicate rationalFloat = new RationalFloatPredicate();
    final Predicate adapt = new Adapt();
    final Predicate exact = new Exact();
    final Predicate fast = new Fast();
    final Predicate slow = new Slow();
    return List.of(
      // mine
      rationalFloat,bigFloat,
      // Shewchuk predicates.c
      exact,adapt,fast,slow); }

  // ground truth predicate.
  // TODO: may be different for different problems
  public static final Predicate truth () {
    return new BigFloatPredicate(); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
