package ca.mcmaster.se2aa4.island.teamXXX;

import ca.mcmaster.se2aa4.island.teamXXX.Drone;
import ca.mcmaster.se2aa4.island.teamXXX.Direction;
import ca.mcmaster.se2aa4.island.teamXXX.Point;
import ca.mcmaster.se2aa4.island.teamXXX.Commands;
import ca.mcmaster.se2aa4.island.teamXXX.Condition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/**
 * Geographic mapping system for island exploration missions.
 * Maintains spatial records of key locations and manages positional tracking.
 *
 * <p>Key responsibilities:
 * - Tracking Points of Interest (POIs)
 * - Maintaining emergency site and creek locations
 * - Calculating spatial relationships
 * - Managing exploration state transitions</p>
 */
public class OnMap {

    private Map<Integer[], Types> pois = new HashMap<>();
    private Map<Integer[], String> creeks = new HashMap<Integer[], String>();
    private final Logger logger = LogManager.getLogger();
    private String site_id = "";
    private Integer[] site_loc;

    public Point pos = new Point();
    
    private Direction dir = Direction.N;
    private Condition cond = Condition.START;


    /**
     * Retrieves current exploration phase state
     * @return Active exploration condition (START/SCAN/etc)
     */
    public Condition getState() {
        return cond;
    }

    /**
     * Updates exploration phase state
     * @param new_state Next mission phase to transition to
     */
    public void setState(Condition new_state) {
        cond = new_state;
    }

    /**
     * Initializes directional orientation
     * @param orient Starting compass direction
     */
    public void setInitDir(Direction orient) {
        dir = orient;
        pos.setDir(dir.DirToStr());
    }

    /**
     * Updates geographic features based on sensor data
     * @param drone Sensor data interface containing latest scan results
     */
    public void updateTypes(Drone drone) {

        Types tile_type = null;
        Integer[] coord2 = new Integer[2];

        coord2[0] = pos.getX();
        coord2[1] = pos.getY();


        if (drone.getCreekFound()) {
            tile_type = Types.CREEK;
            creeks.put(coord2, drone.creek);
            pois.put(coord2, tile_type);
            drone.setCreekFound(false);
        }

        //check if a site was found, if so, set it as site location, and put it in pois list
        if (drone.getSiteFound()) {
            tile_type = Types.SITE;
            site_loc = pos.coordsToArr(pos);
            site_id = drone.site;
            pois.put(coord2, tile_type);
            drone.setSiteFound(false); //make it false now that it is marked
        }

    }

    /**
     * Logs all identified points of interest
     */
    public void printPois() {
        StringBuilder points = new StringBuilder();
        for (Integer[] coord : pois.keySet()) {
            String coord_str = Arrays.toString(coord);
            points.append(coord_str).append(": ").append(pois.get(coord)).append(", ");
            logger.info("pois2: " + points + ", ");
        }
    }

    /**
     * Determines optimal creek identification based on spatial analysis
     * @return Creek ID closest to emergency site, or fallback values:
     *         - "NA" if no features found
     *         - Emergency site ID if no creeks found
     */
    public String returnCreek() {


        if (creeks.isEmpty() && !pois.containsValue(Types.SITE)) {
            return "NA";
        }

        else if (creeks.isEmpty()) {
            return site_id;
        }

        else if (creeks.size() == 1 || (!pois.containsValue(Types.SITE))) {
            for (Integer[] coord : creeks.keySet()) {
                return creeks.get(coord);
            }

        }

        int i = 0;
        String least_dist_creek = "";
        Double least_dist = 0.0;
        Double cur_dist = 0.0;

        for (Integer[] coord : creeks.keySet()) {

            if (i == 0) {
                least_dist = pos.getDiff(coord[0], site_loc[0], coord[1], site_loc[1]);
                least_dist_creek = creeks.get(coord);
            }

            else {
                cur_dist = pos.getDiff(coord[0], site_loc[0], coord[1], site_loc[1]);

                if (cur_dist < least_dist) {
                    least_dist = cur_dist;
                    least_dist_creek = creeks.get(coord);
                }
            }
            i++;
        }
        return least_dist_creek;
    }

    /**
     * Updates drone's positional coordinates based on movement commands
     * @param command Navigation action to process
     */
    public void updatePos(Commands command) {
        if (command == Commands.FLY) {
            pos.updateFwd();
        }
        else if (command == Commands.TURN_L) {
            pos.updateFwd();
            pos.updateL();
        }
        else if (command == Commands.TURN_R) {
            pos.updateFwd();
            pos.updateR();
        }
    }
}

