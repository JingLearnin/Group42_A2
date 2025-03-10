package ca.mcmaster.se2aa4.island.teamXXX;

class Drone {
    private int batteryLevel;
    private String heading;
    private final Radar radar; // Composition: Drone has a Radar
    private final Photo photo; // Composition: Drone has a photo scanner

    public Drone(int batteryLevel, String heading) {
        this.batteryLevel = batteryLevel;
        this.heading = heading;
        this.radar = new Radar(); // The Drone creates its Radar instance
        this.photo = new Photo(); 
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void fly() {}

    public void turnLeft() {}

    public void turnRight() {}

    public void stop(){}


    public String scanWithRadar(){
        String radarResult = radar.RadarScan(heading);
        return radarResult;
    }

    public String scanWithPhoto(){
        String photoResult = photo.PhotoScan();
        return photoResult;
    }
}
