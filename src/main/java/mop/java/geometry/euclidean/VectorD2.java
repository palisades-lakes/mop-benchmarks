package mop.java.geometry.euclidean;

/** Interface for classes representing subsets of
 * <code>R<sup>2</sup></code>, allowing coordinate representations
 * that are more precise than <code>double/Double</code>.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-21
 */

public final record VectorD2 (double x, double y)
  implements VectorR2<Double> {

  public final Double getX () { return x; }
  public final Double getY () { return y; }

  @Override
  public final VectorD2 add (final VectorR2<Double> v) {
    return new VectorD2(x+v.getX(),y+v.getY()); }

  @Override
  public final VectorD2 subtract (final VectorR2<Double> v) {
    return new VectorD2(x-v.getX(),y-v.getY()); }

  @Override
  public VectorD2 scale (final Double a) {
    return new VectorD2(a*x,a*y); }

  // TODO: more accurate fma version?
  @Override
  public final Double l2norm2 () { return x*x + y*y; }

  // TODO: more accurate fma version?
  @Override
  public final Double wedge (final VectorR2<Double> v) {
    return x*v.getY() - y*v.getX(); }

  /** Project <code>p</code> onto (the boundary of) <code>c</code>. */
  public final VectorD2 project (final VectorD2 c,
                                 final double r) {
    final VectorD2 v = subtract(c);
    final double l2 = Math.sqrt(l2norm2());
    return v.scale(r/l2).add(c); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------

