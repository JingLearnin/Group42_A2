package ca.mcmaster.se2aa4.island.teamXXX;

public class Direction {
    private String currentHeading;

    public Direction(String initialHeading) {
        this.currentHeading = initialHeading;
    }

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
    }

    public String getCurrentHeading() {
        return this.currentHeading;
    }
}