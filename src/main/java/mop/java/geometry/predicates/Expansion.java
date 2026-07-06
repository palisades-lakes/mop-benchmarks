package mop.java.geometry.predicates;
// 2026-05-14
// macro expand predicates.c via https://godbolt.org/
// minimal changes to compile as java
// 2026-05-15
// split into algorithm type classes

/**
 * Adaptive precision floating point based on:
 * <ul>
 * <li><a href="https://www.cs.cmu.edu/~quake/robust.html">
 * Jonathan Shewchuk, website:
 * Adaptive Precision Floating-Point Arithmetic and Fast Robust
 * mop.java.numbers.predicates.Predicates for Computational Geometry
 * </a></li>
 * <li>
 * <a href="https://www.cs.cmu.edu/afs/cs/project/quake/public/code/predicates.c">
 * Jonathan Shewchuk, predicates.c
 * </a></li>
 * <li>
 * <a href="https://github.com/libigl/libigl-predicates/blob/master/predicates.c">
 * libigl-predicates github
 * </a></li>
 * <li><a href="https://link.springer.com/article/10.1007/PL00009321">
 * Jonathan Shewchuk, 1997,
 * Adaptive Precision Floating-Point Arithmetic and Fast Robust
 * mop.java.numbers.predicates.Predicates for Computational Geometry
 * (53 pages, published)
 * </a></li>
 * <li>
 * <a href="https://people.eecs.berkeley.edu/~jrs/papers/robustr.pdf">
 * Jonathan Shewchuk, 1997,
 * Adaptive Precision Floating-Point Arithmetic and Fast Robust
 * mop.java.numbers.predicates.Predicates for Computational Geometry
 * (59 pages, tech report)
 * </a></li>
 * <li>
 * <a href="https://people.eecs.berkeley.edu/~jrs/papers/robust-predicates.pdf">
 * Jonathan Shewchuk, 1996,
 * Robust adaptive floating-point geometric predicates,
 * </a>
 * </li>
 * <li><a href="https://dl.acm.org/doi/10.1145/237218.237337">
 * Jonathan Shewchuk, 1996,
 * Robust adaptive floating-point geometric predicates,
 * SCG '96: Proceedings of the twelfth annual symposium on
 * Computational geometry,
 * (10 pages)
 * </a?</li>
 * *</ul>
 * <p>
 * Data Structures:
 * <a href="https://github.com/carrotsearch/hppc">hppc</a>
 * </p>
 * <p>
 *   This version's priority is correctness, and simplicity.
 *   Later versions can optimize guided by benchmarks and
 *   profiling.
 * </p>
 * <p>
 *   Basic idea: a finite subset of the rationals is represented
 *   by an implied
 *   sum of <i>non-overlapping</i> <code>double</code> terms.
 *   This set has the same range as the set of <code>double</code>s,
 *   with finer precision.
 *   Finite cardinality because <code>double</code> is finite
 *   and the number of terms is limited by the maximum array length.
 *   <br>
 *   TODO: work out the precision: equivalent number of bits
 *   <br>
 *   TODO: what is the maximum number of non-overlapping terms?
 *   <br>
 *   Possible extension: add an exponent (<code>long</code> or
 *   even <code>BigInteger</code> to extend range.
 *
 * @author palisades dot lakes at gmail dot com,
 * @version 2026-07-06
 */

// strictfp unnecessary for JDK17 and later
public final class Expansion {

  //--------------------------------------------------------------------
  // require IEEE 754
  //--------------------------------------------------------------------
  //  On some machines, the exact arithmetic routines might be
  //  defeated by the
  //   use of internal extended precision floating-point registers.
  //  Sometimes this problem can be fixed by defining certain values
  //  to be
  //  volatile, thus forcing them to be stored to memory and rounded
  //  off.
  //  This isn't a great solution, though, as it slows the arithmetic
  //  down.
  //  To try this out, write "#define INEXACT volatile" below.
  //  Normally, however, INEXACT should be defined to be nothing.
  //  ("#define
  //  INEXACT".)
  //  #define INEXACT                          /* Nothing */
  /* #define INEXACT volatile */

  // Java equivalent is <code>strictfp</code> on class or methods,
  // but that has no effect after Java 17, which requires
  // all floating point calculations to have IEEE 754 semantics.
  static {
    assert (0 > "17".compareTo(System.getProperty("java.version")))
      : "Java: " + System.getProperty("java.version") +
      " not supported";
  }

  //--------------------------------------------------------------------
  // initialize some constants
  //--------------------------------------------------------------------

  public static final double EPSILON = 0x1.0p-53;
  public static final double SPLITTER = 0x1.0000002p27;

//  static {
//    double check = 1.0;
//    boolean every_other = true;
//    double half = 0.5;
//    double epsilon = 1.0;
//    double splitter = 1.0;
//    double lastcheck;
//    do {
//      lastcheck = check; epsilon *= half;
//      if (every_other) { splitter *= 2.0; }
//      every_other = !every_other;
//      check = 1.0 + epsilon;
//    } while ((check != 1.0) && (check != lastcheck));
//    splitter += 1.0;
//    EPSILON = epsilon;
//    SPLITTER = splitter;
//    System.out.println("EPSILON=" + Double.toHexString(EPSILON));
//    System.out.println("SPLITTER=" + Double.toHexString(SPLITTER));
//  }

  public static final double resulterrbound =
    (3.0 + 8.0 * EPSILON) * EPSILON;

  //--------------------------------------------------------------------
  // class methods
  //--------------------------------------------------------------------
  public static final int grow_expansion (final int elen,
                                          final double[] e,
                                          final double b,
                                          final double[] h) {
    double Q;
    double Qnew;
    int eindex;
    double enow;
    double bvirt;
    double avirt, bround, around;

    Q = b;
    for (eindex = 0; eindex < elen; eindex++) {
      enow = e[eindex];
      Qnew = (Q + enow);
      bvirt = (Qnew - Q);
      avirt = Qnew - bvirt;
      bround = enow - bvirt;
      around = Q - avirt;
      h[eindex] = around + bround;
      Q = Qnew;
    }
    h[eindex] = Q;
    return eindex + 1;
  }

  //--------------------------------------------------------------------
  public static final int grow_expansion_zeroelim (final int elen,
                                                   final double[] e,
                                                   final double b,
                                                   final double[] h) {
    double Q, hh;
    double Qnew;
    int eindex, hindex;
    double enow;
    double bvirt;
    double avirt, bround, around;

    hindex = 0;
    Q = b;
    for (eindex = 0; eindex < elen; eindex++) {
      enow = e[eindex];
      Qnew = (Q + enow);
      bvirt = (Qnew - Q);
      avirt = Qnew - bvirt;
      bround = enow - bvirt;
      around = Q - avirt;
      hh = around + bround;
      Q = Qnew;
      if (hh != 0.0) {
        h[hindex++] = hh;
      }
    }
    if ((Q != 0.0) || (hindex == 0)) {
      h[hindex++] = Q;
    }
    return hindex;
  }

  //--------------------------------------------------------------------
  public static final int expansion_sum (final int elen,
                                         final double[] e,
                                         final int flen,
                                         final double[] f,
                                         final double[] h) {
    double Q;
    double Qnew;
    int findex, hindex, hlast;
    double hnow;
    double bvirt;
    double avirt, bround, around;

    Q = f[0];
    for (hindex = 0; hindex < elen; hindex++) {
      hnow = e[hindex];
      Qnew = (Q + hnow); bvirt = (Qnew - Q);
      avirt = Qnew - bvirt; bround = hnow - bvirt; around = Q - avirt;
      h[hindex] = around + bround;
      Q = Qnew;
    }
    h[hindex] = Q;
    hlast = hindex;
    for (findex = 1; findex < flen; findex++) {
      Q = f[findex];
      for (hindex = findex; hindex <= hlast; hindex++) {
        hnow = h[hindex];
        Qnew = (Q + hnow); bvirt = (Qnew - Q);
        avirt = Qnew - bvirt; bround = hnow - bvirt; around = Q - avirt;
        h[hindex] = around + bround;
        Q = Qnew;
      }
      h[++hlast] = Q;
    }
    return hlast + 1;
  }

  //--------------------------------------------------------------------
  public static final int expansion_sum_zeroelim1 (final int elen,
                                                   final double[] e,
                                                   final int flen,
                                                   final double[] f,
                                                   final double[] h) {
    double Q;
    double Qnew;
    int index, findex, hindex, hlast;
    double hnow;
    double bvirt;
    double avirt, bround, around;

    Q = f[0];
    for (hindex = 0; hindex < elen; hindex++) {
      hnow = e[hindex];
      Qnew = (Q + hnow); bvirt = (Qnew - Q);
      avirt = Qnew - bvirt; bround = hnow - bvirt; around = Q - avirt;
      h[hindex] = around + bround;
      Q = Qnew;
    }
    h[hindex] = Q;
    hlast = hindex;
    for (findex = 1; findex < flen; findex++) {
      Q = f[findex];
      for (hindex = findex; hindex <= hlast; hindex++) {
        hnow = h[hindex];
        Qnew = (Q + hnow); bvirt = (Qnew - Q);
        avirt = Qnew - bvirt; bround = hnow - bvirt; around = Q - avirt;
        h[hindex] = around + bround;
        Q = Qnew;
      }
      h[++hlast] = Q;
    }
    hindex = -1;
    for (index = 0; index <= hlast; index++) {
      hnow = h[index];
      if (hnow != 0.0) {
        h[++hindex] = hnow;
      }
    }
    if (hindex == -1) {
      return 1;
    }
    else {
      return hindex + 1;
    }
  }

  //--------------------------------------------------------------------
  public static final int expansion_sum_zeroelim2 (final int elen,
                                                   final double[] e,
                                                   final int flen,
                                                   final double[] f,
                                                   final double[] h) {
    double Q, hh;
    double Qnew;
    int eindex, findex, hindex, hlast;
    double enow;
    double bvirt;
    double avirt, bround, around;

    hindex = 0;
    Q = f[0];
    for (eindex = 0; eindex < elen; eindex++) {
      enow = e[eindex];
      Qnew = (Q + enow); bvirt = (Qnew - Q);
      avirt = Qnew - bvirt; bround = enow - bvirt; around = Q - avirt;
      hh = around + bround;
      Q = Qnew;
      if (hh != 0.0) {
        h[hindex++] = hh;
      }
    }
    h[hindex] = Q;
    hlast = hindex;
    for (findex = 1; findex < flen; findex++) {
      hindex = 0;
      Q = f[findex];
      for (eindex = 0; eindex <= hlast; eindex++) {
        enow = h[eindex];
        Qnew = (Q + enow); bvirt = (Qnew - Q);
        avirt = Qnew - bvirt; bround = enow - bvirt; around = Q - avirt;
        hh = around + bround;
        Q = Qnew;
        if (hh != 0) {
          h[hindex++] = hh;
        }
      }
      h[hindex] = Q;
      hlast = hindex;
    }
    return hlast + 1;
  }

  //--------------------------------------------------------------------
  public static final int fast_expansion_sum (final int elen,
                                              final double[] e,
                                              final int flen,
                                              final double[] f,
                                              final double[] h) {
    double Q;
    double Qnew;
    double bvirt;
    double avirt, bround, around;
    int eindex, findex, hindex;
    double enow, fnow;

    enow = e[0];
    fnow = f[0];
    eindex = findex = 0;
    if ((fnow > enow) == (fnow > -enow)) {
      Q = enow;
      enow = e[++eindex];
    }
    else {
      Q = fnow;
      fnow = f[++findex];
    }
    hindex = 0;
    if ((eindex < elen) && (findex < flen)) {
      if ((fnow > enow) == (fnow > -enow)) {
        Qnew = (enow + Q); bvirt = Qnew - enow;
        h[0] = Q - bvirt;
        enow = e[++eindex];
      }
      else {
        Qnew = (fnow + Q); bvirt = Qnew - fnow;
        h[0] = Q - bvirt;
        fnow = f[++findex];
      }
      Q = Qnew;
      hindex = 1;
      while ((eindex < elen) && (findex < flen)) {
        if ((fnow > enow) == (fnow > -enow)) {
          Qnew = (Q + enow); bvirt = (Qnew - Q);
          avirt = Qnew - bvirt; bround = enow - bvirt;
          around = Q - avirt; h[hindex] = around + bround;
          enow = e[++eindex];
        }
        else {
          Qnew = (Q + fnow); bvirt = (Qnew - Q);
          avirt = Qnew - bvirt; bround = fnow - bvirt;
          around = Q - avirt; h[hindex] = around + bround;
          fnow = f[++findex];
        }
        Q = Qnew;
        hindex++;
      }
    }
    while (eindex < elen) {
      Qnew = (Q + enow); bvirt = (Qnew - Q);
      avirt = Qnew - bvirt; bround = enow - bvirt; around = Q - avirt;
      h[hindex] = around + bround;
      enow = e[++eindex];
      Q = Qnew;
      hindex++;
    }
    while (findex < flen) {
      Qnew = (Q + fnow); bvirt = (Qnew - Q);
      avirt = Qnew - bvirt; bround = fnow - bvirt; around = Q - avirt;
      h[hindex] = around + bround;
      fnow = f[++findex];
      Q = Qnew;
      hindex++;
    }
    h[hindex] = Q;
    return hindex + 1;
  }

  //--------------------------------------------------------------------
  public static final int fast_expansion_sum_zeroelim (final int elen,
                                                       final double[] e,
                                                       final int flen,
                                                       final double[] f,
                                                       final double[] h) {
    assert elen <= e.length;
    assert flen <= f.length;
    double Q;
    double Qnew;
    double hh;
    double bvirt;
    double avirt, bround, around;
    int eindex, findex, hindex;
    double enow, fnow;

    enow = e[0];
    fnow = f[0];
    eindex = findex = 0;
    if ((fnow > enow) == (fnow > -enow)) {
      Q = enow;
      enow = e[++eindex];
    } else {
      Q = fnow;
      fnow = f[++findex];
    }
    hindex = 0;
    if ((eindex < elen) && (findex < flen)) {
      if ((fnow > enow) == (fnow > -enow)) {
        Qnew = (enow + Q); bvirt = Qnew - enow; hh = Q - bvirt;
        enow = e[++eindex];
      } else {
        Qnew = (fnow + Q); bvirt = Qnew - fnow; hh = Q - bvirt;
        fnow = f[++findex];
      }
      Q = Qnew;
      if (hh != 0.0) {
        h[hindex++] = hh;
      }
      while ((eindex < elen) && (findex < flen)) {
        if ((fnow > enow) == (fnow > -enow)) {
          Qnew = (Q + enow); bvirt = (Qnew - Q); avirt = Qnew - bvirt; bround = enow - bvirt; around = Q - avirt; hh = around + bround;
          eindex++;
          enow = (eindex < e.length) ? e[eindex] : 0.0;
        } else {
          Qnew = (Q + fnow); bvirt = (Qnew - Q); avirt = Qnew - bvirt; bround = fnow - bvirt; around = Q - avirt; hh = around + bround;
          findex++;
          fnow = (findex < f.length) ? f[findex]: 0.0;
        }
        Q = Qnew;
        if (hh != 0.0) {
          h[hindex++] = hh;
        }
      }
    }
    while (eindex < elen) {
      Qnew = (Q + enow); bvirt = (Qnew - Q); avirt = Qnew - bvirt; bround = enow - bvirt; around = Q - avirt; hh = around + bround;
      eindex++;
      enow = (eindex < e.length) ? e[eindex] : 0.0;
      Q = Qnew;
      if (hh != 0.0) {
        h[hindex++] = hh;
      }
    }
    while (findex < flen) {
      Qnew = (Q + fnow); bvirt = (Qnew - Q); avirt = Qnew - bvirt; bround = fnow - bvirt; around = Q - avirt; hh = around + bround;
      findex++;
      fnow = (findex<f.length) ? f[findex] : 0.0;
      Q = Qnew;
      if (hh != 0.0) {
        h[hindex++] = hh;
      }
    }
    if ((Q != 0.0) || (hindex == 0)) {
      h[hindex++] = Q;
    }
    return hindex; }

  //--------------------------------------------------------------------
  public static final int linear_expansion_sum (final int elen,
                                                final double[] e,
                                                final int flen,
                                                final double[] f,
                                                final double[] h) {
    double Q, q;
    double Qnew;
    double R;
    double bvirt;
    double avirt, bround, around;
    int eindex, findex, hindex;
    double enow, fnow;
    double g0;

    enow = e[0];
    fnow = f[0];
    eindex = findex = 0;
    if ((fnow > enow) == (fnow > -enow)) {
      g0 = enow;
      enow = e[++eindex];
    }
    else {
      g0 = fnow;
      fnow = f[++findex];
    }
    if ((eindex < elen) && ((findex >= flen)
      || ((fnow > enow) == (fnow > -enow)))) {
      Qnew = (enow + g0); bvirt = Qnew - enow; q = g0 - bvirt;
      enow = e[++eindex];
    }
    else {
      Qnew = (fnow + g0); bvirt = Qnew - fnow; q = g0 - bvirt;
      fnow = f[++findex];
    }
    Q = Qnew;
    for (hindex = 0; hindex < elen + flen - 2; hindex++) {
      if ((eindex < elen) && ((findex >= flen)
        || ((fnow > enow) == (fnow > -enow)))) {
        R = (enow + q); bvirt = R - enow;
        h[hindex] = q - bvirt;
        enow = e[++eindex];
      }
      else {
        R = (fnow + q); bvirt = R - fnow;
        h[hindex] = q - bvirt;
        fnow = f[++findex];
      }
      Qnew = (Q + R); bvirt = (Qnew - Q);
      avirt = Qnew - bvirt; bround = R - bvirt; around = Q - avirt;
      q = around + bround;
      Q = Qnew;
    }
    h[hindex] = q;
    h[hindex + 1] = Q;
    return hindex + 2;
  }

  //--------------------------------------------------------------------
  public static final int linear_expansion_sum_zeroelim (
    final int elen,
    final double[] e,
    final int flen,
    final double[] f,
    final double[] h) {
    double Q, q, hh;
    double Qnew;
    double R;
    double bvirt;
    double avirt, bround, around;
    int eindex, findex, hindex;
    int count;
    double enow, fnow;
    double g0;

    enow = e[0];
    fnow = f[0];
    eindex = findex = 0;
    hindex = 0;
    if ((fnow > enow) == (fnow > -enow)) {
      g0 = enow;
      enow = e[++eindex];
    }
    else {
      g0 = fnow;
      fnow = f[++findex];
    }
    if ((eindex < elen) && ((findex >= flen)
      || ((fnow > enow) == (fnow > -enow)))) {
      Qnew = (enow + g0); bvirt = Qnew - enow; q = g0 - bvirt;
      enow = e[++eindex];
    }
    else {
      Qnew = (fnow + g0); bvirt = Qnew - fnow; q = g0 - bvirt;
      fnow = f[++findex];
    }
    Q = Qnew;
    for (count = 2; count < elen + flen; count++) {
      if ((eindex < elen) && ((findex >= flen)
        || ((fnow > enow) == (fnow > -enow)))) {
        R = (enow + q); bvirt = R - enow; hh = q - bvirt;
        enow = e[++eindex];
      }
      else {
        R = (fnow + q); bvirt = R - fnow; hh = q - bvirt;
        fnow = f[++findex];
      }
      Qnew = (Q + R); bvirt = (Qnew - Q);
      avirt = Qnew - bvirt; bround = R - bvirt; around = Q - avirt;
      q = around + bround;
      Q = Qnew;
      if (hh != 0) {
        h[hindex++] = hh;
      }
    }
    if (q != 0) {
      h[hindex++] = q;
    }
    if ((Q != 0.0) || (hindex == 0)) {
      h[hindex++] = Q;
    }
    return hindex;
  }

  //--------------------------------------------------------------------
  public static final int scale_expansion (final int elen,
                                           final double[] e,
                                           final double b,
                                           final double[] h) {
    double Q;
    double sum;
    double product1;
    double product0;
    int eindex, hindex;
    double enow;
    double bvirt;
    double avirt, bround, around;
    double c;
    double abig;
    double ahi, alo, bhi, blo;
    double err1, err2, err3;

    c = (SPLITTER * b); abig = (c - b);
    bhi = c - abig; blo = b - bhi;
    Q = (e[0] * b); c = (SPLITTER * e[0]);
    abig = (c - e[0]); ahi = c - abig; alo = e[0] - ahi;
    err1 = Q - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); h[0] = (alo * blo) - err3;
    hindex = 1;
    for (eindex = 1; eindex < elen; eindex++) {
      enow = e[eindex];
      product1 = (enow * b); c = (SPLITTER * enow);
      abig = (c - enow); ahi = c - abig; alo = enow - ahi;
      err1 = product1 - (ahi * bhi); err2 = err1 - (alo * bhi);
      err3 = err2 - (ahi * blo); product0 = (alo * blo) - err3;
      sum = (Q + product0); bvirt = (sum - Q);
      avirt = sum - bvirt; bround = product0 - bvirt;
      around = Q - avirt; h[hindex] = around + bround;
      hindex++;
      Q = (product1 + sum); bvirt = (Q - product1);
      avirt = Q - bvirt; bround = sum - bvirt;
      around = product1 - avirt; h[hindex] = around + bround;
      hindex++;
    }
    h[hindex] = Q;
    return elen + elen;
  }

  //--------------------------------------------------------------------
  public static final int scale_expansion_zeroelim (final int elen,
                                                    final double[] e,
                                                    final double b,
                                                    final double[] h) {
    double Q, sum;
    double hh;
    double product1;
    double product0;
    int eindex, hindex;
    double enow;
    double bvirt;
    double avirt, bround, around;
    double c;
    double abig;
    double ahi, alo, bhi, blo;
    double err1, err2, err3;

    c = (SPLITTER * b); abig = (c - b);
    bhi = c - abig; blo = b - bhi;
    Q = (e[0] * b); c = (SPLITTER * e[0]);
    abig = (c - e[0]); ahi = c - abig; alo = e[0] - ahi;
    err1 = Q - (ahi * bhi); err2 = err1 - (alo * bhi);
    err3 = err2 - (ahi * blo); hh = (alo * blo) - err3;
    hindex = 0;
    if (hh != 0) { h[hindex++] = hh; }
    for (eindex = 1; eindex < elen; eindex++) {
      enow = e[eindex];
      product1 = (enow * b); c = (SPLITTER * enow);
      abig = (c - enow); ahi = c - abig; alo = enow - ahi;
      err1 = product1 - (ahi * bhi); err2 = err1 - (alo * bhi);
      err3 = err2 - (ahi * blo); product0 = (alo * blo) - err3;
      sum = (Q + product0); bvirt = (sum - Q);
      avirt = sum - bvirt; bround = product0 - bvirt;
      around = Q - avirt; hh = around + bround;
      if (hh != 0) { h[hindex++] = hh; }
      Q = (product1 + sum); bvirt = Q - product1;
      hh = sum - bvirt;
      if (hh != 0) { h[hindex++] = hh; } }
    if ((Q != 0.0) || (hindex == 0)) { h[hindex++] = Q; }
    return hindex;
  }

  //--------------------------------------------------------------------
  public static final int compress (final int elen,
                                    final double[] e,
                                    final double[] h) {
    double Q, q;
    double Qnew;
    int eindex, hindex;
    double bvirt;
    double enow, hnow;
    int top, bottom;

    bottom = elen - 1;
    Q = e[bottom];
    for (eindex = elen - 2; eindex >= 0; eindex--) {
      enow = e[eindex];
      Qnew = (Q + enow); bvirt = Qnew - Q; q = enow - bvirt;
      if (q != 0) {
        h[bottom--] = Qnew;
        Q = q;
      }
      else {
        Q = Qnew;
      }
    }
    top = 0;
    for (hindex = bottom + 1; hindex < elen; hindex++) {
      hnow = h[hindex];
      Qnew = (hnow + Q); bvirt = Qnew - hnow; q = Q - bvirt;
      if (q != 0) {
        h[top++] = q;
      }
      Q = Qnew;
    }
    h[top] = Q;
    return top + 1;
  }

  //--------------------------------------------------------------------

  public static final double estimate (final int elen,
                                       final double[] e) {
    double Q;
    int eindex;

    Q = e[0];
    for (eindex = 1; eindex < elen; eindex++) {
      Q += e[eindex];
    }
    return Q;
  }

  //--------------------------------------------------------------------
  // disable construction
  //--------------------------------------------------------------------

  private Expansion () {
    throw new UnsupportedOperationException();
  }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
