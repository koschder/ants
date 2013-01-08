package ants.tactics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import tactics.minmax.Game;
import ants.state.Ants;
import ants.state.World;
import api.entities.Move;
import api.entities.Tile;
import api.entities.Unit;

/**
 * 
 * @author kases1, kustl1
 * @deprecated did not work as hoped, abandoned
 */
public class CombatSituation implements Game {
    private Map<Tile, CombatUnit> enemyAnts;
    private Map<Tile, CombatUnit> myAnts;
    private List<Move> moves;
    private boolean max = true;
    private boolean valid = true;

    public CombatSituation(List<Unit> enemyAnts, List<Unit> myAnts) {
        this.enemyAnts = new HashMap<Tile, CombatUnit>();
        for (Unit unit : enemyAnts) {
            this.enemyAnts.put(unit.getTile(), new CombatUnit(unit));
        }
        this.myAnts = new HashMap<Tile, CombatUnit>();
        for (Unit unit : myAnts) {
            this.myAnts.put(unit.getTile(), new CombatUnit(unit));
        }
    }

    public CombatSituation(Map<Tile, CombatUnit> enemyAnts, Map<Tile, CombatUnit> myAnts, List<Move> moves) {
        initAnts(enemyAnts, myAnts);

        this.moves = moves;
        Set<Tile> orders = new HashSet<Tile>();
        for (Move m : moves) {
            if (this.enemyAnts.containsKey(m.getTile())) {
                this.max = false;
                CombatUnit unit = this.enemyAnts.remove(m.getTile());
                Tile next = Ants.getWorld().getTile(m.getTile(), m.getDirection());
                unit.setTile(next);
                this.enemyAnts.put(next, unit);
                if (orders.contains(next)) {
                    valid = false;
                    return;
                }
                orders.add(next);
            } else if (this.myAnts.containsKey(m.getTile())) {
                this.max = true;
                CombatUnit unit = this.myAnts.remove(m.getTile());
                Tile next = Ants.getWorld().getTile(m.getTile(), m.getDirection());
                unit.setTile(next);
                this.myAnts.put(next, unit);
                if (orders.contains(next)) {
                    valid = false;
                    return;
                }
                orders.add(next);
            }
        }
    }

    private void initAnts(Map<Tile, CombatUnit> enemyAnts, Map<Tile, CombatUnit> myAnts) {
        this.enemyAnts = new HashMap<Tile, CombatUnit>();
        for (Entry<Tile, CombatUnit> entry : enemyAnts.entrySet()) {
            this.enemyAnts.put(entry.getKey(), new CombatUnit(entry.getValue()));
        }
        this.myAnts = new HashMap<Tile, CombatUnit>();
        for (Entry<Tile, CombatUnit> entry : myAnts.entrySet()) {
            this.myAnts.put(entry.getKey(), new CombatUnit(entry.getValue()));
        }
    }

    @Override
    public Game move(List<Move> moves) {
        return new CombatSituation(enemyAnts, myAnts, moves);
    }

    @Override
    public List<Move> getMoves() {
        return moves;
    }

    @Override
    public Iterator<Game> expand() {
        return new CombatSituationIterator(this);
    }

    @Override
    public int evalHeuristicValue() {
        Map<Tile, Set<Tile>> enemies = new HashMap<Tile, Set<Tile>>();
        final World world = Ants.getWorld();
        for (Tile myTile : myAnts.keySet()) {
            enemies.put(myTile, new HashSet<Tile>());
            for (Tile enemyTile : enemyAnts.keySet()) {
                enemies.put(enemyTile, new HashSet<Tile>());
                if (world.isInAttackZone(myTile, enemyTile)) {
                    enemies.get(myTile).add(enemyTile);
                    enemies.get(enemyTile).add(myTile);
                }

            }
        }
        int myDead = 0;
        int enemyDead = 0;
        for (Tile myTile : myAnts.keySet()) {
            for (Tile enemy : enemies.get(myTile)) {
                if (enemies.get(myTile).size() >= enemies.get(enemy).size())
                    myDead++;
            }
        }
        for (Tile enemy : enemyAnts.keySet()) {
            for (Tile myTile : enemies.get(enemy)) {
                if (enemies.get(enemy).size() >= enemies.get(myTile).size())
                    enemyDead++;
            }
        }
        return myDead - enemyDead;
    }

    public Map<Tile, CombatUnit> getMovingUnits() {
        return max ? myAnts : enemyAnts;
    }

    @Override
    public int getUtilityValue() {
        // no useful implementation
        return 0;
    }

    @Override
    public boolean isTerminal() {
        // no useful implementation
        return false;
    }

    @Override
    public boolean isMaxToMove() {
        return max;
    }

    public Map<Tile, CombatUnit> getEnemyAnts() {
        return enemyAnts;
    }

    public Map<Tile, CombatUnit> getMyAnts() {
        return myAnts;
    }

    @Override
    public String toString() {
        return moves.toString();
    }

    @Override
    public boolean isValid() {
        return valid;
    }
}
