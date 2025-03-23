package ca.mcmaster.se2aa4.island.teamXXX;

/**
 * Defines all valid drone command actions supported by the system.
 * Converts enum values to API-compatible command strings for communication with drone.
 *
 * <p>Each enum constant maps to a specific drone operation. Used primarily to encapsulate
 * command types and ensure type safety in action handling logic.</p>
 */


public enum Commands {

    STANDBY,
    FLY,
    ECHO_FWD,
    ECHO_R,
    ECHO_L,
    SCAN,
    TURN_R,
    TURN_L,
    STOP;
    
/**
 * Converts the command enum to its corresponding API string.
 * 
 * <p><b>Usage Note:</b> For `TURN_R`/`TURN_L`, additional parameters (direction) 
 * must be sent separately in the request payload.</p>
 *
 * @return API-compatible command string, or `null` for STANDBY
 * 
 * @example <caption>Basic Usage</caption>
 * Commands.FLY.getCommand();   // returns "fly"
 * Commands.TURN_R.getCommand();// returns "heading"
 */
    public String getCommand() {
        switch (this) {
            case FLY:
                return "fly";
            
            case ECHO_FWD, ECHO_L, ECHO_R:
                return "echo";

            case SCAN:
                return "scan";

            case TURN_R, TURN_L:
                return "heading";

            case STOP:
                return "stop";
        
            default:
                return null;
        }
    }
}

