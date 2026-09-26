package mop.java.geometry.triangle;

import mop.java.geometry.euclidean.VectorD2;

/** Triangles "embedded" in <code>R<sup>2</sup></code>>.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-09-26
 */

public abstract class AbstractTriangle2D implements Triangle2D {

  private final VectorD2 p0;
  private final VectorD2 p1;
  private final VectorD2 p2;
  public final VectorD2 getP0 () { return p0; }
  public final VectorD2 getP1 () { return p1; }
  public final VectorD2 getP2 () { return p2; }

  //--------------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------------
  // TODO: hashcode, equals

  public final String toHexString () {
    return getClass().getSimpleName() + "[" +
      p0.toHexString() + ", " +
      p1.toHexString() + ", " +
      p2.toHexString() + "]"; }

  public String toString () { return toHexString(); }
  public String description () { return toString(); }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public AbstractTriangle2D (final VectorD2 a,
                             final VectorD2 b,
                             final VectorD2 c) {
    super();
    this.p0 = a; this.p1 = b; this.p2 = c; }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
