package ca.mcmaster.se2aa4.island.teamXXX;

class Drone {
    private int batteryLevel;
    private String heading;

    public Drone(int batteryLevel, String heading) {
        this.batteryLevel = batteryLevel;
        this.heading = heading;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void fly() {}

    public void turnLeft() {}

    public void turnRight() {}

    public void stop(){}

}
