package mop.java.numbers;

/**  Division of natural numbers by the Burnnikel-Ziegler algorithm.
 * <br>
 * Isolated into a pure static (no instances) class with minimal
 * dependencies.
 * <br>
 * @author palisades dot lakes at gmail dot com
 * @version 2024-01-02
 */

//@SuppressWarnings("unchecked")
final class BurnikelZieglerDivision {

  //--------------------------------------------------------------
  // divide
  //--------------------------------------------------------------
  /** This method implements algorithm 2 from pg. 5 of the
   * Burnikel-Ziegler paper. It divides a 3n-digit number by a
   * 2n-digit number.<br/>
   * The parameter beta is 2<sup>32</sup> so all shifts are
   * multiples of 32 bits.<br/>
   * <br/>
   * {@code this} must be a nonnegative number such that
   * {@code 2*this.hiBit() <= 3*b.hiBit()}
   */

  private static final BoundedNatural[]
  divide3n2n (final BoundedNatural a,
              final BoundedNatural b) {

    final int n = b.hiInt() / 2;   // half the length of b in ints

    // step 1: view this as [a1,a2,a3] where each ai is n ints
    // or less; let a12=[a1,a2]
    BoundedNatural a12 = a.shiftDown(32*n);

    // step 2: view b as [b1,b2] where each bi is n ints or less
    BoundedNatural b1 = b.shiftDown(n*32);
    final BoundedNatural b2 = b.words(0, n);
    BoundedNatural r;
    BoundedNatural d;
    BoundedNatural q;
    // TODO: word shift or shifted comparison
    if (a.compareTo(b.shiftUp(32*n)) < 0) {
      // step 3a: if a1<b1, let quotient=a12/b1 and r=a12%b1
      // Doesn't need modified a12
      final BoundedNatural[] qr = divide2n1n(a12,b1);
      q = qr[0];
      r = qr[1];
      // step 4: d=quotient*b2
      d = q.multiply(b2); }
    else {
      // step 3b: if a1>=b1, let quotient=beta^n-1
      //and r=a12-b1*2^n+b1
      q = BoundedNatural.ones(n);
      a12 = a12.add(b1);
      b1 = b1.shiftUp(32*n);
      r = a12.subtract(b1);
      // step 4: d=quotient*b2=(b2 << 32*n) - b2
      d = b2.shiftUp(32*n).subtract(b2); }
    // step 5: r = r*beta^n + a3 - d (paper says a4)
    // However, don't subtract d until after the while loop
    // so r doesn't become negative
    r = r.shiftUp(n<<5).add(a.words(0,n));
    // step 6: add b until r>=d
    while (r.compareTo(d) < 0) {
      r = r.add(b);
      q = q.subtract(a.one()); }
    return new BoundedNatural[] { q, r.subtract(d), }; }

  /** This method implements algorithm 1 from pg. 4 of the
   * Burnikel-Ziegler paper. It divides a 2n-digit number by an
   * n-digit number.<br/>
   * The parameter beta is 2<sup>32</sup> so all shifts are
   * multiples of 32 bits. <br/>
   * {@code this} must be a nonnegative number such that
   * {@code this.hiBit() <= 2*b.hiBit()}
   * @param b a positive number such that {@code b.hiBit()} is even
   */

  private static final BoundedNatural[]
  divide2n1n (final BoundedNatural a,
              final BoundedNatural b) {
    final int n = b.hiInt();
    //assert a.isValid();
    //assert b.isValid();
    // step 1: base case
    if (((n%2) != 0) || (n < BoundedNatural.BURNIKEL_ZIEGLER_THRESHOLD)) {
      final BoundedNatural[] qr = a.divideAndRemainderKnuth(b);
      return new BoundedNatural[] { qr[0], qr[1], }; }

    // step 2: view this as [a1,a2,a3,a4]
    // where each ai is n/2 ints or less
    // aUpper = [a1,a2,a3]
    final BoundedNatural aUpper = a.shiftDown(32*(n/2));
    BoundedNatural aa = a.words(0,n/2); // this = a4

    // step 3: q1=aUpper/b, r1=aUpper%b
    final BoundedNatural[] qr1 = divide3n2n(aUpper,b);

    // step 4: quotient=[r1,this]/b, r2=[r1,this]%b
    aa = aa.add(qr1[1],32*(n/2));   // this = [r1,this]

    final BoundedNatural[] qr2 = divide3n2n(aa,b);
    // step 5: let quotient=[q1,quotient] and return r2
    final BoundedNatural q2 = qr2[0].add(qr1[0], 32*(n/2));
    return new BoundedNatural[] { q2,qr2[1], }; }

  //--------------------------------------------------------------------
  /** Returns a {@code BoundedNatural} containing
   * {@code blockLength} ints from {@code this} number, starting
   * at {@code index*blockLength}.<br/>
   * Used by Burnikel-Ziegler division.
   * @param index the block index
   * @param numBlocks the total number of blocks in {@code this}
   * @param blockLength length of one block in units of 32 bits
   */

  private static final BoundedNatural getBlock (final BoundedNatural t,
                                                final int index,
                                                final int numBlocks,
                                                final int blockLength) {
    final int blockStart = index * blockLength;
    if (blockStart >= t.hiInt()) { return t.zero(); }
    final int blockEnd;
    if (index == (numBlocks-1)) { blockEnd = t.hiInt(); }
    else { blockEnd = (index+1) * blockLength; }
    if (blockEnd > t.hiInt()) { return t.zero(); }
    return t.words(blockStart,blockEnd); }

  //--------------------------------------------------------------

  static final BoundedNatural[]
  divideAndRemainder (final BoundedNatural u,
                      final BoundedNatural v) {
    final int s = v.hiInt();

    // step 1: let m = min{2^k | (2^k)*BURNIKEL_ZIEGLER_THRESHOLD > s}
    final int s0 = s/BoundedNatural.BURNIKEL_ZIEGLER_THRESHOLD;
    final int m = 1 << (32-Integer.numberOfLeadingZeros(s0));

    final int j = ((s+m)-1) / m; // step 2a: j = ceil(s/m)
    final int n = j * m; // step 2b: block length in 32-bit units
    final long n32 = 32L * n; // block length in bits
    // step 3: sigma = max{T | (2^T)*B < beta^n}
    final int sigma = (int) Math.max(0, n32-v.hiBit());

    // step 4a: shift b so its length is a multiple of n
    //assert 0<=sigma;
    final BoundedNatural bShifted = v.shiftUp(sigma);
    // step 4b: shift a by the same amount
    final BoundedNatural aShifted = u.shiftUp(sigma);

    // step 5: t is the number of blocks needed to accommodate a
    // plus one additional bit
    final int t = Math.max(2,(int) ((aShifted.hiBit()+n32) / n32));

    // step 6: conceptually split a into blocks a[t-1], ..., a[0]
    // the most significant block of a
    final BoundedNatural a1 = getBlock(aShifted,t-1, t, n);

    // step 7: z[t-2] = [a[t-1], a[t-2]]
    // the second to most significant block
    BoundedNatural z = getBlock(aShifted,t-2, t, n);
    z = z.add(a1,32*n);   // z[t-2]

    // schoolbook division on blocks, dividing 2-block by 1-block
    BoundedNatural q = u.zero();
    for (int i=t-2;i>0;i--) {
      // step 8a: compute (qi,ri) such that z=b*qi+ri
      // Doesn't need modified z
      final BoundedNatural[] qri = divide2n1n(z,bShifted);
      // step 8b: z = [ri, a[i-1]]
      z = getBlock(aShifted,i-1, t, n);   // a[i-1]
      z = z.add(qri[1], 32*n);
      // update q (part of step 9)
      q = q.add(qri[0],(i*n)<<5); }
    // final iteration of step 8: do the loop one more time
    // for i=0 but leave z unchanged
    //assert z.isValid();
    //assert bShifted.isValid();
    final BoundedNatural[] qri = divide2n1n(z,bShifted);

    // step 9: a and b were shifted, so shift back
    return new BoundedNatural[] {
      q.add(qri[0]),
      qri[1].shiftDown(sigma), }; }

  //--------------------------------------------------------------
  // construction
  //-------------------------------------------------------------
  /** not instantiable. */
  private BurnikelZieglerDivision () {
    throw new
      UnsupportedOperationException(
      "can't instantiate " + getClass().getCanonicalName()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
