package com.studio332.omega.objects;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.studio332.omega.util.Assets;


public class WarningIndicator extends Group {
   private Image warnOn;
   private Action pulse;
   private boolean warn = false;
   
   public WarningIndicator() {
      super ();
      this.warnOn = new Image(Assets.instance().getAtlasRegion("warn-on"));
      addActor(warnOn);
      setWidth(this.warnOn.getWidth());
      setHeight(this.warnOn.getHeight());
      this.warnOn.getColor().a = 0;
   }
   
   public void warn() {

      if ( this.warn == false ) {
         this.warn = true;
         this.pulse = forever(sequence(new Action() {
            
            @Override
            public boolean act(float delta) {
               //SoundManager.instance().playSound(SoundManager.ALERT);
               return true;
            }
         }, fadeIn(0.3f),fadeOut(0.3f)));
         this.warnOn.addAction(this.pulse);
      }
   }
   
   public boolean isOn() {
      return this.warn;
   }

   public void cancelWarn() {
      this.warn = false;
      this.warnOn.removeAction(this.pulse);
      this.warnOn.getColor().a = 0;
   }
   
   @Override
   public void setPosition(float x, float y) {
      this.warnOn.setPosition(x, y);
      super.setPosition(x, y);
   }
   
   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
      this.warnOn.draw(batch, parentAlpha);
      super.draw(batch, parentAlpha);
   }
  
}
