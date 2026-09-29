package mop.java.geometry.euclidean;

/** Interface for classes representing subsets of
 * <code>R<sup>2</sup></code>, allowing coordinate representations
 * that are more precise than <code>double/Double</code>.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-27
 */

public final record VectorD2 (double x, double y)
  implements VectorR2<Double> {

  public final VectorD2 add (final VectorD2 v) {
    return new VectorD2(x+v.x,y+v.y); }

  public final VectorD2 subtract (final VectorD2 v) {
    return new VectorD2(x-v.x,y-v.y); }

  public final double dL2norm2 () { return x*x + y*y; }

  public final double wedge (final VectorD2 v) {
    return x*v.y - y*v.x; }

  /** Project <code>p</code> onto (the boundary of) <code>c</code>. */
  public final VectorD2 project (final VectorD2 c,
                                 final double r) {
    final VectorD2 v = subtract(c);
    final double l2 = Math.sqrt(dL2norm2());
    return v.scale(r/l2).add(c); }

  //--------------------------------------------------------------------
  // VectorR2<Double>
  //--------------------------------------------------------------------

  @Override
  public final Double getX () { return x; }
  @Override
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
  public final Double l2norm2 () { return dL2norm2(); }

  // TODO: more accurate fma version?
  @Override
  public final Double wedge (final VectorR2<Double> v) {
    return x*v.getY() - y*v.getX(); }

  @Override
  public final String toHexString () {
    return "(" +
      Double.toHexString(x) + "," +
      Double.toHexString(y) + ")"; }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------

