package mop.java.scripts.accumulators;

import org.apache.commons.math3.fraction.BigFraction;

import mop.java.numbers.BigFractions;

//----------------------------------------------------------------
/** BigFraction from Long bug?
 * <br>
 * jy --source 25 src/scripts/java/mop/java/scripts/LBF.java
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */

@SuppressWarnings("unused")
public final class LBF {

  public static final void main (final String[] args) {

    final long l = -1321315252193142600L;
    System.out.println("long:" + l);
    final Object ll = l;
    System.out.println("Long:" + ll);
    final BigFraction bf0 = (BigFraction) BigFractions.toBigFraction(ll);
    System.out.println(" BigFractions.toBigFraction(Long):" + bf0);
  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
