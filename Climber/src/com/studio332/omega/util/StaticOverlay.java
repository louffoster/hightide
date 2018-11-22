package com.studio332.omega.util;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class StaticOverlay extends Image {
   private AtlasRegion[] noise = new AtlasRegion[2];
   private Random rand = new Random(System.currentTimeMillis());
   private Label txt;
   private ShapeRenderer shapeRenderer;

   public StaticOverlay() {
      super();
      setWidth(195);
      setHeight(322);
      this.noise[0] = Assets.instance().getAtlasRegion("static-1");
      this.noise[1] = Assets.instance().getAtlasRegion("static-2");
      
      Color c = new Color(1,1,1, 1.0f);
      LabelStyle st = new LabelStyle(Assets.instance().getFont(), c);
      this.txt = new Label("Malfunction", st);
      this.txt.setFontScale(0.8f);
      txt.setWidth(getWidth());
      this.txt.setAlignment(Align.center);
      this.txt.setPosition(8, 180);
      this.shapeRenderer = new ShapeRenderer();
   }

   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      
      float x=getX()-4;
      float y=getY()-6;
      for ( int i = 0; i<273; i++ ) {
         if ( this.rand.nextInt(100) > 50 ) {
            batch.draw(this.noise[1], x,y);
         } else {
            batch.draw(this.noise[0], x,y);
         }
         x+=16;
         if ( x > getX()+getWidth()) {
            x = getX()-4;
            y += 16;
         }
      }
      
      if ( getColor().a > 0 ) {
         batch.end();  
         
         this.shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
         this.shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
         this.shapeRenderer.translate(getX(), getY(), 0);
         this.shapeRenderer.setColor(0.5f, 0f, 0f, getColor().a);
         this.shapeRenderer.begin(ShapeType.Filled);
         this.shapeRenderer.rect(7, 180, getWidth()-4f, 30);
         this.shapeRenderer.end();
         Gdx.gl20.glLineWidth(1);
         this.shapeRenderer.setColor(1f, 1f, 1f, getColor().a);
         this.shapeRenderer.begin(ShapeType.Line);
         this.shapeRenderer.rect(7, 180, getWidth()-4f, 30);
         this.shapeRenderer.end();
         
         batch.begin();  
         
         this.txt.draw(batch, parentAlpha);
      }
   }
}
