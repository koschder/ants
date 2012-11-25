package ants.missions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.PathFinder.Strategy;
import ants.LogCategory;
import ants.entities.Ant;
import ants.state.Ants;
import ants.tasks.Task.Type;
import ants.util.LiveInfo;
import api.entities.Tile;
import api.pathfinder.SearchTarget;

/***
 * This mission used for attacking the enemies hills
 * 
 * @author kases1,kustl1
 * 
 */
public class AttackHillMission extends BaseMission {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.ATTACK_HILLS);

    /***
     * the enemy hill.
     */
    Tile enemyHill;
    int attackSaftey = -40;
    int staySaftey = -70;
    int gatherAntsRadius = 20;

    public AttackHillMission(Tile hill) {
        enemyHill = hill;
        LOGGER.info("AttackHillMission created. EnemyHill is %s", enemyHill);
    }

    /***
     * mission is as long vaild, as long the enemy hill exists.
     */
    @Override
    protected String isSpecificMissionValid() {
        if (!Ants.getWorld().getEnemyHills().contains(enemyHill))
            return "Enemy hill " + enemyHill + " is no longer there";
        return null;
    }

    @Override
    public Type getTaskType() {
        return Type.ATTACK_HILLS;
    }

    public Tile getHill() {
        return enemyHill;
    }

    @Override
    protected boolean checkAnts() {
        return false;
    }

    @Override
    public boolean isComplete() {
        return !Ants.getWorld().getEnemyHills().contains(enemyHill);
    }

    @Override
    public void execute() {
        gatherAnts();
        moveAnts();

        LiveInfo.liveInfo(Ants.getAnts().getTurn(), enemyHill, "AttackHillMission attackers are: %s", ants);
    }

    private void gatherAnts() {
        Map<Ant, List<Tile>> newAnts = gatherAnts(enemyHill, 5, gatherAntsRadius);
        LOGGER.info("AttackHillMission:new ants gathered for hill %s amount %s Ants: %s", enemyHill, newAnts.size(),
                newAnts.keySet());

        for (Entry<Ant, List<Tile>> entry : newAnts.entrySet()) {
            Ant a = entry.getKey();
            a.setPath(entry.getValue());
            addAnt(a);
        }
    }

    private void moveAnts() {
        List<Ant> antsToRelease = new ArrayList<Ant>();
        for (Ant a : ants) {
            LOGGER.info("Move ant %s with path %s enemyhill %s", a, a.getPath(), enemyHill);
            String abortReason = checkEnviroment(a, false, false, false);
            if (abortReason.length() > 0) {
                // if food is nearby we get the food first.
                LOGGER.info("remove ant %s something interesting [%s] nearby", a, abortReason);
                antsToRelease.add(a);
            } else {
                if (!move(a)) {
                    antsToRelease.add(a);
                }
            }
        }
        LOGGER.info("Release ants %s of AttackHillMission %s", antsToRelease, enemyHill);
        removeAnts(antsToRelease);
    }

    private boolean move(Ant ant) {

        if (!recalculatePath(ant)) {
            LOGGER.info("Ant has a invalid path, and cannot be recalculated", ant);
            return false;
        }

        Tile nextStep = ant.getPath().get(0);
        int safetyNextTile = (Ants.getInfluenceMap().getSafety(nextStep));

        if (safetyNextTile > attackSaftey) {
            LOGGER.info("AttackHillMission: everything is saftey %s move ant %s forwards to EnemyHill is %s",
                    safetyNextTile, ant, enemyHill);
            if (putMissionOrder(ant, nextStep)) {
                ant.getPath().remove(0);
            } else {
                putMissionOrder(ant);
            }
            return true;
        }
        int currentSafety = (Ants.getInfluenceMap().getSafety(ant.getTile()));

        if (currentSafety > staySaftey) {
            // current position is safety enough
            LOGGER.info(
                    "AttackHillMission:current position is safety enough (%s) safteyNextTile (%s), do not move ant %s towards %s. Turns waited %s",
                    currentSafety, safetyNextTile, ant, enemyHill, ant.getTurnsWaited());
            putMissionOrder(ant);
        } else {
            // rückzuuuuuuuuuuuuuuuuuuug !!
            List<SearchTarget> list = Ants.getWorld().getSuccessor(ant.getTile(), true);

            boolean orderIssued = false;
            // go to the saftiest tile
            do {
                // LOGGER.info("rückzuuuuuuuuuuuuuuuuuuug list size" + list.size());
                if (list.size() > 0) {
                    int bestSafety = -99999;
                    SearchTarget safteyTile = null;
                    for (SearchTarget t : list) {
                        int safety = (Ants.getInfluenceMap().getSafety(ant.getTile()));
                        // LOGGER.info("compare safety %s vs %s", safety, bestSafety);
                        if (safety > bestSafety) {
                            safteyTile = t.getTargetTile();
                            bestSafety = safety;
                        }
                    }
                    list.remove(safteyTile);
                    if (putMissionOrder(ant, safteyTile.getTargetTile())) {
                        ant.getPath().add(0, ant.getTile());
                        orderIssued = true;
                        LOGGER.info(
                                "AttackHillMission:dangerous here! move ant %s backwards to %s. (Safety is: %s) enemyHill %s",
                                ant, safteyTile.getTargetTile(), bestSafety, enemyHill);
                    }
                } else {
                    putMissionOrder(ant);
                    orderIssued = true;
                }

            } while (!orderIssued);
        }
        return true;
    }

    private boolean recalculatePath(Ant ant) {
        int tileToCheck = Math.min(5, ant.getPath().size() - 1);
        // check if path doesn't lead throw water
        if (!Ants.getWorld().isPassable(ant.getPath().get(tileToCheck))) {
            List<Tile> path = Ants.getPathFinder().search(Strategy.AStar, ant.getTile(), enemyHill, 30);
            if (path == null || path.size() == 0) {
                return false;
            }
            LOGGER.info("AttackHillMission:recalculatePath path for ant %s path: %s.", ant, path);
            ant.setPath(path);
            return true;
        }
        return true;
    }
}
