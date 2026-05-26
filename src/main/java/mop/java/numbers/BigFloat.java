package mop.java.numbers;

import mop.java.Exceptions;

import java.util.Objects;

import static mop.java.numbers.Doubles.doubleMergeBits;
import static mop.java.numbers.Floats.floatMergeBits;
import static mop.java.numbers.Numbers.loBit;

/** A sign times a {@link BoundedNatural} significand times 2 to a
 * <code>int</code> exponent.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-25
 */

@SuppressWarnings("unused")
public final class BigFloat implements Ringlike<BigFloat> {

  // TODO: infinite and NaN  representations and singletons.

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
  // Ringlike
  //--------------------------------------------------------------

  @Override
  public final boolean isZero () {
    return significand().isZero(); }

  @Override
  public final boolean isOne () {
    return equals(ONE); }

  //--------------------------------------------------------------

  @Override
  public final BigFloat negate () {
    if (isZero()) { return this; }
    return valueOf(! nonNegative(),significand(),exponent()); }

  @Override
  public final BigFloat abs () {
    if (nonNegative()) { return this; }
    return negate(); }

  //--------------------------------------------------------------

  private static final BigFloat add6 (final boolean p0,
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

  @Override
  public final BigFloat add (final BigFloat q) {
    return add6(
      nonNegative(),
      significand(),
      exponent(),
      q.nonNegative(),
      q.significand(),
      q.exponent()); }

  //--------------------------------------------------------------

  private static final BigFloat
  add6 (final boolean p0,
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

  private static final BigFloat
  add5 (final boolean p0,
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

  private final BigFloat
  add3 (final boolean p1,
        final long t11,
        final int e11) {
    //assert 0L<=t11;
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
    //assert Double.isFinite(z);
    // escape on zero needed for add()
    if (0.0==z) { return this; }
    return add3(
      Doubles.nonNegative(z),
      Doubles.significand(z),
      Doubles.exponent(z)); }

  public final BigFloat
  addAll (final double[] z) {
    //assert Double.isFinite(z);
    BigFloat s = this;
    for (final double zi : z) { s = s.add(zi); }
    return s; }

  //--------------------------------------------------------------

  public final BigFloat
  addAbs (final double z) {
    //assert Double.isFinite(z);
    // escape on zero needed for add()
    if (0.0==z) { return this; }
    return add3(
      true,
      Doubles.significand(z),
      Doubles.exponent(z)); }

  public final BigFloat
  addAbsAll (final double[] z) {
    //assert Double.isFinite(z);
    BigFloat s = this;
    for (final double zi : z) { s = s.addAbs(zi); }
    return s; }

  //--------------------------------------------------------------

  @Override
  public final BigFloat
  subtract (final BigFloat q) {
    return add6(
      nonNegative(),
      significand(),
      exponent(),
      ! q.nonNegative(),
      q.significand(),
      q.exponent()); }

  //--------------------------------------------------------------
  // used in Rational.addWithDenom()?

  public static final BigFloat
  product (final BoundedNatural x0,
           final boolean p1,
           final long x1) {
    //assert 0L<=x1;
    final int e0 = x0.loBit();
    final int e1 = loBit(x1);
    final BoundedNatural y0 =  ((0==e0) ? x0 : x0.shiftDown(e0));
    final long y1 = (((0==e1)||(64==e1)) ? x1 : (x1 >>> e1));
    return valueOf(p1,NaturalMultiply.multiply(y0,y1),e0+e1); }

  private final BigFloat
  multiply (final boolean p,
            final BoundedNatural t,
            final int e) {
    return valueOf(
      (nonNegative()==p),
      significand().multiply(t),
      Math.addExact(exponent(),e)); }

  @Override
  public final BigFloat
  multiply (final BigFloat q) {
    return
      multiply(q.nonNegative(),q.significand(),q.exponent()); }

  //--------------------------------------------------------------

  private final BigFloat
  multiply (final boolean p1,
            final long t11,
            final int e11) {

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
    //assert Double.isFinite(z);
    // escape on zero needed for add()
    if (0.0==z) { return this; }
    return multiply(
      Doubles.nonNegative(z),
      Doubles.significand(z),
      Doubles.exponent(z)); }

  //--------------------------------------------------------------

  @Override
  public final BigFloat
  square () {
    //if (isZero() ) { return EMPTY; }
    //if (isOne()) { return ONE; }
    return valueOf(true,significand().square(),2*exponent()); }

  //--------------------------------------------------------------

  public final BigFloat
  add2 (final double z) {
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

    return
      add6(
        Doubles.nonNegative(z0)==Doubles.nonNegative(z1),
        BoundedNatural.product(t0,t1),
        e0+e1,
        nonNegative(),
        significand(),
        exponent()); }

  public final BigFloat
  addProducts (final double[] z0,
               final double[] z1)  {
    final int n = z0.length;
    //assert n==z1.length;
    BigFloat s = this;
    for (int i=0;i<n;i++) { s = s.addProduct(z0[i],z1[i]); }
    return s; }

  //--------------------------------------------------------------
  /** 'Exact' <code>(a*x) + y</code> (aka fma). */

  public static final BigFloat
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

  /** 'Exact' <code>(a*x) + y</code> (aka fma). */

  public static final BigFloat[]
    axpy (final double[] a,
          final double[] x,
          final double[] y) {
    final int n = a.length;
    //assert n==x.length;
    //assert n==y.length;
    final BigFloat[] bf = new BigFloat[n];
    for (int i=0;i<n;i++) { bf[i] = axpy(a[i],x[i],y[i]); }
    return bf; }

  /** 'Exact' <code>(this*x) + y</code> (aka fma). */

  public static final BigFloat
  axpy (final double a,
        final BigFloat x,
        final double y) {
    return x.multiply(a).add(y); }

  /** Exact <code>(a*x) + y</code> (aka fma). */

  public static final BigFloat[] axpy (final double[] a,
                                       final BigFloat[] x,
                                       final double[] y) {
    final int n = x.length;
    //assert n==x.length;
    //assert n==y.length;
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
    //assert n==z1.length;
    BigFloat s = this;
    for (int i=0;i<n;i++) { s = s.addL1(z0[i],z1[i]); }
    return s; }

  //--------------------------------------------------------------
  // internal special case: add 2*z0*z1

  private final BigFloat
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
    //assert n==z1.length;
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
      return floatMergeBits(p0,s0.intValue(),e0); }
    if (0 > es) {
      final int e1 = e0 + es;
      final int s1 = (s0.intValue() << -es);
      return floatMergeBits(p0,s1,e1); }
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
        return floatMergeBits(p0,s3,e3); }
      // no carry
      return floatMergeBits(p0,s2,e1); }
    // round down
    return floatMergeBits(p0,s1,e1); }

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
      return doubleMergeBits(p0,s0.longValue(),e0); }
    if (0 > es) {
      final int e1 = e0 + es;
      final long s1 = (s0.longValue() << -es);
      return doubleMergeBits(p0,s1,e1); }
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
        return doubleMergeBits(p0,s3,e3); }
      // no carry
      return doubleMergeBits(p0,s2,e1); }
    // round down
    return doubleMergeBits(p0,s1,e1); }

  @Override
  public final double doubleValue () {
    return doubleValue(nonNegative(),significand(),exponent()); }

  //--------------------------------------------------------------
  // Comparable methods
  //--------------------------------------------------------------

  @Override
  public final int compareTo (final BigFloat q) {

    if (nonNegative() && (! q.nonNegative())) { return 1; }
    if ((! nonNegative()) && q.nonNegative()) { return -1; }
    // same signs
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

  public final boolean equals (final BigFloat q) {
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

  @Override
  public final String toString () {
    return
      (nonNegative() ? "" : "-")
      + "0x" + significand().toString()
      + "p" + exponent(); }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private BigFloat (final boolean p,
                    final BoundedNatural t,
                    final int e) {
    //assert null!=t0;
    _nonNegative = p;
    _significand = t;
    _exponent = e; }

  //--------------------------------------------------------------

  public static final BigFloat ZERO =
    new BigFloat(true,BoundedNatural.ZERO,0);

    private static final BigFloat ONE =
      new BigFloat(true,BoundedNatural.valueOf(1),0);

//    private static final BigFloat TWO =
  //    new BigFloat(true,BoundedNatural.valueOf(1),1);
  //
  //  private static final BigFloat TEN =
  //    new BigFloat(true,BoundedNatural.valueOf(5),1);
  //
  //  private static final BigFloat MINUS_ONE =
  //    new BigFloat(false,BoundedNatural.valueOf(1),0);

  //--------------------------------------------------------------

  //  private static final BigFloat reduce (final boolean p0,
  //                                        final BoundedNatural t0,
  //                                        final int e0) {
  //    //if (t0.isZero()) { return ZERO; }
  //    final int shift = t0.loBit();
  //    if (0>=shift) { return new BigFloat(p0,t0,e0); }
  //    return new BigFloat(p0, t0.shiftDown(shift),e0+shift); }

  private final BigFloat reduce () {
    final boolean p0 = nonNegative();
    final BoundedNatural t0 = significand();
    final int e0 = exponent();
    final int shift = t0.loBit();
    if (0>=shift) { return this; }
    return new BigFloat(p0, t0.shiftDown(shift),e0+shift); }

  public static final BigFloat valueOf (final boolean p,
                                        final BoundedNatural t,
                                        final int e) {
    //return reduce(p0,t0,e0); }
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

  public static final BigFloat valueOf (final float x)  {
    return valueOf(
      Floats.nonNegative(x),
      Floats.significand(x),
      Floats.exponent(x)); }

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
