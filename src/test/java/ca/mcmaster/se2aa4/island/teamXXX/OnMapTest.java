package ca.mcmaster.se2aa4.island.teamXXX;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OnMapTest {
    private OnMap onMap;
    private Drone drone;

    @BeforeEach
    public void setUp() {
        onMap = new OnMap();
        drone = new Drone(); // Assuming a default constructor exists
    }

    @Test
    public void testInitialState() {
        // Check initial condition
        assertEquals(Condition.START, onMap.getState(), 
            "Initial state should be START");
    }

    @Test
    public void testStateTransition() {
        // Change state and verify
        onMap.setState(Condition.SCAN);
        assertEquals(Condition.SCAN, onMap.getState(), 
            "State should change to SCAN");
    }

    @Test
    public void testPositionUpdate() {
        // Test different movement commands
        onMap.updatePos(Commands.FLY);
        assertEquals(0, onMap.pos.getX(), "X coordinate after FLY");
        assertEquals(1, onMap.pos.getY(), "Y coordinate after FLY");

        onMap.updatePos(Commands.TURN_L);
        assertEquals(-1, onMap.pos.getX(), "X coordinate after TURN_L");

        
    }

    @Test
    public void testReturnCreek() {
        // Test creek return scenarios
        assertEquals("NA", onMap.returnCreek(), 
            "Should return NA when no creeks or sites");
    }
}
