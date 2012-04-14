package ants.entities;

import ants.state.Ants;

/**
 * Represents a route from one tile to another.
 */
public class Route implements Comparable<Route> {
    private Ant ant;

    private final Tile start;

    private final Tile end;

    private final int distance;

    public Route(Tile start, Tile end, int distance, Ant ant) {
        this.start = start;
        this.end = end;
        this.distance = distance;
        this.ant = ant;
    }

    public Tile getStart() {
        return start;
    }

    public Tile getEnd() {
        return end;
    }

    public int getDistance() {
        return distance;
    }

    public Ant getAnt() {
        return ant;
    }

    @Override
    public int compareTo(Route route) {
        return distance - route.distance;
    }

    @Override
    public int hashCode() {
        return start.hashCode() * Ants.MAX_MAP_SIZE * Ants.MAX_MAP_SIZE + end.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Route) {
            Route route = (Route) o;
            result = start.equals(route.start) && end.equals(route.end);
        }
        return result;
    }
}
