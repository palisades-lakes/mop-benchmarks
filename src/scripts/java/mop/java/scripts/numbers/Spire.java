package mop.java.scripts.numbers;

import spire.math.Algebraic;
import spire.math.Algebraic$;
import spire.math.Rational;
import spire.math.Rational$;
import spire.math.Real;
import spire.math.Real$;
import spire.math.RealAlgebra;

/** Experiment with calling Scala spire.
 *
 * <pre>
 * j --source 25 -ea src/scripts/java/nzqr/jmh/scripts/Spire.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-03
 */
@SuppressWarnings({"unused"})
public final class Spire {


  public static final void main (final String[] args) {

    System.out.println(Math.sqrt(Math.PI));

    final Rational q0 = Rational$.MODULE$.apply(Math.PI);
    System.out.println(q0);
    System.out.println(q0.doubleValue());
    final Rational q1 = q0.$plus(q0);
    System.out.println(q1);
    System.out.println(q1.doubleValue());

    System.out.println();
    final Algebraic a0 = Algebraic$.MODULE$.apply(Math.PI);
    System.out.println(a0);
    System.out.println(a0.doubleValue());
    final Algebraic a1 = a0.$plus(a0);
    System.out.println(a1);
    System.out.println(a1.doubleValue());
    final Algebraic a2 = a1.sqrt();
    System.out.println(a2);
    System.out.println(a2.doubleValue());

    System.out.println();
    final Real r0 = Real$.MODULE$.apply(Math.PI);
    System.out.println(r0);
    System.out.println(r0.doubleValue());
    final Real r1 = r0.$plus(r0);
    System.out.println(r1);
    System.out.println(r1.doubleValue());
    final Real r2 = r1.sqrt();
    System.out.println(r2);
    System.out.println(r2.doubleValue());

    System.out.println();
    final RealAlgebra ra = new RealAlgebra();
    final Real r3 = ra.fromAlgebraic(a2);
    System.out.println(r3);
    System.out.println(r3.doubleValue());
  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
