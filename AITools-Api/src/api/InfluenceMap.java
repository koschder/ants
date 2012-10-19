package api;


public interface InfluenceMap {
	public int getSafety(Tile tile);

    public abstract void update(UnitMap map);
}
