package ca.mcmaster.se2aa4.island.teamXXX;
/**
 * Defines the core contract for exploration state handlers.
 *
 * <p>Primary Purpose:
 * - Standardizes state transition logic
 * - Provides polymorphism foundation for exploration phases
 * - Enables command generation through uniform interface</p>
 *
 * <p>Implementations typically represent distinct mission phases:
 * - Island Approach
 * - Perimeter Scan
 * - Site Investigation
 * - Emergency Return</p>
 */
public interface Phase {
    public Commands getNextMove();
}

