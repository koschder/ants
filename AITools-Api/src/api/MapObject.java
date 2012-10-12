package api;

import java.util.List;

public class MapObject {

    private List<Tile> tiles;
    private String desc;

    public MapObject(List<Tile> tiles, String desc) {
        this.setTiles(tiles);
        this.setDesc(desc);
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    private void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public String getDesc() {
        return desc;
    }

    private void setDesc(String desc) {
        this.desc = desc;
    }
}
