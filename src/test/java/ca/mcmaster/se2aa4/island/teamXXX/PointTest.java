package ca.mcmaster.se2aa4.island.teamXXX;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PointTest {

    @Test
    public void testConstructor() {
        Point p = new Point();
        // The point should start at (0,0)
        assertEquals(0, p.getX(), "Initial X should be 0");
        assertEquals(0, p.getY(), "Initial Y should be 0");
        // The internal coordinates list should contain 0 at indices 0 and 1.
        assertEquals(0, p.coordinates.get(0), "Coordinates[0] should be 0");
        assertEquals(0, p.coordinates.get(1), "Coordinates[1] should be 0");
        // Initial direction should be N.
        assertEquals(Direction.N, p.cur_dir, "Initial direction should be N");
    }
    
    @Test
    public void testSetDir() {
        Point p = new Point();
        p.setDir("E");
        // After setting direction to "E", cur_dir should become Direction.E.
        assertEquals(Direction.E, p.cur_dir, "After setDir('E'), direction should be E");
    }
    
    @Test
    public void testUpdateFwd_North() {
        Point p = new Point();
        // Default direction is N; moving forward should increment Y.
        p.updateFwd();
        assertEquals(0, p.getX(), "X should remain 0 when moving North");
        assertEquals(1, p.getY(), "Y should increment to 1 when moving North");
    }
    
    @Test
    public void testUpdateFwd_East() {
        Point p = new Point();
        p.setDir("E"); // Change direction to East.
        p.updateFwd();
        // Moving East should increment X.
        assertEquals(1, p.getX(), "X should increment to 1 when moving East");
        assertEquals(0, p.getY(), "Y should remain 0 when moving East");
    }
    
    @Test
    public void testUpdateFwd_South() {
        Point p = new Point();
        p.setDir("S"); // Change direction to South.
        p.updateFwd();
        // Moving South should decrement Y.
        assertEquals(0, p.getX(), "X should remain 0 when moving South");
        assertEquals(-1, p.getY(), "Y should decrement to -1 when moving South");
    }
    
    @Test
    public void testUpdateFwd_West() {
        Point p = new Point();
        p.setDir("W"); // Change direction to West.
        p.updateFwd();
        // Moving West should decrement X.
        assertEquals(-1, p.getX(), "X should decrement to -1 when moving West");
        assertEquals(0, p.getY(), "Y should remain 0 when moving West");
    }
    
    @Test
    public void testUpdateR() {
        Point p = new Point();
        // With initial direction N, updateR() should adjust position and change direction to E.
        p.updateR();
        // When turning right from N, X should increase by 1, Y remains unchanged.
        assertEquals(1, p.getX(), "After updateR from N, X should be 1");
        assertEquals(0, p.getY(), "After updateR from N, Y should remain 0");
        assertEquals(Direction.E, p.cur_dir, "After updateR from N, direction should be E");
    }
    
    @Test
    public void testUpdateL() {
        Point p = new Point();
        // With initial direction N, updateL() should adjust position and change direction to W.
        p.updateL();
        // When turning left from N, X should decrease by 1, Y remains unchanged.
        assertEquals(-1, p.getX(), "After updateL from N, X should be -1");
        assertEquals(0, p.getY(), "After updateL from N, Y should remain 0");
        assertEquals(Direction.W, p.cur_dir, "After updateL from N, direction should be W");
    }
    
    @Test
    public void testGetDiff() {
        Point p = new Point();
        // Calculate Euclidean distance between (0,0) and (3,4). Should return 5.
        Double distance = p.getDiff(0, 3, 0, 4);
        assertEquals(5.0, distance, 0.001, "Euclidean distance between (0,0) and (3,4) should be 5");
    }
    
    @Test
    public void testCoordsToArr() {
        Point p = new Point();
        // Move the point to a new position:
        p.setDir("E");
        p.updateFwd(); // Moves East: X becomes 1.
        p.setDir("N");
        p.updateFwd(); // Moves North: Y becomes 1.
        // The expected coordinates are [1,1].
        Integer[] arr = p.coordsToArr(p);
        assertEquals(1, arr[0], "X coordinate should be 1");
        assertEquals(1, arr[1], "Y coordinate should be 1");
    }
}
