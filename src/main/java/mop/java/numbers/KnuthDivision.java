package mop.java.numbers;

import java.util.Arrays;
import java.util.List;

import static mop.java.numbers.Numbers.*;

/** Division of natural numbers by the Knuth algorithm.
 * <br>
 * Isolated into a pure static (no instances) class with minimal
 * dependencies. Fortran-style representation of natural numbers
 * as bare little-endian <code>int[]</code>, with the elements treated
 * as unsigned. There is limited evidence that this improves
 * performance, but it is unclear to me if the lost safety/modularity
 * is worth it. So may move code back into
 * {@link BoundedNatural} at some point.
 * <br>
 * Non-instantiable.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2024-01-02
 */

final class KnuthDivision {
  
  //--------------------------------------------------------------
  private static final int KNUTH_POW2_THRESH_LEN = 6;
  private static final int KNUTH_POW2_THRESH_ZEROS = 3*32;

  /** shifted fused multiply-subtract.
   * <br>
   * DANGER: read-only <code>v</code>,
   * but destroys <code>u<</code>.
   */

  private static final List fms (final int[] u,
                                 final int n0,
                                 final long x,
                                 final int[] v,
                                 final int n1,
                                 final int i0) {
    final int n2 = n0-1-i0;
    final int[] uu;
    if (n2 <= u.length) { uu = u; }
    else { uu = Arrays.copyOf(u, n2); }
    long carry = 0;
    for (int i=n2-n1,j=0;j<n1;j++,i++) {
      final long prod = (unsigned(v[j])*x) + carry;
      final long diff = unsigned(uu[i])-prod;
      uu[i] = (int) diff;
      carry = hiWord(prod);
      // TODO: is this related to possibility x*u > this,
      // so difference is negative?
      if (loWord(diff) > unsigned(~((int)prod))) { carry++; } }
    return List.of(uu, loWord(carry)); }

  /** A primitive used for division. This method adds in one
   * multiple of the divisor a back to the dividend result at a
   * specified start. It is used when qhat was estimated too
   * large, and must be adjusted.
   * <p>
   * Never called in testing, (prob less than 2^32),
   * so DANGER that changes have made it incorrect...
   */

  private static final int[] divadd (final int[] u,
                                     final int n0,
                                     final int[] v,
                                     final int n1,
                                     final int i0) {
    final int n = n0 - 1 - i0;
    final int off = n - n1;
    int[] ww = Arrays.copyOf(u,n);
    long carry = 0;
    for (int i=off,j=0;j<n1;i++,j++) {
      final long sum = unsigned(v[j]) + unsigned(ww[i]) + carry;
      ww[i] = (int) sum;
      carry = sum >>> 32; }
    return ww; }

  private static final int[][] knuthDivision (final int[] u,
                                              final int[] v) {
      final int nv = NaturalInts.hiInt(v);
      final int lShift =
        (nv>=1 ? Integer.numberOfLeadingZeros(v[nv-1]) : 0);
      final int[] d = NaturalInts.shiftUp(v, lShift);
      final int nd = NaturalInts.hiInt(d);
      // TODO: shiftUp with possibly oversized destination array
      int[] r = NaturalInts.shiftUp(u, lShift);
      final int nr0 = NaturalInts.hiInt(r);
      final int nr = Math.max(nd,nr0) + 1;
      if (nr > r.length) { r = Arrays.copyOf(r,nr); }
      final int nq = nr - nd;
      int[] qq = new int[nq];
      final long dh = unsigned(d[nd-1]);
      final long dl = unsigned(d[nd-2]);
      for (int j = 0; j < (nq - 1); j++) {
        boolean correctQhat = true;
        final int i = nr - j - 1;
        final long rh = unsigned(r[i]);
        final long rm = unsigned(r[i - 1]);
        long qhat; long qrem;
        if (rh == dh) {
          qhat = 0xFFFFFFFFL; qrem = rh + rm;
          correctQhat = (qrem >= rh); }
        else {
          final long nChunk = (rh << 32) | rm;
          if (nChunk >= 0) {
            qhat = loWord(nChunk / dh);
            qrem = loWord(nChunk - (qhat * dh)); }
          else {
            final long tmp = Ints.divWord(nChunk, dh);
            qhat = loWord(tmp); qrem = hiWord(tmp); } }
        if (0L == qhat) { continue; }
        if (correctQhat) {
          final long nl = unsigned(r[i - 2]);
          long rs = (qrem << 32) | nl;
          long estProduct = dl * qhat;
          if (Long.compareUnsigned(estProduct, rs) > 0) {
            qhat--;
            qrem = loWord(qrem + dh);
            if (qrem >= dh) {
              estProduct -= (dl);
              rs = (qrem << 32) | nl;
              if (Long.compareUnsigned(estProduct, rs) > 0) { qhat--; } } } }
        r[i] = 0;
        final List rc = fms(r, nr, qhat, d, nd, j);
        r = (int[]) rc.get(0);
        final long borrow = (Long) rc.get(1);
        if (borrow > rh) {
          r = divadd(r, nr, d, nd, j);
          qhat--; }
        qq[nq-1-j] = (int) qhat; }

      long qhat; long qrem;
      boolean correctQhat = true;
      final long nh = unsigned(r[nd]);
      final long nm = unsigned(r[nd - 1]);
      if (nh == dh) {
        qhat = 0xFFFFFFFFL; qrem = nh + nm; correctQhat = (qrem >= nh); }
      else {
        final long nChunk = (nh << 32) | nm;
        if (nChunk >= 0) {
          qhat = loWord(nChunk / dh);
          qrem = loWord(nChunk - (qhat * dh)); }
        else {
          final long tmp = Ints.divWord(nChunk, dh);
          qhat = loWord(tmp); qrem = hiWord(tmp); } }
      if (0L != qhat) {
        if (correctQhat) {
          final long nl = unsigned(r[nd - 2]);
          long rs = (qrem << 32) | nl;
          long estProduct = dl * qhat;
          if (Long.compareUnsigned(estProduct, rs) > 0) {
            qhat--; qrem = loWord(qrem + dh);
            if (qrem >= dh) {
              estProduct -= (dl);
              rs = (qrem << 32) | nl;
              if (Long.compareUnsigned(estProduct, rs) > 0) { qhat--; } } } }
        r[nd] = 0;
        final List rc = fms(r, nr, qhat, d, nd, nq - 1);
        r = (int[]) rc.get(0);
        final long borrow = (Long) rc.get(1);
        if (borrow > nh) {
          r = divadd(r, nr, d, nd, nq - 1);
          qhat--; }
        qq[0] = (int) qhat; }

      // TODO: shiftDown in place
      if (0 < lShift) { r = NaturalInts.shiftDown(r, lShift); }
      return new int[][] { qq, r, }; }

  public static final int[][] divideAndRemainder (final int[] u,
                                                  final int[] v) {
    // Cancel common powers of 2 if above KNUTH_POW2_* thresholds
    if (NaturalInts.hiInt(u) >= KNUTH_POW2_THRESH_LEN) {
      final int shift = Math.min(NaturalInts.loBit(u),
                                 NaturalInts.loBit(v));
      if (shift >= KNUTH_POW2_THRESH_ZEROS) {
        final int[] a = NaturalInts.shiftDown(u, shift);
        final int[] b = NaturalInts.shiftDown(v, shift);
        final int[][] qr = divideAndRemainder(a,b);
        final int[] r = NaturalInts.shiftUp(qr[1], shift);
        return new int[][] { qr[0], r, }; } }
    return knuthDivision(u, v); }

  //--------------------------------------------------------------
  // disable constructor
  //--------------------------------------------------------------

  private KnuthDivision () {
    throw new
      UnsupportedOperationException(
      "can't instantiate " + getClass().getCanonicalName()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
