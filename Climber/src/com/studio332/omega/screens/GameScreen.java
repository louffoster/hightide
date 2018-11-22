package com.studio332.omega.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.studio332.omega.Climber;
import com.studio332.omega.model.CardInfo;
import com.studio332.omega.model.CardInfo.Type;
import com.studio332.omega.model.ClimberGame;
import com.studio332.omega.model.ClimberGame.HazardListener;
import com.studio332.omega.model.ClimberGame.State;
import com.studio332.omega.model.Settings;
import com.studio332.omega.objects.CardPool;
import com.studio332.omega.objects.Cliff;
import com.studio332.omega.objects.Cliff.CliffListener;
import com.studio332.omega.objects.CliffMap;
import com.studio332.omega.objects.CliffProgress;
import com.studio332.omega.objects.HealthMeter;
import com.studio332.omega.objects.HelpOverlay;
import com.studio332.omega.objects.WarningIndicator;
import com.studio332.omega.objects.WinPanel;
import com.studio332.omega.util.Assets;
import com.studio332.omega.util.ClimberActions;
import com.studio332.omega.util.GameControlListener;
import com.studio332.omega.util.Overlay;
import com.studio332.omega.util.SoundManager;
import com.studio332.omega.util.StaticOverlay;

public class GameScreen  extends AbstractScreen implements CliffListener, GameControlListener, HazardListener {
   private ClimberGame model;
   private HealthMeter health;
   private Cliff cliff;
   private CliffMap map;
   private CliffProgress cliffProgress;
   private Label timerLabel;
   private WarningIndicator warnIndicator;

   private CardPool pool;
   private Popup popup;
   private Image hazardOutcome;
   
   // control buttons
   private Button scanButton;
   private Button pauseButton;
   private Button helpButton;
   private Button[] abeButtons = new Button[3];
   
   // help overlay!
   private HelpOverlay helpOverlay;
      
   // flashy dimmy overlay goodness
   private Overlay overlay;
   private Overlay dimmer;
   private StaticOverlay malfunctionOverlay;
   private float warnPulseDelay = 0;
   private ScheduledExecutorService executor;
   
   public GameScreen(Climber game) {
      super(game);
      
      this.model = new ClimberGame();
      this.model.setHazardListener( this );
      
      // create a scheduled executor cuz the one here is busted
      this.executor = Executors.newSingleThreadScheduledExecutor();  

      // create nifty gfx panels for left/right sides of screen ( stats areas )
      Image left = new Image( Assets.instance().getAtlasRegion("left_panel2"));
      Image leftedge = new Image( Assets.instance().getAtlasRegion("leftedge"));
      leftedge.setPosition(left.getWidth(), 0.0f);
      Image right = new Image( Assets.instance().getAtlasRegion("right_panel2"));
      right.setPosition(Climber.TGT_WIDTH-right.getWidth(), 0.0f);
      Image rightedge = new Image( Assets.instance().getAtlasRegion("rightedge"));
      rightedge.setPosition(right.getX()-rightedge.getWidth(), 0.0f);
      
      // timer label
      Color c = new Color(0.2f, .8f, 1f, 1.0f);
      LabelStyle st = new LabelStyle(Assets.instance().getFont(), c);
      this.timerLabel = new Label("0:00.0", st);
      this.timerLabel.setFontScale(1.25f);
      this.timerLabel.setPosition(60, 692);
      
      // create a health gauges
      this.health = new HealthMeter(this.model);
      this.health.setPosition(32, 395);
      
      // create the cliff and give it a reference to ike
      float cliffW = Climber.TGT_WIDTH-left.getWidth()-right.getWidth();
      this.cliff = new Cliff( cliffW, Climber.TGT_HEIGHT, left.getWidth(), right.getWidth(), this.model);
      this.cliff.setPosition(left.getWidth(), 0);
      this.cliff.setListener(this);
      this.cliff.setVisible(false);
      
      // detailed scale map of cliff face & ike
      this.map = new CliffMap(this.model, this.cliff);
      this.map.setPosition( 7, 64);
      
      // Normal progress-bar view of cliff climb progress
      this.cliffProgress = new CliffProgress(this.model, this.cliff);
      this.cliffProgress.setPosition(left.getWidth()-this.cliffProgress.getWidth()-3, 333);
      
      // card pool & movement plan
      this.pool = new CardPool(this.model);
      this.pool.setWidth(right.getWidth());
      this.pool.setHeight(Climber.TGT_HEIGHT);
      this.pool.setPosition(Climber.TGT_WIDTH-right.getWidth()+5, 10);
      
      this.warnIndicator = new WarningIndicator();
      this.warnIndicator.setPosition(15, 400);
      
      // populate the stage!
      this.stage.addActor(this.cliff);
      this.stage.addActor(leftedge);
      this.stage.addActor(rightedge);
      this.stage.addActor(left);
      this.stage.addActor(right);
      this.stage.addActor( this.map);
      this.stage.addActor(this.cliffProgress);
      this.stage.addActor(this.pool);
      this.stage.addActor(this.timerLabel);

      addControlButtons();      
      this.stage.addActor(this.health);
      this.stage.addActor(this.warnIndicator);
 
      //  add an overlay for flash effects
      this.overlay = new Overlay();
      this.stage.addActor(this.overlay);
      this.dimmer = new Overlay();
      this.dimmer.setColor(0f,0f,0f,0f);
      this.stage.addActor(this.dimmer);
      
      this.popup = new Popup();
      this.popup.setListener(this);
      this.popup.setPosition(
            (Climber.TGT_WIDTH-this.popup.getWidth())/2, 
            (Climber.TGT_HEIGHT-this.popup.getHeight())/2);
      this.stage.addActor(this.popup);
      
      // the image that shows the outcome of a hazard when it timesout
      this.hazardOutcome = new Image( Assets.instance().getDrawable("bat-hit"));
      this.hazardOutcome.getColor().a = 0;
      this.hazardOutcome.setTouchable(Touchable.disabled);
      this.stage.addActor(this.hazardOutcome);
      
      // add the help overlay
      this.helpOverlay = new HelpOverlay(this.model);
      this.helpOverlay.getColor().a = 0;
      this.helpOverlay.setTouchable(Touchable.disabled);
      this.stage.addActor( this.helpOverlay );
      
      this.malfunctionOverlay = new StaticOverlay();
      this.stage.addActor(this.malfunctionOverlay);
      this.malfunctionOverlay.setTouchable(Touchable.disabled);
      this.malfunctionOverlay.getColor().a = 0f;
      
      // fade in the game play screen
      this.stage.getRoot().getColor().a = 0.0f;
      this.stage.getRoot().addAction( sequence(fadeIn(0.3f), new Action() {

         @Override
         public boolean act(float delta) {
            startGame();
            return true;
         }
      }, delay(1f), new Action() {

         @Override
         public boolean act(float delta) {
            SoundManager.instance().playGameMusic();
            return true;
         }
         
      }));
      
      this.stage.addListener(new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (model.getState().equals(State.PLAN) && x >=200 && x<=1000) {
               model.startPlay();
               popup.clearActions();
               popup.addAction(sequence(fadeOut(0.1f), new Action() {

                  @Override
                  public boolean act(float delta) {
                     pauseButton.setDisabled(false);
                     helpButton.setDisabled(false);
//                     /// HACK 
//                    reachedTop();
                     return true;
                  }

               }));
            }
            return super.touchDown(event, x, y, pointer, button);
         }
      });
   }
   
   private void startGame() {
      this.cliff.setVisible(true);
      this.model.deal();
   }
  
   private void addControlButtons() {
      
      this.helpButton = new Button(
            Assets.instance().getDrawable("help-dim"),
            Assets.instance().getDrawable("help-lit"),
            Assets.instance().getDrawable("help-lit") );
      this.helpButton.setPosition(4, 332);
      this.stage.addActor(this.helpButton);
      this.helpButton.setDisabled(true);
      this.helpButton.addListener( new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
            helpClicked();
         }
      });
      
      this.pauseButton = new Button(
            Assets.instance().getDrawable("pause-dim"),
            Assets.instance().getDrawable("pause-lit"),
            Assets.instance().getDrawable("pause-lit") );
      this.pauseButton.setPosition(85, 332);
      this.stage.addActor(this.pauseButton);
      this.pauseButton.setDisabled(true);
      this.pauseButton.addListener( new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
            pauseClicked();
         }
      });
      
      this.scanButton = new Button(
            Assets.instance().getDrawable("scan-dim"),
            Assets.instance().getDrawable("scan-lit"),
            Assets.instance().getDrawable("scan-lit") );
      this.scanButton.setPosition(5,6);
      this.scanButton.setChecked(true);
      this.scanButton.addListener( new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
            if ( model.isPlaying() == true ) {
               toggleMap();
            } else {
               scanButton.setChecked(false);
            }
         }
      });
      this.stage.addActor(this.scanButton);
      
      // abe buttons
      ButtonStyle st = new ButtonStyle(
            Assets.instance().getDrawable("abe-dim"),
            Assets.instance().getDrawable("abe-lit"),null);
      float y = 6;
      for ( int i=0; i<3; i++) {
         Button b = new Button( st);
         b.setPosition(154, y);
         this.abeButtons[i] = b;
         this.stage.addActor(b);
         b.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
               if ( model.getState().equals(State.PLAY)) {
                  Button abe = (Button) event.getListenerActor();
                  if ( abe.isDisabled() == false ) {
                     SoundManager.instance().playSound(SoundManager.CLICK);
                     model.abeRedraw();
                     ButtonStyle st = new ButtonStyle(
                           Assets.instance().getDrawable("abe-used"), 
                           Assets.instance().getDrawable("abe-used"), null);
                     abe.setStyle(st);
                     abe.setDisabled(true);
                  }
               }
            }
         });
         y+=107;
      }
   }
   
   private void toggleMap() {
      // can't toggle off scan gotta wait til it expires
      if ( this.scanButton.isChecked() == false && this.map.isScanning() ) {
         this.scanButton.setChecked(true);
         return;
      }
      
      SoundManager.instance().playSound(SoundManager.CLICK);
      this.map.showScan();
   }
   
   private void helpClicked() {
      this.helpOverlay.setTouchable(Touchable.disabled);
      if ( this.helpButton.isDisabled() == false && this.model.isOver() == false ) {
         if ( this.model.getState().equals(State.PAUSE) == false ) {
            SoundManager.instance().playSound(SoundManager.CLICK);
            SoundManager.instance().pause();
            this.model.showHelp();
            this.helpOverlay.setTouchable(Touchable.enabled);
         } else {
            this.helpButton.setChecked(false);
         }
      } else {
         this.helpButton.setChecked(false);
      }
   }
   
   private void pauseClicked() {
      if ( this.pauseButton.isDisabled() == false && this.model.isOver() == false ) {
         if ( this.model.isPaused() == false) {
            SoundManager.instance().playSound(SoundManager.CLICK);
            this.model.togglePause();
            SoundManager.instance().pause();
            this.overlay.setColor( new Color(0.0f,0f,0f,0f));
            this.overlay.addAction( ClimberActions.fadeTo(0.7f, 0.5f) );
            this.popup.showPausedMessage();
            this.popup.addAction( fadeIn(0.5f));
         } else {
            this.pauseButton.setChecked(true);
         }
      } else {
         this.pauseButton.setChecked(false);
      }
   }
   
   @Override
   protected boolean backClicked() {
      if ( this.model.isOver() ) {
         quitClicked();
      } else {
         if ( this.model.isPaused() == false) {
            pauseClicked();
         } else {
            resumeClicked();
         }
      }
      return true;
   }
   
   @Override
   public void resumeClicked() {
      this.model.togglePause();
      this.pauseButton.setChecked(false);
      this.overlay.addAction( fadeOut(0.25f) );
      this.popup.addAction( fadeOut(0.25f));
      SoundManager.instance().resume();
   }

   @Override
   public void render(float delta) {
      
      // call 'simulation'
      final float FIXED_TIMESTEP = 1.0f / 60.0f;
      final float MINIMUM_TIMESTEP = 1.0f / 600.0f;
      float frameTime = delta;
      while ( frameTime > 0.0 ){
          float deltaTime = Math.min( frameTime, FIXED_TIMESTEP );
          frameTime -= deltaTime;
          if (frameTime < MINIMUM_TIMESTEP) {
              deltaTime += frameTime;
              frameTime = 0.0f;
          }
          this.model.updateModel(deltaTime);
          if ( this.model.getState().equals(State.HELP)) {
             
             if ( this.helpOverlay.getColor().a == 0) {
                this.helpOverlay.addAction( fadeIn(.5f) );
                this.helpOverlay.setTouchable(Touchable.enabled);
                Settings.instance().rulesViewed();
             } 
             
          } else {
             if ( this.model.getState().equals(State.PLAN) ) {
                if ( this.map.isScanning() == false && this.map.isRecharging() == false) {
                   // first time in, show scanner
                   this.map.showScan();
                   this.popup.showPlan();
                   this.popup.addAction( fadeIn(0.75f) );
                } else {
                   // scaning in process. check for scanner entering recharge mode
                   if ( this.map.isRecharging() ) {
                      
                      // as soon as it does, start the game
                      this.model.startPlay();
                      this.popup.showClimb();
                      this.popup.addAction( sequence(delay(0.75f),fadeOut(0.5f), delay(0.5f), new Action() {
      
                        @Override
                        public boolean act(float delta) {
                           pauseButton.setDisabled(false);
                           helpButton.setDisabled(false);
                           return true;
                        }
                         
                      }));
                   }
                }
             }
             
             // whenever not in help mode, see if help is fully visible
             // if it is, hide it and revert help buttons to unchecked
             if ( this.helpOverlay.getColor().a == 1f) {
                this.helpButton.setChecked(false);
                this.helpOverlay.setTouchable(Touchable.disabled);
                this.helpOverlay.addAction( fadeOut(.5f) );
             }
          }
          updateMapScanner(deltaTime); 
          
          if ( this.cliff.isWaterThreat() ) {
             if ( this.warnIndicator.isOn() == false && this.model.isOver() == false) {
                this.warnIndicator.warn();
                this.map.showWarning("water");
             }
          } else {
             if ( this.model.isHazardActive() == false && this.warnIndicator.isOn() ) {
                this.warnIndicator.cancelWarn();
                this.map.clearWarning();
             }
          }
          
          // is ike DEAD?
          if ( this.model.getHealth() == 0 && this.model.isPlaying()) {
             SoundManager.instance().stopAllLooping();
             this.model.killIke();
             this.warnIndicator.cancelWarn();
             this.executor.schedule( new Runnable() {

                @Override
                public void run() {
                   dimmer.addAction( ClimberActions.fadeTo(0.7f, 0.5f) );
                   popup.showGameOver();
                   popup.addAction( fadeIn(0.5f));
                }
                
             }, 2000, TimeUnit.MILLISECONDS);
          }
          
          if ( this.map.isAlert() && this.model.isPlaying() ) {
             if ( this.warnPulseDelay <= 0 ) {
                this.warnPulseDelay = 1.0f;
                if ( this.model.isImpediment()) {
                   this.overlay.setColor( new Color(0.4f, 0.4f, 0.1f, 0f));
                } else {
                   this.overlay.setColor( new Color(0.7f,0f,0f,0f));
                }
                this.overlay.addAction( ClimberActions.pulse(0.5f));
             } else {
                this.warnPulseDelay -= delta;
             }
          }
      }
      
      // get delta x and y for ikes movements
      if ( this.model.isPlaying() ) {
         if ( this.model.isClimbing()  ) {
            float delY = this.model.getClimbDistance(delta);
            float delX = this.model.getSidewaysDistance(delta);
            this.cliff.getIke().setClimbAngle( this.model.getClimbAngle() );
            
            
            // Move it!
            boolean xBlocked = false;
            if ( !this.cliff.sidle(delX) ) {
               xBlocked = true;
            }
            boolean yBlocked = false;
            if ( !this.cliff.climb( delY, delta )) {
               yBlocked = true;
            }
            
            if ( xBlocked || yBlocked ) {
               if ( this.model.interruptMove(xBlocked, yBlocked) ) {
                  if ( this.model.isPlaying() ) {
                     if ( this.model.isShielded()) {
                        this.overlay.setColor(new Color(0.1f, .1f, 1.2f, 0.5f));
                     } else {
                        this.overlay.setColor(new Color(1.2f, .1f, .1f, 0.5f));
                     }
                     this.overlay.addAction(ClimberActions.pulse(1.0f));
                     SoundManager.instance().playSound(SoundManager.HEAD_BONK);
                  }
               }
            }
         } else {
            this.cliff.getIke().stopMoving();
         }
         
         // Update percent climbed and timer
         updateStats();
      }

      super.render(delta);
   }
   
   private void updateMapScanner( float deltaTime ) {
      // when scan is going on, just decrement scan duration timer
      if ( this.map.isScanning() ) {
         this.map.updateTimer(deltaTime);
      } else if ( this.map.isRecharging() ) {
         // once it goes recharge, disable and dim the scan button
         if ( this.scanButton.isDisabled() == false ) {
            this.scanButton.setDisabled(true);
            ButtonStyle st = new ButtonStyle(
                  Assets.instance().getDrawable("scan-recharge"), 
                  Assets.instance().getDrawable("scan-recharge"), null);
            this.scanButton.setStyle(st);
         }
         if ( this.scanButton.isChecked() ) {
            this.scanButton.setChecked(false);
         }
         this.map.updateTimer(deltaTime);
      } else {
         // its ready again. reset style
         if ( this.scanButton.isDisabled() ) {
            this.scanButton.setDisabled(false);
            ButtonStyle st = new ButtonStyle(
                  Assets.instance().getDrawable("scan-dim"), 
                  Assets.instance().getDrawable("scan-lit"), 
                  Assets.instance().getDrawable("scan-lit"));
            this.scanButton.setStyle(st);
         }
      }    
   }
   
   private void updateStats() {
      if ( this.model.isPlaying() ) {
         float elapsed = this.model.getElapsedSec();
         if ( elapsed > 0 ) {
            int mins = (int)(elapsed / 60);
            float sec = elapsed - (mins*60);
            String t = String.format("%d:%04.1f", mins, sec);
            this.timerLabel.setText(t);
         }
      }
   }

   @Override
   public void reachedTop() {
      if ( this.model.isOver() == false ) {
         SoundManager.instance().stopAllLooping();

         this.warnIndicator.cancelWarn();
         this.model.winGame();
         this.overlay.setColor( new Color(0.0f,0f,0f,0f));
         this.overlay.addAction( ClimberActions.fadeTo(0.7f, 0.5f));
         Image[] flip  = new Image[3];
         float x[] = {270,540,410};
         float y[] = {240,250,70};
         float delays[] = {0.5f,2f, 3.5f};
         for ( int i=0; i<3; i++) {
            flip[i] = new Image(Assets.instance().getDrawable("legflip"+(i+1)));
            flip[i].setPosition(x[i], y[i]);
            flip[i].getColor().a = 0;
            flip[i].setName("flip");
            stage.addActor(flip[i]);
         }
         
         float totalDelay = 0;
         for ( int i=0; i<3; i++) {
            final int fr = i;
            totalDelay += delays[i];
            flip[i].addAction(sequence(delay(delays[i]), new Action() {
               @Override
               public boolean act(float delta) {
                  SoundManager.instance().playLegFlipMusic(fr);
                  return true;
               }
               
            }, fadeIn(0.5f)) );
         }
         this.stage.addAction(sequence(delay(totalDelay-1f), new Action() {

            @Override
            public boolean act(float delta) {
               SoundManager.instance().playLegFlipMusic(3);
                showWinPanel();
                return true;
            }
            
         }));
      }
   }

   private void showWinPanel() {
      final WinPanel winPanel = new WinPanel( this.game,
            this.model.getElapsedSec(), 
            Settings.instance().getCurrentMap().getBestTimeSec());
      winPanel.setName("win");
      this.stage.addActor(winPanel);
      winPanel.getColor().a = 0f;
      winPanel.addListener(this);
      winPanel.addAction( sequence(fadeIn(0.5f), new Action() {

         @Override
         public boolean act(float delta) {
            for (Actor a : stage.getActors() ) {
               if ( a.getName() != null && a.getName().equals("flip")) {
                  a.setVisible(false);
               }
            }
            return true;
         }
         
      }));
      
      if ( Settings.instance().isBestTime(this.model.getElapsedSec() ) ) {
         Settings.instance().setBestTime(this.model.getElapsedSec());
      }
   }

   @Override
   public void drowned() {
      if ( this.model.isPlaying()){
         SoundManager.instance().stopAllLooping();
         SoundManager.instance().playSound(SoundManager.DROWN);
         this.model.killIke();
         this.dimmer.addAction( ClimberActions.fadeTo(0.7f, 0.5f) );
         this.popup.showGameOver();
         this.popup.addAction( fadeIn(0.5f));
         this.warnIndicator.cancelWarn();
      }
   }

   @Override
   public void restartClicked() {
      this.popup.getColor().a = 0;
      this.executor.shutdown();
      this.model.endGame();
      this.map.setVisible(false);
      this.cliffProgress.setVisible(false);
      this.stage.addAction( sequence(fadeOut(0.5f), new Action() {
         
         @Override
         public boolean act(float delta) {
            game.showGameScreen();
            return false;
         }
      }));
      
   }

   @Override
   public void quitClicked() {
      this.executor.shutdown();
      SoundManager.instance().playSound(SoundManager.CLICK);
      SoundManager.instance().stopAllLooping();
      this.model.endGame(); 
      backToMenu();
   }
   
   private void backToMenu() {
      this.stage.addAction( sequence(fadeOut(0.5f), new Action() {
         @Override
         public boolean act(float delta) {
            game.showMenuScreen();
            return true;
         }
      }));
   }

   @Override
   public void newHazard(CardInfo hazard) {
      if ( this.warnIndicator.isOn() == false) {
         this.warnIndicator.warn();
         this.map.showWarning(hazard.getImageName());
      }  
      
      if ( hazard.getType().equals(Type.MALFUNCTION)) {
         if ( this.malfunctionOverlay.getColor().a == 0 ) {
            this.malfunctionOverlay.setTouchable(Touchable.enabled);
            this.malfunctionOverlay.getColor().a = 1f;
            SoundManager.instance().loopSound(SoundManager.MALFUNCTION);
         }
      }
   }
   
   @Override
   public void hazardTimedOut(CardInfo hazard) {
      // if this hazard causes damage when it times out it will have
      // a power set > 0. Show the damage outcome popup
      if ( hazard.getType().equals(CardInfo.Type.HAZARD) ) {
         String file = hazard.getImageName()+"-hit";
         if ( this.model.isShielded() == false) {
            this.hazardOutcome.setDrawable( Assets.instance().getDrawable(file));
            this.hazardOutcome.setPosition(
                  (Climber.TGT_WIDTH-this.hazardOutcome.getWidth())/2,
                  (Climber.TGT_HEIGHT-this.hazardOutcome.getHeight())/2);
            this.hazardOutcome.addAction( sequence(fadeIn(0.2f), delay(2.5f),fadeOut(0.2f) ) );
            this.overlay.setColor( new Color(0.5f,0f,0f,0f));
            this.overlay.addAction( sequence(ClimberActions.fadeTo(0.25f, 0.5f), fadeOut(0.2f) ) );
         }
      }
      
      if ( this.warnIndicator.isOn() ) {
         this.warnIndicator.cancelWarn();
         this.map.clearWarning();
      }
      
      if ( this.malfunctionOverlay.getColor().a != 0 ) {
         this.malfunctionOverlay.setTouchable(Touchable.disabled);
         this.malfunctionOverlay.getColor().a = 0f;
         SoundManager.instance().stopSound(SoundManager.MALFUNCTION);
      }
   }
   
   @Override
   public void dispose() {
      this.executor.shutdown();
      super.dispose();
   }
   
   @Override
   public void hazardCleared() {
      if ( this.warnIndicator.isOn() ) {
         this.warnIndicator.cancelWarn();
         this.map.clearWarning();
      }
   }

   public void fadeToMenu() {
      backToMenu();
   }
}
