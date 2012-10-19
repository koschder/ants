package api;

public interface InfluenceMap {
    public int getSafety(Tile tile);

    public int getTotalInfluence(Integer player);

    public int getTotalOpponentInfluence();

    public abstract void update(UnitMap map);
}
