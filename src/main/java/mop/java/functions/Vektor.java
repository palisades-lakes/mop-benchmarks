package mop.java.functions;

import static java.lang.Math.abs;
import static java.lang.Math.fma;

import java.util.Arrays;

/** Immutable <code>double</code> approximation to
 * <b>R</b><sup>n</sup>.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2018-09-07
 */
public final class Vektor {

  //--------------------------------------------------------------
  // fields
  //--------------------------------------------------------------

  private final double[] x;

  //  private double _l2norm2 = Double.NaN;
  //  private static int _l2Misses = 0;
  //  public static final int l2Misses () { return _l2Misses; }
  //  private static int _l2Calls = 0;
  //  public static final int l2Calls () { return _l2Calls; }
  //
  //  private double _supnorm = Double.NaN;
  //  private static int _supMisses = 0;
  //  public static final int supMisses () { return _supMisses; }
  //  private static int _supCalls = 0;
  //  public static final int supCalls () { return _supCalls; }

  //--------------------------------------------------------------
  // coordinates
  //--------------------------------------------------------------

  public final int dimension () { return x.length; }

  public final double[] unsafeCoordinates () { return x; }

  public final double coordinate (final int i) { return x[i]; }

  public final double[] coordinates () { return x.clone(); }

  //--------------------------------------------------------------
  // metrics
  //--------------------------------------------------------------
  // Note: no improvement from unrolling (5) supnorm

  public final double supnorm () {
    final int n = x.length;
    double xmax = abs(x[0]);
    for (int i=1;i<n; i++) {
      final double axi = abs(x[i]);
      if (xmax < axi) { xmax = axi; } }
    return xmax; }

  //--------------------------------------------------------------
  // Note: simple l2norm2 and dot product are not accurate enough,
  // and slow things down.

  public final double l2norm2 () {
    final int n = x.length;
    final int n5 = n % 5;
    double s = 0.0;
    int i=0;
    while (i<n5) {
      final double xi = x[i++]; s = fma(xi,xi,s); }
    while (i<n) {
      final double x0 = x[i++];
      final double x1 = x[i++];
      final double s1 = fma(x1,x1,x0*x0);
      final double x2 = x[i++];
      final double x3 = x[i++];
      final double s3 = fma(x3,x3,x2*x2);
      final double x4 = x[i++];
      s += fma(x4,x4,s3+s1); }
    return s; }

  //--------------------------------------------------------------
  // TODO: replace uses of this with shortened Vektors

  private final double dot (final double[] y,
                            final int n) {
    double s = 0.0;
    final int n5 = (n%5);
    int i=0;
    while (i<n5) { s = fma(x[i],y[i++],s); }
    while (i<n) {
      final int i1 = i+1;
      final double z1 = fma(x[i1],y[i1],x[i]*y[i]);
      final int i2 = i+2;
      final int i3 = i+3;
      final double z3 = fma(x[i3],y[i3],x[i2]*y[i2]);
      final int i4 = i+4;
      s += fma(x[i4],y[i4],z1+z3);
      i = i4 + 1; }
    return s; }

  public final double dot (final double[] y) {
    return dot(y,x.length); }

  public final double dot (final Vektor that) {
    return dot(that.x); }

  //--------------------------------------------------------------
  // linear space ops
  //--------------------------------------------------------------
  // Note: unrolling axpy, etc., make it slower.

  public final void axpy (final double a,
                          final double[] y) {
    final int n = x.length;
    for (int i=0;i<n;i++) { y[i] = fma(a,x[i],y[i]); } }

  //--------------------------------------------------------------

  public final Vektor axpy (final double a,
                            final Vektor that) {
    final double[] y = that.x;
    final int n = x.length;
    final double[] z = new double[n];
    for (int i=0;i<n;i++) { z[i] = fma(a,x[i],y[i]); }
    return unsafeMake(z); }

  //--------------------------------------------------------------
  /** z = a*this+b*that+z */

  public final void axbypz (final double a,
                            final double b,
                            final Vektor that,
                            final double[] z) {
    final double[] y = that.x;
    final int n = x.length;
    for (int i=0;i<n;i++) {
      z[i] = fma(a,x[i],fma(b,y[i],z[i])); } }

  //--------------------------------------------------------------

  public final Vektor add (final Vektor that) {
    final double[] y = that.x;
    final int n = x.length;
    final double[] z = new double[n];
    for (int i=0;i<n;i++) { z[i] = x[i]+y[i]; }
    return unsafeMake(z); }

  //--------------------------------------------------------------

  public final Vektor subtract (final Vektor that) {
    final double[] y = that.x;
    final int n = x.length;
    final double[] z = new double[n];
    for (int i=0;i<n;i++) { z[i] = x[i]-y[i]; }
    return unsafeMake(z); }

  //--------------------------------------------------------------
  /** return a*this - that*/

  public final Vektor axmy (final double a,
                            final Vektor that) {
    final double[] y = that.x;
    final int n = x.length;
    final double[] z = new double[n];
    for (int i=0;i<n;i++) { z[i] = fma(a,x[i],-y[i]); }
    return unsafeMake(z); }

  //--------------------------------------------------------------
  /** y = a*this */

  public final void scale (final double a,
                           final double[] y) {
    final int n = x.length;
    for (int i=0;i<n;i++) { y[i] = a*x[i]; } }

  //--------------------------------------------------------------

  public final Vektor negate () {
    final int n = x.length;
    final double[] z = new double[n];
    for (int i=0;i<n;i++) { z[i] = -x[i]; }
    return unsafeMake(z); }

  //--------------------------------------------------------------
  // matrix ops
  //--------------------------------------------------------------
  /** Compute y = A'*x where A is an array of column arrays
   */

  public static final void transformTranspose (final double[][] a,
                                               final Vektor x,
                                               final double[] y,
                                               final int nrows,
                                               final int ncols) {
    for (int j=0;j<ncols;j++) {
      y[j] = x.dot(a[j],nrows); } }

  //--------------------------------------------------------------
  /** y = A'*x */

  public static final void transformTranspose (final double[][] a,
                                               final Vektor x,
                                               final double[] y,
                                               final int yOff,
                                               final int nrows,
                                               final int ncols) {
    for (int j=yOff;j<(ncols+yOff); j++) {
      y[j] = x.dot(a[j],nrows); } }

  //--------------------------------------------------------------
  /** each coordinate within
   * <code>ulps * Math.ulp(1+abs(this)+abs(that))</code>
   */

  public final boolean approximatelyEqual (final double ulps,
                                           final Vektor that) {
    return Doubles.approximatelyEqual(ulps,x,that.x); }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------

  @Override
  public final String toString () {
    return
      getClass().getSimpleName() +
      "\n" +
      Arrays.toString(x); }

  @Override
  public final int hashCode () {
    return Arrays.hashCode(x); }

  @Override
  public final boolean equals (final Object that) {
    if (! (that instanceof Vektor)) { return false; }
    return Arrays.equals(
      x,
      ((Vektor) that).x); }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private Vektor (final double[] coordinates) {
    x = coordinates; }

  public final static Vektor unsafeMake (final double[] coordinates) {
    return new Vektor(coordinates); }

  public static final Vektor make (final double[] coordinates) {
    return new Vektor(coordinates.clone()); }

  // TODO: more compact representation, Vektor interface?
  /** Return a vector of dimension <code>n</code>, whose
   * coordinates are all <code>xi</code>.
   */
  public static final Vektor constantVektor (final int n,
                                             final double xi) {
    final double[] x = new double[n];
    Arrays.fill(x,xi);
    return new Vektor(x); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------

