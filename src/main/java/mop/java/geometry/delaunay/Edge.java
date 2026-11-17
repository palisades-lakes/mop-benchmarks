package mop.java.geometry.delaunay;

import mop.java.cmplx.TwoSimplex;
import mop.java.cmplx.ZeroSimplex;

import java.util.*;

/** Generalization of half- and quad-edge mesh representation.
 * </p>
 * @author palisades dot lakes at gmail dot co
 * @version 2026-11-16
 */
public interface Edge extends Comparable<Edge> {

  // TODO: half edge structure typically have slots:
  //  origin
  //  reverse
  //  faceNExt
  //  face


  public ZeroSimplex origin ();
  public ZeroSimplex dest ();

  public default String name () {
    return origin() + "->" + dest(); }

  /** The edge from the destination to the origin. */
  public Edge reverse ();
  /** The next edge ccw in the left face. */
  public Edge faceNext ();
  public Edge facePrev ();

  //--------------------------------------------------------------------
  // vertex neighborhood
  //--------------------------------------------------------------------

  /** The next edge ccw around the origin. */
  public Edge next ();

  /** The next edge cw around the origin. */
  public Edge prev ();

  //--------------------------------------------------------------------
  // Basic Topological Operators
  //--------------------------------------------------------------------
  /** Break all the links from <code>this</code> pair to any
   * other <code>HEdge</code>.
   * Repair the <code>faceNext</code> links in neighbors.
   */

  public void delete ();

  //--------------------------------------------------------------------
  /** Lischinski: <br>
   * Add a new edge e connecting
   * the destination of <code>this</code>
   * to the origin of <code>that</code></code>,
   * in such a way that all three have the same
   * left face after the connection is complete.
   * Additionally, the data pointers of the new edge are set.
   */

  public Edge connect (final Edge that);

  /** Lischinski: <br>
   * Essentially turns edge e counterclockwise inside its enclosing
   * face (at least quadrilateral).
   * <br>
   * <em>Rewritten to return a new HEdge, to maintain that constraint
   * between <code>next()</code> and <code>origin()</code></em>.
   */

  public Edge swap ();

  //--------------------------------------------------------------------
  // collect mesh elements
  //--------------------------------------------------------------------
  /** Collect the vertices as a set of points.
   * Skip elements of <code>exclude</code>.
   * <b>Assumes mesh is connected!</b>
   */

  public default SortedSet<ZeroSimplex>
  zeroSimplexes (final Set<ZeroSimplex> exclude) {
    final SortedSet<ZeroSimplex> t = new TreeSet<>();
    final Set<Edge> visited = new HashSet<>();
    final Stack<Edge> toVisit = new Stack<>();
    toVisit.push(this);
    while (! toVisit.empty()) {
      final Edge e0 = toVisit.pop();
      if (! visited.contains(e0)) {
        visited.add(e0);
        toVisit.push(e0.reverse());
        toVisit.push(e0.faceNext());
        final ZeroSimplex p = e0.origin();
        if (!exclude.contains(p)) { t.add(p); } } }
    return t; }

  public default SortedSet<ZeroSimplex> zeroSimplexes () {
    return zeroSimplexes(new TreeSet<>()); }

  /** Collect connected triangular faces, skip others. */
  public default SortedSet<TwoSimplex> twoSimplexes () {
    validateMesh();
    final SortedSet<TwoSimplex> faces = new TreeSet<>();
    final Set<Edge> visited = new HashSet<>();
    final Stack<Edge> toVisit = new Stack<>();
    toVisit.push(this);
    while (! toVisit.empty()) {
      final Edge e0 = toVisit.pop();
      if (! visited.contains(e0)) {
        visited.add(e0);
        final Edge e1 = e0.faceNext(); visited.add(e1);
        final Edge e2 = e1.faceNext(); visited.add(e2);
        assert ! (e0.equals(e1) || e0.equals(e2) || (e1.equals(e2)));
        toVisit.push(e0.reverse());
        toVisit.push(e1.reverse());
        toVisit.push(e2.reverse());
        Edge e3 = e2.faceNext();
        if (e0.equals(e3)) { // triangular
          final ZeroSimplex z0 = e0.origin();
          final ZeroSimplex z1 = e1.origin();
          final ZeroSimplex z2 = e2.origin();
          assert (! z0.equals(z1)) :
            "e0 <> e1" + e0 + "\n" + e1
            + "\n" + z0 + "<>" + z1;
          assert ! z1.equals(z2) : "e1 <> e2" + e1 + "\n" + e2;
          assert ! z2.equals(z0) : "e2 <> e0" + e2 + "\n" + e0;
          final TwoSimplex face = TwoSimplex.make(z0,z1,z2);
          faces.add(face); }
        else {
          while (! e0.equals(e3)) {
            visited.add(e3); toVisit.push(e3.reverse());
            e3 = e3.faceNext(); } } } }
    return faces; }

  //--------------------------------------------------------------------
  // Comparable
  //--------------------------------------------------------------------

  @Override
  public default int compareTo (final Edge h) {
    final int c = origin().compareTo(h.origin());
    if (0 != c) { return c; }
    return dest().compareTo(h.dest()); }

  //--------------------------------------------------------------------
  // debugging
  //--------------------------------------------------------------------


  public default void validate () {
    assert Objects.equals(origin(),next().origin());
    assert Objects.equals(origin(),prev().origin());
    assert Objects.equals(origin(),facePrev().dest());
    assert Objects.equals(dest(),faceNext().origin());
    assert Objects.equals(this,reverse().reverse()); }

  public default void validateMesh () {
    final Set<Edge> visited = new HashSet<>();
    final Stack<Edge> toVisit = new Stack<>();
    toVisit.push(this);
    while (! toVisit.empty()) {
      final Edge e = toVisit.pop();
      if (! visited.contains(e)) {
        e.validate();
        visited.add(e);
        toVisit.push(e.next());
        toVisit.push(e.reverse());
      }}
  }
  public static void printFace (final Set<Edge> visited,
                                      final Stack<Edge> toVisit) {
    final Edge e0 = toVisit.pop();
    if ((null!=e0) && (! visited.contains(e0))) {
      System.out.println("face:" );
      Edge e1 = e0;
      do {
        toVisit.remove(e1);
        if (! visited.contains(e1)) {
          System.out.println(e1);
          visited.add(e1);
          final Edge ri = e1.reverse();
          if (! visited.contains(ri)) { toVisit.push(ri); } }
        e1 = e1.faceNext();
        assert null != e1;
      } while (e0 != e1); } }

  public default void printMesh () {
    final Set<Edge> visited = new HashSet<>();
    final Stack<Edge> toVisit = new Stack<>();
    toVisit.push(this);
    while (!toVisit.empty()) { printFace(visited,toVisit); } }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------
