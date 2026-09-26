package mop.java.geometry.euclidean;

import mop.java.numbers.BigFloatX;

/** Subset of <code>R<sup>2</sup></code>, with <code>BigFloatX</code>.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-25
 */

public final record VectorBF2X(BigFloatX x, BigFloatX y)
  implements VectorR2<BigFloatX> {

  public final BigFloatX getX () { return x; }
  public final BigFloatX getY () { return y; }

  @Override
  public final VectorBF2X add (final VectorR2<BigFloatX> v) {
    return new VectorBF2X(x.add(v.getX()), y.add(v.getY())); }

  @Override
  public final VectorBF2X subtract (final VectorR2<BigFloatX> v) {
    return new VectorBF2X(x.subtract(v.getX()),
                          y.subtract(v.getY())); }

  public static final VectorBF2X dif (final VectorD2 v0,
                                      final VectorD2 v1) {
    return new VectorBF2X(BigFloatX.dif(v0.getX(), v1.getX()),
                          BigFloatX.dif(v0.getY(),v1.getY())); }

  @Override
  public VectorBF2X scale (final BigFloatX a) {
    return new VectorBF2X(a.multiply(x), a.multiply(y)); }

  @Override
  public final BigFloatX l2norm2 () { return BigFloatX.l2norm2(x,y); }

  @Override
  public final BigFloatX wedge (final VectorR2<BigFloatX> v) {
//    return BigFloatX.wedge(x, y, v.getX(), v.getY()); }
    // assumes coordinates are all finite and sums of exponents fit
    // in int
    return BigFloatX.wedge(x, y, v.getX(), v.getY()); }


  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------

