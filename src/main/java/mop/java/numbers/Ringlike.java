package mop.java.numbers;

import mop.java.Exceptions;

/** arithmetic operations.
 * <br>
 * 'Ringlike' because many number-like objects will define some
 * subset of these operations, but they won't obey the required
 * properties (eq associativity). And it's expected to throw
 * unsupported operation exceptions where convenient.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-17
 */

@SuppressWarnings("unchecked")
public interface Ringlike<T extends Ringlike>
extends Comparable<T> {

  //--------------------------------------------------------------
  // TODO: fma?
  //--------------------------------------------------------------

  default T add (final T x) {
    throw Exceptions.unsupportedOperation(this,"add",x); }

  default T subtract (final T x) {
    throw Exceptions.unsupportedOperation(this,"subtract",x); }

  default T negate () {
    throw Exceptions.unsupportedOperation(this,"negate"); }

  default T zero () {
    throw Exceptions.unsupportedOperation(this,"zero"); }

  default boolean isZero () {
    throw Exceptions.unsupportedOperation(this,"isZero"); }

  default T abs () {
    throw Exceptions.unsupportedOperation(this,"abs"); }

  default T absDiff (final T x) {
    return (T) subtract(x).abs(); }

  //--------------------------------------------------------------

  default T square () {
    throw Exceptions.unsupportedOperation(this,"square"); }

  default T multiply (final T x) {
    throw Exceptions.unsupportedOperation(this,"multiply",x); }

  default T divide (final T x) {
    throw Exceptions.unsupportedOperation(this,"divide",x); }

  default T invert () {
    throw Exceptions.unsupportedOperation(this,"invert"); }

  default T one () {
    throw Exceptions.unsupportedOperation(this,"one"); }

  default boolean isOne() {
    throw Exceptions.unsupportedOperation(this,"isOne"); }

  default Object[] divideAndRemainder (final T x) {
    final T d = divide(x);
    final T r = subtract((T) x.multiply(d));
    return new Object[] { d, r, }; }

  default T remainder (final T x) {
    final T d = divide(x);
    return subtract((T) x.multiply(d)); }

  default T gcd (final T x) {
    throw Exceptions.unsupportedOperation(this,"gcd",x); }

  /** Return a list of <code>this/f,u/f</code>
   * where <code>f</code> is the {@link #gcd} of this and
   * <code>u</code>>
   */
  default T[] reduce (final T u) {
    throw Exceptions.unsupportedOperation(this,"reduce",u); }

  //--------------------------------------------------------------
  // 'Number' interface
  //--------------------------------------------------------------

  default int intValue () {
    throw Exceptions.unsupportedOperation(this,"intValue"); }

  default long longValue () {
    throw Exceptions.unsupportedOperation(this,"longValue"); }

  default float floatValue () {
    return (float) doubleValue(); }

  default double doubleValue () {
    throw Exceptions.unsupportedOperation(this,"doubleValue"); }

  //--------------------------------------------------------------

  default T min (final T that) {
    return (compareTo(that) < 0 ? (T) this : that); }

  default T max (final T that) {
    return (compareTo(that) > 0 ? (T) this : that); }

  //--------------------------------------------------------------

  default int compareTo (final T that) {
    throw Exceptions.unsupportedOperation(
      this,"compareTo",that); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------

