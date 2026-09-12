package mop.java.scripts.triangles;

import mop.java.geometry.Generators;
import mop.java.geometry.triangle.*;
import mop.java.numbers.BigFloat;
import mop.java.numbers.RelaxedInterval;
import mop.java.numbers.RoundingInterval;
import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.GeneratorBase;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** TODO: worth creating 'exact' colinear points represented
 *    by implied affine combination: <code>a,p0,p1</code>?
 * <pre>
 * mvn -q clean install && j src/scripts/java/mop/java/scripts/triangles/ColinearTrials.java
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-09-09
 */

public final class ColinearTrials {

  //--------------------------------------------------------------

//  private static final Vector2D fmaAffine (final double a,
//                                           final Vector2D p0,
//                                           final Vector2D p1) {
//    final double x1 = p1.getX();
//    final double y1 = p1.getY();
//    final double dx = p0.getX() - x1;
//    final double dy = p0.getY() - y1;
//    final double x2 = Math.fma(a,dx,x1);
//    final double y2 = Math.fma(a,dy,y1);
//    return Vector2D.of(x2,y2); }

  private static final Vector2D bfAffine (final double a,
                                           final Vector2D p0,
                                           final Vector2D p1) {
    final BigFloat dx = BigFloat.sum(p0.getX(),-p1.getX());
    final BigFloat dy = BigFloat.sum(p0.getY(),-p1.getY());
    final BigFloat x2 = BigFloat.axpy(a,dx,p1.getX());
    final BigFloat y2 = BigFloat.axpy(a,dy,p1.getY());
    return Vector2D.of(x2.doubleValue(),y2.doubleValue()); }

  private static final Generator
  colinearTriangleGenerator (final Generator vectorGenerator,
                             final Generator doubleGenerator) {
    // TODO: named local class rather than anonymous with name?
    return new GeneratorBase("colinearTriangleGenerator") {
      @Override
      public final Object next () {
        final Vector2D p0 = (Vector2D) vectorGenerator.next();
        final Vector2D p1 = (Vector2D) vectorGenerator.next();
        final double a = doubleGenerator.nextDouble();
        //final Vector2D p2 = p0.multiply(a).add(1.0-a,p1);
        //final Vector2D p2 = fmaAffine(a,p0,p1);
        final Vector2D p2 = bfAffine(a,p0,p1);
        return TriangleVector2D.of(p0,p1,p2); } }; }

//--------------------------------------------------------------

  public static final void
  colinearTrials () {

    final double pMu = 1.0;
    final double pSigma = 1.0;
    final double aMu = 0.0;
    final double aSigma = 3.0;
    final Generator colinearGenerator =
      colinearTriangleGenerator(
        Generators.vector2dGenerator(
          Doubles.laplaceGenerator(
            PRNG.well44497b("seeds/Well44497b-2019-01-07.txt"),
            pMu, pSigma)),
        Doubles.laplaceGenerator(
          PRNG.well44497b("seeds/Well44497b-2019-01-09.txt"),
          aMu,aSigma));

    final int ntriangles = 1023*1023;
    int nexact = 0;
    int nround = 0;
    int ndit = 0;
    int ndibf = 0;
    int ndibfd = 0;
    int nrit = 0;
    int nribf = 0;
    int nribfd = 0;
    int nsit = 0;
    int nsibf = 0;
    int nsibfd = 0;
    for (int i=0;i<ntriangles;i++) {
      final BigFloatTriangle2D t =
        (BigFloatTriangle2D)
          BigFloatTriangle2D.from(
            (Triangle2D) colinearGenerator.next());
      final BigFloat bf = t.getV20xV10();
      final double bfd = bf.doubleValue();
      if (t.getV20xV10().isZero()) { nexact++; }
      if (0.0 == bfd) { nround++; }
      final RelaxedIntervalTriangle2D dit =
        (RelaxedIntervalTriangle2D) RelaxedIntervalTriangle2D.from(t);
      final RelaxedInterval di = dit.getV20xV10();
      if (di.containsZero()) { ndit++; }
      if (di.contains(bf)) { ndibf++; }
      if (di.contains(bfd)) { ndibfd++; }
      final RoundingIntervalTriangle2D rit =
        (RoundingIntervalTriangle2D) RoundingIntervalTriangle2D.from(t);
      final RoundingInterval ri = rit.getV20xV10();
      if (ri.containsZero()) { nrit++; }
      if (ri.contains(bf)) { nribf++; }
      if (ri.contains(bfd)) { nribfd++; }
      final ShewchukIntervalTriangle2D sit =
        (ShewchukIntervalTriangle2D) ShewchukIntervalTriangle2D.from(t);
      final RelaxedInterval si = sit.twiceSignedAreaInterval();
      if (si.containsZero()) { nsit++; }
      if (si.contains(bf)) { nsibf++; }
      if (si.contains(bfd)) { nsibfd++; }
    }
    System.out.println("pMu,pSigma= " + Double.toHexString(pMu) +
                         ", " + Double.toHexString(pSigma));
    System.out.println("aMu,aSigma= " + Double.toHexString(aMu) +
                         ", " + Double.toHexString(aSigma));
    System.out.println(
      "Exact colinear= " + nexact + "/" + ntriangles +
        " = " + ((double) nexact)/ntriangles);
    System.out.println(
      "Round colinear= " + nround + "/" + ntriangles +
        " = " + ((double) nround)/ntriangles);
    System.out.println(
      "Round interval colinear= " + nrit + "/" + ntriangles +
        " = " + ((double) nrit)/ntriangles);
    System.out.println(
      "Round interval contains bf= " + nribf + "/" + ntriangles +
        " = " + ((double) nribf)/ntriangles);
    System.out.println(
      "Round interval contains bfd= " + nribfd + "/" + ntriangles +
        " = " + ((double) nribfd)/ntriangles);
    System.out.println(
      "Relaxed Interval colinear= " + ndit + "/" + ntriangles +
        " = " + ((double) ndit)/ntriangles);
    System.out.println(
      "Relaxed Interval contains bf= " + ndibf + "/" + ntriangles +
        " = " + ((double) ndibf)/ntriangles);
    System.out.println(
      "Relaxed Interval contains bfd= " + ndibfd + "/" + ntriangles +
        " = " + ((double) ndibfd)/ntriangles);
    System.out.println(
      "Shewchuk interval colinear= " + nsit + "/" + ntriangles +
        " = " + ((double) nsit)/ntriangles);
    System.out.println(
      "Shewchuk interval contains bf= = " + nsibf + "/" + ntriangles +
        " = " + ((double) nsibf)/ntriangles);
    System.out.println(
      "Shewchuk interval contains bfd= = " + nsibfd + "/" + ntriangles +
        " = " + ((double) nsibfd)/ntriangles);
  }

  //--------------------------------------------------------------------

  @SuppressWarnings("unused")
  public static final void main (final String[] args) {
    colinearTrials(); }

  //--------------------------------------------------------------------
  // disable construction
  //--------------------------------------------------------------------

  private ColinearTrials () { throw new UnsupportedOperationException(); }

  //-------------------------------------------------------------------
} // end class
//-------------------------------------------------------------------
