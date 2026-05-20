package mop.java.geometry.predicates.jts;

import mop.java.geometry.predicates.Predicate;
import org.locationtech.jts.math.DD;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-05-19
 */

public final class DDFast implements Predicate {

//--------------------------------------------------------------------

  public static DD triAreaDDFast (
    double[] a, double[] b, double[] c) {

    DD t1 = DD.valueOf(b[0]).selfSubtract(a[0])
              .selfMultiply(
                DD.valueOf(c[1]).selfSubtract(a[1]));

    DD t2 = DD.valueOf(b[1]).selfSubtract(a[1])
              .selfMultiply(
                DD.valueOf(c[0]).selfSubtract(a[0]));

    return t1.selfSubtract(t2);
    }

  //--------------------------------------------------------------------
  // incircle
  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  public final double incircle (final double[] a,
                                final double[] b,
                                final double[] c,
                                final double[] p) {
    DD aTerm = (DD.sqr(a[0]).selfAdd(DD.sqr(a[1])))
      .selfMultiply(triAreaDDFast(b, c, p));
    DD bTerm = (DD.sqr(b[0]).selfAdd(DD.sqr(b[1])))
      .selfMultiply(triAreaDDFast(a, c, p));
    DD cTerm = (DD.sqr(c[0]).selfAdd(DD.sqr(c[1])))
      .selfMultiply(triAreaDDFast(a, b, p));
    DD pTerm = (DD.sqr(p[0]).selfAdd(DD.sqr(p[1])))
      .selfMultiply(triAreaDDFast(a, b, c));

    DD sum = aTerm.selfSubtract(bTerm).selfAdd(cTerm).selfSubtract(pTerm);
    return sum.doubleValue();
  }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public DDFast () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
