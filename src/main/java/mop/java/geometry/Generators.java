package mop.java.geometry;

import mop.java.geometry.tetrahedron.Tetrahedron3D;
import mop.java.geometry.tetrahedron.TetrahedronVector3D;
import mop.java.geometry.triangle.Triangle2D;
import mop.java.geometry.triangle.TriangleVector2D;
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
  // TODO:
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
//  gaussianGenerator (final int n,
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

  public static final Generator
  triangleGenerator (final Generator vectorGenerator) {
    // TODO: named local class rather than anonymous with name?
    return new GeneratorBase("triangleGenerator") {
      @Override
      public final Object next () {
        final Vector2D p0 = (Vector2D) vectorGenerator.next();
        final Vector2D p1 = (Vector2D) vectorGenerator.next();
        final Vector2D p2 = (Vector2D) vectorGenerator.next();
        return TriangleVector2D.of(p0,p1,p2); } }; }

  public static final Generator
  triangleGenerator (final int n,
                     final Generator vectorGenerator) {
    return new GeneratorBase("triangleGenerator[" + n + "]") {
      final Generator tGenerator = triangleGenerator(vectorGenerator);
      @Override
      public final Object next () {
        final Triangle2D[] p =  new Triangle2D[n];
        for (int i = 0; i < n; i++) {
          p[i] = (Triangle2D) tGenerator.next(); }
        return p; } }; }

//  public static final Generator
//  triangleGenerator (final Function<Triangle2D,Triangle2D> converter,
//                     Generator vectorGenerator) {
//    final Generator tGenerator = triangleGenerator(vectorGenerator);
//    return new GeneratorBase(converter + " * triangleGenerator") {
//      @Override
//      public final Object next () {
//        return converter.apply((Triangle2D) tGenerator.next()); } }; }

//  public static final Generator
//  triangleGenerator (final int n,
//                     final Function<Triangle2D,Triangle2D> converter,
//                     final Generator vectorGenerator) {
//    return new GeneratorBase(
//      converter + " * triangleGenerator[" + n + "]") {
//      final Generator tGenerator =
//        triangleGenerator(converter, vectorGenerator);
//      @Override
//      public final Object next () {
//        final Triangle2D[] p =  new Triangle2D[n];
//        for (int i = 0; i < n; i++) {
//          p[i] = (Triangle2D) tGenerator.next(); }
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

  public static final Generator
  tetrahedraGenerator (final Generator vectorGenerator) {
    // TODO: named local class rather than anonymous with name?
    return new GeneratorBase("tetrahedraGenerator") {
      @Override
      public final Object next () {
        final Vector3D p0 = (Vector3D) vectorGenerator.next();
        final Vector3D p1 = (Vector3D) vectorGenerator.next();
        final Vector3D p2 = (Vector3D) vectorGenerator.next();
        final Vector3D p3 = (Vector3D) vectorGenerator.next();
        return TetrahedronVector3D.of(p0, p1, p2, p3); } }; }

  public static final Generator
  tetrahedraGenerator (final int n,
                       final Generator vectorGenerator) {
    return new GeneratorBase("tetrahedraGenerator[" + n + "]") {
      final Generator tGenerator = tetrahedraGenerator(vectorGenerator);
      @Override
      public final Object next () {
        final Tetrahedron3D[] p =  new Tetrahedron3D[n];
        for (int i = 0; i < n; i++) {
          p[i] = (TetrahedronVector3D) tGenerator.next(); }
        return p; } }; }


//  public static final Generator
//  tetrahedraGenerator (final Function<Tetrahedron3D,Tetrahedron3D> converter,
//                     Generator vectorGenerator) {
//    final Generator tGenerator = tetrahedraGenerator(vectorGenerator);
//    return new GeneratorBase(
//      converter + " * tetrahedraGenerator") {
//      @Override
//      public final Object next () {
//        return converter.apply((Tetrahedron3D) tGenerator.next()); } }; }

//  public static final Generator
//  tetrahedraGenerator (final int n,
//                     final Function<Tetrahedron3D,Tetrahedron3D> converter,
//                     final Generator vectorGenerator) {
//    return new GeneratorBase(
//      converter + " * tetrahedraGenerator[" + n + "]") {
//      final Generator tGenerator =
//        tetrahedraGenerator(converter, vectorGenerator);
//      @Override
//      public final Object next () {
//        final Tetrahedron3D[] p =  new Tetrahedron3D[n];
//        for (int i = 0; i < n; i++) {
//          p[i] = (Tetrahedron3D) tGenerator.next(); }
//        return p; } }; }

  //--------------------------------------------------------------
} // end class
//--------------------------------------------------------------
