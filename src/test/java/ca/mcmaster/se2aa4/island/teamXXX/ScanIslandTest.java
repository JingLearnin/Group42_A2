package ca.mcmaster.se2aa4.island.teamXXX;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters.IntegerConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class ScanIslandTest {

    private ScanIsland scanIsland;
    private DummyDrone drone;
    private DummyOnMap map;

    @BeforeEach
    public void setUp() {
        
        drone = new DummyDrone();
        map = new DummyOnMap();
        map.setState(Condition.PRESCAN);
        scanIsland = new ScanIsland(drone, map);
    }

    @Test
    public void testGetNextMove_PrescanFacingIsland() {
        
        drone.setFacingIsland(true);
        Commands command = scanIsland.getNextMove();
        // In the PRESCAN branch, if facing_island() returns true, getNextMove() should return TURN_R.
        assertEquals(Commands.TURN_R, command, "Expected TURN_R when in PRESCAN and facing island.");
        assertEquals(Condition.SCAN, map.getState(), "Map state should change to SCAN after PRESCAN branch.");
    }

    
    private static class DummyDrone extends Drone {
        private boolean facingIsland = false;
        private String found = "";
        private int range = 5;

        public void setFacingIsland(boolean facing) {
            facingIsland = facing;
        }

        @Override
        public Boolean facing_island() {
            return facingIsland;
        }

        @Override
        public String getFound() {
            return found;
        }

        @Override
        public Integer getRange() {
            return range;
        }

        @Override
        public Boolean isWater() {
            return false;
        }
    }

    
    private static class DummyOnMap extends OnMap {
        private Condition state;

        @Override
        public Condition getState() {
            return state;
        }

        @Override
        public void setState(Condition newState) {
            state = newState;
        }
    }
}