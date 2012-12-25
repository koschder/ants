package api.test;

import api.entities.Tile;

/**
 * the PixelDecorator is used with MapOutput to print custom content on a map's tile.
 * 
 * @author kaeserst, kustl1
 * 
 */
public interface PixelDecorator {
    public String getLabel(Tile tile);
}
