package search;

import java.util.ArrayList;
import java.util.List;

import api.entities.Aim;
import api.entities.Tile;

public class Barrier {

    private Aim a;
    private List<Tile> barrier;
    private List<Tile> barrierPlaceTiles;

    public Barrier(List<Tile> bar, Aim aimOfBarrier) {
        setBarrier(bar);
        setAimOfBarrier(aimOfBarrier);

        barrierPlaceTiles = new ArrayList<Tile>(barrier);

        if (barrier.size() > 1)
            barrierPlaceTiles.remove(0);
        if (barrier.size() > 2)
            barrierPlaceTiles.remove(barrierPlaceTiles.size() - 1);

        if (barrierPlaceTiles.size() > 3) {
            List<Tile> remove = new ArrayList<Tile>();
            for (int i = 1; i < barrierPlaceTiles.size() - 1; i = i + 2) {
                remove.add(barrierPlaceTiles.get(i));
            }
            barrierPlaceTiles.removeAll(remove);
        }
    }

    public Aim getAimOfBarrier() {
        return a;
    }

    public void setAimOfBarrier(Aim a) {
        this.a = a;
    }

    public List<Tile> getBarrier() {
        return new ArrayList<Tile>(barrier);
    }

    public List<Tile> getBarrierPlaceTiles() {
        return new ArrayList<Tile>(barrierPlaceTiles);
    }

    public void setBarrier(List<Tile> barrier) {
        this.barrier = barrier;
    }

}
