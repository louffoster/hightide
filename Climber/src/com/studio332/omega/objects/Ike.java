package com.studio332.omega.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.studio332.omega.model.ClimberGame;
import com.studio332.omega.util.Assets;

public class Ike extends Image {
   private enum MoveState {
      NONE, VERTICAL, LEFT, RIGHT
   };

   private boolean stopPending = false;
   private boolean died = false;
   private Animation anim;
   private Animation leftAnim;
   private Animation rightAnim;
   private Animation deathAnim;
   private float stateTime = 0f;
   private TextureRegion lastFrame = null;
   private MoveState moveState = MoveState.NONE;
   private ClimberGame game;

   public Ike(ClimberGame game) {
      super(Assets.instance().getAtlasRegion("ikeDrown"));
      setOrigin(getWidth() * 0.5f, getHeight() * 0.5f);
      this.game = game;
      
      // vertical climb animation
      Array<TextureRegion> a = new Array<TextureRegion>();
      for (int i=1;i<=12;i++) {
         a.add(Assets.instance().getAtlasRegion("ikeUpDown"+i));
      }
      // rate calc. time for 1 move should = time for 1 full cycle.
      float secPerMove = ClimberGame.CLIMB_DISTANCE / ClimberGame.CLIMB_RATE;
      float frameTime = (secPerMove+(secPerMove*0.25f)) / a.size; 
      this.anim = new Animation(frameTime, a, Animation.LOOP); 
      
      // side anim
      Array<TextureRegion> la = new Array<TextureRegion>();
      for (int i=1;i<=13;i++) {
         la.add(Assets.instance().getAtlasRegion("ikeRight"+i));
      }
      frameTime = (secPerMove+(secPerMove*0.25f)) / (la.size+11); 
      this.leftAnim = new Animation(frameTime, la, Animation.LOOP); 
      
      // side anim
      Array<TextureRegion> ra = new Array<TextureRegion>();
      for (int i=1;i<=13;i++) {
         ra.add(Assets.instance().getAtlasRegion("ikeLeft"+i));
      }
      frameTime = (secPerMove+(secPerMove*0.25f)) / (ra.size+11); 
      this.rightAnim = new Animation(frameTime, ra, Animation.LOOP); 
      
      Array<TextureRegion> da = new Array<TextureRegion>();
      for (int i=1;i<=8;i++) {
         da.add(Assets.instance().getAtlasRegion("ikeFall"+i));
      }
      this.deathAnim = new Animation(0.15f, da, Animation.NORMAL);
      
      // init anim timing data
      this.stateTime = 0f;
      this.lastFrame = this.anim.getKeyFrame(this.stateTime, true);
   }
   
   public void setClimbAngle( int angle )  {
      switch (angle) {
         case 90:
            this.moveState = MoveState.RIGHT;
            break;
         case 270:
            this.moveState = MoveState.LEFT;
            break;
         default:
            this.moveState = MoveState.VERTICAL;
      }
   }
   
   public void stopMoving() {
      if ( this.stopPending == false && this.moveState != MoveState.NONE) {
         this.stopPending = true;
      }
   }

   public void kill() {
      this.died = true;
      this.stateTime = 0;
   }

   public boolean isDead() {
      return this.died;
   }

   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
      if ( isDead() ) {
         setWidth(200);
         this.stateTime += Gdx.graphics.getDeltaTime();
         TextureRegion r = this.deathAnim.getKeyFrame(this.stateTime, false);
         setDrawable(new TextureRegionDrawable(r) );
      } else if ( this.game.isPlaying() ) {
         if ( this.moveState != MoveState.NONE) {
            this.stateTime += Gdx.graphics.getDeltaTime();
            TextureRegion r = null;
            if ( this.moveState == MoveState.VERTICAL) {
               r = this.anim.getKeyFrame(this.stateTime, true);
               finishAnimation(this.anim);
            } else if ( this.moveState == MoveState.LEFT) {
               r = this.leftAnim.getKeyFrame(this.stateTime, true);
               finishAnimation(this.leftAnim);
            } else if ( this.moveState == MoveState.RIGHT) {
               r = this.rightAnim.getKeyFrame(this.stateTime, true);
               finishAnimation(this.rightAnim);
            }
            setDrawable(new TextureRegionDrawable(r));
   
         } else {
            this.stateTime = 0;
            setDrawable(new TextureRegionDrawable(this.lastFrame));
         
         }
      }
      super.draw(batch, parentAlpha);
   }
   
   private void finishAnimation(Animation animation) {
      if ( this.stopPending && animation.getKeyFrameIndex(this.stateTime) == 0 ) {
         this.stopPending = false;
         this.moveState = MoveState.NONE;
      }
   }
}
