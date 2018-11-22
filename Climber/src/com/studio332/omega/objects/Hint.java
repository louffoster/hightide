package com.studio332.omega.objects;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.studio332.omega.util.Assets;

public class Hint extends Group {
   private ShapeRenderer shapeRenderer;
   private Label label;

   public Hint() {
      super();
      setWidth(200);
      setHeight(80);
      
      Color c = new Color(0.60f, 0.41f, 0.96f, 1f);
      LabelStyle st = new LabelStyle(Assets.instance().getFont(), c);
      this.label = new Label("Can't trash\nhazards!", st);
      this.label.setWidth(getWidth());
      this.label.setFontScale(0.8f);
      this.label.setAlignment(Align.center);
      this.label.setPosition(0, 10);
      addActor(this.label);
      
      getColor().a = 0;
      
      this.shapeRenderer = new ShapeRenderer();
   }
   
   public void showTrashActiveHint() {
      this.label.setText("In use,\nCan't trash");
      displayHint();
   }
   
   public void showTrashHazardHint() {
      this.label.setText("Can't trash\nhazards!");
      displayHint();
   }
   
   public void showPlayHazardHint() {
      this.label.setText("Can't play\nhazards!");
      displayHint();
   }
   
   private void displayHint() {
      addAction( sequence(fadeIn(0.5f), delay(1.2f), fadeOut(.5f) ) );
   }
   
   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
      batch.end();  
      
      Gdx.gl.glEnable(GL20.GL_BLEND);
      Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
      this.shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
      this.shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
      this.shapeRenderer.translate(getX(), getY(), 0);
      this.shapeRenderer.setColor(0.60f, 0.41f, 0.96f, getColor().a);
      this.shapeRenderer.begin(ShapeType.Filled);
      this.shapeRenderer.rect(0,0,getWidth(),getHeight());
      this.shapeRenderer.setColor(.21f, 0.01f, .28f, getColor().a);
      this.shapeRenderer.rect(2,2,getWidth()-4, getHeight()-4);
      this.shapeRenderer.end();
      Gdx.gl.glDisable(GL20.GL_BLEND);
      
      batch.begin();
      super.draw(batch, parentAlpha);
   }
}
