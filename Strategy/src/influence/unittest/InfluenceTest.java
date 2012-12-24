package influence.unittest;

import influence.DefaultInfluenceMap;

import org.junit.Assert;
import org.junit.Test;

import api.entities.Tile;
import api.strategy.InfluenceMap;
import api.test.MapOutput;
import api.test.PixelDecorator;

import static org.junit.Assert.*;

/**
 * the class containing all unit tests to prove the functionalities of the influence map
 * 
 * @author kaeserst
 * 
 */
public class InfluenceTest {

    /**
     * this test creates an influence map, and writes an html file to see the results
     */
    @Test
    public void safetyInfluenceTest() {

        System.out.println("safetyInfluenceTest");
        String sMap = getDefaultTestMap();

        InfluenceTestMap map = new InfluenceTestMap(37, sMap);

        final InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);

        MapOutput put = new MapOutput();
        put.setMap(map);
        put.setClusterSize(5);
        put.addAllUnits();
        put.saveHtmlMap("InfluenceTest_safetyInfluenceTest", getSafetyDecorator(iMap));

        Assert.assertEquals(iMap.getInfluence(1, new Tile(2, 4)), iMap.getInfluence(0, new Tile(2, 4)));
        Assert.assertTrue(iMap.getSafety(new Tile(2, 3)) > 0);
        Assert.assertTrue(iMap.getSafety(new Tile(2, 5)) < 0);

    }

    private String getDefaultTestMap() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woo0o1ooooowwwwwwwwwwoo2ooooooo0oooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooo0ooowwwwwwwwwwwwwooooowooo1ooooow";
        sMap += "woooooooooooowoooooooooooowooooooooow";
        sMap += "wooooooooooo0woooowoooooo2wooooooooow";
        sMap += "woooo11ooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        return sMap;
    }

    /**
     * tests the update mechanism
     */
    @Test
    public void safetyInfluenceUpdateTest() {

        System.out.println("safetyInfluenceTest");
        String sMap = getDefaultTestMap();

        InfluenceTestMap initialMap = new InfluenceTestMap(37, sMap);

        sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "wo0ooo1oooowwwwwwwwwwooo2ooooo0ooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooooooowwwwwwwwwwwwwooooowooooooooow";
        sMap += "wooo0oooooooowooooooooooo2wooo1ooooow";
        sMap += "woooo1ooooo0owoooowooooooowooooooooow";
        sMap += "wooooooooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        InfluenceTestMap updateMap = new InfluenceTestMap(37, sMap);

        final DefaultInfluenceMap iMap = new DefaultInfluenceMap(initialMap, 8, 4);
        iMap.update(updateMap);

        MapOutput put = new MapOutput();
        put.setMap(updateMap);
        put.setClusterSize(5);
        put.addAllUnits();
        put.saveHtmlMap("InfluenceTest_safetyInfluenceUpdateTest", getSafetyDecorator(iMap));

    }

    private PixelDecorator getSafetyDecorator(final InfluenceMap iMap) {
        return new PixelDecorator() {

            @Override
            public String getLabel(Tile tile) {
                return String.valueOf(iMap.getSafety(tile));
            }
        };
    }

    /**
     * tests the total influence function each player
     */
    @Test
    public void totalInfluenceTest() {

        System.out.println("safetyInfluenceTest");
        String sMap = getDefaultTestMap();

        InfluenceTestMap map = new InfluenceTestMap(37, sMap);

        InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);

        assertEquals(2520, iMap.getTotalInfluence(0));
        assertEquals(2370, iMap.getTotalInfluence(1));
        assertEquals(1120, iMap.getTotalInfluence(2));

    }
}
