package mop.java.geometry.delaunay.jts;

import java.util.Collection;

/** Customize
 * <a href="https://github.com/locationtech/jts/blob/master/modules/core/src/main/java/org/locationtech/jts/triangulate/quadedge/LastFoundQuadEdgeLocator.java">
 * LastFoundQuadEdgeLocator</a>
 * for mop test framework, etc.
 */
public class LastFoundQuadEdgeLocator implements QuadEdgeLocator {
    private QuadEdgeSubdivision subdiv;
    private QuadEdge            lastEdge = null;

    public LastFoundQuadEdgeLocator (QuadEdgeSubdivision subdiv) {
        this.subdiv = subdiv;
        init();
    }

    private void init() {
        lastEdge = findEdge();
    }

    private QuadEdge findEdge() {
        Collection edges = subdiv.getEdges();
        // assume there is an edge - otherwise will get an exception
        return (QuadEdge) edges.iterator().next();
    }

    /**
     * Locates an edge e, such that either v is on e, or e is an edge of a triangle containing v.
     * The search starts from the last located edge and proceeds on the general direction of v.
     */
    public QuadEdge locate (Vertex v) {
        if (! lastEdge.isLive()) {
            init();
        }

        QuadEdge e = subdiv.locateFromEdge(v, lastEdge);
        lastEdge = e;
        return e;
    }
}
