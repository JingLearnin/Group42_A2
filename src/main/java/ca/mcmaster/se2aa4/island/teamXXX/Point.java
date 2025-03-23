package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;

/**
 * Represents a 2D coordinate position with directional movement capabilities.
 *
 * <p>Key responsibilities:
 * - Cartesian coordinate tracking
 * - Directional orientation management
 * - Movement vector calculations
 * - Coordinate system transformations</p>
 */
public class Point {

    private final Logger logger = LogManager.getLogger();
    private Integer X = 0;
    private Integer Y = 0;
    public ArrayList<Integer> coordinates = new ArrayList<Integer>();
    public Direction cur_dir = Direction.N;

    /**
     * Initializes position at origin (0,0) facing North
     */
    public Point(){
        coordinates.add(0, X);
        coordinates.add(1, Y);
    }

    /**
     * Sets compass orientation from string input
     * @param dir Cardinal direction string (N/E/S/W)
     */
    public void setDir(String dir) {
        cur_dir = cur_dir.StrToDir(dir);
    }

    /**
     * Moves position forward based on current orientation:
     * - North: Y+1
     * - East: X+1
     * - South: Y-1
     * - West: X-1
     */
    public void updateFwd(){

        if (this.cur_dir == Direction.N){
            Y++;
            coordinates.set(1, Y);
        }
        else if (this.cur_dir == Direction.E){
            X++;
            coordinates.set(0, X);
        }
        else if (this.cur_dir == Direction.S){
            Y--;
            coordinates.set(1, Y);        }
        else if (this.cur_dir == Direction.W){
            X--;
            coordinates.set(0, X);        }

    }

    /**
     * Executes right turn maneuver with coordinated movement:
     * - Adjusts position diagonally during turn
     * - Updates orientation clockwise
     */
    public void updateR(){

        if (this.cur_dir == Direction.N){
            X++;

            coordinates.set(0, X);

            cur_dir = Direction.E;
        }
        else if (this.cur_dir == Direction.E){
            Y--;
            coordinates.set(1, Y);
            cur_dir = Direction.S;
        }
        else if (this.cur_dir == Direction.S){
            X--;
            coordinates.set(0, X);
            cur_dir = Direction.W;
        }
        else if (this.cur_dir == Direction.W){
            X++;
            coordinates.set(0, X);
            cur_dir = Direction.N;
        }
        else {
            logger.error("Drone is Lost");
        }
    }

    /**
     * Executes left turn maneuver with coordinated movement:
     * - Adjusts position diagonally during turn
     * - Updates orientation counter-clockwise
     */
    public void updateL(){

        if (this.cur_dir == Direction.N){
            X--;
            coordinates.set(0, X);
            cur_dir = Direction.W;
        }
        else if (this.cur_dir == Direction.E){
            Y++;
            coordinates.set(1, Y);
            cur_dir = Direction.N;
        }
        else if (this.cur_dir == Direction.S){
            X++;
            coordinates.set(0, X);
            cur_dir = Direction.E;
        }
        else if (this.cur_dir == Direction.W){
            X--;
            coordinates.set(0, X);
            cur_dir = Direction.S;
        }
        else {
            logger.error("Drone is lost");
        }
    }

    /**
     * Calculates Euclidean distance between two coordinates
     * @param x1 First point's X coordinate
     * @param x2 Second point's X coordinate
     * @param y1 First point's Y coordinate
     * @param y2 Second point's Y coordinate
     * @return Straight-line distance between points
     */
    public Double getDiff(Integer x1, Integer x2, Integer y1, Integer y2) {
        return Math.sqrt(Math.pow((x2-x1), 2)+Math.pow((y2-y1), 2));
    }

    /**
     * Converts position to array format
     * @param position1 Point to convert
     * @return Integer array [X,Y] coordinates
     */
    public Integer[] coordsToArr(Point position1) {
        Integer[] coordinates_arr = new Integer[2];
        coordinates_arr[0] = position1.getX();
        coordinates_arr[1] = position1.getY();
        return coordinates_arr;
    }

    /**
     * @return Current X coordinate
     */
    public Integer getX() {return this.X;}

    /**
     * @return Current Y coordinate
     */
    public Integer getY() {return this.Y;}
}

