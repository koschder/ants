package ants.missions;

import java.util.ArrayList;
import java.util.List;

import pathfinder.PathFinder.Strategy;
import ants.entities.Ant;
import ants.state.Ants;
import api.entities.Tile;

/***
 * this mission is implemented to follow a path defined while creating the class
 * 
 * @author kases1, kustl1
 * 
 */
public abstract class PathMission extends BaseMission {

    public List<Tile> path = new ArrayList<Tile>();

    public PathMission(Ant ant, List<Tile> path) {
        super(ant);
        setPath(path);
    }

    private void setPath(List<Tile> path) {
        if (getAnt() != null && path.size() > 0 && path != null && path.get(0).equals(getAnt().getTile()))
            path.remove(0);
        this.path = path;

    }

    protected Ant getAnt() {
        return ants.size() > 0 ? ants.get(0) : null;
    }

    /***
     * 
     * @return the whole path as a string.
     */
    public String getPathString() {
        if (path == null)
            return "path is null";
        return path.toString();
    }

    @Override
    public boolean isComplete() {
        return path == null || path.isEmpty();
    }

    @Override
    public void execute() {
        if (!moveToNextTile())
            abandonMission();
    }

    /***
     * puts the order in the order list where the ant has to go and remove this path piece.
     * 
     * @return true if order is put successful.
     */
    protected boolean moveToNextTile() {
        if (path == null)
            return false;

        Tile nextStep = path.remove(0);
        // Aim aim = ant.getTile().directionTo(nextStep);
        // Aim aim = ant.getTile().directionTo(nextStep);
        if (putMissionOrder(getAnt(), nextStep)) {
            return true;
        }
        return false;
    }

    @Override
    protected String getVisualizeInfos() {
        return super.getVisualizeInfos() + "<br/>Path: " + getPathString();
    }

    /***
     * Abort mission if something disturbs the mission
     * 
     * @param ant
     *            to check
     * @param food
     *            is food nearby
     * @param enemyAnts
     *            is nearby
     * @param enemyHill
     *            is nearby
     * @return
     */
    protected String abortMission(Ant ant, boolean checkFood, boolean checkEnemyAnts, boolean checkEnemyHill) {
        final boolean foodNearby = Ants.getWorld().isFoodNearby(ant.getTile()) && checkFood;
        List<Ant> enemy = ant.getEnemiesInRadius(Ants.getWorld().getViewRadius2(), false);
        final boolean enemyIsMayor = enemy.size() > getAnts().size() && checkEnemyAnts;
        boolean enemyHillNearby = checkEnemyHill;
        if (enemyHillNearby) {
            enemyHillNearby = false;
            int maxDistanceOfEnemyHill = 10;
            for (Tile enemyHill : Ants.getWorld().getEnemyHills()) {
                if (Ants.getWorld().manhattanDistance(ant.getTile(), enemyHill) < maxDistanceOfEnemyHill) {
                    List<Tile> path = Ants.getPathFinder().search(Strategy.AStar, ant.getTile(), enemyHill,
                            maxDistanceOfEnemyHill);
                    if (path != null) {
                        enemyHillNearby = true;
                        break;
                    }
                }
            }
        }

        return (foodNearby ? "food," : "") + (enemyIsMayor ? "enemy," : "") + (enemyHillNearby ? "enemyHill," : "");
    }
}
