package api.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import api.entities.Tile;
import api.entities.Unit;
import api.map.MapObject;
import api.map.TileMap;
import api.map.UnitMap;

/**
 * this class is used in the unit test to visualize the test results
 * 
 * @author kaeserst, kustl1
 * 
 */
public class MapOutput {

    private TileMap map;

    private int clusterSize = -1;
    private String relativeBasePath = "./../../res/";
    private String comment = "";
    private String contentStore = "";

    private List<MapObject> objects = new ArrayList<MapObject>();

    /**
     * saves the map into a html file
     * 
     * @param fileName
     * @param decorator
     */
    public void saveHtmlMap(String fileName, PixelDecorator decorator) {
        if (map == null)
            throw new IllegalArgumentException("map must be defined");

        StringBuilder sb = new StringBuilder();
        if (fileName.contains("/"))
            relativeBasePath = "./../../../res/";
        sb.append("<html>\n<head>\n<link rel=\"stylesheet\" href=\"" + relativeBasePath
                + "css/cssUnitTest.css\" />\n</head>\n<body>\n");
        sb.append("<h1>" + fileName + "</h1>");
        sb.append(contentStore);
        String content = getContent(decorator);
        sb.append(content);
        sb.append("\n</body>\n</html>\n");

        try {
            File logdir = new File("logs");
            logdir.mkdirs();
            Writer output = null;
            String text = sb.toString();
            if (fileName.contains("/")) {
                File dir = new File(logdir, fileName.split("/")[0]);
                dir.mkdirs();
                // fileName = fileName.split("/")[1];
            }
            File file = new File(logdir, fileName + ".html");
            output = new BufferedWriter(new FileWriter(file));
            output.write(text);
            output.close();
            // System.out.println("Log file saved: " + file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getContent(PixelDecorator decorator) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>\n");
        for (int i = 0; i < map.getRows(); i++) {

            StringBuilder sbRow = new StringBuilder();
            for (int j = 0; j < map.getCols(); j++) {

                String cssclass = "";
                if (isClusterline(i, map.getRows()))
                    cssclass += "horzBorder ";
                if (isClusterline(j, map.getCols()))
                    cssclass += "vertBorder ";

                String background = map.isPassable(new Tile(i, j)) ? "land" : "water";
                cssclass += background;

                String dots = getObjects(i, j);

                String content = "";
                if (decorator != null)
                    content = decorator.getLabel(new Tile(i, j));

                sbRow.append(String.format("<td class=\"%s\">%s%s</td>", cssclass, content, dots));

            }
            sb.append(String.format("<tr>%s</tr>\n", sbRow.toString()));
        }

        sb.append("</table>\n");
        sb.append("<h2>Comments</h2>" + comment.toString());
        sb.append(getObjectHistory());
        sb.append("<br/><br/>");

        return sb.toString();
    }

    public void saveHtmlMap(String fileName) {
        saveHtmlMap(fileName, null);
    }

    private String getObjects(int i, int j) {
        StringBuilder sb = new StringBuilder();
        Tile t = new Tile(i, j);
        for (MapObject o : objects) {
            if (o.getTiles().contains(t)) {
                int color = objects.indexOf(o);
                String dotPath = relativeBasePath + "dot" + color + ".png";
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
            String dotPath = relativeBasePath + "dot" + color + ".png";
            sb.append(String.format("<br/><img src=\"%s\" class=\"poi\"> %s", dotPath, o.getDesc()));
        }
        return sb.toString();
    }

    private boolean isClusterline(int i, int maxSize) {
        if (clusterSize == -1)
            return false;

        if (i % clusterSize == 0 || i % clusterSize == clusterSize - 1)
            return true;

        return false;
    }

    /**
     * add object to the map
     * 
     * @param tiles
     * @param desc
     */
    public void addObject(List<Tile> tiles, String desc) {

        if (tiles == null || tiles.size() == 0)
            return;

        objects.add(new MapObject(tiles, desc));

    }

    /**
     * add all units of UnitMap to the OutputMap
     */
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

    public void setMap(TileMap map) {
        this.map = map;
    }

    public void setClusterSize(int clusterSize) {
        this.clusterSize = clusterSize;
    }

    public void addComment(String comment) {
        this.comment += "<br/>" + comment.replace(">", "&gt;").replace("<", "&lt;");
    }

    public void cleanUp(boolean store, PixelDecorator decorator) {
        if (store)
            contentStore += getContent(decorator);
        objects.clear();
        comment = "";
    }

}
