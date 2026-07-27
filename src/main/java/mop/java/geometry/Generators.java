package mop.java.geometry;

import mop.java.prng.Generator;
import mop.java.prng.GeneratorBase;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

/** Generators of pseudo-random geometric objects as zero-arity
 * 'functions' that return different values on each call.
 * <br>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-27
 */

public final class Generators {

  //--------------------------------------------------------------
  // For now just generate double[2] and double[3]
  // Later:
  // Constructors for:
  // <ol>
  // <li> org.apache.commons.geometry.euclidean.twod.Vector2D
  // <li> org.apache.commons.geometry.euclidean.twod.Segment
  // <li> 2d Triangle general
  // <li> 2d Triangle approx co-linear
  // <li> 2d Triangle plus vector approx co-circular
  // <li> org.apache.commons.geometry.euclidean.threed.Vector3D
  // <li> org.apache.commons.geometry.euclidean.threed.Triangle3D
  // <li> 2d plane in r3
  // <li> 3d tetrahedron
  // <li> 3d tetrahedron approx co-planar
  // <li> 3d tetrahedron plus point approx co-spherical
  // </ol>
  // use double[n] array generators for coordinates
  // random mu, sigma, zmin, zmax?
//  subnormalGenerator
//  exponentialGenerator
//    laplaceGenerator
//      normalGenerator
//  finiteGenerator
//  uniformGenerator (final int n,
//                    final UniformRandomProvider urp,
//                    final double zmin,
//                    final double zmax)
//    gaussianGenerator (final int n,
//                     final UniformRandomProvider urp,
//                     final double mu,
//                     final double sigma)
  //--------------------------------------------------------------
//  public static final Function<double[], Object>
//    vector2D = Vector2D::of;

  public static final Generator
  vector2dGenerator (final Generator doubleGenerator) {
    // TODO: named local class rather than anonymous with name?
    return new GeneratorBase("vector2dGenerator") {
      @Override
      public final Object next () {
        final double x = doubleGenerator.nextDouble();
        final double y = doubleGenerator.nextDouble();
        return Vector2D.of(x, y); } }; }

  public static final Generator
  vector2dGenerator (final int n,
                     final Generator doubleGenerator) {
    return new GeneratorBase("vector2dGenerator[" + n + "]") {
      final Generator vGenerator = vector2dGenerator(doubleGenerator);
      @Override
      public final Object next () {
        final Vector2D[] p =  new Vector2D[n];
        for (int i = 0; i < n; i++) {
          p[i] = (Vector2D) vGenerator.next(); }
        return p; } }; }

  //--------------------------------------------------------------

//  public static final Generator
//  triangleGenerator (final Generator doubleGenerator) {
//    // TODO: named local class rather than anonymous with name?
//    return new GeneratorBase("triangledGenerator") {
//      @Override
//      public final Object next () {
//        final double x = doubleGenerator.nextDouble();
//        final double y = doubleGenerator.nextDouble();
//        return Vector2D.of(x, y); } }; }
//
//  public static final Generator
//  TriangleGenerator (final int n,
//                     final Generator doubleGenerator) {
//    return new GeneratorBase("triangleGenerator[" + n + "]") {
//      final Generator vGenerator = vector2dGenerator(doubleGenerator);
//      @Override
//      public final Object next () {
//        final Vector2D[] p =  new Vector2D[n];
//        for (int i = 0; i < n; i++) {
//          p[i] = (Vector2D) vGenerator.next(); }
//        return p; } }; }

  //--------------------------------------------------------------
//  public static final Function<double[], Vector3D>
//    vector3D = Vector3D::of;

  public static final Generator
  vector3dGenerator (final Generator doubleGenerator) {
    return new GeneratorBase("vector3dGenerator") {
      @Override
      public final Object next () {
        final double x = doubleGenerator.nextDouble();
        final double y = doubleGenerator.nextDouble();
        final double z = doubleGenerator.nextDouble();
        return Vector3D.of(x,y,z); } }; }

  public static final Generator
  vector3dGenerator (final int n,
                     final Generator doubleGenerator) {
    return new GeneratorBase("vector3dGenerator[" + n + "]") {
      final Generator vGenerator = vector3dGenerator(doubleGenerator);
      @Override
      public final Object next () {
        final Vector3D[] p =  new Vector3D[n];
        for (int i = 0; i < n; i++) {
          p[i] = (Vector3D) vGenerator.next(); }
        return p; } }; }

  //--------------------------------------------------------------
  // TODO: transform generated coordinates to get correlated values, etc

//  public static final Generator
//  generator (final Generator coordinateGenerator,
//             final Function<double[], Object> constructor) {
//    return new GeneratorBase("geometryGenerator") {
//      @Override
//      public final Object next () {
//        final double[] coords = (double[]) coordinateGenerator.next();
//        return constructor.apply(coords); } }; }

  //--------------------------------------------------------------
//  private static final String SEED0 =
//    "seeds/Well44497b-2019-01-05.txt";
//  private static final UniformRandomProvider URP0 =
//    PRNG.well44497b(SEED0);
//
//  public static final Generator
//    exponentialVector2dGenerator = generator(
//    Doubles.exponentialGenerator(2, URP0, 1.0, 10.0),
//    Vector2D::of);
  //--------------------------------------------------------------
} // end class
//--------------------------------------------------------------
