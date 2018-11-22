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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.studio332.omega.Climber;
import com.studio332.omega.util.Assets;

public class AdPopup extends Group  {
   private ShapeRenderer shapeRenderer;
   private final String amazonUrl = 
         "http://www.amazon.com/Omega-Project-Steve-Alten/dp/0765336324/ref=sr_1_1?s=books&ie=UTF8&qid=1367858231&sr=1-1&keywords=the+omega+project";
   
   public AdPopup( final Climber game) {
      super();
      Image ad = new Image(Assets.instance().getDrawable("cover"));
      ad.setPosition(2,52);
      addActor(ad);

      this.shapeRenderer = new ShapeRenderer();
      
      setWidth(950);
      setHeight(604);//554);
      setPosition( (Climber.TGT_WIDTH-getWidth())/2f, (Climber.TGT_HEIGHT-getHeight())/2f);
      
      float centerOffset=567;
      Color txtC = new Color(0.8f, 0.8f, 0.8f, 1.0f);
      LabelStyle st = new LabelStyle(Assets.instance().getHelpFont(), txtC);
      Label l = new Label("The adventure\ncontinues in...", st);
      l.setFontScale(0.8f);
      l.setWidth(getWidth()-centerOffset);
      l.setAlignment(Align.center);
      l.setPosition(367, 510);
      addActor(l);
      
      LabelStyle st2 = new LabelStyle(Assets.instance().getHelpFont(), new Color(0.2f, 0.8f, 1f, 0.7f));
      Label l2 = new Label("The OMEGA\nPROJECT", st2);
      l2.setFontScale(1.25f);
      l2.setWidth(getWidth()-centerOffset);
      l2.setAlignment(Align.center);
      l2.setPosition(367, 425);
      addActor(l2);
      
      String msg = "The new MUST-READ\nthriller by\n"+
            "NY Times\nbest-selling author\nSteve Alten.\n\n"+
            "Available everywhere\nAugust 2013.";
      Label l3 = new Label(msg, st);
      l3.setFontScale(0.8f);
      l3.setWidth(getWidth()-centerOffset);
      l3.setAlignment(Align.center);
      l3.setPosition(367, 137);
      addActor(l3);
      
      Label l4 = new Label("Order your copy from", st);
      //Label l4 = new Label("Tap below to pre-order", st);
      l4.setFontScale(0.8f);
      l4.setWidth(getWidth()-centerOffset);
      l4.setAlignment(Align.center);
      l4.setPosition(367, 120);
      addActor(l4);
      
      Label l5 = new Label("amazon.com", st2);        
      l5.setWidth(getWidth()-centerOffset);
      l5.setAlignment(Align.center);
      l5.setPosition(367, 80);
      addActor(l5);
      
      l5.addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            game.linkToWebsite(amazonUrl);
            return false;
         }
      });
      
      this.addListener(new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            game.closeAdPopup();
            return true;
         }
      });
      
      addExcerpts(game);
      
      // studio332 ad
      Label l6 = new Label("Enjoy this? Check out BlastWords, an addictive new word game from Studio332!", st);
      l6.setFontScale(0.7f);
      l6.setWidth(getWidth()-centerOffset);
      l6.setPosition(10, 7);
      addActor(l6);
      Image gp = new Image(Assets.instance().getDrawable("google"));
      gp.setPosition(818,2);
      addActor(gp);
      gp.addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            game.linkToWebsite("market://details?id=com.studio332.blastwords");
            return false;
         }
      });
   }
   
   private void addExcerpts(final Climber game) {
      float exerptWidth = 200;
      Color txtC = new Color(0.8f, 0.8f, 0.8f, 1.0f);
      LabelStyle st = new LabelStyle(Assets.instance().getHelpFont(), txtC);
      Label l1 = new Label("Read FREE\nexcerpts from\nthe book", st);
      l1.setFontScale(0.8f);
      l1.setWidth( exerptWidth );
      l1.setAlignment(Align.center);
      l1.setPosition(750, 470);
      addActor(l1);
      
      LabelStyle st2 = new LabelStyle(Assets.instance().getHelpFont(), new Color(0.2f, 0.8f, 1f, 0.7f));
      Label e1 = new Label("Excerpt #1", st2);
      e1.setWidth( exerptWidth );
      e1.setAlignment(Align.center);
      e1.setPosition(750, 330);
      addActor(e1);
      e1.addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            game.openChapter("book/excerpt1.pdf");
            return false;
         }
      });
      
      Label e2 = new Label("Excerpt #2", st2);
      e2.setWidth( exerptWidth );
      e2.setAlignment(Align.center);
      e2.setPosition(750, 270);
      addActor(e2);
      e2.addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            game.openChapter("book/excerpt2.pdf");
            return false;
         }
      });
      
      Label e3 = new Label("Excerpt #3", st2);
      e3.setWidth( exerptWidth );
      e3.setAlignment(Align.center);
      e3.setPosition(750, 210);
      addActor(e3);
      e3.addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            game.openChapter("book/excerpt3.pdf");
            return false;
         }
      });
      
      Label e4 = new Label("Excerpt #4", st2);
      e4.setWidth( exerptWidth );
      e4.setAlignment(Align.center);
      e4.setPosition(750, 150);
      addActor(e4);
      e4.addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            game.openChapter("book/excerpt4.pdf");
            return false;
         }
      });
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
         
         this.shapeRenderer.begin(ShapeType.Line);
         Gdx.gl20.glLineWidth(1);
         this.shapeRenderer.setColor(1,1,1,  getColor().a);
         this.shapeRenderer.rect(0, 0, getWidth(), getHeight());  // outer border
         this.shapeRenderer.rect(0, 50, 367, getHeight()-50);
         this.shapeRenderer.line(750, 50, 750, getHeight());
         this.shapeRenderer.line(750, 440, 950, 440);
         this.shapeRenderer.line(0, 50, getWidth(), 50);

         this.shapeRenderer.end();
         
         Gdx.gl.glDisable(GL20.GL_BLEND);
         
         batch.begin();
      }
      super.draw(batch, parentAlpha);
   }

}
