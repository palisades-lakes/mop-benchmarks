package mop.java.algebra;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.CollectionSampler;

import mop.java.Exceptions;

/** Utilities merging <code>java.util.Set</code> and
 * <code>mop.java.sets.Set</code>.
 * >br>
 * Static methods only; no state.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-08-21
 */


@SuppressWarnings("unchecked")
public final class Sets {

  /** Default notion of equivalence for most sets.
   */
  public static final BiPredicate OBJECT_EQUALS = Objects::equals;

  //--------------------------------------------------------------
  /** Does the set contain the element?
   */

  public static final boolean contains (final Object set,
                                        final Object element) {
    if (set instanceof Set) {
      return ((Set) set).contains(element); }
    if (set instanceof java.util.Set) {
      return ((java.util.Set) set).contains(element); }
    throw Exceptions.unsupportedOperation(
      null,"contains",set,element); }

  //--------------------------------------------------------------

  public static final Supplier generator (final Object set,
                                          final Map options) {
    if (set instanceof Set) {
      return ((Set) set).generator(options); }

    if (set instanceof java.util.Set) {
      final UniformRandomProvider urp = Set.urp(options);
      assert null != urp;
      @SuppressWarnings("unchecked")
      final CollectionSampler cs =
        new CollectionSampler(urp,((java.util.Set) set));
      return cs::sample; }

    throw Exceptions.unsupportedOperation(
      null,"contains",set,options); }

  //--------------------------------------------------------------
  // predicates on equivalence relation
  //--------------------------------------------------------------
  /** Is a = a?
   */
  public final static boolean isReflexive (final Set elements,
                                           final BiPredicate equivalent,
                                           final Supplier generator) {
    final Object a = generator.get();
    assert elements.contains(a);
    return equivalent.test(a,a); }

  /** Is a = a?
   */
  public final static boolean isReflexive (final Set elements,
                                           final Supplier generator) {
    return isReflexive(elements,elements.equivalence(),generator); }

  //--------------------------------------------------------------
  /** Is a = a?
   */
  public final static boolean isSymmetric (final Set elements,
                                           final BiPredicate equivalent,
                                           final Supplier generator) {
    final Object a = generator.get();
    assert elements.contains(a);
    final Object b = generator.get();
    assert elements.contains(b);
    final boolean ab = equivalent.test(a,b);
    final boolean ba = equivalent.test(b,a);
//    assert ab==ba :
//      "\nset=" + Classes.className(elements) + " " + elements
//      + "\nequivalent=" + Classes.className(equivalent) + " " + equivalent
//      + "\na=" + Classes.className(a) + " " + a
//      + "\nb=" + Classes.className(b) + " " + b
//      + "\na==b -> " + ab
//      + "\nb==a -> " + ba;
    return ab == ba; }

  /** Is a = a?
   */
  public final static boolean isSymmetric (final Set elements,
                                           final Supplier generator) {
    return isSymmetric(elements,elements.equivalence(),generator); }

  //--------------------------------------------------------------
  // disable constructor
  //--------------------------------------------------------------

  private Sets () {
    throw new UnsupportedOperationException(
      "can't instantiate " + getClass()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
