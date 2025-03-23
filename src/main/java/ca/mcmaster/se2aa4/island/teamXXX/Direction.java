package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.Objects;


/**
 * Represents cardinal directions and provides directional transformation logic.
 * 
 * <p>Core navigation component handling heading calculations and string conversions.
 * Used by {@code Drone} and {@code Controller} for orientation management.</p>
 */

public enum Direction {

    N, E, S, W;

    private Direction dir;
    private final Logger logger = LogManager.getLogger();

    /**
 * Converts string abbreviation to Direction (case-sensitive).
 * 
 * @param cur_dir  ("N"/"E"/"S"/"W")
 * @return Direction
 * @throws IllegalArgumentException 
 * 
 * @example <caption>Valid Conversion</caption>
 * Direction east = Direction.E.StrToDir("E"); // return E
 * 
 * @example <caption>Invalid Input Handling</caption>
 * Direction invalid = Direction.N.StrToDir("X"); // return null
 */
    public Direction StrToDir(String cur_dir) {

        try {

            if (Objects.equals(cur_dir, "E")) {
                return E;
            } else if (Objects.equals(cur_dir, "N")) {
                return N;
            } else if (Objects.equals(cur_dir, "S")) {
                return S;
            } else if (Objects.equals(cur_dir, "W")) {
                return W;
            }
        } catch (Exception e) {
                logger.info("No valid direction was inputted.");
        }
        return dir;
    }

    /**
 * Returns uppercase letter representation of current direction.
 * 
 * 
 * @return "N", "E", "S", or "W"
 */
    public String DirToStr() {

        try {

            switch (this) {
                case N -> {
                    return "N";
                }
                case E -> {
                    return "E";
                }
                case S -> {
                    return "S";
                }
                case W -> {
                    return "W";
                }
            }
        } catch (Exception e) {
            logger.info("Something went wrong.");
        }
        return "STOP";
    }

    /**
 * Calculates next heading after 90-degree clockwise rotation.
 * 
 * @return New direction without modifying current instance
 */
    public Direction turnR() {
        switch (this) {
            case N -> this.dir = E;
            case E -> this.dir = S;
            case S -> this.dir = W;
            case W -> this.dir = N;
        }
        return this.dir;
    }

    /**
 * Calculates next heading after 90-degree counter-clockwise rotation.
 * 
 * @return New direction without modifying current instance
 */
    public Direction turnL() {
        switch (this) {
            case N -> this.dir = W;
            case E -> this.dir = N;
            case S -> this.dir = E;
            case W -> this.dir = S;
        }
        return this.dir;
    }
}
