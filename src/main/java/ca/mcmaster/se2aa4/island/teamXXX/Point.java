package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Point {
    private int x; 
    private int y; 
    public Direction current_orient = Direction.NORTH;
    private final Logger logger = LogManager.getLogger();

    public Point(int x, int y) {
        this.x = x; 
        this.y = y; 
    }

    public int getXCoordinate() {
        return this.x; 
    }

    public int getYCoordinate() {
        return this.y; 
    }

    public void setOrientation(String orient) {
        current_orient = current_orient.getHeadingFromString(orient);
    }

    public void moveForward() {
        switch (this.current_orient) {
            case NORTH -> this.y++; // Move up
            case EAST -> this.x++; // Move right
            case SOUTH -> this.y--; // Move down
            case WEST -> this.x--; // Move left
        }
    }

    public void moveRight() {
        switch (this.current_orient) {
            case NORTH -> {
                this.x++; // Move right
                this.current_orient = Direction.EAST;
            }
            case EAST -> {
                this.y--; // Move down
                this.current_orient = Direction.SOUTH;
            }
            case SOUTH -> {
                this.x--; // Move left
                this.current_orient = Direction.WEST;
            }
            case WEST -> {
                this.y++; // Move up
                this.current_orient = Direction.NORTH;
            }
            default -> logger.error("Drone is Lost");
        }
    }

    public void moveLeft() {
        switch (this.current_orient) {
            case NORTH -> {
                this.x--; // Move left
                this.current_orient = Direction.WEST;
            }
            case EAST -> {
                this.y++; // Move up
                this.current_orient = Direction.NORTH;
            }
            case SOUTH -> {
                this.x++; // Move right
                this.current_orient = Direction.EAST;
            }
            case WEST -> {
                this.y--; // Move down
                this.current_orient = Direction.SOUTH;
            }
            default -> logger.error("Drone is Lost");
        }
    }

    public Double diffBtwnPoints(Integer x1, Integer x2, Integer y1, Integer y2) {
        return Math.sqrt(Math.pow((x2-x1), 2)+Math.pow((y2-y1), 2));
    }

}

