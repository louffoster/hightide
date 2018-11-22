package com.studio332.omega.model;

import com.google.gson.JsonObject;

// main card naming hazards. ex: 
//          main = rockslide
//          zoom = rockslide-big
//          hit  = rockslide-hit
//          safe = rockslide-safe
// so.. append -big, -hit, -safe to all filenames to determine image

// hazard: no extra data needed]
//   power tells how much damge is dealt by this card
//
// flip: ( flips right and left)
//    power tells how many seconds the flip lasts
// 
// reduce:
//    means tha all power is 0.5
//    power tells how many seconds the reduce lasts
// 

public class CardInfo {
   public enum Type {MOVE, BOOST, DEFEND, WILD, HEAL, TANK, HAZARD, REDUCE, FLIP, WAVE, MALFUNCTION};
   public enum Status {READY, ACTIVE, EXECUTING, EXPIRED};
   private String imageName;
   private Type type;
   private int angle;
   private int power;
   private boolean boosted = false;
   private Status status = Status.READY;
   private float timerDuration = 0f;
   private float timerTimeLeft = 0f;
   private final String[] dirs = {"n","ne","e","se","s","sw","w","nw"};
   
   public static CardInfo fromJson( JsonObject obj ) {
      String img = obj.get("imageName").getAsString();
      String t = obj.get("type").getAsString();
      Type type = Type.valueOf(t.toUpperCase());
      if ( type.equals(Type.MOVE)) {
         int angle = obj.get("angle").getAsInt();
         int power = obj.get("power").getAsInt();
         return new CardInfo(img, type, angle, power);
      } else {
         int power = 0;
         if ( obj.has("power")) {
            power = obj.get("power").getAsInt();
         }
         return new CardInfo(img, type, 0, power);
      } 
   }
   
   private CardInfo( String img, Type t, int angle, int power) {
      this.imageName = img;
      this.type = t;
      this.angle = angle;
      this.power = power;
   }
   
   public CardInfo(CardInfo card) {
      this.imageName = card.imageName;;
      this.type = card.type;
      this.angle = card.angle;
      this.power = card.power;
   }
   
   public boolean isTimedHazard() {
      return ( this.type.equals(Type.FLIP) || 
               this.type.equals(Type.REDUCE) || 
               this.type.equals(Type.HAZARD) ||
               this.type.equals(Type.WAVE) ||
               this.type.equals(Type.MALFUNCTION));
   }
   
   public boolean isAbeCard() {
      return ( this.type.equals(Type.BOOST) || 
               this.type.equals(Type.DEFEND) ||
               this.type.equals(Type.WILD));
   }

   public boolean isActive() {
      return this.status.equals(Status.ACTIVE);
   }
   
   public void activate() {
      this.status = Status.ACTIVE;
   }
   
   public boolean isTimerActive() {
      return (this.status.equals(Status.EXECUTING) ||
              this.type.equals(Type.FLIP) || 
              this.type.equals(Type.REDUCE) || 
              this.type.equals(Type.HAZARD));
   }
   
   public void execute() {
      this.status = Status.EXECUTING;
   }
   
   public void boostPower() {
      this.power *=2;
      this.boosted = true;
   }
   
   public boolean isBoosted() {
      return this.boosted;
   }
   
   public void startTimedCard( float life ) {
      this.status = Status.EXECUTING;
      this.timerDuration = life;
      this.timerTimeLeft = life;
   }
   
   public void update(float dt ) {
      if ( this.status == Status.EXECUTING ) {
         this.timerTimeLeft -= dt;
         this.timerTimeLeft = Math.max(0, this.timerTimeLeft);
         if ( this.timerTimeLeft == 0 ) {
            this.status = Status.EXPIRED;
         }
      }
   }
   
   public float getTimerPercent() {
      return this.timerTimeLeft  / this.timerDuration;
   }

   public boolean isExpired() {
      return this.status.equals(Status.EXPIRED);
   }
   
   public boolean canRecalibrate() {
      return (this.type == Type.MOVE || this.type == Type.WILD);
   }

   public void recalibrate(int newAngle ) {
      this.type = Type.MOVE;
      this.angle = newAngle;
      int idx = this.angle/45;
      this.status = Status.READY;
      this.imageName = this.dirs[idx]+this.power;
   }
   
   public boolean isDiagonalMove() {
      return (this.type.equals(Type.MOVE) && (this.angle % 90 != 0));
   }
   
   public boolean isClimb() {
     return (this.type.equals(Type.MOVE) && (this.angle == 0 || this.angle == 45 || this.angle == 315));
   }
   public String getImageName() {
      return imageName;
   }
   
   public int getAngle() {
      return angle;
   }

   public int getPower() {
      return power;
   }

   public Type getType() {
      return type;
   }

   @Override
   public String toString() {
      return "CardInfo [imageName=" + imageName + ", type=" + type + ", angle=" + angle + ", power="
            + power;
   }
}
