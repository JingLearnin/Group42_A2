package ca.mcmaster.se2aa4.island.teamXXX;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DirectionTest {

    @Test
    public void testStrToDirValid() {
        // For valid inputs, expect the corresponding enum constant.
        assertEquals(Direction.N, Direction.N.StrToDir("N"), "StrToDir should return N for input 'N'");
        assertEquals(Direction.E, Direction.E.StrToDir("E"), "StrToDir should return E for input 'E'");
        assertEquals(Direction.S, Direction.S.StrToDir("S"), "StrToDir should return S for input 'S'");
        assertEquals(Direction.W, Direction.W.StrToDir("W"), "StrToDir should return W for input 'W'");
    }

    

    @Test
    public void testDirToStr() {
        // Check that each enum constant returns its string representation.
        assertEquals("N", Direction.N.DirToStr(), "DirToStr should return 'N' for Direction.N");
        assertEquals("E", Direction.E.DirToStr(), "DirToStr should return 'E' for Direction.E");
        assertEquals("S", Direction.S.DirToStr(), "DirToStr should return 'S' for Direction.S");
        assertEquals("W", Direction.W.DirToStr(), "DirToStr should return 'W' for Direction.W");
    }

    @Test
    public void testTurnR() {
        // Because turnR() modifies an internal field on the enum constant,
        // we use each constant only once.
        Direction n = Direction.N;
        Direction turnedR = n.turnR();
        assertEquals(Direction.E, turnedR, "Turning right from N should yield E");

        Direction e = Direction.E;
        Direction turnedR2 = e.turnR();
        assertEquals(Direction.S, turnedR2, "Turning right from E should yield S");

        Direction s = Direction.S;
        Direction turnedR3 = s.turnR();
        assertEquals(Direction.W, turnedR3, "Turning right from S should yield W");

        Direction w = Direction.W;
        Direction turnedR4 = w.turnR();
        assertEquals(Direction.N, turnedR4, "Turning right from W should yield N");
    }

    @Test
    public void testTurnL() {
        // Similarly, test the counter-clockwise rotation.
        Direction n = Direction.N;
        Direction turnedL = n.turnL();
        assertEquals(Direction.W, turnedL, "Turning left from N should yield W");

        Direction w = Direction.W;
        Direction turnedL2 = w.turnL();
        assertEquals(Direction.S, turnedL2, "Turning left from W should yield S");

        Direction s = Direction.S;
        Direction turnedL3 = s.turnL();
        assertEquals(Direction.E, turnedL3, "Turning left from S should yield E");

        Direction e = Direction.E;
        Direction turnedL4 = e.turnL();
        assertEquals(Direction.N, turnedL4, "Turning left from E should yield N");
    }
}