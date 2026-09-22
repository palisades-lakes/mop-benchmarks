package mop.java.geometry.euclidean;

/** Interface for classes representing subsets of
 * <code>R<sup>2</sup></code>, allowing coordinate representations
 * that are more precise than <code>double/Double</code>.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-21
 */

public interface VectorR2<C> {

  /** The exact value of the X coordinate. */
  public C getX ();

  /** The exact value of the y coordinate. */
  public C getY ();

  public VectorR2<C> add (final VectorR2<C> v);

  public VectorR2<C> subtract (final VectorR2<C> v);

  public VectorR2<C> scale (final C a);

  public C l2norm2 ();

  /** AKA wedge product, cross product (in 3D), ... */
  public C wedge (final VectorR2<C> v);

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------

