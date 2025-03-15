package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class SearchingIslandStage implements Stages {

    private final Logger logger = LogManager.getLogger();

    private ActionHandler actionHandler;
    private Direction directionHandler;
    private Queue<JSONObject> moveQueue;

    private boolean reachedEnd = false;
    private SearchState currentState = SearchState.ECHO_SURVEY;
    private boolean finalScan = false;
    private int groundRange;

    private List<Coordinate> creeksFound;
    private List<Coordinate> emergencySites;
    private boolean movingRight = true; // Determines scan direction

    public enum SearchState {
        ECHO_SURVEY,
        MOVE_FORWARD,
        SCAN_AREA,
        CHANGE_DIRECTION
    }

    public SearchingIslandStage(Direction directionHandler, ActionHandler actionHandler) {
        this.directionHandler = directionHandler;
        this.actionHandler = actionHandler;

        this.moveQueue = new LinkedList<>();
        this.creeksFound = new ArrayList<>();
        this.emergencySites = new ArrayList<>();

        logger.info("Transitioned to SearchingIslandStage - Beginning structured grid search.");
    }

    @Override
    public JSONObject decide() {
        // If we already have commands enqueued, just return the next one
        if (!moveQueue.isEmpty()) {
            return moveQueue.poll();
        } else {
            return executeSearchPattern();
        }
    }

    /**
     * Based on the current state, enqueue the appropriate action(s)
     * in moveQueue, then return the first command.
     */
    private JSONObject executeSearchPattern() {
        switch (currentState) {
            case ECHO_SURVEY:
                logger.info("[ECHO_SURVEY] Performing echoAll before movement...");
                actionHandler.echoAll(moveQueue, directionHandler.getCurrentHeading(), directionHandler);
                currentState = SearchState.MOVE_FORWARD; // Move forward after the echo
                break;

            case MOVE_FORWARD:
                logger.info("[MOVE_FORWARD] Moving forward in structured search pattern...");
                moveQueue.offer(actionHandler.createFly());

                // After moving, transition to scanning
                currentState = SearchState.SCAN_AREA;
                break;

            case SCAN_AREA:
                logger.info("[SCAN_AREA] Scanning current position...");
                moveQueue.offer(actionHandler.createScan());
                break;

            case CHANGE_DIRECTION:
                logger.info("[CHANGE_DIRECTION] Turning for next search line...");
                if (movingRight) {
                    moveQueue.offer(actionHandler.turnRight());
                } else {
                    moveQueue.offer(actionHandler.turnLeft());
                }
                movingRight = !movingRight; // Flip direction
                currentState = SearchState.MOVE_FORWARD; // Resume movement after turn
                break;

            default:
                throw new IllegalStateException("Undefined state: " + currentState);
        }

        // Return first enqueued action
        return moveQueue.poll();
    }

    @Override
    public void react(JSONObject response) {
        // Logging energy use
        int cost = response.getInt("cost");
        logger.info("Energy used: " + cost);

        JSONObject extras = response.getJSONObject("extras");

        // Extract response safely
        this.groundRange = extras.has("range") ? extras.getInt("range") : 0;
        String found = extras.has("found") ? extras.getString("found") : "UNKNOWN";

        logger.info("Drone Response - Found: " + found + ", Range: " + groundRange);

        switch (currentState) {
            case ECHO_SURVEY:
                if ("OUT_OF_RANGE".equals(found)) {
                    finalScan = true; // Plan final scan if needed
                }
                currentState = SearchState.MOVE_FORWARD; // Proceed to move
                break;

            case MOVE_FORWARD:
                if (groundRange > 0) {
                    groundRange--; 
                    logger.info("Moving forward, remaining range: " + groundRange);
                    currentState = SearchState.MOVE_FORWARD;
                } else {
                    logger.info("Reached movement limit -> Switching to SCAN_AREA");
                    currentState = SearchState.SCAN_AREA;
                }
                break;

            case SCAN_AREA:
                List<String> biomes = new ArrayList<>();
                if (extras.has("biomes")) {
                    biomes = extras.getJSONArray("biomes")
                                   .toList()
                                   .stream()
                                   .map(Object::toString)
                                   .collect(Collectors.toList());
                }

                if (biomes.isEmpty() && found.equals("UNKNOWN")) {
                    // If scanning still yields nothing, switch directions
                    logger.info("Scan yielded no useful info, changing direction.");
                    currentState = SearchState.CHANGE_DIRECTION;
                } else if (!biomes.contains("OCEAN")) {
                    logger.info("No ocean detected, continuing forward.");
                    currentState = SearchState.MOVE_FORWARD;
                } else {
                    logger.info("Ocean detected! Changing direction.");
                    currentState = SearchState.CHANGE_DIRECTION;
                }

                if (extras.has("creeks")) {
                    logger.info("Creeks found: " + extras.getJSONArray("creeks").toString());
                    creeksFound.add(directionHandler.getPosition());
                }

                if (extras.has("sites")) {
                    logger.info("Emergency sites found: " + extras.getJSONArray("sites").toString());
                    emergencySites.add(directionHandler.getPosition());
                }
                break;

            case CHANGE_DIRECTION:
                movingRight = !movingRight;
                logger.info("Direction changed, resuming search.");
                currentState = SearchState.MOVE_FORWARD;
                break;

            default:
                throw new IllegalStateException("Undefined state: " + currentState);
        }
    }

    /**
     * Returns all discovered creek locations as a string.
     */
    public String getCreekLocations() {
        StringBuilder creekLocations = new StringBuilder();
        for (Coordinate creek : creeksFound) {
            creekLocations.append("(")
                          .append(creek.getX())
                          .append(", ")
                          .append(creek.getY())
                          .append(") ");
        }
        return creekLocations.toString();
    }

    /**
     * If you want to end after a certain condition, set reachedEnd = true
     */
    public boolean reachedEnd() {
        return this.reachedEnd;
    }

    public String deliverFinalReport() {
        return "Mission complete. Creeks found: " + creeksFound.size()
               + ", Emergency Sites found: " + emergencySites.size();
    }
}
