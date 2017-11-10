
package io.workmanw.yapa.models;

import java.util.Objects;

import com.jmethods.catatumbo.Embeddable;

import com.google.gson.JsonObject;

@Embeddable
public class VideoIntelModel {
    public static final String TYPE_SHOT_LABEL = "Shot";
    public static final String TYPE_SEGEMENT_LABEL = "Segement";
    public static final String TYPE_FRAME_LABEL = "Frame";

    private String type;
    private String mid;
    private String description;
    private float score;

    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getMid() {
        return this.mid;
    }
    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public float getScore() {
        return this.score;
    }
    public void setScore(float score) {
        this.score = score;
    }

    public JsonObject toJson() {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("mid", this.getMid());
        jsonObj.addProperty("description", this.getDescription());
        jsonObj.addProperty("score", this.getScore());
        return jsonObj;
    }
}
