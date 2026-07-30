package mop.java.benchmarks.tetrahedra;

import mop.java.geometry.tetrahedron.Tetrahedron3D;
import org.apache.commons.geometry.euclidean.threed.Vector3D;

/** <pre>
 * mvn clean install && java -cp target\benchmarks.jar mop.java.benchmarks.tetrahedra.InSphere
 * </pre>
 * @author palisades dot lakes at gmail dot com
 * @version 2026-07-29
 */

public class InSphere extends Base {

  @Override
  public final double operation (final Tetrahedron3D t,
                                 final Vector3D p) {
    return t.inSphere(p); }

  @SuppressWarnings("unused")
  public static final void main (final String[] args)  {
    Defaults.run("InSphere"); } }
