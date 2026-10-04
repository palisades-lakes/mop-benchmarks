package mop.java.jfx;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import mop.java.geometry.euclidean.VectorD2;

import java.util.Collection;
import java.util.List;

/** Convert various objects into JFX representations for display.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-04
 */

public final class JFX {

  public static final Color FRAME_COLOR = Color.web("#AA0000FF");
  public static final Color MESH_COLOR = Color.web("#0000AAFF");

  //--------------------------------------------------------------------
  /** JFX edge as a Line */

  private static final Shape edge (final VectorD2 p0,
                                   final VectorD2 p1,
                                   final Color color) {
    final Shape s = new Line(p0.x(), p0.y(), p1.x(), p1.y());
    s.setStroke(color);
    s.setStrokeWidth(1);
    s.setStrokeType(StrokeType.CENTERED);
    s.setStrokeLineCap(StrokeLineCap.ROUND);
    s.setStrokeLineJoin(StrokeLineJoin.ROUND);
    s.setStrokeMiterLimit(1.0);
    return s; }

  //--------------------------------------------------------------------
  /** Convert the edges,
   * represented by <code>VectorD2[][3]</code> coordinates,
   * into a JFX Group Node for display.
   * Skip edges that have any vertices in <code>frame</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public static final Group edges (final Collection<VectorD2[]> points,
                                   final Color color,
                                   final String id) {
    final Group group = new Group();
    final List<Node> children = group.getChildren();
    for (final VectorD2[] p : points) {
      children.add(edge(p[0],p[1],color)); }
    group.setId(id);
    return group; }

  //--------------------------------------------------------------------
  /** JFX triangle as a polyline */

  private static final Shape triangle (final VectorD2 p0,
                                       final VectorD2 p1,
                                       final VectorD2 p2,
                                       final Color color) {
    final Shape s = new Polyline(p0.x(), p0.y(),
                                 p1.x(), p1.y(),
                                 p2.x(), p2.y());
    s.setStroke(color);
    s.setStrokeWidth(1);
    s.setStrokeType(StrokeType.CENTERED);
    s.setStrokeLineCap(StrokeLineCap.ROUND);
    s.setStrokeLineJoin(StrokeLineJoin.ROUND);
    s.setStrokeMiterLimit(1.0);
    return s; }

  //--------------------------------------------------------------------
  /** Convert the triangles,
   * represented by <code>VectorD2[][3]</code> coordinates,
   * into a JFX Group Node for display.
   * Skip triangles that have any vertices in <code>frame</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public static final Group triangles (final Collection<VectorD2[]> points,
                                       final Color color,
                                       final String id) {
    final Group group = new Group();
    final List<Node> children = group.getChildren();
    for (final VectorD2[] p : points) {
      children.add(triangle(p[0],p[1],p[2],color)); }
    group.setId(id);
    return group; }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------
  /** disabled constructor, class method only. */

  private JFX () {
    throw new UnsupportedOperationException(
      getClass().getSimpleName() + ": construction not allowed."); }

//--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
