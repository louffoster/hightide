package com.studio332.omega.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.Color;
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

public class IntroScreen extends AbstractScreen {

   public IntroScreen(final Climber game) {
      super(game);
      
      
      Image[] flip  = new Image[3];
      float x[] = {0,427,(427+426)};
      String names[] = {"3FrameIntroA", "3FrameIntroB", "3FrameIntroC"};
      for ( int i=0; i<3; i++) {
         flip[i] = new Image(Assets.instance().getDrawable(names[i]));
         flip[i].setPosition(x[i], 0);
         flip[i].getColor().a = 0;
         stage.addActor(flip[i]);
      }

      SoundManager.instance().playIntroMusic();
      
      Color c = new Color(.61f, .61f, .7f, 1f);
      LabelStyle st = new LabelStyle(Assets.instance().getFont(), c);
      
      final Label ls = creatLabel("12 million years in the future...", st);
      ls.setColor(0,0,0,0);
      ls.setPosition(
            (this.stage.getWidth()-ls.getWidth())/2+4,
            (this.stage.getHeight()-ls.getHeight())/2+156);
      
      final Label l = creatLabel("12 million years in the future...", st);
      l.setPosition(
            (this.stage.getWidth()-l.getWidth())/2,
            (this.stage.getHeight()-l.getHeight())/2+160);
      
      final Label l2a = creatLabel("Scientist, Robert 'Ike' Eisenbraun awakens...", st);
      l2a.setColor(0,0,0,0);
      l2a.setPosition(
            (this.stage.getWidth()-l2a.getWidth())/2+4,
            (this.stage.getHeight()-l2a.getHeight())/2+76);
      final Label l2 = creatLabel("Scientist, Robert 'Ike' Eisenbraun awakens...", st);
      l2.setPosition(
            (this.stage.getWidth()-l2.getWidth())/2,
            (this.stage.getHeight()-l2.getHeight())/2+80);
      
   
      l.addAction( sequence(fadeIn(2f), delay(6f), fadeOut(2.0f)));
      ls.addAction( sequence(fadeIn(2f), delay(6f), fadeOut(2.0f)));
      l2a.addAction( sequence(delay(5.5f), fadeIn(2f), delay(5f), fadeOut(2.0f)));
      l2.addAction( sequence(delay(5.5f), fadeIn(2f), delay(5f), fadeOut(2.0f)));
      flip[0].addAction(  sequence(delay(5f), fadeIn(1.0f), delay(3f)));
      flip[1].addAction(  sequence(delay(8f), fadeIn(1.0f), delay(3f)));
      flip[2].addAction(  sequence(delay(11f), fadeIn(1.0f), delay(3f)));
      
      this.stage.getRoot().addAction( sequence(delay(15f),fadeOut(2f), new Action() {
         
       @Override
       public boolean act(float delta) {
          game.showSplashScreen();
          return false;
       }}));
      
      // add a tap handler to skip the sequence
      this.stage.addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            l.clearActions();
            SoundManager.instance().stopMusic();
            l.addAction(sequence(fadeOut(0.5f), new Action() {
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
   
   private Label creatLabel(String msg, LabelStyle st) {
      Label nl = new Label(msg, st);
      nl.setFontScale(1.5f);
      nl.setAlignment(Align.center);
      nl.setPosition(
            (this.stage.getWidth()-nl.getWidth())/2,
            (this.stage.getHeight()-nl.getHeight())/2+100);
      nl.getColor().a = 0;
      this.stage.addActor(nl);
      return nl;
   }

}
