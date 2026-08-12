package mop.java.numbers;

import java.util.Arrays;

/** Utilities for <code>int[]</code>, <code>int</code>,
 * and <code>long</code>,
 * related to {@link BoundedNatural}.
 *  <br>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-11
 */

//@SuppressWarnings("unchecked")
public final class NaturalInts {

  /** The value of <code>hiBit</code> is assumed to fit in an
   * <code>int</code> and to be a multiple of 32.
   * The maximum number of words, determined to hold
   * {@link #MAX_BITS}.
   */
  public final static int MAX_WORDS = (Integer.MAX_VALUE >> 5);

  /** The value of <code>hiBit</code> is assumed to fit in an
   * <code>int</code>. That means it can be at most
   * <code>{@link Integer#MAX_VALUE}</code>.
   * For convenience, I want the representation to use
   * all the bits in each of the <code>int</code> words,
   * so <code>MAX_BITS</code> should be a multiple of 32.
   */

  public static final int MAX_BITS = (MAX_WORDS << 5);

  //--------------------------------------------------------------
  // manipulating word arrays
  //--------------------------------------------------------------

  static final int[] setWord (final int[] a,
                              final int i,
                              final int w) {
    assert 0<=i : "Negative index: " + i;
    assert i < MAX_WORDS : "word index too large" + i;
    final int h = hiInt(a);
    if (0==w) {
      if (i>=h) { return a; }
      final int[] u = Arrays.copyOf(a,h);
      u[i] = 0;
      return u; }
    final int n = Math.max(i+1,h);
    final int[] u = Arrays.copyOf(a,n);
    u[i] = w;
    return u; }

  private static final int[] shiftDownByWords (final int[] w,
                                               final int iShift) {
    final int nt = hiInt(w);
    if (0>=(nt-iShift)) { return new int[0]; }
    return Arrays.copyOfRange(w,iShift,nt); }

  private static final int[] shiftDownByBits (final int[] w,
                                              final int iShift,
                                              final int bShift) {
    final int nt = hiInt(w);
    final int nv = nt-iShift;
    // shifting all bits off the end, covers zero input case
    if (0>=nv) { return new int[0]; }

    final int[] vv = new int[nv];
    final int rShift = 32-bShift;
    int w0 = w[iShift];
    int i = 0;
    int j = iShift+1;
    while (j<nt) {
      final int w1 = w[j];
      final int wi = ((w1<<rShift) | (w0>>>bShift));
      w0 = w1;
      vv[i] = wi;
      i++; j++;}
    final int vvn = (w0>>>bShift);
    if (0!=vvn) { vv[i] = vvn; }
    return vv; }

  static final int[] shiftDown (final int[] a,
                                final int downShift) {
    assert 0<=downShift;
    if (0==downShift) { return a; }
    final int iShift = (downShift>>>5);
    final int bShift = (downShift&0x1F);
    if (0==bShift) { return shiftDownByWords(a,iShift); }
    return shiftDownByBits(a,iShift,bShift); }

  private static final int[] shiftUpByWords (final int[] w,
                                             final int iShift) {
    final int nt = hiInt(w);
    final int nv = nt+iShift;
    final int[] vv = new int[nv];
    System.arraycopy(w,0,vv,iShift,nt);
    return vv; }

  private static final int[] shiftUpByBits (final int[] tt,
                                            final int iShift,
                                            final int bShift) {
    final int nt = hiInt(tt);
    final int nv = nt+iShift;
    final int rShift = 32-bShift;
    final int[] vv = new int[nv+1];
    int w0 = tt[0];
    vv[iShift] = (w0<<bShift);
    for (int i = 1,j = iShift+1;i<nt;i++,j++) {
      final int w1 = tt[i];
      final int wj = ((w1<<bShift)|(w0>>>rShift));
      w0 = w1;
      vv[j] = wj; }
    final int vvn = (w0>>>rShift);
    if (0!=vvn) { vv[nv] = vvn; }
    return vv; }

  static final int[] shiftUp (final int[] a,
                              final int upShift) {
    assert 0<=upShift;
    if (0==upShift) { return a; }
    final int iShift = (upShift>>>5);
    final int bShift = (upShift&0x1f);
    if (0==bShift) { return shiftUpByWords(a,iShift); }
    return shiftUpByBits(a,iShift,bShift); }

  // TODO: change BoundedNatural to ensure no leading zero words?
  /** NOTE: doesn't assume no leading zero words. */
  static final int hiInt (final int[] x) {
    final int n = x.length;
    if (0==n) { return 0; }
    int i = n-1;
    while ((0<=i) && (0==x[i])) { i--; }
    return i+1; }

  /** throw an {@link ArithmeticException} if
   * <code>nwords</code> exceeds {@link #MAX_WORDS}.
   */

  static final void checkOverflow (final int nwords) {
    if (nwords > MAX_WORDS) {
      throw new ArithmeticException(
        "Attempting to create an instance of BoundedNatural"
          + " overflowing the range: "
          + nwords + " words."); } }

  static final int hiBit (final int[] u) {
    final int i = hiInt(u)-1;
    if (0>i) { return 0; }
    final int wi = u[i];
    return ((i<<5)+Integer.SIZE)-Integer.numberOfLeadingZeros(wi); }

  private static final int loInt (final int[] u) {
    // Search for lowest order nonzero int
    final int nt = hiInt(u); // might be 0
    for (int i=0;i<nt;i++) { if (0!=u[i]) { return i; } }
    assert 0==nt;
    return 0; }

  static final int loBit (final int[] u) {
    // Search for lowest order nonzero int
    final int i=loInt(u);
    if (i==hiInt(u)) { return 0; } // all bits zero
    final int h =
      (i<<5) + Integer.numberOfTrailingZeros(u[i]);
    assert h >= 0;
    assert h <= MAX_BITS;
    return h; }

  //--------------------------------------------------------------
  // disable constructor
  //--------------------------------------------------------------
  private NaturalInts () {
    throw new UnsupportedOperationException(
      "can't instantiate " + getClass()); }
//--------------------------------------------------------------
}
//--------------------------------------------------------------
