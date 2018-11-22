package com.studio332.omega.util;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.studio332.omega.Climber;

public class Overlay extends Image {
   
   public Overlay() {
      super( Assets.instance().getPixel() );
      setSize(Climber.TGT_WIDTH, Climber.TGT_HEIGHT);
      setColor(0.0f,0.0f, 0.0f, 0.0f);
      setTouchable(Touchable.disabled);
   }
}
