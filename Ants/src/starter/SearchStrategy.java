package starter;

import java.util.List;

public interface SearchStrategy {
    /**
     * 
     * @param from
     * @param to
     * @return the path in a list or null if no path is found.
     */
    public List<Tile> bestPath(Tile from, Tile to);
}
