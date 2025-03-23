package ca.mcmaster.se2aa4.island.teamXXX;

/**
 * Represents the finite state machine (FSM) states for drone operation control.
 * Manages progression through mission phases using explicit state transitions.
 *
 * <p>Used to coordinate sequential behaviors like island discovery, scanning,
 * and emergency stopping. States should transition via {@code changeCondition}.</p>
 */

public enum Condition {

    START,
    DISCOVER,
    GO_TO_ISLAND,
    PRESCAN,
    SCAN,
    UTURN,
    EVAL_ECHO,
    STOP;

    /**
 * Progresses to the next logical state in mission workflow.
 * 
 * <p><b>Transition Rules:</b>
 * <ul>
 *   <li>START → DISCOVER</li>
 *   <li>DISCOVER → GO_TO_ISLAND</li>
 *   <li>GO_TO_ISLAND → PRESCAN</li>
 *   <li>PRESCAN → SCAN</li>
 *   <li>SCAN → STOP</li>
 *   <li>Unhandled states return original value</li>
 * </ul>
 *
 * @param cond Current state to transition from
 * @return Next state in sequence
 * 
 * @example <caption>Sequential Transition</caption>
 * Condition.START.changeCondition(Condition.START); // returns DISCOVER
 * Condition.SCAN.changeCondition(Condition.SCAN);   // returns STOP
 */
    public Condition changeCondition(Condition cond) {

        switch (cond) {
            case START -> cond = DISCOVER;
            case DISCOVER -> cond = GO_TO_ISLAND;
            case GO_TO_ISLAND -> cond = PRESCAN;
            case PRESCAN -> cond = SCAN;
            case SCAN -> cond = STOP;
        }
        return cond;
    }
}
