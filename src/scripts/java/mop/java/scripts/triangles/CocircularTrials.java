package mop.java.scripts.triangles;

import mop.java.geometry.Generators;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.triangle.TriangleBF2;
import mop.java.numbers.BigFloat;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;

/** TODO: worth creating 'exact' cocircular points represented
 *    by: <code>center, radius, angle</code>?
 * <pre>
 * mvn -q install && j src/scripts/java/mop/java/scripts/triangles/CocircularTrials.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-29
 */

public final class CocircularTrials {

  //--------------------------------------------------------------


  public static final void
  cocircularTrials () {

    final double cMu = 1.0;
    final double cSigma = 1.0;
    final double rLambda = 1.0;
    final double pMu = 0.0;
    final double pSigma = 3.0;
    final Generator centerGenerator = Generators.vectorD2Generator(
      Doubles.laplaceGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
        cMu, cSigma));
    final Generator radiusGenerator = Doubles.exponentialGenerator(
      PRNG.well44497b("seeds/Well44497b-2019-01-09.txt"),
      rLambda);
    final Generator pointGenerator = Generators.vectorD2Generator(
      Doubles.laplaceGenerator(
        PRNG.well44497b("seeds/Well44497b-2019-01-11.txt"),
        pMu, pSigma));

    final int ntriangles = 1023;
    final int npoints = 1023;
    int ntrys  = 0;
    int nexact = 0;
    int nround = 0;
    for (int i=0;i<ntriangles;i++) {
      final VectorD2 c = (VectorD2) centerGenerator.next();
      final double r = radiusGenerator.nextDouble();
      final VectorD2 p0 = ((VectorD2) pointGenerator.next()).project(c,r);
      final VectorD2 p1 = ((VectorD2) pointGenerator.next()).project(c,r);
      final VectorD2 p2 = ((VectorD2) pointGenerator.next()).project(c,r);
      final TriangleBF2 t = (TriangleBF2) TriangleBF2.of(p0,p1,p2);
      for (int j=0;j<npoints;j++) {
        ntrys++;
        final VectorD2 p = ((VectorD2) pointGenerator.next()).project(c,r);
        final BigFloat bf = t.inCircleDistanceBF(p);
        final double bfd = bf.doubleValue();
        if (bf.isZero()) { nexact++; }
        if (0.0 == bfd) { nround++; }
      } }
    System.out.println("ntriangles,npoints= " + ntriangles + ", " + npoints);
    System.out.println("cMu,cSigma= " + Double.toHexString(cMu) +
                         ", " + Double.toHexString(cSigma));
    System.out.println("rLambda= " + Double.toHexString(rLambda));
    System.out.println("pMu,pSigma= " + Double.toHexString(pMu) +
                         ", " + Double.toHexString(pSigma));
    System.out.println(
      "Exact bf  cocircular= " + nexact + "/" + ntrys +
        " = " + ((double) nexact)/ntrys);
    System.out.println(
      "Round bfd cocircular= " + nround + "/" + ntrys +
        " = " + ((double) nround)/ntrys);
    }

  //--------------------------------------------------------------------

  @SuppressWarnings("unused")
  public static final void main (final String[] args) {
    cocircularTrials(); }

  //--------------------------------------------------------------------
  // disable construction
  //--------------------------------------------------------------------

  private CocircularTrials () { throw new UnsupportedOperationException(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
