package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import java.util.Queue;
import java.util.LinkedList;

public class DroneController {

    private final Logger logger = LogManager.getLogger();

    String currentHeading;
    int batteryLevel;
    Queue<JSONObject> moveQueue;
    String previousAction;
    Boolean landFound = false;

    public DroneController(String initialHeading, int initialBatteryLevel) {

        // Set initial heading and battery level for the drone
        this.currentHeading = initialHeading;
        this.batteryLevel = initialBatteryLevel;

        // Initialize a move queue for the drone
        this.moveQueue = new LinkedList<>();
    }

    // Decides the next moves for the drone
    public JSONObject decide() {
        JSONObject currentAction = new JSONObject();
        if (!moveQueue.isEmpty()) {
            // Take a move from the queue
            currentAction = moveQueue.poll();
        } else {
            // Otherwise, echo, scan, and fly
            currentAction.put("action", "echo");
            JSONObject parameters = new JSONObject();
            parameters.put("direction", "S");
            currentAction.put("parameters", parameters);

            JSONObject scan = new JSONObject();
            scan.put("action", "scan");
            moveQueue.offer(scan);

            JSONObject fly = new JSONObject();
            fly.put("action", "fly");
            moveQueue.offer(fly);
        }
        this.previousAction = currentAction.getString("action");
        return currentAction;
    }

    // Reacts to information returned by the game engine
    public void react(JSONObject response) {
        // Update battery level
        int cost = response.getInt("cost");
        this.batteryLevel -= cost;

        if (previousAction.equals("echo")) {

            // When in front of island, scan and stop
            int range = response.getJSONObject("extras").getInt("range");
            if (range == 0) {
                JSONObject scan = new JSONObject();
                scan.put("action", "scan");
                moveQueue.offer(scan);
                JSONObject stop = new JSONObject();
                stop.put("action", "stop");
                moveQueue.offer(stop);
            }

            // Change heading when the island is found
            String found = response.getJSONObject("extras").getString("found");
            if (!found.equals("OUT_OF_RANGE") && !landFound) {
                JSONObject scan = new JSONObject();
                scan.put("action", "scan");
                moveQueue.offer(scan);
                JSONObject changeHeading = new JSONObject();
                changeHeading.put("action", "heading");
                JSONObject parameters = new JSONObject();
                parameters.put("direction", "S");
                changeHeading.put("parameters", parameters);
                moveQueue.offer(changeHeading);
                landFound = true;
            }
        }
    }
}
