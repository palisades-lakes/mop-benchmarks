package mop.java.geometry.euclidean;

import mop.java.numbers.Add;
import mop.java.numbers.Multiply;
import mop.java.numbers.Square;
import mop.java.numbers.Subtract;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Approximate elements of <code>Rationals^2</code>, the two-dimensional
 * vector (affine, euclidean) space over the rational number field.
 * <p>
 * Would be nice if the <code>x, y</code> coordinates could be
 * instances of some appropriate, field-like interface.
 * Doesn't work, because I want to use <code>Double</code> (and other
 * <code>Number</code> classes), as well as my own number classes
 * (eq <code>BigFloat</code>).
 * The <code>Number</code> don't provide any arithmetic ops
 * </p>
 */
public record Vector2Q (Object x, Object y) {

  public final Vector2Q add (final Vector2Q v) {
    return new Vector2Q(Add.add(x, v.x),
                        Add.add(y, v.y)); }

  public final Vector2Q subtract (final Vector2Q v) {
    return new Vector2Q(Subtract.subtract(x, v.x),
                        Subtract.subtract(y, v.y)); }

  public final Vector2Q subtract (final Vector2D v) {
    return new Vector2Q(Subtract.subtract(x, v.getX()),
                        Subtract.subtract(y, v.getY())); }

  public final Object l2norm2 () {
    return Add.add(Square.square(x), Square.square(y)); }

  /** AKA wedge product, cross product (in 3D), ... */
  public final Object blade (final Vector2Q v) {
    // TODO: more accurate version via fma?
    return Subtract.subtract(
      Multiply.multiply(x,v.y),
      Multiply.multiply(y,v.x)); }

}
