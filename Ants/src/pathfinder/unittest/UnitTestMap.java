package pathfinder.unittest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import ants.entities.Ilk;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

import pathfinder.entities.Aim;
import pathfinder.entities.SearchTarget;
import pathfinder.entities.SearchableMap;
import pathfinder.entities.Tile;

public class UnitTestMap extends SearchableMap {

    int[][] map;

    int water = 0;
    int land = 1;

    public UnitTestMap(int x, String sMap) {
        map = new int[sMap.length() / x][x];

        for (int i = 0; i < sMap.length(); i++) {
            int row = i / x;
            int col = i % x;
            map[row][col] = sMap.charAt(i) == 'w' || sMap.charAt(i) == '%' ? water : land;
        }
    }
    
    
    public UnitTestMap(List<String> sMapRows) {
        int mapCols = sMapRows.get(0).length();
    
        map = new int[sMapRows.size()][mapCols];

        for (int i = 0; i < sMapRows.size(); i++) {
            for (int j = 0; j < mapCols; j++) {
            char charIt = sMapRows.get(i).charAt(j);
            map[i][j] = charIt == 'w' || charIt == '%' ? water : land;
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

    public boolean isPassable(SearchTarget tile) {
        return map[tile.getTargetTile().getRow()][tile.getTargetTile().getCol()] == land;
    }

    public boolean isVisible(SearchTarget tile) {
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
        
        returnString.add(String.format("# Amount of path tiles: %s",path == null ? 0 : path.size()));
        
        return returnString;
    }

    public void printMap(List<Tile> tiles) {
        printMap(tiles, -1);
    }

    public void saveHtmlMap(String fileName, List<Tile> tiles, int clusterSize) {

        StringBuilder sb = new StringBuilder();
        StringBuilder comment = new StringBuilder();
        sb.append("<html>\n<head>\n<link rel=\"stylesheet\" href=\"cssUnitTest.css\" />\n</head>\n<body>\n<table>\n");
        boolean horizLine = false;
        boolean vertLine = false;
        
        for (String s : stringMap(tiles, clusterSize)) {
        if(s.startsWith("#")){
            comment.append("<br/>"+s);
            continue;           
        }
            StringBuilder sbRow = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '-') {
                    horizLine = true;
                    break;
                }

                if (s.charAt(i) == '|') {
                    vertLine = true;
                    continue;
                }

                String cssclass = "";
                if (vertLine)
                    cssclass += "vertBorder ";
                if (horizLine)
                    cssclass += "horzBorder ";

                String background = s.charAt(i) == 'W' ? "water" : "land";
                cssclass += background;
                
                String dotPath = "http://upload.wikimedia.org/wikipedia/commons/e/e0/Red_Dot.gif";
                
                String content = s.charAt(i) == 'P' ? String.format("<img src=\"%s\" class\"poi\">",dotPath) : "&nbsp;";
                sbRow.append(String.format("<td class=\"%s\">%s</td>", cssclass, content));
                vertLine = false;
            }
            if (sbRow.length() > 0){
                sb.append(String.format("<tr>%s</tr>\n", sbRow.toString()));
            horizLine = false;
            }
        }

        sb.append("</table>\nComments:\n"+comment.toString()+"\n</body>\n</html>\n");

        try {
            String fName = "logs/"+fileName+".html";
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

}
