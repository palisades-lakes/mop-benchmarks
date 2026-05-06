package mop.java.numbers;

import mop.java.algebra.OneSetOneOperation;
import mop.java.algebra.OneSetTwoOperations;
import mop.java.algebra.Set;
import mop.java.prng.Generator;
import mop.java.prng.GeneratorBase;
import mop.java.prng.Generators;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.CollectionSampler;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

/** Natural numbers as a commutative semi-ring,
 * allowing a variety of implementations,
 * some only covering subsets.
 *
 * Implementations (eventually):
 * <ul>
 * <li> {@link BoundedNatural}
 * <li> {@link UnboundedNatural}
 * <li> <code>java.math.BigInteger</code> (only nonnegative)
 * <li> <code>java.lang.Long</code> (only nonnegative)
 * <li> <code>java.lang.Integer</code> (only nonnegative)
 * <li> ...
 * </ul>
 *
 * The code is unnecessarily complicated because
 * (1) java doesn't have true dynamic method lookup,
 * and (2) it's not possible to have an interface implemented by
 * both BigInteger and newly written classes.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2024-01-03
 */
@SuppressWarnings({"unchecked","static-method","preview","boxing"})
public final class Naturals implements Set {

  //--------------------------------------------------------------
  // utilities
  //--------------------------------------------------------------

  private static final BigInteger toBigInteger (final Object x) {
    return switch (x) {
    case Byte y -> BigInteger.valueOf(y.longValue()); 
    case Short y -> BigInteger.valueOf(y.longValue()); 
    case Integer y -> BigInteger.valueOf(y.longValue()); 
    case Long y -> BigInteger.valueOf(y.longValue()); 
    case BoundedNatural y -> y.toBigInteger();
    case BigInteger y -> y;
    default -> throw new UnsupportedOperationException(
      "can't convert " + x.getClass().getName() +
      " to BigInteger"); }; }

  private static final BoundedNatural toBoundedNatural (final Object x) {
    return switch (x)  {
      case BoundedNatural y -> y;
      case Byte y -> BoundedNatural.valueOf(y.longValue());
      case Short y -> BoundedNatural.valueOf(y.longValue());
      case Integer y -> BoundedNatural.valueOf(y.longValue());
      case Long y -> BoundedNatural.valueOf(y.longValue());
      case BigInteger y -> BoundedNatural.valueOf(y);
      default ->
        throw new UnsupportedOperationException(
          "can't convert " + x.getClass().getName() +
            " to BoundedNatural"); }; }

  //--------------------------------------------------------------
  // operations for algebraic structures over BigFloats.
  //--------------------------------------------------------------
  // TODO: How do (should) we handle unsigned interpretation of 
  // all of an int's bits?
  // TODO: cleaner handling of overflow to BigInteger or whatever,
  // especially how to control which larger class is returned?

  /** UNSAFE: Assumes all arguments are non-negative */

  private static final Object add (final Long y0,
                                   final Long y1) {

    try { return Math.addExact(y0,y1); }
    // TODO: return BigInteger or BoundedNatural or ?
    catch (final ArithmeticException e) {
      return toBigInteger(y0).add(toBigInteger(y1)); } }

  /** UNSAFE: Assumes all arguments are non-negative,
   * and Long, BigInteger, or BoundedNatural */

  private static final Object add (final Object x0,
                                   final Long y1) {

    return switch (x0) {
    case final Byte y0 -> add(y0.longValue(),y1);
    case final Short y0 -> add(y0.longValue(),y1);
    case final Integer y0 -> add(y0.longValue(),y1);
    case final Long y0 -> add(y0,y1); 
    case final BigInteger y0 -> y0.add(toBigInteger(y1));
    case final BoundedNatural y0 -> y0.add(toBoundedNatural(y1));
    default -> throw new UnsupportedOperationException(
      "can't add " + 
        x0.getClass().getName() + " and Long"); }; }

  //--------------------------------------------------------------

  public final Object add (final Object x0,
                           final Object x1) {
    assert contains(x0);
    assert contains(x1);
    return switch (x1) {
      case final BoundedNatural y1 -> y1.add(toBoundedNatural(x0));
      // reduce number of cases to implement by converting all
      // "primitive" numbers to Long.
      // TODO: profile to determine if it's worth keeping returned
      // values as int or smaller
      case final Byte y1 -> add(x0,y1.longValue());
      case final Short y1 -> add(x0,y1.longValue());
      case final Integer y1 -> add(x0,y1.longValue());
      case final Long y1 -> add(x0,y1);
      // TODO: these 2 cases return a result of the same type as the
      // first argument. will probably want to change that to return
      // the larger, which needs to be determined
      case final BigInteger y1 -> y1.add(toBigInteger(x0));
      default -> throw new UnsupportedOperationException(
        "can't add " +
          x0.getClass().getName() +
          " and " +
          x1.getClass().getName()); }; }

  //--------------------------------------------------------------

  public final BinaryOperator<Object> adder () {
    return new BinaryOperator<> () {
      @Override
      public final String toString () { return "Naturals.add()"; }
      @Override
      public final Object apply (final Object x0,
                                 final Object x1) {
        return Naturals.this.add(x0,x1); } }; }

  //--------------------------------------------------------------

  public final Object additiveIdentity () {
    return Integer.valueOf(0); }

  //--------------------------------------------------------------

  /** UNSAFE: Assumes all arguments are non-negative */

  private static final Object multiply (final Long y0,
                                        final Long y1) {

    try { return Math.multiplyExact(y0,y1); }
    // TODO: return BigInteger or BoundedNatural or ?
    catch (final ArithmeticException e) {
      return toBigInteger(y0).multiply(toBigInteger(y1)); } }

  /** UNSAFE: Assumes all arguments are non-negative,
   * and Long, BigInteger, or BoundedNatural */

  private static final Object multiply (final Object x0,
                                        final Long y1) {

    return switch (x0) {
      case final Byte y0 -> multiply(y0.longValue(),y1);
      case final Short y0 -> multiply(y0.longValue(),y1);
      case final Integer y0 -> multiply(y0.longValue(),y1);
      case final Long y0 -> multiply(y0,y1);
      case final BigInteger y0 -> y0.multiply(toBigInteger(y1));
      case final BoundedNatural y0 -> y0.multiply(toBoundedNatural(y1));
      default -> throw new UnsupportedOperationException(
      "can't multiply " + 
        x0.getClass().getName() + " and Long"); }; }

  //--------------------------------------------------------------

  public final Object multiply (final Object x0,
                                final Object x1) {
    assert contains(x0);
    assert contains(x1);
    return switch (x1) {
      // reduce number of cases to implement by converting all
      // "primitive" numbers to Long.
      // TODO: profile to determine if it's worth keeping returned
      // values as int or smaller
      case final Byte y1 -> multiply(x0,y1.longValue());
      case final Short y1 -> multiply(x0,y1.longValue());
      case final Integer y1 -> multiply(x0,y1.longValue());
      case final Long y1 -> multiply(x0,y1);
      // TODO: these 2 cases return a result of the same type as the
      // first argument. will probably want to change that to return
      // the larger, which needs to be determined
      case final BigInteger y1 -> y1.multiply(toBigInteger(x0));
      case final BoundedNatural y1 -> y1.multiply(toBoundedNatural(x0));
      default -> throw new UnsupportedOperationException(
        "can't multiply " +
          x0.getClass().getName() +
          " and " +
          x1.getClass().getName()); }; }

  //--------------------------------------------------------------

  public final BinaryOperator<Object> multiplier () {
    return new BinaryOperator<>() {
      @Override
      public final String toString () { return "Naturals.multiply"; }
      @Override
      public final Object apply (final Object x0,
                                 final Object x1) {
        return Naturals.this.multiply(x0,x1); } }; }

  //--------------------------------------------------------------
  public final Object multiplicativeIdentity () {
    return Integer.valueOf(1); }

  //--------------------------------------------------------------
  // non-ring arithmetic methods
  //--------------------------------------------------------------
  private static final Object absDiff (final Long y0,
                                       final Long y1) {

    try { return Math.abs(Math.subtractExact(y0,y1)); }
    // TODO: return BigInteger or BoundedNatural or ?
    catch (final ArithmeticException e) {
      return toBigInteger(y0).subtract(toBigInteger(y1)).abs(); } }

  /** UNSAFE: Assumes all arguments are non-negative,
   * and Long, BigInteger, or BoundedNatural */

  private static final Object absDiff (final Object x0,
                                       final Long y1) {

    return switch (x0) {
    case final Byte y0 -> absDiff(y0.longValue(),y1);
    case final Short y0 -> absDiff(y0.longValue(),y1);
    case final Integer y0 -> absDiff(y0.longValue(),y1);
    case final Long y0 -> absDiff(y0,y1); 
    case final BigInteger y0 -> y0.subtract(toBigInteger(y1)).abs();
    case final BoundedNatural y0 -> y0.absDiff(toBoundedNatural(y1));
    default -> throw new UnsupportedOperationException(
      "can't absDiff " + 
        x0.getClass().getName() + " and Long"); }; }

  //--------------------------------------------------------------

  public final Object absDiff (final Object x0,
                               final Object x1) {
    assert contains(x0);
    assert contains(x1);
    return switch (x1) {
    // reduce number of cases to implement by converting all
    // "primitive" numbers to Long.
    // TODO: profile to determine if it's worth keeping returned
    // values as int or smaller
    case final Byte y1 -> absDiff(x0,y1.longValue());
    case final Short y1 -> absDiff(x0,y1.longValue());
    case final Integer y1 -> absDiff(x0,y1.longValue());
    case final Long y1 -> absDiff(x0,y1);
    // TODO: these 2 cases return a result of the same type as the 
    // first argument. will probably want to change that to return
    // the larger, which needs to be determined
    case final BigInteger y1 -> y1.subtract(toBigInteger(x0)).abs();
    case final BoundedNatural y1 -> y1.absDiff(toBoundedNatural(x0));
    default -> throw new UnsupportedOperationException(
      "can't absDiff " + 
        x0.getClass().getName() +
        " and " +
        x1.getClass().getName()); }; }

  //--------------------------------------------------------------

  public final BinaryOperator<Object> absDiffer () {
    return new BinaryOperator<> () {
      @Override
      public final String toString () { return "Naturals.absDiff()"; }
      @Override
      public final Object apply (final Object x0,
                                 final Object x1) {
        return Naturals.this.absDiff(x0,x1); } }; }

  //--------------------------------------------------------------

  private static final Object[] divideAndRemainder (final Long y0,
                                                    final Long y1) {
    return new Long[] { y0/y1, y0%y1 }; }

  private static final Object[] divideAndRemainder (final Object x0,
                                                    final Long y1) {

    return switch (x0) {
    case final Byte y0 -> divideAndRemainder(y0.longValue(),y1);
    case final Short y0 -> divideAndRemainder(y0.longValue(),y1);
    case final Integer y0 -> divideAndRemainder(y0.longValue(),y1);
    case final Long y0 -> divideAndRemainder(y0,y1);
    case final BigInteger y0 -> y0.divideAndRemainder(toBigInteger(y1));
    case final BoundedNatural y0 -> y0.divideAndRemainder(toBoundedNatural(y1));
    default -> throw new UnsupportedOperationException(
      "can't divideAndRemainder " +
        x0.getClass().getName() + " and Long"); }; }

  //--------------------------------------------------------------
  /** doesn't pre-check that args are Naturals. */

  public final Object[]
  divideAndRemainderUnsafe (final Object x0,
                            final Object x1) {
    return switch (x1) {
      case final BoundedNatural y1 -> divideAndRemainder(toBoundedNatural(x0),y1);
      // reduce number of cases to implement by converting all
      // "primitive" numbers to Long.
      // TODO: profile to determine if it's worth keeping returned
      // values as int or smaller
      case final Byte y1 -> divideAndRemainder(x0,y1.longValue());
      case final Short y1 -> divideAndRemainder(x0,y1.longValue());
      case final Integer y1 -> divideAndRemainder(x0,y1.longValue());
      case final Long y1 -> divideAndRemainder(x0,y1);
      // TODO: these 2 cases return a result of the same type as the
      // first argument. will probably want to change that to return
      // the larger, which needs to be determined
      case final BigInteger y1 -> toBigInteger(x0).divideAndRemainder(y1);
      default -> throw new UnsupportedOperationException(
        "can't divideAndRemainder " +
          x0.getClass().getName() +
          " and " +
          x1.getClass().getName()); }; }

  //--------------------------------------------------------------

  public final Object[]
  divideAndRemainder (final Object x0,
                      final Object x1) {
    assert contains(x0);
    assert contains(x1);
    return divideAndRemainderUnsafe(x0,x1); }

  //--------------------------------------------------------------

  public final BinaryOperator<Object> divideAndRemainderer () {
    return new BinaryOperator<> () {
      @Override
      public final String toString () {
        return "Naturals.divideAndRemainder()"; }
      @Override
      public final Object apply (final Object x0,
                                 final Object x1) {
        return Naturals.this.divideAndRemainder(x0,x1); } }; }

  //--------------------------------------------------------------
  // Set methods
  //--------------------------------------------------------------

  @Override
  public final boolean contains (final Object x) {
    return switch (x) {
    case final BoundedNatural y -> true;
    case final Integer y -> y>=0;
    case final Long y -> y>=0;
    case final Short y -> y>=0;
    case final Byte y -> y>=0;
    // TODO: is signum() better for all the integer classes?
    // TODO: might be useful to define signum/isNegative/... for
    // all Number classes.
    case final BigInteger y -> y.signum()>=0;
    default -> throw new UnsupportedOperationException(); }; }

  //--------------------------------------------------------------
  // Unfortunately, it looks like Number.equals() is only true
  // for args of the same class, rather than same value.
  // Coercing "primitive" numbers to Long to save code.
  // TODO: profile to check if retaining smaller numbers helps.

  /** Test for equal value as Natural numbers.
   *
   * UNSAFE: Assumes all arguments are non-negative,
   * and "primitive".
   */
  private static final boolean equals (final Object x0,
                                       final Long y1) {
    return switch (x0) {
    case final Byte y0 -> equals(y0.longValue(),y1);
    case final Short y0 -> equals(y0.longValue(),y1);
    case final Integer y0 -> equals(y0.longValue(),y1);
    case final Long y0 -> y0.equals(y1);
    case final BigInteger y0 -> y0.equals(toBigInteger(y1));
    case final BoundedNatural y0 -> y0.equals(toBoundedNatural(y1));
    default -> throw new UnsupportedOperationException(); }; }

  /** Test for equal values as Natural numbers. */

  private final boolean equals (final Object x0,
                                final Object x1) {
    assert contains(x0);
    assert contains(x1);
    return switch (x1) {
    case final Byte y1 -> equals(x0,y1.longValue());
    case final Short y1 -> equals(x0,y1.longValue());
    case final Integer y1 -> equals(x0,y1.longValue());
    case final Long y1 -> equals(x0,y1);
    case final BigInteger y1 -> y1.equals(toBigInteger(x0));
    case final BoundedNatural y1 -> y1.equals(toBoundedNatural(x0));
    default ->
    throw new UnsupportedOperationException(
      x0.getClass().getName() + " " + x1.getClass().getName()); }; }

  //--------------------------------------------------------------

  /** Test for equal value as Natural numbers. */

  @Override
  public final BiPredicate equivalence () {
    return new BiPredicate<Object,Object>() {
      @Override
      public final boolean test (final Object x0,
                                 final Object x1) {
        return get().equals(x0,x1); } }; }

  //--------------------------------------------------------------

  public static final Generator
  generator (final UniformRandomProvider urp)  {
    //    final Generator g3 =
    //      randomBitsGenerator (1L+BoundedNatural.MAX_WORDS,urp);
    final CollectionSampler gs =
      new CollectionSampler(urp,List.of(
        BoundedNatural.generator(urp,2048),
        Generators.nonNegativeBigIntegerGenerator(1024, urp),
        Generators.nonNegativeByteGenerator(urp),
        Generators.nonNegativeShortGenerator(urp),
        Generators.nonNegativeIntGenerator(urp),
        Generators.nonNegativeLongGenerator(urp)));
    return new GeneratorBase ("NaturalsGenerator") {
      @Override
      public final Object next () {
        final Generator g = ((Generator) gs.sample());
        return g.next();} }; }

  // TODO: determine which generator from options.
  @Override
  public final Supplier generator (final Map options) {
    final UniformRandomProvider urp = Set.urp(options);
    final Generator g = generator(urp);
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
    return that instanceof Naturals; }

  @Override
  public final String toString () { return "Naturals"; }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private Naturals () { }

  private static final Naturals SINGLETON = new Naturals();

  public static final Naturals get () { return SINGLETON; }

  //--------------------------------------------------------------
  // TODO: fill in multiply methods for the commutative semi-ring

  public static final OneSetOneOperation ADDITION_MONOID =
    OneSetOneOperation.commutativeMonoid(
      get().adder(),
      get(),
      get().additiveIdentity());

  public static final OneSetOneOperation MULTIPLICATIVE_MAGMA =
    OneSetOneOperation.magma(get().multiplier(),get());

  public static final OneSetTwoOperations RING =
    OneSetTwoOperations.commutativeSemiring(
      get().adder(),
      get().additiveIdentity(),
      get().multiplier(),
      get().multiplicativeIdentity(),
      get());

  //--------------------------------------------------------------
}
//--------------------------------------------------------------

