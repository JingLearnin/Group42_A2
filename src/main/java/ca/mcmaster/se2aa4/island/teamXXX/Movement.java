package ca.mcmaster.se2aa4.island.teamXXX;

import ca.mcmaster.se2aa4.island.teamXXX.OnMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ca.mcmaster.se2aa4.island.teamXXX.Drone;

import java.util.Objects;

/**
* Handles drone navigation logic during island exploration phases.
* Implements state-driven movement decisions and obstacle avoidance.
* 
* <p>Key responsibilities:
* - Pathfinding algorithms
* - Sensor range analysis
* - State transition management
* - Collision prevention</p>
*/

public class Movement implements Phase {

    private final Logger logger = LogManager.getLogger();
    private Commands cur_command = Commands.ECHO_FWD;
    private int init_dir = 0;
    private Integer fwd_range;
    private Integer l_range;
    private Integer r_range;
    private Drone drone = new Drone();
    private OnMap map = new OnMap();


    private Boolean turned = false;
    private Boolean trans_state = false;
    private Integer temp_range = 0;


    /*
    Input: Drone, Mapping
    Output:N/A
    The constructor.
     */
    public Movement(Drone in_drone, OnMap on_map) {
        this.drone = in_drone;
        this.map = on_map;
    }

/**
 * Generates next movement command based on exploration phase.
 * 
 * @return Valid navigation command from Commands enum
 * 
 * <p>State transitions:
 * 1. INIT -> GO_TO_ISLAND (when ground detected)
 * 2. GO_TO_ISLAND -> PRESCAN (when island reached)
 * 3. PRESCAN -> DISCOVER (scanning phase)</p>
 */
    public Commands getNextMove() {
        if (!trans_state) {
    
            if (Objects.equals(drone.getFound(), "GROUND")) {
      
                if (init_dir == 0) {
                    if (cur_command.equals(Commands.ECHO_FWD)) {
                        drone.setFacingIsland();
                        logger.info("counter = " + init_dir + " flag: " + drone.facing_island());
                        init_dir++;
                    }
                }
                trans_state = true;
                map.setState(Condition.GO_TO_ISLAND);
      

                //if the previous action echoed left and found ground turn left
                if (cur_command == Commands.ECHO_L) {
                    cur_command = Commands.TURN_L;
                }
                //if the previous action echoed right and found ground turn right
                else if (cur_command == Commands.ECHO_R) {
                    cur_command = Commands.TURN_R;
                }
                else { //fly straight to island
                    cur_command = Commands.FLY;
                }

                temp_range = drone.getRange();
                return cur_command;

            }
        }
        if (map.getState() == Condition.GO_TO_ISLAND) {
            if (temp_range > 0) {
                temp_range -= 1;
                cur_command = Commands.FLY;

                return cur_command;
            }
            else {
                logger.info("ISLAND REACHED");
                map.setState(Condition.PRESCAN);
                return cur_command;            }
        }
        else if (map.getState() == Condition.DISCOVER) {
            if (cur_command == Commands.STANDBY) {
                cur_command = Commands.ECHO_FWD;
                init_dir++;
                return cur_command;            }
            //first echo forward then left
            else if (cur_command == Commands.ECHO_FWD) {
                fwd_range = drone.getRange();
                cur_command = Commands.ECHO_L;
                return cur_command;            }
            //echo left then echo right
            else if (cur_command == Commands.ECHO_L) {
                l_range = drone.getRange();
                cur_command = Commands.ECHO_R;
                return cur_command;            }

   
            else if (cur_command == Commands.ECHO_R) {
                r_range = drone.getRange();
                if (turned) {
                    //forward range has the greatest value
                    if (fwd_range > r_range && fwd_range > l_range) {
                        cur_command = Commands.FLY;
                    }
                    //left range has the greatest value
                    else if (l_range > r_range && l_range > fwd_range) {
                        cur_command = Commands.TURN_L;
                    }
                    //right range has the greatest value
                    else if (r_range > l_range && r_range > fwd_range) {
                        cur_command = Commands.TURN_R;
                    }
                    else if (fwd_range.equals(l_range) || fwd_range.equals(r_range)) {
                        cur_command =  Commands.FLY;
                    }
                    else {
                        logger.info("Something went wrong");
                        cur_command = Commands.STOP;
                    }

                } else {
                    cur_command = Commands.FLY;
                }
                return cur_command; }
            else if (cur_command == Commands.FLY || cur_command == Commands.TURN_L || cur_command == Commands.TURN_R) {
                cur_command = Commands.ECHO_FWD;
                return cur_command;            } else {
                logger.info("Something went wrong.");
                return cur_command;            }
        }
        logger.info("In wrong State");
        return cur_command;    }
}
