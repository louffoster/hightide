package com.studio332.omega.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.studio332.omega.util.Assets;

public class Recalibrator extends Group {
   private Listener listener;
   private Image touchHighlight;
   private int highlightCell = -1;
   private int angles[] = {225, 180, 135,
                           270, -1,  90,
                           315, 0,   45};
   
   public Recalibrator() {
      super();
      setVisible(false);
      
      Image bkg = new Image( Assets.instance().getAtlasRegion("recalibrate"));
      addActor(bkg);
      setWidth(bkg.getWidth());
      setHeight(bkg.getHeight());
      
      this.touchHighlight = new Image(Assets.instance().getPixel());
      this.touchHighlight.setWidth(65);
      this.touchHighlight.setHeight(65);
      this.touchHighlight.getColor().a = 0;
      addActor(this.touchHighlight);
      
      addListener( new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            findTouchedMove(x, y);
            return true;
         }
         
         @Override
         public void touchDragged(InputEvent event, float x, float y, int pointer) {
            findTouchedMove(x, y);
         }
         
         @Override
         public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            doRecalibrate();
            
         }
      });
   }
   
   private void doRecalibrate() {
      if ( this.listener != null ) {
         this.listener.recalibrated(this.angles[this.highlightCell]);
         this.highlightCell = -1;
         this.touchHighlight.getColor().a = 0;
      }
   }
   
   private void findTouchedMove( float x, float y) {
      if ( x<0 || x>getWidth() || y<0 || y> getHeight()) {
         this.highlightCell = -1;
      }
      int col = 2;
      if ( x < 68   ) {
         col = 0;
      } else if ( x  < 135 ) {
         col = 1;
      }
      
      int row = 2;
      if ( y < 68   ) {
         row = 0;
      } else if ( y < 135 ) {
         row = 1;
      }
      
      this.highlightCell = row*3+col;
      positionLitSquare();
   }
   
   public void setListener( Listener l) {
      this.listener = l;
   }
   
   private void positionLitSquare() {
      if ( this.highlightCell == -1 ) {
         this.touchHighlight.getColor().a = 0;
      } else {
         int r = this.highlightCell / 3;
         int c = this.highlightCell % 3;
         if ( r == 1 && c == 1 ) {
            return;
         }
         float xOff = this.touchHighlight.getWidth()*c;
         float xPad = 3.3f*c;
         float yOff = this.touchHighlight.getHeight()*r;
         float yPad = 5*r+2.5f;
         this.touchHighlight.setPosition(5+xOff+xPad,  yOff+yPad);
         touchHighlight.setColor(new Color(0.62f,.42f,.96f, .75f));
      }
   }
   
   @Override
   public void setVisible(boolean visible) {
      this.highlightCell = -1;
      if ( visible == false ) {
         getColor().a = 0;
      } else {
         getColor().a = 1;
      }
      super.setVisible(visible);
   }
   
   public interface Listener {
      void recalibrated( int newAngle );
   }
}
