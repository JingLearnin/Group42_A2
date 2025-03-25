package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class ActionPlanner {
    private final Drone drone;
    private final OnMap map;
    private final PhaseManager phaseManager;
    private final Controller controller;

    private static final int STOP_THRESHOLD = 100;

    public ActionPlanner(Drone drone, OnMap map, Controller controller) {
        this.drone = drone;
        this.map = map;
        this.controller = controller;
        this.phaseManager = new PhaseManager(drone, map);
    }
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
    public JSONObject getNextCommand() {
        if (drone.getBudget() <= STOP_THRESHOLD) {
            return controller.CommandsToJSON(Commands.STOP);
        }

        Condition state = map.getState();

        if (state == Condition.START) {
            map.setInitDir(drone.getDir());
            map.setState(state.changeCondition(state));
            JSONObject params = new JSONObject().put("direction", drone.getDir().DirToStr());
            return new JSONObject().put("action", "echo").put("parameters", params);
        }

        if (drone.getCreekFound() || drone.getSiteFound()) {
            map.updateTypes(drone);
        }

        Phase phase = phaseManager.getCurrentPhase();
        if (phase != null) {
            Commands cmd = phase.getNextMove();
            map.updatePos(cmd);
            return controller.CommandsToJSON(cmd);
        }

        return controller.CommandsToJSON(Commands.STOP);
    }
}
