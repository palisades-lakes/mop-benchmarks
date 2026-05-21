package mop.java.numbers;

import mop.java.algebra.OneSetOneOperation;
import mop.java.algebra.Set;
import mop.java.algebra.TwoSetsOneOperation;
import mop.java.prng.Generator;
import mop.java.prng.GeneratorBase;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.CollectionSampler;
import org.apache.commons.rng.sampling.distribution.ContinuousSampler;
import org.apache.commons.rng.sampling.distribution.ContinuousUniformSampler;

import java.util.List;
import java.util.Map;
import java.util.function.*;

/** The set of arbitrary (high, not really arbitrary) precision
 * floating point numbers
 * represented by <code>XDouble</code>.
 * <br>
 * With the current operations, this is a floating linear space,
 * meaning: commutative addition with an identity and inverse,
 * and scalar multiplication by <code>double</code> floating
 * point, so not associative or distributive over addition.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-21
 */
@SuppressWarnings({"unchecked","static-method","unused"})
public final class XDoubles implements Set {

  //--------------------------------------------------------------
  // operations for algebraic structures over XDoubles.
  //--------------------------------------------------------------

    public final BinaryOperator<XDouble> adder () {
    return new BinaryOperator<> () {
      @Override
      public final String toString () { return "XDouble.add()"; }
      @Override
      public final XDouble apply (final XDouble q0, final XDouble q1) {
        return q0.add(q1); } }; }

  //--------------------------------------------------------------

  public final XDouble additiveIdentity () { return XDouble.ZERO; }

  //--------------------------------------------------------------
  public final UnaryOperator<XDouble> additiveInverse () {
    return new UnaryOperator<> () {
      @Override
      public final String toString () { return "XDouble.negate()"; }
      @Override
      public final XDouble apply (final XDouble q) {
        return q.negate(); } }; }

  //--------------------------------------------------------------
  // scalar multiplication
  //--------------------------------------------------------------

  public final BiFunction<XDouble,Double,XDouble> scaler () {

    return new BiFunction<> () {
      @Override
      public final String toString () { return "XDouble.scale()"; }
      @Override
      public final XDouble apply (final XDouble q, final Double s) {
        return q.scale(s); } }; }

  //--------------------------------------------------------------
  // Set methods
  //--------------------------------------------------------------

  @Override
  public final boolean contains (final Object element) {
    return element instanceof XDouble; }

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
    return (BiPredicate<XDouble, XDouble>) XDouble::equals; }

  //--------------------------------------------------------------

//  public static final Generator
//  fromBigIntegerGenerator (final UniformRandomProvider urp,
//                           final int eMin,
//                           final int eMax) {
//    //assert eMin<eMax;
//    final int eRan = eMax-eMin;
//    final double dp = 0.9;
//    return new GeneratorBase ("fromBigIntegerGenerator") {
//      private final ContinuousSampler choose =
//        new ContinuousUniformSampler(urp,0.0,1.0);
//      private final Generator g0 =
//        Generators.bigIntegerGenerator(1024, urp);
//      private final CollectionSampler edgeCases =
//        new CollectionSampler(
//          urp,
//          List.of(
//            XDouble.valueOf(0L),
//            XDouble.valueOf(1L),
//            XDouble.valueOf(2L),
//            XDouble.valueOf(10L),
//            XDouble.valueOf(-1L)));
//      @Override
//      public Object next () {
//        final boolean edge = choose.sample() > dp;
//        if (edge) { return edgeCases.sample(); }
//        final BigInteger bi = (BigInteger) g0.next();
//        final boolean nonNegative = (0 <= bi.signum());
//        final BoundedNatural significand =
//          BoundedNatural.valueOf(nonNegative ? bi : bi.negate());
//        final int exponent = urp.nextInt(eRan) + eMin;
//        return
//          XDouble.valueOf(nonNegative,significand,exponent); } }; }
//
//  public static final Generator
//  fromBigIntegerGenerator (final UniformRandomProvider urp) {
//    // default bounds allow multiply within int exponent range.
//    return
//      fromBigIntegerGenerator(
//        urp,Integer.MIN_VALUE/2,Integer.MAX_VALUE/2); }

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
            XDouble.valueOf(0L),
            XDouble.valueOf(1L),
            XDouble.valueOf(2L),
            XDouble.valueOf(10L),
            XDouble.valueOf(-1L)));
      @Override
      public Object next () {
        final boolean edge = choose.sample() > dp;
        if (edge) { return edgeCases.sample(); }
        return XDouble.valueOf(g.nextDouble()); } }; }

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
        final XDouble[] z = new XDouble[n];
        for (int i=0;i<n;i++) { z[i] = (XDouble) g.next(); }
        return z; } }; }

  public static final Generator
  generator (final int n,
             final UniformRandomProvider urp) {
    return new GeneratorBase ("rationalGenerator:" + n) {
      final Generator g = generator(urp);
      @Override
      public final Object next () {
        final XDouble[] z = new XDouble[n];
        for (int i=0;i<n;i++) { z[i] = (XDouble) g.next(); }
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
    return that instanceof XDoubles; }

  @Override
  public final String toString () { return "XDoubles"; }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private XDoubles () { }

  private static final XDoubles SINGLETON = new XDoubles();

  public static final XDoubles get () { return SINGLETON; }

  //--------------------------------------------------------------

  public static final OneSetOneOperation ADDITIVE_MAGMA =
    OneSetOneOperation.magma(get().adder(),get());

  public static final TwoSetsOneOperation FLOATING_POINT_SPACE =
    TwoSetsOneOperation.floatingPointSpace(
      get().scaler(),get(),Doubles.get());

  //--------------------------------------------------------------
}
//--------------------------------------------------------------

