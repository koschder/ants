package pathfinder.search;

import java.util.List;

import pathfinder.entities.AbstractWraparoundMap;
import pathfinder.entities.Clustering;
import pathfinder.entities.DirectedEdge;
import pathfinder.entities.Edge;
import pathfinder.entities.SearchTarget;

public class ClusteredMap extends AbstractWraparoundMap  {

    private Clustering clustering;
    
    public ClusteredMap(Clustering c){
        clustering = c;
    }
    
    @Override
    public int getRows() {
        return clustering.getRows();
    }

    @Override
    public int getCols() {
        return clustering.getCols();
    }

    @Override
    public boolean isPassable(SearchTarget tile) {
        return true;
    }

    @Override
    public boolean isVisible(SearchTarget tile) {
        return true;
    }

    @Override
    public List<SearchTarget> getSuccessor(SearchTarget currentEdge, boolean isNextMove) {
       
        if(!(currentEdge instanceof DirectedEdge)){
            throw new IllegalArgumentException("SearchTarget must be of the type DirectedEdge");
        }
        
        DirectedEdge e = (DirectedEdge)currentEdge;
        
        return e.getCluster().getEdgeWithNeighbourCluster(e);
      
    }

}
