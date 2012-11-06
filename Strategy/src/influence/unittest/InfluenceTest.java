package influence.unittest;

import static org.junit.Assert.assertEquals;
import influence.DefaultInfluenceMap;

import org.junit.Test;

import api.entities.Tile;
import api.strategy.InfluenceMap;
import api.test.MapOutput;
import api.test.PixelDecorator;

public class InfluenceTest {

    @Test
    public void safetyInfluenceTest() {

        System.out.println("safetyInfluenceTest");
        String sMap = getDefaultTestMap();

        UnitTestInfluenceMap map = new UnitTestInfluenceMap(37, sMap);

        final InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);

        MapOutput put = new MapOutput();
        put.setMap(map);
        put.setClusterSize(5);
        put.addAllUnits();
        put.saveHtmlMap("frickling_safetyInfluenceTest", getSafetyDecorator(iMap));

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

    @Test
    public void safetyInfluenceUpdateTest() {

        System.out.println("safetyInfluenceTest");
        String sMap = getDefaultTestMap();

        UnitTestInfluenceMap initialMap = new UnitTestInfluenceMap(37, sMap);

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

        UnitTestInfluenceMap updateMap = new UnitTestInfluenceMap(37, sMap);

        final DefaultInfluenceMap iMap = new DefaultInfluenceMap(initialMap, 8, 4);
        iMap.update(updateMap);

        MapOutput put = new MapOutput();
        put.setMap(updateMap);
        put.setClusterSize(5);
        put.addAllUnits();
        put.saveHtmlMap("frickling_safetyInfluenceUpdateTest", getSafetyDecorator(iMap));

    }

    private PixelDecorator getSafetyDecorator(final InfluenceMap iMap) {
        return new PixelDecorator() {

            @Override
            public String getLabel(Tile tile) {
                return String.valueOf(iMap.getSafety(tile));
            }
        };
    }

    @Test
    public void totalInfluenceTest() {

        System.out.println("safetyInfluenceTest");
        String sMap = getDefaultTestMap();

        UnitTestInfluenceMap map = new UnitTestInfluenceMap(37, sMap);

        InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);

        assertEquals(2820, iMap.getTotalInfluence(0));
        assertEquals(2110, iMap.getTotalInfluence(1));
        assertEquals(1300, iMap.getTotalInfluence(2));

    }
}
