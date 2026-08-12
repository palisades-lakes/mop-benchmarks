package mop.java.numbers;

import static mop.java.numbers.Numbers.*;
import static mop.java.numbers.Numbers.hiWord;

/** Multiplication of natural numbers.
 * <br>
 * Non-instantiable.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-11
 */

final class NaturalMultiply {

  //--------------------------------------------------------------
  // the equivalent method in BigInteger is marked @IntrinsicCandidate
  // it may not be possible to get the same performance with
  // ordinary java code?
  // UNSAFE: modifies contents of ww.

  private static final void multiplySimple (final int[] uu, final int nu,
                                            final int[] vv, final int nv,
                                            final int[] ww) {
    long carry;
    for (int iu=0;iu<nu;iu++) {
      carry = 0L;
      final long ui = unsigned(uu[iu]);
      for (int iv=0,iw=iu;iv<nv;iv++,iw++) {
        final long vi = unsigned(vv[iv]);
        final long wi = unsigned(ww[iw]);
        final long product = (vi*ui) + wi + carry;
        ww[iw] = (int) product;
        carry = (product>>>32); }
      final int i2 = iu+nv;
      ww[i2] = (int) carry; } }

  private static final BoundedNatural multiplySimple (final BoundedNatural u,
                                                      final BoundedNatural v) {
    // the equivalent method in BigInteger is marked @IntrinsicCandidate
    // it may not be possible to get the same performance with
    // ordinary java code?
    final int nu = u.hiInt();
    final int nv = v.hiInt();
    final int[] ww = new int[nu+nv];
    // UNSAFE: direct reference to internal arrays
    // UNSAFE: modifies contents of ww.
    multiplySimple(u.words(),nu,v.words(),nv,ww);
    return BoundedNatural.unsafe(ww); }

  //--------------------------------------------------------------
  static final BoundedNatural multiply (final BoundedNatural t,
                                        final long v) {
    if (0L==v) { return BoundedNatural.ZERO; }
    if (1L==v) { return t; }
    if (t.isZero()) { return BoundedNatural.ZERO; }
    assert 0L < v;
    final long hi = Numbers.hiWord(v);
    final long lo = Numbers.loWord(v);
    final int n0 = t.hiInt();
    final int[] tt = t.words();
    // TODO: assume minimal carry and allocate smaller array;
    // then fix when needed
    final int nv = n0+((hi==0)?1:2);
    final int[] vv = new int[nv];
    long carry = 0;
    int i=0;
    for (;i<n0;i++) {
      final long product = (unsigned(tt[i])*lo) + carry;
      vv[i] = (int) product;
      carry = (product>>>32); }
    vv[i] = (int) carry;
    if (0!=hi) {
      carry = 0;
      i=0;
      for (;i<n0;i++) {
        final int i1 = i+1;
        final long product = (unsigned(tt[i])*hi)
          + unsigned(vv[i1]) + carry;
        vv[i1] = (int) product;
        carry = (product>>>32); }
      vv[i+1]= (int) carry; }
    return BoundedNatural.unsafe(vv); }

  //--------------------------------------------------------------

//  private static final BoundedNatural multiply (final BoundedNatural t,
//                                                final long u,
//                                                final int upShift) {
//    ////assert isValid();
//    assert 0L<=u;
//    assert 0<=upShift;
//    if (0L==u) { return t.zero(); }
//    if (0==upShift) { return multiply(t,u); }
//    if (t.isZero()) { return t; }
//    return t.multiply(BoundedNatural.valueOf(u,upShift)); }

  //--------------------------------------------------------------

  private static final BoundedNatural multiplyKaratsuba (final BoundedNatural u,
                                                         final BoundedNatural v) {
    final int n0 = u.hiInt();
    final int n1 = v.hiInt();
    final int half = (Math.max(n0,n1)+1) / 2;
    final BoundedNatural xl = u.words(0,half);
    final BoundedNatural xh = u.words(half,u.hiInt());
    final BoundedNatural yl = v.words(0,half);
    final BoundedNatural yh = v.words(half,v.hiInt());
    final BoundedNatural p1 = xh.multiply(yh);
    final BoundedNatural p2 = xl.multiply(yl);
    final BoundedNatural p3 = xh.add(xl).multiply(yh.add(yl));
    final int h32 = half*32;
    final BoundedNatural p4 = p1.shiftUp(h32);
    final BoundedNatural p5 = p4.add(p3.subtract(p1).subtract(p2))
                                .shiftUp(h32);
    return p5.add(p2); }

  //--------------------------------------------------------------

  private static final BoundedNatural exactDivideBy3 (final BoundedNatural u) {
    final int n = u.hiInt();
    final int[] t = new int[n];
    long borrow = 0L;
    for (int i=0;i<n;i++) {
      final long x = u.uword(i);
      final long w = x-borrow;
      if (x<borrow) { borrow = 1L; }
      else { borrow = 0L; }
      // 0xAAAAAAAB is the modular inverse of 3 (mod 2^32). Thus,
      // the effect of this is to divide by 3 (mod 2^32).
      // This is much faster than division on most architectures.
      final long q = loWord(w*0xAAAAAAABL);
      t[i] = (int) q;
      // Check the borrow.
      if (q>=0x55555556L) {
        borrow++;
        if (q>=0xAAAAAAABL) { borrow++; } } }
    return BoundedNatural.unsafe(t); }

  //--------------------------------------------------------------

  private static final BoundedNatural getToomSlice (final BoundedNatural u,
                                                    final int lowerSize,
                                                    final int upperSize,
                                                    final int slice,
                                                    final int fullsize) {
    final int n = u.hiInt();
    final int offset = fullsize-n;
    int start;
    final int end;
    if (0==slice) { start = -offset; end = upperSize-1-offset; }
    else {
      start = (upperSize+((slice-1)*lowerSize))-offset;
      end = (start+lowerSize)-1; }
    if (start < 0) { start = 0; }
    if (end < 0) { return u.zero(); }
    final int sliceSize = (end-start) + 1;
    if (sliceSize<=0) { return u.zero(); }
    // While performing Toom-Cook, all slices are positive and
    // the sign is adjusted when the final number is composed.
    if ((0==start) && (sliceSize>=n)) { return u; }
    final int i1 = n-start;
    final int i0 = i1-sliceSize;
    return u.words(i0,i1); }

  //--------------------------------------------------------------

  private static final BoundedNatural multiplyToomCook3 (final BoundedNatural u,
                                                         final BoundedNatural v) {
    final int n0 = u.hiInt();
    final int n1 = v.hiInt();
    final int largest = Math.max(n0,n1);
    // words in the lower-order slices.
    final int k = (largest+2)/3; // ceil(largest/3)
    // words the highest-order slice.
    final int r = largest - (2 * k);

    // Obtain slices of the numbers. a2 and b2 are the most
    // significant bits of the numbers a and b, and a0 and b0 the
    // least significant.
    final BoundedNatural a2 = getToomSlice(u,k,r,0,largest);
    final BoundedNatural a1 = getToomSlice(u,k,r,1,largest);
    final BoundedNatural a0 = getToomSlice(u,k,r,2,largest);
    final BoundedNatural b2 = getToomSlice(v,k,r,0,largest);
    final BoundedNatural b1 = getToomSlice(v,k,r,1,largest);
    final BoundedNatural b0 = getToomSlice(v,k,r,2,largest);
    final BoundedNatural v0 = a0.multiply(b0);
    BoundedNatural da1 = a2.add(a0);
    BoundedNatural db1 = b2.add(b0);

    // might be negative
    final BoundedNatural da1_a1;
    final int ca = da1.compareTo(a1);
    if (0 < ca) { da1_a1 = da1.subtract(a1); }
    else { da1_a1 = a1.subtract(da1); }
    // might be negative
    final BoundedNatural db1_b1;
    final int cb = db1.compareTo(b1);
    if (0 < cb) { db1_b1 = db1.subtract(b1); }
    else { db1_b1 = b1.subtract(db1); }
    final int cv = ca * cb;
    final BoundedNatural vm1 = da1_a1.multiply(db1_b1);

    da1 = da1.add(a1);
    db1 = db1.add(b1);
    final BoundedNatural v1 = da1.multiply(db1);
    final BoundedNatural v2 =
      da1.add(a2).shiftUp(1).subtract(a0)
         .multiply(
           db1.add(b2).shiftUp(1).subtract(b0));

    final BoundedNatural vinf = a2.multiply(b2);

    // The algorithm requires two divisions by 2 and one by 3.
    // All divisions are known to be exact, that is, they do not
    // produce remainders, and all results are positive. The
    // divisions by 2 are implemented as right shifts which are
    // relatively efficient, leaving only an exact division by 3,
    // which is done by a specialized linear-time algorithm.
    BoundedNatural t2;
    // handle missing sign of vm1
    if (0 < cv) { t2 = exactDivideBy3(v2.subtract(vm1)); }
    else { t2 = exactDivideBy3(v2.add(vm1));}

    BoundedNatural tm1;
    // handle missing sign of vm1
    if (0 < cv) { tm1 = v1.subtract(vm1); }
    else { tm1 = v1.add(vm1); }
    tm1 = tm1.shiftDown(1);

    BoundedNatural t1 = v1.subtract(v0);
    t2 = t2.subtract(t1).shiftDown(1);
    t1 = t1.subtract(tm1);
    t1 = t1.subtract(vinf);
    t2 = t2.subtract(vinf.shiftUp(1));
    tm1 = tm1.subtract(t2);

    // Number of bits to shift left.
    final int ss = k * 32;

    return
      vinf.shiftUp(ss)
          .add(t2).shiftUp(ss)
          .add(t1).shiftUp(ss)
          .add(tm1).shiftUp(ss)
          .add(v0); }

  private static final int MULTIPLY_SQUARE_THRESHOLD = 20;
  private static final int KARATSUBA_THRESHOLD = 80;
  private static final int TOOM_COOK_THRESHOLD = 240;

  public static final BoundedNatural multiply (final BoundedNatural u,
                                               final BoundedNatural v) {
    if ((v.isZero()) || (u.isZero())) { return v.zero(); }
    final int n0 = v.hiInt();
    // equals is expensive and unlikely unless u==v
//    if (u.equals(v) && (n0>MULTIPLY_SQUARE_THRESHOLD)) {
      if ((u==v) && (n0>MULTIPLY_SQUARE_THRESHOLD)) {
        return v.square(); }
    if (n0==1) { return NaturalMultiply.multiply(u,v.uword(0)); }
    final int n1 = u.hiInt();
    if (n1==1) { return NaturalMultiply.multiply(v,u.uword(0)); }
    if ((n0<KARATSUBA_THRESHOLD) || (n1<KARATSUBA_THRESHOLD)) {
      return NaturalMultiply.multiplySimple(u,v); }
    if ((n0<TOOM_COOK_THRESHOLD) && (n1<TOOM_COOK_THRESHOLD)) {
      return multiplyKaratsuba(v,u); }
    return multiplyToomCook3(v,u); }

  //--------------------------------------------------------------
  // square
  //--------------------------------------------------------------
  /** From {@link java.math.BigInteger}:
   * <p>
   * The algorithm used here is adapted from Colin Plumb's C
   * library.
   * <p>
   * Technique: Consider the partial products in the
   * multiplication of "abcde" by itself:
   *<pre>
   * a b c d e
   * * a b c d e
   * ==================
   * ae be ce de ee
   * ad bd cd dd de
   * ac bc cc cd ce
   * ab bb bc bd be
   * aa ab ac ad ae
   * </pre>
   * Note that everything above the main diagonal:
   * <pre>
   * ae be ce de = (abcd) * e
   * ad bd cd = (abc) * d
   * ac bc = (ab) * c
   * ab = (a) * b
   * </pre>
   * is a copy of everything below the main diagonal:
   * <pre>
   * de
   * cd ce
   * bc bd be
   * ab ac ad ae
   * </pre>
   * Thus, the sum is 2 * (off the diagonal) + diagonal.
   * This is accumulated beginning with the diagonal (which
   * consist of the squares of the digits of the input), which
   * is then divided by two, the off-diagonal added, and
   * multiplied by two again. The low bit is simply a copy of
   * the low bit of the input, so it doesn't need special care.
   */

  static final BoundedNatural squareSimple (final BoundedNatural t) {
    final int nt = t.hiInt();
    final int[] tt = t.words();
    final int[] vv = new int[2*nt];
    // diagonal
    for (int i=0;i<nt;i++) {
      final long tti = unsigned(tt[i]);
      final long prod = tti*tti;
      final int i2 = 2*i;
      vv[i2] = (int) prod;
      vv[i2+1] = (int) hiWord(prod); }
    // off diagonal
    for (int i0=0;i0<nt;i0++) {
      long prod;// = 0L;
      long carry = 0L;
      final long tt0 = unsigned(tt[i0]);
      int i2 = 0;
      for (int i1=0;i1<i0;i1++) {
        i2 = i0+i1;
        prod = unsigned(vv[i2]) + carry;
        carry = hiWord(prod);
        long vvi2 = loWord(prod);
        final long tt1 = unsigned(tt[i1]);
        final long tt01 = tt0*tt1;
        prod = vvi2 + tt01;
        carry = hiWord(prod) + carry;
        vvi2 = loWord(prod);
        prod = vvi2 + tt01;
        carry = hiWord(prod) + carry;
        vv[i2] = (int) prod;
      }
      while ((0L!=carry)&&(i2<(2*nt))) {
        i2++;
        prod = unsigned(vv[i2]) + carry;
        carry = hiWord(prod);
        vv[i2] = (int) prod;  }
      assert 0L==carry;
    }
    return BoundedNatural.unsafe(vv); }


  static final BoundedNatural squareKaratsuba (final BoundedNatural u) {
    final int n = u.hiInt();
    final int half = (n+1)/2;
    final BoundedNatural xl = u.words(0,half);
    final BoundedNatural xh = u.words(half,n);
    final BoundedNatural xhs = xh.square();
    final BoundedNatural xls = xl.square();
    // (xh^2<<64) + (((xl+xh)^2-(xh^2+xl^2))<<32) + xl^2
    final int h32 = half*32;
    return
      xhs.shiftUp(h32)
      .add(
        xl.add(xh).square().subtract(xhs.add(xls)))
      .shiftUp(h32)
      .add(xls); }

  //--------------------------------------------------------------

  static final BoundedNatural squareToomCook3 (final BoundedNatural u) {
    final int n = u.hiInt();
    // k is the size (in ints) of the lower-order slices.
    final int k = (n+2)/3;   // Equal to ceil(largest/3)
    // r is the size (in ints) of the highest-order slice.
    final int r = n-(2*k);
    // Obtain slices of the numbers. a2 is the most significant
    // bits of the number, and a0 the least significant.
    final BoundedNatural a2 = getToomSlice(u,k,r,0,n);
    final BoundedNatural a1 = getToomSlice(u,k,r,1,n);
    final BoundedNatural a0 = getToomSlice(u,k,r,2,n);
    final BoundedNatural v0 = a0.square();
    final BoundedNatural da0 = a2.add(a0);
    // subtract here causes errors due to negative answer
    final BoundedNatural vm1 = da0.absDiff(a1).square();
    final BoundedNatural da1 = da0.add(a1);
    final BoundedNatural v1 = da1.square();
    final BoundedNatural vinf = a2.square();
    final BoundedNatural v2 = da1.add(a2).shiftUp(1).subtract(a0).square();

    // The algorithm requires two divisions by 2 and one by 3.
    // All divisions are known to be exact, that is, they do not
    // produce remainders, and all results are positive. The
    // divisions by 2 are implemented as right shifts which are
    // relatively efficient, leaving only a division by 3.
    // The division by 3 is done by an optimized algorithm for
    // this case.
    BoundedNatural t2 = exactDivideBy3(v2.subtract(vm1));
    BoundedNatural tm1 = v1.subtract(vm1).shiftDown(1);
    BoundedNatural t1 = v1.subtract(v0);
    t2 = t2.subtract(t1).shiftDown(1);
    t1 = t1.subtract(tm1);
    t1 = t1.subtract(vinf);
    t2 = t2.subtract(vinf.shiftUp(1));
    tm1 = tm1.subtract(t2);
    final int k32 = k*32;
    return
      vinf.shiftUp(k32)
      .add(t2).shiftUp(k32)
      .add(t1).shiftUp(k32)
      .add(tm1).shiftUp(k32)
      .add(v0); }

  //--------------------------------------------------------------
  // disable constructor
  //--------------------------------------------------------------

  private NaturalMultiply () {
    throw new
    UnsupportedOperationException(
      "can't instantiate " + getClass().getCanonicalName()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
