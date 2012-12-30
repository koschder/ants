package pathfinder.unittest;

import junit.framework.Assert;

import org.junit.Test;

import pathfinder.entities.DirectedEdge;
import pathfinder.entities.Edge;
import pathfinder.entities.Vertex;
import api.entities.Tile;

/**
 * comparing edges
 */
public class EdgeTest {

    /**
     * comparing edges
     */
    @Test
    public void compareEdgeTest() {

        Tile t1 = new Tile(3, 4);
        Tile t2 = new Tile(10, 3);
        Tile t3 = new Tile(2, 5);

        Edge e1 = new Edge(t1, t2, null);
        Edge e2 = new Edge(t2, t1, null);
        Edge e3 = new Edge(t2, t3, null);

        Assert.assertTrue(e1.equals(e2));
        Assert.assertFalse(e1.equals(e3));

    }

    @Test
    public void compareVertexTest() {

        Tile t1 = new Tile(3, 4);
        Tile t2 = new Tile(10, 3);
        Tile t3 = new Tile(10, 3);
        Edge e1 = new Edge(t1, t2, null);
        Vertex v1 = new Vertex(t1, e1);
        Vertex v2 = new Vertex(t2, e1);
        Vertex v3 = new Vertex(t3, e1);

        boolean b1 = v1.equals(v2);

        Assert.assertFalse(b1);
        Assert.assertTrue(v2.equals(v3));

    }

    /**
     * compare directed edges
     */
    @Test
    public void directedEdgeTest() {
        DirectedEdge c0Edge = new DirectedEdge(new Tile(0, 0), new Tile(0, 10), null);
        DirectedEdge c1Edge = new DirectedEdge(new Tile(0, 0), new Tile(0, 10), null);

        Assert.assertTrue(c0Edge.equals(c1Edge));

        DirectedEdge c2Edge = new DirectedEdge(new Tile(0, 10), new Tile(0, 0), null);
        DirectedEdge c3Edge = new DirectedEdge(new Tile(0, 0), new Tile(0, 10), null);

        Assert.assertFalse(c2Edge.equals(c3Edge));

    }

}
