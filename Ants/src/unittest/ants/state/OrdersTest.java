package unittest.ants.state;

import java.util.*;

import org.junit.*;

import ants.entities.*;
import ants.missions.*;
import ants.state.*;

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
        });
    }
}
