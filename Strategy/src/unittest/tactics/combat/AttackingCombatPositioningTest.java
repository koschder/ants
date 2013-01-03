package unittest.tactics.combat;

import influence.DefaultInfluenceMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import tactics.combat.AttackingCombatPositioning;
import tactics.combat.CombatPositioning;
import tactics.combat.DefaultCombatPositioning;
import unittest.influence.InfluenceTestMap;
import api.entities.Tile;
import api.entities.Unit;
import api.strategy.InfluenceMap;
import api.test.MapOutput;
import api.test.TestUnit;

import static org.junit.Assert.*;

public class AttackingCombatPositioningTest {
    @Test
    public void testPositioning_1vs2_targetOppositeDirection() {
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

        CombatPositioning pos = new AttackingCombatPositioning(map, iMap, myUnits, enemyUnits, new Tile(8, 4));
        assertEquals(new Tile(5, 4), pos.getNextTile(myUnit));
    }

    @Test
    public void testPositioning_1vs2_targetOppositeDirection_TargetObstructed() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woo1o1ooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooooooooowwwwwwwwwwwooooowooooooooow";
        sMap += "wooooooowwowwwwwwwwwwooooowooooooooow";
        sMap += "wooo0wwwwoooowoooooooooooowooooooooow";
        sMap += "wooooooowoooowoooowooooooowooooooooow";
        sMap += "wooooooooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        InfluenceTestMap map = new InfluenceTestMap(37, sMap);

        final InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);
        List<Unit> enemyUnits = map.getUnits(1);
        List<Unit> myUnits = map.getUnits(0);

        final Unit myUnit = myUnits.get(0);

        CombatPositioning pos = new AttackingCombatPositioning(map, iMap, myUnits, enemyUnits, new Tile(5, 10));
        assertEquals(new Tile(7, 4), pos.getNextTile(myUnit));
    }

    @Test
    public void testPositioning_2vs2_targetOppositeDirection_ConflictingPaths() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woo1o1ooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooooooooowwwwwwwwwwwooooowooooooooow";
        sMap += "wooooooowwowwwwwwwwwwooooowooooooooow";
        sMap += "woooowwww0o0owoooooooooooowooooooooow";
        sMap += "wooooooowoooowoooowooooooowooooooooow";
        sMap += "wooooooooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        InfluenceTestMap map = new InfluenceTestMap(37, sMap);

        final InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);
        List<Unit> enemyUnits = map.getUnits(1);
        List<Unit> myUnits = map.getUnits(0);

        final Unit myUnit1 = new TestUnit(0, new Tile(6, 9));
        final Unit myUnit2 = new TestUnit(0, new Tile(6, 11));

        CombatPositioning pos = new AttackingCombatPositioning(map, iMap, myUnits, enemyUnits, new Tile(5, 10));
        assertEquals(new Tile(6, 10), pos.getNextTile(myUnit1));
        assertEquals(new Tile(6, 11), pos.getNextTile(myUnit2));
    }

    @Test
    public void testPositioning_2vs1_targetBehindEnemy() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woo0oo0oooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooooooooowwwwwwwwwwwooooowooooooooow";
        sMap += "woo1oooowwowwwwwwwwwwooooowooooooooow";
        sMap += "woooowwwwoooowoooooooooooowooooooooow";
        sMap += "wooooooowoooowoooowooooooowooooooooow";
        sMap += "wooooooooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        InfluenceTestMap map = new InfluenceTestMap(37, sMap);

        final InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);
        List<Unit> enemyUnits = map.getUnits(1);
        List<Unit> myUnits = map.getUnits(0);

        Unit myUnit1 = new TestUnit(0, new Tile(2, 3));
        Unit myUnit2 = new TestUnit(0, new Tile(2, 6));

        CombatPositioning pos = new AttackingCombatPositioning(map, iMap, myUnits, enemyUnits, new Tile(6, 3));
        assertEquals(new Tile(2, 4), pos.getNextTile(myUnit1));
        assertEquals(new Tile(2, 5), pos.getNextTile(myUnit2));

        // next step
        sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "wooo0oooooowwwwwwwwwwooooooooooooooow";
        sMap += "wooooo0oooowwwwwwwwwwooooowooooooooow";
        sMap += "wooooooooowwwwwwwwwwwooooowooooooooow";
        sMap += "woo1oooowwowwwwwwwwwwooooowooooooooow";
        sMap += "woooowwwwoooowoooooooooooowooooooooow";
        sMap += "wooooooowoooowoooowooooooowooooooooow";
        sMap += "wooooooooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        map = new InfluenceTestMap(37, sMap);

        iMap.update(map);
        enemyUnits = map.getUnits(1);
        myUnits = map.getUnits(0);

        myUnit1 = new TestUnit(0, new Tile(2, 4));
        myUnit2 = new TestUnit(0, new Tile(3, 6));

        pos = new AttackingCombatPositioning(map, iMap, myUnits, enemyUnits, new Tile(6, 3));
        assertEquals(new Tile(3, 4), pos.getNextTile(myUnit1));
        assertEquals(new Tile(3, 5), pos.getNextTile(myUnit2));

    }

    @Test
    public void testPositioning_swarm() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooo0ooooowwwwwwwwwwooooooooooooooow";
        sMap += "woo00000ooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "w1oooooooowwwwwwwwwwwooooowooooooooow";
        sMap += "wooo1oooooowwwwwwwwwwooooowooooooooow";
        sMap += "wo1ooooooooowoooooooooooowooooooooow";
        sMap += "wooooooowoooowoooowooooooowooooooooow";
        sMap += "wooooooooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        InfluenceTestMap map = new InfluenceTestMap(37, sMap);

        final InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);
        List<Unit> enemyUnits = map.getUnits(1);
        List<Unit> myUnits = map.getUnits(0);

        MapOutput put = new MapOutput();
        put.addObject(getTiles(myUnits), "MyUnits");
        put.addObject(getTiles(enemyUnits), "enemyUnits");
        put.setMap(map);
        put.cleanUp(true, null);

        for (int i = 0; i < 4; i++) {

            DefaultCombatPositioning pos = new AttackingCombatPositioning(map, iMap, myUnits, enemyUnits,
                    new Tile(8, 4));

            List<Tile> myNewUnits = new ArrayList<Tile>();
            List<Unit> myNewUnit = new ArrayList<Unit>();

            for (Unit u : myUnits) {
                myNewUnits.add(pos.getNextTile(u));
                myNewUnit.add(new TestUnit(0, pos.getNextTile(u)));
            }
            put.addObject(myNewUnits, "myNewUnits_" + i);
            put.addObject(getTiles(enemyUnits), "enemyUnits");
            put.addObject(pos.getFormationTiles(), "formationTiles");
            put.addObject(Arrays.asList(pos.getEnemyClusterCenter()), "EnemyClusterCenter");
            put.addObject(Arrays.asList(pos.getClusterCenter()), "MyClusterCenter");
            myUnits.clear();
            myUnits.addAll(myNewUnit);
            put.addComment(pos.getLog());
            put.cleanUp(true, null);
        }

        put.saveHtmlMap("testPositioning_swarm");

    }

    private static List<Tile> getTiles(List<Unit> units) {
        List<Tile> tiles = new ArrayList<Tile>();
        for (Unit unit : units) {
            tiles.add(unit.getTile());
        }
        return tiles;
    }

}
