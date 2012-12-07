package ants.search;

import java.util.List;

import search.BreadthFirstSearch;
import ants.entities.Ant;
import ants.state.Ants;
import api.entities.Tile;
import api.pathfinder.SearchableMap;

public class AntsBreadthFirstSearch extends BreadthFirstSearch {

    public AntsBreadthFirstSearch(SearchableMap map) {
        super(map);
    }

    public List<Tile> findEnemiesInRadius(Tile origin, int maxDistance2) {
        return findClosestTiles(origin, Integer.MAX_VALUE, Integer.MAX_VALUE, maxDistance2, new GoalTest() {
            @Override
            public boolean isGoal(Tile tile) {
                return Ants.getWorld().getIlk(tile).hasEnemyAnt();
            }
        });
    }

    public List<Tile> findFriendsInRadius(Tile origin, int maxDistance2) {
        return findClosestTiles(origin, Integer.MAX_VALUE, Integer.MAX_VALUE, maxDistance2, new GoalTest() {
            @Override
            public boolean isGoal(Tile tile) {
                return Ants.getWorld().getIlk(tile).hasFriendlyAnt();
            }
        });
    }

    public Ant findMyClosestUnemployedAnt(Tile tile, int maxNodes) {
        Tile found = findSingleClosestTile(tile, maxNodes, new GoalTest() {
            @Override
            public boolean isGoal(Tile tile) {
                final boolean hasFriendlyAnt = Ants.getWorld().getIlk(tile).hasFriendlyAnt();
                if (hasFriendlyAnt) {
                    if (Ants.getPopulation().getMyAntAt(tile, true) != null)
                        return true;
                    else {
                        LOGGER.debug("%s has friendly ant, but is not unemployed", tile);
                    }
                }
                return false;
            }
        });
        if (found != null)
            return Ants.getPopulation().getMyAntAt(found, true);
        return null;
    }

}
