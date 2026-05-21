package mop.java.numbers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.CollectionSampler;

import mop.java.Exceptions;
import mop.java.algebra.Set;
import mop.java.prng.Generator;
import mop.java.prng.GeneratorBase;
import mop.java.prng.Generators;

/** Utilities for Object and primitive numbers.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-21
 */
@SuppressWarnings({"unchecked","unused"})
public final class Numbers implements Set {

  /** <code>(int &amp; UNSIGNED_MASK)</code>
   * returns <code>long<code> containing <code>unsigned int</code>
 . */
  private static final long UNSIGNED_MASK = 0xFFFFFFFFL;

  static final long unsigned (final int i) { return i & UNSIGNED_MASK; }

  static final long loWord (final long i) { return i & UNSIGNED_MASK; }

  static final long hiWord (final long i) { return i >>> 32; }

  //--------------------------------------------------------------
//  /** Like {@link Arrays#toString(double[])}. */
//
//  public static final String toHexString (final double[] x) {
//    if (null == x) { return "null"; }
//    final StringBuilder b = new StringBuilder("[");
//    if (x.length > 0) { b.append(Double.toHexString(x[0])); }
//    for (int i=1;i<x.length;i++) {
//      // Arrays.toString wastes space with commas
//      //b.append(",");
//      b.append(" "); b.append(Double.toHexString(x[i])); }
//    return b.append("]").toString(); }

//  public static final String toHexString (final int[] x) {
//    if (null == x) { return "null"; }
//    final StringBuilder b = new StringBuilder("[");
//    if (x.length > 0) { b.append(Integer.toHexString(x[0])); }
//    for (int i=1;i<x.length;i++) {
//      // Arrays.toString wastes space with commas
//      //b.append(",");
//      b.append(" "); b.append(Integer.toHexString(x[i])); }
//    return b.append("]").toString(); }

//  public static final String toHexString (final long[] x) {
//    if (null == x) { return "null"; }
//    final StringBuilder b = new StringBuilder("[");
//    if (x.length > 0) { b.append(Long.toHexString(x[0])); }
//    for (int i=1;i<x.length;i++) {
//      // Arrays.toString wastes space with commas
//      //b.append(",");
//      b.append(" "); b.append(Long.toHexString(x[i])); }
//    return b.append("]").toString(); }

//  public static final String toHexString (final long x) {
//    return Long.toHexString(x); }
//
//  public static final String toHexString (final int x) {
//    return Integer.toHexString(x); }
//
//  public static final String toHexString (final double x) {
//    return Double.toHexString(x); }
//
//  public static final String toHexString (final BigInteger x) {
//    return x.toString(0x10); }
//
  //--------------------------------------------------------------
  // useful class methods
  //--------------------------------------------------------------
  // Useful for passing as a Function method reference

  public static final double doubleValue (final Object x) {
    if (x instanceof Hilo) {
      return ((Hilo) x).doubleValue(); }
    if (x instanceof BigFloat) {
      return ((BigFloat) x).doubleValue(); }
    if (x instanceof RationalFloat) {
      return ((RationalFloat) x).doubleValue(); }
    if (x instanceof BoundedNatural) {
      return ((BoundedNatural) x).doubleValue(); }
    if (x instanceof Number) {
      return ((Number) x).doubleValue(); }
    throw Exceptions.unsupportedOperation(x,"doubleValue",x); }

  public static float floatValue (final Object x) {
    if (x instanceof Hilo) {
      return ((Hilo) x).floatValue(); }
    if (x instanceof BigFloat) {
      return ((BigFloat) x).floatValue(); }
    if (x instanceof RationalFloat) {
      return ((RationalFloat) x).floatValue(); }
    if (x instanceof BoundedNatural) {
      return ((BoundedNatural) x).floatValue(); }
    if (x instanceof Number) {
      return ((Number) x).floatValue(); }
    throw Exceptions.unsupportedOperation(x,"floatValue",x); }

  //--------------------------------------------------------------

  /** inclusive */
  public static final int loBit (final BigInteger i) {
    return i.getLowestSetBit(); }

  /** exclusive */
  public static final int hiBit (final BigInteger i) {
    return i.bitLength(); }

  /** inclusive */
  public static final int loBit (final BoundedNatural i) {
    return i.loBit(); }

  /** exclusive */
  public static final int hiBit (final BoundedNatural i) {
    return i.hiBit(); }

  /** inclusive */
  public static final int loBit (final int i) {
    return Integer.numberOfTrailingZeros(i)&0x1F; }

  /** exclusive */
  public static final int hiBit (final int i) {
    return Integer.SIZE -  Integer.numberOfLeadingZeros(i); }

  /** inclusive */
  public static final int loBit (final long i) {
    return Long.numberOfTrailingZeros(i)&0x3F; }

  /** exclusive */
  public static final int hiBit (final long i) {
    return Long.SIZE-Long.numberOfLeadingZeros(i); }

  //--------------------------------------------------------------

  public static final boolean isZero (final double x) {
    return 0.0 == x; }

  public static final boolean isZero (final float x) {
    return 0.0F == x; }

  public static final boolean isZero (final byte x) {
    return 0 == x; }

  public static final boolean isZero (final short x) {
    return 0 == x; }

  public static final boolean isZero (final int x) {
    return 0 == x; }

  public static final boolean isZero (final long x) {
    return 0L == x; }

  public static final boolean isZero (final BigInteger x) {
    return 0 == x.signum(); }

  public static final boolean isZero (final BigDecimal x) {
    return 0 == BigDecimal.ZERO.compareTo(x); }

  public static final boolean isZero (final Number x) {
    if (x instanceof BigInteger) { return isZero((BigInteger) x); }
    if (x instanceof BigDecimal) { return isZero((BigDecimal) x); }
    if (x instanceof Double) { return isZero(x.doubleValue()); }
    if (x instanceof final Float v) { return isZero(v.floatValue()); }
    if (x instanceof final Integer i) { return isZero(i.intValue()); }
    if (x instanceof final Long l) { return isZero(l.longValue()); }
    if (x instanceof final Byte b) { return isZero(b.byteValue()); }
    if (x instanceof final Short i) { return isZero(i.shortValue()); }
    throw Exceptions.unsupportedOperation(x,"isZero",x); }

  public static final boolean isZero (final Object x) {
    if (x instanceof Ringlike) { return ((Ringlike) x).isZero(); }
    if (x instanceof Number) { return isZero((Number) x); }
    throw Exceptions.unsupportedOperation(x,"valueOf",x); }

  //--------------------------------------------------------------

  public static final String description (final String name,
                                          final BigInteger i) {

    return name + "[lo,hi)=" +
      loBit(i) + "," +
      hiBit(i) + ")" +
      " : " + i.toString(0x10); }

  public static final String description (final String name,
                                          final int i) {

    return name + " = " +
      Integer.toHexString(i) +
      "; " + i +
      "\n" + "lo,hi bits= [" +
      loBit(i) + "," +
      hiBit(i) + ")"; }

  public static final String description (final String name,
                                          final long i) {

    return name + " = " +
      Long.toHexString(i) + "; " +
      i + "\n" +
      "lo,hi bits= [" + loBit(i) +
      "," + hiBit(i) + ")"; }

  //--------------------------------------------------------------
  // Set methods
  //--------------------------------------------------------------

  @Override
  public final boolean contains (final Object element) {
    return element instanceof Number; }

  @Override
  public final boolean contains (final float element) {
    return true; }

  @Override
  public final boolean contains (final double element) {
    return true; }

  @Override
  public final boolean contains (final byte element) {
    return true; }

  @Override
  public final boolean contains (final short element) {
    return true; }

  @Override
  public final boolean contains (final int element) {
    return true; }

  @Override
  public final boolean contains (final long element) {
    return true; }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------

  @Override
  public final int hashCode () { return 0; }

  // singleton
  @Override
  public final boolean equals (final Object that) {
    return that instanceof Numbers; }

  @Override
  public final String toString () { return "D"; }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  public static final Generator
  finiteNumberGenerator (final int n,
                         final UniformRandomProvider urp) {
    return new GeneratorBase ("finiteNumberGenerator:" + n) {
      final Generator g = finiteNumberGenerator(urp);
      @Override
      public final Object next () {
        final Object[] z = new Object[n];
        for (int i=0;i<n;i++) { z[i] = g.next(); }
        return z; } }; }

  /** Intended primarily for testing. Pick a number generator
   * from a list and then sample that type.
   */

  public static final Generator
  finiteNumberGenerator (final UniformRandomProvider urp) {
    return new GeneratorBase ("finiteNumberGenerator") {
      private final CollectionSampler<Generator> generators =
        new CollectionSampler(
          urp,
          List.of(
            Generators.byteGenerator(urp),
            Generators.shortGenerator(urp),
            Generators.intGenerator(urp),
            Generators.longGenerator(urp),
            Floats.finiteGenerator(urp),
            Doubles.finiteGenerator(urp),
            Generators.bigIntegerGenerator(1024, urp),
            //bigDecimalGenerator(urp),
            RationalFloats.generator(urp)));
      @Override
      public final Object next () {
        return generators.sample().next(); } }; }

  public static final Generator
  numberGenerator (final int n,
                   final UniformRandomProvider urp) {
    return new GeneratorBase ("numberGenerator:" + n) {
      final Generator g = numberGenerator(urp);
      @Override
      public final Object next () {
        final Number[] z = new Number[n];
        for (int i=0;i<n;i++) { z[i] = (Number) g.next(); }
        return z; } }; }

  public static final Generator
  numberGenerator (final UniformRandomProvider urp) {
    return new GeneratorBase ("numberGenerator") {
      private final CollectionSampler<Generator> generators =
        new CollectionSampler(
          urp,
          List.of(
            Generators.byteGenerator(urp),
            Generators.shortGenerator(urp),
            Generators.intGenerator(urp),
            Generators.longGenerator(urp),
            Floats.generator(urp),
            Doubles.generator(urp),
            Generators.bigIntegerGenerator(1024, urp),
            //bigDecimalGenerator(urp),
            RationalFloats.generator(urp)
            ));
      @Override
      public final Object next () {
        return generators.sample().next(); } }; }

  private Numbers () { }

  private static final Numbers SINGLETON = new Numbers();

  public static final Numbers get () { return SINGLETON; }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------

