package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum Direction {
    EAST, NORTH, SOUTH, WEST;

    private final Logger log = LogManager.getLogger();

    /*
    Converts a string ("EAST", "NORTH", "SOUTH", "WEST") into a Heading enum.
    If the input is invalid, logs an error and returns a default (NORTH).
    */
    public Direction getHeadingFromString(String directionStr) {
            return switch (directionStr) {
                case "E" -> Direction.EAST;
                case "N" -> Direction.NORTH;
                case "S" -> Direction.SOUTH;
                case "W" -> Direction.WEST;
                default -> {
                    log.info("Invalid heading input: " + directionStr);
                    yield Direction.NORTH; // Default direction
                }
            };
    }

    /*
    Returns the string representation of the current heading.
    */
    public String toDirectionString() {
        return switch (this) {
            case NORTH -> "N";
            case EAST -> "E";
            case SOUTH -> "S";
            case WEST -> "W";
        };
    }

    /*
    Rotates the heading to the right (clockwise).
    */
    public Direction rotateRight(Direction direction) {
        return switch (direction) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.NORTH;
        };
    }

    /*
    Rotates the heading to the left (counterclockwise).
    */
    public Direction rotateLeft(Direction direction) {
        return switch (direction) {
            case NORTH -> Direction.WEST;
            case EAST -> Direction.NORTH;
            case SOUTH -> Direction.EAST;
            case WEST -> Direction.SOUTH;
        };
    }
}
