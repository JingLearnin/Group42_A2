package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class SearchingIslandStage implements Stages {

    private final Logger logger = LogManager.getLogger();
    private final ActionHandler actionHandler;
    private final Direction directionHandler;
    private final Queue<JSONObject> moveQueue;
    private final List<Coordinate> creeksFound;

    private boolean foundOcean = false;
    private int groundRange = 0;
    private int flipCount = 0; // ✅ Track number of flips

    private enum SearchState {
        INITIAL_SCAN, MOVE_FORWARD, TORF, FLIP, SCAN_AREA, CROSSING, STOP
    }

    private SearchState currentState = SearchState.INITIAL_SCAN;

    public SearchingIslandStage(Direction directionHandler, ActionHandler actionHandler) {
        this.directionHandler = directionHandler;
        this.actionHandler = actionHandler;
        this.moveQueue = new LinkedList<>();
        this.creeksFound = new ArrayList<>();

        logger.info("Transitioning to SearchingIslandStage - Beginning structured search.");
    }

    @Override
    public JSONObject decide() {
        if (!moveQueue.isEmpty()) {
            return moveQueue.poll();
        }

        switch (currentState) {
            case INITIAL_SCAN:
                logger.info("[INITIAL_SCAN] Scanning ground...");
                moveQueue.offer(actionHandler.createScan());
                break;

            case MOVE_FORWARD:
                logger.info("[MOVE_FORWARD] No ocean found, moving forward and scanning again...");
                directionHandler.updatePosition();
                moveQueue.offer(actionHandler.createFly());
                moveQueue.offer(actionHandler.createScan());
                break;

            case TORF:
                logger.info("[TORF] Ocean detected, echoing in forward direction...");
                moveQueue.offer(actionHandler.createEcho(directionHandler.getCurrentHeading()));
                break;

            case FLIP:
                logger.info("[FLIP] Performing a U-turn using two turns...");

                boolean turnLeft = flipCount % 2 == 0; // Even flipCount = left turn, odd = right turn

                for (int i = 0; i < 2; i++) {
                    String newHeading = turnLeft
                            ? directionHandler.getLeftDirection()
                            : directionHandler.getRightDirection();
                    moveQueue.offer(actionHandler.createHeading(newHeading));
                    directionHandler.setHeading(newHeading);
                }

                flipCount++; // ✅ Increment flip count
                currentState = SearchState.SCAN_AREA; // ✅ Always scan after flipping
                break;

            case SCAN_AREA:
                logger.info("[SCAN_AREA] Scanning the new area after flipping...");
                moveQueue.offer(actionHandler.createScan());
                currentState = SearchState.TORF;
                break;

            case CROSSING:
                if (groundRange > 0) {
                    logger.info("[CROSSING] Flying forward " + groundRange + " times...");
                    for (int i = 0; i < groundRange; i++) {
                        directionHandler.updatePosition();
                        moveQueue.offer(actionHandler.createFly());
                    }
                    currentState = SearchState.TORF;
                } else {
                    logger.info("[CROSSING] Ground range is 0, continuing forward and scanning.");
                    currentState = SearchState.MOVE_FORWARD;
                }
                break;

            case STOP:
                logger.info("[STOP] Mission complete. Stopping...");
                moveQueue.offer(actionHandler.createStop());
                break;

            default:
                throw new IllegalStateException("Undefined state: " + currentState);
        }

        return moveQueue.poll();
    }

    @Override
    public void react(JSONObject response) {
        int cost = response.getInt("cost");
        logger.info("Energy used: " + cost);

        JSONObject extras = response.getJSONObject("extras");

        switch (currentState) {
            case INITIAL_SCAN:
            case MOVE_FORWARD:
                if (extras.has("biomes")) {
                    JSONArray biomes = extras.getJSONArray("biomes");
                    List<String> biomeList = biomes.toList().stream().map(Object::toString).collect(Collectors.toList());

                    if (biomeList.size() == 1 && biomeList.contains("OCEAN")) {
                        logger.info("[INITIAL_SCAN] Strict ocean detected (ONLY ocean), transitioning to TORF.");
                        currentState = SearchState.TORF;
                    } else {
                        logger.info("[INITIAL_SCAN] No strict ocean detected, continuing MOVE_FORWARD.");
                        currentState = SearchState.MOVE_FORWARD;
                    }
                }
                break;

            case TORF:
                if (extras.has("found")) {
                    String found = extras.getString("found");
                    groundRange = extras.getInt("range");

                    if (found.equals("OUT_OF_RANGE")) {
                        logger.info("[TORF] Out of range detected, transitioning to FLIP.");
                        currentState = SearchState.FLIP;
                    } else if (found.equals("GROUND")) {
                        if (groundRange > 0) {
                            logger.info("[TORF] Ground detected at range " + groundRange + ", transitioning to CROSSING.");
                            currentState = SearchState.CROSSING;
                        } else {
                            logger.info("[TORF] Ground detected but range is 0. Continuing forward and scanning instead.");
                            currentState = SearchState.MOVE_FORWARD;
                        }
                    }
                }
                break;

            case STOP:
                logger.info("[STOP] Mission stopped.");
                break;

            default:
                break;
        }

        // ✅ Stop when a creek or site is found
        if (extras.has("creeks") || extras.has("sites")) {
            JSONArray creeksArray = extras.optJSONArray("creeks");
            JSONArray sitesArray = extras.optJSONArray("sites");

            if ((creeksArray != null && !creeksArray.isEmpty()) || (sitesArray != null && !sitesArray.isEmpty())) {
                logger.info("[FOUND LOCATION] Stopping mission - Creek or Site Found.");
                currentState = SearchState.STOP;
            }
        }
    }

    public String getCreekLocations() {
        if (creeksFound.isEmpty()) {
            return "No creeks found.";
        }
        return creeksFound.stream()
                .map(coordinate -> "(" + coordinate.getX() + ", " + coordinate.getY() + ")")
                .collect(Collectors.joining(", "));
    }
}
