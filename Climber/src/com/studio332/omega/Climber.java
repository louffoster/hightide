package com.studio332.omega;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.studio332.omega.screens.AbstractScreen;
import com.studio332.omega.screens.AdPopup;
import com.studio332.omega.screens.GameScreen;
import com.studio332.omega.screens.IntroScreen;
import com.studio332.omega.screens.MenuScreen;
import com.studio332.omega.screens.SplashScreen;
import com.studio332.omega.screens.TitleScreen;
import com.studio332.omega.util.Assets;
import com.studio332.omega.util.ClimberActions;
import com.studio332.omega.util.Overlay;
import com.studio332.omega.util.SoundManager;

public class Climber extends Game {
   
   // commmon gameplay constants
   public static final float TGT_WIDTH = 1280;
   public static final float TGT_HEIGHT = 736;
   private AdPopup adPopup;
   private boolean adPopupOpen = false;
   private Overlay dimmer;

   //public static final String LOG_NAME = Climber.class.getSimpleName();
   
   private WebLinker webLinker = null;
   
   public Climber(WebLinker wl ) {
      super();
      this.webLinker = wl;
   }
   
   public void linkToWebsite( String url ) {
      if ( this.webLinker != null ) {
         this.webLinker.openBrowser(url);
      }
   }
   
   public void openChapter( String path ) {
      if ( this.webLinker != null ) {
         FileHandle chapterHandle = Gdx.files.internal(path);
         if ( chapterHandle.exists() ) {
            byte[] foo = chapterHandle.readBytes();
            this.webLinker.openFile(foo, chapterHandle.file().getName());
         }
      }
   }
   
   public void dimScreen() {
      AbstractScreen as = (AbstractScreen)getScreen();
      as.getStage().addActor(this.dimmer);
      this.dimmer.addAction( ClimberActions.fadeTo(0.7f, 0.5f) );
   }
   public void unDimScreen() {
      this.dimmer.addAction( sequence(fadeOut(0.25f), new Action(){

         @Override
         public boolean act(float delta) {
            dimmer.remove();
            return true;
         }
         
      }) );
   }
   
   public boolean isAsPopupOpen() {
      return this.adPopupOpen;
   }
   public void showAdPopup(boolean dim) {
      if (dim) {
         dimScreen();
      }
      AbstractScreen as = (AbstractScreen)getScreen();
      as.getStage().addActor(this.adPopup);
      this.adPopup.setTouchable(Touchable.enabled);
      this.adPopup.addAction( fadeIn(1f) );
      adPopupOpen = true;
   }
   public void closeAdPopup() {
      unDimScreen();
      this.adPopup.setTouchable(Touchable.disabled);
      this.adPopup.addAction( sequence(fadeOut(0.5f), new Action() {

         @Override
         public boolean act(float delta) {
            adPopup.remove();
            adPopupOpen = false;
            return true;
         }
         
      }));
   }
   
   @Override
   public void create() {
      //Gdx.app.log(LOG_NAME, "Creating game");

      Texture.setEnforcePotImages(false);
      Assets.instance().load();
      
      this.adPopup = new AdPopup(this);
      this.adPopup.getColor().a = 0;
      this.adPopup.setTouchable(Touchable.disabled);
      
      this.dimmer = new Overlay();
      this.dimmer.setColor(0f,0f,0f,0f);
      
      showIntroScreen();
   }
   
   public void showIntroScreen () {
      setScreen( new IntroScreen(this) );
   }
   
   public void showSplashScreen () {
      setScreen( new SplashScreen(this) );
   }
   
   public void showTitle () {
      setScreen( new TitleScreen(this) );
   }
   
   public void showMenuScreen () {
      setScreen( new MenuScreen(this) );
   }
   
   public void fadeToMenuScreen () {
      GameScreen gameS = (GameScreen)getScreen();
      gameS.fadeToMenu();
   }
   
   public void showGameScreen () {
      setScreen( new GameScreen(this) );
   }

   @Override
   public void resize(int width, int height) {
   }

   @Override
   public void render() {
      super.render();   // passes the render along to the current screen
      // output the current FPS
      //fpsLogger.log();
   }

   @Override
   public void pause() {
      SoundManager.instance().pause();
   }

   @Override
   public void resume() {
      //Gdx.app.log( LOG_NAME, "Resume Game" );
      //System.err.println("RESUME");
      SoundManager.instance().resume();
   }

   @Override
   public void dispose() {
      //Gdx.app.log( LOG_NAME, "Dispose Game" );
      getScreen().dispose();
     // System.err.println("DISPOSE");
   }

}
