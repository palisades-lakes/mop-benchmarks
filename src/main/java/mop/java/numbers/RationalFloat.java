package mop.java.numbers;

import mop.java.Exceptions;

import java.math.BigInteger;
import java.util.Objects;

import static mop.java.numbers.Numbers.hiBit;

/** Representing a rational number as a sign times a ratio of
 * {@link BoundedNatural} numbers times 2 to a <code>int</code> exponent.
 * <br>
 * The idea is that most data will start as <code>double</code>;
 * extracting the resulting powers of 2 from the numerator and
 * denominator should keep the RationalFLoats smaller, and make
 * arithmetic on them faster.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */


public final class RationalFloat
implements Ringlike<RationalFloat> {

  //--------------------------------------------------------------
  // instance fields and methods
  //--------------------------------------------------------------

  private final boolean _nonNegative;
  public final boolean nonNegative () { return _nonNegative; }

  private final BoundedNatural _numerator;
  public final BoundedNatural numerator () { return _numerator; }

  private final BoundedNatural _denominator;
  public final BoundedNatural denominator () { return _denominator; }

  private final int _exponent;
  public final int exponent () { return _exponent; }

  //--------------------------------------------------------------

  @Override
  public final boolean isZero () { return numerator().isZero(); }

  private static final boolean isOne (final BoundedNatural n,
                                      final BoundedNatural d) {
    return n.equals(d); }

  @Override
  public final boolean isOne () {
    return isOne(numerator(),denominator()); }

  //--------------------------------------------------------------

  @Override
  public final RationalFloat negate () {
    if (isZero()) { return this; }
    return
      valueOf(
        !nonNegative(),numerator(),denominator(),exponent()); }

  @Override
  public final RationalFloat abs () {
    if (nonNegative()) { return this; }
    return negate(); }

  public final RationalFloat reciprocal () {
    //assert !(numerator().isZero());
    return valueOf(
      nonNegative(),
      denominator(),
      numerator(),
      -exponent()); }

  //--------------------------------------------------------------

  private static final RationalFloat add6 (final boolean p0,
                                           final BoundedNatural t0,
                                           final int e0,
                                           final boolean p1,
                                           final BoundedNatural t1,
                                           final int e1) {
    if (e0<e1) { return add6(p1,t1,e1,p0,t0,e0); }
    final int de = e0-e1;
    if (p0!=p1) {
      // different signs
      final BoundedNatural t0s = (de>0) ? t0.shiftUp(de) : t0;
      final int c01 = t0s.compareTo(t1);
      // t1 > t0s
      if (0>c01) { return valueOf(p1,t1.subtract(t0s),e1); }
      // t0s > t1
      if (0<c01) { return valueOf(p0,t0s.subtract(t1),e1); }
      return ZERO; }
    // same signs
    if (0<de) { return valueOf(p0,t1.add(t0,de),e1);}
    return valueOf(p0,t0.add(t1),e1); }

  //--------------------------------------------------------------

  private static final RationalFloat add7 (final boolean p0,
                                           final BoundedNatural n0,
                                           final BoundedNatural d0,
                                           final int e0,
                                           final boolean p1,
                                           final BoundedNatural n1,
                                           final int e1) {
    final BoundedNatural n0d1 = n0;
    final BoundedNatural n1d0 = n1.multiply(d0);

    final BoundedNatural a;
    final BoundedNatural b;
    final int e;
    if (e0 == e1) {
      a = n0d1;
      b = n1d0;
      e = e0; }
    else if (e0 > e1) {
      a = n0d1.shiftUp(e0-e1);
      b = n1d0;
      e = e1; }
    else {
      a = n0d1;
      b = n1d0.shiftUp(e1-e0);
      e = e0; }

    final boolean p;
    final BoundedNatural n;
    if (p0) {
      if (p1) { n = a.add(b); p = true; }
      else {
        final int c = a.compareTo(b);
        if (0 <= c) { n = a.subtract(b); p = true; }
        else { n = b.subtract(a); p = false; } } }
    else {
      if (p1) {
        final int c = b.compareTo(a);
        if (0 <= c) { n = b.subtract(a); p = true; }
        else { n = a.subtract(b); p = false; } }
      else { n = a.add(b); p = false; } }

    final BoundedNatural d = d0;
    return valueOf(p,n,d,e); }

  private static final RationalFloat add8 (final boolean p0,
                                           final BoundedNatural n0,
                                           final BoundedNatural d0,
                                           final int e0,
                                           final boolean p1,
                                           final BoundedNatural n1,
                                           final BoundedNatural d1,
                                           final int e1) {
    final BoundedNatural n0d1 = n0.multiply(d1);
    final BoundedNatural n1d0 = n1.multiply(d0);

    final BoundedNatural a;
    final BoundedNatural b;
    final int e;
    if (e0 == e1) {
      a = n0d1;
      b = n1d0;
      e = e0; }
    else if (e0 > e1) {
      a = n0d1.shiftUp(e0-e1);
      b = n1d0;
      e = e1; }
    else {
      a = n0d1;
      b = n1d0.shiftUp(e1-e0);
      e = e0; }

    final boolean p;
    final BoundedNatural n;
    if (p0) {
      if (p1) { n = a.add(b); p = true; }
      else {
        final int c = a.compareTo(b);
        if (0 <= c) { n = a.subtract(b); p = true; }
        else { n = b.subtract(a); p = false; } } }
    else {
      if (p1) {
        final int c = b.compareTo(a);
        if (0 <= c) { n = b.subtract(a); p = true; }
        else { n = a.subtract(b); p = false; } }
      else { n = a.add(b); p = false; } }

    final BoundedNatural d = d0.multiply(d1);
    return valueOf(p,n,d,e); }

  //--------------------------------------------------------------

  private final RationalFloat add4 (final boolean p1,
                                    final BoundedNatural n1,
                                    final BoundedNatural d1,
                                    final int e1) {
    final boolean p0 = nonNegative();
    final BoundedNatural n0 = numerator();
    final BoundedNatural d0 = denominator();
    final int e0 = exponent();
    if (d0.isOne()) {
      if (d1.isOne()) { return add6(p0,n0,e0,p1,n1,e1); }
      return add7(p1,n1,d1,e1,p0,n0,e0); }
    if (d1.isOne()) { return add7(p0,n0,d0,e0,p1,n1,e1); }
    return add8(p0,n0,d0,e0,p1,n1,d1,e1); }

  //--------------------------------------------------------------

  @Override
  public final RationalFloat add (final RationalFloat that) {
    //if (isZero()) { return that; }
    //if (that.isZero()) { return this; }
    final boolean p0 = nonNegative();
    final BoundedNatural n0 = numerator();
    final BoundedNatural d0 = denominator();
    final int e0 = exponent();
    final boolean p1 = that.nonNegative();
    final BoundedNatural n1 = that.numerator();
    final BoundedNatural d1 = that.denominator();
    final int e1 = that.exponent();
    if (d0.isOne()) {
      if (d1.isOne()) { return add6(p0,n0,e0,p1,n1,e1); }
      return add7(p1,n1,d1,e1,p0,n0,e0); }
    if (d1.isOne()) { return add7(p0,n0,d0,e0,p1,n1,e1); }
    return add8(p0,n0,d0,e0,p1,n1,d1,e1); }

  //--------------------------------------------------------------

  private static final RationalFloat add7 (final boolean p0,
                                           final BoundedNatural n0,
                                           final BoundedNatural d0,
                                           final int e0,
                                           final boolean p1,
                                           final long n1,
                                           final int e1) {
    //if (d0.isOne()) { return add6(p0,n0,e0,p1,n1,e1); }
    final BoundedNatural n1d0 = NaturalMultiply.multiply(d0,n1);

    final BoundedNatural a;
    final BoundedNatural b;
    final int e;
    if (e0 == e1) {
      a = n0;
      b = n1d0;
      e = e0; }
    else if (e0 > e1) {
      a = n0.shiftUp(e0-e1);
      b = n1d0;
      e = e1; }
    else {
      a = n0;
      b = n1d0.shiftUp(e1-e0);
      e = e0; }

    final boolean p;
    final BoundedNatural n;
    if (p0) {
      if (p1) { n = a.add(b); p = true; }
      else {
        final int c = a.compareTo(b);
        if (0 <= c) { n = a.subtract(b); p = true; }
        else { n = b.subtract(a); p = false; } } }
    else {
      if (p1) {
        final int c = b.compareTo(a);
        if (0 <= c) { n = b.subtract(a); p = true; }
        else { n = a.subtract(b); p = false; } }
      else { n = a.add(b); p = false; } }

    return valueOf(p,n,d0,e); }

  //--------------------------------------------------------------
  // 0 upShift

  private static final RationalFloat add5 (final boolean p0,
                                           final BoundedNatural t0,
                                           final boolean p1,
                                           final long t1,
                                           final int e) {
    //assert 0L<=t1;
    if (p0==p1) { return valueOf(p0,t0.add(t1),e); }
    // different signs
    final int c = t0.compareTo(t1);
    // t0>t1
    if (0<c) { return valueOf(p0,t0.subtract(t1),e); }
    // t1>t0
    if (0>c) { return valueOf(p1,t0.subtractFrom(t1),e); }
    return ZERO; }

  //--------------------------------------------------------------
  /** both denominators 1.
   * significands adjusted to the same exponent.
   * 2nd arg starts as <code>double</code>.
   */

  private static final RationalFloat add6 (final boolean p0,
                                           final BoundedNatural t0,
                                           final boolean p1,
                                           final long t1,
                                           final int upShift,
                                           final int e) {
    //assert 0L<t1;
    //assert 0<=upShift
    if (p0==p1) { return valueOf(p0,t0.add(t1,upShift),e); }
    final int c = t0.compareTo(t1,upShift);
    if (0<c) { return valueOf(p0,t0.subtract(t1,upShift),e); }
    if (0>c) { return valueOf(p1,t0.subtractFrom(t1,upShift),e); }
    return ZERO; }

  //--------------------------------------------------------------

  private final RationalFloat add3 (final boolean p1,
                                    final long t11,
                                    final int e11) {
    //if (0 == t11) { return this; }
    //assert 0L<t11;
    //if (isZero()) { return valueOf(p1,t11,e11); }

    final boolean p0 = nonNegative();
    final BoundedNatural n0 = numerator();
    final BoundedNatural d0 = denominator();
    final int e0 = exponent();

    // minimize long bits
    final int shift = Numbers.loBit(t11);
    final long t1 = (t11>>>shift);
    final int e1 = e11+shift;

    if (d0.isOne()) {
      if (e0<e1) { return add6(p0,n0,p1,t1,e1-e0,e0); }
      if (e0==e1) { return add5(p0,n0,p1,t1,e0); }
      return add5(p0,n0.shiftUp(e0-e1),p1,t1,e1); }

    return add7(p0,n0,d0,e0,p1,t1,e1); }

  //--------------------------------------------------------------

  public final RationalFloat add (final double z) {
    //assert Double.isFinite(z);
    // escape on zero needed for add()
    if (0.0==z) { return this; }
    return add3(
      Doubles.nonNegative(z),
      Doubles.significand(z),
      Doubles.exponent(z));}

  public final RationalFloat
  addAll (final double[] z) {
    //assert Double.isFinite(z);
    RationalFloat s = this;
    for (final double zi : z) { s = s.add(zi); }
    return s; }

  //--------------------------------------------------------------

  public final RationalFloat addAbs (final double z) {
    //assert Double.isFinite(z);
    if (0.0==z) { return this; }
    return add3(
      true,
      Doubles.significand(z),
      Doubles.exponent(z));}

  public final RationalFloat
  addAbsAll (final double[] z) {
    //assert Double.isFinite(z);
    RationalFloat s = this;
    for (final double zi : z) { s = s.addAbs(zi); }
    return s; }

  //--------------------------------------------------------------

  @Override
  public final RationalFloat subtract (final RationalFloat q) {
    if (isZero()) { return q.negate(); }
    if (q.isZero()) { return this; }
    return add4(
      ! q.nonNegative(),
      q.numerator(),
      q.denominator(),
      q.exponent()); }

  public final RationalFloat subtract (final double z) {
    return add(-z); }

  //--------------------------------------------------------------

  private final RationalFloat multiply (final boolean p,
                                        final BoundedNatural n,
                                        final BoundedNatural d,
                                        final int e) {
    return valueOf(
      nonNegative() == p,
      numerator().multiply(n),
      denominator().multiply(d),
      exponent() + e); }

  @Override
  public final RationalFloat multiply (final RationalFloat q) {
    //    if (isZero() ) { return EMPTY; }
    //    if (q.isZero()) { return EMPTY; }
    //    if (q.isOne()) { return this; }
    //    if (isOne()) { return q; }
    return multiply(
      q.nonNegative(),
      q.numerator(),
      q.denominator(),
      q.exponent()); }

  //--------------------------------------------------------------

  private final RationalFloat
  multiply (final boolean p1,
            final long t11,
            final int e11) {

    // minimize long bits
    final int shift = Numbers.loBit(t11);
    final long t1 = (t11>>>shift);
    final int e1 = e11+shift;

    final BoundedNatural d = denominator();

    if (d.isOne()) {
      return valueOf(
        (nonNegative()==p1),
        NaturalMultiply.multiply(numerator(),t1),
        exponent()+e1); }

    return valueOf(
      (nonNegative()==p1),
      NaturalMultiply.multiply(numerator(),t1),
      d,
      exponent()+e1); }

  public final RationalFloat
  multiply (final double z) {
    //assert Double.isFinite(z);
    // escape on zero needed for add()
    if (0.0==z) { return this; }
    return multiply(
      Doubles.nonNegative(z),
      Doubles.significand(z),
      Doubles.exponent(z)); }

  //--------------------------------------------------------------

  @Override
  public final RationalFloat square () {
    if (isZero() ) { return ZERO; }
    if (isOne()) { return this; }
    return multiply(
      nonNegative(),numerator(),denominator(),exponent()); }

  //--------------------------------------------------------------

  public final RationalFloat add2 (final double z) {
    //assert Double.isFinite(z);
    if (0.0==z) { return this; }
    final long tz = Doubles.significand(z);
    final int ez = Doubles.exponent(z);
    final int s = Numbers.loBit(tz);
    final long t;
    final int e;
    if ((0==s) || (64==s)) { t=tz; e=ez; }
    else { t=(tz>>>s); e=ez+s; }
    final BoundedNatural t2 = BoundedNatural.fromSquare(t);
    final int e2 = (e<<1);

    final boolean p0 = nonNegative();
    final BoundedNatural n0 = numerator();
    final BoundedNatural d0 = denominator();
    final int e0 = exponent();
    if (d0.isOne()) { return add6(p0,n0,e0,true,t2,e2); }
    return add7(p0,n0,d0,e0,true,t2,e2); }

  public final RationalFloat
  add2All (final double[] z) {
    RationalFloat s = this;
    for (final double zi : z) { s = s.add2(zi); }
    return s; }

  //--------------------------------------------------------------

  public final RationalFloat addProduct (final double z0,
                                         final double z1) {
    if (BoundedNatural.ONE.equals(denominator())) {
      final BigFloat sum =
        BigFloat.valueOf(
          nonNegative(),
          numerator(),
          exponent())
        .addProduct(z0,z1);
      return valueOf(
        sum.nonNegative(),sum.significand(),sum.exponent()); }

    //assert Double.isFinite(z0);
    //assert Double.isFinite(z1);
    final boolean p =
      Doubles.nonNegative(z0) == Doubles.nonNegative(z1);
    final BoundedNatural n = BoundedNatural.valueOf(
      Doubles.significand(z0))
      .multiply(BoundedNatural.valueOf(Doubles.significand(z1)));
    final int e = Doubles.exponent(z0) + Doubles.exponent(z1);

    final boolean p0 = nonNegative();
    final BoundedNatural n0 = numerator();
    final BoundedNatural d0 = denominator();
    final int e0 = exponent();

    if (d0.isOne()) { return add6(p0,n0,e0,p,n,e); }
    return add7(p0,n0,d0,e0,p,n,e); }

  public final RationalFloat
  addProducts (final double[] z0,
               final double[] z1)  {
    final int n = z0.length;
    //assert n==z1.length;
    RationalFloat s = this;
    for (int i=0;i<n;i++) { s = s.addProduct(z0[i],z1[i]); }
    return s; }

  //--------------------------------------------------------------
  /** Exact <code>a*x+y</code> (aka fma). */

  public static final RationalFloat
  axpy (final double a,
        final double x,
        final double y) {
    if ((0.0==a) || (0.0==x)) { return valueOf(y); }
    final long t01 = Doubles.significand(a);
    final int e01 = Doubles.exponent(a);
    final int shift0 = Numbers.loBit(t01);
    final long t0 = (t01>>>shift0);
    final int e0 = e01+shift0;

    final long t11 = Doubles.significand(x);
    final int e11 = Doubles.exponent(x);
    final int shift1 = Numbers.loBit(t11);
    final long t1 = (t11>>>shift1);
    final int e1 = e11+shift1;

    return
      valueOf(
        Doubles.nonNegative(a)==Doubles.nonNegative(x),
        BoundedNatural.product(t0,t1),
        e0+e1)
      .add(y); }

  //    return valueOf(y).addProduct(a,x); }

  /** Exact <code>a*x+y</code> (aka fma). */

  @SuppressWarnings("unused")
  public static final RationalFloat[]
    axpy (final double[] a,
          final double[] x,
          final double[] y) {
    final int n = a.length;
    //assert n==x.length;
    //assert n==y.length;
    final RationalFloat[] bf = new RationalFloat[n];
    for (int i=0;i<n;i++) { bf[i] = axpy(a[i],x[i],y[i]); }
    return bf; }

  /** Exact <code>this*x+y</code> (aka fma). */

  public static final RationalFloat
  axpy (final double a,
        final RationalFloat x,
        final double y) {
    return x.multiply(a).add(y); }

  /** Exact <code>a*this+y</code> (aka fma). */

  @SuppressWarnings("unused")
  public static final RationalFloat[] axpy (final double[] a,
                                            final RationalFloat[] x,
                                            final double[] y) {
    final int n = x.length;
    //assert n==x.length;
    //assert n==y.length;
    final RationalFloat[] bf = new RationalFloat[n];
    for (int i=0;i<n;i++) { bf[i] = axpy(a[i],x[i],y[i]); }
    return bf; }

  //--------------------------------------------------------------

  public RationalFloat addL1 (final double z0,
                              final double z1) {
    if (z0>z1) { return add(z0).add(-z1); }
    if (z0<z1) { return add(-z0).add(z1); }
    return this; }

  public final RationalFloat
  addL1Distance (final double[] z0,
                 final double[] z1) {
    final int n = z0.length;
    //assert n==z1.length;
    RationalFloat s = this;
    for (int i=0;i<n;i++) { s = s.addL1(z0[i],z1[i]); }
    return s; }

  //--------------------------------------------------------------
  // internal special case: add 2*z0*z1

  private final RationalFloat
  addProductTwice (final double z0,
                   final double z1) {
    //assert Double.isFinite(z0);
    //assert Double.isFinite(z1);
    if ((0.0==z0) || (0.0==z1)) { return this; }

    final long t01 = Doubles.significand(z0);
    final int e01 = Doubles.exponent(z0);
    final int shift0 = Numbers.loBit(t01);
    final long t0 = (t01>>>shift0);
    final int e0 = e01+shift0;

    final long t11 = Doubles.significand(z1);
    final int e11 = Doubles.exponent(z1);
    final int shift1 = Numbers.loBit(t11);
    final long t1 = (t11>>>shift1);
    final int e1 = e11+shift1;

    final boolean p = nonNegative();
    final BoundedNatural n = numerator();
    final BoundedNatural d = denominator();
    final int e = exponent();

    final boolean p3 =
      Doubles.nonNegative(z0)==Doubles.nonNegative(z1);
    final BoundedNatural n3 = BoundedNatural.product(t0,t1);
    final int e3 = e0+e1+1;

    if (d.isOne()) { return add6(p,n,e,p3,n3,e3); }
    return add7(p,n,d,e,p3,n3,e3); }

  //--------------------------------------------------------------

  public final RationalFloat
  addL2 (final double z0,
         final double z1) {
    return
      add2(z0).add2(z1).addProductTwice(z0,-z1); }

  public final RationalFloat
  addL2Distance (final double[] z0,
                 final double[] z1) {
    final int n = z0.length;
    //assert n==z1.length;
    RationalFloat s = this;
    for (int i=0;i<n;i++) { s = s.addL2(z0[i],z1[i]); }
    return s; }

  //--------------------------------------------------------------
  // Number methods
  //--------------------------------------------------------------
//  /** Returns the low order bits of the truncated quotient.
//   * <br>>
//   * TODO: should it really truncate or round instead? Or
//   * should there be more explicit round, floor, ceil, etc.?
//   */

  //  @Override
  //  public final int intValue () {
  //    return bigIntegerValue().intValue(); }

//  /** Returns the low order bits of the truncated quotient.
//   * <br>
//   * TODO: should it really truncate or round instead? Or
//   * should there be more explicit round, floor, ceil, etc.?
//   */

  //  @Override
  //  public final long longValue () {
  //    return bigIntegerValue().longValue(); }

//  /** Returns the truncated quotient.
//   * <br>
//   * TODO: should it round instead? Or
//   * should there be more explicit round, floor, ceil, etc.?
//   */

  //  public final BigInteger bigIntegerValue () {
  //    final BoundedNatural nd = numerator().divide(denominator());
  //    final BigInteger x = nd.bigIntegerValue();
  //    return (nonNegative() ? x : x.negate()); }

  //--------------------------------------------------------------
  /** Half-even rounding from {@link BoundedNatural} ratio to
   * <code>float</code>.
   * @return closest half-even rounded <code>float</code>.
   */

  @Override
  public final float floatValue () {
    final boolean p0 = nonNegative();
    final BoundedNatural n0 = numerator();
    final BoundedNatural d0 = denominator();
    final int e0 = exponent();

    if (d0.isOne()) { return BigFloat.floatValue(p0,n0,e0); }
    if (n0.isZero()) { return (p0 ? 0.0F : -0.0F); }

    // TODO: fix this hack or call Rational
    final boolean large = (e0 >= 0);
    final BoundedNatural n00 = (large ? n0.shiftUp(e0) : n0);
    final BoundedNatural d00 = (large ? d0 : d0.shiftUp(-e0));

    // choose exponent, and shift numerator and denominator so
    // quotient has the right number of bits.
    final int e1 = n00.hiBit() - d00.hiBit() - 1;
    final boolean small = (e1 > 0);
    final BoundedNatural n1 = (small ? n00 : n00.shiftUp(-e1));
    final BoundedNatural d1 = (small ? d00.shiftUp(e1) : d00);

    // ensure numerator is less than 2x denominator
    final BoundedNatural d11 = d1.shiftUp(1);
    final BoundedNatural d2;
    final int e2;
    if (n1.compareTo(d11) < 0) { d2 = d1; e2 = e1;}
    else { d2 = d11; e2 = e1 + 1; }

    // check for out of range
    if (e2 > Float.MAX_EXPONENT) {
      return (p0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY); }
    if (e2 < Floats.MINIMUM_SUBNORMAL_EXPONENT) {
      return (p0 ? 0.0F : -0.0F); }

    // subnormal numbers need slightly different handling
    final boolean sub = (e2 < Float.MIN_EXPONENT);
    final int e3 = sub ? Float.MIN_EXPONENT : e2;
    final BoundedNatural d3 = (sub ? d2.shiftUp(e3-e2) : d2);
    final BoundedNatural n3 =
      n1.shiftUp(Floats.STORED_SIGNIFICAND_BITS);

    final int e4 = e3 - Floats.STORED_SIGNIFICAND_BITS;

    //Debug.println("num=" + n3.toHexString());
    //Debug.println("den=" + d3.toHexString());

    final BoundedNatural[] qr = n3.divideAndRemainder(d3);

    //Debug.println("quo=" + qr[0].toHexString());
    //Debug.println("quo=" + Long.toHexString(qr[0].longValueExact()));
    //Debug.println("rem=" + qr[1].toHexString());

    // round down or up?
    // want to know if remainder/denominator is more or less than 1/2
    // comparing 2*remainder to denominator
    // TODO: faster way to do this?
    final int c = qr[1].shiftUp(1).compareTo(d3);
    final int q4 = qr[0].intValue();
    final boolean even = (0x0 == (q4 & 0x1));
    final boolean down = (c < 0) || ((c == 0) && even);
    final int q;
    final int e;
    if (down) {
      q = q4;
      e = (sub ? e4 - 1 : e4); }
    else {
      final int q5 = q4 + 1;
      // handle carry if needed after round up
      final boolean carry = (hiBit(q5) > Floats.SIGNIFICAND_BITS);
      q = carry ? q5 >>> 1 : q5;
    e = (sub ? (carry ? e4 : e4 - 1) : (carry ? e4 + 1 : e4)); }
    return Floats.makeFloat(!p0,e,q); }

  //--------------------------------------------------------------

  public static final double doubleValue  (final boolean p0,
                                           final BoundedNatural n0,
                                           final BoundedNatural d0,
                                           final int e0) {
    if (n0.isZero()) { return (p0 ? 0.0 : -0.0); }

    final boolean neg = !p0;

    // TODO: fix this hack
    final boolean large = (e0 >= 0);
    final BoundedNatural n00 = (large ? n0.shiftUp(e0) : n0);
    final BoundedNatural d00 = (large ? d0 : d0.shiftUp(-e0));

    // choose exponent, and shift numerator and denominator so
    // quotient has the right number of bits.
    final int e1 = n00.hiBit()-d00.hiBit()-1;
    final boolean small = (e1 > 0);
    final BoundedNatural n1 = small ? n00 : n00.shiftUp(-e1);
    final BoundedNatural d1 = small ? d00.shiftUp(e1) : d00;

    // ensure numerator is less than 2x denominator
    final BoundedNatural d11 = d1.shiftUp(1);
    final BoundedNatural d2;
    final int e2;
    if (n1.compareTo(d11) < 0) { d2 = d1; e2 = e1;}
    else { d2 = d11; e2 = e1 + 1; }

    // check for out of range
    if (e2 > Double.MAX_EXPONENT) {
      return (neg
        ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY); }
    if (e2 < Doubles.MINIMUM_SUBNORMAL_EXPONENT) {
      return (neg ? -0.0 : 0.0); }

    // subnormal numbers need slightly different handling
    final boolean sub = (e2 < Double.MIN_EXPONENT);
    final int e3 = (sub ? Double.MIN_EXPONENT : e2);
    final BoundedNatural d3 = sub ? d2.shiftUp(e3-e2) : d2;
    final BoundedNatural n3 =
      n1.shiftUp(Doubles.STORED_SIGNIFICAND_BITS);

    final int e4 = e3 - Doubles.STORED_SIGNIFICAND_BITS;

    final BoundedNatural[] qr = n3.divideAndRemainder(d3);
    final BoundedNatural qr0 = qr[0];
    final BoundedNatural qr1 = qr[1];

    // round down or up?
    // want to know if remainder/denominator is
    // more or less than 1/2
    // comparing 2*remainder to denominator
    // TODO: faster way to do this?
    final int c = - d3.compareTo(qr1.shiftUp(1));
    final long q4 = qr0.longValue();
    final boolean even = (0x0L == (q4 & 0x1L));
    final boolean down = (c < 0) || ((c == 0) && even);
    final long q;
    final int e;
    if (down) {
      q = q4;
      e = (sub ? e4-1 : e4); }
    else {
      final long q5 = q4+1;
      // handle carry if needed after round up
      final boolean carry = (hiBit(q5) > Doubles.SIGNIFICAND_BITS);
      q = (carry ? q5 >>> 1 : q5);
      e = (sub ? (carry ? e4 : e4 - 1) : (carry ? e4 + 1 : e4)); }
    return Doubles.makeDouble(neg,q,e); }

  /** Half-even rounding to <code>double</code>.
   */

  @Override
  public final double doubleValue () {
    final BoundedNatural d0 = denominator();
    if (d0.isOne()) {
      return BigFloat.doubleValue(
        nonNegative(),numerator(),exponent()); }
    return doubleValue(
      nonNegative(),numerator(),denominator(),exponent()); }

  //--------------------------------------------------------------
  // Comparable methods
  //--------------------------------------------------------------

  @Override
  public final int compareTo (final RationalFloat q) {
    if (nonNegative() && (! q.nonNegative())) { return 1; }
    if ((! nonNegative()) && q.nonNegative()) { return -1; }
    // same signs
    final BoundedNatural n0d1 = numerator().multiply(q.denominator());
    final BoundedNatural n1d0 = q.numerator().multiply(denominator());
    final int e0 = exponent();
    final int e1 = q.exponent();
    final int c;
    if (e0 <= e1) {
      c = n0d1.compareTo(n1d0.shiftUp(e1-e0)); }
    else {
      c = n0d1.shiftUp(e0-e1).compareTo(n1d0); }
    return (nonNegative() ? c : -c); }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------

  public final boolean equals (final RationalFloat q) {
    if (this == q) { return true; }
    if (null == q) { return false; }
    final RationalFloat rf0 = reduce();
    final RationalFloat rf1 = q.reduce();
    // assuming reduced
    return
      (rf0.nonNegative() == rf1.nonNegative())
      &&
      (rf0.exponent() == rf1.exponent())
      &&
      rf0.numerator().equals(rf1.numerator())
      &&
      rf0.denominator().equals(rf1.denominator()); }

  @Override
  public boolean equals (final Object o) {
    if (!(o instanceof RationalFloat)) { return false; }
    return equals((RationalFloat) o); }

  @Override
  public int hashCode () {
    final RationalFloat r = reduce();
    int h = 17;
    h = (31*h) + (r.nonNegative() ? 1 : 0);
    h = (31*h) + r.exponent();
    h = (31*h) + Objects.hash(r.numerator(),r.denominator());
    return h; }

  @Override
  public final String toString () {
    final boolean neg = ! nonNegative();
    final String n = numerator().toHexString();
    return
      (neg ? "-" : "") + "0x"
      + n
      + "p" + exponent()
      + " / "
      + denominator().toHexString(); }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private RationalFloat (final boolean p,
                         final BoundedNatural n,
                         final BoundedNatural d,
                         final int e) {
    _nonNegative = p;
    _numerator = n;
    _denominator = d;
    _exponent = e; }

  //--------------------------------------------------------------
  /** optimize denominator == 1 case. */

  private static final RationalFloat
  reduce (final boolean p,
          final BoundedNatural n,
          final int e) {

    if (n.isZero()) { return ZERO; }
    if (n.isOne()) {
      return new RationalFloat(
        p,BoundedNatural.ONE,BoundedNatural.ONE,e); }
    final int en = n.loBit();
    final BoundedNatural n0 =
      (en != 0) ? n.shiftDown(en) : n;
    final int e0 = (e + en);
    return new RationalFloat(p,n0,BoundedNatural.ONE,e0); }

  private static final RationalFloat
  reduce (final boolean p,
          final BoundedNatural n,
          final BoundedNatural d,
          final int e) {

    if (n.isZero()) { return ZERO; }
    if (d.isOne()) { return reduce(p,n,e); }

    // TODO: is numerator 1 case worth optimizing?
    if (n.isOne()) {
      final int ed = d.loBit();
      final BoundedNatural d0 =
        (ed != 0) ? d.shiftDown(ed) : d;
      final int e0 = e - ed;
      return new RationalFloat(p,BoundedNatural.ONE,d0,e0); }

    final int en = n.loBit();
    final int ed = d.loBit();
    final BoundedNatural n0 =
      (en != 0) ? n.shiftDown(en) : n;
    final BoundedNatural d0 =
      (ed != 0) ? d.shiftDown(ed) : d;
    final int e0 = (e + en) - ed;

    // might have numerator or denominator 1 after shift
    if (d0.isOne()) {
      if (n0.isOne()) {
        return new RationalFloat(
          p,BoundedNatural.ONE,BoundedNatural.ONE,e0); }
      return new RationalFloat(p,n0,BoundedNatural.ONE,e0); }
    if (n0.isOne()) {
      return new RationalFloat(p,BoundedNatural.ONE,d0,e0); }

    final BoundedNatural gcd = n0.gcd(d0);
    final BoundedNatural n1 = n0.divide(gcd);
    final BoundedNatural d1 = d0.divide(gcd);
    return new RationalFloat(p,n1,d1,e0); }

  private final RationalFloat reduce () {
    return
      reduce(
        nonNegative(),numerator(),denominator(),exponent()); }

  //--------------------------------------------------------------

  public static final RationalFloat valueOf (final boolean p,
                                             final BoundedNatural n,
                                             final BoundedNatural d,
                                             final int e) {
    //    return reduce(nonNegative,n,d,e); }
    return new RationalFloat(p,n,d,e); }

  public static final RationalFloat valueOf (final boolean p,
                                             final BoundedNatural n,
                                             final int e) {
    //return reduce(p,n,e); }
    return new RationalFloat(p,n,BoundedNatural.ONE,e); }

  //  public static final RationalFloat valueOf (final boolean p,
  //                                             final BoundedNatural x)  {
  //    //return reduce(p, x, BoundedNatural.ONE,0); }
  //  return new RationalFloat(p, x, BoundedNatural.ONE,0); }

  public static final RationalFloat valueOf (final BigInteger n,
                                             final BigInteger d) {
    return valueOf(
      0 <= (n.signum()*d.signum()),
      BoundedNatural.valueOf(n.abs()),
      BoundedNatural.valueOf(d.abs()),
      0); }

  //--------------------------------------------------------------

  private static final RationalFloat valueOf (final boolean p0,
                                              final long t0,
                                              final int e0)  {
    if (0L==t0) { return ZERO; }
    //assert 0L < t0;
    final int shift = Numbers.loBit(t0);
    final long t1;
    final int e1;
    if ((0==shift)||(64==shift)) { t1=t0; e1=e0; }
    else { t1 = (t0 >>> shift); e1 = e0 + shift; }
    return valueOf(p0,BoundedNatural.valueOf(t1),e1); }

  public static final RationalFloat valueOf (final double x)  {
    return valueOf(
      Doubles.nonNegative(x),
      Doubles.significand(x),
      Doubles.exponent(x)); }

  //--------------------------------------------------------------

  private static final RationalFloat valueOf (final boolean p,
                                              final int e,
                                              final int t)  {
    if (0 == t) { return ZERO; }
    //assert 0 < t;
    return valueOf(p,BoundedNatural.valueOf(t),e); }

  public static final RationalFloat valueOf (final float x)  {
    return valueOf(
      Floats.nonNegative(x),
      Floats.exponent(x),
      Floats.significand(x)); }

  //--------------------------------------------------------------

  //  public static final RationalFloat valueOf (final byte x)  {
  //    final boolean nonNegative = (0 <= x);
  //    return valueOf(
  //      nonNegative,
  //      BoundedNatural.valueOf(nonNegative ? x : -x)); }
  //
  //  public static final RationalFloat valueOf (final short x)  {
  //    final boolean nonNegative = (0 <= x);
  //    return valueOf(
  //      nonNegative,
  //      BoundedNatural.valueOf(nonNegative ? x : -x)); }
  //
  //  public static final RationalFloat valueOf (final int x)  {
  //    final boolean nonNegative = (0 <= x);
  //    return valueOf(
  //      nonNegative,
  //      BoundedNatural.valueOf(nonNegative ? x : -x)); }
  //
  //  public static final RationalFloat valueOf (final long x)  {
  //    final boolean nonNegative = (0L <= x);
  //    return
  //      valueOf(nonNegative,BoundedNatural.valueOf(nonNegative ? x : -x)); }

  //--------------------------------------------------------------

  public static final RationalFloat valueOf (final Double x)  {
    return valueOf(x.doubleValue()); }

  public static final RationalFloat valueOf (final Float x)  {
    return valueOf(x.floatValue()); }

  public static final RationalFloat valueOf (final Byte x)  {
    return valueOf(x.byteValue()); }

  public static final RationalFloat valueOf (final Short x)  {
    return valueOf(x.shortValue()); }

  public static final RationalFloat valueOf (final Integer x)  {
    return valueOf(x.intValue()); }

  public static final RationalFloat valueOf (final BigInteger x)  {
    return valueOf(0<=x.signum(),BoundedNatural.valueOf(x.abs()),0); }

  public static final RationalFloat valueOf (final Long x)  {
    return valueOf(x.longValue()); }

  ////  public static final RationalFloat valueOf (final BigDecimal x)  {
  ////    throw Exceptions.unsupportedOperation(null,"valueOf",x); }

  public static final RationalFloat valueOf (final Object x)  {
    if (x instanceof RationalFloat) { return (RationalFloat) x; }
    if (x instanceof BigInteger) { return valueOf((BigInteger) x); }
    if (x instanceof Double) { return valueOf((Double) x); }
    if (x instanceof Float) { return valueOf((Float) x); }
    if (x instanceof Byte) { return valueOf((Byte) x); }
    if (x instanceof Short) { return valueOf((Short) x); }
    if (x instanceof Integer) { return valueOf((Integer) x); }
    if (x instanceof Long) { return valueOf((Long) x); }
    if (x instanceof BoundedNatural) { return valueOf(x); }
    //    if (x instanceof BigDecimal) { return valueOf((BigDecimal) x); }
    throw Exceptions.unsupportedOperation(
      RationalFloat.class,"valueOf",x); }

  //--------------------------------------------------------------
  // Note: these need to be reduced.

  public static final RationalFloat ZERO =
    new RationalFloat(true,BoundedNatural.ZERO,BoundedNatural.ONE,0);

  public static final RationalFloat ONE =
    new RationalFloat(true,BoundedNatural.ONE,BoundedNatural.ONE,0);

  //  public static final RationalFloat TWO =
  //    new RationalFloat(true,BoundedNatural.ONE,BoundedNatural.ONE,1);

  //  public static final RationalFloat TEN =
  //    new RationalFloat(true,BoundedNatural.valueOf(5),BoundedNatural.ONE,1);

  //  public static final RationalFloat MINUS_ONE =
  //  new RationalFloat(false,BoundedNatural.ONE,BoundedNatural.ONE,0);

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
