package influence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import logging.Logger;
import logging.LoggerFactory;
import search.BreadthFirstSearch;
import api.entities.Tile;
import api.entities.Unit;
import api.map.UnitMap;
import api.search.PathPiece;
import api.strategy.InfluenceMap;
import api.strategy.SearchableUnitMap;
import api.test.MapOutput;
import api.test.PixelDecorator;

/**
 * Default {@link InfluenceMap} implementation. This assumes all units have the same influence and calculates the units'
 * exerted influence proportionally to the distance.
 * 
 * @author kases1, kustl1
 * 
 */
public class DefaultInfluenceMap implements InfluenceMap {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.INFLUENCE);
    private static final int MAX_INFLUENCE = 100;
    private static final int ATTACKRADIUSINFLUENCE = 50;
    private static final int EXTENDEDATTACKRADIUSINFLUENCE = 30;
    private static final int VIEWRADIUSINFLUENCE = 10;
    private final Map<Integer, int[][]> influence;
    private final int viewRadius2, attackRadius2;
    private double decay = 0.1;

    @Override
    public int getSafety(Tile tile) {
        int myInfluence = 0;
        int enemyInfluence = 0;
        for (Entry<Integer, int[][]> entry : influence.entrySet()) {
            if (entry.getKey().equals(0))
                myInfluence = entry.getValue()[tile.getRow()][tile.getCol()];
            else
                enemyInfluence += entry.getValue()[tile.getRow()][tile.getCol()];
        }
        return myInfluence - enemyInfluence;
    }

    @Override
    public int getTotalInfluence(Integer player) {
        int totalInfluence = 0;
        final int[][] playerInfluence = influence.get(player);
        for (int i = 0; i < playerInfluence.length; i++) {
            for (int j = 0; j < playerInfluence[i].length; j++) {
                totalInfluence += playerInfluence[i][j];
            }
        }
        return totalInfluence;
    }

    @Override
    public int getTotalOpponentInfluence() {
        int totalInfluence = 0;
        for (Integer player : influence.keySet()) {
            if (player != 0)
                totalInfluence += getTotalInfluence(player);
        }
        return totalInfluence;
    }

    /**
     * Default constructor
     * 
     * @param map
     * @param viewRadius2
     * @param attackRadius2
     */
    public DefaultInfluenceMap(SearchableUnitMap map, int viewRadius2, int attackRadius2) {
        this.viewRadius2 = viewRadius2;
        this.attackRadius2 = attackRadius2;
        influence = new HashMap<Integer, int[][]>();
        for (Integer player : map.getPlayers()) {
            final int[][] playerInfluence = new int[map.getRows()][map.getCols()];
            influence.put(player, playerInfluence);
            updateInfluence(map, player, 0);
        }
    }

    private void updateInfluence(SearchableUnitMap map, Integer player, double decay) {
        decayInfluence(player, decay);
        BreadthFirstSearch bfs = new BreadthFirstSearch(map);
        for (Unit unit : map.getUnits(player)) {
            final Tile tile = unit.getTile();
            updateTileInfluence(tile, player, MAX_INFLUENCE, decay);
            List<Tile> tilesInAttackRadius = bfs.floodFill(tile, attackRadius2);
            List<Tile> tilesInExpandedAttackRadius = bfs.floodFill(tile, attackRadius2 * 2);
            List<Tile> tilesInViewRadius = bfs.floodFill(tile, viewRadius2);
            LOGGER.debug("Calculating influence for unit %s; attackRadius: %s, viewRadius: %s", unit,
                    tilesInAttackRadius, tilesInViewRadius);
            for (Tile t : tilesInViewRadius) {
                if (tilesInAttackRadius.contains(t)) {
                    updateTileInfluence(t, player, ATTACKRADIUSINFLUENCE, decay);
                } else if (tilesInExpandedAttackRadius.contains(t)) {
                    updateTileInfluence(t, player, EXTENDEDATTACKRADIUSINFLUENCE, decay);
                } else {
                    updateTileInfluence(t, player, VIEWRADIUSINFLUENCE, decay);
                }
            }
        }
    }

    @Override
    public void update(SearchableUnitMap map) {
        for (Integer player : map.getPlayers()) {
            if (!influence.containsKey(player))
                influence.put(player, new int[map.getRows()][map.getCols()]);
            updateInfluence(map, player, decay);
        }
    }

    private void decayInfluence(Integer player, double decay) {
        final int[][] playerInfluence = influence.get(player);
        for (int i = 0; i < playerInfluence.length; i++) {
            for (int j = 0; j < playerInfluence[i].length; j++) {
                playerInfluence[i][j] = ((int) Math.floor(playerInfluence[i][j] * decay));
            }
        }
    }

    private void updateTileInfluence(Tile tile, Integer player, int newInfluence, double decay) {
        influence.get(player)[tile.getRow()][tile.getCol()] += (int) Math.ceil(newInfluence * (1 - decay));
    }

    private int maxPathCost = 5;
    private int defaultPathCost = 1;

    @Override
    public int getPathCosts(PathPiece dest) {
        int maxInfluence = 200; // cost spectrum -1000 to 1000

        int costs = 0;
        for (Tile t : dest.getPath()) {
            // prohibit negative values (if safety is negative) (maxCost + getSafety(t))
            // reverse safety, negative safety means the path cost must rise
            int safteyCost = (maxInfluence + getSafety(t) * -1) * maxPathCost / (maxInfluence * 2);
            costs += defaultPathCost + safteyCost;
        }
        return costs;
    }

    @Override
    public int getInfluence(Integer player, Tile tile) {
        final int[][] playerInfluence = influence.get(player);
        if (playerInfluence == null)
            return 0;
        return playerInfluence[tile.getRow()][tile.getCol()];
    }

    /**
     * Prints the influence map to an HTML file for debugging purposes
     * 
     * @param turn
     * @param world
     * @param myUnits
     * @param enemyUnits
     */
    public void printDebugMap(int turn, UnitMap world, List<Tile> myUnits, List<Tile> enemyUnits) {
        MapOutput output = new MapOutput();
        output.setMap(world);
        output.addObject(myUnits, "myUnits");
        output.addObject(enemyUnits, "enemyUnits");

        output.saveHtmlMap("influence/" + turn, new PixelDecorator() {
            @Override
            public String getLabel(Tile tile) {
                StringBuilder label = new StringBuilder();
                for (int player : influence.keySet()) {
                    label.append(getInfluence(player, tile));
                    label.append("/");
                }
                return label.toString();
            }
        });
    }

}
