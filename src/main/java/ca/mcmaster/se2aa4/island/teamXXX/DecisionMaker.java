package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Core decision engine coordinating drone mission phases and resource management.
 * 
 * <p>Orchestrates navigation, scanning, and emergency protocols based on real-time 
 * drone status and map data. Integrates with {@code Movement} and {@code ScanIsland}
 * subsystems for phase-specific logic.</p>
 */
public class DecisionMaker {
    private final Logger logger = LogManager.getLogger();
    private Drone drone = new Drone();
    private OnMap map = new OnMap();
    private Movement find_island = new Movement(drone, map);
    private ScanIsland scan_island = new ScanIsland(drone, map);
    private int STOP_BUDGET;

/**
 * Generates next action JSON based on mission phase and resource constraints.
 * 
 * <p><b>Decision Workflow:</b>
 * <ol>
 *   <li>Check remaining battery against STOP_BUDGET</li>
 *   <li>Route to phase-specific handler (discovery/scanning)</li>
 *   <li>Update drone/map state after action</li>
 * </ol>
 *
 * @return Valid JSON action, or emergency stop command
 * 
 * @see Movement#getNextMove()
 * @see ScanIsland#getNextMove()
 * @example <caption>Discovery Phase</caption>
 * // When state=DISCOVER, delegates to Movement subsystem
 * JSONObject cmd = decisionMaker.nextCommand(); // e.g. {"action":"fly"}
 */
    public JSONObject nextCommand() {
        Controller controller = new Controller(drone);

        try {

            // ensure drone has enough battery to perform stop action
            if (drone.getBudget() > STOP_BUDGET && drone.getBudget() > 100) {
                logger.info("this is the state: " + map.getState());
                logger.info("BUDGET LEFT: " + drone.getBudget());

                if (map.getState() == Condition.START) {
                    JSONObject parameters = new JSONObject();
                    JSONObject actions = new JSONObject();
                    actions.put("action", "echo");
            

                    Direction current_head = drone.getDir();
                    String current_head_str = current_head.DirToStr();
                    map.setInitDir(current_head);
                    
           

                    JSONObject ret_action = actions.put("parameters", parameters.put("direction", current_head_str));
                    logger.info(ret_action);

                    Condition current_state = map.getState();
                    map.setState(current_state.changeCondition(current_state));

                    logger.info("new state: " + map.getState());
                    return ret_action;
                }
                // state requires next command to be determined through MOVEMENT
                if (map.getState() == Condition.DISCOVER || map.getState() == Condition.GO_TO_ISLAND) {

                    Commands current_act = find_island.getNextMove();
                    map.updatePos(current_act);
                    logger.info("*new coordinates: " + map.pos.coordinates);
                    return controller.CommandsToJSON(current_act);

                }
                // state requires next command to be determined through ScanIsland
                else if (map.getState() == Condition.PRESCAN || map.getState() == Condition.SCAN || map.getState() == Condition.UTURN || map.getState() == Condition.EVAL_ECHO) {
                    if (drone.getCreekFound() || drone.getSiteFound()) {
                        map.updateTypes(drone);
                    }
                    Commands current_act = scan_island.getNextMove();
                    map.updatePos(current_act);

                    map.printPois();
                    return controller.CommandsToJSON(current_act);

                } else if (map.getState() == Condition.STOP) {
                    JSONObject json = new JSONObject();
                    json.put("action", "stop");
                    return json;
                }
            }
            logger.info("STOP: NO BUDGET AVIALBLE");
            return controller.CommandsToJSON(Commands.STOP);
        }catch (Exception e) {
            logger.info("Something went wrong");
            return controller.CommandsToJSON(Commands.STOP);
        }
    }

    /**
 * Calculates emergency stop threshold as 2% of initial battery.
 * Should be called after drone initialization.
 */
    public void setThres() {
        logger.info((int) (drone.getBudget()*0.02));
    }

    /**
 * Synchronizes drone status with latest sensor data.
 * 
 * @param s Raw sensor data string (JSON format expected)
 */
    public void initializeDrone(String s) {
        drone.initializeStats(s);
    }

    /**
 * Updates real-time drone status from latest sensor feedback.
 * 
 * <p>Synchronizes critical mission parameters after each action execution.
 * Typically invoked with API response data to refresh:
 * <ul>
 *   <li>Remaining battery budget</li>
 *   <li>Current heading direction</li>
 *   <li>Position coordinates</li>
 *   <li>Resource detection flags (e.g., creek/site found)</li>
 * </ul>
 *
 * @param s Raw JSON string from drone sensors. Expected format:
 *          {@code {"cost": 85, "status": "OK", "extras": {...}}}
 * 
 * @throws JSONException If input lacks required fields (depends on {@code Drone.updateStats()} implementation)
 * 
 * @example <caption>Updating after Fly Action</caption>
 * String response = "{\"cost\":30, \"status\":\"SUCCESS\"}";
 * decisionMaker.refreshDrone(response);
 * assert drone.getBudget() == (previous_budget - 30);
 */
    public void refreshDrone(String s) {
        drone.updateStats(s);
    }

/**
 * Retrieves first identified creek ID from map data.
 * 
 * @return Creek ID string, or empty if none found
 */
    public String CreekFound() {
        return map.returnCreek();
    }

}