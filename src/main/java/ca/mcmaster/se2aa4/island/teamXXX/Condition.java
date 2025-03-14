package ca.mcmaster.se2aa4.island.teamXXX;

public enum Condition {
    START,
    SEARCH,
    NAVIGATE,
    PRE_SCAN,
    ISLAND_SCAN,
    TURN_AROUND,
    ECHO_READ,
    HALT;

    public Condition changeCondition(Condition state) {

        switch (state) {
            case START -> {
                return Condition.SEARCH;
            }
            case SEARCH -> {
                return Condition.NAVIGATE;
            }
            case NAVIGATE -> {
                return Condition.PRE_SCAN;
            }
            case PRE_SCAN -> {
                return Condition.ISLAND_SCAN;
            }
            case ISLAND_SCAN -> {
                return Condition.HALT;
            }
            default -> {
                return state; 
            }
        }
    }
}
