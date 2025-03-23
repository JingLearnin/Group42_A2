package ca.mcmaster.se2aa4.island.teamXXX;

import ca.mcmaster.se2aa4.island.teamXXX.Drone;
import ca.mcmaster.se2aa4.island.teamXXX.OnMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implements phased island scanning operations using a state machine pattern.
 *
 * <p>Key responsibilities:
 * - Manages zig-zag scanning pattern execution
 * - Handles U-turn maneuvers at island boundaries
 * - Coordinates state transitions between scanning phases
 * - Maintains emergency site detection readiness</p>
 */
public class ScanIsland  implements Phase{
    private final Logger logger = LogManager.getLogger();

    private Drone drone;
    private OnMap map;

    private Commands prev_command = Commands.FLY;
    private Commands prev_turn;

    private Boolean to_ground = false;
    private Boolean u_turned = false;
    private Boolean u_turned_left = false;

    private int counter = 0;
    private int idx = 0;

    private Commands[] uturn_right = {Commands.FLY, Commands.FLY, Commands.FLY, Commands.TURN_R, Commands.ECHO_FWD, Commands.FLY, Commands.TURN_R, Commands.TURN_R, Commands.TURN_L};
    private Commands[] uturn_left = {Commands.FLY, Commands.FLY, Commands.FLY, Commands.TURN_L, Commands.ECHO_FWD, Commands.FLY, Commands.TURN_L, Commands.TURN_L, Commands.TURN_R};

    /**
     * Initializes scanning controller with drone interface and mapping system
     * @param drone_in Sensor/actuator interface
     * @param mapping Geographic data repository
     */
    public ScanIsland(Drone drone_in, OnMap mapping) {
        this.drone = drone_in;
        this.map = mapping;
    }

    /**
     * Generates next movement command based on current exploration state
     *
     * <p>State Transition Logic:
     * 1. PRESCAN: Initial alignment checks
     * 2. SCAN: Primary scanning operations
     * 3. EVAL_ECHO: Boundary detection handling
     * 4. UTURN: Perimeter following maneuvers</p>
     *
     * @return Next valid navigation command
     */
    public Commands getNextMove() {
        
        if (map.getState() == Condition.PRESCAN) {
            if (drone.facing_island()) {
                logger.info("DRONE TURNING INTO ISLAND TO INITIALIZE");
                prev_command = Commands.TURN_R;
            } else {
                prev_command = Commands.SCAN;
            }
            map.setState(Condition.SCAN);
            u_turned_left = false;
            return prev_command;
        }
        if (map.getState() == Condition.EVAL_ECHO) {
            String forward = drone.getFound();
        
            if (forward.equals("GROUND")){
                map.setState(Condition.SCAN);
                logger.info("Switching states: "+ map.getState());
                to_ground = true;
                u_turned = false;
                prev_command = Commands.FLY;
                return prev_command;
            }
            else { 
                int range = drone.getRange();
                return determineUturn(range);
            }
        }
        if (map.getState() == Condition.SCAN) {
            if (to_ground) {
                if (counter <= drone.getRange()) {
                    prev_command = Commands.FLY;
                    counter++;
                } else {
                    to_ground = false;
                    counter = 0;
                    prev_command = Commands.SCAN;
                    map.setState(Condition.SCAN);
                    logger.info("Switching states: "+ map.getState());
                }
                return prev_command;
            }
            
            if (prev_command == Commands.SCAN) {
                prev_command = Commands.FLY;
                if (drone.isWater()) {
                    prev_command = Commands.ECHO_FWD;
                    map.setState(Condition.EVAL_ECHO);
                    logger.info("Switching states: "+ map.getState());
                }
                return prev_command;
            } else {
                prev_command = Commands.SCAN;
                return prev_command;
            }
        }
        if (map.getState() == Condition.UTURN) {
            return UTurn();
        }

        logger.info("Drone is not in a valid state for ScanIsland");
        return Commands.STOP;
    }


    /**
     * Determines U-turn requirements based on sensor range data
     * @param range Current obstacle detection range
     * @return Appropriate boundary response command
     */
    private Commands determineUturn(int range) {
        if (u_turned) {
            logger.info("Drone has Uturned into an empty line, stopping drone...");
            return Commands.STOP;
        } else if (range == 0) {
            logger.info("Drone is unable to U-turn");
            return Commands.STOP;
        } else if (range <= 3) { 
            logger.info("SMALL UTURN");
            idx = 4 - range;
        }
        map.setState(Condition.UTURN);
        logger.info("Switching states: "+ map.getState());
        return UTurn();
    }

    /**
     * Executes pre-programmed U-turn maneuver sequence
     *
     * <p>Maneuver Characteristics:
     * - Maintains island perimeter tracking
     * - Adjusts for left/right turn preferences
     * - Validates movement feasibility mid-sequence</p>
     *
     * @return Next command in U-turn sequence
     */
    private Commands UTurn() {
        if (idx == 5 && !drone.getFound().equals("GROUND")) {
            if (drone.getRange() < 2){
                return Commands.STOP;
            }
        } 
        if (idx < uturn_left.length) {
            if (u_turned_left) {
                prev_command = uturn_right[idx];
                idx++;
                prev_turn = Commands.TURN_R;
                return prev_command;
            } 
            else {
                prev_command = uturn_left[idx];
                idx++;
                prev_turn = Commands.TURN_L;
                return prev_command;
            }
        }


        if (prev_turn == Commands.TURN_L) {
            u_turned_left = true;
        } else {
            u_turned_left = false;
        }
        u_turned = true;
        idx = 0;
        prev_command = Commands.ECHO_FWD;
        
        map.setState(Condition.EVAL_ECHO);
        logger.info("Switching states: "+ map.getState());

        return prev_command;
    }
}
