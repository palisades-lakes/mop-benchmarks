package mop.java.geometry.predicates.jts;

import mop.java.geometry.predicates.Predicate;
import org.locationtech.jts.math.DD;

/** From org.locationtech.jts.triangulate.quadedge.TrianglePredicate
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-05-19
 */

public final class DDNormalized implements Predicate {

  //--------------------------------------------------------------------
  // incircle
  //--------------------------------------------------------------------
  /** TrianglePredicate.isInCircleNonRobust.
   */
  public final double incircle (final double[] a,
                                final double[] b,
                                final double[] c,
                                final double[] p) {
    DD adx = DD.valueOf(a[0]).selfSubtract(p[0]);
    DD ady = DD.valueOf(a[1]).selfSubtract(p[1]);
    DD bdx = DD.valueOf(b[0]).selfSubtract(p[0]);
    DD bdy = DD.valueOf(b[1]).selfSubtract(p[1]);
    DD cdx = DD.valueOf(c[0]).selfSubtract(p[0]);
    DD cdy = DD.valueOf(c[1]).selfSubtract(p[1]);

    DD abdet = adx.multiply(bdy).selfSubtract(bdx.multiply(ady));
    DD bcdet = bdx.multiply(cdy).selfSubtract(cdx.multiply(bdy));
    DD cadet = cdx.multiply(ady).selfSubtract(adx.multiply(cdy));
    DD alift = adx.multiply(adx).selfAdd(ady.multiply(ady));
    DD blift = bdx.multiply(bdx).selfAdd(bdy.multiply(bdy));
    DD clift = cdx.multiply(cdx).selfAdd(cdy.multiply(cdy));

    DD sum = alift.selfMultiply(bcdet)
                  .selfAdd(blift.selfMultiply(cadet))
                  .selfAdd(clift.selfMultiply(abdet));

    return sum.doubleValue();
  }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  // TODO: singleton?

  public DDNormalized () { super(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
