package mop.java.scripts.triangles;

import mop.java.geometry.Generators;
import mop.java.geometry.triangle.TriangleR2;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;

/** Profile triangle classes over 'cocircular' examples.
 * <pre>
 * mvn -q install && jy src/scripts/java/mop/java/scripts/triangles/SignedAreaProfile.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 202-09-26
 */

public final class SignedAreaProfile {

  //--------------------------------------------------------------
  // each row contains npoints 'cocircular' points

  public static final TriangleR2[]
  triangles (final int nTriangles) {

    final double pMu = 0.0;
    final double pSigma = 3.0;

    final Generator triangleGenerator =
      Generators.triangleGenerator(
        nTriangles,
        Generators.vectorD2Generator(
          Doubles.laplaceGenerator(
            PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
            pMu, pSigma)));

    final TriangleR2[] t =
      TriangleR2.convertTriangles(
        (TriangleR2[]) triangleGenerator.next(),
      "SegmentD2Eager");
    System.gc();
    System.gc();
    System.out.println("nTriangles= " + nTriangles);
    System.out.println("pMu,pSigma= " + Double.toHexString(pMu) +
                         ", " + Double.toHexString(pSigma));
    System.out.flush();

    return t; }

  //--------------------------------------------------------------

  public static final void
  areaTrials () {

    final int ntrys = 524288;
    final int nTriangles = 524288;
    final TriangleR2[] t = triangles(nTriangles);
    System.gc();
    System.gc();
    int nzero=0;
    for (int i=0; i<ntrys; i++) {
      for (int j=0;j<nTriangles;j++) {
          final double area = t[j].twiceSignedArea();
          if (0.0 == area) { nzero++; } } }
    System.out.println("Zero areas= " + nzero); }

  //--------------------------------------------------------------------

  public static final void main (final String[] ignore) {
    areaTrials(); }

  //--------------------------------------------------------------------
  // disable construction
  //--------------------------------------------------------------------

  private SignedAreaProfile () {
    throw new UnsupportedOperationException(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
