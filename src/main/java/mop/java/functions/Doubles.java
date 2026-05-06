package mop.java.functions;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.abs;
import static java.lang.Math.fma;
import static java.lang.Math.ulp;

/** BLAS-like operations on instances of <code>double[]</code>.
 *
 * Static methods only; no state.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2018-10-06
 */

public final class Doubles {

  //--------------------------------------------------------------
  // scalar ops
  //--------------------------------------------------------------
  /** Are the args within <code>ulps</code> ulps of each other?
   */
  public static final boolean approximatelyLess (final double ulps,
                                                 final double z0,
                                                 final double z1) {
    if (z0 < z1) { return true; }
    final double ulp = ulp(1.0 + abs(z0) + abs(z1));
    final double delta = abs(ulps) * ulp;
    final boolean result = (z0 <= (z1 + delta));
    if (! result) {
      System.out.println(
        z0 + "-" + z1 + " = " + (z0-z1) + " > " + delta
        + "->" + result + " : " + ((z0-z1)/ulp)); }
    return result; }

  //--------------------------------------------------------------
  /** Are the args within <code>ulps</code> ulps of each other?
   */
  public static final boolean approximatelyEqual (final double ulps,
                                                  final double z0,
                                                  final double z1) {
    if (z0 == z1) { return true; }
    final double delta =
      ulps * ulp(1.0 + abs(z0) + abs(z1));
    final boolean result = abs(z0-z1) <= delta;
    if (! result) {
      System.out.println(delta + " >= " + z0 + "-" + z1); }
    return abs(z0-z1) <= delta; }

  //--------------------------------------------------------------
  // 'vector' or 'list'  ops
  //--------------------------------------------------------------

  public static final double min (final double[] x) {
    final int n = x.length;
    double xmin = POSITIVE_INFINITY;
    for (int i=0;i<n;i++) {
      // NaN elements will be ignored.
      if (x[i] < xmin) { xmin=x[i]; } }
    return xmin; }

  //--------------------------------------------------------------

  public static final double min (final double[][] x) {
    double xmin = POSITIVE_INFINITY;
    for (final double[] xi : x) {
      final double ximin = min(xi);
      if (ximin < xmin) { xmin=ximin; } }
    return xmin; }

  //--------------------------------------------------------------

  public static final double max (final double[] x) {
    final int n = x.length;
    double xmax = NEGATIVE_INFINITY;
    for (int i=0;i<n;i++) {
      // NaN elements will be ignored.
      if (x[i] > xmax) { xmax=x[i]; } }
    return xmax; }

  //--------------------------------------------------------------

  public static final double max (final double[][] x) {
    double xmax = NEGATIVE_INFINITY;
    for (final double[] xi : x) {
      final double ximax = max(xi);
      if (ximax > xmax) { xmax=ximax; } }
    return xmax; }

  //--------------------------------------------------------------

  public static final boolean strictlyIncreasing (final double[] x) {
    final int n = x.length;
    for (int i=1;i<n;i++) {
      if (x[i-1] >= x[i]) { return false; } }
    return true; }

  //--------------------------------------------------------------
  /** Are corresponding elements within <code>ulps</code> ulps
   * of each other?
   * TODO: accept 2 null args?
   */
  public static final boolean approximatelyEqual (final double ulps,
                                                  final double[] z0,
                                                  final double[] z1) {
    final int n = z0.length;
    if (n != z1.length) { return false; }
    for (int i=0;i<n;i++) {
      if (! approximatelyEqual(ulps,z0[i],z1[i])) {
        return false; } }
    return true; }

  //--------------------------------------------------------------
  /** z = a*x+b*y */

  public static final void linearCombination (final double a,
                                              final double[] x,
                                              final double b,
                                              final double[] y,
                                              final double[] z,
                                              final int n) {
    // TODO: what's best for accuracy/speed?
    for (int i=0;i<n;i++) { z[i] = fma(a,x[i],b*y[i]); } }

  //--------------------------------------------------------------
  //  /** z = a*x+b*y */
  //
  //  public static final double[]
  //    linearCombination (final double a,
  //                       final double[] x,
  //                       final double b,
  //                       final double[] y) {
  //    final int n = x.length;
  //    final double[] z = new double[n];
  //    linearCombination(a,x,b,y,z,n);
  //    return z; }

  //--------------------------------------------------------------
  //  /** z = a*x+b*y+z */
  //
  //  public static final void axbypz (final double a,
  //                                 final double[] x,
  //                                 final double b,
  //                                 final double[] y,
  //                                 final double[] z) {
  //    final int n = x.length;
  //    for (int i=0;i<n;i++) {
  //      z[i] = fma(a,x[i],fma(b,y[i],z[i])); } }

  //--------------------------------------------------------------
  /** y = a*x+y */

  public static final void axpy (final double a,
                                 final double[] x,
                                 final double[] y,
                                 final int n) {
    for (int i=0;i<n;i++) { y[i] = fma(a,x[i],y[i]); } }

  /** y = a*x+y */

  public static final void axpy (final double a,
                                 final double[] x,
                                 final double[] y) {
    axpy(a,x,y,x.length); }

  /** z = a*x+y */
  public static final void axpy (final double a,
                                 final double[] x,
                                 final double[] y,
                                 final double[] z) {
    final int n = z.length;
    for (int i=0;i<n;i++) { z[i] = fma(a,x[i],y[i]); } }

  /** return z = a*x+y */
  public static final double[] safeAxpy (final double a,
                                         final double[] x,
                                         final double[] y) {
    final int n = x.length;
    final double[] z = new double[n];
    for (int i=0;i<n;i++) { z[i] = fma(a,x[i],y[i]); }
    return z; }

  //--------------------------------------------------------------
  /** x = ax-y NOTE: difference from  axpy*/

  //  public static final void axmy (final double a,
  //                                 final double[] x,
  //                                 final double[] y) {
  //    final int n = x.length;
  //    for (int i=0;i<n;i++) { x[i] = fma(a,x[i],-y[i]); } }

  //  /** return z = ax-y NOTE: difference from  axpy*/
  //
  //  public static final double[] safeAxmy (final double a,
  //                                         final double[] x,
  //                                         final double[] y) {
  //    final int n = x.length;
  //    final double[] z = new double[n];
  //    for (int i=0;i<n;i++) { z[i] = fma(a,x[i],-y[i]); }
  //    return z; }

  //--------------------------------------------------------------
  /** Overwrite </code>dst</code>.
   */

  public static final void negate (final double[] src,
                                   final double[] dst,
                                   final int n) {
    //assert n <= dst.length;
    //assert n <= src.length;
    for (int i=0;i<n;i++) { dst[i] = -src[i]; } }

  /** Overwrite </code>dst</code>.
   */

  public static final void negate (final double[] src,
                                   final double[] dst) {
    final int n = src.length;
    //assert n == dst.length;
    for (int i=0;i<n;i++) { dst[i] = -src[i]; } }

  //  /** Overwrite </code>Z</code>.
  //   */
  //
  //  public static final void negate1 (final double[] z) {
  //    final int n = z.length;
  //    for (int i=0;i<n;i++) { z[i] = -z[i]; } }

  //  /** Return </code>-x</code>.
  //   */
  //
  //  public static final double[] negate (final double[] x) {
  //    final int n = x.length;
  //    final double[] y = new double[n];
  //    negate(x,y);
  //    return y; }

  //--------------------------------------------------------------
  /** <b>Destructive(!!!)</b> <code>z[i] = a*z[i]</code>.
   */

  public static final void scale1 (final double a,
                                   final double[] z,
                                   final int n) {
    for (int i=0;i<n;i++) { z[i] = a*z[i]; } }

  /** <b>Destructive(!!!)</b> <code>z[i] = a*z[i]</code>.
   */

  public static final void scale1 (final double a,
                                   final double[] z) {
    scale1(a,z,z.length); }


  /** Return new array <code>az[i] = a*z[i]</code>.
   */

  public static final double[] scale (final double a,
                                      final double[] z) {
    final int n = z.length;
    final double[] az = new double[n];
    for (int i=0;i<n;i++) { az[i] = a*z[i]; }
    return az; }

  public static final void scale (final double a,
                                  final double[] src,
                                  final double[] dst,
                                  final int n) {
    //assert n <= dst.length;
    //assert n <= src.length;
    for (int i=0;i<n;i++) { dst[i] = a*src[i]; } }

  public static final void scale (final double a,
                                  final double[] src,
                                  final double[] dst) {
    scale(a,src,dst,dst.length); }

  public static final void scale (final double a,
                                  final double[] src,
                                  final int sStart,
                                  final double[] dst,
                                  final int dStart,
                                  final int n) {
    //assert n <= dst.length;
    //assert n <= src.length;
    for (int i=0;i<n;i++) { dst[i+dStart] = a*src[i+sStart]; } }

  //--------------------------------------------------------------
  //  /** Destructive!  ok if z is x and/or y.*/
  //
  //  public static final void subtract (final double[] x,
  //                                     final double[] y,
  //                                     final double[] z) {
  //    final int n = z.length;
  //    for (int i=0;i<n;i++) { z[i] = x[i]-y[i]; } }
  //
  //--------------------------------------------------------------
  //  /** return x-y */
  //
  //  public static final double[] subtract (final double[] x,
  //                                         final double[] y) {
  //    final int n = x.length;
  //    final double[] z = new double[n];
  //    for (int i=0;i<n;i++) { z[i] = x[i]-y[i]; }
  //    return z; }

  //--------------------------------------------------------------
  /** z = x-y, with an offset into z.
   */
  public static final void subtract (final double[] x,
                                     final double[] y,
                                     final double[] z,
                                     final int n) {
    for (int i=0;i<n;i++) { z[i] = y[i]-x[i]; } }

  //--------------------------------------------------------------
  /** z = x-y, with an offset into z, and a bound.
   */
  public static final void subtract (final double[] x,
                                     final double[] y,
                                     final double[] z,
                                     final int zStart,
                                     final int n) {
    //assert n <= x.length;
    //assert n <= y.length;
    //assert n <= (z.length-zStart);
    //assert (n+zStart) <= z.length;
    for (int i=0;i<n;i++) { z[zStart+i] = y[i]-x[i]; } }

  //--------------------------------------------------------------

  //  public static final double dot (final double[] x,
  //                                  final double[] y) {
  //    final int n = x.length;
  //    double s = 0.0;
  //    final int n5 = (n%5);
  //    int i=0;
  //    while (i<n5) { s = fma(x[i],y[i++],s); }
  //    while (i<n) {
  //      final int i1 = i+1;
  //      final double z1 = fma(x[i1],y[i1],x[i]*y[i]);
  //      final int i2 = i+2;
  //      final int i3 = i+3;
  //      final double z3 = fma(x[i3],y[i3],x[i2]*y[i2]);
  //      final int i4 = i+4;
  //      s += fma(x[i4],y[i4],z1+z3);
  //      i += 5; }
  //    return s; }

  public static final double dot (final double[] x,
                                  final double[] y,
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
      i += 5; }
    return s; }

  //--------------------------------------------------------------

  //  public static final double l2norm2 (final double[] x) {
  //    final int n = x.length;
  //    final int n5 = n % 5;
  //    double s = 0.0;
  //    int i=0;
  //    while (i<n5) { final double xi = x[i++]; s = fma(xi,xi,s); }
  //    while (i<n) {
  //      final double x0 = x[i];
  //      final double x1 = x[i+1];
  //      final double s1 = fma(x1,x1,x0*x0);
  //      final double x2 = x[i+2];
  //      final double x3 = x[i+3];
  //      final double s3 = fma(x3,x3,x2*x2);
  //      final double x4 = x[i+4];
  //      s += fma(x4,x4,s3+s1);
  //      i += 5; }
  //    return s; }

  public static final double l2norm2 (final double[] x,
                                      final int n) {
    final int n5 = n % 5;
    double s = 0.0;
    int i=0;
    while (i<n5) { final double xi = x[i++]; s = fma(xi,xi,s); }
    while (i<n) {
      final double x0 = x[i];
      final double x1 = x[i+1];
      final double s1 = fma(x1,x1,x0*x0);
      final double x2 = x[i+2];
      final double x3 = x[i+3];
      final double s3 = fma(x3,x3,x2*x2);
      final double x4 = x[i+4];
      s += fma(x4,x4,s3+s1);
      i += 5; }
    return s; }

  //--------------------------------------------------------------
  //  /** maximum absolute value over the elements.
  //   */
  //
  //  public static final double maxabs (final double[] x) {
  //    final int n = x.length;
  //    double xmax = abs(x[0]);
  //    for (int i=1;i<n; i++) {
  //      final double axi = abs(x[i]);
  //      if (xmax < axi) { xmax = axi; } }
  //    return xmax; }

  //--------------------------------------------------------------

  public static final void swap (final double[] src,
                                 final double[] dst,
                                 final int pivot,
                                 final int n) {
    final int k = n-pivot;
    System.arraycopy(src,pivot,dst,0,k);
    System.arraycopy(src,0,dst,k,pivot); }

  //--------------------------------------------------------------
  /** z[k] = z[k+1]; z[n] = newZ; */

  public static final void shiftLeft (final double[] z,
                                      final int n,
                                      final double newZ) {
    for (int k=0;k<n;k++) { z[k] = z[k+1]; }
    z[n] = newZ; }

  //--------------------------------------------------------------
  //  /** <code>x1[i] = x0[i]</code> */
  //
  //  public static final void copy (final double[] x0,
  //                                 final double[] x1) {
  //    System.arraycopy(x0,0,x1,0,x0.length); }

  /** <code>x1[i] = x0[i]</code> */

  public static final void copy (final double[] x0,
                                 final double[] x1,
                                 final int n) {
    System.arraycopy(x0,0,x1,0,n); }

  //--------------------------------------------------------------

  public static final void copy (final double[][] srcCols,
                                 final double[][] dstCols,
                                 final int nrows,
                                 final int ncols) {
    for (int j=0;j<ncols;j++) {
      copy(srcCols[j],dstCols[j],nrows); } }

  //--------------------------------------------------------------
  // matrix ops
  //--------------------------------------------------------------
  /** Solve r'x = y where r is a dense upper triangular matrix
   * @param r
   * @param x right side on input, solution on output
   */
  public static final void trisolveTranspose (final double[][] r,
                                              final double[] x,
                                              final int n) {
    for (int i=0;i<n;i++) {
      x[i] = (x[i]-dot(x,r[i],i)) / r[i][i]; } }

  //--------------------------------------------------------------
  /** Solve R x = y where R is a dense upper triangular matrix
   * @param r
   * @param x right side on input, solution on output
   */
  public static final void trisolve (final double[][] r,
                                     final double[] x,
                                     final int n) {
    for (int i=n-1;i>=0;i--) {
      x[i] /= r[i][i];
      axpy(-x[i],r[i],x,i); } }

  //--------------------------------------------------------------
  /** Compute y = A*x where A is an array of column arrays
   */
  public static final void transform (final double[][] a,
                                      final double[] x,
                                      final double[] y,
                                      final int nrows,
                                      final int ncols) {
    //Doubles.scale(y,x[0],A[0],m);
    final double x0 = x[0];
    final double[] a0 = a[0];
    for (int i=0;i<nrows;i++) { y[i] = x0*a0[i]; }
    for (int j=1;j<ncols;j++) { axpy(x[j],a[j],y,nrows); } }

  //--------------------------------------------------------------
  /** Compute y = A'*x where A is an array of column arrays
   */

  public static final void transformTranspose (final double[][] a,
                                               final double[] x,
                                               final double[] y,
                                               final int nrows,
                                               final int ncols) {
    for (int j=0;j<ncols;j++) { y[j] = dot(a[j],x,nrows); } }

  //--------------------------------------------------------------
  // disable constructor
  //--------------------------------------------------------------

  private Doubles () {
    throw new UnsupportedOperationException(
      "can't instantiate " + getClass()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
