package ca.mcmaster.se2aa4.island.teamXXX;

public enum Commands {
    MOVE,
    ECHO_FWD,
    ECHO_R,
    ECHO_L,
    SCAN,
    TURN_R,
    TURN_L,
    WAIT,
    STOP;


    /**
     * Returns the action object as a string
     * @param n/a
     * @return String
     */
    public String getCommand() {
        switch (this) {
            case MOVE:
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