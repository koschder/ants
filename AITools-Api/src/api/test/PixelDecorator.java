package api.test;

import api.entities.Tile;

/**
 * the PixelDecorator is used with MapOutput to print custom content on a map's tile.
 * 
 * @author kases1, kustl1
 * 
 */
public interface PixelDecorator {
    public String getLabel(Tile tile);
}
