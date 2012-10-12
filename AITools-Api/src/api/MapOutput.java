package api;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class MapOutput {

    private InfluenceMap influenceMap;

    private TileMap map;

    private int clusterSize = -1;

    private String comment = "";

    private List<MapObject> objects = new ArrayList<MapObject>();

    public void saveHtmlMap(String fileName) {

        if (map == null)
            throw new IllegalArgumentException("map must be defined");

        StringBuilder sb = new StringBuilder();
        sb.append("<html>\n<head>\n<link rel=\"stylesheet\" href=\"./../../res/css/cssUnitTest.css\" />\n</head>\n<body>\n<table>\n");
        sb.append("<h1>" + fileName + "</h1>");
        for (int i = 0; i < map.getRows(); i++) {

            StringBuilder sbRow = new StringBuilder();
            for (int j = 0; j < map.getCols(); j++) {

                String cssclass = "";
                if (isClusterline(i) || (clusterSize != -1 && map.getRows() <= i + 2))
                    cssclass += "horzBorder ";
                if (isClusterline(j) || (clusterSize != -1 && map.getCols() <= j + 2))
                    cssclass += "vertBorder ";

                String background = map.isPassable(new Tile(i, j)) ? "land" : "water";
                cssclass += background;

                String dots = getObjects(i, j);

                String content = "";
                if (influenceMap != null)
                    content = influenceMap.getSafety(new Tile(i, j)) + "";

                sbRow.append(String.format("<td class=\"%s\">%s%s</td>", cssclass, content, dots));

            }
            sb.append(String.format("<tr>%s</tr>\n", sbRow.toString()));
        }

        sb.append("</table>\n");
        sb.append("<h2>Comments</h2>" + comment.toString());
        sb.append(getObjectHistory());
        sb.append("\n</body>\n</html>\n");

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

    private String getObjects(int i, int j) {
        StringBuilder sb = new StringBuilder();
        Tile t = new Tile(i, j);
        for (MapObject o : objects) {
            if (o.getTiles().contains(t)) {
                int color = objects.indexOf(o);
                String dotPath = "./../../res/dot" + color + ".jpg";
                sb.append(String.format("<img src=\"%s\" class=\"poi\">", dotPath));
            }

        }
        return sb.toString();
    }

    private String getObjectHistory() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>Object History</h2>");
        for (MapObject o : objects) {
            int color = objects.indexOf(o);
            String dotPath = "./../../res/dot" + color + ".jpg";
            sb.append(String.format("<br/><img src=\"%s\" class=\"poi\"> %s", dotPath, o.getDesc()));
        }
        return sb.toString();
    }

    private boolean isClusterline(int i) {
        if (clusterSize == -1)
            return false;

        if (i % clusterSize == 0 || i % clusterSize == clusterSize - 1)
            return true;

        return false;
    }

    public void addObject(List<Tile> tiles, String desc) {

        objects.add(new MapObject(tiles, desc));

    }

    public void addAllUnits() {

        if (map instanceof UnitMap) {

            UnitMap m = (UnitMap) map;

            for (int i : m.getPlayers()) {
                List<Tile> tiles = new ArrayList<Tile>();
                for (Unit u : m.getUnits(i)) {
                    tiles.add(u.getTile());
                }
                objects.add(new MapObject(tiles, "Player " + i));
            }

        }
    }

    public void setInfluenceMap(InfluenceMap influenceMap) {
        this.influenceMap = influenceMap;
    }

    public void setMap(TileMap map) {
        this.map = map;
    }

    public void setClusterSize(int clusterSize) {
        this.clusterSize = clusterSize;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}