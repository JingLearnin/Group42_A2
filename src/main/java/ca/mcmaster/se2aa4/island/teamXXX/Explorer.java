package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();
    private DroneController controller;

    @Override
    public void initialize(String input) {
        logger.info(">> Initializing Exploration System...");
        JSONObject data = new JSONObject(new JSONTokener(new StringReader(input)));
        logger.info(">> Initialization Data:\n{}", data.toString(2));

        String startingDirection = data.getString("heading");
        int batteryLevel = data.getInt("budget");

        controller = new DroneController(startingDirection, batteryLevel);
        logger.info(">> Initial Direction: {}", startingDirection);
        logger.info(">> Battery Level: {}", batteryLevel);
    }

    @Override
    public String takeDecision() {
        JSONObject nextMove = controller.decide(); // Calls `decide()` as expected
        logger.info(">> Next Move: {}", nextMove.toString());
        return nextMove.toString();
    }

    @Override
    public void acknowledgeResults(String feedback) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(feedback)));
        logger.info(">> Processing Response:\n{}", response.toString(2));

        int cost = response.getInt("cost");
        logger.info(">> Energy Used: {}", cost);

        String status = response.getString("status");
        logger.info(">> Drone Status: {}", status);

        JSONObject extraData = response.getJSONObject("extras");
        logger.info(">> Additional Data: {}", extraData);

        controller.react(response); // Calls `react()` which exists in `DroneController`
    }

    @Override
    public String deliverFinalReport() {
        return controller.landFound ? "Island found during exploration." : "Exploration completed without locating the island.";
    }
}
