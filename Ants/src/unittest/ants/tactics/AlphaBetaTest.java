package unittest.ants.tactics;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import tactics.minmax.AlphaBeta;
import tactics.minmax.Game;
import ants.entities.Ant;
import ants.state.Ants;
import ants.tactics.CombatSituation;
import api.entities.Tile;
import api.entities.Unit;

@SuppressWarnings("deprecation")
public class AlphaBetaTest {
    @Before
    public void setup() {
        Ants.setWorld(new StubWorld(25, 25));
    }

    @Test
    public void testAlphaBeta() {
        List<Unit> enemyAnts = new ArrayList<Unit>();
        List<Unit> myAnts = new ArrayList<Unit>();

        // enemyAnts.add(new Ant(new Tile(14, 13), 1));
        // enemyAnts.add(new Ant(new Tile(14, 14), 1));
        enemyAnts.add(new Ant(new Tile(15, 14), 1));
        enemyAnts.add(new Ant(new Tile(15, 15), 1));
        myAnts.add(new Ant(new Tile(17, 17), 0));
        // myAnts.add(new Ant(new Tile(18, 17), 0));
        // myAnts.add(new Ant(new Tile(18, 18), 0));
        // myAnts.add(new Ant(new Tile(18, 19), 0));

        long start = System.currentTimeMillis();
        Game g = new AlphaBeta().bestMove(new CombatSituation(enemyAnts, myAnts), 1);
        // Game g = new AlphaBeta().bestMove(new CombatSituation(myAnts, enemyAnts), 1);
        System.out.println("AlphaBeta took " + (System.currentTimeMillis() - start) + "ms");
        System.out.println(g.getMoves());
    }
}
