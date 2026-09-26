package mop.java.scripts.triangles;

import mop.java.geometry.Generators;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.triangle.Triangle2D;
import mop.java.geometry.triangle.TriangleBF2;
import mop.java.geometry.triangle.TriangleD2Lazy;
import mop.java.numbers.BigFloat;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.GeneratorBase;
import mop.java.prng.PRNG;

/** <pre>
 * mvn -q install && jy src/scripts/java/mop/java/scripts/triangles/ColinearProfile.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-25
 */

public final class ColinearProfile {

  //--------------------------------------------------------------
  // TODO: make this an operation on VectorBF2, and add
  //  a vector rounding operation that returns VectorD2

  private static final VectorD2 bfAffine (final double a,
                                          final VectorD2 p0,
                                          final VectorD2 p1) {
    final BigFloat dx = BigFloat.sum(p0.getX(),-p1.getX());
    final BigFloat dy = BigFloat.sum(p0.getY(),-p1.getY());
    final BigFloat x2 = BigFloat.axpy(a,dx,p1.getX());
    final BigFloat y2 = BigFloat.axpy(a,dy,p1.getY());
    return new VectorD2(x2.doubleValue(),y2.doubleValue()); }

  private static final Generator
  colinearTriangleGenerator (final Generator vectorGenerator,
                             final Generator doubleGenerator) {
    // TODO: named local class rather than anonymous with name?
    return new GeneratorBase("colinearTriangleGenerator") {
      @Override
      public final Object next () {
        final VectorD2 p0 = (VectorD2) vectorGenerator.next();
        final VectorD2 p1 = (VectorD2) vectorGenerator.next();
        final double a = doubleGenerator.nextDouble();
        final VectorD2 p2 = bfAffine(a,p0,p1);
        return TriangleD2Lazy.of(p0, p1, p2); } }; }

//--------------------------------------------------------------

  public static final void
  trials () {

    final double pMu = 1.0;
    final double pSigma = 10.0;
    final double aMu = 0.0;
    final double aSigma = 100.0;
    final Generator colinearGenerator =
      colinearTriangleGenerator(
        Generators.vectorD2Generator(
          Doubles.laplaceGenerator(
            PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
            pMu, pSigma)),
        Doubles.laplaceGenerator(
          PRNG.well44497b("seeds/Well44497b-2019-01-09.txt"),
          aMu,aSigma));

    final int ntriangles = 1024*1024;
    final TriangleBF2[] triangles = new TriangleBF2[ntriangles];
    for (int i=0;i<ntriangles;i++) {
      triangles[i] =
        (TriangleBF2)
          TriangleBF2.from((Triangle2D) colinearGenerator.next()); }

    System.gc();
    System.gc();

    System.out.println("pMu,pSigma= " + Double.toHexString(pMu) +
                         ", " + Double.toHexString(pSigma));
    System.out.println("aMu,aSigma= " + Double.toHexString(aMu) +
                         ", " + Double.toHexString(aSigma));
    System.out.flush();

    final int ntrials = 1024;
    int nexact = -1;
    int nround = -1;
    for (int k=0;k<ntrials;k++) {
      nexact = 0;
      nround = 0;
      for (int i=0;i<ntriangles;i++) {
        final TriangleBF2 t = triangles[i];
        t.clearCaches();
        final BigFloat bf = t.getV20xV10();
        final double bfd = bf.doubleValue();
        if (t.getV20xV10().isZero()) { nexact++; }
        if (0.0 == bfd) { nround++; } } }

    System.out.println(
      " Exact colinear= " + nexact + "/" + ntriangles +
        " = " + ((double) nexact)/ntriangles);
    System.out.println(
      " Round colinear= " + nround + "/" + ntriangles +
        " = " + ((double) nround)/ntriangles); }

  //--------------------------------------------------------------------

  public static final void main (final String[] ignore) { trials(); }

  //--------------------------------------------------------------------
  // disable construction
  //--------------------------------------------------------------------

  private ColinearProfile () {
    throw new UnsupportedOperationException(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
