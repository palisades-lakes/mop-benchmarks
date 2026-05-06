package mop.java.numbers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.CollectionSampler;
import org.apache.commons.rng.sampling.distribution.ContinuousSampler;
import org.apache.commons.rng.sampling.distribution.ContinuousUniformSampler;

import clojure.lang.Numbers;
import clojure.lang.Ratio;
import mop.java.algebra.OneSetOneOperation;
import mop.java.algebra.OneSetTwoOperations;
import mop.java.algebra.Set;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.GeneratorBase;

/** The set of rational numbers represented by
 * <code>Ratio</code>.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-05-11
 */
@SuppressWarnings("unchecked")
public final class Ratios implements Set {

  //--------------------------------------------------------------
  // operations for algebraic structures over Ratios.
  //--------------------------------------------------------------

  public static final Ratio abs (final Ratio q) {
    return new Ratio(q.numerator.abs(),q.denominator.abs()); }

  //--------------------------------------------------------------

  // TODO: is consistency with other algebraic structure classes
  // worth the indirection?

  public final Ratio add (final Ratio q0,
                          final Ratio q1) {
    assert contains(q0);
    assert contains(q1);
    return Numbers.toRatio(Numbers.add(q0,q1)); }

  public final BinaryOperator<Ratio> adder () {
    return new BinaryOperator<> () {
      @Override
      public final String toString () { return "Ratio.add()"; }
      @Override
      public final Ratio apply (final Ratio q0,
                                final Ratio q1) {
        return Ratios.this.add(q0,q1); } }; }

  //--------------------------------------------------------------

  // TODO: is consistency with other algebraic structure classes
  // worth the indirection?

  public final Ratio negate (final Ratio q) {
    assert contains(q);
    return Numbers.toRatio(Numbers.minus(q)); }

  public final UnaryOperator<Ratio> additiveInverse () {
    return new UnaryOperator<> () {
      @Override
      public final String toString () { return "Ratio.negate()"; }
      @Override
      public final Ratio apply (final Ratio q) {
        return Ratios.this.negate(q); } }; }

  //--------------------------------------------------------------

  @SuppressWarnings("static-method")
  private final Ratio zero () {
    return new Ratio(BigInteger.ZERO,BigInteger.ONE); }

  public final Object additiveIdentity () {
    return zero(); }

  //--------------------------------------------------------------

  public final Ratio multiply (final Ratio q0,
                               final Ratio q1) {
    assert contains(q0);
    assert contains(q1);
    return Numbers.toRatio(Numbers.multiply(q0,q1)); }

  public final BinaryOperator<Ratio> multiplier () {
    return new BinaryOperator<>() {
      @Override
      public final String toString () { return "Ratio.multiply()"; }
      @Override
      public final Ratio apply (final Ratio q0,
                                final Ratio q1) {
        return Ratios.this.multiply(q0,q1); } }; }

  //--------------------------------------------------------------

  @SuppressWarnings("static-method")
  private final Ratio one () {
    return new Ratio(BigInteger.ONE,BigInteger.ONE); }

  public final Object multiplicativeIdentity () {
    return one(); }

  //--------------------------------------------------------------

  private final Ratio reciprocal (final Ratio q) {
    assert contains(q);
    // only a partial inverse
    final BigInteger n = q.numerator;
    final BigInteger d = q.denominator;
    // only a partial inverse
    if (BigInteger.ZERO.equals(n)) { return null; }
    return new Ratio(d,n); }

  public final UnaryOperator<Ratio> multiplicativeInverse () {
    return new UnaryOperator<> () {
      @Override
      public final String toString () { return "Ratio.inverse()"; }
      @Override
      public final Ratio apply (final Ratio q) {
        return Ratios.this.reciprocal(q); } }; }

  //--------------------------------------------------------------
  // Set methods
  //--------------------------------------------------------------

  @Override
  public final boolean contains (final Object element) {
    return element instanceof Ratio; }

  //--------------------------------------------------------------
  // Ratio.equals reduces both arguments before checking
  // numerator and denominators are equal.
  // Guessing our Ratios are usually already reduced.
  // Try n0*d1 == n1*d0 instead
  // TODO: use BigIntegerNZQR.bitLength() to decide
  // which method to use?

  // clojure.lang.Ratio doesn't equate 1/1 and 2/2!

  @SuppressWarnings("static-method")
  public final boolean equals (final Ratio q0,
                               final Ratio q1) {
    if (q0 == q1) { return true; }
    if (null == q0) {
      if (null == q1) { return true; }
      return false; }
    if (null == q1) { return false; }
    final BigInteger n0 = q0.numerator;
    final BigInteger d0 = q0.denominator;
    final BigInteger n1 = q1.numerator;
    final BigInteger d1 = q1.denominator;
    return n0.multiply(d1).equals(n1.multiply(d0)); }

  @Override
  public final BiPredicate equivalence () {
    return new BiPredicate<Ratio,Ratio>() {
      @Override
      public final boolean test (final Ratio q0,
                                 final Ratio q1) {
        return Ratios.this.equals(q0,q1); } }; }

  //--------------------------------------------------------------
  /** Intended primarily for testing. Sample a random double
   * (see <code>mop.java.prng.DoubleSampler</code>)
   * and convert to <code>BigDecimal</code>
   * with DOUBLE_P probability;
   * otherwise return ZERO or
   * ONE, TEN,
   * with equal probability (these are potential edge cases).
   *
   * TODO: sample rounding modes?
   */

  public static final Generator
  ratioGenerator (final UniformRandomProvider urp) {
    final double dp = 0.9;
    return new GeneratorBase ("ratioGenerator") {
      private final ContinuousSampler choose =
        new ContinuousUniformSampler(urp,0.0,1.0);
      private final Generator fdg = Doubles.finiteGenerator(urp);
      private final CollectionSampler edgeCases =
        new CollectionSampler(
          urp,
          List.of(
            new Ratio(BigInteger.ZERO,BigInteger.ONE),
            new Ratio(BigInteger.ONE,BigInteger.ONE),
            new Ratio(BigInteger.TWO,BigInteger.TWO),
            new Ratio(BigInteger.TEN,BigInteger.TEN),
            new Ratio(BigInteger.ONE,BigInteger.ONE),
            new Ratio(BigInteger.TWO,BigInteger.ONE),
            new Ratio(BigInteger.TEN,BigInteger.ONE),
            new Ratio(BigInteger.ONE,BigInteger.TWO),
            new Ratio(BigInteger.TWO,BigInteger.TWO),
            new Ratio(BigInteger.TEN,BigInteger.TWO),
            new Ratio(BigInteger.ONE,BigInteger.TEN),
            new Ratio(BigInteger.TWO,BigInteger.TEN),
            new Ratio(BigInteger.TEN,BigInteger.TEN)));
      @Override
      public Object next () {
        final boolean edge = choose.sample() > dp;
        if (edge) { return edgeCases.sample(); }
        return Numbers.toRatio(
          new BigDecimal(fdg.nextDouble())); } }; }

  public static final Generator
  ratioGenerator (final int n,
                  final UniformRandomProvider urp) {
    return new GeneratorBase ("ratioGenerator:" + n) {
      final Generator g = ratioGenerator(urp);
      @Override
      public final Object next () {
        final Ratio[] z = new Ratio[n];
        for (int i=0;i<n;i++) { z[i] = (Ratio) g.next(); }
        return z; } }; }

  @Override
  public final Supplier generator (final Map options) {
    final UniformRandomProvider urp = Set.urp(options);
    final Generator bfs = Ratios.ratioGenerator(urp);
    return
      new Supplier () {
      @Override
      public final Object get () { return bfs.next(); } }; }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------

  @Override
  public final int hashCode () { return 0; }

  // singleton
  @Override
  public final boolean equals (final Object that) {
    return that instanceof Ratios; }

  @Override
  public final String toString () { return "Ratios"; }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private Ratios () { }

  private static final Ratios SINGLETON = new Ratios();

  public static final Ratios get () { return SINGLETON; }

  //--------------------------------------------------------------

  public static final OneSetOneOperation ADDITIVE_MAGMA =
    OneSetOneOperation.magma(get().adder(),get());

  public static final OneSetOneOperation MULTIPLICATIVE_MAGMA =
    OneSetOneOperation.magma(get().multiplier(),get());

  public static final OneSetTwoOperations FIELD =
    OneSetTwoOperations.field(
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

