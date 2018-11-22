package com.studio332.omega.model;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Settings {
   private static final Settings instance = new Settings();
   private boolean soundOn = true;
   private boolean musicOn = true;
   private boolean rulesEverSeen = false;
   private List<MapInfo> maps = new ArrayList<MapInfo>();
   private int currMapIndex = 0;
   
   private static final String PREFS_NAME = "hightide";
   
   public static Settings instance() {
      return Settings.instance;
   }
   
   private Settings() {
      // Load maps all available maps
      FileHandle handle = Gdx.files.internal("data/maps.json");
      JsonParser parser = new JsonParser();
      String jsonStr = handle.readString();
      JsonArray array = parser.parse(jsonStr).getAsJsonArray();
      for ( int i=0; i<array.size(); i++) {
         JsonObject obj = array.get(i).getAsJsonObject();
         this.maps.add( new MapInfo(
               obj.get("level").getAsInt(),
               obj.get("name").getAsString(), 
               obj.get("file").getAsString(),
               obj.get("difficulty").getAsInt(),
               obj.get("waterRate").getAsFloat(),
               obj.get("waterDelay").getAsFloat(),
               obj.get("batCount").getAsInt(),
               obj.get("rockslideCount").getAsInt(),
               obj.get("slowCount").getAsInt(),
               obj.get("confuseCount").getAsInt(),
               obj.get("malfunctionCount").getAsInt(),
               obj.get("surgeCount").getAsInt(),
               obj.get("win").getAsString()));
      }
      
      // crack the preferences for this app and load them
      Preferences p =  Gdx.app.getPreferences( PREFS_NAME );
      if ( p.contains("soundOn")) {
         this.soundOn = p.getBoolean("soundOn");
      }
      if ( p.contains("musicOn")) {
         this.musicOn = p.getBoolean("musicOn");
      }
      if ( p.contains("rulesEverSeen")) { 
         this.rulesEverSeen = p.getBoolean("rulesEverSeen");
      }
      
      // look for best times on all of the maps
      // best time is scored as a key matching the map file
      // with an integer value that is the best time in seconds.
      for ( MapInfo mi : this.maps ) {
         if ( p.contains(mi.getFile())) {
            mi.setBestTime( p.getFloat(mi.getFile()));
         } else {
            mi.setBestTime( 0 );
         }
      }
   }
   
   public boolean isCurrMapLocked() {
      if ( this.currMapIndex==0) {
         return false;
      } 
      
      int prior = this.currMapIndex -1;
      return ( this.maps.get(prior).getBestTimeSec()==0);
   }
   
   public MapInfo getCurrentMap() {
      return this.maps.get(this.currMapIndex);
   }
   
   public void nextMap() {
      this.currMapIndex++;
      if ( this.currMapIndex >= this.maps.size()) {
         this.currMapIndex = 0;
      }
   }
   
   public void priorMap() {
      this.currMapIndex--;
      if ( this.currMapIndex < 0) {
         this.currMapIndex = this.maps.size()-1;
      }
   }
   
   public boolean rulesEverSeen() {
      return this.rulesEverSeen;
   }
   
   public void rulesViewed() {
      this.rulesEverSeen = true;
      Preferences p =  Gdx.app.getPreferences( PREFS_NAME );
      p.putBoolean("rulesEverSeen", this.rulesEverSeen);
      p.flush();
   }
   
   public void toggleSound() {
      this.soundOn = !this.soundOn;
      Preferences p =  Gdx.app.getPreferences( PREFS_NAME );
      p.putBoolean("soundOn", this.soundOn);
      p.flush();
   }
   
   public boolean isSoundOn() {
      return this.soundOn;
   }
   
   public void toggleMusic() {
      this.musicOn = !this.musicOn;
      Preferences p =  Gdx.app.getPreferences( PREFS_NAME );
      p.putBoolean("musicOn", this.musicOn);
      p.flush();
   }
   
   public boolean isMusicOn() {
      return this.musicOn;
   }

   public boolean isBestTime(float elapsedSec) {
      MapInfo curr = getCurrentMap();
      if ( curr.getBestTimeSec() == 0) {
         return true;
      }
      return (elapsedSec < curr.getBestTimeSec() );
   }
   
   public void setBestTime(float elapsedSec) {
      MapInfo curr = getCurrentMap();
      curr.setBestTime(elapsedSec);
      Preferences p =  Gdx.app.getPreferences( PREFS_NAME );
      p.putFloat(curr.getFile(), elapsedSec);
      p.flush();
   }
}
