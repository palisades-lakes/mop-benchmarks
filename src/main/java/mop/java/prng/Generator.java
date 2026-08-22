package mop.java.prng;

import mop.java.Exceptions;

/** Generators of primitives or Objects as zero-arity 'functions'
 * that return different values on each call.
 * <p>
 * TODO: parameterize interface by return type?
 * <p>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-27
 */

//
public interface Generator {

  // default methods throw UnsupportetOperationException.

  default String name () {
    throw Exceptions.unsupportedOperation(this,"name"); }

  default Object next () {
    throw Exceptions.unsupportedOperation(this,"next"); }

  @SuppressWarnings("unused")
  default boolean nextBoolean () {
    throw Exceptions.unsupportedOperation(this,"nextBoolean"); }

  default byte nextByte () {
    throw Exceptions.unsupportedOperation(this,"nextByte"); }

  @SuppressWarnings("unused")
  default char nextChar () {
    throw Exceptions.unsupportedOperation(this,"nextChar"); }

  default short nextShort () {
    throw Exceptions.unsupportedOperation(this,"nextShort"); }

  default int nextInt () {
    throw Exceptions.unsupportedOperation(this,"nextInt"); }

  default long nextLong () {
    throw Exceptions.unsupportedOperation(this,"nextLong"); }

  default float nextFloat () {
    throw Exceptions.unsupportedOperation(this,"nextFloat"); }

  default double nextDouble () {
    throw Exceptions.unsupportedOperation(this,"nextDouble"); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------

