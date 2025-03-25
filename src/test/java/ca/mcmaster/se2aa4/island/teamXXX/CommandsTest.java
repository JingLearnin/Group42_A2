package ca.mcmaster.se2aa4.island.teamXXX;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class CommandsTest {

    @Test
    public void testGetCommand() {
        // Test that FLY returns "fly"
        assertEquals("fly", Commands.FLY.getCommand(), "FLY command should return 'fly'");
        
        // Test that all echo-related commands return "echo"
        assertEquals("echo", Commands.ECHO_FWD.getCommand(), "ECHO_FWD should return 'echo'");
        assertEquals("echo", Commands.ECHO_R.getCommand(), "ECHO_R should return 'echo'");
        assertEquals("echo", Commands.ECHO_L.getCommand(), "ECHO_L should return 'echo'");
        
        // Test that SCAN returns "scan"
        assertEquals("scan", Commands.SCAN.getCommand(), "SCAN command should return 'scan'");
        
        // Test that TURN_R and TURN_L return "heading"
        assertEquals("heading", Commands.TURN_R.getCommand(), "TURN_R should return 'heading'");
        assertEquals("heading", Commands.TURN_L.getCommand(), "TURN_L should return 'heading'");
        
        // Test that STOP returns "stop"
        assertEquals("stop", Commands.STOP.getCommand(), "STOP command should return 'stop'");
        
        // Test that STANDBY returns null
        assertNull(Commands.STANDBY.getCommand(), "STANDBY should return null");
    }
}
