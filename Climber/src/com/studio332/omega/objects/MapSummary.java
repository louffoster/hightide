package com.studio332.omega.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.studio332.omega.model.MapInfo;
import com.studio332.omega.model.Settings;
import com.studio332.omega.util.Assets;

public class MapSummary extends Group {
   private ShapeRenderer shapeRenderer;
   private Label level;
   private Label mapName;
   private Label difficulty;
   private Label bestTime;
   private Group lock;
   
   public MapSummary() {
      super();
      this.shapeRenderer = new ShapeRenderer();
      setWidth(300);
      setHeight(100);
      
      Color c = new Color(0.5f, 0.53f, 0.6f, 1.0f);
      LabelStyle st = new LabelStyle(Assets.instance().getFont(), c);
      this.level = new Label("00:", st);
      this.level.setPosition(15,getHeight()-35);
      addActor(this.level);
      
      this.mapName = new Label("", st);
      this.mapName.setAlignment(Align.left);
      this.mapName.setWidth(getWidth()-50);
      this.mapName.setPosition(62,getHeight()-20);
      addActor(this.mapName);
      
      Label diff = new Label("Difficulty:", st);
      diff.setWidth(getWidth()-30);
      diff.setPosition(15,33);
      addActor(diff);
      
      this.difficulty  = new Label("1", st);
      this.difficulty.setWidth(getWidth()-30);
      this.difficulty.setPosition(15,33);
      this.difficulty.setAlignment(Align.right);
      addActor(this.difficulty);
      
      Label best = new Label("Best:", st);
      best.setWidth(getWidth()-30);
      best.setPosition(15,5);
      addActor(best);
      
      this.bestTime  = new Label("--:--.-", st);
      this.bestTime.setWidth(getWidth()-30);
      this.bestTime.setPosition(15,5);
      this.bestTime.setAlignment(Align.right);
      addActor(this.bestTime);
      
      this.lock = new Group();
      this.lock.setVisible(false);
      Image dim = new Image(Assets.instance().getPixel());
      dim.setWidth(getWidth());
      dim.setHeight(getHeight());
      dim.setColor(0f,0,0,0.7f);
      Image lockImg = new Image( Assets.instance().getDrawable("locked"));
      lockImg.setPosition(
            (getWidth()-lockImg.getWidth())/2, 
            (getHeight()-lockImg.getHeight())/2);
      this.lock.addActor(dim);
      this.lock.addActor(lockImg);
      addActor( this.lock );
      
   }
   
   public void setMapInfo(MapInfo mi) {
      this.level.setText( String.format("%02d:", mi.getLevel()));
      this.mapName.setText(mi.getName());
      this.difficulty.setText(""+mi.getDifficulty());
      if ( mi.getBestTimeSec() == 0 ) {
         this.bestTime.setText("--:--.-");
      } else {
         int mins = (int)(mi.getBestTimeSec()  / 60);
         float sec = mi.getBestTimeSec()  - (mins*60);
         this.bestTime.setText( String.format("%d:%04.1f", mins, sec));
      }
      
      if (Settings.instance().isCurrMapLocked() == false) {
         this.lock.setVisible(false);
      } else {
         this.lock.setVisible(true);
      }
   }
   
   public boolean isLocked() {
      return this.lock.isVisible();
   }
   
   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
 
      
      batch.end();  
      this.shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
      this.shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
      Gdx.gl.glEnable(GL20.GL_BLEND);
      Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
      
      this.shapeRenderer.setColor(0,0,0, 0.75f);
      this.shapeRenderer.begin(ShapeType.Filled); 
      this.shapeRenderer.rect(getX(),getY(),getWidth(),getHeight());
      this.shapeRenderer.end();
      Gdx.gl20.glLineWidth(3);
      this.shapeRenderer.setColor(0f, 0f, 0f, parentAlpha);
      this.shapeRenderer.begin(ShapeType.Line); 
      this.shapeRenderer.rect(getX(),getY(),getWidth(),getHeight());
      this.shapeRenderer.end();
      
      Gdx.gl.glDisable(GL20.GL_BLEND);
      
      batch.begin();
      
      super.draw(batch, parentAlpha);
   }
}
