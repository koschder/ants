package influence.unittest;

import influence.DefaultInfluenceMap;

import org.junit.Test;

import api.InfluenceMap;
import api.MapOutput;

public class InfluenceTest {

    @Test
    public void safetyInfluenceTest() {

        System.out.println("safetyInfluenceTest");
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woo0o1ooooowwwwwwwwwwoo2ooooooo0oooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooo0ooowwwwwwwwwwwwwooooowooo1ooooow";
        sMap += "woooooooooooowoooooooooooowooooooooow";
        sMap += "wooooooooooo0woooowoooooo2wooooooooow";
        sMap += "woooo1oooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        UnitTestInfluenceMap map = new UnitTestInfluenceMap(37, sMap);

        InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);

        MapOutput put = new MapOutput();
        put.setMap(map);
        put.setClusterSize(5);
        put.setInfluenceMap(iMap);
        put.addAllUnits();
        put.saveHtmlMap("frickling_safetyInfluenceTest");

    }
}
