package com.studio332.omega.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.studio332.omega.model.MapInfo;
import com.studio332.omega.model.Settings;
import com.studio332.omega.util.Assets;
import com.studio332.omega.util.GameControlListener;
import com.studio332.omega.util.SoundManager;

public class Popup extends Group {
   private GameControlListener listener;
   private Label message;
   private Button resume;
   private Button restart;
   private Button quit;
   private ShapeRenderer shapeRenderer;
   private Color popupColor = new Color(0.15f, .73f, 0.94f, 1.0f);

   public Popup() {
      super();
      this.shapeRenderer = new ShapeRenderer();
      setWidth(220);
      setHeight(150);
      
      LabelStyle st = new LabelStyle(Assets.instance().getFont(), this.popupColor);
      this.message = new Label("PAUSED", st);
      this.message.setWidth(getWidth());
      this.message.setAlignment(0, Align.center);
      this.message.setPosition(
            (getWidth()-this.message.getWidth())/2, 
            (getHeight()-this.message.getHeight())/2+this.message.getHeight() );
      addActor(this.message);
      
      this.resume = new Button(
            Assets.instance().getDrawable("resume-dim"),
            Assets.instance().getDrawable("resume-lit"), null );
      this.resume.setPosition(2,2);
      this.resume.addListener( new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
            resumeClicked();
         }
      });
      addActor(this.resume);
      
      this.restart = new Button(
            Assets.instance().getDrawable("restart-dim"),
            Assets.instance().getDrawable("restart-lit"), null );
      this.restart.setPosition(74,2);
      this.restart.addListener( new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
            restartClicked();
         }
      });
      addActor(this.restart);
      
      this.quit = new Button(
            Assets.instance().getDrawable("quit-dim"),
            Assets.instance().getDrawable("quit-lit"), null );
      this.quit.setPosition(146,2);
      this.quit.addListener( new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
            quitClicked();
         }
      });
      addActor(this.quit);
      getColor().a = 0;
   }
   
   public void showPausedMessage() {
      this.message.setText("PAUSED");
      disableButtons(false, false, false);
      setWidth(218);
      this.message.setWidth(getWidth());
      this.message.setPosition(
            (getWidth()-this.message.getWidth())/2, 
            (getHeight()-this.message.getHeight())/2+this.message.getHeight()+10 );
   }
   
   public void showGameOver() {
      this.message.setText("Game\nOver");
      SoundManager.instance().stopMusic();
      disableButtons(true, false, false);
      setWidth(218);
      this.message.setWidth(getWidth());
      this.message.setPosition(
            (getWidth()-this.message.getWidth())/2, 
            (getHeight()-this.message.getHeight())/2+this.message.getHeight()+10 );
   }
   
   private void disableButtons(boolean resumeDisable, boolean restartDisable, boolean endDisable) {
      this.resume.setVisible(true);
      this.resume.setTouchable(Touchable.enabled);
      if (resumeDisable) {
         this.resume.setVisible(false);
         this.resume.setTouchable(Touchable.disabled);
      }
      
      this.restart.setVisible(true);
      this.restart.setTouchable(Touchable.enabled);
      if (restartDisable) {
         this.restart.setVisible(false);
         this.restart.setTouchable(Touchable.disabled);
      }
      
      this.quit.setVisible(true);
      this.quit.setTouchable(Touchable.enabled);
      if (endDisable) {
         this.quit.setVisible(false);
         this.quit.setTouchable(Touchable.disabled);
      }
   }
   
   public void showPlan() { 
      MapInfo mi = Settings.instance().getCurrentMap();
      String l = String.format("Level %02d\n%s\nPlan your\nroute", mi.getLevel(), mi.getName());
      this.message.setText(l);
      disableButtons(true, true, true);
      setWidth(250);
      this.message.setWidth(getWidth());
      this.message.setPosition(
            (getWidth()-this.message.getWidth())/2, 
            (getHeight()-this.message.getHeight())/2+this.message.getHeight()-30f );
   }
   
   public void showClimb() { 
      this.message.setText("Climb now!");
      disableButtons(true, true, true);
      setWidth(250);
      this.message.setPosition(
            (getWidth()-this.message.getWidth())/2, 
            (getHeight()-this.message.getHeight())/2+this.message.getHeight()-24 );
   }
   
   public void setListener( GameControlListener l) {
      this.listener = l;
   }
   
   private void resumeClicked() {
      SoundManager.instance().playSound(SoundManager.CLICK);
      if ( this.listener != null) {
         this.listener.resumeClicked();
      }
   }
   
   private void restartClicked() {
      SoundManager.instance().playSound(SoundManager.CLICK);
      if ( this.listener != null) {
         this.listener.restartClicked();
      }
   }
   
   private void quitClicked() {
      SoundManager.instance().playSound(SoundManager.CLICK);
      if ( this.listener != null) {
         this.listener.quitClicked();
      }
   }
   
   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
      if ( getColor().a > 0  ) {
         batch.end();
         
         this.shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
         this.shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
         this.shapeRenderer.translate(getX(), getY(), 0);
         Gdx.gl.glEnable(GL20.GL_BLEND);
         Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
         
         this.shapeRenderer.begin(ShapeType.Filled);
         this.shapeRenderer.setColor(0f, 0f, 0f,  getColor().a);
         this.shapeRenderer.rect(0,0, getWidth(), getHeight());
         this.shapeRenderer.end();
         
         Gdx.gl20.glLineWidth(2);
         this.shapeRenderer.begin(ShapeType.Line);
         this.shapeRenderer.setColor(this.popupColor.r, this.popupColor.g, this.popupColor.b, getColor().a);
         this.shapeRenderer.rect(0,0, getWidth(), getHeight());
         this.shapeRenderer.end();
         
         Gdx.gl.glEnable(GL20.GL_BLEND);
         Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
         
         this.shapeRenderer.begin(ShapeType.Filled);
         this.shapeRenderer.setColor(0f, 0f, 0f,  getColor().a);
         this.shapeRenderer.rect(0,0, getWidth(), getHeight());
         this.shapeRenderer.end();
         
         Gdx.gl.glDisable(GL20.GL_BLEND);
         
         batch.begin();
      }
      super.draw(batch, parentAlpha);
   }
}
