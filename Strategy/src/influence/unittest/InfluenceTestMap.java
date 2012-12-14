package influence.unittest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import api.entities.Aim;
import api.entities.Tile;
import api.entities.Unit;
import api.map.AbstractWraparoundMap;
import api.pathfinder.SearchTarget;
import api.pathfinder.SearchableUnitMap;
import api.strategy.InfluenceMap;
import api.test.TestUnit;

public class InfluenceTestMap extends AbstractWraparoundMap implements SearchableUnitMap {

    int[][] map;
    Map<Integer, List<Unit>> players = new HashMap<Integer, List<Unit>>();
    int water = 0;
    int land = 1;

    public InfluenceTestMap(int x, String sMap) {
        super(x, x);
        List<String> sMapRows = new ArrayList<String>();

        while (sMap.length() >= x) {
            sMapRows.add(sMap.substring(0, x));
            sMap = sMap.substring(x);
        }
        init(sMapRows);
    }

    public InfluenceTestMap(List<String> sMapRows) {
        super(sMapRows.size(), sMapRows.size());
        init(sMapRows);
    }

    private void init(List<String> sMapRows) {
        int mapCols = sMapRows.get(0).length();

        map = new int[sMapRows.size()][mapCols];

        for (int i = 0; i < sMapRows.size(); i++) {
            for (int j = 0; j < mapCols; j++) {
                final int row = i;
                final int col = j;
                Character charIt = sMapRows.get(i).charAt(j);
                map[i][j] = charIt == 'w' || charIt == '%' ? water : land;

                if (Character.isDigit(charIt)) {
                    final int p = Character.getNumericValue(charIt);

                    if (!players.containsKey(p))
                        players.put(p, new ArrayList<Unit>());

                    players.get(p).add(new TestUnit(p, new Tile(row, col)));

                }

            }
        }
    }

    public InfluenceTestMap(int x, int y) {
        super(x, y);
        map = new int[x][y];
        for (int i = 0; i < x; i++)
            for (int j = 0; j < y; j++)
                map[i][j] = land;
    }

    public int getRows() {
        return map.length;
    }

    public int getCols() {
        return map[0].length;
    }

    public boolean isPassable(Tile tile) {
        return map[tile.getTargetTile().getRow()][tile.getTargetTile().getCol()] == land;
    }

    public boolean isVisible(Tile tile) {
        return true;
    }

    public List<SearchTarget> getSuccessorsForPathfinding(SearchTarget target, boolean isNextMove) {
        Tile state = target.getTargetTile();
        List<SearchTarget> list = new ArrayList<SearchTarget>();
        if (isPassable(getTile(state, Aim.NORTH)))
            list.add(getTile(state, Aim.NORTH));
        if (isPassable(getTile(state, Aim.SOUTH)))
            list.add(getTile(state, Aim.SOUTH));
        if (isPassable(getTile(state, Aim.WEST)))
            list.add(getTile(state, Aim.WEST));
        if (isPassable(getTile(state, Aim.EAST)))
            list.add(getTile(state, Aim.EAST));

        return list;
    }

    @Override
    public List<SearchTarget> getSuccessorsForSearch(SearchTarget currentEdge, boolean isNextMove) {
        return getSuccessorsForPathfinding(currentEdge, isNextMove);
    }

    @Override
    public List<Unit> getUnits(int player) {
        return players.get(player);
    }

    @Override
    public Set<Integer> getPlayers() {
        return players.keySet();
    }

    @Override
    public Tile getSafestNeighbour(Tile tile, InfluenceMap influenceMap) {
        Tile safestTile = null;
        int bestSafety = Integer.MIN_VALUE;
        for (Tile neighbour : get4Neighbours(tile)) {
            final int safety = influenceMap.getSafety(neighbour);
            if (safety > bestSafety) {
                safestTile = neighbour;
                bestSafety = safety;
            }
        }
        return safestTile;
    }
}
