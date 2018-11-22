package com.studio332.omega.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.studio332.omega.model.ClimberGame;

/**
 * Scale map of cliff to show iverall progress percentage
 * 
 * @author lfoster
 *
 */
public class CliffProgress extends Actor {
   private Cliff cliff;
   private ShapeRenderer shapeRenderer;
   private ClimberGame model;
   
   public CliffProgress( ClimberGame model,Cliff cliff ) {
      super();
      this.cliff = cliff;
      this.model = model;
      this.shapeRenderer = new ShapeRenderer();
      setWidth(33);
      setHeight(344);
   }
   
   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      if ( this.model.isMalfunction() || this.model.isOver() ) {
         return;
      }
      
      float ikeT = getHeight()*this.cliff.getPercentProgress();
      float ikeH = getHeight()*(this.cliff.getIkeHeight()*0.55f / this.cliff.getFullHeight());
      
      float water = this.model.getWaterLevelPercent() / 100.0f;
      
      batch.end();
      Gdx.gl.glEnable(GL10.GL_BLEND);
      Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
      
     
      this.shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
      this.shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
     
      this.shapeRenderer.begin(ShapeType.Filled);
      this.shapeRenderer.setColor(new Color(.0f, 0.5f, 0.7f, 0.8f));
      this.shapeRenderer.rect(getX(),getY(), getWidth(), getHeight()*water);
      
      this.shapeRenderer.setColor(new Color(.0f, 0.2f, 0.9f, 1f));
      this.shapeRenderer.rect(getX()+8,getY()+(ikeT-ikeH), getWidth()-16, ikeH);
      
      this.shapeRenderer.end();
     
      Gdx.gl.glDisable(GL10.GL_BLEND);
      batch.begin();
   }

}
