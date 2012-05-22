package ants.search;

import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import ants.entities.Aim;
import ants.entities.SearchTarget;
import ants.entities.Tile;
import ants.state.Ants;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class SimpleSearchStrategy implements SearchStrategy {

    @Override
    public List<Tile> bestPath(SearchTarget areaFrom, SearchTarget areaTo) {

        if (!(areaFrom instanceof Tile && areaTo instanceof Tile))
            throw new RuntimeException("SimpleSearchStrategy not implmented for class" + areaTo.getClass());

        Tile from = (Tile) areaFrom;
        Tile to = (Tile) areaTo;

        if (from.getCol() == to.getCol() || from.getRow() == to.getRow())
            return getStraightPath(from, to);
        List<Tile> path = getSimpleViaPath(from, to, new Tile(from.getRow(), to.getCol()));
        if (path != null)
            return path;
        return getSimpleViaPath(from, to, new Tile(to.getRow(), from.getCol()));

    }

    private List<Tile> getSimpleViaPath(Tile from, Tile to, Tile via) {
        List<Tile> path = new ArrayList<Tile>();
        Logger.debug(LogCategory.PATHFINDING, "calling simpleViaPath with from %s to %s via %s", from, to, via);

        List<Tile> firstLeg = getStraightPath(from, via);
        if (firstLeg == null)
            return null;
        List<Tile> secondLeg = getStraightPath(via, to);
        if (secondLeg == null)
            return null;

        path.addAll(firstLeg);
        path.addAll(secondLeg);
        return path;
    }

    private List<Tile> getStraightPath(Tile from, Tile to) {
        List<Tile> path = new ArrayList<Tile>();

        List<Aim> directions = Ants.getWorld().getDirections(from, to);
        if (!(directions.size() == 1))
            Logger.error(LogCategory.PATHFINDING, "more than 1 direction from %s to %s", from, to);
        Aim aim = directions.get(0);

        if (!Ants.getWorld().getIlk(from, aim).isUnoccupied())
            return null;

        Tile t = from;
        while (!t.equals(to)) {
            if (!Ants.getWorld().getIlk(t, aim).isPassable())
                return null; // straight path is blocked
            t = Ants.getWorld().getTile(t, aim);
            path.add(t);
        }
        path.add(to);
        return path;
    }

    @Override
    public void setMaxCost(int i) {
        // no maxcost for this SearchStratey
        throw new RuntimeErrorException(null, "this function is not implemented yet");
    }

    @Override
    public void setSearchSpace(Tile p1, Tile p2) {
        throw new RuntimeErrorException(null, "this function is not implemented yet");
    }
}
