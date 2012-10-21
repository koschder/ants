package api.entities;


public interface Unit {

    public abstract boolean isMine();

    public abstract int getPlayer();

    public abstract Tile getTile();

}