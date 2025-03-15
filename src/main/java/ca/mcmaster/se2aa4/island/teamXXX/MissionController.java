package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class MissionController {
    
    private final Logger logger = LogManager.getLogger();
    private Stages currentStage;
    private GoIslandStage goIslandStage;
    private SearchingIslandStage searchingIslandStage;
    
    public MissionController(String initialHeading) {
        this.goIslandStage = new GoIslandStage(initialHeading);
        this.searchingIslandStage = new SearchingIslandStage(goIslandStage.getDirectionHandler(), goIslandStage.getActionHandler());
        this.currentStage = goIslandStage;
    }

    public JSONObject decide() {
        logger.info("Current Stage: " + currentStage.getClass().getSimpleName());

        if (currentStage instanceof GoIslandStage && ((GoIslandStage) currentStage).isTransitionToSearching()) {
            logger.info("Transitioning to SearchingIslandStage...");
            currentStage = searchingIslandStage;
        }

        return currentStage.decide();
    }

    public void react(JSONObject response) {
        currentStage.react(response);
    }

    public Stages getCurrentStage() {
        return currentStage;
    }
}
