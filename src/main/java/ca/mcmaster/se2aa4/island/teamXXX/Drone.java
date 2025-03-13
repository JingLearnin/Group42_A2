package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.*;
import java.io.StringReader;

import eu.ace_design.island.game.actions.Heading;

import java.util.ArrayList;

public class Drone {

    private final Logger log = LogManager.getLogger();
    private Integer energyReserve = 0;
    private Direction currentHeading = Direction.NORTH;
    private String discoveredLocation;
    private Integer scanRange;
    private Boolean isOverWater = false;
    private Boolean creekDetected = false;
    private Boolean siteDetected = false;
    private Boolean islandInView = false;
    public String creekID = "";
    public String siteID = "";

    /*
    Initializes the Drone's energy reserve and heading.
    */
    public void initializeDrone(String jsonInput) {
        JSONObject droneData = new JSONObject(new JSONTokener(new StringReader(jsonInput)));
        energyReserve = droneData.getInt("budget");
        updateHeading(droneData);
    }

    /*
    Updates drone status using the provided JSON response.
    */
    public void refreshDroneStatus(String jsonResponse) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(jsonResponse)));
        log.info(response);

        //Battery check
        if (energyReserve < response.getInt("cost")) {
            log.warn("Insufficient energy for this action.");
        } else {
            energyReserve -= response.getInt("cost");
        }

        if (response.has("extras")) {
            JSONObject extras = response.getJSONObject("extras");
            if (extras.has("found")) {
                discoveredLocation = extras.getString("found");

            }

            if (extras.has("biomes")) {
                JSONArray biomesData = extras.getJSONArray("biomes");
                ArrayList<String> biomesList = new ArrayList<>();
                for (int i = 0; i < biomesData.length(); i++) {
                    biomesList.add(String.valueOf(biomesData.get(i)));
                }
                this.isOverWater = biomesList.contains("OCEAN");
            }

            if (extras.has("range")) {
                scanRange = extras.getInt("range");
                log.info("Updated scan range: " + scanRange);
            }

            if (extras.has("creeks")) {
                JSONArray creeksData = extras.getJSONArray("creeks");

                if (!creeksData.isEmpty()) {
                    for (int i = 0; i < creeksData.length(); i++) {
                        creekID = creeksData.get(i).toString();
                        creekDetected = true;
                    }
                }
            }

            if (extras.has("sites")) {
                JSONArray sitesData = extras.getJSONArray("sites");

                if (!sitesData.isEmpty()) {
                    for (int i = 0; i < sitesData.length(); i++) {
                        siteID = sitesData.get(i).toString();
                        siteDetected = true;
                    }
                }
            }
        }
        updateHeading(response);
    }



    /*
    Updates the drone’s heading based on response data.
    */
    private void updateHeading(JSONObject response) {
        if (response.has("heading")) {
            String newHeading = response.getString("heading");
            log.info("New heading detected: " + newHeading);
            currentHeading = currentHeading.getHeadingFromString(newHeading);
            log.info("Updated heading: " + currentHeading);
        }
    }


    /*
     Getters and Setters
     */

}
