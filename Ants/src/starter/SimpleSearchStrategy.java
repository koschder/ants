package starter;

import java.util.ArrayList;
import java.util.List;

import starter.Logger.LogCategory;

public class SimpleSearchStrategy implements SearchStrategy {
    private Ants ants;

    public SimpleSearchStrategy(Ants ants) {
        this.ants = ants;
    }

    @Override
    public List<Tile> bestPath(Tile from, Tile to) {
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

        List<Aim> directions = ants.getDirections(from, to);
        if (!(directions.size() == 1))
            Logger.error(LogCategory.PATHFINDING, "more than 1 direction from %s to %s", from, to);
        Aim aim = directions.get(0);

        if (!ants.getIlk(from, aim).isUnoccupied())
            return null;

        Tile t = from;
        while (!t.equals(to)) {
            if (!ants.getIlk(t, aim).isPassable())
                return null; // straight path is blocked
            t = ants.getTile(t, aim);
            path.add(t);
        }
        path.add(to);
        return path;
    }
}
