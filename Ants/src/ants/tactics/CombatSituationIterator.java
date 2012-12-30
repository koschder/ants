package ants.tactics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import tactics.minmax.Game;
import api.entities.Aim;
import api.entities.Move;

public class CombatSituationIterator implements Iterator<Game> {
    private CombatSituation combatSituation;
    private List<CombatUnit> ants;
    private int moveIndex[];

    public CombatSituationIterator(CombatSituation combatSituation) {
        this.combatSituation = combatSituation;
        this.ants = new ArrayList<CombatUnit>(combatSituation.getMovingUnits().values());
        this.moveIndex = new int[ants.size()];
        Arrays.fill(moveIndex, 0);
    }

    @Override
    public boolean hasNext() {
        for (int i = 0; i < moveIndex.length; i++) {
            if (moveIndex[i] < 4)
                return true;
        }
        return false;
    }

    private CombatSituation getNext() {
        int i = 0;
        List<Move> moves = new ArrayList<Move>();
        for (CombatUnit a : ants) {
            Aim aim = getAim(moveIndex[i++]);
            moves.add(new Move(a.getTile(), aim));
        }
        updateIndex(0);
        return new CombatSituation(combatSituation.getEnemyAnts(), combatSituation.getMyAnts(), moves);

    }

    private void updateIndex(int i) {
        if (i >= moveIndex.length)
            return;
        moveIndex[i]++;
        if (moveIndex[i] > 4) {
            moveIndex[i] = 0;
            updateIndex(++i);
        }

    }

    private Aim getAim(int move) {
        switch (move) {
        case 0:
            return null;
        case 1:
            return Aim.EAST;
        case 2:
            return Aim.NORTH;
        case 3:
            return Aim.SOUTH;
        case 4:
            return Aim.WEST;
        default:
            throw new IllegalStateException("moveIndex is not valid: " + move);
        }
    }

    @Override
    public CombatSituation next() {

        final CombatSituation next = getNext();
        // System.out.println(Arrays.toString(moveIndex));
        return next;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
