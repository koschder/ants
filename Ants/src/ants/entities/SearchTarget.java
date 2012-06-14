package ants.entities;

import java.util.List;

public interface SearchTarget {

    List<SearchTarget> getSuccessors();

    boolean isSearchable(boolean bParentNode);

    int manhattanDistanceTo(SearchTarget dest);
    double beelineTo(SearchTarget dest);

    List<Tile> getPath();

    boolean isInSearchSpace(Tile searchSpace1, Tile searchSpace2);

    Tile getTargetTile();

    String toShortString();

    boolean isFinal(SearchTarget to);

    int getCost();

}
