package com.studio332.omega.objects;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.studio332.omega.Climber;
import com.studio332.omega.model.CardInfo;
import com.studio332.omega.model.ClimberGame;
import com.studio332.omega.model.CardInfo.Type;
import com.studio332.omega.model.ClimberGame.State;
import com.studio332.omega.objects.Meter.Orientation;
import com.studio332.omega.util.Assets;
import com.studio332.omega.util.ClimberActions;
import com.studio332.omega.util.SoundManager;

public class CardPool extends Group implements Recalibrator.Listener {
   private ClimberGame game;
   private float cardH;
   private float cardW;
   private int touchedCardIndex = -1;
   private long lastTouchCardTime = -1;
   private Image trashOverlay;
   private AtlasRegion dimCard;
   private AtlasRegion highlightCard;
   private AtlasRegion trashCard;
   private AtlasRegion activeCard;
   private boolean trashOnUp = false;
   private boolean trashing = false;
   private Image zoomedCard;
   private Meter recharge[] = new Meter[ClimberGame.POOL_SIZE];
   private Meter active[] = new Meter[ClimberGame.POOL_SIZE];
   private Recalibrator recalibrator;
   private Hint hint;
   
   private static final long ZOOM_TIME = 300;
   
   public CardPool( ClimberGame g ) {
      super();
      
      this.game = g;
      AtlasRegion a = Assets.instance().getAtlasRegion("n1");
      this.cardH = a.getRegionHeight();
      this.cardW = a.getRegionWidth();
      
      this.dimCard = Assets.instance().getAtlasRegion("dim-card");
      this.highlightCard = Assets.instance().getAtlasRegion("card-highlight");
      this.activeCard = Assets.instance().getAtlasRegion("card-active");
      this.trashCard = Assets.instance().getAtlasRegion("trash-card");
      
      addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if ( recalibrator.isVisible() ||  game.getState().equals(State.PLAN) || game.isPlaying() == false  ) {
               return false;
            }
            touchedCardIndex = -1;
            trashOnUp = false;
            findTouchedCardIndex(x,y);
            return true;
         }
         
         @Override
         public void touchDragged(InputEvent event, float x, float y, int pointer) {
            if (  game.getState().equals(State.PLAN) || game.isPlaying() == false ) {
               return;
            }
            findTouchedCardIndex(x,y);
         }
         
         @Override
         public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            if (  game.getState().equals(State.PLAN) || game.isPlaying() == false ) {
               return;
            }
            
            if ( touchedCardIndex > -1 ) {
               if ( trashOnUp ) {
                  trashCard();
               } else {
                  if ( zoomedCard.isVisible() ) {
                     zoomedCard.setVisible(false);
                     touchedCardIndex = -1;
                  }
                  executeCard();
               }
            } else {
               touchedCardIndex = -1;
            }
         }
      });
      
      this.zoomedCard = new Image(Assets.instance().getDrawable("n1-big"));
      this.zoomedCard.setVisible(false);
      this.zoomedCard.setPosition(-this.zoomedCard.getWidth()-11, -6);
      addActor(this.zoomedCard);
      
      // setup card timers
      for (int i=0; i< ClimberGame.POOL_SIZE; i++ ) {
         this.recharge[i] = new Meter(this.cardW - 4, this.cardH - 4, Orientation.HORIZONTAL);
         this.active[i] = new Meter(31, this.cardH-4, Orientation.VERTICAL);
      }
      
      this.trashOverlay = new Image( Assets.instance().getPixel());
      this.trashOverlay.setWidth(53);
      this.trashOverlay.setHeight(Climber.TGT_HEIGHT-10);
      this.addActor(this.trashOverlay);
      
      this.recalibrator = new Recalibrator();
      this.recalibrator.setListener( this );
      this.recalibrator.setPosition(-7, -7);
      
      this.hint = new Hint();
      addActor(this.hint);
      addActor(this.recalibrator);

   }
   
   @Override
   public void setPosition(float x, float y) {
      float meterY = y;
      for (int i=0; i< ClimberGame.POOL_SIZE; i++ ) {
         this.recharge[i].setPosition(x+2, meterY+2);
         this.active[i].setPosition(x+2, meterY+2);
         meterY+=(this.cardH+2);
      }
      this.trashOverlay.setPosition(getWidth()-this.trashOverlay.getWidth()-10, -4);
      super.setPosition(x, y);
   }
   
   private void findTouchedCardIndex( float x, float y) {
      if ( x < 0) {
         this.touchedCardIndex = -1;
         return;
      }
      
      // hold some key info
      int priorIdx = this.touchedCardIndex;
      float touchedY = y;
      
      // drug over trash area?
      this.trashOnUp = false;
      if ( x > this.cardW ) {
         this.trashOnUp = true;
         return;
      }

      this.touchedCardIndex = -1;
      for (int r = 0; r < 8; r++) {
         if ( touchedY >= ((this.cardH+2)*r) && touchedY <= ((this.cardH+2)*(r+1)) ) {
            if ( this.game.getPoolCard(r) != null) {
               this.touchedCardIndex = r;
               break;
            }
         }
      }
      
      // track how long its been since the last cards touched changed
      if ( this.touchedCardIndex > -1 && this.touchedCardIndex != priorIdx) {
         this.lastTouchCardTime = System.currentTimeMillis();
      }
   }
   
   private boolean touchedTimedCard() {
      if ( this.touchedCardIndex == -1 ) {
         return false;
      }
      CardInfo ci = this.game.getPoolCard(this.touchedCardIndex);
      if ( ci == null ) {
         return false;
      }
      return ( ci.isTimedHazard() ||  ci.isTimerActive() );
   }
   
   private void trashCard() {
      this.trashing = true;
      this.trashOnUp  = false;
      if ( touchedTimedCard()  ) {
         CardInfo c = this.game.getPoolCard(this.touchedCardIndex);
         if ( c.getType().equals(Type.TANK) || c.getType().equals(Type.DEFEND) ) {
            this.hint.showTrashActiveHint();
         } else {
            this.hint.showTrashHazardHint();
         }
         SoundManager.instance().playSound(SoundManager.NO_PLAY);
         this.hint.setPosition(-this.hint.getWidth()-18, this.touchedCardIndex*cardH+10);
      } else {
         SoundManager.instance().playSound(SoundManager.TRASH);
         trashOverlay.setColor(new Color(0.7f,0,0, 0.5f));
         trashOverlay.addAction( sequence(ClimberActions.pulse(0.5f), new Action() {
   
            @Override
            public boolean act(float delta) {
               trashing = false;
               return false;
            }
            
         }));
         this.game.trashCard( this.touchedCardIndex);
      }
      this.touchedCardIndex = -1;
      this.lastTouchCardTime = -1;
   }
   
   private void executeCard() {
      if ( touchedTimedCard()  ) {
         SoundManager.instance().playSound(SoundManager.NO_PLAY);
         this.hint.showPlayHazardHint();
         this.hint.setPosition(-this.hint.getWidth()-18, this.touchedCardIndex*cardH+10);
         this.touchedCardIndex = -1;
         return;
      }
      
      if ( this.game.executeCard(this.touchedCardIndex) ) {
         // if the last touch put us in recalibrate mode, handle it
         if ( this.game.getState().equals(State.RECALIBRATE) ) {
            this.recalibrator.setVisible(true);
         } else {
            this.touchedCardIndex = -1;
         }
      }
   }
   
   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
      
      // highlight stuff during trash moves
      if ( this.trashing == false ) {
         if (this.trashOnUp) {
            this.trashOverlay.setColor(new Color(.075f, .75f, .94f, 0.4f));
         } else {
            this.trashOverlay.setColor(new Color(0,0,0,0));
         }
      }
      
      if ( this.game.isOver() == false) {
         batch.end();
         
         // draw the replace meter for each card that is to be replaced
         for ( int i=0; i<ClimberGame.POOL_SIZE; i++) {
            float cardRecharge = this.game.getCardReplaceTime(i);
            if ( this.game.getPoolCard(i) == null && cardRecharge > 0) {
               float rt = ClimberGame.REPLACE_CARD_DELAY - cardRecharge;
               this.recharge[i].draw(batch, rt/ClimberGame.REPLACE_CARD_DELAY);
            }
         }
         batch.begin();
      }
            
      if ( this.recalibrator.isVisible() == false ) {
         this.zoomedCard.setVisible(false);
         this.zoomedCard.getColor().a = 0f;
         if ( this.touchedCardIndex > -1 && System.currentTimeMillis()-this.lastTouchCardTime > ZOOM_TIME) {
            CardInfo ci = this.game.getPoolCard(this.touchedCardIndex);
            if ( ci != null ) {
               this.zoomedCard.setDrawable(Assets.instance().getDrawable(ci.getImageName()+"-big"));
               this.zoomedCard.getColor().a = 0.8f;
               this.zoomedCard.setVisible(true);
            } else {
               // this can happen if someone is holding a hazard and its
               // timer expires. the card is nulled out of the pool, but there
               // is still a reference to it with touchedCardIndex. Just reset the index.
               this.touchedCardIndex = -1;
            }
         }
      } else {
         this.zoomedCard.setVisible(false);
      }
      
      float x = getX();
      float y = getY();
      for ( int i=0; i<ClimberGame.POOL_SIZE; i++ ) {
         CardInfo ci = this.game.getPoolCard(i);
         if ( ci != null && this.game.getCardReplaceTime(i) <= 0) {
            drawCard(batch, x,y, i, ci);
         }
                 
         y+= this.cardH+2;
      }    
      
      batch.end();
      
      if ( this.game.isOver() == false ) {
         for ( int i=0; i<ClimberGame.POOL_SIZE; i++) {
            CardInfo ci = this.game.getPoolCard(i);
            if (  ci != null ) {
               if (ci.isTimerActive() && this.game.getCardReplaceTime(i) <= 0 ) {
                  if ( ci.isTimedHazard() ) {
                     this.active[i].setHazardSkin();
                  } else {
                     this.active[i].setMeterSkin();
                  }
                  this.active[i].draw(batch, ci.getTimerPercent());
               }
            }
         }
      }
   
      batch.begin();
            
      super.draw(batch, parentAlpha);
   }

   private void drawCard(SpriteBatch batch, float x, float y, int cardIdx, CardInfo ci) {
      AtlasRegion card = Assets.instance().getAtlasRegion(ci.getImageName());
      batch.draw(card, x, y);
     
      if ( this.trashOnUp && cardIdx == this.touchedCardIndex  ) {
         batch.draw(this.trashCard, x, y);
         return;
      }
            
      // card unavailable to touch, dim it
      if ( this.recalibrator.isVisible() || 
           this.game.getState().equals(State.DEAL) || 
           this.game.getState().equals(State.PLAN)) { 
         batch.draw(this.dimCard, x, y);
         return;
      } 
      
      // this is a card like boost that is active. render the border
      if ( ci.isActive() || ( ci.isTimerActive() && ci.isTimedHazard() == false) ) {
         batch.draw(this.dimCard, x, y);
         batch.draw(this.activeCard, x, y);
         return;
      } 
      
      if ( this.touchedCardIndex > -1 && this.touchedCardIndex == cardIdx ) {
         batch.draw(this.activeCard, x, y);
         return;
      }
      
      if ( this.game.getState().equals(State.RECALIBRATE)) {
         if ( ci.getType().equals(CardInfo.Type.MOVE)) {
            batch.draw(this.highlightCard, x, y);
         } else {
            batch.draw(this.dimCard, x, y);
         }
      }
   }

   @Override
   public void recalibrated(int newAngle) {
      this.game.recalibrateCard(this.touchedCardIndex, newAngle);
      this.recalibrator.setVisible(false);
      executeCard();
   }  
}
