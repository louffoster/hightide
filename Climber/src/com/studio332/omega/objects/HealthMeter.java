package com.studio332.omega.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.studio332.omega.model.ClimberGame;
import com.studio332.omega.util.Assets;

public class HealthMeter extends Actor {
   private ClimberGame model;
   
   public HealthMeter( ClimberGame model ) {
      super();
      this.model = model;
   }
   
   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
      
      batch.draw( Assets.instance().getAtlasRegion("stamina-"+this.model.getHealth()), 
            getX(), getY());
         
      super.draw(batch, parentAlpha);
   }
}
