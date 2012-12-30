package api.test;

import api.entities.Tile;
import api.entities.Unit;

/**
 * TestUnit are only used for UnitTests
 * 
 * @author kases1, kustl1
 * 
 */
public class TestUnit implements Unit {
    private int player;
    private Tile tile;

    public TestUnit(int player, Tile tile) {
        this.player = player;
        this.tile = tile;
    }

    @Override
    public boolean isMine() {
        return player == 0;
    }

    @Override
    public int getPlayer() {
        return player;
    }

    @Override
    public Tile getTile() {
        return tile;
    }

    @Override
    public int hashCode() {
        return tile.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TestUnit other = (TestUnit) obj;

        return other.getTile().equals(getTile());
    }

}
