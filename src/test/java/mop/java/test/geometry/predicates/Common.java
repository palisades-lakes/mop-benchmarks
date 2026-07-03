package mop.java.test.geometry.predicates;

import mop.java.geometry.predicates.Adapt;
import mop.java.geometry.predicates.BigFloatPredicate;
import mop.java.geometry.predicates.Exact;
import mop.java.geometry.predicates.Fast;
import mop.java.geometry.predicates.Predicate;
import mop.java.geometry.predicates.RationalFloatPredicate;
import mop.java.geometry.predicates.Slow;
import mop.java.geometry.predicates.jts.DDFast;
import mop.java.geometry.predicates.jts.DDNormalized;
import mop.java.geometry.predicates.jts.DDSlow;
import mop.java.geometry.predicates.jts.DoubleNonRobust;
import mop.java.geometry.predicates.jts.InCircleNormalized;
import mop.java.geometry.predicates.macro.AdaptMacro;
import mop.java.geometry.predicates.macro.DefaultMacro;
import mop.java.geometry.predicates.macro.ExactMacro;
import mop.java.geometry.predicates.macro.FastMacro;
import mop.java.geometry.predicates.macro.SlowMacro;

import java.util.List;

//----------------------------------------------------------------
/** Common code for geometry predicate tests.
  *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-03
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
    //final Predicate exact = new Exact();
    final Predicate fast = new Fast();
    final Predicate slow = new Slow();
    final Predicate adaptMacro = new AdaptMacro();
    final Predicate defaultMacro = new DefaultMacro();
    final Predicate exactMacro = new ExactMacro();
    final Predicate fastMacro = new FastMacro();
    final Predicate slowMacro = new SlowMacro();
    return List.of(
      // JTS
      ddFast,ddSlow,doubleNonRobust,
      // mine
      rationalFloat,bigFloat,
      // Shewchuk predicates.c
      //exact,
      adapt,fast,slow,
      exactMacro, adaptMacro, defaultMacro, fastMacro, slowMacro); }

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
    final Predicate adaptMacro = new AdaptMacro();
    final Predicate defaultMacro = new DefaultMacro();
    final Predicate exactMacro = new ExactMacro();
    final Predicate fastMacro = new FastMacro();
    final Predicate slowMacro = new SlowMacro();
    return List.of(
      // JTS
      ddFast,ddNormalized,ddSlow,doubleNonRobust,inCircleNormalized,
      // mine
      rationalFloat,bigFloat,
      // Shewchuk predicates.c
      exact, adapt,fast ,slow,
      exactMacro, adaptMacro, defaultMacro, fastMacro, slowMacro
      ); }

  public static final List<Predicate> orient3dPredicates () {
    final Predicate bigFloat = new BigFloatPredicate();
    final Predicate rationalFloat = new RationalFloatPredicate();
    final Predicate adapt = new Adapt();
    //final Predicate exact = new Exact();
    final Predicate fast = new Fast();
    final Predicate slow = new Slow();
    final Predicate adaptMacro = new AdaptMacro();
    final Predicate defaultMacro = new DefaultMacro();
    final Predicate exactMacro = new ExactMacro();
    final Predicate fastMacro = new FastMacro();
    final Predicate slowMacro = new SlowMacro();
    return List.of(
      // mine
      rationalFloat,bigFloat,
      // Shewchuk predicates.c
      //exact,
      adapt,fast,slow,
      exactMacro, adaptMacro, defaultMacro, fastMacro, slowMacro); }

  public static final List<Predicate> inSpherePredicates () {
    final Predicate bigFloat = new BigFloatPredicate();
    final Predicate rationalFloat = new RationalFloatPredicate();
    final Predicate adapt = new Adapt();
    //final Predicate exact = new Exact();
    final Predicate fast = new Fast();
    final Predicate slow = new Slow();
    final Predicate adaptMacro = new AdaptMacro();
    final Predicate defaultMacro = new DefaultMacro();
    final Predicate exactMacro = new ExactMacro();
    final Predicate fastMacro = new FastMacro();
    final Predicate slowMacro = new SlowMacro();
    return List.of(
      // mine
      rationalFloat,bigFloat,
      // Shewchuk predicates.c
      //exact,
      adapt,fast,slow,
      exactMacro, adaptMacro, defaultMacro, fastMacro, slowMacro); }

  // ground truth predicate.
  // TODO: may be different for different problems
  public static final Predicate truth () {
    return new BigFloatPredicate(); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
