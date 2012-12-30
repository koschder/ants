package unittest.tactics.combat;

import influence.DefaultInfluenceMap;
import influence.unittest.InfluenceTestMap;

import java.util.List;

import org.junit.Test;

import tactics.combat.CombatPositioning;
import tactics.combat.DefendingCombatPositioning;
import api.entities.Tile;
import api.entities.Unit;
import api.strategy.InfluenceMap;
import api.test.TestUnit;

import static org.junit.Assert.*;

public class DefendingCombatPositioningTest {
    @Test
    public void testPositioning_noOpponent_1Unit() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woo0ooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooooooowwwwwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooooowoooooooooooowooooooooow";
        sMap += "woooooooooooowoooowooooooowooooooooow";
        sMap += "wooooooooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        InfluenceTestMap map = new InfluenceTestMap(37, sMap);

        final InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);
        List<Unit> enemyUnits = map.getUnits(1);
        List<Unit> myUnits = map.getUnits(0);

        final Unit myUnit = myUnits.get(0);

        CombatPositioning pos = new DefendingCombatPositioning(map, iMap, myUnits, enemyUnits, new Tile(1, 3));
        assertEquals(new Tile(2, 2), pos.getNextTile(myUnit));
    }

    @Test
    public void testPositioning_noOpponent_2Units() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "wo0oooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woo0ooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooooooowwwwwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooooowoooooooooooowooooooooow";
        sMap += "woooooooooooowoooowooooooowooooooooow";
        sMap += "wooooooooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        InfluenceTestMap map = new InfluenceTestMap(37, sMap);

        final InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);
        List<Unit> enemyUnits = map.getUnits(1);
        List<Unit> myUnits = map.getUnits(0);

        final Unit myUnit1 = new TestUnit(0, new Tile(1, 2));
        final Unit myUnit2 = new TestUnit(0, new Tile(2, 3));

        CombatPositioning pos = new DefendingCombatPositioning(map, iMap, myUnits, enemyUnits, new Tile(1, 3));
        assertEquals(new Tile(2, 2), pos.getNextTile(myUnit1));
        assertEquals(new Tile(2, 4), pos.getNextTile(myUnit2));
    }

    @Test
    public void testPositioning_1Opponent_2Units() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "wo0oooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woo0ooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wo1ooooowwwwwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooooowoooooooooooowooooooooow";
        sMap += "woooooooooooowoooowooooooowooooooooow";
        sMap += "wooooooooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        InfluenceTestMap map = new InfluenceTestMap(37, sMap);

        final InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);
        List<Unit> enemyUnits = map.getUnits(1);
        List<Unit> myUnits = map.getUnits(0);

        final Unit myUnit1 = new TestUnit(0, new Tile(1, 2));
        final Unit myUnit2 = new TestUnit(0, new Tile(2, 3));

        CombatPositioning pos = new DefendingCombatPositioning(map, iMap, myUnits, enemyUnits, new Tile(1, 3));
        assertEquals(new Tile(2, 2), pos.getNextTile(myUnit1));
        assertEquals(new Tile(2, 3), pos.getNextTile(myUnit2));
    }

    @Test
    public void testPositioning_opponents_from2sides() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woo1ooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wo0o0oooooowwwwwwwwwwooooowooooooooow";
        sMap += "wo0o0ooowwwwwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooooowoooooooooooowooooooooow";
        sMap += "woooooooooooowoooowooooooowooooooooow";
        sMap += "woo1oooooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        InfluenceTestMap map = new InfluenceTestMap(37, sMap);

        final InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);
        List<Unit> enemyUnits = map.getUnits(1);
        List<Unit> myUnits = map.getUnits(0);

        final Unit myUnit1 = new TestUnit(0, new Tile(4, 2));
        final Unit myUnit2 = new TestUnit(0, new Tile(4, 4));
        final Unit myUnit3 = new TestUnit(0, new Tile(5, 2));
        final Unit myUnit4 = new TestUnit(0, new Tile(5, 4));

        CombatPositioning pos = new DefendingCombatPositioning(map, iMap, myUnits, enemyUnits, new Tile(5, 3));
        assertEquals(new Tile(4, 2), pos.getNextTile(myUnit1));
        assertEquals(new Tile(4, 4), pos.getNextTile(myUnit2));
        assertEquals(new Tile(6, 2), pos.getNextTile(myUnit3));
        assertEquals(new Tile(6, 4), pos.getNextTile(myUnit4));
    }

    @Test
    public void testPositioning_opponents_freshlySpawnedUnit() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wo1oooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooooooowwwwwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooooowoooooooooooowooooooooow";
        sMap += "woo0ooooooooowoooowooooooowooooooooow";
        sMap += "woo0oooooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        InfluenceTestMap map = new InfluenceTestMap(37, sMap);

        final InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);
        List<Unit> enemyUnits = map.getUnits(1);
        List<Unit> myUnits = map.getUnits(0);

        final Unit myUnit1 = new TestUnit(0, new Tile(7, 3));
        final Unit myUnit2 = new TestUnit(0, new Tile(8, 3));

        CombatPositioning pos = new DefendingCombatPositioning(map, iMap, myUnits, enemyUnits, new Tile(8, 3));
        assertEquals(new Tile(7, 3), pos.getNextTile(myUnit1));
        assertEquals(new Tile(8, 2), pos.getNextTile(myUnit2));
    }
}
