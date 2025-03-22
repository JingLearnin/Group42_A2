package ca.mcmaster.se2aa4.island.teamXXX;

public class Direction {
    private String currentHeading;
    private Coordinate position;

    public Direction(String initialHeading, Coordinate initialPosition) {
        this.currentHeading = initialHeading;
        this.position = initialPosition;
    }

    public String getLeftDirection() {
        return getLeftDirection(currentHeading);
    }

    public String getRightDirection() {
        return getRightDirection(currentHeading);
    }

    public String getLeftDirection(String heading) {
        return switch (heading) {
            case "N" -> "W";
            case "W" -> "S";
            case "S" -> "E";
            case "E" -> "N";
            default -> heading;
        };
    }

    public String getRightDirection(String heading) {
        return switch (heading) {
            case "N" -> "E";
            case "E" -> "S";
            case "S" -> "W";
            case "W" -> "N";
            default -> heading;
        };
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
            case "N" -> position.changeY(1);
            case "S" -> position.changeY(-1);
            case "E" -> position.changeX(1);
            case "W" -> position.changeX(-1);
        }
    }
}
