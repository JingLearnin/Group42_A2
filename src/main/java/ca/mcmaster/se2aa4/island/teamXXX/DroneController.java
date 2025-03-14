package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import java.util.Queue;
import java.util.LinkedList;

public class DroneController {

    private final Logger logger = LogManager.getLogger();
    private Direction directionHandler;
    private Coordinate position;
    private ActionHandler actionHandler;

    private String currentHeading;
    private int batteryLevel;
    private Queue<JSONObject> moveQueue;
    private String previousAction;
    public boolean landFound = false;
    public boolean isOnPath = false;
    public boolean atIsland = false;
    private String orientation = "";
    private boolean rotate = false;
    private String lastEchoDirection = "";

    public DroneController(String initialHeading, int initialBatteryLevel) {
        this.currentHeading = initialHeading;
        this.batteryLevel = initialBatteryLevel;
        this.moveQueue = new LinkedList<>();
        this.directionHandler = new Direction(initialHeading);
        this.position = new Coordinate(0, 0); // Initialize position at (0,0)
        this.actionHandler = new ActionHandler();
    }

    public JSONObject decide() {
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

    public void react(JSONObject response) {
        logger.info("Current Position: (" + position.getX() + ", " + position.getY() + ")");
        int cost = response.getInt("cost");
        this.batteryLevel -= cost;

        if (previousAction.equals("echo")) {
            int range = response.getJSONObject("extras").getInt("range");
            String found = response.getJSONObject("extras").getString("found");

            if (found.equals("GROUND")) {
                moveQueue.clear();
                if (range == 0) {
                    moveQueue.offer(actionHandler.createStop());
                    return;
                }
                if (!currentHeading.equals(lastEchoDirection)) {
                    moveQueue.offer(actionHandler.createHeading(lastEchoDirection));
                    currentHeading = lastEchoDirection;
                }
                for (int i = 0; i < range; i++) {
                    moveQueue.offer(actionHandler.createFly());
                    updatePosition();
                }
                moveQueue.offer(actionHandler.createScan());
                landFound = true;
            } else if (found.equals("OUT_OF_RANGE")) {
                boolean allOutOfRange = true;
                for (String direction : new String[]{currentHeading, directionHandler.getLeftDirection(), directionHandler.getRightDirection()}) {
                    moveQueue.offer(actionHandler.createEcho(direction));
                    lastEchoDirection = direction;
                }
                if (allOutOfRange) {
                    moveQueue.offer(actionHandler.createFly());
                    updatePosition();
                }
            }
        }
    }

    private void updatePosition() {
        switch (currentHeading) {
            case "N": position.changeY(1); break;
            case "S": position.changeY(-1); break;
            case "E": position.changeX(1); break;
            case "W": position.changeX(-1); break;
        }
    }
}
