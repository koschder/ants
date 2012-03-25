package starter;

import java.util.List;

public interface SearchStrategy {
    public List<Tile> bestPath(Tile from, Tile to);
}
