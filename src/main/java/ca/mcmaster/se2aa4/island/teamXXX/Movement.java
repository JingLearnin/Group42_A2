package ca.mcmaster.se2aa4.island.teamXXX;

import ca.mcmaster.se2aa4.island.teamXXX.OnMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ca.mcmaster.se2aa4.island.teamXXX.Drone;
import java.util.Objects;

public class Movement {

    private final Logger log = LogManager.getLogger();
    private Commands currentCommand = Commands.ECHO_FWD;
    private int initialFacing = 0;

    private Boolean hasTurned = false;
    private Boolean transitionPhase = false;
    private Integer movementSteps = 0;

    private Integer fRange;
    private Integer lRange;
    private Integer rRange;
    
    private Drone drone = new Drone();
    private OnMap map = new OnMap();

    /**
     * @param Drone, Map 
     * Constructor for NavigationSystem.
     */
    public Movement(Drone drone, OnMap map) {
        this.drone = drone;
        this.map = map;
    }

    /*
    Determines the final movement decision when approaching an island.
     */
    private Commands determineFinalMove() {
        if (currentCommand == Commands.ECHO_L) {
            return Commands.TURN_L;
        } else if (currentCommand == Commands.ECHO_R) {
            return Commands.TURN_R;
        } else {
            return Commands.MOVE;
        }
    }

    /*
    Returns the next movement command based on the drone's state.
     */
    public Commands getNextCommand() {
        if (!transitionPhase) {
            if (Objects.equals(drone.getDiscoveredLocation(), "GROUND")) {
                if (initialFacing == 0) {
                    if (currentCommand.equals(Commands.ECHO_FWD)) {
                        drone.setIslandInView();
                        log.info("Counter = " + initialFacing + ", Island in view: " + drone.isIslandInView());
                        initialFacing++;
                    }
                }
                transitionPhase = true;
                map.setState(Condition.NAVIGATE);
                movementSteps = drone.getScanRange();
                return determineFinalMove();
            }
        }

        if (map.getState() == Condition.NAVIGATE) {
            if (movementSteps > 0) {
                movementSteps -= 1;
                return Commands.ECHO_FWD;
            } else {
                log.info("ISLAND REACHED");
                map.setState(Condition.PRE_SCAN);
                return currentCommand;
            }
        } else if (map.getState() == Condition.SEARCH) {
            if (currentCommand == Commands.WAIT) {
                initialFacing++;
                return Commands.ECHO_FWD;
            } else if (currentCommand == Commands.ECHO_FWD) {
                fRange = drone.getScanRange();
                return Commands.ECHO_L;
            } else if (currentCommand == Commands.ECHO_L) {
                lRange = drone.getScanRange();
                return Commands.ECHO_R;
            } else if (currentCommand == Commands.ECHO_R) {
                rRange = drone.getScanRange();
                if (hasTurned) {
                    if (fRange > rRange && fRange > lRange) {
                        return Commands.MOVE;
                    } else if (lRange > rRange && lRange > fRange) {
                        return Commands.ECHO_L;
                    } else if (rRange > lRange && rRange > fRange) {
                        return Commands.ECHO_R;
                    } else if (fRange.equals(lRange) || fRange.equals(rRange)) {
                        return Commands.MOVE;
                    } else {
                        return Commands.STOP;
                    }
                } else {
                    return Commands.MOVE;
                }
            } else if (currentCommand == Commands.ECHO_FWD || currentCommand == Commands.TURN_L || currentCommand == Commands.TURN_R) {
                return Commands.ECHO_FWD;
            } else {
                log.info("Unexpected state encountered.");
                return currentCommand;
            }
        }

        return currentCommand;
    }
}