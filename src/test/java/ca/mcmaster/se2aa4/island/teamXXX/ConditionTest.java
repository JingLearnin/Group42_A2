package ca.mcmaster.se2aa4.island.teamXXX;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ConditionTest {

    @Test
    public void testTransitionFromStart() {
        // When in START state, it should transition to DISCOVER.
        Condition next = Condition.START.changeCondition(Condition.START);
        assertEquals(Condition.DISCOVER, next, "START should transition to DISCOVER");
    }
    
    @Test
    public void testTransitionFromDiscover() {
        // When in DISCOVER state, it should transition to GO_TO_ISLAND.
        Condition next = Condition.DISCOVER.changeCondition(Condition.DISCOVER);
        assertEquals(Condition.GO_TO_ISLAND, next, "DISCOVER should transition to GO_TO_ISLAND");
    }
    
    @Test
    public void testTransitionFromGoToIsland() {
        // When in GO_TO_ISLAND state, it should transition to PRESCAN.
        Condition next = Condition.GO_TO_ISLAND.changeCondition(Condition.GO_TO_ISLAND);
        assertEquals(Condition.PRESCAN, next, "GO_TO_ISLAND should transition to PRESCAN");
    }
    
    @Test
    public void testTransitionFromPrescan() {
        // When in PRESCAN state, it should transition to SCAN.
        Condition next = Condition.PRESCAN.changeCondition(Condition.PRESCAN);
        assertEquals(Condition.SCAN, next, "PRESCAN should transition to SCAN");
    }
    
    @Test
    public void testTransitionFromScan() {
        // When in SCAN state, it should transition to STOP.
        Condition next = Condition.SCAN.changeCondition(Condition.SCAN);
        assertEquals(Condition.STOP, next, "SCAN should transition to STOP");
    }
    
    @Test
    public void testUnhandledStatesRemainUnchanged() {
        // UTURN, EVAL_ECHO, and STOP are unhandled states and should remain unchanged.
        Condition nextUturn = Condition.UTURN.changeCondition(Condition.UTURN);
        assertEquals(Condition.UTURN, nextUturn, "UTURN should remain UTURN");

        Condition nextEvalEcho = Condition.EVAL_ECHO.changeCondition(Condition.EVAL_ECHO);
        assertEquals(Condition.EVAL_ECHO, nextEvalEcho, "EVAL_ECHO should remain EVAL_ECHO");

        Condition nextStop = Condition.STOP.changeCondition(Condition.STOP);
        assertEquals(Condition.STOP, nextStop, "STOP should remain STOP");
    }
}
