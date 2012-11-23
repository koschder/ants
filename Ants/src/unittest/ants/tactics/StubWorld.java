package unittest.ants.tactics;

import ants.state.World;

public class StubWorld extends World {

    public StubWorld(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public int getAttackRadius2() {
        return 2;
    }
}
