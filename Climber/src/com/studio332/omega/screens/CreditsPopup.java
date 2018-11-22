package com.studio332.omega.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.studio332.omega.Climber;
import com.studio332.omega.util.Assets;

public class CreditsPopup extends Group {

   private ShapeRenderer shapeRenderer;
   
   public CreditsPopup() {
      super();
      setWidth(620);
      setHeight(630);
      setPosition( (Climber.TGT_WIDTH-getWidth())/2, (Climber.TGT_HEIGHT-getHeight())/2);
      this.shapeRenderer = new ShapeRenderer();
      setTouchable(Touchable.disabled);
      
      Image s332 = new Image( Assets.instance().getDrawable("s332smetal"));
      s332.setPosition(0, 5);
      addActor(s332);
      
      Image cdtLogo = new Image( Assets.instance().getDrawable("credits"));
      cdtLogo.setPosition(400, 520);
      addActor(cdtLogo);
      
      Color c = new Color(.61f, .61f, .7f, 1f);
      LabelStyle st = new LabelStyle(Assets.instance().getFont(), c);
      Label pl = new Label("Programming :", st);
      pl.setPosition(200, 460);
      pl.setAlignment(Align.right);
      pl.setWidth(100);
      addActor(pl);
      
      Label pll = new Label("Lou Foster", st);
      pll.setPosition(320, 460);
      pll.setAlignment(Align.left);
      pll.setWidth(100);
      addActor(pll);
      
      Label ad = new Label("Art Direction :", st);
      ad.setPosition(200, 410);
      ad.setAlignment(Align.right);
      ad.setWidth(100);
      addActor(ad);
      
      Label adb = new Label("Bryan Glickman", st);
      adb.setPosition(320, 410);
      adb.setAlignment(Align.left);
      adb.setWidth(100);
      addActor(adb);
      
      Label sm = new Label("Sound & Music :", st);
      sm.setPosition(200, 360);
      sm.setAlignment(Align.right);
      sm.setWidth(100);
      addActor(sm);
      
      Label sma = new Label("Allen Foster", st);
      sma.setPosition(320, 360);
      sma.setAlignment(Align.left);
      sma.setWidth(100);
      addActor(sma);
      
      Label qa = new Label("QA :", st);
      qa.setPosition(165, 275);
      qa.setAlignment(Align.right);
      qa.setWidth(100);
      addActor(qa);
      
      Label qal = new Label("Nancy Hopkins\nJackson Foster\nJeff Foster,\nEd & Tavia Brown", st);
      qal.setPosition(295, 220);
      qal.setAlignment(Align.left);
      qal.setWidth(100);
      addActor(qal);
      
      Label l2 = new Label("Based on 'The Omega Project'\nby New York Times Bestselling Author\nSteve Alten", st);
      l2.setFontScale(0.8f);
      l2.setPosition(35, 120);
      l2.setAlignment(Align.left);
      l2.setWidth(getWidth());
      addActor(l2);
      
      addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            hide();
            return false;
         }
      });
   }
   
   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
      if ( getColor().a > 0) {
         batch.end();
         
         Gdx.gl.glEnable(GL20.GL_BLEND);
         Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
         Gdx.gl20.glLineWidth(1);
         
         this.shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
         this.shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
         this.shapeRenderer.translate(getX(), getY(), 0);
         
         this.shapeRenderer.setColor(0f, 0f, 0f,  0.85f);
         this.shapeRenderer.begin(ShapeType.Filled);
         this.shapeRenderer.rect(0,0,getWidth(), getHeight());
         this.shapeRenderer.end();
         this.shapeRenderer.setColor(.6f,.6f,.6f, 1f);
         this.shapeRenderer.begin(ShapeType.Line);
         this.shapeRenderer.rect(2,2,getWidth()-4, getHeight()-4);
         this.shapeRenderer.end();
         
         
         Gdx.gl.glDisable(GL20.GL_BLEND);
         batch.begin();
      }
      
      super.draw(batch, parentAlpha);
   }
   
   public void hide() {
      getColor().a = 0;
      setTouchable(Touchable.disabled);
   }
}
