package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.StringReader;
import java.util.ArrayList;

public class Drone {

    private final Logger logger = LogManager.getLogger();
    private Integer budget = 0;
    private Direction heading = Direction.N;
    private String found;
    private Integer range;
    private Boolean water = false;
    private Boolean creek_found = false;
    private Boolean site_found = false;
    private Boolean facing_island = false;
    public String creek = "";
    public String site = "";
    
   /**
 * Initializes operational parameters from mission configuration.
 * 
 * @param s JSON string containing:
 *          - "budget": Initial battery units
 *          - "heading": Starting direction
 * @throws JSONException On malformed input
 */
    public void initializeStats(String s) {
        JSONObject initial = new JSONObject(new JSONTokener(new StringReader(s)));
        setBudget(initial);
        updateHeading(initial);
    }

    /**
 * Updates drone state from action response data.
 * 
 * @param s JSON response containing:
 *          - "cost": Action energy expenditure
 *          - "extras": Sensor readings
 * @throws IllegalStateException When battery budget is exhausted
 */
    public void updateStats(String s) {
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info(info);

        Integer cost = info.getInt( "cost");

        if (budget < cost) {
            logger.warn("You don't have enough battery");
        }

        else {
            budget -= cost;
        }

        
        if (info.has("extras")) {
            JSONObject extraInfo = info.getJSONObject("extras");
            //Updates the "found" parameter and throws an exception if the given JSONObject or needed parameters are empty.
            if (extraInfo.has("found")) {
                found = extraInfo.getString("found");

            }
            if (extraInfo.has("biomes")) {
          
                JSONArray biomes_json = new JSONArray();
                biomes_json = extraInfo.getJSONArray("biomes");
                ArrayList<String> biomes = new ArrayList<String>();

                for (int i = 0; i < biomes_json.length(); i++) {
                    biomes.add(String.valueOf(biomes_json.get(i)));
                }
                determineWater(biomes);
            }

            if (extraInfo.has("range")) {
                range = extraInfo.getInt("range");
                logger.info("This is new range: " + range);
            }
            if (extraInfo.has("creeks")) {
                JSONArray creeks_json = new JSONArray();
                creeks_json = extraInfo.getJSONArray("creeks");

                if (!creeks_json.isEmpty()) {
                    for (int i = 0; i < creeks_json.length(); i++) {
                        creek = creeks_json.get(i).toString();
                        creek_found = true;
                    }
                }
            }

            if (extraInfo.has("sites")) {
                JSONArray sites_json = new JSONArray();
                sites_json = extraInfo.getJSONArray("sites");

                if (!sites_json.isEmpty()) {
                    for (int i = 0; i < sites_json.length(); i++) {
                        site = sites_json.get(i).toString();
                        site_found = true;
                    }
                }
            }     
        }       
        updateHeading(info);
        if (creek_found) {
            logger.info("CREEK FOUND: " + creek);
        }
        if (site_found) {
            logger.info("SITE FOUND: " + site);
        }
    }

    /**
 * Processes action results and updates drone state.
 * 
 * @param s JSON response with structure:
 *        {
 *          "cost": [energy expenditure],
 *          "extras": {
 *            "biomes": [array of strings]?,
 *            "creeks": [array of strings]?,
 *            "sites": [array of strings]?,
 *            "range": [integer]?
 *          }
 *        }
 */
    public void updateScan(JSONObject info) {
        if (info.has("extras")) {
            JSONObject extraInfo = info.getJSONObject("extras");

            if (extraInfo.has("biomes")) {
                //create JSONArray to loop through
                JSONArray biomes_json = new JSONArray();
                biomes_json = extraInfo.getJSONArray("biomes");
                ArrayList<String> biomes = new ArrayList<String>();

                for (int i = 0; i < biomes_json.length(); i++) {
                    biomes.add(String.valueOf(biomes_json.get(i)));
                }
                determineWater(biomes);
            }
        }       
    }

/**
 * Analyzes biome data for water detection.
 * 
 * @param biomes List of biome identifiers from scan
 * @return true if all scanned biomes are oceanic
 */
    private void determineWater(ArrayList<String> biomes) {
        ArrayList<String> waterBiome = new ArrayList<String>();
        waterBiome.add("OCEAN");
        if (waterBiome.equals(biomes)) {
            logger.info("WATER");
            this.water = true;
        } else {
            this.water = false;
        }

    }

/**
 * Extracts initial energy allocation
 * @param initInfo Root JSON configuration object
 */
    private void setBudget(JSONObject initInfo) {
        budget = initInfo.getInt("budget");
        logger.info("This is budget: " + budget);
    }

/**
 * Updates directional orientation from JSON data
 * @param info Contains "heading" field with cardinal direction
 */
    private void updateHeading(JSONObject info) {
        if (info.has("heading")) {
            String current_head = info.getString("heading");
            logger.info("This is current_dir:" + current_head);
            heading = heading.StrToDir(current_head);
            logger.info("the dir: " + heading);
        }
    }


/**
 * @return Current remaining battery units
 */
    public Integer getBudget() {
        return budget;
    }

/**
 * @return Current cardinal heading (immutable Direction enum)
 */
    public Direction getDir() {
        return heading;
    }

    public Integer getRange() {
        return range;
    }

    public void setRange(int value) {
        this.range = value;
    }

    public String getFound() {
        return found;
    }

    public Boolean isWater() {
        return water;
    }

    public void setFacingIsland() {
        this.facing_island = true;
    }
    public Boolean facing_island() {
        return facing_island;
    }
    
    public Boolean getCreekFound() {
        return creek_found;
    }
    public void setCreekFound(Boolean bool) {
        this.creek_found = bool;
    }

    public Boolean getSiteFound() {
        return site_found;
    }
    public void setSiteFound(Boolean bool) {
        this.site_found = bool;
    }

    public void setDir(Direction new_orient) {
        heading = new_orient;
    }

}

