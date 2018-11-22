package com.studio332.omega.objects;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.studio332.omega.Climber;
import com.studio332.omega.model.Settings;
import com.studio332.omega.util.Assets;
import com.studio332.omega.util.GameControlListener;
import com.studio332.omega.util.SoundManager;

public class WinPanel extends Group {
   private ShapeRenderer shapeRenderer;
   private GameControlListener listener;
   
   public WinPanel(final Climber game, float elapsedTimeSec, float bestTimeSec) {
      super();
      this.shapeRenderer = new ShapeRenderer();
     
      setWidth(700);
      setHeight(650);
      setPosition((Climber.TGT_WIDTH-getWidth())/2, 50);
      
      Image i = new Image(Assets.instance().getDrawable("cliff-top"));
      addActor(i);
      i.setPosition(6,8);
      
      Image m = new Image(Assets.instance().getDrawable("moon"));
      addActor(m);
      m.setPosition(110,210);
      
      String win = Settings.instance().getCurrentMap().getWinMessage();
      Color txtC = new Color(.61f, .61f, .7f, 1f);
      LabelStyle st = new LabelStyle(Assets.instance().getFont(), txtC);
      String lastTxt = "";
      if ( Settings.instance().getCurrentMap().getLevel()==10) {
         lastTxt = "final ";
      }
      Label yay = new Label("You have reached\nthe "+lastTxt+"summit\nand "+win+"!", st);
      yay.setFontScale(1.2f);
      yay.setAlignment(Align.center);
      yay.setWidth(700);
      addActor(yay);
      yay.setPosition(0,265);
      
      String ts = formatTime(elapsedTimeSec);
      Label timeLabel = new Label("Time: "+ts, st);
      timeLabel.setAlignment(Align.center);
      timeLabel.setWidth(700);
      addActor(timeLabel);
      timeLabel.setPosition(0,200);
     
      final Label adText = new Label("Continue the adventure...", st);
      adText.setPosition(125,27);
      adText.setAlignment(Align.center);
      adText.setFontScale(0.8f);
      adText.setVisible(false);
      addActor(adText);
      adText.addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            game.showAdPopup(false);
            return true;
         }
      });
      
      // button controls
      final Button restart = new Button(
            Assets.instance().getDrawable("restart-alt"),
            Assets.instance().getDrawable("restart-alt-lit"), null );
      restart.setPosition(550,7);
      restart.addListener( new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
            SoundManager.instance().playSound(SoundManager.CLICK);
            if ( listener != null) {
               remove();
               listener.restartClicked();
            }
         }
      });
      
      final Button quit = new Button(
            Assets.instance().getDrawable("quit-alt"),
            Assets.instance().getDrawable("quit-alt-lit"), null );
      quit.setPosition(622,7);
      quit.addListener( new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
            SoundManager.instance().playSound(SoundManager.CLICK);
            if ( listener != null) {
               remove();
               listener.quitClicked();
            }
         }
      });
      
      final Button nextBtn = new Button(
            Assets.instance().getDrawable("next"),
            Assets.instance().getDrawable("next"), null );
      nextBtn.setPosition(570,75);
      nextBtn.addListener( new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
            SoundManager.instance().playSound(SoundManager.CLICK);
            if ( listener != null) {
               remove();
               Settings.instance().nextMap();
               listener.restartClicked();
            }
         }
      });
      
      boolean newHiScore = false;
      final Group timesG = new Group();
      if ( bestTimeSec == 0 || (elapsedTimeSec < bestTimeSec) ) {
         Label congrat = new Label("Congratulations!", st);
         congrat.setAlignment(Align.center);
         congrat.setWidth(700);
         timesG.addActor(congrat);
         congrat.setPosition(0,130);
         Label best = new Label("New best time", st);
         best.setAlignment(Align.center);
         best.setWidth(700);
         timesG.addActor(best);
         best.setPosition(0,95);
         newHiScore = true;
      } else {
         Label congrat = new Label("Current best time:", st);
         congrat.setAlignment(Align.center);
         congrat.setWidth(700);
         timesG.addActor(congrat);
         congrat.setPosition(0,130);
         Label best = new Label(formatTime(bestTimeSec), st);
         best.setAlignment(Align.center);
         best.setWidth(700);
         timesG.addActor(best);
         best.setPosition(0,95);
      }
      timesG.getColor().a = 0;
      addActor(timesG);
      
      // add buttons last so other text doesn't end up on
      // to of them and prevent clicks
      restart.getColor().a = 0;
      addActor(restart);
      quit.getColor().a = 0;
      addActor(quit);
      nextBtn.getColor().a = 0;
      addActor(nextBtn);
      
      if ( newHiScore ) {
         timesG.addAction( sequence(delay(3f), new Action() {
         
            @Override
            public boolean act(float delta) {
               SoundManager.instance().playHiScoreMusic();
               quit.getColor().a = 1;
               restart.getColor().a = 1f;
               if ( Settings.instance().getCurrentMap().getLevel() < 10 ) {
                  nextBtn.getColor().a = 1f;
               }
               adText.setVisible(true);
               return true;
            }
            
         }, fadeIn(2f)));
      } else {
         timesG.addAction( sequence(delay(3f), fadeIn(1f), new Action() {

            @Override
            public boolean act(float delta) {
               
               quit.getColor().a = 1;
               restart.getColor().a = 1f;
               adText.setVisible(true);
               if ( Settings.instance().getCurrentMap().getLevel() < 10 ) {
                  nextBtn.getColor().a = 1f;
               }
               return true;
            }
         }));
      }
   }
   
   public void addListener( GameControlListener l ) {
      this.listener = l;
   }
   
   private String formatTime( float elapsedSec ) {
      float elapsed = elapsedSec;
      int mins = (int)(elapsed / 60);
      float sec = elapsed - (mins*60);
      return String.format("%d:%04.1f", mins, sec);
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
         Gdx.gl20.glLineWidth(2);
         this.shapeRenderer.setColor(1,1,1,  getColor().a);
         this.shapeRenderer.rect(4, 4, getWidth()-8, getHeight()-8);
         this.shapeRenderer.end();
         
         Gdx.gl.glDisable(GL20.GL_BLEND);
         
         batch.begin();
      }
      super.draw(batch, parentAlpha);
   }

}
