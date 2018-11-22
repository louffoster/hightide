package com.studio332.omega.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.studio332.omega.Climber;
import com.studio332.omega.model.Settings;
import com.studio332.omega.objects.MapSummary;
import com.studio332.omega.util.Assets;
import com.studio332.omega.util.SoundManager;

public class MenuScreen extends AbstractScreen {
   private boolean fadingOut = false;
   private MapSummary mapSummary;
   private CreditsPopup credits;


   public MenuScreen(final Climber game) {
      super(game);
      
      Image l = new Image(Assets.instance().getAtlasRegion("menu-l"));
      this.stage.addActor(l);
      Image r = new Image(Assets.instance().getAtlasRegion("menu-r"));
      r.setPosition(640, 0);
      this.stage.addActor(r);
      
      // show map info & start game!!
      this.mapSummary = new MapSummary();
      this.mapSummary.setPosition(200, (this.stage.getHeight()-this.mapSummary.getHeight())/2+40);
      this.mapSummary.setMapInfo( Settings.instance().getCurrentMap() );
      this.stage.addActor(this.mapSummary);
      this.mapSummary.addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if ( creditsUp() == false ) {
               if ( mapSummary.isLocked() == false ) {
                  launchGame();
               } else {
                  SoundManager.instance().playSound(SoundManager.NO_PLAY);
               }
            }
            return super.touchDown(event, x, y, pointer, button);
         }
      });
      
      // select prior map
      Image prev = new Image(Assets.instance().getDrawable("left"));
      prev.setPosition(130, (this.stage.getHeight()-this.mapSummary.getHeight())/2+39);
      this.stage.addActor(prev);
      prev.addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if ( creditsUp() == false ) {
               SoundManager.instance().playSound(SoundManager.MENU_CLICK);
               Settings.instance().priorMap();
               mapSummary.setMapInfo( Settings.instance().getCurrentMap() );
            }
            return false;
         }
      });
      
      // select next map
      Image next = new Image(Assets.instance().getDrawable("right"));
      next.setPosition( 
            this.mapSummary.getX()+this.mapSummary.getWidth()+5,
            (this.stage.getHeight()-this.mapSummary.getHeight())/2+39);
      this.stage.addActor(next);
      next.addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if ( creditsUp() == false ) {
               SoundManager.instance().playSound(SoundManager.MENU_CLICK);
               Settings.instance().nextMap();
               mapSummary.setMapInfo( Settings.instance().getCurrentMap() );
            }
            return false;
         }
      });
      
      Button musicButton = new Button(
            Assets.instance().getDrawable("music-off"),
            Assets.instance().getDrawable("music"),
            Assets.instance().getDrawable("music") );
      musicButton.setChecked(true);
      musicButton.setPosition(935, 110);
      if ( Settings.instance().isMusicOn() == false) {
         musicButton.setChecked(false);
      }
      this.stage.addActor(musicButton);
      musicButton.addListener( new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
            if ( creditsUp() == false ) {
               SoundManager.instance().playSound(SoundManager.MENU_CLICK);
               Settings.instance().toggleMusic();
               if (Settings.instance().isMusicOn()) {
                  SoundManager.instance().playMenuMusic();
               } else {
                  SoundManager.instance().stopMusic();
               }
            }
         }
      });
      
      Button soundButton = new Button(
            Assets.instance().getDrawable("sound-off"),
            Assets.instance().getDrawable("sound"),
            Assets.instance().getDrawable("sound") );
      soundButton.setChecked(true);
      if ( Settings.instance().isSoundOn() == false) {
         soundButton.setChecked(false);
      }
      soundButton.setPosition(1050, 120);
      stage.addActor(soundButton);
      soundButton.addListener( new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
            if ( creditsUp() == false ) {
               SoundManager.instance().playSound(SoundManager.MENU_CLICK);
               Settings.instance().toggleSound();
            }
         }
      });
      
      // Info / credits button
      Button infoBtn = new Button(
            Assets.instance().getDrawable("info"),
            Assets.instance().getDrawable("info"), null);
      infoBtn.setPosition(1170, 116);
      this.stage.addActor(infoBtn);
      infoBtn.addListener( new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
            if ( creditsUp() == false ) {
               SoundManager.instance().playSound(SoundManager.MENU_CLICK);
               showCredits();
            }
         }
      });
      
      SoundManager.instance().playMenuMusic();
      
      this.credits = new CreditsPopup();
      this.credits.getColor().a = 0;
      this.stage.addActor( this.credits );
      
      this.stage.addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if ( x<170 && y<170 ) {
               if ( creditsUp() == false ) {
                  SoundManager.instance().playSound(SoundManager.MENU_CLICK);
                  game.showAdPopup(true);
               }
            }
            return true;
         }
      });
   }
   
   private void showCredits() {
      this.credits.getColor().a = 1;
      this.credits.setTouchable(Touchable.enabled);
   }
   
   private boolean creditsUp() {
      return (this.credits.getColor().a == 1f);
   }
   
   private void launchGame() {
      fadingOut = true;
      SoundManager.instance().stopMusic();
      SoundManager.instance().playSound(SoundManager.MENU_CLICK);
      stage.addAction( sequence(fadeOut(0.5f), new Action() {

         @Override
         public boolean act(float delta) {
            game.showGameScreen();
            return true;
         }
         
      }));
   }
   
   @Override
   public void render(float delta) {
      if ( this.fadingOut) {
         Gdx.gl.glClearColor(0,0,0, 1f);
      } else {
         Gdx.gl.glClearColor(0.09f, 0f, 0.2f, 1f);
      }
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
      
      // update and draw the stage actors
      stage.act( delta );
      stage.draw();
   }
   
   @Override
   protected boolean backClicked() {
      if (creditsUp() ) {
         this.credits.hide();
         return true;
      } else if ( game.isAsPopupOpen()  ) {
         game.closeAdPopup();
         return true;
      }
      return false;
   }

}
