package starter;

import java.util.Comparator;
import java.util.Map;

class DistanceComparator implements Comparator<Ant> {

    Map<Ant, Integer> base;

    public DistanceComparator(Map<Ant, Integer> base) {
        this.base = base;
    }

    public int compare(Ant a, Ant b) {
        return base.get(a).compareTo(base.get(b));
    }
}