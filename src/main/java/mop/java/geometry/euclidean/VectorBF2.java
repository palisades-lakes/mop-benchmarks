package mop.java.geometry.euclidean;

import mop.java.numbers.BigFloat;

/** Subset of <code>R<sup>2</sup></code>, with <code>BigFloat</code>.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-21
 */

public final record VectorBF2(BigFloat x, BigFloat y)
  implements VectorR2<BigFloat> {

  public final BigFloat getX () { return x; }
  public final BigFloat getY () { return y; }

  @Override
  public final VectorBF2 add (final VectorR2<BigFloat> v) {
    return new VectorBF2(x.add(v.getX()), y.add(v.getY())); }

  @Override
  public final VectorBF2 subtract (final VectorR2<BigFloat> v) {
    return new VectorBF2(x.subtract(v.getX()),
                         y.subtract(v.getY())); }

  public static final VectorBF2 dif (final VectorD2 v0,
                                     final VectorD2 v1) {
    return new VectorBF2(BigFloat.dif(v0.getX(),v1.getX()),
                         BigFloat.dif(v0.getY(),v1.getY())); }

  @Override
  public VectorBF2 scale (final BigFloat a) {
    return new VectorBF2(a.multiply(x),a.multiply(y)); }

  @Override
  public final BigFloat l2norm2 () { return BigFloat.l2norm2(x,y); }

  @Override
  public final BigFloat wedge (final VectorR2<BigFloat> v) {
    return BigFloat.wedge(x, y, v.getX(), v.getY()); }


  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------

