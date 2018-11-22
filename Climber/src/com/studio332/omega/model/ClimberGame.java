package com.studio332.omega.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.studio332.omega.model.CardInfo.Type;
import com.studio332.omega.util.SoundManager;


/**
 * Gameplay model for Omega Project : Climber game
 * 
 * @author lfoster
 *
 */
public class ClimberGame {
   public enum Side {NONE, LEFT, RIGHT};
   public enum State {INIT, HELP, DEAL, PLAN, PLAY, ABE, RECALIBRATE, PAUSE, DEAD, WIN, ENDED};
   
   private boolean started = false;
   private float elapsedTimeSec = 0f;
   private int health = MAX_HEALTH;
   private List<CardInfo> deck;
   private CardInfo[] pool;
   private boolean firstDeal = true;
   private State state = State.INIT;
   private State priorState = State.INIT;
   private float dealCardDelay=0;
   private float dealStartDelay = 0;
   private float waterLevelPercent = 0f;
   private final float waterRate;
   private float waterDelay;
   
   private List<Help> help;
   private Map<String,String> cardTypeHelp = new HashMap<String, String>();
   
   private float [] replaceCardTimes;
   
   private float currClimb;
   private float currSideways;
   private CardInfo currCard = null;
   private Stack<CardInfo> cardStack = new Stack<CardInfo>();
   
   // card timers
   private float shieldTimer = 0;
   private float tankTimer = 0;
   private float hazardTimer = 0;
   
   // hazard info
   private CardInfo currHazard = null;
   private HazardListener hazardListener = null;
   
   // used to make climb non-smooth
   private float totalY = 0;
   private float maxY = CLIMB_DISTANCE/2;;
   private float delayMoveY = 0;
   private float totalX = 0;
   private float maxX = CLIMB_DISTANCE/2;
   private float delayMoveX = 0;
   
   public static final float CLIMB_DISTANCE = 200;
   public static final float CLIMB_RATE = 200;
   private static float DEAL_CARD_DELAY = 0.08f;
   private static final float MOVE_DELAY = 0.1f;
   
   public static final float REPLACE_CARD_DELAY = 4f;
   public static final float SHIELD_DURATION = 7.0f;
   public static final float TANK_DURATION = 15.0f;
   public static final float HAZARD_TIMEOUT = 8.0f;
   public static int POOL_SIZE = 8;
   public static int MAX_HEALTH = 3;

   public ClimberGame() {
      this.help = new ArrayList<Help>();
      this.deck = new ArrayList<CardInfo>();
      this.pool = new CardInfo[POOL_SIZE];
      this.replaceCardTimes = new float[POOL_SIZE];
      for ( int i=0; i<POOL_SIZE; i++) {
         this.replaceCardTimes[i] = -1f;
      }
      fillDeck();
      initHelp();
      this.waterRate = Settings.instance().getCurrentMap().getWaterRate();
      this.waterDelay = Settings.instance().getCurrentMap().getWaterDelay();
   }
   
   public void showHelp() {
      this.priorState = this.state;
      this.state = State.HELP;
   }
   
   public void clearHelp() {
      this.state = this.priorState;
   }
   
   public void startPlay() {
      this.state = State.PLAY;
      this.started = true;
   }
   
   public boolean isMalfunction() {
      return ( this.currHazard != null && this.currHazard.getType().equals(Type.MALFUNCTION));
   }
   
   public void setHazardListener( HazardListener l ) {
      this.hazardListener = l;
   }
   
   public List<CardInfo> getCardDiscionary() {
      FileHandle handle = Gdx.files.internal("data/deck.json");
      JsonParser p = new JsonParser();
      String jsonStr = handle.readString();
      JsonArray array = p.parse(jsonStr).getAsJsonArray();
      List<CardInfo> dict = new ArrayList<CardInfo>();
      for ( int i=0; i<array.size(); i++) {
         JsonObject obj = array.get(i).getAsJsonObject();
         dict.add( CardInfo.fromJson(obj));
      }
      return dict;
   }
   
   public void winGame() {
      this.state = State.WIN;
      clearCurrAction();
   }
   
   public void endGame() {
      if (this.state != State.ENDED  ) {
         this.state = State.ENDED;
         clearCurrAction();
      }
   }
   
   public void killIke() {
      this.state = State.DEAD;
      clearCurrAction();
   }
   
   private void clearCurrAction() {
      this.currSideways = 0;
      this.currClimb = 0;
      this.cardStack.clear();
      this.currCard = null;
   }
   
   public State getState() {
      return this.state;
   }
   
   public float getCardReplaceTime(int idx) {
      return this.replaceCardTimes[idx];
   }
   
   public float getWaterLevelPercent() {
      return this.waterLevelPercent;
   }
   
   public void abeRedraw() {
      
      // cancel any outstanding hazards with a redraw
      if ( this.currHazard != null ) {
         this.currHazard = null;
         this.hazardTimer = 0;
         if ( this.hazardListener != null ) {
            this.hazardListener.hazardCleared();
         }
      }
      
      // cards are GONE now, no replace timer
      for ( int i = 0; i< POOL_SIZE; i++) {
         this.pool[i] = null;
         this.replaceCardTimes[i] = -1f;
      }
      
      // kill any looping hazard sounds
      SoundManager.instance().stopSound(SoundManager.BAT_FLAP);
      SoundManager.instance().stopSound(SoundManager.ROCK_SLIDE);
      SoundManager.instance().stopSound(SoundManager.SLOW);
      SoundManager.instance().stopSound(SoundManager.CONFUSE);
      SoundManager.instance().stopSound(SoundManager.WAVE);
      
      // also wipe out any ongoing pony tank and/or shield
      this.shieldTimer = -1f;
      this.tankTimer = -1f;
      
      this.state = State.DEAL;
      this.dealStartDelay = 0.15f;
   }
   
   public void togglePause() {
      if ( isPaused() ) {
         this.state = this.priorState;
      } else { 
         this.priorState = this.state;
         this.state = State.PAUSE;
      }
   }
   
   public boolean isOver() {
      return (this.state.equals(State.DEAD) || 
              this.state.equals(State.WIN) ||
              this.state.equals(State.ENDED));
   }
   
   public boolean isPaused() {
      return this.state.equals(State.PAUSE) || this.state.equals(State.HELP);
   }
   
   public boolean isPlaying() {
      return ( isPaused() == false && isOver() == false);
   }
   
   public void deal() {
      this.state = State.DEAL;
      this.firstDeal = true;
      this.dealCardDelay = DEAL_CARD_DELAY;
      this.dealStartDelay = 0.1f;
   }
   
   public CardInfo getPoolCard( int idx) {
      if ( this.replaceCardTimes[idx] > 0 ) {
         return null;
      }
      return this.pool[idx];
   }
   
   private void initHelp() {
      this.help.clear();
      FileHandle handle = Gdx.files.internal("data/help.json");
      JsonParser p = new JsonParser();
      String jsonStr = handle.readString();
      JsonArray array = p.parse(jsonStr).getAsJsonArray();
      for ( int i=0; i<array.size(); i++) {
         JsonObject obj = array.get(i).getAsJsonObject();   
         this.help.add( 
               new Help(obj.get("callout").getAsString(),
                        obj.get("title").getAsString(),
                        obj.get("help").getAsString() ));
      }
      
      this.cardTypeHelp.clear();
      handle = Gdx.files.internal("data/help-type.json");
      jsonStr = handle.readString();
      array = p.parse(jsonStr).getAsJsonArray();
      for ( int i=0; i<array.size(); i++) {
         JsonObject obj = array.get(i).getAsJsonObject();  
         this.cardTypeHelp.put(obj.get("type").getAsString(), obj.get("help").getAsString());
      }
   }
   
   public Map<String,String> getCardTypeHelp() {
      return this.cardTypeHelp;
   }
   
   public Help getCalloutHelp(String callout) {
      for ( Help h : this.help ) {
         if ( h.getCallout().equalsIgnoreCase(callout)) {
            return h;
         }
      }
      return null;
   }
   
   private void fillDeck() {
      FileHandle handle = Gdx.files.internal("data/deck.json");
      JsonParser p = new JsonParser();
      String jsonStr = handle.readString();
      JsonArray array = p.parse(jsonStr).getAsJsonArray();
      for ( int i=0; i<array.size(); i++) {
         JsonObject obj = array.get(i).getAsJsonObject();
         int cnt = obj.get("count").getAsInt();
         
         // override default counts by mountain-specific ones
         String img = obj.get("imageName").getAsString();
         if ( img.equalsIgnoreCase("bat")) {
            cnt = Settings.instance().getCurrentMap().getBatCount();
         }
         
         if ( img.equalsIgnoreCase("rockslide")) {
            cnt = Settings.instance().getCurrentMap().getRockslideCount();
         }
         
         if ( img.equalsIgnoreCase("slow")) {
            cnt = Settings.instance().getCurrentMap().getSlowCount();
         }
         
         if ( img.equalsIgnoreCase("confuse")) {
            cnt = Settings.instance().getCurrentMap().getConfuseCount();
         }
         
         if ( img.equalsIgnoreCase("wave")) {
            cnt = Settings.instance().getCurrentMap().getWaveCount();
         }
         
         if ( img.equalsIgnoreCase("malfunction")) {
            cnt = Settings.instance().getCurrentMap().getMalfunctionCount();
         }
         
         for (int c=0; c<cnt; c++ ) {
            this.deck.add( CardInfo.fromJson(obj));
         }
      }
   }
   
   public int getHealth() {
      return this.health;
   }
   
   public float getElapsedSec() {
      return this.elapsedTimeSec;
   }
   
   /**
    * Update the game model. Update timers, advance game state and trigger hazards
    * @param dt
    */
   public void updateModel( float dt ) {  
      if ( isPaused() || isOver() ) {
         return;
      }
      
      if ( this.started ) {
         this.elapsedTimeSec += dt;
      }
      
      if ( getElapsedSec() > this.waterDelay) {
         if ( this.currHazard != null && this.currHazard.getType().equals(Type.WAVE)) {
            this.waterLevelPercent += this.waterRate*2f*dt;
         } else {
            this.waterLevelPercent += this.waterRate*dt;
         }
         this.waterLevelPercent = Math.min(100f, this.waterLevelPercent);
      }
      
      if ( this.state.equals(State.DEAL)) {
         dealToPool(dt);
         return;
      } 
      
      // decrement card replace times to see if its ok to add a new card
      for ( int i=0; i<this.replaceCardTimes.length; i++ ) {
         if ( this.replaceCardTimes[i] > 0 ) {
            this.replaceCardTimes[i] -= dt;
            this.replaceCardTimes[i] = Math.max(0, this.replaceCardTimes[i]);
            if ( this.replaceCardTimes[i] == 0  ) {
               CardInfo card = dealCard();
               this.pool[i] = new CardInfo(card);
               if ( card.isTimedHazard() ) {
                  if ( card.getType().equals(Type.HAZARD) ){
                     this.hazardTimer = HAZARD_TIMEOUT;
                  }  else {
                     this.hazardTimer = card.getPower();
                  }
                  this.pool[i].startTimedCard(this.hazardTimer);
                  hazardActivated( card );
               }
            }
         }
      }
      
      // decrement shield timer
      if ( this.shieldTimer > 0 )  {
         this.shieldTimer -= dt;
         this.shieldTimer = Math.max(0, this.shieldTimer);
      }
      
      // decrement pony tank timer
      if ( this.tankTimer > 0 )  {
         this.tankTimer -= dt;
         this.tankTimer = Math.max(0, this.tankTimer);
      }
      
      // decrement HAZARD timer
      if ( this.hazardTimer > 0 )  {
         this.hazardTimer -= dt;
         this.hazardTimer = Math.max(0, this.hazardTimer);
         if ( this.hazardTimer == 0 ) { 
            hazardEnded( );
         }
      }
      
      // run thru cards and update the meters for onging cards on each
      for ( int i=0; i<this.pool.length; i++) {
         CardInfo ci = this.pool[i];
         if ( ci != null ) {
            ci.update(dt);
            if ( ci.isExpired() ) {
               this.pool[i] = null;
               this.replaceCardTimes[i] = REPLACE_CARD_DELAY;
            }
         }
      }
      
      if ( this.currCard != null && this.currCard.getType().equals(Type.MOVE) && 
           this.currSideways == 0 && this.currClimb == 0 ){
         this.currCard = null;
      }
      
      executeQueuedCards();
   }
   
   private void executeQueuedCards() {
      if ( this.currCard == null && this.cardStack.empty() == false ) {
         this.currCard = this.cardStack.pop();
         
         if (  this.currCard.isBoosted() ) {
            SoundManager.instance().playSound(SoundManager.BOOST);
         }
         
         // special movement hazard; halve the movement
         float dist = this.currCard.getPower()*CLIMB_DISTANCE;
         if ( this.currHazard != null && this.currHazard.getType().equals(Type.REDUCE)) {
            dist = dist*0.5f;
         }
         
         switch (this.currCard.getAngle()) {
         case 0:
            this.currSideways = 0f;
            this.currClimb = dist;
            break;
         case 45:
            this.currSideways = dist;
            this.currClimb = dist;
            break;
         case 90:
            this.currSideways = dist;
            this.currClimb = 0f;
            break;
         case 135:
            this.currSideways = dist;
            this.currClimb = -dist;
            break;
         case 180:
            this.currSideways = 0f;
            this.currClimb = -dist;
            break;
         case 225:
            this.currSideways = -dist;
            this.currClimb = -dist;
            break;
         case 270:
            this.currSideways = -dist;
            this.currClimb = 0f;
            break;
         case 315:
            this.currSideways = -dist;
            this.currClimb = dist;
            break;
         }
         
         // special movement hazard; flip left right
         if ( this.currHazard != null && this.currHazard.getType().equals(Type.FLIP)) {
            this.currSideways *= -1f;
         }
      }
   }
   
   private boolean hasHazard() {
      if ( this.currHazard != null) {
         return true;
      }
      for ( int i=0; i< POOL_SIZE; i++) {
         if ( this.pool[i] != null && this.pool[i].isTimedHazard() ) {
            return true;
         }
      }
      return false;
   }
   
   private CardInfo dealCard() {
      // There can only be 1 hazard at a time
      // AND if there is a malfunction, there cant be any ABE cards
      boolean hasHazard = hasHazard();
      
      // shuffle whats left and pick a card
      CardInfo card=null;
      int retryCnt = 5;
      while ( true ) {
         
         // pick a card and refill deck if its done
         Collections.shuffle(this.deck);
         card = this.deck.remove(0);
         if ( this.deck.size() == 0) {
            fillDeck();
         }
 
                  
         // done if the new card is not a second hazard
         // ALSO: don't start game with a hazard in play
         // AND: don't play ABE if mlfuntion is active
         if  ( (card.isTimedHazard() && (hasHazard || this.firstDeal)) ||
               card.isAbeCard() && isMalfunction() ) {
            this.deck.add(card); // put it back!!
            retryCnt--;
            if ( retryCnt == 0) {
               this.deck.clear();
               fillDeck();
               retryCnt = 5;
            }
            continue;
         } else {
            break;
         }
      }
      return card;
   }

   /**
    * Deal cards from deck to pool. Once pool is full, switch to PICK mode.
    * Only allow 1 hazard at a time, and start their timer immediately
    * @param dt
    */
   private void dealToPool(float dt) {
      if ( this.dealStartDelay > 0 ) {
         this.dealStartDelay -= dt;
         if ( this.dealStartDelay <= 0 ) {
            this.dealStartDelay = 0;
            SoundManager.instance().playSound(SoundManager.DEAL);
         }
         return;
      }
      
      this.dealCardDelay -= dt;
      if ( this.dealCardDelay <= 0f ) {
         this.dealCardDelay = DEAL_CARD_DELAY;
         
         CardInfo card = dealCard();
         
         for ( int i=0; i< POOL_SIZE; i++) {
            if ( this.pool[i] == null ) {
               this.pool[i] = card;
               // card has now been added to available pool.
               // see if it is n HAZARD and kick it
               if ( card.isTimedHazard() ) {
                  if ( card.getType().equals(Type.HAZARD) ){
                     this.hazardTimer = HAZARD_TIMEOUT;
                  } else {
                     this.hazardTimer = card.getPower();
                  }
                  this.pool[i].startTimedCard(this.hazardTimer);
                  hazardActivated( card );
               }
               break;
            }
         }
         
         // see if pool is full
         boolean full = true;
         for ( int i=0; i< POOL_SIZE; i++) {
            if ( this.pool[i] == null ) {
               full = false;
               break;
            }
         }
         
         // deck is now full; allow card picks
         if ( full == true ) {
            this.dealCardDelay = 0;
            if ( this.firstDeal ) {
               this.firstDeal = false;
               this.state = State.PLAN;
               if ( Settings.instance().rulesEverSeen() == false ) {
                  this.priorState = State.PLAN;
                  this.state = State.HELP;
               } 
            } else {
               this.state = State.PLAY;
            }
         }
      }
   }
   
   /**
    * Hazard card just turned up in pool; activate it an notfy listeners
    * @param card
    */
   private void hazardActivated(CardInfo card) {
      this.currHazard = card;
      if ( this.hazardListener != null ) {
         this.hazardListener.newHazard(card);
      }
      if ( this.currHazard.getImageName().equals("bat") ) {
         SoundManager.instance().loopSound(SoundManager.BAT_FLAP);
      } else if ( this.currHazard.getImageName().equals("rockslide") ) {
         SoundManager.instance().playSound(SoundManager.ROCK_SLIDE);
      } else if ( this.currHazard.getImageName().equals("malfunction")) {
         startMalfunction();
      } else if ( this.currHazard.getImageName().equals("confuse") ) {
         SoundManager.instance().loopSound(SoundManager.CONFUSE);
      } else if ( this.currHazard.getImageName().equals("wave") ) {
         SoundManager.instance().loopSound(SoundManager.WAVE);
      } else if ( this.currHazard.getImageName().equals("slow") ) {
         SoundManager.instance().loopSound(SoundManager.SLOW);
      }
   }
   
   private void startMalfunction() {
      this.shieldTimer = -1;
      for ( int i=0;i<this.pool.length; i++) {
         if ( this.pool[i] != null && this.pool[i].isAbeCard() ) {
            this.pool[i] = null;
         }
      }
      for ( int i=0;i<this.replaceCardTimes.length; i++) {
         this.replaceCardTimes[i] = -1;
      }
   }

   /**
    * Current hazard has timed out. Handle it. Some cards lose their
    * effect, other trigger damaage or game over
    */
   private void hazardEnded() {
      if ( this.currHazard.getType().equals(Type.HAZARD)) {
         boolean whackTheBitch = false;
         if ( this.currHazard.getImageName().equals("bat") ) {
            SoundManager.instance().stopSound(SoundManager.BAT_FLAP);
            if ( isShielded() == false ) {
               SoundManager.instance().playSound(SoundManager.BAT_ATTACK);
               whackTheBitch= true;
            }
         } else if ( this.currHazard.getImageName().equals("rockslide") ) {
            SoundManager.instance().stopSound(SoundManager.ROCK_SLIDE);
            if ( isShielded() == false ) {
               SoundManager.instance().playSound(SoundManager.HEAD_BONK);
               whackTheBitch = true;
            }
         }
         
         if ( whackTheBitch ) {
            this.health -= this.currHazard.getPower();
            this.health = Math.max(0, this.health);
         } else {
            SoundManager.instance().playSound(SoundManager.HAPPY);
         }
      } else {
         
         if ( this.currHazard.getImageName().equals("slow") ) {
            SoundManager.instance().stopSound(SoundManager.SLOW);
         } else if ( this.currHazard.getImageName().equals("wave") ) {
            SoundManager.instance().stopSound(SoundManager.WAVE);
         } else if ( this.currHazard.getImageName().equals("confuse") ) {
            SoundManager.instance().stopSound(SoundManager.CONFUSE);
         } 
         
         SoundManager.instance().playSound(SoundManager.HAPPY);
         if ( this.currHazard.getType().equals(Type.MALFUNCTION)) {
            replaceMalfunctionedCards();
         }
      }
      if ( this.hazardListener != null ) {
         this.hazardListener.hazardTimedOut(this.currHazard);
      }
      this.currHazard = null;
   }
   
   private void replaceMalfunctionedCards() {
      for ( int i=0; i<this.pool.length; i++) {
         if ( this.pool[i] == null && this.replaceCardTimes[i] == -1) {
            this.replaceCardTimes[i] = REPLACE_CARD_DELAY;
         }
      }
   }

   /** 
    * Ike bashed into something during execution of a move.
    * For straight on moves, end the move and damage. 
    * @param yBlocked 
    * @param xBlocked 
    * @return Returns TRUE if damage was dealt
    */
   public boolean interruptMove(boolean xBlocked, boolean yBlocked) {
      if ( this.currCard == null ) {
         return false;
      }
      if ( !this.currCard.isDiagonalMove() && (xBlocked || yBlocked)  ) {
         this.currClimb = 0;
         this.currSideways = 0;
         if ( isShielded() == false ) {
            this.health--;
            this.health = Math.max(0, this.health);
         }
         return true;
      }
      return false;
   }
   
   public boolean isHazardActive() {
      return (this.currHazard != null);
   }
   
   public boolean isImpediment() {
      if ( this.currHazard == null  ) {
         return false;
      }
      
      return ( this.currHazard.getType().equals(Type.REDUCE) || 
               this.currHazard.getType().equals(Type.FLIP) );
   }
   
   /**
    * Check if shield is active
    * @return
    */
   public boolean isShielded() {
      return (this.shieldTimer > 0);
   }
   
   /**
    * Check if the pony tank is in use
    * @return
    */
   public boolean hasTank() {
      return (this.tankTimer > 0);
   }
   
   public float getClimbDistance( float dt ) {
      if ( this.currClimb > 0 ) {
         
         if ( this.delayMoveY > 0) {
            this.delayMoveY-=dt;
            this.delayMoveY = Math.max(0, this.delayMoveY);
            if ( this.delayMoveY == 0 ) {
               SoundManager.instance().playSound(SoundManager.CLIMB);
            }
            return 0;
         } 
         
         float del = dt*CLIMB_RATE;
         this.totalY+=del;
         if ( Math.abs(this.totalY) >= maxY ) {
            this.delayMoveY = MOVE_DELAY;
            this.totalY = 0;
         }
            
         this.currClimb -= del;
         if ( this.currClimb < 0 ) {
            del += this.currClimb;
            this.currClimb = 0;
            this.delayMoveY = 0;
            this.totalY = 0;
         }
         return del;
         
         
      } else if ( this.currClimb < 0 ) {
         float del = dt*CLIMB_RATE;
         this.currClimb += del;
         if ( this.currClimb > 0 ) {
            del -= this.currClimb;
            this.currClimb = 0;
         }
         return -1*del;
      }
      return 0;
   }
   
   public boolean isClimbing() {
      return (this.currCard != null &&  this.currCard.getType().equals(Type.MOVE));
   }
   public int getClimbAngle() {
      if ( this.currCard != null ) {
         return this.currCard.getAngle();
      }
      return 0;
   }
   
   public float getSidewaysDistance( float dt ) {
      if ( this.delayMoveX > 0) {
         this.delayMoveX-=dt;
         this.delayMoveX = Math.max(0, this.delayMoveX);
         if ( this.delayMoveX == 0 ) {
            SoundManager.instance().playSound(SoundManager.SIDEWAYS);
         }
         return 0;
      } 
      
      float del = dt*CLIMB_RATE;
      this.totalX+=del;
      if ( Math.abs(this.totalX) >= maxX ) {
         this.delayMoveX = MOVE_DELAY;
         this.totalX = 0;
      }
      
      if ( this.currSideways > 0 ) {
         this.currSideways -= del;
         if ( this.currSideways < 0 ) {
            del += this.currSideways;
            this.currSideways = 0;
         }
         return del;
      } else if ( this.currSideways < 0 ) {
         this.currSideways += del;
         if ( this.currSideways > 0 ) {
            del -= this.currSideways;
            this.currSideways = 0;
         }
         return -1*del;
      }
      return 0;
   }
   
   public boolean executeCard( int index) {
      if (index < 0 || index > POOL_SIZE ) {
         return false;
      }
      if ( this.pool[index] == null || this.pool[index].isActive() ) {
         return false;
      }
      
      // all but moves are executed immediately
      CardInfo execCard =  this.pool[index];
      boolean played = false;
      if ( execCard.getType().equals(CardInfo.Type.BOOST)) {
         
         SoundManager.instance().playSound(SoundManager.DIAMOND);
         this.pool[index].activate();
      } else if ( execCard.getType().equals(Type.DEFEND )) {
         
         SoundManager.instance().playSound(SoundManager.DIAMOND);
         int boosts = getBoostCount();
         this.shieldTimer = SHIELD_DURATION;
         if ( boosts >  0) {
            this.shieldTimer += (SHIELD_DURATION*0.5f*boosts);
         }
         this.pool[index].startTimedCard(this.shieldTimer);
         
      } else if ( execCard.getType().equals(Type.TANK)) {
         
         SoundManager.instance().playSound(SoundManager.TANK);
         int boosts = getBoostCount();
         this.tankTimer = TANK_DURATION;
         if ( boosts > 0 ) {
            this.tankTimer += (TANK_DURATION*0.5f*boosts);
         }
         this.pool[index].startTimedCard(this.tankTimer);
         
      } else if ( execCard.getType().equals(CardInfo.Type.HEAL)) {
         
         SoundManager.instance().playSound(SoundManager.DIAMOND);
         this.health += execCard.getPower();
         this.health = Math.min(MAX_HEALTH, this.health);
         played = true;   
      } else if ( execCard.getType().equals(CardInfo.Type.WILD)) {
         
         SoundManager.instance().playSound(SoundManager.DIAMOND);
         this.priorState = this.state;
         this.state = State.RECALIBRATE;
         this.pool[index].activate();
         
      } else if ( execCard.getType().equals(CardInfo.Type.MOVE)) {
         
         SoundManager.instance().playSound(SoundManager.CLICK);
         played = true; 
         for (int i=0; i<getBoostCount();i++ ) {
            execCard.boostPower();
         }
         
         this.cardStack.push( execCard );
      }
      
      if ( played ) {
         this.pool[index] = null;
         
         // if there is a malfunction, don't replace timer
         if ( this.currHazard != null && this.currHazard.getType().equals(Type.MALFUNCTION)) {
            return true;
         }
         this.replaceCardTimes[index] = REPLACE_CARD_DELAY;
      }
      
      return true;
   }
   
   private int getBoostCount() {
      int cnt = 0;
      for ( int i=0; i<POOL_SIZE; i++) {
         CardInfo ci = this.pool[i];
         if ( ci != null && ci.isActive() && ci.getType().equals(Type.BOOST)) {
            this.pool[i] = null;
            this.replaceCardTimes[i] = REPLACE_CARD_DELAY;
            cnt++;
         }
      }
      return cnt;
   }
   
   public void trashCard( int idx ) { 
      if ( idx >= 0 && idx < POOL_SIZE ) {
         this.pool[idx] = null;
         this.replaceCardTimes[idx] = REPLACE_CARD_DELAY;
      }
   }

   /**
    * Reverse the selected card from the pool
    * @param idx
    */
   public void recalibrateCard(int idx, int newAngle) {
     this.state = this.priorState;
     if ( idx >= 0 && idx < POOL_SIZE ) {
         CardInfo ci = this.pool[idx];
         if ( ci != null && ci.canRecalibrate() ) {
            ci.recalibrate(newAngle);
         } 
      }
   }
   
   /**
    * Listen for hazard events
    */
   public interface HazardListener {
      public void newHazard( CardInfo hazard );
      public void hazardTimedOut( CardInfo hazard );
      public void hazardCleared();
   }
}
