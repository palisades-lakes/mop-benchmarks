package mop.java.numbers;

import static mop.java.numbers.Numbers.hiWord;
import static mop.java.numbers.Numbers.unsigned;

/** Addition of natural numbersrepresented by (unsigned) int[].
 * <br>
 * Separating these methods into a small static class seems to
 * make addition/subtraction of BoundedNatural faster,
 * but I don't pretend to understand why.
 * <br>
 * Non-instantiable.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2024-01-22
 */

final class NaturalAdd {

  //--------------------------------------------------------------

  static final int[] add (final int[] tt, final int nt,
                          final int[] uu, final int nu) {
    if (nt<nu) { return add(uu,nu,tt,nt); }
    final int[] vv = new int[nt+1];
    long sum = 0L;
    int i=0;
    for (;i<nu;i++) {
      sum += unsigned(tt[i]) + unsigned(uu[i]);
      vv[i] = (int) sum;
      sum = hiWord(sum);}
    for (;(0L!=sum) && (i<nt);i++) {
      sum += unsigned(tt[i]);
      vv[i] = (int) sum;
      sum = hiWord(sum);}
    if (i<nt) { System.arraycopy(tt,i,vv,i,nt-i); }
    else if (0L!=sum) { vv[nt] = 1; }
    return vv; }

  // UNSAFE!!! should check that <code>tt</code> is "larger" than
  // <code>uu</code>.
  // TODO: what happens if dif is negative?
   static final int[] subtract (final int[] tt, final int nt,
                                final int[] uu, final int nu) {
    final int[] vv = new int[nt];
    long dif = 0L;
    int i=0;
    for (;i<nu;i++) {
      dif += unsigned(tt[i])-unsigned(uu[i]);
      vv[i] = (int) dif;
      dif= (dif>>32); }
    for (;(0L!=dif) && (i<nt);i++) {
      dif += unsigned(tt[i]);
      vv[i] = (int) dif;
      dif = (dif>>32); }
    //assert 0L==dif;
    if (i<nt) { System.arraycopy(tt,i,vv,i,nt-i); }
    return vv; }

  //--------------------------------------------------------------
  // disable constructor
  //--------------------------------------------------------------

  private NaturalAdd () {
    throw new UnsupportedOperationException(
      "can't instantiate " + getClass().getCanonicalName()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
