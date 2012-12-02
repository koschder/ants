package influence.unittest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import api.entities.Aim;
import api.entities.Tile;
import api.entities.Unit;
import api.map.AbstractWraparoundMap;
import api.map.UnitMap;
import api.pathfinder.SearchTarget;

public class UnitTestInfluenceMap extends AbstractWraparoundMap implements UnitMap {

    int[][] map;
    Map<Integer, List<Unit>> players = new HashMap<Integer, List<Unit>>();
    int water = 0;
    int land = 1;

    public UnitTestInfluenceMap(int x, String sMap) {

        List<String> sMapRows = new ArrayList<String>();

        while (sMap.length() >= x) {
            sMapRows.add(sMap.substring(0, x));
            sMap = sMap.substring(x);
        }
        init(sMapRows);
    }

    public UnitTestInfluenceMap(List<String> sMapRows) {
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

                    players.get(p).add(new Unit() {

                        @Override
                        public boolean isMine() {
                            return p == 0;
                        }

                        @Override
                        public Tile getTile() {

                            return new Tile(row, col);
                        }

                        @Override
                        public int getPlayer() {
                            return p;
                        }
                    });

                }

            }
        }
    }

    public UnitTestInfluenceMap(int x, int y) {
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

    public List<SearchTarget> getSuccessors(SearchTarget target, boolean isNextMove) {
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
    public Collection<Unit> getUnits(int player) {
        return players.get(player);
    }

    @Override
    public Set<Integer> getPlayers() {
        return players.keySet();
    }
}
