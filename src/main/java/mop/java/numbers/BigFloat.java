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
 *   <dt>isNaN()</dt>  <dd><code>boolean</code></dd>
 *   <dt>isInfinite()</dt> <dd><code>boolean</code> If <code>true</code>
 *   positive or negative infinity depending on
 *   <code>nonnegative()</code>. Ignored if <code>isNaN()</code>.</dd>
 * <dt>nonNegative()</dt> <dd><code>boolean</code></dd>
 * <dt>exponent()</dt> <dd>signed 32bit <code>int</code>.
 * Values in [<code>Integer.MIN_VALUE</code>,
 * <code>Integer.MAX_VALUE]</code>.</dd>
 * <dt>significand()</dt> <dd>unsigned integer as
 * <code>BoundedNatural</code>,
 * an non-negative integer in [0,<code>BoundedNatural.maxValue()</code>].
 * Ignored if <code>isNaN()</code> or <code>isInfinite()</code>,
 * and then should <code>null</code>.</dd>
 * </dl>
 * Finite values are
 * <code>(nonNegative()?1:-1) * significand() * 2^exponent()</code>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-24
 */

public final class BigFloat implements Ringlike<BigFloat> {
// TODO: should this be a record class?

  //--------------------------------------------------------------
  // instance fields and methods
  //--------------------------------------------------------------
  // TODO: can't be both NaN and infinite. Better way to capture that?

  private final boolean _isNaN;
  public final boolean isNaN () { return _isNaN; }

  private final boolean _isInfinite;
  /** Positive or negative infinity if <code>true</code>. */
  public final boolean isInfinite () { return _isInfinite; }

  public final boolean isFinite () {
    return ! (isNaN() || isInfinite()); }

  private final boolean _nonNegative;
  public final boolean nonNegative () { return _nonNegative; }

  public final boolean isPositiveInfinity () {
    return isInfinite() && nonNegative(); }

  public final boolean isNegativeInfinity () {
    return isInfinite() && (! nonNegative()); }

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
    makeFinite(true, BoundedNatural.ZERO, 0);

  public static final BigFloat POSITIVE_ZERO = ZERO;

  public static final BigFloat NEGATIVE_ZERO =
    makeFinite(false, BoundedNatural.ZERO, 0);

  private static final BigFloat ONE =
    makeFinite(true, BoundedNatural.valueOf(1), 0);

  public static final BigFloat NaN = makeNaN();

  public static final BigFloat POSITIVE_INFINITY = makeInfinity(true);

  public static final BigFloat NEGATIVE_INFINITY = makeInfinity(false);

  //    private static final BigFloat TWO =
  //    finite(true,BoundedNatural.valueOf(1),1);
  //
  //  private static final BigFloat TEN =
  //    finite(true,BoundedNatural.valueOf(5),1);
  //
  //  private static final BigFloat MINUS_ONE =
  //    finite(false,BoundedNatural.valueOf(1),0);

  //--------------------------------------------------------------
  // Value classification
  //--------------------------------------------------------------

  /** Note: has positive and negative zero, like <code>double</code>
   * via nonNegative().
   */
  @Override
  public final boolean isZero () {
    return equals(ZERO); }

  @Override
  public final boolean isOne () { return equals(ONE); }

  //--------------------------------------------------------------

  @Override
  public final BigFloat negate () {
    if (isFinite()) {
      // positive and negative zeros!
      return valueOf(! nonNegative(),significand(),exponent()); }
    if (isNaN()) { return this; }
    // TODO: is saving a few new instances worth this?
    if (isPositiveInfinity()) { return NEGATIVE_INFINITY; }
    if (isNegativeInfinity()) { return POSITIVE_INFINITY; }
    throw new UnsupportedOperationException("shouldn't get here"); }

  @Override
  public final BigFloat abs () {
    if (isFinite()) {
      if (nonNegative()) { return this; }
      return valueOf(true,significand(),exponent()); }
    if (isNaN()) { return this; }
    throw new UnsupportedOperationException("shouldn't get here"); }

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

    if (isFinite() && q.isFinite()) {
      return add6(
        nonNegative(), significand(), exponent(),
        q.nonNegative(), q.significand(), q.exponent()); }

    if (isNaN() || q.isNaN()) { return NaN; }
    if (isPositiveInfinity()) {
      if (q.isNegativeInfinity()) { return NaN; }
      return POSITIVE_INFINITY; }
    if (isNegativeInfinity()) {
      if (q.isPositiveInfinity()) { return NaN; }
      return NEGATIVE_INFINITY; }
    throw new UnsupportedOperationException("shouldn't get here"); }

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
    if (isFinite() && Double.isFinite(z)) {
      // escape on zero needed for add()
      if (0.0==z) { return this; }
      return add3(
        Doubles.nonNegative(z),
        Doubles.significand(z),
        Doubles.exponent(z)); }
    if (isNaN()) { return NaN; }
    if (Double.isNaN(z)) { return NaN; }
    if (isPositiveInfinity()) {
      if (Double.NEGATIVE_INFINITY==z) { return NaN; }
      return POSITIVE_INFINITY; }
    if (isNegativeInfinity()) {
      if (Double.POSITIVE_INFINITY==z) { return NaN; }
      return NEGATIVE_INFINITY; }

    throw new UnsupportedOperationException("shouldn't get here"); }

  public final BigFloat
  addAll (final double[] z) {
    assert isFinite();
    BigFloat s = this;
    for (final double zi : z) { s = s.add(zi); }
    return s; }

  //--------------------------------------------------------------

  public final BigFloat
  addAbs (final double z) {
    if (isFinite() && Double.isFinite(z)) {
      // escape on zero needed for add()
      if (0.0==z) { return this; }
      return add3(
        true,
        Doubles.significand(z),
        Doubles.exponent(z)); }
    if (isNaN()) { return NaN; }
    if (Double.isNaN(z)) { return NaN; }
    if (isPositiveInfinity()) { return POSITIVE_INFINITY; }
    if (isNegativeInfinity()) {
      if (Double.NEGATIVE_INFINITY==z) { return NaN; }
      return NEGATIVE_INFINITY; }

    throw new UnsupportedOperationException("shouldn't get here"); }

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
    if (isFinite() && q.isFinite()) {
      return add6(
        nonNegative(),
        significand(),
        exponent(),
        ! q.nonNegative(),
        q.significand(),
        q.exponent()); }
    if (isNaN() || q.isNaN()) { return NaN; }
    if (isPositiveInfinity()) {
      if (q.isPositiveInfinity()) { return NaN; }
      return POSITIVE_INFINITY; }
    if (isNegativeInfinity()) {
      if (q.isNegativeInfinity()) { return NaN; }
      return NEGATIVE_INFINITY; }

    throw new UnsupportedOperationException("shouldn't get here"); }

  //--------------------------------------------------------------
  /** Return the "exact" value of <code>z0+z1</code>,
   * without intermediate <code>BigFloat</code> instances.
   */

  public static final BigFloat sum (final double z0,
                                    final double z1) {
    if (Double.isFinite(z0) && Double.isFinite(z1)) {
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
      if (0.0 == z0) { return BigFloat.valueOf(z1); }
      if (0.0 == z1) { return BigFloat.valueOf(z0); }
      final int e0 = Doubles.exponent(z0);
      final int e1 = Doubles.exponent(z1);
      if (e0<e1) { return sum(z1,z0); }
      final BoundedNatural s = BoundedNatural.valueOf(t0, e0-e1);
      if (p0 == p1) { return BigFloat.valueOf(p0, s.add(t1), e1); }
      if (p0) {
        if (0 <= s.compareTo(t1)) {
          return BigFloat.valueOf(true, s.subtract(t1), e1); }
        return BigFloat.valueOf(false, s.subtractFrom(t1), e1); }
      if (0 <= s.compareTo(t1)) {
        return BigFloat.valueOf(false, s.subtract(t1), e1); }
      return BigFloat.valueOf(true, s.subtractFrom(t1), e1); }

    if (Double.isNaN(z0) || Double.isNaN(z1)) { return NaN; }
    if (z0 == Double.POSITIVE_INFINITY) {
      if (Double.NEGATIVE_INFINITY == z1) { return NaN; }
      return POSITIVE_INFINITY; }
    if (z0 == Double.NEGATIVE_INFINITY) {
      if (Double.POSITIVE_INFINITY == z1) { return NaN; }
      return NEGATIVE_INFINITY; }

    throw new UnsupportedOperationException("shouldn't get here"); }

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
    final boolean sameSigns = (nonNegative()==q.nonNegative());
    if (isFinite() && q.isFinite()) {
      return valueOf(
        sameSigns,
        significand().multiply(q.significand()),
        Math.addExact(exponent(),q.exponent()));}
    if (isNaN() || q.isNaN()) { return NaN; }
    if (isPositiveInfinity()) {
      if (sameSigns) { return POSITIVE_INFINITY; }
      return NEGATIVE_INFINITY; }
    if (isNegativeInfinity()) {
      if (sameSigns) { return POSITIVE_INFINITY; }
      return NEGATIVE_INFINITY; }
    throw new UnsupportedOperationException("shouldn't get here"); }

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
    if (isFinite() && Double.isFinite(z)) {
      return multiply(
        Doubles.nonNegative(z),
        Doubles.significand(z),
        Doubles.exponent(z)); }

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

    throw new UnsupportedOperationException("shouldn't get here"); }


//--------------------------------------------------------------

  @Override
  public final BigFloat
  square () {
    if (isFinite()) {
      if (isZero() ) { return ZERO; }
      if (isOne()) { return ONE; }
      return valueOf(true, significand().square(),2*exponent()); }
    if (isNaN()) { return NaN; }
    if (isPositiveInfinity()) { return POSITIVE_INFINITY; }
    if (isNegativeInfinity()) { return POSITIVE_INFINITY; }
    throw new UnsupportedOperationException("shouldn't get here"); }

  //--------------------------------------------------------------
  // geometry
  //--------------------------------------------------------------
  /** Compute squared l2norm without intermediate instances. */

  public static final BigFloat l2norm2 (final BigFloat x,
                                        final BigFloat y) {
    if (x.isFinite() && y.isFinite()) {
      return add6(true,x.significand().square(),2*x.exponent(),
                  true,y.significand().square(),2*y.exponent()); }
    if (x.isNaN() || y.isNaN()) { return NaN; }
    if ((! x.isFinite()) || (! y.isFinite())) {
      return POSITIVE_INFINITY; }

    throw new UnsupportedOperationException("shouldn't get here"); }

  //--------------------------------------------------------------

  public static final BigFloat
  wedge (final BigFloat x0,
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
    if (isFinite() && Double.isFinite(z)) {
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
        nonNegative(), significand(), exponent(), true, t2, e2); }
    if (isNaN() || Double.isNaN(z)) { return NaN; }
    if (isNegativeInfinity()) {
      if (Double.isFinite(z)) { return NEGATIVE_INFINITY; }
      return NaN; }
    if (isPositiveInfinity()) { return POSITIVE_INFINITY; }

    throw new UnsupportedOperationException("shouldn't get here"); }


  public final BigFloat
  add2All (final double[] z) {
    BigFloat s = this;
    for (final double zi : z) { s = s.add2(zi); }
    return s; }

  //--------------------------------------------------------------

  public final BigFloat
  addProduct (final double z0,
              final double z1) {
    final boolean sameSigns =
      (Doubles.nonNegative(z0) == Doubles.nonNegative(z1));
    if (isFinite() && Double.isFinite(z0) && Double.isFinite(z1)) {
      if ((0.0==z0) || (0.0==z1)) { return this; }
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
          sameSigns,
          BoundedNatural.product(t0,t1),
          e0+e1,
          nonNegative(),
          significand(),
          exponent()); }

      if (isNaN() || Double.isNaN(z0) || Double.isNaN(z1)) { return NaN; }
    final boolean infiniteProduct =
      Double.isInfinite(z0) || Double.isInfinite(z1);
    if (sameSigns) {
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

    throw new UnsupportedOperationException("shouldn't get here"); }


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
  @SuppressWarnings("unused")
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
  @SuppressWarnings("unused")
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
  // Dynamic method lookup
  // Arithmetic cases as needed.
  //--------------------------------------------------------------

  public final BigFloat add (final Object q) {
    return switch (q) {
      case BigFloat qq -> add(qq);
      case Double qq -> add(qq);
      default ->
        throw new UnsupportedOperationException(
          "No method to add BigFloat to " +
            q.getClass().getName() ); }; }

  public final BigFloat subtract (final Object q) {
    return switch (q) {
      case BigFloat qq -> subtract(qq);
      case Double qq -> subtract(qq);
      default ->
        throw new UnsupportedOperationException(
          "No method to subtract BigFloat to " +
            q.getClass().getName() ); }; }

  public final BigFloat multiply (final Object q) {
    return switch (q) {
      case BigFloat qq -> multiply(qq);
      case Double qq -> multiply(qq);
      default ->
        throw new UnsupportedOperationException(
          "No method to multiply BigFloat by " +
            q.getClass().getName() ); }; }

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
  // methods equivalent to ==,<=,>=,>,< for double
  // Double.equals and Double.compareTo have results that differ
  // from the corresponding <code>double</code> operators.
  // TODO: optimize using appropriate benchmarks
  //--------------------------------------------------------------

  public final boolean opEQ (final BigFloat q) {
    if (isNaN() || q.isNaN()) { return false; }
    if (this==q) { return true; } // identical objects
    if (isZero()) { return q.isZero(); } // regardless of +/- zero
    return 0==compareTo(q); }

  public final boolean opGT (final BigFloat q) {
    if (isNaN() || q.isNaN()) { return false; }
    if (this == q) { return false; } // identical objects
    if (isZero() && q.isZero()) { return false; } // regardless of +/- zero
    return 0 < compareTo(q);  }

  // TODO: optimize?
  public final boolean opGE (final BigFloat q) {
    if (isNaN() || q.isNaN()) { return false; }
    return  opEQ(q) || opGT(q); }

  @SuppressWarnings("unused")
  public final boolean opLT (final BigFloat q) {
    if (isNaN() || q.isNaN()) { return false; }
    return ! opGE(q); }

  @SuppressWarnings("unused")
  public final boolean opLE (final BigFloat q) {
    if (isNaN() || q.isNaN()) { return false; }
    return ! opGT(q); }

  //--------------------------------------------------------------
  // WARNING: Doubles.exponent() and BigFloat.exponent() are not
  // directly comparable!!!

  public final boolean opEQ (final double q) {
    if (isNaN() || Double.isNaN(q)) { return false; }
    if (isZero()) { return q==0.0; } // regardless of +/- zero
    return 0==compareTo(q); }

  public final boolean opGT (final double q) {
    if (isNaN() || Double.isNaN(q)) { return false; }
    if (isZero() && (q == 0.0)) { return false; } // regardless of +/- zero
    return 0 < compareTo(q);  }

//  public final boolean opEQ (final double q) {
//    if (isNaN() || Double.isNaN(q)) { return false; }
//    if (isZero()) { return 0.0==q; } // regardless of +/- zero
//    // TODO: mark when reduced
//    final BigFloat r0 = reduce();
//    return (r0.significand().equals(Doubles.significand(q)))
//      && (r0.nonNegative() == Doubles.nonNegative(q))
//      && (r0.exponent() == Doubles.exponent(q)); }
//
//  public final boolean opGT (final double q) {
//    if (isNaN() || Double.isNaN(q)) { return false; }
//    if (isZero()) { return 0.0>q; } // regardless of +/- zero
//    if (nonNegative() && (! Doubles.nonNegative(q))) { return true; }
//    if ((! nonNegative()) &&  Doubles.nonNegative(q)) { return false; }
//    // same signs
//    // TODO: cache reduced flag?
//    final BigFloat r0 = reduce();
//    if (r0.nonNegative()) { // both positive
//      if (r0.exponent() > Doubles.exponent(q)) { return true; }
//      if (r0.exponent() < Doubles.exponent(q)) { return false; }
//      return (r0.significand().compareTo(Doubles.significand(q)) > 0); }
//    // else both negative
//    if (r0.exponent() < Doubles.exponent(q)) { return true; }
//    if (r0.exponent() > Doubles.exponent(q)) { return false; }
//    return (r0.significand().compareTo(Doubles.significand(q)) < 0); }

  // TODO: optimize?
  public final boolean opGE (final double q) {
    if (isNaN() || Double.isNaN(q)) { return false; }
    return  opEQ(q) || opGT(q); }

  public final boolean opLT (final double q) {
    if (isNaN() || Double.isNaN(q)) { return false; }
    return ! opGE(q); }

  public final boolean opLE (final double q) {
    if (isNaN() || Double.isNaN(q)) { return false; }
    return ! opGT(q); }

  //--------------------------------------------------------------
  // Comparable methods
  //--------------------------------------------------------------

  @Override
  public final int compareTo (final BigFloat q) {
    if (this == q) { return 0; }
    // see java.lang.Double.compareTo(Double)
    if (isNaN()) { return q.isNaN() ? 0 : 1; }
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

  public final int compareTo (final double q) {
    // see java.lang.Double.compareTo(Double)
    if (isNaN()) { return Double.isNaN(q) ?  0 : 1; }
    if (Double.isNaN(q)) { return -1; }

    // handle pos and neg zeros here
    if (nonNegative() && (q<0.0)) { return 1; }
    if ((! nonNegative()) && (q>=0.0)) { return -1; }

    // same signs, but may not be finite
    if (isPositiveInfinity()) {
      return (q == Double.POSITIVE_INFINITY) ? 0 : 1; }
    if (isNegativeInfinity()) {
      return (q == Double.NEGATIVE_INFINITY) ?  0 : -1; }

    // <code>this</code> is finite
    if (q == Double.POSITIVE_INFINITY) { return -1; }
    if (q == Double.NEGATIVE_INFINITY) { return 1; }

    // both finite
    final BoundedNatural t0 = significand();
    final BoundedNatural t1 =
      BoundedNatural.valueOf(Doubles.significand(q));
    final int e0 = exponent();
    final int e1 = Doubles.exponent(q);
    final int c;
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
    if (isNaN()) { return q.isNaN(); }
    if (isPositiveInfinity()) { return q.isPositiveInfinity(); }
    if (isNegativeInfinity()) { return q.isNegativeInfinity(); }
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
    if (isNaN()) { return "NaN"; }
    if (isInfinite()) {
      if (nonNegative()) { return "POSITIVE_INFINITY"; }
      return "NEGATIVE_INFINITY"; }
    return
      (nonNegative() ? "" : "-")
        + "0x" + significand().toHexString() +
        // TODO: hex exponent? Double.toHexString() prints decimal
        "p" + exponent(); }

  @Override
  public final String toString () { return toHexString(); }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private BigFloat (final boolean nan,
                    final boolean infinite,
                    final boolean p,
                    final BoundedNatural t,
                    final int e) {
    assert ! (nan && infinite);
    _isNaN = nan;
    _isInfinite = infinite;
    _nonNegative = p;
    _significand = t;
    _exponent = e; }

  private static final BigFloat makeFinite (final boolean p,
                                            final BoundedNatural t,
                                            final int e) {
    return new BigFloat(false,false,p,t,e); }

  private static final BigFloat makeNaN () {
    return new BigFloat(true,false,true,null,0); }

  private static final BigFloat makeInfinity (final boolean p) {
    return new BigFloat(false,true,p,null,0); }

  //--------------------------------------------------------------

  //  private static final BigFloat reduce (final boolean p0,
  //                                        final BoundedNatural t0,
  //                                        final int e0) {
  //    //if (t0.isZero()) { return ZERO; }
  //    final int shift = t0.loBit();
  //    if (0>=shift) { return finite(p0,t0,e0); }
  //    return finite(p0, t0.shiftDown(shift),e0+shift); }

  public final BigFloat reduce () {
    if (! isFinite()) { return this; }
    final boolean p0 = nonNegative();
    final BoundedNatural t0 = significand();
    final int e0 = exponent();
    final int shift = t0.loBit();
    if (0>=shift) { return this; }
    return makeFinite(p0, t0.shiftDown(shift), e0+shift); }

  public static final BigFloat valueOf (final boolean p,
                                        final BoundedNatural t,
                                        final int e) {
    //return reduce(p,t,e); }
    return makeFinite(p, t, e); }

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
    if (Double.isFinite(z)) {
      return valueOf(
        Doubles.nonNegative(z),
        Doubles.significand(z),
        Doubles.exponent(z)); }
    if (Double.isNaN(z)) { return NaN; }
    if (Double.POSITIVE_INFINITY == z) { return POSITIVE_INFINITY; }
    if (Double.NEGATIVE_INFINITY == z) { return NEGATIVE_INFINITY; }

    throw new UnsupportedOperationException("shouldn't get here"); }

//--------------------------------------------------------------

  private static final BigFloat valueOf (final boolean nonNegative,
                                         final int t0,
                                         final int e0)  {
    //if (0==t0) { return ZERO; }
    return valueOf(nonNegative,BoundedNatural.valueOf(t0),e0); }

  public static final BigFloat valueOf (final float z)  {
    if (Float.isFinite(z)) {
      return valueOf(
        Floats.nonNegative(z),
        Floats.significand(z),
        Floats.exponent(z)); }
    if (Float.isNaN(z)) { return NaN; }
    if (Float.POSITIVE_INFINITY == z) { return POSITIVE_INFINITY; }
    if (Float.NEGATIVE_INFINITY == z) { return NEGATIVE_INFINITY; }

    throw new UnsupportedOperationException("shouldn't get here"); }

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
