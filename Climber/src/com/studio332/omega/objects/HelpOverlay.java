package com.studio332.omega.objects;

import java.util.List;

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
import com.studio332.omega.model.CardInfo;
import com.studio332.omega.model.CardInfo.Type;
import com.studio332.omega.model.ClimberGame;
import com.studio332.omega.model.ClimberGame.State;
import com.studio332.omega.model.Help;
import com.studio332.omega.util.Assets;
import com.studio332.omega.util.SoundManager;

public class HelpOverlay extends Group {
   private ClimberGame model;
   private ShapeRenderer shapeRenderer;
   private Label helpTitle;
   private Label helpText;
   private String activeLetter = "";
   private final Color[] colors = { 
         new Color(0.0f, 0f, 0f,  getColor().a),
         new Color(0.6f, 0.4f, 1f,  getColor().a) };
   private static final int DARK = 0;
   private static final int LIGHT = 1;
   private boolean cardMode = false;
   private List<CardInfo> cardDict;
   private int cardIdx = 0;
   private Image cardImageSmall;
   private Image cardImageBig;
   private Label cardTypeHelp;
   private boolean tapHandled = false;

   public HelpOverlay(final ClimberGame model) {
      super();
      this.model = model;
      this.cardDict = this.model.getCardDiscionary();
      

      LabelStyle st = new LabelStyle(Assets.instance().getHelpFont(), this.colors[1]);
      
      final Group cardGroup = new Group();
      CardInfo ci = this.cardDict.get(this.cardIdx);
      this.cardImageBig = new Image(Assets.instance().getDrawable(ci.getImageName()+"-big"));
      this.cardImageSmall = new Image(Assets.instance().getDrawable(ci.getImageName()));
      this.cardImageBig.setPosition(
            (Climber.TGT_WIDTH-this.cardImageBig.getWidth())/2f-140, 
            (Climber.TGT_HEIGHT-this.cardImageBig.getWidth())/2f-this.cardImageSmall.getHeight());
      this.cardImageSmall.setPosition(
            (Climber.TGT_WIDTH-this.cardImageSmall.getWidth())/2-140f, 
            this.cardImageBig.getY()+this.cardImageBig.getHeight()+5f);
      cardGroup.addActor(this.cardImageSmall);
      cardGroup.addActor(this.cardImageBig);
      Label cardTitle = new Label("Card dictionary", st);
      cardTitle.setWidth(370);
      cardTitle.setAlignment(Align.center);
      cardTitle.setPosition( (Climber.TGT_WIDTH-370)/2, 555);
      cardGroup.addActor(cardTitle);
      this.cardTypeHelp = new Label(this.model.getCardTypeHelp().get("Move"), st);
      this.cardTypeHelp.setFontScale(0.8f);
      this.cardTypeHelp.setWrap(true);
      this.cardTypeHelp.setWidth(290);
      this.cardTypeHelp.setHeight(270);
      this.cardTypeHelp.setAlignment(Align.left);
      this.cardTypeHelp.setPosition( Climber.TGT_WIDTH/2+90, 235);
      cardGroup.addActor(this.cardTypeHelp);
      
      cardGroup.setVisible(false);
      addActor(cardGroup);
      
      this.shapeRenderer = new ShapeRenderer();
      setWidth(Climber.TGT_WIDTH);
      setHeight(Climber.TGT_HEIGHT);
      getColor().a = 0;
      
      // help box
      st = new LabelStyle(Assets.instance().getHelpFont(),  this.colors[1]);
      this.helpTitle = new Label("High Tide Help", st);
      this.helpTitle.setWrap(false);
      this.helpTitle.setWidth(370);
      this.helpTitle.setAlignment(Align.center);
      this.helpTitle.setPosition( (Climber.TGT_WIDTH-370)/2, 527);
      
      // help text
      String t = "Ike must race to the mountain ";
      t += "top before the rising water catches him.\n\nTap a letter for more help.";
      this.helpText = new Label(t, st);
      this.helpText.setFontScale(0.8f);
      this.helpText.setWrap(true);
      this.helpText.setWidth(420);
      this.helpText.setHeight(270);
      this.helpText.setAlignment(Align.center);
      this.helpText.setPosition( (Climber.TGT_WIDTH-420)/2-2, 280);
      
      final Group labelGroup = new Group();
      HelpLabel a = new HelpLabel("F", st);
      a.setPosition(912,337);
      labelGroup.addActor(a);
      
      HelpLabel b = new HelpLabel("A", st);
      b.setPosition(283,652);
      labelGroup.addActor(b);
      
      HelpLabel c = new HelpLabel("B", st);
      c.setPosition(283,462);
      labelGroup.addActor(c);
      
      HelpLabel d = new HelpLabel("C", st);
      d.setPosition(283,292);
      labelGroup.addActor(d);
      
      HelpLabel e = new HelpLabel("D", st);
      e.setPosition(283,133);
      labelGroup.addActor(e);
      
      HelpLabel f = new HelpLabel("E", st);
      f.setPosition(283,13);
      labelGroup.addActor(f);
      addActor(labelGroup);
      
      final Label card = new Label("Cards", st);
      card.setAlignment(Align.center);
      card.setFontScale(0.95f);
      card.setPosition(836,612);
      card.setHeight(85);
      card.addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            SoundManager.instance().playSound(SoundManager.CLICK);
            cardMode = true;
            card.setVisible(false);
            labelGroup.setVisible(false);
            helpText.setVisible(false);
            helpTitle.setVisible(false);
            cardGroup.setVisible(true);
            tapHandled = true;
            return true;
         }
      });
      addActor(card);
      
      Label done = new Label("Done", st);
      done.setAlignment(Align.center);
      done.setFontScale(0.95f);
      done.setPosition(842,12);
      done.setHeight(85);
      done.addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            SoundManager.instance().playSound(SoundManager.CLICK);
            tapHandled = true;
            if ( cardMode ) {
               cardMode =false;
               card.setVisible(true);
               labelGroup.setVisible(true);
               helpText.setVisible(true);
               helpTitle.setVisible(true);
               cardGroup.setVisible(false);
            } else {
               model.clearHelp();
               SoundManager.instance().playGameMusic();
            }
            return true;
         }
      });
      addActor(done);
      addActor(this.helpTitle);
      addActor(this.helpText);
      
      addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if ( cardMode && tapHandled == false ) {
               if ( x > Climber.TGT_WIDTH/2-80) {
                  cardIdx++;
                  if ( cardIdx >= cardDict.size()) {
                     cardIdx = 0;
                  }
               } else {
                  cardIdx--;
                  if ( cardIdx < 0 ) {
                     cardIdx = cardDict.size()-1;
                  }
               }
               SoundManager.instance().playSound(SoundManager.CLICK);
               CardInfo ci = cardDict.get(cardIdx);
               cardImageBig.setDrawable(Assets.instance().getDrawable(ci.getImageName()+"-big"));
               cardImageSmall.setDrawable(Assets.instance().getDrawable(ci.getImageName()));
               if ( ci.getType().equals(Type.MOVE ) ) {
                  cardTypeHelp.setText(model.getCardTypeHelp().get("Move"));
               } else if ( ci.getType().equals(Type.HEAL ) ||  ci.getType().equals(Type.TANK )) {
                  cardTypeHelp.setText(model.getCardTypeHelp().get("Item"));
               } else if ( ci.getType().equals(Type.WILD ) ||  ci.getType().equals(Type.DEFEND)) {
                  cardTypeHelp.setText(model.getCardTypeHelp().get("ABE"));
               } else if (ci.getType().equals(Type.BOOST) ) {
                  cardTypeHelp.setText(model.getCardTypeHelp().get( ci.getImageName()));
               } else {
                  cardTypeHelp.setText(model.getCardTypeHelp().get( ci.getImageName()));
               }
            }
            tapHandled = false;
            return true;
         }
      });
   }
   
   private void showHelp(String itemId ) {
      SoundManager.instance().playSound(SoundManager.CLICK);
      this.activeLetter = itemId.trim().toUpperCase();
      Help help = this.model.getCalloutHelp(this.activeLetter);
      this.helpTitle.setText(help.getTitle());
      this.helpText.setText(help.getText());
   }

   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
      batch.end();
      
      Gdx.gl.glEnable(GL20.GL_BLEND);
      Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
      
      
      this.shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
      this.shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
      this.shapeRenderer.translate(getX(), getY(), 0);
      
      if ( this.cardMode == false ) {
         drawPoolBracket();
         drawProgressBracket();
         drawAbeBracket();
         drawStaminaCallout();
         drawScanCallout();
         drawMapCallout();
         
         drawCard();
         
         Gdx.gl20.glLineWidth(2);
         drawHelpBox();
      } else {
         drawCardDictionary();
      }
      drawDone();

      Gdx.gl.glDisable(GL20.GL_BLEND);
      
      batch.begin();
      
      super.draw(batch, parentAlpha);
   }
   
   private void drawCardDictionary() {
      this.shapeRenderer.begin(ShapeType.Filled);
      this.shapeRenderer.setColor(0f, 0f, 0f,  getColor().a);
      this.shapeRenderer.rect((Climber.TGT_WIDTH-380)/2-150, 180, 380+300, 415);

      setHelpColor(LIGHT);
      this.shapeRenderer.rect((Climber.TGT_WIDTH-374)/2-150, 183, 374+300, 409);
      
      this.shapeRenderer.setColor(0f, 0f, 0f,  getColor().a);
      this.shapeRenderer.rect((Climber.TGT_WIDTH-368)/2-150, 186, 368+300, 403);
      this.shapeRenderer.end();
      
      this.shapeRenderer.begin(ShapeType.Line);
      float l = (Climber.TGT_WIDTH-368)/2;
      setHelpColor(LIGHT);
      this.shapeRenderer.line(l-150, 550, l+368+150, 550);
      Gdx.gl20.glLineWidth(3);
      float arrowL = (Climber.TGT_WIDTH)/2-300;
      float arrowR = (Climber.TGT_WIDTH)/2+20;
      float midH = (Climber.TGT_HEIGHT)/2;
      
      this.shapeRenderer.line(arrowL+45, midH+75, arrowL, midH);
      this.shapeRenderer.line(arrowL+45, midH-75, arrowL, midH);
      this.shapeRenderer.line(arrowR-45, midH+75, arrowR, midH);
      this.shapeRenderer.line(arrowR-45, midH-75, arrowR, midH);
      
      float txtLinePs =  (Climber.TGT_WIDTH)/2+70;
      this.shapeRenderer.line(txtLinePs, 550, txtLinePs, 182);
      
      this.shapeRenderer.end();
   }

   private void drawHelpBox() {
      this.shapeRenderer.begin(ShapeType.Filled);
      this.shapeRenderer.setColor(0f, 0f, 0f,  getColor().a);
      this.shapeRenderer.rect((Climber.TGT_WIDTH-380)/2, 300, 380, 270);

      setHelpColor(LIGHT);
      this.shapeRenderer.rect((Climber.TGT_WIDTH-374)/2, 303, 374, 264);
      
      setHelpColor(DARK);
      this.shapeRenderer.rect((Climber.TGT_WIDTH-368)/2, 306, 368, 258);
      this.shapeRenderer.end();
      
      this.shapeRenderer.begin(ShapeType.Line);
      float l = (Climber.TGT_WIDTH-368)/2;
      setHelpColor(LIGHT);
      this.shapeRenderer.line(l, 520, l+368, 520);
      this.shapeRenderer.end();
   }
   
   private void drawDone() {
      this.shapeRenderer.begin(ShapeType.Filled);
      setHelpColor(DARK);
      this.shapeRenderer.rect(820, 25, 120, 55);
      this.shapeRenderer.end(); 
      
      this.shapeRenderer.setColor(0,0,0,getColor().a);
      this.shapeRenderer.begin(ShapeType.Line);
      this.shapeRenderer.rect(820, 25, 120, 55);
      this.shapeRenderer.end();
      
      Gdx.gl20.glLineWidth(1);
      this.shapeRenderer.begin(ShapeType.Line);
      setHelpColor(LIGHT);
      this.shapeRenderer.rect(822, 27, 116, 51);
      this.shapeRenderer.end();
   }
   
   private void drawCard() {
      this.shapeRenderer.begin(ShapeType.Filled);
      setHelpColor(DARK);
      this.shapeRenderer.rect(820, 625, 120, 55);
      this.shapeRenderer.end(); 
      
      this.shapeRenderer.setColor(0,0,0,getColor().a);
      this.shapeRenderer.begin(ShapeType.Line);
      this.shapeRenderer.rect(820, 625, 120, 55);
      this.shapeRenderer.end();
      
      Gdx.gl20.glLineWidth(1);
      this.shapeRenderer.begin(ShapeType.Line);
      setHelpColor(LIGHT);
      this.shapeRenderer.rect(822, 627, 116, 51);
      this.shapeRenderer.end();
   }
   
   private void setHelpColor(int idx) {
      this.shapeRenderer.setColor(this.colors[idx].r, this.colors[idx].g,this.colors[idx].b,getColor().a);
   }

   private void drawPoolBracket() {
      for ( int i=0; i<2; i++) {
         this.shapeRenderer.begin(ShapeType.Line);
         setHelpColor(i);
         Gdx.gl20.glLineWidth(6-i*2);
         this.shapeRenderer.line(1020, 60, 1020, 690);
         this.shapeRenderer.line(1020, 60, 1050, 60);
         this.shapeRenderer.line(1020, 690, 1050, 690);
         this.shapeRenderer.line(1020, 375, 980, 375);
         this.shapeRenderer.end();
      }
      
      drawDot(950,375, this.activeLetter.equalsIgnoreCase("F"));
   }
   
   private void drawDot( float x, float y, boolean lit) {
      if ( lit ) {
         setHelpColor(LIGHT);
      } else {
         setHelpColor(DARK);
      }
      this.shapeRenderer.begin(ShapeType.Filled);
      this.shapeRenderer.rect(x-28,y-30, 60,60);
      this.shapeRenderer.end();
      setHelpColor(LIGHT);
      this.shapeRenderer.begin(ShapeType.Line);
      Gdx.gl20.glLineWidth(2);
      this.shapeRenderer.rect(x-25,y-27, 54,54);
      this.shapeRenderer.end();
   }
   
   private void drawProgressBracket() {
      for ( int i=0; i<2; i++) {
         this.shapeRenderer.begin(ShapeType.Line);
         setHelpColor(i);
         Gdx.gl20.glLineWidth(6-i*2);
         this.shapeRenderer.line(250, 370, 250, 630);
         this.shapeRenderer.line(250, 370, 220, 370);
         this.shapeRenderer.line(250, 630, 220, 630);
         this.shapeRenderer.line(250, 500, 290, 500);
         this.shapeRenderer.end();
      }
      drawDot(320,500, this.activeLetter.equals("B"));
   }
   
   private void drawAbeBracket() {
      for ( int i=0; i<2; i++) {
         this.shapeRenderer.begin(ShapeType.Line);
         setHelpColor(i);
         Gdx.gl20.glLineWidth(6-i*2);
         this.shapeRenderer.line(250, 60, 250, 280);
         this.shapeRenderer.line(250, 60, 220, 60);
         this.shapeRenderer.line(250, 280, 220, 280);
         this.shapeRenderer.line(250, 170, 290, 170);
         this.shapeRenderer.end();
      }
      drawDot(320,170, this.activeLetter.equals("D"));
   }
   
   private void drawStaminaCallout() {
      for ( int i=0; i<2; i++) {
         this.shapeRenderer.begin(ShapeType.Line);
         setHelpColor(i);
         Gdx.gl20.glLineWidth(6-i*2);
         this.shapeRenderer.line(130, 610, 320, 690);
         this.shapeRenderer.end();
      }
      drawDot(320,690, this.activeLetter.equals("A"));
   }
   
   private void drawScanCallout() {
      for ( int i=0; i<2; i++) {
         this.shapeRenderer.begin(ShapeType.Line);
         setHelpColor(i);
         Gdx.gl20.glLineWidth(6-i*2);
         this.shapeRenderer.line(110, 30, 320, 50);
         this.shapeRenderer.end();
      }
      drawDot(320,50, this.activeLetter.equals("E"));
   }
   
   private void drawMapCallout() {
      for ( int i=0; i<2; i++) {
         this.shapeRenderer.begin(ShapeType.Line);
         setHelpColor(i);
         Gdx.gl20.glLineWidth(6-i*2);
         this.shapeRenderer.line(110, 300, 320, 330);
         this.shapeRenderer.end();
      }
      drawDot(320,330, this.activeLetter.equals("C"));
   }
   
   /*
    * Helper class to show help click points and respond to touch
    */
   private class HelpLabel extends Label {
      public HelpLabel(CharSequence text, LabelStyle style) {
         super(text, style);
         setAlignment(Align.center);
         setWidth(80);
         setHeight(80);
         setFontScale(0.95f);
         addListener( new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               if ( model.getState().equals(State.HELP)) {
                  showHelp(getText().toString());
               }
               return super.touchDown(event, x, y, pointer, button);
            }
         });
      }
      
   }
}
