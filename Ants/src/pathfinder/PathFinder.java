package pathfinder;

import java.util.List;

import pathfinder.entities.Clustering;
import pathfinder.entities.Clustering.ClusterType;
import pathfinder.entities.DirectedEdge;
import pathfinder.entities.SearchTarget;
import pathfinder.entities.SearchableMap;
import pathfinder.entities.Tile;
import pathfinder.search.AStarSearchStrategy;
import pathfinder.search.HPAStarSearchStrategy;
import pathfinder.search.SearchStrategy;
import pathfinder.search.SimpleSearchStrategy;

public class PathFinder {

    public enum WorldType {
        Pizza,
        Globe
    };

    public enum Strategy { Simple, AStar, HpaStar };
 
    private SearchableMap map;
    private Clustering cluster;

    public void setMap(SearchableMap m) {
        map = m;
    }

    public void InitClustering(WorldType t, int x, ClusterType clusterType){
        
        cluster = new Clustering(this, x, map.getRows(), map.getCols());
        cluster.setClusterType(clusterType);
        cluster.setWorldType(t);
        
    }
    

    public List<Tile> search(Strategy strat,SearchTarget start, SearchTarget end, int maxCost) {
       SearchStrategy strategy = getStartegy(strat);
       strategy.setMaxCost(maxCost);
       return strategy.search(start, end);
       }
    
    public List<Tile> search(Strategy strat,SearchTarget start, SearchTarget end, Tile searchSpace0, Tile searchSpace1, int maxCost) {
        SearchStrategy strategy = getStartegy(strat);
        strategy.setMaxCost(maxCost);
        strategy.setSearchSpace(searchSpace0, searchSpace1);
        return strategy.search(start, end);
    }

    private SearchStrategy getStartegy(Strategy strat) {
        if(strat == Strategy.Simple)
            return new SimpleSearchStrategy(this);
        if(strat == Strategy.AStar)
            return new AStarSearchStrategy(this);
        if(strat == Strategy.HpaStar)
            return new HPAStarSearchStrategy(this);
        
        throw new RuntimeException("Stragegy not implemented");
    }


    public void cluster() {
        cluster.perform();        
    }

    public SearchableMap getMap(){
        return this.map;
    }



    public Clustering getClustering() {
        return this.cluster;
    }

//
//    public List<SearchTarget> getSuccessor(SearchTarget state) {
//        if(state instanceof Tile)
//            return getMap().getSuccessor(state,false);
//        else if(state instanceof DirectedEdge){
//            return getClustering().getSuccessors((DirectedEdge)state);
//            
//        }
//        return null;
//    }


    public List<SearchTarget> getSuccessor(SearchTarget state, boolean b) {
        return getMap().getSuccessor(state,b);
    }
 
}
