package ants.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tactics.combat.CombatPositioning;
import tactics.combat.DefaultCombatPositioning;
import ants.entities.Ant;
import ants.missions.PathMission;
import ants.search.AntsBreadthFirstSearch;
import ants.state.Ants;
import api.entities.Tile;
import api.entities.Unit;

public class RavTask extends BaseTask {

    @Override
    public Type getType() {
        return Type.COMBAT;
    }

    @Override
    protected void doPerform() {
        AntsBreadthFirstSearch bfs = new AntsBreadthFirstSearch(Ants.getWorld());
        // this odd loop structure is necessary to avoid a ConcurrentModificationException when marking an ant as
        // employed
        long start = System.currentTimeMillis();
        while (true) {
            // if there are unemployed ants for which we can't find a next move, this avoids an infinite loop
            if ((System.currentTimeMillis() - start) > 50)
                break;
            // break out when there are no more unemployed ants
            Collection<Ant> unemployed = Ants.getPopulation().getMyUnemployedAnts();
            if (!unemployed.iterator().hasNext())
                break;

            Ant ant = unemployed.iterator().next();
            List<Ant> unemployedFriends = bfs.findUnemployedAntsInRadius(ant.getTile(), Integer.MAX_VALUE, 25);
            unemployedFriends.add(ant);
            List<Unit> unemployedUnits = new ArrayList<Unit>(unemployedFriends);
            List<Tile> enemies = bfs.findEnemiesInRadius(ant.getTile(), 25 * 25);
            CombatPositioning pos = new DefaultCombatPositioning(null, Ants.getWorld(), Ants.getInfluenceMap(),
                    unemployedUnits, enemies);
            for (Ant soldier : unemployedFriends) {
                final Tile nextTile = pos.getNextTile(soldier);
                if (nextTile != null) {
                    List<Tile> path = new ArrayList<Tile>();
                    path.add(nextTile);
                    addPathMission(soldier, path);
                }
            }
        }
    }

    private void addPathMission(Ant soldier, List<Tile> path) {
        addMission(new PathMission(soldier, path) {

            @Override
            public Type getTaskType() {
                return Type.COMBAT;
            }

            @Override
            protected String isSpecificMissionValid() {
                return null;
            }
        });
    }

}
