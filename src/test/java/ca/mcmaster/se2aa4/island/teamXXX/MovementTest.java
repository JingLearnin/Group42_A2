package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

public class MovementTest {

    // Test the branch where the drone first detects "GROUND" while trans_state is false.
    @Test
    public void testInitialGroundDetection() throws Exception {
        // Create a FakeDroneMovement that returns "GROUND" for getFound() and a range of 5.
        FakeDroneMovement fakeDrone = new FakeDroneMovement("GROUND", 5);
        // Create a FakeOnMapMovement with any initial state (it will be overwritten).
        FakeOnMapMovement fakeMap = new FakeOnMapMovement(Condition.START);
        Movement movement = new Movement(fakeDrone, fakeMap);
        
      
        Commands cmd = movement.getNextMove();
        assertEquals(Commands.FLY, cmd, "When ground is detected initially, expected command should be FLY.");
        assertEquals(Condition.GO_TO_ISLAND, fakeMap.getState(), "Map state should be updated to GO_TO_ISLAND.");
        assertTrue(fakeDrone.wasFacingIslandCalled(), "Drone should have been set to face island.");
        
        // Verify that temp_range was set to the drone's range (5)
        Field tempRangeField = Movement.class.getDeclaredField("temp_range");
        tempRangeField.setAccessible(true);
        int tempRange = (Integer) tempRangeField.get(movement);
        assertEquals(5, tempRange, "temp_range should be set to drone's range (5).");
    }
    
    // Test the branch for GO_TO_ISLAND state when temp_range > 0.
    @Test
    public void testGoToIslandStateFly() throws Exception {
        FakeDroneMovement fakeDrone = new FakeDroneMovement("NOT_GROUND", 5);
        // Set the OnMap state explicitly to GO_TO_ISLAND.
        FakeOnMapMovement fakeMap = new FakeOnMapMovement(Condition.GO_TO_ISLAND);
        Movement movement = new Movement(fakeDrone, fakeMap);
        
        // Force the internal state: set trans_state to true and temp_range to 3.
        Field transStateField = Movement.class.getDeclaredField("trans_state");
        transStateField.setAccessible(true);
        transStateField.set(movement, true);
        
        Field tempRangeField = Movement.class.getDeclaredField("temp_range");
        tempRangeField.setAccessible(true);
        tempRangeField.set(movement, 3);
        
        // In GO_TO_ISLAND state, if temp_range > 0, getNextMove() should return FLY and decrement temp_range.
        Commands cmd = movement.getNextMove();
        assertEquals(Commands.FLY, cmd, "In GO_TO_ISLAND state with temp_range > 0, command should be FLY.");
        int tempRange = (Integer) tempRangeField.get(movement);
        assertEquals(2, tempRange, "temp_range should be decremented by 1.");
    }
    
    // Test the DISCOVER branch when cur_command is STANDBY.
    @Test
    public void testDiscoverStateWithStandby() throws Exception {
        FakeDroneMovement fakeDrone = new FakeDroneMovement("NOT_GROUND", 5);
        // Set the OnMap state to DISCOVER.
        FakeOnMapMovement fakeMap = new FakeOnMapMovement(Condition.DISCOVER);
        Movement movement = new Movement(fakeDrone, fakeMap);
        
        // Use reflection to force cur_command to STANDBY.
        Field curCommandField = Movement.class.getDeclaredField("cur_command");
        curCommandField.setAccessible(true);
        curCommandField.set(movement, Commands.STANDBY);
        
        Commands cmd = movement.getNextMove();
        // In DISCOVER state, if cur_command is STANDBY, it should change to ECHO_FWD.
        assertEquals(Commands.ECHO_FWD, cmd, "In DISCOVER state with STANDBY, command should become ECHO_FWD.");
    }
    
    // Test the DISCOVER branch when cur_command is ECHO_FWD.
    @Test
    public void testDiscoverStateEchoFwd() throws Exception {
        FakeDroneMovement fakeDrone = new FakeDroneMovement("NOT_GROUND", 5);
        FakeOnMapMovement fakeMap = new FakeOnMapMovement(Condition.DISCOVER);
        Movement movement = new Movement(fakeDrone, fakeMap);
        
        // Set cur_command to ECHO_FWD explicitly.
        Field curCommandField = Movement.class.getDeclaredField("cur_command");
        curCommandField.setAccessible(true);
        curCommandField.set(movement, Commands.ECHO_FWD);
        
        Commands cmd = movement.getNextMove();
        // When cur_command is ECHO_FWD in DISCOVER state, it should set fwd_range and then change to ECHO_L.
        assertEquals(Commands.ECHO_L, cmd, "In DISCOVER state with ECHO_FWD, command should become ECHO_L.");
        
        // Verify that fwd_range is set to the drone's range (5).
        Field fwdRangeField = Movement.class.getDeclaredField("fwd_range");
        fwdRangeField.setAccessible(true);
        int fwdRange = (Integer) fwdRangeField.get(movement);
        assertEquals(5, fwdRange, "fwd_range should be set to drone's range (5).");
    }
    
    // Test the DISCOVER branch when cur_command is ECHO_L.
    @Test
    public void testDiscoverStateEchoL() throws Exception {
        FakeDroneMovement fakeDrone = new FakeDroneMovement("NOT_GROUND", 7);
        FakeOnMapMovement fakeMap = new FakeOnMapMovement(Condition.DISCOVER);
        Movement movement = new Movement(fakeDrone, fakeMap);
        
        // Set cur_command to ECHO_L.
        Field curCommandField = Movement.class.getDeclaredField("cur_command");
        curCommandField.setAccessible(true);
        curCommandField.set(movement, Commands.ECHO_L);
        
        Commands cmd = movement.getNextMove();
        // When cur_command is ECHO_L in DISCOVER state, it should set l_range and then change to ECHO_R.
        assertEquals(Commands.ECHO_R, cmd, "In DISCOVER state with ECHO_L, command should become ECHO_R.");
        Field lRangeField = Movement.class.getDeclaredField("l_range");
        lRangeField.setAccessible(true);
        int lRange = (Integer) lRangeField.get(movement);
        assertEquals(7, lRange, "l_range should be set to drone's range (7).");
    }
    
    // Test the DISCOVER branch when cur_command is ECHO_R and 'turned' is false.
    @Test
    public void testDiscoverStateEchoRWithoutTurned() throws Exception {
        FakeDroneMovement fakeDrone = new FakeDroneMovement("NOT_GROUND", 10);
        FakeOnMapMovement fakeMap = new FakeOnMapMovement(Condition.DISCOVER);
        Movement movement = new Movement(fakeDrone, fakeMap);
        
        // Set cur_command to ECHO_R.
        Field curCommandField = Movement.class.getDeclaredField("cur_command");
        curCommandField.setAccessible(true);
        curCommandField.set(movement, Commands.ECHO_R);
        
        // Ensure 'turned' is false (default is false, but we enforce it).
        Field turnedField = Movement.class.getDeclaredField("turned");
        turnedField.setAccessible(true);
        turnedField.set(movement, false);
        
        Commands cmd = movement.getNextMove();
        // With turned false in the ECHO_R branch, the command should become FLY.
        assertEquals(Commands.FLY, cmd, "In DISCOVER state with ECHO_R and turned false, command should be FLY.");
    }
    
    // Test the DISCOVER branch when cur_command is FLY (or TURN_L/ TURN_R), which should reset to ECHO_FWD.
    @Test
    public void testDiscoverStateResetToEchoFwd() throws Exception {
        FakeDroneMovement fakeDrone = new FakeDroneMovement("NOT_GROUND", 10);
        FakeOnMapMovement fakeMap = new FakeOnMapMovement(Condition.DISCOVER);
        Movement movement = new Movement(fakeDrone, fakeMap);
        
        // Set cur_command to FLY.
        Field curCommandField = Movement.class.getDeclaredField("cur_command");
        curCommandField.setAccessible(true);
        curCommandField.set(movement, Commands.FLY);
        
        Commands cmd = movement.getNextMove();
        // In DISCOVER state, if cur_command is FLY, it should reset to ECHO_FWD.
        assertEquals(Commands.ECHO_FWD, cmd, "In DISCOVER state with FLY, command should reset to ECHO_FWD.");
    }
}

// --- Stub classes for Movement tests ---

// FakeDroneMovement extends Drone to control getFound() and getRange() behavior,
// and to record if setFacingIsland() is called.
class FakeDroneMovement extends Drone {
    private final String fakeFound;
    private final int fakeRange;
    private boolean facingIslandCalled = false;
    
    public FakeDroneMovement(String found, int range) {
        super();
        this.fakeFound = found;
        this.fakeRange = range;
    }
    
    @Override
    public String getFound() {
        return fakeFound;
    }
    
    @Override
    public Integer getRange() {
        return fakeRange;
    }
    
    @Override
    public void setFacingIsland() {
        facingIslandCalled = true;
    }
    
    public boolean wasFacingIslandCalled() {
        return facingIslandCalled;
    }
}

// FakeOnMapMovement extends OnMap to control and observe state changes.
class FakeOnMapMovement extends OnMap {
    private Condition state;
    
    public FakeOnMapMovement(Condition state) {
        super();
        this.state = state;
    }
    
    @Override
    public Condition getState() {
        return state;
    }
    
    @Override
    public void setState(Condition newState) {
        this.state = newState;
    }
}