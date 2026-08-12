package mop.java.geometry.triangle;

import mop.java.numbers.BigFloat;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Standard calculations implemented in BigFloat.
 * Should be exact, up to BigFloat resolution.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-08-11
 */

public final class BigFloatTriangle2D extends Triangle2D {

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

    final double px = p.getX();
    final double py = p.getY();

    // TODO: BigFloatVector operations
    final BigFloat apx = BigFloat.dif(a.getX(),px);
    final BigFloat bpx = BigFloat.dif(b.getX(),px);
    final BigFloat cpx = BigFloat.dif(c.getX(),px);
    final BigFloat apy = BigFloat.dif(a.getY(),py);
    final BigFloat bpy = BigFloat.dif(b.getY(),py);
    final BigFloat cpy = BigFloat.dif(c.getY(),py);

    final BigFloat axb = BigFloat.crossProduct(apx,apy,bpx,bpy);
    final BigFloat bxc = BigFloat.crossProduct(bpx,bpy,cpx,cpy);
    final BigFloat cxa = BigFloat.crossProduct(cpx,cpy,apx,apy);

    final BigFloat a2 = BigFloat.l2norm2(apx,apy);
    final BigFloat b2 = BigFloat.l2norm2(bpx,bpy);
    final BigFloat c2 = BigFloat.l2norm2(cpx,cpy);

  // TODO: 3d dot product
    return BigFloat.dot(a2,b2,c2,bxc,cxa,axb).doubleValue(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  private BigFloatTriangle2D (final Vector2D a,
                              final Vector2D b,
                              final Vector2D c)  {
    super(a,b,c); }

  public static final Triangle2D of (final Vector2D a,
                                     final Vector2D b,
                                     final Vector2D c) {
    return new BigFloatTriangle2D(a,b,c); }

  /** Convert other triangle classes. */

  public static final Triangle2D from (final Triangle2D t) {
    return of(t.getP0(), t.getP1(), t.getP2()); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
