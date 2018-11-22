package com.studio332.omega.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.studio332.omega.Climber;
import com.studio332.omega.util.Assets;
import com.studio332.omega.util.SoundManager;


public class TitleScreen extends AbstractScreen {

   public TitleScreen(final Climber game) {
      super(game);
      final Group g = new Group();
      g.getColor().a = 0;
      
      final Image op = new Image(Assets.instance().getAtlasRegion("title"));
      op.setPosition(
            (this.stage.getWidth()-op.getWidth())/2,
            (this.stage.getHeight()-op.getHeight())/2+this.stage.getHeight()*0.2f);
      g.addActor(op);
      
      Color c = new Color(.61f, .61f, .7f, 1f);
      LabelStyle st = new LabelStyle(Assets.instance().getFont(), c);
      final Label l2 = new Label("Based on 'The Omega Project'\nby New York Times Bestselling Author\nSteve Alten", st);
      l2.setPosition(0,0);
      l2.setAlignment(Align.right);
      l2.setFontScale(0.9f);
      l2.setPosition(
            (this.stage.getWidth()-l2.getWidth())-this.stage.getWidth()*0.025f,
            this.stage.getHeight()*0.025f);
      l2.getColor().a = 0;
      g.addActor(l2);
      
      this.stage.addActor(g);
      
      g.addAction( sequence(fadeIn( 1f), delay(12f), fadeOut(1.5f), new Action() {
         @Override
         public boolean act(float delta) {
            game.showMenuScreen();
            return true;
         }
      }) );
      
      op.addAction( fadeIn(0.5f) );
      l2.addAction( sequence( delay(5f), fadeIn(0.5f)) );
      
      this.stage.addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            g.clearActions();
            l2.clearActions();
            SoundManager.instance().stopMusic();
            g.addAction(sequence(fadeOut(0.5f), new Action() {
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

}
