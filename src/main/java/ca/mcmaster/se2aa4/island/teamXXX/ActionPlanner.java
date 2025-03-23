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
