package ca.mcmaster.se2aa4.island.teamXXX;

public class PhaseManager {
    private final Movement movement;
    private final ScanIsland scanIsland;
    private final OnMap map;

    public PhaseManager(Drone drone, OnMap map) {
        this.movement = new Movement(drone, map);
        this.scanIsland = new ScanIsland(drone, map);
        this.map = map;
    }

    public Phase getCurrentPhase() {
        Condition state = map.getState();
        return switch (state) {
            case DISCOVER, GO_TO_ISLAND -> movement;
            case PRESCAN, SCAN, UTURN, EVAL_ECHO -> scanIsland;
            default -> null;
        };
    }
}
