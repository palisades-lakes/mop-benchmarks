package mop.java.functions;

import mop.java.Exceptions;

/** A general set, intended to be used for the domain,
 * codomain, support, range, etc., of {@link Function functions}.
 * <p>
 * To start, emphasizing linear (aka vector) spaces.
 * <p>
 * <b>Design note:</b>
 * This is a loose interface, with (almost) all operations are
 * optional, like the Java Collection classes.
 * There are 2 main reasons:
 * <ul>
 * <li> Over-use of static, compile-time type constraints leads to
 * brittle, complex code that simply fails in the application
 * areas of interest here (see below).
 * Dynamic (runtime) validation and tests
 * are preferred.
 * <li> Suppose we moved <code>dimension()</code> to a more
 * specialized child interface, say, DimensionalDomain and
 * required it there. An important operation on domains is to
 * construct a new domain as the cartesian product of 2 existing
 * domains. Whether the cartesian product has a dimension depends
 * on whether the 2 component domains have dimensions. Which means
 * we need at least 2 implementation classes.
 * The same will be true of any other property of a domain that
 * might or might not be true of cartesian products, leading to a
 * combinatorial explosion of implementation classes.
 * </ul>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2018-09-07
 */
public interface Domain {

  //--------------------------------------------------------------
  /** Optional operation for domains which are
   * 'finite-dimensional' in some sense.
   * <p>
   * TODO: how to handle infinite dimensional (eg Hilbert) spaces?
   * Return -1 for countable infinity?
   */
  public default int dimension () {
    throw Exceptions.unsupportedOperation(
      this,"dimension"); }

  //--------------------------------------------------------------
  /** Is <code>x</code> an element of the Domain? */

  public default boolean contains (final Object x) {
    throw Exceptions.unsupportedOperation(this,"contains",x); }

  /** Is <code>x</code> an element of the Domain? */

  @SuppressWarnings("unused")
  public default boolean contains (final double x)  {
    throw Exceptions.unsupportedOperation(
      getClass(),"contains",Double.TYPE); }

  // TODO: other primitives...

  //--------------------------------------------------------------
  /** Return the domain whose elements are ordered pairs
   * of an element of <code>this</code> and and an element of
   * <code>that/code>. Will probably accept as elements objects
   * which are not explicit pairs, eg,
   * <b>R</b><sup>n</sup> x <b>R</b><sup>m</sup>
   * might be identified with <b>R</b><sup>n+m</sup>,
   * if convenient.
   */
  public default Domain cartesianProduct (final Domain that) {
    throw Exceptions.unsupportedOperation(
      this,"cartesianProduct",that); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------

