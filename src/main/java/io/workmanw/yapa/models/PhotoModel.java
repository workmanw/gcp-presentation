package io.workmanw.yapa.models;

import com.jmethods.catatumbo.Entity;
import com.jmethods.catatumbo.Identifier;

import com.google.gson.JsonObject;

@Entity(kind="Photo")
public class PhotoModel extends BaseModel {
  public PhotoModel() { }

  @Identifier
  private long id;

  private String name;

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public JsonObject toJson() {
    JsonObject jsonObj = new JsonObject();
    jsonObj.addProperty("id", this.id);
    jsonObj.addProperty("name", this.name);
    return jsonObj;
  }

  public void fromJson(JsonObject jsonObj) {
    this.setName(jsonObj.get("name").getAsString());
  }
}