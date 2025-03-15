package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import java.util.Queue;
import java.util.LinkedList;

public class GoIslandStage implements Stages {

    private final Logger logger = LogManager.getLogger();
    
    private ActionHandler actionHandler;
    private Direction directionHandler;
    private Queue<JSONObject> moveQueue;
    private String currentHeading;
    private int batteryLevel;
    private String previousAction;
    private String lastEchoDirection = "";
    private boolean transitionToSearching = false;

    public GoIslandStage(String initialHeading) {
        this.currentHeading = initialHeading;
        this.batteryLevel = 10000;
        this.moveQueue = new LinkedList<>();
        this.directionHandler = new Direction(initialHeading, new Coordinate(0, 0));
        this.actionHandler = new ActionHandler(directionHandler);
    }

    @Override
    public JSONObject decide() {
        if (transitionToSearching) {
            return null; // Signal transition to the next stage
        }

        JSONObject currentAction = new JSONObject();

        if (!moveQueue.isEmpty()) {
            currentAction = moveQueue.poll();
        } else {
            actionHandler.echoAll(moveQueue, currentHeading, directionHandler);
            currentAction = moveQueue.poll();
        }

        this.previousAction = currentAction.getString("action");
        return currentAction;
    }

    @Override
    public void react(JSONObject response) {
        logger.info("Current Position: (" + directionHandler.getCurrentHeading() + ") X: " + directionHandler.getPosition().getX() + ", Y: " + directionHandler.getPosition().getY());
        
        int cost = response.getInt("cost");
        this.batteryLevel -= cost;

        if (previousAction.equals("echo")) {
            int range = response.getJSONObject("extras").getInt("range");
            String found = response.getJSONObject("extras").getString("found");

            if (found.equals("GROUND")) {
                moveQueue.clear();
                if (range == 0) {
                    transitionToSearching = true;
                    return;
                }
                if (!currentHeading.equals(lastEchoDirection)) {
                    moveQueue.offer(actionHandler.createHeading(lastEchoDirection));
                    directionHandler.setHeading(lastEchoDirection);
                    currentHeading = lastEchoDirection;
                }
                for (int i = 0; i < range; i++) {
                    moveQueue.offer(actionHandler.createFly());
                }
                moveQueue.offer(actionHandler.createScan());
            } else if (found.equals("OUT_OF_RANGE")) {
                for (String direction : new String[]{currentHeading, directionHandler.getLeftDirection(), directionHandler.getRightDirection()}) {
                    moveQueue.offer(actionHandler.createEcho(direction));
                    lastEchoDirection = direction;
                }
                moveQueue.offer(actionHandler.createFly());
            }
        }
    }

    public boolean isTransitionToSearching() {
        return transitionToSearching;
    }

    public Direction getDirectionHandler() {
        return directionHandler;
    }

    public ActionHandler getActionHandler() {
        return actionHandler;
    }
}
