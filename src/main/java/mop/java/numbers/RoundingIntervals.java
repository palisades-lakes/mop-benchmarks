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

/** minimal ouble intervals
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-11
 */
@SuppressWarnings({"unchecked","static-method","unused"})
public final class RoundingIntervals implements Set {

  //--------------------------------------------------------------
  // operations for algebraic structures over RoundingIntervals.
  //--------------------------------------------------------------

  // TODO: is consistency with other algebraic structure classes
  // worth the indirection?

  private final RoundingInterval add (final RoundingInterval q0,
                                      final RoundingInterval q1) {
    //assert contains(q0);
    //assert contains(q1);
    return q0.add(q1); }

  public final BinaryOperator<RoundingInterval> adder () {
    return new BinaryOperator<> () {
      @Override
      public final String toString () { return "BF.add()"; }
      @Override
      public final RoundingInterval apply (final RoundingInterval q0,
                                           final RoundingInterval q1) {
        return RoundingIntervals.this.add(q0, q1); } }; }

  //--------------------------------------------------------------

  public final RoundingInterval additiveIdentity () {
    return RoundingInterval.ZERO; }

  //--------------------------------------------------------------
  // TODO: is consistency with other algebraic structure classes
  // worth the indirection?

  private final RoundingInterval negate (final RoundingInterval q) {
    //assert contains(q);
    return q.negate(); }

  public final UnaryOperator<RoundingInterval> additiveInverse () {
    return new UnaryOperator<> () {
      @Override
      public final String toString () { return "BF.negate()"; }
      @Override
      public final RoundingInterval apply (final RoundingInterval q) {
        return RoundingIntervals.this.negate(q); } }; }

  //--------------------------------------------------------------

  private final RoundingInterval multiply (final RoundingInterval q0,
                                           final RoundingInterval q1) {
    //assert contains(q0);
    //assert contains(q1);
    return q0.multiply(q1); }

  public final BinaryOperator<RoundingInterval> multiplier () {
    return new BinaryOperator<>() {
      @Override
      public final String toString () { return "BF.multiply"; }
      @Override
      public final RoundingInterval apply (final RoundingInterval q0,
                                           final RoundingInterval q1) {
        return RoundingIntervals.this.multiply(q0, q1); } }; }

  //--------------------------------------------------------------

  public final RoundingInterval multiplicativeIdentity () {
    return RoundingInterval.ONE; }

  //--------------------------------------------------------------
  // Set methods
  //--------------------------------------------------------------

  @Override
  public final boolean contains (final Object element) {
    return element instanceof RoundingInterval; }

  //--------------------------------------------------------------

  @Override
  public final BiPredicate equivalence () {
    //final boolean result = q0.equals(q1);
//        if (! result) {
//          System.out.println("nonNegative:" +
//            (q0.nonNegative()==q1.nonNegative()));
//          System.out.println("exponent:" +
//            (q0.exponent()==q1.exponent()));
//          System.out.println("significand:" +
//            (q0.significand()==q1.significand()));
//          System.out.println(q0.significand().getClass());
//          System.out.println(q0.significand());
//          System.out.println(q1.significand().getClass());
//          System.out.println(q1.significand());
//        }
    return (BiPredicate<RoundingInterval, RoundingInterval>) RoundingInterval::equals; }

  //--------------------------------------------------------------

  // Is this characteristic of most inputs?
  public static final Generator
  fromDoubleGenerator (final UniformRandomProvider urp) {
    final double dp = 0.9;
    return new GeneratorBase ("fromDoubleGenerator") {
      private final ContinuousSampler choose =
        new ContinuousUniformSampler(urp,0.0,1.0);
      private final Generator g = Doubles.finiteGenerator(urp);
      private final CollectionSampler edgeCases =
        new CollectionSampler(
          urp,
          List.of(
            RoundingInterval.ZERO,
            RoundingInterval.ONE,
            new RoundingInterval(2.0, 2.0),
            new RoundingInterval(10.0, 10.0),
            new RoundingInterval(-1.0, -1.0)));
      @Override
      public Object next () {
        final boolean edge = choose.sample() > dp;
        if (edge) { return edgeCases.sample(); }
        final double z0 = g.nextDouble();
        final double z1 = g.nextDouble();
        if (Double.isNaN(z0) || Double.isNaN(z1)) {
          return RoundingInterval.NaN; }
        return new RoundingInterval(Math.min(z0,z1),
                                    Math.max(z0,z1)); } }; }

  // Is this characteristic of most inputs?
  public static final Generator
  generator (final UniformRandomProvider urp) {
    return fromDoubleGenerator(urp); }

  public static final Generator
  fromDoubleGenerator (final int n,
                       final UniformRandomProvider urp) {
    return new GeneratorBase ("fromDoubleGenerator:" + n) {
      final Generator g = fromDoubleGenerator(urp);
      @Override
      public final Object next () {
        final RoundingInterval[] z = new RoundingInterval[n];
        for (int i=0;i<n;i++) { z[i] = (RoundingInterval) g.next(); }
        return z; } }; }

  public static final Generator
  generator (final int n,
             final UniformRandomProvider urp) {
    return new GeneratorBase ("rationalGenerator:" + n) {
      final Generator g = generator(urp);
      @Override
      public final Object next () {
        final RoundingInterval[] z = new RoundingInterval[n];
        for (int i=0;i<n;i++) { z[i] = (RoundingInterval) g.next(); }
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
    return that instanceof RoundingIntervals; }

  @Override
  public final String toString () { return "RoundingIntervals"; }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private RoundingIntervals () { }

  private static final RoundingIntervals
    SINGLETON = new RoundingIntervals();

  public static final RoundingIntervals get () { return SINGLETON; }

  //--------------------------------------------------------------

  public static final OneSetOneOperation ADDITIVE_MAGMA =
    OneSetOneOperation.magma(get().adder(),get());

  public static final OneSetOneOperation MULTIPLICATIVE_MAGMA =
    OneSetOneOperation.magma(get().multiplier(),get());

  public static final OneSetTwoOperations RING =
    OneSetTwoOperations.commutativeRing(
      get().adder(),
      get().additiveIdentity(),
      get().additiveInverse(),
      get().multiplier(),
      get().multiplicativeIdentity(),
      get());

  //--------------------------------------------------------------
}
//--------------------------------------------------------------

