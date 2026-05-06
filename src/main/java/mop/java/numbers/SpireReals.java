package mop.java.numbers;

import mop.java.Exceptions;
import mop.java.algebra.OneSetOneOperation;
import mop.java.algebra.OneSetTwoOperations;
import mop.java.algebra.Set;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.GeneratorBase;
import mop.java.prng.Generators;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.CollectionSampler;
import org.apache.commons.rng.sampling.distribution.ContinuousSampler;
import org.apache.commons.rng.sampling.distribution.ContinuousUniformSampler;
import spire.math.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/** The set of computable real numbers, represented by
 * <code>spire.math.Real</code>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-12-02
 */
@SuppressWarnings({"unchecked","static-method"})
public final class SpireReals implements Set {

  //--------------------------------------------------------------

  public static final Real toReal (final double x) {
    return Real$.MODULE$.apply(x); }

  public static final Real toReal (final float x) {
    return Real$.MODULE$.apply(x); }

  public static final Real toReal (final long x) {
    return Real$.MODULE$.apply(x); }

  public static final Real toReal (final int x) {
    return Real$.MODULE$.apply(x); }

  public static final Real toReal (final short x) {
    return Real$.MODULE$.apply(x); }

  public static final Real toReal (final byte x) {
    return Real$.MODULE$.apply(x); }

  public static final Real toReal (final Rational q) {
    return Real$.MODULE$.apply(q); }

  public static final Real toReal (final SafeLong x) {
    return toReal(SpireRationals.toRational(x)); }

  public static final Real toReal (final BigInteger x) {
    return toReal(SpireRationals.toRational(x)); }

  public static final Real toReal (final SafeLong n,
                                   final SafeLong d) {
    return toReal(SpireRationals.toRational(n,d)); }

  public static final Real toReal (final BigInteger n,
                                   final BigInteger d) {
    return toReal(SpireRationals.toRational(n,d)); }

  public static final Real toReal (final Object x) {
    if (x instanceof Real) { return (Real) x; }
    if (x instanceof SafeLong) { return toReal((SafeLong) x); }
    if (x instanceof BigInteger) {return toReal((BigInteger) x); }
    // TODO: too tricky with rounding modes, etc.
    // if (x instanceof BigDecimal) {return toReal((BigDecimal) x); }
    if (x instanceof Double) {
      return toReal(((Double) x).doubleValue()); }
    if (x instanceof Integer) {
      return toReal(((Integer) x).intValue()); }
    if (x instanceof Long) {
      return toReal(((Long) x).longValue()); }
    if (x instanceof Float) {
      return toReal(((Float) x).floatValue()); }
    if (x instanceof Short) {
      return toReal(((Short) x).intValue()); }
    if (x instanceof Byte) {
      return toReal(((Byte) x).intValue()); }
    throw Exceptions.unsupportedOperation(
      SpireReals.class,"toReal",x); }

  //--------------------------------------------------------------

  public static final Real[] toRealArray (final Object[] x) {
    final int n = x.length;
    final Real[] y = new Real[n];
    for (int i=0;i<n;i++) { y[i] = toReal(x[i]); }
    return y; }

  public static final Real[]
    toRealArray (final double[] x) {
    final int n = x.length;
    final Real[] y = new Real[n];
    for (int i=0;i<n;i++) { y[i] = toReal(x[i]); }
    return y; }

  public static final Real[]
    toRealArray (final float[] x) {
    final int n = x.length;
    final Real[] y = new Real[n];
    for (int i=0;i<n;i++) { y[i] = toReal(x[i]); }
    return y; }

  public static final Real[]
    toRealArray (final long[] x) {
    final int n = x.length;
    final Real[] y = new Real[n];
    for (int i=0;i<n;i++) { y[i] = toReal(x[i]); }
    return y; }

  public static final Real[]
    toRealArray (final int[] x) {
    final int n = x.length;
    final Real[] y = new Real[n];
    for (int i=0;i<n;i++) { y[i] = toReal(x[i]); }
    return y; }

  public static final Real[]
    toRealArray (final short[] x) {
    final int n = x.length;
    final Real[] y = new Real[n];
    for (int i=0;i<n;i++) { y[i] = toReal(x[i]); }
    return y; }

  public static final Real[]
    toRealArray (final byte[] x) {
    final int n = x.length;
    final Real[] y = new Real[n];
    for (int i=0;i<n;i++) { y[i] = toReal(x[i]); }
    return y; }

  //--------------------------------------------------------------

  public static final Real[] toRealArray (final Object x) {

    if (x instanceof Real[]) { return (Real[]) x; }

    if (x instanceof byte[]) {
      return toRealArray((byte[]) x); }

    if (x instanceof short[]) {
      return toRealArray((short[]) x); }

    if (x instanceof int[]) {
      return toRealArray((int[]) x); }

    if (x instanceof long[]) {
      return toRealArray((long[]) x); }

    if (x instanceof float[]) {
      return toRealArray((float[]) x); }

    if (x instanceof double[]) {
      return toRealArray((double[]) x); }

    if (x instanceof Object[]) {
      return toRealArray((Object[]) x); }

    throw Exceptions.unsupportedOperation(
      SpireReals.class,"toRealArray",x); }

  //--------------------------------------------------------------

  public static final double doubleValue (final Real f) {
    return f.toDouble(); }
  //  return f.doubleValue(); }

  public static final float floatValue (final Real f) {
    return f.toFloat(); }
  //  return f.floatValue(); }

  public static final int intValue (final Real f) {
    return f.intValue(); }

  public static final long longValue (final Real f) {
    return f.longValue(); }

  //--------------------------------------------------------------
  // operations for algebraic structures over Reals.
  //--------------------------------------------------------------

  // TODO: is consistency with other algebraic structure classes
  // worth the indirection?

  private final Real add (final Real q0,
                          final Real q1) {
    //assert contains(q0);
    //assert contains(q1);
    return q0.$plus(q1); }

  public final BinaryOperator<Real> adder () {
    return new BinaryOperator<> () {
      @Override
      public final String toString () { return "BF.add()"; }
      @Override
      public final Real apply (final Real q0,
                               final Real q1) {
        return SpireReals.this.add(q0,q1); } }; }

  //--------------------------------------------------------------

  public static final Real ZERO = Real$.MODULE$.zero();

  public final Real additiveIdentity () { return ZERO; }

  //--------------------------------------------------------------

  // TODO: is consistency with other algebraic structure classes
  // worth the indirection?

  private final Real negate (final Real q) {
    //assert contains(q);
    return q.unary_$minus(); }

  public final UnaryOperator<Real> additiveInverse () {
    return new UnaryOperator<> () {
      @Override
      public final String toString () { return "BF.negate()"; }
      @Override
      public final Real apply (final Real q) {
        return SpireReals.this.negate(q); } }; }

  //--------------------------------------------------------------

  private final Real multiply (final Real q0,
                               final Real q1) {
    //assert contains(q0);
    //assert contains(q1);
    return q0.$times(q1); }

  public final BinaryOperator<Real> multiplier () {
    return new BinaryOperator<>() {
      @Override
      public final String toString () { return "BF.multiply()"; }
      @Override
      public final Real apply (final Real q0,
                               final Real q1) {
        return SpireReals.this.multiply(q0,q1); } }; }

  //--------------------------------------------------------------

  private static final Real ONE = Real$.MODULE$.one();

  public final Real multiplicativeIdentity () { return ONE; }

  //--------------------------------------------------------------

  public static final Real reciprocal (final Real q) {
    return ONE.$div(q); }

  public final UnaryOperator<Real> multiplicativeInverse () {
    return new UnaryOperator<> () {
      @Override
      public final String toString () { return "BF.inverse()"; }
      @Override
      public final Real apply (final Real q) {
        return SpireReals.reciprocal(q); } }; }

  //--------------------------------------------------------------
  // Set methods
  //--------------------------------------------------------------

  @Override
  public final boolean contains (final Object element) {
    return element instanceof Real; }

  //--------------------------------------------------------------

  public final boolean equals (final Real q0,
                               final Real q1) {
    return q0.equals(q1); }

  @Override
  public final BiPredicate equivalence () {
    return new BiPredicate<Real,Real>() {
      @Override
      public final boolean test (final Real q0,
                                 final Real q1) {
        return q0.equals(q1); } }; }

  //--------------------------------------------------------------

  @Override
  public final Supplier generator (final Map options) {
    final UniformRandomProvider urp = Set.urp(options);
    final Generator g =
      SpireReals.rationalFromSafeLongGenerator(urp);
    //    Generators.rationalFromDoubleGenerator(urp);
    return
      new Supplier () {
      @Override
      public final Object get () { return g.next(); } }; }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------

  @Override
  public final int hashCode () { return 0; }

  // singleton
  @Override
  public final boolean equals (final Object that) {
    return that instanceof SpireReals; }

  @Override
  public final String toString () { return "BF"; }

  //--------------------------------------------------------------
  // ordering
  //--------------------------------------------------------------

  public static final int compareTo (final Object x,
                                     final Object y) {
    return ((Real) x).compare((Real) y); }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------
  // TODO: generate irrational numbers

  public static final Generator
  rationalFromEintegerGenerator (final int n,
                                 final UniformRandomProvider urp) {
    return new GeneratorBase ("rationalFromEintegerGenerator:" + n) {
      final Generator g = rationalFromSafeLongGenerator(urp);
      @Override
      public final Object next () {
        final Real[] z = new Real[n];
        for (int i=0;i<n;i++) { z[i] = (Real) g.next(); }
        return z; } }; }

  public static final Generator
  rationalFromSafeLongGenerator (final UniformRandomProvider urp) {
    final double dp = 0.9;
    return new GeneratorBase ("rationalFromSafeLongGenerator") {
      private final ContinuousSampler choose =
        new ContinuousUniformSampler(urp,0.0,1.0);
      final Generator gn = safeLongGenerator(urp);
      final Generator gd = nonzeroSafeLongGenerator(urp);
      private final CollectionSampler edgeCases =
        new CollectionSampler(
          urp,
          List.of(
            ZERO,
            ONE,
            ONE.unary_$minus()));
      @Override
      public Object next () {
        final boolean edge = choose.sample() > dp;
        if (edge) { return edgeCases.sample(); }
        final SafeLong n = (SafeLong) gn.next();
        final SafeLong d = (SafeLong) gd.next();
        final Real f = toReal(n,d);
        return f; } }; }

  public static final Generator
  rationalFromDoubleGenerator (final int n,
                               final UniformRandomProvider urp) {
    return new GeneratorBase ("rationalFromDoubleGenerator:" + n) {
      final Generator g = rationalFromDoubleGenerator(urp);
      @Override
      public final Object next () {
        final Real[] z = new Real[n];
        for (int i=0;i<n;i++) { z[i] = (Real) g.next(); }
        return z; } }; }

  /** Intended primarily for testing. Sample a random double
   * (see mop.java.prng.DoubleSampler}
   * and convert to <code>Real</code>
   * with DOUBLE_P probability;
   * otherwise return {@link SpireReals#ZERO} or
   * {@link SpireReals#ONE}, <code>ONE.unary_$minus()</code>>,
   * with equal probability (these are potential edge cases).
   */

  public static final Generator
  rationalFromDoubleGenerator (final UniformRandomProvider urp) {
    final double dp = 0.9;
    return new GeneratorBase ("rationalFromDoubleGenerator") {
      private final ContinuousSampler choose =
        new ContinuousUniformSampler(urp,0.0,1.0);
      private final Generator fdg = Doubles.finiteGenerator(urp);
      private final CollectionSampler edgeCases =
        new CollectionSampler(
          urp,
          List.of(
            ZERO,
            ONE,
            ONE.unary_$minus()));
      @Override
      public Object next () {
        final boolean edge = choose.sample() > dp;
        if (edge) { return edgeCases.sample(); }
        return toReal(fdg.nextDouble()); } }; }

  public static final Generator
  nonzeroSafeLongGenerator (final int n,
                            final UniformRandomProvider urp) {
    return new GeneratorBase ("nonzeroSafeLongGenerator:" + n) {
      final Generator g = nonzeroSafeLongGenerator(urp);
      @Override
      public final Object next () {
        final SafeLong[] z = new SafeLong[n];
        for (int i=0;i<n;i++) { z[i] = (SafeLong) g.next(); }
        return z; } }; }

  /** Intended primarily for testing. <b>
   * Generate enough bytes to at least cover the range of
   * <code>double</code> values.
   */

  public static final Generator
  nonzeroSafeLongGenerator (final UniformRandomProvider urp) {
    final double dp = 0.99;
    return new GeneratorBase ("nonzeroSafeLongGenerator") {
      private final ContinuousSampler choose =
        new ContinuousUniformSampler(urp,0.0,1.0);
      private final CollectionSampler edgeCases =
        new CollectionSampler(
          urp,
          List.of(
            SafeLong$.MODULE$.one(),
            SafeLong$.MODULE$.ten()));
      @Override
      public Object next () {
        final boolean edge = choose.sample() > dp;
        if (edge) { return edgeCases.sample(); }
        // TODO: bound infinite loop?
        for (;;) {
          final BigInteger bi = new BigInteger(Generators.nextBytes(urp,1024));
          final SafeLong e = new SafeLongBigInteger(bi);
          if (! e.isZero()) { return e; } } } }; }

  public static final Generator
  safeLongGenerator (final int n,
                     final UniformRandomProvider urp) {
    return new GeneratorBase ("safeLongGenerator:" + n) {
      final Generator g = safeLongGenerator(urp);
      @Override
      public final Object next () {
        final SafeLong[] z = new SafeLong[n];
        for (int i=0;i<n;i++) { z[i] = (SafeLong) g.next(); }
        return z; } }; }

  /** Intended primarily for testing. <b>
   * Generate enough bytes to at least cover the range of
   * <code>double</code> values.
   */

  public static final Generator
  safeLongGenerator (final UniformRandomProvider urp) {
    final double dp = 0.99;
    return new GeneratorBase ("safeLongGenerator") {
      private final ContinuousSampler choose =
        new ContinuousUniformSampler(urp,0.0,1.0);
      private final CollectionSampler edgeCases =
        new CollectionSampler(
          urp,
          List.of(
            SafeLong$.MODULE$.zero(),
            SafeLong$.MODULE$.one(),
            SafeLong$.MODULE$.ten()));
      @Override
      public Object next () {
        final boolean edge = choose.sample() > dp;
        if (edge) { return edgeCases.sample(); }
        final BigInteger bi = new BigInteger(Generators.nextBytes(urp,1024));
        final SafeLong e = new SafeLongBigInteger(bi);
        return e; } }; }

  private SpireReals () { }

  private static final SpireReals SINGLETON = new SpireReals();

  public static final SpireReals get () { return SINGLETON; }

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

