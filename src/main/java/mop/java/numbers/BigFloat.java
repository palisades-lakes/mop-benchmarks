package mop.java.numbers;

import mop.java.Exceptions;
import java.util.Objects;

//----------------------------------------------------------------------
/** A sign times a {@link BoundedNatural} significand times 2 to a
 * <code>int</code> exponent.
 * <br>
 * Implementation:
 * (compare with
 * <a href="https://en.wikipedia.org/wiki/Double-precision_floating-point_format">
 *   IEEE 754 binary64</a>)
 *
 * <dl>
 * <dt>nonNegative()</dt> <code>boolean</code>
 * <dt>exponent()</dt> signed 32bit <code>int</code>.
 * Values in [<code>Integer.MIN_VALUE+1</code>,
 * <code>Integer.MAX_VALUE]</code> indicate finite
 * <code>BigFloat</code>s.
 * <code>Integer.MIN_VALUE</code> means a non-finite value.
 * <code>NaN</code> vs positive and negative infinity
 * is determined by the <code>significand()</code>,
 * with <code>null</code> meaning NaN and anything else infinity.
 * <dt>significand()</dt> (unsigned) <code>BoundedNatural</code>,
 * an non-negative integer in [0,<code>BoundedNatural.maxValue()</code>]
 * </dl>
 * Finite values are
 * <code>(nonNegative()?1:-1) * significand() * 2^exponent()</code>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-27
 */

@SuppressWarnings("unused")
public final class BigFloat implements Ringlike<BigFloat> {

  //--------------------------------------------------------------
  // instance fields and methods
  //--------------------------------------------------------------

  private final boolean _nonNegative;
  public final boolean nonNegative () { return _nonNegative; }

  // TODO: long exponent?
  private final int _exponent;
  public final int exponent () { return _exponent; }

  // must always be non-negative
  private final BoundedNatural _significand;
  public final BoundedNatural significand () { return _significand; }

  //--------------------------------------------------------------
  // Constants
  //--------------------------------------------------------------

  public static final BigFloat ZERO =
    new BigFloat(true,BoundedNatural.ZERO,0);

  public static final BigFloat POSITIVE_ZERO = ZERO;

  public static final BigFloat NEGATIVE_ZERO =
    new BigFloat(false,BoundedNatural.ZERO,0);

  private static final BigFloat ONE =
    new BigFloat(true,BoundedNatural.valueOf(1),0);

  public static final BigFloat NaN =
    new BigFloat(true,null,Integer.MIN_VALUE);

  public static final BigFloat POSITIVE_INFINITY =
    new BigFloat(true,BoundedNatural.ZERO,Integer.MIN_VALUE);

  public static final BigFloat NEGATIVE_INFINITY =
    new BigFloat(false,BoundedNatural.ZERO,Integer.MIN_VALUE);

  //    private static final BigFloat TWO =
  //    new BigFloat(true,BoundedNatural.valueOf(1),1);
  //
  //  private static final BigFloat TEN =
  //    new BigFloat(true,BoundedNatural.valueOf(5),1);
  //
  //  private static final BigFloat MINUS_ONE =
  //    new BigFloat(false,BoundedNatural.valueOf(1),0);

  //--------------------------------------------------------------
  // Value classification
  //--------------------------------------------------------------

  public final boolean isFinite () {
    return exponent()>Integer.MIN_VALUE; }

  public final boolean isNaN () {
    return (exponent() == Integer.MIN_VALUE) &&
      (null == significand()); }

  public final boolean isInfinite () {
    return (exponent() == Integer.MIN_VALUE) &&
      (null != significand()); }

  public final boolean isPositiveInfinity () {
    return isInfinite() && nonNegative(); }

  public final boolean isNegativeInfinity () {
    return isInfinite() && (! nonNegative()); }

  /** Note: has positive and negative zero, like <code>double</code>
   * via nonNegative().
   */
  @Override
  public final boolean isZero () { return equals(ZERO); }

  @Override
  public final boolean isOne () { return equals(ONE); }

  //--------------------------------------------------------------

  @Override
  public final BigFloat negate () {
    if (isNaN()) { return this; }
    // TODO: is saving a few new instances worth this?
    if (isPositiveInfinity()) { return NEGATIVE_INFINITY; }
    if (isNegativeInfinity()) { return POSITIVE_INFINITY; }
    // positive and negative zeros!
    return valueOf(! nonNegative(),significand(),exponent()); }

  @Override
  public final BigFloat abs () {
    if (isNaN()) { return this; }
    if (nonNegative()) { return this; }
    return valueOf(true,significand(),exponent()); }

  //--------------------------------------------------------------
  // assuming args correspond to finite numbers

  private static final BigFloat add6 (final boolean p0,
                                      final BoundedNatural t0,
                                      final int e0,
                                      final boolean p1,
                                      final BoundedNatural t1,
                                      final int e1) {
    if (e0<e1) { return add6(p1,t1,e1,p0,t0,e0); }
    final int de = e0-e1;
    if (p0!=p1) { // different signs
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

  @Override
  public final BigFloat add (final BigFloat q) {

    if (isNaN() || q.isNaN()) { return NaN; }
    if (isPositiveInfinity()) {
      if (q.isNegativeInfinity()) { return NaN; }
      return POSITIVE_INFINITY; }
    if (isNegativeInfinity()) {
      if (q.isPositiveInfinity()) { return NaN; }
      return NEGATIVE_INFINITY; }

    return add6(
      nonNegative(), significand(), exponent(),
      q.nonNegative(), q.significand(), q.exponent()); }

  //--------------------------------------------------------------

  private static final BigFloat
  add6 (final boolean p0,
        final BoundedNatural t0,
        final boolean p1,
        final long t1,
        final int upShift,
        final int e) {
    assert 0L<t1;
    assert 0<=upShift;
    if (p0==p1) { return valueOf(p0,t0.add(t1,upShift),e); }
    final int c = t0.compareTo(t1,upShift);
    if (0<c) { return valueOf(p0,t0.subtract(t1,upShift),e); }
    if (0>c) { return valueOf(p1,t0.subtractFrom(t1,upShift),e); }
    return ZERO; }

  //--------------------------------------------------------------

  private static final BigFloat
  add5 (final boolean p0,
        final BoundedNatural t0,
        final boolean p1,
        final long t1,
        final int e) {
    assert 0L<=t1;
    if (p0==p1) { return valueOf(p0,t0.add(t1),e); }
    // different signs
    final int c = t0.compareTo(t1);
    // t0>t1
    if (0<c) { return valueOf(p0,t0.subtract(t1),e); }
    // t1>t0
    if (0>c) { return valueOf(p1,t0.subtractFrom(t1),e); }
    return ZERO; }

  //--------------------------------------------------------------

  private final BigFloat
  add3 (final boolean p1,
        final long t11,
        final int e11) {
    // only called when this.isFinite() is true!
    assert 0L<=t11;
    //if (0L==t11) { return this; }

    final boolean p0 = nonNegative();
    final BoundedNatural t0 = significand();
    final int e0 = exponent();

    // minimize long bits
    final int shift = Numbers.loBit(t11);
    final long t1 = (t11>>>shift);
    final int e1 = e11+shift;

    if (e0<e1) { return add6(p0,t0,p1,t1,e1-e0,e0); }
    if (e0==e1) { return add5(p0,t0,p1,t1,e0); }
    return add5(p0,t0.shiftUp(e0-e1),p1,t1,e1); }

  //--------------------------------------------------------------

  public final BigFloat
  add (final double z) {
    if (isNaN()) { return NaN; }
    if (Double.isNaN(z)) { return NaN; }
    if (isPositiveInfinity()) {
      if (Double.NEGATIVE_INFINITY==z) { return NaN; }
      return POSITIVE_INFINITY; }
    if (isNegativeInfinity()) {
      if (Double.POSITIVE_INFINITY==z) { return NaN; }
      return NEGATIVE_INFINITY; }

    // escape on zero needed for add()
    if (0.0==z) { return this; }
    return add3(
      Doubles.nonNegative(z),
      Doubles.significand(z),
      Doubles.exponent(z)); }

  public final BigFloat
  addAll (final double[] z) {
    assert isFinite();
    BigFloat s = this;
    for (final double zi : z) { s = s.add(zi); }
    return s; }

  //--------------------------------------------------------------

  public final BigFloat
  addAbs (final double z) {
    if (isNaN()) { return NaN; }
    if (Double.isNaN(z)) { return NaN; }
    if (isPositiveInfinity()) { return POSITIVE_INFINITY; }
    if (isNegativeInfinity()) {
      if (Double.NEGATIVE_INFINITY==z) { return NaN; }
      return NEGATIVE_INFINITY; }
    // escape on zero needed for add()
    if (0.0==z) { return this; }
    return add3(
      true,
      Doubles.significand(z),
      Doubles.exponent(z)); }

  public final BigFloat
  addAbsAll (final double[] z) {
    assert isFinite();
    BigFloat s = this;
    for (final double zi : z) { s = s.addAbs(zi); }
    return s; }

  //--------------------------------------------------------------

  @Override
  public final BigFloat
  subtract (final BigFloat q) {
    if (isNaN() || q.isNaN()) { return NaN; }
    if (isPositiveInfinity()) {
      if (q.isPositiveInfinity()) { return NaN; }
      return POSITIVE_INFINITY; }
    if (isNegativeInfinity()) {
      if (q.isNegativeInfinity()) { return NaN; }
      return NEGATIVE_INFINITY; }

    return add6(
      nonNegative(),
      significand(),
      exponent(),
      ! q.nonNegative(),
      q.significand(),
      q.exponent()); }

  //--------------------------------------------------------------
  /** Return the "exact" value of <code>z0+z1</code>,
   * without intermediate <code>BigFloat</code> instances.
   */

  public static final BigFloat sum (final double z0,
                                    final double z1) {
    if (Double.isNaN(z0) || Double.isNaN(z1)) { return NaN; }
    switch (z0) {
      case Double.POSITIVE_INFINITY -> {
        if (Double.NEGATIVE_INFINITY == z1) { return NaN; }
        return POSITIVE_INFINITY;
      }
      case Double.NEGATIVE_INFINITY -> {
        if (Double.POSITIVE_INFINITY == z1) { return NaN; }
        return NEGATIVE_INFINITY;
      }
      case 0.0 -> {
        return BigFloat.valueOf(z1);
      }
      default -> {
      }
    }
    if (0.0==z1) { return BigFloat.valueOf(z0); }
    final int e0 = Doubles.exponent(z0);
    final int e1 = Doubles.exponent(z1);
    if (e0<e1) { return sum(z1,z0); }
    // TODO: handle infinities and NaN!
    final boolean p0 = Doubles.nonNegative(z0);
    final long t0 = Doubles.significand(z0);

    final boolean p1 = Doubles.nonNegative(z1);
    final long t1 = Doubles.significand(z1);

    // IEEE 754:
    // https://en.wikipedia.org/wiki/Double-precision_floating-point_format
    // -1022<=e0,e1<=1023; 0<=abs(e0-e1)<=2045
    // 0<=t0,t1<=2^53

    // need to convert one signifcand to BoundedNatural to handle
    // overflow in significand shift and addition/subtraction
    final BoundedNatural s = BoundedNatural.valueOf(t0, e0-e1);
    if (p0 == p1) { return BigFloat.valueOf(p0, s.add(t1), e1); }
    if (p0) {
      if (0 <= s.compareTo(t1)) {
        return BigFloat.valueOf(true, s.subtract(t1), e1); }
      return BigFloat.valueOf(false, s.subtractFrom(t1), e1); }
    if (0 <= s.compareTo(t1)) {
      return BigFloat.valueOf(false, s.subtract(t1), e1); }
    return BigFloat.valueOf(true, s.subtractFrom(t1), e1); }

  /** Return the "exact" value of <code>z0-z1</code>,
   * without intermediate <code>BigFloat</code> instances.
   */

  public static final BigFloat dif (final double z0,
                                    final double z1) {
    // TODO: expand this? probably not worth while
    return sum(z0,-z1); }

  //--------------------------------------------------------------
  // used in Rational.addWithDenom()?

  public static final BigFloat
  product (final BoundedNatural x0,
           final boolean p1,
           final long x1) {
    assert 0L<=x1;
    final int e0 = x0.loBit();
    final int e1 = Numbers.loBit(x1);
    final BoundedNatural y0 =  ((0==e0) ? x0 : x0.shiftDown(e0));
    final long y1 = (((0==e1)||(64==e1)) ? x1 : (x1 >>> e1));
    return valueOf(p1,NaturalMultiply.multiply(y0,y1),e0+e1); }

  @Override
  public final BigFloat
  multiply (final BigFloat q) {
    if (isNaN() || q.isNaN()) { return NaN; }
    final boolean sameSigns = (nonNegative()==q.nonNegative());
    if (isPositiveInfinity()) {
      if (sameSigns) { return POSITIVE_INFINITY; }
      return NEGATIVE_INFINITY; }
    if (isNegativeInfinity()) {
      if (sameSigns) { return POSITIVE_INFINITY; }
      return NEGATIVE_INFINITY; }
    return valueOf(
      sameSigns,
      significand().multiply(q.significand()),
      Math.addExact(exponent(),q.exponent()));}

  //--------------------------------------------------------------

  private final BigFloat
  multiply (final boolean p1,
            final long t11,
            final int e11) {
    // handle nonfinite cases in caller
    // minimize long bits
    final int shift = Numbers.loBit(t11);
    final long t1 = (t11>>>shift);
    final int e1 = e11+shift;

    return valueOf(
      (nonNegative()==p1),
      NaturalMultiply.multiply(significand(),t1),
      exponent()+e1); }

  public final BigFloat
  multiply (final double z) {
    if (isNaN() || Double.isNaN(z)) { return NaN; }
    // escape on zero needed for add()?
    //if (0.0==z) { return this; }
    final boolean sameSigns = (nonNegative() == Doubles.nonNegative(z));
    if (isPositiveInfinity()) {
      if (sameSigns) { return POSITIVE_INFINITY; }
      return NEGATIVE_INFINITY; }
    if (isNegativeInfinity()) {
      if (sameSigns) { return POSITIVE_INFINITY; }
      return NEGATIVE_INFINITY; }

    return multiply(
      Doubles.nonNegative(z),
      Doubles.significand(z),
      Doubles.exponent(z)); }

  //--------------------------------------------------------------

  @Override
  public final BigFloat
  square () {
    if (isNaN()) { return NaN; }
    if (isPositiveInfinity()) { return POSITIVE_INFINITY; }
    if (isNegativeInfinity()) { return POSITIVE_INFINITY; }
    if (isZero() ) { return ZERO; }
    if (isOne()) { return ONE; }
    return valueOf(true, significand().square(),2*exponent()); }

  //--------------------------------------------------------------
  // geometry
  //--------------------------------------------------------------
  /** Compute squared l2norm without intermediate instances. */

  public static final BigFloat l2norm2 (final BigFloat x,
                                        final BigFloat y) {
    if (x.isNaN() || y.isNaN()) { return NaN; }
    if ((! x.isFinite()) || (! y.isFinite())) {
      return POSITIVE_INFINITY; }
    return add6(true,x.significand().square(),2*x.exponent(),
                true,y.significand().square(),2*y.exponent()); }

  //--------------------------------------------------------------

  public static final BigFloat
  crossProduct (final BigFloat x0,
                final BigFloat y0,
                final BigFloat x1,
                final BigFloat y1) {
    assert x0.isFinite();
    assert y0.isFinite();
    assert x1.isFinite();
    assert y1.isFinite();

    return
      add6(
        (x0.nonNegative()==y1.nonNegative()),
        x0.significand().multiply(y1.significand()),
        Math.addExact(x0.exponent(),y1.exponent()),
        ! (y0.nonNegative()==x1.nonNegative()),
        y0.significand().multiply(x1.significand()),
        Math.addExact(y0.exponent(),x1.exponent())); }

  //--------------------------------------------------------------

  public static final BigFloat
  dot (final BigFloat x0,
       final BigFloat y0,
       final BigFloat z0,
       final BigFloat x1,
       final BigFloat y1,
       final BigFloat z1) {

    assert x0.isFinite();
    assert y0.isFinite();
    assert z0.isFinite();
    assert x1.isFinite();
    assert y1.isFinite();
    assert z1.isFinite();

    final BigFloat dxy =
      add6(
        (x0.nonNegative()==x1.nonNegative()),
        x0.significand().multiply(x1.significand()),
        Math.addExact(x0.exponent(),x1.exponent()),
        (y0.nonNegative()==y1.nonNegative()),
        y0.significand().multiply(y1.significand()),
        Math.addExact(y0.exponent(),y1.exponent()));

    final BigFloat dz = valueOf(
      (z0.nonNegative()==z1.nonNegative()),
      z0.significand().multiply(z1.significand()),
      Math.addExact(z0.exponent(),z1.exponent()));

    return dxy.add(dz); }

  //--------------------------------------------------------------
  // accumulator methods
  //--------------------------------------------------------------
  /** add z*z */

  public final BigFloat
  add2 (final double z) {
    if (isNaN() || Double.isNaN(z)) { return NaN; }
    if (isNegativeInfinity()) {
      if (Double.isFinite(z)) { return NEGATIVE_INFINITY; }
      return NaN; }
    if (isPositiveInfinity()) { return POSITIVE_INFINITY; }
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
    return add6(
      nonNegative(),
      significand(),
      exponent(),
      true,
      t2,
      e2); }

  public final BigFloat
  add2All (final double[] z) {
    BigFloat s = this;
    for (final double zi : z) { s = s.add2(zi); }
    return s; }

  //--------------------------------------------------------------

  public final BigFloat
  addProduct (final double z0,
              final double z1) {
    if (isNaN() || Double.isNaN(z0) || Double.isNaN(z1)) { return NaN; }
    if ((0.0==z0) || (0.0==z1)) { return this; }
    final boolean nonnegativeProduct =
      (Doubles.nonNegative(z0) == Doubles.nonNegative(z1));
    final boolean infiniteProduct =
      Double.isInfinite(z0) || Double.isInfinite(z1);
    if (nonnegativeProduct) {
      if (isPositiveInfinity()) { return POSITIVE_INFINITY; }
      if (infiniteProduct) {
        if (isNegativeInfinity()) { return NaN; }
        return POSITIVE_INFINITY; }
      if (isNegativeInfinity()) { return NEGATIVE_INFINITY; } }
    else {
      if (isNegativeInfinity()) { return NEGATIVE_INFINITY; }
      if (infiniteProduct) {
        if (isPositiveInfinity()) { return NaN; }
        return NEGATIVE_INFINITY; }
      if (isPositiveInfinity()) { return POSITIVE_INFINITY; } }

    // everything is finite
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

    return
      add6(
        nonnegativeProduct,
        BoundedNatural.product(t0,t1),
        e0+e1,
        nonNegative(),
        significand(),
        exponent()); }

  public final BigFloat
  addProducts (final double[] z0,
               final double[] z1)  {
    final int n = z0.length;
    assert n==z1.length;
    BigFloat s = this;
    for (int i=0;i<n;i++) { s = s.addProduct(z0[i],z1[i]); }
    return s; }

  //--------------------------------------------------------------
  /** 'Exact' <code>(a*x) + y</code> (aka fma). */

  public static final BigFloat
  axpy (final double a,
        final double x,
        final double y) {
    // TODO: axpy should return exact s+e with both doubles?
    //  if true, could call add
    assert Double.isFinite(a);
    assert Double.isFinite(x);
    assert Double.isFinite(y);
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

  /** 'Exact' <code>(a*x) + y</code> (aka fma). */

  public static final BigFloat[] axpy (final double[] a,
                                       final double[] x,
                                       final double[] y) {
    final int n = a.length;
    assert n==x.length;
    assert n==y.length;
    final BigFloat[] bf = new BigFloat[n];
    for (int i=0;i<n;i++) { bf[i] = axpy(a[i],x[i],y[i]); }
    return bf; }

  /** 'Exact' <code>(this*x) + y</code> (aka fma). */

  public static final BigFloat
  axpy (final double a,
        final BigFloat x,
        final double y) {
    assert Double.isFinite(a);
    assert x.isFinite();
    assert Double.isFinite(y);
    return x.multiply(a).add(y); }

  /** Exact <code>(a*x) + y</code> (aka fma). */

  public static final BigFloat[] axpy (final double[] a,
                                       final BigFloat[] x,
                                       final double[] y) {
    final int n = x.length;
    assert n==a.length;
    assert n==y.length;
    final BigFloat[] bf = new BigFloat[n];
    for (int i=0;i<n;i++) { bf[i] = axpy(a[i],x[i],y[i]); }
    return bf; }

  //--------------------------------------------------------------

  public BigFloat addL1 (final double z0,
                         final double z1) {
    if (z0>z1) { return add(z0).add(-z1); }
    if (z0<z1) { return add(-z0).add(z1); }
    return this; }

  public final BigFloat
  addL1Distance (final double[] z0,
                 final double[] z1) {
    final int n = z0.length;
    assert n==z1.length;
    BigFloat s = this;
    for (int i=0;i<n;i++) { s = s.addL1(z0[i],z1[i]); }
    return s; }

  //--------------------------------------------------------------
  // internal special case: add 2*z0*z1

  private final BigFloat
  addProductTwice (final double z0,
                   final double z1) {
    assert isFinite();
    assert Double.isFinite(z0);
    assert Double.isFinite(z1);
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

    return
      add6(
        nonNegative(),
        significand(),
        exponent(),
        Doubles.nonNegative(z0)==Doubles.nonNegative(z1),
        BoundedNatural.product(t0,t1),
        e0+e1+1); }

  //--------------------------------------------------------------

  public final BigFloat
  addL2 (final double z0,
         final double z1) {
    final double mz1 = -z1;
    return
      add2(z0).add2(z1).addProductTwice(z0,mz1); }

  public final BigFloat
  addL2Distance (final double[] z0,
                 final double[] z1) {
    final int n = z0.length;
    assert n==z1.length;
    BigFloat s = this;
    for (int i=0;i<n;i++) { s = s.addL2(z0[i],z1[i]); }
    return s; }

  //--------------------------------------------------------------
  // Number methods
  //--------------------------------------------------------------
  /** Unsupported.
   * <br>
   * TODO: should it really truncate or round instead? Or
   * should there be more explicit round, floor, ceil, etc.?
   */
  @Override
  public final int intValue () {
    throw Exceptions.unsupportedOperation(this,"intValue"); }

  /** Unsupported.
   * <br>
   * TODO: should it really truncate or round instead? Or
   * should there be more explicit round, floor, ceil, etc.?
   */
  @Override
  public final long longValue () {
    throw Exceptions.unsupportedOperation(this,"longValue"); }

  //--------------------------------------------------------------
  /** get the least significant int word of (u >>> shift) */

  private static final int getShiftedInt (final BoundedNatural u,
                                          final int downShift) {
    assert 0<=downShift;
    final int iShift = (downShift>>>5);
    if (u.hiInt()<=iShift) { return 0; }
    final int rShift = (downShift & 0x1f);
    if (0==rShift) { return u.word(iShift); }
    final int r2 = 32-rShift;
    // TODO: optimize using startWord and endWord.
    final long lo = (u.uword(iShift) >>> rShift);
    final long hi = (u.uword(iShift+1) << r2);
    return (int) (hi | lo); }

  private static final boolean testBit (final int[] tt,
                                        final int nt,
                                        final int i) {
    assert 0<=nt;
    final int iShift = (i>>>5);
    if (nt<=iShift) { return false; }
    final int bShift = (i & 0x1F);
    return 0!=(tt[iShift] & (1<<bShift)); }

  private static final boolean roundUp (final BoundedNatural u,
                                        final int e) {
    final int nt = u.hiInt();
    if (nt<=(e>>>5)) { return false; }
    final int[] tt = u.words();
    final int e1 = e-1;
    final int n1 = (e1>>>5);
    if (nt<=n1) { return false; }
    final int w1 = (tt[n1] & (1<<(e1&0x1F)));
    if (0==w1) { return false; }
    final int e2 = e-2;
    if (0<=e2) {
      final int n2 = (e2>>>5);
      if (nt<=n2) { return false; }
      final int tt2 = tt[n2];
      for (int i=e2-(n2<<5);i>=0;i--) {
        if (0!=(tt2&(1<<i))) { return true; } }
      for (int i=n2-1;i>=0;i--) { if (0!=tt[i]) { return true; } } }
    return testBit(tt,nt,e); }

  public static final float floatValue (final boolean p0,
                                        final BoundedNatural s0,
                                        final int e0) {
    if (s0.isZero()) { return (p0 ? 0.0F : -0.0F); }
    // DANGER: what if hiBit isn't in the int range?
    final int eh = s0.hiBit();
    // TODO: does Math.clamp work here? faster?
    final int es =
      Math.clamp(
        eh - Floats.SIGNIFICAND_BITS,
        Floats.MINIMUM_EXPONENT_INTEGRAL_SIGNIFICAND - e0,
        Floats.MAXIMUM_EXPONENT_INTEGRAL_SIGNIFICAND - e0 - 1);
    if (0==es) {
      return Floats.floatMergeBits(p0,s0.intValue(),e0); }
    if (0 > es) {
      final int e1 = e0 + es;
      final int s1 = (s0.intValue() << -es);
      return Floats.floatMergeBits(p0,s1,e1); }
    if (eh <= es) { return (p0 ? 0.0F : -0.0F); }
    // eh > es > 0
    final boolean up = roundUp(s0,es);
    // TODO: faster way to select the right bits as a int?
    //final int s1 = s0.shiftDown(es).intValue();
    final int s1 = getShiftedInt(s0,es);
    final int e1 = e0 + es;
    if (up) {
      final int s2 = s1 + 1;
      if (Numbers.hiBit(s2) > Floats.SIGNIFICAND_BITS) { // carry
        // lost bit has to be zero, since there was just a carry
        final int s3 = (s2 >> 1);
        final int e3 = e1 + 1;
        return Floats.floatMergeBits(p0,s3,e3); }
      // no carry
      return Floats.floatMergeBits(p0,s2,e1); }
    // round down
    return Floats.floatMergeBits(p0,s1,e1); }

  /** @return closest half-even rounded <code>float</code>
   */

  @Override
  public final float floatValue () {
    return floatValue(nonNegative(),significand(),exponent()); }

  //--------------------------------------------------------------
  /** get the least significant two int words of
   * <code>(this>>>downShift)</code>
   * as a long.
   */

  private static final long getShiftedLong (final BoundedNatural u,
                                            final int downShift) {
    assert 0<=downShift;
    final int nt = u.hiInt();
    final int iShift = (downShift>>>5);
    if (nt<=iShift) { return 0L; }
    final long wi = u.uword(iShift);
    final int bShift = (downShift&0x1F);
    final int iShift1 = iShift+1;

    if (0==bShift) {
      if (nt==iShift1) { return wi; }
      return ((u.uword(iShift1)<<32) | wi); }

    final long lo0 = (wi>>>bShift);
    if (nt==iShift1) { return lo0; }
    final long u1 = u.uword(iShift1);
    final int rShift = 32-bShift;
    final long lo1 = (u1<<rShift);
    final long lo = lo1 | lo0;
    final long hi0 = (u1>>>bShift);
    final int iShift2 = iShift+2;
    if (nt==iShift2) { return (hi0 << 32) | lo; }
    final long hi1 = u.uword(iShift2)<<rShift;
    final long hi = hi1 | hi0;
    return (hi << 32) | lo; }

  //--------------------------------------------------------------
  /** @return closest half-even rounded <code>double</code>
   */

  public static final double doubleValue (final boolean p0,
                                          final BoundedNatural s0,
                                          final int e0) {
    if (s0.isZero()) { return (p0 ? 0.0 : -0.0); }
    final int eh = s0.hiBit();
    final int es =
      Math.clamp(
        eh - Doubles.SIGNIFICAND_BITS,
        Doubles.MINIMUM_EXPONENT_INTEGRAL_SIGNIFICAND - e0,
        Doubles.MAXIMUM_EXPONENT_INTEGRAL_SIGNIFICAND - e0 - 1);
    if ((eh-es)>Doubles.SIGNIFICAND_BITS) {
      return
        (p0 ?
         Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY); }
    if (0==es) {
      return Doubles.doubleMergeBits(p0,s0.longValue(),e0); }
    if (0 > es) {
      final int e1 = e0 + es;
      final long s1 = (s0.longValue() << -es);
      return Doubles.doubleMergeBits(p0,s1,e1); }
    if (eh <= es) { return (p0 ? 0.0 : -0.0); }
    // eh > es > 0
    final boolean up = roundUp(s0,es);
    final long s1 = getShiftedLong(s0,es);
    final int e1 = e0 + es;
    if (up) {
      final long s2 = s1 + 1L;
      if (Numbers.hiBit(s2) > Doubles.SIGNIFICAND_BITS) { // carry
        // lost bit has to be zero, since there was just a carry
        final long s3 = (s2>>1);
        final int e3 = e1 + 1;
        return Doubles.doubleMergeBits(p0,s3,e3); }
      // no carry
      return Doubles.doubleMergeBits(p0,s2,e1); }
    // round down
    return Doubles.doubleMergeBits(p0,s1,e1); }

  @Override
  public final double doubleValue () {
    return doubleValue(nonNegative(),significand(),exponent()); }

  //--------------------------------------------------------------
  // Comparable methods
  //--------------------------------------------------------------

  @Override
  public final int compareTo (final BigFloat q) {
    // see java.lang.Double.compareTo(Double)
    if (isNaN()) {
      if (q.isNaN()) { return 0; }
      return 1; }
    if (q.isNaN()) { return -1; }

    // handle pos and neg zeros here
    if (nonNegative() && (! q.nonNegative())) { return 1; }
    if ((! nonNegative()) && q.nonNegative()) { return -1; }

    // same signs, but may not be finite
    if (isPositiveInfinity()) {
      if (q.isPositiveInfinity()) { return 0; }
      return 1; }
    if (isNegativeInfinity()) {
      if (q.isNegativeInfinity()) { return 0; }
      return -1; }

    // <code>this</code> is finite
    if (q.isPositiveInfinity()) { return -1; }
    if (q.isNegativeInfinity()) { return 1; }

    // both finite
    final BoundedNatural t0 = significand();
    final BoundedNatural t1 = q.significand();
    final int e0 = exponent();
    final int e1 = q.exponent();
    final int c;
    //if (e0 <= e1) { c = t0.compareTo(t1.shiftUp(e1-e0)); }
    if (e0 <= e1) { c = t0.compareTo(t1.shiftUp(e1-e0)); }
    else { c = t0.shiftUp(e0-e1).compareTo(t1); }
    return (nonNegative() ? c : -c); }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------

  private static final boolean reducedEquals (final BigFloat a,
                                              final BigFloat b) {
    // assuming a and b have minimum significand and maximum
    // exponent
    if (a==b) { return true; }
    // assuming reduced
    if ((null==a) || ! a.significand().equals(b.significand())) { return false; }
    if (a.significand().isZero()) { return true; }
    return (a.nonNegative() == b.nonNegative())
      && (a.exponent() == b.exponent()); }

  /** See Double.equals(Object). Compares field values, so NaN==NaN!
   * Goal is to be consistent with <code>equals()</code> and
   * <code>hashCode()</code>
   */
  public final boolean equals (final BigFloat q) {
    if (isNaN() && q.isNaN()) { return true; }
    return reducedEquals(reduce(),q.reduce()); }

  @Override
  public final boolean equals (final Object o) {
    if (!(o instanceof BigFloat)) { return false; }
    return equals((BigFloat) o); }

  @Override
  public final int hashCode () {
    final BigFloat a = reduce();
    int h = 17;
    h = (31*h) + (a.nonNegative() ? 0 : 1);
    h = (31*h) + a.exponent();
    h = (31*h) + Objects.hash(a.significand());
    return h; }

  public final String toHexString () {
    return
      (nonNegative() ? "" : "-")
        + "0x" + (isNaN() ? "null" : significand().toHexString())
        // TODO: hex exponent?
        + "p" + exponent(); }

  @Override
  public final String toString () { return toHexString(); }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private BigFloat (final boolean p,
                    final BoundedNatural t,
                    final int e) {
    _nonNegative = p;
    _significand = t;
    _exponent = e; }

  //--------------------------------------------------------------

  //  private static final BigFloat reduce (final boolean p0,
  //                                        final BoundedNatural t0,
  //                                        final int e0) {
  //    //if (t0.isZero()) { return ZERO; }
  //    final int shift = t0.loBit();
  //    if (0>=shift) { return new BigFloat(p0,t0,e0); }
  //    return new BigFloat(p0, t0.shiftDown(shift),e0+shift); }

  private final BigFloat reduce () {
    if (! isFinite()) { return this; }
    final boolean p0 = nonNegative();
    final BoundedNatural t0 = significand();
    final int e0 = exponent();
    final int shift = t0.loBit();
    if (0>=shift) { return this; }
    return new BigFloat(p0, t0.shiftDown(shift),e0+shift); }

  public static final BigFloat valueOf (final boolean p,
                                        final BoundedNatural t,
                                        final int e) {
    //return reduce(p,t,e); }
    return new BigFloat(p,t,e); }

  //--------------------------------------------------------------

  private static final BigFloat valueOf (final boolean nonNegative,
                                         final long t0,
                                         final int e0)  {
    //if (0L==t0) { return ZERO; }
    //assert 0L<t0;
    final int shift = Numbers.loBit(t0);
    final long t1;
    final int e1;
    if ((0==shift)||(64==shift)) { t1=t0; e1=e0; }
    else { t1 = (t0 >>> shift); e1 = e0 + shift; }
    return valueOf(nonNegative,BoundedNatural.valueOf(t1),e1); }

  public static final BigFloat valueOf (final double z)  {
    if (Double.isNaN(z)) { return NaN; }
    if (Double.POSITIVE_INFINITY == z) { return POSITIVE_INFINITY; }
    if (Double.NEGATIVE_INFINITY == z) { return NEGATIVE_INFINITY; }
    return valueOf(
      Doubles.nonNegative(z),
      Doubles.significand(z),
      Doubles.exponent(z)); }

  //--------------------------------------------------------------

  private static final BigFloat valueOf (final boolean nonNegative,
                                         final int t0,
                                         final int e0)  {
    //if (0==t0) { return ZERO; }
    return valueOf(nonNegative,BoundedNatural.valueOf(t0),e0); }

  public static final BigFloat valueOf (final float z)  {
    if (Float.isNaN(z)) { return NaN; }
    if (Float.POSITIVE_INFINITY == z) { return POSITIVE_INFINITY; }
    if (Float.NEGATIVE_INFINITY == z) { return NEGATIVE_INFINITY; }
    return valueOf(
      Floats.nonNegative(z),
      Floats.significand(z),
      Floats.exponent(z)); }

  //--------------------------------------------------------------

  //  public static final BigFloat valueOf (final byte t)  {
  //    if (0<=t) { return valueOf(true,BoundedNatural.valueOf(t),0); }
  //    return valueOf(false,BoundedNatural.valueOf(-t),0); }
  //
  //  public static final BigFloat valueOf (final short t)  {
  //    if (0<=t) { return valueOf(true,BoundedNatural.valueOf(t),0); }
  //    return valueOf(false,BoundedNatural.valueOf(-t),0); }
  //
  //  public static final BigFloat valueOf (final int t)  {
  //    if (0<=t) { return valueOf(true,BoundedNatural.valueOf(t),0); }
  //    return valueOf(false,BoundedNatural.valueOf(-t),0); }

  //  public static final BigFloat valueOf (final long t)  {
  //    if (0<=t) { return valueOf(true,BoundedNatural.valueOf(t),0); }
  //    return valueOf(false,BoundedNatural.valueOf(-t),0); }

  //--------------------------------------------------------------

  //  public static final BigFloat valueOf (final Double x)  {
  //    return valueOf(x.doubleValue()); }
  //
  //  public static final BigFloat valueOf (final Float x)  {
  //    return valueOf(x.floatValue()); }
  //
  //  public static final BigFloat valueOf (final Byte x)  {
  //    return valueOf(x.byteValue()); }
  //
  //  public static final BigFloat valueOf (final Short x)  {
  //    return valueOf(x.shortValue()); }
  //
  //  public static final BigFloat valueOf (final Integer x)  {
  //    return valueOf(x.intValue()); }
  //
  //  public static final BigFloat valueOf (final Long x)  {
  //    return valueOf(x.longValue()); }
  //
  //  public static final BigFloat valueOf (final BigDecimal x)  {
  //    throw Exceptions.unsupportedOperation(null,"valueOf",x); }
  //
  //  public static final BigFloat valueOf (final BoundedNatural x)  {
  //    return valueOf(true,x,0); }
  //
  //  public static final BigFloat valueOf (final Number x)  {
  //    if (x instanceof Double) { return valueOf((Double) x); }
  //    if (x instanceof Float) { return valueOf((Float) x); }
  //    if (x instanceof Byte) { return valueOf((Byte) x); }
  //    if (x instanceof Short) { return valueOf((Short) x); }
  //    if (x instanceof Integer) { return valueOf((Integer) x); }
  //    if (x instanceof Long) { return valueOf((Long) x); }
  //    if (x instanceof BigDecimal) { return valueOf((BigDecimal) x); }
  //    throw Exceptions.unsupportedOperation(null,"valueOf",x); }
  //
  //  public static final BigFloat valueOf (final Object x)  {
  //    if (x instanceof BigFloat) { return (BigFloat) x; }
  //    if (x instanceof BoundedNatural) { return valueOf((BoundedNatural) x); }
  //    return valueOf((Number) x); }
  //
  //--------------------------------------------------------------
}
//--------------------------------------------------------------
