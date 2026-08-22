package mop.java.prng;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.CollectionSampler;
import org.apache.commons.rng.sampling.distribution.ContinuousSampler;
import org.apache.commons.rng.sampling.distribution.ContinuousUniformSampler;

import mop.java.numbers.Doubles;

/** Generators of primitives or Objects as zero-arity 'functions'
 * that return different values on each call.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */

@SuppressWarnings({"unused","unchecked"})
public final class Generators {

  //--------------------------------------------------------------
  // TODO; Integer[], Double[], etc., generators?
  // TODO: move Generator definitions into Set classes

  public static final Generator
  byteGenerator (final UniformRandomProvider urp) {
    return new GeneratorBase ("byteGenerator") {
      @Override
      public final byte nextByte () { return (byte) urp.nextInt(); }
      @Override
      public final Object next () { return nextByte(); } }; }

  public static final Generator
  nonNegativeByteGenerator (final UniformRandomProvider urp) {
    return new GeneratorBase ("byteGenerator") {
      // TODO: make this uniform over non-negative values
      @Override
      public final byte nextByte () {
        // No unsigned 8 bit integer in Java, so use the low 7
        // bits as an unsigned not quite byte
        return (byte) (0x7F & urp.nextInt()); }
      @Override
      public final Object next () { return nextByte(); } }; }

  public static final Generator
  byteGenerator (final int n,
                 final UniformRandomProvider urp) {
    return new GeneratorBase ("byteGenerator:" + n) {
      final Generator g = byteGenerator(urp);
      @Override
      public final Object next () {
        final byte[] z = new byte[n];
        for (int i=0;i<n;i++) { z[i] = g.nextByte(); }
        return z; } }; }

  //--------------------------------------------------------------

  public static final Generator
  shortGenerator (final UniformRandomProvider urp) {
    return new GeneratorBase ("shortGenerator") {
      @Override
      public final short nextShort () { return (short) urp.nextInt(); }
      @Override
      public final Object next () { return nextShort(); } }; }

  public static final Generator
  nonNegativeShortGenerator (final UniformRandomProvider urp) {
    return new GeneratorBase ("shortGenerator") {
      // TODO: make this uniform over non-negative values
      @Override
      public final short nextShort () {
        // No unsigned short in Java, so use the lower 15 bits.
        return (short) (0x7FFF & urp.nextInt()); }
      @Override
      public final Object next () { return nextShort(); } }; }

  public static final Generator
  shortGenerator (final int n,
                  final UniformRandomProvider urp) {
    return new GeneratorBase ("shortGenerator:" + n) {
      final Generator g = shortGenerator(urp);
      @Override
      public final Object next () {
        final short[] z = new short[n];
        for (int i=0;i<n;i++) { z[i] = g.nextShort(); }
        return z; } }; }

  //--------------------------------------------------------------

  public static final Generator
  intGenerator (final UniformRandomProvider urp) {
    return new GeneratorBase ("intGenerator") {
      @Override
      public final int nextInt () { return urp.nextInt(); }
      @Override
      public final Object next () { return nextInt(); } }; }

  public static final Generator
  nonNegativeIntGenerator (final UniformRandomProvider urp) {
    return new GeneratorBase ("intGenerator") {
      // TODO: make this uniform over non-negative values
      @Override
      public final int nextInt () {
        return Math.abs(urp.nextInt()); }
      @Override
      public final Object next () { return nextInt(); } }; }

  public static final Generator
  intGenerator (final int n,
                final UniformRandomProvider urp) {
    return new GeneratorBase ("intGenerator:" + n) {
      final Generator g = intGenerator(urp);
      @Override
      public final Object next () {
        final int[] z = new int[n];
        for (int i=0;i<n;i++) { z[i] = g.nextInt(); }
        return z; } }; }

  //--------------------------------------------------------------

  public static final Generator
  positiveIntGenerator (final UniformRandomProvider urp) {
    return new GeneratorBase ("positiveIntGenerator") {
      @Override
      public final int nextInt () {
        // TODO: fix infinite loop?
        for (;;) {
          final int x = urp.nextInt();
          if (x != 0) { return Math.abs(x); } } }
      @Override
      public final Object next () { return nextLong(); } }; }

  public static final Generator
  positiveIntGenerator (final int n,
                        final UniformRandomProvider urp) {
    return new GeneratorBase ("positiveIntGenerator:" + n) {
      final Generator g = positiveLongGenerator(urp);
      @Override
      public final Object next () {
        final int[] z = new int[n];
        for (int i=0;i<n;i++) { z[i] = g.nextInt(); }
        return z; } }; }

  //--------------------------------------------------------------

  public static final Generator
  longGenerator (final UniformRandomProvider urp) {
    return new GeneratorBase ("longGenerator") {
      @Override
      public final long nextLong () { return urp.nextLong(); }
      @Override
      public final Object next () { return nextLong(); } }; }

  public static final Generator
  nonNegativeLongGenerator (final UniformRandomProvider urp) {
    return new GeneratorBase ("longGenerator") {
      // TODO: make this uniform over non-negative values
      @Override
      public final long nextLong () {
        return Math.abs(urp.nextLong()); }
      @Override
      public final Object next () { return nextLong(); } }; }

  public static final Generator
  longGenerator (final int n,
                 final UniformRandomProvider urp) {
    return new GeneratorBase ("longGenerator:" + n) {
      final Generator g = longGenerator(urp);
      @Override
      public final Object next () {
        final long[] z = new long[n];
        for (int i=0;i<n;i++) { z[i] = g.nextLong(); }
        return z; } }; }

  //--------------------------------------------------------------

  public static final Generator
  positiveLongGenerator (final UniformRandomProvider urp) {
    return new GeneratorBase ("positiveLongGenerator") {
      @Override
      public final long nextLong () {
        // TODO: fix infinite loop?
        for (;;) {
          final long x = urp.nextLong();
          if (x != 0L) { return Math.abs(x); } } }
      @Override
      public final Object next () { return nextLong(); } }; }

  public static final Generator
  positiveLongGenerator (final int n,
                         final UniformRandomProvider urp) {
    return new GeneratorBase ("positiveLongGenerator:" + n) {
      final Generator g = positiveLongGenerator(urp);
      @Override
      public final Object next () {
        final long[] z = new long[n];
        for (int i=0;i<n;i++) { z[i] = g.nextLong(); }
        return z; } }; }

  //--------------------------------------------------------------

  public static final byte[]
  nextBytes (final UniformRandomProvider urp,
             final int n) {
    final byte[] b = new byte[n];
    urp.nextBytes(b);
    return b; }

  //--------------------------------------------------------------
  /** Intended primarily for testing. <b>
   * @param nbytes generate a BigInteger with this many bytes
   */

  public static final Generator
  bigIntegerGenerator (final int nbytes,
                       final UniformRandomProvider urp) {
    final double dp = 0.99;
    return new GeneratorBase ("bigIntegerGenerator") {
      private final ContinuousSampler choose =
        new ContinuousUniformSampler(urp,0.0,1.0);
      private final CollectionSampler edgeCases =
        new CollectionSampler(
          urp,
          List.of(
            BigInteger.valueOf(0L),
            BigInteger.ONE,
            BigInteger.valueOf(2L),
            BigInteger.valueOf(10L)));
      @Override
      public Object next () {
        final boolean edge = choose.sample() > dp;
        if (edge) { return edgeCases.sample(); }
        return new BigInteger(nextBytes(urp,nbytes)); } }; }

  public static final Generator
  bigIntegerGenerator (final int nbytes,
                       final UniformRandomProvider urp,
                       final int nints) {
    return new GeneratorBase ("bigIntegerGenerator:" + nints) {
      final Generator g = bigIntegerGenerator(nbytes, urp);
      @Override
      public final Object next () {
        final BigInteger[] z = new BigInteger[nints];
        for (int i=0;i<nints;i++) { z[i] = (BigInteger) g.next(); }
        return z; } }; }

  //--------------------------------------------------------------
  /** Intended primarily for testing. <b>
   * @param nbytes generate a BigInteger with up to this many bytes
   */

  public static final Generator
  nonNegativeBigIntegerGenerator (final int nbytes,
                                  final UniformRandomProvider urp) {
    return new GeneratorBase ("nonNegativeBigIntegerGenerator") {
      // TODO: choose within a range, rather than number of bytes
      @Override
      public Object next () {
        // TODO: make this uniform over non-negative values
        return new BigInteger(nextBytes(urp,nbytes)).abs(); } }; }

  public static final Generator
  nonNegativeBigIntegerGenerator (final int nbytes,
                                  final UniformRandomProvider urp,
                                  final int nints) {
    return new GeneratorBase ("nonNegativeBigIntegerGenerator:" + nints) {
      final Generator g = nonNegativeBigIntegerGenerator(nbytes, urp);
      @Override
      public final Object next () {
        final BigInteger[] z = new BigInteger[nints];
        for (int i=0;i<nints;i++) { z[i] = (BigInteger) g.next(); }
        return z; } }; }

  //--------------------------------------------------------------
  /** Intended primarily for testing. <b>
   * @param nbytes generate a BigInteger with up to this many bytes
   */

  public static final Generator
  nonzeroBigIntegerGenerator (final int nbytes,
                              final UniformRandomProvider urp) {
    final double dp = 0.99;
    return new GeneratorBase ("nonzeroBigIntegerGenerator") {
      private final ContinuousSampler choose =
        new ContinuousUniformSampler(urp,0.0,1.0);
      private final CollectionSampler edgeCases =
        new CollectionSampler(
          urp,
          List.of(
            BigInteger.ONE,
            BigInteger.valueOf(2L),
            BigInteger.valueOf(10L)));
      @Override
      public Object next () {
        final boolean edge = choose.sample() > dp;
        if (edge) { return edgeCases.sample(); }
        for (int i=0;i<16;i++) {
          final BigInteger b =
            new BigInteger(nextBytes(urp,nbytes));
          if (0 != b.signum()) { return b; } }
        throw new IllegalStateException(
          "got too many pseudorandom zero BigIntegers!"); } }; }

  public static final Generator
  nonzeroBigIntegerGenerator (int nbytes,
                              final UniformRandomProvider urp,
                              final int nints) {
    return new GeneratorBase ("nonzeroBigIntegerGenerator:" + nints) {
      final Generator g = nonzeroBigIntegerGenerator(nbytes, urp);
      @Override
      public final Object next () {
        final BigInteger[] z = new BigInteger[nints];
        for (int i=0;i<nints;i++) { z[i] = (BigInteger) g.next(); }
        return z; } }; }

  //--------------------------------------------------------------
  /** Intended primarily for testing. <b>
   * @param nbytes generate a BigInteger with up to this many bytes
   */

  public static final Generator
  positiveBigIntegerGenerator (final int nbytes,
                               final UniformRandomProvider urp) {
    final double dp = 0.99;
    return new GeneratorBase ("positiveBigIntegerGenerator") {
      private final ContinuousSampler choose =
        new ContinuousUniformSampler(urp,0.0,1.0);
      private final CollectionSampler edgeCases =
        new CollectionSampler(
          urp,
          List.of(
            BigInteger.ONE,
            BigInteger.valueOf(2L),
            BigInteger.valueOf(10L)));
      @Override
      public Object next () {
        final boolean edge = choose.sample() > dp;
        if (edge) { return edgeCases.sample(); }
        // TODO: bound infinite loop?
        for (int i=0;i<16;i++) {
          final BigInteger b =
            new BigInteger(nextBytes(urp,nbytes)).abs();
          if (0 != b.signum()) { return b; } }
        throw new IllegalStateException(
          "got too many pseudorandom zero BigIntegers!"); } }; }

  public static final Generator
  positiveBigIntegerGenerator (final int nbytes,
                               final UniformRandomProvider urp,
                               final int nints) {
    return new GeneratorBase ("positiveBigIntegerGenerator:" + nints) {
      final Generator g = positiveBigIntegerGenerator(nbytes, urp);
      @Override
      public final Object next () {
        final BigInteger[] z = new BigInteger[nints];
        for (int i=0;i<nints;i++) {
          z[i] = (BigInteger) g.next(); }
        return z; } }; }

  //--------------------------------------------------------------
  // TODO: move this into BigFractions, then into test code,
  // until BigFraction passes reasonable tests, like round trip
  // invariance double -> BigFraction -> double
  // TODO: options?
  // TODO: using a DoubleSampler: those are (?) the most likely
  // values to see, but could do something to extend the
  // range to values not representable as double.
  // TODO: move all BigFraction dependent code into tests,
  // until it starts passing simple
  // double -> BigFraction -> double round trip tests,
  // correct rounding to nearest double, etc.

  /** Intended primarily for testing. Sample a random double
   * (see {@link Doubles#finiteGenerator})
   * and convert to <code>BigFraction</code>
   * with high probability probability;
   * otherwise return one of a set of edge case values
   * (eg <code>BigFraction.ZERO</code>,
   * with equal probability.
   */

  public static final Generator
  bigFractionGenerator (final UniformRandomProvider urp) {
    final double dp = 0.9;
    return new Generator () {
      private final ContinuousSampler choose =
        new ContinuousUniformSampler(urp,0.0,1.0);
      private final Generator fdg = Doubles.finiteGenerator(urp);
      private final CollectionSampler edgeCases =
        new CollectionSampler(
          urp,
          List.of(
            BigFraction.ZERO,
            BigFraction.ONE,
            BigFraction.MINUS_ONE));
      @Override
      public Object next () {
        final boolean edge = choose.sample() > dp;
        if (edge) { return edgeCases.sample(); }
        return new BigFraction(fdg.nextDouble()); } }; }

  public static final Generator
  bigFractionGenerator (final int n,
                        final UniformRandomProvider urp) {
    return new Generator () {
      final Generator g = bigFractionGenerator(urp);
      @Override
      public final Object next () {
        final BigFraction[] z = new BigFraction[n];
        for (int i=0;i<n;i++) { z[i] = (BigFraction) g.next(); }
        return z; } }; }

  //--------------------------------------------------------------

  private static final String SEED0 =
    "seeds/Well44497b-2019-01-05.txt";

  //  private static final String SEED1 =
  //    "seeds/Well44497b-2019-01-07.txt";

  public static final Map<String,IntFunction<Generator>>
    factories =
    Map.of(
      "exponential",
      dim -> {
        final UniformRandomProvider urp0 = PRNG.well44497b(SEED0);
        //final UniformRandomProvider urp1 = PRNG.well44497b(SEED1);
        final int emax = Doubles.deMax(dim)/2;
        final double dmax = (1<<emax);
        return
          //Doubles.shuffledGenerator(
          //Doubles.zeroSumGenerator(
          Doubles.exponentialGenerator(dim,urp0,0.0,dmax)
          //),
          //urp1)
          ; },
      "finite",
      dim -> {
        final UniformRandomProvider urp0 = PRNG.well44497b(SEED0);
        //final UniformRandomProvider urp1 = PRNG.well44497b(SEED1);
        final int emax = Doubles.deMax(dim)/2;
        return
          //Doubles.shuffledGenerator(
          //Doubles.zeroSumGenerator(
          Doubles.finiteGenerator(dim,urp0,emax)
          //),
          //urp1)
          ; },
      "gaussian",
      dim -> {
        final UniformRandomProvider urp0 = PRNG.well44497b(SEED0);
        //final UniformRandomProvider urp1 = PRNG.well44497b(SEED1);
        final int emax = Doubles.deMax(dim)/2;
        final double dmax = (1<<emax);
        return
          //Doubles.shuffledGenerator(
          //Doubles.zeroSumGenerator(
          Doubles.gaussianGenerator(dim,urp0,0.0,dmax)
          //),
          //urp1)
          ; },
      "laplace",
      dim -> {
        final UniformRandomProvider urp0 = PRNG.well44497b(SEED0);
        //final UniformRandomProvider urp1 = PRNG.well44497b(SEED1);
        final int emax = Doubles.deMax(dim)/2;
        final double dmax = (1<<emax);
        return
          //Doubles.shuffledGenerator(
          //Doubles.zeroSumGenerator(
          Doubles.laplaceGenerator(dim,urp0,0.0,dmax)
          //),urp1)
          ; },
      "uniform",
      dim -> {
        final UniformRandomProvider urp0 = PRNG.well44497b(SEED0);
        //final UniformRandomProvider urp1 = PRNG.well44497b(SEED1);
        final int emax = Doubles.deMax(dim)/2;
        final double dmax = (1<<emax);
        return
          //Doubles.shuffledGenerator(
          //Doubles.zeroSumGenerator(
          Doubles.uniformGenerator(dim,urp0,-dmax,dmax)
          //),
          //urp1)
          ; }
          );

  public static final Generator make (final String name,
                                      final int dim) {
    return factories.get(name).apply(dim); }

  //--------------------------------------------------------------
  // disable constructor
  //--------------------------------------------------------------

  private Generators () {
    throw new UnsupportedOperationException(
      "can't instantiate " + getClass()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------

