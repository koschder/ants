package unittest.ants.tactics;

import influence.DefaultInfluenceMap;
import influence.unittest.InfluenceTestMap;

import java.util.List;

import org.junit.Test;

import ants.tactics.CombatPositioning;
import ants.tactics.DefaultCombatPositioning;
import api.entities.Tile;
import api.entities.Unit;
import api.strategy.InfluenceMap;

import static org.junit.Assert.*;

public class CombatPositioningTest {
    @Test
    public void testPositioning() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woo1o1ooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooo0oooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooooooowwwwwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooooowoooooooooooowooooooooow";
        sMap += "woooooooooooowoooowooooooowooooooooow";
        sMap += "wooooooooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        InfluenceTestMap map = new InfluenceTestMap(37, sMap);

        final InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);
        List<Unit> enemyAnts = map.getUnits(1);
        List<Unit> myAnts = map.getUnits(0);

        final Unit myAnt = myAnts.get(0);

        CombatPositioning pos = new DefaultCombatPositioning(map, iMap, myAnts, enemyAnts, null);
        assertEquals(new Tile(5, 4), pos.getNextTile(myAnt));
    }
}
