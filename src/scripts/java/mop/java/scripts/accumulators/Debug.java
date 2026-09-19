package mop.java.scripts.accumulators;

import java.io.PrintStream;

/** Debugging output.
 * Hacky substitute for mess of dependencies and
 * configuration needed by java logging libraries.
 * Intended only for use during development;
 * no //Debug.* references should persist in 'production' code.
 * <br>
 * Static methods only; no state.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-19
 */

//@SuppressWarnings("unused")
public final class Debug {

  public static final boolean DEBUG = false;

  public static final PrintStream OUT = System.out;

  //--------------------------------------------------------------
  // methods
  //--------------------------------------------------------------
  /** Hex string representation of the bits
   * implementing the double. Not at all the same as
   * {@link Double#toHexString}.
   */

  public static final String hexBits (final double x) {
    return
      Long.toHexString(Double.doubleToLongBits(x))
      .toUpperCase(); }

  //--------------------------------------------------------------

  public static final void println () {
    if (DEBUG) { OUT.println(); } }

  public static final void println (final String msg) {
    if (DEBUG) { OUT.println(msg); } }

  //--------------------------------------------------------------

  public static final void printf (final String format,
                                   final Object... args) {
    if (DEBUG) { OUT.printf(format,args); } }

  //--------------------------------------------------------------

  public static final void printf (final String format,
                                   final boolean arg) {
    if (DEBUG) { OUT.printf(format, arg); } }

  //--------------------------------------------------------------

  public static final void printf (final String format,
                                   final int arg) {
    if (DEBUG) { OUT.printf(format, arg); } }

  //--------------------------------------------------------------

  public static final void printf (final String format,
                                   final double arg) {
    if (DEBUG) { OUT.printf(format, arg); } }

  //--------------------------------------------------------------

  public static final void printf (final String format,
                                   final double arg0,
                                   final double arg1) {
    if (DEBUG) {
      OUT.printf(format,
                 arg0, arg1); } }

  //--------------------------------------------------------------
  // disable constructor
  //--------------------------------------------------------------

  private Debug () {
    throw new UnsupportedOperationException(
      "can't instantiate " + getClass()); }


  //--------------------------------------------------------------
}
//--------------------------------------------------------------
