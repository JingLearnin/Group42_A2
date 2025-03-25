package ca.mcmaster.se2aa4.island.teamXXX;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;


public class DecisionMakerTest {

    private DecisionMaker decisionMaker;

    @BeforeEach
    public void setUp() throws Exception {
        decisionMaker = new DecisionMaker();

        // Use reflection to set drone's budget to a high value so that the STOP condition is not triggered.
        Field droneField = DecisionMaker.class.getDeclaredField("drone");
        droneField.setAccessible(true);
        Drone drone = (Drone) droneField.get(decisionMaker);

        
        Field budgetField = Drone.class.getDeclaredField("budget");
        budgetField.setAccessible(true);
        budgetField.set(drone, 200);  // Set budget to 200

        Field stopBudgetField = DecisionMaker.class.getDeclaredField("STOP_BUDGET");
        stopBudgetField.setAccessible(true);
        stopBudgetField.set(decisionMaker, 50);

    }

    @Test
    public void testNextCommandInStartState() throws Exception {
        
        JSONObject result = decisionMaker.nextCommand();

        // Check that the returned JSON object contains "action":"echo".
        assertEquals("echo", result.getString("action"), "Expected action to be 'echo' in START state");
    }
}