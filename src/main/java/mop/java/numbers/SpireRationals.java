package mop.java.numbers;

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

import mop.java.Exceptions;
import mop.java.algebra.OneSetOneOperation;
import mop.java.algebra.OneSetTwoOperations;
import mop.java.algebra.Set;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.GeneratorBase;
import mop.java.prng.Generators;
import spire.math.Rational;
import spire.math.Rational$;
import spire.math.SafeLong;
import spire.math.SafeLong$;
import spire.math.SafeLongBigInteger;

/** The set of rational numbers represented by
 * <code>Rational</code>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-12-01
 */
@SuppressWarnings({"unchecked","static-method"})
public final class SpireRationals implements Set {

  //--------------------------------------------------------------

  public static final Rational toRational (final double x) {
    return Rational$.MODULE$.apply(x); }

  public static final Rational toRational (final float x) {
    return Rational$.MODULE$.apply(x); }

  public static final Rational toRational (final long x) {
    return Rational$.MODULE$.apply(x); }

  public static final Rational toRational (final int x) {
    return Rational$.MODULE$.apply(x); }

  public static final Rational toRational (final short x) {
    return Rational$.MODULE$.apply(x); }

  public static final Rational toRational (final byte x) {
    return Rational$.MODULE$.apply(x); }

  public static final Rational toRational (final SafeLong x) {
    return Rational$.MODULE$.apply(x); }

  public static final Rational toRational (final BigInteger x) {
    final SafeLong sl = new SafeLongBigInteger(x);
    return Rational$.MODULE$.apply(sl); }

  public static final Rational toRational (final SafeLong n,
                                           final SafeLong d) {
    return Rational$.MODULE$.apply(n,d); }

  public static final Rational toRational (final BigInteger n,
                                           final BigInteger d) {
    final SafeLong nsl = new SafeLongBigInteger(n);
    final SafeLong dsl = new SafeLongBigInteger(d);
    return toRational(nsl,dsl); }

  public static final Rational toRational (final Object x) {
    if (x instanceof Rational) { return (Rational) x; }
    if (x instanceof SafeLong) { return toRational((SafeLong) x); }
    if (x instanceof BigInteger) {return toRational((BigInteger) x); }
    // TODO: too tricky with rounding modes, etc.
    // if (x instanceof BigDecimal) {return toRational((BigDecimal) x); }
    if (x instanceof Double) {
      return toRational(((Double) x).doubleValue()); }
    if (x instanceof Integer) {
      return toRational(((Integer) x).intValue()); }
    if (x instanceof Long) {
      return toRational(((Long) x).longValue()); }
    if (x instanceof Float) {
      return toRational(((Float) x).floatValue()); }
    if (x instanceof Short) {
      return toRational(((Short) x).intValue()); }
    if (x instanceof Byte) {
      return toRational(((Byte) x).intValue()); }
    throw Exceptions.unsupportedOperation(
      SpireRationals.class,"toRational",x); }

  //--------------------------------------------------------------

  public static final Rational[] toRationalArray (final Object[] x) {
    final int n = x.length;
    final Rational[] y = new Rational[n];
    for (int i=0;i<n;i++) { y[i] = toRational(x[i]); }
    return y; }

  public static final Rational[]
    toRationalArray (final double[] x) {
    final int n = x.length;
    final Rational[] y = new Rational[n];
    for (int i=0;i<n;i++) { y[i] = toRational(x[i]); }
    return y; }

  public static final Rational[]
    toRationalArray (final float[] x) {
    final int n = x.length;
    final Rational[] y = new Rational[n];
    for (int i=0;i<n;i++) { y[i] = toRational(x[i]); }
    return y; }

  public static final Rational[]
    toRationalArray (final long[] x) {
    final int n = x.length;
    final Rational[] y = new Rational[n];
    for (int i=0;i<n;i++) { y[i] = toRational(x[i]); }
    return y; }

  public static final Rational[]
    toRationalArray (final int[] x) {
    final int n = x.length;
    final Rational[] y = new Rational[n];
    for (int i=0;i<n;i++) { y[i] = toRational(x[i]); }
    return y; }

  public static final Rational[]
    toRationalArray (final short[] x) {
    final int n = x.length;
    final Rational[] y = new Rational[n];
    for (int i=0;i<n;i++) { y[i] = toRational(x[i]); }
    return y; }

  public static final Rational[]
    toRationalArray (final byte[] x) {
    final int n = x.length;
    final Rational[] y = new Rational[n];
    for (int i=0;i<n;i++) { y[i] = toRational(x[i]); }
    return y; }

  //--------------------------------------------------------------

  public static final Rational[] toRationalArray (final Object x) {

    if (x instanceof Rational[]) { return (Rational[]) x; }

    if (x instanceof byte[]) {
      return toRationalArray((byte[]) x); }

    if (x instanceof short[]) {
      return toRationalArray((short[]) x); }

    if (x instanceof int[]) {
      return toRationalArray((int[]) x); }

    if (x instanceof long[]) {
      return toRationalArray((long[]) x); }

    if (x instanceof float[]) {
      return toRationalArray((float[]) x); }

    if (x instanceof double[]) {
      return toRationalArray((double[]) x); }

    if (x instanceof Object[]) {
      return toRationalArray((Object[]) x); }

    throw Exceptions.unsupportedOperation(
      SpireRationals.class,"toRationalArray",x); }

  //--------------------------------------------------------------
  // from Rational to other numbers
  //--------------------------------------------------------------

  public static final double doubleValue (final Rational f) {
    return f.toDouble(); }
  //  return f.doubleValue(); }

  public static final float floatValue (final Rational f) {
    return f.toFloat(); }
  //  return f.floatValue(); }

  public static final int intValue (final Rational f) {
    return f.intValue(); }

  public static final long longValue (final Rational f) {
    return f.longValue(); }

  //--------------------------------------------------------------
  // operations for algebraic structures over Rationals.
  //--------------------------------------------------------------

  // TODO: is consistency with other algebraic structure classes
  // worth the indirection?

  private final Rational add (final Rational q0,
                              final Rational q1) {
    //assert contains(q0);
    //assert contains(q1);
    return q0.$plus(q1); }

  public final BinaryOperator<Rational> adder () {
    return new BinaryOperator<> () {
      @Override
      public final String toString () { return "BF.add()"; }
      @Override
      public final Rational apply (final Rational q0,
                                   final Rational q1) {
        return SpireRationals.this.add(q0,q1); } }; }

  //--------------------------------------------------------------

  public static final Rational ZERO = Rational$.MODULE$.zero();

  public final Rational additiveIdentity () { return ZERO; }

  //--------------------------------------------------------------

  // TODO: is consistency with other algebraic structure classes
  // worth the indirection?

  private final Rational negate (final Rational q) {
    //assert contains(q);
    return q.unary_$minus(); }

  public final UnaryOperator<Rational> additiveInverse () {
    return new UnaryOperator<> () {
      @Override
      public final String toString () { return "BF.negate()"; }
      @Override
      public final Rational apply (final Rational q) {
        return SpireRationals.this.negate(q); } }; }

  //--------------------------------------------------------------

  private final Rational multiply (final Rational q0,
                                   final Rational q1) {
    //assert contains(q0);
    //assert contains(q1);
    return q0.$times(q1); }

  public final BinaryOperator<Rational> multiplier () {
    return new BinaryOperator<>() {
      @Override
      public final String toString () { return "BF.multiply()"; }
      @Override
      public final Rational apply (final Rational q0,
                                   final Rational q1) {
        return SpireRationals.this.multiply(q0,q1); } }; }

  //--------------------------------------------------------------

  private static final Rational ONE = Rational$.MODULE$.one();

  public final Rational multiplicativeIdentity () { return ONE; }

  //--------------------------------------------------------------

  public static final Rational reciprocal (final Rational q) {
    // only a partial inverse
    return q.inverse(); }

  public final UnaryOperator<Rational> multiplicativeInverse () {
    return new UnaryOperator<> () {
      @Override
      public final String toString () { return "BF.inverse()"; }
      @Override
      public final Rational apply (final Rational q) {
        return q.inverse(); } }; }

  //--------------------------------------------------------------
  // Set methods
  //--------------------------------------------------------------

  @Override
  public final boolean contains (final Object element) {
    return element instanceof Rational; }

  //--------------------------------------------------------------

  public final boolean equals (final Rational q0,
                               final Rational q1) {
    return q0.equals(q1); }

  @Override
  public final BiPredicate equivalence () {
    return new BiPredicate<Rational,Rational>() {
      @Override
      public final boolean test (final Rational q0,
                                 final Rational q1) {
        return q0.equals(q1); } }; }

  //--------------------------------------------------------------

  @Override
  public final Supplier generator (final Map options) {
    final UniformRandomProvider urp = Set.urp(options);
    final Generator g =
      SpireRationals.rationalFromSafeLongGenerator(urp);
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
    return that instanceof SpireRationals; }

  @Override
  public final String toString () { return "BF"; }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------


  public static final Generator
  rationalFromEintegerGenerator (final int n,
                                 final UniformRandomProvider urp) {
    return new GeneratorBase ("rationalFromEintegerGenerator:" + n) {
      final Generator g = rationalFromSafeLongGenerator(urp);
      @Override
      public final Object next () {
        final Rational[] z = new Rational[n];
        for (int i=0;i<n;i++) { z[i] = (Rational) g.next(); }
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
        final Rational f = toRational(n,d);
        return f; } }; }

  public static final Generator
  rationalFromDoubleGenerator (final int n,
                               final UniformRandomProvider urp) {
    return new GeneratorBase ("rationalFromDoubleGenerator:" + n) {
      final Generator g = rationalFromDoubleGenerator(urp);
      @Override
      public final Object next () {
        final Rational[] z = new Rational[n];
        for (int i=0;i<n;i++) { z[i] = (Rational) g.next(); }
        return z; } }; }

  /** Intended primarily for testing. Sample a random double
   * (see mop.java.prng.DoubleSampler)
   * and convert to <code>Rational</code>
   * with DOUBLE_P probability;
   * otherwise return {@link SpireRationals#ZERO} or
   * {@link SpireRationals#ONE},
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
        return toRational(fdg.nextDouble()); } }; }

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

  private SpireRationals () { }

  private static final SpireRationals SINGLETON = new SpireRationals();

  public static final SpireRationals get () { return SINGLETON; }

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

