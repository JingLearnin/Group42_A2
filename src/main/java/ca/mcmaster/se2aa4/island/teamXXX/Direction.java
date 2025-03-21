package ca.mcmaster.se2aa4.island.teamXXX;

public class Direction {
    private String currentHeading;
    private Coordinate position;

    public Direction(String initialHeading, Coordinate initialPosition) {
        this.currentHeading = initialHeading;
        this.position = initialPosition;
    }

    // ✅ Fix: These were missing in ActionHandler
    public String getLeftDirection() {
        switch (currentHeading) {
            case "N": return "W";
            case "S": return "E";
            case "E": return "N";
            case "W": return "S";
            default: return currentHeading;
        }
    }

    public String getRightDirection() {
        switch (currentHeading) {
            case "N": return "E";
            case "S": return "W";
            case "E": return "S";
            case "W": return "N";
            default: return currentHeading;
        }
    }

    public void setHeading(String newHeading) {
        this.currentHeading = newHeading;
        updatePosition();
    }

    public String getCurrentHeading() {
        return this.currentHeading;
    }

    public Coordinate getPosition() {
        return this.position;
    }

    public void updatePosition() {
        switch (currentHeading) {
            case "N": position.changeY(1); break;
            case "S": position.changeY(-1); break;
            case "E": position.changeX(1); break;
            case "W": position.changeX(-1); break;
        }
    }
}
