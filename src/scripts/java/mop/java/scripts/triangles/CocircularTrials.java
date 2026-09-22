package mop.java.scripts.triangles;

import mop.java.geometry.Generators;
import mop.java.geometry.euclidean.VectorD2;
import mop.java.geometry.triangle.RelaxedIntervalTriangle2D;
import mop.java.geometry.triangle.RoundingIntervalTriangle2D;
import mop.java.geometry.triangle.ShewchukIntervalTriangle2D;
import mop.java.geometry.triangle.TriangleBF2;
import mop.java.numbers.BigFloat;
import mop.java.numbers.Doubles;
import mop.java.numbers.RelaxedInterval;
import mop.java.numbers.RoundingInterval;
import mop.java.prng.Generator;
import mop.java.prng.PRNG;

/** TODO: worth creating 'exact' cocircular points represented
 *    by: <code>center, radius, angle</code>?
 * <pre>
 * mvn -q clean install && j src/scripts/java/mop/java/scripts/triangles/CocircularTrials.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-21
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
    int nret = 0;
    int nrebf = 0;
    int nrebfd = 0;
    int nrot = 0;
    int nrobf = 0;
    int nrobfd = 0;
    int nsit = 0;
    int nsibf = 0;
    int nsibfd = 0;
    for (int i=0;i<ntriangles;i++) {
      final VectorD2 c = (VectorD2) centerGenerator.next();
      final double r = radiusGenerator.nextDouble();
      final VectorD2 p0 = ((VectorD2) pointGenerator.next()).project(c,r);
      final VectorD2 p1 = ((VectorD2) pointGenerator.next()).project(c,r);
      final VectorD2 p2 = ((VectorD2) pointGenerator.next()).project(c,r);
      final TriangleBF2 t =
        (TriangleBF2) TriangleBF2.of(p0,p1,p2);
      for (int j=0;j<npoints;j++) {
        ntrys++;
        final VectorD2 p = ((VectorD2) pointGenerator.next()).project(c,r);
        final BigFloat bf = t.inCircleDistanceBF(p);
        final double bfd = bf.doubleValue();
        if (bf.isZero()) { nexact++; }
        if (0.0 == bfd) { nround++; }
        final RoundingIntervalTriangle2D rot =
          (RoundingIntervalTriangle2D) RoundingIntervalTriangle2D.from(t);
        final RoundingInterval ro = rot.inCircleInterval(p);
        if (ro.containsZero()) { nrot++; }
        if (ro.contains(bf)) { nrobf++; }
        if (ro.contains(bfd)) { nrobfd++; }
        final RelaxedIntervalTriangle2D ret =
          (RelaxedIntervalTriangle2D) RelaxedIntervalTriangle2D.from(t);
        final RelaxedInterval re = ret.inCircleInterval(p);
        if (re.containsZero()) { nret++; }
        if (re.contains(bf)) { nrebf++; }
        if (re.contains(bfd)) { nrebfd++; }
        final ShewchukIntervalTriangle2D sit =
          (ShewchukIntervalTriangle2D) ShewchukIntervalTriangle2D.from(t);
        final RelaxedInterval si = sit.inCircleInterval(p);
        if (si.containsZero()) { nsit++; }
        if (si.contains(bf)) { nsibf++; }
        if (si.contains(bfd)) { nsibfd++; }
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
    System.out.println(
      "Relaxed Interval cocircular= " + nret + "/" + ntrys +
        " = " + ((double) nret)/ntrys);
    System.out.println(
      "Relaxed Interval contains bf = " + nrebf + "/" + ntrys +
        " = " + ((double) nrebf)/ntrys);
    System.out.println(
      "Relaxed Interval contains bfd= " + nrebfd + "/" + ntrys +
        " = " + ((double) nrebfd)/ntrys);
    System.out.println(
      "Rounding interval cocircular= " + nrot + "/" + ntrys +
        " = " + ((double) nrot)/ntrys);
    System.out.println(
      "Rounding interval contains bf = " + nrobf + "/" + ntrys +
        " = " + ((double) nrobf)/ntrys);
    System.out.println(
      "Rounding interval contains bfd= " + nrobfd + "/" + ntrys +
        " = " + ((double) nrobfd)/ntrys);
    System.out.println(
      "Shewchuk interval cocircular= " + nsit + "/" + ntrys +
        " = " + ((double) nsit)/ntrys);
    System.out.println(
      "Shewchuk interval contains bf=  " + nsibf + "/" + ntrys +
        " = " + ((double) nsibf)/ntrys);
    System.out.println(
      "Shewchuk interval contains bfd= " + nsibfd + "/" + ntrys +
        " = " + ((double) nsibfd)/ntrys);
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
