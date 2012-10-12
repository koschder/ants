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

    public DefaultInfluenceMap(UnitMap map, int viewRadius2, int attackRadius2) {
        this.viewRadius2 = viewRadius2;
        this.attackRadius2 = attackRadius2;
        influence = new HashMap<Integer, int[][]>();
        for (Integer player : map.getPlayers()) {
            final int[][] playerInfluence = new int[map.getRows()][map.getCols()];
            influence.put(player, playerInfluence);
            for (Unit unit : map.getUnits(player)) {
                final Tile tile = unit.getTile();
                playerInfluence[tile.getRow()][tile.getCol()] = MAX_INFLUENCE;
                int mx = (int) Math.sqrt(this.viewRadius2);
                for (int row = -mx; row <= mx; ++row) {
                    for (int col = -mx; col <= mx; ++col) {
                        int d = row * row + col * col;
                        if (d <= this.viewRadius2) {
                            Tile t = map.getTile(tile, new Tile(row, col));
                            if (t.equals(tile))
                                continue;
                            if (d <= this.attackRadius2)
                                playerInfluence[tile.getRow()][tile.getCol()] += 50;
                            else
                                playerInfluence[tile.getRow()][tile.getCol()] += 10;
                        }
                    }
                }
            }
        }
    }

    public void update(UnitMap map) {

    }

}
