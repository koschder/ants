package ants.unittest;

import junit.framework.Assert;

import org.junit.Test;

import ants.entities.DirectedEdge;
import ants.entities.Edge;
import ants.entities.Tile;
import ants.search.Cluster;

public class EdgeTest {

    @Test
    public void test() {

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
    public void DirectedEdgeTest() {
        DirectedEdge c0Edge = new DirectedEdge(new Tile(0,0),new Tile(0,10),null);
        DirectedEdge c1Edge = new DirectedEdge(new Tile(0,0),new Tile(0,10),null);
    
        Assert.assertTrue(c0Edge.equals(c1Edge));
        
        DirectedEdge c2Edge = new DirectedEdge(new Tile(0,10),new Tile(0,0),null);
        DirectedEdge c3Edge = new DirectedEdge(new Tile(0,0),new Tile(0,10),null);
    
        Assert.assertTrue(c2Edge.equals(c3Edge));
        
        DirectedEdge c4Edge = new DirectedEdge(new Tile(0,0),new Tile(0,10),null);
        DirectedEdge c5Edge = new DirectedEdge(new Tile(0,0),new Tile(0,10),new Cluster(0,0,0,null));
    
        Assert.assertFalse(c4Edge.equals(c5Edge));
    }


}
