package ca.mcmaster.se2aa4.island.teamXXX;

import ca.mcmaster.se2aa4.island.teamXXX.Drone;
import ca.mcmaster.se2aa4.island.teamXXX.Commands;
import ca.mcmaster.se2aa4.island.teamXXX.Direction;
import org.json.JSONObject;

/**
 * Translates drone commands into API-compatible JSON payloads and manages directional state.
 * 
 * <p>Serves as the bridge between logical commands ({@code Commands} enum) and physical 
 * drone API requirements. Handles directional transformations for rotation/echo actions.</p>
 */

public class Controller {
    private Drone drone;

/**
 * Initializes controller with target drone.
 * 
 * @param in_drone Drone entity to control. Must not be null.
 */
    Controller(Drone in_drone) {
        drone = in_drone;
    }

/**
 * Converts command to API-ready JSON with dynamic parameter handling.
 * 
 * <p><b>Key Behaviors:</b>
 * <ul>
 *   <li>For turning commands (TURN_L/TURN_R): Updates drone's internal direction state</li>
 *   <li>For echo commands (ECHO_*): Computes direction without changing drone state</li>
 *   <li>STANDBY returns null (no action)</li>
 * </ul>
 * 
 * @param cmd Command to translate
 * @return Valid JSON payload, or null for STANDBY
 * @throws NullPointerException if cmd requires direction and drone has invalid heading
 * 
 * @example <caption>Fly Command</caption>
 * JSONObject json = controller.CommandsToJSON(Commands.FLY);
 * // Returns {"action": "fly"}
 */
    JSONObject CommandsToJSON(Commands cmd) {

        String cmd_str = cmd.getCommand();

        Direction dir = drone.getDir();
        String dir_str = dir.DirToStr();
        JSONObject params = new JSONObject();

        //put action in JSONObject
        JSONObject cmd_json = new JSONObject();
        cmd_json.put("action", cmd_str);

        switch (cmd) {
            case ECHO_FWD -> {
                cmd_json.put("parameters",params.put("direction",dir_str));
            }
            case ECHO_R -> {
                dir = dir.turnR();
                cmd_json.put("parameters",params.put("direction",dir.DirToStr()));
            }
            case ECHO_L -> {
                dir = dir.turnL();
                cmd_json.put("parameters",params.put("direction",dir.DirToStr()));

            }
            case FLY, STOP, SCAN -> {
                return cmd_json;
            }
            case TURN_L -> {
                dir = dir.turnL();
                cmd_json.put("parameters",params.put("direction",dir.DirToStr()));

                drone.setDir(dir); 
            }
            case TURN_R -> {
                dir = dir.turnR();
                cmd_json.put("parameters",params.put("direction",dir.DirToStr()));

                drone.setDir(dir); 
            }
            case STANDBY -> {
                return null;
            }
        }
        return cmd_json;
    }
}

