package mop.java.numbers;

import mop.java.algebra.OneSetOneOperation;
import mop.java.algebra.OneSetTwoOperations;
import mop.java.algebra.Set;
import mop.java.prng.Generator;
import mop.java.prng.GeneratorBase;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.CollectionSampler;
import org.apache.commons.rng.sampling.distribution.ContinuousSampler;
import org.apache.commons.rng.sampling.distribution.ContinuousUniformSampler;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

//import mop.java.numbers.BigInteger;

/** The set of quad precision floating point numbers
 * represented by <code>Hilo</code>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-21
 */
@SuppressWarnings({"unchecked","static-method"})
public final class Hilos implements Set {

  //--------------------------------------------------------------
  // operations for algebraic structures over Hilo.
  //--------------------------------------------------------------

  public final BinaryOperator<Hilo> adder () {
    return new BinaryOperator<> () {
      @Override
      public final String toString () { return "Hilo.add()"; }
      @Override
      public final Hilo apply (final Hilo q0,
                               final Hilo q1) {
        return q0.add(q1); } }; }

  public final Hilo additiveIdentity () { return Hilo.ZERO; }

  public final UnaryOperator<Hilo> additiveInverse () {
    return new UnaryOperator<> () {
      @Override
      public final String toString () { return "Hilo.negate()"; }
      @Override
      public final Hilo apply (final Hilo q) {
        return q.negate(); } }; }

  public final BinaryOperator<Hilo> multiplier () {
    return new BinaryOperator<>() {
      @Override
      public final String toString () { return "Hilo.multiply"; }
      @Override
      public final Hilo apply (final Hilo q0,
                               final Hilo q1) {
        return q0.multiply(q1); } }; }

  public final Hilo multiplicativeIdentity () { return Hilo.ONE; }

  public final UnaryOperator<Hilo> multiplicativeInverse () {
    return new UnaryOperator<> () {
      @Override
      public final String toString () { return "Hilo.invert()"; }
      @Override
      public final Hilo apply (final Hilo q) {
        return q.invert(); } }; }

  //--------------------------------------------------------------
  // Set methods
  //--------------------------------------------------------------

  @Override
  public final boolean contains (final Object element) {
    return element instanceof Hilo; }

  //--------------------------------------------------------------

  @Override
  public final BiPredicate equivalence () {
    return (BiPredicate<Hilo, Hilo>) Hilo::equals; }

  // Is this characteristic of most inputs?
  public static final Generator
  fromDoubleGenerator (final UniformRandomProvider urp) {
    final double dp = 0.9;
    return new GeneratorBase ("fromDoubleGenerator") {
      private final ContinuousSampler choose =
        new ContinuousUniformSampler(urp,0.0,1.0);
      // TODO: other double generators?
      private final Generator g = Doubles.finiteGenerator(urp);
      private final CollectionSampler edgeCases =
        new CollectionSampler(
          urp,
          List.of(
            Hilo.valueOf(0.0),
            Hilo.valueOf(1.0),
            Hilo.valueOf(2.0),
            Hilo.valueOf(10.0),
            Hilo.valueOf(-1.0)));
      @Override
      public Object next () {
        final boolean edge = choose.sample() > dp;
        if (edge) { return edgeCases.sample(); }
        return Hilo.sum(g.nextDouble(), g.nextDouble()); } }; }

//  public static final Generator
//  fromDoubleGenerator (final int n,
//                       final UniformRandomProvider urp) {
//    return new GeneratorBase ("fromDoubleGenerator:" + n) {
//      final Generator g = fromDoubleGenerator(urp);
//      @Override
//      public final Object next () {
//        final Hilo[] z = new Hilo[n];
//        for (int i=0;i<n;i++) { z[i] = (Hilo) g.next(); }
//        return z; } }; }

  // Is this characteristic of most inputs?
  public static final Generator
  generator (final UniformRandomProvider urp) {
    return fromDoubleGenerator(urp); }

  public static final Generator
  generator (final int n,
             final UniformRandomProvider urp) {
    return new GeneratorBase ("rationalGenerator:" + n) {
      final Generator g = generator(urp);
      @Override
      public final Object next () {
        final Hilo[] z = new Hilo[n];
        for (int i=0;i<n;i++) { z[i] = (Hilo) g.next(); }
        return z; } }; }

  // TODO: determine which generator from options.
  @Override
  public final Supplier generator (final Map options) {
    final UniformRandomProvider urp = Set.urp(options);
    final Generator g = generator(urp);
    return g::next; }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------

  @Override
  public final int hashCode () { return 0; }

  // singleton
  @Override
  public final boolean equals (final Object that) {
    return that instanceof Hilos; }

  @Override
  public final String toString () { return "Hilos"; }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private Hilos () { }

  private static final Hilos SINGLETON = new Hilos();

  public static final Hilos get () { return SINGLETON; }

  //--------------------------------------------------------------
  // pre-defined structures
  //--------------------------------------------------------------

  public static final OneSetOneOperation ADDITIVE_MAGMA =
    OneSetOneOperation.magma(get().adder(),get());

  public static final OneSetOneOperation MULTIPLICATIVE_MAGMA =
    OneSetOneOperation.magma(get().multiplier(),get());

  public static final OneSetTwoOperations FLOATING_POINT =
    OneSetTwoOperations.floatingPoint(
      get().adder(),
      get().additiveIdentity(),
      get().additiveInverse(),
      get().multiplier(),
      get().multiplicativeIdentity(),
      get().multiplicativeInverse(),
      get());

  //--------------------------------------------------------------
}
//--------------------------------------------------------------

