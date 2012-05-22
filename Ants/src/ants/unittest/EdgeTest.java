package ants.unittest;

import junit.framework.Assert;

import org.junit.Test;

import ants.entities.Edge;
import ants.entities.Tile;

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

}
