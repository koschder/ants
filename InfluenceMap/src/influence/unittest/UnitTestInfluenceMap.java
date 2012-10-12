package influence.unittest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import api.AbstractWraparoundMap;
import api.Aim;
import api.InfluenceMap;
import api.SearchTarget;
import api.Tile;
import api.Unit;
import api.UnitMap;

public class UnitTestInfluenceMap extends AbstractWraparoundMap implements UnitMap {

    int[][] map;
    Map<Integer, List<Unit>> players = new HashMap<Integer, List<Unit>>();
    int water = 0;
    int land = 1;

    public UnitTestInfluenceMap(int x, String sMap) {
        map = new int[sMap.length() / x][x];

        for (int i = 0; i < sMap.length(); i++) {
            int row = i / x;
            int col = i % x;
            map[row][col] = sMap.charAt(i) == 'w' || sMap.charAt(i) == '%' ? water : land;
        }
    }

    public UnitTestInfluenceMap(List<String> sMapRows) {
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

    public List<SearchTarget> getSuccessor(SearchTarget target, boolean isNextMove) {
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

    public void printMap(List<Tile> path, int clusterSize) {

        for (String s : stringMap(path, clusterSize)) {
            System.out.println(s);
        }
    }

    public List<String> stringMap(List<Tile> path, int clusterSize) {
        List<String> returnString = new ArrayList<String>();
        for (int r = 0; r < getRows(); r++) {
            String verticalLine = "";
            String row = "";
            for (int c = 0; c < getCols(); c++) {
                verticalLine += "-";
                Tile t = new Tile(r, c);
                if (c % clusterSize == 0 && clusterSize != -1)
                    row += "|";
                if (path != null && path.contains(t)) {
                    row += "P";
                } else if (map[r][c] == land) {
                    row += " ";
                } else {
                    row += "W";
                }
            }
            if (r % clusterSize == 0 && clusterSize != -1) {
                returnString.add(verticalLine);
            }
            returnString.add(row);
        }

        returnString.add(String.format("# Amount of path tiles: %s", path == null ? 0 : path.size()));

        return returnString;
    }

    public void printMap(List<Tile> tiles) {
        printMap(tiles, -1);
    }

    public void saveHtmlMap(String fileName, InfluenceMap influenceMap) {

        StringBuilder sb = new StringBuilder();
        StringBuilder comment = new StringBuilder();
        sb.append("<html>\n<head>\n<link rel=\"stylesheet\" href=\"cssUnitTest.css\" />\n</head>\n<body>\n<table>\n");
        boolean horizLine = false;
        boolean vertLine = false;

        for (int i = 0; i < getRows(); i++) {

            StringBuilder sbRow = new StringBuilder();
            for (int j = 0; j < getCols(); j++) {

                String cssclass = "";
                if (vertLine)
                    cssclass += "vertBorder ";
                if (horizLine)
                    cssclass += "horzBorder ";

                String background = isPassable(new Tile(i, j)) ? "land" : "water";
                cssclass += background;

                String dotPath = "http://upload.wikimedia.org/wikipedia/commons/e/e0/Red_Dot.gif";

                String content = influenceMap.getSafety(new Tile(i, j)) + "";
                sbRow.append(String.format("<td class=\"%s\">%s</td>", cssclass, content));

            }
            sb.append(String.format("<tr>%s</tr>\n", sbRow.toString()));
        }

        sb.append("</table>\nComments:\n" + comment.toString() + "\n</body>\n</html>\n");

        try {
            String fName = "logs/" + fileName + ".html";
            Writer output = null;
            String text = sb.toString();
            File file = new File(fName);
            output = new BufferedWriter(new FileWriter(file));
            output.write(text);
            output.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
