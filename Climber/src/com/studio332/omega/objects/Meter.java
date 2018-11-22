package com.studio332.omega.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Meter {
   public enum Orientation {HORIZONTAL, VERTICAL};
   private ShapeRenderer shapeRenderer;
   private float x;
   private float y;
   private float w;
   private float h;
   private Orientation orientation;
   private Color outlineColor[] = new Color[2];
   private Color fillColor[] = new Color[2];
   private int skin = 0;
   
   public Meter(float width, float height, Orientation orient) {
      this.shapeRenderer = new ShapeRenderer();
      this.w = width;
      this.h = height;
      this.orientation = orient;
      this.skin = 0;
      this.outlineColor[0] = new Color(0.0f, 0.3f, 0.4f, 1f);
      this.fillColor[0] = new Color(0.0f, 0.5f, 0.3f, 1f);
      this.outlineColor[1] = new Color(0.7f, 0.0f, 0.0f, 1f);
      this.fillColor[1] = new Color(0.9f, 0.0f, 0.0f, 1f);
   }
   
   public void setHazardSkin() {
      this.skin = 1;
   }
   public void setMeterSkin() {
      this.skin = 0;
   }
   
   public void setPosition(float x, float y) {
      this.x = x;
      this.y = y;
   }
   
   public void draw(SpriteBatch batch, float percent ) {
      this.shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
      this.shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
      this.shapeRenderer.translate(this.x, this.y, 0);
      this.shapeRenderer.setColor( this.outlineColor[this.skin]  );
      this.shapeRenderer.begin(ShapeType.Filled);
      this.shapeRenderer.rect(0, 0, this.w, this.h );
      this.shapeRenderer.setColor(0.0f, 0.0f, 0.0f, 1f);
      this.shapeRenderer.rect(2,2,this.w-4,this.h-4);
      this.shapeRenderer.setColor( this.fillColor[this.skin] );
      if ( this.orientation.equals(Orientation.HORIZONTAL)) {
         float barW = (this.w-4)*percent;
         this.shapeRenderer.rect(2,2,barW,this.h-4);
      } else {
         float barH = (this.h-4)*percent;
         this.shapeRenderer.rect(2,2,this.w-4,barH);
      }
      this.shapeRenderer.end();
      
   }
}
