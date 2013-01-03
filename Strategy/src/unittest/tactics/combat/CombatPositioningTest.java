package unittest.tactics.combat;

import influence.DefaultInfluenceMap;

import java.util.List;

import org.junit.Test;

import tactics.combat.CombatPositioning;
import tactics.combat.DefaultCombatPositioning;
import unittest.influence.InfluenceTestMap;
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
        List<Unit> enemyUnits = map.getUnits(1);
        List<Unit> myUnits = map.getUnits(0);

        final Unit myUnit = myUnits.get(0);

        CombatPositioning pos = new DefaultCombatPositioning(map, iMap, myUnits, enemyUnits, null);
        assertEquals(new Tile(5, 4), pos.getNextTile(myUnit));
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
        List<Unit> enemyUnits = map.getUnits(1);
        List<Unit> myUnits = map.getUnits(0);

        final Unit myUnit = myUnits.get(0);

        CombatPositioning pos = new DefaultCombatPositioning(map, iMap, myUnits, enemyUnits, null);
        assertEquals(new Tile(4, 5), pos.getNextTile(myUnit));
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
        List<Unit> enemyUnits = map.getUnits(1);
        List<Unit> myUnits = map.getUnits(0);

        final Unit unit1 = new TestUnit(0, new Tile(2, 2));
        final Unit unit2 = new TestUnit(0, new Tile(2, 6));

        CombatPositioning pos = new DefaultCombatPositioning(map, iMap, myUnits, enemyUnits, null);
        assertEquals(new Tile(2, 3), pos.getNextTile(unit1));
        assertEquals(new Tile(2, 5), pos.getNextTile(unit2));
    }

    @Test
    public void testPositioning_2vs1_advance() {
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
        List<Unit> enemyUnits = map.getUnits(1);
        List<Unit> myUnits = map.getUnits(0);

        final Unit unit1 = new TestUnit(0, new Tile(2, 3));
        final Unit unit2 = new TestUnit(0, new Tile(2, 5));

        CombatPositioning pos = new DefaultCombatPositioning(map, iMap, myUnits, enemyUnits, null);
        assertEquals(new Tile(3, 3), pos.getNextTile(unit1));
        assertEquals(new Tile(3, 5), pos.getNextTile(unit2));
    }

    @Test
    public void testPositioning_2vs1_split() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woo0oo1ooo0wwwwwwwwwwooooooooooooooow";
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

        final Unit unit1 = new TestUnit(0, new Tile(2, 3));
        final Unit unit2 = new TestUnit(0, new Tile(2, 10));

        CombatPositioning pos = new DefaultCombatPositioning(map, iMap, myUnits, enemyUnits, null);
        assertEquals(new Tile(2, 4), pos.getNextTile(unit1));
        assertEquals(new Tile(2, 9), pos.getNextTile(unit2));
    }

    @Test
    public void testPositioning_3vs2() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "wooooo1ooo0wwwwwwwwwwooooooooooooooow";
        sMap += "wooooo1ooo0wwwwwwwwwwooooowooooooooow";
        sMap += "wooooooooo0wwwwwwwwwwooooowooooooooow";
        sMap += "wooooooowwwwwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooooowoooooooooooowooooooooow";
        sMap += "woooooooooooowoooowooooooowooooooooow";
        sMap += "wooooooooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        InfluenceTestMap map = new InfluenceTestMap(37, sMap);

        final InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);
        List<Unit> enemyUnits = map.getUnits(1);
        List<Unit> myUnits = map.getUnits(0);

        final Unit unit1 = new TestUnit(0, new Tile(2, 10));
        final Unit unit2 = new TestUnit(0, new Tile(3, 10));
        final Unit unit3 = new TestUnit(0, new Tile(4, 10));

        CombatPositioning pos = new DefaultCombatPositioning(map, iMap, myUnits, enemyUnits, null);
        assertEquals(new Tile(2, 9), pos.getNextTile(unit1));
        assertEquals(new Tile(2, 10), pos.getNextTile(unit2));
        assertEquals(new Tile(4, 9), pos.getNextTile(unit3));
    }
}
