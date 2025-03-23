package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class DecisionMaker {
    private final Logger logger = LogManager.getLogger();
    private final Drone drone = new Drone();
    private final OnMap map = new OnMap();
    private final Controller controller = new Controller(drone);
    private final ActionPlanner planner = new ActionPlanner(drone, map, controller);

    public JSONObject nextCommand() {
        try {
            return planner.getNextCommand();
        } catch (Exception e) {
            logger.warn("Fallback: emergency STOP due to exception", e);
            return controller.CommandsToJSON(Commands.STOP);
        }
    }

    public void initializeDrone(String s) {
        drone.initializeStats(s);
    }

    public void refreshDrone(String s) {
        drone.updateStats(s);
    }

    public void setThres() {
        logger.info((int) (drone.getBudget() * 0.02));
    }

    public String CreekFound() {
        return map.returnCreek();
    }
}
