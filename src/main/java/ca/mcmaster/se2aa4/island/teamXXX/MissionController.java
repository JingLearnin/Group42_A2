package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class MissionController {
    
    private Stages currentStage;
    private GoIslandStage goIslandStage;
    
    public MissionController(String initialHeading) {
        this.goIslandStage = new GoIslandStage(initialHeading);
        this.currentStage = goIslandStage;
    }

    public JSONObject decide() {
        System.out.println("Current Stage: " + currentStage.getClass().getSimpleName());
        return currentStage.decide();
    }

    public void react(JSONObject response) {
        currentStage.react(response);
    }
}
