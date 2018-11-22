package com.studio332.omega.model;

public class MapInfo {
   private final int level;
   private final String name;
   private final String file;
   private final int difficulty;
   private final float waterRate;
   private final float waterDelay;
   private final int batCount;
   private final int rockslideCount;
   private final int slowCount;
   private final int confuseCount;
   private final int malfunctionCount;
   private final int waveCount;
   private final String winMsg;
   private float bestTimeSec = 0f;
   
   public MapInfo( int level, String name, String file, int diff, float water, float wDelay, int bat, int rock, 
         int slow, int confuse, int broke, int wave, String win) {
      this.level = level;
      this.name = name;
      this.difficulty = diff;
      this.file = file;
      this.waterRate = water;
      this.waterDelay = wDelay;
      this.batCount = bat;
      this.rockslideCount = rock;
      this.slowCount = slow;
      this.confuseCount = confuse;
      this.malfunctionCount = broke;
      this.waveCount = wave;
      this.winMsg = win;
      this.bestTimeSec = Integer.MAX_VALUE;
   }
   
   public int getLevel() {
      return level;
   }

   public String getName() {
      return name;
   }

   public String getFile() {
      return file;
   }

   public int getDifficulty() {
      return difficulty;
   }
   
   public void setBestTime( float sec ) {
      this.bestTimeSec= sec;
   }
   
   public float getBestTimeSec() {
      return this.bestTimeSec;
   }

   public float getWaterRate() {
      return waterRate;
   }
   
   public float getWaterDelay() {
      return waterDelay;
   }

   public int getBatCount() {
      return batCount;
   }

   public int getRockslideCount() {
      return rockslideCount;
   }
   
   public String getWinMessage() {
      return this.winMsg;
   }

   public int getSlowCount() {
      return slowCount;
   }

   public int getConfuseCount() {
      return confuseCount;
   }

   public int getMalfunctionCount() {
      return malfunctionCount;
   }

   public int getWaveCount() {
      return waveCount;
   }

   @Override
   public String toString() {
      return "MapInfo [name=" + name + ", file=" + file + ", difficulty=" + difficulty
            + ", waterRate=" + waterRate + ", batCount=" + batCount + ", rockslideCount="
            + rockslideCount + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((file == null) ? 0 : file.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      MapInfo other = (MapInfo) obj;
      if (file == null) {
         if (other.file != null)
            return false;
      } else if (!file.equals(other.file))
         return false;
      return true;
   }
   
   
}
