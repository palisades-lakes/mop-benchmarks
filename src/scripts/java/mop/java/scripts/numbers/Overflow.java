package mop.java.scripts.numbers;

import mop.java.numbers.BoundedNatural;

//----------------------------------------------------------------
/** Test overflow for various number implementations.
 * <p>
 * <pre>
 * mvn clean install -DskipTests
 * j src/scripts/java/nzqr/java/scripts/numbers/Overflow.java
 * jy src/scripts/java/nzqr/java/scripts/numbers/Overflow.java
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2022-08-25
 */

public final class Overflow {

  private static final void overflowBoundedNatural1 () {
    final BoundedNatural n = BoundedNatural.maxValue().add(1);
    System.out.println(n.hiBit()); }

  private static final void overflowBoundedNaturalOne () {
    BoundedNatural n = BoundedNatural.maxValue();
    n = n.add(n.one());
    System.out.println(n.hiBit()); }

  private static final void overflowBoundedNatural () {
    BoundedNatural n = BoundedNatural.maxValue();
    n = n.add(n);
    System.out.println(n.hiBit()); }

  public static final void main (final String[] args) {
    System.out.println();
    overflowBoundedNatural();
    overflowBoundedNaturalOne();
    overflowBoundedNatural1();
  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
