package com.studio332.omega.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.studio332.omega.model.Settings;

public class SoundManager {
   private static final SoundManager instance = new SoundManager();
   private Music currMusic = null;
   private Music gameMusic;
   private Music introMusic;
   private Music splashMusic;
   private Music menuMusic;
   private Music[] legFlipMusic = new Music[4];
   private Music hiScoreMusic;

   private Map<String, Sound> sounds = new HashMap<String, Sound>();
   private Set<Sound> looping = new HashSet<Sound>();

   public static final String ALERT = "sounds/alert.mp3";
   public static final String HEAD_BONK = "sounds/head_bonk.mp3";
   public static final String CLIMB = "sounds/climb.mp3";
   public static final String SIDEWAYS = "sounds/sideways.mp3";
   public static final String CLICK = "sounds/click.mp3";
   public static final String TRASH = "sounds/delete.mp3";
   public static final String DEAL = "sounds/deal.mp3";
   public static final String BOOST = "sounds/boost.mp3";
   public static final String HAPPY = "sounds/happy.mp3";
   public static final String SCAN = "sounds/scan2.mp3";
   public static final String BAT_FLAP = "sounds/bat_flaps.mp3";
   public static final String BAT_ATTACK = "sounds/bat_attack.mp3";
   public static final String ROCK_SLIDE = "sounds/rockslide.mp3";
   public static final String NO_PLAY = "sounds/no_play.mp3";
   public static final String DIAMOND = "sounds/diamond.mp3";
   public static final String DROWN = "sounds/drown.mp3";
   public static final String SCREAM = "sounds/scream.mp3";
   public static final String MENU_CLICK = "sounds/menu_click.mp3";
   public static final String TANK = "sounds/tank.mp3";
   public static final String HI_SCORE = "sounds/hiscore.mp3";
   public static final String LEG_FLIP = "sounds/legflip.mp3";
   public static final String MALFUNCTION = "sounds/malfunction.mp3";
   public static final String CONFUSE = "sounds/confuse.mp3";
   public static final String WAVE = "sounds/tidal_wave.mp3";
   public static final String SLOW = "sounds/loose_rock.mp3";

   public static SoundManager instance() {
      return SoundManager.instance;
   }

   public void init() {
      FileHandle soundFile = Gdx.files.internal(HEAD_BONK);
      this.sounds.put(HEAD_BONK, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal(CLIMB);
      this.sounds.put(CLIMB, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal(SIDEWAYS);
      this.sounds.put(SIDEWAYS, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal(CLICK);
      this.sounds.put(CLICK, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal(TRASH);
      this.sounds.put(TRASH, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal(DEAL);
      this.sounds.put(DEAL, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal(BOOST);
      this.sounds.put(BOOST, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal(HAPPY);
      this.sounds.put(HAPPY, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal(SCAN);
      this.sounds.put(SCAN, Gdx.audio.newSound(soundFile));
      this.sounds.put(BAT_FLAP, Gdx.audio.newSound(Gdx.files.internal(BAT_FLAP)));
      this.sounds.put(BAT_ATTACK, Gdx.audio.newSound(Gdx.files.internal(BAT_ATTACK)));
      this.sounds.put(ROCK_SLIDE, Gdx.audio.newSound(Gdx.files.internal(ROCK_SLIDE)));
      this.sounds.put(NO_PLAY, Gdx.audio.newSound(Gdx.files.internal(NO_PLAY)));
      this.sounds.put(DIAMOND, Gdx.audio.newSound(Gdx.files.internal(DIAMOND)));
      this.sounds.put(DROWN, Gdx.audio.newSound(Gdx.files.internal(DROWN)));
      this.sounds.put(SCREAM, Gdx.audio.newSound(Gdx.files.internal(SCREAM)));
      this.sounds.put(MENU_CLICK, Gdx.audio.newSound(Gdx.files.internal(MENU_CLICK)));
      this.sounds.put(ALERT, Gdx.audio.newSound(Gdx.files.internal(ALERT)));
      this.sounds.put(TANK, Gdx.audio.newSound(Gdx.files.internal(TANK)));
      this.sounds.put(MALFUNCTION, Gdx.audio.newSound(Gdx.files.internal(MALFUNCTION)));
      this.sounds.put(CONFUSE, Gdx.audio.newSound(Gdx.files.internal(CONFUSE)));
      this.sounds.put(WAVE, Gdx.audio.newSound(Gdx.files.internal(WAVE)));
      this.sounds.put(SLOW, Gdx.audio.newSound(Gdx.files.internal(SLOW)));
      
      this.gameMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/game.ogg"));
      this.gameMusic.setLooping(true);
      this.gameMusic.setVolume(0.85f);
      this.introMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/intro.mp3"));
      this.splashMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/splash.mp3"));
      this.menuMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/menu.mp3"));
      this.menuMusic.setLooping(true);
      this.hiScoreMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/hiscore.mp3"));
      for ( int i=0; i<4; i++) {
         try {
            this.legFlipMusic[i] = Gdx.audio.newMusic(Gdx.files.internal("sounds/flip"+(i+1)+".mp3"));
         } catch (Exception e) {
            // NO-OP
         }

      }
   }
   
   public void playLegFlipMusic(int frame) {
     stopMusic();
     for ( int i=0;i<frame;i++) {
        if ( this.legFlipMusic[i] != null ) {
           this.legFlipMusic[i].stop();
        }
     }
     if ( this.legFlipMusic[frame] != null ) {
        this.legFlipMusic[frame].play();
     }
   }
   public void playHiScoreMusic() {
      stopMusic();
      this.hiScoreMusic.play();
    }

   public void playMenuMusic() {
      if (this.currMusic != null ) {
         this.currMusic.stop();
      }

      this.currMusic = this.menuMusic;
      if (Settings.instance().isMusicOn() ) {
         this.currMusic.play();
      }
   }

   public void stopMusic() {
      if (this.currMusic != null) {
         this.currMusic.stop();
      }
   }

   public void playIntroMusic() {
      this.currMusic = this.introMusic;
      this.currMusic.setLooping(false);
      this.currMusic.play();
      
   }
   public void playSplashMusic() {
      this.currMusic = this.splashMusic;
      this.currMusic.setLooping(false);
      this.currMusic.play();
      
   }
   public void playGameMusic() {      
      if (this.currMusic != null && this.currMusic.isPlaying()) {
         this.currMusic.stop();
      }
      
      this.currMusic = this.gameMusic;
      if (Settings.instance().isMusicOn() ) {
         this.currMusic.play();
      }
   }
   
   public void pause() {
      if ( this.currMusic != null ) {
         this.currMusic.pause();
      }
      pauseAllLooping();
      for (Sound s : this.sounds.values() ) {
         s.stop();
      }
   }
   
   public void resume() {
      if ( this.currMusic != null ) {
         this.currMusic.play();
      }
      resumeAllLooping();
   }

   public void playSound(final String sound) {
      if (Settings.instance().isSoundOn()) {
         Sound s = this.sounds.get(sound);
         if (s != null) {
            s.play();
         }
      }
   }
   
   public void loopSound(final String sound) {
      if (Settings.instance().isSoundOn()) {
         Sound s = this.sounds.get(sound);
         if (s != null) {
            if ( this.looping.contains(s) == false ) {
               s.loop();
               this.looping.add(s);
            }
         }
      }
   }
   
   public void stopAllLooping() {
      for ( Sound s : this.looping ) {
         s.stop();
      }
      this.looping.clear();
   }
   
   private void pauseAllLooping() {
      for ( Sound s : this.looping ) {
         s.stop();
      }
   }
   
   private void resumeAllLooping() {
      for ( Sound s : this.looping ) {
         s.loop();
      }
   }
   
   public void stopSound(final String sound ) {
      Sound s = this.sounds.get(sound);
      if ( s != null ) {
         s.stop();
         this.looping.remove(s);
      }
   }
}
