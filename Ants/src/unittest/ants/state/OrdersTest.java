package unittest.ants.state;

import org.junit.Test;

import ants.entities.Ant;
import ants.entities.Move;
import ants.missions.Mission;
import ants.state.Orders;

public class OrdersTest {
    @Test(expected = IllegalStateException.class)
    public void testAddMissionIllegalUse() {
        Orders orders = new Orders();
        orders.addMission(new Mission() {

            @Override
            public boolean isValid() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isComplete() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public Move getLastMove() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Ant getAnt() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void execute() {
                // TODO Auto-generated method stub

            }

            @Override
            public void setup() {
                // TODO Auto-generated method stub

            }
        });
    }
}
