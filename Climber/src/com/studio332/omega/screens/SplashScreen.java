package com.studio332.omega.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.studio332.omega.Climber;
import com.studio332.omega.util.Assets;
import com.studio332.omega.util.SoundManager;

public class SplashScreen extends AbstractScreen {

   public SplashScreen(final Climber game) {
      super(game);
      final Image i = new Image(Assets.instance().getDrawable("332silver"));
      stage.addActor(i);
      i.setPosition(
            (this.stage.getWidth()-i.getWidth())/2, 
            (this.stage.getHeight()-i.getHeight())/2+i.getHeight());
      i.getColor().a = 0;
      i.addAction( fadeIn(0.6f));
      i.addAction( sequence(delay(0.3f), new Action() {
         
         @Override
         public boolean act(float delta) {
            SoundManager.instance().playSplashMusic();
            return true;
         }
      }));
      
      Color c = new Color(0.24f,0.24f,0.24f, 1.0f);
      LabelStyle st = new LabelStyle(Assets.instance().getFont(), c);
      final Label l = new Label("PRESENTS", st);
      l.getColor().a = 0;
      l.setWidth(this.stage.getWidth());
      l.setAlignment(Align.center);
      stage.addActor(l);
      l.setPosition(0,(this.stage.getHeight()-i.getHeight())/2+70);
      l.addAction(sequence(delay(1.5f), fadeIn(1.5f)));
      
      final Image black = new Image( Assets.instance().getPixel());
      black.setWidth(this.stage.getWidth());
      black.setHeight(this.stage.getHeight());
      black.setColor(0,0,0,0);
      stage.addActor(black);
      black.addAction(sequence(delay(7f),fadeIn(0.5f), new Action() {

         @Override
         public boolean act(float delta) {
            game.showTitle();
            return true;
         }
         
      }));
      
      this.stage.addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            i.clearActions();
            l.clearActions();
            black.clearActions();
            SoundManager.instance().stopMusic();
            black.addAction(sequence(fadeIn(0.5f), new Action() {
               @Override
               public boolean act(float delta) {
                  game.showMenuScreen();
                  return false;
               }
            }));
            return true;
         }
      });
   }
   
   @Override
   public void render(float delta) {
      // background is white instead of black
      Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
      stage.act( delta );
      stage.draw();
   }

}
