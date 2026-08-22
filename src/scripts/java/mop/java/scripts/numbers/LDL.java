package mop.java.scripts.numbers;

//----------------------------------------------------------------
/** Test long -> double -> long arithmetic.
 * <p>
 * <pre>
 * j src/scripts/java/mop/java/scripts/numbers/LDL.java
 * jy src/scripts/java/mop/java/scripts/numbers/LDL.java
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */

public final class LDL {

  @SuppressWarnings("unused")
  public static final void main (final String[] args) {

    for (int i=0;i<256;i++) {
      final byte b = (byte) i;
      System.out.println(b);
    }

    final long l = Long.MAX_VALUE;
    for (int i=0;i<32;i++) {
      final double d = l - 1.0e0;
      final long ld = (long) d;
      assert ld == (l - 1L) :
        "\ni=" + i +
        "\n        Long: " + Long.toString(l-1L,16) +
        "\n      Double: " + Double.toHexString(d) +
        "\n(long)Double: " + Long.toString(ld,16);
    }


  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
