package mop.java.numbers;

import static mop.java.numbers.Numbers.unsigned;

/** gcd of natural numbers.
 * <br>
 * Non-instantiable.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2024-01-02
 */

final class NaturalDivide {

  //--------------------------------------------------------------
  /** Interpret {@code d} as unsigned. */

  static final BoundedNatural[]
  divideAndRemainder (final BoundedNatural u,
                      final int d) {
    if (1==d) { return new BoundedNatural[] { u, u.zero(), }; }
    final int nu = u.hiInt();
    final long dd = unsigned(d);
    if (1==nu) {
      final long nn = u.uword(0);
      final int q = (int) (nn/dd);
      final int r = (int) (nn-(q*dd));
      return new BoundedNatural[] { BoundedNatural.valueOf(q),
                                    BoundedNatural.valueOf(r), }; }

    BoundedNatural qq = u;
    final int shift = Integer.numberOfLeadingZeros(d);
    int r = u.word(u.hiInt()-1);
    long rr = unsigned(r);
    if (rr < dd) {
      qq = qq.setWord(qq.hiInt()-1,0); }
    else {
      final int rrdd = (int) (rr / dd);
      qq = qq.setWord(u.hiInt()-1,rrdd);
      r = (int) (rr-(rrdd*dd));
      rr = unsigned(r); }

    int xlen = u.hiInt();
    while (--xlen > 0) {
      final long nEst = (rr << 32) | u.uword(xlen-1);
      final int q;
      if (nEst >= 0) {
        q = (int) (nEst / dd);
        r = (int) (nEst - (q * dd)); }
      else {
        final long tmp = Ints.divWord(nEst,dd);
        q = (int) Numbers.loWord(tmp);
        r = (int) Numbers.hiWord(tmp); }
      qq = qq.setWord(xlen-1,q);
      rr = unsigned(r); }
    if (shift > 0) {
      return new BoundedNatural[] { qq, BoundedNatural.valueOf(r%d), }; }
    return new BoundedNatural[] { qq, BoundedNatural.valueOf(r),}; }


  //--------------------------------------------------------------
  // disable constructor
  //--------------------------------------------------------------

  private NaturalDivide () {
    throw new
    UnsupportedOperationException(
      "can't instantiate " + getClass().getCanonicalName()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
