package com.studio332.omega.util;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Assets {
   private static final Assets instance = new Assets();
   private Map<String, Texture> gfxMap = new HashMap<String, Texture>();
   public TextureAtlas gameAtlas;
   private BitmapFont font;
   private BitmapFont helpFont;

   public static Assets instance() {
      return Assets.instance;
   }
   
   public void load() {
      SoundManager.instance().init();
      
      add("pixel","pixel.png");  // for making tinted overlays

      this.font = new BitmapFont(
            Gdx.files.internal("data/bitsumis.fnt"), 
            Gdx.files.internal("data/bitsumis.png"), false);
      this.helpFont = new BitmapFont(
            Gdx.files.internal("data/hn.fnt"), 
            Gdx.files.internal("data/hn.png"), false);
      
      this.gameAtlas = new TextureAtlas(Gdx.files.internal( "data/game_atlas.atlas"));
   }
   
   public BitmapFont getFont() {
      return this.font;
   }
   
   public BitmapFont getHelpFont() {
      return this.helpFont;
   }
   
   private void add( final String name, final String file) {
      Texture tx = new Texture("data/"+file);
      tx.setFilter(TextureFilter.Linear, TextureFilter.Linear);
      this.gfxMap.put(name, tx);
   }
   
   public Texture getPixel() {
      return this.gfxMap.get("pixel");
   }
   
   public AtlasRegion getAtlasRegion(final String name) {
      return Assets.instance().gameAtlas.findRegion(name);
   }
   
   public Drawable getDrawable(final String name) {
      AtlasRegion ar =  Assets.instance().getAtlasRegion(name);
      return ( new TextureRegionDrawable( ar) );
   }
}
