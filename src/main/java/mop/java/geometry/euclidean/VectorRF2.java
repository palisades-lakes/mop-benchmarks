package mop.java.geometry.euclidean;

import mop.java.numbers.RationalFloat;

/** Subset of <code>R<sup>2</sup></code>, with <code>RationalFloat</code>.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 202-09-29
 */

public final record VectorRF2(RationalFloat x, RationalFloat y)
  implements VectorR2<RationalFloat> {

  public final RationalFloat getX () { return x; }
  public final RationalFloat getY () { return y; }

  @Override
  public final VectorRF2 add (final VectorR2<RationalFloat> v) {
    return new VectorRF2(x.add(v.getX()), y.add(v.getY())); }

  @Override
  public final VectorRF2 subtract (final VectorR2<RationalFloat> v) {
    return new VectorRF2(x.subtract(v.getX()),
                         y.subtract(v.getY())); }

  public static final VectorRF2 dif (final VectorD2 v0,
                                     final VectorD2 v1) {
    return new VectorRF2(RationalFloat.dif(v0.x(),v1.x()),
                         RationalFloat.dif(v0.y(),v1.y())); }

  @Override
  public VectorRF2 scale (final RationalFloat a) {
    return new VectorRF2(a.multiply(x), a.multiply(y)); }

  @Override
  public final RationalFloat l2norm2 () {
    return RationalFloat.l2norm2(x,y); }

  @Override
  public final RationalFloat wedge (final VectorR2<RationalFloat> v) {
    return RationalFloat.wedge(x, y, v.getX(), v.getY()); }

  @Override
  public final String toHexString () {
    return "(" + x.toHexString() + "," + y.toHexString() + ")"; }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------

