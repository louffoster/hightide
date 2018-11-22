package com.studio332.omega.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class RisingWater extends Actor {
   private ShapeRenderer shapeRenderer;

   public RisingWater() {
      super();
      this.shapeRenderer = new ShapeRenderer();
   }

   public void draw(SpriteBatch batch, float waterH ) {
      batch.end();

      Gdx.gl.glEnable(GL20.GL_BLEND);
      Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

      this.shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
      this.shapeRenderer.setTransformMatrix(batch.getTransformMatrix());

      shapeRenderer.begin(ShapeType.Filled);
      shapeRenderer.setColor(new Color(.0f, 0.5f, 0.7f, 0.8f));
      shapeRenderer.rect(getX(), getY(), getWidth(), waterH);
      shapeRenderer.end();

      Gdx.gl20.glLineWidth(3);
      shapeRenderer.begin(ShapeType.Line);
      shapeRenderer.setColor(new Color(.3f, 0.8f, 1f, 0.9f));
      shapeRenderer.line(getX(), waterH, getX() + getWidth(), waterH);
      shapeRenderer.end();

      Gdx.gl.glDisable(GL20.GL_BLEND);

      batch.begin();
   }
}
