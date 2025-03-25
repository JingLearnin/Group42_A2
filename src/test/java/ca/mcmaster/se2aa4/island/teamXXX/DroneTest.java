package ca.mcmaster.se2aa4.island.teamXXX;

import static org.junit.jupiter.api.Assertions.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class DroneTest {

    @Test
    public void testInitializeStats() {
        Drone drone = new Drone();
        String configJson = "{\"budget\": 1000, \"heading\": \"E\"}";
        drone.initializeStats(configJson);
        
        // After initialization, the budget should be set to 1000.
        assertEquals(1000, drone.getBudget(), "Budget should be initialized to 1000");
        
        
        assertEquals(Direction.E, drone.getDir(), "Heading should be updated to E");
    }

    @Test
    public void testUpdateStatsBudgetDeduction() {
        Drone drone = new Drone();
        // Initialize with budget 500 and heading "N"
        String configJson = "{\"budget\": 500, \"heading\": \"N\"}";
        drone.initializeStats(configJson);
        
        // Update stats with a cost of 100 and no extras.
        String updateJson = "{\"cost\": 100}";
        drone.updateStats(updateJson);
        
        // Expect budget to be reduced: 500 - 100 = 400.
        assertEquals(400, drone.getBudget(), "Budget should be deducted correctly");
    }

    @Test
    public void testUpdateStatsWithExtrasAndBiomes() {
        Drone drone = new Drone();
        String configJson = "{\"budget\": 1000, \"heading\": \"N\"}";
        drone.initializeStats(configJson);
        
        // Build extras JSON with found, biomes, range, creeks, and sites.
        JSONObject extras = new JSONObject();
        extras.put("found", "GROUND");
        
        // To simulate water detection, use exactly ["OCEAN"].
        JSONArray biomes = new JSONArray();
        biomes.put("OCEAN");
        extras.put("biomes", biomes);
        
        // Provide a range value.
        extras.put("range", 50);
        
        // Add a creek and a site.
        JSONArray creeks = new JSONArray();
        creeks.put("creek1");
        extras.put("creeks", creeks);
        JSONArray sites = new JSONArray();
        sites.put("site1");
        extras.put("sites", sites);
        
        // Assemble the update JSON.
        JSONObject update = new JSONObject();
        update.put("cost", 200);
        update.put("extras", extras);
        
        drone.updateStats(update.toString());
        
        // Expect budget: 1000 - 200 = 800.
        assertEquals(800, drone.getBudget(), "Budget should be deducted correctly");
        
        // Check that the 'found' field is updated.
        assertEquals("GROUND", drone.getFound(), "Found should be set to GROUND");
        
        // Range should be updated.
        assertEquals(50, drone.getRange(), "Range should be updated to 50");
        
        // Check that creek and site info are captured.
        assertTrue(drone.getCreekFound(), "Creek should be marked as found");
        assertEquals("creek1", drone.creek, "Creek id should be set to creek1");
        assertTrue(drone.getSiteFound(), "Site should be marked as found");
        assertEquals("site1", drone.site, "Site id should be set to site1");
        
        // Since the biomes array is exactly ["OCEAN"], water should be set to true.
        assertTrue(drone.isWater(), "Water should be true when biomes equal ['OCEAN']");
    }

    @Test
    public void testUpdateScanSetsWaterCorrectly() {
        Drone drone = new Drone();
        // Build a JSON object for updateScan with extras containing biomes ["OCEAN"].
        JSONObject extras = new JSONObject();
        JSONArray biomes = new JSONArray();
        biomes.put("OCEAN");
        extras.put("biomes", biomes);
        JSONObject scanResponse = new JSONObject();
        scanResponse.put("extras", extras);
        
        drone.updateScan(scanResponse);
        // Expect water to be set to true.
        assertTrue(drone.isWater(), "Water should be true when biomes equal ['OCEAN']");
        
        // Now change biomes to a non-water value (e.g., ["FOREST"]).
        JSONArray otherBiomes = new JSONArray();
        otherBiomes.put("FOREST");
        extras.put("biomes", otherBiomes);
        scanResponse.put("extras", extras);
        
        drone.updateScan(scanResponse);
        // Expect water to be false.
        assertFalse(drone.isWater(), "Water should be false when biomes are not ['OCEAN']");
    }
}
