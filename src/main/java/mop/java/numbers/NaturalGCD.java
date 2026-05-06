package mop.java.numbers;

/** gcd of natural numbers.
 * <br>
 * Non-instantiable.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2024-01-01
 */

public final class NaturalGCD {


  /** Algorithm B from Knuth section 4.5.2 */

  private static final BoundedNatural gcdKnuth (final BoundedNatural u,
                                                final BoundedNatural v) {
    BoundedNatural a = u;
    BoundedNatural b = v;
    // B1
    final int sa = a.loBit();
    final int s = Math.min(sa,b.loBit());
    if (s!=0) { a = a.shiftDown(s); b = b.shiftDown(s); }
    // B2
    int tsign = (s==sa) ? -1 : 1;
    BoundedNatural t = (0<tsign) ? a : b;
    for (int lb=t.loBit();lb>=0;lb=t.loBit()) {
      // B3 and B4
      t = t.shiftDown(lb);
      // step B5
      if (0<tsign) { a = t; } else { b = t; }
      final int an = a.hiInt();
      final int bn = b.hiInt();
      if ((an<2) && (bn<2)) {
        final int x = a.word(an-1);
        final int y = b.word(bn-1);
        BoundedNatural r = BoundedNatural.valueOf(
          Ints.unsignedGcd(x, y));
        if (s > 0) { r = r.shiftUp(s); }
        return r; }
      // B6
      tsign = a.compareTo(b);
      if (0==tsign) {
        break; }
      else if (0<tsign) { a = a.subtract(b); t = a;  }
      else { b = b.subtract(a); t = b; }
    }
    if (s > 0) { a = a.shiftUp(s); }
    return a; }

  //--------------------------------------------------------------
  /** Use Euclid until the numbers are approximately the
   * same length, then use the Knuth algorithm.
   */

  static final BoundedNatural gcd (final BoundedNatural u,
                                           final BoundedNatural v) {
    BoundedNatural a = u;
    BoundedNatural b = v;
    while (!b.isZero()) {
      if (Math.abs(a.hiInt()-b.hiInt()) < 2) { return gcdKnuth(a,b); }
      final BoundedNatural[] qr = a.divideAndRemainder(b);
      a = b;
      b = qr[1]; }
    return a; }

  //--------------------------------------------------------------
  // disable constructor
  //--------------------------------------------------------------

  private NaturalGCD () {
    throw new
    UnsupportedOperationException(
      "can't instantiate " + getClass().getCanonicalName()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
