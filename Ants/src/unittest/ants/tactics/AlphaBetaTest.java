package unittest.ants.tactics;

import java.util.*;

import org.junit.*;

import tactics.minmax.*;
import ants.entities.*;
import ants.state.*;
import ants.tactics.*;
import api.entities.*;

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
