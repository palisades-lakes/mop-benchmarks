package mop.java;

import java.io.PrintStream;

/** Debugging output.
 * Hacky substitute for mess of dependencies and
 * configuration needed by java logging libraries.
 * Intended only for use during development;
 * no Debug.* references should persist in 'production' code.
 * <br>
 * Static methods only; no state.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-24
 */

@SuppressWarnings("unused")
public final class Debug {

  public static boolean DEBUG = false;

  public static PrintStream OUT = System.out;

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

  // TODO: wrapping/truncating long arrays
  public static final String toHexString (final double[] x) {
    final StringBuilder sb = new StringBuilder("[");
    for (final double v : x) {
      sb.append(Double.toHexString(v)); sb.append(" "); }
    sb.replace(sb.length()-1, sb.length(), "]");
    return sb.toString(); }

//--------------------------------------------------------------
//  /** hex string for <code>int[]</code> interpreted as
//   * little endian unsigned words of a large int. */
//
//  public static final String toHexString (final int[] m) {
//    //final StringBuilder b = new StringBuilder("0x");
//    final StringBuilder b = new StringBuilder();
//    final int n = m.length;
//    if (0 == n) { b.append('0'); }
//    else {
//      b.append(String.format("%08x",Long.valueOf(unsigned(m[0]))));
//      for (int i=1;i<n;i++) {
//        //b.append(" ");
//        b.append(
//          String.format("%08x",Long.valueOf(unsigned(m[i])))); } }
//    return b.toString(); }

  //--------------------------------------------------------------

  public static final void println () {
    if (DEBUG) { OUT.println(); } }

  public static final void println (final String msg) {
    if (DEBUG) { OUT.println(msg); } }

  public static final void println (final boolean msg) {
    if (DEBUG) { OUT.println(msg); } }

  public static final void println (final int msg) {
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
    if (DEBUG) { OUT.printf(format, arg0, arg1); } }

  //--------------------------------------------------------------
  // disable constructor
  //--------------------------------------------------------------

  private Debug () {
    throw new UnsupportedOperationException(
      "can't instantiate " + getClass()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
