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
import spire.math.Algebraic;
import spire.math.Algebraic$;
import spire.math.Rational;
import spire.math.SafeLong;
import spire.math.SafeLong$;
import spire.math.SafeLongBigInteger;

/** The set of algebraic numbers, represented by
 * <code>spire.math.Algebraic</code> instances.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-12-02
 */
@SuppressWarnings({"unchecked","static-method"})
public final class SpireAlgebraics implements Set {

  //--------------------------------------------------------------

  public static final Algebraic toAlgebraic (final double x) {
    return Algebraic$.MODULE$.apply(x); }

  public static final Algebraic toAlgebraic (final float x) {
    return Algebraic$.MODULE$.apply(x); }

  public static final Algebraic toAlgebraic (final long x) {
    return Algebraic$.MODULE$.apply(x); }

  public static final Algebraic toAlgebraic (final int x) {
    return Algebraic$.MODULE$.apply(x); }

  public static final Algebraic toAlgebraic (final short x) {
    return Algebraic$.MODULE$.apply(x); }

  public static final Algebraic toAlgebraic (final byte x) {
    return Algebraic$.MODULE$.apply(x); }

  public static final Algebraic toAlgebraic (final Rational q) {
    return Algebraic$.MODULE$.apply(q); }

  public static final Algebraic toAlgebraic (final SafeLong x) {
    return toAlgebraic(SpireRationals.toRational(x)); }

  public static final Algebraic toAlgebraic (final BigInteger x) {
    return toAlgebraic(SpireRationals.toRational(x)); }

  public static final Algebraic toAlgebraic (final SafeLong n,
                                             final SafeLong d) {
    return toAlgebraic(SpireRationals.toRational(n,d)); }

  public static final Algebraic toAlgebraic (final BigInteger n,
                                             final BigInteger d) {
    return toAlgebraic(SpireRationals.toRational(n,d)); }

  public static final Algebraic toAlgebraic (final Object x) {
    if (x instanceof Algebraic) { return (Algebraic) x; }
    if (x instanceof SafeLong) { return toAlgebraic((SafeLong) x); }
    if (x instanceof BigInteger) {return toAlgebraic((BigInteger) x); }
    // TODO: too tricky with rounding modes, etc.
    // if (x instanceof BigDecimal) {return toAlgebraic((BigDecimal) x); }
    if (x instanceof Double) {
      return toAlgebraic(((Double) x).doubleValue()); }
    if (x instanceof Integer) {
      return toAlgebraic(((Integer) x).intValue()); }
    if (x instanceof Long) {
      return toAlgebraic(((Long) x).longValue()); }
    if (x instanceof Float) {
      return toAlgebraic(((Float) x).floatValue()); }
    if (x instanceof Short) {
      return toAlgebraic(((Short) x).intValue()); }
    if (x instanceof Byte) {
      return toAlgebraic(((Byte) x).intValue()); }
    throw Exceptions.unsupportedOperation(
      SpireAlgebraics.class,"toAlgebraic",x); }

  //--------------------------------------------------------------

  public static final Algebraic[] toAlgebraicArray (final Object[] x) {
    final int n = x.length;
    final Algebraic[] y = new Algebraic[n];
    for (int i=0;i<n;i++) { y[i] = toAlgebraic(x[i]); }
    return y; }

  public static final Algebraic[]
    toAlgebraicArray (final double[] x) {
    final int n = x.length;
    final Algebraic[] y = new Algebraic[n];
    for (int i=0;i<n;i++) { y[i] = toAlgebraic(x[i]); }
    return y; }

  public static final Algebraic[]
    toAlgebraicArray (final float[] x) {
    final int n = x.length;
    final Algebraic[] y = new Algebraic[n];
    for (int i=0;i<n;i++) { y[i] = toAlgebraic(x[i]); }
    return y; }

  public static final Algebraic[]
    toAlgebraicArray (final long[] x) {
    final int n = x.length;
    final Algebraic[] y = new Algebraic[n];
    for (int i=0;i<n;i++) { y[i] = toAlgebraic(x[i]); }
    return y; }

  public static final Algebraic[]
    toAlgebraicArray (final int[] x) {
    final int n = x.length;
    final Algebraic[] y = new Algebraic[n];
    for (int i=0;i<n;i++) { y[i] = toAlgebraic(x[i]); }
    return y; }

  public static final Algebraic[]
    toAlgebraicArray (final short[] x) {
    final int n = x.length;
    final Algebraic[] y = new Algebraic[n];
    for (int i=0;i<n;i++) { y[i] = toAlgebraic(x[i]); }
    return y; }

  public static final Algebraic[]
    toAlgebraicArray (final byte[] x) {
    final int n = x.length;
    final Algebraic[] y = new Algebraic[n];
    for (int i=0;i<n;i++) { y[i] = toAlgebraic(x[i]); }
    return y; }

  //--------------------------------------------------------------

  public static final Algebraic[] toAlgebraicArray (final Object x) {

    if (x instanceof Algebraic[]) { return (Algebraic[]) x; }

    if (x instanceof byte[]) {
      return toAlgebraicArray((byte[]) x); }

    if (x instanceof short[]) {
      return toAlgebraicArray((short[]) x); }

    if (x instanceof int[]) {
      return toAlgebraicArray((int[]) x); }

    if (x instanceof long[]) {
      return toAlgebraicArray((long[]) x); }

    if (x instanceof float[]) {
      return toAlgebraicArray((float[]) x); }

    if (x instanceof double[]) {
      return toAlgebraicArray((double[]) x); }

    if (x instanceof Object[]) {
      return toAlgebraicArray((Object[]) x); }

    throw Exceptions.unsupportedOperation(
      SpireAlgebraics.class,"toAlgebraicArray",x); }

  //--------------------------------------------------------------

  public static final double doubleValue (final Algebraic f) {
    return f.toDouble(); }
  //  return f.doubleValue(); }

  public static final float floatValue (final Algebraic f) {
    return f.toFloat(); }
  //  return f.floatValue(); }

  public static final int intValue (final Algebraic f) {
    return f.intValue(); }

  public static final long longValue (final Algebraic f) {
    return f.longValue(); }

  //--------------------------------------------------------------
  // operations for algebraic structures over Algebraic.
  //--------------------------------------------------------------

  // TODO: is consistency with other algebraic structure classes
  // worth the indirection?

  private final Algebraic add (final Algebraic q0,
                               final Algebraic q1) {
    //assert contains(q0);
    //assert contains(q1);
    return q0.$plus(q1); }

  public final BinaryOperator<Algebraic> adder () {
    return new BinaryOperator<> () {
      @Override
      public final String toString () { return "BF.add()"; }
      @Override
      public final Algebraic apply (final Algebraic q0,
                                    final Algebraic q1) {
        return SpireAlgebraics.this.add(q0,q1); } }; }

  //--------------------------------------------------------------

  public static final Algebraic ZERO = Algebraic$.MODULE$.Zero();

  public final Algebraic additiveIdentity () { return ZERO; }

  //--------------------------------------------------------------

  // TODO: is consistency with other algebraic structure classes
  // worth the indirection?

  private final Algebraic negate (final Algebraic q) {
    //assert contains(q);
    return q.unary_$minus(); }

  public final UnaryOperator<Algebraic> additiveInverse () {
    return new UnaryOperator<> () {
      @Override
      public final String toString () { return "BF.negate()"; }
      @Override
      public final Algebraic apply (final Algebraic q) {
        return SpireAlgebraics.this.negate(q); } }; }

  //--------------------------------------------------------------

  private final Algebraic multiply (final Algebraic q0,
                                    final Algebraic q1) {
    //assert contains(q0);
    //assert contains(q1);
    return q0.$times(q1); }

  public final BinaryOperator<Algebraic> multiplier () {
    return new BinaryOperator<>() {
      @Override
      public final String toString () { return "BF.multiply()"; }
      @Override
      public final Algebraic apply (final Algebraic q0,
                                    final Algebraic q1) {
        return SpireAlgebraics.this.multiply(q0,q1); } }; }

  //--------------------------------------------------------------

  private static final Algebraic ONE = Algebraic$.MODULE$.One();

  public final Algebraic multiplicativeIdentity () { return ONE; }

  //--------------------------------------------------------------

  public static final Algebraic reciprocal (final Algebraic q) {
    return ONE.$div(q); }

  public final UnaryOperator<Algebraic> multiplicativeInverse () {
    return new UnaryOperator<> () {
      @Override
      public final String toString () { return "BF.inverse()"; }
      @Override
      public final Algebraic apply (final Algebraic q) {
        return SpireAlgebraics.reciprocal(q); } }; }

  //--------------------------------------------------------------
  // Set methods
  //--------------------------------------------------------------

  @Override
  public final boolean contains (final Object element) {
    return element instanceof Algebraic; }

  //--------------------------------------------------------------

  public final boolean equals (final Algebraic q0,
                               final Algebraic q1) {
    return q0.equals(q1); }

  @Override
  public final BiPredicate equivalence () {
    return new BiPredicate<Algebraic,Algebraic>() {
      @Override
      public final boolean test (final Algebraic q0,
                                 final Algebraic q1) {
        return q0.equals(q1); } }; }

  //--------------------------------------------------------------

  @Override
  public final Supplier generator (final Map options) {
    final UniformRandomProvider urp = Set.urp(options);
    final Generator g =
      SpireAlgebraics.rationalFromSafeLongGenerator(urp);
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
    return that instanceof SpireAlgebraics; }

  @Override
  public final String toString () { return "BF"; }

  //--------------------------------------------------------------
  // ordering
  //--------------------------------------------------------------

  public static final int compareTo (final Object x,
                                     final Object y) {
    return ((Algebraic) x).compare((Algebraic) y); }

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
        final Algebraic[] z = new Algebraic[n];
        for (int i=0;i<n;i++) { z[i] = (Algebraic) g.next(); }
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
        final Algebraic f = toAlgebraic(n,d);
        return f; } }; }

  public static final Generator
  rationalFromDoubleGenerator (final int n,
                               final UniformRandomProvider urp) {
    return new GeneratorBase ("rationalFromDoubleGenerator:" + n) {
      final Generator g = rationalFromDoubleGenerator(urp);
      @Override
      public final Object next () {
        final Algebraic[] z = new Algebraic[n];
        for (int i=0;i<n;i++) { z[i] = (Algebraic) g.next(); }
        return z; } }; }

  /** Intended primarily for testing. Sample a random double
   * (see <code>mop.java.prng.DoubleSampler</code>)
   * and convert to <code>Algebraic</code>
   * with DOUBLE_P probability;
   * otherwise return {@link SpireAlgebraics#ZERO} or
   * {@link SpireAlgebraics#ONE}, <code>ONE.unary_$minus()</code>>,
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
        return toAlgebraic(fdg.nextDouble()); } }; }

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

  private SpireAlgebraics () { }

  private static final SpireAlgebraics SINGLETON = new SpireAlgebraics();

  public static final SpireAlgebraics get () { return SINGLETON; }

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

