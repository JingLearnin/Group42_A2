package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public interface Stages {
    JSONObject decide();
    void react(JSONObject response);
}
