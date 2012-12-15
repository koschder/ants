package unittest.tactics.combat;

import influence.DefaultInfluenceMap;
import influence.unittest.InfluenceTestMap;

import java.util.List;

import org.junit.Test;

import tactics.combat.CombatPositioning;
import tactics.combat.DefaultCombatPositioning;

import api.entities.Tile;
import api.entities.Unit;
import api.strategy.InfluenceMap;
import api.test.TestUnit;

import static org.junit.Assert.*;

public class CombatPositioningTest {
    @Test
    public void testPositioning_1vs2() {
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

    @Test
    public void testPositioning_1vs2_2() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "wo1oooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooo0oooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooooooowwwwwwwwwwwwwooooowooooooooow";
        sMap += "wooooo1oooooowoooooooooooowooooooooow";
        sMap += "woooooooooooowoooowooooooowooooooooow";
        sMap += "wooooooooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        InfluenceTestMap map = new InfluenceTestMap(37, sMap);

        final InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);
        List<Unit> enemyAnts = map.getUnits(1);
        List<Unit> myAnts = map.getUnits(0);

        final Unit myAnt = myAnts.get(0);

        CombatPositioning pos = new DefaultCombatPositioning(map, iMap, myAnts, enemyAnts, null);
        assertEquals(new Tile(4, 5), pos.getNextTile(myAnt));
    }

    @Test
    public void testPositioning_2vs1() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "wo0ooo0oooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooo1oooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooooooowwwwwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooooowoooooooooooowooooooooow";
        sMap += "woooooooooooowoooowooooooowooooooooow";
        sMap += "wooooooooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        InfluenceTestMap map = new InfluenceTestMap(37, sMap);

        final InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);
        List<Unit> enemyAnts = map.getUnits(1);
        List<Unit> myAnts = map.getUnits(0);

        final Unit ant1 = new TestUnit(0, new Tile(2, 2));
        final Unit ant2 = new TestUnit(0, new Tile(2, 6));

        CombatPositioning pos = new DefaultCombatPositioning(map, iMap, myAnts, enemyAnts, null);
        assertEquals(new Tile(2, 3), pos.getNextTile(ant1));
        assertEquals(new Tile(2, 5), pos.getNextTile(ant2));
    }

    @Test
    public void testPositioning_2vs1_preClustered() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woo0o0ooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooo1oooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooooooowwwwwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooooowoooooooooooowooooooooow";
        sMap += "woooooooooooowoooowooooooowooooooooow";
        sMap += "wooooooooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        InfluenceTestMap map = new InfluenceTestMap(37, sMap);

        final InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);
        List<Unit> enemyAnts = map.getUnits(1);
        List<Unit> myAnts = map.getUnits(0);

        final Unit ant1 = new TestUnit(0, new Tile(2, 3));
        final Unit ant2 = new TestUnit(0, new Tile(2, 5));

        CombatPositioning pos = new DefaultCombatPositioning(map, iMap, myAnts, enemyAnts, null);
        assertEquals(new Tile(3, 3), pos.getNextTile(ant1));
        assertEquals(new Tile(3, 5), pos.getNextTile(ant2));
    }
}
