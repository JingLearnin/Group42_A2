package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import java.util.Queue;
import java.util.LinkedList;

public class DroneController {

    private final Logger logger = LogManager.getLogger();
    private Direction directionHandler;

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
        this.directionHandler = new Direction(initialHeading); // Fixed reference
    }

    public JSONObject decide() {
        JSONObject currentAction = new JSONObject();

        if (!moveQueue.isEmpty()) {
            currentAction = moveQueue.poll();
        } else {
            echoAll();
            currentAction = moveQueue.poll();
        }

        this.previousAction = currentAction.getString("action");
        return currentAction;
    }

    private void echoAll() {
        moveQueue.offer(createEcho(currentHeading));
        moveQueue.offer(createEcho(directionHandler.getLeftDirection()));
        moveQueue.offer(createEcho(directionHandler.getRightDirection()));
    }

    public void react(JSONObject response) {
        int cost = response.getInt("cost");
        this.batteryLevel -= cost;

        if (previousAction.equals("echo")) {
            int range = response.getJSONObject("extras").getInt("range");
            String found = response.getJSONObject("extras").getString("found");

            if (found.equals("GROUND")) {
                moveQueue.clear();
                if (range == 0) {
                    moveQueue.offer(createStop());
                    return;
                }
                if (!currentHeading.equals(lastEchoDirection)) {
                    moveQueue.offer(createHeading(lastEchoDirection));
                    currentHeading = lastEchoDirection;
                }
                for (int i = 0; i < range; i++) {
                    moveQueue.offer(createFly());
                }
                moveQueue.offer(createScan());
                landFound = true;
            } else if (found.equals("OUT_OF_RANGE")) {
                boolean allOutOfRange = true;
                for (String direction : new String[]{currentHeading, directionHandler.getLeftDirection(), directionHandler.getRightDirection()}) {
                    moveQueue.offer(createEcho(direction));
                    lastEchoDirection = direction;
                }
                if (allOutOfRange) {
                    moveQueue.offer(createFly());
                }
            }
        }
    }

    private JSONObject createEcho(String direction) {
        JSONObject echo = new JSONObject();
        echo.put("action", "echo");
        JSONObject params = new JSONObject();
        params.put("direction", direction);
        echo.put("parameters", params);
        return echo;
    }

    private JSONObject createScan() {
        JSONObject scan = new JSONObject();
        scan.put("action", "scan");
        return scan;
    }

    private JSONObject createFly() {
        JSONObject fly = new JSONObject();
        fly.put("action", "fly");
        return fly;
    }

    private JSONObject createStop() {
        JSONObject stop = new JSONObject();
        stop.put("action", "stop");
        return stop;
    }

    private JSONObject createHeading(String direction) {
        JSONObject heading = new JSONObject();
        heading.put("action", "heading");
        JSONObject params = new JSONObject();
        params.put("direction", direction);
        heading.put("parameters", params);
        return heading;
    }
}
