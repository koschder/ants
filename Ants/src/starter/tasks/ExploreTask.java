package starter.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import starter.Ant;
import starter.Route;
import starter.Tile;

public class ExploreTask extends BaseTask {

    private Set<Tile> unseenTiles;
    //max distance to follow
    private int MAXDISTANCE = 60;

    @Override
    public void perform() {
        if (unseenTiles == null) {
            unseenTiles = new HashSet<Tile>();
            for (int row = 0; row < ants.getRows(); row++) {
                for (int col = 0; col < ants.getCols(); col++) {
                    unseenTiles.add(new Tile(row, col));
                }
            }
        }
        // remove any tiles that can be seen, run each turn
        for (Iterator<Tile> locIter = unseenTiles.iterator(); locIter.hasNext();) {
            Tile next = locIter.next();
            if (ants.isVisible(next)) {
                locIter.remove();
            }
        }
        for (Ant ant : ants.getMyAnts()) {
            final Tile antLoc = ant.getTile();
            if (!orders.containsValue(antLoc)) {
                List<Route> unseenRoutes = new ArrayList<Route>();
                for (Tile unseenLoc : unseenTiles) {
                    int distance = ants.getDistance(antLoc, unseenLoc);
                    if(MAXDISTANCE< distance)
                        continue;
                    Route route = new Route(antLoc, unseenLoc, distance);
                    unseenRoutes.add(route);
                }
                Collections.sort(unseenRoutes);
                for (Route route : unseenRoutes) {
                    if (doMoveLocation(route.getStart(), route.getEnd())) {
                        break;
                    }
                }
            }
        }
    }

}