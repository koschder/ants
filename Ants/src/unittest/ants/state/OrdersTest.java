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
                return null;
            }

            @Override
            public boolean isComplete() {
                return false;
            }

            @Override
            public void execute() {

            }

            @Override
            public void setup() {

            }

            @Override
            public List<Ant> getAnts() {
                return null;
            }

            @Override
            public Type getTaskType() {
                return null;
            }

            @Override
            public boolean isAbandoned() {
                return false;
            }
        });
    }
}
