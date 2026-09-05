package mop.java.numbers;

import mop.java.prng.Generator;
import mop.java.prng.GeneratorBase;
import mop.java.prng.Generators;
import org.apache.commons.rng.UniformRandomProvider;

import java.math.BigInteger;
import java.util.Arrays;

import static mop.java.numbers.Numbers.*;

/** Immutable large but bounded (like BigInteger) non-negative integers
 * (natural numbers) as a bit sequence,
 * represented by an <code>int[]</code> of words,
 * starting with the least significant word at
 * <code>int[0]</code>, and the most significant at
 * <code>int[nwords-1]</code>.
 * Each <code>int</code> word is treated as unsigned 32 bits,
 * using <code>long</code> arithmetic and
 * {@link mop.java.numbers.Numbers#unsigned(int)}
 * to convert the <code>int</code> bits
 * to the corresponding unsigned value in a <code>long</code>
 * <br>
 * The range of numbers that can be represented by instances
 * of this class is bounded by:
 * <ol>
 * <li> I wish to index bits with positive <code>int</code>,
 * so the length of the bit sequence is limited to at most
 * {@link Integer#MAX_VALUE}.
 * <li>For simplicity, I want all bits in all words
 * to be available. That means the maximum length
 * of the bit sequence should be a multiple of 32.
 * This suggests
 * <code>MAX_WORDS = (Integer.MAX_VALUE &gt;&gt; 5)</code>.
 * and
 * <code>MAX_BITS = (MAX_WORDS &lt;&lt; 5)</code>
 * <br>
 * </ol>
 * (The limit I am talking about is separate from
 * the limit imposed by the available memory.)
 * <br>
 * In any case, the number of <code>words</code> must be less than
 * <code>Integer.MAX_VALUE</code>, because that's the largest
 * <code>n</code> that could be passed to <code>new int[n]</code>.
 * <br>
 * Note: the javadoc for <code>math.BigInteger</code>
 * says: "BigInteger must support values in the range
 * <code>-2^Integer.MAX_VALUE</code> (exclusive) to
 * <code>+2^Integer.MAX_VALUE</code> (exclusive)
 * and may support values outside of that range."
 * If the implementation is based on a <code>int[]</code>,
 * an array of 32-bit (unsigned) <code>int</code> words,
 * then the range should be <code>+/- 2^(maxArraySize+5)</code>
 * This suggests that the maximum array size should be at least
 * <code>Integer.MAX_VALUE-5</code>.
 * <br>
 * The unbounded natural numbers are a commutative semi-ring,
 * but this implementation fails to be closed under '+' and '*',
 * when the operation result exceeds the bound.
 *  <br>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-05
 */

//
public final class BoundedNatural
implements Ringlike<BoundedNatural> {

  //--------------------------------------------------------------
  // fields
  //--------------------------------------------------------------
  /** This array is never modified.
   */

  private final int[] _words;
  final int[] words () { return _words; }

  public final int hiInt () {
    return NaturalInts.hiInt(words()); }

  public final int hiBit () {
    return NaturalInts.hiBit(words()); }

  public final int loBit () {
    return NaturalInts.loBit(words()); }

  //--------------------------------------------------------------

  public final int word (final int i) {
    assert 0<=i : "Negative index: " + i;
    //assert i < MAX_WORDS : "word index too large " + i;
    if (hiInt()<=i) { return 0; }
    return _words[i]; }

  public final long uword (final int i) {
    assert 0<=i : "Negative index: " + i;
    //assert i < MAX_WORDS : "word index too large " + i;
    if (hiInt()<=i) { return 0L; }
    return unsigned(_words[i]); }

  //--------------------------------------------------------------
  /** Return the <code>[i0,i1)</code> words as a new
   * <code>BoundedNatural</code> with <code>[0,i1-i0)</code> words.
   */

  public final BoundedNatural words (final int i0,
                                     final int i1) {
    assert 0<=i0;
    assert i0<i1;
//    assert i0 < NaturalInts.MAX_WORDS : "word index too large" + i0;
//    assert i1 < NaturalInts.MAX_WORDS : "word index too large" + i1;
//    if ((0==i0) && (hiInt()<=i1)) { return this; }
    return unsafe(Arrays.copyOfRange(words(),i0,i1)); }
//    final int[] tt = words();
//    final int[] vv = new int[n];
//    System.arraycopy(tt,i0,vv,0,n);
//    return unsafe(vv); }

  public final BoundedNatural setWord (final int i,
                                       final int w) {
    return unsafe(NaturalInts.setWord(words(), i, w)); }

  /** Singleton. */
  static final BoundedNatural ZERO =
    new BoundedNatural(new int[0]);

  @Override
  public final boolean isZero () { return 0==hiInt(); }

  @Override
  public final BoundedNatural zero () { return ZERO; }

  /** Don't use a singleton for this---takes up too much space. */
  public static final BoundedNatural maxValue () {
    return ones(NaturalInts.MAX_WORDS); }

  //--------------------------------------------------------------
  // ordering
  //--------------------------------------------------------------

  @Override
  public final int compareTo (final BoundedNatural u) {
    final int b0 = hiBit();
    final int b1 = u.hiBit();
    if (b0<b1) { return -1; }
    if (b0>b1) { return 1; }
    for (int i = hiInt()-1;i>=0;i--) {
      final long u0i = uword(i);
      final long u1i = u.uword(i);
      if (u0i<u1i) { return -1; }
      if (u0i>u1i) { return 1; } }
    return 0; }

  //--------------------------------------------------------------

  public final int compareTo (final long u) {
    assert 0L<=u;
    final int nt = hiInt();
    final long ulo = loWord(u);
    final long uhi = hiWord(u);
    final int nu = ((0L!=uhi) ? 2 : (0L!=ulo) ? 1 : 0);
    if (nt<nu) { return -1; }
    if (nt>nu) { return 1; }
    final int[] tt = words();
    if (2==nu) {
      final long tti = unsigned(tt[1]);
      if (tti<uhi) { return -1; }
      if (tti>uhi) { return 1; } }
    if (1<=nu) {
      final long tti = unsigned(tt[0]);
      if (tti<ulo) { return -1; }
      if (tti>ulo) { return 1; } }
    return 0; }

  public final boolean equals (final long u) {
    return 0 == compareTo(u); }

  //--------------------------------------------------------------

  public final int compareTo (final long u,
                              final int upShift) {
    assert 0L<=u;
    assert 0<=upShift : "upShift=" + upShift;

    if (0==upShift) { return compareTo(u); }

    final int nt = hiInt();
    if (0==nt) { return ((0L==u) ? 0 : -1); }
    else if (0L==u) { return 0; }
    final int iShift = (upShift>>>5);
    if (nt<iShift) { return -1; }
    if (nt>(iShift+3)) { return 1; }

    int i = nt-1;
    final int[] tt = words();
    final int tthi = tt[i];
    final int mt = ((i<<5) + Integer.SIZE) -
      Integer.numberOfLeadingZeros(tthi);
    final int mu = Numbers.hiBit(u) + upShift;
    if (mt<mu) { return -1; }
    if (mt>mu) { return 1; }

    long tti = unsigned(tthi);
    i--;
    final int bShift = (upShift&0x1F);
    if (0==bShift) {
      final long uhi = hiWord(u);
      final long ulo = loWord(u);
      if (0L!=uhi) {
        if (tti<uhi) { return -1; }
        if (tti>uhi) { return 1; }
        tti = unsigned(tt[i--]);
      }
      if (tti<ulo) { return -1; }
      if (tti>ulo) { return 1; }
    }
    else {
      final long uhi = (u>>>(64-bShift));
      if (0L!=uhi) {
        if (tti<uhi) { return -1; }
        if (tti>uhi) { return 1; }
        tti = unsigned(tt[i--]);
        final long us = (u<<bShift);
        final long umid = hiWord(us);
        if (tti<umid) { return -1; }
        if (tti>umid) { return 1; }
        tti = unsigned(tt[i--]);
        final long ulo = loWord(us);
        if (tti<ulo) { return -1; }
        if (tti>ulo) { return 1; } }
      else {
        final long us = (u<<bShift);
        final long umid = hiWord(us);
        if (0L!=umid) {
          if (tti<umid) { return -1; }
          if (tti>umid) { return 1; }
          tti = unsigned(tt[i--]);
          final long ulo = loWord(us);
          if (tti<ulo) { return -1; }
          if (tti>ulo) { return 1; } }
        else {
          final long ulo = loWord(us);
          if (tti<ulo) { return -1; }
          if (tti>ulo) { return 1; } } } }

    while (i>=0) { if (0!=tt[i--]) { return 1; } }
    return 0; }

  //--------------------------------------------------------------

  public static final BoundedNatural product (final long t0,
                                              final long t1) {
    assert 0L<=t0;
    assert 0L<=t1;
    //if ((0L==t0||(0L==t1))) { return zero(); }

    final long lo0 = loWord(t0);
    final long lo1 = loWord(t1);
    final long hi0 = hiWord(t0);
    final long hi1 = hiWord(t1);

    long sum = lo0*lo1;
    final int w0 = (int) sum;
    // TODO: fix lurking overflow issue
    // works here because t0,t1 53 bit double significands
    //final long hilo2 = Math.addExact(hi0*lo1,hi1*lo0);
    sum = hiWord(sum) + (hi0*lo1) + (hi1*lo0);
    final int w1 = (int) sum;
    sum = hiWord(sum) + (hi0*hi1);
    final int w2 = (int) sum;
    final int w3 = (int) hiWord(sum);
    if (0!=w3) { return new BoundedNatural(new int[] {w0,w1,w2,w3,}); }
    if (0!=w2) { return new BoundedNatural(new int[] {w0,w1,w2,}); }
    if (0!=w1) { return new BoundedNatural(new int[] {w0,w1,}); }
    if (0!=w0) { return new BoundedNatural(new int[] {w0,}); }
    return ZERO; }

  // TODO: fix lurking overflow issue
  // probably only works as long as t is double significand

  public static final BoundedNatural fromSquare (final long t) {
    assert 0L<=t;
    //if (0L==t) { return zero(); }
    final long hi = hiWord(t);
    final long lo = loWord(t);
    final long lolo = lo*lo;
    final long hilo2 = ((hi*lo)<<1);
    //final long hilo2 = Math.multiplyExact(2,hi*lo);
    final long hihi = hi*hi;
    long sum = lolo;
    final int w0 = (int) sum;
    sum = hiWord(sum) + hilo2;
    final int w1 = (int) sum;
    sum = hiWord(sum) + hihi ;
    final int w2 = (int) sum;
    final int w3 = (int) hiWord(sum);

    if (0!=w3) { return new BoundedNatural(new int[] { w0,w1,w2,w3,}); }
    if (0!=w2) { return new BoundedNatural(new int[] { w0,w1,w2, }); }
    if (0!=w1) { return new BoundedNatural(new int[] {w0,w1}); }
    if (0!=w0) { return new BoundedNatural(new int[] {w0}); }
    return ZERO; }

  //--------------------------------------------------------------
  // add (non-negative) longs
  //--------------------------------------------------------------

  public final BoundedNatural add (final long u) {
    assert 0L<u;
    //if (0L==u) { return this; }
    final int nt = hiInt();
    //if (0==nt) { return valueOf(u); }
    final long uhi = hiWord(u);
    final long ulo = loWord(u);
    final int nu = ((0L!=uhi)?2:(0L!=ulo)?1:0);
    final int nv = Math.max(nu,nt);
    NaturalInts.checkOverflow(nv);
    if (0==nv) { return ZERO; }
    final int[] tt = words();
    final int[] vv = new int[nv];
    long sum = ulo;
    if (0<nt) { sum += unsigned(tt[0]); }
    vv[0] = (int) sum;
    sum = hiWord(sum);
    if (1<nv) {
      sum += uhi;
      if (1<nt) { sum += unsigned(tt[1]); }
      vv[1] = (int) sum;
      sum = hiWord(sum); }

    int i=2;
    for (;i<nt;i=Math.addExact(i,1)) {
      if (0L==sum) { break; }
      sum += unsigned(tt[i]);
      vv[i] = (int) sum;
      sum = hiWord(sum); }
    if (0L!=sum) {
      final int nvv = Math.addExact(nv,1);
      NaturalInts.checkOverflow(nvv);
      final int[] vvv = new int[nvv];
//      for (int j=0;j<nv;j++) { vvv[j]=vv[j]; }
      System.arraycopy(vv,0,vvv,0,nv);
      vvv[nv] = 1;
      return new BoundedNatural(vvv); }

    for (;i<nt;i++) { vv[i] = tt[i]; }
    return new BoundedNatural(vv); }

  //--------------------------------------------------------------

  private final BoundedNatural addByWords (final long u,
                                           final int iShift) {
    final int nt = hiInt();
    final int[] tt = words();
    final long hi = hiWord(u);
    final int nu = iShift+((0L==hi)?1:2);
    final int nv = Math.max(nt,nu);
    final int[] vv = new int[nv];
//    for (int i=0;i<Math.min(iShift,nt);i++) { vv[i] = tt[i]; }
    System.arraycopy(tt,0,vv,0,Math.min(iShift,nt));

    long sum = loWord(u);
    int i = iShift;
    if (i<nt) { sum += unsigned(tt[i]); }
    vv[i++] = (int) sum;
    sum = hiWord(sum);
    if (i<nu) {
      sum += hi;
      if (i<nt) { sum += unsigned(tt[i]); }
      vv[i++] = (int) sum;
      sum = hiWord(sum); }

    for (;i<nt;i++) {
      if (0L==sum) { break; }
      sum += unsigned(tt[i]);
      vv[i] = (int) sum;
      sum = hiWord(sum); }

    if (0L!=sum) {
      final int[] vvv = new int[nv+1];
      System.arraycopy(vv, 0, vvv, 0, nv);
      System.arraycopy(vv,0,vvv,0,nv);
      vvv[nv] = 1;
      return new BoundedNatural(vvv); }

    for (;i<nt;i++) { vv[i] = tt[i]; }
    return new BoundedNatural(vv); }

  private final BoundedNatural addByBits (final long u,
                                          final int iShift,
                                          final int bShift) {
    final int nt = hiInt();
    final int[] tt = words();
    final long us = (u<<bShift);
    final long mid = hiWord(us);
    final long hi = (u>>>(64-bShift));
    final int nu = iShift+((0L==hi)?((0L==mid)?1:2):3);
    final int nv = Math.max(nt,nu);
    final int[] vv = new int[nv];
//    for (int i=0;i<Math.min(iShift,nt);i++) { vv[i] = tt[i]; }
    System.arraycopy(tt,0,vv,0,Math.min(iShift,nt));
    long sum = loWord(us);

    int i=iShift;
    if (i<nt) { sum += unsigned(tt[i]); }
    vv[i++] = (int) sum;
    sum = hiWord(sum);
    if (i<nu) {
      sum += mid;
      if (i<nt) { sum += unsigned(tt[i]); }
      vv[i++] = (int) sum;
      sum = hiWord(sum);
      if (i<nu) {
        sum += hi;
        if (i<nt) { sum += unsigned(tt[i]); }
        vv[i++] = (int) sum;
        sum = hiWord(sum); } }

    boolean nocarry = (0==(int)sum);
    for (;i<nt;i++) {
      if (nocarry) { break; }
      final long vvi = 1L + unsigned(tt[i]);
      vv[i] = (int) vvi;
      nocarry = (0==(int)hiWord(vvi)); }

    if (!nocarry) {
      final int[] vvv = new int[nv+1];
      System.arraycopy(vv,0,vvv,0,nv);
      vvv[nv] = 1;
      return new BoundedNatural(vvv); }

    for (;i<nt;i++) { vv[i] = tt[i]; }
    return new BoundedNatural(vv); }

  public final BoundedNatural add (final long u,
                                   final int upShift) {
    assert 0<u;
    assert 0<upShift;
    final int iShift = (upShift>>>5);
    final int bShift = (upShift&0x1F);
    if (0==bShift) { return addByWords(u,iShift);}
    return addByBits(u,iShift,bShift); }

  //--------------------------------------------------------------
  // subtract (non-negative) longs
  //--------------------------------------------------------------

  public final BoundedNatural subtract (final long u) {
    assert 0L<=u;
    assert 0<=compareTo(u);
    //if (0L==u) { return this; }
    final int nt = hiInt();
    final int[] tt = words();
    final int[] vv = new int[nt];
    // at least 1 element in tt or u==0
    long dif = unsigned(tt[0])-loWord(u);
    vv[0] = (int) dif;
    dif = (dif>>32);
    if (1<nt) {
      dif = (unsigned(tt[1])-hiWord(u))+dif;
      vv[1] = (int) dif;
      dif = (dif>>32); }
    int i=2;
    for (;i<nt;i++) {
      if (0L==dif) { break; }
      dif = unsigned(tt[i])+dif;
      vv[i] = (int) dif;
      dif = (dif>>32); }
    for (;i<nt;i++) { vv[i] = tt[i]; }
    assert 0L==dif : dif;
    return unsafe(vv); }

  //--------------------------------------------------------------

  private final BoundedNatural subtractByWords (final long u,
                                                final int iShift) {
    final int nt = hiInt();
    final int[] tt = words();
    final int[] vv = new int[nt];
    // assert iShift<=n || 0L==u
    System.arraycopy(tt,0,vv,0,iShift);

    int i=iShift;
    long dif = unsigned(tt[i])-loWord(u);
    vv[i++] = (int) dif;
    dif = (dif>>32);
    if (i<nt) { // else high word is 0
      dif += unsigned(tt[i])-hiWord(u);
      vv[i] = (int) dif;
      dif = (dif>>32); }

    i = iShift+2;
    for (;i<nt;i++) {
      if (0L==dif) { break; }
      dif += unsigned(tt[i]);
      vv[i] = (int) dif;
      dif = (dif>>32); }
    assert 0L==dif;

    for (;i<nt;i++) { vv[i] = tt[i]; }

    return unsafe(vv); }

  private final BoundedNatural subtractByBits (final long u,
                                               final int iShift,
                                               final int bShift)  {
    final int nt = hiInt();
    // assert iShift<=nt || 0L==u
    final int[] tt = words();
    final int[] vv = new int[nt];
    //for (int i=0;i<iShift;i++) { vv[i] = tt[i]; }
    System.arraycopy(tt,0,vv,0,iShift);
    final long us = (u<<bShift);
    int i=iShift;
    long dif = unsigned(tt[i])-loWord(us);
    vv[i++] = (int) dif;
    dif = (dif>>32);
    if (i<nt) { // else upper 2 words must be 0
      dif += unsigned(tt[i])-hiWord(us);
      vv[i++] = (int) dif;
      dif = (dif>>32);
      if (i<nt) {// else upper word must be 0
        dif += unsigned(tt[i])-(u>>>(64-bShift));
        vv[i] = (int) dif;
        dif = (dif>>32); } }

    i = iShift+3;
    for (;i<nt;i++) {
      if (0L==dif) { break; }
      dif += unsigned(tt[i]);
      vv[i] = (int) dif;
      dif = (dif>>32); }
    assert 0L==dif;

    for (;i<nt;i++) { vv[i] = tt[i]; }
    return unsafe(vv); }

  public final BoundedNatural subtract (final long u,
                                        final int upShift) {
    assert 0L<=u;
    assert 0<=upShift;
    //if (0L==u) { return this; }
    //if (0==upShift) { return subtract(u); }
    //if (isZero()) { assert 0L==u; return this; }
    final int iShift = (upShift>>>5);
    final int bShift = (upShift&0x1f);
    if (0==bShift) { return subtractByWords(u,iShift);  }
    return subtractByBits(u,iShift,bShift); }

  //--------------------------------------------------------------

  public final BoundedNatural subtractFrom (final long u) {
    assert 0L<=u;
    assert 0>=compareTo(u);
    //if (0L==u) { return this; }
    // at least 1 element in tt or u==0
    long dif = loWord(u)-uword(0);
    final int vv0 = (int) dif;
    dif = (hiWord(u)-uword(1))+(dif>>32);
    final int vv1 = (int) dif;
    assert 0L== (dif>>32) :  (dif>>32);
    if (0==vv1) { return unsafe(new int[] {vv0}); }
    return unsafe(new int[] {vv0,vv1}); }

  //--------------------------------------------------------------

  private final BoundedNatural subtractFromByWords (final long u,
                                                    final int iShift) {
    final int nt = hiInt();
    final int[] tt = words();
    final int[] vv = new int[iShift+3];
    // assert iShift<=n || 0L==u
    long dif = 0;
    int i=0;
    for (;i<Math.min(nt,iShift);i++) {
      dif -= unsigned(tt[i]);
      vv[i] = (int) dif;
      dif = (dif>>32); }
    for (;i<iShift;i++) {
      vv[i] = (int) dif;
      dif = (dif>>32); }
    dif += loWord(u);
    i=iShift;
    if (i<nt) { dif -= unsigned(tt[i]); }
    vv[i++] = (int) dif;
    dif = (dif>>32);
    dif += hiWord(u);
    if (i<nt) { dif -= unsigned(tt[i]); }
    vv[i] = (int) dif;
    assert 0L==(dif>>32);
    return unsafe(vv); }

  private final BoundedNatural subtractFromByBits (final long u,
                                                   final int iShift,
                                                   final int bShift) {
    final int nt = hiInt();
    final int[] tt = words();
    final int[] vv = new int[iShift+3];
    // assert iShift<=n || 0L==u
    long dif = 0;
    int i=0;
    for (;i<Math.min(nt,iShift);i++) {
      dif -= unsigned(tt[i]);
      vv[i] = (int) dif;
      dif = (dif>>32); }
    for (;i<iShift;i++) {
      vv[i] = (int) dif;
      dif = (dif>>32); }
    i=iShift;
    final int hi = (int) hiWord(u);
    final int lo = (int) u;
    final int rShift = 32-bShift;
    dif += unsigned(lo<<bShift);
    if (i<nt) { dif -= unsigned(tt[i]); }
    vv[i++] = (int) dif;
    dif = (dif>>32);
    dif += unsigned((hi<<bShift)|(lo>>>rShift));
    if (i<nt) { dif -= unsigned(tt[i]); }
    vv[i++] = (int) dif;
    dif = (dif>>32);
    dif += unsigned(hi>>>rShift);
    if (i<nt) { dif -= unsigned(tt[i]); }
    vv[i] = (int) dif;
    assert 0L==(dif>>32);
    return unsafe(vv); }

  public final BoundedNatural subtractFrom (final long u,
                                            final int upShift) {
    assert 0L<=u;
    assert 0<=upShift;
    //if (0L==u) { return this; }
    //if (0==upShift) { return subtractFrom(u); }
    //if (isZero()) { return from(u,upShift); }
    final int iShift = (upShift>>>5);
    final int bShift = (upShift&0x1f);
    if (0==bShift) {
      return subtractFromByWords(u,iShift); }
    return subtractFromByBits(u,iShift,bShift); }

  //--------------------------------------------------------------
  // arithmetic with shifted Naturals
  //--------------------------------------------------------------
  /** <code>add(u<<(32*iShift))</code> */

  private final BoundedNatural addByWords (final BoundedNatural u,
                                           final int iShift) {
    final int nt = hiInt();
    final int[] tt = words();
    final int[] uu = u.words();
    assert 0<u.hiInt();
    final int nu = u.hiInt()+iShift;
    final int nv = Math.max(nt,nu);
    final int[] vv = new int[nv];
    System.arraycopy(tt,0,vv,0,Math.min(nt,iShift));
    long sum = 0L;
    int i=iShift;
    for (;i<nu;i++) {
      sum += unsigned(uu[i-iShift]);
      if (i<nt) { sum += unsigned(tt[i]); }
      vv[i] = (int) sum;
      sum = hiWord(sum); }

    for (;i<nt;i++) {
      if (0L==sum) { break; }
      sum += unsigned(tt[i]);
      vv[i] = (int) sum;
      sum = hiWord(sum); }

    if (0L!=sum) {
      final int[] vvv = new int[nv+1];
      System.arraycopy(vv,0,vvv,0,nv);
      vvv[nv] = 1;
      return new BoundedNatural(vvv); }

    for (;i<nt;i++) { vv[i] = tt[i]; }
    return new BoundedNatural(vv); }

  private final BoundedNatural addByBits (final BoundedNatural u,
                                          final int iShift,
                                          final int bShift) {
    final int nt = hiInt();
    final int[] tt = words();
    final int nu0 = u.hiInt();
    final int[] uu = u.words();
    assert 0<u.hiInt();
    final int rShift = 32-bShift;
    final int uhi = (uu[nu0-1]>>rShift);
    final int nu1 = nu0+iShift;
    final int nv = Math.max(nt,nu1+((0==uhi)?0:1));
    final int[] vv = new int[nv];
    System.arraycopy(tt,0,vv,0,Math.min(nt,iShift));
    long sum = 0L;
    int u0 = 0;
    int i=iShift;
    for (;i<nu1;i++) {
      final int u1 = uu[i-iShift];
      sum += unsigned((u1<<bShift)|(u0>>>rShift));
      u0 = u1;
      if (i<nt) { sum += unsigned(tt[i]); }
      vv[i] = (int) sum;
      sum = hiWord(sum); }
    final long ui = unsigned(u0>>>rShift);
    if (0L!=ui) {
      sum += ui;
      if (i<nt) { sum += unsigned(tt[i]); }
      vv[i++] = (int) sum;
      sum = hiWord(sum); }

    for (;i<nt;i++) {
      if (0L==sum) { break; }
      sum += unsigned(tt[i]);
      vv[i] = (int) sum;
      sum = hiWord(sum); }

    if (0L!=sum) {
      final int[] vvv = new int[nv+1];
      System.arraycopy(vv,0,vvv,0,nv);
      vvv[nv] = 1;
      return new BoundedNatural(vvv); }

    for (;i<nt;i++) { vv[i] = tt[i]; }
    return new BoundedNatural(vv); }

  public final BoundedNatural add (final BoundedNatural u,
                                   final int upShift) {
    assert 0<=upShift;
    //if (0==upShift) { return add(u); }
    //if (isZero()) { return u.shiftUp(upShift); }
    if (u.isZero()) { return this; }
    final int iShift = (upShift>>>5);
    final int bShift = (upShift&0x1f);
    if (0==bShift) { return addByWords(u,iShift); }
    return addByBits(u,iShift,bShift); }

  //--------------------------------------------------------------
  // Ring-like
  //--------------------------------------------------------------

  @Override
  public final BoundedNatural abs () { return this; }

  //--------------------------------------------------------------
  // no int arithmetic overflow checks

  @Override
  public final BoundedNatural add (final BoundedNatural u) {
    return unsafe(NaturalAdd.add(words(),hiInt(),
                                 u.words(),u.hiInt())); }

//  @Override
//  public final BoundedNatural add (final BoundedNatural u) {
//    final int nt = hiInt();
//    final int nu = u.hiInt();
//    if (nt<nu) { return u.add(this); }
//    final int[] tt = words();
//    final int[] uu = u.words();
//    NaturalInts.checkOverflow(nt+1);
//    final int[] vv = new int[nt+1];
//    long sum = 0L;
//    int i=0;
//    for (;i<nu;i++) {
//      sum += unsigned(tt[i]) + unsigned(uu[i]);
//      vv[i] = (int) sum;
//      sum = hiWord(sum);}
//    for (;i<nt;i++) {
//      if (0L==sum) { break; }
//      sum += unsigned(tt[i]);
//      vv[i] = (int) sum;
//      sum = hiWord(sum);}
//    for (;i<nt;i++) { vv[i] = tt[i]; }
//    if (0L!=sum) { vv[nt] = 1; }
//    return new BoundedNatural(vv); }
//
  //--------------------------------------------------------------

  @Override
  public final BoundedNatural subtract (final BoundedNatural u) {
    assert 0<=compareTo(u);
    final int nu = u.hiInt();
    if (0>=nu) { return this; }
    return unsafe(NaturalAdd.subtract(words(),hiInt(),u.words(),nu)); }

//  @Override
//  public final BoundedNatural subtract (final BoundedNatural u) {
//    assert 0<=compareTo(u);
//    final int nt = hiInt();
//    final int nu = u.hiInt();
//    assert nu<=nt;
//    final int[] tt = words();
//    final int[] uu = u.words();
//    if (0>=nu) { return this; }
//    final int[] vv = new int[nt];
//    long dif = 0L;
//    int i=0;
//    for (;i<nu;i++) {
//      dif += unsigned(tt[i])-unsigned(uu[i]);
//      vv[i] = (int) dif;
//      dif= (dif>>32); }
//    for (;i<nt;i++) {
//      if (0L==dif) { break; }
//      dif += unsigned(tt[i]);
//      vv[i] = (int) dif;
//      dif = (dif>>32); }
//    assert 0L==dif;
//    for (;i<nt;i++) { vv[i] = tt[i]; }
//    return unsafe(vv); }

  //--------------------------------------------------------------

  @Override
  public final BoundedNatural absDiff (final BoundedNatural uu) {
    //    //assert isValid();
    //    //assert u.isValid();
    final int c = compareTo(uu);
    if (c==0) { return zero(); }
    if (c<0) { return uu.subtract(this); }
    return subtract(uu); }

  //--------------------------------------------------------------
  // multiplicative monoid-like
  //--------------------------------------------------------------
  // TODO: singleton class for one() and zero()?

  public static final BoundedNatural ONE =
    new BoundedNatural(new int[] {1});

  @Override
  public final BoundedNatural one () { return ONE; }

  public static final BoundedNatural ones (final int n) {
    final int[] vv = new int[n];
    Arrays.fill(vv, -1);
    return unsafe(vv); }

  @Override
  public final boolean isOne () {
    return (1 == hiInt()) && (1 == words()[0]); }

  //--------------------------------------------------------------
  // square
  //--------------------------------------------------------------

  private static final int KARATSUBA_SQUARE_THRESHOLD = 128;
  private static final int TOOM_COOK_SQUARE_THRESHOLD = 216;

  @Override
  public final BoundedNatural square () {
    if (isZero()) { return zero(); }
    if (isOne()) { return one(); }
    final int n = hiInt();
    if (n < KARATSUBA_SQUARE_THRESHOLD) {
      return NaturalMultiply.squareSimple(this); }
    if (n < TOOM_COOK_SQUARE_THRESHOLD) {
      return NaturalMultiply.squareKaratsuba(this); }
    // For a discussion of overflow detection see multiply()
    return NaturalMultiply.squareToomCook3(this); }

  //--------------------------------------------------------------
  // multiply
  //--------------------------------------------------------------

  @Override
  public final BoundedNatural multiply (final BoundedNatural v) {

    return NaturalMultiply.multiply(v,this); }

  //--------------------------------------------------------------
  // divide
  //--------------------------------------------------------------
  static final int BURNIKEL_ZIEGLER_THRESHOLD = 80;
  private static final int BURNIKEL_ZIEGLER_OFFSET = 40;
  private static boolean useKnuthDivision (final BoundedNatural u,
                                           final BoundedNatural v) {
    final int nn = u.hiInt();
    final int nd = v.hiInt();
    return
      (nd < BURNIKEL_ZIEGLER_THRESHOLD)
        ||
        ((nn-nd) < BURNIKEL_ZIEGLER_OFFSET); }

  //--------------------------------------------------------------
  /** for testing, not meant to be called in normal code. */
  public final BoundedNatural[]
  divideAndRemainderKnuth (final BoundedNatural v) {
    if (v.isOne()) { return new BoundedNatural[] { this,zero(), }; }
    if (isZero()) { return new BoundedNatural[] { zero(),zero(), }; }

    final int cmp = compareTo(v);
    if (0==cmp) { return new BoundedNatural[] { one(),zero(), }; }
    if (0>cmp) { return new BoundedNatural[] { zero(),this, }; }

    if (1==v.hiInt()) {
      return NaturalDivide.divideAndRemainder(this, v.word(0)); }

    final int[][] qr =
        KnuthDivision.divideAndRemainder(words(), v.words());
      return new BoundedNatural[] {
        BoundedNatural.unsafe(qr[0]),
        BoundedNatural.unsafe(qr[1]), }; }


  public final BoundedNatural[]
  divideAndRemainderBurnikelZiegler (final BoundedNatural v) {
    if (v.isOne()) { return new BoundedNatural[] { this,zero(), }; }
    if (isZero()) { return new BoundedNatural[] { zero(),zero(), }; }

    final int cmp = compareTo(v);
    if (0==cmp) { return new BoundedNatural[] { one(),zero(), }; }
    if (0>cmp) { return new BoundedNatural[] { zero(),this, }; }

    if (1==v.hiInt()) {
      return NaturalDivide.divideAndRemainder(this, v.word(0)); }

    return BurnikelZieglerDivision.divideAndRemainder(this,v); }

//--------------------------------------------------------------

  @Override
  public final BoundedNatural[]
  divideAndRemainder (final BoundedNatural v) {
    //assert isValid();
    //assert u.isValid();
    if (v.isOne()) { return new BoundedNatural[] { this,zero(), }; }
    if (isZero()) { return new BoundedNatural[] { zero(),zero(), }; }

    final int cmp = compareTo(v);
    if (0==cmp) { return new BoundedNatural[] { one(),zero(), }; }
    if (0>cmp) { return new BoundedNatural[] { zero(),this, }; }

    if (1==v.hiInt()) {
      return NaturalDivide.divideAndRemainder(this, v.word(0)); }

    if (useKnuthDivision(this, v)) {
      final int[][] qr =
        KnuthDivision.divideAndRemainder(words(), v.words());
      return new BoundedNatural[] {
        BoundedNatural.unsafe(qr[0]),
        BoundedNatural.unsafe(qr[1]), }; }

    return divideAndRemainderBurnikelZiegler(v); }

  //--------------------------------------------------------------

  @Override
  public final BoundedNatural divide (final BoundedNatural u) {
    //assert isValid();
    //assert u.isValid();
    return divideAndRemainder(u)[0]; }

  @Override
  public final BoundedNatural remainder (final BoundedNatural u) {
    //assert isValid();
    //assert u.isValid();
    return divideAndRemainder(u)[1]; }

  //--------------------------------------------------------------
  // gcd
  //--------------------------------------------------------------

  @Override
  public final BoundedNatural gcd (final BoundedNatural u) {
    //assert isValid();
    //assert u.isValid();
    return NaturalGCD.gcd(this,u); }

  //--------------------------------------------------------------

  @Override
  public final BoundedNatural[] reduce (final BoundedNatural d0) {
      //assert isValid();
      //assert d.isValid();
      final int shift = Math.min(loBit(),d0.loBit());
      final BoundedNatural n = ((shift != 0) ? shiftDown(shift) : this);
      final BoundedNatural d = ((shift != 0) ? d0.shiftDown(shift) : d0);
      if (n.equals(d)) { return new BoundedNatural[] { one(),one(),}; }
      if (d.isOne()) { return new BoundedNatural[] { n,one(), }; }
      if (n.isOne()) { return new BoundedNatural[] { one(),d, }; }
      final BoundedNatural g = NaturalGCD.gcd(n,d);
      if (g.compareTo(n.one()) > 0) {
        final BoundedNatural ng = n.divide(g);
        final BoundedNatural dg = d.divide(g);
        return new BoundedNatural[] {ng,dg,}; }
      return new BoundedNatural[] {n,d,}; }

  //--------------------------------------------------------------
  // Uints
  //--------------------------------------------------------------

  public final BoundedNatural shiftDown (final int downShift) {
    if (0==downShift) { return this; }
    return new BoundedNatural(
      NaturalInts.shiftDown(words(), downShift)); }

  //--------------------------------------------------------------

  public final BoundedNatural shiftUp (final int upShift) {
    assert 0<=upShift;
    if (0==upShift) { return this; }
    if (isZero()) { return this; }
    return new BoundedNatural(NaturalInts.shiftUp(words(), upShift)); }

//  public final BoundedNatural setBit (final int i) {
//    assert 0<=i;
//    final int iw = (i>>>5);
//    final int w = word(iw);
//    final int ib = (i&0x1F);
//    return setWord(iw,(w|(1<<ib))); }

  //--------------------------------------------------------------
  // 'Number' methods
  //--------------------------------------------------------------

  @Override
  public final int intValue () {
    // TODO: handle 'negative' words correctly!
    return switch (hiInt()) {
      case 0 -> 0;
      case 1 -> _words[0];
      default -> throw new UnsupportedOperationException(
        "Too large for int:" + this); }; }

  @Override
  public final long longValue () {
    return switch (hiInt()) {
      case 0 -> 0;
      case 1 -> unsigned(_words[0]);
      case 2 -> (unsigned(_words[1]) << 32) | unsigned(_words[0]);
      default -> throw new UnsupportedOperationException(
        "Too large for long:" + this); }; }

  private final byte[] bigEndianBytes () {
    final int hi = hiBit();
    // an extra zero byte to avoid getting a negative
    // two's complement input to new BigInteger(b).
    final int n = 1 + ((hi)/8);
    final byte[] b = new byte[n];
    int j = 0;
    int w = 0;
    for (int i=0;i<n;i++) {
      if (0==(i%4)) { w = word(j++); }
      else { w = (w>>>8); }
      b[n-1-i] = (byte) w; }
    return b; }

  public final BigInteger toBigInteger () {
    return new BigInteger(bigEndianBytes()); }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------

  @Override
  public final int hashCode () {
    int hashCode = 0;
    for (int i=0; i<hiInt(); i++) {
      hashCode = ((31 * hashCode) + _words[i]); }
    return hashCode; }

  @Override
  public final boolean equals (final Object x) {
    if (x==this) { return true; }
    if (!(x instanceof final BoundedNatural u)) { return false; }
    final int nt = hiInt();
    if (nt!=u.hiInt()) { return false; }
    for (int i=0; i<nt; i++) {
      if (_words[i]!=u._words[i]) { return false; } }
    return true; }

  public final String toHexString () {
    final StringBuilder b = new StringBuilder();
    final int n = hiInt()-1;
    if (0>n) { b.append('0'); }
    else {
      b.append(String.format("%x",uword(n)));
      for (int i=n-1;i>=0;i--) {
        //b.append(" ");
        b.append(String.format("%08x",uword(i))); } }
    return b.toString(); }

  /** hex string. */
  @Override
  public final String toString () { return toHexString(); }

  //--------------------------------------------------------------

  public static final Generator generator (final UniformRandomProvider urp,
                                            final int nwords) {
    final Generator g =  Generators.intGenerator(nwords,urp);
    return new GeneratorBase ("BoundedNaturalGenerator") {
      // TODO: choose within a range, rather than number of ints
      @Override
      public Object next () {
        // TODO: make this uniform over non-negative values
        return BoundedNatural.unsafe((int[]) g.next()); } }; }

  //--------------------------------------------------------------
  // construction
  //-------------------------------------------------------------
  /** UNSAFE: doesn't copy <code>words</code> or check
   * <code>loInt</code> or <code>hiInt</code.
   */

  private BoundedNatural (final int[] words) {
    //NaturalInts.checkOverflow(words.length);
    _words = words; }

  /** Doesn't copy <code>words</code> or check <code>loInt</code>
   * or <code>hiInt</code>.
   */

  static final BoundedNatural unsafe (final int[] words) {
    return new BoundedNatural(words); }

  /** Copy <code>words</code>, stripping leading zeros.
   */
  public static final BoundedNatural make (final int[] words) {
    NaturalInts.checkOverflow(words.length);
    final int end = NaturalInts.hiInt(words);
    return new BoundedNatural(Arrays.copyOf(words,end)); }

  //--------------------------------------------------------------
//  /** If there are leading zeros, return a copy without them.
//   *  If none, return <code>this</code>.
//   */
//  public final BoundedNatural compress () {
//    final int hi = NaturalInts.hiInt(words());
//    if (words().length == hi) { return this; }
//    return new BoundedNatural(Arrays.copyOf(words(),hi)); }

  //--------------------------------------------------------------
  /** From a big endian {@code byte[]}, as produced by
   * {@link BigInteger#toByteArray()}.
   */

  private static final BoundedNatural fromBigEndianBytes (final byte[] a) {
    final int nBytes = a.length;
    int keep = 0;
    while ((keep<nBytes) && (0==a[keep])) { keep++; }
    final int nInts = ((nBytes-keep) + 3) >>> 2;
    final int[] result = new int[nInts];
    int b = nBytes-1;
    for (int i = nInts - 1; i >= 0; i--) {
      result[i] = a[b--] & 0xff;
      final int bytesRemaining = (b - keep) + 1;
      final int bytesToTransfer = Math.min(3,bytesRemaining);
      for (int j = 8; j <= (bytesToTransfer << 3); j += 8) {
        result[i] |= ((a[b--] & 0xff) << j); } }
    Ints.reverse(result);
    return make(result); }

  public static final BoundedNatural valueOf (final BigInteger u) {
    assert 0<=u.signum();
    return fromBigEndianBytes(u.toByteArray()); }

  //-------------------------------------------------------------

  public static final BoundedNatural valueOf (final String s,
                                              final int radix) {
    return make(Ints.littleEndian(s,radix)); }

  public static final BoundedNatural valueOf (final String s) {
    return valueOf(s,0x10); }

  /** <code>0L &le; u</code>. */

  public static final BoundedNatural valueOf (final long u) {
    assert 0L<=u;
    //if (0L==u) { return zero(); }
    final int lo = (int) u;
    final int hi = (int) hiWord(u);
    if (0==hi) {
      if (0==lo) { return new BoundedNatural(new int[0]); }
      return new BoundedNatural(new int[] {lo}); }
    return new BoundedNatural(new int[] { lo,hi }); }

  public static final BoundedNatural valueOf (final long u,
                                              final int upShift) {
    assert 0<=u;
    assert 0<=upShift;
    final int iShift = (upShift>>>5);
    final int bShift = (upShift&0x1f);
    final int[] vv;
    if (0==bShift) {
      vv = new int[iShift+2];
      vv[iShift] = (int) u;
      vv[iShift+1] = (int) hiWord(u); }
    else {
      final long us = (u<<bShift);
      final int vv0 = (int) us;
      final int vv1 = (int) hiWord(us);
      final int vv2 = (int) (u>>>(64-bShift));
      if (0!=vv2) {
        vv = new int[iShift+3];
        vv[iShift] = vv0;
        vv[iShift+1] = vv1;
        vv[iShift+2] = vv2; }
      else if (0!=vv1) {
        vv = new int[iShift+2];
        vv[iShift] = vv0;
        vv[iShift+1] = vv1; }
      else if (0!=vv0) {
        vv = new int[iShift+1];
        vv[iShift] = vv0; }
      else { return ZERO; } }
    NaturalInts.checkOverflow(vv.length);
    return unsafe(vv); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
