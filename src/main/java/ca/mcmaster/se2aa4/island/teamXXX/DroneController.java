package ca.mcmaster.se2aa4.island.teamXXX;

class DroneController {
    private final Drone drone;

    public DroneController(int battery, String heading) {
        this.drone = new Drone(battery, heading);
    }

    public void executeMission() {
        if (drone.getBatteryLevel() <= 0) {
            System.out.println("Battery empty. Mission aborted.");
            drone.stop();
            return;
        }

        System.out.println("Starting mission: Scanning with radar...");

        while (drone.scanWithRadar() != "GROUND") {
            System.out.println("No land detected. Moving drone down one grid.");
            drone.turnRight();
            drone.fly();
            drone.turnLeft();
        } 

        //Starting position is (1,1)
        //move down one grid until the radar echos "Gound",
        //then fly forward until reaches ground cell (photo scan),


        //while above island
        //grid scan every cell if it's a emergency site / a creek 
        //

    }
}

