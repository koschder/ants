package pathfinder.unittest;

import java.util.ArrayList;
import java.util.List;

import api.entities.Aim;
import api.entities.Tile;
import api.map.AbstractWraparoundMap;
import api.pathfinder.SearchTarget;

public class UnitTestMap extends AbstractWraparoundMap {

    int[][] map;

    int water = 0;
    int land = 1;
    int friendlyUnit = 2;

    public UnitTestMap(int x, String sMap) {
        map = new int[sMap.length() / x][x];

        for (int i = 0; i < sMap.length(); i++) {
            int row = i / x;
            int col = i % x;
            initCell(row, col, sMap.charAt(i));
        }
    }

    private void initCell(int row, int col, char type) {
        switch (type) {
        case 'w':
        case '%':
            map[row][col] = water;
            break;
        case 'x':
            map[row][col] = friendlyUnit;
            break;
        default:
            map[row][col] = land;
            break;
        }
    }

    public UnitTestMap(List<String> sMapRows) {
        int mapCols = sMapRows.get(0).length();

        map = new int[sMapRows.size()][mapCols];

        for (int i = 0; i < sMapRows.size(); i++) {
            for (int j = 0; j < mapCols; j++) {
                initCell(i, j, sMapRows.get(i).charAt(j));
            }
        }
    }

    public UnitTestMap(int x, int y) {
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
        return map[tile.getRow()][tile.getCol()] != water;
    }

    public boolean isFriendlyUnit(Tile tile) {
        return map[tile.getRow()][tile.getCol()] == friendlyUnit;
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

}
