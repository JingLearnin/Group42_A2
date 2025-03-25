package ca.mcmaster.se2aa4.island.teamXXX;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;

/**
 * A simple unit test for DecisionMaker.
 * This test forces a scenario where the mission state is START,
 * the drone has sufficient budget, and we expect nextCommand()
 * to return a JSON with action "echo".
 */
public class DecisionMakerTest {

    private DecisionMaker decisionMaker;

    @BeforeEach
    public void setUp() throws Exception {
        decisionMaker = new DecisionMaker();

        // Use reflection to set drone's budget to a high value so that the STOP condition is not triggered.
        Field droneField = DecisionMaker.class.getDeclaredField("drone");
        droneField.setAccessible(true);
        Drone drone = (Drone) droneField.get(decisionMaker);

        // Assume Drone has a private field "budget" (of type int) we can set.
        Field budgetField = Drone.class.getDeclaredField("budget");
        budgetField.setAccessible(true);
        budgetField.set(drone, 200);  // Set budget to 200

        // Set STOP_BUDGET to a value lower than the budget so that the branch for normal operation is taken.
        Field stopBudgetField = DecisionMaker.class.getDeclaredField("STOP_BUDGET");
        stopBudgetField.setAccessible(true);
        stopBudgetField.set(decisionMaker, 50);

        // Optionally, if needed, set the drone's direction to a known value (assuming Drone has such functionality).
        // For example, if Drone has a setter for direction, we could do:
        // drone.setDir(Direction.N);
        // Otherwise, we assume the default is already Direction.N.
    }

    @Test
    public void testNextCommandInStartState() throws Exception {
        // Assume that OnMap's default state is Condition.START.
        // Therefore, nextCommand() should return a JSON with "action": "echo".
        JSONObject result = decisionMaker.nextCommand();

        // Check that the returned JSON object contains "action":"echo".
        assertEquals("echo", result.getString("action"), "Expected action to be 'echo' in START state");
    }
}