package com.studio332.omega.objects;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.studio332.omega.model.ClimberGame;
import com.studio332.omega.objects.Meter.Orientation;
import com.studio332.omega.util.Assets;
import com.studio332.omega.util.ClimberActions;
import com.studio332.omega.util.SoundManager;

public class CliffMap extends Actor {
   private enum Mode {NONE, SCAN, RECHARGE, READY};
   
   private ClimberGame model;
   private AtlasRegion climbable;
   private AtlasRegion climbable2;
   private AtlasRegion blocked;
   private AtlasRegion blocked2;
   private Cliff cliff;
   private AtlasRegion ikeMarker;
   private AtlasRegion ikeMarker2;
   private ShapeRenderer shapeRenderer;
   private float timeout = 0f;
   private float scanTime = 0;
   private float tileSz;
   private Random rand = new Random(System.currentTimeMillis());
   private Mode mode = Mode.NONE;
   private Meter rechargeMeter;
   private Label rechargeLabel;
   private Label warnLabel;
   private Label warnMessage;
   private boolean showWarning = false;
   private Animation omegaAnim;
   private float stateTimer =0;
   
   private static final float SCAN_TIME = 6.0f;
   private static final int MAP_HEIGHT = 30;
   private static final int MAP_WIDTH = 17;
   private static final float RECHAGRE_TIME = 3.0f;
   private static final float METER_HEIGHT = 20f;

   
   public CliffMap(ClimberGame model, Cliff cliff) {
      super();
      
      this.model = model;
      this.shapeRenderer = new ShapeRenderer();
      
      this.cliff = cliff;
      this.climbable = Assets.instance().getAtlasRegion("climbable");
      this.climbable2 = Assets.instance().getAtlasRegion("climbable2");
      this.blocked = Assets.instance().getAtlasRegion("blocked");
      this.blocked2 = Assets.instance().getAtlasRegion("blocked2");
      
      this.ikeMarker = Assets.instance().getAtlasRegion("ike-marker");
      this.ikeMarker2 = Assets.instance().getAtlasRegion("ike-marker2");
      this.tileSz = this.climbable.getRegionWidth();
      
      setWidth(this.tileSz*MAP_WIDTH+4);
      setHeight(this.tileSz*MAP_HEIGHT+METER_HEIGHT+4);
      
      Color c = new Color(0.396f, .878f, 0.965f, 1.0f);
      LabelStyle st = new LabelStyle(Assets.instance().getFont(), c);
      this.rechargeLabel = new Label("recharge", st);
      this.rechargeLabel.setFontScale(0.75f);
      this.rechargeLabel.setWidth(getWidth()-4);
      this.rechargeMeter = new Meter(getWidth(), METER_HEIGHT, Orientation.HORIZONTAL);
      
      c = new Color(0.7f, .0f, 0.0f, 1.0f);
      st = new LabelStyle(Assets.instance().getFont(), c);
      this.warnLabel = new Label("ALERT", st);
      this.warnLabel.setFontScale(1.2f);
      this.warnLabel.setAlignment(Align.center);
      this.warnLabel.setWidth(getWidth()-4);
      this.warnLabel.addAction( forever( 
            sequence(ClimberActions.fadeTo(1f, .5f), ClimberActions.fadeTo(0.8f, .5f))));
      
      this.warnMessage = new Label("Bats\nnearby!", st);
      this.warnMessage.setFontScale(0.8f);
      this.warnMessage.setAlignment(Align.center);
      this.warnMessage.setWidth(getWidth()-4);
      
      // omega anim
      Array<TextureRegion> a = new Array<TextureRegion>();
      for (int i=1;i<=8;i++) {
         a.add(Assets.instance().getAtlasRegion("omega"+i));
      }
      this.omegaAnim = new Animation(0.15f, a, Animation.LOOP);
   }
   
   @Override
   public void act(float delta) {
      this.warnLabel.act(delta);
      super.act(delta);
   }
 
   @Override
   public void setPosition(float x, float y) {
      this.rechargeMeter.setPosition(x,y);
      this.rechargeLabel.setPosition(x+6, y+22);
      this.warnLabel.setPosition(x+2, y+228);
      this.warnMessage.setPosition(x+2, y+155);
      super.setPosition(x, y);
   }
   
   public void showScan( ) {
      if ( this.mode.equals(Mode.READY) || this.mode.equals(Mode.NONE) ) {
         this.timeout = SCAN_TIME;
         this.scanTime = this.timeout;
         if ( this.mode.equals(Mode.NONE) ) {
            this.timeout = SCAN_TIME*0.5f;
            this.scanTime = this.timeout;
         }
         this.mode = Mode.SCAN;
         SoundManager.instance().playSound(SoundManager.SCAN);
      }
   }
   
   public void clearWarning() {
      this.showWarning = false;
      SoundManager.instance().stopSound(SoundManager.ALERT);
   }
   
   public boolean isAlert() {
      return this.showWarning;
   }
   
   public void showWarning( String warnType ) {
      if ( this.model.isOver()) {
         return;
      }
      this.showWarning = true;
      if ( warnType.equalsIgnoreCase("rockslide") ) {
         this.warnMessage.setText("Rockslide\ndetected!");
      } else if ( warnType.equalsIgnoreCase("bat") ) {
         this.warnMessage.setText("Bats\nnearby!");
      } else if ( warnType.equalsIgnoreCase("slow") ) {
         this.warnMessage.setText("Loose\nRocks!");
      } else if ( warnType.equalsIgnoreCase("confuse") ) {
         this.warnMessage.setText("Confused!");
      } else if ( warnType.equalsIgnoreCase("water") ) {
         this.warnMessage.setText("Water\nclose!");
         SoundManager.instance().loopSound(SoundManager.ALERT);
      } else if ( warnType.equalsIgnoreCase("wave")) {
         this.warnMessage.setText("Tidal\nSurge!");
      }
   }
   
   public boolean isScanning() {
      return ( this.mode.equals(Mode.SCAN) ) ;
   }
   
   public void updateTimer( float delSec ) {
      if ( this.model.isPaused() || this.model.isOver()) {
         return;
      }
      
      if ( this.timeout > 0f ) {
         this.timeout -= delSec;
         this.timeout = Math.max(0, this.timeout);
         
         if ( this.timeout == 0 ) {
            if ( this.mode.equals(Mode.SCAN)) {
               SoundManager.instance().stopSound(SoundManager.SCAN);
               this.mode = Mode.RECHARGE;
               this.timeout = RECHAGRE_TIME;
               this.rechargeLabel.setText("recharge");
            } else if ( this.mode.equals(Mode.RECHARGE)) {
               this.mode = Mode.READY;
               this.rechargeLabel.setText("Ready");
            }
         }
      }
   }
   
   public boolean isRecharging() {
      return (this.mode.equals(Mode.RECHARGE) );
   }
   
   public boolean isReady() {
      return (this.mode.equals(Mode.READY) );
   }
   
   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      this.stateTimer += Gdx.graphics.getDeltaTime(); 

      if ( this.model.isOver() || this.model.isMalfunction() ) {
         clearWarning();
         return;
      }
      
      if ( this.model.isPaused()){
         return;
      }
      
      batch.end();  
      
      // just draw the frame of the map
      this.shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
      this.shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
      this.shapeRenderer.translate(getX(), getY(), 0);
      this.shapeRenderer.setColor(0.0f, 0.33f, 0.4f, 1f);
      this.shapeRenderer.begin(ShapeType.Filled);
      this.shapeRenderer.rect(0,0,getWidth(),getHeight());
      this.shapeRenderer.setColor(0, 0, 0, 1f);
      this.shapeRenderer.rect(2,2,getWidth()-4, getHeight()-4);
      this.shapeRenderer.end();
      
      if ( isScanning() ) {
         renderMapView(batch, parentAlpha);
      } else {
         float rt = RECHAGRE_TIME - this.timeout;
         this.rechargeMeter.draw(batch, rt/RECHAGRE_TIME);

      }
      
      // dim out a rect behind the text
      if ( this.showWarning ) {
         Gdx.gl.glEnable(GL20.GL_BLEND);
         Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
   
         this.shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
         this.shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
         
         this.shapeRenderer.setColor(0.0f, 0.0f, 0.0f, 0.4f);
         this.shapeRenderer.begin(ShapeType.Filled); 
         this.shapeRenderer.rect(getX()+2,getY()+2,getWidth()-4, getHeight()-4);
         this.shapeRenderer.end();
         
         Gdx.gl.glDisable(GL20.GL_BLEND);
      }
      
      batch.begin();
  
      if ( isScanning() == false) {
         this.rechargeLabel.draw(batch, parentAlpha);
         if (  this.showWarning == false  ) {
            TextureRegion omega = this.omegaAnim.getKeyFrame(this.stateTimer, true);
            batch.draw(omega, 30,180);                     
         }
      }
      
      if ( this.showWarning) {
         this.warnLabel.draw(batch, parentAlpha);
         this.warnMessage.draw(batch, parentAlpha);
      }  
   }
   
   private void renderMapView(SpriteBatch batch, float parentAlpha) {

      float rt = this.timeout;
      this.rechargeMeter.draw(batch, rt/this.scanTime);
      
      batch.begin();
      int minRow = this.cliff.getBottomRow();
      float mapX = getX()+2;
      float mapY = getY()+METER_HEIGHT;
      for ( int row = 0; row<MAP_HEIGHT; row++ ) {
         for ( int col=0; col<MAP_WIDTH; col++) {
            if ( (row+minRow) >= this.cliff.getMaxRows() ) {
               batch.draw(this.climbable, mapX+this.tileSz*col, mapY+this.tileSz*row);
            } else {
               if ( this.cliff.isClimbable(row+minRow, col)) {
                  if ( this.rand.nextInt(100) > 50 ) {
                     batch.draw(this.climbable, mapX+this.tileSz*col, mapY+this.tileSz*row);
                  } else {
                     batch.draw(this.climbable2, mapX+this.tileSz*col, mapY+this.tileSz*row);
                  }
               } else {
                  if ( this.rand.nextInt(100) > 50 ) {
                     batch.draw(this.blocked, mapX+this.tileSz*col, mapY+this.tileSz*row);
                  } else {
                     batch.draw(this.blocked2, mapX+this.tileSz*col, mapY+this.tileSz*row);
                  }
               }
            }
         }
      }
     
      // the -8 is to account for the fat border around the scan
      float x = this.cliff.getIkeX();
      float xRatio = (this.getWidth()-8) / this.cliff.getFullWidth();
      float scaledX = x*xRatio;
      
      float y = this.cliff.getIkeY();
      float yRatio = (this.tileSz*this.cliff.getMaxRows()) / this.cliff.getFullHeight();
      float scaledY = y*yRatio;
       scaledY -= (this.tileSz*minRow);

      if (this.rand.nextInt(100) > 50) {
         batch.draw(this.ikeMarker, mapX + scaledX, mapY + scaledY);
      } else {
         batch.draw(this.ikeMarker2, mapX + scaledX, mapY + scaledY);
      }
      batch.end();
      
   }
}
