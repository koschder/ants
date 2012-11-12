package unittest.ants.tactics;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import tactics.minmax.AlphaBeta;
import tactics.minmax.Game;
import ants.entities.Ant;
import ants.state.Ants;
import ants.tactics.CombatSituation;
import api.entities.Tile;
import api.entities.Unit;

@Ignore
public class AlphaBetaTest {
    @Before
    public void setup() {
        Ants.setWorld(new StubWorld(25, 25));
    }

    @Test
    public void testAlphaBeta() {
        List<Unit> enemyAnts = new ArrayList<Unit>();
        List<Unit> myAnts = new ArrayList<Unit>();

        enemyAnts.add(new Ant(new Tile(15, 15), 1));
        enemyAnts.add(new Ant(new Tile(16, 15), 1));
        enemyAnts.add(new Ant(new Tile(16, 16), 1));
        myAnts.add(new Ant(new Tile(17, 17), 0));
        myAnts.add(new Ant(new Tile(18, 17), 0));
        myAnts.add(new Ant(new Tile(18, 18), 0));

        Game g = new AlphaBeta().bestMoveTimeLimited(new CombatSituation(enemyAnts, myAnts), 500);
        System.out.println(g.getMoves());
    }
}
