package mop.java.geometry;

import mop.java.numbers.Doubles;
import mop.java.prng.Generator;
import mop.java.prng.GeneratorBase;
import mop.java.prng.PRNG;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.rng.UniformRandomProvider;

import java.util.function.Function;

/**
 * Generators of pseudo-random geometric objects as zero-arity
 * 'functions' that return different values on each call.
 * <br>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-05-15
 */

public final class Generators {

  //--------------------------------------------------------------
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

  public static final Function<double[], Vector2D>
    vector2D = Vector2D::of;
  public static final Function<double[], Vector3D>
    vector3D = Vector3D::of;

  //--------------------------------------------------------------
  // TODO: transform generated coordinates to get correlated values, etc

  public static final Generator
  generator (final Generator coordinateGenerator,
             final Function<double[], Object> constructor) {
    return new GeneratorBase("geometryGenerator") {
      @Override
      public final Object next () {
        final double[] coords = (double[]) coordinateGenerator.next();
        return constructor.apply(coords);
      }
    };
  }

  //--------------------------------------------------------------
  private static final String SEED0 =
    "seeds/Well44497b-2019-01-05.txt";
  private static final UniformRandomProvider URP0 =
    PRNG.well44497b(SEED0);

  public static final Generator
    exponentialVector2dGenerator = generator(
    Doubles.exponentialGenerator(2, URP0, 1.0, 10.0),
    Vector2D::of);


  //--------------------------------------------------------------
} // end class
//--------------------------------------------------------------
