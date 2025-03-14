package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;
import java.util.Queue;

public class ActionHandler {

    private Direction directionHandler;

    public ActionHandler(Direction directionHandler) {
        this.directionHandler = directionHandler;
    }

    public void echoAll(Queue<JSONObject> moveQueue, String currentHeading, Direction directionHandler) {
        moveQueue.offer(createEcho(currentHeading));
        moveQueue.offer(createEcho(directionHandler.getLeftDirection()));
        moveQueue.offer(createEcho(directionHandler.getRightDirection()));
    }

    public JSONObject createEcho(String direction) {
        JSONObject echo = new JSONObject();
        echo.put("action", "echo");
        JSONObject params = new JSONObject();
        params.put("direction", direction);
        echo.put("parameters", params);
        return echo;
    }

    public JSONObject createScan() {
        JSONObject scan = new JSONObject();
        scan.put("action", "scan");
        return scan;
    }

    public JSONObject createFly() {
        JSONObject fly = new JSONObject();
        fly.put("action", "fly");
        directionHandler.updatePosition();
        return fly;
    }

    public JSONObject createStop() {
        JSONObject stop = new JSONObject();
        stop.put("action", "stop");
        return stop;
    }

    public JSONObject createHeading(String direction) {
        JSONObject heading = new JSONObject();
        heading.put("action", "heading");
        JSONObject params = new JSONObject();
        params.put("direction", direction);
        heading.put("parameters", params);
        
        directionHandler.setHeading(direction);
        return heading;
    }
}
