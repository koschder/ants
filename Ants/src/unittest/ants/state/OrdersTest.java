package unittest.ants.state;

import java.util.List;

import org.junit.Test;

import ants.entities.Ant;
import ants.missions.Mission;
import ants.state.Orders;
import ants.tasks.Task.Type;

public class OrdersTest {
    @Test(expected = IllegalStateException.class)
    public void testAddMissionIllegalUse() {
        Orders orders = new Orders();
        orders.addMission(new Mission() {

            @Override
            public String isValid() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean isComplete() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void execute() {
                // TODO Auto-generated method stub

            }

            @Override
            public void setup() {
                // TODO Auto-generated method stub

            }

            @Override
            public List<Ant> getAnts() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Type getTaskType() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean isAbandoned() {
                // TODO Auto-generated method stub
                return false;
            }
        });
    }
}
