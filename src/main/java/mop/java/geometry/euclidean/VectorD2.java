package mop.java.geometry.euclidean;

import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Interface for classes representing subsets of
 * <code>R<sup>2</sup></code>, allowing coordinate representations
 * that are more precise than <code>double/Double</code>.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-21
 */

public final record VectorD2 (double x, double y)
  implements VectorR2<Double> {

  public final Double getX () { return x; };
  public final Double getY () { return y; }

  @Override
  public final VectorD2 add (final VectorR2<Double> v) {
    return new VectorD2(x+v.getX(),y+v.getY()); };

  @Override
  public final VectorD2 subtract (final VectorR2<Double> v) {
    return new VectorD2(x-v.getX(),y-v.getY()); };

  public static final VectorD2 dif (final Vector2D v0,
                                     final Vector2D v1) {
    return new VectorD2(v0.getX() - v1.getX(),
                         v0.getY() - v1.getY()); };

  // TODO: more accurate fma version?
  @Override
  public final Double l2norm2 () { return x*x + y*y; }

  // TODO: more accurate fma version?
  @Override
  public final Double wedge (final VectorR2<Double> v) {
    return x*v.getY() - y*v.getX(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------

