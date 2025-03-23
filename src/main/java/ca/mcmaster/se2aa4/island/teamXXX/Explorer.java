package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import ca.mcmaster.se2aa4.island.teamXXX.DecisionMaker;
import org.json.JSONObject;
import org.json.JSONTokener;


public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();
    private DecisionMaker decisionMaker = new DecisionMaker();


/**
 * Sets up initial mission parameters. 
 * First method called by framework.
 * 
 * @param s JSON configuration: {
 *   "heading": initial_direction,
 *   "budget": starting_battery
 * }
 */
    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}",info.toString(2));
        String direction = info.getString("heading");
        Integer batteryLevel = info.getInt("budget");
        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);
        //pass the info to drone 
        decisionMaker.initializeDrone(s);
        decisionMaker.setThres();
    }

/** 
 * Generates next exploration command.
 * Called repeatedly by framework during mission.
 * 
 * @return JSON command string for ACE engine
 */
    @Override
    public String takeDecision() {
        JSONObject decision = decisionMaker.nextCommand();
        logger.info("** Decision: {}",decision.toString());
        return decision.toString();
    }
/**
 * Processes action outcomes from previous command.
 * 
 * @param s JSON response containing:
 *   - "cost": action energy expenditure
 *   - "status": operation success/failure
 *   - "extras": sensor data payload
 */
    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Response received:\n"+response.toString(2));
        Integer cost = response.getInt("cost");
        logger.info("The cost of the action was {}", cost);
        String status = response.getString("status");
        logger.info("The status of the drone is {}", status);
        JSONObject extraInfo = response.getJSONObject("extras");
        logger.info("Additional information received: {}", extraInfo);
        //pass the info to drone 
        decisionMaker.refreshDrone(s); 
        logger.info(response);
    }

/**
 * Final mission output after exploration completes.
 * 
 * @return Identified creek ID or "no creek found"
 */
    @Override
    public String deliverFinalReport() {
        String result = decisionMaker.CreekFound();
        logger.info("This is the creek/inlet to be returned: " + result);
        if (Objects.equals(result, "NA")) {
            return "no creek found";
        }
        return result;
    }

}
