package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.stream.Collectors;
import java.util.*;

public class SearchingIslandStage implements Stages {

    private final Logger logger = LogManager.getLogger();
    private final ActionHandler actionHandler;
    private final Direction directionHandler;
    private final Queue<JSONObject> moveQueue;
    private final List<Coordinate> creeksFound;
    private final List<String> creekIDs;

    private int groundRange = 0;
    private boolean flipLeft = true;
    private String intendedFlipDirection = null;

    private enum SearchState {
        INITIAL_SCAN, MOVE_FORWARD, TORF, CHECK_FLIP_SAFETY, PERFORM_FLIP, OPPOSITE_TURN, SCAN_AREA, CROSSING, STOP
    }

    private SearchState currentState = SearchState.INITIAL_SCAN;

    public SearchingIslandStage(Direction directionHandler, ActionHandler actionHandler) {
        this.directionHandler = directionHandler;
        this.actionHandler = actionHandler;
        this.moveQueue = new LinkedList<>();
        this.creeksFound = new ArrayList<>();
        this.creekIDs = new ArrayList<>();

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
                logger.info("[MOVE_FORWARD] Moving forward and scanning again...");
                directionHandler.updatePosition();
                moveQueue.offer(actionHandler.createFly());
                moveQueue.offer(actionHandler.createScan());
                break;

            case TORF:
                logger.info("[TORF] Echoing in current direction...");
                moveQueue.offer(actionHandler.createEcho(directionHandler.getCurrentHeading()));
                break;

            case CHECK_FLIP_SAFETY:
                intendedFlipDirection = flipLeft
                        ? directionHandler.getLeftDirection()
                        : directionHandler.getRightDirection();
                logger.info("[CHECK_FLIP_SAFETY] Echoing in flip direction: {}", intendedFlipDirection);
                moveQueue.offer(actionHandler.createEcho(intendedFlipDirection));
                break;

            case PERFORM_FLIP:
                logger.info("[PERFORM_FLIP] Flipping 2x toward {}", flipLeft ? "LEFT" : "RIGHT");
                for (int i = 0; i < 2; i++) {
                    String turnDir = flipLeft
                            ? directionHandler.getLeftDirection()
                            : directionHandler.getRightDirection();
                    moveQueue.offer(actionHandler.createHeading(turnDir));
                    directionHandler.setHeading(turnDir);
                }
                flipLeft = !flipLeft;
                currentState = SearchState.SCAN_AREA;
                break;

            case OPPOSITE_TURN:
                logger.warn("[OPPOSITE_TURN] Flip too dangerous. Turning 3x in opposite direction.");
                for (int i = 0; i < 3; i++) {
                    String turnDir = flipLeft
                            ? directionHandler.getRightDirection()
                            : directionHandler.getLeftDirection();
                    moveQueue.offer(actionHandler.createHeading(turnDir));
                    directionHandler.setHeading(turnDir);
                }
                flipLeft = !flipLeft;
                currentState = SearchState.SCAN_AREA;
                break;

            case SCAN_AREA:
                logger.info("[SCAN_AREA] Scanning after turn...");
                moveQueue.offer(actionHandler.createScan());
                currentState = SearchState.TORF;
                break;

            case CROSSING:
                logger.info("[CROSSING] Crossing {} tiles forward...", groundRange);
                for (int i = 0; i < groundRange; i++) {
                    directionHandler.updatePosition();
                    moveQueue.offer(actionHandler.createFly());
                }
                currentState = SearchState.TORF;
                break;

            case STOP:
                logger.info("[STOP] Stopping mission...");
                moveQueue.offer(actionHandler.createStop());
                break;

            default:
                throw new IllegalStateException("Unknown state: " + currentState);
        }

        return moveQueue.poll();
    }

    @Override
    public void react(JSONObject response) {
        int cost = response.getInt("cost");
        JSONObject extras = response.getJSONObject("extras");

        logger.info("Energy used: {}", cost);
        logger.info("[DEBUG] Full extras response: {}", extras.toString(2));

        switch (currentState) {
            case INITIAL_SCAN:
            case MOVE_FORWARD:
                if (extras.has("biomes")) {
                    JSONArray biomes = extras.getJSONArray("biomes");
                    List<String> biomeList = biomes.toList().stream().map(Object::toString).toList();

                    if (biomeList.size() == 1 && biomeList.contains("OCEAN")) {
                        logger.info("[INITIAL_SCAN] Only ocean detected. Transitioning to TORF.");
                        currentState = SearchState.TORF;
                    } else {
                        logger.info("[INITIAL_SCAN] Land detected. Move forward.");
                        currentState = SearchState.MOVE_FORWARD;
                    }
                }
                break;

            case TORF:
                if (extras.has("found")) {
                    String found = extras.getString("found");
                    groundRange = extras.getInt("range");

                    if (found.equals("OUT_OF_RANGE")) {
                        logger.warn("[TORF] Out of range. Checking flip safety...");
                        currentState = SearchState.CHECK_FLIP_SAFETY;
                    } else if (found.equals("GROUND")) {
                        if (groundRange > 0) {
                            logger.info("[TORF] Ground at range {}. Crossing.", groundRange);
                            currentState = SearchState.CROSSING;
                        } else {
                            logger.info("[TORF] Ground at range 0. Move forward.");
                            currentState = SearchState.MOVE_FORWARD;
                        }
                    }
                }
                break;

            case CHECK_FLIP_SAFETY:
                if (extras.has("found") && extras.has("range")) {
                    String found = extras.getString("found");
                    int range = extras.getInt("range");

                    if (found.equals("OUT_OF_RANGE") && range < 2) {
                        logger.warn("[CHECK_FLIP_SAFETY] Flip too risky (range {}). Turning opposite.", range);
                        currentState = SearchState.OPPOSITE_TURN;
                    } else {
                        logger.info("[CHECK_FLIP_SAFETY] Flip is safe. Proceeding.");
                        currentState = SearchState.PERFORM_FLIP;
                    }
                }
                break;

            case STOP:
                logger.info("[STOP] Mission completed or aborted.");
                break;

            default:
                break;
        }

        if (extras.has("creeks")) {
            JSONArray creeks = extras.getJSONArray("creeks");
            if (!creeks.isEmpty()) {
                for (int i = 0; i < creeks.length(); i++) {
                    String creekId = creeks.getString(i);
                    if (!creekIDs.contains(creekId)) {
                        creekIDs.add(creekId);
                        creeksFound.add(new Coordinate(
                                directionHandler.getPosition().getX(),
                                directionHandler.getPosition().getY()
                        ));
                        logger.info("[FOUND CREEK] ID: {} at ({}, {})", creekId,
                                directionHandler.getPosition().getX(),
                                directionHandler.getPosition().getY());
                    }
                }

                if (creekIDs.size() >= 5) {
                    logger.info("[MISSION COMPLETE] Found 5 creeks. Listing all:");
                    for (int i = 0; i < creekIDs.size(); i++) {
                        Coordinate coord = creeksFound.get(i);
                        logger.info("Creek {}: ID = {}, Position = ({}, {})",
                                i + 1, creekIDs.get(i), coord.getX(), coord.getY());
                    }
                    currentState = SearchState.STOP;
                }
            }
        }
    }

    public String deliverFinalReport() {
        if (creekIDs.isEmpty()) {
            return "No creeks found.";
        }
        return "Creek IDs:\n" + creekIDs.stream()
                .map(id -> "- " + id)
                .collect(Collectors.joining("\n"));
    }

    public String getCreekLocations() {
        if (creeksFound.isEmpty()) {
            return "No creeks found.";
        }
        return creeksFound.stream()
                .map(c -> "(" + c.getX() + ", " + c.getY() + ")")
                .collect(Collectors.joining(", "));
    }
}
