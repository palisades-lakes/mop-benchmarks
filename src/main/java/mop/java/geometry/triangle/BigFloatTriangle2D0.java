package mop.java.geometry.triangle;

import mop.java.numbers.BigFloat;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Previous version of BigFloatTriangle2D00 for performance comparison.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-08-10
 */

public final class BigFloatTriangle2D0 extends Triangle2D {

  //--------------------------------------------------------------------

  public final boolean signedAreaExact () { return true; }

  // TODO: reduce the number of BigFloat instances.
  //  For example, implement BigFloat.sum(double,double);
  //  Also, triangle translation could be done just once.
  //  Consider boolean predicate, so can return the sign of the
  //  final BigFloat.
  // TODO: BigFloatVector, Vector<BigFloat>...

  public final double signedArea () {
    final Vector2D pa = getP0();
    final Vector2D pb = getP1();
    final Vector2D pc = getP2();

    final BigFloat ax = BigFloat.valueOf(pa.getX());
    final BigFloat ay = BigFloat.valueOf(pa.getY());
    final BigFloat bx = BigFloat.valueOf(pb.getX());
    final BigFloat by = BigFloat.valueOf(pb.getY());
    final BigFloat cx = BigFloat.valueOf(pc.getX());
    final BigFloat cy = BigFloat.valueOf(pc.getY());
    final BigFloat acx = ax.subtract(cx);
    final BigFloat acy = ay.subtract(cy);
    final BigFloat bcx = bx.subtract(cx);
    final BigFloat bcy = by.subtract(cy);
    return
      ((acx.multiply(bcy)).subtract(acy.multiply(bcx))).doubleValue(); }

  //--------------------------------------------------------------------

  public final boolean inCircleExact () { return true; }

  public final double inCircle (final Vector2D p) {
    // TODO: move BigFloat creation to BigFloat.subtract(double,double)
    final Vector2D a = getP0();
    final Vector2D b = getP1();
    final Vector2D c = getP2();

    final double px = -p.getX();
    final double py = -p.getY();
//    final BigFloat ax = BigFloat.valueOf(a.getX());
//    final BigFloat ay = BigFloat.valueOf(a.getY());
//    final BigFloat bx = BigFloat.valueOf(b.getX());
//    final BigFloat by = BigFloat.valueOf(b.getY());
//    final BigFloat cx = BigFloat.valueOf(c.getX());
//    final BigFloat cy = BigFloat.valueOf(c.getY());
//    final BigFloat apx = ax.add(px);
//    final BigFloat bpx = bx.add(px);
//    final BigFloat cpx = cx.add(px);
//    final BigFloat apy = ay.add(py);
//    final BigFloat bpy = by.add(py);
//    final BigFloat cpy = cy.add(py);

    final BigFloat apx = BigFloat.sum(a.getX(),px);
    final BigFloat bpx = BigFloat.sum(b.getX(),px);
    final BigFloat cpx = BigFloat.sum(c.getX(),px);
    final BigFloat apy = BigFloat.sum(a.getY(),py);
    final BigFloat bpy = BigFloat.sum(b.getY(),py);
    final BigFloat cpy = BigFloat.sum(c.getY(),py);

    // TODO: crossProduct
    final BigFloat axb = apx.multiply(bpy).subtract(bpx.multiply(apy));
    final BigFloat bxc = bpx.multiply(cpy).subtract(cpx.multiply(bpy));
    final BigFloat cxa = cpx.multiply(apy).subtract(apx.multiply(cpy));
    // TODO: l2norm2
    final BigFloat a2 = apx.square().add(apy.square());
    final BigFloat b2 = bpx.square().add(bpy.square());
    final BigFloat c2 = cpx.square().add(cpy.square());

    return a2.multiply(bxc)
             .add(b2.multiply(cxa))
             .add(c2.multiply(axb))
             .doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private BigFloatTriangle2D0 (final Vector2D a,
                               final Vector2D b,
                               final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new BigFloatTriangle2D0(a, b, c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
