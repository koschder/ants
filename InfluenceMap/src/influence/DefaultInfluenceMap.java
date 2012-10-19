package influence;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import api.InfluenceMap;
import api.Tile;
import api.Unit;
import api.UnitMap;

public class DefaultInfluenceMap implements InfluenceMap {
    private static final int MAX_INFLUENCE = 100;
    private final Map<Integer, int[][]> influence;
    private final int viewRadius2, attackRadius2;

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

    public DefaultInfluenceMap(UnitMap map, int viewRadius2, int attackRadius2) {
        this.viewRadius2 = viewRadius2;
        this.attackRadius2 = attackRadius2;
        influence = new HashMap<Integer, int[][]>();
        for (Integer player : map.getPlayers()) {
            final int[][] playerInfluence = new int[map.getRows()][map.getCols()];
            influence.put(player, playerInfluence);
            updateInfluence(map, player, 0);
        }
    }

    private void updateInfluence(UnitMap map, Integer player, double decay) {
        decayInfluence(player, decay);
        for (Unit unit : map.getUnits(player)) {
            final Tile tile = unit.getTile();
            updateTileInfluence(tile, player, MAX_INFLUENCE, decay);
            int mx = (int) Math.sqrt(this.viewRadius2);
            for (int row = -mx; row <= mx; ++row) {
                for (int col = -mx; col <= mx; ++col) {
                    int d = row * row + col * col;
                    if (d <= this.viewRadius2) {
                        Tile tRadius = map.getTile(tile, new Tile(row, col));
                        if (tRadius.equals(tile) || !map.isPassable(tRadius))
                            continue;
                        if (d <= this.attackRadius2)
                            updateTileInfluence(tRadius, player, 50, decay);
                        else
                            updateTileInfluence(tRadius, player, 10, decay);
                    }
                }
            }
        }
    }

    @Override
    public void update(UnitMap map) {
        for (Integer player : map.getPlayers()) {
            if (!influence.containsKey(player))
                influence.put(player, new int[map.getRows()][map.getCols()]);
            updateInfluence(map, player, 0.5);
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

}
