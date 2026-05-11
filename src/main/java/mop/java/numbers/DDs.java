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

/** The set of quad precision {@link DD} floating point numbers
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-09
 */
@SuppressWarnings({ "unchecked", "static-method" })
public final class DDs implements Set {

  //--------------------------------------------------------------
  // operations for algebraic structures over BigFloats.
  //--------------------------------------------------------------
  // TODO: is consistency with other algebraic structure classes
  // worth the indirection?

  private final DD add (final DD q0,
                        final DD q1) {
    assert contains(q0);
    assert contains(q1);
    return q0.add(q1); }

  public final BinaryOperator<DD> adder () {
    return new BinaryOperator<>() {
      @Override
      public final String toString () { return "DD.add()"; }

      @Override
      public final DD apply (final DD q0, final DD q1) {
        return DDs.this.add(q0, q1); }  }; }

  //--------------------------------------------------------------

  public final DD additiveIdentity () { return DD.ZERO; }

  //--------------------------------------------------------------
  // TODO: is consistency with other algebraic structure classes
  // worth the indirection?

  private final DD negate (final DD q) {
    //assert contains(q);
    return q.negate(); }

  public final UnaryOperator<DD> additiveInverse () {
    return new UnaryOperator<>() {
      @Override
      public final String toString () { return "DD.negate()"; }

      @Override
      public final DD apply (final DD q) {
        return DDs.this.negate(q); } }; }

  //--------------------------------------------------------------

  private final DD multiply (final DD q0,
                             final DD q1) {
    assert contains(q0);
    assert contains(q1);
    return q0.multiply(q1); }

  public final BinaryOperator<DD> multiplier () {
    return new BinaryOperator<>() {
      @Override
      public final String toString () { return "DD.multiply"; }

      @Override
      public final DD apply (final DD q0,
                             final DD q1) {
        return DDs.this.multiply(q0, q1); } }; }

  //--------------------------------------------------------------

  @SuppressWarnings("static-method")
  public final DD multiplicativeIdentity () { return DD.ONE; }

  //--------------------------------------------------------------

  @SuppressWarnings("static-method")
  private final DD reciprocal (final DD q) { return q.reciprocal();  }

  public final UnaryOperator<DD> multiplicativeInverse () {
    return new UnaryOperator<> () {
      @Override
      public final String toString () { return "DD.inverse()"; }
      @Override
      public final DD apply (final DD q) { return reciprocal(q); } }; }

  //--------------------------------------------------------------
  // Set methods
  //--------------------------------------------------------------

  @Override
  public final boolean contains (final Object element) {
    return element instanceof DD; }

  //--------------------------------------------------------------

  @Override
  public final BiPredicate equivalence () {
    return (BiPredicate<DD, DD>) DD::equals; }

  //--------------------------------------------------------------
  // Is this characteristic of most inputs?

  public static final Generator
  fromDoubleGenerator (final UniformRandomProvider urp) {
    final double dp = 0.9;
    return new GeneratorBase("fromDoubleGenerator") {
      private final ContinuousSampler choose =
        new ContinuousUniformSampler(urp, 0.0, 1.0);
      private final Generator g = Doubles.finiteGenerator(urp);
      private final CollectionSampler edgeCases =
        new CollectionSampler(
          urp,
          List.of(
            DD.sum(0.0, 0.0),
            DD.sum(1.0, 0.0),
            DD.sum(2.0, 0.0),
            DD.sum(10.0, 0.0),
            DD.sum(-1.0, 0.0)));

      @Override
      public Object next () {
        final boolean edge = choose.sample() > dp;
        if (edge) { return edgeCases.sample(); }
        // TODO: almost surely wrong!
        // maybe ok if uniform generator
        // otherwise same distribution for high and low terms
        return DD.sum(g.nextDouble(), g.nextDouble()); } }; }

  // Is this characteristic of most inputs?
  public static final Generator
  generator (final UniformRandomProvider urp) {
    return fromDoubleGenerator(urp); }

  @SuppressWarnings("unused")
  public static final Generator
  fromDoubleGenerator (final int n,
                       final UniformRandomProvider urp) {
    return new GeneratorBase("fromDoubleGenerator:" + n) {
      final Generator g = fromDoubleGenerator(urp);

      @Override
      public final Object next () {
        final DD[] z = new DD[n];
        for (int i = 0; i < n; i++) { z[i] = (DD) g.next(); }
        return z; } }; }

  public static final Generator
  generator (final int n,
             final UniformRandomProvider urp) {
    return new GeneratorBase("rationalGenerator:" + n) {
      final Generator g = generator(urp);

      @Override
      public final Object next () {
        final DD[] z = new DD[n];
        for (int i = 0; i < n; i++) { z[i] = (DD) g.next(); }
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
    return that instanceof DDs; }

  @Override
  public final String toString () { return "DDs"; }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private DDs () { }

  private static final DDs SINGLETON = new DDs();

  public static final DDs get () { return SINGLETON; }

  //--------------------------------------------------------------

  public static final OneSetOneOperation ADDITIVE_MAGMA =
    OneSetOneOperation.magma(get().adder(), get());

  public static final OneSetOneOperation MULTIPLICATIVE_MAGMA =
    OneSetOneOperation.magma(get().multiplier(), get());

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

