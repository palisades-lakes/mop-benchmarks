package mop.java.geometry.delaunay.gg4;

/** Delaunay triangulation, after
 * <a href="https://www.researchgate.net/publication/262235495_Incremental_Delaunay_Triangulation">
 *  Dani Lischinski
 *  <i>Incremental Delaunay triangulation</i>
 *  <b>Graphics Gems IV</b> Academic Press 1994 </a>

 * @author palisades dot lakes at gmail dot com
 * @version 2026-10-01
 */

public final class QuadEdge {

  private final Edge[] e;
  public final Edge edge (final int i) { return e[i]; }

  //--------------------------------------------------------------------
  // construction
  //--------------------------------------------------------------------

  public QuadEdge () {
    e = new Edge[] {
      new Edge(this),
      new Edge(this),
      new Edge(this),
      new Edge(this), };
    e[0].num = 0;
    e[1].num = 1;
    e[2].num = 2;
    e[3].num = 3;
    e[0].next = e[0];
    e[1].next = e[3];
    e[2].next = e[2];
    e[3].next = e[1];
  }

  //--------------------------------------------------------------------
} // end class
//--------------------------------------------------------------------

